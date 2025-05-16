package com.appiancorp.record.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.record.sources.ReadOnlyRecordSource;
import com.appiancorp.record.sources.ReadOnlyRecordSourceField;
import com.appiancorp.record.sources.RecordSourceSubType;
import com.appiancorp.record.sources.RecordSourceType;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordSourceCfg;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/*
 * Record source metadata. Includes the type and UUID of the source, as well as a designer configured friendly
 * name. Has a foreign key to associate the source back to the record type that it contributes to.
 */
@Hidden
@Entity
@Table(name = "record_type_sources")
@IgnoreJpa
@XmlAccessorType
@XmlRootElement(name = "recordSourceCfg", namespace = Type.APPIAN_NAMESPACE)
// Specify RecordSourceCfgBinder as a customer binder
@ComplexForeignKey(nullable=true, breadcrumb = BreadcrumbText.SKIP)
@ForeignKeyCustomBinder(CustomBinderType.RECORD_SOURCE_CFG)
@XmlType(
    name = "RecordSourceCfg",
    namespace = Type.APPIAN_NAMESPACE,
    propOrder = {
        "sourceUuid", "sourceType", "sourceSubType", "sourceContextExpr", "friendlyName", "sourceFilterExpr", "sourceAndCustomFields",
        "uuid", "refreshSchedule", "incrementalRefreshSchedule", "readRateLimit", "skipFailureEnabled", "recordIdGeneratorUuid"
    })
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class RecordSourceCfg implements Id<Long>, ReadOnlyRecordSource {
  private static final long serialVersionUID = 1L;
  public static final String PROP_READ_RATE_LIMIT = "readRateLimit";
  public static final String PROP_SKIP_FAILURE_ENABLED = "skipFailureEnabled";
  public static final String PROP_SOURCE_TYPE = "sourceTypeByte";
  public static final String PROP_SOURCE_SUBTYPE = "sourceSubTypeByte";
  public static final String PROP_RECORD_TYPE_ID = "recordTypeId";
  public static final String PROP_SOURCE_UUID = "sourceUuid";
  public static final String PROP_FRIENDLY_NAME = "friendlyName";
  public static final String COLUMN_RECORD_ID_GENERATOR_UUID = "record_id_generator_uuid";

  private Long id;
  private String uuid;
  private Long recordTypeId;
  private RecordSourceType sourceType = RecordSourceType.RDBMS_TABLE;
  private RecordSourceSubType sourceSubType = RecordSourceSubType.NONE;
  private String sourceUuid;
  private String sourceContextExpr;
  private String friendlyName;
  private String sourceFilterExpr;

  private List<RecordSourceFieldCfg> sourceAndCustomFields = new ArrayList<>();
  private RecordSourceRefreshScheduleCfg refreshSchedule;
  private RecordSourceRefreshScheduleCfg incrementalRefreshSchedule;
  private Double rateLimitReadsPerSecond;
  private boolean skipFailureEnabled;
  private String recordIdGeneratorUuid;

  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;

  public RecordSourceCfg() {}

  public RecordSourceCfg(DesignerDtoRecordSourceCfg dto) {
    this.id = dto.getId();
    if (StringUtils.isNotEmpty(dto.getSourceType())) {
      this.sourceType = RecordSourceType.valueOf(dto.getSourceType());
    }
    if (StringUtils.isNotEmpty(dto.getSourceSubType())) {
      this.sourceSubType = RecordSourceSubType.valueOf(dto.getSourceSubType());
    }
    this.sourceUuid = dto.getSourceUuid();
    this.sourceContextExpr = dto.getSourceContextExpr();
    this.uuid = dto.getUuid();
    this.friendlyName = dto.getFriendlyName();
    this.sourceFilterExpr = dto.getSourceFilterExpr();
    this.expressionTransformationState = dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY;
    this.sourceAndCustomFields = dto.getSourceAndCustomFields()
        .stream()
        .map(RecordSourceFieldCfg::new)
        .collect(Collectors.toList());

    /* This check exists to be backwards compatible with a version released to internal customers
    with record sources with non-null refresh schedules that weren't populated. */
    if (dto.getRefreshSchedule() != null && dto.getRefreshSchedule().isActivated() != null) {
      this.refreshSchedule = new RecordSourceRefreshScheduleCfg(dto.getRefreshSchedule());
    }
    if (dto.getIncrementalRefreshSchedule() != null && dto.getIncrementalRefreshSchedule().isActivated() != null) {
      this.incrementalRefreshSchedule = new RecordSourceRefreshScheduleCfg(dto.getIncrementalRefreshSchedule());
    }
    this.rateLimitReadsPerSecond = dto.getReadRateLimit();
    this.skipFailureEnabled = dto.isSkipFailureEnabled();
    this.recordIdGeneratorUuid = dto.getRecordIdGeneratorUuid();
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

  @Override
  @Column(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @XmlTransient
  @Column(name = "record_type_id", insertable = false, updatable = false)
  public Long getRecordTypeId() {
    return recordTypeId;
  }

  private void setRecordTypeId(Long recordTypeId) {
    this.recordTypeId = recordTypeId;
  }

  @Column(name = "source_type")
  private byte getSourceTypeByte() {
    return sourceType.getCode();
  }

  private void setSourceTypeByte(byte sourceTypeCode) {
    sourceType = RecordSourceType.getByCode(sourceTypeCode);
  }

  @Override
  @Transient
  public RecordSourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(RecordSourceType type) {
    this.sourceType = type;
  }

  @Column(name = "source_sub_type")
  private byte getSourceSubTypeByte() {
    return sourceSubType.getCode();
  }

  private void setSourceSubTypeByte(byte sourceSubTypeCode) {
    sourceSubType = RecordSourceSubType.getByCode(sourceSubTypeCode);
  }

  @Override
  @Transient
  public RecordSourceSubType getSourceSubType() {
    return sourceSubType;
  }

  public void setSourceSubType(RecordSourceSubType subType) {
    this.sourceSubType = subType;
  }

  @Override
  @Column(name = "source_uuid")
  public String getSourceUuid() {
    return sourceUuid;
  }

  public void setSourceUuid(String sourceUuid) {
    this.sourceUuid = sourceUuid;
  }

  @Override
  @Lob
  @Column(name = "source_context_expr")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeSourceContextExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION_NO_INDEX)
  public String getSourceContextExpr() {
    return sourceContextExpr;
  }

  public void setSourceContextExpr(String sourceContextExpr) {
    this.sourceContextExpr = sourceContextExpr;
  }

  @Override
  @Column(name = "friendly_name")
  public String getFriendlyName() {
    return friendlyName;
  }

  public void setFriendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
  }

  @Override
  @Lob
  @Column(name = "source_filter_expr")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeSourceFilterExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getSourceFilterExpr() {
    return sourceFilterExpr;
  }

  public void setSourceFilterExpr(String sourceFilterExpr) {
    this.sourceFilterExpr = sourceFilterExpr;
  }

  /**
   * Returns the record fields that map to source properties as well as custom fields
   */
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "source_id", nullable = false)
  @OrderColumn(name = "order_idx", nullable = false)
  @XmlElement(name = "field")
  @Column(name = "fields")
  @HasForeignKeys(breadcrumb = BreadcrumbText.recordTypeDataModel)
  public List<RecordSourceFieldCfg> getSourceAndCustomFields() {
    return sourceAndCustomFields;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordSourceField> getSourceAndCustomFieldsReadOnly() {
    return ImmutableList.copyOf(getSourceAndCustomFields());
  }

  public void setSourceAndCustomFields(List<RecordSourceFieldCfg> sourceAndCustomFields) {
    this.sourceAndCustomFields = sourceAndCustomFields;
  }

  @Transient
  @XmlTransient
  public List<RecordSourceFieldCfg> getSourceFields() {
    return sourceAndCustomFields.stream()
        .filter(field -> !field.getIsCustomField())
        .collect(Collectors.toList());
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordSourceField> getSourceFieldsReadOnly() {
    return ImmutableList.copyOf(getSourceFields());
  }

  @XmlTransient
  @Transient
  public List<RecordSourceFieldCfg> getCustomFields() {
    return sourceAndCustomFields.stream()
        .filter(ReadOnlyRecordSourceField::getIsCustomField)
        .collect(Collectors.toList());
  }

  @Override
  @XmlTransient
  @Transient
  public ImmutableList<ReadOnlyRecordSourceField> getCustomFieldsReadOnly() {
    return ImmutableList.copyOf(getCustomFields());
  }

  @Override
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "refresh_schedule_id")
  public RecordSourceRefreshScheduleCfg getRefreshSchedule() {
    return refreshSchedule;
  }

  public void setRefreshSchedule(RecordSourceRefreshScheduleCfg refreshSchedule) {
    this.refreshSchedule = refreshSchedule;
  }

  @Override
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "incremental_refresh_sched_id", nullable = true)
  public RecordSourceRefreshScheduleCfg getIncrementalRefreshSchedule() {
    return incrementalRefreshSchedule;
  }

  public void setIncrementalRefreshSchedule(RecordSourceRefreshScheduleCfg incrementalRefreshSchedule) {
    this.incrementalRefreshSchedule = incrementalRefreshSchedule;
  }

  @Override
  @Column(name = "read_rate_limit")
  public Double getReadRateLimit() {
    return rateLimitReadsPerSecond;
  }

  public void setReadRateLimit(Double rateLimitReadsPerSecond) {
    this.rateLimitReadsPerSecond = rateLimitReadsPerSecond;
  }

  @Override
  @Column(name = "skip_failure_enabled")
  public boolean isSkipFailureEnabled() {
    return skipFailureEnabled;
  }

  public void setSkipFailureEnabled(boolean skipFailureEnabled) {
    this.skipFailureEnabled = skipFailureEnabled;
  }

  @Override
  @Column(name = "record_id_generator_uuid")
  public String getRecordIdGeneratorUuid() {
    return recordIdGeneratorUuid;
  }

  public void setRecordIdGeneratorUuid(String recordIdGeneratorUuid) {
    this.recordIdGeneratorUuid = recordIdGeneratorUuid;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<String> getSourceFieldNamesReadOnly() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (ReadOnlyRecordSourceField field : sourceAndCustomFields) {
      if (!field.getIsCustomField()) {
        builder.add(field.getSourceFieldName());
      }
    }
    return builder.build();
  }

  @Override
  @Transient
  @XmlTransient
  public ReadOnlyRecordSourceField getRecordIdSourceField() {
    return sourceAndCustomFields.stream()
        .filter(field -> field.getIsRecordId())
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Record Id not present on record source config"));
  }

  @Override
  @Transient
  @XmlTransient
  public String getRecordIdSourceFieldName() {
    return getRecordIdSourceField().getSourceFieldName();
  }

  @Transient
  @XmlTransient
  @Override
  public ExpressionTransformationState getExpressionTransformationState() {
    return expressionTransformationState;
  }

  @Override
  @Transient
  public boolean supportsIncrementalSync() {
    return (sourceType == RecordSourceType.EXPRESSION || sourceType == RecordSourceType.RDBMS_TABLE) &&
        this.sourceSubType == RecordSourceSubType.NONE;
  }

  public void setExpressionTransformationState(ExpressionTransformationState state) {
    this.expressionTransformationState = state;
  }

  @Transient
  @Override
  public ReadOnlyRecordSource copyWithNewSourceAndCustomFields(List<ReadOnlyRecordSourceField> sourceAndCustomFields) {
    RecordSourceCfg recordSourceCfg = new RecordSourceCfg();
    recordSourceCfg.id = this.id;
    recordSourceCfg.uuid = this.uuid;
    recordSourceCfg.recordTypeId = this.recordTypeId;
    recordSourceCfg.sourceType = this.sourceType;
    recordSourceCfg.sourceSubType = this.sourceSubType;
    recordSourceCfg.sourceUuid = this.sourceUuid;
    recordSourceCfg.sourceContextExpr = this.sourceContextExpr;
    recordSourceCfg.friendlyName = this.friendlyName;
    recordSourceCfg.sourceFilterExpr = this.sourceFilterExpr;
    recordSourceCfg.expressionTransformationState = this.expressionTransformationState;

    recordSourceCfg.sourceAndCustomFields = sourceAndCustomFields.stream().map(RecordSourceFieldCfg::new).toList();
    recordSourceCfg.refreshSchedule = new RecordSourceRefreshScheduleCfg(this.refreshSchedule);
    if (this.incrementalRefreshSchedule != null) {
      recordSourceCfg.incrementalRefreshSchedule = new RecordSourceRefreshScheduleCfg(this.incrementalRefreshSchedule);
    }
    recordSourceCfg.rateLimitReadsPerSecond = this.rateLimitReadsPerSecond;
    recordSourceCfg.skipFailureEnabled = this.skipFailureEnabled;
    recordSourceCfg.recordIdGeneratorUuid = this.recordIdGeneratorUuid;
    return recordSourceCfg;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordSourceCfg)) {
      return false;
    }

    RecordSourceCfg that = (RecordSourceCfg)o;

    return new EqualsBuilder().append(id, that.getId())
        .append(uuid, that.uuid)
        .append(sourceType, that.sourceType)
        .append(sourceSubType, that.sourceSubType)
        .append(recordTypeId, that.recordTypeId)
        .append(sourceUuid, that.sourceUuid)
        .append(sourceContextExpr, that.sourceContextExpr)
        .append(friendlyName, that.friendlyName)
        .append(sourceFilterExpr, that.sourceFilterExpr)
        .append(sourceAndCustomFields, that.sourceAndCustomFields)
        .append(refreshSchedule, that.refreshSchedule)
        .append(incrementalRefreshSchedule, that.incrementalRefreshSchedule)
        .append(rateLimitReadsPerSecond, that.rateLimitReadsPerSecond)
        .append(skipFailureEnabled, that.skipFailureEnabled)
        .append(recordIdGeneratorUuid, that.recordIdGeneratorUuid)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(
        id,
        uuid,
        sourceType,
        sourceSubType,
        recordTypeId,
        sourceUuid,
        sourceContextExpr,
        friendlyName,
        sourceFilterExpr,
        sourceAndCustomFields,
        refreshSchedule,
        incrementalRefreshSchedule,
        rateLimitReadsPerSecond,
        skipFailureEnabled,
        recordIdGeneratorUuid
    );
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

}
