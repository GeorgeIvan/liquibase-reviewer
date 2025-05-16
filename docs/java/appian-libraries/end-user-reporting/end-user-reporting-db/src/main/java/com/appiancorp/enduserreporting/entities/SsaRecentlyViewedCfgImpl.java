package com.appiancorp.enduserreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.sql.Timestamp;

import com.appiancorp.enduserreporting.persistence.SsaRecentlyViewedCfg;
import com.appiancorp.security.user.User;

@Entity
@Table(name = "ssa_recently_viewed")
public class SsaRecentlyViewedCfgImpl implements SsaRecentlyViewedCfg {
  private Long id;
  private String objectUuid;
  private Byte objectType;
  private User user;
  private Timestamp timestamp;

  public SsaRecentlyViewedCfgImpl() {}

  public SsaRecentlyViewedCfgImpl(
      Long id,
      String objectUuid,
      Byte objectType,
      User user,
      Timestamp timestamp
  ) {
    this.id = id;
    this.objectUuid = objectUuid;
    this.objectType = objectType;
    this.user = user;
    this.timestamp = timestamp;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "object_uuid", nullable = false, unique = true)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "object_type", nullable = false)
  public Byte getObjectType() {
    return objectType;
  }

  public void setObjectType(Byte objectType) {
    this.objectType = objectType;
  }

  @ManyToOne(fetch= FetchType.LAZY) @JoinColumn(name="user_id", updatable=false)
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Column(name = "viewed_timestamp", nullable = false)
  private Long getTimestampLong() { return timestamp == null ? null : timestamp.getTime(); }

  private void setTimestampLong(Long timestampLong) {
    this.timestamp = timestampLong == null ? null : new Timestamp(timestampLong);
  }

  @Transient
  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

}


