package com.appiancorp.oauth.inbound.persistence;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.type.Id;
import com.appiancorp.security.audit.HasAuditInfo;

@Entity
@Table(name = "third_party_oauth2_config")
public class ThirdPartyOAuthConfig implements Id<Long>, ThirdPartyOAuthConfigEntity, HasAuditInfo {

  public static final String IS_ACTIVE_KEY = "active";
  public static final String IS_REVOKED_KEY = "revoked";
  public static final String DESCRIPTION_KEY = "description";
  public static final String ISSUER_KEY = "issuer";
  public static final String CLIENT_ID_KEY = "clientId";

  private Long id;
  private boolean isActive;
  private boolean isRevoked;
  private String description;
  private String issuer;
  private String audience;
  private String jwkSetFileId;
  private String jwkSetUri;
  private Long jwkLastUpdateDate;
  private Long serviceAccountId;
  private String clientId;
  private String clientIdMapping;
  private String jwkSetSourceType;
  private Long lastUsedDate;
  private AuditInfo auditInfo = new AuditInfo();

  @Override
  @javax.persistence.Id
  @Column(name = "id", nullable = false)
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "is_active", nullable = false)
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  @Column(name = "is_revoked", nullable = false)
  public boolean isRevoked() {
    return isRevoked;
  }

  public void setRevoked(boolean revoked) {
    isRevoked = revoked;
  }

  @Column(name = DESCRIPTION_KEY, nullable = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = ISSUER_KEY, nullable = false)
  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  @Column(name = "audience", nullable = false)
  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  @Column(name = "jwk_set_file_id", nullable = true)
  public String getJwkSetFileId() {
    return jwkSetFileId;
  }

  public void setJwkSetFileId(String jwkSetFileId) {
    this.jwkSetFileId = jwkSetFileId;
  }

  @Column(name = "jwk_set_uri", nullable = true)
  public String getJwkSetUri() {
    return jwkSetUri;
  }

  public void setJwkSetUri(String jwkSetUri) {
    this.jwkSetUri = jwkSetUri;
  }

  @Column(name = "svc_acc_id", nullable = false)
  public Long getServiceAccountId() {
    return serviceAccountId;
  }

  public void setServiceAccountId(Long serviceAccountId) {
    this.serviceAccountId = serviceAccountId;
  }

  @Column(name = "client_id", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @Column(name = "client_id_mapping", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getClientIdMapping() {
    return clientIdMapping;
  }

  public void setClientIdMapping(String clientIdMapping) {
    this.clientIdMapping = clientIdMapping;
  }

  @Column(name = "jwk_set_source_type", nullable = false, length = 20)
  public String getJwkSetSourceType() {
    return jwkSetSourceType;
  }

  public void setJwkSetSourceType(String jwkSetSourceType) {
    this.jwkSetSourceType = jwkSetSourceType;
  }
  @Transient
  public Timestamp getLastUsedDate() {
    return lastUsedDate == null ? null : new Timestamp(lastUsedDate);
  }

  public void setLastUsedDate(Date lastUsedDate) {
    this.lastUsedDate = lastUsedDate == null ? null : lastUsedDate.getTime();
  }

  @Column(name = "last_used_ts", nullable = true)
  public Long getLastUsedDateAsLong() {
    return lastUsedDate;
  }

  public void setLastUsedDateAsLong(Long lastUsedDate) {
    this.lastUsedDate = lastUsedDate;
  }

  @Transient
  public Timestamp getJwkLastUpdateDate() {
    return new Timestamp(jwkLastUpdateDate);
  }

  public void setJwkLastUpdateDate(Date jwkLastUpdateDate) {
    this.jwkLastUpdateDate = jwkLastUpdateDate == null ? System.currentTimeMillis() : jwkLastUpdateDate.getTime();
  }

  @Column(name = "jwk_last_update_date", nullable = true)
  public Long getJwkLastUpdateDateAsLong() {
    return jwkLastUpdateDate;
  }

  public void setJwkLastUpdateDateAsLong(Long jwkLastUpdateDate) {
    this.jwkLastUpdateDate = jwkLastUpdateDate;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }
}
