package com.appiancorp.deploymentpackages.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appiancorp.deploymentpackages.persistence.service.PackageDbScriptBuilder;
import com.appiancorp.security.user.User;
import com.google.common.base.MoreObjects;

@Entity
@Table(name="dpkg_db_script")
public class PackageDbScript {
  private Long id;
  private Long orderIndex;
  private Long packageId;
  private Long ddlDocId;
  private User createdByUser;
  private Long createdTs;

  /** for Hibernate to call during queries */
  PackageDbScript() {}

  public PackageDbScript(PackageDbScriptBuilder builder) {
    this.id = builder.getId();
    this.orderIndex = builder.getOrderIndex();
    this.ddlDocId = builder.getDdlDocId();
    this.packageId = builder.getPackageId();
    this.createdByUser = builder.getCreatedByUser();
    this.createdTs = builder.getCreatedTs();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the 1-based order in which this DB script should be executed, relative to its siblings
   */
  @Column(name = "order_index", nullable = false)
  public Long getOrderIndex() {
    return orderIndex;
  }

  public void setOrderIndex(Long orderIndex) {
    this.orderIndex = orderIndex;
  }

  @Column(name = "package_id", nullable = false, insertable = false, updatable = false )
  public Long getPackageId() {
    return packageId;
  }

  void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  @Column(name = "ddl_doc_id", nullable = false)
  public Long getDdlDocId() {
    return ddlDocId;
  }

  public void setDdlDocId(Long ddlDocId) {
    this.ddlDocId = ddlDocId;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
  public User getCreatedByUser() {
    return createdByUser;
  }

  public void setCreatedByUser(User user) {
    this.createdByUser = user;
  }

  @Column(name = "created_ts", updatable = false, nullable = false)
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("order", orderIndex)
        .add("packageId", packageId)
        .add("ddlDocId", ddlDocId)
        .add("createdByUserId", createdByUser.getRdbmsId())
        .add("createdTimeStamp", createdTs)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageDbScript that = (PackageDbScript)o;
    return Objects.equals(ddlDocId, that.ddlDocId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ddlDocId);
  }
}
