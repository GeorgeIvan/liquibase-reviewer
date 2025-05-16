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
@Table(name = "mining_categ_attr_finding")
@PrimaryKeyJoinColumn(name = "mining_insight_finding_id")
@CopilotClass(name = "attributeFinding", description = "The attribute finding captures statistics about a particular categorical attribute of the process.")
public class MiningCategoricalAttributeFinding extends MiningInsightFinding {
  private String attributeName;
  private String attributeValue;

  public MiningCategoricalAttributeFinding() {}

  public MiningCategoricalAttributeFinding(MiningInsightFinding miningInsightFinding) {
    super(miningInsightFinding);
  }

  @Column(name = "attribute_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "attribute", description = "The categorical attribute name.")
  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  @Column(name = "attribute_value", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "value", description = "The value of the categorical attribute.")
  public String getAttributeValue() {
    return attributeValue;
  }

  public void setAttributeValue(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  @Override
  public String toString() {
    return "MiningCategoricalAttributeFinding{" + "id=" + this.getId() +
        ", miningInsight=" + insightToString() + ", orderIndex=" + this.getOrderIndex() +
        ", snapshotImpact=" + this.getSnapshotImpact() + ", snapshotCount=" + this.getSnapshotCount() +
        ", snapshotMetric=" + this.getSnapshotMetric() + ", attributeName=" + attributeName +
        ", attributeValue=" + attributeValue + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningCategoricalAttributeFinding)) {
      return false;
    }
    MiningCategoricalAttributeFinding that = (MiningCategoricalAttributeFinding)o;
    return super.equals(o) && Objects.equals(attributeName, that.attributeName) &&
        Objects.equals(attributeValue, that.attributeValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), attributeName, attributeValue);
  }
}
