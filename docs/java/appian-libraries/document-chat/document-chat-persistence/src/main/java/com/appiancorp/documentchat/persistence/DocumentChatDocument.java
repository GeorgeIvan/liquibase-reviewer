package com.appiancorp.documentchat.persistence;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Stores the state of a document chat document
 */
@Entity
@Table(name="document_chat_document")
public class DocumentChatDocument {

  public static final String ID_FIELD = "id";
  public static final String DOCUMENT_ID = "documentId";
  public static final String KNOWLEDGE_SET_ID = "knowledgeSetId";
  public static final String DOCUMENT_UUID_FIELD = "documentUuid";
  public static final String UPLOADED_BY_FIELD = "uploadedBy";
  public static final String UPLOADED_ON_FIELD = "uploadedOn";

  private Long id;
  private Long documentId;
  private Long knowledgeSetId;
  private String documentUuid;
  private Long uploadedBy;
  private Timestamp uploadedOn;

  private DocumentChatDocument(DocumentChatDocumentBuilder builder) {
    this.id = builder.getId();
    this.documentId = builder.getDocumentId();
    this.knowledgeSetId = builder.getKnowledgeSetId();
    this.documentUuid = builder.getDocumentUuid();
    this.uploadedBy = builder.getUploadedBy();
    this.uploadedOn = builder.getUploadedOn();
  }

  // Required for Hibernate reads
  DocumentChatDocument(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "document_id", nullable = false)
  public Long getDocumentId() {
    return documentId;
  }

  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
  }

  @Column(name = "knowledge_set_id", nullable = false)
  public Long getKnowledgeSetId() {
    return knowledgeSetId;
  }

  public void setKnowledgeSetId(Long knowledgeSetId) {
    this.knowledgeSetId = knowledgeSetId;
  }

  @Column(name = "document_uuid", nullable = false)
  public String getDocumentUuid() {
    return documentUuid;
  }

  public void setDocumentUuid(String documentUuid) {
    this.documentUuid = documentUuid;
  }

  @Column(name = "uploaded_by", nullable = false)
  public Long getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(Long uploadedBy) {
    this.uploadedBy = uploadedBy;
  }

  @Column(name="uploaded_on", nullable = false)
  @SuppressWarnings("unused")
  private Long getUploadedOnLong() {
    return uploadedOn == null ? null : uploadedOn.getTime();
  }

  @SuppressWarnings("unused")
  private void setUploadedOnLong(Long uploadedOn) {
    this.uploadedOn = uploadedOn == null ? null : new Timestamp(uploadedOn);
  }

  @Transient
  public Timestamp getUploadedOn() {
    return uploadedOn == null ? null : new Timestamp(uploadedOn.getTime());
  }

  public void setUploadedOn(Timestamp uploadedOn) {
    this.uploadedOn = uploadedOn == null ? null : new Timestamp(uploadedOn.getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentChatDocument that = (DocumentChatDocument)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatDocument (for hibernate performance)
    return getId().equals(that.getId()) && getUploadedOn().equals(that.getUploadedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatDocument (for hibernate performance)
    return Objects.hash(getId(), getUploadedOn());
  }

  public static DocumentChatDocumentBuilder builder() {
    return new DocumentChatDocumentBuilder();
  }

  public final static class DocumentChatDocumentBuilder {
    private Long id;
    private Long documentId;
    private Long knowledgeSetId;
    private String documentUuid;
    private Long uploadedBy;
    private Timestamp uploadedOn;

    private DocumentChatDocumentBuilder() {}

    public Long getId() {
      return id;
    }

    public DocumentChatDocumentBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getDocumentId() {
      return documentId;
    }

    public DocumentChatDocumentBuilder setDocumentId(Long documentId) {
      this.documentId = documentId;
      return this;
    }

    public Long getKnowledgeSetId() {
      return knowledgeSetId;
    }

    public DocumentChatDocumentBuilder setKnowledgeSetId(Long knowledgeSetId) {
      this.knowledgeSetId = knowledgeSetId;
      return this;
    }

    public String getDocumentUuid() {
      return documentUuid;
    }

    public DocumentChatDocumentBuilder setDocumentUuid(String documentUuid) {
      this.documentUuid = documentUuid;
      return this;
    }

    public Long getUploadedBy() {
      return uploadedBy;
    }

    public DocumentChatDocumentBuilder setUploadedBy(Long uploadedBy) {
      this.uploadedBy = uploadedBy;
      return this;
    }

    public Timestamp getUploadedOn() {
      return uploadedOn;
    }

    public DocumentChatDocumentBuilder setUploadedOn(Timestamp uploadedOn) {
      this.uploadedOn = uploadedOn;
      return this;
    }

    public DocumentChatDocument build() {
      return new DocumentChatDocument(this);
    }

  }
}
