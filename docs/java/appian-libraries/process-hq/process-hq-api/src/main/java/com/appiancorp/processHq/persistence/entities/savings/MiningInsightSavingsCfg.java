package com.appiancorp.processHq.persistence.entities.savings;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.MiningInsight;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;

@Entity
@Table(name = "mining_insight_savings_cfg")
@CopilotClass(name = "savings", description = "Provides details about potential savings discovered in the insight.")
public class MiningInsightSavingsCfg implements HasAuditInfo {

  public static final String PROP_SNAPSHOT_SAVINGS = "snapshotSavings";
  private long id;
  private MiningInsight insight;
  private String comparisonBasisType;
  private double potentialPercentage;
  private double snapshotSavings;
  private PotentialSavingsUnit unit;
  private PotentialSavingsTimeframe timeframe;
  private AuditInfo auditInfo = new AuditInfo();

  public enum ComparisonBasis {
    LAST_LEVEL,
    INVESTIGATION
  }

  public MiningInsightSavingsCfg() {
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

  @Column(name = "comparison_basis_type", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "comparisonType", description = "The comparison type determines the set of filters to represent the savings.", validValues = {
      "LAST_LEVEL", "INVESTIGATION"})
  public String getComparisonBasisType() {
    return comparisonBasisType;
  }

  public void setComparisonBasisType(String comparisonBasisType) {
    this.comparisonBasisType = comparisonBasisType;
  }

  @Column(name = "potential_percentage", nullable = false)
  @CopilotField(name = "potentialPercentage", description = "The percentage of Cases affected by the savings.")
  public double getPotentialPercentage() {
    return potentialPercentage;
  }

  public void setPotentialPercentage(Double potentialPercentage) {
    this.potentialPercentage = potentialPercentage;
  }

  @Transient
  @CopilotField(name = "savingsUnit", description = "The time unit of identified potential savings.")
  public PotentialSavingsUnit getUnit() {
    return unit;
  }

  public void setUnit(PotentialSavingsUnit unit) {
    this.unit = unit;
  }

  @Column(name = "unit", nullable = false)
  private byte getUnitByte() {
    return unit != null ? unit.getCode() : PotentialSavingsUnit.DAYS.getCode();
  }

  private void setUnitByte(byte unitByte) {
    setUnit(PotentialSavingsUnit.valueOf(unitByte));
  }

  @Transient
  @CopilotField(name = "savingsTimeframe", description = "The time frame of the process corresponding to the savings.")
  public PotentialSavingsTimeframe getTimeframe() {
    return timeframe;
  }

  public void setTimeframe(PotentialSavingsTimeframe timeframe) {
    this.timeframe = timeframe;
  }

  @Column(name = "savings_timeframe", nullable = false)
  private byte getTimeframeByte() {
    return timeframe != null ? timeframe.getCode() : PotentialSavingsTimeframe.YEAR.getCode();
  }

  private void setTimeframeByte(byte timeframeByte) {
    setTimeframe(PotentialSavingsTimeframe.valueOf(timeframeByte));
  }

  @Column(name = "snapshot_savings")
  @CopilotField(name = "savingsTime", description = "The amount of time that can be saved given the insight findings, timeframe, potential percentage, and comparison type")
  public double getSnapshotSavings() {
    return snapshotSavings;
  }

  public void setSnapshotSavings(Double snapshotSavings) {
    this.snapshotSavings = snapshotSavings;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public String toString() {
    String insightIdString = insight == null ? "null" : String.valueOf(insight.getId());
    return "MiningInsightSavingsCfg{" + "id=" + id + ", insight=[id=" + insightIdString +
        "], comparisonBasisType='" + comparisonBasisType + '\'' + ", potentialPercentage=" +
        potentialPercentage + ", snapshotSavings=" + snapshotSavings + ", unit=" + unit + ", timeframe=" +
        timeframe + ", auditInfo=" + auditInfo + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningInsightSavingsCfg that = (MiningInsightSavingsCfg)o;
    return id == that.id && Objects.equals(insight, that.insight) &&
        Objects.equals(comparisonBasisType, that.comparisonBasisType) &&
        Objects.equals(potentialPercentage, that.potentialPercentage) &&
        Objects.equals(snapshotSavings, that.snapshotSavings) && unit == that.unit &&
        timeframe == that.timeframe && Objects.equals(auditInfo, that.auditInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, insight, comparisonBasisType, potentialPercentage, snapshotSavings, unit,
        timeframe, auditInfo);
  }
}
