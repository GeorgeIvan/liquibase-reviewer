package com.appiancorp.oauth.inbound.persistence;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.type.Id;

@Entity
@Table(name = "oauth2_config")
public class OAuthConfig implements Id<Long>, OAuthConfigEntity {

  public static final String IS_ACTIVE_KEY = "active";
  public static final String IS_REVOKED_KEY = "revoked";
  public static final String ALIAS_KEY = "alias";
  public static final String CLIENT_ID_KEY = "client_id";

  private Long id;
  private Long createdBy;
  private String alias;
  private boolean isActive;
  private boolean isRevoked;
  private String clientId;
  private String salt;
  private Long serviceAccountId;
  private Long createdDate;
  private Long lastUsedDate;

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

  @Column(name = "created_by", nullable = false)
  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  @Column(name = "alias", nullable = false)
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  @Column(name = "client_id", nullable = false)
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @Column(name = "salt", nullable = false)
  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  @Column(name = "serv_acct_id", nullable = false)
  public Long getServiceAccountId() {
    return serviceAccountId;
  }

  public void setServiceAccountId(Long serviceAccountId) {
    this.serviceAccountId = serviceAccountId;
  }

  @Column(name = "created_date", nullable = false)
  public Long getCreatedDateAsLong() {
    return createdDate;
  }

  public void setCreatedDateAsLong(Long createdDate) {
    this.createdDate = createdDate;
  }

  @Transient
  public Timestamp getCreatedDate() {
    return new Timestamp(createdDate);
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate == null ? System.currentTimeMillis() : createdDate.getTime();
  }

  @Column(name = "last_used_date")
  public Long getLastUsedDateAsLong() {
    return lastUsedDate;
  }

  public void setLastUsedDateAsLong(Long lastUsedDate) {
    this.lastUsedDate = lastUsedDate;
  }

  @Transient
  public Timestamp getLastUsedDate() {
    return lastUsedDate == null ? null : new Timestamp(lastUsedDate);
  }

  public void setLastUsedDate(Date lastUsedDate) {
    this.lastUsedDate = lastUsedDate == null ? null : lastUsedDate.getTime();
  }

  // Do not expose credentials here!
  @Override
  public String toString() {
    return "OAuth 2 Config [clientId: " + clientId + "]";
  }

}
