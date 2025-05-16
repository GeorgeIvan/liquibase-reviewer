package com.appiancorp.object.versions;

import static com.appiancorp.core.util.PortablePreconditions.checkNotNull;
import static com.appiancorp.core.util.PortablePreconditions.checkNotNullOrEmpty;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.kougar.mapper.parameters.UuidParameterConverter;
import com.appiancorp.kougar.mapper.parameters.annotations.ConvertWith;
import com.appiancorp.type.Id;

/**
 * An entity class to store design object version uuids in the RDBMS. A new version uuid (instance of
 * DesignObjectVersion) is persisted whenever the user exports a design object that supports export-version
 * tracking.
 */
@Entity
@Table(name="design_object_version")
@XmlAccessorType(XmlAccessType.NONE)
public class DesignObjectVersion implements Id<Long> {
  private Long id;
  private String objectUuid;
  private String versionUuid;
  private QName objectType;

  public DesignObjectVersion() {}

  public DesignObjectVersion(
      String objectUuid,
      String versionUuid,
      QName objectType) {
    this.objectUuid = checkNotNullOrEmpty(objectUuid);
    this.versionUuid = checkNotNullOrEmpty(versionUuid);
    this.objectType = checkNotNull(objectType);
  }

  @Override
  @javax.persistence.Id
  @com.appiancorp.kougar.mapper.Transient
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @com.appiancorp.kougar.mapper.Transient
  @Column(name = "object_uuid", length = Constants.COL_MAXLEN_UUID, nullable = false)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @XmlAttribute
  @ConvertWith(UuidParameterConverter.class)
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = false)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Transient
  @com.appiancorp.kougar.mapper.Transient
  public QName getObjectType() {
    return objectType;
  }

  public void setObjectType(QName objectType) {
    this.objectType = objectType;
  }

  @Column(name = "object_type", length = Constants.COL_MAXLEN_INDEXABLE, nullable = false)
  @com.appiancorp.kougar.mapper.Transient
  public String getObjectTypeAsString() {
    return objectType == null ? null : objectType.toString();
  }

  public void setObjectTypeAsString(String objectTypeStr) {
    objectType = QName.valueOf(objectTypeStr);
  }

  /**
   * Two design object versions are considered equal if their {@link #getObjectUuid() object uuid},
   * {@link #getVersionUuid() version uuid}, and {@link #getObjectType() object type} match.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignObjectVersion that = (DesignObjectVersion)o;
    return Objects.equals(objectUuid, that.objectUuid) &&
        Objects.equals(versionUuid, that.versionUuid) &&
        Objects.equals(objectType, that.objectType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUuid, versionUuid, objectType);
  }

  @Override
  public String toString() {
    return "DesignObjectVersion [id=" + id + ", objectUuid=" + objectUuid + ", versionUuid=" + versionUuid +
        ", objectType=" + objectType + "]";
  }
}
