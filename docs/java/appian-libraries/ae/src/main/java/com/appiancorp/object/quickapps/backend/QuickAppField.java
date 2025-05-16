package com.appiancorp.object.quickapps.backend;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.appiancorp.core.expr.portable.cdt.HasInstructions;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

/**
 * QuickAppField is the bean that represents a single record field that a user can specify in a {@link QuickApp}. A QuickApp
 * can contain multiple QuickAppFields. A QuickAppField can contain multiple {@link QuickAppFieldConfig}
 **/
@Entity
@Table(name = "quickapp_fld")
public class QuickAppField implements Id<Long>, HasInstructions {
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_NAME = "name";
  public static final String PROP_INSTRUCTIONS = "instructions";
  public static final String PROP_TYPE = "byteType";
  public static final String PROP_REQUIRED = "required";
  public static final String PROP_HELP_TOOLTIP = "helpTooltip";
  public static final String PROP_PLACEHOLDER_TEXT = "placeholderText";
  public static final String PROP_FIELD_CONFIGS = "configs";

  private Long id;
  private String name;
  private String instructions;
  private String helpTooltip;
  private String placeholderText;
  private QuickAppFieldType type;
  private boolean required;
  private List<QuickAppFieldConfig> configs = new ArrayList<>();
  private String columnName;
  private String joinTableName;
  private String inverseJoinColumnName;
  private String lookupTableName;
  private boolean isDeleted;
  private QuickAppFieldCategory category;

  public QuickAppField(){}

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the enum type that describes the type of the field. e.g. Text, Paragraph, etc.
   */
  @Transient
  public QuickAppFieldType getType() {
    return type;
  }

  public void setType(QuickAppFieldType type) {
    this.type = type;
  }

  /* These methods will be used for hibernate */
  @Column(name = "type", nullable = false)
  private byte getTypeByte() {
    return type != null ? type.getCode() : QuickAppFieldType.TEXT.getCode();
  }

  private void setTypeByte(byte type) {
    setType(QuickAppFieldType.valueOf(type));
  }

