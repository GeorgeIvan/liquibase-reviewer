package com.appiancorp.oauth.inbound.authserver.persistence.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.oauth.inbound.authserver.tokens.TokenFamily;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;

@Entity
@Table(name = "oauth_inbound_token_family")
public class TokenFamilyEntity implements TokenFamily, HasAuditInfo, Serializable {

  public static final String TABLE_NAME = "oauth_inbound_token_family";
  public static final String PROP_AUTH_CODE_HASH_KEY = "authCodeHash";
  public static final String PROP_REFRESH_TOKEN_ID_KEY = "refreshTokenId";
  public static final String PROP_REFRESH_TOKEN_EXP_KEY = "refreshTokenExpirationTs";
  public static final int COL_MAXLEN_STRING = 255;

  private String id;
  private String authCodeHash;
  private Long authCodeExpirationTs;
  private String scopes;
  private String codeChallenge;
  private String redirectUri;
  private String sessionId;
  private String userUuid;
  private Boolean isAuthCodeUsed;
  private String refreshTokenId;
  private Long refreshTokenExpirationTs;

  @Override
  @Id
  @Column(name = "id", unique = true, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  @Column(name = "auth_code_hash", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAuthCodeHash() {
    return authCodeHash;
  }

  public void setAuthCodeHash(String authCodeHash) {
    this.authCodeHash = authCodeHash;
  }

  @Override
  @Column(name = "auth_code_expiration_ts", nullable = true)
  public Long getAuthCodeExpirationTs() {
    return authCodeExpirationTs;
  }

  public void setAuthCodeExpirationTs(Long authCodeExpirationTs) {
    this.authCodeExpirationTs = authCodeExpirationTs;
  }

  @Override
  @Column(name = "scopes", nullable = true, length = COL_MAXLEN_STRING)
  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  @Override
  @Column(name = "code_challenge", nullable = true, length = COL_MAXLEN_STRING)
  public String getCodeChallenge() {
    return codeChallenge;
  }

  public void setCodeChallenge(String codeChallenge) {
    this.codeChallenge = codeChallenge;
  }

  @Override
  @Column(name = "redirect_uri", nullable = true, length = COL_MAXLEN_STRING)
  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  @Override
  @Column(name = "session_id", nullable = false, length = COL_MAXLEN_STRING)
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  @Column(name = "user_uuid", nullable = false, length = COL_MAXLEN_STRING)
  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  @Override
  @Column(name = "is_auth_code_used", nullable = true)
  public Boolean isAuthCodeUsed() {
    return isAuthCodeUsed;
  }

  public void setAuthCodeUsed(Boolean authCodeUsed) {
    this.isAuthCodeUsed = authCodeUsed;
  }

  @Override
  @Column(name = "refresh_token_id", nullable = true, length = COL_MAXLEN_STRING)
  public String getRefreshTokenId() {
    return refreshTokenId;
  }

  public void setRefreshTokenId(String refreshTokenId) {
    this.refreshTokenId = refreshTokenId;
  }

  @Override
  @Column(name = "refresh_token_expiration_ts", nullable = true)
  public Long getRefreshTokenExpirationTs() {
    return refreshTokenExpirationTs;
  }

  public void setRefreshTokenExpirationTs(Long refreshTokenExpirationTs) {
    this.refreshTokenExpirationTs = refreshTokenExpirationTs;
  }

  private AuditInfo auditInfo = new AuditInfo();

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  public String toString() {
    return "TokenFamilyEntity[id=" + id + "]";
  }

  @Override
  public TokenFamilyEntity copy() {
    return (TokenFamilyEntity)TokenFamilyBuilder.anOAuthInboundTokenFamily()
        .withId(this.id)
        .withAuthCodeHash(this.authCodeHash)
        .withAuthCodeExpirationTs(this.authCodeExpirationTs)
        .withScopes(this.scopes)
        .withCodeChallenge(this.codeChallenge)
        .withRedirectUri(this.redirectUri)
        .withSessionId(this.sessionId)
        .withUserUuid(this.userUuid)
        .withIsAuthCodeUsed(this.isAuthCodeUsed)
        .withRefreshTokenId(this.refreshTokenId)
        .withRefreshTokenExpirationTs(this.refreshTokenExpirationTs)
        .build();
  }
}
