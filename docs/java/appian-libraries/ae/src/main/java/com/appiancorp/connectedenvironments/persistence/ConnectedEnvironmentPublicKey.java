package com.appiancorp.connectedenvironments.persistence;

import java.security.PublicKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.connectedenvironments.KeyUtils;
import com.appiancorp.type.Id;

@Entity
@Table(name = "connected_env_public_key")
public class ConnectedEnvironmentPublicKey implements Id<Long> {
  private static final long serialVersionUID = 1L;
  public static final String PROP_URL = "url";

  private Long id;
  private PublicKey publicKey;
  private String url;

  public ConnectedEnvironmentPublicKey(PublicKey publicKey, String url) {
    this.publicKey = publicKey;
    this.url = url;
  }

  public ConnectedEnvironmentPublicKey() {
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

  @Lob
  @Column(name = "public_key", nullable = false)
  private byte[] getSerializedPublicKey() {
    return publicKey == null ? null : KeyUtils.encodeKey(publicKey);
  }

  @SuppressWarnings("unused")
  private void setSerializedPublicKey(byte[] serializedPublicKey) {
    try {
      this.publicKey = serializedPublicKey == null ? null : KeyUtils.decodePublicKey(serializedPublicKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transient
  public PublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(PublicKey publicKey) {
    this.publicKey = publicKey;
  }

  @Column(name = "url", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
