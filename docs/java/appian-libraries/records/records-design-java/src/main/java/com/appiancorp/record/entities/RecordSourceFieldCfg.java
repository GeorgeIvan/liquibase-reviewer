package com.appiancorp.record.entities;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;

import com.appian.core.persist.Constants;
import com.appiancorp.core.API;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.PortableTypedValue;
import com.appiancorp.core.expr.portable.PropertyDescriptor;
import com.appiancorp.core.expr.portable.Value;
import com.appiancorp.core.expr.portable.cdt.RecordFieldCalculationType;
import com.appiancorp.core.expr.portable.cdt.RecordFieldPurposeInProduct;
import com.appiancorp.core.expr.portable.cdt.RecordFieldSubtype;
import com.appiancorp.core.expr.portable.cdt.RecordFieldTemplateType;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.record.fieldmetadata.ReadOnlyRecordSourceFieldMetaData;
import com.appiancorp.record.sources.FieldAttribute;
import com.appiancorp.record.sources.ReadOnlyRecordSourceField;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.DesignerDtoRecordSourceField;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/*
 * Defines a field for use in configuring a replicated record. It contains info about the field as it is exposed
 * by the recordType and also info about the last source needed to compute its value. Currently there is only
 * one source used to compute its value.
 */
@Hidden
@Entity
@Table(name = "record_source_fields")
@IgnoreJpa
@XmlRootElement(name = "recordSourceField", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = "recordSourceField", namespace = Type.APPIAN_NAMESPACE, propOrder = {"uuid", "type",
    "sourceFieldName", "sourceFieldType", "fieldName", "displayName", "description", "isRecordId", "isUnique", "isCustomField",
    "customFieldExpr", "customFieldDefaultValueStr", "fieldCalculationType", "fieldTemplateType",
    "recordFieldSecurityMembershipFilter", "fieldFormat", "fieldMetaData", "isIndexable", "subType",
    "purposeInProduct",
    RecordSourceFieldCfg.RECORD_FIELD_ATTRIBUTES})
