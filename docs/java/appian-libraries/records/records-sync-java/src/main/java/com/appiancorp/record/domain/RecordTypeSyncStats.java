package com.appiancorp.record.domain;

import java.io.Serial;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.appiancorp.record.data.domain.SyncStats;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.external.IgnoreJpa;

@Entity
@Hidden
@Table(name = RecordTypeSyncStats.TABLE_NAME)
@XmlTransient
@IgnoreJpa
public final class RecordTypeSyncStats implements com.appiancorp.type.Id<Long>, SyncStats {
  public static final String TABLE_NAME = "record_sync_stats";
  public static final String PROP_RECORD_TYPE_UUID = "recordTypeUuid";
  public static final String PROP_ROW_COUNT = "rowCount";
  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private String recordTypeUuid;
  private Long lastUpdatedMs;
  private Integer rowCount;

  public RecordTypeSyncStats() {}

  public RecordTypeSyncStats(Long id, String recordTypeUuid) {
    this.id = id;
    this.recordTypeUuid = recordTypeUuid;
  }

  @Override
  @Id
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "record_type_uuid")
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  @Override
  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Column(name = "last_updated_ms")
  public Long getLastUpdatedMs() {
    return lastUpdatedMs;
  }

  public void setLastUpdatedMs(Long lastUpdatedMs) {
    this.lastUpdatedMs = lastUpdatedMs;
  }

  @Column(name = "row_count")
  public Integer getRowCount() {
    return rowCount;
  }

  public void setRowCount(Integer rowCount) {
    this.rowCount = rowCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecordTypeSyncStats that = (RecordTypeSyncStats)o;
    return Objects.equals(lastUpdatedMs, that.lastUpdatedMs) &&
        Objects.equals(recordTypeUuid, that.recordTypeUuid) &&
        Objects.equals(rowCount, that.rowCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastUpdatedMs, recordTypeUuid, rowCount);
  }
}
