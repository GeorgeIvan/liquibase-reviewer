package com.appiancorp.controlpanel.persistence.tier;

import static com.appiancorp.controlpanel.persistence.ControlPanel.ALL_ROLES;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.annotations.Immutable;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.controlpanel.persistence.ControlPanel;
import com.appiancorp.controlpanel.persistence.SettingsJsonConverter;
import com.appiancorp.core.expr.portable.annotations.VisibleForTesting;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ForeignKey;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.ControlPanelTierItemDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.appiancorp.type.refs.GroupRefImpl;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An entity class to store a control panel tier item in RDBMS.
 */
@Entity
@IgnoreJpa
@Table(name = "control_panel_tier_item")
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name = "controlPanelTierItem")
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(name = "ControlPanelTierItem", namespace = Type.APPIAN_NAMESPACE, propOrder = {
    com.appiancorp.type.Id.LOCAL_PART, Uuid.LOCAL_PART, Name.LOCAL_PART, "controlPanelUuid", "parentUuid",
    "settingsJson", "urlStub", "isCollection", "isActive"})
@XmlSeeAlso({GroupRefImpl.class})
@Tracked
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:anoninnerlength"})
public class ControlPanelTierItem
    implements HasRoleMap, HasAuditInfo, Id<Long>, Name, Uuid<String>, HasVersionHistory, HasTypeQName {
  private static final long serialVersionUID = 87687937L;

  public static final String LOCAL_PART = "ControlPanelTierItemDesignObject";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);
  public static final String FEATURE_TOGGLE = "ae.studio.control-panel";
  public static final String PROP_URL_STUB = "urlStub";
  public static final String PROP_PARENT = "parent";
  public static final String PROP_ID = "id";
  public static final String CONTROL_PANEL_TIER_ITEM_RM = "control_panel_tier_item_rm";
  private static final String CONTROL_PANEL_TIER_ITEM_ID = "control_panel_tier_item_id";
  private static final String ROLE_MAP_ENTRY_ID = "rm_entry_id";

  private Long id;
  private String uuid;
  private String name;
  private String versionUuid;
  private ControlPanel controlPanel;
  private ControlPanelTierItem parent;
  private String urlStub;
  private String urlStubUnique;
  private Boolean isCollection;
  private String settings;
  private Boolean isActive;
  private AuditInfo auditInfo = new AuditInfo();
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  public ControlPanelTierItem() {
  }

  @VisibleForTesting
  public ControlPanelTierItem(
      String name,
      ControlPanel controlPanel,
      ControlPanelTierItem parent,
      String urlStub,
      Boolean isCollection) {
    this.name = name;
    this.controlPanel = controlPanel;
    this.parent = parent;
    this.urlStub = urlStub;
    this.isCollection = isCollection;
    this.isActive = Boolean.TRUE;
  }

  public ControlPanelTierItem(ControlPanelTierItemDto controlPanelTierItemDto) {
    if (controlPanelTierItemDto.getId() != null && controlPanelTierItemDto.getId().getValue() != null &&
        controlPanelTierItemDto.getId().getValue().toValue_Value().intValue() !=
            com.appiancorp.core.expr.portable.Type.INTEGER.nullOf()) {
      this.id = Long.valueOf(controlPanelTierItemDto.getId().getValue().toValue_Value());
    }
    this.uuid = controlPanelTierItemDto.getUuid();
    this.name = controlPanelTierItemDto.getName();
    this.versionUuid = controlPanelTierItemDto.getVersionUuid();
    if (controlPanelTierItemDto.getControlPanel() != null &&
        controlPanelTierItemDto.getControlPanel().getValue() != null &&
        controlPanelTierItemDto.getControlPanel().getValue().toValue_Value().intValue() !=
            com.appiancorp.core.expr.portable.Type.INTEGER.nullOf()) {
      this.controlPanel = new ControlPanel();
      this.controlPanel.setId(
          Long.valueOf(controlPanelTierItemDto.getControlPanel().getValue().toValue_Value()));
    }
    if (controlPanelTierItemDto.getParentUuid() != null && !controlPanelTierItemDto.getParentUuid().isEmpty()) {
      this.parent = new ControlPanelTierItem();
      this.parent.setUuid(controlPanelTierItemDto.getParentUuid());
    }
    this.urlStub = controlPanelTierItemDto.getUrlStub();
    this.isCollection = controlPanelTierItemDto.isIsCollection();
    this.isActive = controlPanelTierItemDto.isIsActive() == null ? Boolean.TRUE : controlPanelTierItemDto.isIsActive();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Transient
  public String getDescription() {
    return "";
  }

  @Column(name = "version_uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Immutable
  @JoinColumn(name = "control_panel_id", nullable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY, optional = false)
  public ControlPanel getControlPanel() {
    return controlPanel;
  }

  @Transient
  @XmlElement(name = "controlPanelUuid", namespace = Type.APPIAN_NAMESPACE)
  public String getControlPanelUuid() {
    return this.controlPanel == null ? null : controlPanel.getUuid();
  }

  public void setControlPanelUuid(String controlPanelUuid) {
    if (controlPanel == null) {
      controlPanel = new ControlPanel();
    }
    controlPanel.setUuid(controlPanelUuid);
  }

  public void setControlPanel(ControlPanel controlPanel) {
    this.controlPanel = controlPanel;
  }

  @Transient
  @ForeignKey(type="controlPanel", uuidField="controlPanelUuid", nullable=false, breadcrumb=BreadcrumbText.controlPanel)
  public Long getControlPanelId() {
    return this.controlPanel == null ? null : controlPanel.getId();
  }

  public void setControlPanelId(Long controlPanelId) {
    if (controlPanel == null) {
      controlPanel = new ControlPanel();
    }
    controlPanel.setId(controlPanelId);
  }

  @Immutable
  @JoinColumn(name = "parent_uuid", referencedColumnName = "uuid")
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
  public ControlPanelTierItem getParent() {
    return parent;
  }

  @Transient
  @XmlElement(name = "parentUuid", namespace = Type.APPIAN_NAMESPACE)
  public String getParentUuid() {
    return this.parent == null ? null : parent.getUuid();
  }

  public void setParentUuid(String parentUuid) {
    if (parent == null) {
      parent = new ControlPanelTierItem();
    }
    parent.setUuid(parentUuid);
  }

  public void setParent(ControlPanelTierItem parent) {
    this.parent = parent;
  }

  @Transient
  @ForeignKey(type="controlPanelTierItem", uuidField="parentUuid", nullable=true, breadcrumb=BreadcrumbText.controlPanelTierItemParent)
  public Long getParentId() {
    return this.parent == null ? null : parent.getId();
  }

  public void setParentId(Long parentId) {
    if (parent == null) {
      parent = new ControlPanelTierItem();
    }
    parent.setId(parentId);
  }

  @Column(name = "url_stub", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(name = "urlStub", namespace = Type.APPIAN_NAMESPACE)
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @Column(name = "url_stub_unique", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUrlStubUnique() {
    return urlStubUnique;
  }

  public void setUrlStubUnique(String urlStubUnique) {
    this.urlStubUnique = urlStubUnique;
  }

  @Column(name = "is_collection", nullable = false)
  @XmlElement(name = "isCollection", namespace = Type.APPIAN_NAMESPACE)
  public Boolean getIsCollection() {
    return isCollection;
  }

  public void setIsCollection(Boolean isCollection) {
    this.isCollection = isCollection;
  }

  @Lob
  @XmlElement
  @Column(name = "settings", nullable = false)
  public String getSettingsJson() {
    return settings;
  }

  public void setSettingsJson(String settings) {
    this.settings = settings;
  }

  @Transient
  @XmlTransient
  @HasForeignKeys(breadcrumb = BreadcrumbText.controlPanelTierItemSettings)
  public TierItemSettings getSettings() {
    return SettingsJsonConverter.storedFormJsonToStoredFormSettings(getSettingsJson(), TierItemSettings.class);
  }

  @Column(name = "is_active", nullable = false)
  @XmlElement(name = "isActive", namespace = Type.APPIAN_NAMESPACE)
  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @PrePersist
  private void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) {
      uuid = UUID.randomUUID().toString();
    }
    if (Strings.isNullOrEmpty(versionUuid)) {
      versionUuid = UUID.randomUUID().toString();
    }
    onPreUpdate();
  }

  @PreUpdate
  private void onPreUpdate() {
    if (Strings.isNullOrEmpty(settings)) {
      settings = "{}";
    }
    // This is just UNIQUE CONSTRAINT ON url_stub, control_panel_id, parent_uuid
    // however, since parent_uuid can be NULL, the way to do that differs DRAMATICALLY across DB vendors
    // therefore, moved the logic into here and rely on a very simple unique column
    if (parent == null) {
      urlStubUnique = controlPanel.getId() + "//" + urlStub;
    } else {
      urlStubUnique = controlPanel.getId() + "/" + (getParentUuid() == null ? "" : parent.getUuid()) + "/" + urlStub;
    }
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
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("uuid", uuid)
        .add("name", name)
        .add("versionUuid", versionUuid)
        .add("auditInfo", auditInfo)
        .toString();
  }

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = CONTROL_PANEL_TIER_ITEM_RM, joinColumns = @JoinColumn(name = CONTROL_PANEL_TIER_ITEM_ID), inverseJoinColumns = @JoinColumn(name = ROLE_MAP_ENTRY_ID))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  public Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient
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

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @Override
  public void setPublic(boolean isPublic) {
    // ControlPanelTierItems are never public (visible to all users).
  }

  @Override
  @Transient
  public boolean isPublic() {
    return false;
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.CONTROL_PANEL_VIEWER.getName();
  }

  public boolean equivalentTo(final ControlPanelTierItem controlPanelTierItem) {
    return EQUIVALENCE.equivalent(this, controlPanelTierItem);
  }

  private static final Equivalence<ControlPanelTierItem> EQUIVALENCE = new Equivalence<>() {
    @Override
    protected boolean doEquivalent(ControlPanelTierItem a, ControlPanelTierItem b) {
      if (a == b) {
        return true;
      }
      if (a.getClass() != b.getClass()) {
        return false;
      }
      return Objects.equal(a.id, b.id) && stringEquals(a.uuid, b.uuid) && stringEquals(a.name, b.name) &&
          stringEquals(a.urlStub, b.urlStub) && stringEquals(a.settings, b.settings) &&
          Objects.equal(a.getControlPanelId(), b.getControlPanelId()) &&
          Objects.equal(a.getParentUuid(), b.getParentUuid()) &&
          Objects.equal(a.isCollection, b.isCollection) &&
          Objects.equal(a.isActive, b.isActive);
    }

    @Override
    protected int doHash(ControlPanelTierItem controlPanelTierItem) {
      return Objects.hashCode(controlPanelTierItem.id, controlPanelTierItem.uuid, controlPanelTierItem.name,
          controlPanelTierItem.urlStub, controlPanelTierItem.settings,
          controlPanelTierItem.getControlPanelId(), controlPanelTierItem.getParentUuid(),
          controlPanelTierItem.isCollection, controlPanelTierItem.isActive);
    }

    private static boolean stringEquals(String a, String b) {
      return Objects.equal(a, b) || Strings.isNullOrEmpty(a) && Strings.isNullOrEmpty(b);
    }
  };
}
