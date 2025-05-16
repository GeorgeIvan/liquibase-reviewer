package com.appiancorp.cache;

import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;
import static com.appiancorp.cache.CacheAttributes.CacheAttributeKey.CLEANUP_TIMER_INTERVAL;
import static com.appiancorp.cache.CacheAttributes.CacheAttributeKey.DAO_INTERFACE_CLASS;
import static com.appiancorp.cache.CacheAttributes.CacheAttributeKey.ENTRY_CLASS;
import static com.appiancorp.cache.CacheAttributes.CacheAttributeKey.EXPIRATION;
import static com.appiancorp.suite.cfg.ConfigurationFactory.getConfiguration;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Transactional;

import com.appiancorp.common.DurableTimerTask;
import com.appiancorp.common.config.ApplicationContextHolder;
import com.appiancorp.common.config.persistence.SpringTransactionService;
import com.appiancorp.common.persistence.GenericDao;
import com.appiancorp.monitoring.prometheus.CachePrometheusMetrics;
import com.appiancorp.rdbms.config.DataConfiguration;
import com.appiancorp.rdbms.datasource.DatabaseTypeUtils;
import com.appiancorp.rdbms.hb.DaoContext;
import com.appiancorp.rdbms.hb.GenericDaoHbImpl;
import com.appiancorp.security.auth.SpringSecurityContextHelper;
import com.appiancorp.suite.SuiteConfiguration;
import com.appiancorp.suite.cfg.FeatureToggleConfiguration;
import com.appiancorp.suiteapi.common.exceptions.AppianException;

public class DatabaseAuxiliaryCache extends CacheBase implements AuxiliaryCache {

  private static final Logger LOG = Logger.getLogger(DatabaseAuxiliaryCache.class);

  private static final long METRICS_DELAY_MS = 10_000;
  private static final long METRICS_INTERVAL_MS = 20_000;


  @Inject private SpringTransactionService transactionService;

  private final Class daoClass;
  private final CacheDao dao;
  private final Class<? extends DatabaseCacheEntry> cacheEntryClass;
  private final long expirationInMillis;
  private final PrometheusBackedCacheStatistics cacheStatistics;
  private final Timer memoryMetricsAndShrinker;

  public DatabaseAuxiliaryCache(Properties configProps) throws ClassNotFoundException {
    super(configProps);

    cacheEntryClass = (Class<? extends DatabaseCacheEntry>)Class.forName(configProps.getProperty(ENTRY_CLASS.getKey()));
    expirationInMillis = Long.parseLong(configProps.getProperty(EXPIRATION.getKey()));
    daoClass = Class.forName(configProps.getProperty(DAO_INTERFACE_CLASS.getKey()));
    dao = (CacheDao)ApplicationContextHolder.getBean(daoClass);
    CachePrometheusMetrics.initializeMetrics(getName(), getImpl(), -1, true);
    this.cacheStatistics = new PrometheusBackedCacheStatistics(getName(), getImpl(), 0);

    memoryMetricsAndShrinker = new Timer(true);
    // Schedule cleanup task
    final long cleanupTimerIntervalMillis = Long.parseLong(configProps.getProperty(CLEANUP_TIMER_INTERVAL.getKey(), "-1"));
    if (cleanupTimerIntervalMillis > 0) {
      memoryMetricsAndShrinker.schedule(new DurableTimerTask(LOG, () -> {
        LOG.info("Removing expired entries from db cache: " + getName());
        transactionService.runInTransaction(() -> {
          SpringSecurityContextHelper.runAsAdmin(() -> dao.deleteExpired(expirationInMillis));
          return null;
        });
      }), cleanupTimerIntervalMillis, cleanupTimerIntervalMillis);
    }
    // Schedule metrics task
    memoryMetricsAndShrinker.scheduleAtFixedRate(new DurableTimerTask(LOG, () -> {
      transactionService.runInTransaction(() -> {
        // Number of entries in the cache
        CachePrometheusMetrics.setContentsGauge(getName(), getImpl(),
            SpringSecurityContextHelper.runAsAdmin(() -> dao.count()));
        // Overall size of the cache in the database
        CachePrometheusMetrics.setContentsSizeGauge(getName(), getImpl(), SpringSecurityContextHelper.runAsAdmin(() -> getTableSize()));
        return null;
      });
    }), METRICS_DELAY_MS, METRICS_INTERVAL_MS);

    LOG.info("Initialized DatabaseAuxiliaryCache: " + getName());
  }

