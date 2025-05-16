package com.appiancorp.processHq.persistence.entities.customKpi.duration;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.customKpi.KpiMeasureOption;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiType;

@Entity
@Table(name = "mining_kpi_type_duration")
@PrimaryKeyJoinColumn(name = "kpi_type_id")
@CopilotClass(name="Duration KPI")
public class MiningKpiTypeDuration extends MiningKpiType {
  private KpiMeasureOption kpiMeasureOption;
  private KpiDurationUnitOption kpiUnitOption;

  public MiningKpiTypeDuration() {}

  @Transient
  @CopilotField(name = "measureOption", description = "Enums used to represent the possible ways to measure your KPI configuration.")
  public KpiMeasureOption getKpiMeasureOption() {
    return kpiMeasureOption;
  }

  public void setKpiMeasureOption(KpiMeasureOption kpiMeasureOption) {
    this.kpiMeasureOption = kpiMeasureOption;
  }

  @Column(name = "measure_option")
  private Byte getKpiMeasureOptionByte() {
    return kpiMeasureOption != null ? kpiMeasureOption.getCode() : KpiMeasureOption.MEDIAN.getCode();
  }

  private void setKpiMeasureOptionByte(Byte type) {
    setKpiMeasureOption(KpiMeasureOption.valueOf(type));
  }

  @Transient

  @CopilotField(name = "unitOption", description = "Enums used to represent the possible ways to measure your KPI configuration.")
  public KpiDurationUnitOption getKpiUnitOption() {
    return kpiUnitOption;
  }

  public void setKpiUnitOption(KpiDurationUnitOption kpiUnitOption) {
    this.kpiUnitOption = kpiUnitOption;
  }

  @Column(name = "unit_option")
  private Byte getKpiUnitOptionByte() {
    return kpiUnitOption != null ? kpiUnitOption.getCode() : KpiDurationUnitOption.AUTOMATIC.getCode();
  }

  private void setKpiUnitOptionByte(Byte type) {
    setKpiUnitOption(KpiDurationUnitOption.valueOf(type));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiTypeDuration)) {
      return false;
    }
    MiningKpiTypeDuration that = (MiningKpiTypeDuration)o;
    return super.equals(o) && kpiMeasureOption == that.kpiMeasureOption && kpiUnitOption == that.kpiUnitOption;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), kpiMeasureOption, kpiUnitOption);
  }

  @Override
  public String toString() {
    return "MiningKpiTypeDuration{" + "id=" + getId() + ", mining_kpi_id=" + getMiningKpi().getId() +
        ", kpiMeasureOption=" + kpiMeasureOption + ", kpiUnitOption=" + kpiUnitOption + '}';
  }
}
