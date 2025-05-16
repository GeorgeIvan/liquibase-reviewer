package com.appiancorp.dataset.entities;

import static com.appiancorp.dataset.entities.DatasetFieldEntity.DATASET_ID_COLUMN_NAME;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.dataset.Dataset;
import com.appiancorp.dataset.ReadOnlyDatasetField;
import com.appiancorp.record.domain.RecordTypeDefinition;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A Hibernate entity class to store Datasets. A Dataset is a subset of the properties from some
 * Record Type Definition that has been specified as useful for higher-level analysis in an
 * Appian subsystem such as Process Mining or Self Service Analytics.
 */
@Entity
@Table(name = "dataset")
public class DatasetEntity implements Dataset<DatasetFieldEntity> {
  public static final String PROP_ROOT_RECORD_TYPE_UUID = "rootRecordTypeUuid";
  public static final String PROP_RECORD_TYPE_DEFINITION = "recordTypeDefinition";
  private Long id;
  private String uuid;
  private String rootRecordTypeUuid;
  private Set<DatasetFieldEntity> datasetFields = new HashSet<>();
  private RecordTypeDefinition recordTypeDefinition;

  private DatasetEntity() {
    // Required for Hibernate to work
  }

  /**
   * DO NOT USE or make protected/public. This is to give Hibernate enough information to be
   * able to join on the record_type table based on the root_record_type_uuid. So we can make
   * join across tables and make queries based on it.
   */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "root_record_type_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
  private RecordTypeDefinition getRecordTypeDefinition() {
    return recordTypeDefinition;
  }

  private void setRecordTypeDefinition(RecordTypeDefinition recordTypeDefinition) {
    this.recordTypeDefinition = recordTypeDefinition;
  }

  public DatasetEntity(String rootRecordTypeUuid) {
    this.uuid = UUID.randomUUID().toString();
    this.rootRecordTypeUuid = rootRecordTypeUuid;
  }

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
  @Column(name = "uuid", nullable = false, updatable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "root_record_type_uuid", nullable = false, updatable = false, length = Constants.COL_MAXLEN_UUID)
  public String getRootRecordTypeUuid() {
    return rootRecordTypeUuid;
  }

  public void setRootRecordTypeUuid(String rootRecordTypeUuid) {
    this.rootRecordTypeUuid = rootRecordTypeUuid;
  }

  @Override
  @Transient
  public ImmutableSet<ReadOnlyDatasetField> getDatasetFieldsReadOnly() {
    return ImmutableSet.copyOf(datasetFields);
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = DATASET_ID_COLUMN_NAME, nullable = false)
  @OrderBy
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<DatasetFieldEntity> getDatasetFields() {
    return datasetFields;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDatasetFields(Set<DatasetFieldEntity> datasetFields) {
    this.datasetFields = datasetFields;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, rootRecordTypeUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DatasetEntity other = (DatasetEntity)obj;
    return Objects.equals(this.uuid, other.uuid) &&
        Objects.equals(this.rootRecordTypeUuid, other.rootRecordTypeUuid);
  }

  @Override
  public String toString() {
    return "Dataset [uuid=" + uuid + ", rootRecordTypeUuid=" + rootRecordTypeUuid + "]";
  }
}
