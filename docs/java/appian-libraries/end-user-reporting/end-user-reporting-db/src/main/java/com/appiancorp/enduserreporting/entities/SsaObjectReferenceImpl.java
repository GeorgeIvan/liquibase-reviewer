package com.appiancorp.enduserreporting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appiancorp.enduserreporting.persistence.SsaObjectReference;
import com.appiancorp.type.Id;

@Entity
@Table(name = "ssa_object_reference")
public class SsaObjectReferenceImpl implements SsaObjectReference, Id<Long> {
  private Long id;
  private Long parentId;
  private Long childId;

  public SsaObjectReferenceImpl() {}

  public SsaObjectReferenceImpl(Long parentId, Long childId) {
    this.parentId = parentId;
    this.childId = childId;
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

  @Column(name = "parent_id", nullable = false, insertable = false, updatable = false)
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "child_id", nullable = false)
  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }
}
