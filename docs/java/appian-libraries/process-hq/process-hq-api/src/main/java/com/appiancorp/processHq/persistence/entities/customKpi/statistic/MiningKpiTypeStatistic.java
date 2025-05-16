package com.appiancorp.processHq.persistence.entities.customKpi.statistic;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.processHq.persistence.entities.customKpi.KpiMeasureOption;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiType;

@Entity
@Table(name = "mining_kpi_type_statistic")
@PrimaryKeyJoinColumn(name = "kpi_type_id")
public class MiningKpiTypeStatistic extends MiningKpiType {
  private KpiMeasureOption kpiMeasureOption;
  private String kpiUnit;

  public MiningKpiTypeStatistic() {}

  @Transient
  public KpiMeasureOption getKpiMeasureOption() {
    return this.kpiMeasureOption;
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

  @Column(name = "unit", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getKpiUnit() {
    return kpiUnit;
  }

  public void setKpiUnit(String kpiUnit) {
    this.kpiUnit = kpiUnit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiTypeStatistic)) {
      return false;
    }
    MiningKpiTypeStatistic that = (MiningKpiTypeStatistic)o;
    return super.equals(o) && kpiMeasureOption == that.kpiMeasureOption &&
        Objects.equals(kpiUnit, that.kpiUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), kpiMeasureOption, kpiUnit);
  }

  @Override
  public String toString() {
    return "MiningKpiTypeStatistic{" + "id=" + getId() + ", mining_kpi_id=" + getMiningKpi().getId() +
        ", kpiMeasureOption=" + kpiMeasureOption + ", kpiUnit=" + kpiUnit + '}';
  }
}
