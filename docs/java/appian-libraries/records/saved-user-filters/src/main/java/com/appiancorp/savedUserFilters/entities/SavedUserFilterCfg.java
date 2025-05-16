package com.appiancorp.savedUserFilters.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@Entity
@Table(name = "saved_user_filter")
public class SavedUserFilterCfg implements Id<Long> {
  private Long id;
  private String appliedQueryFilters;
  private String appliedOptionLabels;
  private String userFilterUuid;
  private Integer userFilterTypeId;
  private String name;

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, unique = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "applied_query_filters", nullable = false)
  @Lob
  public String getAppliedQueryFilters() {
    return appliedQueryFilters;
  }

  public void setAppliedQueryFilters(String appliedQueryFilters) {
    this.appliedQueryFilters = appliedQueryFilters;
  }

  @Column(name = "applied_option_labels")
  @Lob
  public String getAppliedOptionLabels() {
    return appliedOptionLabels;
  }

  public void setAppliedOptionLabels(String appliedOptionLabels) {
    this.appliedOptionLabels = appliedOptionLabels;
  }

  @Column(name = "user_filter_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getUserFilterUuid() {
    return userFilterUuid;
  }

  public void setUserFilterUuid(String userFilterUuid) {
    this.userFilterUuid = userFilterUuid;
  }

  @Column(name = "user_filter_type_id", nullable = false)
  public Integer getUserFilterTypeId() {
    return userFilterTypeId;
  }

  public void setUserFilterTypeId(Integer userFilterTypeId) {
    this.userFilterTypeId = userFilterTypeId;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    SavedUserFilterCfg rhs = (SavedUserFilterCfg)obj;
    return Objects.equal(this.id, rhs.id) &&
        Objects.equal(this.name, rhs.name) &&
        Objects.equal(this.userFilterUuid, rhs.userFilterUuid) &&
        Objects.equal(this.userFilterTypeId, rhs.userFilterTypeId) &&
        Objects.equal(this.appliedQueryFilters, rhs.appliedQueryFilters) &&
        Objects.equal(this.appliedOptionLabels, rhs.appliedOptionLabels);
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, name, userFilterUuid, userFilterTypeId, appliedQueryFilters, appliedOptionLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("name", name)
        .add("user_filter_uuid", userFilterUuid)
        .toString();
  }
}
