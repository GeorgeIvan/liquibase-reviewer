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
@Table(name = "mining_kpi_field_attribute")
@PrimaryKeyJoinColumn(name = "mining_kpi_field_id")
@CopilotClass(name="kpiFieldAttribute")
public class MiningKpiFieldAttribute extends MiningKpiField {
  private String attributeCategory;
  private String attributeValue;

  public MiningKpiFieldAttribute() {}

  @Column(name = "attribute_category", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "category")
  public String getAttributeCategory() {
    return attributeCategory;
  }

  public void setAttributeCategory(String attributeCategory) {
    this.attributeCategory = attributeCategory;
  }

  @Column(name = "attribute_value", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "value")
  public String getAttributeValue() {
    return attributeValue;
  }

  public void setAttributeValue(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiFieldAttribute)) {
      return false;
    }
    MiningKpiFieldAttribute that = (MiningKpiFieldAttribute)o;
    return super.equals(o) && Objects.equals(attributeCategory, that.attributeCategory) &&
        Objects.equals(attributeValue, that.attributeValue);
  }

  @Override
  @Transient
  public MiningKpiFieldType getFieldType() {
    return MiningKpiFieldType.ATTRIBUTE;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), attributeCategory, attributeValue);
  }

  @Override
  public String toString() {
    return "MiningKpiFieldAttribute{" + "id=" + getId() + ", attributeCategory='" + attributeCategory + '\'' +
        ", attributeValue='" + attributeValue + '\'' + '}';
  }

  @Override
  @Transient
  public MiningKpiKey getMiningKpiKey() {
    return new FieldAttributeKey(attributeCategory, attributeValue);
  }

  public record FieldAttributeKey(String category, String value) implements MiningKpiKey {}
}
