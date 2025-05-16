package com.appiancorp.processHq.persistence.entities.customKpi.field;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi;

@Entity
@Table(name = "mining_kpi_field")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MiningKpiField {

  private long id;
  private MiningKpi miningKpi;

  public MiningKpiField() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_kpi_id", referencedColumnName = "id", nullable = false, updatable = false)
  public MiningKpi getMiningKpi() {
    return miningKpi;
  }

  public void setMiningKpi(MiningKpi miningKpi) {
    this.miningKpi = miningKpi;
  }

  @Transient
  public abstract MiningKpiKey getMiningKpiKey();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiField)) {
      return false;
    }
    MiningKpiField that = (MiningKpiField)o;
    return id == that.id;
  }

  @Transient
  public abstract MiningKpiFieldType getFieldType();

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "MiningKpiField{" + "id=" + id + ", miningKpi=" + (Objects.isNull(miningKpi) ? null : miningKpi.getId()) + '}';
  }
}
