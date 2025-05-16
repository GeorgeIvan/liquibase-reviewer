package com.appiancorp.portal.persistence;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.appian.core.persist.Constants;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.type.Id;

/**
 * Information about the publishing status of a Portal. This data is stored in a separate database row for
 * two reasons:
 *
 *   (1) We want to easily update publish information without updating the audit trail on the Portal
 *       design object. This is simpler if the publish info doesn't share the same DB row.
 *
 *   (2) We'd like the publish info to outlive the Portal design object, so that a user can e.g. delete
 *       three Portal objects and all of our cleanup/unpublish activity can happen in a background thread.
 *
 * The expected lifecycle of a PortalPublishInfo is:
 *
 *   - It is created at the same time a Portal is created and has a 1:1 relationship to that Portal
 *
 *   - It survives (for awhile) even after the related Portal is deleted
 *
 *   - It is eventually deleted by background processing, once the related serverless webapp (if any) is
 *     unpublished
 */
@Entity
@Table(name = "portal_publish_info")
public class PortalPublishInfo implements HasAuditInfo, Id<Long> {
  public static final String PROP_PORTAL_UUID = "portalUuid";
  public static final String PROP_SERVERLESS_WEBAPP_UUID = "serverlessWebappUuid";
  public static final String PROP_PORTAL_STATUS = "statusByte";

  private Long id;
  private String portalUuid;
  private PortalStatus status;
  private String serverlessWebappUuid;
  private String lastComputedUrlStub;
  private String lastFullUrl;
  private String testUuid;
  private long targetTag;
  private long appliedTag;
  private long version;
  private String mostRecentErrorJson;
  private String affectedPrecedentUuid;
  private String affectedPrecedentType;
  private PublishReason publishReason;
  private AuditInfo auditInfo = new AuditInfo();

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "portal_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getPortalUuid() {
    return portalUuid;
  }

  public void setPortalUuid(String portalUuid) {
    this.portalUuid = portalUuid;
  }

  @Transient
  public PortalStatus getStatus() {
    return status;
  }

  public void setStatus(PortalStatus status) {
    this.status = status;
  }

  @Column(name = "status", nullable = false)
  public byte getStatusByte() {
    return (byte) status.ordinal();
  }

  public void setStatusByte(byte statusByte) {
    this.status = PortalStatus.values()[statusByte];
  }

  @Column(name = "serverless_webapp_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  public String getServerlessWebappUuid() {
    return serverlessWebappUuid;
  }

  public void setServerlessWebappUuid(String serverlessWebappUuid) {
    this.serverlessWebappUuid = serverlessWebappUuid;
  }

  @Column(name = "last_computed_url_stub", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getLastComputedUrlStub() {
    return lastComputedUrlStub;
  }

  public void setLastComputedUrlStub(String lastComputedUrlStub) {
    this.lastComputedUrlStub = lastComputedUrlStub;
  }

  @Column(name = "last_full_url", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getLastFullUrl() {
    return lastFullUrl;
  }

  public void setLastFullUrl(String lastFullUrl) {
    this.lastFullUrl = lastFullUrl;
  }

  @Column(name = "test_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  public String getTestUuid() {
    return testUuid;
  }

  public void setTestUuid(String testUuid) {
    this.testUuid = testUuid;
  }

  @Column(name = "target_tag", nullable = false)
  public long getTargetTag() {
    return targetTag;
  }

  public void setTargetTag(long targetTag) {
    this.targetTag = targetTag;
  }

  @Column(name = "applied_tag", nullable = false)
  public long getAppliedTag() {
    return appliedTag;
  }

  public void setAppliedTag(long appliedTag) {
    this.appliedTag = appliedTag;
  }

  @Version
  @Column(name = "version", nullable = false)
  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Lob
  @Column(name = "most_recent_error", nullable = true)
  public String getMostRecentErrorJson() {
    return mostRecentErrorJson;
  }

  public void setMostRecentErrorJson(String mostRecentErrorJson) {
    this.mostRecentErrorJson = mostRecentErrorJson;
  }

  @Column(name = "affected_precedent_uuid", nullable = true)
  public String getAffectedPrecedentUuid() {
    return affectedPrecedentUuid;
  }

  public void setAffectedPrecedentUuid(String affectedPrecedentUuid) {
    this.affectedPrecedentUuid = affectedPrecedentUuid;
  }

  @Column(name = "affected_precedent_type", nullable = true)
  public String getAffectedPrecedentType() {
    return affectedPrecedentType;
  }

  public void setAffectedPrecedentType(String affectedPrecedentTypeQNameString) {
    this.affectedPrecedentType = affectedPrecedentTypeQNameString;
  }

  @Transient
  public PublishReason getPublishReason() {
    return publishReason;
  }

  public void setPublishReason(PublishReason publishReason) {
    this.publishReason = publishReason;
  }

  @Column(name = "publish_reason", nullable = false)
  private byte getPublishReasonByte() {
    return publishReason != null ? publishReason.getIndex() : PublishReason.UNKNOWN.getIndex();
  }

  public void setPublishReasonByte(byte publishReasonByte) {
    setPublishReason(PublishReason.valueOf(publishReasonByte));
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Transient
  public boolean isPortalPublishedOrRepublished() {
    return Arrays.asList(PortalStatus.PUBLISH_REQUEST_SENT, PortalStatus.REPUBLISH_REQUEST_WITH_SAME_URL_SENT,
        PortalStatus.REPUBLISH_REQUEST_WITH_NEW_URL_SENT).contains(status);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PortalPublishInfo that = (PortalPublishInfo)o;
    return id.equals(that.id) && portalUuid.equals(that.portalUuid) && status == that.status &&
        Objects.equals(serverlessWebappUuid, that.serverlessWebappUuid) &&
        Objects.equals(lastComputedUrlStub, that.lastComputedUrlStub) &&
        Objects.equals(lastFullUrl, that.lastFullUrl) && testUuid.equals(that.testUuid) &&
        targetTag == that.targetTag && appliedTag == that.appliedTag && version == that.version &&
        auditInfo.equals(that.auditInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, portalUuid, status, serverlessWebappUuid, lastComputedUrlStub, lastFullUrl,
        testUuid, targetTag, appliedTag, version, auditInfo);
  }
}
