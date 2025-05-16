package com.appiancorp.documentunderstanding.persistence;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.documentunderstanding.OcrOperationStatus;

/**
 * Stores the state of a doc extraction job
 */
@Entity
@Table(name="doc_extract_job")
public class DocExtractJob {

  public static final String ID_FIELD = "id";
  public static final String VENDOR_FIELD = "vendor";
  public static final String JOB_STATUS_FIELD = "jobStatus";
  public static final String APPIAN_DOC_ID_FIELD = "appianDocId";
  public static final String VERSION_FIELD = "version";
  public static final String INTERPRETED_RESULTS_DOC_ID_FIELD = "interpretedResultsDocId";
  public static final String CREATE_TIME_FIELD = "createTime";
  public static final String CREATE_TIME_LONG_FIELD = "createTimeLong";
  public static final String DOC_EXTRACT_RAW_RESULTS_FIELD = "docExtractRawResults";
  public static final String FETCH_ATTEMPTS_FIELD = "fetchAttempts";

  private Long id;
  private Vendor vendor;
  private OcrOperationStatus jobStatus;
  private Long appianDocId;
  private GoogleDocExtractVendorJob googleDocExtractVendorJob;
  private GoogleInputDocument googleInputDocument;
  private Integer version;
  private Integer fetchAttempts;
  private Long interpretedResultsDocId;
  private Timestamp createTime;
  private Set<DocExtractRawResults> docExtractRawResults;

  private DocExtractJob(DocExtractJobBuilder builder) {
    this.id = builder.getId();
    this.vendor = builder.getVendor();
    this.jobStatus = builder.getJobStatus();
    this.appianDocId = builder.getAppianDocId();
    this.googleDocExtractVendorJob = builder.getGoogleDocExtractVendorJob();
    this.googleInputDocument = builder.getGoogleInputDocument();
    this.version = builder.getVersion();
    this.fetchAttempts = builder.getFetchAttempts();
    this.interpretedResultsDocId = builder.getInterpretedResultsDocId();
    this.createTime = builder.getCreateTime();
    this.docExtractRawResults = builder.getDocExtractRawResults();
  }

