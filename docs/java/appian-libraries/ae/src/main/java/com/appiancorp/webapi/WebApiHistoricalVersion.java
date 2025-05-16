package com.appiancorp.webapi;

import static java.util.Objects.requireNonNull;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.google.common.base.Objects;

@Hidden
@Entity
@Table(name = WebApiHistoricalVersion.TABLE_NAME)
public class WebApiHistoricalVersion implements Id<Long>, Name, Uuid<String>, HasWebApiExpression {

  public static final String TABLE_NAME = "web_api_history";
  public static final String LOCAL_PART = "WebApiHistoricalVersion";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_WEB_API_ID = "webApiId";
  public static final String PROP_UUID = "uuid";
  public static final String PROP_NAME = "name";
  public static final String PROP_URL_ALIAS = "urlAlias";
  public static final String PROP_HTTP_METHOD = "httpMethod";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_EXPRESSION = "expression";
  public static final String PROP_VERSION_NUMBER = "versionNumber";
  public static final String PROP_VERSION_UUID = "versionUuid";
  public static final String PROP_REQUEST_BODY_TYPE = "requestBodyType";
  public static final String PROP_RECEIVE_DOCUMENTS_FOLDER = "receiveDocumentsFolder";
  public final static String PROP_LOGGING_ENABLED = "loggingEnabled";

  private Long id;
  private Long webApiId;
  private String uuid;
  private String name;
  private String urlAlias;
  private String httpMethod;
  private String description;
  private String expression;
  private AuditInfo auditInfo;
  private Integer versionNumber;
  private Integer latestVersionNumber;
  private String versionUuid;
  private String requestBodyType;
  private Long receiveDocumentsFolder;
  private Boolean loggingEnabled = Boolean.FALSE;

  public WebApiHistoricalVersion() {
  }

  public WebApiHistoricalVersion(final WebApi webApi) {
    this.webApiId = requireNonNull(webApi.getId());
    this.uuid = requireNonNull(webApi.getUuid());
    this.name = requireNonNull(webApi.getName());
    this.urlAlias = requireNonNull(webApi.getUrlAlias());
    this.httpMethod = requireNonNull(webApi.getHttpMethod());
    this.description = webApi.getDescription();
    this.expression = webApi.getExpression();
    requireNonNull(webApi.getAuditInfo());
    this.auditInfo = new AuditInfo(webApi.getAuditInfo());
    this.versionUuid = webApi.getVersionUuid();
    this.requestBodyType = webApi.getRequestBodyType();
    this.loggingEnabled = requireNonNull(webApi.getLoggingEnabled());
    this.receiveDocumentsFolder = webApi.getReceiveDocumentsFolder();
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * This maps to the primary key of the web_api table, pointing to the current version. There may be multiple
   * historical versions pointing to the same current version in web_api.
   *
   * @return Long - the id of the web_api row that this historical version is pointing to
   */
  @Column(name = "web_api_id", updatable = false, nullable = false)
  public Long getWebApiId() {
    return webApiId;
  }

  public void setWebApiId(final Long webApiId) {
    this.webApiId = webApiId;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Column(name = "name", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Column(name = "url_alias", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUrlAlias() {
    return urlAlias;
  }

  public void setUrlAlias(final String urlAlias) {
    this.urlAlias = urlAlias;
  }

  @Column(name = "http_method", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(final String httpMethod) {
    this.httpMethod = httpMethod;
  }

  @Column(name = "description", updatable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  @Column(name = "expression", updatable = false, nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getExpression() {
    return expression;
  }

  @Override
  public void setExpression(final String expression) {
    this.expression = expression;
  }

  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(final String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Transient
  public Integer getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(final Integer versionNumber) {
    this.versionNumber = versionNumber;
  }

  @Transient
  public Integer getLatestVersionNumber() {
    return latestVersionNumber;
  }

  public void setLatestVersionNumber(final Integer latestVersionNumber) {
    this.latestVersionNumber = latestVersionNumber;
  }

  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(final AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public String toString() {
    return "WebApiHistoricalVersion{" +
        "id=" + id +
        ", webApiId=" + webApiId +
        ", uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", urlAlias='" + urlAlias + '\'' +
        ", httpMethod='" + httpMethod + '\'' +
        ", versionNumber='" + versionNumber + '\'' +
        ", versionUuid='" + versionUuid + '\'' +
        ", requestBodyType='" + requestBodyType + '\'' +
        ", receiveDocumentsFolder='" + receiveDocumentsFolder + '\'' +
        ", loggingEnabled='" + loggingEnabled + '\'' +
        '}';
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    final WebApiHistoricalVersion other = (WebApiHistoricalVersion)obj;

    return Objects.equal(this.getDescription(), other.getDescription()) &&
        Objects.equal(this.getExpression(), other.getExpression()) &&
        Objects.equal(this.getUrlAlias(), other.getUrlAlias()) &&
        Objects.equal(this.getHttpMethod(), other.getHttpMethod()) &&
        Objects.equal(this.getId(), other.getId()) &&
        Objects.equal(this.getUuid(), other.getUuid()) &&
        Objects.equal(this.getName(), other.getName()) &&
        Objects.equal(this.getWebApiId(), other.getWebApiId()) &&
        Objects.equal(this.getVersionNumber(), other.getVersionNumber()) &&
        Objects.equal(this.getLatestVersionNumber(), other.getLatestVersionNumber()) &&
        Objects.equal(this.getAuditInfo(), other.getAuditInfo()) &&
        Objects.equal(this.getVersionUuid(), other.getVersionUuid()) &&
        Objects.equal(this.getRequestBodyType(), other.getRequestBodyType()) &&
        Objects.equal(this.getReceiveDocumentsFolder(), other.getReceiveDocumentsFolder()) &&
        Objects.equal(this.getLoggingEnabled(), other.getLoggingEnabled());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name, this.description, this.urlAlias, this.expression, this.httpMethod,
        this.id, this.uuid, this.webApiId, this.auditInfo, this.versionNumber, this.latestVersionNumber,
        this.versionUuid, this.requestBodyType, this.receiveDocumentsFolder, this.loggingEnabled);
  }

  @Column(name= "request_body_type", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getRequestBodyType() {
    return requestBodyType;
  }

  public void setRequestBodyType(String requestBodyType) {
    this.requestBodyType = requestBodyType;
  }

  @Column(name= "folder_id")
  public Long getReceiveDocumentsFolder() {
    return receiveDocumentsFolder;
  }

  public void setReceiveDocumentsFolder(Long receiveDocumentsFolder) {
    this.receiveDocumentsFolder = receiveDocumentsFolder;
  }

  @Column(name= "is_logging_enabled")
  public Boolean getLoggingEnabled() {
    return loggingEnabled;
  }

  public void setLoggingEnabled(Boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }
}
