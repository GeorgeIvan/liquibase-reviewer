package com.appiancorp.documentunderstanding.persistence;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="doc_ext_google_vendor_job")
public class GoogleDocExtractVendorJob extends DocExtractVendorJob {

  public static final String GOOGLE_JOB_ID = "googleJobId";
  public static final String GOOGLE_SOURCE_BUCKET = "googleSourceBucket";
  public static final String GOOGLE_DESTINATION_BUCKET = "googleDestinationBucket";

  private String googleJobId;
  private String sourceBucket;
  private String destinationBucket;
  private String externalFilename;

  private GoogleDocExtractVendorJob(GoogleDocExtractVendorJobBuilder builder) {
    this.googleJobId = builder.getGoogleJobId();
    this.sourceBucket = builder.getSourceBucket();
    this.destinationBucket = builder.getDestinationBucket();
    this.externalFilename = builder.getExternalFilename();
    this.setJob(builder.getJob());
  }

  // Required for Hibernate reads
  GoogleDocExtractVendorJob() {}

  @Column(name="google_job_id", nullable = false)
  public String getGoogleJobId() {
    return googleJobId;
  }

  public void setGoogleJobId(String googleJobId) {
    this.googleJobId = googleJobId;
  }

  @Column(name="source_bucket", nullable = false)
  public String getSourceBucket() {
    return sourceBucket;
  }

  public void setSourceBucket(String sourceBucket) {
    this.sourceBucket = sourceBucket;
  }

  @Column(name="destination_bucket", nullable = false)
  public String getDestinationBucket() {
    return destinationBucket;
  }

  public void setDestinationBucket(String destinationBucket) {
    this.destinationBucket = destinationBucket;
  }

  @Column(name="external_filename", nullable = false)
  public String getExternalFilename() {
    return externalFilename;
  }

  public void setExternalFilename(String externalFilename) {
    this.externalFilename = externalFilename;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GoogleDocExtractVendorJob that = (GoogleDocExtractVendorJob)o;
    return getGoogleJobId().equals(that.getGoogleJobId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getGoogleJobId());
  }

  public static GoogleDocExtractVendorJobBuilder builder() {
    return new GoogleDocExtractVendorJobBuilder();
  }

  public static final class GoogleDocExtractVendorJobBuilder {
    private String googleJobId;
    private String sourceBucket;
    private String destinationBucket;
    private String externalFilename;
    private DocExtractJob job;

    private GoogleDocExtractVendorJobBuilder() {

    }

    public GoogleDocExtractVendorJobBuilder setGoogleJobId(String googleJobId) {
      this.googleJobId = googleJobId;
      return this;
    }

    public GoogleDocExtractVendorJobBuilder setSourceBucket(String sourceBucket) {
      this.sourceBucket = sourceBucket;
      return this;
    }

    public GoogleDocExtractVendorJobBuilder setDestinationBucket(String destinationBucket) {
      this.destinationBucket = destinationBucket;
      return this;
    }

    public GoogleDocExtractVendorJobBuilder setJob(DocExtractJob job) {
      this.job = job;
      return this;
    }

    public GoogleDocExtractVendorJobBuilder setExternalFilename(String externalFilename) {
      this.externalFilename = externalFilename;
      return this;
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

    private DocExtractJob getJob() {
      return job;
    }

    private String getExternalFilename() {
      return externalFilename;
    }

    public GoogleDocExtractVendorJob build() {
      return new GoogleDocExtractVendorJob(this);
    }

  }
}
