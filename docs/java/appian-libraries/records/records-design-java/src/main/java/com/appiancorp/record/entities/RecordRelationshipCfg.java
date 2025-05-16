package com.appiancorp.record.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appian.core.persist.Constants;
import com.appiancorp.core.data.ImmutableDictionary;
import com.appiancorp.core.data.RecordField;
import com.appiancorp.core.expr.portable.Type;
import com.appiancorp.core.expr.portable.Value;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.record.relatedrecords.ReadOnlyRecordRelationship;
import com.appiancorp.record.relatedrecords.RelationshipOperator;
import com.appiancorp.record.relatedrecords.RelationshipType;
import com.appiancorp.record.relatedrecords.UpdateBehavior;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordRelationshipCfg;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.DataStoreEntityRefImpl;
import com.appiancorp.type.refs.RecordTypeRef;
import com.appiancorp.type.refs.RecordTypeRefImpl;
import com.appiancorp.type.refs.Ref;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@Hidden
@Entity
@Table(name = RecordRelationshipCfg.RELATIONSHIP_TABLE_NAME)
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordRelationshipCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RecordRelationshipCfg.LOCAL_PART,
    propOrder={"id", "uuid", "relationshipName", "targetRecordTypeUuid", "relationshipType", "relationshipData",
        "updateBehavior"})
public class RecordRelationshipCfg implements Id<Long>, ReadOnlyRecordRelationship {
  public static final String RELATIONSHIP_TABLE_NAME = "record_type_relationships";
  public static final String UUID_KEY = "uuid";
  public static final String NAME = "relationshipName";
  public static final String LOCAL_PART = "RecordRelationshipCfg";
  public static final String JOIN_TABLE_REF = "joinTableRef";
  public static final String SOURCE_RECORD_TYPE_ID = "sourceRecordTypeId";
  public static final String TARGET_RECORD_UUID = "targetRecordTypeUuid";
  public static final String RELATIONSHIP_TYPE = "relationshipType";
  public static final String RELATIONSHIP_TYPE_BYTE = "relationshipTypeByte";

  private static final String SOURCE_RECORD_FIELD_UUID = "sourceRecordTypeFieldUuid";
  private static final String TARGET_RECORD_FIELD_UUID = "targetRecordTypeFieldUuid";
  private static final String RELATIONSHIP_OPERATOR = "relationshipOperator";
  private static final String JOIN_TABLE_SOURCE_FIELD = "joinTableSourceField";
  private static final String JOIN_TABLE_TARGET_FIELD = "joinTableTargetField";
  public static final String SRC_RT_ID_COLUMN_NAME = "src_rt_id";
  public static final String RELATIONSHIP_TYPE_COLUMN_NAME = "relationship_type";
  public static final String UPDATE_BEHAVIOR_COLUMN_NAME = "update_behavior";

  private Long id;
  private String uuid;
  private String relationshipName;
  private Long sourceRecordTypeId;
  private String sourceRecordTypeFieldUuid;
  private String targetRecordTypeUuid;
  private String targetRecordTypeFieldUuid;
  private RelationshipType relationshipType = RelationshipType.MANY_TO_ONE;
  private RelationshipOperator relationshipOperator = RelationshipOperator.EQUALS;
  private String joinTableSourceField;
  private String joinTableTargetField;
  private Ref<String,String> joinTableRef;
  private UpdateBehavior updateBehavior = UpdateBehavior.NON_CASCADING;

  public RecordRelationshipCfg() {}

  public RecordRelationshipCfg(DesignerDtoRecordRelationshipCfg dto) {
    this.id = dto.getId();
    this.uuid = dto.getUuid();
    this.relationshipName = dto.getName();
    this.sourceRecordTypeId = dto.getSourceRecordTypeId();
    this.sourceRecordTypeFieldUuid = dto.getSourceRecordTypeFieldUuid();
    this.targetRecordTypeFieldUuid = dto.getTargetRecordTypeFieldUuid();
    this.targetRecordTypeUuid = dto.getTargetRecordTypeUuid();
    this.relationshipOperator = RelationshipOperator.fromText(dto.getRelationshipOperator());
    this.relationshipType = RelationshipType.fromText(dto.getRelationshipType());
    this.joinTableSourceField = dto.getJoinTableSourceField();
    this.joinTableTargetField = dto.getJoinTableTargetField();
    this.joinTableRef = setRefOnValidString(dto.getJoinTableRef());
    this.updateBehavior = UpdateBehavior.fromText(dto.getUpdateBehavior());
  }