  // Required for Hibernate reads
  DocExtractJob(){}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "vendor", nullable = false)
  public Vendor getVendor() {
    return vendor;
  }

  public void setVendor(Vendor vendor) {
    this.vendor = vendor;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "job_status", nullable = false)
  public OcrOperationStatus getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(OcrOperationStatus jobStatus) {
    this.jobStatus = jobStatus;
  }

  @Column(name = "appian_doc_id", nullable = false)
  public Long getAppianDocId() {
    return appianDocId;
  }

  public void setAppianDocId(Long appianDocId) {
    this.appianDocId = appianDocId;
  }

  @OneToOne(mappedBy = "job", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public GoogleDocExtractVendorJob getGoogleDocExtractVendorJob() {
    return googleDocExtractVendorJob;
  }

  public void setGoogleDocExtractVendorJob(GoogleDocExtractVendorJob googleDocExtractVendorJob ) {
    this.googleDocExtractVendorJob = googleDocExtractVendorJob;
  }

  @OneToOne(mappedBy = "job", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  public GoogleInputDocument getGoogleInputDocument() {
    return googleInputDocument;
  }

  public void setGoogleInputDocument(GoogleInputDocument googleInputDocument ) {
    this.googleInputDocument = googleInputDocument;
  }

  @Column(name = "version", nullable = false)
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Column(name = "fetch_attempts", nullable = false)
  public Integer getFetchAttempts() {
    return fetchAttempts;
  }

  public void setFetchAttempts(Integer fetchAttempts) {
    this.fetchAttempts = fetchAttempts;
  }

  @Column(name = "interpreted_results_doc_id")
  public Long getInterpretedResultsDocId() {
    return interpretedResultsDocId;
  }

  public void setInterpretedResultsDocId(Long interpretedResultsDocId) {
    this.interpretedResultsDocId = interpretedResultsDocId;
  }

  @Column(name="create_time", nullable = false)
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

  @OneToMany(mappedBy = "docExtractJob", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy
  public Set<DocExtractRawResults> getDocExtractRawResults() {
    return docExtractRawResults;
  }

  public void setDocExtractRawResults(Set<DocExtractRawResults> docExtractRawResults) {
    this.docExtractRawResults = docExtractRawResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocExtractJob that = (DocExtractJob)o;
    // Uses a minimal set of fields to uniquely identify a DocExtractJob (for hibernate performance)
    return getAppianDocId().equals(that.getAppianDocId()) && getCreateTime().equals(that.getCreateTime());
  }

  @Override
  public int hashCode() {
    // Uses a minimal set of fields to uniquely identify a DocExtractJob (for hibernate performance)
    return Objects.hash(getAppianDocId(), getCreateTime());
  }

  public static DocExtractJobBuilder builder() {
    return new DocExtractJobBuilder();
  }

  public final static class DocExtractJobBuilder {
    private Long id;
    private Vendor vendor;
    private OcrOperationStatus jobStatus;
    private Long appianDocId;
    private GoogleDocExtractVendorJob googleDocExtractVendorJob;
    private GoogleInputDocument googleInputDocument;
    private Integer version;
    private Integer fetchAttempts;
    private Long interpretedResultsDocId;
    private Timestamp createTime;
    private Set<DocExtractRawResults> docExtractRawResults;

    private DocExtractJobBuilder() {}

    public DocExtractJobBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public DocExtractJobBuilder setVendor(Vendor vendor) {
      this.vendor = vendor;
      return this;
    }

    public DocExtractJobBuilder setJobStatus(OcrOperationStatus jobStatus) {
      this.jobStatus = jobStatus;
      return this;
    }

    public DocExtractJobBuilder setAppianDocId(Long appianDocId) {
      this.appianDocId = appianDocId;
      return this;
    }

    public DocExtractJobBuilder setGoogleDocExtractVendorJob(GoogleDocExtractVendorJob googleDocExtractVendorJob) {
      this.googleDocExtractVendorJob = googleDocExtractVendorJob;
      return this;
    }

    public DocExtractJobBuilder setGoogleInputDocument(GoogleInputDocument googleInputDocument) {
      this.googleInputDocument = googleInputDocument;
      return this;
    }

    public DocExtractJobBuilder setVersion(Integer version) {
      this.version = version;
      return this;
    }

    public DocExtractJobBuilder setFetchAttempts(Integer fetchAttempts) {
      this.fetchAttempts = fetchAttempts;
      return this;
    }

    public DocExtractJobBuilder setInterpretedResultsDocId(Long interpretedResultsDocId) {
      this.interpretedResultsDocId = interpretedResultsDocId;
      return this;
    }

    public DocExtractJobBuilder setCreateTime(Timestamp createTime) {
      this.createTime = createTime == null ? null : new Timestamp(createTime.getTime());
      return this;
    }

    public DocExtractJobBuilder setDocExtractRawResults(Set<DocExtractRawResults> docExtractRawResults) {
      this.docExtractRawResults = docExtractRawResults;
      return this;
    }

    private Long getId() {
      return id;
    }

    private Vendor getVendor() {
      return vendor;
    }

    private OcrOperationStatus getJobStatus() {
      return jobStatus;
    }

    private Long getAppianDocId() {
      return appianDocId;
    }

    private GoogleDocExtractVendorJob getGoogleDocExtractVendorJob() {
      return googleDocExtractVendorJob;
    }

    private GoogleInputDocument getGoogleInputDocument() {
      return googleInputDocument;
    }

    private Integer getVersion() {
      return version;
    }

    private Integer getFetchAttempts() {
      return fetchAttempts;
    }

    private Long getInterpretedResultsDocId() {
      return interpretedResultsDocId;
    }

    private Timestamp getCreateTime() {
      return createTime == null ? null : new Timestamp(createTime.getTime());
    }

    private Set<DocExtractRawResults> getDocExtractRawResults() {
      return docExtractRawResults;
    }

    public DocExtractJob build() {
      return new DocExtractJob(this);
    }

  }
}
