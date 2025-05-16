package com.appiancorp.security.ssl;

import java.sql.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.appian.core.persist.Constants;
import com.appiancorp.common.xml.DateAdapter;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "certificates")
@XmlRootElement(name = "certificateData", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = CertificateData.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "id",
    "alias",
    "commonName",
    "issuer",
    "keyType",
    "dateOfIssue",
    "dateOfExpiration",
    "thumbprint",
    "serialNumber",
    "certType"
    })
@XmlAccessorType(XmlAccessType.FIELD)
@IgnoreJpa
public class CertificateData implements Id<Long> {
  private static final long serialVersionUID = 3L;
  private static final int COL_LEN_1MB = 1048576;

  public static final String LOCAL_PART = "CertificateData";
  public static final String PROP_ALIAS = "alias";
  public static final String PROP_KEYTYPE = "keyType";
  public static final String PROP_ISSUER = "issuer";
  public static final String PROP_CERT_TYPE = "certType";

  private Long id;
  private String alias;

  private String commonName;
  private String issuer;
  private String keyType;
  @XmlTransient
  private Date dateOfIssue;
  @XmlTransient
  private Date dateOfExpiration;
  private String thumbprint;
  private String serialNumber;
  @XmlTransient
  private byte[] serializedKey;
  @XmlTransient
  private List<byte[]> certChain;

  @Hidden
  @XmlType(name = "CertificateType", namespace = Type.APPIAN_NAMESPACE)
  @XmlEnum
  public enum CertificateType {
    CLIENT(true),
    SAML(true),
    DKIM(false),
    TRUSTED(true),
    CE_PRIVATE(true),
    CE_PUBLIC(true),
    API_KEY(false),
    OAUTH_TOKEN_PRIVATE(true),
    OAUTH_TOKEN_PUBLIC(true),
    OAUTH_SECRET_PRIVATE(true),
    OAUTH_SECRET_PUBLIC(true),
    REMOTE_FRAMEWORKS_PRIVATE(false),
    REMOTE_FRAMEWORKS_PUBLIC(true),
    REMOTE_FRAMEWORKS_LAST_SUCCESSFUL_PRIVATE(false),
    PORTAL_AUTH_PAIR(false),
    MTLS_FOR_WEB_API_CLIENT(true);

    private final boolean shouldLog;

    CertificateType(boolean shouldLog) {
      this.shouldLog = shouldLog;
    }

    public boolean shouldLog() {
      return shouldLog;
    }

    public String value() {
      return name();
    }
  }

  @XmlElement(required = true)
  private CertificateType certType;

  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "alias", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Column(name = "common_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  @Column(name = "issuer", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  @Column(name="key_type", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getKeyType() {
    return keyType;
  }

  public void setKeyType(String keyType) {
    this.keyType = keyType;
  }

  /*
   * Date of Issue
   */
  @Column(name="issue_date", nullable = false)
  private Long getDateOfIssueAsLong() {
    return dateOfIssue == null ? null : dateOfIssue.getTime();
  }

  @SuppressWarnings("unused")
  private void setDateOfIssueAsLong(Long issue) {
    this.dateOfIssue = issue == null ? null : new Date(issue);
  }

  @Transient
  @XmlSchemaType(name="date")
  @XmlJavaTypeAdapter(DateAdapter.class)
  public Date getDateOfIssue() {
    return dateOfIssue;
  }

  public void setDateOfIssue(Date dateOfIssue) {
    this.dateOfIssue = dateOfIssue;
  }

  /*
   * Date of Expiration
   */
  @Column(name="expiration_date", nullable = false)
  private Long getDateOfExpirationAsLong() {
    return dateOfExpiration == null ? null : dateOfExpiration.getTime();
  }

  @SuppressWarnings("unused")
  private void setDateOfExpirationAsLong(Long expiration) {
    this.dateOfExpiration = expiration == null ? null : new Date(expiration);
  }

  @Transient
  @XmlSchemaType(name="date")
  @XmlJavaTypeAdapter(DateAdapter.class)
  public Date getDateOfExpiration() {
    return dateOfExpiration;
  }

  public void setDateOfExpiration(Date dateOfExpiration) {
    this.dateOfExpiration = dateOfExpiration;
  }

  @Column(name = "thumbprint", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getThumbprint() {
    return thumbprint;
  }

  public void setThumbprint(String thumbprint) {
    this.thumbprint = thumbprint;
  }

  @Column(name = "serial_num", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setCertType(@NotNull CertificateType certType) {
    this.certType = certType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "cert_type", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public CertificateType getCertType() {
    return this.certType;
  }

  /**
   * Serialized private key
   *
   * NOTE: The length is defined to force the resulting column to be a medium-sized BLOB.
   */
  @Column(name = "serialized_key", nullable = false, length = COL_LEN_1MB)
  @Lob
  public byte[] getSerializedKey() {
    return serializedKey;
  }

  public void setSerializedKey(byte[] encodedKey) {
    this.serializedKey = encodedKey;
  }

  /*
   * Certificate chain
   */
  @OrderColumn(name="order_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "certificate_chains",
      joinColumns = @JoinColumn(name = "certificate_id")
  )
  @Cascade({CascadeType.ALL})
  @Column(name= "cert", nullable = false, length = COL_LEN_1MB)
  @org.hibernate.annotations.Type(type = "com.appiancorp.rdbms.hb.type.JdkSerializedMaterializedBlobType")
  @Lob
  public List<byte[]> getSerializedCertificateChain() {
    return certChain;
  }

  public void setSerializedCertificateChain(List<byte[]> certChain) {
    this.certChain = certChain;
  }
}