  public RecordRelationshipCfg(ImmutableDictionary relationshipData) {
    this.uuid = extractString(relationshipData, UUID_KEY);
    this.relationshipName = extractString(relationshipData, NAME);
    this.sourceRecordTypeFieldUuid = extractString(relationshipData, SOURCE_RECORD_FIELD_UUID);
    this.targetRecordTypeFieldUuid = extractString(relationshipData, TARGET_RECORD_FIELD_UUID);
    this.targetRecordTypeUuid = extractString(relationshipData, TARGET_RECORD_UUID);
    this.relationshipOperator = RelationshipOperator.fromText(extractString(relationshipData, RELATIONSHIP_OPERATOR));
    this.relationshipType = RelationshipType.fromText(extractString(relationshipData, RELATIONSHIP_TYPE));
    this.joinTableSourceField = extractString(relationshipData, JOIN_TABLE_SOURCE_FIELD);
    this.joinTableTargetField = extractString(relationshipData, JOIN_TABLE_TARGET_FIELD);
    this.joinTableRef = setRefOnValidString(extractString(relationshipData, JOIN_TABLE_REF));
  }

  public RecordRelationshipCfg(ReadOnlyRecordRelationship readOnlyRelationship) {
    this.id = readOnlyRelationship.getId();
    this.uuid = readOnlyRelationship.getUuid();
    this.relationshipName = readOnlyRelationship.getRelationshipName();
    this.sourceRecordTypeId = readOnlyRelationship.getSourceRecordTypeId();
    this.sourceRecordTypeFieldUuid = readOnlyRelationship.getSourceRecordTypeFieldUuid();
    this.targetRecordTypeFieldUuid = readOnlyRelationship.getTargetRecordTypeFieldUuid();
    this.targetRecordTypeUuid = readOnlyRelationship.getTargetRecordTypeUuid();
    this.relationshipOperator = readOnlyRelationship.getRelationshipOperator();
    this.relationshipType = readOnlyRelationship.getRelationshipType();
    this.joinTableSourceField = readOnlyRelationship.getJoinTableSourceField();
    this.joinTableTargetField = readOnlyRelationship.getJoinTableTargetField();
    this.joinTableRef = readOnlyRelationship.getJoinTableRef();
    this.updateBehavior = readOnlyRelationship.getUpdateBehavior();
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

  @Override
  @XmlElement
  @Column(name = "uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @XmlElement
  @Column(name = "relationship_name", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getRelationshipName() {
    return relationshipName;
  }

  public void setRelationshipName(String relationshipName) {
    this.relationshipName = relationshipName;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRelationshipSourceField)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_QUERY_INFO)
  public String getSourceRecordTypeFieldUuid() {
    return sourceRecordTypeFieldUuid;
  }

  public void setSourceRecordTypeFieldUuid(String sourceRecordTypeFieldUuid) {
    this.sourceRecordTypeFieldUuid = sourceRecordTypeFieldUuid;
  }

  @XmlElement
  @Column(name = "target_rt_uuid", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getTargetRecordTypeUuid() {
    return targetRecordTypeUuid;
  }

  public void setTargetRecordTypeUuid(String targetRecordTypeUuid) {
    this.targetRecordTypeUuid = targetRecordTypeUuid;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRelationshipTargetUuid)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<Long,String> getTargetRecordTypeRef() {
    return new RecordTypeRefImpl(targetRecordTypeUuid);
  }

  public void setTargetRecordTypeRef(Ref<Long,String> target) {
    Preconditions.checkNotNull(target, "Target is required");
    if (!(target instanceof RecordTypeRef)) {
      throw new UnsupportedOperationException("Unsupported target type " + target.getClass().getName());
    }
    String targetRecordTypeUuid = target.getUuid();
    Preconditions.checkNotNull(targetRecordTypeUuid, "Uuid is required");
    setTargetRecordTypeUuid(targetRecordTypeUuid);
  }

  @Transient
  @XmlTransient
  public String getTargetRecordTypeFieldUuid() {
    return targetRecordTypeFieldUuid;
  }

  public void setTargetRecordTypeFieldUuid(String targetRecordTypeFieldUuid) {
    this.targetRecordTypeFieldUuid = targetRecordTypeFieldUuid;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRelationshipTargetField)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_STORED_FORM)
  public String getTargetJoinFieldStoredForm() {
    return RecordField.getStoredForm(targetRecordTypeUuid, targetRecordTypeFieldUuid, null);
  }

  @XmlElement
  @Column(name = "relationship_data", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRelationshipData)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_RELATIONSHIP_CFG_DATA)
  @Lob
  public String getRelationshipData() {
    Map<String, String> data = new HashMap<>();
    data.put(SOURCE_RECORD_FIELD_UUID, sourceRecordTypeFieldUuid);
    data.put(TARGET_RECORD_FIELD_UUID, targetRecordTypeFieldUuid);
    data.put(RELATIONSHIP_OPERATOR, String.valueOf(relationshipOperator.getCode()));
    data.put(JOIN_TABLE_SOURCE_FIELD, joinTableSourceField);
    data.put(JOIN_TABLE_TARGET_FIELD, joinTableTargetField);
    data.put(JOIN_TABLE_REF, joinTableRef != null ?
        (joinTableRef.getId() == null ? joinTableRef.getUuid().toString() : joinTableRef.getId().toString()) :
        null
    );
    return new Gson().toJson(data);
  }

  public void setRelationshipData(String relationshipData) {
    Map data = new Gson().fromJson(relationshipData, Map.class);
    setSourceRecordTypeFieldUuid((String)data.get(SOURCE_RECORD_FIELD_UUID));
    setTargetRecordTypeFieldUuid((String)data.get(TARGET_RECORD_FIELD_UUID));

    String relationshipOperatorByteString = (String)data.get(RELATIONSHIP_OPERATOR);
    setRelationshipOperator(RelationshipOperator.valueOfStringCode(relationshipOperatorByteString));

    setJoinTableSourceField((String)data.get(JOIN_TABLE_SOURCE_FIELD));
    setJoinTableTargetField((String)data.get(JOIN_TABLE_TARGET_FIELD));
    Object dataStoreEntityUuid = data.get(JOIN_TABLE_REF);
    setJoinTableRef(setRefOnValidString(dataStoreEntityUuid));
  }

  @XmlTransient
  @Column(name = SRC_RT_ID_COLUMN_NAME, insertable = false, updatable = false)
  public Long getSourceRecordTypeId() {
    return sourceRecordTypeId;
  }

  public void setSourceRecordTypeId(Long sourceRecordTypeId) {
    this.sourceRecordTypeId = sourceRecordTypeId;
  }

  @XmlTransient
  @Column(name = RELATIONSHIP_TYPE_COLUMN_NAME, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  private byte getRelationshipTypeByte() {
    return getRelationshipType().getCode();
  }

  private void setRelationshipTypeByte(byte relationshipTypeByte) {
    setRelationshipType(RelationshipType.valueOf(relationshipTypeByte));
  }

  @Transient
  @XmlElement
  public RelationshipType getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(RelationshipType relationshipType) {
    this.relationshipType = relationshipType;
  }

  @Transient
  @XmlTransient
  public RelationshipOperator getRelationshipOperator() {
    return relationshipOperator;
  }

  public void setRelationshipOperator(RelationshipOperator operator) {
    this.relationshipOperator = operator;
  }

  @Transient
  @XmlTransient
  public String getJoinTableSourceField() {
    return joinTableSourceField;
  }

  public void setJoinTableSourceField(String joinTableSourceField) {
    this.joinTableSourceField = joinTableSourceField;
  }

  @Transient
  @XmlTransient
  public String getJoinTableTargetField() {
    return joinTableTargetField;
  }

  public void setJoinTableTargetField(String joinTableTargetField) {
    this.joinTableTargetField = joinTableTargetField;
  }

  @Transient
  @XmlTransient
  public Ref<String,String> getJoinTableRef() {
    return joinTableRef;
  }

  public void setJoinTableRef(Ref<String,String> joinTableRef) {
    this.joinTableRef = joinTableRef;
  }

  @XmlTransient
  @Column(name = UPDATE_BEHAVIOR_COLUMN_NAME, nullable = false)
  public byte getUpdateBehaviorByte() {
    return getUpdateBehavior().getCode();
  }

  public void setUpdateBehaviorByte(byte updateBehaviorByte) {
    setUpdateBehavior(UpdateBehavior.valueOf(updateBehaviorByte));
  }

  @Transient
  @XmlElement
  public UpdateBehavior getUpdateBehavior() {
    return updateBehavior;
  }

  public void setUpdateBehavior(UpdateBehavior updateBehavior) {
    this.updateBehavior = updateBehavior;
  }

  @PrePersist
  void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) {
      uuid = java.util.UUID.randomUUID().toString();
    }
  }

