package com.appiancorp.deploymentpackages.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.deploymentpackages.persistence.service.PackageObjectBuilder;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store the PackageObject object in RDBMS.
 */
@Entity
@Table(name = "dpkg_object")
public class PackageObject {
  public static final String PROP_PACKAGE_ID = "packageId";
  public static final String PROP_OBJECT_UUID = "objectUuid";
  public static final String PROP_OBJECT_UUID_SHA_256 = "objectUuidSHA256";
  private Long id;
  private Long packageId;
  private String objectUuid;
  private String objectType;
  private String objectUuidSHA256;

  public PackageObject(PackageObjectBuilder builder) {
    id = builder.getId();
    packageId = builder.getPackageId();
    objectUuid = builder.getObjectUuid();
    objectType = builder.getObjectType();
    objectUuidSHA256 = builder.getObjectUuidSHA256();
  }

  /** for Hibernate to call during queries */
  public PackageObject() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Column(name = "package_id", insertable = false, updatable = false, nullable = false)
  public Long getPackageId() {
    return packageId;
  }
  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  @Column(name = "object_uuid_sha256", updatable = false, nullable = false, length = 64)
  public String getObjectUuidSHA256() {
    return objectUuidSHA256;
  }
  public void setObjectUuidSHA256(String objectUuidSHA256) {
    this.objectUuidSHA256 = objectUuidSHA256;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("packageId", packageId)
        .add("objectUuid", objectUuid)
        .add("objectType", objectType)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageObject that = (PackageObject)o;
    return Objects.equals(objectUuid, that.objectUuid) && Objects.equals(objectType, that.objectType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUuid, objectType);
  }
}
