package com.appiancorp.record.entities;

import static com.appiancorp.record.entities.RecordEventsCfgEntity.LOCAL_PART;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_COMMENT_EVENT_TYPE_ID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_COMMENT_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_AUTOMATION_IDENTIFIER_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_RECORD_TYPE_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_RELATIONSHIP_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_TIMESTAMP_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_TYPE_RECORD_TYPE_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_TYPE_RELATIONSHIP_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_TYPE_VALUE_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_USER_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_GENERATE_COMMON_EVENTS;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_REPLY_RECORD_TYPE_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_REPLY_RELATIONSHIP_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_REPLY_COMMENT_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_REPLY_USER_FIELD_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_EVENT_REPLY_TIMESTAMP_FIELD_UUID;

import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_SUBSCRIBER_RECORD_TYPE_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_SUBSCRIBER_RELATIONSHIP_UUID;
import static com.appiancorp.record.entities.RecordEventsCfgEntity.PROP_SUBSCRIBER_USER_FIELD_UUID;

import static com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.appiancorp.record.entities.utils.RecordEventsCfgBuilderImpl;
import com.appiancorp.record.recordevents.RecordEventsCfg;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "record_events_config")
@IgnoreJpa
@XmlRootElement(namespace = APPIAN_NAMESPACE, name = LOCAL_PART)
@XmlType(namespace = APPIAN_NAMESPACE, name = LOCAL_PART, propOrder = {
    Uuid.LOCAL_PART,
    PROP_EVENT_RECORD_TYPE_UUID,
    PROP_EVENT_RELATIONSHIP_UUID,
    PROP_EVENT_TYPE_RECORD_TYPE_UUID,
    PROP_EVENT_TYPE_RELATIONSHIP_UUID,
    PROP_EVENT_TIMESTAMP_FIELD_UUID,
    PROP_EVENT_USER_FIELD_UUID,
    PROP_EVENT_TYPE_VALUE_FIELD_UUID,
    PROP_GENERATE_COMMON_EVENTS,
    PROP_EVENT_AUTOMATION_IDENTIFIER_FIELD_UUID,
    PROP_EVENT_COMMENT_FIELD_UUID,
    PROP_COMMENT_EVENT_TYPE_ID,
    PROP_EVENT_REPLY_RECORD_TYPE_UUID,
    PROP_EVENT_REPLY_RELATIONSHIP_UUID,
    PROP_EVENT_REPLY_COMMENT_FIELD_UUID,
    PROP_EVENT_REPLY_USER_FIELD_UUID,
    PROP_EVENT_REPLY_TIMESTAMP_FIELD_UUID,
    PROP_SUBSCRIBER_RECORD_TYPE_UUID,
    PROP_SUBSCRIBER_RELATIONSHIP_UUID,
    PROP_SUBSCRIBER_USER_FIELD_UUID
})
public class RecordEventsCfgEntity
    implements Id<Long>, RecordEventsCfg {
  private static final long serialVersionUID = 1L;
  public static final String LOCAL_PART = "recordEventsConfig";
  public static final String PROP_BASE_RECORD_TYPE_UUID = "baseRecordTypeUuid";
  public static final String PROP_EVENT_RELATIONSHIP_UUID = "eventRelationshipUuid";
  public static final String PROP_EVENT_TYPE_RELATIONSHIP_UUID = "eventTypeRelationshipUuid";
  public static final String PROP_EVENT_TIMESTAMP_FIELD_UUID = "eventTimestampFieldUuid";
  public static final String PROP_EVENT_USER_FIELD_UUID = "eventUserFieldUuid";
  public static final String PROP_EVENT_TYPE_VALUE_FIELD_UUID = "eventTypeValueFieldUuid";
  public static final String PROP_EVENT_RECORD_TYPE_UUID = "eventRecordTypeUuid";
  public static final String PROP_EVENT_TYPE_RECORD_TYPE_UUID = "eventTypeRecordTypeUuid";
  public static final String PROP_GENERATE_COMMON_EVENTS = "generateCommonEvents";
  public static final String PROP_EVENT_AUTOMATION_IDENTIFIER_FIELD_UUID = "eventAutomationIdentifierFieldUuid";
  public static final String PROP_EVENT_COMMENT_FIELD_UUID = "eventCommentFieldUuid";
  public static final String PROP_COMMENT_EVENT_TYPE_ID = "commentEventTypeId";
  public static final String PROP_EVENT_REPLY_RECORD_TYPE_UUID = "eventReplyRecordTypeUuid";
  public static final String PROP_EVENT_REPLY_RELATIONSHIP_UUID = "eventReplyRelationshipUuid";
  public static final String PROP_EVENT_REPLY_COMMENT_FIELD_UUID = "eventReplyCommentFieldUuid";
  public static final String PROP_EVENT_REPLY_USER_FIELD_UUID = "eventReplyUserFieldUuid";
  public static final String PROP_EVENT_REPLY_TIMESTAMP_FIELD_UUID = "eventReplyTimestampFieldUuid";
  public static final String PROP_SUBSCRIBER_RECORD_TYPE_UUID = "subscriberRecordTypeUuid";
  public static final String PROP_SUBSCRIBER_RELATIONSHIP_UUID = "subscriberRelationshipUuid";
  public static final String PROP_SUBSCRIBER_USER_FIELD_UUID = "subscriberUserFieldUuid";

  private Long id;
  private String uuid;
  private String baseRecordTypeUuid;
  private String eventRelationshipUuid;
  private String eventTypeRelationshipUuid;
  private String eventRecordTypeUuid;
  private String eventTypeRecordTypeUuid;
  private String eventTimestampFieldUuid;
  private String eventUserFieldUuid;
  private String eventTypeValueFieldUuid;
  private boolean generateCommonEvents;
  private String eventAutomationIdentifierFieldUuid;
  private String eventCommentFieldUuid;
  private Integer commentEventTypeId;
  private String eventReplyRecordTypeUuid;
  private String eventReplyRelationshipUuid;
  private String eventReplyUserFieldUuid;
  private String eventReplyCommentFieldUuid;
  private String eventReplyTimestampFieldUuid;
  private String subscriberRecordTypeUuid;
  private String subscriberRelationshipUuid;
  private String subscriberUserFieldUuid;

  public RecordEventsCfgEntity() {
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = Id.LOCAL_PART, nullable = false, updatable = false)
  @XmlTransient
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = Uuid.LOCAL_PART, updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @XmlTransient
  @Column(name = "base_record_type_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getBaseRecordTypeUuid() {
    return baseRecordTypeUuid;
  }

  @Override
  public void setBaseRecordTypeUuid(String uuid) {
    this.baseRecordTypeUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_RECORD_TYPE_UUID)
  @Column(name = "event_record_type_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventRecordTypeUuid() {
    return eventRecordTypeUuid;
  }

  @Override
  public void setEventRecordTypeUuid(String uuid) {
    this.eventRecordTypeUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_TYPE_RECORD_TYPE_UUID)
  @Column(name = "event_type_record_type_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventTypeRecordTypeUuid() {
    return eventTypeRecordTypeUuid;
  }

  @Override
  public void setEventTypeRecordTypeUuid(String uuid) {
    this.eventTypeRecordTypeUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_RELATIONSHIP_UUID)
  @Column(name = "event_relationship_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventRelationshipUuid() {
    return eventRelationshipUuid;
  }

  @Override
  public void setEventRelationshipUuid(String uuid) {
    this.eventRelationshipUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_TYPE_RELATIONSHIP_UUID)
  @Column(name = "event_type_relationship_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventTypeRelationshipUuid() {
    return eventTypeRelationshipUuid;
  }

  @Override
  public void setEventTypeRelationshipUuid(String uuid) {
    this.eventTypeRelationshipUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_TYPE_VALUE_FIELD_UUID)
  @Column(name = "type_value_field_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventTypeValueFieldUuid() {
    return eventTypeValueFieldUuid;
  }

  @Override
  public void setEventTypeValueFieldUuid(String uuid) {
    this.eventTypeValueFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_TIMESTAMP_FIELD_UUID)
  @Column(name = "event_timestamp_field_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventTimestampFieldUuid() {
    return eventTimestampFieldUuid;
  }

  @Override
  public void setEventTimestampFieldUuid(String uuid) {
    this.eventTimestampFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_EVENT_USER_FIELD_UUID)
  @Column(name = "event_user_field_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getEventUserFieldUuid() {
    return eventUserFieldUuid;
  }

  @Override
  public void setEventUserFieldUuid(String uuid) {
    this.eventUserFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = PROP_GENERATE_COMMON_EVENTS)
  @Column(name = "generate_common_events", nullable = false)
  public boolean getGenerateCommonEvents() {
    return generateCommonEvents;
  }

  @Override
  public void setGenerateCommonEvents(boolean generateCommonEvents) {
    this.generateCommonEvents = generateCommonEvents;
  }

  @Override
  @XmlElement(name = "eventAutomationIdentifierFieldUuid")
  @Column(name = "event_actor_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventAutomationIdentifierFieldUuid() {
    return eventAutomationIdentifierFieldUuid;
  }

  @Override
  public void setEventAutomationIdentifierFieldUuid(String uuid) {
    this.eventAutomationIdentifierFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = "eventCommentFieldUuid")
  @Column(name = "event_comment_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventCommentFieldUuid() {
    return eventCommentFieldUuid;
  }

  @Override
  public void setEventCommentFieldUuid(String uuid) {
    this.eventCommentFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = "commentEventTypeId")
  @Column(name = "comment_event_type_id")
  public Integer getCommentEventTypeId() {
    return commentEventTypeId;
  }

  @Override
  public void setCommentEventTypeId(Integer eventTypeId) {
    this.commentEventTypeId = eventTypeId;
  }

  @Override
  @XmlElement(name = "eventReplyRecordTypeUuid")
  @Column(name = "reply_record_type_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventReplyRecordTypeUuid() {
    return eventReplyRecordTypeUuid;
  }

  @Override
  public void setEventReplyRecordTypeUuid(String uuid) {
    this.eventReplyRecordTypeUuid = uuid;
  }

  @Override
  @XmlElement(name = "eventReplyRelationshipUuid")
  @Column(name = "reply_relationship_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventReplyRelationshipUuid() {
    return eventReplyRelationshipUuid;
  }

  @Override
  public void setEventReplyRelationshipUuid(String uuid) {
    this.eventReplyRelationshipUuid = uuid;
  }

  @Override
  @XmlElement(name = "eventReplyUserFieldUuid")
  @Column(name = "reply_user_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventReplyUserFieldUuid() {
    return eventReplyUserFieldUuid;
  }

  @Override
  public void setEventReplyUserFieldUuid(String uuid) {
    this.eventReplyUserFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = "eventReplyCommentFieldUuid")
  @Column(name = "reply_comment_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventReplyCommentFieldUuid() {
    return eventReplyCommentFieldUuid;
  }

  @Override
  public void setEventReplyCommentFieldUuid(String uuid) {
    this.eventReplyCommentFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = "eventReplyTimestampFieldUuid")
  @Column(name = "reply_timestamp_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getEventReplyTimestampFieldUuid() {
    return eventReplyTimestampFieldUuid;
  }

  @Override
  public void setEventReplyTimestampFieldUuid(String uuid) {
    this.eventReplyTimestampFieldUuid = uuid;
  }

  @Override
  @XmlElement(name = "subscriberRecordTypeUuid")
  @Column(name = "subscriber_record_type_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getSubscriberRecordTypeUuid() {
    return subscriberRecordTypeUuid;
  }

  @Override
  public void setSubscriberRecordTypeUuid(String uuid) {
    this.subscriberRecordTypeUuid = uuid;
  }

  @Override
  @XmlElement(name = "subscriberRelationshipUuid")
  @Column(name = "subscriber_relationship_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getSubscriberRelationshipUuid() {
    return subscriberRelationshipUuid;
  }

  @Override
  public void setSubscriberRelationshipUuid(String uuid) {
    this.subscriberRelationshipUuid = uuid;
  }

  @Override
  @XmlElement(name = "subscriberUserFieldUuid")
  @Column(name = "subscriber_user_field_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getSubscriberUserFieldUuid() {
    return subscriberUserFieldUuid;
  }

  @Override
  public void setSubscriberUserFieldUuid(String uuid) {
    this.subscriberUserFieldUuid = uuid;
  }

  public static RecordEventsCfgBuilder builder() {
    return new RecordEventsCfgBuilderImpl();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecordEventsCfgEntity that = (RecordEventsCfgEntity)o;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getUuid(), that.getUuid()) &&
        Objects.equals(getEventRecordTypeUuid(), that.getEventRecordTypeUuid()) &&
        Objects.equals(getEventTypeRecordTypeUuid(), that.getEventTypeRecordTypeUuid()) &&
        Objects.equals(getEventRelationshipUuid(), that.getEventRelationshipUuid()) &&
        Objects.equals(getEventTypeRelationshipUuid(), that.getEventTypeRelationshipUuid()) &&
        Objects.equals(getEventTimestampFieldUuid(), that.getEventTimestampFieldUuid()) &&
        Objects.equals(getEventUserFieldUuid(), that.getEventUserFieldUuid()) &&
        Objects.equals(getEventTypeValueFieldUuid(), that.getEventTypeValueFieldUuid()) &&
        Objects.equals(getGenerateCommonEvents(), that.getGenerateCommonEvents()) &&
        Objects.equals(getEventAutomationIdentifierFieldUuid(), that.getEventAutomationIdentifierFieldUuid()) &&
        Objects.equals(getEventCommentFieldUuid(), that.getEventCommentFieldUuid()) &&
        Objects.equals(getCommentEventTypeId(), that.getCommentEventTypeId()) &&
        Objects.equals(getEventReplyRecordTypeUuid(), that.getEventReplyRecordTypeUuid()) &&
        Objects.equals(getEventReplyRelationshipUuid(), that.getEventReplyRelationshipUuid()) &&
        Objects.equals(getEventReplyCommentFieldUuid(), that.getEventReplyCommentFieldUuid()) &&
        Objects.equals(getEventReplyUserFieldUuid(), that.getEventReplyUserFieldUuid()) &&
        Objects.equals(getEventReplyTimestampFieldUuid(), that.getEventReplyTimestampFieldUuid()) &&
        Objects.equals(getSubscriberRecordTypeUuid(), that.getSubscriberRecordTypeUuid()) &&
        Objects.equals(getSubscriberRelationshipUuid(), that.getSubscriberRelationshipUuid()) &&
        Objects.equals(getSubscriberUserFieldUuid(), that.getSubscriberUserFieldUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUuid(), getEventRecordTypeUuid(), getEventTypeRecordTypeUuid(),
        getEventRelationshipUuid(), getEventTypeRelationshipUuid(), getEventTimestampFieldUuid(),
        getEventUserFieldUuid(), getEventTypeValueFieldUuid(), getGenerateCommonEvents(),
        getEventAutomationIdentifierFieldUuid(), getEventCommentFieldUuid(), getCommentEventTypeId(),
        getEventReplyRecordTypeUuid(), getEventReplyRelationshipUuid(), getEventReplyCommentFieldUuid(),
        getEventReplyUserFieldUuid(), getEventReplyTimestampFieldUuid(),getSubscriberRecordTypeUuid(),
        getSubscriberRelationshipUuid(), getSubscriberUserFieldUuid());
  }

  @Override
  public String toString() {
    return "RecordEventsCfgEntity {" + "id=" + id + ", uuid='" + uuid + "', eventRecordTypeUuid='" +
        eventRecordTypeUuid + "', eventTypeRecordTypeUuid='" + eventTypeRecordTypeUuid +
        "', eventRelationshipUuid='" + eventRelationshipUuid + "', eventTypeRelationshipUuid='" +
        eventTypeRelationshipUuid + "', eventTimestampFieldUuid='" + eventTimestampFieldUuid +
        "', eventUserFieldUuid='" + eventUserFieldUuid + "', eventTypeValueFieldUuid='" +
        eventTypeValueFieldUuid + "', eventRecordTypeUuidUuid='" + eventRecordTypeUuid +
        ", eventTypeRecordTypeUuid='" + eventTypeRecordTypeUuid +  ", generateCommonEvents=" +
        generateCommonEvents + ", eventAutomationIdentifierFieldUuid=" + eventAutomationIdentifierFieldUuid +
        ", eventCommentFieldUuid=" + eventCommentFieldUuid +
        ", commentEventTypeId=" + commentEventTypeId +
        ", eventReplyRecordTypeUuid=" + eventReplyRecordTypeUuid +
        ", eventReplyRelationshipUuid=" + eventReplyRelationshipUuid +
        ", eventReplyUserFieldUuid=" + eventReplyUserFieldUuid +
        ", eventReplyCommentFieldUuid=" + eventReplyCommentFieldUuid +
        ", eventReplyTimestampFieldUuid=" + eventReplyTimestampFieldUuid +
        ", subscriberRecordTypeUuid=" + subscriberRecordTypeUuid +
        ", subscriberRelationshipUuid=" + subscriberRelationshipUuid +
        ", subscriberUserFieldUuid=" + subscriberUserFieldUuid + "'}";
  }
}
