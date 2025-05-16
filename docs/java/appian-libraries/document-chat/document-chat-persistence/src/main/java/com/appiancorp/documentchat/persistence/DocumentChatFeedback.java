package com.appiancorp.documentchat.persistence;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="document_chat_feedback")
public class DocumentChatFeedback {
  private Long id;
  private Long knowledgeSetId;
  private Long userMessageId;
  private Long systemMessageId;
  private Long rating;
  private String feedback;
  private Long createdBy;
  private Timestamp createdOn;

  private DocumentChatFeedback(DocumentChatFeedbackBuilder builder) {
    this.id = builder.getId();
    this.knowledgeSetId = builder.getKnowledgeSetId();
    this.userMessageId = builder.getUserMessageId();
    this.systemMessageId = builder.getSystemMessageId();
    this.rating = builder.getRating();
    this.feedback = builder.getFeedback();
    this.createdBy = builder.getCreatedBy();
    this.createdOn = builder.getCreatedOn();
  }

  DocumentChatFeedback() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "knowledge_set_id")
  public Long getKnowledgeSetId() {
    return knowledgeSetId;
  }

  public void setKnowledgeSetId(Long knowledgeSetId) {
    this.knowledgeSetId = knowledgeSetId;
  }

  @Column(name = "user_message_id")
  public Long getUserMessageId() {
    return userMessageId;
  }

  public void setUserMessageId(Long userMessageId) {
    this.userMessageId = userMessageId;
  }

  @Column(name = "system_message_id")
  public Long getSystemMessageId() {
    return systemMessageId;
  }

  public void setSystemMessageId(Long systemMessageId) {
    this.systemMessageId = systemMessageId;
  }

  @Column(name = "rating", nullable = false)
  public Long getRating() {
    return rating;
  }

  public void setRating(Long rating) {
    this.rating = rating;
  }

  @Column(name = "feedback")
  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  @Column(name = "created_by", nullable = false)
  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  @Column(name="created_on", nullable = false)
  private Long getCreatedOnLong() {
    return createdOn == null ? null : createdOn.getTime();
  }

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
    DocumentChatFeedback that = (DocumentChatFeedback)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatFeedback (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatFeedback (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatFeedbackBuilder builder() {
    return new DocumentChatFeedbackBuilder();
  }

  public final static class DocumentChatFeedbackBuilder {
    private Long id;
    private Long knowledgeSetId;
    private Long userMessageId;
    private Long systemMessageId;
    private Long rating;
    private String feedback;
    private Long createdBy;
    private Timestamp createdOn;

    public DocumentChatFeedbackBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getId() {
      return id;
    }

    public DocumentChatFeedbackBuilder setKnowledgeSetId(Long knowledgeSetId) {
      this.knowledgeSetId = knowledgeSetId;
      return this;
    }

    public Long getKnowledgeSetId() {
      return knowledgeSetId;
    }

    public DocumentChatFeedbackBuilder setUserMessageId(Long userMessageId) {
      this.userMessageId = userMessageId;
      return this;
    }

    public Long getUserMessageId() {
      return userMessageId;
    }

    public DocumentChatFeedbackBuilder setSystemMessageId(Long systemMessageId) {
      this.systemMessageId = systemMessageId;
      return this;
    }

    public Long getSystemMessageId() {
      return systemMessageId;
    }

    public DocumentChatFeedbackBuilder setRating(Long rating) {
      this.rating = rating;
      return this;
    }

    public Long getRating() {
      return rating;
    }

    public DocumentChatFeedbackBuilder setFeedback(String feedback) {
      this.feedback = feedback;
      return this;
    }

    public String getFeedback() {
      return feedback;
    }

    public DocumentChatFeedbackBuilder setCreatedBy(Long createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Long getCreatedBy() {
      return createdBy;
    }

    public DocumentChatFeedbackBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatFeedback build() {
      return new DocumentChatFeedback(this);
    }
  }
}
