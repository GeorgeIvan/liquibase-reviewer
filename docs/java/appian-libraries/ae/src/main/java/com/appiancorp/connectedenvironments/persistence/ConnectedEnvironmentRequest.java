package com.appiancorp.connectedenvironments.persistence;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.google.common.base.Objects;

@Entity
@Table(name = "connected_env_req")
public class ConnectedEnvironmentRequest implements Id<Long>, Comparable<ConnectedEnvironmentRequest> {
  private static final long serialVersionUID = 1L;
  public static final String PROP_URL = "url";
  public static final String PROP_REQUEST_TYPE = "requestType";
  public static final String PROP_NONCE = "nonce";

  private Long id;
  private String name;
  private String url;
  private RequestType requestType;
  private Status status;
  private Long initiatedDate;
  private String initiatorName;
  private String initiatorUsername;
  private String initiatorIp;
  private String approverName;
  private String approverUsername;
  private String approverIp;
  private Long decisionDate;
  private String nonce;
  private Long expirationDate;

  public enum RequestType {
    OUTGOING, INCOMING, ESTABLISHED
  }

  public enum Status {
    APPROVED, DENIED, TIMED_OUT, SENT, RESPONSE_ERROR, RECEIVED, WITHDRAWN, ENABLED, DISABLED, DELETED
  }

  private ConnectedEnvironmentRequest() {
  }

  protected ConnectedEnvironmentRequest(ConnectedEnvironmentRequestBuilder builder) {
    this.name = builder.getName();
    this.url = builder.getUrl();
    this.requestType = builder.getRequestType();
    this.status = builder.getStatus();
    this.initiatedDate = builder.getInitiatedDate();
    this.initiatorName = builder.getInitiatorName();
    this.initiatorUsername = builder.getInitiatorUsername();
    this.initiatorIp = builder.getInitiatorIp();
    this.approverName =  builder.getApproverName();
    this.approverUsername = builder.getApproverUsername();
    this.approverIp = builder.getApproverIp();
    this.decisionDate = builder.getDecisionDate();
    this.nonce = builder.getNonce();
    this.expirationDate = builder.getExpirationDate();
  }

  @Override
  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "name", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "url", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "request_type", nullable = false)
  public RequestType getRequestType() {
    return requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "initiated_date", nullable = false)
  private Long getInitiatedDateAsLong() {
    return initiatedDate;
  }

  @SuppressWarnings("unused")
  private void setInitiatedDateAsLong(Long initiatedDateAsLong) {
    this.initiatedDate = initiatedDateAsLong;
  }

  @Transient
  public Date getInitiatedDate() {
    return initiatedDate == null ? null : new Date(initiatedDate);
  }

  public void setInitiatedDate(Date initiatedDate) {
    this.initiatedDate = initiatedDate == null ? null : initiatedDate.getTime();
  }

  @Column(name = "initiator_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getInitiatorName() {
    return initiatorName;
  }

  public void setInitiatorName(String initiatorName) {
    this.initiatorName = initiatorName;
  }

  @Column(name = "initiator_username", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getInitiatorUsername() {
    return initiatorUsername;
  }

  public void setInitiatorUsername(String initiatorUsername) {
    this.initiatorUsername = initiatorUsername;
  }

  @Column(name = "initiator_ip", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getInitiatorIp() {
    return initiatorIp;
  }

  public void setInitiatorIp(String initiatorIp) {
    this.initiatorIp = initiatorIp;
  }

  @Column(name = "approver_name", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getApproverName() {
    return approverName;
  }

  public void setApproverName(String approverName) {
    this.approverName = approverName;
  }

  @Column(name = "approver_username", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getApproverUsername() {
    return approverUsername;
  }

  public void setApproverUsername(String approverUsername) {
    this.approverUsername = approverUsername;
  }

  @Column(name = "approver_ip", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getApproverIp() {
    return approverIp;
  }

  public void setApproverIp(String approverIp) {
    this.approverIp = approverIp;
  }

  @Column(name = "decision_date", nullable = true)
  private Long getDecisionDateAsLong() {
    return decisionDate;
  }

  @SuppressWarnings("unused")
  private void setDecisionDateAsLong(Long decisionDateAsLong) {
    this.decisionDate = decisionDateAsLong;
  }

  @Transient
  public Date getDecisionDate() {
    return decisionDate == null ? null : new Date(decisionDate);
  }

  public void setDecisionDate(Date decisionDate) {
    this.decisionDate = decisionDate == null ? null : decisionDate.getTime();
  }

  @Column(name = "nonce", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  @Column(name = "expiration_date", nullable = false)
  private Long getExpirationDateAsLong() {
    return expirationDate;
  }

  @SuppressWarnings("unused")
  private void setExpirationDateAsLong(Long expirationDateAsLong) {
    this.expirationDate = expirationDateAsLong;
  }

  @Transient
  public Date getExpirationDate() {
    return expirationDate == null ? null : new Date(expirationDate);
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate == null ? null : expirationDate.getTime();
  }

  @Override
  public int compareTo(ConnectedEnvironmentRequest o) {
    return this.getInitiatedDate().compareTo(o.getInitiatedDate());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConnectedEnvironmentRequest that = (ConnectedEnvironmentRequest)o;
    return Objects.equal(id, that.id) && Objects.equal(name, that.name) && Objects.equal(url, that.url) &&
        requestType == that.requestType && status == that.status &&
        Objects.equal(initiatedDate, that.initiatedDate) &&
        Objects.equal(initiatorName, that.initiatorName) &&
        Objects.equal(initiatorUsername, that.initiatorUsername) &&
        Objects.equal(initiatorIp, that.initiatorIp) && Objects.equal(approverName, that.approverName) &&
        Objects.equal(approverUsername, that.approverUsername) &&
        Objects.equal(approverIp, that.approverIp) && Objects.equal(decisionDate, that.decisionDate) &&
        Objects.equal(nonce, that.nonce) && Objects.equal(expirationDate, that.expirationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, url, requestType, status, initiatedDate, initiatorName,
        initiatorUsername, initiatorIp, approverName, approverUsername, approverIp, decisionDate, nonce,
        expirationDate);
  }
}
