package com.appiancorp.quickAccess.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.security.user.User;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store user object edits in RDBMS.
 */
@Entity
@Table(name = "user_obj_edit")
public class UserObjectEdit {
  public static final String PROP_EDIT_TS = "editTs";
  public static final String PROP_OBJECT_UUID_SHA_256 = "objectUuidSHA256";
  public static final String PROP_USER = "user";
  public static final String PROP_OBJECT_TYPE = "objectTypeId";

  private Long id;
  private User user;
  private String objectUuid;
  private String objectUuidSHA256;
  private Long objectTypeId;
  private Long editTs;

  public UserObjectEdit() {}

  @javax.persistence.Id
  @Column(name = "id", updatable = false)
  @GeneratedValue
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usr_id", nullable = false)
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Column(name = "object_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getObjectUuid() {
    return objectUuid;
  }

  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Column(name = "object_uuid_sha256", updatable = false, nullable = false, length = 64)
  public String getObjectUuidSHA256() {
    return objectUuidSHA256;
  }

  public void setObjectUuidSHA256(String objectUuidSHA256) {
    this.objectUuidSHA256 = objectUuidSHA256;
  }

  @Column(name = "object_type_id", nullable = false)
  public Long getObjectTypeId() {
    return objectTypeId;
  }

  public void setObjectTypeId(Long objectTypeId) {
    this.objectTypeId = objectTypeId;
  }

  @Column(name = "edit_ts", nullable = false)
  public Long getEditTs() {
    return editTs;
  }

  public void setEditTs(Long editTs) {
    this.editTs = editTs;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("userId", user.getRdbmsId())
        .add("objectUuid", objectUuid)
        .add("objectTypeId", objectTypeId)
        .add("editTs", editTs)
        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    UserObjectEdit that = (UserObjectEdit)obj;
    return user.getRdbmsId().equals(that.user.getRdbmsId()) &&
        objectUuid.equals(that.objectUuid) && objectTypeId.equals(that.objectTypeId) &&
        editTs.equals(that.editTs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user.getRdbmsId(), objectUuid, objectTypeId, editTs);
  }
}
