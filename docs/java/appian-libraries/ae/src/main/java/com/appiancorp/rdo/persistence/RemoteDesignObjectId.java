package com.appiancorp.rdo.persistence;

import com.appian.core.persist.Constants;
import com.appiancorp.type.external.IgnoreJpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rdo_id")
@IgnoreJpa
public class RemoteDesignObjectId {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue
  private Long id;

  @Column(name = "uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  private String uuid;

  @Column(name = "type_id", nullable = false)
  private Long typeId;

  RemoteDesignObjectId() {
  }

  public RemoteDesignObjectId(String uuid, long typeId) {
    this.uuid = uuid;
    this.typeId = typeId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }
}
