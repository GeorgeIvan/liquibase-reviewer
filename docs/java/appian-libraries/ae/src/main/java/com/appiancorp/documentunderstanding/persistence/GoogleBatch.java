package com.appiancorp.documentunderstanding.persistence;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Stores the state of a doc extraction batch in Google.
 */
@Entity
@Table(name = "doc_ext_google_batch")
public class GoogleBatch extends DocExtractBatch {
  public static final String ID_FIELD = "id";
  public static final String BATCH_STATUS_FIELD = "batchStatus";
  public static final String GOOGLE_JOB_ID = "googleJobId";
  public static final String GOOGLE_SOURCE_BUCKET = "googleSourceBucket";
  public static final String GOOGLE_DESTINATION_BUCKET = "googleDestinationBucket";
  public static final String CREATE_TIME_FIELD = "createTime";
  public static final String CREATE_TIME_LONG_FIELD = "createTimeLong";
  public static final String DONE_FIELD = "done";

  private String googleJobId;
  private String sourceBucket;
  private String destinationBucket;
  private Timestamp createTime;
  private List<GoogleInputDocument> googleInputDocuments;

  private GoogleBatch(GoogleBatchBuilder builder) {
    super(builder.getDone());
    this.setId(builder.getId());
    this.googleJobId = builder.getGoogleJobId();
    this.sourceBucket = builder.getSourceBucket();
    this.destinationBucket = builder.getDestinationBucket();
    this.createTime = builder.getCreateTime();
    this.googleInputDocuments = builder.getGoogleInputDocuments();
  }

  // Required for Hibernate reads
  GoogleBatch() {
  }

  @Column(name = "google_job_id", nullable = false)
  public String getGoogleJobId() {
    return googleJobId;
  }

  public void setGoogleJobId(String googleJobId) {
    this.googleJobId = googleJobId;
  }

  @Column(name = "source_bucket", nullable = false)
  public String getSourceBucket() {
    return sourceBucket;
  }

  public void setSourceBucket(String sourceBucket) {
    this.sourceBucket = sourceBucket;
  }

  @Column(name = "destination_bucket", nullable = false)
  public String getDestinationBucket() {
    return destinationBucket;
  }

  public void setDestinationBucket(String destinationBucket) {
    this.destinationBucket = destinationBucket;
  }

  @Column(name = "create_time", nullable = false)
  @SuppressWarnings("unused")
  private Long getCreateTimeLong() {
    return createTime == null ? null : createTime.getTime();
  }

  @SuppressWarnings("unused")
  private void setCreateTimeLong(Long createTime) {
    this.createTime = createTime == null ? null : new Timestamp(createTime);
  }

  @Transient
  public Timestamp getCreateTime() {
    return createTime == null ? null : new Timestamp(createTime.getTime());
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime == null ? null : new Timestamp(createTime.getTime());
  }

  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public List<GoogleInputDocument> getGoogleInputDocuments() {
    return googleInputDocuments;
  }

  public void setGoogleInputDocuments(List<GoogleInputDocument> googleInputDocuments) {
    this.googleInputDocuments = googleInputDocuments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GoogleBatch that = (GoogleBatch)o;
    // Uses a minimal set of fields to uniquely identify a GoogleBatch (for hibernate performance)
    return getGoogleJobId().equals(that.getGoogleJobId()) && getCreateTime().equals(that.getCreateTime());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a GoogleBatch (for hibernate performance)
    return Objects.hash(getGoogleJobId(), getCreateTime());
  }

  public static GoogleBatchBuilder builder() {
    return new GoogleBatchBuilder();
  }

  public final static class GoogleBatchBuilder {
    private Long id;
    private String googleJobId;
    private String sourceBucket;
    private String destinationBucket;
    private Timestamp createTime;
    private boolean done;
    private List<GoogleInputDocument> googleInputDocuments;

    private GoogleBatchBuilder() {
    }

    public GoogleBatchBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public GoogleBatchBuilder setGoogleJobId(String googleJobId) {
      this.googleJobId = googleJobId;
      return this;
    }

    public GoogleBatchBuilder setSourceBucket(String sourceBucket) {
      this.sourceBucket = sourceBucket;
      return this;
    }

    public GoogleBatchBuilder setDestinationBucket(String destinationBucket) {
      this.destinationBucket = destinationBucket;
      return this;
    }

    public GoogleBatchBuilder setCreateTime(Timestamp createTime) {
      this.createTime = createTime == null ? null : new Timestamp(createTime.getTime());
      return this;
    }

    public GoogleBatchBuilder setDone(boolean done) {
      this.done = done;
      return this;
    }

    public GoogleBatchBuilder setGoogleInputDocuments(List<GoogleInputDocument> googleInputDocuments){
      this.googleInputDocuments = googleInputDocuments;
      return this;
    }

    private Long getId() {
      return id;
    }

    private String getGoogleJobId() {
      return googleJobId;
    }

    private String getSourceBucket() {
      return sourceBucket;
    }

    private String getDestinationBucket() {
      return destinationBucket;
    }

    private Timestamp getCreateTime() {
      return createTime == null ? null : new Timestamp(createTime.getTime());
    }

    private boolean getDone() {
      return done;
    }

    private List<GoogleInputDocument> getGoogleInputDocuments() {
      return googleInputDocuments;
    }

    public GoogleBatch build() {
      return new GoogleBatch(this);
    }
  }
}
