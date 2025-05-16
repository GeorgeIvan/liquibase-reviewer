package com.appiancorp.designguidance.entities;

import static com.appiancorp.designguidance.persistence.DesignGuidancePersistenceConstants.COL_MAXLEN_INDEXABLE;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.dl.replicator.SynchronousReplicationState;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store a {@link SynchronousReplicationState} in RDBMS for recording synchronous
 * updates to Design Objects for calculating guidance.
 */
@Entity
@Table(name = "dg_sync_replication_trans")
public class DesignGuidanceSynchronousTransactionEntity implements DesignGuidanceSynchronousTransaction {
  public static final String PROP_ID = "id";
  public static final String PROP_SOURCE_KEY = "sourceKey";
  public static final String PROP_TXN_ID = "txnId";

  private Long id;
  private String sourceKey;
  private Long txnId;
  /**
   * Required for Hibernate
   */
  DesignGuidanceSynchronousTransactionEntity() {}

  public DesignGuidanceSynchronousTransactionEntity(
      Long id, String sourceKey, Long txnId) {
    this.id = id;
    this.sourceKey = sourceKey;
    this.txnId = txnId;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "source_key", length = COL_MAXLEN_INDEXABLE, nullable = false)
  public String getSourceKey() {
    return sourceKey;
  }

  @Override
  public void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  @Override
  @Column(name = "txn_id", nullable = false)
  public Long getTxnId() {
    return txnId;
  }

  @Override
  public void setTxnId(Long txnId) {
    this.txnId = txnId;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add(PROP_ID, id)
        .add(PROP_SOURCE_KEY, sourceKey)
        .add(PROP_TXN_ID, txnId)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignGuidanceSynchronousTransactionEntity that = (DesignGuidanceSynchronousTransactionEntity)o;
    return Objects.equals(sourceKey, that.sourceKey) && Objects.equals(txnId, that.txnId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceKey, txnId);
  }
}
