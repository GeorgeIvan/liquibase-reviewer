package com.appiancorp.security.dkim;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appiancorp.core.expr.portable.Value;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "dkim_configuration")
@XmlRootElement(name = "dkimConfigurationData", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = DKIMConfigurationData.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    DKIMConfigurationData.PROP_ID, DKIMConfigurationData.PROP_DOMAIN, DKIMConfigurationData.PROP_SELECTOR,
    DKIMConfigurationData.PROP_KEY_SIZE, DKIMConfigurationData.PROP_PUBLIC_KEY,
    DKIMConfigurationData.PROP_PRIVATE_KEY, DKIMConfigurationData.PROP_VERSION,
    DKIMConfigurationData.PROP_SIGNING_ALGORITHM, DKIMConfigurationData.PROP_MESSAGE_CANONICALIZATION,
    DKIMConfigurationData.PROP_IS_VERIFIED, DKIMConfigurationData.PROP_LAST_VERIFICATION_TIME})
@XmlAccessorType(XmlAccessType.FIELD)
@IgnoreJpa
public class DKIMConfigurationData implements Id<Long> {
  private static final long serialVersionUID = 1L;
  private static final int COL_LEN = 2048;
  public static final String LOCAL_PART = "DKIMConfigurationData";
  public static final String PROP_ID = "id";
  public static final String PROP_DOMAIN = "domain";
  public static final String PROP_SELECTOR = "selector";
  public static final String PROP_KEY_SIZE = "keySize";
  public static final String PROP_PUBLIC_KEY = "publicKey";
  public static final String PROP_PRIVATE_KEY = "privateKey";
  public static final String PROP_VERSION = "version";
  public static final String PROP_SIGNING_ALGORITHM = "signingAlgorithm";
  public static final String PROP_MESSAGE_CANONICALIZATION = "messageCanonicalization";
  public static final String PROP_IS_VERIFIED = "isVerified";
  public static final String PROP_LAST_VERIFICATION_TIME = "lastVerificationTime";

  private Long id;
  private String domain;
  private String selector;
  private int keySize;
  private String publicKey;
  private String privateKey;
  private String version;
  private String signingAlgorithm;
  private String messageCanonicalization;
  private boolean isVerified;
  private Long lastVerificationTime;

  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "domain", nullable = false, unique = true)
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Column(name = "selector", nullable = false)
  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  @Column(name = "key_size", nullable = false)
  public int getKeySize() {
    return keySize;
  }

  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }

  @Column(name = "public_key", nullable = false, length = COL_LEN)
  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  @Column(name = "private_key", nullable = false, length = COL_LEN)
  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  @Column(name = "version", nullable = false)
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Column(name = "signing_algorithm", nullable = false)
  public String getSigningAlgorithm() {
    return signingAlgorithm;
  }

  public void setSigningAlgorithm(String signingAlgorithm) {
    this.signingAlgorithm = signingAlgorithm;
  }

  @Column(name = "message_canonicalization", nullable = false)
  public String getMessageCanonicalization() {
    return messageCanonicalization;
  }

  public void setMessageCanonicalization(String messageCanonicalization) {
    this.messageCanonicalization = messageCanonicalization;
  }

  @Column(name = "is_verified", nullable = false)
  public boolean getIsVerified() {
    return isVerified;
  }

  public void setIsVerified(boolean isVerified) {
    this.isVerified = isVerified;
  }

  @Column(name = "last_verification_time")
  public Long getLastVerificationTime() {
    if (lastVerificationTime == null) {
      return null;
    }
    return lastVerificationTime;
  }

  public void setLastVerificationTime(Long lastVerificationTime) {
    if (lastVerificationTime == null) {
      this.lastVerificationTime = null;
      return;
    }
    this.lastVerificationTime = lastVerificationTime;
  }

  @Transient
  public Value getValue() {
    com.appiancorp.type.cdt.value.DKIMConfigurationData dkimConfigurationData = new com.appiancorp.type.cdt.value.DKIMConfigurationData();
    dkimConfigurationData.setId(this.getId());
    dkimConfigurationData.setDomain(this.getDomain());
    dkimConfigurationData.setSelector(this.getSelector());
    dkimConfigurationData.setPublicKey(this.getPublicKey());
    dkimConfigurationData.setPrivateKey(this.getPrivateKey());
    dkimConfigurationData.setKeySize(this.getKeySize());
    dkimConfigurationData.setVersion(this.getVersion());
    dkimConfigurationData.setSigningAlgorithm(this.getSigningAlgorithm());
    dkimConfigurationData.setMessageCanonicalization(this.getMessageCanonicalization());
    dkimConfigurationData.setIsVerified(this.getIsVerified());
    dkimConfigurationData.setLastVerificationTime(getTimestamp(this.getLastVerificationTime()));

    return dkimConfigurationData.toValue();
  }

  @Transient
  private Timestamp getTimestamp(Long milliseconds) {
    if (milliseconds == null) {
      return null;
    }
    return new Timestamp(milliseconds);
  }
}
