package com.appiancorp.security.external;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@Hidden
@Entity
@Table(name = ExternalSystem.TBL_EXTERNAL_SYSTEM)
@XmlRootElement(name = "externalSystem", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = ExternalSystem.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "id",
    "uuid",
    "name",
    "description",
    "key",
    "attributes",
    "showPerUser",
    "testExpression",
    "allowedPlugins"
})
@XmlAccessorType(XmlAccessType.FIELD)
@IgnoreJpa
public class ExternalSystem implements Id<Long>, Uuid<String>, Name, HasAuditInfo, HasRoleMap, ExternalSystemKey {
  private static final long serialVersionUID = 1L;
  public static final String LOCAL_PART = "ExternalSystem";

  public static final String DEFAULT_ATTR_KEY_USERNAME = "username";
  public static final String DEFAULT_ATTR_KEY_PASSWORD = "password";

  public static final String TBL_EXTERNAL_SYSTEM = "external_sys";
  public static final String TBL_EXTERNAL_SYSTEM_RM = "external_sys_rm";
  public static final String JOIN_COL_EXTERNAL_SYSTEM_ID = "external_sys_id";

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public static final String PROP_NAME = "name";
  public static final String PROP_KEY = "key";

  public static final ImmutableSet<String> NON_DATATYPE_COLUMNS = ImmutableSet.<String>builder()
    .add(Id.LOCAL_PART, Uuid.LOCAL_PART, "key", "name", "description", "attributes", "showPerUser",
      "allowedPlugins", PROP_CREATED_BY, PROP_CREATED_BY_USER_ID, PROP_CREATED_TS, PROP_UPDATED_BY,
       PROP_UPDATED_BY_USER_ID, PROP_UPDATED_TS)
    .build();

  // IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
    Roles.EXTERNAL_SYSTEM_ADMIN, Roles.EXTERNAL_SYSTEM_EDITOR, Roles.EXTERNAL_SYSTEM_AUDITOR,
    Roles.EXTERNAL_SYSTEM_VIEWER);

  private Long id;
  private String uuid;
  private String key;
  private String name;
  private String description;
  @XmlElement(name = "attribute", nillable = true)
  private List<SecuredAttribute> attributes = new ArrayList<SecuredAttribute>();
  @XmlTransient
  private AuditInfo auditInfo = new AuditInfo();
  @XmlTransient
  private Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private boolean showPerUser;
  private String testExpression;
  @XmlElement(name = "allowedPlugin", nillable = true)
  private Set<String> allowedPlugins;
  @XmlTransient
  private boolean isPublic;
  @XmlTransient
  private boolean hideFromUi;

  public ExternalSystem() {
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "unique_key", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Column(name = "name", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "external_sys_id", nullable = false)
  @OrderColumn(name = "order_idx", nullable = false)
  public List<SecuredAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<SecuredAttribute> attributes) {
    this.attributes = attributes;
  }

  @Transient
  public Map<String, SecuredAttribute> getAttributesByKey() {
    Map<String, SecuredAttribute> attributesByKey = Maps.newHashMap();
    for(SecuredAttribute attribute : getAttributes()) {
      attributesByKey.put(attribute.getKey(), attribute);
    }
    return attributesByKey;
  }

  @Transient
  public SecuredAttribute findSecuredAttributeByKey(String key) {
    for (SecuredAttribute attribute : getAttributes()) {
      if (attribute.getKey().equals(key)) {
        return attribute;
      }
    }
    return null;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true,
    length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient
  public RoleMap getRoleMap() {
    return roleMapEntries == null ? null : RoleMap.builder().entries(roleMapEntries).build();
  }

  public void setRoleMap(RoleMap roleMap) {
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

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = TBL_EXTERNAL_SYSTEM_RM, joinColumns = @JoinColumn(name = JOIN_COL_EXTERNAL_SYSTEM_ID),
    inverseJoinColumns = @JoinColumn(name = RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  @Transient
  public boolean isPublic() {
    return isPublic;
  }

  @Override
  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.EXTERNAL_SYSTEM_VIEWER.getName();
  }

  /**
   * Determines if users will be able to see this external system when configuring per-user credentials
   */
  @Column(name = "show_per_user", nullable = false)
  public boolean isShowPerUser() {
    return showPerUser;
  }

  public void setShowPerUser(boolean showPerUser) {
    this.showPerUser = showPerUser;
  }

  /**
   * Determines if an external system is always hidden from the user
   */
  @Column(name = "hide_from_ui", nullable = false)
  public boolean isHideFromUi() {
    return hideFromUi;
  }

  public void setHideFromUi(boolean hideFromUi) {
    this.hideFromUi = hideFromUi;
  }

  /**
   * The expression that will be used to test a connection to this external system
   */
  @Column(name = "test_expr", length = Constants.COL_MAXLEN_EXPRESSION, nullable = true)
  @Lob
  @ComplexForeignKey(nullable=false, breadcrumb= BreadcrumbText.testExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getTestExpression() {
    return testExpression;
  }

  public void setTestExpression(String testExpression) {
    this.testExpression = testExpression;
  }

  /**
   * The list of plugins that are allowed to access this external system
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
        name = "external_sys_plugins",
        joinColumns = @JoinColumn(name = "external_sys_id")
  )
  @Column(name = "unique_key", nullable = false)
  public Set<String> getAllowedPlugins() {
    return allowedPlugins;
  }

  public void setAllowedPlugins(Set<String> allowedPlugins) {
    this.allowedPlugins = allowedPlugins;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("uuid", uuid)
        .add("name", name)
        .add("description", description)
        .add("key", key)
        .add("showPerUser", showPerUser)
        .add("testExpression", testExpression)
        .add("isPublic", isPublic)
        .toString();
  }

  public static Equivalence<ExternalSystem> ignoreIdAndAuditEquivalence() {
    return new Equivalence<ExternalSystem>() {
      @Override
      protected boolean doEquivalent(ExternalSystem a, ExternalSystem b) {
        return Objects.equal(a.getKey(), b.getKey()) &&
            Objects.equal(a.getAllowedPlugins(), b.getAllowedPlugins()) &&
            Objects.equal(a.getDescription(), b.getDescription()) &&
            Objects.equal(a.getName(), b.getName()) &&
            Objects.equal(a.getTestExpression(), b.getTestExpression()) &&
            SecuredAttribute.ignoreIdEquivalence().pairwise().equivalent(a.getAttributes(), b.getAttributes());
      }

      @Override
      protected int doHash(ExternalSystem externalSystem) {
        return Objects.hashCode(externalSystem.getKey(), externalSystem.getAllowedPlugins(),
            externalSystem.getDescription(), externalSystem.getName(), externalSystem.getTestExpression(),
            SecuredAttribute.ignoreIdEquivalence().pairwise().hash(externalSystem.getAttributes()));
      }
    };
  }
}
