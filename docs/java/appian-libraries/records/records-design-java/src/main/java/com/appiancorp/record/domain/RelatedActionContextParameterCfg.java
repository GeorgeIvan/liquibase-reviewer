package com.appiancorp.record.domain;

import static com.appiancorp.core.expr.portable.cdt.ValueConfigType.EXPRESSION;
import static com.appiancorp.core.expr.portable.cdt.ValueConfigType.GUIDED;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.cdt.ValueConfigType;
import com.appiancorp.core.expr.portable.annotations.VisibleForTesting;
import com.appiancorp.record.recordactions.ReadOnlyRelatedActionContextParameter;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRelatedActionContextParameterCfg;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "ra_context_param")
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="relatedActionContextParameterCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RelatedActionContextParameterCfg.LOCAL_PART,
    propOrder={"id", "parameterName", "parameterType", "value",
    "valueType", "valueConfigType"})

public class RelatedActionContextParameterCfg
    implements Id<Long>, ReadOnlyRelatedActionContextParameter {

  public static final String LOCAL_PART="RelatedActionContextParameterCfg";

  public static final String PROP_ID = "id";
  public static final String PROP_PARAMETER_NAME = "parameterName";
  public static final String PROP_PARAMETER_TYPE = "parameterType";
  public static final String PROP_VALUE = "value";
  public static final String PROP_VALUE_TYPE = "valueType";
  public static final String PROP_VALUE_CONFIG_TYPE = "valueConfigType";

  private Long id;
  private String parameterName;
  private int parameterType;
  private String value;
  private int valueType;
  private ValueConfigType valueConfigType;

  public RelatedActionContextParameterCfg() {}

  @VisibleForTesting
  public RelatedActionContextParameterCfg(Long id, String parameterName, int parameterType, String value, int valueType, ValueConfigType valueConfigType) {
    this.id = id;
    this.parameterName = parameterName;
    this.parameterType = parameterType;
    this.value = value;
    this.valueType = valueType;
    this.valueConfigType = valueConfigType;
  }

  public RelatedActionContextParameterCfg(DesignerDtoRelatedActionContextParameterCfg dto) {
    this.id = dto.getId();
    this.parameterName = dto.getParameterName();
    this.parameterType = dto.getParameterType();
    this.value = dto.getValue();
    this.valueType = dto.getValueType();
    this.valueConfigType = dto.getValueConfigType();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="parameter_name", nullable = false)
  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  @Column(name="parameter_type", nullable = false)
  public int getParameterType() {
    return parameterType;
  }

  public void setParameterType(int parameterType) {
    this.parameterType = parameterType;
  }

  @Lob
  @Column(name="value", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name="value_type", nullable = true)
  public int getValueType() {
    return valueType;
  }

  public void setValueType(int valueType) {
    this.valueType = valueType;
  }

  @Transient
  public ValueConfigType getValueConfigType() {
    return valueConfigType;
  }

  public void setValueConfigType(ValueConfigType valueConfigType) {
    this.valueConfigType = valueConfigType;
  }

  @XmlTransient
  @Column(name = "value_config_type", nullable = false)
  private byte getValueConfigTypeByte() {
    if (valueConfigType == null) {
      return 0;
    }

    switch (valueConfigType) {
      case EXPRESSION:
        return 0;
      case GUIDED:
        return 1;
      default:
        return 0;
    }
  }

  private void setValueConfigTypeByte(byte index) {
    if (index == 0) {
      valueConfigType = EXPRESSION;
    } else {
      valueConfigType = GUIDED;
    }

    setValueConfigType(valueConfigType);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RelatedActionContextParameterCfg)) {
      return false;
    }

    RelatedActionContextParameterCfg that = (RelatedActionContextParameterCfg)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(parameterName, that.parameterName)
        .append(parameterType, that.parameterType)
        .append(value, that.value)
        .append(valueType, that.valueType)
        .append(valueConfigType, that.valueConfigType)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, parameterName, parameterType,
        value, valueType, valueConfigType);
  }
}
