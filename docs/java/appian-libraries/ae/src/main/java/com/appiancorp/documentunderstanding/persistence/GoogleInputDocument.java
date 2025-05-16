package com.appiancorp.documentunderstanding.persistence;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "doc_ext_google_input_docs")
public class GoogleInputDocument extends DocExtractVendorJob {

  private String externalFilename;
  private GoogleBatch batch;

  private GoogleInputDocument(GoogleInputDocumentBuilder builder) {
    this.externalFilename = builder.getExternalFilename();
    this.setJob(builder.getJob());
    this.setBatch(builder.getBatch());
  }

  // Required for Hibernate reads
  GoogleInputDocument() {
  }

  @Column(name = "external_filename", nullable = false)
  public String getExternalFilename() {
    return externalFilename;
  }

  public void setExternalFilename(String externalFilename) {
    this.externalFilename = externalFilename;
  }

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "batch_id")
  public GoogleBatch getBatch() {
    return batch;
  }

  public void setBatch(GoogleBatch batch) {
    this.batch = batch;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GoogleInputDocument that = (GoogleInputDocument)o;
    return getExternalFilename().equals(that.getExternalFilename());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getExternalFilename());
  }

  public static GoogleInputDocumentBuilder builder() {
    return new GoogleInputDocumentBuilder();
  }

  public static final class GoogleInputDocumentBuilder {
    private String externalFilename;
    private DocExtractJob job;
    private GoogleBatch batch;

    private GoogleInputDocumentBuilder() {
    }

    public GoogleInputDocumentBuilder setJob(DocExtractJob job) {
      this.job = job;
      return this;
    }

    public GoogleInputDocumentBuilder setBatch(GoogleBatch batch) {
      this.batch = batch;
      return this;
    }

    public GoogleInputDocumentBuilder setExternalFilename(String externalFilename) {
      this.externalFilename = externalFilename;
      return this;
    }

    private DocExtractJob getJob() {
      return job;
    }

    private GoogleBatch getBatch() {
      return batch;
    }

    private String getExternalFilename() {
      return externalFilename;
    }

    public GoogleInputDocument build() {
      return new GoogleInputDocument(this);
    }
  }
}
