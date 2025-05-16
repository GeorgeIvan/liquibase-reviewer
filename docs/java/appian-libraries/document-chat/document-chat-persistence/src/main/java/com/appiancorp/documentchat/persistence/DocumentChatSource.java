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
 * Stores the state of a document chat source
 */
@Entity
@Table(name="document_chat_source")
public class DocumentChatSource {

  public static final String ID_FIELD = "id";
  public static final String MESSAGE_ID_FIELD = "messageId";
  public static final String DOCUMENT_ID_FIELD = "documentId";
  public static final String SOURCE_TEXT_FIELD = "sourceText";
  public static final String CREATED_BY_FIELD = "createdBy";
  public static final String CREATED_ON_FIELD = "createdOn";

  private Long id;
  private Long messageId;
  private Long documentId;
  private String sourceText;
  private Long createdBy;
  private Timestamp createdOn;

  private DocumentChatSource(DocumentChatSourceBuilder builder) {
    this.id = builder.getId();
    this.messageId = builder.getMessageId();
    this.documentId = builder.getDocumentId();
    this.sourceText = builder.getSourceText();
    this.createdBy = builder.getCreatedBy();
    this.createdOn = builder.getCreatedOn();
  }

  // Required for Hibernate reads
  DocumentChatSource(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "message_id", nullable = false)
  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  @Column(name = "document_id", nullable = true)
  public Long getDocumentId() {
    return documentId;
  }

  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
  }

  @Lob
  @Column(name = "source_text", nullable = false)
  public String getSourceText() {
    return sourceText;
  }

  public void setSourceText(String sourceText) {
    this.sourceText = sourceText;
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
    DocumentChatSource that = (DocumentChatSource)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatSource (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatSource (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatSourceBuilder builder() {
    return new DocumentChatSourceBuilder();
  }

  public final static class DocumentChatSourceBuilder {
    private Long id;
    private Long messageId;
    private Long documentId;
    private String sourceText;
    private Long createdBy;
    private Timestamp createdOn;

    private DocumentChatSourceBuilder() {}

    public Long getId() {
      return id;
    }

    public DocumentChatSourceBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getMessageId() {
      return messageId;
    }

    public DocumentChatSourceBuilder setMessageId(Long messageId) {
      this.messageId = messageId;
      return this;
    }

    public Long getDocumentId() {
      return documentId;
    }

    public DocumentChatSourceBuilder setDocumentId(Long documentId) {
      this.documentId = documentId;
      return this;
    }

    public String getSourceText() {
      return sourceText;
    }

    public DocumentChatSourceBuilder setSourceText(String sourceText) {
      this.sourceText = sourceText;
      return this;
    }

    public Long getCreatedBy() {
      return createdBy;
    }

    public DocumentChatSourceBuilder setCreatedBy(Long createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatSourceBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public DocumentChatSource build() {
      return new DocumentChatSource(this);
    }

  }
}
