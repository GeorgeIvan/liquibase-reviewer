package com.appiancorp.processhqrecord.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.processhqrecord.model.ProcessHqRecordType;
import com.appiancorp.processhqrecord.model.ShowInDataFabricOverride;
import com.appiancorp.record.domain.RecordTypeDefinition;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;

@Entity
@Table(name = "phq_record_type")
public final class ProcessHqRecordTypeEntity implements ProcessHqRecordType, Id<Long>, Uuid<String> {
  public static final String PROP_RECORD_TYPE_DEFINITION = "recordTypeDefinition";
  public static final String PROP_SHOW_IN_DATA_FABRIC_OVERRIDE = "showInDataFabricOverrideByte";
  public static final String PROP_RECORD_NAME_OVERRIDE = "recordNameOverride";

  private Long id;
  private String uuid;
  private String rootRecordTypeUuid;
  private ShowInDataFabricOverride showInDataFabricOverride;
  private RecordTypeDefinition recordTypeDefinition;
  private String recordNameOverride;

  private ProcessHqRecordTypeEntity() {
    // Required for Hibernate to work
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  /**
   * DO NOT USE or make protected/public. This is to give Hibernate enough information to be
   * able to join on the record_type table based on the root_record_type_uuid. So we can make
   * join across tables and make queries based on it.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "root_record_type_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
  private RecordTypeDefinition getRecordTypeDefinition() {
    return recordTypeDefinition;
  }

  private void setRecordTypeDefinition(RecordTypeDefinition recordTypeDefinition) {
    this.recordTypeDefinition = recordTypeDefinition;
  }

  public ProcessHqRecordTypeEntity(String rootRecordTypeUuid) {
    this.uuid = UUID.randomUUID().toString();
    this.rootRecordTypeUuid = rootRecordTypeUuid;
    this.showInDataFabricOverride = ShowInDataFabricOverride.NO_OVERRIDE;
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
  @Column(name = "uuid", nullable = false, updatable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Column(name = "root_record_type_uuid", nullable = false, updatable = false, length = Constants.COL_MAXLEN_UUID)
  public String getRootRecordTypeUuid() {
    return rootRecordTypeUuid;
  }

  @Override
  public void setRootRecordTypeUuid(String rootRecordTypeUuid) {
    this.rootRecordTypeUuid = rootRecordTypeUuid;
  }

  @Override
  @Column(name = "show_in_data_fabric_override", nullable = false)
  public Byte getShowInDataFabricOverrideByte() {
    if (showInDataFabricOverride == null) {
      setShowInDataFabricOverride(ShowInDataFabricOverride.NO_OVERRIDE);
    }
    return showInDataFabricOverride.getOverrideByte();
  }

  @Override
  @Transient
  public ShowInDataFabricOverride getShowInDataFabricOverride() {
    if (showInDataFabricOverride == null) {
      setShowInDataFabricOverride(ShowInDataFabricOverride.NO_OVERRIDE);
    }
    return showInDataFabricOverride;
  }

  @Override
  public void setShowInDataFabricOverrideByte(Byte showInDataFabricOverrideByte) {
    setShowInDataFabricOverride(ShowInDataFabricOverride.getOverrideByByte(showInDataFabricOverrideByte));
  }

  @Override
  public void setShowInDataFabricOverride(ShowInDataFabricOverride showInDataFabricOverride) {
    this.showInDataFabricOverride = showInDataFabricOverride;
  }

  @Override
  @Column(name = "record_name_override", nullable = true, updatable = true, unique = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getRecordNameOverride() {
    return recordNameOverride;
  }

  @Override
  public void setRecordNameOverride(String recordNameOverride) {
    this.recordNameOverride = recordNameOverride;
  }
}
