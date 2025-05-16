package com.appiancorp.deploymentpackages.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.deploymentpackages.persistence.service.PackageActivityObjectBuilder;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = "dpkg_activity_object")
public class PackageActivityObject {
  public static final String PROP_PACKAGE_ACTIVITY_ID = "packageActivityId";

  private Long id;
  private Long packageActivityId;
  private String objectUuid;
  private String objectType;

  /** for Hibernate to call during queries */
  PackageActivityObject() {}

  public PackageActivityObject(PackageActivityObjectBuilder builder) {
    id = builder.getId();
    packageActivityId = builder.getPackageActivityId();
    objectUuid = builder.getObjectUuid();
    objectType = builder.getObjectType();
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

  @Column(name = "package_activity_id", insertable = false, updatable = false, nullable = false)
  public Long getPackageActivityId() {
    return packageActivityId;
  }
  public void setPackageActivityId(Long packageActivityId) {
    this.packageActivityId = packageActivityId;
  }

  @Column(name = "object_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "object_type", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("packageActivityId", packageActivityId)
        .add("objectUuid", objectUuid)
        .add("objectType", objectType)
        .toString();
  }
}
