package com.appiancorp.record.entities;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appiancorp.config.xsd.TypeQNameUtil;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.record.fieldmetadata.ReadOnlyRecordSourceFieldMetaData;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.RuleRef;
import com.appiancorp.type.refs.XmlRuleRefAdapter;
import com.google.common.base.Preconditions;

@Hidden
@Entity
@Table(name = "rs_field_metadata")
@IgnoreJpa
@XmlRootElement(name = "recordSourceFieldMetaData", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = "recordSourceFieldMetaData", namespace = Type.APPIAN_NAMESPACE, propOrder = {Uuid.LOCAL_PART, "metaDataType",
    "metaDataExpression", "metaDataUiObject"})
@XmlAccessorType(XmlAccessType.NONE)
public class RecordSourceFieldMetaDataCfg implements Id<Long>, Uuid<String>, ReadOnlyRecordSourceFieldMetaData {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String uuid;
  private RecordSourceFieldCfg sourceField;
  private Long metaDataType;
  private String metaDataExpression;
  private String metaDataObjectUuid;
  private static final String RULE_TYPE_QNAME = "{http://www.appian.com/ae/types/2009}ContentFreeformRule";
  private transient ExpressionTransformationState
      expressionTransformationState = ExpressionTransformationState.STORED;

  public RecordSourceFieldMetaDataCfg() {}

  public RecordSourceFieldMetaDataCfg(
      Long id,
      String uuid,
      RecordSourceFieldCfg sourceField,
      Long metaDataType,
      String metaDataExpression,
      String metaDataObjectUuid,
      ExpressionTransformationState expressionTransformationState) {
    this.id = id;
    this.uuid = uuid;
    this.sourceField = sourceField;
    this.metaDataType = metaDataType;
    this.metaDataExpression = metaDataExpression;
    this.metaDataObjectUuid = metaDataObjectUuid;
    this.expressionTransformationState = expressionTransformationState;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  @XmlTransient
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @JoinColumn(name = "field_id", nullable = false, insertable = false, updatable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH},
      fetch = FetchType.EAGER, optional = false)
  @XmlTransient
  public RecordSourceFieldCfg getSourceField() {
    return this.sourceField;
  }

  public void setSourceField(RecordSourceFieldCfg sourceField) {
    this.sourceField = sourceField;
  }

  @Column(name = "meta_data_type", nullable = false)
  @XmlElement(name = "metaDataType", namespace = Type.APPIAN_NAMESPACE)
  public Long getMetaDataType() {
    return this.metaDataType;
  }

  public void setMetaDataType(Long metaDataTypeId) {
    this.metaDataType = metaDataTypeId;
  }

  @Lob
  @Column(name = "meta_data_expression", nullable = true)
  @XmlElement(name = "metaDataExpression", namespace = Type.APPIAN_NAMESPACE)
  @ComplexForeignKey(nullable=false, breadcrumb= BreadcrumbText.recordTypeMetadataValidationExpression, variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getMetaDataExpression() {
    return this.metaDataExpression;
  }

  public void setMetaDataExpression(String metaDataValues) {
    this.metaDataExpression = metaDataValues;
  }

  @Column(name = "meta_data_object_uuid", nullable = true)
  @XmlTransient
  public String getMetaDataObjectUuid() {
    return this.metaDataObjectUuid;
  }

  public void setMetaDataObjectUuid(String metaDataObjectUuid) {
    this.metaDataObjectUuid = metaDataObjectUuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecordSourceFieldMetaDataCfg that = (RecordSourceFieldMetaDataCfg) o;
    return Objects.equals(id, that.id)
        && Objects.equals(uuid, that.uuid)
        && Objects.equals(metaDataType, that.metaDataType)
        && Objects.equals(metaDataExpression, that.metaDataExpression)
        && Objects.equals(metaDataObjectUuid, that.metaDataObjectUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uuid, metaDataType, metaDataExpression, metaDataObjectUuid);
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

  @Transient
  @XmlTransient
  @Override
  public ExpressionTransformationState getExpressionTransformationState() {
    return expressionTransformationState;
  }

  public void setExpressionTransformationState(ExpressionTransformationState state) {
    this.expressionTransformationState = state;
  }

  @Override
  @XmlElement(type = Object.class)
  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeMetadataUiObject)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<?,?> getMetaDataUiObject() {
    if (getMetaDataObjectUuid() != null && !getMetaDataObjectUuid().isEmpty()) {
      QName qName = QName.valueOf(RULE_TYPE_QNAME);

      if (TypeQNameUtil.getQName(RuleRef.class).equals(qName)) {
        return new XmlRuleRefAdapter.RuleRefImpl(getMetaDataObjectUuid());
      } else {
        throw new IllegalStateException("No default JAXB class found for type " + qName);
      }
    }
    return null;
  }

  public void setMetaDataUiObject(Ref<?,?> uiObject) {
    Preconditions.checkNotNull(uiObject, "Target is required");
    if (!(uiObject instanceof XmlRuleRefAdapter.RuleRefImpl)) {
      throw new UnsupportedOperationException("Unsupported target type " + uiObject.getClass().getName());
    }
    Object uiObjectUuidObj = uiObject.getUuid();
    Preconditions.checkNotNull(uiObjectUuidObj, "Uuid is required");
    if (!(uiObjectUuidObj instanceof String)) {
      throw new UnsupportedOperationException("Unsupported uuid type " +
          uiObjectUuidObj.getClass().getSimpleName());
    }
    setMetaDataObjectUuid((String)uiObjectUuidObj);
  }

  @Override
  public String toString() {
    return "RecordSourceFieldMetaDataCfg{" + "metaDataObjectUuid='" + metaDataObjectUuid + '\'' +
        ", metaDataExpression='" + metaDataExpression + '\'' + ", metaDataType=" + metaDataType +
        ", sourceField=" + sourceField + ", uuid='" + uuid + '\'' + ", id=" + id + '}';
  }
}
