package com.appiancorp.processHq.persistence.entities.customKpi.field;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;

@Entity
@Table(name = "mining_kpi_field_sequence")
@PrimaryKeyJoinColumn(name = "mining_kpi_field_id")
@CopilotClass(name="kpiFieldSequence")
public class MiningKpiFieldSequence extends MiningKpiField {
  private String fromActivity;
  private String toActivity;
  private boolean isDirectFollower;

  public MiningKpiFieldSequence() {}

  @Column(name = "from_activity", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "fromActivity", description = "The name of activity at the start of the sequence.")
  public String getFromActivity() {
    return fromActivity;
  }

  public void setFromActivity(String fromActivity) {
    this.fromActivity = fromActivity;
  }

  @Column(name = "to_activity", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "toActivity", description = "The name of activity at the end of the sequence.")
  public String getToActivity() {
    return toActivity;
  }

  public void setToActivity(String toActivity) {
    this.toActivity = toActivity;
  }

  @Column(name = "is_direct_follower")
  @CopilotField(name = "isDirectFollower", description = "This boolean indicates that the toActivity immediately follows the fromActivity when true. When false, there is at least one other activity in between.")
  public boolean getIsDirectFollower() {
    return isDirectFollower;
  }

  public void setIsDirectFollower(boolean isDirectFollower) {
    this.isDirectFollower = isDirectFollower;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiFieldSequence)) {
      return false;
    }
    MiningKpiFieldSequence that = (MiningKpiFieldSequence)o;
    return super.equals(o) && isDirectFollower == that.isDirectFollower &&
        Objects.equals(fromActivity, that.fromActivity) && Objects.equals(toActivity, that.toActivity);
  }

  @Override
  @Transient
  public MiningKpiFieldType getFieldType() {
    return MiningKpiFieldType.SEQUENCE;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isDirectFollower, fromActivity, toActivity);
  }

  @Override
  public String toString() {
    return "MiningKpiFieldSequence{" + "id=" + getId() + ", fromActivity='" + fromActivity + '\'' +
        ", toActivity='" + toActivity + '\'' + ", isDirectFollower=" + isDirectFollower + '}';
  }

  @Override
  @Transient
  public MiningKpiKey getMiningKpiKey() {
    return new SequenceKey(fromActivity, toActivity, isDirectFollower);
  }

  public record SequenceKey(String fromActivity, String toActivity, boolean isDirectFollower) implements MiningKpiKey {}
}