@BreadcrumbProperty(value = "fieldName", format = BreadcrumbText.recordTypeSourceFieldFormat,
    breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_SEPARATE_NODES)
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class RecordSourceFieldCfg implements Id<Long>, Uuid<String>, ReadOnlyRecordSourceField {

  public static final String PROP_DISPLAY_NAME = "displayName";
  public static final String PROP_DESCRIPTION = "description";
  private static final Logger LOG = Logger.getLogger(RecordSourceFieldCfg.class.getName());
  private static final long serialVersionUID = 1L;
  public static final String RECORD_FIELD_ATTRIBUTES = "recordFieldAttributes";

  private Long id;
  private String uuid;
  private String type;
  private String sourceFieldName;
  private String sourceFieldType;
  private String fieldName;
  private String displayName;
  private String description;
  private boolean isRecordId;
  private boolean isUnique;
  private boolean isCustomField = false;
  private String customFieldExpr;
  private String customFieldDefaultValueStr;
  private RecordFieldCalculationType fieldCalculationType;
  private RecordFieldTemplateType fieldTemplateType;
  private transient Value<?> customFieldDefaultValue;
  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;
  private Integer sourceFieldTypeLength;
  private String recordFieldSecurityMembershipFilter;
  private String fieldFormat;
  private Set<RecordSourceFieldMetaDataCfg> fieldMetaData = new LinkedHashSet<>();
  private boolean isIndexable = false;
  private RecordFieldSubtype subType;
  private RecordFieldPurposeInProduct purposeInProduct;
  private static final Pattern SOURCE_FIELD_TYPE_LENGTH_PATTERN = Pattern.compile("\\((\\d+)\\)");
  private Set<RecordFieldAttribute> recordFieldAttributes = new HashSet<>();

  public RecordSourceFieldCfg() {}

  public RecordSourceFieldCfg(DesignerDtoRecordSourceField dto) {
    this.id = dto.getId();
    this.uuid = dto.getUuid();
    this.type = dto.getType();
    this.sourceFieldType = dto.getSourceFieldType();
    this.sourceFieldName = dto.getSourceFieldName();
    this.fieldName = dto.getFieldName();
    this.displayName = dto.getDisplayName();
    this.description = dto.getDescription();
    this.isRecordId = dto.isIsRecordId();
    this.isUnique = dto.isIsUnique();
    this.isCustomField = dto.isIsCustomField();
    this.customFieldExpr = dto.getCustomFieldExpr();
    this.customFieldDefaultValueStr = encodeAsJson((PortableTypedValue)dto.getCustomFieldDefaultValue());
    this.fieldCalculationType = dto.getFieldCalculationType();
    this.fieldTemplateType = dto.getFieldTemplateType();
    this.setExpressionTransformationState(dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY);
    this.recordFieldSecurityMembershipFilter = dto.getRecordFieldSecurityMembershipFilter();
    this.fieldFormat = dto.getFieldFormat();
    this.fieldMetaData = dto.getFieldMetaData()
        .stream()
        .map(recordFieldMetaData -> new RecordSourceFieldMetaDataCfg(recordFieldMetaData.getId(),
            recordFieldMetaData.getUuid(), this, recordFieldMetaData.getMetaDataType(),
            recordFieldMetaData.getMetaDataExpression(), recordFieldMetaData.getMetaDataObjectUuid(),
            recordFieldMetaData.isExprIsEvaluable() ?
                ExpressionTransformationState.STORED :
                ExpressionTransformationState.DISPLAY))
        .collect(Collectors.toSet());
    this.recordFieldAttributes = dto.getFieldAttributes()
        .stream()
        .filter(Objects::nonNull)
        .map(fieldAttribute -> new RecordFieldAttribute(fieldAttribute, this))
        .collect(Collectors.toSet());
    this.isIndexable = dto.isIsIndexable();
    this.subType = dto.getSubType();
    this.purposeInProduct = dto.getPurposeInProduct();
  }

  public RecordSourceFieldCfg(ReadOnlyRecordSourceField readOnlyField) {
    this.id = readOnlyField.getId();
    this.uuid = readOnlyField.getUuid();
    this.type = readOnlyField.getType();
    this.sourceFieldType = readOnlyField.getSourceFieldType();
    this.sourceFieldName = readOnlyField.getSourceFieldName();
    this.fieldName = readOnlyField.getFieldName();
    this.displayName = readOnlyField.getDisplayName();
    this.description = readOnlyField.getDescription();
    this.isRecordId = readOnlyField.getIsRecordId();
    this.isUnique = readOnlyField.getIsUnique();
    this.isCustomField = readOnlyField.getIsCustomField();
    this.customFieldExpr = readOnlyField.getCustomFieldExpr();
    this.customFieldDefaultValueStr = readOnlyField.getCustomFieldDefaultValueStr();
    this.fieldCalculationType = readOnlyField.getFieldCalculationType();
    this.fieldTemplateType = readOnlyField.getFieldTemplateType();
    this.expressionTransformationState = readOnlyField.getExpressionTransformationState();
    this.recordFieldSecurityMembershipFilter = readOnlyField.getRecordFieldSecurityMembershipFilter();
    this.fieldFormat = readOnlyField.getFieldFormat();
    this.fieldMetaData = readOnlyField.getMetaData()
        .stream()
        .map(recordFieldMetaData -> new RecordSourceFieldMetaDataCfg(recordFieldMetaData.getId(),
            recordFieldMetaData.getUuid(), this, recordFieldMetaData.getMetaDataType(),
            recordFieldMetaData.getMetaDataExpression(), recordFieldMetaData.getMetaDataObjectUuid(), recordFieldMetaData.getExpressionTransformationState()))
        .collect(Collectors.toSet());
    this.isIndexable = readOnlyField.getIsIndexable();
    this.subType = readOnlyField.getSubType();
    this.purposeInProduct = readOnlyField.getPurposeInProduct();
    this.recordFieldAttributes = readOnlyField.getFieldAttributes()
        .stream()
        .map(RecordFieldAttribute::new)
        .collect(Collectors.toSet());
  }

  public RecordSourceFieldCfg(PropertyDescriptor propertyDescriptor, boolean isRecordId) {
    this.uuid = propertyDescriptor.getName();
    this.type = propertyDescriptor.getType().getQNameAsString();
    this.fieldName = propertyDescriptor.getName();
    this.isRecordId = isRecordId;
    this.isCustomField = false;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "type", nullable = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  @Column(name = "source_field_name")
  public String getSourceFieldName() {
    return sourceFieldName;
  }

  public void setSourceFieldName(String name) {
    this.sourceFieldName = name;
  }

  @Override
  @Column(name = "source_field_type")
  public String getSourceFieldType() {
    return sourceFieldType;
  }

  public void setSourceFieldType(String type) {
    this.sourceFieldType = type;
  }

  @Override
  @Column(name = "field_name", nullable = false)
  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String name) {
    this.fieldName = name;
  }

  @Override
  @Column(name = "display_name", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob /* Potentially supporting expressionable display names in the future */
  public String getDisplayName() {
    /* When not provided, displayName is stored as "", we need this to return null to be omitted from the XML */
    return Strings.isNullOrEmpty(displayName) ? null : displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  @Column(name = "description", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob /* Potentially supporting expressionable descriptions in the future */
  public String getDescription() {
    /* When not provided, description is stored as "", we need this to return null to be omitted from the XML */
    return Strings.isNullOrEmpty(description) ? null : description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @Column(name = "is_record_id", nullable = false)
  public boolean getIsRecordId() {
    return isRecordId;
  }

  public void setIsRecordId(boolean isRecordId) {
    this.isRecordId = isRecordId;
  }

  @Override
  @Column(name = "is_unique", nullable = false)
  public boolean getIsUnique() {
    return isUnique;
  }

  public void setIsUnique(boolean isUnique) {
    this.isUnique = isUnique;
  }

  @Override
  @Column(name = "is_custom_field", nullable = false)
  public boolean getIsCustomField() {
    return isCustomField;
  }

  public void setIsCustomField(boolean isCustomField) {
    this.isCustomField = isCustomField;
  }

  @Transient
  @Override
  @XmlElement(name = "purposeInProduct")
  public RecordFieldPurposeInProduct getPurposeInProduct() {
    return purposeInProduct;
  }

  public void setPurposeInProduct(RecordFieldPurposeInProduct purposeInProduct) {
    this.purposeInProduct = purposeInProduct;
  }

  @Column(name = "purpose_in_product", nullable = true)
  private Byte getPurposeInProductByte() {
    if (purposeInProduct == null) {
      return null;
    } else {
      return (byte) purposeInProduct.ordinal();
    }
  }

  private void setPurposeInProductByte(Byte purposeInProductByte) {
    if (purposeInProductByte == null) {
      setPurposeInProduct(null);
    } else {
      setPurposeInProduct(RecordFieldPurposeInProduct.values()[purposeInProductByte]);
    }
  }

  @Override
  @Column(name = "custom_field_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeCustomFieldExpression,
      variableBindings = VariableBindings.RECORD_TYPE)
  @XmlElement(nillable = true)
  public String getCustomFieldExpr() {
    return customFieldExpr;
  }

  public void setCustomFieldExpr(String customFieldExpr) {
    this.customFieldExpr = customFieldExpr;
  }

  @Override
  @Column(name = "custom_field_default_value", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getCustomFieldDefaultValueStr() {
    return customFieldDefaultValueStr;
  }

  public void setCustomFieldDefaultValueStr(String customFieldDefaultValueStr) {
    this.customFieldDefaultValueStr = customFieldDefaultValueStr;
    this.customFieldDefaultValue = null;
  }

  @Override
  @Transient
  public Value<?> getCustomFieldDefaultValue() {
    if (customFieldDefaultValue == null) {
      customFieldDefaultValue = convertDefaulValueStrToValue();
    }

    return customFieldDefaultValue;
  }

  @Transient
  @Override
  @XmlElement(name = "fieldCalculationType")
  public RecordFieldCalculationType getFieldCalculationType() {
    return fieldCalculationType;
  }

  public void setFieldCalculationType(RecordFieldCalculationType fieldCalculationType) {
    this.fieldCalculationType = fieldCalculationType;
  }

  @Override
  @Transient
  public boolean getIsQueryTimeCustomField() {
    return this.fieldCalculationType == RecordFieldCalculationType.QUERY_TIME;
  }

  @Column(name = "field_calculation_type", nullable = false)
  private Byte getFieldCalculationTypeByte() {
    if (fieldCalculationType == null) {
      return isCustomField ?
          (byte) RecordFieldCalculationType.WRITE_TIME.ordinal() :
          (byte) RecordFieldCalculationType.NA.ordinal();
    }

    return (byte) fieldCalculationType.ordinal();
  }

  private void setFieldCalculationTypeByte(Byte type) {
    RecordFieldCalculationType recordFieldCalculationType = RecordFieldCalculationType.NA;

    if (type == 1) {
      recordFieldCalculationType = RecordFieldCalculationType.WRITE_TIME;
    } else if (type == 2) {
      recordFieldCalculationType = RecordFieldCalculationType.QUERY_TIME;
    }
    setFieldCalculationType(recordFieldCalculationType);
  }

  @Transient
  @Override
  @XmlElement(name = "fieldTemplateType")
  public RecordFieldTemplateType getFieldTemplateType() {
    return fieldTemplateType;
  }

  public void setFieldTemplateType(RecordFieldTemplateType fieldTemplateType) {
    this.fieldTemplateType = fieldTemplateType;
  }

  @Column(name = "field_template_type", nullable = false)
  private Byte getFieldTemplateTypeByte() {
    if (fieldTemplateType == null) {
      if (fieldCalculationType == RecordFieldCalculationType.QUERY_TIME) {
        return (byte) RecordFieldTemplateType.AGGREGATION.ordinal();
      }
      return (byte) RecordFieldTemplateType.NA.ordinal();
    }
    return (byte) fieldTemplateType.ordinal();
  }

  private void setFieldTemplateTypeByte(Byte type) {
    RecordFieldTemplateType recordFieldTemplateType = RecordFieldTemplateType.NA;
    if (type == 1) {
      recordFieldTemplateType = RecordFieldTemplateType.AGGREGATION;
    } else if (type == 2) {
      recordFieldTemplateType = RecordFieldTemplateType.DATE_DIFF;
    } else if (type == 3) {
      recordFieldTemplateType = RecordFieldTemplateType.DEFAULT_VALUE;
    }
    setFieldTemplateType(recordFieldTemplateType);
  }

  private Value<?> convertDefaulValueStrToValue() {
    if (customFieldDefaultValueStr != null) {
      try {
        return com.appiancorp.core.expr.portable.Type.getType(type).fromJsonValue(customFieldDefaultValueStr);
      } catch (IOException e) {
        LOG.error("Unable to deserialize custom field default value from JSON", e);
      }
    }

    // Ensure the underlying value is null instead of one of K's default values to ensure this value plays
    // nice with ADS
    return com.appiancorp.core.expr.portable.Type.getType(type).valueOf(null);
  }

  @Lob
  @Override
  @Column(name = "rfs_membership_filter", length = Constants.COL_MAXLEN_EXPRESSION)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_ROW_LEVEL_SECURITY_CFG_DATA)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordFieldSecurity)
  public String getRecordFieldSecurityMembershipFilter() {
    return this.recordFieldSecurityMembershipFilter;
  }

  @Override
  @Column(name = "field_format")
  @XmlElement(name = "fieldFormat")
  public String getFieldFormat() {
    return this.fieldFormat;
  }

  public void setFieldFormat(String fieldFormat) {
    this.fieldFormat = fieldFormat;
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "field_id", nullable = false, referencedColumnName = "id")
  @OrderBy("metaDataType ASC")
  @XmlElement(name = "fieldMetaData")
  @HasForeignKeys(breadcrumb=BreadcrumbText.SKIP)
  public Set<RecordSourceFieldMetaDataCfg> getFieldMetaData() {
    return fieldMetaData;
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "source_field_id", nullable = false, referencedColumnName = "id")
  @XmlElement(name = "recordFieldAttributes")
  public Set<RecordFieldAttribute> getRecordFieldAttributes() {
    return recordFieldAttributes;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordFieldAttribute> getFieldAttributes() {
    return getRecordFieldAttributes() != null ? ImmutableList.copyOf(getRecordFieldAttributes()) : ImmutableList.of();
  }

  @Override
  @Transient
  public List<ReadOnlyRecordSourceFieldMetaData> getMetaData() {
    return getFieldMetaData() != null ? ImmutableList.copyOf(getFieldMetaData()) : ImmutableList.of();
  }

  public void setFieldMetaData(Set<RecordSourceFieldMetaDataCfg> recordSourceFieldMetaDataCfg) {
    this.fieldMetaData = recordSourceFieldMetaDataCfg;
  }

  @Override
  @Column(name = "is_indexable", nullable = false)
  @XmlElement(name = "isIndexable")
  public boolean getIsIndexable() {
    return isIndexable;
  }

  public void setIsIndexable(boolean isIndexable) {
    this.isIndexable = isIndexable;
  }

  public void setRecordFieldSecurityMembershipFilter(String recordFieldSecurityMembershipFilter) {
    this.recordFieldSecurityMembershipFilter = recordFieldSecurityMembershipFilter;
  }

  public void setRecordFieldAttributes(Set<RecordFieldAttribute> recordFieldAttributes) {
    this.recordFieldAttributes = recordFieldAttributes;
  }

  @Override
  public boolean hasAttribute(FieldAttribute attribute) {
    return getFieldAttributes().stream().anyMatch(attr -> attribute.name().equals(attr.getAttributeType()));
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

  @Transient
  @Override
  @XmlElement(name = "subType")
  public RecordFieldSubtype getSubType() {
    return subType;
  }

  public void setSubType(RecordFieldSubtype subType) {
    this.subType = subType;
  }

  @Column(name="sub_type", nullable = false)
  private byte getSubTypeByte() {
    if (subType == null) {
      return (byte) RecordFieldSubtype.NA.ordinal();
    }
    return (byte) subType.ordinal();
  }

  private void setSubTypeByte(byte subTypeByte) {
    RecordFieldSubtype newSubType = RecordFieldSubtype.NA;

    if (subTypeByte == 1) {
      newSubType = RecordFieldSubtype.XL_TEXT;
    }

    setSubType(newSubType);
  }

  /**
   * @param typedValue encode this
   * @return a Json encoding of <tt>typedValue</tt> as an Appian Value.
   */
  private String encodeAsJson(PortableTypedValue typedValue) {
    if (typedValue == null) {
      return null;
    }
    Value<?> value = API.typedValueToValue(typedValue);
    final String encoded = value.toJson();
    return encoded;
  }

  @Override
  public boolean hasSameType(ReadOnlyRecordSourceField field) {
    if (this == field) {
      return true;
    }

    if (!(field instanceof RecordSourceFieldCfg)) {
      return false;
    }

    RecordSourceFieldCfg that = (RecordSourceFieldCfg)field;

    return Objects.equals(this.getType(), that.getType());
  }

  /**
   * Only returns a meaningful number when the {@link this#sourceFieldType} has a pattern of two parentheses
   * with ONLY a number between them. This is the case for string types, ex. "VARCHAR(255)" or "LONGVARCHAR(4000)",
   * for example. If such a pattern does not exist, then 0 will be returned by default
   *
   * @return the size of the field as an integer, extracted as explained above
   */
  @Transient
  @XmlTransient
  @Override
  public int getSourceFieldTypeLength() {
    if (sourceFieldTypeLength != null) {
      return sourceFieldTypeLength;
    }
    if (sourceFieldType == null) {
      return 0;
    }

    sourceFieldTypeLength = 0;
    Matcher matcher = SOURCE_FIELD_TYPE_LENGTH_PATTERN.matcher(sourceFieldType);
    if (matcher.find()) {
      String fieldLengthString = matcher.group(1);
      sourceFieldTypeLength = Integer.parseInt(fieldLengthString);
    }

    return sourceFieldTypeLength;
  }

  @Transient
  @XmlTransient
  private Set<String> getAttributeTypeList() {
    if (getRecordFieldAttributes() == null) {
      return Collections.emptySet();
    }
    return getRecordFieldAttributes().
        stream().map(RecordFieldAttribute::getAttributeType).collect(Collectors.toSet());
  }

  @Override
  public boolean hasSameCustomFieldExpr(ReadOnlyRecordSourceField field) {
    if (this == field) {
      return true;
    }

    if (!(field instanceof RecordSourceFieldCfg)) {
      return false;
    }

    RecordSourceFieldCfg that = (RecordSourceFieldCfg)field;
    return Objects.equals(this.getCustomFieldExpr(), that.getCustomFieldExpr());
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordSourceFieldCfg)) {
      return false;
    }

    RecordSourceFieldCfg that = (RecordSourceFieldCfg)o;
    return new EqualsBuilder().append(isRecordId, that.getIsRecordId())
        .append(isUnique, that.getIsUnique())
        .append(id, that.id)
        .append(uuid, that.uuid)
        .append(type, that.type)
        .append(sourceFieldName, that.sourceFieldName)
        .append(sourceFieldType, that.sourceFieldType)
        .append(fieldName, that.fieldName)
        .append(displayName, that.displayName)
        .append(description, that.description)
        .append(isCustomField, that.isCustomField)
        .append(customFieldExpr, that.customFieldExpr)
        .append(customFieldDefaultValueStr, that.customFieldDefaultValueStr)
        .append(fieldCalculationType, that.fieldCalculationType)
        .append(fieldTemplateType, that.fieldTemplateType)
        .append(recordFieldSecurityMembershipFilter, that.recordFieldSecurityMembershipFilter)
        .append(fieldFormat, that.fieldFormat)
        //Hibernate returning FieldMetadata as persitentbag. Equalsverifier is used the testcase to check equals and hashcode contract.
        // Because Persistent bag doesn't respect the equals/hashcode contract it's reinitialized as an arraylist.
        .append(fieldMetaData == null ? Lists.newArrayList() : Lists.newArrayList(fieldMetaData),
            that.fieldMetaData == null ? Lists.newArrayList() : Lists.newArrayList(that.fieldMetaData))
        .append(recordFieldAttributes == null ? Lists.newArrayList() : Lists.newArrayList(recordFieldAttributes),
            that.recordFieldAttributes == null ? Lists.newArrayList() : Lists.newArrayList(that.recordFieldAttributes))
        .append(isIndexable, that.isIndexable)
        .append(subType, that.subType)
        .append(purposeInProduct, that.purposeInProduct)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, uuid, type, sourceFieldName, sourceFieldType, fieldName, displayName, description,
        isRecordId, isUnique, isCustomField, customFieldExpr, customFieldDefaultValueStr,
        fieldCalculationType, fieldTemplateType, recordFieldSecurityMembershipFilter, fieldFormat,
        fieldMetaData, isIndexable, subType, purposeInProduct, getAttributeTypeList());
  }

  protected final Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

}
