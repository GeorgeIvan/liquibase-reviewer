package com.appiancorp.designguidance.entities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.core.expr.Parse;
import com.appiancorp.core.expr.ParseFactory;
import com.appiancorp.core.expr.TypeTransformation;
import com.appiancorp.core.expr.exceptions.ScriptException;
import com.appiancorp.core.expr.fn.ref.Devariant;
import com.appiancorp.core.expr.portable.ConvertToAppianExpression;
import com.appiancorp.core.expr.portable.Type;
import com.appiancorp.core.expr.portable.Value;
import com.appiancorp.core.expr.portable.environment.EvaluationEnvironment;
import com.appiancorp.core.expr.portable.string.Strings;
import com.google.common.base.MoreObjects;
import com.google.gson.annotations.Expose;

/**
 * An entity class to store design guidance in RDBMS.
 */
@Entity
@Table(name = DesignGuidanceEntity.DESIGN_GUIDANCE_OBJECT_TABLE_NAME)
public class DesignGuidanceEntity implements DesignGuidance<Value, DesignGuidanceObjectInfoEntity> {
  public static final int SIMPLIFY_EXPRESSION_MASK = ConvertToAppianExpression.TO_EXPRESSION_SIMPLIFY_CDT |
      ConvertToAppianExpression.TO_EXPRESSION_SIMPLIFY_DICTIONARY |
      ConvertToAppianExpression.TO_EXPRESSION_READABLE_VARIANT |
      ConvertToAppianExpression.TO_EXPRESSION_SIMPLIFY_ENUM |
      ConvertToAppianExpression.TO_EXPRESSION_SIMPLIFY_MAP |
      ConvertToAppianExpression.TO_EXPRESSION_SIMPLIFY_RECORD_MAP |
      ConvertToAppianExpression.TO_EXPRESSION_PARSABLE_EXPRESSION_TYPE;

  private Long id;
  @Expose private String objectUuid;
  @Expose private String designGuidanceKey;
  private Long objectTypeId;
  private String metaData;
  @Expose private boolean dismissed;
  private boolean deactivated;
  private boolean fromInitialLoad;
  @Expose private int instanceCount;
  private boolean hidden;
  private GuidanceType guidanceType;
  private String[] messageParameters;
  private DesignGuidanceObjectInfoEntity objectInfo;
  @Expose private String objectTypeName;
  //Setting int type so that we can do max(priority) in projection. Postgres doesn't allow max(boolean) function
  //0: low priority, 1: high priority
  private int priority;

  public static final String DESIGN_GUIDANCE_OBJECT_TABLE_NAME = "dg_object";
  public static final String PROP_ID = "id";
  public static final String PROP_OBJECT_UUID = "objectUuid";
  public static final String PROP_DESIGN_GUIDANCE_KEY = "designGuidanceKey";
  public static final String PROP_OBJECT_TYPE_ID = "objectTypeId";
  public static final String PROP_META_DATA = "metaData";
  public static final String PROP_DISMISSED = "dismissed";
  public static final String PROP_DEACTIVATED = "deactivated";
  public static final String PROP_FROM_INITIAL_LOAD = "fromInitialLoad";
  public static final String PROP_INSTANCE_COUNT = "instanceCount";
  public static final String PROP_HIDDEN = "hidden";
  public static final String PROP_GUIDANCE_TYPE = "guidanceType";
  public static final String PROP_GUIDANCE_TYPE_COLUMN_NAME = "guidance_type";
  public static final String PROP_OBJECT_INFO = "objectInfo";
  public static final String PROP_OBJECT_INFO_FK_COLUMN_NAME = "object_info_id";
  public static final String PROP_OBJECT_TYPE_NAME = "objectTypeName";
  public static final String PROP_PRIORITY = "priority";

  public DesignGuidanceEntity() {
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Override
  @Column(name = "object_uuid", nullable = false)
  public String getObjectUuid() {
    return objectUuid;
  }

  @Override
  @Column(name = "design_guidance_key", nullable = false)
  public String getDesignGuidanceKey() {
    return designGuidanceKey;
  }

  @Override
  @Column(name = "object_type_id", nullable = false)
  public Long getObjectTypeId() {
    if(!Objects.isNull(this.objectTypeName)){
      this.objectTypeId = com.appiancorp.core.expr.portable.Type.getType(objectTypeName).getTypeId();
    }
    return objectTypeId;
  }

  @Override
  @Transient
  public String getMetaData() {
    return metaData;
  }

  @Override
  @Column(name=PROP_DISMISSED, nullable = false)
  public boolean isDismissed() {
    return dismissed;
  }

  @Override
  @Column(name=PROP_DEACTIVATED, nullable = false)
  public boolean isDeactivated() {
    return deactivated;
  }

  @Override
  @Column(name="from_initial_load", nullable = false)
  public boolean isFromInitialLoad() {
    return fromInitialLoad;
  }

  @Override
  @Column(name="instance_count", nullable = false)
  public int getInstanceCount() {
    return instanceCount;
  }

  @Override
  @Transient
  public boolean isHidden() {
    return hidden;
  }

  @Override
  @Enumerated(EnumType.STRING)
  @Column(name = PROP_GUIDANCE_TYPE_COLUMN_NAME, nullable = false)
  public GuidanceType getGuidanceType() {
    return guidanceType;
  }

  //We have to set this to be nullable because design guidance can have null objectInfo before migration
  @Override
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = PROP_OBJECT_INFO_FK_COLUMN_NAME, nullable = true)
  public DesignGuidanceObjectInfoEntity getObjectInfo() {
    return objectInfo;
  }

