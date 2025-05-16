package com.appiancorp.webapi;

import static com.appiancorp.core.expr.TypeTransformation.TYPE_ID_TO_TYPE_NAMESPACE_CURRENT_IF_LATEST_VERSION_MODE_ON_FOR_RULES;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.base.MultilineToStringHelper;
import com.appian.core.base.ToStringFunction;
import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.cdt.HttpMethod;
import com.appiancorp.core.expr.portable.environment.EvaluationEnvironment;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKey;
import com.appiancorp.ix.refs.ForeignKeyCollabType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.locking.NeedsLockValidation;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.AppianType;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.WebApiDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.WebApiRef;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

@Hidden
@Entity
@Table(name = WebApi.TABLE_NAME)
@XmlRootElement(name = "webApi", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = WebApi.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {Uuid.LOCAL_PART,
    WebApi.PROP_NAME, WebApi.PROP_DESCRIPTION, WebApi.PROP_EXPRESSION, WebApi.PROP_URL_ALIAS,
    WebApi.PROP_HTTP_METHOD, WebApi.PROP_SYSTEM, WebApi.PROP_REQUEST_BODY_TYPE,
    WebApi.RECEIVE_DOCUMENTS_FOLDER_UUID, WebApi.PROP_LOGGING_ENABLED})
