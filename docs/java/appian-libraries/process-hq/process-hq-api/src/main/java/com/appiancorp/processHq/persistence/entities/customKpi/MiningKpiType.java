package com.appiancorp.processHq.persistence.entities.customKpi;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mining_kpi_type")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MiningKpiType {
  public static final String PROP_ID = "id";
  public static final String PROP_MINING_KPI = "miningKpi";
  private long id;
  private MiningKpi miningKpi;

  public MiningKpiType() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_kpi_id", referencedColumnName = "id", nullable = false)
  public MiningKpi getMiningKpi() {
    return miningKpi;
  }

  public void setMiningKpi(MiningKpi miningKpi) {
    this.miningKpi = miningKpi;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiType)) {
      return false;
    }
    MiningKpiType that = (MiningKpiType)o;
    return id == that.id && Objects.equals(miningKpi.getId(), that.miningKpi.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, miningKpi);
  }

  @Override
  public String toString() {
    return "MiningKpiType{" + "id=" + id + "mining_kpi_id=" + miningKpi.getId() +'}';
  }
}
