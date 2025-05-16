package com.appiancorp.documentchat.persistence;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Stores the state of a document chat conversation
 */
@Entity
@Table(name="document_chat_conversation")
public class DocumentChatConversation {

  public static final String ID_FIELD = "id";
  public static final String KNOWLEDGE_SET_ID_FIELD = "knowledgeSetId";
  public static final String CREATED_BY_FIELD = "createdBy";
  public static final String CREATED_ON_FIELD = "createdOn";
  public static final String CREATED_ON_LONG_FIELD = "createdOnLong";
  public static final String MODIFIED_BY_FIELD = "modifiedBy";
  public static final String MODIFIED_ON_FIELD = "modifiedOn";

  private Long id;
  private Long knowledgeSetId;
  private Long createdBy;
  private Timestamp createdOn;
  private Long modifiedBy;
  private Timestamp modifiedOn;

  private DocumentChatConversation(DocumentChatConversationBuilder builder) {
    this.id = builder.getId();
    this.knowledgeSetId = builder.getKnowledgeSetId();
    this.createdBy = builder.getCreatedBy();
    this.createdOn = builder.getCreatedOn();
    this.modifiedBy = builder.getModifiedBy();
    this.modifiedOn = builder.getModifiedOn();
  }

  // Required for Hibernate reads
  DocumentChatConversation(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "knowledge_set_id", nullable = false)
  public Long getKnowledgeSetId() {
    return knowledgeSetId;
  }

  public void setKnowledgeSetId(Long knowledgeSetId) {
    this.knowledgeSetId = knowledgeSetId;
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
  public Timestamp setModifiedOn() {
    return modifiedOn == null ? null : new Timestamp(modifiedOn.getTime());
  }

  public void setModifiedOn(Timestamp modifiedOn) {
    this.modifiedOn = modifiedOn == null ? null : new Timestamp(modifiedOn.getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentChatConversation that = (DocumentChatConversation)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatConversation (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatConversation (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatConversationBuilder builder() {
    return new DocumentChatConversationBuilder();
  }

  public final static class DocumentChatConversationBuilder {

    private Long id;
    private Long knowledgeSetId;
    private Long createdBy;
    private Timestamp createdOn;
    private Long modifiedBy;
    private Timestamp modifiedOn;

    private DocumentChatConversationBuilder() {}

    public Long getId() {
      return id;
    }

    public DocumentChatConversationBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getKnowledgeSetId() {
      return knowledgeSetId;
    }

    public DocumentChatConversationBuilder setKnowledgeSetId(Long knowledgeSetId) {
      this.knowledgeSetId = knowledgeSetId;
      return this;
    }

    public Long getCreatedBy() {
      return createdBy;
    }

    public DocumentChatConversationBuilder setCreatedBy(Long createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatConversationBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public Long getModifiedBy() {
      return modifiedBy;
    }

    public DocumentChatConversationBuilder setModifiedBy(Long modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    public Timestamp getModifiedOn() {
      return modifiedOn;
    }

    public DocumentChatConversationBuilder setModifiedOn(Timestamp modifiedOn) {
      this.modifiedOn = modifiedOn;
      return this;
    }

    public DocumentChatConversation build() {
      return new DocumentChatConversation(this);
    }

  }
}
