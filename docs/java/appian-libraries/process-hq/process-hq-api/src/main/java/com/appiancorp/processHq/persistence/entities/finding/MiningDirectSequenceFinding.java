package com.appiancorp.processHq.persistence.entities.finding;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;

@Entity
@Table(name = "mining_direct_seq_finding")
@PrimaryKeyJoinColumn(name = "mining_insight_finding_id")
@CopilotClass(name = "activitySequenceFinding", description = "The activity sequence finding captures statistics about the flow from one activity to another one.")
public class MiningDirectSequenceFinding extends MiningInsightFinding {
  private String fromActivity;
  private String toActivity;
  private Long numOccurrences;

  public MiningDirectSequenceFinding() {}

  public MiningDirectSequenceFinding(MiningInsightFinding miningInsightFinding) {
    super(miningInsightFinding);
  }

  @Column(name = "from_activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "fromActivity", description = "The name of the activity of the sequence start point.")
  public String getFromActivity() {
    return fromActivity;
  }

  public void setFromActivity(String fromActivity) {
    this.fromActivity = fromActivity;
  }

  @Column(name = "to_activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "toActivity", description = "The name of the activity of the sequence end point.")
  public String getToActivity() {
    return toActivity;
  }

  public void setToActivity(String toActivity) {
    this.toActivity = toActivity;
  }

  @Column(name = "number_of_occurrences", nullable = false)
  @CopilotField(name = "count", description = "The number of times the activity sequence occurred in the process.")
  public Long getNumOccurrences() {
    return numOccurrences;
  }

  public void setNumOccurrences(Long numOccurrences) {
    this.numOccurrences = numOccurrences;
  }

  @Override
  public String toString() {
    return "MiningDirectSequenceFinding{" + "id=" + this.getId() + ", miningInsight=" + insightToString() +
        ", orderIndex=" + this.getOrderIndex() + ", snapshotImpact=" + this.getSnapshotImpact() +
        ", snapshotCount=" + this.getSnapshotCount() + ", snapshotMetric=" + this.getSnapshotMetric() +
        ", fromActivity=" + fromActivity + ", toActivity=" + toActivity +
        ", numOccurrences=" + numOccurrences + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningDirectSequenceFinding)) {
      return false;
    }
    MiningDirectSequenceFinding that = (MiningDirectSequenceFinding)o;
    return super.equals(o) && Objects.equals(fromActivity, that.fromActivity) &&
        Objects.equals(toActivity, that.toActivity) && Objects.equals(numOccurrences, that.numOccurrences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fromActivity, toActivity, numOccurrences);
  }
}
