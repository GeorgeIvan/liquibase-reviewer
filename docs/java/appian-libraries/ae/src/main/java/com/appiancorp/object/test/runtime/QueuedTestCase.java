package com.appiancorp.object.test.runtime;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.type.Id;
import com.google.common.base.MoreObjects;

@SuppressWarnings("serial")
@Entity
@Table(name = QueuedTestCase.TABLE_NAME)
public class QueuedTestCase implements Id<Long> {

  public static final String TABLE_NAME = "test_queue";

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_TEST_DATA_ID = "testDataId";
  public static final String PROP_STATUS = "status";
  public static final String PROP_STARTED_TS_LONG = "startedLong";

  private Long id;
  private TestJob testJob;
  private Long testDataId;
  private QueueStatus status;
  private Timestamp startedTs;

  public QueuedTestCase() {}

  public QueuedTestCase(Long testDataId) {
    this.testDataId = testDataId;
  }


  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "test_job_id", nullable = false)
  public TestJob getTestJob() {
    return testJob;
  }

  public void setTestJob(TestJob testJob) {
    this.testJob = testJob;
  }

  @Column(name="test_data_id", nullable = false)
  public Long getTestDataId() {
    return testDataId;
  }

  public void setTestDataId(Long testDataId) {
    this.testDataId = testDataId;
  }

  @Column(name="status", nullable = false)
  @Enumerated(EnumType.STRING)
  public QueueStatus getStatus() {
    return status;
  }

  public void setStatus(QueueStatus status) {
    this.status = status;
  }

  @Column(name="started_ts")
  private Long getStartedLong() {
    return startedTs == null ? null : startedTs.getTime();
  }

  @SuppressWarnings("unused")
  private void setStartedLong(Long started) {
    this.startedTs = started == null ? null : new Timestamp(started);
  }

  @Transient
  public Timestamp getStartedTs() {
    return startedTs == null ? null : new Timestamp(startedTs.getTime());
  }

  public void setStartedTs(Timestamp started) {
    this.startedTs = started == null ? null : new Timestamp(started.getTime());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("testJob", testJob)
        .add("testDataId", testDataId)
        .add("status", status)
        .add("startedTs", startedTs)
        .toString();
  }
}
