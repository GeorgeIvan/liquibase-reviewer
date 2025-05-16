package com.appiancorp.savedUserFilters.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@Entity
@Table(name = "saved_user_filter_set")
public class SavedUserFilterSetCfg implements Id<Long> {

  private Long id;
  private String name;
  private String recordViewUuid;
  private String recordTypeUuid;
  private String userUuid;
  private Boolean isFavorite;
  private String searchText;
  private List<SavedUserFilterCfg> savedUserFilters;

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

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "record_view_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getRecordViewUuid() {
    return recordViewUuid;
  }

  public void setRecordViewUuid(String recordViewUuid) {
    this.recordViewUuid = recordViewUuid;
  }

  @Column(name = "record_type_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Column(name = "user_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  @Column(name = "is_favorite")
  public Boolean getIsFavorite() {
    return isFavorite;
  }

  public void setIsFavorite(Boolean isFavorite) {
    this.isFavorite = isFavorite;
  }

  @Column(name = "search_text", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getSearchText() {
    return searchText;
  }

  public void setSearchText(String searchText) {
    this.searchText = searchText;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
  @JoinColumn(name = "parent_set_id", nullable = false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<SavedUserFilterCfg> getSavedUserFilters() {
    return savedUserFilters;
  }

  public void setSavedUserFilters(List<SavedUserFilterCfg> savedUserFilters) {
    this.savedUserFilters = savedUserFilters;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    SavedUserFilterSetCfg rhs = (SavedUserFilterSetCfg)obj;
    return Objects.equal(this.id, rhs.id) &&
        Objects.equal(this.name, rhs.name) &&
        Objects.equal(this.recordTypeUuid, rhs.recordTypeUuid) &&
        Objects.equal(this.recordViewUuid, rhs.recordViewUuid) &&
        Objects.equal(this.userUuid, rhs.userUuid) &&
        Objects.equal(this.isFavorite, rhs.isFavorite) &&
        Objects.equal(this.searchText, rhs.searchText) &&
        Objects.equal(this.savedUserFilters, rhs.savedUserFilters);
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, name, recordTypeUuid, recordViewUuid, userUuid, isFavorite, searchText, savedUserFilters);
  }


  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("name", name)
        .add("record_type_uuid", recordTypeUuid)
        .add("record_view_uuid", recordViewUuid)
        .add("user_uuid", userUuid)
        .add("is_favorite", isFavorite)
        .add("search_text", searchText)
        .add("user_filters", savedUserFilters)
        .toString();
  }
}
