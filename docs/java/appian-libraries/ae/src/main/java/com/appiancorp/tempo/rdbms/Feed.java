package com.appiancorp.tempo.rdbms;

import static com.appiancorp.type.refs.FeedRef.LOCAL_PART;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.FeedRef;
import com.appiancorp.type.refs.Ref;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

@Hidden
@Entity @Table(name=Feed.TBL_TEMPO_FEED)
@XmlRootElement(name = "tempoFeed", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name=LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder={"uuid", "name", "description", "showInCatalog", "systemFeed"})
@IgnoreJpa
@Tracked
public class Feed implements com.appiancorp.type.Id<Long>, Uuid<String>, FeedRef, Serializable, HasAuditInfo, HasTypeQName,
    HasVersionHistory {
  private static final long serialVersionUID = 1L;

  public static final String TBL_TEMPO_FEED = "tp_feed";
  public static final String TBL_TEMPO_FEED_RM = "tp_feed_rm";
  public static final String COL_ID = "id";
  public static final String JOIN_COL_TEMPO_FEED_ID = "tp_feed_id";

  public static final String PROP_ID = "id";
  public static final String PROP_UUID = "uuid";
  public static final String PROP_NAME = "name";
  public static final String PROP_ROLE_MAP = "roleMap";
  public static final String PROP_VERSION_UUID = "versionUuid";

  // IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(Roles.TEMPO_FEED_ADMIN, Roles.TEMPO_FEED_EDITOR);

  public static final int MAX_LENGTH_NAME = 50;
  public static final int MAX_LENGTH_DESCRIPTION = 1000;

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String versionUuid;
  private AuditInfo auditInfo = new AuditInfo();
  private boolean showInCatalog = true;
  private boolean systemFeed;
  // GWT-Transient
  private transient Set<RoleMapEntry> roleMap = new HashSet<>();

  public Feed() {}
  /**
   * Creates a new instance with values for all the fields required to persist the object.
   */
  public Feed(String name) {
    this.name = name;
  }
  /**
   * Shallow copy constructor.
   */
  public Feed(Feed f) {
    this.id = f.id;
    this.uuid = f.uuid;
    this.name = f.name;
    this.description = f.description;
    this.showInCatalog = f.showInCatalog;
    this.roleMap = f.roleMap;
    this.systemFeed = f.systemFeed;
    this.versionUuid = f.versionUuid;
  }


  @Column(name="id")
  @Id @GeneratedValue
  @XmlTransient
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /* No index is specified explicitly, because having unique="true" automatically triggers the creation of an index. */
  @Column(name="uuid", length=Constants.COL_MAXLEN_UUID, unique=true, nullable=false, updatable=false)
  public String getUuid() {
    return uuid;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name="name", length=MAX_LENGTH_NAME, unique=false, nullable=false, updatable=true)
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Column(name="description", length=MAX_LENGTH_DESCRIPTION, unique=false, nullable=true, updatable=true)
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @XmlTransient
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  @Override
  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Override @Embedded
  @XmlTransient//MOXy-BUG
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }
  @SuppressWarnings("unused")
  private void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Column(name="show_in_catalog", nullable=false)
  public boolean isShowInCatalog() {
    return showInCatalog;
  }
  public void setShowInCatalog(boolean showInCatalog) {
    this.showInCatalog = showInCatalog;
  }

  @Column(name="is_system", unique=false, nullable=false, updatable=false)
  public boolean isSystemFeed() {
    return systemFeed;
  }

  public void setSystemFeed(boolean systemFeed) {
    this.systemFeed = systemFeed;
    if (systemFeed) {
      showInCatalog = false;
    }
  }

  /* The getter/setter below are not public because the data is lazy-loaded. */
  @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
  @JoinTable(name=TBL_TEMPO_FEED_RM,
    joinColumns=@JoinColumn(name=JOIN_COL_TEMPO_FEED_ID),
    inverseJoinColumns=@JoinColumn(name=RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  @XmlTransient
  Set<RoleMapEntry> getRoleMap() {
    return roleMap;
  }

  public void setRoleMap(Set<RoleMapEntry> roleMapEntries) {
    this.roleMap = roleMapEntries;
  }

  @Override
  public String toString() {
    return "Feed[id="+id+", name="+name+"]";
  }

  // ================================================================
  // Useful methods that operate on collections of feeds.
  // ================================================================

  public static Set<Long> getIds(Collection<? extends Feed> feeds) {
    if (feeds == null || feeds.isEmpty()) {
      return Collections.EMPTY_SET;
    }
    Set<Long> ids = new HashSet<Long>(feeds.size());
    for (Feed f : feeds) {
      ids.add(f.getId());
    }
    return ids;
  }
  public static List<String> getUuids(Collection<? extends Feed> feeds) {
    List<String> uuids = new ArrayList<String>(feeds.size());
    for (Feed f : feeds) {
      uuids.add(f.getUuid());
    }
    return uuids;
  }
  public static Function<Feed,String> selectUuid = new Function<Feed,String>() {
    @Override
    public String apply(Feed input) {
      return input.getUuid();
    }
  };
  public static <T extends Feed> Map<Long, T> getMapKeyedById(Collection<T> feeds) {
    Map<Long, T> feedsMap = new HashMap<Long, T>(feeds.size());
    for (T feed : feeds) {
      feedsMap.put(feed.getId(), feed);
    }
    return feedsMap;
  }
  public static <T extends Feed> Map<String, T> getMapKeyedByUuid(Collection<T> feeds) {
    Map<String, T> feedsMap = new HashMap<String, T>(feeds.size());
    for (T feed : feeds) {
      feedsMap.put(feed.getUuid(), feed);
    }
    return feedsMap;
  }
  @Override
  public Ref<Long,String> build(Long id, String uuid) {
    Feed feed = new Feed();
    feed.setId(id);
    feed.setUuid(uuid);
    return feed;
  }
  @Transient
  @XmlTransient
  @Override
  public QName getTypeQName() {
    return QNAME;
  }
}
