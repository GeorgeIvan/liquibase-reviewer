package com.appiancorp.enduserreporting.entities;

import static com.appiancorp.enduserreporting.entities.SsaDashboardType.DASHBOARD_TYPE;
import static com.appiancorp.enduserreporting.entities.SsaReportType.REPORT_TYPE;
import static com.appiancorp.security.user.User.JOIN_COL_USR_ID;

import java.io.Serial;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.collection.internal.PersistentSet;

import com.appiancorp.enduserreporting.persistence.SsaLibraryObjectCfg;
import com.appiancorp.enduserreporting.persistence.SsaSupportedComponentType;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.security.user.User;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings("java:S1448")
@Entity
@Table(name = "ssa_library_object")
public class SsaLibraryObjectCfgImpl implements SsaLibraryObjectCfg, Id<Long>, Uuid<String>, HasAuditInfo {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String recordTypeUuid;
  private Set<SsaObjectReferenceImpl> ssaObjectReferences;
  private SsaSupportedComponentType selectedComponent = SsaSupportedComponentType.GRID;
  private SsaObjectType objectType = REPORT_TYPE;
  private String configJson;
  private boolean isPublished; // legacy field to share objects with the DFRC group. Use isShared instead
  private boolean isSharedWithPhq;
  private AuditInfo auditInfo = new AuditInfo();
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private transient List<SsaDashboardItemDecorator> dashboardItemDecorators = Collections.emptyList();
  private transient Set<User> favoritedByUsers = new HashSet<>();
  private transient boolean isShared; // true if isPublished is true or if the object is shared via role map
  private transient boolean isEditor;
  private transient boolean isFavorited; // true if the logged-in user has favorited the object

  public static final String PROP_ID = "id";
  public static final String PROP_FAVORITED_BY_USERS = "favoritedByUsers";