  @Column(name = "instructions", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  @Column(name = "required", nullable = false)
  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  @Column(name = "help_tooltip", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getHelpTooltip() {
    return helpTooltip;
  }

  public void setHelpTooltip(String helpTooltip) {
    this.helpTooltip = helpTooltip;
  }

  @Column(name = "placeholder_text", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getPlaceholderText() {
    return placeholderText;
  }

  public void setPlaceholderText(String placeholderText) {
    this.placeholderText = placeholderText;
  }

  @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.LAZY, orphanRemoval=true)
  @JoinColumn(name = "quickapp_fld_id", nullable=false)
  @OrderColumn(name= "order_idx", nullable=false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<QuickAppFieldConfig> getConfigs() {
    return configs;
  }

  public void setConfigs(List<QuickAppFieldConfig> configs) {
    this.configs = configs;
  }

  /**
   * The column name to be used for this field. This will be used in either the @Column or the @JoinColumn annotation
   */
  @Column(name = "col_name", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  /**
   * For multi-select fields only. This name will be used for the join table to persist the ManyToMany
   * relationship. This name is stored with the prefix already attached.
   */
  @Column(name = "join_tbl_name", updatable = false, nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getJoinTableName() {
    return joinTableName;
  }

  public void setJoinTableName(String joinTableName) {
    this.joinTableName = joinTableName;
  }

  /**
   * For multi-select fields only. This column in the join table holds foreign keys to the main table ("main" CDT).
   */
  @Column(name = "inv_join_col_name", updatable = false, nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getInverseJoinColumnName() {
    return inverseJoinColumnName;
  }

  public void setInverseJoinColumnName(String inverseJoinColumnName) {
    this.inverseJoinColumnName = inverseJoinColumnName;
  }

  /**
   * Table name for the lookup table to be used for this field. This name is stored with the prefix already attached.
   */
  @Column(name = "lookup_tbl_name", updatable = false, nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getLookupTableName() {
    return this.lookupTableName;
  }

  public void setLookupTableName(String lookupTableName) {
    this.lookupTableName = lookupTableName;
  }

  /**
   * When the user deletes a field, rather than deleting the row, we mark it as deleted via this column. We
   * don't want to delete, so the old field information can be used to prevent table/column name collisions
   * for new fields that the user might add subsequently.
   */
  @Column(name = "is_deleted", updatable = true, nullable = false)
  public boolean isDeleted() {
    return this.isDeleted;
  }

  public void setDeleted(boolean active) {
    this.isDeleted = active;
  }

  @Transient
  public QuickAppFieldCategory getCategory() {
    return category;
  }

  public void setCategory(QuickAppFieldCategory category) {
    this.category = category;
  }

  @Column(name = "category", nullable = false)
  private byte getCategoryByte() {
    return category != null ? category.getIndex() : QuickAppFieldCategory.USER.getIndex();
  }

  private void setCategoryByte(byte index) {
    setCategory(QuickAppFieldCategory.valueOf(index));
  }

  /**
   *  Equivalent to implies name, instructions, type, requiredness,
   *    helpTooltips, placeholderText are equal.
   */
  public boolean equivalentTo(final QuickAppField field) {
    return EQUIVALENCE.equivalent(this, field);
  }

  /**
   * Equivalent to with configs implies:
   *  - fields on the QuickAppField are equal based on {@link #equivalentTo(QuickAppField)}
   *  - all QuickAppFieldConfigs on the beans are equivalent based on {@link QuickAppFieldConfig#equivalentTo(QuickAppFieldConfig)}
   */
  public boolean equivalentToWithConfigs(final QuickAppField field){
    boolean equivalent = equivalentTo(field);
    List<QuickAppFieldConfig> inputFields = field.getConfigs();

    if (equivalent && (configs.size() == inputFields.size())) {
      for (int i = 0; i < configs.size(); i++) {
        equivalent &= configs.get(i).equivalentTo(inputFields.get(i));
      }
    }
    return equivalent;
  }

  private static final Equivalence<QuickAppField> EQUIVALENCE = new Equivalence<QuickAppField>() {
    @Override
    protected boolean doEquivalent(QuickAppField firstQuickAppField, QuickAppField secondQuickAppField) {
      return Objects.equal(firstQuickAppField.getName(), secondQuickAppField.getName()) &&
        Objects.equal(firstQuickAppField.getInstructions(), secondQuickAppField.getInstructions()) &&
        Objects.equal(firstQuickAppField.getType(), secondQuickAppField.getType()) &&
        Objects.equal(firstQuickAppField.isRequired(), secondQuickAppField.isRequired()) &&
        Objects.equal(firstQuickAppField.getHelpTooltip(), secondQuickAppField.getHelpTooltip()) &&
        Objects.equal(firstQuickAppField.getPlaceholderText(), secondQuickAppField.getPlaceholderText()) &&
        Objects.equal(firstQuickAppField.getColumnName(), secondQuickAppField.getColumnName()) &&
        Objects.equal(firstQuickAppField.getJoinTableName(), secondQuickAppField.getJoinTableName()) &&
        Objects.equal(firstQuickAppField.getInverseJoinColumnName(), secondQuickAppField.getInverseJoinColumnName()) &&
        Objects.equal(firstQuickAppField.getLookupTableName(), secondQuickAppField.getLookupTableName()) &&
        Objects.equal(firstQuickAppField.isDeleted(), secondQuickAppField.isDeleted()) &&
        Objects.equal(firstQuickAppField.getCategory(), secondQuickAppField.getCategory());
    }

    @Override
    protected int doHash(QuickAppField quickAppField) {
      return Objects.hashCode(quickAppField.getName(), quickAppField.getInstructions(), quickAppField.getType(),
        quickAppField.isRequired(), quickAppField.getHelpTooltip(), quickAppField.getPlaceholderText(),
        quickAppField.getColumnName(), quickAppField.getJoinTableName(), quickAppField.getInverseJoinColumnName(),
        quickAppField.getLookupTableName(), quickAppField.isDeleted());
    }
  };

  @Override
  public String toString() {
    return "QuickAppField [id=" + id + ", name=" + name + ", type=" + type.getText() + "]";
  }
}
