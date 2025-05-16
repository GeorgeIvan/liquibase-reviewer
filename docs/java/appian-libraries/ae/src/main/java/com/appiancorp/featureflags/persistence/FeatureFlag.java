package com.appiancorp.featureflags.persistence;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
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
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.FeatureFlagDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.GroupRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

@Hidden
@Entity
@Table(name = FeatureFlag.FEATURE_FLAG_TABLE)
@XmlRootElement(name = FeatureFlag.FEATURE_FLAG_TABLE, namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = FeatureFlag.LOCAL_PART,
    namespace = Type.APPIAN_NAMESPACE,
    propOrder = {
        FeatureFlag.PROP_ID, FeatureFlag.PROP_UUID, FeatureFlag.PROP_NAME, FeatureFlag.PROP_DESCRIPTION,
        FeatureFlag.PROP_CONDITIONS
    })
@XmlSeeAlso({GroupRefImpl.class})
@IgnoreJpa
@Tracked
public class FeatureFlag implements HasRoleMap, HasTypeQName, HasAuditInfo, Id<Long>, Name, Uuid<String>,
    HasVersionHistory, NeedsLockValidation {
  private static final long serialVersionUID = -4249855075308474788L;

  public static final String LOCAL_PART = "FeatureFlag";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String FEATURE_FLAG_TABLE = "feature_flag";
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public static final String PROP_NAME = "name";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_STATE = "state";
  public static final String PROP_CONDITIONS = "conditions";
  public static final String PROP_VERSION_UUID = "versionUuid";

  public enum State {
    ENABLED_FOR_ALL, DISABLED_FOR_ALL, USE_CONDITIONS
  }

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(Roles.FEATURE_FLAG_ADMIN,
      Roles.FEATURE_FLAG_EDITOR, Roles.FEATURE_FLAG_VIEWER);

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private State state;
  private String conditions;
  private transient FeatureFlagCondition conditionObject;
  private String versionUuid;
  private AuditInfo auditInfo = new AuditInfo();
  private boolean needsLockValidationOnUpdate = true;
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  public FeatureFlag() {
    // default state so we don't need to marshal it
    this.state = State.ENABLED_FOR_ALL;
  }

  public FeatureFlag(FeatureFlagDto featureFlagDto, FeatureFlagCondition condition) {
    this.id = featureFlagDto.getId();
    this.uuid = featureFlagDto.getUuid();
    this.name = featureFlagDto.getName();
    this.description = featureFlagDto.getDescription();
    this.setConditionObject(condition);
    this.state = condition.isGloballyEnabled() ? State.ENABLED_FOR_ALL : State.USE_CONDITIONS;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = FeatureFlag.PROP_ID)
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = FeatureFlag.PROP_UUID, updatable = false, nullable = false, unique = true,
      length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @SuppressWarnings("unused")
  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Column(name = FeatureFlag.PROP_NAME, nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = FeatureFlag.PROP_DESCRIPTION, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = FeatureFlag.PROP_STATE, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlTransient
  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Column(name = FeatureFlag.PROP_CONDITIONS, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb= BreadcrumbText.featureFlagCondition)
  @ForeignKeyCustomBinder(CustomBinderType.FEATURE_FLAG_CONDITIONS)
  public String getConditions() {
    return conditions;
  }

  public void setConditions(String conditions) {
    this.conditions = conditions;
    this.conditionObject = new Gson().fromJson(conditions, FeatureFlagCondition.class);
  }

  @Transient
  @XmlTransient
  public FeatureFlagCondition getConditionObject() {
    return conditionObject;
  }

  public void setConditionObject(FeatureFlagCondition conditionObject) {
    this.conditionObject = conditionObject;
    this.conditions = new Gson().toJson(conditionObject);
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
      roleMapBuilder.entries(roleMapEntry);
    }

    return roleMapBuilder.build();
  }

  @Override
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

  // clearRoleMap() call is needed when a FeatureFlag is updated in RDBMS. The code eventually
  // hits FeatureFlag.discardRoleMap() which sets the role map entries to a PersistentMap. This tells
  // Hibernate to ignore the field during an update (we do this intentionally to prevent wiping out role
  // map entries).
  public void clearRoleMap() {
    this.roleMapEntries = new HashSet<>();
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet(); // This tells Hibernate to ignore this field during the update.
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "feature_flag_rm", joinColumns = @JoinColumn(name = "feature_flag_id"),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @XmlTransient
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  @Transient
  @XmlTransient
  public boolean isPublic() {
    // false by default so that viewer role is respected. Any user can still execute a feature flag
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // Feature flags are always "public" in that they can always be executed by any user context.
    // The method is required to meet the interface, but the value is intentionally ignored.
  }

  @Override
  @Transient
  @XmlTransient
  public String getFallbackRoleName() {
    return Roles.FEATURE_FLAG_VIEWER.getName();
  }

  @Override
  @XmlTransient
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = false)
  public String getVersionUuid() {
    return versionUuid;
  }

  @Override
  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Override
  @Embedded
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  @Override
  public String toString() {
    return "FeatureFlag{" + "id=" + id + ", uuid='" + uuid + '\'' + ", name='" + name + '\'' + '}';
  }

  public boolean equivalentTo(final FeatureFlag featureFlag) {
    return EQUIVALENCE.equivalent(this, featureFlag);
  }

  private static Equivalence<FeatureFlag> EQUIVALENCE = new Equivalence<FeatureFlag>() {
    @Override
    protected boolean doEquivalent(FeatureFlag lhs, FeatureFlag rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return Objects.equal(lhs.id, rhs.id) &&
          stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.name, rhs.name) &&
          stringEquals(lhs.description, rhs.description) &&
          Objects.equal(lhs.state, rhs.state) &&
          stringEquals(lhs.conditions, rhs.conditions);
    }

    @Override
    protected int doHash(FeatureFlag featureFlag) {
      return Objects.hashCode(featureFlag.id, featureFlag.uuid, featureFlag.name, featureFlag.description,
          featureFlag.conditions, featureFlag.state, featureFlag.auditInfo, featureFlag.versionUuid,
          featureFlag.getRoleMap());
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  /**
   * Controls whether 'this' Feature Flag is configured to perform a design object lock validation
   * when it is next updated via {@link com.appiancorp.common.service.EntityServiceTxImpl}. Note that
   * this is a transient configuration that's not stored in the backing DB.
   */
  public void setNeedsLockValidationOnUpdate(boolean needsLockValidationOnUpdate) {
    this.needsLockValidationOnUpdate = needsLockValidationOnUpdate;
  }

  /** see {@link #setNeedsLockValidationOnUpdate(boolean)} */
  @Override
  public boolean needsLockValidationOnUpdate() {
    return needsLockValidationOnUpdate;
  }
}
