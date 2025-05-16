package com.appiancorp.processHq.persistence.entities.customKpi.alert;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi;

@Entity
@Table(name = "mining_kpi_threshold")
public class MiningKpiThreshold {
  private Long id;
  private MiningKpi miningKpi;
  private Double thresholdValue;
  private String name;
  private boolean displayOnGraph;
  private Set<MiningKpiAlert> alerts = new HashSet<>();

  public MiningKpiThreshold() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  @Column(name = "threshold_value", nullable = false)
  public Double getThresholdValue() {
    return thresholdValue;
  }

  public void setThresholdValue(Double thresholdValue) {
    this.thresholdValue = thresholdValue;
  }

  @Column(name = "name", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "display_on_graph", nullable = false)
  public boolean getDisplayOnGraph() {
    return displayOnGraph;
  }

  public void setDisplayOnGraph(boolean displayOnGraph) {
    this.displayOnGraph = displayOnGraph;
  }

  @OneToMany(mappedBy = "miningKpiThreshold", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  public Set<MiningKpiAlert> getAlerts() {
    return alerts;
  }

  public void setAlerts(Set<MiningKpiAlert> alerts) {
    this.alerts = alerts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiThreshold)) {
      return false;
    }
    MiningKpiThreshold that = (MiningKpiThreshold)o;
    return id == that.id && Objects.equals(Objects.isNull(miningKpi) ? null : miningKpi.getId(),
        Objects.isNull(that.miningKpi) ? null : that.miningKpi.getId()) &&
        Objects.equals(thresholdValue, that.thresholdValue) && Objects.equals(name, that.name) &&
        displayOnGraph == that.displayOnGraph;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, thresholdValue, name, displayOnGraph);
  }

  @Override
  public String toString() {
    return "MiningKpiThreshold{" + "id=" + id + ", miningKpi=" +
        (Objects.isNull(miningKpi) ? null : miningKpi.getId()) + ", thresholdValue=" + thresholdValue +
        ", name=" + name + ", displayOnGraph=" + displayOnGraph + "}";
  }
}
