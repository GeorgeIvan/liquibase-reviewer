package com.appiancorp.message.integrations.kafka.designobject;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.message.integrations.model.MessageFormat;
import com.appiancorp.message.integrations.model.OffsetResetStrategy;
import com.appiancorp.message.integrations.model.PublishStatus;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.suiteapi.type.Type;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store Message Executor Design Object History in RDBMS.
 */
@Entity
@Table(name = "msg_executor_history")
public class MessageExecutorHistoryEntity {

  public static final String LOCAL_PART = "MessageExecutor";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = com.appiancorp.type.Id.LOCAL_PART;
  public static final String PROP_MESSAGE_EXECUTOR_ID = "messageExecutorId";
  public static final String PROP_UUID = "uuid";
  public static final String PROP_NAME = "name";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_VERSION_NUMBER = "versionNumber";
  public static final String PROP_VERSION_UUID = "versionUuid";

  private Long id;
  private Long messageExecutorId;
  private String uuid;
  private String name;
  private String description;
  private String versionUuid;
  private String connectedSystemUuid;
  private Integer connectedSystemVersionNo;
  private String topicNames;
  private String consumerGroupId;
  private OffsetResetStrategy offsetResetStrategy;
  private MessageFormat messageKeyFormat;
  private MessageFormat messageValueFormat;
  private String filterExp;
  private Boolean preserveOrder;
  private Boolean batchingEnabled;
  private Integer maxBatchSize;
  private String msgHandlerExp;
  private PublishStatus publishStatus;
  private Long serviceAccount;

  private AuditInfo auditInfo = new AuditInfo();
  private Integer versionNumber;
  private Integer latestVersionNumber;

  public MessageExecutorHistoryEntity(){
  }

