package com.appiancorp.security.symmetric;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.appiancorp.type.Id;

@Entity
@Table(name = "symmetric_keys")
public class SymmetricKeyData implements Id<Long> {
  private static final long serialVersionUID = 1L;

  public static final String PROP_NAME = "keyName";
  public static final String PROP_KEY_VERSION = "keyVersion";
  public static final String PROP_KEY_TYPE = "keyType";

  private Long id;
  private String keyName;
  private Long keyVersion;
  private Instant creationTimestamp;
  private Instant originatorUseEndTimestamp;
  private Instant recipientUseEndTimestamp;
  private SymmetricKeyType keyType;
  private byte[] serializedKey;

  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "key_name", nullable = false)
  public String getKeyName() {
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  @Column(name = "key_version", nullable = false)
  public Long getKeyVersion() {
    return keyVersion;
  }

  public void setKeyVersion(Long keyVersion) {
    this.keyVersion = keyVersion;
  }

  @Transient
  public Instant getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(Instant creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  @Column(name = "creation_ts", nullable = false)
  public Long getCreationTimestampAsLong() {
    return creationTimestamp == null ? null : creationTimestamp.toEpochMilli();
  }

  public void setCreationTimestampAsLong(Long timeStamp) {
    this.creationTimestamp = timeStamp == null ? null : Instant.ofEpochMilli(timeStamp);
  }

  @Transient
  public Instant getOriginatorUseEndTimestamp() {
    return originatorUseEndTimestamp;
  }

  public void setOriginatorUseEndTimestamp(Instant originatorUseEndTimestamp) {
    this.originatorUseEndTimestamp = originatorUseEndTimestamp;
  }

  @Column(name = "originator_use_end_ts", nullable = true)
  public Long getOriginatorUseEndTimestampAsLong() {
    return originatorUseEndTimestamp == null ? null : originatorUseEndTimestamp.toEpochMilli();
  }

  public void setOriginatorUseEndTimestampAsLong(Long timeStamp) {
    this.originatorUseEndTimestamp = timeStamp == null ? null : Instant.ofEpochMilli(timeStamp);
  }

  @Transient
  public Instant getRecipientUseEndTimestamp() {
    return recipientUseEndTimestamp;
  }

  public void setRecipientUseEndTimestamp(Instant recipientUseEndTimestamp) {
    this.recipientUseEndTimestamp = recipientUseEndTimestamp;
  }

  @Column(name = "recipient_use_end_ts", nullable = true)
  public Long getRecipientUseEndTimestampAsLong() {
    return recipientUseEndTimestamp == null ? null : recipientUseEndTimestamp.toEpochMilli();
  }

  public void setRecipientUseEndTimestampAsLong(Long timeStamp) {
    recipientUseEndTimestamp = timeStamp == null ? null : Instant.ofEpochMilli(timeStamp);
  }

  public void setKeyType(@NotNull SymmetricKeyType keyType) {
    this.keyType = keyType;
  }

  @Convert(converter =  SymmetricKeyType.SymmetricKeyTypeConverter.class)
  @Column(name = "key_type", nullable = false)
  public SymmetricKeyType getKeyType() {
    return this.keyType;
  }

  @Column(name = "serialized_key", nullable = false)
  @Lob
  public byte[] getSerializedKey() {
    return serializedKey;
  }

  public void setSerializedKey(byte[] encodedKey) {
    this.serializedKey = encodedKey;
  }

}
