package com.appiancorp.documentchatcomponent.persistence;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="dcc_processed_document")
public class DocumentChatComponentDocument {
  private Long id;
  private Long appianDocumentId;
  private Long appianDocumentVersionId;
  private Timestamp appianDocumentCreatedOn;
  private Timestamp createdOn;
  private Timestamp lastChattedOn;
  private Long appianDocumentFileSize;

  private DocumentChatComponentDocument(DocumentChatComponentDocumentBuilder builder) {
    this.id = builder.getId();
    this.appianDocumentId = builder.getAppianDocumentId();
    this.appianDocumentVersionId = builder.getAppianDocumentVersionId();
    this.appianDocumentCreatedOn = builder.getAppianDocumentCreatedOn();
    this.createdOn = builder.getCreatedOn();
    this.lastChattedOn = builder.getLastChattedOn();
    this.appianDocumentFileSize = builder.getAppianDocumentFileSize();
  }

  DocumentChatComponentDocument() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "appian_document_id")
  public Long getAppianDocumentId() {
    return appianDocumentId;
  }

  public void setAppianDocumentId(Long appianDocumentId) {
    this.appianDocumentId = appianDocumentId;
  }

  @Column(name = "appian_document_version_id")
  public Long getAppianDocumentVersionId() {
    return appianDocumentVersionId;
  }

  public void setAppianDocumentVersionId(Long appianDocumentVersionId) {
    this.appianDocumentVersionId = appianDocumentVersionId;
  }

  @Column(name="appian_document_created_on", nullable = false)
  private Long getAppianDocumentCreatedOnLong() {
    return appianDocumentCreatedOn == null ? null : appianDocumentCreatedOn.getTime();
  }

  private void setAppianDocumentCreatedOnLong(Long appianDocumentCreatedOn) {
    this.appianDocumentCreatedOn = appianDocumentCreatedOn == null ? null : new Timestamp(appianDocumentCreatedOn);
  }

  @Transient
  public Timestamp getAppianDocumentCreatedOn() {
    return appianDocumentCreatedOn == null ? null : new Timestamp(appianDocumentCreatedOn.getTime());
  }

  public void setAppianDocumentCreatedOn(Timestamp appianDocumentCreatedOn) {
    this.appianDocumentCreatedOn = appianDocumentCreatedOn == null ? null : new Timestamp(appianDocumentCreatedOn.getTime());
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

  @Column(name="last_chatted_on", nullable = false)
  private Long getLastChattedOnLong() {
    return lastChattedOn == null ? null : lastChattedOn.getTime();
  }

  private void setLastChattedOnLong(Long lastChattedOn) {
    this.lastChattedOn = lastChattedOn == null ? null : new Timestamp(lastChattedOn);
  }

  @Transient
  public Timestamp getLastChattedOn() {
    return lastChattedOn == null ? null : new Timestamp(lastChattedOn.getTime());
  }

  public void setLastChattedOn(Timestamp lastChattedOn) {
    this.lastChattedOn = lastChattedOn == null ? null : new Timestamp(lastChattedOn.getTime());
  }

  public void setAppianDocumentFileSize(Long appianDocumentFileSize) {
    this.appianDocumentFileSize = appianDocumentFileSize;
  }

  @Column(name = "appian_document_file_size")
  public Long getAppianDocumentFileSize() {
    return appianDocumentFileSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentChatComponentDocument that = (DocumentChatComponentDocument)o;
    // Uses a minimal set of fields to uniquely identify a DocumentChatFeedback (for hibernate performance)
    return getId().equals(that.getId()) && getCreatedOn().equals(that.getCreatedOn());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocumentChatFeedback (for hibernate performance)
    return Objects.hash(getId(), getCreatedOn());
  }

  public static DocumentChatComponentDocumentBuilder builder() {
    return new DocumentChatComponentDocumentBuilder();
  }

  public final static class DocumentChatComponentDocumentBuilder {
    private Long id;
    private Long appianDocumentId;
    private Long appianDocumentVersionId;
    private Timestamp appianDocumentCreatedOn;
    private Timestamp createdOn;
    private Timestamp lastChattedOn;
    private Long appianDocumentFileSize;

    public DocumentChatComponentDocumentBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public Long getId() {
      return id;
    }

    public DocumentChatComponentDocumentBuilder setAppianDocumentId(Long appianDocumentId) {
      this.appianDocumentId = appianDocumentId;
      return this;
    }

    public Long getAppianDocumentId() {
      return appianDocumentId;
    }

    public DocumentChatComponentDocumentBuilder setAppianDocumentVersionId(Long appianDocumentVersionId) {
      this.appianDocumentVersionId = appianDocumentVersionId;
      return this;
    }

    public Long getAppianDocumentVersionId() {
      return appianDocumentVersionId;
    }

    public DocumentChatComponentDocumentBuilder setAppianDocumentCreatedOn(Timestamp appianDocumentCreatedOn) {
      this.appianDocumentCreatedOn = appianDocumentCreatedOn;
      return this;
    }

    public Timestamp getAppianDocumentCreatedOn() {
      return appianDocumentCreatedOn;
    }

    public DocumentChatComponentDocumentBuilder setCreatedOn(Timestamp createdOn) {
      this.createdOn = createdOn;
      return this;
    }

    public Timestamp getCreatedOn() {
      return createdOn;
    }

    public DocumentChatComponentDocumentBuilder setLastChattedOn(Timestamp lastChattedOn) {
      this.lastChattedOn = lastChattedOn;
      return this;
    }

    public Timestamp getLastChattedOn() {
      return lastChattedOn;
    }

    public DocumentChatComponentDocument build() {
      return new DocumentChatComponentDocument(this);
    }

    public DocumentChatComponentDocumentBuilder setAppianDocumentFileSize(Long appianDocumentFileSize) {
      this.appianDocumentFileSize = appianDocumentFileSize;
      return this;
    }

    public Long getAppianDocumentFileSize() {
      return appianDocumentFileSize;
    }
  }
}
