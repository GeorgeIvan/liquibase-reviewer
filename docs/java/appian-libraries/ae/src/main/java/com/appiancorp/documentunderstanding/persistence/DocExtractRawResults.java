package com.appiancorp.documentunderstanding.persistence;

import static com.appiancorp.documentunderstanding.persistence.DocExtractVendorJob.JOB_ID_FIELD;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="doc_extract_raw_results")
public class DocExtractRawResults {
  private Long id;
  private DocExtractJob docExtractJob;
  private Long rawDocId;

  private DocExtractRawResults(DocExtractRawResultsBuilder builder) {
    this.docExtractJob = builder.getDocExtractJob();
    this.rawDocId = builder.getRawDocId();
  }

  // Required for Hibernate reads
  DocExtractRawResults() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = JOB_ID_FIELD, nullable=false)
  public DocExtractJob getDocExtractJob() {
    return docExtractJob;
  }

  public void setDocExtractJob(DocExtractJob docExtractJob) {
    this.docExtractJob = docExtractJob;
  }

  @Column(name = "raw_doc_id")
  public Long getRawDocId() {
    return rawDocId;
  }

  public void setRawDocId(Long rawDocId) {
    this.rawDocId = rawDocId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocExtractRawResults that = (DocExtractRawResults)o;
    return getRawDocId().equals(that.getRawDocId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRawDocId());
  }

  public static DocExtractRawResultsBuilder builder() {
    return new DocExtractRawResultsBuilder();
  }

  public static final class DocExtractRawResultsBuilder {
    private DocExtractRawResultsBuilder() {}

    private DocExtractJob docExtractJob;
    private Long rawDocId;

    public DocExtractRawResultsBuilder setDocExtractJob(DocExtractJob docExtractJob) {
      this.docExtractJob = docExtractJob;
      return this;
    }

    public DocExtractRawResultsBuilder setRawDocId(Long rawDocId) {
      this.rawDocId = rawDocId;
      return this;
    }

    private DocExtractJob getDocExtractJob() {
      return docExtractJob;
    }

    private Long getRawDocId() {
      return rawDocId;
    }

    public DocExtractRawResults build() {
      return new DocExtractRawResults(this);
    }
  }

}
