package com.appiancorp.designguidance.entities;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

/**
 * An entity class to store object information for {@link DesignGuidance}.
 */
@Entity
@Table(name = "dg_object_info")
public class DesignGuidanceObjectInfoEntity implements DesignGuidanceObjectInfo<DesignGuidanceEntity> {

  private Long id;
  private String objectUuid;
  private Long objectTypeId;
  private String objectName;
  private Long modifier;
  private Long modifiedAt;
  private Set<DesignGuidanceEntity> designGuidances;

  public static final String PROP_ID = "id";
  public static final String PROP_OBJECT_UUID = "objectUuid";
  public static final String PROP_OBJECT_TYPE_ID = "objectTypeId";
  public static final String PROP_OBJECT_NAME = "objectName";
  public static final String PROP_MODIFIER = "modifier";
  public static final String PROP_MODIFIED_AT = "modifiedAt";

  public DesignGuidanceObjectInfoEntity() {
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Column(name = "object_uuid", nullable = false)
  public String getObjectUuid() {
    return objectUuid;
  }

  @Column(name = "object_type_id", nullable = false)
  public Long getObjectTypeId() {
    return objectTypeId;
  }

  @Column(name = "object_name", nullable = false)
  public String getObjectName() {
    return objectName;
  }

  @Column(name = "modifier", nullable = false)
  public Long getModifier() {
    return modifier;
  }

  @Column(name = "modified_at", nullable = false)
  public Long getModifiedAt() {
    return modifiedAt;
  }

  @OneToMany(mappedBy = "objectInfo", cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<DesignGuidanceEntity> getDesignGuidances() {
    return designGuidances;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  public void setObjectTypeId(Long objectTypeId) {
    this.objectTypeId = objectTypeId;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public void setModifier(Long modifier) {
    this.modifier = modifier;
  }

  public void setModifiedAt(Long modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public void setDesignGuidances(Set<DesignGuidanceEntity> designGuidances) {
    this.designGuidances = designGuidances;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignGuidanceObjectInfoEntity that = (DesignGuidanceObjectInfoEntity)o;
    return objectUuid.equals(that.objectUuid) && objectTypeId.equals(that.objectTypeId) &&
        objectName.equals(that.objectName) && modifier.equals(that.modifier) &&
        modifiedAt.equals(that.modifiedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUuid, objectTypeId, objectName, modifier, modifiedAt);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add(PROP_ID, id)
        .add(PROP_OBJECT_UUID, objectUuid)
        .add(PROP_OBJECT_TYPE_ID, objectTypeId)
        .add(PROP_OBJECT_NAME, objectName)
        .add(PROP_MODIFIER, modifier)
        .add(PROP_MODIFIED_AT, modifiedAt).toString();
  }
}
