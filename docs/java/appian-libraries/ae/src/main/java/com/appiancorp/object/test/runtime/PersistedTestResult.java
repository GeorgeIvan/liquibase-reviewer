package com.appiancorp.object.test.runtime;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.cdt.RuleTestResultConstants;
import com.appiancorp.suiteapi.type.Datatype;
import com.appiancorp.suiteapi.type.TypeService;
import com.appiancorp.suiteapi.type.TypedValue;
import com.appiancorp.type.Id;
import com.appiancorp.type.xmlconversion.DatatypeValueXmlConverter;
import com.appiancorp.type.xmlconversion.exceptions.FromXmlConversionException;
import com.google.common.base.MoreObjects;

@SuppressWarnings("serial")
@Entity
@Table(name = PersistedTestResult.TABLE_NAME)
public class PersistedTestResult implements Id<Long> {

  public static final String TABLE_NAME = "test_result";

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_TEST_DATA_ID = "testDataId";
  public static final String PROP_TEST_JOB_ID = "testJob.id";

  private Long id;
  private TestJob testJob;
  private Long testDataId; //this testDataId is a testCaseId
  private Long executionMs; // time spent executing the test case
  private Long elapsedMs; // time spent in the queue AND executing the test case to completion
  private Timestamp startedTs;
  private Timestamp endedTs;
  private ResultStatus resultStatus;
  private String resultMessage;
  private String serializedResult;

  public PersistedTestResult(
      TestJob testJob,
      Long testDataId,
      Long executionMs,
      Long elapsedMs,
      Timestamp startedTs,
      Timestamp endedTs,
      ResultStatus resultStatus,
      String resultMessage,
      String serializedResult) {
    this.testJob = testJob;
    this.testDataId = testDataId;
    this.executionMs = executionMs;
    this.elapsedMs = elapsedMs;
    this.startedTs = startedTs == null ? null : new Timestamp(startedTs.getTime());
    this.endedTs = endedTs == null ? null : new Timestamp(endedTs.getTime());
    this.resultStatus = resultStatus;
    this.resultMessage = resultMessage;
    this.serializedResult = serializedResult;
  }

  public PersistedTestResult() {
  }

  public static String getTableName() {
    return TABLE_NAME;
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

  @ManyToOne(fetch = FetchType.LAZY)
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

  @Column(name="execution_ms", nullable = true)
  public Long getExecutionMs() {
    return executionMs;
  }

  public void setExecutionMs(Long executionMs) {
    this.executionMs = executionMs;
  }

  @Column(name="elapsed_ms", nullable = true)
  public Long getElapsedMs() {
    return elapsedMs;
  }

  public void setElapsedMs(Long elapsedMs) {
    this.elapsedMs = elapsedMs;
  }

  @Column(name="started_ts", nullable = true)
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

  @Column(name="ended_ts", nullable = true)
  private Long getEndedLong() {
    return endedTs == null ? null : endedTs.getTime();
  }

  @SuppressWarnings("unused")
  private void setEndedLong(Long ended) {
    this.endedTs = ended == null ? null : new Timestamp(ended);
  }

  @Transient
  public Timestamp getEndedTs() {
    return endedTs == null ? null : new Timestamp(endedTs.getTime());
  }

  public void setEndedTs(Timestamp endedTs) {
    this.endedTs = endedTs == null ? null : new Timestamp(endedTs.getTime());
  }

  @Column(name="result_status", nullable = false)
  @Enumerated(EnumType.STRING)
  public ResultStatus getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(ResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  @Column(name="result_message", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  @Column(name="result_output", length=Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getSerializedResult() {
    return serializedResult;
  }

  @Transient
  public TypedValue getRuleTestResult(TypeService typeService) throws FromXmlConversionException {
    Datatype conversionType = typeService.getTypeByQualifiedName(RuleTestResultConstants.QNAME);
    return DatatypeValueXmlConverter.convertFromXml(this.getSerializedResult(), conversionType, typeService);
  }

  public void setSerializedResult(String serializedResult) {
    this.serializedResult = serializedResult;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("testJob", testJob)
        .add("testDataId", testDataId)
        .add("executionMs", executionMs)
        .add("elapsedMs", elapsedMs)
        .add("startedTs", startedTs)
        .add("endedTs", endedTs)
        .add("resultStatus", resultStatus)
        .add("resultMessage", resultMessage)
        .add("serializedResult", serializedResult)
        .toString();
  }
}
