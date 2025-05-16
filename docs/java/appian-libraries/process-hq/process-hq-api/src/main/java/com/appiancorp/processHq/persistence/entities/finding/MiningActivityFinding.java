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
@Table(name = "mining_activity_finding")
@PrimaryKeyJoinColumn(name = "mining_insight_finding_id")
@CopilotClass(name = "activityFinding", description = "The activity finding captures statistics about a particular process activity.")
public class MiningActivityFinding extends MiningInsightFinding {
  private String activity;
  private Long numOccurrences;

  public MiningActivityFinding() {}

  public MiningActivityFinding(MiningInsightFinding miningInsightFinding) {
    super(miningInsightFinding);
  }

  @Column(name = "activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "activity", description = "The activity name.")
  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  @Column(name = "number_of_occurrences", nullable = false)
  @CopilotField(name = "count", description = "The number of times the activity occurred in the process.")
  public Long getNumOccurrences() {
    return numOccurrences;
  }

  public void setNumOccurrences(Long numOccurrences) {
    this.numOccurrences = numOccurrences;
  }

  @Override
  public String toString() {
    return "MiningActivityFinding{" + "id=" + this.getId() + ", miningInsight=" + insightToString() +
        ", orderIndex=" + this.getOrderIndex() + ", snapshotImpact=" + this.getSnapshotImpact() +
        ", snapshotCount=" + this.getSnapshotCount() + ", snapshotMetric=" + this.getSnapshotMetric() +
        ", activity=" + activity + ", numOccurrences=" + numOccurrences + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningActivityFinding)) {
      return false;
    }
    MiningActivityFinding that = (MiningActivityFinding)o;
    return super.equals(o) && Objects.equals(activity, that.activity) &&
        Objects.equals(numOccurrences, that.numOccurrences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), activity, numOccurrences);
  }
}
