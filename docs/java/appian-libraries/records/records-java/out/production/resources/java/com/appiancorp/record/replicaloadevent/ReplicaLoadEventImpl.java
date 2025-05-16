package com.appiancorp.record.replicaloadevent;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.appiancorp.record.service.ReplicaLoadCause;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.MoreObjects;

/**
 * Represents a single replica load event for a given record type
 */
@XmlTransient
@Entity(name = "ReplicaLoadEvent")
@IgnoreJpa
@Hidden
@Table(name = "replica_load_event")
public class ReplicaLoadEventImpl implements ReplicaLoadEvent {
  public static final String PROP_ID = ID_FIELD;
  public static final String PROP_RECORD_TYPE_UUID = "recordTypeUuid";
  public static final String PROP_START_TIME = "startTimeMs";
  public static final String PROP_END_TIME = "endTimeMs";
  public static final String PROP_STATUS = "statusByte";
  public static final String PROP_TOTAL_SOURCE_ROWS = "totalSourceRows";
  public static final String PROP_TOTAL_SOURCE_ROWS_TO_SYNC = "totalSourceRowsToSync";
  public static final String PROP_REPLICA_ROWS_WRITTEN = "replicaRowsWritten";
  public static final String PROP_TRIGGER_NAME = "triggerName";
  public static final String PROP_CAUSE_BYTE = "causeByte";
  public static final String PROP_INITIATOR_UUID = "initiatorUuid";
  public static final String PROP_USES_ROLLING_SYNC = "usedRollingSync";
  public static final String PROP_ROLLING_SYNC_ENABLED = "rollingSyncEnabled";

  private Long id;
  private String recordTypeUuid;
  private Long startTimeMs;
  private Long endTimeMs = Long.MAX_VALUE;
  private Integer totalSourceRows;
  private Integer totalSourceRowsToSync;
  private Integer replicaRowsWritten;
  private ReplicaLoadEventStatus status = ReplicaLoadEventStatus.RUNNING;
  private String triggerName;
  private ReplicaLoadCause replicaLoadCause;
  private String initiatorUuid;
  private boolean isRetry = false;
  private boolean usedRollingSync = false;
  private boolean rollingSyncEnabled = false;
  private String cancelledBy;

  public ReplicaLoadEventImpl() {}

  public ReplicaLoadEventImpl(String recordTypeUuid, Long startTimeMs, ReplicaLoadCause cause) {
    this(null, recordTypeUuid, startTimeMs, cause);
  }

  public ReplicaLoadEventImpl(String recordTypeUuid, Long startTimeMs, ReplicaLoadCause cause,
      String triggerName, String initiatorUuid, boolean isRetry) {
    this(recordTypeUuid, startTimeMs, cause);
    this.triggerName = triggerName;
    this.initiatorUuid = initiatorUuid;
    this.isRetry = isRetry;
  }

  public ReplicaLoadEventImpl(ReplicaLoadEvent replicaLoadEvent) {
    this.id = replicaLoadEvent.getId();
    this.recordTypeUuid = replicaLoadEvent.getRecordTypeUuid();
    this.startTimeMs = replicaLoadEvent.getStartTimeMs();
    this.endTimeMs = replicaLoadEvent.getEndTimeMs();
    this.totalSourceRows = replicaLoadEvent.getTotalSourceRows();
    this.totalSourceRowsToSync = replicaLoadEvent.getTotalSourceRowsToSync();
    this.replicaRowsWritten = replicaLoadEvent.getReplicaRowsWritten();
    this.status = replicaLoadEvent.getStatus();
    this.triggerName = replicaLoadEvent.getTriggerName();
    this.replicaLoadCause = replicaLoadEvent.getReplicaLoadCause();
    this.initiatorUuid = replicaLoadEvent.getInitiatorUuid();
    this.isRetry = replicaLoadEvent.getIsRetry();
    this.usedRollingSync = replicaLoadEvent.getUsedRollingSync();
    this.rollingSyncEnabled = replicaLoadEvent.getRollingSyncEnabled();
    this.cancelledBy = replicaLoadEvent.getCancelledBy();
  }

  public ReplicaLoadEventImpl(Long id, String recordTypeUuid, long startTimeMs, ReplicaLoadCause replicaLoadCause) {
    this.id = id;
    this.recordTypeUuid = recordTypeUuid;
    this.startTimeMs = startTimeMs;
    this.replicaLoadCause = replicaLoadCause;
  }

