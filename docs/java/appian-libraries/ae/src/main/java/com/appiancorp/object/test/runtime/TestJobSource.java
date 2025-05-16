package com.appiancorp.object.test.runtime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;

@SuppressWarnings("serial")
@Entity
@Table(name="test_job_source")
public class TestJobSource implements Id<Long> {

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_TEST_JOB = "testJob";
  public static final String PROP_OBJECT_UUID = "objectUuid";
  public static final String PROP_OBJECT_TYPE = "objectType";

  private Long id;
  private TestJob testJob;
  private String objectUuid;
  private String objectType;

  public TestJobSource() {}

  public TestJobSource(String objectUuid, QName objectType) {
    this.objectUuid = objectUuid;
    this.objectType = objectType.toString();
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

  @ManyToOne
  @JoinColumn(name = "test_job_id", nullable = false)
  public TestJob getTestJob() {
    return testJob;
  }

  public void setTestJob(TestJob testJob) {
    this.testJob = testJob;
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
  public QName getObjectTypeQName() {
    return QName.valueOf(objectType);
  }

  public void setObjectTypeQName(QName objectTypeQName) {
    this.objectType = objectTypeQName.toString();
  }

  @Override
  public String toString() {
    return "TestJobSource{" + "id=" + id + ", testJob='" + testJob + '\'' + ", objectUuid='" +
        objectUuid + '\'' + ", objectType='" + objectType + '\'' + '}';
  }
}
