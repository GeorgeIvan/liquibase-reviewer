package com.appiancorp.object.test.runtime;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appiancorp.object.test.TestData;
import com.appiancorp.security.user.User;
import com.appiancorp.type.Id;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.collect.Sets;

@SuppressWarnings("serial")
@Entity
@Table(name=TestJob.TABLE_NAME)
public class TestJob implements Id<Long> {

  public static final String TABLE_NAME = "test_job";

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_CREATED_TS_LONG = "createdTsLong";

  private Long id;
  private Timestamp createdTs;
  private User createdBy;
  private Set<PersistedTestResult> persistedTestResults;
  private Set<QueuedTestCase> queuedTestCases;
  private Set<TestJobSource> testJobSources = Sets.newHashSet();

  public TestJob(){}

  public TestJob(List<TestData> testDataList) {
    this.queuedTestCases = testDataList.stream()
        .flatMap(x -> x.getTestCaseIds().stream())
        .map(QueuedTestCase::new)
        .collect(Collectors.toSet());
  }

  public TestJob(List<TestData> testDataList, List<String> uuids, QName objectType) {
    this(testDataList);
    this.testJobSources = uuids.stream()
        .map(uuid -> new TestJobSource(uuid, objectType))
        .collect(Collectors.toSet());
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="created_ts", updatable=false)
  private Long getCreatedTsLong() {
    return createdTs == null ? null : createdTs.getTime();
  }
  @SuppressWarnings("unused")
  private void setCreatedTsLong(Long startedTsLong) {
    this.createdTs = startedTsLong == null ? null : new Timestamp(startedTsLong);
  }

  @Transient
  public Timestamp getCreatedTs() {
    return createdTs == null ? null : new Timestamp(createdTs.getTime());
  }
  public void setCreatedTs(Timestamp createdTs) {
    this.createdTs = createdTs == null ? null : new Timestamp(createdTs.getTime());
  }

  @ManyToOne(fetch= FetchType.EAGER) @JoinColumn(name="created_by", updatable=false)
  private User getCreatedByUser() {
    return createdBy;
  }
  @SuppressWarnings("unused")
  private void setCreatedByUser(User startedBy) {
    this.createdBy = startedBy;
  }

  @OrderBy
  @OneToMany(mappedBy = "testJob", cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<PersistedTestResult> getPersistedTestResults() {
    return persistedTestResults;
  }
  public void setPersistedTestResults(Set<PersistedTestResult> persistedTestResults) {
    this.persistedTestResults = persistedTestResults;
  }

  @OrderBy
  @OneToMany(mappedBy = "testJob", cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<QueuedTestCase> getQueuedTestCases() {
    return queuedTestCases;
  }
  public void setQueuedTestCases(Set<QueuedTestCase> queuedTestCases) {
    this.queuedTestCases = queuedTestCases;
  }

  @Transient
  public UserRef getCreatedBy() {
    return createdBy == null ? null : new UserRefImpl(this.createdBy.getUsername(), null);
  }
  public void setCreatedBy(UserRef startedBy) {
    this.createdBy = new User(startedBy);
  }

  @OneToMany(mappedBy = "testJob", cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<TestJobSource> getTestJobSources() {
    return testJobSources;
  }

  public void setTestJobSources(Set<TestJobSource> testJobSources) {
    this.testJobSources = testJobSources;
  }
}
