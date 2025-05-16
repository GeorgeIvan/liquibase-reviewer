package com.appiancorp.designguidance.entities;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appian.dl.replicator.ReplicationState;

/**
 * An entity class to store a {@link ReplicationState} in RDBMS for maintaining the state of Design Guidance
 * replication in RDBMS. Contains the mappings to the RDBMS table for Hibernate.
 */
@Entity
@Table(name = "dg_replication_state")
public class DesignGuidanceReplicationStateEntity implements DesignGuidanceReplicationState {
  /**
   * This constant is necessary for compatibility with the {@link ReplicationState} which now uses a
   * combination of the primary term and a sequence number to guarantee ordering and no conflicts. Since
   * we are working against a single RDBMS which gives us the guarantees we need, a simple version will work
   * for us. However, we must be compatible with the replication state bean, so we use this constant value
   * any time we need to supply a value for the primary term.
   */
  public static final Long RDBMS_NO_PRIMARY_TERM = -1L;
  private String sourceKey;
  private long version;

  // These hold the info from the last successful replication run (null if no successful run of replication
  // has completed after the sink was last cleared).
  private Long upToDateAsOf;
  private Long latestReplicatedTxnId;

  // These hold the info for replication that's currently running (null when replication is not in progress).
  private String replicatingServer;
  private Long replicatingServerHeartbeat;

  /**
   * Required for Hibernate
   */
  DesignGuidanceReplicationStateEntity() {}

  public DesignGuidanceReplicationStateEntity(
      String sourceKey,
      long version,
      Long upToDateAsOf,
      Long latestReplicatedTxnId,
      String replicatingServer,
      Long replicatingServerHeartbeat) {
    this.sourceKey = sourceKey;
    this.version = version;
    this.upToDateAsOf = upToDateAsOf;
    this.latestReplicatedTxnId = latestReplicatedTxnId;
    this.replicatingServer = replicatingServer;
    this.replicatingServerHeartbeat = replicatingServerHeartbeat;
  }

  @javax.persistence.Id
  @Column(name = "source_key", updatable = false, nullable = false)
  public String getSourceKey() {
    return sourceKey;
  }

  void setSourceKey(String sourceKey) {
    this.sourceKey = sourceKey;
  }

  /**
   * This field uses {@link Version} for optimistic locking. It must not be edited manually. Hibernate will
   * increment the version every time the field is updated.
   */
  @Version
  @Column(name = "version", updatable = true, nullable = false)
  public long getVersion() {
    return version;
  }

  void setVersion(Long version) {
    this.version = version;
  }

  @Column(name = "up_to_date_as_of", updatable = true, nullable = true)
  public Long getUpToDateAsOfLong() {
    return upToDateAsOf;
  }

  @Transient
  public Timestamp getUpToDateAsOf() {
    return upToDateAsOf != null ? new Timestamp(upToDateAsOf) : null;
  }

  public void setUpToDateAsOfLong(Long upToDateAsOf) {
    this.upToDateAsOf = upToDateAsOf;
  }

  public void setUpToDateAsOf(Timestamp upToDateAsOf) {
    this.upToDateAsOf = upToDateAsOf.getTime();
  }

  @Column(name = "latest_txn_id", nullable = true, updatable = true)
  public Long getLatestReplicatedTxnId() {
    return latestReplicatedTxnId;
  }

  public void setLatestReplicatedTxnId(Long latestReplicatedTxnId) {
    this.latestReplicatedTxnId = latestReplicatedTxnId;
  }

  @Column(name = "replicating_server", nullable = true, updatable = true)
  public String getReplicatingServer() {
    return replicatingServer;
  }

  public void setReplicatingServer(String replicatingServer) {
    this.replicatingServer = replicatingServer;
  }

  @Transient
  public boolean isReplicationNotInProgress() {
    return replicatingServer == null;
  }

  @Column(name = "replicating_server_heartbeat", nullable = true, updatable = true)
  public Long getReplicatingServerHeartbeatLong() {
    return replicatingServerHeartbeat;
  }

  @Transient
  public Timestamp getReplicatingServerHeartbeat() {
    return replicatingServerHeartbeat != null ? new Timestamp(replicatingServerHeartbeat) : null;
  }

  public void setReplicatingServerHeartbeatLong(Long replicatingServerHeartbeat) {
    this.replicatingServerHeartbeat = replicatingServerHeartbeat;
  }

  public void setReplicatingServerHeartbeat(Timestamp replicatingServerHeartbeat) {
    this.replicatingServerHeartbeat = replicatingServerHeartbeat.getTime();
  }

  public ReplicationState toReplicationState() {
    return ReplicationState.builder()
        .sourceKey(sourceKey)
        .seqNo(version)
        .primaryTerm(RDBMS_NO_PRIMARY_TERM)
        .upToDateAsOf(getUpToDateAsOf())
        .latestReplicatedTxnId(latestReplicatedTxnId)
        .replicatingServer(replicatingServer)
        .replicatingServerHeartbeat(getReplicatingServerHeartbeat())
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DesignGuidanceReplicationStateEntity that = (DesignGuidanceReplicationStateEntity)o;
    return new EqualsBuilder().append(version, that.version)
        .append(sourceKey, that.sourceKey)
        .append(upToDateAsOf, that.upToDateAsOf)
        .append(latestReplicatedTxnId, that.latestReplicatedTxnId)
        .append(replicatingServer, that.replicatingServer)
        .append(replicatingServerHeartbeat, that.replicatingServerHeartbeat)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceKey, version, upToDateAsOf, latestReplicatedTxnId, replicatingServer,
        replicatingServerHeartbeat);
  }

  @Override
  public String toString() {
    return "DesignGuidanceReplicationState{" + "sourceKey='" + sourceKey + '\'' + ", version=" + version +
        ", upToDateAsOf=" + upToDateAsOf + ", latestReplicatedTxnId=" + latestReplicatedTxnId +
        ", replicatingServer='" + replicatingServer + '\'' + ", replicatingServerHeartbeat=" +
        replicatingServerHeartbeat + '}';
  }
}