  public Map<String,Value> toMap() {
    return new HashMap<String,Value>() {{
      put(UUID_KEY, Type.STRING.valueOf(uuid));
      put(NAME, Type.STRING.valueOf(relationshipName));
      put(TARGET_RECORD_UUID, Type.STRING.valueOf(targetRecordTypeUuid));
      put(SOURCE_RECORD_FIELD_UUID, Type.STRING.valueOf(sourceRecordTypeFieldUuid));
      put(TARGET_RECORD_FIELD_UUID, Type.STRING.valueOf(targetRecordTypeFieldUuid));
      put(RELATIONSHIP_TYPE, Type.STRING.valueOf(relationshipType.getText()));
      put(JOIN_TABLE_SOURCE_FIELD, Type.STRING.valueOf(joinTableSourceField));
      put(JOIN_TABLE_TARGET_FIELD, Type.STRING.valueOf(joinTableTargetField));
      put(JOIN_TABLE_REF, Type.STRING.valueOf(joinTableRef != null ? joinTableRef.getUuid().toString() : null));
    }};
  }

  public com.appiancorp.type.cdt.value.DesignerDtoRecordRelationshipCfg toDesignerDtoRecordRelationshipCfg() {
    com.appiancorp.type.cdt.value.DesignerDtoRecordRelationshipCfg relCfg = new com.appiancorp.type.cdt.value.DesignerDtoRecordRelationshipCfg();
    relCfg.setUuid(uuid);
    relCfg.setId(id);
    relCfg.setName(relationshipName);
    relCfg.setSourceRecordTypeId(sourceRecordTypeId);
    relCfg.setSourceRecordTypeFieldUuid(sourceRecordTypeFieldUuid);
    relCfg.setTargetRecordTypeFieldUuid(targetRecordTypeFieldUuid);
    relCfg.setTargetRecordTypeUuid(targetRecordTypeUuid);
    relCfg.setRelationshipOperator(relationshipOperator.getText());
    relCfg.setRelationshipType(relationshipType.getText());
    relCfg.setJoinTableSourceField(joinTableSourceField);
    relCfg.setJoinTableTargetField(joinTableTargetField);
    relCfg.setJoinTableRef(joinTableRef != null ? joinTableRef.getUuid() : null);
    relCfg.setUpdateBehavior(updateBehavior.getText());
    return relCfg;
  }

