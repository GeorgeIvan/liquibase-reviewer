package com.appiancorp.object.test.runtime;

import com.appian.core.persist.Constants;
import com.appiancorp.object.test.TestDataId;
import com.appiancorp.type.Id;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;
import java.util.Objects;

@Entity
@Table(name = LastTestResult.TABLE_NAME)
public class LastTestResult implements Id<Long> {
  static final String TABLE_NAME = "last_test_result";

  public static final String PROP_OBJ_UUID = "objectUuid";
  public static final String PROP_OUTDATED_STATUS = "outdatedStatus";
  public static final String PROP_ID = "id";

  private Long id;

  private String objectUuid;
  private Long objectVersionId;
  private String objectType;

  private PersistedTestResult testResult;

  private OUTDATED_STATUS outdatedStatus;

  public enum OUTDATED_STATUS {
    UP_TO_DATE,
    VERSION_CHANGED,
    PRECEDENT_CHANGED
  };

  protected LastTestResult() {}

  public LastTestResult(
          TestDataId testDataId,
          PersistedTestResult testResult,
          OUTDATED_STATUS outdatedStatus) {
    this(testDataId.getObjectUuid(), testDataId.getObjectVersionId(), testDataId.getObjectType(), testResult, outdatedStatus);
  }

  public LastTestResult(
          String objectUuid,
          Long objectVersionId,
          QName objectType,
          PersistedTestResult testResult,
          OUTDATED_STATUS outdatedStatus) {
    this.objectVersionId = objectVersionId;
    this.objectUuid = objectUuid;
    this.setObjectTypeQName(objectType);
    this.testResult = testResult;
    this.outdatedStatus = outdatedStatus;
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

  @Column(name = "obj_uuid", nullable=false, length = Constants.COL_MAXLEN_UUID)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "obj_version_id", nullable=false)
  public Long getObjectVersionId() {
    return objectVersionId;
  }

  public void setObjectVersionId(Long objectVersionId) {
    this.objectVersionId = objectVersionId;
  }

  @Column(name = "obj_type", nullable=false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @Transient
  public QName getObjectTypeQName() {
    return QName.valueOf(objectType);
  }

  public void setObjectTypeQName(QName objectTypeQName) {
    this.objectType = objectTypeQName.toString();
  }

  @OneToOne(fetch = FetchType.LAZY)
  /* See this article for explanation: https://stackoverflow.com/questions/8563592/jpas-cascade-remove-and-hibernates-ondelete-used-together */
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "test_result_id", nullable=false)
  public PersistedTestResult getTestResult() {
    return testResult;
  }

  public void setTestResult(PersistedTestResult testResult) {
    this.testResult = testResult;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "outdated_status", nullable=false)
  public OUTDATED_STATUS getOutdatedStatus() {
    return outdatedStatus;
  }

  public void setOutdatedStatus(OUTDATED_STATUS outdatedStatus) {
    this.outdatedStatus = outdatedStatus;
  }

  static Equivalence<LastTestResult> IGNORE_ID_EQUIVALENCE = new Equivalence<LastTestResult>() {
    @Override
    protected boolean doEquivalent(LastTestResult a, LastTestResult b) {
      return Objects.equals(a.objectType, b.objectType) &&
              Objects.equals(a.objectUuid, b.objectUuid) &&
              Objects.equals(a.objectVersionId, b.objectVersionId) &&
              /* call the method to get Id directly because lazy initialization wraps the object during tests which
                 makes it look like the object is null when it really isn't. This forces id retrieval. */
              Objects.equals(a.getTestResult().getId(), b.getTestResult().getId()) &&
              Objects.equals(a.outdatedStatus, b.outdatedStatus);
    }

    @Override
    protected int doHash(LastTestResult t) {
      return Objects.hash(t.objectType, t.objectUuid, t.objectVersionId, t.getTestResult().getId(), t.outdatedStatus);
    }
  };

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("objectVersionId", objectVersionId)
        .add("objectUuid", objectUuid)
        .add("objectType", objectType)
        .add("outdatedStatus", outdatedStatus)
        .toString();
  }

  public boolean equivalentTo(final LastTestResult lastTestResult) {
    return IGNORE_ID_EQUIVALENCE.equivalent(this, lastTestResult);
  }
}
