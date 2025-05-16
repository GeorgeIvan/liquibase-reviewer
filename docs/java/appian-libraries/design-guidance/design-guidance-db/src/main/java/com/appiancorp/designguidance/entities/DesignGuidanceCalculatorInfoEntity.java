package com.appiancorp.designguidance.entities;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * An entity class to store calculator versions IN RDBMS.
 */
@Entity
@Table(name = "dg_calculator_info")
public class DesignGuidanceCalculatorInfoEntity implements DesignGuidanceCalculatorInfo {
  private String key;
  private Long versionNumber;
  private Long typeId;

  public DesignGuidanceCalculatorInfoEntity() {
  }

  public DesignGuidanceCalculatorInfoEntity(String key, Long versionNumber) {
    this.key = key;
    this.versionNumber = versionNumber;
  }

  public DesignGuidanceCalculatorInfoEntity(String key, Long versionNumber, Long typeId) {
    this.key = key;
    this.versionNumber = versionNumber;
    this.typeId = typeId;
  }

  @Id
  @Column(name = "guidance_key", nullable = false)
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Column(name = "version_number", nullable = false)
  public Long getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(Long versionNumber) {
    this.versionNumber = versionNumber;
  }

  @Transient
  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignGuidanceCalculatorInfoEntity that = (DesignGuidanceCalculatorInfoEntity)o;
    return Objects.equals(key, that.key) &&
        Objects.equals(versionNumber, that.versionNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, versionNumber);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("key", key)
        .add("versionNumber", versionNumber)
        .toString();
  }
}
