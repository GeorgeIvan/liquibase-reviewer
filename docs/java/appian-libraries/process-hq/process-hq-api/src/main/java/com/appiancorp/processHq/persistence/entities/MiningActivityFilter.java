package com.appiancorp.processHq.persistence.entities;

import static com.google.common.base.Objects.equal;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;

@Entity
@Table(name = "mining_activity_filter")
@CopilotClass(name = "activityFilter", description = "A filter for selecting Cases with the selected activity.")
public class MiningActivityFilter {
  public static final String PROP_FILTER_GROUP = "filterGroup";
  public static final String PROP_ID = "id";
  private long id;
  private String activityName;
  private MiningFilterGroup miningFilterGroup;
  private boolean inverted;
  private boolean isEndpointFilter;
  private boolean isStartFilter;
  private Long createdTs;

  public MiningActivityFilter() {}

  public MiningActivityFilter(
      Long id,
      MiningFilterGroup miningFilterGroup,
      boolean inverted,
      boolean isEndpointFilter,
      String activityName,
      Long createdTs,
      boolean isStartFilter
  ) {
    this.id = id;
    this.miningFilterGroup = miningFilterGroup;
    this.inverted = inverted;
    this.isEndpointFilter = isEndpointFilter;
    this.activityName = activityName;
    this.createdTs = createdTs;
    this.isStartFilter = isStartFilter;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Column(name = "activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "activity", description = "The name of selected activity.")
  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mining_filter_group_id", referencedColumnName = "id", nullable = false)
  public MiningFilterGroup getFilterGroup() {
    return miningFilterGroup;
  }

  public void setFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.miningFilterGroup = miningFilterGroup;
  }

  @Column(name = "inverted", nullable = false)
  @CopilotField(name = "inverted", description = "Indicates if the filter should equal or not equal the activity")
  public boolean getInverted() {
    return inverted;
  }

  public void setInverted(boolean inverted) {
    this.inverted = inverted;
  }

  @Column(name = "isEndpointFilter", nullable = false)
  @CopilotField(name = "isEndpointFilter", description = "The flag indicates if the filter set to the terminal activity.")
  public boolean getIsEndpointFilter() {
    return isEndpointFilter;
  }

  public void setIsEndpointFilter(boolean isEndpointFilter) {
    this.isEndpointFilter = isEndpointFilter;
  }

  @Column(name = "is_start_filter", nullable = false)
  @CopilotField(name = "isStartFilter", description = "The flag indicates if the filter set to the first activity.")
  public boolean getIsStartFilter() {
    return isStartFilter;
  }

  public void setIsStartFilter(boolean isStartFilter) {
    this.isStartFilter = isStartFilter;
  }

  @Column(name = "created_ts", nullable = false)
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  public boolean contentsEquals(Object object) {
    if (!(object instanceof MiningActivityFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningActivityFilter other = ((MiningActivityFilter) object);
    return equal(this.getActivityName(), other.getActivityName()) &&
        equal(this.getInverted(), other.getInverted()) &&
        equal(this.getIsEndpointFilter(), other.getIsEndpointFilter()) &&
        equal(this.getCreatedTs(), other.getCreatedTs()) &&
        equal(this.getIsStartFilter(), other.getIsStartFilter());
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof MiningActivityFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningActivityFilter other = ((MiningActivityFilter) object);
    return equal(this.getId(), other.getId()) &&
        equal(this.getActivityName(), other.getActivityName()) &&
        equal(this.getInverted(), other.getInverted()) &&
        equal(this.getIsEndpointFilter(), other.getIsEndpointFilter()) &&
        equal(this.getCreatedTs(), other.getCreatedTs()) &&
        equal(this.getIsStartFilter(), other.getIsStartFilter());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId(), this.getActivityName(), this.getIsEndpointFilter(), this.getInverted(),
        this.getIsStartFilter(), this.getCreatedTs());
  }

  @Override
  public String toString() {
    return "MiningActivityFilter{" + "id=" + id + ", activityName='" + activityName + '\'' + ", inverted=" +
        inverted + ", isEndpointFilter=" + isEndpointFilter + ", isStartFilter='" + isStartFilter + '\'' +
        ", createdTs='" + createdTs + '}';
  }
}
