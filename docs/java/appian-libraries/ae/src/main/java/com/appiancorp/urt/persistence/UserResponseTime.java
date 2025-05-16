package com.appiancorp.urt.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.suiteapi.type.Hidden;

@Hidden
@Entity
@Table(name = UserResponseTime.TABLE_NAME)
public class UserResponseTime implements Serializable {

  static final String TABLE_NAME = "user_response_time";
  static final String PROP_ID = "id";
  static final String PROP_OBJECT_TYPE = "objectTypeAsString";
  static final String PROP_OBJECT_URL_STUB = "objectUrlStub";
  static final String PROP_VIEW_URL_STUB = "viewUrlStub";
  static final String PROP_VIEW_NAME = "viewName";
  static final String PROP_RECORD_INSTANCE_ID = "recordInstanceId";
  static final String PROP_RECORD_INSTANCE_NAME = "recordInstanceName";
  static final String PROP_USER_ID = "userId";
  static final String PROP_CREATED_TS = "createdTs";
  static final String PROP_RESPONSE_TIME_IN_MS = "responseTimeInMs";
  static final String PROP_MAX_RESPONSE_TIME_IN_MS = "maxResponseTimeInMs";
  static final String PROP_AVG_RESPONSE_TIME_IN_MS = "avgResponseTimeInMs";
  static final String PROP_METRICS = "metrics";
  static final String PROP_SAVE_METRICS = "saveMetrics";
  static final String PROP_DISPLAY_NAME = "displayName";

  private Long id;
  private QName objectType;
  private String objectUrlStub;
  private String viewUrlStub;
  private String viewName;
  private String recordInstanceId;
  private String recordInstanceName;
  private Long userId;
  private Integer responseTimeInMs;
  private Long createdTs;
  private byte[] metrics;
  private Long metricsSize;
  private byte[] saveMetrics;
  private Long saveMetricsSize;
  private Integer maxResponseTimeInMs;
  private Double avgResponseTimeInMs;
  private String displayName;

  public UserResponseTime() {
  }

  @Column(name = "id")
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Transient
  @com.appiancorp.kougar.mapper.Transient
  public QName getObjectType() {
    return objectType;
  }

  public void setObjectType(QName objectType) {
    this.objectType = objectType;
  }

  @Column(name = "object_type", length = Constants.COL_MAXLEN_INDEXABLE, nullable = false)
  @com.appiancorp.kougar.mapper.Transient
  public String getObjectTypeAsString() {
    return objectType == null ? null : objectType.toString();
  }

  public void setObjectTypeAsString(String objectTypeStr) {
    objectType = QName.valueOf(objectTypeStr);
  }

  @Column(name = "object_url_stub", length = Constants.COL_MAXLEN_INDEXABLE, nullable = false)
  public String getObjectUrlStub() {
    return objectUrlStub;
  }

  public void setObjectUrlStub(String objectUrlStub) {
    this.objectUrlStub = objectUrlStub;
  }

  @Column(name = "view_url_stub", length = Constants.COL_MAXLEN_INDEXABLE, nullable = true)
  public String getViewUrlStub() {
    return viewUrlStub;
  }

  public void setViewUrlStub(String viewUrlStub) {
    this.viewUrlStub = viewUrlStub;
  }

  @Column(name = "view_name", length = Constants.COL_MAXLEN_INDEXABLE, nullable = true)
  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  @Column(name = "record_instance_id", length = Constants.COL_MAXLEN_INDEXABLE, nullable = true)
  public String getRecordInstanceId() {
    return recordInstanceId;
  }

  public void setRecordInstanceId(String recordInstanceId) {
    this.recordInstanceId = recordInstanceId;
  }

  @Column(name = "record_instance_name", length = Constants.COL_MAXLEN_INDEXABLE, nullable = true)
  public String getRecordInstanceName() {
    return recordInstanceName;
  }

  public void setRecordInstanceName(String recordInstanceName) {
    this.recordInstanceName = recordInstanceName;
  }

  @Column(name = "usr_id", nullable = false)
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "response_time_ms", nullable = false)
  public Integer getResponseTimeInMs() {
    return responseTimeInMs;
  }

  public void setResponseTimeInMs(Integer responseTimeInMs) {
    this.responseTimeInMs = responseTimeInMs;
  }

  @Column(name = "created_ts", nullable = false)
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Column(name = "metrics", nullable = false)
  @Lob
  public byte[] getMetrics() {
    return metrics;
  }

  public void setMetrics(byte[] metrics) {
    this.metrics = metrics;
  }

  @Column(name = "metrics_size", nullable = false)
  public Long getMetricsSize() {
    return metricsSize;
  }

  public void setMetricsSize(Long metricsSize) {
    this.metricsSize = metricsSize;
  }

  @Column(name = "save_metrics", nullable = true)
  @Lob
  public byte[] getSaveMetrics() {
    return saveMetrics;
  }

  public void setSaveMetrics(byte[] saveMetrics) {
    this.saveMetrics = saveMetrics;
  }

  @Column(name = "save_metrics_size", nullable = true)
  public Long getSaveMetricsSize() {
    return saveMetricsSize;
  }

  public void setSaveMetricsSize(Long saveMetricsSize) {
    this.saveMetricsSize = saveMetricsSize;
  }

  @Transient
  public Integer getMaxResponseTimeInMs() {
    return maxResponseTimeInMs;
  }

  public void setMaxResponseTimeInMs(Integer maxResponseTimeInMs) {
    this.maxResponseTimeInMs = maxResponseTimeInMs;
  }

  @Transient
  public Double getAvgResponseTimeInMs() {
    return avgResponseTimeInMs;
  }

  public void setAvgResponseTimeInMs(Double avgResponseTimeInMs) {
    this.avgResponseTimeInMs = avgResponseTimeInMs;
  }

  @Transient
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Transient
  public String getCompositeId() {
    String compositeId = objectType + ";" + objectUrlStub;
    return viewUrlStub == null ? compositeId : compositeId + ";" + viewUrlStub;
  }
}
