package com.appiancorp.object.test;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.object.test.runtime.PersistedTestResult;
import com.appiancorp.object.test.runtime.QueuedTestCase;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.util.DatatypeUtils;
import com.google.common.base.Equivalence;

@Entity
@Table(name = PersistedTestData.TABLE_NAME)
@SuppressWarnings("serial")
public final class PersistedTestData implements Id<Long>, Uuid<String> {
  static final String TABLE_NAME = "test_data";

  public static final String PROP_OBJ_UUID = "objectUuid";
  public static final String PROP_OBJ_VERSION_ID = "objectVersionId";
  public static final String PROP_OBJ_TYPE = "objectType";
  public static final String PROP_TEST_CASE_INDEX = "testCaseIndex";
  public static final String PROP_ID = "id";

  private String uuid;
  private Long id;

  private Long objectVersionId;
  private String objectUuid;
  private String objectType;

  private int testCaseIndex;

  private String testData;
  private String testDataType;

  private Set<QueuedTestCase> queuedTestCases;
  private Set<PersistedTestResult> persistedTestResults;

  public PersistedTestData() {}

  public PersistedTestData(
      TestCaseId testCaseId,
      String testData,
      QName testDataTypeQName) {
    this.objectVersionId = testCaseId.getObjectVersionId();
    this.objectUuid = testCaseId.getObjectUuid();
    this.setObjectTypeQName(testCaseId.getObjectType());
    this.testCaseIndex = testCaseId.getTestCaseIndex();
    this.testData = testData;
    this.setTestDataTypeQName(testDataTypeQName);
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = java.util.UUID.randomUUID().toString();
    }
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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

  @Column(name = "obj_version_id", nullable=false)
  public Long getObjectVersionId() {
    return objectVersionId;
  }

  public void setObjectVersionId(Long objectVersionId) {
    this.objectVersionId = objectVersionId;
  }

  @Column(name = "order_idx", nullable=false)
  public int getTestCaseIndex() {
    return testCaseIndex;
  }

  public void setTestCaseIndex(int testCaseIndex) {
    this.testCaseIndex = testCaseIndex;
  }

  @Column(name = "obj_uuid", nullable=false, length = Constants.COL_MAXLEN_UUID)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "obj_type", nullable=false, length = Constants.COL_MAXLEN_INDEXABLE)
  private String getObjectType() {
    return objectType;
  }

  @SuppressWarnings("unused")
  private void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @Transient
  public TestDataId getTestDataId() {
    return new TestDataId(objectUuid, objectVersionId, getObjectTypeQName());
  }

  @Column(name = "data", length=Constants.COL_MAXLEN_EXPRESSION, nullable=false)
  @Lob
  @Basic(fetch=FetchType.LAZY)
  public String getSerializedTestData() {
    return testData;
  }

  public void setSerializedTestData(String serializedTestData) {
    this.testData = serializedTestData;
  }

  @Transient
  public QName getObjectTypeQName() {
    return QName.valueOf(objectType);
  }

  public void setObjectTypeQName(QName objectTypeQName) {
    this.objectType = objectTypeQName.toString();
  }

  @Column(name = "data_type", nullable=false, length = Constants.COL_MAXLEN_INDEXABLE)
  private String getSerializedTestDataType() {
    return testDataType;
  }

  @SuppressWarnings("unused")
  private void setSerializedTestDataType(String serializedTestDataType) {
    this.testDataType = serializedTestDataType;
  }

  @Transient
  public QName getTestDataTypeQName() {
    return DatatypeUtils.valueOf(testDataType);
  }

  public void setTestDataTypeQName(QName qName) {
    testDataType = qName.toString();
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = QueuedTestCase.PROP_TEST_DATA_ID)
  public Set<QueuedTestCase> getQueuedTestCases() {
    return queuedTestCases;
  }

  public void setQueuedTestCases(Set<QueuedTestCase> queuedTestCases) {
    this.queuedTestCases = queuedTestCases;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = PersistedTestResult.PROP_TEST_DATA_ID)
  public Set<PersistedTestResult> getPersistedTestResults() {
    return persistedTestResults;
  }

  public void setPersistedTestResults(Set<PersistedTestResult> persistedTestResults) {
    this.persistedTestResults = persistedTestResults;
  }

  /**
   * This {@link Equivalence} ignores identifiers that are specified for storage purposes (ID and UUID) and
   * only tests to ensure that the reference object and test data are the same.
   *
   * This is to avoid saying that the stored version (which would contain both ID and UUID) and the
   * constructed version (which could contain both, either, or neither) are not equal.
   */
  static Equivalence<PersistedTestData> IGNORE_ID_EQUIVALENCE = new Equivalence<PersistedTestData>() {
    @Override
    protected boolean doEquivalent(PersistedTestData a, PersistedTestData b) {
      return Objects.equals(a.objectType, b.objectType) &&
          Objects.equals(a.objectUuid, b.objectUuid) &&
          Objects.equals(a.objectVersionId, b.objectVersionId) &&
          Objects.equals(a.testCaseIndex, b.testCaseIndex) &&
          Objects.equals(a.testDataType, b.testDataType) &&
          Objects.equals(a.testData, b.testData);
    }

    @Override
    protected int doHash(PersistedTestData t) {
      return Objects.hash(t.objectType, t.objectUuid, t.objectVersionId, t.testCaseIndex, t.testDataType, t.testData);
    }
  };

  @Override
  public String toString() {
    return "PersistedTestData [uuid=" + uuid + ", id=" + id + ", objectVersionId=" + objectVersionId +
        ", objectUuid=" + objectUuid + ", objectType=" + objectType + ", testCaseIndex=" + testCaseIndex +
        ", serializedTestDataType=" + testDataType + "]";
  }
}