  //IMPORTANT: The order is significant (from highest to lowest privileges)
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.SSA_LIBRARY_OBJECT_EDITOR,
      Roles.SSA_LIBRARY_OBJECT_VIEWER
  );

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Serial
  private Object readResolve() {
    if (this.dashboardItemDecorators == null) {
      this.dashboardItemDecorators = Collections.emptyList();
    }
    if (this.roleMapEntries == null) {
      this.roleMapEntries = new HashSet<>();
    }
    return this;
  }

  public SsaLibraryObjectCfgImpl() {}

  /**
   * constructor needed for hibernate GET ALL query when the
   * SSA RoleMap feature toggle (ae.records-insights.end-user-reporting-role-map-security-enabled) is on.
   * Includes only fields needed for analytics library
   */
  public SsaLibraryObjectCfgImpl(
      Long id,
      String uuid,
      String name,
      String description,
      String recordTypeUuid,
      boolean isPublished,
      AuditInfo auditInfo,
      Byte selectedComponentByte,
      byte objectTypeByte,
      boolean isSharedWithPhq,
      boolean isShared,
      boolean isFavorited,
      boolean isEditor) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.description = description;
    this.recordTypeUuid = recordTypeUuid;
    this.isPublished = isPublished;
    this.auditInfo = auditInfo;
    this.selectedComponent = SsaSupportedComponentType.getComponentTypeByByte(selectedComponentByte);
    this.objectType = getSsaLibraryObjectTypeByByte(objectTypeByte);
    this.isSharedWithPhq = isSharedWithPhq;
    this.isShared = isShared;
    this.isFavorited = isFavorited;
    this.isEditor = isEditor;
  }

  /**
   * Constructor needed for hibernate GET ALL query when the
   * SSA RoleMap feature toggle (ae.records-insights.end-user-reporting-role-map-security-enabled) is off.
   * Includes only fields needed for analytics library
   */
  public SsaLibraryObjectCfgImpl(
      Long id,
      String uuid,
      String name,
      String description,
      String recordTypeUuid,
      boolean isPublished,
      AuditInfo auditInfo,
      Byte selectedComponentByte,
      byte objectTypeByte,
      boolean isSharedWithPhq,
      boolean isShared,
      boolean isFavorited) {
    this(id, uuid, name, description, recordTypeUuid, isPublished, auditInfo, selectedComponentByte,
        objectTypeByte, isSharedWithPhq, isShared, isFavorited, false);
  }

  public SsaLibraryObjectCfgImpl(
      SsaLibraryObjectCfg ssaLibraryObjectCfg,
      String name,
      String description,
      AuditInfo auditInfo
  ) {
    this.id = null;
    this.uuid = null;
    this.name = name;
    this.description = description;
    this.recordTypeUuid = ssaLibraryObjectCfg.getRecordTypeUuid();
    this.selectedComponent = SsaSupportedComponentType.getComponentTypeByName(
        ssaLibraryObjectCfg.getSelectedComponentName());
    this.objectType = ssaLibraryObjectCfg.getObjectType();
    this.configJson = ssaLibraryObjectCfg.getConfigJson();
    this.isPublished = false;
    this.isShared = false;
    this.isEditor = false;
    this.isFavorited = false;
    this.auditInfo = auditInfo;
  }

  /**
   * Constructor needed for hibernate GET BY UUID & OBJECT TYPE query
   * Sets the calculated isFavorited flag on the library object
   */
  public SsaLibraryObjectCfgImpl(
      SsaLibraryObjectCfgImpl ssaLibraryObjectCfg,
      boolean isFavorited
  ) {
    this.id = ssaLibraryObjectCfg.getId();
    this.uuid = ssaLibraryObjectCfg.getUuid();
    this.name = ssaLibraryObjectCfg.getName();
    this.description = ssaLibraryObjectCfg.getDescription();
    this.recordTypeUuid = ssaLibraryObjectCfg.getRecordTypeUuid();
    this.selectedComponent = SsaSupportedComponentType.getComponentTypeByName(
        ssaLibraryObjectCfg.getSelectedComponentName());
    this.objectType = ssaLibraryObjectCfg.getObjectType();
    this.configJson = ssaLibraryObjectCfg.getConfigJson();
    this.roleMapEntries = ssaLibraryObjectCfg.getRoleMapEntries();
    this.ssaObjectReferences = ssaLibraryObjectCfg.getSsaObjectReferences();
    this.auditInfo = ssaLibraryObjectCfg.getAuditInfo();
    this.isPublished = ssaLibraryObjectCfg.getIsPublished();
    this.isShared = ssaLibraryObjectCfg.getIsShared();
    this.isSharedWithPhq = ssaLibraryObjectCfg.getIsSharedWithPhq();
    this.isEditor = ssaLibraryObjectCfg.getIsEditor();
    this.favoritedByUsers = ssaLibraryObjectCfg.getFavoritedByUsers();
    this.isFavorited = isFavorited;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false, unique = true)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "record_type_uuid", nullable = true)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "parent_id", nullable=false)
  public Set<SsaObjectReferenceImpl> getSsaObjectReferences() {
    return ssaObjectReferences;
  }

  /* See https://stackoverflow.com/questions/5587482/hibernate-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenc */
  public void setSsaObjectReferences(Set<SsaObjectReferenceImpl> ssaObjectReferences) {
    if(this.ssaObjectReferences == null) {
      this.ssaObjectReferences = ssaObjectReferences;
    } else {
      this.ssaObjectReferences.clear();
      if (ssaObjectReferences != null && !ssaObjectReferences.isEmpty()) {
        this.ssaObjectReferences.addAll(ssaObjectReferences);
      }
    }
  }

  @Transient
  public Set<Long> getSsaObjectReferenceChildIds() {
    return ssaObjectReferences.stream()
        .map(SsaObjectReferenceImpl::getChildId)
        .collect(Collectors.toSet());
  }

  public void setSsaObjectReferencesByChildIds(Set<Long> ssaObjectReferenceChildIds) {
    Set<SsaObjectReferenceImpl> ssaObjectReferencesToAdd = new HashSet<>();

    if (ssaObjectReferenceChildIds != null && !ssaObjectReferenceChildIds.isEmpty()) {
      /* Get already existing SSA Object References, so we don't rewrite them */
      retainExistingSsaObjectReferences(ssaObjectReferencesToAdd, ssaObjectReferenceChildIds);

      /* Create any new object references and include them in the new set */
      ssaObjectReferencesToAdd.addAll(
          ssaObjectReferenceChildIds.stream()
              .map(childReferenceId -> new SsaObjectReferenceImpl(id, childReferenceId))
              .collect(Collectors.toSet())
      );
    }

    this.setSsaObjectReferences(ssaObjectReferencesToAdd);
  }

  @Transient
  private void retainExistingSsaObjectReferences(Set<SsaObjectReferenceImpl> ssaObjectReferencesToAdd,
      Set<Long> ssaObjectReferenceChildIds){
    if (this.ssaObjectReferences == null || this.ssaObjectReferences.isEmpty()) {
      return;
    }

    Set<Long> currentSsaObjectReferenceIds = getSsaObjectReferenceChildIds();
    currentSsaObjectReferenceIds.retainAll(ssaObjectReferenceChildIds);

    ssaObjectReferencesToAdd.addAll(getSsaObjectReferencesByIds(currentSsaObjectReferenceIds));
    ssaObjectReferenceChildIds.removeAll(currentSsaObjectReferenceIds);
  }

  @Transient
  private Set<SsaObjectReferenceImpl> getSsaObjectReferencesByIds(Set<Long> ssaObjectReferenceIds) {
    if (ssaObjectReferenceIds == null || ssaObjectReferenceIds.isEmpty()) {
      return Collections.emptySet();
    }

    return ssaObjectReferences.stream()
        .filter(ssaObjectReference -> ssaObjectReferenceIds.contains(ssaObjectReference.getChildId()))
        .collect(Collectors.toSet());
  }

  @Transient
  private SsaSupportedComponentType getSelectedComponent() {
    return selectedComponent;
  }

  @Transient
  public String getSelectedComponentName() {
    return selectedComponent == null ? null : selectedComponent.getComponentName();
  }

  public void setSelectedComponent(SsaSupportedComponentType selectedComponent) {
    this.selectedComponent = selectedComponent;
  }

  @Column(name = "selected_component", nullable = true)  //Return type is the Byte instead of byte since selectedComponent is nullable
  private Byte getSelectedComponentByte() {
    return selectedComponent == null ? null : selectedComponent.getComponentByte();
  }

  private void setSelectedComponentByte(Byte selectedComponentByte) {
    setSelectedComponent(SsaSupportedComponentType.getComponentTypeByByte(selectedComponentByte));
  }

  @Transient
  public SsaObjectType getObjectType() {
    return objectType;
  }

  public void setObjectType(SsaObjectType objectType) {
    this.objectType = objectType;
  }

  @Column(name = "object_type", nullable = false)
  private byte getObjectTypeByte() {
    return objectType.getObjectTypeByte();
  }

  private void setObjectTypeByte(byte objectTypeByte) {
    objectType = getSsaLibraryObjectTypeByByte(objectTypeByte);
  }

  private static SsaObjectType getSsaLibraryObjectTypeByByte(byte objectTypeByte) {
    if (objectTypeByte == REPORT_TYPE.getObjectTypeByte()) {
      return REPORT_TYPE;
    } else if (objectTypeByte == DASHBOARD_TYPE.getObjectTypeByte()) {
      return DASHBOARD_TYPE;
    }
    throw new IllegalArgumentException(
        "The following object type is not supported for the SsaLibraryObjectCfg entity: " + objectTypeByte);
  }

  @Column(name = "config_json", nullable = false)
  @Lob
  public String getConfigJson() {
    return configJson;
  }

  public void setConfigJson(String configJson) {
    this.configJson = configJson;
  }

  /* isPublished refers to sharing with the DF Report Creators Group. This field and associated code path associated code
     path will be removed in AN-297792. The new general access sharing will use the isSharedWithPhq field, which represents
     sharing to the PHQ Users group.
   */
  @Column(name = "is_published", nullable = false)
  public boolean getIsPublished() {
    return isPublished;
  }

  public void setIsPublished(boolean isPublished) {
    this.isPublished = isPublished;
  }

  @Override
  @Transient
  public boolean getIsShared() {
    return isShared;
  }

  @Override
  @Transient
  public boolean getIsEditor() {
    return isEditor;
  }

  @Column(name = "is_shared_with_phq", nullable = false)
  public boolean getIsSharedWithPhq() {
    return isSharedWithPhq;
  }

  public void setIsSharedWithPhq(boolean isSharedWithPhq) {
    this.isSharedWithPhq = isSharedWithPhq;
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
  @Transient
  public List<SsaDashboardItemDecorator> getDashboardItemDecorators() {
    return dashboardItemDecorators;
  }

  @Override
  public void setDashboardItemDecorators(List<SsaDashboardItemDecorator> dashboardItemDecorators) {
    if (REPORT_TYPE.equals(objectType)) {
      throw new IllegalArgumentException("Reports cannot have dashboard items.");
    }
    this.dashboardItemDecorators = dashboardItemDecorators;
  }

  @Override
  @Transient
  public String getCreatedByUsername() {
    return getAuditInfo().getCreatedByUsername();
  }

  @Override
  @Transient
  public Timestamp getCreatedTs() {
    return getAuditInfo().getCreatedTs();
  }

  @Override
  @Transient
  public String getUpdatedByUsername() {
    return getAuditInfo().getUpdatedByUsername();
  }

  @Override
  @Transient
  public Timestamp getLastUpdatedTs() {
    return getAuditInfo().getUpdatedTs();
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "ssa_library_object_rm",
      joinColumns = @JoinColumn(name = "ssa_library_object_id"),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Transient
  public boolean getIsFavoritedByCurrentUser() {
    return isFavorited;
  }

  @ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JoinTable(
      name = "ssa_library_object_usr_fav",
      joinColumns = @JoinColumn(name = "ssa_library_object_id"),
      inverseJoinColumns = @JoinColumn(name = JOIN_COL_USR_ID))
  public Set<User> getFavoritedByUsers() {
    return favoritedByUsers;
  }

  public void setFavoritedByUsers(Set<User> users) {
    this.favoritedByUsers = users;
  }

  // ============================= BEGIN IMPLEMENTATION OF HasRoleMap ==============================
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

  /**
   * @deprecated Although this method is required to be public to implement an interface, devs should
   * not call it unless they know exactly what they're doing, since this method doesn't ensure the Group
   * ids in `roleMap` are converted to the proper RDBMS representation. Instead, call
   * {@link com.appiancorp.enduserreporting.service.SsaLibraryObjectService#setRoleMap(String, RoleMap)}.
   */
  @Override
  @Deprecated
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
  @Transient
  public String getFallbackRoleName() {
    return "";
  }

  @Override
  @Transient
  public boolean isPublic() {
    // SSA Library Objects are never public (visible to all users).
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // SSA Library objects are never public (visible to all users).
  }

  @Override
  @SuppressWarnings("checkstyle:CyclomaticComplexity")
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
      }
    SsaLibraryObjectCfgImpl that = (SsaLibraryObjectCfgImpl)o;
    return isPublished == that.isPublished && isEditor == that.isEditor && isShared == that.isShared &&
        isSharedWithPhq == that.isSharedWithPhq && isFavorited == that.isFavorited &&
        Objects.equals(id, that.id) &&
        Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name) &&
        Objects.equals(description, that.description) &&
        Objects.equals(recordTypeUuid, that.recordTypeUuid) &&
        Objects.equals(ssaObjectReferences, that.ssaObjectReferences) &&
        selectedComponent == that.selectedComponent && Objects.equals(objectType, that.objectType) &&
        Objects.equals(configJson, that.configJson) && Objects.equals(auditInfo, that.auditInfo) &&
        Objects.equals(roleMapEntries, that.roleMapEntries) &&
        Objects.equals(dashboardItemDecorators, that.dashboardItemDecorators) &&
        Objects.equals(favoritedByUsers, that.favoritedByUsers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uuid, name, description, recordTypeUuid, ssaObjectReferences, selectedComponent,
        objectType, configJson, isPublished, isShared, auditInfo, roleMapEntries, dashboardItemDecorators,
        favoritedByUsers, isEditor, isSharedWithPhq, isFavorited);
  }

  @Override
  public String toString() {
    return "SsaLibraryObjectCfgImpl{" + "id=" + id + ", uuid='" + uuid + '\'' + ", objectType='" + objectType + '\'' +
        '}';
  }
}
