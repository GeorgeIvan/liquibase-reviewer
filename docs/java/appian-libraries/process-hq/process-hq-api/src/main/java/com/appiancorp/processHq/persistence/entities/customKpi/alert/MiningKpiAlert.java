package com.appiancorp.processHq.persistence.entities.customKpi.alert;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mining_kpi_alert")
public class MiningKpiAlert {
  private Long id;
  private MiningKpiThreshold miningKpiThreshold;
  private boolean enabled;
  private Long lastAlertEndTimeMs;

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
  @JoinColumn(name = "kpi_threshold_id", referencedColumnName = "id", nullable = false, updatable = false)
  public MiningKpiThreshold getMiningKpiThreshold() {
    return miningKpiThreshold;
  }

  public void setMiningKpiThreshold(MiningKpiThreshold miningKpiThreshold) {
    this.miningKpiThreshold = miningKpiThreshold;
  }

  @Column(name = "enabled", nullable = false)
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Column(name = "last_alert_end_time_ms", nullable = true)
  public Long getLastAlertEndTimeMs() {
    return lastAlertEndTimeMs;
  }

  public void setLastAlertEndTimeMs(Long lastAlertEndTimeMs) {
    this.lastAlertEndTimeMs = lastAlertEndTimeMs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiAlert)) {
      return false;
    }
    MiningKpiAlert that = (MiningKpiAlert)o;
    return Objects.equals(id, that.id) &&
        Objects.equals(Objects.isNull(miningKpiThreshold) ? null : miningKpiThreshold.getId(),
        Objects.isNull(that.miningKpiThreshold) ? null : that.miningKpiThreshold.getId()) &&
        enabled == that.enabled && Objects.equals(this.lastAlertEndTimeMs, that.lastAlertEndTimeMs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, enabled, lastAlertEndTimeMs);
  }

  @Override
  public String toString() {
    return "MiningKpiAlert{" + "id=" + id + ", miningKpiThreshold=" +
        (Objects.isNull(miningKpiThreshold) ? null : miningKpiThreshold.getId()) + ", enabled=" + enabled +
        ", lastAlertEndTimeMs=" + lastAlertEndTimeMs + "}";
  }
}
