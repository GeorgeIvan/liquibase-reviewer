package com.appiancorp.security.user;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.suiteapi.type.Datatype;
import com.appiancorp.type.AppianTypeLong;
import com.appiancorp.type.refs.GroupRef;
import com.appiancorp.type.refs.Ref;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;

@Entity
@Table(name="grp")
@GwtCompatible
public final class Group implements GroupRef {
  private static final long serialVersionUID = 1L;

  public static final String PROP_UUID = "uuid";
  public static final String JOIN_COL_GROUP_ID = "group_id";

  private Long id;
  private String uuid;

  private Group() {} // for JAXB only
  /**
   * Constructor for a persisted Group, where id is already known.
   */
  public Group(Long id, String uuid) {
    this();
    this.id = id;
    this.uuid = uuid;
  }
  /**
   * Constructor for an unsaved Group, where id is not yet known.
   */
  public Group(String uuid) {
    this(null, uuid);
  }

  public Group(GroupRef groupRef) {
    this(groupRef.getId(), groupRef.getUuid());
  }
  @Override
  public Ref<Long,String> build(Long id, String uuid) {
    return new Group(id, uuid);
  }

  @Override
  @Id
  @GeneratedValue
  @Column(name="id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name="uuid", length=Constants.COL_MAXLEN_UUID, nullable=false, unique=true, updatable=false)
  public String getUuid() {
    return uuid;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Group").add("id", id).add("uuid", uuid).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Group group = (Group)o;
    return Objects.equals(id, group.id) && Objects.equals(uuid, group.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uuid);
  }

  public Datatype datatype() {
    Datatype datatype = new Datatype();
    datatype.setId(AppianTypeLong.GROUP);
    return datatype;
  }

  public Object toTypedValue_Value() {
    return id;
  }
}
