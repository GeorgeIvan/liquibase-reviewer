package com.appiancorp.record.replicaloadevent;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.appiancorp.record.recordindexevent.RecordIndexEvent;
import com.appiancorp.record.recordindexevent.RecordIndexEventStatus;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.external.IgnoreJpa;

@XmlTransient
@Entity(name = "RecordIndexEvent")
@IgnoreJpa
@Hidden
@Table(name = "record_index_event")
public class RecordIndexEventImpl implements RecordIndexEvent {
  private Long id;
  private RecordIndexEventStatus status = RecordIndexEventStatus.RUNNING;
  private Long startTimeMs;
  private Long endTimeMs = Long.MAX_VALUE;
  private Integer successCount;
  private Integer inProgressCount;
  private Integer failedCount;
  private ReplicaLoadEvent replicaLoadEvent;

  public RecordIndexEventImpl() {}

  public RecordIndexEventImpl(Long startTimeMs) {
    this.startTimeMs = startTimeMs;
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

  @XmlTransient
  @Column(name = "status", nullable = false)
  private Byte getStatusByte() {
    return status.getCode();
  }

  private void setStatusByte(Byte status) {
    this.status = RecordIndexEventStatus.getByCode(status);
  }

  @Override
  @Transient
  public RecordIndexEventStatus getStatus() {
    return status;
  }

  @Override
  public void setStatus(RecordIndexEventStatus status) {
    this.status = status;
  }

  @Override
  @Column(name = "start_time_ms", updatable = false, nullable = false)
  public Long getStartTimeMs() {
    return startTimeMs;
  }

  @Override
  public void setStartTimeMs(Long startTimeMs) {
    this.startTimeMs = startTimeMs;
  }

  @Override
  @Column(name = "end_time_ms")
  public Long getEndTimeMs() {
    return endTimeMs;
  }

  @Override
  public void setEndTimeMs(Long endTimeMs) {
    this.endTimeMs = endTimeMs;
  }

  @Override
  @Column(name = "success_count")
  public Integer getSuccessCount() {
    return successCount;
  }

  @Override
  public void setSuccessCount(Integer successCount) {
    this.successCount = successCount;
  }

  @Override
  @Column(name = "in_progress_count")
  public Integer getInProgressCount() {
    return inProgressCount;
  }

  @Override
  public void setInProgressCount(Integer inProgressCount) {
    this.inProgressCount = inProgressCount;
  }

  @Override
  @Column(name = "failed_count")
  public Integer getFailedCount() {
    return failedCount;
  }

  @Override
  public void setFailedCount(Integer failedCount) {
    this.failedCount = failedCount;
  }

  @Override
  @OneToOne(targetEntity = ReplicaLoadEventImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "recordIndexEvent")
  public ReplicaLoadEvent getReplicaLoadEvent() {
    return replicaLoadEvent;
  }

  @Override
  public void setReplicaLoadEvent(ReplicaLoadEvent replicaLoadEvent) {
    this.replicaLoadEvent = replicaLoadEvent;
  }
}
