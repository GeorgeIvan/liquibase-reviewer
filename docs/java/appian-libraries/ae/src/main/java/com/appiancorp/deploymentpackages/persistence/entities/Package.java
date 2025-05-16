package com.appiancorp.deploymentpackages.persistence.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.Hibernate;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.assertions.Preconditions;
import com.appiancorp.deploymentpackages.persistence.service.PackageBuilder;
import com.appiancorp.deploymentpackages.persistence.service.PackageService;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store the Package object in RDBMS.
 */
@Entity
@Table(name = "dpkg")
public class Package {
  public static final String PROP_PACKAGE_ID = "packageId";
  public static final String PROP_PACKAGE_UUID = "uuid";
  public static final String PROP_PACKAGE_NAME_LC = "nameLc";
  public static final String PROP_PACKAGE_APP_UUID = "appUuid";
  public static final String PROP_CREATED_BY_USER_UUID = "createdByUserUuid";
  public static final String PROP_CREATED_TS = "createdTs";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_TICKET = "projMgmtUrl";
  public static final String PROP_PACKAGE_NAME = "name";
  public static final String PROP_INCLUDE_APP_CONFIG = "includeAppConfig";
  public static final String PROP_ICF_DOC_ID = "icfDocumentId";

  private Long id;
  private String uuid;
  private String name;
  private String nameLc;
  private String description;
  private String appUuid;
  private String projMgmtUrl;
  private Long createdTs;
  private String createdByUserUuid;
  private Set<PackageObject> packageObjects = new HashSet<>();
  private Set<PackageActivity> packageActivities = new HashSet<>();
  private Long modifiedTs;
  private String modifiedByUserUuid;
  private Long version;
  private Set<PackageDbScript> packageDbScripts = new HashSet<>();
  private String dataSourceUuid;
  private Set<PackagePlugin> packagePlugins = new HashSet<>();
  private boolean includeAppConfig;
  private Long icfDocumentId;
  private PackageIcfStatus icfStatus;

  /** for Hibernate to call during queries */
  Package() {}

