package com.appiancorp.message.integrations.kafka.designobject;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.appian.core.persist.Constants;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store Message Executor's topic names in RDBMS.
 */
@Entity
@Table(name = "msg_executor_topic",
    uniqueConstraints = @UniqueConstraint(columnNames = {"msg_executor_id", "topic_name"})
)
public class MessageExecutorTopicEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String MSG_EXECUTOR_ID = "msgExecutorId";

  private Long id;
  private Long msgExecutorId;
  private String topicName;

  public MessageExecutorTopicEntity() {}

  public MessageExecutorTopicEntity(Long msgExecutorId, String topicName) {
    this.msgExecutorId = msgExecutorId;
    this.topicName = topicName;
  }

  public MessageExecutorTopicEntity(MessageExecutorEntity messageExecutorEntity, String topicName) {
    this.msgExecutorId = messageExecutorEntity.getId();
    this.topicName = topicName;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "msg_executor_id", nullable = false)
  public Long getMsgExecutorId() {
    return msgExecutorId;
  }

  public void setMsgExecutorId(Long msgExecutorId) {
    this.msgExecutorId = msgExecutorId;
  }

  @Column(name = "topic_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public MessageExecutorTopicEntity withUpdatedTopicName(String topicName) {
    this.topicName = topicName;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageExecutorTopicEntity that = (MessageExecutorTopicEntity) o;
    return Objects.equals(msgExecutorId, that.msgExecutorId) &&
        Objects.equals(topicName, that.topicName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(msgExecutorId, topicName);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("msgExecutorId", msgExecutorId)
        .add("topicName", topicName)
        .toString();
  }
}
