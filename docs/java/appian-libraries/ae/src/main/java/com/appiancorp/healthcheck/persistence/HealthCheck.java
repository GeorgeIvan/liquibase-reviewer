package com.appiancorp.healthcheck.persistence;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.user.User;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Objects;

/**
 * An entity class to store health check object in RDBMS.
 */
@Entity
@Table(name = "healthcheck")
public class HealthCheck implements Serializable {
  private Long id;
  private Long startTs;
  private Long endTs;
  private Status status;
  private Step step;
  private String serverName;
  private User runBy;
  private Boolean requiresReview;
  private User reviewedBy;
  private Long reviewedTs;
  private Long analysisStartTs;
  private Long analysisEndTs;
  private Long analysisPeriod;
  private Boolean isScheduled;
  private Long uploadedCollectionZip;
  private Long reportDoc;
  private Long heapSizeStart;
  private Long heapSizeEnd;
  private Long collectorsTotal;
  private Long collectorsRun;
  private String runningCollector;
  private Long statusConstraint;
  private String requestId;
  private Boolean preventedHealthCheckRun;
  private Long dataReviewStartTs;
  private String errorCode;
  private Long heartBeatTs;
  private Long scheduledStartTs;
  private Long scheduledFrequency;
  private Long lastStepStartTs;

  public enum Status {
    RUNNING, COMPLETED, FAILED, CANCELLING, CANCELLED
  }

  public enum Step {
    DATA_COLLECTION, ZIPPING_COLLECTION, DOWNLOAD_ZIP, REVIEW_DATA, REQUESTING_ANALYSIS, WAITING_ON_ANALYSIS_DATA, VIEW_REPORT
  }

  HealthCheck() {}

  private HealthCheck(HealthCheckBuilder healthCheckBuilder) {
    this.startTs = healthCheckBuilder.getStartTs();
    this.status = healthCheckBuilder.getStatus();
    this.step = healthCheckBuilder.getStep();
    this.serverName = healthCheckBuilder.getServerName();
    this.runBy = healthCheckBuilder.getRunBy();
    this.statusConstraint = healthCheckBuilder.getStatusConstraint();
    this.isScheduled = healthCheckBuilder.getIsScheduled();
    this.requiresReview = healthCheckBuilder.getRequiresReview();
    this.collectorsTotal = healthCheckBuilder.getCollectorsTotal();
    this.heartBeatTs = healthCheckBuilder.getHeartBeatTs();
    this.scheduledStartTs = healthCheckBuilder.getScheduledStartTs();
    this.scheduledFrequency = healthCheckBuilder.getScheduledFrequency();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "start_ts", nullable = false, updatable = false)
  public Long getStartTs() {
    return startTs;
  }

  public void setStartTs(Long startTime) {
    this.startTs = startTime;
  }

  @Column(name = "end_ts")
  public Long getEndTs() {
    return endTs;
  }

  public void setEndTs(Long endTime) {
    this.endTs = endTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "step")
  public Step getStep() {
    return step;
  }

  public void setStep(Step step) {
    this.step = step;
  }

