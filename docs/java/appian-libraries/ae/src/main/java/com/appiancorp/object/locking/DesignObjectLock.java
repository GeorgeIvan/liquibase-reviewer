package com.appiancorp.object.locking;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.type.cdt.DesignObjectLockDto;
import com.google.common.base.Equivalence;

@Entity
@Table(name = "design_object_lock")
public class DesignObjectLock implements Serializable {
  private Long id;
  private String objectUuid;
  private Long usrId;
  private String sessionUuid;
  private String uiSourceUuid;
  private Timestamp acquiredTimestamp;

  public DesignObjectLock() {
  }

  public DesignObjectLock(
      String objectUuid,
      Long usrId,
      String sessionUuid,
      String uiSourceKey,
      Timestamp acquiredTimestamp) {
    this.objectUuid = checkNotNull(objectUuid);
    this.usrId = checkNotNull(usrId);
    this.sessionUuid = checkNotNull(sessionUuid);
    this.uiSourceUuid = checkNotNull(uiSourceKey);
    this.acquiredTimestamp = checkNotNull(acquiredTimestamp);
  }

  @javax.persistence.Id
  @Column(name = "id", updatable = false)
  @GeneratedValue
  public Long getId() {
    return this.id;
  }

  @SuppressWarnings("unused")
  private void setId(Long id) {
    this.id = id;
  }

  @Column(name = "object_uuid")
  public String getObjectUuid() {
    return this.objectUuid;
  }

  @SuppressWarnings("unused")
  private void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "usr_id")
  public Long getUsrId() {
    return this.usrId;
  }

  @SuppressWarnings("unused")
  private void setUsrId(Long usrId) {
    this.usrId = usrId;
  }

  @Column(name = "session_uuid")
  public String getSessionUuid() {
    return this.sessionUuid;
  }

  @SuppressWarnings("unused")
  private void setSessionUuid(String sessionUuid) {
    this.sessionUuid = sessionUuid;
  }

  @Column(name = "ui_source_uuid")
  public String getUiSourceUuid() {
    return this.uiSourceUuid;
  }

  @SuppressWarnings("unused")
  private void setUiSourceUuid(String uiSourceUuid) {
    this.uiSourceUuid = uiSourceUuid;
  }

  @Column(name = "acquired_ts")
  public Long getAcquiredTimestampLong() {
    return this.acquiredTimestamp.getTime();
  }

  @SuppressWarnings("unused")
  private void setAcquiredTimestampLong(Long acquiredTimestamp) {
    this.acquiredTimestamp = new Timestamp(checkNotNull(acquiredTimestamp));
  }

  @Transient
  public Timestamp getAcquiredTimestamp() {
    return acquiredTimestamp;
  }

  public void setAcquiredTimestamp(Timestamp acquiredTimestamp) {
    this.acquiredTimestamp = acquiredTimestamp;
  }

  @Override
  public String toString() {
    return String.format("[uuid=%s, sessionUuid=%s, userId=%s, uiSourceUuid=%s]", objectUuid, sessionUuid,
        usrId, uiSourceUuid);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DesignObjectLock that = (DesignObjectLock)o;
    return areFieldsEqual(that);
  }

  private boolean areFieldsEqual(DesignObjectLock that) {
    if (!objectUuid.equals(that.objectUuid)) {
      return false;
    }
    if (!usrId.equals(that.usrId)) {
      return false;
    }
    if (!sessionUuid.equals(that.sessionUuid)) {
      return false;
    }
    if (!uiSourceUuid.equals(that.uiSourceUuid)) {
      return false;
    }
    return acquiredTimestamp.equals(that.acquiredTimestamp);
  }

  @Override
  public int hashCode() {
    int result = objectUuid.hashCode();
    result = 31 * result + usrId.hashCode();
    result = 31 * result + sessionUuid.hashCode();
    result = 31 * result + uiSourceUuid.hashCode();
    result = 31 * result + acquiredTimestamp.hashCode();
    return result;
  }

  /**
   * @return An equivalence that checks all properties of {@link DesignObjectLockDto}.
   */
  public static Equivalence<DesignObjectLockDto> designObjectLockEqualCheck() {
    return new DesignObjectLockInstance();
  }

  private static class DesignObjectLockInstance extends Equivalence<DesignObjectLockDto> {
    @Override
    protected boolean doEquivalent(DesignObjectLockDto lhs, DesignObjectLockDto rhs) {
      return Objects.equals(lhs.getObjectUuid(), rhs.getObjectUuid()) &&
          Objects.equals(lhs.getUsername(), rhs.getUsername()) &&
          Objects.equals(lhs.getSessionUuid(), rhs.getSessionUuid()) &&
          Objects.equals(lhs.getUiSourceUuid(), rhs.getUiSourceUuid()) &&
          Objects.equals(lhs.isIsLocked(), rhs.isIsLocked());
    }

    @Override
    protected int doHash(DesignObjectLockDto designObjectLockDto) {
      return Objects.hash(designObjectLockDto.getObjectUuid(), designObjectLockDto.getUsername(), designObjectLockDto.getSessionUuid(),
          designObjectLockDto.getUiSourceUuid(), designObjectLockDto.isIsLocked());
    }
  }
}
