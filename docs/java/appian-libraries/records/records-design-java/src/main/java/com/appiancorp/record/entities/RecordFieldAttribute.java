package com.appiancorp.record.entities;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.record.sources.FieldAttribute;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordSourceFieldAttribute;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "rt_source_field_attribute")
@IgnoreJpa
@XmlRootElement(name = "recordFieldAttributes", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = "recordFieldAttributes", namespace = Type.APPIAN_NAMESPACE, propOrder = {"attributeType"})
@XmlAccessorType(XmlAccessType.NONE)
public class RecordFieldAttribute implements Id<Long>,  ReadOnlyRecordFieldAttribute {
  private Long id;
  private RecordSourceFieldCfg sourceField;
  private FieldAttribute attributeType;

  public RecordFieldAttribute() {
  }

  public RecordFieldAttribute(DesignerDtoRecordSourceFieldAttribute dtoRecordSourceFieldAttribute, RecordSourceFieldCfg sourceField) {
    this.id = dtoRecordSourceFieldAttribute.getId();
    this.sourceField = sourceField;
    this.attributeType = FieldAttribute.valueOf(dtoRecordSourceFieldAttribute.getAttributeType());
  }

  public RecordFieldAttribute(ReadOnlyRecordFieldAttribute readOnlyRecordFieldAttribute) {
    this.id = readOnlyRecordFieldAttribute.getId();
    this.sourceField = new RecordSourceFieldCfg(readOnlyRecordFieldAttribute.getSourceField());
    this.attributeType = FieldAttribute.getByName(readOnlyRecordFieldAttribute.getAttributeType());
  }

  public RecordFieldAttribute(Long id, RecordSourceFieldCfg sourceField, FieldAttribute attributeType) {
    this.id = id;
    this.sourceField = sourceField;
    this.attributeType = attributeType;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @JoinColumn(name = "source_field_id", nullable = false, insertable = false, updatable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
  @XmlTransient
  public RecordSourceFieldCfg getSourceField() {
    return this.sourceField;
  }

  public void setSourceField(RecordSourceFieldCfg sourceField) {
    this.sourceField = sourceField;
  }

  @Column(name = "attribute_type", nullable = false)
  public byte getAttributeTypeByte() {
    if (attributeType != null) {
      return attributeType.getCode();
    }

    return 0;
  }

  public void setAttributeTypeByte(byte attributeTypeByte) {
    this.attributeType = FieldAttribute.getByCode(attributeTypeByte);
  }

  @Override
  @Transient
  @XmlElement(name = "attributeType", namespace = Type.APPIAN_NAMESPACE)
  public String getAttributeType() {
    return attributeType.name();
  }

  public void setAttributeType(String attributeType) {
    this.attributeType = FieldAttribute.getByName(attributeType);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordFieldAttribute that)) {
      return false;
    }

    return new EqualsBuilder().append(id, that.id)
        .append(attributeType, that.attributeType)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, attributeType);
  }
}