  private String extractString(ImmutableDictionary relationshipData, String key) {
    return relationshipData.getOrDefault(key, Type.STRING.nullValue()).getValue().toString();
  }

  private Ref<String,String> setRefOnValidString(Object uuid) {
    if (!Type.STRING.isNull(uuid) && uuid instanceof String) {
      return new DataStoreEntityRefImpl(null, (String)uuid);
    }
    return null;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordRelationshipCfg)) {
      return false;
    }

    RecordRelationshipCfg that = (RecordRelationshipCfg)o;

    return new EqualsBuilder().append(id, that.id)
        .append(uuid, that.uuid)
        .append(relationshipName, that.relationshipName)
        .append(sourceRecordTypeId, that.sourceRecordTypeId)
        .append(sourceRecordTypeFieldUuid, that.sourceRecordTypeFieldUuid)
        .append(targetRecordTypeUuid, that.targetRecordTypeUuid)
        .append(targetRecordTypeFieldUuid, that.targetRecordTypeFieldUuid)
        .append(relationshipType, that.relationshipType)
        .append(relationshipOperator, that.relationshipOperator)
        .append(joinTableSourceField, that.joinTableSourceField)
        .append(joinTableTargetField, that.joinTableTargetField)
        .append(joinTableRef, that.joinTableRef)
        .append(updateBehavior, that.updateBehavior)
        .isEquals();
  }

  /**
   * Determine if two relationship cfgs are functionally equivalent for runtime query behavior
   * @param o The query to compare against
   * @return Whether or not the two queries are functionally equivalent
   */
  @Override
  public boolean functionalEquals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordRelationshipCfg)) {
      return false;
    }

    RecordRelationshipCfg that = (RecordRelationshipCfg)o;

    return Objects.equals(getUuid(), that.getUuid()) &&
        Objects.equals(getTargetRecordTypeUuid(), that.getTargetRecordTypeUuid()) &&
        Objects.equals(getRelationshipType(), that.getRelationshipType()) &&
        Objects.equals(getRelationshipOperator(), that.getRelationshipOperator()) &&
        Objects.equals(getTargetRecordTypeFieldUuid(), that.getTargetRecordTypeFieldUuid()) &&
        Objects.equals(getSourceRecordTypeFieldUuid(), that.getSourceRecordTypeFieldUuid());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(
        id,
        uuid,
        relationshipName,
        sourceRecordTypeId,
        sourceRecordTypeFieldUuid,
        targetRecordTypeUuid,
        targetRecordTypeFieldUuid,
        relationshipType,
        relationshipOperator,
        joinTableSourceField,
        joinTableTargetField,
        joinTableRef,
        updateBehavior
    );
  }
}
