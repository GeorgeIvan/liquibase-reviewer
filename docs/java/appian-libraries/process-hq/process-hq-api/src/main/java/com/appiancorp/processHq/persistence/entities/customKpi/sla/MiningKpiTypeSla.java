package com.appiancorp.processHq.persistence.entities.customKpi.sla;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiType;

@Entity
@Table(name = "mining_kpi_type_sla")
@PrimaryKeyJoinColumn(name = "kpi_type_id")
public class MiningKpiTypeSla extends MiningKpiType {
  private double duration;
  private KpiSlaDurationUnitOption unitOption;
  private boolean isViolation;

  public MiningKpiTypeSla() {}

  @Transient
  public KpiSlaDurationUnitOption getDurationUnitOption() {
    return unitOption;
  }

  public void setDurationUnitOption(KpiSlaDurationUnitOption unitOption) {
    this.unitOption = unitOption;
  }

  @Column(name = "time_unit")
  private Byte getDurationUnitOptionByte() {
    return unitOption != null ? unitOption.getCode() : KpiSlaDurationUnitOption.DAYS.getCode();
  }

  private void setDurationUnitOptionByte(Byte type) {
    setDurationUnitOption(KpiSlaDurationUnitOption.valueOf(type));
  }

  @Column(name = "duration")
  public double getDuration() {
    return duration;
  }

  public void setDuration(double duration) {
    this.duration = duration;
  }

  @Column(name = "is_violation")
  public boolean isViolation() {
    return isViolation;
  }

  public MiningKpiTypeSla setViolation(boolean violation) {
    isViolation = violation;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiTypeSla)) {
      return false;
    }
    MiningKpiTypeSla that = (MiningKpiTypeSla)o;
    return super.equals(o) && Double.compare(that.duration, duration) == 0 &&
        isViolation == that.isViolation && unitOption == that.unitOption;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), duration, isViolation, unitOption);
  }

  @Override
  public String toString() {
    return "MiningKpiTypeSla{" + "duration=" + duration + ", unitOption=" + unitOption + ", isViolation=" +
        isViolation + '}';
  }
}
