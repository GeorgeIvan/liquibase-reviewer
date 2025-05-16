package com.appiancorp.documentchat.persistence;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.collection.internal.PersistentSet;

import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.google.common.collect.ImmutableSet;

/**
 * Stores the state of a document chat knowledge set
 */
@Entity
@Table(name="document_chat_knowledge_set")
public class DocumentChatKnowledgeSet implements HasRoleMap {

  public static final String ID_FIELD = "id";
  public static final String NAME_FIELD = "name";
  public static final String DESCRIPTION_FIELD = "description";
  public static final String FOLDER_ID_FIELD = "folderId";
  public static final String STATUS_ID_FIELD = "statusId";
  public static final String KNOWLEDGE_BASE_UUID = "knowledgeBaseUuid";
  public static final String CREATED_BY_FIELD = "createdBy";
  public static final String CREATED_ON_FIELD = "createdOn";
  public static final String MODIFIED_BY_FIELD = "modifiedBy";
  public static final String MODIFIED_ON_FIELD = "modifiedOn";

  private Long id;
  private String name;
  private String description;
  private String folderId;
  private Long statusId;
  private String knowledgeBaseUuid;
  private Long createdBy;
  private Timestamp createdOn;
  private Long modifiedBy;
  private Timestamp modifiedOn;

  private String userRoleName;
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.DOCUMENT_CHAT_ADMINISTRATOR,
      Roles.DOCUMENT_CHAT_VIEWER
  );

  private DocumentChatKnowledgeSet(DocumentChatKnowledgeSetBuilder builder) {
    this.id = builder.getId();
    this.name = builder.getName();
    this.description = builder.getDescription();
    this.folderId = builder.getFolderId();
    this.statusId = builder.getStatusId();
    this.createdBy = builder.getCreatedBy();
    this.createdOn = builder.getCreatedOn();
    this.modifiedBy = builder.getModifiedBy();
    this.modifiedOn = builder.getModifiedOn();
    this.knowledgeBaseUuid = builder.getKnowledgeBaseUuid();
  }

  // Required for Hibernate reads
  DocumentChatKnowledgeSet(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "name", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "folder_id", nullable = false)
  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  @Column(name = "status_id", nullable = false)
  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  @Column(name = "knowledge_base_uuid", nullable = false)
  public String getKnowledgeBaseUuid() {
    return knowledgeBaseUuid;
  }

  public void setKnowledgeBaseUuid(String knowledgeBaseUuid) {
    this.knowledgeBaseUuid = knowledgeBaseUuid;
  }

  @Column(name = "created_by", nullable = false)
  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  @Column(name="created_on", nullable = false)
  @SuppressWarnings("unused")
  private Long getCreatedOnLong() {
    return createdOn == null ? null : createdOn.getTime();
  }

  @SuppressWarnings("unused")
  private void setCreatedOnLong(Long createdOn) {
    this.createdOn = createdOn == null ? null : new Timestamp(createdOn);
  }

  @Transient
  public Timestamp getCreatedOn() {
    return createdOn == null ? null : new Timestamp(createdOn.getTime());
  }

  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn == null ? null : new Timestamp(createdOn.getTime());
  }

  @Column(name = "modified_by", nullable = false)
  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @Column(name="modified_on", nullable = false)
  @SuppressWarnings("unused")
  private Long getModifiedOnLong() {
    return modifiedOn == null ? null : modifiedOn.getTime();
  }

  @SuppressWarnings("unused")
  private void setModifiedOnLong(Long modifiedOn) {
    this.modifiedOn = modifiedOn == null ? null : new Timestamp(modifiedOn);
  }

  @Transient
  public Timestamp getModifiedOn() {
    return modifiedOn == null ? null : new Timestamp(modifiedOn.getTime());
  }

  public void setModifiedOn(Timestamp modifiedOn) {
    this.modifiedOn = modifiedOn == null ? null : new Timestamp(modifiedOn.getTime());
  }

  // =============================================================
  // document_chat_rm
  // =============================================================

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "document_chat_rm",
      joinColumns = @JoinColumn(name = "knowledge_set_id"),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
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
    // Default to not allowing access to the knowledge set
    return "";
  }

  @Override
  @Transient
  public boolean isPublic() {
    // Knowledge Sets are never public (visible to all users).
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // Knowledge Sets are never public (visible to all users).
  }
  // ============================== END IMPLEMENTATION OF HasRoleMap ===============================

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentChatKnowledgeSet that = (DocumentChatKnowledgeSet)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatKnowledgeSet (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatKnowledgeSet (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatKnowledgeSetBuilder builder() {
    return new DocumentChatKnowledgeSetBuilder();
  }

  public final static class DocumentChatKnowledgeSetBuilder {

    private Long id;
    private String name;
    private String description;
    private String folderId;
    private Long statusId;
    private String knowledgeBaseUuid;
    private Long createdBy;
    private Timestamp createdOn;
    private Long modifiedBy;
    private Timestamp modifiedOn;

    private DocumentChatKnowledgeSetBuilder() {}

    public Long getId() {
      return id;
    }

    public DocumentChatKnowledgeSetBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public String getName() {
      return name;
    }

    public DocumentChatKnowledgeSetBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public String getDescription() {
      return description;
    }

    public DocumentChatKnowledgeSetBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    public String getFolderId() {
      return folderId;
    }

    public DocumentChatKnowledgeSetBuilder setFolderId(String folderId) {
      this.folderId = folderId;
      return this;
    }

    public Long getStatusId() {
      return statusId;
    }

    public DocumentChatKnowledgeSetBuilder setStatusId(Long statusId) {
      this.statusId = statusId;
      return this;
    }

    public String getKnowledgeBaseUuid() {
      return knowledgeBaseUuid;
    }

    public DocumentChatKnowledgeSetBuilder setKnowledgeBaseUuid(String knowledgeBaseUuid) {
      this.knowledgeBaseUuid = knowledgeBaseUuid;
      return this;
    }

    public Long getCreatedBy() {
      return createdBy;
    }

    public DocumentChatKnowledgeSetBuilder setCreatedBy(Long createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatKnowledgeSetBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public Long getModifiedBy() {
      return modifiedBy;
    }

    public DocumentChatKnowledgeSetBuilder setModifiedBy(Long modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    public Timestamp getModifiedOn() {
      return modifiedOn;
    }

    public DocumentChatKnowledgeSetBuilder setModifiedOn(Timestamp modifiedOn) {
      this.modifiedOn = modifiedOn;
      return this;
    }

    public DocumentChatKnowledgeSet build() {
      return new DocumentChatKnowledgeSet(this);
    }

  }
}
