package com.appiancorp.security.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.refs.RecordReferenceRef;
import com.appiancorp.type.refs.RecordReferenceRefImpl;
import com.appiancorp.type.refs.RecordTypeRef;
import com.appiancorp.type.refs.RecordTypeRefImpl;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

@Entity
@Table(name = "record_follower_cfg")
public final class RecordFollowerCfg {

  public static final String LOCAL_PART = "RecordFollowerCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = "id";
  public static final String PROP_FOLLOWER = "follower";
  public static final String PROP_RECORD_TYPE = "recordType";
  public static final String PROP_RECORD_ID = "recordId";

  private Id id = new Id();

  public RecordFollowerCfg() {
  }

  public RecordFollowerCfg(RecordTypeRef recordType, RecordReferenceRef record, User follower) {
    this.id = new Id(recordType.getId(), record.getId(), follower.getRdbmsId());
  }

  @EmbeddedId
  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  @Transient
  public UserRef getFollowerRef() {
    return this.id == null ? null : new UserRefImpl(this.id.followerId, null);
  }

  @Transient
  public RecordTypeRef getRecordTypeRef() {
    return this.id == null ? null : new RecordTypeRefImpl(this.id.recordTypeId);
  }

  @Transient
  public RecordReferenceRef getRecordReferenceRef() {
    return this.id == null ? null : new RecordReferenceRefImpl(this.id.recordId, null);
  }


  private static final Equivalence<RecordFollowerCfg> fullEquality = new Equivalence<RecordFollowerCfg>() {
    @Override
    protected boolean doEquivalent(RecordFollowerCfg a, RecordFollowerCfg b) {

      return Objects.equal(a.id.recordId, b.id.recordId) &&
          Objects.equal(a.id.recordTypeId, b.id.recordTypeId) &&
          Objects.equal(a.id.followerId, b.id.followerId);
    }

    @Override
    protected int doHash(RecordFollowerCfg t) {
      return Objects.hashCode(t.id.recordId, t.id.recordTypeId, t.id.followerId);
    }
  };

  public static Equivalence<RecordFollowerCfg> fullEquality(){
    return fullEquality;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RecordFollowerCfg [");
    builder.append(id);
    builder.append("]");
    return builder.toString();
  }


  @Embeddable
  public static final class Id implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PROP_RECORD_TYPE_ID = "recordTypeId";
    public static final String PROP_RECORD_ID = "recordId";
    public static final String PROP_FOLLOWER_ID = "followerId";

    private Long recordTypeId;
    private String recordId;
    private Long followerId;

    public Id() {
    }

    public Id(Long recordTypeId, String recordId, Long followerId) {
      this.recordTypeId = recordTypeId;
      this.recordId = recordId;
      this.followerId = followerId;
    }

    @Column(name = "usr_id")
    public Long getFollowerId() {
      return followerId;
    }

    public void setFollowerId(Long followerId) {
      this.followerId = followerId;
    }

    @Column(name = "record_type_id")
    public Long getRecordTypeId() {
      return recordTypeId;
    }

    public void setRecordTypeId(Long recordTypeId) {
      this.recordTypeId = recordTypeId;
    }

    @Column(name = "record_id")
    public String getRecordId() {
      return recordId;
    }

    public void setRecordId(String recordId) {
      this.recordId = recordId;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Id [recordTypeId=");
      builder.append(recordTypeId);
      builder.append(", recordId=");
      builder.append(recordId);
      builder.append(", followerId=");
      builder.append(followerId);
      builder.append("]");
      return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Id id = (Id)o;
      return recordTypeId.equals(id.recordTypeId) && recordId.equals(id.recordId) &&
          followerId.equals(id.followerId);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(recordTypeId, recordId, followerId);
    }
  }

}