  public Package(PackageBuilder packageBuilder) {
    id = packageBuilder.getId();
    uuid = packageBuilder.getUuid();
    name = packageBuilder.getName();
    description = packageBuilder.getDescription();
    appUuid = packageBuilder.getAppUuid();
    projMgmtUrl = packageBuilder.getProjMgmtUrl();
    createdTs = packageBuilder.getCreatedTs();
    createdByUserUuid = packageBuilder.getCreatedByUserUuid();
    Set<PackageObject> newPackageObjects = packageBuilder.getPackageObjects();
    if (newPackageObjects != null) {
      packageObjects.addAll(newPackageObjects);
    }
    Set<PackageActivity> newPackageActivities = packageBuilder.getPackageActivities();
    if (newPackageActivities != null) {
      packageActivities.addAll(newPackageActivities);
    }
    modifiedTs = packageBuilder.getModifiedTs();
    modifiedByUserUuid = packageBuilder.getModifiedByUserUuid();
    version = packageBuilder.getVersion();
    Set<PackageDbScript> packageDbScripts = packageBuilder.getPackageDbScripts();
    if(packageDbScripts!=null) {
      this.packageDbScripts.addAll(packageDbScripts);
    }
    this.dataSourceUuid = packageBuilder.getDataSourceUuid();
    Set<PackagePlugin> packagePlugins = packageBuilder.getPackagePlugins();
    if(packagePlugins!=null) {
      this.packagePlugins.addAll(packagePlugins);
    }
    this.includeAppConfig = packageBuilder.getIncludeAppConfig();
    this.icfDocumentId = packageBuilder.getIcfDocumentId();
    this.icfStatus = packageBuilder.getIcfStatus();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "name_lc", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getNameLc() {
    return nameLc;
  }

  public void setNameLc(String nameLc) {
    this.nameLc = nameLc;
  }

  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "app_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getAppUuid() {
    return appUuid;
  }

  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  @Column(name = "proj_mgmt_url", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getProjMgmtUrl() {
    return projMgmtUrl;
  }

  public void setProjMgmtUrl(String projMgmtUrl) {
    this.projMgmtUrl = projMgmtUrl;
  }

  @Column(name = "created_ts", updatable = false, nullable = false)
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Column(name = "created_by_user_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getCreatedByUserUuid() {
    return createdByUserUuid;
  }

  public void setCreatedByUserUuid(String createdByUserUuid) {
    this.createdByUserUuid = createdByUserUuid;
  }

  /**
   * DO NOT USE THIS METHOD. It exists to allow the JPA implementation to understand the schema, but in
   * general it will fail with LazyInitializationException. Instead you can call a service method such as
   * {@link PackageService#getPackageObjects(Long)}.
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "package_id", nullable = false)
  @OrderBy
  public Set<PackageObject> getPackageObjects() {
    return packageObjects;
  }

  void setPackageObjects(Set<PackageObject> packageObjects) {
    Preconditions.checkNotNull(packageObjects, "Illegal null packageObjects");
    this.packageObjects = packageObjects;
  }

  /**
   * DO NOT USE THIS METHOD. It exists to allow the JPA implementation to understand the schema, but in
   * general it will fail with LazyInitializationException. Instead you can call a service method such as
   * {@link PackageService#getPackageActivities(Long)}.
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "package_id", nullable = false)
  @OrderBy
  Set<PackageActivity> getPackageActivities() {
    return packageActivities;
  }

  void setPackageActivities(Set<PackageActivity> packageActivities) {
    Preconditions.checkNotNull(packageActivities, "Illegal null packageActivities");
    this.packageActivities = packageActivities;
  }

  @Column(name = "modified_ts", nullable = false)
  public Long getModifiedTs() {
    return modifiedTs;
  }

  public void setModifiedTs(Long modifiedTs) {
    this.modifiedTs = modifiedTs;
  }

  @Column(name = "modified_by_user_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getModifiedByUserUuid() {
    return modifiedByUserUuid;
  }

  public void setModifiedByUserUuid(String modifiedByUserUuid) {
    this.modifiedByUserUuid = modifiedByUserUuid;
  }

  @Column(name = "version", nullable = false)
  @Version
  public Long getVersion() {
    return version;
  }

  void setVersion(Long version) {
    this.version = version;
  }

  /**
   * DO NOT USE THIS METHOD if you are outside service implementation(@Transaction).It exists to allow the
   * JPA implementation to understand the schema, but in general it will fail with LazyInitializationException.
   * Instead you can call a service method such as {@link PackageService#getPackageDbScripts(Long)}. But if you
   * are in a service method and have started a transaction, then it's safe to call.
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "package_id", nullable = false)
  @OrderBy
  public Set<PackageDbScript> getPackageDbScripts() {
    return packageDbScripts;
  }

  public void setPackageDbScripts(Set<PackageDbScript> packageDbScripts) {
    Preconditions.checkNotNull(packageDbScripts, "Illegal null packageDbScripts");
    this.packageDbScripts = packageDbScripts;
  }

  @Column(name = "data_source_uuid", length = Constants.COL_MAXLEN_INDEXABLE, nullable = true)
  public String getDataSourceUuid() {
    return dataSourceUuid;
  }

  public void setDataSourceUuid(String dataSourceUuid) {
    this.dataSourceUuid = dataSourceUuid;
  }

  /**
   * DO NOT USE THIS METHOD if you are outside service implementation(@Transaction).It exists to allow the
   * JPA implementation to understand the schema, but in general it will fail with LazyInitializationException.
   * Instead you can call a service method such as {@link PackageService#getPackagePlugins(Long)}. But if you
   * are in a service method and have started a transaction, then it's safe to call.
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "package_id", nullable = false)
  @OrderBy
  public Set<PackagePlugin> getPackagePlugins() {
    return packagePlugins;
  }

  public void setPackagePlugins(Set<PackagePlugin> packagePlugins) {
    Preconditions.checkNotNull(packagePlugins, "Illegal null packagePlugins");
    this.packagePlugins = packagePlugins;
  }

  @Column(name="include_app_config", nullable = false)
  public boolean getIncludeAppConfig() {
    return includeAppConfig;
  }

  public void setIncludeAppConfig(boolean includeAppConfig) {
    this.includeAppConfig = includeAppConfig;
  }

  @Column(name = "icf_document_id", nullable = true)
  public Long getIcfDocumentId(){
    return icfDocumentId;
  }

  public void setIcfDocumentId(Long icfDocumentId) {
    if (icfDocumentId != null && icfDocumentId.intValue() == com.appiancorp.core.Constants.INTEGER_NULL) {
      this.icfDocumentId = null;
    } else {
      this.icfDocumentId = icfDocumentId;
    }
  }

  @Transient
  public PackageIcfStatus getIcfStatus() {
    return icfStatus;
  }

  public void setIcfStatus(PackageIcfStatus icfStatus) {
    this.icfStatus = icfStatus;
  }

  /* These methods will be used for hibernate */
  @Column(name="icf_status", nullable = false)
  private byte getIcfStatusByte() {
    return icfStatus.getIndex();
  }

  private void setIcfStatusByte(byte index) {
    setIcfStatus(PackageIcfStatus.valueOf(index));
  }

  @PrePersist
  @PreUpdate
  private void onPrePersistOrUpdate() {
    if (name != null) {
      nameLc = name.toLowerCase();
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("uuid", uuid)
        .add("name", name)
        .add("nameLc", nameLc)
        .add("description", description)
        .add("appUuid", appUuid)
        .add("projMgmtUrl", projMgmtUrl)
        .add("createdTs", new Date(createdTs))
        .add("createdByUserUuid", createdByUserUuid)
        .add("packageObjects", Hibernate.isInitialized(packageObjects)
            ? packageObjects : "<uninitialized>")
        .add("packageActivities", Hibernate.isInitialized(packageActivities)
            ? packageActivities : "<uninitialized>")
        .add("modifiedTs", new Date(modifiedTs))
        .add("modifiedByUserUuid", modifiedByUserUuid)
        .add("version", version)
        .add("packageDbScripts", Hibernate.isInitialized(packageDbScripts)
            ? packageDbScripts : "<uninitialized>")
        .add("dataSourceUuid", dataSourceUuid)
        .add("packagePlugins", Hibernate.isInitialized(packagePlugins)
            ? packagePlugins : "<uninitialized>")
        .add("includeAppConfig", includeAppConfig)
        .add("icfDocumentId", icfDocumentId)
        .add("icfStatus", icfStatus)
        .toString();
  }
}
