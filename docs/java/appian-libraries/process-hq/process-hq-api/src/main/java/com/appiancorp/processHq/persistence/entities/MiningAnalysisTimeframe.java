package com.appiancorp.processHq.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "mining_analysis_timeframe")
public class MiningAnalysisTimeframe {

  private long id;
  private MiningInsight insight;
  private ProcessMiningFilterGroupAnalysisTimePeriodType analysisTimePeriod;
  private Long analysisStartTs;
  private Long analysisEndTs;

  public MiningAnalysisTimeframe() {
    // Empty constructor for Hibernate
  }

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
  @JoinColumn(name = "mining_insight_id", referencedColumnName = "id", nullable = false, unique = true)
  public MiningInsight getInsight() {
    return insight;
  }

  public void setInsight(MiningInsight insight) {
    this.insight = insight;
  }

  @Transient
  public ProcessMiningFilterGroupAnalysisTimePeriodType getAnalysisTimePeriod() {
    return analysisTimePeriod;
  }

  public void setAnalysisTimePeriod(ProcessMiningFilterGroupAnalysisTimePeriodType analysisTimePeriod) {
    this.analysisTimePeriod = analysisTimePeriod;
  }

  @Column(name = "time_period", nullable = false)
  private byte getAnalysisTimePeriodByte() {
    return analysisTimePeriod != null ? analysisTimePeriod.getCode() : ProcessMiningFilterGroupAnalysisTimePeriodType.ALL_TIME.getCode();
  }

  private void setAnalysisTimePeriodByte(byte type) {
    setAnalysisTimePeriod(ProcessMiningFilterGroupAnalysisTimePeriodType.valueOf(type));
  }

  @Column(name = "start_ts", nullable = false)
  public Long getAnalysisStartTs() {
    return analysisStartTs;
  }

  public void setAnalysisStartTs(Long analysisStartTs) {
    this.analysisStartTs = analysisStartTs;
  }

  @Column(name = "end_ts", nullable = false)
  public Long getAnalysisEndTs() {
    return analysisEndTs;
  }

  public void setAnalysisEndTs(Long analysisEndTs) {
    this.analysisEndTs = analysisEndTs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningAnalysisTimeframe that = (MiningAnalysisTimeframe)o;
    return getId() == that.getId() && Objects.equals(getInsight().getId(), that.getInsight().getId()) &&
        getAnalysisTimePeriod() == that.getAnalysisTimePeriod() &&
        Objects.equals(getAnalysisStartTs(), that.getAnalysisStartTs()) &&
        Objects.equals(getAnalysisEndTs(), that.getAnalysisEndTs());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getInsight().getId(), getAnalysisTimePeriod(), getAnalysisStartTs(),
        getAnalysisEndTs());
  }

  @Override
  public String toString() {
    return "MiningAnalysisTimeframe{" + "id=" + id + ", insightId=" + insight.getId() +
        ", analysisTimePeriod=" + analysisTimePeriod + ", analysisStartTs=" + analysisStartTs +
        ", analysisEndTs=" + analysisEndTs + '}';
  }
}
