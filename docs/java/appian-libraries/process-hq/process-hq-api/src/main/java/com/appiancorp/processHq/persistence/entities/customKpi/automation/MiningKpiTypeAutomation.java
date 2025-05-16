package com.appiancorp.processHq.persistence.entities.customKpi.automation;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiType;

@Entity
@Table(name = "mining_kpi_type_automation")
@PrimaryKeyJoinColumn(name = "kpi_type_id")
public class MiningKpiTypeAutomation extends MiningKpiType {

  /**
   * Required for hibernate
   */
  public MiningKpiTypeAutomation() {
  }

  @Override
  public String toString() {
    return "MiningKpiTypeAutomation{" + "id=" + getId() + ", mining_kpi_id=" + getMiningKpi().getId() + '}';
  }

}