  @Id
  @GeneratedValue
  @Column(name = ID_FIELD, updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "record_type_uuid", updatable = false, nullable = false)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Column(name = "start_time_ms", updatable = false, nullable = false)
  public Long getStartTimeMs() {
    return startTimeMs;
  }

  public void setStartTimeMs(Long startTimeMs) {
    this.startTimeMs = startTimeMs;
  }

  @Column(name = "end_time_ms")
  @Nullable
  public Long getEndTimeMs() {
    return endTimeMs;
  }

  public void setEndTimeMs(Long endTimeMs) {
    this.endTimeMs = endTimeMs;
  }

  @Column(name = "total_source_rows")
  public Integer getTotalSourceRows() {
    return totalSourceRows;
  }

  public void setTotalSourceRows(Integer totalSourceRows) {
    this.totalSourceRows = totalSourceRows;
  }

  @Column(name = "total_source_rows_to_sync")
  public Integer getTotalSourceRowsToSync() {
    return totalSourceRowsToSync;
  }

  public void setTotalSourceRowsToSync(Integer totalSourceRowsToSync) {
    this.totalSourceRowsToSync = totalSourceRowsToSync;
  }

  @Column(name = "replica_rows_written")
  public Integer getReplicaRowsWritten() {
    return replicaRowsWritten;
  }

  public void setReplicaRowsWritten(Integer replicaRowsWritten) {
    this.replicaRowsWritten = replicaRowsWritten;
  }

  @XmlTransient
  @Column(name = "status")
  private Byte getStatusByte() {
    return status.getCode();
  }

  private void setStatusByte(Byte status) {
    this.status = ReplicaLoadEventStatus.getByCode(status);
  }

  @Transient
  public ReplicaLoadEventStatus getStatus() {
    return status;
  }

  public void setStatus(ReplicaLoadEventStatus status) {
    this.status = status;
  }

  @Column(name = "trigger_name")
  public String getTriggerName() {
    return triggerName;
  }

  public void setTriggerName(String triggerName) {
    this.triggerName = triggerName;
  }

  @XmlTransient
  @Column(name = "cause", updatable = false, nullable = false)
  private Byte getCauseByte() {
    return replicaLoadCause == null ? null : replicaLoadCause.getCode();
  }

  private void setCauseByte(Byte code) {
    this.replicaLoadCause = ReplicaLoadCause.getCauseByCode(code);
  }

  @Transient
  public ReplicaLoadCause getReplicaLoadCause() {
    return replicaLoadCause;
  }

  public void setReplicaLoadCause(ReplicaLoadCause replicaLoadCause) {
    this.replicaLoadCause = replicaLoadCause;
  }

  @Column(name = "initiator_uuid")
  public String getInitiatorUuid() {
    return initiatorUuid;
  }

  public void setInitiatorUuid(String initiatorUuid) {
    this.initiatorUuid = initiatorUuid;
  }

  @Column(name = "is_retry", updatable = false, nullable = false)
  public boolean getIsRetry() {
    return isRetry;
  }

  public void setIsRetry(boolean isRetry) {
    this.isRetry = isRetry;
  }

  @Column(name = "used_rolling_sync", nullable = false)
  public boolean getUsedRollingSync() {
    return usedRollingSync;
  }

  public void setUsedRollingSync(boolean usedRollingSync) {
    this.usedRollingSync = usedRollingSync;
  }

  @Override
  @Column(name = "rolling_sync_enabled", nullable = false)
  public boolean getRollingSyncEnabled() {
    return rollingSyncEnabled;
  }

  @Override
  public void setRollingSyncEnabled(boolean rollingSyncEnabled) {
    this.rollingSyncEnabled = rollingSyncEnabled;
  }

  @Override
  @Column(name = "cancelled_by")
  public String getCancelledBy() {
    return cancelledBy;
  }

  @Override
  public void setCancelledBy(String cancelledBy) {
    this.cancelledBy = cancelledBy;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add(ID_FIELD, id)
        .add("recordTypeUuid", recordTypeUuid)
        .add("startTimeMs", startTimeMs)
        .add("endTimeMs", endTimeMs)
        .add("totalSourceRows", totalSourceRows)
        .add("totalSourceRowsToSync", totalSourceRowsToSync)
        .add("replicaRowsWritten", replicaRowsWritten)
        .add("status", status)
        .add("triggerName", triggerName)
        .add("cause", replicaLoadCause)
        .add("initiatorUuid", initiatorUuid)
        .add("isRetry", isRetry)
        .add("usedRollingSync", usedRollingSync)
        .toString();
  }
}
