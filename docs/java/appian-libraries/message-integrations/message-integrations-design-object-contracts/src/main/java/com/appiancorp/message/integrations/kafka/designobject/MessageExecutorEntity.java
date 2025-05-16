package com.appiancorp.message.integrations.kafka.designobject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.cdt.MessageExecutorConstants;
import com.appiancorp.message.integrations.MessageIntegrationConstants;
import com.appiancorp.message.integrations.model.MessageFormat;
import com.appiancorp.message.integrations.model.OffsetResetStrategy;
import com.appiancorp.message.integrations.model.PublishStatus;
import com.appiancorp.message.integrations.model.RunningStatus;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.action.security.RoleMapDefinitionFacade;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.GroupRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An entity class to store Message Executor Design Object in RDBMS.
 */
@Entity
@IgnoreJpa
@Tracked
@Table(name = "msg_executor")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name = "messageExecutor")
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt in to XML serialization
@XmlType(name = "MessageExecutor", namespace = Type.APPIAN_NAMESPACE,
    propOrder = {
        com.appiancorp.type.Id.LOCAL_PART,
        Uuid.LOCAL_PART,
        Name.LOCAL_PART,
        "description",
        "versionUuid",
        "connectedSystemUuid",
        "consumerGroupId",
        "topicNames",
        "messageKeyFormat",
        "messageValueFormat",
        "msgHandlerExp",
        "filterExp",
        "preserveOrder",
        "batchingEnabled",
        "maxBatchSize",
        "offsetResetStrategy",
        "serviceAccount",
        "publishStatus",
        "runningStatus"
})
@XmlSeeAlso({GroupRefImpl.class})
@SuppressWarnings({"java:S1200"})
public class MessageExecutorEntity implements HasRoleMap, HasAuditInfo, com.appiancorp.type.Id<Long>,
    Name, Uuid<String>, HasVersionHistory, HasTypeQName {

  private static final long serialVersionUID = 1L;

  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(Roles.MESSAGE_EXECUTOR_ADMIN,
      Roles.MESSAGE_EXECUTOR_EDITOR, Roles.MESSAGE_EXECUTOR_VIEWER);
  private static final String MESSAGE_EXECUTOR_ROLE_MAP = "msg_executor_rm";
  private static final String MESSAGE_EXECUTOR_ID = "msg_executor_id";
  private static final String ROLE_MAP_ENTRY_ID = "rm_entry_id";

  public static final String PUBLISH_STATUS_KEY = "publishStatusByte";
  public static final String UUID_KEY = "uuid";
  public static final String NAME_KEY = "name";
  public static final String RUNNING_STATUS_KEY = "runningStatusByte";
  public static final String SAMPLE_EXPRESSION = "/*Sample*/";
  public static final String TEST_PREFIX = "Test_";
  public static final String CONS_GROUP_ID_PREFIX = MessageIntegrationConstants.MSG_INTEGRATION_CONSUMER_CLIENT_ID_BASE;


  public static final Map<RoleMapDefinitionFacade.RoleKey,Role> MESSAGE_EXECUTOR_ROLE_KEY_TO_ROLE =
      ImmutableMap.<RoleMapDefinitionFacade.RoleKey,Role>builder()
      .put(RoleMapDefinitionFacade.RoleKey.ADMINISTRATOR, Roles.MESSAGE_EXECUTOR_ADMIN)
      .put(RoleMapDefinitionFacade.RoleKey.EDITOR, Roles.MESSAGE_EXECUTOR_EDITOR)
      .put(RoleMapDefinitionFacade.RoleKey.VIEWER, Roles.MESSAGE_EXECUTOR_VIEWER)
      .build();

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String versionUuid;
  private String connectedSystemUuid;
  private Integer connectedSystemVersionNo;
  private MessageExecutorTopicEntity[] topicNameArray;
  private Set<MessageExecutorTopicEntity> topicNames = new HashSet<>();
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
  private RunningStatus runningStatus;
  private Long serviceAccount;

  private AuditInfo auditInfo = new AuditInfo();
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private boolean isPublic;

  private static final Equivalence<MessageExecutorEntity> equalDataCheckInstance = new MessageExecutorDataEquivalence();

  public MessageExecutorEntity(){
  }

  //just for testing
  public MessageExecutorEntity(String name, String description) {
    this.name = name;
    this.description = description;
    this.batchingEnabled = Boolean.FALSE;
    this.consumerGroupId = TEST_PREFIX + UUID.randomUUID();
    this.filterExp = SAMPLE_EXPRESSION;
    this.messageKeyFormat = MessageFormat.JSON;
    this.messageValueFormat = MessageFormat.JSON;
    this.msgHandlerExp = SAMPLE_EXPRESSION;
    this.offsetResetStrategy = OffsetResetStrategy.EARLIEST;
    this.preserveOrder = Boolean.FALSE;
    this.publishStatus = PublishStatus.DRAFT;
    this.runningStatus = RunningStatus.NOT_RUNNING;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  @XmlAttribute(name = com.appiancorp.type.Id.LOCAL_PART, namespace = com.appiancorp.type.Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = com.appiancorp.type.Uuid.LOCAL_PART, namespace = com.appiancorp.type.Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = com.appiancorp.type.Name.LOCAL_PART, namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable =true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlAttribute(name = "description", namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "version_uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = "versionUuid", namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }


  @Column(name = "conn_sys_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = "connectedSystemUuid", namespace = com.appiancorp.type.Name.NAMESPACE)
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

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = MESSAGE_EXECUTOR_ID, referencedColumnName = "id")
//  @XmlAttribute(name = "topicNames", namespace = com.appiancorp.type.Name.NAMESPACE)
  @XmlElementWrapper(name="topicNames", required=true)
  @XmlElement(name="topicName")
  @XmlJavaTypeAdapter(MessageExecutorTopicAdapter.class)
  public Set<MessageExecutorTopicEntity> getTopicNames() {
    return topicNames;
  }

  @SuppressWarnings("unused")
  private void beforeMarshal(Marshaller marshaller) {
    this.topicNames = topicNameArray == null ? null : new HashSet<>(Arrays.asList(topicNameArray));
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    this.topicNameArray = topicNames == null ? null : topicNames.toArray(new MessageExecutorTopicEntity[topicNames.size()]);
  }

  public void setTopicNames(Set<MessageExecutorTopicEntity> topicNames) {
    this.topicNames = topicNames;
  }

  public void addTopic(MessageExecutorTopicEntity topic) {
    this.topicNames.add(topic);
  }

  public void removeTopic(MessageExecutorTopicEntity topic) {
    this.topicNames.remove(topic);
  }

  @Column(name = "consumer_group_id", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = "consumerGroupId", namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getConsumerGroupId() {
    return consumerGroupId;
  }

  public void setConsumerGroupId(String consumerGroupId) {
    this.consumerGroupId = consumerGroupId;
  }

  @Transient
  @XmlAttribute(name = "offsetResetStrategy", namespace = com.appiancorp.type.Name.NAMESPACE)
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
  @XmlAttribute(name = "messageKeyFormat", namespace = com.appiancorp.type.Name.NAMESPACE)
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
  @XmlAttribute(name = "messageValueFormat", namespace = com.appiancorp.type.Name.NAMESPACE)
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
  @XmlAttribute(name = "filterExp", namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getFilterExp() {
    return filterExp;
  }

  public void setFilterExp(String filterExp) {
    this.filterExp = filterExp;
  }

  @Column(name = "preserve_order", nullable = false)
  @XmlAttribute(name = "preserveOrder", namespace = com.appiancorp.type.Name.NAMESPACE)
  public Boolean getPreserveOrder() {
    return preserveOrder;
  }

  public void setPreserveOrder(Boolean preserveOrder) {
    this.preserveOrder = preserveOrder;
  }

  @Column(name = "batching_enabled", nullable = false)
  @XmlAttribute(name = "batchingEnabled", namespace = com.appiancorp.type.Name.NAMESPACE)
  public Boolean getBatchingEnabled() {
    return batchingEnabled;
  }

  public void setBatchingEnabled(Boolean batchingEnabled) {
    this.batchingEnabled = batchingEnabled;
  }

  @Column(name = "max_batch_size", nullable = true)
  @XmlAttribute(name = "maxBatchSize", namespace = com.appiancorp.type.Name.NAMESPACE)
  public Integer getMaxBatchSize() {
    return maxBatchSize;
  }

  public void setMaxBatchSize(Integer maxBatchSize) {
    this.maxBatchSize = maxBatchSize;
  }

  @Lob
  @Column(name = "msg_handler_exp", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @XmlAttribute(name = "msgHandlerExp", namespace = com.appiancorp.type.Name.NAMESPACE)
  public String getMsgHandlerExp() {
    return msgHandlerExp;
  }

  public void setMsgHandlerExp(String msgHandlerExp) {
    this.msgHandlerExp = msgHandlerExp;
  }

  @Transient
  @XmlAttribute(name = "publishStatus", namespace = com.appiancorp.type.Name.NAMESPACE)
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

  @Transient
  @XmlAttribute(name = "runningStatus", namespace = com.appiancorp.type.Name.NAMESPACE)
  public RunningStatus getRunningStatus() {
    return runningStatus;
  }

  public void setRunningStatus(RunningStatus runningStatus) {
    this.runningStatus = runningStatus;
  }

  @Column(name = "running_status", nullable = false)
  public Byte getRunningStatusByte() {
    return runningStatus.getValue();
  }

  public void setRunningStatusByte(Byte value) {
    setRunningStatus(RunningStatus.fromValue(value));
  }

  @Column(name = "svc_acc_id", nullable = true)
  @XmlAttribute(name = "serviceAccount", namespace = com.appiancorp.type.Name.NAMESPACE)
  public Long getServiceAccount() {
    return serviceAccount;
  }

  public void setServiceAccount(Long serviceAccount) {
    this.serviceAccount = serviceAccount;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public void discardRoleMap() {
    this.roleMapEntries = new PersistentSet(); // This tells Hibernate to ignore this field during the update.
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = MESSAGE_EXECUTOR_ROLE_MAP, joinColumns = @JoinColumn(name = MESSAGE_EXECUTOR_ID), inverseJoinColumns = @JoinColumn(name = ROLE_MAP_ENTRY_ID))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  public Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  public void setRoleMap(RoleMap roleMap) {
    if (this.roleMapEntries != null) {
      this.roleMapEntries.clear();
    }
    if (roleMap != null) {
      if (this.roleMapEntries == null) {
        this.roleMapEntries = new HashSet<>();
      }
      this.roleMapEntries.addAll(roleMap.getEntriesByRole().values());
    }
  }

  @Override
  @Transient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      roleMapBuilder.entries(roleMapEntry);
    }

    return roleMapBuilder.build();
  }

  @Override
  @Transient
  public boolean isPublic() {
    return isPublic;
  }

  @Override
  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.MESSAGE_EXECUTOR_VIEWER.getName();
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @PrePersist
  private void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) { //create flow with default values
      this.uuid = UUID.randomUUID().toString();
      this.consumerGroupId = CONS_GROUP_ID_PREFIX + this.uuid;
      this.filterExp = null;
      this.offsetResetStrategy = OffsetResetStrategy.EARLIEST;
      this.messageKeyFormat = MessageFormat.STRING;
      this.messageValueFormat = MessageFormat.STRING;
      this.preserveOrder = true;
      this.batchingEnabled = false;
      this.msgHandlerExp = "";
      this.publishStatus = PublishStatus.DRAFT;
      this.runningStatus = RunningStatus.NOT_RUNNING;
    }
    if (Strings.isNullOrEmpty(versionUuid)) {
      versionUuid = UUID.randomUUID().toString();
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("uuid", uuid)
        .add("name", name)
        .add("description", description)
        .add("versionUuid", versionUuid)
        .add("connectedSystemUuid", connectedSystemUuid)
        .add("topicName", topicNames)
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
        .add("runningStatus", runningStatus)
        .add("connectedSystemVersionNo", connectedSystemVersionNo)
        .add("serviceAccount", serviceAccount)
        .add("auditInfo", auditInfo)
        .toString();
  }

  @XmlTransient
  @Transient
  @Override
  public QName getTypeQName() {
    return MessageExecutorConstants.QNAME;
  }

  public boolean equivalentTo(final MessageExecutorEntity currentVersion) {
    return equalDataCheckInstance.equivalent(this, currentVersion);
  }

  public static Equivalence<MessageExecutorEntity> equalityForNonGeneratedFields() {
    return equalDataCheckInstance;
  }

  private static class MessageExecutorDataEquivalence extends Equivalence<MessageExecutorEntity> {
    @Override
    protected boolean doEquivalent(final MessageExecutorEntity lhs, final MessageExecutorEntity rhs) {
      if (lhs == rhs) {
        return true;
      }

      if (null == rhs || null == lhs) {
        return false;
      }

      return new EqualsBuilder().append(lhs.name, rhs.name)
          .append(lhs.description, rhs.description)
          .append(lhs.connectedSystemUuid, rhs.connectedSystemUuid)
          .append(lhs.consumerGroupId, rhs.consumerGroupId)
          .append(lhs.preserveOrder, rhs.preserveOrder)
          .append(lhs.offsetResetStrategy, rhs.offsetResetStrategy)
          .append(lhs.messageKeyFormat, rhs.messageKeyFormat)
          .append(lhs.messageValueFormat, rhs.messageValueFormat)
          .append(lhs.msgHandlerExp, rhs.msgHandlerExp)
          .append(lhs.filterExp, rhs.filterExp)
          .append(lhs.serviceAccount, rhs.serviceAccount)
          .append(lhs.topicNames, rhs.topicNames)
          .append(lhs.isPublic, rhs.isPublic)
          .append(lhs.batchingEnabled, rhs.batchingEnabled)
          .append(lhs.maxBatchSize, rhs.maxBatchSize)
          .append(lhs.versionUuid, rhs.versionUuid)
          .isEquals();
    }

    @Override
    protected int doHash(final MessageExecutorEntity t) {
      return Objects.hashCode(t.name, t.description, t.connectedSystemUuid, t.consumerGroupId, t.messageKeyFormat,
          t.messageValueFormat, t.filterExp, t.serviceAccount, t.topicNames, t.isPublic, t.preserveOrder,
          t.offsetResetStrategy, t.auditInfo, t.maxBatchSize, t.batchingEnabled, t.versionUuid);
    }
  }
}
