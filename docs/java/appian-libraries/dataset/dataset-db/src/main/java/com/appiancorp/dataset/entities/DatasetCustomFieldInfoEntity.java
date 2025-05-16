package com.appiancorp.dataset.entities;

import static com.appiancorp.dataset.entities.DatasetFieldEntity.DATASET_FIELD_ID_COLUMN_NAME;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.cdt.DatasetCustomFieldTemplateType;
import com.appiancorp.dataset.DatasetCustomFieldInfo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Stores the definition specific for the Analyst Custom Field.
 * <p>The <tt>displayName</tt> and <tt>description</tt> fields are saved in {@link DatasetFieldEntity}
 * referencing this DatasetCustomFieldInfo.
 * </p>
 */
@Entity
@Table(name = "dataset_custom_field_info")
public final class DatasetCustomFieldInfoEntity implements DatasetCustomFieldInfo<DatasetFieldEntity> {
  public static final String PROP_FIELD_TEMPLATE_TYPE_BYTE = "fieldTemplateTypeByte";
  static final String DATASET_FIELD_NAME = "datasetField";
  private Long id;
  private String returnType;
  private String expression;
  private String defaultValue;
  private DatasetCustomFieldTemplateType fieldTemplateType;
  private DatasetFieldEntity datasetField;

  private DatasetCustomFieldInfoEntity() {
    // Needed for hibernate
  }

  /**
   * Construct {#link DatasetCustomFieldInfoEntity} from {@link DatasetCustomFieldInfoBuilder}.
   * This constructor does not set the {@link DatasetFieldEntity} because the cross-reference;
   * the {@link DatasetFieldEntity} must be explicitly set via {@link #setDatasetField(DatasetFieldEntity)}.
   *
   * @param id                the id of {#link DatasetCustomFieldInfoEntity}
   * @param returnType        the return type of the custom field expression
   * @param expression        the custom field expression
   * @param defaultValue      the default value of custom field
   * @param fieldTemplateType the type of custom field
   */
  private DatasetCustomFieldInfoEntity(
      Long id,
      String returnType,
      String expression,
      String defaultValue,
      DatasetCustomFieldTemplateType fieldTemplateType) {
    this.id = id;
    this.returnType = returnType;
    this.expression = expression;
    this.defaultValue = defaultValue;
    this.fieldTemplateType = fieldTemplateType;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * The direct call to this method should be avoided; the main purpose of this getter is support
   * the reverse relation to {@link DatasetFieldEntity} mirroring the foreign key relationship from
   * <tt>dataset_custom_field_info</tt> to <tt>dataset_field</tt> tables.
   * @return the {@link DatasetFieldEntity} owning <tt>this</tt>.
   */
  @Override
  @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "dataset_field_id", referencedColumnName = DATASET_FIELD_ID_COLUMN_NAME, nullable = false, updatable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public DatasetFieldEntity getDatasetField() {
    return datasetField;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDatasetField(DatasetFieldEntity datasetField) {
    this.datasetField = datasetField;
  }

  @Override
  @Column(name = "return_type", nullable = false, updatable = false)
  public String getReturnType() {
    return returnType;
  }

  @Override
  public void setReturnType(String return_type) {
    this.returnType = return_type;
  }

  @Override
  @Column(name = "expression", length = Constants.COL_MAXLEN_EXPRESSION, nullable = false)
  @Lob
  public String getExpression() {
    return expression;
  }

  @Override
  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  @Column(name = "default_value", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Column(name = "template_type", nullable = false)
  public Byte getFieldTemplateTypeByte() {
    if (fieldTemplateType == null) {
      return (byte)DatasetCustomFieldTemplateType.NA.ordinal();
    }
    return (byte)fieldTemplateType.ordinal();
  }

  public void setFieldTemplateTypeByte(Byte type) {
    DatasetCustomFieldTemplateType recordFieldTemplateType = DatasetCustomFieldTemplateType.NA;
    if (type >= 0 && type < DatasetCustomFieldTemplateType.values().length) {
      recordFieldTemplateType = DatasetCustomFieldTemplateType.values()[type];
    }
    this.fieldTemplateType = recordFieldTemplateType;
  }

  @Override
  @Transient
  public DatasetCustomFieldTemplateType getFieldTemplateType() {
    return fieldTemplateType == null ? DatasetCustomFieldTemplateType.NA : fieldTemplateType;
  }

  @Override
  @Transient
  public void setFieldTemplateType(DatasetCustomFieldTemplateType fieldTemplateType) {
    this.fieldTemplateType = fieldTemplateType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatasetCustomFieldInfoEntity that = (DatasetCustomFieldInfoEntity)o;
    return Objects.equals(returnType, that.returnType) &&
        Objects.equals(expression, that.expression) &&
        Objects.equals(defaultValue, that.defaultValue) &&
        fieldTemplateType == that.fieldTemplateType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(returnType, expression, defaultValue, fieldTemplateType);
  }

  @Override
  public String toString() {
    return "DatasetCustomFieldInfo [returnType='" + returnType + '\'' + ", expression='" +
        expression + '\'' + ", defaultValue='" + defaultValue + '\'' + ", fieldTemplateType=" +
        fieldTemplateType + ']';
  }

  public static class DatasetCustomFieldInfoBuilder {
    private Long id;
    private String returnType;
    private String expression;
    private String defaultValue;
    private DatasetCustomFieldTemplateType fieldTemplateType;

    public DatasetCustomFieldInfoBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public DatasetCustomFieldInfoBuilder returnType(String returnType) {
      this.returnType = returnType;
      return this;
    }

    public DatasetCustomFieldInfoBuilder expression(String expression) {
      this.expression = expression;
      return this;
    }

    public DatasetCustomFieldInfoBuilder defaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public DatasetCustomFieldInfoBuilder fieldTemplateType(DatasetCustomFieldTemplateType fieldTemplateType) {
      this.fieldTemplateType = fieldTemplateType;
      return this;
    }

    public DatasetCustomFieldInfoEntity build() {
      return new DatasetCustomFieldInfoEntity(id, returnType, expression, defaultValue, fieldTemplateType);
    }
  }
}
