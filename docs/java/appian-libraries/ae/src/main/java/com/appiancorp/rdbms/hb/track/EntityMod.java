package com.appiancorp.rdbms.hb.track;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appian.dl.txn.TxnOpType;

/**
 * Metadata about a change to a tracked object in RDBMS.
 */
@Entity
@Table(name = "tx_data")
public class EntityMod {
  private Long id;
  private TxnOpType op;
  private String entityType;
  private String entityName;
  private Long valueId;
  private String valueUuid;

  public EntityMod() {}

  public EntityMod(TxnOpType op, String entityType, String entityName, Long valueId, String valueUuid) {
    this.op = op;
    this.entityType = entityType;
    this.entityName = entityName;
    this.valueId = valueId;
    this.valueUuid = valueUuid;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  @SuppressWarnings("unused")
  private void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the {@link TxnOpType} corresponding to the type of operation made on the tracked object.
   *
   * @return the operation type
   */
  @Transient
  public TxnOpType getOp() {
    return op;
  }

  @Column(name = "op", nullable = false)
  private byte getOpByte() {
    switch (op) {
    case DELETE:
      return 1;
    case UPSERT:
      return 2;
    }
    throw new IllegalStateException("Invalid op type: " + op);
  }

  @SuppressWarnings("unused")
  private void setOpByte(byte b) {
    if (b == 1) {
      this.op = TxnOpType.DELETE;
    } else if (b == 2) {
      this.op = TxnOpType.UPSERT;
    } else {
      throw new IllegalArgumentException("Invalid op type: " + op);
    }
  }

  /**
   * Gets the string representation of the qualified name of the entity whose instance was changed (e.g.
   * {http://www.appian.com/ae/types/2009}RecordType).
   *
   * @return the string representation of the qualified name
   */
  @Column(name = "entity_type", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getEntityType() {
    return entityType;
  }

  /**
   * Gets the qualified name of the entity whose instance was changed (e.g.
   * {http://www.appian.com/ae/types/2009}RecordType).
   *
   * @return the qualified name
   */
  @Transient
  public QName getEntityTypeQName() {
    return QName.valueOf(entityType);
  }

  @SuppressWarnings("unused")
  private void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  /**
   * Gets the name of the persistentClass corresponding to the entity whose instance was changed (e.g.
   * "com.appiancorp.record.domain.RecordType")
   *
   * @return the entity name
   */
  @Column(name = "entity_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getEntityName() {
    return entityName;
  }

  @SuppressWarnings("unused")
  private void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  /**
   * Gets the id of the value that was changed.
   *
   * @return the id
   */
  @Column(name = "value_id", nullable = true)
  public Long getValueId() {
    return valueId;
  }

  @SuppressWarnings("unused")
  private void setValueId(Long valueId) {
    this.valueId = valueId;
  }

  /**
   * Gets the uuid of the value that was changed.
   *
   * @return the uuid
   */
  @Column(name = "value_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getValueUuid() {
    return valueUuid;
  }

  @SuppressWarnings("unused")
  private void setValueUuid(String valueUuid) {
    this.valueUuid = valueUuid;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EntityMod{op=")
        .append(op)
        .append(", entityName=")
        .append(entityName)
        .append(", entityType=")
        .append(entityType)
        .append(", valueId=")
        .append(valueId)
        .append(", valueUuid=")
        .append(valueUuid)
        .append("}");
    return builder.toString();
  }
}
