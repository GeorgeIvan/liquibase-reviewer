package com.appiancorp.documentchat.persistence;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Stores the state of a document chat message
 */
@Entity
@Table(name="document_chat_message")
public class DocumentChatMessage {

  public static final String ID_FIELD = "id";
  public static final String CONVERSATION_ID_FIELD = "conversationId";
  public static final String MESSAGE_TEXT_FIELD = "messageText";

  // AN-266110 Add score and feedback
  public static final String ROLE_ID_FIELD = "roleId";
  public static final String CREATED_BY_FIELD = "createdBy";
  public static final String CREATED_ON_FIELD = "createdOn";

  private Long id;
  private Long conversationId;
  private String messageText;
  // AN-266110 Add score and feedback
  private Long roleId;
  private Long createdBy;
  private Timestamp createdOn;

  private DocumentChatMessage(DocumentChatMessageBuilder builder) {
    this.id = builder.getId();
    this.conversationId = builder.getConversationId();
    this.messageText = builder.getMessageText();
    // AN-266110 Add score and feedback
    this.roleId = builder.getRoleId();
    this.createdBy = builder.getCreatedBy();
    this.createdOn = builder.getCreatedOn();
  }

  // Required for Hibernate reads
  DocumentChatMessage(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "conversation_id", nullable = false)
  public Long getConversationId() {
    return conversationId;
  }

  public void setConversationId(Long conversationId) {
    this.conversationId = conversationId;
  }

  @Lob
  @Column(name = "message_text", nullable = false)
  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  // AN-266110 Add score and feedback

  @Column(name = "role_id", nullable = false)
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentChatMessage that = (DocumentChatMessage)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatMessage (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatMessage (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatMessageBuilder builder() {
    return new DocumentChatMessageBuilder();
  }

  public final static class DocumentChatMessageBuilder {
    private Long id;
    private Long conversationId;
    private String messageText;
    // AN-266110 Add score and feedback
    private Long roleId;
    private Long createdBy;
    private Timestamp createdOn;

    private DocumentChatMessageBuilder() {}

    public Long getId() {
      return id;
    }

    public DocumentChatMessageBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getConversationId() {
      return conversationId;
    }

    public DocumentChatMessageBuilder setConversationId(Long conversationId) {
      this.conversationId = conversationId;
      return this;
    }

    public String getMessageText() {
      return messageText;
    }

    public DocumentChatMessageBuilder setMessageText(String messageText) {
      this.messageText = messageText;
      return this;
    }

    // AN-266110 Add score and feedback

    public Long getRoleId() {
      return roleId;
    }

    public DocumentChatMessageBuilder setRoleId(Long roleId) {
      this.roleId = roleId;
      return this;
    }

    public Long getCreatedBy() {
      return createdBy;
    }

    public DocumentChatMessageBuilder setCreatedBy(Long createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatMessageBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public DocumentChatMessage build() {
      return new DocumentChatMessage(this);
    }

  }
}
