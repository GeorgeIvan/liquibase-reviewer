package com.appiancorp.controlpanel.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
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

import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.action.security.RoleMapDefinitionFacade;
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
import com.appiancorp.type.cdt.value.ControlPanelDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.GroupRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An entity class to store a control panel in RDBMS.
 */
@Entity
@IgnoreJpa
@Tracked
@Table(name = "control_panel")
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name = "controlPanel")
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(name = "ControlPanel", namespace = Type.APPIAN_NAMESPACE, propOrder = {
    com.appiancorp.type.Id.LOCAL_PART, Uuid.LOCAL_PART, Name.LOCAL_PART, "description", "settingsJson",
    "urlStub"})
@XmlSeeAlso({GroupRefImpl.class})
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:anoninnerlength"})
public class ControlPanel
    implements HasRoleMap, HasAuditInfo, Id<Long>, Name, Uuid<String>, HasVersionHistory, HasTypeQName {
  private static final long serialVersionUID = 876879L;

  public static final String LOCAL_PART = "ControlPanelDesignObject";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);
  public static final String FEATURE_TOGGLE = "ae.studio.control-panel";
  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(Roles.CONTROL_PANEL_ADMINISTRATOR,
      Roles.CONTROL_PANEL_EDITOR, Roles.CONTROL_PANEL_VIEWER);
  public static final String CONTROL_PANEL_ALIAS = "controlPanel";
  public static final String PROP_NAME = "name";
  public static final String PROP_URL_STUB = "urlStub";
  private static final String CONTROL_PANEL_ROLE_MAP = "control_panel_rm";
  private static final String CONTROL_PANEL_ID = "control_panel_id";
  private static final String ROLE_MAP_ENTRY_ID = "rm_entry_id";

  public static final Map<RoleMapDefinitionFacade.RoleKey,Role> CONTROL_PANEL_ROLE_KEY_TO_ROLE = ImmutableMap.<RoleMapDefinitionFacade.RoleKey,Role>builder()
      .put(RoleMapDefinitionFacade.RoleKey.ADMINISTRATOR, Roles.CONTROL_PANEL_ADMINISTRATOR)
      .put(RoleMapDefinitionFacade.RoleKey.EDITOR, Roles.CONTROL_PANEL_EDITOR)
      .put(RoleMapDefinitionFacade.RoleKey.VIEWER, Roles.CONTROL_PANEL_VIEWER)
      .build();

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String versionUuid;
  private String settings;
  private String urlStub;
  private AuditInfo auditInfo = new AuditInfo();
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  public ControlPanel() {
  }

  public ControlPanel(String name, String description, String urlStub) {
    this.name = name;
    this.description = description;
    this.urlStub = urlStub;
  }

  public ControlPanel(ControlPanelDto controlPanelDto) {
    if (controlPanelDto.getId() != null && controlPanelDto.getId().getValue() != null &&
        controlPanelDto.getId().getValue().toValue_Value().intValue() !=
            com.appiancorp.core.expr.portable.Type.INTEGER.nullOf()) {
      this.id = Long.valueOf(controlPanelDto.getId().getValue().toValue_Value());
    }
    this.uuid = controlPanelDto.getUuid();
    this.name = controlPanelDto.getName();
    this.description = controlPanelDto.getDescription();
    this.versionUuid = controlPanelDto.getVersionUuid();
    this.urlStub = controlPanelDto.getUrlStub();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  @XmlAttribute(name = com.appiancorp.type.Id.LOCAL_PART, namespace = com.appiancorp.type.Id.NAMESPACE)
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

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement(name = "description", namespace = Type.APPIAN_NAMESPACE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "version_uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
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
  @HasForeignKeys(breadcrumb = BreadcrumbText.controlPanelSettings)
  public Settings getSettings() {
    return SettingsJsonConverter.storedFormJsonToStoredFormSettings(getSettingsJson(), Settings.class);
  }

  @Column(name = "url_stub", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(name = "urlStub", namespace = Type.APPIAN_NAMESPACE)
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @PrePersist
  private void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) {
      uuid = UUID.randomUUID().toString();
    }
    if (Strings.isNullOrEmpty(versionUuid)) {
      versionUuid = UUID.randomUUID().toString();
    }
    if (Strings.isNullOrEmpty(settings)) {
      settings = "{}";
    }
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = CONTROL_PANEL_ROLE_MAP, joinColumns = @JoinColumn(name = CONTROL_PANEL_ID), inverseJoinColumns = @JoinColumn(name = ROLE_MAP_ENTRY_ID))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  public Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
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
  @Transient
  public boolean isPublic() {
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // ControlPanels are never public (visible to all users).
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.CONTROL_PANEL_VIEWER.getName();
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
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
        .add("description", description)
        .add("versionUuid", versionUuid)
        .add("auditInfo", auditInfo)
        .toString();
  }

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  public boolean equivalentTo(final ControlPanel controlPanel) {
    return EQUIVALENCE.equivalent(this, controlPanel);
  }

  private static Equivalence<ControlPanel> EQUIVALENCE = new Equivalence<>() {
    @Override
    protected boolean doEquivalent(ControlPanel lhs, ControlPanel rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return Objects.equal(lhs.id, rhs.id) && stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.name, rhs.name) && stringEquals(lhs.description, rhs.description) &&
          stringEquals(lhs.urlStub, rhs.urlStub) && stringEquals(lhs.settings, rhs.settings);
    }

    @Override
    protected int doHash(ControlPanel controlPanel) {
      return Objects.hashCode(controlPanel.uuid, controlPanel.name, controlPanel.description);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  public boolean areInterfaceTypeUrlStubsEquivalent(ControlPanel other) {
    if (this.equivalentTo(other)) {
      return true;
    }
    List<InterfaceType> aInterfaceTypes = Optional.ofNullable(this.getSettings())
        .map(Settings::getInterfaceConfig)
        .map(InterfaceCfg::interfaceTypes)
        .orElse(new ArrayList<>());
    List<InterfaceType> bInterfaceTypes = Optional.ofNullable(other.getSettings())
        .map(Settings::getInterfaceConfig)
        .map(InterfaceCfg::interfaceTypes)
        .orElse(new ArrayList<>());
    if (aInterfaceTypes.equals(bInterfaceTypes)) {
      return true;
    }
    if (aInterfaceTypes.size() != bInterfaceTypes.size()) {
      return false;
    }
    Map<String, String> aInterfaceTypeMap = new HashMap<>();
    Map<String, String> bInterfaceTypeMap = new HashMap<>();
    for (InterfaceType interfaceType : aInterfaceTypes) {
      aInterfaceTypeMap.put(interfaceType.uuid(), interfaceType.urlStub());
    }
    for (InterfaceType interfaceType : bInterfaceTypes) {
      bInterfaceTypeMap.put(interfaceType.uuid(), interfaceType.urlStub());
    }
    for (Map.Entry<String, String> entry : aInterfaceTypeMap.entrySet()) {
      String bUrlStub = bInterfaceTypeMap.get(entry.getKey());
      if (!bUrlStub.equals(entry.getValue())) {
        return false;
      }
    }
    return true;
  }
}