  public MessageExecutorHistoryEntity(final MessageExecutorEntity messageExecutorEntity){
    this.messageExecutorId = requireNonNull(messageExecutorEntity.getId());
    this.uuid = requireNonNull(messageExecutorEntity.getUuid());
    this.name = requireNonNull(messageExecutorEntity.getName());
    this.description = messageExecutorEntity.getDescription();
    this.versionUuid = messageExecutorEntity.getVersionUuid();
    this.connectedSystemUuid = messageExecutorEntity.getConnectedSystemUuid();
    this.connectedSystemVersionNo = messageExecutorEntity.getConnectedSystemVersionNo();
    if (messageExecutorEntity.getTopicNames() != null && !messageExecutorEntity.getTopicNames().isEmpty()) {
      this.topicNames = messageExecutorEntity.getTopicNames().stream()
          .map(MessageExecutorTopicEntity::getTopicName)
          .collect(Collectors.joining(","));
    }
    this.consumerGroupId = messageExecutorEntity.getConsumerGroupId();
    this.offsetResetStrategy = messageExecutorEntity.getOffsetResetStrategy();
    this.messageKeyFormat = messageExecutorEntity.getMessageKeyFormat();
    this.messageValueFormat = messageExecutorEntity.getMessageValueFormat();
    this.filterExp = messageExecutorEntity.getFilterExp();
    this.preserveOrder = messageExecutorEntity.getPreserveOrder();
    this.batchingEnabled = messageExecutorEntity.getBatchingEnabled();
    this.maxBatchSize = messageExecutorEntity.getMaxBatchSize();
    this.msgHandlerExp = messageExecutorEntity.getMsgHandlerExp();
    this.publishStatus = messageExecutorEntity.getPublishStatus();
    this.serviceAccount = messageExecutorEntity.getServiceAccount();
    this.auditInfo = new AuditInfo(requireNonNull(messageExecutorEntity.getAuditInfo()));
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
  public Long getMessageExecutorId() {
    return messageExecutorId;
  }

  public void setMessageExecutorId(Long messageExecutorId) {
    this.messageExecutorId = messageExecutorId;
  }

  @Column(name = "uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "version_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }


  @Column(name = "conn_sys_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  public String getConnectedSystemUuid() {
    return connectedSystemUuid;
  }

  public void setConnectedSystemUuid(String connectedSystemUuid) {
    this.connectedSystemUuid = connectedSystemUuid;
  }

  @Column(name = "conn_sys_version_no", nullable = true)
  public Integer getConnectedSystemVersionNo() {
    return connectedSystemVersionNo;
  }

  public void setConnectedSystemVersionNo(Integer connectedSystemVersionNo) {
    this.connectedSystemVersionNo = connectedSystemVersionNo;
  }

  @Column(name = "topic_names", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getTopicNames() {
    return topicNames;
  }

  public void setTopicNames(String topicNames) {
    this.topicNames = topicNames;
  }

  @Column(name = "consumer_group_id", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getConsumerGroupId() {
    return consumerGroupId;
  }

  public void setConsumerGroupId(String consumerGroupId) {
    this.consumerGroupId = consumerGroupId;
  }

  @Transient
  public OffsetResetStrategy getOffsetResetStrategy() {
    return offsetResetStrategy;
  }

  public void setOffsetResetStrategy(OffsetResetStrategy offsetResetStrategy) {
    this.offsetResetStrategy = offsetResetStrategy;
  }

  @Column(name = "offset_reset_strategy", nullable = false)
  public Byte getOffsetResetStrategyByte() {
    return offsetResetStrategy.getValue();
  }

  public void setOffsetResetStrategyByte(Byte value) {
    setOffsetResetStrategy(OffsetResetStrategy.fromValue(value));
  }

  @Transient
  public MessageFormat getMessageKeyFormat() {
    return messageKeyFormat;
  }

  public void setMessageKeyFormat(MessageFormat messageKeyFormat) {
    this.messageKeyFormat = messageKeyFormat;
  }

  @Column(name = "msg_key_format", nullable = false)
  public Byte getMessageKeyFormatByte() {
    return messageKeyFormat.getValue();
  }

  public void setMessageKeyFormatByte(Byte value) {
    setMessageKeyFormat(MessageFormat.fromValue(value));
  }

  @Transient
  public MessageFormat getMessageValueFormat() {
    return messageValueFormat;
  }

  public void setMessageValueFormat(MessageFormat messageValueFormat) {
    this.messageValueFormat = messageValueFormat;
  }

  @Column(name = "msg_value_format", nullable = false)
  public Byte getMessageValueFormatByte() {
    return messageValueFormat.getValue();
  }

  public void setMessageValueFormatByte(Byte value) {
    setMessageValueFormat(MessageFormat.fromValue(value));
  }

  @Lob
  @Column(name = "filter_exp", nullable =true, length = Constants.COL_MAXLEN_EXPRESSION)
  public String getFilterExp() {
    return filterExp;
  }

  public void setFilterExp(String filterExp) {
    this.filterExp = filterExp;
  }

  @Column(name = "preserve_order", nullable = false)
  public Boolean getPreserveOrder() {
    return preserveOrder;
  }

  public void setPreserveOrder(Boolean preserveOrder) {
    this.preserveOrder = preserveOrder;
  }

  @Column(name = "batching_enabled", nullable = false)
  public Boolean getBatchingEnabled() {
    return batchingEnabled;
  }

  public void setBatchingEnabled(Boolean batchingEnabled) {
    this.batchingEnabled = batchingEnabled;
  }

  @Column(name = "max_batch_size", nullable = true)
  public Integer getMaxBatchSize() {
    return maxBatchSize;
  }

  public void setMaxBatchSize(Integer maxBatchSize) {
    this.maxBatchSize = maxBatchSize;
  }

  @Lob
  @Column(name = "msg_handler_exp", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  public String getMsgHandlerExp() {
    return msgHandlerExp;
  }

  public void setMsgHandlerExp(String msgHandlerExp) {
    this.msgHandlerExp = msgHandlerExp;
  }

  @Transient
  public PublishStatus getPublishStatus() {
    return publishStatus;
  }

  public void setPublishStatus(PublishStatus publishStatus) {
    this.publishStatus = publishStatus;
  }

  @Column(name = "publish_status", nullable = false)
  public Byte getPublishStatusByte() {
    return publishStatus.getValue();
  }

  public void setPublishStatusByte(Byte value) {
    setPublishStatus(PublishStatus.fromValue(value));
  }

  @Column(name = "svc_acc_id", nullable = true)
  public Long getServiceAccount() {
    return serviceAccount;
  }

  public void setServiceAccount(Long serviceAccount) {
    this.serviceAccount = serviceAccount;
  }

  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Transient
  public Integer getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(final Integer versionNumber) {
    this.versionNumber = versionNumber;
  }

  @Transient
  public Integer getLatestVersionNumber() {
    return latestVersionNumber;
  }

  public void setLatestVersionNumber(final Integer latestVersionNumber) {
    this.latestVersionNumber = latestVersionNumber;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("messageExecutorId", messageExecutorId)
        .add("uuid", uuid)
        .add("name", name)
        .add("description", description)
        .add("versionUuid", versionUuid)
        .add("connectedSystemUuid", connectedSystemUuid)
        .add("topicNames", topicNames)
        .add("consumerGroupId", consumerGroupId)
        .add("offsetResetStrategy", offsetResetStrategy)
        .add("messageKeyFormat", messageKeyFormat)
        .add("messageValueFormat", messageValueFormat)
        .add("filterExp", filterExp)
        .add("preserveOrder", preserveOrder)
        .add("batchingEnabled", batchingEnabled)
        .add("maxBatchSize", maxBatchSize)
        .add("msgHandlerExp", msgHandlerExp)
        .add("publishStatus", publishStatus)
        .add("serviceAccount", serviceAccount)
        .add("auditInfo", auditInfo)
        .toString();
  }

}