@IgnoreJpa
@Tracked
public class WebApi implements Id<Long>, Uuid<String>, Name, HasAuditInfo, HasRoleMap,
    HasTypeQName, WebApiRef, HasWebApiExpression, NeedsLockValidation, HasVersionHistory {
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = Logger.getLogger(WebApi.class);

  public static final String TABLE_NAME = "web_api";
  public final static String METRIC_SUBSYSTEM = "web_api";
  public static final String TBL_WEB_API_ENDPOINT_RM = "web_api_rm";
  public static final String JOIN_COL_WEB_API_ENDPOINT_ID = "web_api_id";
  public final static String LOCAL_PART = "WebApiEndpointDesignObject";
  public final static QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public final static String PROP_NAME = "name";
  public final static String PROP_URL_ALIAS = "urlAlias";
  public final static String PROP_HTTP_METHOD = "httpMethod";
  public final static String PROP_DESCRIPTION = "description";
  public final static String PROP_EXPRESSION = "expression";
  public final static String PROP_LOGGING_ENABLED = "loggingEnabled";
  // There are two different props, one for IX and one for the columns
  // Changing IX requires writing an IX migration
  public final static String PROP_SYSTEM = "system";
  public final static String PROP_VERSION_UUID = "versionUuid";
  public final static String PROP_REQUEST_BODY_TYPE = "requestBodyType";

  public final static String PROP_RECEIVE_DOCUMENTS_FOLDER = "receiveDocumentsFolder";
  public static final String RECEIVE_DOCUMENTS_FOLDER_UUID = "receiveDocumentsFolderUuid";

  public static final ImmutableSet<String> PROPERTIES_WHICH_CANNOT_REFERENCE_DATATYPES = ImmutableSet.of(
    PROP_ID, PROP_UUID, PROP_NAME, PROP_URL_ALIAS, PROP_HTTP_METHOD, PROP_DESCRIPTION, PROP_SYSTEM,
    PROP_CREATED_TS, PROP_CREATED_BY, PROP_CREATED_BY_USER_ID, PROP_LOGGING_ENABLED, PROP_REQUEST_BODY_TYPE, PROP_UPDATED_TS,
    PROP_UPDATED_BY, PROP_UPDATED_BY_USER_ID, PROP_VERSION_UUID
  );

  private static final Equivalence<WebApi> equalDataCheckInstance = new WebApiEndpointDataEquivalence();

  public enum RequestBodyType {
    NONE,
    BINARY,
    MULTIPART
  }

  private Long id;
  private String uuid;
  private String name;
  private String urlAlias;
  private String httpMethod;

  private String description;
  private String expression;
  private String requestBodyType;
  private Long receiveDocumentsFolder;
  private Integer version = 1;

  private Boolean system = Boolean.FALSE;
  private Boolean loggingEnabled = Boolean.FALSE;

  private AuditInfo auditInfo = new AuditInfo();

  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private boolean isPublic;
  private String versionUuid;

  @XmlElement
  private String receiveDocumentsFolderUuid;

  // IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(Roles.WEB_API_ADMIN,
      Roles.WEB_API_EDITOR, Roles.WEB_API_AUDITOR, Roles.WEB_API_VIEWER);

  public static final Set<String> VALID_RECEIVE_DOC_METHODS = ImmutableSet.of(
      HttpMethod.POST.value(),
      HttpMethod.PUT.value(),
      HttpMethod.PATCH.value());

  public WebApi() {
  }

  public WebApi(final WebApi other) {
    this.id = other.getId();
    this.uuid = other.getUuid();
    this.name = other.getName();
    this.urlAlias = other.getUrlAlias();
    this.httpMethod = other.getHttpMethod();
    this.description = other.getDescription();
    this.expression = other.getExpression();
    this.system = other.getSystem();
    this.auditInfo = new AuditInfo(other.getAuditInfo());
    this.versionUuid = other.getVersionUuid();
    this.requestBodyType = other.getRequestBodyType();
    this.receiveDocumentsFolder = other.getReceiveDocumentsFolder();
    this.loggingEnabled = other.getLoggingEnabled();
  }

  public WebApi(
    final String name,
    final String urlAlias,
    final String httpMethod,
    final String description,
    final String expression) {
    this(name, urlAlias, httpMethod, description, expression, false, RequestBodyType.NONE.toString(), null);
  }

  public WebApi(
    final String name,
    final String urlAlias,
    final String httpMethod,
    final String description,
    final String expression,
    final Boolean system,
    final String requestBodyType,
    final Long receiveDocumentsFolder) {
    this.name = name;
    this.urlAlias = urlAlias;
    this.httpMethod = String.valueOf(httpMethod);
    this.description = description;
    this.expression = expression;
    this.system = system;
    this.requestBodyType = requestBodyType;
    this.receiveDocumentsFolder = receiveDocumentsFolder;
  }

  public WebApi(final WebApiDto webApiEndpointCdt) {
    this.name = webApiEndpointCdt.getName();
    this.urlAlias = webApiEndpointCdt.getUrlAlias();
    this.httpMethod = webApiEndpointCdt.getHttpMethod().value();
    this.description = webApiEndpointCdt.getDescription();
    this.expression = webApiEndpointCdt.getExpression();
    if (webApiEndpointCdt.getVersionNumber() != 0) {
      this.version = webApiEndpointCdt.getVersionNumber();
    }
    if (!Strings.isNullOrEmpty(webApiEndpointCdt.getUuid())) {
      this.uuid = webApiEndpointCdt.getUuid();
    }
    final Boolean isSystem = webApiEndpointCdt.isSystem();
    this.system = isSystem == null ? this.system : isSystem;
    this.requestBodyType = webApiEndpointCdt.getRequestBodyType();
    Long folderId = webApiEndpointCdt.getReceiveDocsFolder() == null ?
        null :
        webApiEndpointCdt.getReceiveDocsFolder().getId();
    this.receiveDocumentsFolder = folderId;
    final Boolean isLoggingEnabled = webApiEndpointCdt.isLoggingEnabled();
    this.loggingEnabled = isLoggingEnabled == null ? this.loggingEnabled : isLoggingEnabled;
  }

  public WebApi(final WebApiRef webApiRef) {
    this.uuid = webApiRef.getUuid();
    this.id = webApiRef.getId();
  }

  @Override
  public Ref<Long, String> build(final Long id, final String uuid) {
    final WebApi webApi = new WebApi();
    webApi.setUuid(uuid);
    webApi.setId(id);
    return webApi;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = java.util.UUID.randomUUID().toString();
    }

    loggingEnabled = MoreObjects.firstNonNull(loggingEnabled, false);
    system = MoreObjects.firstNonNull(system, false);
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true,
      length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Column(name = "url_alias", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUrlAlias() {
    return urlAlias;
  }

  public void setUrlAlias(final String urlAlias) {
    this.urlAlias = urlAlias;
  }

  @Column(name = "http_method", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(final String httpMethod) {
    this.httpMethod = httpMethod;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Column(name = "request_body_type", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getRequestBodyType() {
    return requestBodyType;
  }

  public void setRequestBodyType(String requestBodyType) {
    this.requestBodyType = requestBodyType;
  }

  /**
   * Folder ID is local and must be translated to UUID on IX.
   */
  @Column(name = "folder_id")
  @XmlTransient
  @ForeignKey(type = com.appiancorp.ix.Type.CONTENT_KEY, uuidField = "receiveDocumentsFolderUuid", nullable = true, breadcrumb = BreadcrumbText.receiveDocFolder)
  @ForeignKeyCollabType(AppianType.CONTENT_FOLDER)
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

  public void setLoggingEnabled(final Boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  /**
   * This method resets request body type to NONE if the method is not PUT, POST or PATCH. It also
   * clears out the folder ID if request body type is NONE.
   */
  public void resetDocTypeAndFolderIdIfNeeded() {
    if (!VALID_RECEIVE_DOC_METHODS.contains(getHttpMethod())) {
      setRequestBodyType(RequestBodyType.NONE.toString());
      setReceiveDocumentsFolder(null);
      return;
    }

    if (RequestBodyType.NONE.toString().equals(requestBodyType)) {
      setReceiveDocumentsFolder(null);
    }
  }

  @Override
  @Column(name = "expression", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.webApiExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getExpression() {
    return expression;
  }

  @Override
  public void setExpression(final String expression) {
    this.expression = expression;
  }

  @XmlTransient
  @Transient
  public String getRetrievedAllowedValuesExpression() {
    try {
      return EvaluationEnvironment.getSafeExpressionTransformer().toRetrievedForm(
          expression,
          TYPE_ID_TO_TYPE_NAMESPACE_CURRENT_IF_LATEST_VERSION_MODE_ON_FOR_RULES);
    } catch (Exception e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not transform expression. Original expression returned. expression=" + expression + ". WebApi=" + this, e);
      }
    }
    return expression;
  }

  @Column(name = "is_system")
  public Boolean getSystem() {
    return system;
  }

  public void setSystem(final Boolean system) {
    this.system = system;
  }

  @XmlTransient
  @Override
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  @Override
  public void setVersionUuid(final String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Override
  @Embedded
  // Marked XmlTransient because we need to implement binding between k/rdbms
  // userIds before this can be serialized. Unfinished impl in
  // AuditInfoPrimaryDsBinder. When this annotation is removed, add auditInfo
  // back into propOrder above and un-ignore test in UiContainerTest
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(final AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @XmlTransient
  @Transient
  @Override
  public QName getTypeQName() {
    return WebApi.QNAME;
  }

  public WebApi cloneWithNewAuditInfo() {
    final WebApi cloned = new WebApi();
    cloned.id = this.getId();
    cloned.uuid = this.getUuid();
    cloned.name = this.getName();
    cloned.urlAlias = this.getUrlAlias();
    cloned.httpMethod = this.getHttpMethod();
    cloned.description = this.getDescription();
    cloned.expression = this.getExpression();
    cloned.system = this.getSystem();
    cloned.versionUuid = this.getVersionUuid();
    cloned.requestBodyType = this.getRequestBodyType();
    cloned.receiveDocumentsFolder = this.getReceiveDocumentsFolder();
    cloned.loggingEnabled = this.getLoggingEnabled();
    return cloned;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient
  @XmlTransient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      if (Roles.WEB_API_AUDITOR.equals(roleMapEntry.getRole())) {
        roleMapBuilder.users(Roles.WEB_API_VIEWER, roleMapEntry.getUsers());
        roleMapBuilder.groups(Roles.WEB_API_VIEWER, roleMapEntry.getGroups());
      } else {
        roleMapBuilder.entries(roleMapEntry);
      }
    }

    return roleMapBuilder.build();
  }

  public void setRoleMap(final RoleMap roleMap) {
    if (this.roleMapEntries != null) {
      this.roleMapEntries.clear();
    }
    if (roleMap != null) {
      if (this.roleMapEntries == null) {
        this.roleMapEntries = new HashSet<>();
      }
      this.roleMapEntries.addAll(roleMap.getEntriesByRole().values());
    }
  }

  @Override
  public void discardRoleMap() {
    this.roleMapEntries = new PersistentSet(); // This tells Hibernate to ignore this field during the update.
  }

  @Override
  @Transient
  @XmlTransient
  public boolean isPublic() {
    return isPublic;
  }

  @Override
  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @Override
  @Transient
  @XmlTransient
  public String getFallbackRoleName() {
    return Roles.WEB_API_VIEWER.getName();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name, this.description, this.urlAlias, this.expression, this.httpMethod,
        this.id, this.uuid, this.version, this.system, this.requestBodyType, this.receiveDocumentsFolder, this.loggingEnabled);
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = TBL_WEB_API_ENDPOINT_RM, joinColumns = @JoinColumn(name = JOIN_COL_WEB_API_ENDPOINT_ID),
      inverseJoinColumns = @JoinColumn(name = RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  @XmlTransient
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
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

    final WebApi other = (WebApi)obj;

    return
      Objects.equal(this.getDescription(), other.getDescription()) &&
      Objects.equal(this.getExpression(), other.getExpression()) &&
      Objects.equal(this.getUrlAlias(), other.getUrlAlias()) &&
      Objects.equal(this.getHttpMethod(), other.getHttpMethod()) &&
      Objects.equal(this.getId(), other.getId()) &&
      Objects.equal(this.getUuid(), other.getUuid()) &&
      Objects.equal(this.getName(), other.getName()) &&
      Objects.equal(this.getSystem(), other.getSystem() &&
      Objects.equal(this.getVersionUuid(), other.getVersionUuid()) &&
      Objects.equal(this.getRequestBodyType(), other.getRequestBodyType()) &&
      Objects.equal(this.getReceiveDocumentsFolder(), other.getReceiveDocumentsFolder()) &&
      Objects.equal(this.getLoggingEnabled(), other.getLoggingEnabled()));
  }

  /* Equivalence */
  public static ToStringFunction<WebApi> multilineToString(final int indent) {
    return new ToStringFunction<WebApi>() {
      @Override
      public String doToString(final WebApi t) {
        return MultilineToStringHelper.of(t, indent)
            .add(PROP_ID, t.id)
            .add(PROP_UUID, t.uuid)
            .add(PROP_NAME, t.name)
            .add(PROP_URL_ALIAS, t.urlAlias)
            .add(PROP_EXPRESSION, t.expression)
            .add(PROP_HTTP_METHOD, t.httpMethod)
            .add(PROP_SYSTEM, t.system)
            .add(PROP_VERSION_UUID, t.versionUuid)
            .add(PROP_REQUEST_BODY_TYPE, t.requestBodyType)
            .add(RECEIVE_DOCUMENTS_FOLDER_UUID, t.receiveDocumentsFolderUuid)
            .add(PROP_LOGGING_ENABLED, t.loggingEnabled)
            .toString();
      }
    };
  }

  public boolean equivalentTo(final WebApi currentVersion) {
    return equalDataCheckInstance.equivalent(this, currentVersion);
  }

  public static Equivalence<WebApi> equalityForNonGeneratedFields() {
    return equalDataCheckInstance;
  }

  private static class WebApiEndpointDataEquivalence extends Equivalence<WebApi> {
    @Override
    protected boolean doEquivalent(final WebApi lhs, final WebApi rhs) {
      if (lhs == rhs) {
        return true;
      }

      if (null == rhs || null == lhs) {
        return false;
      }

      return
        Objects.equal(lhs.name, rhs.name) &&
        Objects.equal(lhs.description, rhs.description) &&
        Objects.equal(lhs.urlAlias, rhs.urlAlias) &&
        Objects.equal(lhs.expression, rhs.expression) &&
        Objects.equal(lhs.httpMethod, rhs.httpMethod) &&
        Objects.equal(lhs.system, rhs.system) &&
        Objects.equal(lhs.requestBodyType, rhs.requestBodyType) &&
        Objects.equal(lhs.receiveDocumentsFolder, rhs.receiveDocumentsFolder) &&
        Objects.equal(lhs.getLoggingEnabled(), rhs.getLoggingEnabled());
    }

    @Override
    protected int doHash(final WebApi t) {
      return Objects.hashCode(t.name, t.description, t.urlAlias, t.expression, t.httpMethod, t.system,
          t.versionUuid, t.requestBodyType, t.receiveDocumentsFolder, t.loggingEnabled);
    }
  }

  @Override public String toString() {
    return "WebApiEndpointData{" +
        "id=" + id +
        ", uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", urlAlias='" + urlAlias + '\'' +
        ", httpMethod='" + httpMethod + '\'' +
        ", version=" + version +
        ", system=" + system +
        ", versionUuid=" + versionUuid +
        ", requestBodyType=" + requestBodyType +
        ", receiveDocumentsFolder=" + receiveDocumentsFolder +
        ", loggingEnabled=" + loggingEnabled +
        '}';
  }

  @Transient
  @Override
  public boolean needsLockValidationOnUpdate() {
    return true;
  }
}