  @Column(name = "server_name", nullable = false)
  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "run_by", nullable = false, updatable = false)
  User getRunByUser() {
    return runBy;
  }
  @SuppressWarnings("unused")
  private void setRunByUser(User runBy) {
    this.runBy = runBy;
  }
  @Transient
  public UserRef getRunBy() {
    return runBy == null ? null : new UserRefImpl(AuditInfo.getUserName(runBy), this.runBy.getUuid());
  }

  public void setRunBy(UserRef runBy) {
    this.runBy = runBy == null ? null : new User(runBy);
  }

  @Column(name = "requires_review")
  public Boolean getRequiresReview() {
    return requiresReview;
  }

  public void setRequiresReview(Boolean requiresReview) {
    this.requiresReview = requiresReview;
  }

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "reviewed_by")
  User getReviewedByUser() {
    return reviewedBy;
  }
  @SuppressWarnings("unused")
  private void setReviewedByUser(User reviewedBy) {
    this.reviewedBy = reviewedBy;
  }
  @Transient
  public UserRef getReviewedBy() {
    return reviewedBy == null ? null : new UserRefImpl(AuditInfo.getUserName(reviewedBy), reviewedBy.getUuid());
  }

  public void setReviewedBy(UserRef reviewedBy) {
    this.reviewedBy = reviewedBy == null ? null : new User(reviewedBy);
  }

  @Column(name = "reviewed_ts")
  public Long getReviewedTs() {
    return reviewedTs;
  }

  public void setReviewedTs(Long reviewedTime) {
    this.reviewedTs = reviewedTime;
  }

  @Column(name="analysis_start_ts")
  public Long getAnalysisStartTs() {
    return analysisStartTs;
  }

  public void setAnalysisStartTs(Long analysisStartTs) {
    this.analysisStartTs = analysisStartTs;
  }

  @Column(name="analysis_end_ts")
  public Long getAnalysisEndTs() {
    return analysisEndTs;
  }

  public void setAnalysisEndTs(Long analysisEndTs) {
    this.analysisEndTs = analysisEndTs;
  }

  @Column(name="analysis_period")
  public Long getAnalysisPeriod() {
    return analysisPeriod;
  }

  public void setAnalysisPeriod(Long analysisPeriod) {
    this.analysisPeriod = analysisPeriod;
  }

  @Column(name = "error_code")
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  @Column(name="is_scheduled")
  public Boolean getIsScheduled() {
    return isScheduled;
  }

  public void setIsScheduled(Boolean isScheduled) {
    this.isScheduled = isScheduled;
  }

  @Column(name = "uploaded_collection_zip")
  public Long getUploadedCollectionZip() {
    return uploadedCollectionZip;
  }

  public void setUploadedCollectionZip(Long uploadedCollectionZip) {
    this.uploadedCollectionZip = uploadedCollectionZip;
  }

  @Column(name = "report_doc")
  public Long getReportDoc() {
    return reportDoc;
  }

  public void setReportDoc(Long reportDoc) {
    this.reportDoc = reportDoc;
  }

  @Column(name = "heap_size_start")
  public Long getHeapSizeStart() {
    return heapSizeStart;
  }

  public void setHeapSizeStart(Long heapSizeStart) {
    this.heapSizeStart = heapSizeStart;
  }

  @Column(name = "heap_size_end")
  public Long getHeapSizeEnd() {
    return heapSizeEnd;
  }

  public void setHeapSizeEnd(Long heapSizeEnd) {
    this.heapSizeEnd = heapSizeEnd;
  }

  @Column(name = "collectors_total")
  public Long getCollectorsTotal() {
    return collectorsTotal;
  }

  public void setCollectorsTotal(Long collectorsTotal) {
    this.collectorsTotal = collectorsTotal;
  }

  @Column(name = "collectors_run")
  public Long getCollectorsRun() {
    return collectorsRun;
  }

  public void setCollectorsRun(Long collectorsRun) {
    this.collectorsRun = collectorsRun;
  }

  @Column(name = "running_collector")
  public String getRunningCollector() {
    return runningCollector;
  }

  public void setRunningCollector(String runningCollector) {
    this.runningCollector = runningCollector;
  }

  @Column(name = "request_id")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Column(name = "prevented_health_check_run")
  public Boolean getPreventedHealthCheckRun() {
    return preventedHealthCheckRun;
  }

  public void setPreventedHealthCheckRun(Boolean preventedHealthCheckRun) {
    this.preventedHealthCheckRun = preventedHealthCheckRun;
  }

  @Column(name = "data_review_start_time")
  public Long getDataReviewStartTs() {
    return dataReviewStartTs;
  }

  public void setDataReviewStartTs(Long dataReviewStartTs) {
    this.dataReviewStartTs = dataReviewStartTs;
  }


  @Column(name = "last_step_start_time")
  public Long getLastStepStartTs() {
    return lastStepStartTs;
  }

  public void setLastStepStartTs(Long lastStepStartTs) {
    this.lastStepStartTs = lastStepStartTs;
  }

  @Column(name = "heart_beat")
  public Long getHeartBeatTs() {
    return heartBeatTs;
  }

  public void setHeartBeatTs(Long heartBeatTs) {
    this.heartBeatTs = heartBeatTs;
  }

  @Column(name = "scheduled_start")
  public Long getScheduledStartTs() {
    return scheduledStartTs;
  }

  public void setScheduledStartTs(Long scheduledStartTs) {
    this.scheduledStartTs = scheduledStartTs;
  }

  @Column(name = "scheduled_frequency")
  public Long getScheduledFrequency() {
    return scheduledFrequency;
  }

  public void setScheduledFrequency(Long scheduledFrequency) {
    this.scheduledFrequency = scheduledFrequency;
  }
  /*
   * This field (unique long) is used as insert constraint such that we only allow one insertion
   * until that last one is finished or expired. The uniqueness comes from copying id to this.
   * Running health check will have a value of -1.
   * TODO: make a trigger before insertion in liquibase to check for only one instance of running HealthCheck
   */
  @Column(name = "status_constraint")
  public Long getStatusConstraint() {
    return statusConstraint;
  }

  public void setStatusConstraint(Long statusConstraint) {
    this.statusConstraint = statusConstraint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HealthCheck otherHealthCheck = (HealthCheck) o;
    return equalHealthCheckRun(otherHealthCheck) && equalAnalysis(otherHealthCheck);
  }

  private boolean equalHealthCheckRun(HealthCheck that) {
    return Objects.equal(startTs, that.startTs) &&
            Objects.equal(endTs, that.endTs) &&
            Objects.equal(status, that.status) &&
            Objects.equal(serverName, that.serverName) &&
            Objects.equal(runBy, that.runBy) &&
            Objects.equal(isScheduled, that.isScheduled) &&
            Objects.equal(heapSizeStart, that.heapSizeStart) &&
            Objects.equal(heapSizeEnd, that.heapSizeEnd) &&
            Objects.equal(collectorsTotal, that.collectorsTotal) &&
            Objects.equal(collectorsRun, that.collectorsRun);
  }
  private boolean equalAnalysis(HealthCheck that) {
    return Objects.equal(requiresReview, that.requiresReview) &&
            Objects.equal(reviewedBy, that.reviewedBy) &&
            Objects.equal(reviewedTs, that.reviewedTs) &&
            Objects.equal(analysisStartTs, that.analysisStartTs) &&
            Objects.equal(analysisEndTs, that.analysisEndTs) &&
            Objects.equal(reportDoc, that.reportDoc);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(startTs, endTs, status, serverName, runBy, requiresReview, reviewedBy, reviewedTs,
            analysisStartTs, analysisEndTs, isScheduled, reportDoc, heapSizeStart, heapSizeEnd, collectorsTotal,
            collectorsRun);
  }
  @Override
  public String toString() {
    return "HealthCheck{id=" + id
       + ", startTime=" + startTs
       + ", endTime=" + endTs
       + ", status=" + status
       + ", step=" + step
       + ", serverName=" + serverName
       + ", runBy=" + runBy
       + ", requiresReview=" + requiresReview
       + ", reviewedBy=" + reviewedBy
       + ", reviewedTime=" + reviewedTs
       + ", analysisStartTs=" + analysisStartTs
       + ", analysisEndTs=" + analysisEndTs
       + ", isScheduled=" + isScheduled
       + ", reportDoc=" + reportDoc
       + ", heapSizeStart=" + heapSizeStart
       + ", heapSizeEnd=" + heapSizeEnd
       + ", collectorsTotal=" + collectorsTotal
       + ", collectorsRun=" + collectorsRun
       + ", heartBeatTs=" + heartBeatTs + "}";
  }

  /*
   * A builder class for building health check objects. Since we don't use all fields when creating
   * new HealthCheck objects, this builder only uses a small portion of HealthCheck fields.
   */
  public static class HealthCheckBuilder {
    private Long startTs;
    private Status status;
    private Step step;
    private String serverName;
    private User runBy;
    private Long statusConstraint;
    private Boolean isScheduled;
    private Boolean requiresReview;
    private Long collectorsTotal;
    private Long heartBeatTs;
    private Long scheduledStartTs;
    private Long scheduledFrequency;

    public HealthCheckBuilder setStartTs(Long startTs) {
      this.startTs = startTs;
      return this;
    }

    public HealthCheckBuilder setStatus(Status status) {
      this.status = status;
      return this;
    }

    public HealthCheckBuilder setStep(Step step) {
      this.step = step;
      return this;
    }

    public HealthCheckBuilder setServerName(String serverName) {
      this.serverName = serverName;
      return this;
    }

    public HealthCheckBuilder setRunBy(User runBy) {
      this.runBy = runBy;
      return this;
    }

    public HealthCheckBuilder setRequiresReview(Boolean requiresReview) {
      this.requiresReview = requiresReview;
      return this;
    }

    public HealthCheckBuilder setStatusConstraint(Long statusConstraint) {
      this.statusConstraint = statusConstraint;
      return this;
    }

    public HealthCheckBuilder setIsScheduled(Boolean isScheduled) {
      this.isScheduled = isScheduled;
      return this;
    }

    public HealthCheckBuilder setCollectorsTotal(Long collectorsTotal) {
      this.collectorsTotal = collectorsTotal;
      return this;
    }

    public HealthCheckBuilder setHeartBeatTs(Long heartBeatTs) {
      this.heartBeatTs = heartBeatTs;
      return this;
    }

    public HealthCheckBuilder setScheduledStartTs(Long scheduledStartTs) {
      this.scheduledStartTs = scheduledStartTs;
      return this;
    }

    public HealthCheckBuilder setScheduledFrequency(Long scheduledFrequency) {
      this.scheduledFrequency = scheduledFrequency;
      return this;
    }

    public Long getStartTs() {
      return this.startTs;
    }

    public Status getStatus() {
      return this.status;
    }

    public Step getStep() {
      return this.step;
    }

    public String getServerName() {
      return this.serverName;
    }

    public User getRunBy() {
      return this.runBy;
    }

    public Boolean getRequiresReview() {
      return this.requiresReview;
    }

    public Long getStatusConstraint() {
      return this.statusConstraint;
    }

    public Boolean getIsScheduled() {
      return this.isScheduled;
    }

    public Long getCollectorsTotal() {
      return this.collectorsTotal;
    }

    public Long getHeartBeatTs() {
      return this.heartBeatTs;
    }

    public Long getScheduledStartTs() {
      return this.scheduledStartTs;
    }

    public Long getScheduledFrequency() {
      return this.scheduledFrequency;
    }

    public HealthCheck build() {
      return new HealthCheck(this);
    }
  }
}
