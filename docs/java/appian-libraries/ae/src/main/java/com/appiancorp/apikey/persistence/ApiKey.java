package com.appiancorp.apikey.persistence;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;

@Entity
@Table(name = "api_key")
public class ApiKey implements Id<Long>, ApiKeyEntity {
  private static final long serialVersionUID = 2L;
  public static final String ID = "id";
  public static final String USER_ID = "userId";
  public static final String IS_ACTIVE = "active";
  public static final String ALIAS = "alias";
  public static final String IS_REVOKED = "revoked";
  public static final String UUID = "uuid";
  public static final String SERVICE_ACCOUNT_ID = "serviceAccountId";
  public static final String OWNER = "owner";
  public static final String OWNER_ID = "ownerId";

  private Long id;
  private Long userId;
  private String alias;
  private Long createdDate;
  private Long lastUsedDate;
  private boolean isActive;
  private boolean isRevoked;
  private String uuid;
  private Long serviceAccountId;
  private ApiKeyOwner owner;
  private String ownerId;

  ApiKey() {

  }

  ApiKey(ApiKeyBuilder apiKeyBuilder) {
    this.id = apiKeyBuilder.getId();
    this.userId = apiKeyBuilder.getUserId();
    this.alias = apiKeyBuilder.getAlias();
    this.createdDate = apiKeyBuilder.getCreatedDate();
    this.lastUsedDate = apiKeyBuilder.getLastUsedDate();
    this.isActive = apiKeyBuilder.isActive();
    this.isRevoked = apiKeyBuilder.isRevoked();
    this.uuid = apiKeyBuilder.getUuid();
    this.serviceAccountId = apiKeyBuilder.getServiceAccountId();
    this.owner = apiKeyBuilder.getOwner();
    this.ownerId = apiKeyBuilder.getOwnerId();
  }

  @Override
  @javax.persistence.Id
  @Column(name = "id", nullable = false)
  @GeneratedValue
  public Long getId() {
    return id;
  }

  // createOrUpdate logic in the dao depends on id, so use this method with caution
  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "usr_id", nullable = false)
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long id) {
    this.userId = id;
  }

  @Column(name = "alias", nullable = false)
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Column(name = "created_date", nullable = false)
  public Long getCreatedDateAsLong() {
    return createdDate;
  }

  @SuppressWarnings("unused")
  private void setCreatedDateAsLong(Long createdDateAsLong) {
    this.createdDate = createdDateAsLong;
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

  @SuppressWarnings("unused")
  private void setLastUsedDateAsLong(Long lastUsedDateAsLong) {
    this.lastUsedDate = lastUsedDateAsLong;
  }

  @Transient
  public Timestamp getLastUsedDate() {
    return lastUsedDate == null ? null : new Timestamp(lastUsedDate);
  }

  public void setLastUsedDate(Date lastUsedDate) {
    this.lastUsedDate = lastUsedDate == null ? null : lastUsedDate.getTime();
  }

  @Column(name = "is_active", nullable = false)
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  @Column(name = "is_revoked", nullable = false)
  public boolean isRevoked() {
    return isRevoked;
  }

  public void setRevoked(boolean isRevoked) {
    this.isRevoked = isRevoked;
  }

  @Column(name = "uuid", nullable = false)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid= uuid;
  }

  @Column(name = "serv_acct_id", nullable = false)
  public Long getServiceAccountId() {
    return serviceAccountId;
  }

  public void setServiceAccountId(Long serviceAccountId) {
    this.serviceAccountId = serviceAccountId;
  }

  /**
   * @return the enum type that describes the owner of the ApiKey
   */
  @Transient
  public ApiKeyOwner getOwner() {
    return owner;
  }

  public void setOwner(ApiKeyOwner owner) {
    this.owner = owner;
  }

  /* These methods will be used for hibernate */
  @Column(name = "owner", nullable = false)
  private byte getOwnerByte() {
    return owner != null ? owner.getCode() : ApiKeyOwner.DESIGNER_APPLICATION.getCode();
  }

  private void setOwnerByte(byte type) {
    setOwner(ApiKeyOwner.valueOf(type));
  }

  @Column(name = "owner_id", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public ApiKey identity() {
    return this;
  }

  @Override
  public String toString() {
    return "API Key [" + alias + "]";
  }
}
