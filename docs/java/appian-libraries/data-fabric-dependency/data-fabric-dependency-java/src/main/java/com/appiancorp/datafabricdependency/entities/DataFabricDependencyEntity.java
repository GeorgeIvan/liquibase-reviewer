package com.appiancorp.datafabricdependency.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;

@Entity
@Table(name = "data_fabric_dependency")
public class DataFabricDependencyEntity implements DataFabricDependency {
  public static final String PROP_DEPENDENT_UUID = "dependentUuid";
  public static final String PROP_RECORD_UUID = "recordTypeUuid";
  public static final String PROP_DEPENDENT_TYPE_BYTE = "dependentTypeByte";

  private Long id;
  private String recordTypeUuid;
  private DependentType dependentType;
  private String dependentUuid;

  /**
   * Required for Hibernate
   */
  private DataFabricDependencyEntity() { }

  public DataFabricDependencyEntity(String recordTypeUuid, DependentType dependentType, String dependentUuid) {
    this.recordTypeUuid = recordTypeUuid;
    this.dependentType = dependentType;
    this.dependentUuid = dependentUuid;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "record_type_uuid", nullable = false)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  @Override
  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Override
  @Column(name = "dependent_type", nullable = false)
  public byte getDependentTypeByte() {
    return dependentType == null ? 0 : dependentType.getCode();
  }

  @Override
  public void setDependentTypeByte(byte code) {
    setDependentType(DependentType.valueOf(code));
  }

  @Override
  @Transient
  public DependentType getDependentType() {
    return dependentType;
  }

  @Override
  public void setDependentType(DependentType dependentType) {
    this.dependentType = dependentType;
  }

  @Override
  @Column(name = "dependent_uuid", nullable = false, updatable = false, length = Constants.COL_MAXLEN_UUID)
  public String getDependentUuid() {
    return dependentUuid;
  }

  @Override
  public void setDependentUuid(String dependentUuid) {
    this.dependentUuid = dependentUuid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(recordTypeUuid, dependentType, dependentUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DataFabricDependencyEntity other = (DataFabricDependencyEntity)obj;
    return Objects.equals(this.recordTypeUuid, other.recordTypeUuid) &&
        this.dependentType == other.dependentType && Objects.equals(this.dependentUuid, other.dependentUuid);
  }

  @Override
  public String toString() {
    return "DataFabricDependency [recordTypeUuid=" + recordTypeUuid + ", dependentType=" + dependentType.toString() +
        ", dependentUuid=" + dependentUuid + "]";
  }
}
