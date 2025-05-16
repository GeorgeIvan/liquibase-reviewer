package com.appiancorp.processHq.persistence.entities.customKpi.count;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiType;

@Entity
@Table(name = "mining_kpi_type_count")
@PrimaryKeyJoinColumn(name = "kpi_type_id")
@CopilotClass(name="Count KPI")
public class MiningKpiTypeCount extends MiningKpiType {
  private KpiCountOption kpiCountOption;

  public MiningKpiTypeCount() {}

  @Transient
  @CopilotField(name = "countOption", description = "Enums used to represent the possible ways to count your KPI configuration.")
  public KpiCountOption getKpiCountOption() {
    return kpiCountOption;
  }

  public void setKpiCountOption(KpiCountOption kpiCountOption) {
    this.kpiCountOption = kpiCountOption;
  }

  @Column(name = "count_option")
  private Byte getKpiCountOptionByte() {
    return kpiCountOption != null ? kpiCountOption.getCode() : KpiCountOption.OCCURRENCE.getCode();
  }

  private void setKpiCountOptionByte(Byte type) {
    setKpiCountOption(KpiCountOption.valueOf(type));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiTypeCount)) {
      return false;
    }
    MiningKpiTypeCount that = (MiningKpiTypeCount)o;
    return super.equals(o) && kpiCountOption == that.kpiCountOption;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), kpiCountOption);
  }

  @Override
  public String toString() {
    return "MiningKpiTypeCount{" + "id=" + getId() + "mining_kpi_id=" + getMiningKpi().getId() +
        "kpiCountOption=" + kpiCountOption + '}';
  }
}
