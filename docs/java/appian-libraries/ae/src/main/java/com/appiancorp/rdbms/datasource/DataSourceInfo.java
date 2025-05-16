package com.appiancorp.rdbms.datasource;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.appiancorp.connectedsystems.http.oauth.OAuthConfiguration;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.DataSourceDto;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

@Entity
@Table(name = "datasource")
@XmlRootElement(name = DataSourceInfo.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = DataSourceInfo.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "uuid",
    "name",
    "type",
    "connectionUrl"
})
public class DataSourceInfo implements Id<Long>, Uuid<String>, HasAuditInfo {

  public static final String LOCAL_PART = "dataSource";
  protected static final String PROP_NAME = "name";
  private Long id;
  private String uuid;
  private String name;
  private DatabaseType type;
  private String username;
  private String password;
  private String connectionUrl;
  private AuditInfo auditInfo;
  private Integer maxConnections;
  private Integer transactionIsolationLevel;
  private String properties;
  private OAuthConfiguration oAuthConfiguration;

  public DataSourceInfo() {
    auditInfo = new AuditInfo();
  }

  public DataSourceInfo(DataSourceInfo other) {
    this();
    this.id = other.id;
    this.uuid = other.uuid;
    this.name = other.name;
    this.type = other.type;
    this.username = other.username;
    this.password = other.password;
    this.connectionUrl = other.connectionUrl;
  }

  public DataSourceInfo(DataSourceDto dataSourceDto) {
    this();
    this.id = dataSourceDto.getId();
    // Use emptyToNull as empty string represents null in TypedValue
    this.uuid = Strings.emptyToNull(dataSourceDto.getUuid());
    this.name = dataSourceDto.getName();
    this.type = DatabaseType.getDatabaseTypeFromDisplayName(dataSourceDto.getType());
    this.connectionUrl = dataSourceDto.getConnectionUrl();
    this.username = dataSourceDto.getUsername();
    this.password = dataSourceDto.getPassword().getValue();
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlElement(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(name = "type")
  public DatabaseType getType() {
    return type;
  }

  public void setType(DatabaseType type) {
    this.type = type;
  }

  @Column(name = "username", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlTransient
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "password", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlTransient
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "connection_url", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement(name = "connectionString")
  public String getConnectionUrl() {
    return connectionUrl;
  }

  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  @Embedded
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Transient
  @XmlTransient
  public Integer getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(Integer maxConnections) {
    this.maxConnections = maxConnections;
  }

  @Transient
  @XmlTransient
  public String getProperties() {
    return properties;
  }

  public void setProperties(String properties) {
    this.properties = properties;
  }

  @Transient
  @XmlTransient
  public Optional<OAuthConfiguration> getOAuthConfiguration() {
    return Optional.ofNullable(oAuthConfiguration);
  }

  public void setOAuthConfiguration(OAuthConfiguration oAuthConfiguration) {
    this.oAuthConfiguration = oAuthConfiguration;
  }

  @Transient
  @XmlTransient
  public Integer getTransactionIsolationLevel() {
    // Transaction isolation values come from JDBC's Connection.TRANSACTION_{type}
    return transactionIsolationLevel;
  }

  public void setTransactionIsolationLevel(Integer transactionIsolationLevel) {
    this.transactionIsolationLevel = transactionIsolationLevel;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), getName(), getType(), getConnectionUrl(), getUsername(), getPassword());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DataSourceInfo)) {
      return false;
    }
    DataSourceInfo rhs = (DataSourceInfo)obj;
    return Objects.equal(this.getId(), rhs.getId()) &&
        Objects.equal(this.getName(), rhs.getName()) &&
        Objects.equal(this.getType(), rhs.getType()) &&
        Objects.equal(this.getConnectionUrl(), rhs.getConnectionUrl()) &&
        Objects.equal(this.getUsername(), rhs.getUsername()) &&
        Objects.equal(this.getPassword(), rhs.getPassword());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DataSourceInfo{");
    sb.append("id=").append(id);
    sb.append(", uuid='").append(uuid).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", type=").append(type);
    sb.append(", connectionUrl='").append(connectionUrl).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public static class DataSourceInfoEquivalence extends Equivalence<DataSourceInfo> {
    @Override
    protected boolean doEquivalent(final DataSourceInfo lhs, final DataSourceInfo rhs) {
      if (lhs == rhs) {
        return true;
      }

      if (null == rhs || null == lhs) {
        return false;
      }

      return Objects.equal(lhs.name, rhs.name) && Objects.equal(lhs.type, rhs.type) &&
          Objects.equal(lhs.connectionUrl, rhs.connectionUrl) && Objects.equal(lhs.username, rhs.username) &&
          Objects.equal(lhs.password, rhs.password) && Objects.equal(lhs.uuid, rhs.uuid);
    }

    @Override
    protected int doHash(final DataSourceInfo t) {
      return Objects.hashCode(t.name, t.type, t.connectionUrl, t.username, t.password, t.uuid);
    }
  }
}