  private long getTableSize() {
    if (!(getConfiguration(SuiteConfiguration.class).isCloud() ||
        getConfiguration(FeatureToggleConfiguration.class).enableAppianEngineeringFeatures())) {
      LOG.debug("Skipping table size metric collection, not a Cloud or Engineering site");
      return 0;
    }
    try {
      return dao.getStorageSize();
    } catch (Exception e) {
      LOG.error("Could not query database table size", e);
      return 0;
    }
  }

  @Override
  @Transactional
  public Object put(Object key, Object value) {
    try {
      Constructor<DatabaseCacheEntry> constructor = (Constructor<DatabaseCacheEntry>)cacheEntryClass.getConstructor(String.class, byte[].class);
      DatabaseCacheEntry entry = constructor.newInstance(key, value);
      Object o = logLatency(() -> dao.createOrUpdate(entry), CALL_TYPE_PUT, key);
      CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.PUT, getName(), getImpl(), true);
      notifyPut(key, value);
      return o;
    } catch (Exception e) {
      throw new CacheWriteException("Unable to write to database cache", e);
    }
  }

  @Override
  @Transactional
  public void fastPut(Object key, Object value) {
    put(key, value);
  }

  @Override
  @Transactional
  public Object get(Object key) {
    byte[] value = null;
    DatabaseCacheEntry cacheEntry = (DatabaseCacheEntry)logLatency(() -> dao.get((String)key), CALL_TYPE_GET, key);
    if (cacheEntry != null) {
      CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.GET, getName(), getImpl(), true);
      value = cacheEntry.getSerializedEntry();
    } else {
      CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.MISS, getName(), getImpl(), true);
    }
    return value;
  }

  @Override
  @Transactional
  public void putAll(Map entries) {
    Map.Entry entry;
    for (Object o : entries.entrySet()) {
      entry = (Entry)o;
      fastPut(entry.getKey(), entry.getValue());
    }
  }

  @Override
  @Transactional
  public Object remove(Object key) {
    if(key instanceof Object[]) {
      return remove((Object[])key);
    } else {
      logLatency(() -> {
        dao.delete((String)key);
        return null;
      }, CALL_TYPE_REMOVE, key);
      CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.REMOVE, getName(), getImpl(),
          true);
      notifyRemove(key);
      return null;
    }
  }

  @Override
  @Transactional
  public Object[] remove(Object[] keys) {
    Set<String> keyList = new HashSet<>(Arrays.asList((String[])keys));
    logLatency(() -> {
      dao.delete(keyList);
      return null;
    }, CALL_TYPE_REMOVE, keys);
    Arrays.stream(keys).forEach(key -> CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.REMOVE, getName(), getImpl(), true));
    notifyRemove(keyList);
    return null;
  }

  @Override
  @Transactional
  public void clear() {
    dao.deleteAll();
    CachePrometheusMetrics.incrementCounter(CachePrometheusMetrics.CounterType.CLEAR, getName(), getImpl(), true);
  }

  @Override
  public boolean containsKey(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Object> keySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Object> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<Object,Object>> entrySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Transactional
  public int size() {
    return (int)dao.count();
  }

  @Override
  public CacheStatistics getCacheStatistics() {
    return cacheStatistics;
  }

  @Override
  public void stop() {
    memoryMetricsAndShrinker.cancel();
  }

  @Override
  public CachePrometheusMetrics.Impl getImpl() {
    return CachePrometheusMetrics.Impl.DB;
  }

  @Override
  protected boolean collectFullMetrics() {
    return true;
  }

  /**
     * Subclasses of DatabaseCacheEntry also require a subclass of CacheDao typed to the entity
     *
     * @param <T>
     * @param <String>
     */
  public interface CacheDao<T extends DatabaseCacheEntry, String> extends GenericDao<T, String> {

    /**
     * Deletes all expired (not updated for a timespan greater than or equal to expirationInMillis param)
     * entries from the database, regardless of host last updated
     *
     * @param expirationInMillis  Entries that have not been updated for a span greater than or equal to this
     * value will be deleted
     */
    void deleteExpired(long expirationInMillis);

    /**
     * Will return the storage size used by the backing database for the give table;
     * @return size in bytes used by the backend RDBMS storage for this cache
     */
    long getStorageSize();
  }

  /**
   * Subclasses of DatabaseCacheEntry also require a subclass of CacheDaoImpl typed to the entity
   *
   * Subclasses need to be registered with the daoMappings map in the DataConfiguration class
   *
   * @param <T> The subclass of DatabaseCacheEntry
   * @param <String>
   */
  public static abstract class CacheDaoImpl<T extends DatabaseCacheEntry, String> extends GenericDaoHbImpl<T, String> implements CacheDao<T, String> {

    private static final Logger LOG = Logger.getLogger(CacheDaoImpl.class);
    private static final java.lang.String STATEFUL_SAIL_CACHE_TABLE_SIZE_QUERY =
        "SELECT DATA_LENGTH FROM information_schema.TABLES WHERE TABLE_NAME = :TableName LIMIT 1;";
    public CacheDaoImpl(DaoContext daoContext) {
      super(daoContext);
    }

    public CacheDaoImpl(Class persistentClass, DaoContext daoContext) {
      super(persistentClass, daoContext);
    }

    @Override
    public void deleteExpired(long expirationInMillis) {
      long cutoff = System.currentTimeMillis() - expirationInMillis;
      Query query = getSession().createQuery("delete "+getEntityName()+" where "+DatabaseCacheEntry.PROP_UPDATED_TS_LONG+" <= :ts");
      query.setParameter("ts", cutoff);
      query.executeUpdate();
    }

    @Override
    public long getStorageSize() {
      try {
        java.lang.String primaryDataSourceKey = getConfiguration(DataConfiguration.class).getPrimaryDataSourceKey();
        if (DatabaseTypeUtils.isMySqlOrAuroraMySql(primaryDataSourceKey) || DatabaseTypeUtils.isMariaDB(primaryDataSourceKey)) {
          SQLQuery sqlQuery = getSessionProvider().getSession().createSQLQuery(STATEFUL_SAIL_CACHE_TABLE_SIZE_QUERY);
          sqlQuery.setString("TableName", getTableName());
          return ((BigInteger) sqlQuery.uniqueResult()).longValue();
        }
      } catch (AppianException e) {
        LOG.info("Skipping getStorageSize as the current database does not support this query");
      }
      return 0L;
    }

    public abstract java.lang.String getTableName();
  }

  /**
   * Base Hibernate entity class for storing cache entries in the database
   * This should be extended so that an @Entity and @Table annotation can be provided
   *
   * Subclasses need to be registered with the ENTITY_CLASSES set in the DataConfiguration class
   */
  @MappedSuperclass
  public static abstract class DatabaseCacheEntry {

    public static final String PROP_KEY = "key";
    public static final String PROP_UPDATED_TS_LONG = "updatedTsLong";

    private String key;
    private byte[] serializedEntry;
    private Timestamp createdTs;
    private Timestamp updatedTs;

    public DatabaseCacheEntry() {
      createdTs = new Timestamp(System.currentTimeMillis());
      updatedTs = this.createdTs;
    }

    public DatabaseCacheEntry(String key, byte[] serializedEntry) {
      this();
      this.key = key;
      this.serializedEntry = serializedEntry;
    }

    // key

    @Id
    @Column(name="entry_key", nullable=false, length=COL_MAXLEN_INDEXABLE)
    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    // value

    @Column(name="entry_value", nullable=false) @Lob
    public byte[] getSerializedEntry() {
      return serializedEntry;
    }

    public void setSerializedEntry(byte[] serializedEntry) {
      this.serializedEntry = serializedEntry;
    }

    // created timestamp

    @Column(name="created_ts", nullable=false, insertable=true, updatable=false)
    private Long getCreatedTsLong() {
      return createdTs == null ? null : createdTs.getTime();
    }

    @SuppressWarnings("unused")
    private void setCreatedTsLong(Long createdTsLong) {
      this.createdTs = createdTsLong == null ? null : new Timestamp(createdTsLong);
    }

    @Transient
    public Timestamp getCreatedTs() {
      return createdTs;
    }

    public void setCreatedTs(Timestamp createdTs) {
      this.createdTs = createdTs;
    }

    // updated timestamp

    @Column(name="updated_ts", nullable=false, insertable=true, updatable=true)
    private Long getUpdatedTsLong() {
      return updatedTs == null ? null : updatedTs.getTime();
    }

    @SuppressWarnings("unused")
    private void setUpdatedTsLong(Long updatedTsLong) {
      this.updatedTs = updatedTsLong == null ? null : new Timestamp(updatedTsLong);
    }

    @Transient
    public Timestamp getUpdatedTs() {
      return updatedTs;
    }

    public void setUpdatedTs(Timestamp updatedTs) {
      this.updatedTs = updatedTs;
    }

    @Override
    public String toString() {
      return "StatefulSailCacheEntry: key="+key+" created="+createdTs+" updated="+updatedTs;
    }
  }

}
