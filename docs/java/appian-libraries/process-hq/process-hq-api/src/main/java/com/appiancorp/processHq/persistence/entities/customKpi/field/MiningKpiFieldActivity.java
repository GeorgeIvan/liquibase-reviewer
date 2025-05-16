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
@Table(name = "mining_kpi_field_activity")
@PrimaryKeyJoinColumn(name = "mining_kpi_field_id")
@CopilotClass(name="kpiFieldActivity")
public class MiningKpiFieldActivity extends MiningKpiField {
  private String activity;

  public MiningKpiFieldActivity() {}

  @Column(name = "activity", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "activityName")
  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiFieldActivity)) {
      return false;
    }
    MiningKpiFieldActivity that = (MiningKpiFieldActivity)o;
    return super.equals(o) && Objects.equals(activity, that.activity);
  }

  @Override
  @Transient
  public MiningKpiFieldType getFieldType() {
    return MiningKpiFieldType.ACTIVITY;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), activity);
  }

  @Override
  public String toString() {
    return "MiningKpiFieldActivity{" + "id=" + getId() + ", activity='" + activity + '\'' + '}';
  }

  @Override
  @Transient
  public MiningKpiKey getMiningKpiKey() {
    return new ActivityKey(activity);
  }

  public record ActivityKey(String activity) implements MiningKpiKey {}
}
