package com.appiancorp.processHq.persistence.entities.finding;

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

import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.MiningInsight;

@Entity
@Table(name = "mining_insight_finding")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MiningInsightFinding {
  public static final String PROP_INSIGHT = "insight";
  public static final String PROP_SNAPSHOT_IMPACT = "snapshotImpact";
  private Long id;
  private MiningInsight insight;
  private int orderIndex;
  private Long snapshotImpact;
  private Long snapshotCount;
  private Long snapshotMetric;

  public MiningInsightFinding() {}

  public MiningInsightFinding(MiningInsightFinding miningInsightFinding) {
    this.id = miningInsightFinding.id;
    this.insight = miningInsightFinding.insight;
    this.orderIndex = miningInsightFinding.orderIndex;
    this.snapshotImpact = miningInsightFinding.snapshotImpact;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_insight_id", nullable = false)
  public MiningInsight getInsight() {
    return insight;
  }

  public void setInsight(MiningInsight insight) {
    this.insight = insight;
  }

  @Column(name = "order_index", nullable = false)
  @CopilotField(name = "orderIndex", description = "The order index of this findings in the list of all insight's findings.")
  public int getOrderIndex() {
    return orderIndex;
  }

  public void setOrderIndex(int orderIndex) {
    this.orderIndex = orderIndex;
  }

  @Column(name = "snapshot_impact", nullable = false)
  @CopilotField(name = "snapshotImpactTime", description = "The time in milliseconds captures the impact of this finding.")
  public Long getSnapshotImpact() {
    return snapshotImpact;
  }

  public void setSnapshotImpact(Long snapshotImpact) {
    this.snapshotImpact = snapshotImpact;
  }

  @Column(name = "snapshot_count", nullable = false)
  @CopilotField(name = "snapshotCaseCount", description = "The number of Cases are affected by this finding.")
  public Long getSnapshotCount() {
    return snapshotCount;
  }

  public void setSnapshotCount(Long snapshotCount) {
    this.snapshotCount = snapshotCount;
  }

  @Column(name = "snapshot_metric", nullable = false)
  @CopilotField(name = "snapshotMetric", description = "The statistics associated with this finding.")
  public Long getSnapshotMetric() {
    return snapshotMetric;
  }

  public void setSnapshotMetric(Long snapshotMetric) {
    this.snapshotMetric = snapshotMetric;
  }

  @Override
  public String toString() {
    return "MiningInsightFinding{" + "id=" + id + ", miningInsight=" + insightToString() +
        ", orderIndex=" + orderIndex + ", snapshotImpact=" + snapshotImpact +
        ", snapshotCount=" + snapshotCount + ", snapshotMetric=" + snapshotMetric + "}";
  }

  protected String insightToString() {
    if (insight == null) {
      return null;
    } else {
      return "MiningInsight{" + "id=" + insight.getId() + ", name=" + insight.getName() + "}";
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningInsightFinding)) {
      return false;
    }
    MiningInsightFinding that = (MiningInsightFinding)o;
    return Objects.equals(id, that.id) && Objects.equals(insight, that.insight) &&
        orderIndex == that.orderIndex && Objects.equals(snapshotImpact, that.snapshotImpact) &&
        Objects.equals(snapshotCount, that.snapshotCount) && Objects.equals(
        snapshotMetric, that.snapshotMetric);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, insight, orderIndex, snapshotImpact, snapshotCount, snapshotMetric);
  }
}
