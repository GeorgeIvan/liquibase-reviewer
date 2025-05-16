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
import com.appiancorp.processHq.persistence.entities.customKpi.sla.KpiSlaDurationUnitOption;

@Entity
@Table(name = "mining_dur_filter")
@CopilotClass(name = "durationFilter", description = "A filter for selecting Cases with the selected duration requirements.")
public class MiningDurationFilter {

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mining_filter_group_id", referencedColumnName = "id", nullable = false)
  private MiningFilterGroup filterGroup;

  @Column(name = "filter_value", nullable = false)
  private Long filterValue;

  @Column(name = "filter_type", nullable = false)
  private Byte filterType;

  @Column(name = "operator", nullable = false)
  private Byte operator;

  @Column(name = "unit", nullable = false)
  private Byte unit;

  @Column(name = "pre_activity_name", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  private String preActivityName;

  @Column(name = "succ_activity_name", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  private String succActivityName;

  @Column(name = "created_ts", nullable = false)
  private Long createdTs;

  public MiningDurationFilter() {}

  @SuppressWarnings({"checkstyle:ParameterNumber"})
  public MiningDurationFilter(
      Long id,
      MiningFilterGroup filterGroup,
      Long filterValue,
      Byte filterType,
      Byte operator,
      Byte unit,
      String preActivityName,
      String succActivityName,
      Long createdTs
  ) {
    this.id = id;
    this.filterGroup = filterGroup;
    this.filterValue = filterValue;
    this.filterType = filterType;
    this.operator = operator;
    this.unit = unit;
    this.preActivityName = preActivityName;
    this.succActivityName = succActivityName;
    this.createdTs = createdTs;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.filterGroup = miningFilterGroup;
  }

  @CopilotField(name = "filterValue", description = "Indicates the number of units for the duration filter")
  public Long getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(Long filterValue) {
    this.filterValue = filterValue;
  }

  @CopilotField(name = "filterType", description = "Indicates the type that the duration filter is to consider such as case or sequence.")
  public DurationFilterType getFilterType() {
    return DurationFilterType.valueOf(filterType);
  }

  public void setFilterType(DurationFilterType filterType) {
    this.filterType = filterType.getCode();
  }

  @CopilotField(name = "operator", description = "Indicates the operator that the duration filter is to consider such as equals or greater than.")
  public DurationFilterOperator getOperator() {
    return DurationFilterOperator.valueOf(operator);
  }

  public void setOperator(DurationFilterOperator operator) {
    this.operator = operator.getCode();
  }

  @CopilotField(name = "unit", description = "Indicates the unit of measurement that the duration filter is to consider such as minutes or hours.")
  public KpiSlaDurationUnitOption getUnit() {
    return KpiSlaDurationUnitOption.valueOf(unit);
  }

  public void setUnit(KpiSlaDurationUnitOption unit) {
    this.unit = unit.getCode();
  }

  @CopilotField(name = "pre_activity", description = "The name of selected predecessor activity.")
  public String getPreActivityName() {
    return preActivityName;
  }

  public void setPreActivityName(String preActivityName) {
    this.preActivityName = preActivityName;
  }

  @CopilotField(name = "succ_activity", description = "The name of selected successor activity.")
  public String getSuccActivityName() {
    return succActivityName;
  }

  public void setSuccActivityName(String succActivityName) {
    this.succActivityName = succActivityName;
  }

  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof MiningDurationFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningDurationFilter other = (MiningDurationFilter) object;
    return equal(this.getFilterValue(), other.getFilterValue()) &&
        equal(this.getFilterType(), other.getFilterType()) &&
        equal(this.getOperator(), other.getOperator()) &&
        equal(this.getUnit(), other.getUnit()) &&
        equal(this.getPreActivityName(), other.getPreActivityName()) &&
        equal(this.getSuccActivityName(), other.getSuccActivityName()) &&
        equal(this.getCreatedTs(), other.getCreatedTs());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getFilterValue(), this.getFilterType(), this.getOperator(), this.getUnit(),
        this.getPreActivityName(), this.getSuccActivityName(), this.getCreatedTs());
  }

  @Override
  public String toString() {
    return "MiningSequenceFilter{" + "filterValue=" + filterValue +
        ", filterType=" + filterType +
        ", operator=" + operator +
        ", unit=" + unit +
        ", preActivityName='" + preActivityName + '\'' +
        ", succActivityName='" + succActivityName + '\'' +
        ", createdTs=" + createdTs +
        '}';
  }
}