  @Override
  @Column(name="priority", nullable = false)
  public int getPriority() {
    return priority;
  }

  @Transient
  public RecommendationSeverity getRecommendationSeverity() {
    return RecommendationSeverity.getRecommendationSeverity(getPriority());
  }

  /**
   * {@return the metadata as a Devarianted {@link Value}}
   */
  @Transient
  public Value getMetaDataValue() {
    try {
      return convertFromAppianExpression(getMetaData());
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Transient
  public String[] getMessageParameters() {
    if (messageParameters == null) {
      return new String[0];
    }
    return Arrays.copyOf(messageParameters, messageParameters.length);
  }

  @Override
  @Transient
  public String getObjectQName(){
    return this.objectTypeName;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public void setObjectUuid(String objectUuid) {
    this.objectUuid = objectUuid;
  }

  @Override
  public void setDesignGuidanceKey(String designGuidanceKey) {
    this.designGuidanceKey = designGuidanceKey;
  }

  @Override
  public void setObjectTypeId(Long objectTypeId) {
    this.objectTypeId = objectTypeId;
    if ( !Objects.isNull(this.objectTypeId)){
      this.objectTypeName = com.appiancorp.core.expr.portable.Type.getType(this.objectTypeId).getQNameAsString();
    }
  }

  @Override
  public void setMetaData(String metaData) {
    this.metaData = metaData;
  }

  @Override
  public void setMetaData(Value value) {
    this.metaData = convertToAppianExpression(value);
  }

  @Override
  public void setDismissed(boolean dismissed) {
    this.dismissed = dismissed;
  }

  @Override
  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }

  @Override
  public void setFromInitialLoad(boolean fromInitialLoad) {
    this.fromInitialLoad = fromInitialLoad;
  }

  @Override
  public void setInstanceCount(int instanceCount) {
    this.instanceCount = instanceCount;
  }

  @Override
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  @Override
  public void setGuidanceType(GuidanceType guidanceType) {
    this.guidanceType = guidanceType;
  }

  @Override
  public void setMessageParameters(String[] messageParameters) {
    if (messageParameters != null) {
      this.messageParameters = Arrays.copyOf(messageParameters, messageParameters.length);
    }
  }

  @Override
  public void setObjectInfo(DesignGuidanceObjectInfoEntity objectInfo)  {
    this.objectInfo = objectInfo;
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public void setRecommendationSeverity(RecommendationSeverity recommendationSeverity) {
    setPriority(recommendationSeverity.getPriorityValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignGuidanceEntity that = (DesignGuidanceEntity)o;
    return new EqualsBuilder().append(objectUuid, that.objectUuid)
        .append(designGuidanceKey, that.designGuidanceKey)
        .append(objectTypeId, that.objectTypeId)
        .append(metaData, that.metaData)
        .append(dismissed, that.dismissed)
        .append(deactivated, that.deactivated)
        .append(fromInitialLoad, that.fromInitialLoad)
        .append(instanceCount, that.instanceCount)
        .append(hidden, that.hidden)
        .append(guidanceType, that.guidanceType)
        //we only need to care if the design guidance has the correct object info ID, since that's what is stored in RDBMS
        .append(objectInfo == null ? null : objectInfo.getId(),
            that.objectInfo == null ? null : that.objectInfo.getId())
        .append(priority, that.priority)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUuid, designGuidanceKey, objectTypeId, metaData, dismissed, deactivated,
        fromInitialLoad, instanceCount, hidden, guidanceType, objectInfo, priority);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add(PROP_ID, id)
        .add(PROP_OBJECT_UUID, objectUuid)
        .add(PROP_DESIGN_GUIDANCE_KEY, designGuidanceKey)
        .add(PROP_OBJECT_TYPE_ID, objectTypeId)
        .add(PROP_META_DATA, metaData)
        .add(PROP_DISMISSED, dismissed)
        .add(PROP_DEACTIVATED, deactivated)
        .add(PROP_FROM_INITIAL_LOAD, fromInitialLoad)
        .add(PROP_INSTANCE_COUNT, instanceCount)
        .add(PROP_HIDDEN, hidden)
        .add(PROP_GUIDANCE_TYPE, guidanceType)
        .add(PROP_OBJECT_INFO, objectInfo)
        .add(PROP_OBJECT_TYPE_NAME, objectTypeName)
        .add(PROP_PRIORITY, priority)
        .toString();
  }

  public static String convertToAppianExpression(Value v) {
    String expr = ConvertToAppianExpression.of(Devariant.devariant(v), SIMPLIFY_EXPRESSION_MASK, null);
    return EvaluationEnvironment.getStrictExpressionTransformer()
        .toStoredForm(expr, new HashSet<>(),
            TypeTransformation.TYPE_NAMESPACE_TO_TYPE_ID_FALLING_BACK_TO_LATEST_DEACTIVATED_VERSION);
  }

  private static Value convertFromAppianExpression(String expression) throws ScriptException {
    String expr = EvaluationEnvironment.getStrictExpressionTransformer()
        .toRetrievedForm(expression,
            TypeTransformation.TYPE_ID_TO_TYPE_NAMESPACE_CURRENT_IF_LATEST_VERSION_MODE_ON_FOR_RULES);
    if (Strings.isBlank(expr)) {
      return Type.NULL.nullValue();
    }
    Parse parse = ParseFactory.create(expr);
    return parse.eval();
  }

  @Transient
  public TypeIdAndUuidPairEntity getTypeIdAndUuidPair() {
    return new TypeIdAndUuidPairEntity(this.getObjectTypeId(), this.getObjectUuid());
  }
}
