package com.appiancorp.processHq.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;

@Entity
@Table(name = "mining_anlys_error_cfg_succ")
public class MiningAnalysisErrorConfigSuccessor {
  private Long id;
  private String successor;
  private MiningAnalysisErrorConfig miningAnalysisErrorConfig;

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public MiningAnalysisErrorConfigSuccessor setId(Long id) {
    this.id = id;
    return this;
  }

  @Column(name = "successor", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getSuccessor() {
    return successor;
  }

  public MiningAnalysisErrorConfigSuccessor setSuccessor(String successor) {
    this.successor = successor;
    return this;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_anlys_error_cfg_id", nullable = false)
  public MiningAnalysisErrorConfig getMiningAnalysisErrorConfig() {
    return miningAnalysisErrorConfig;
  }

  public void setMiningAnalysisErrorConfig(MiningAnalysisErrorConfig miningAnalysisErrorConfig) {
    this.miningAnalysisErrorConfig = miningAnalysisErrorConfig;
  }

  @Override
  public String toString() {
    return "MiningAnalysisErrorConfigSuccessor{" + "id=" + id + ", successor='" + successor + '\'' +
        ", miningAnalysisErrorConfig=" + miningAnalysisErrorConfig + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningAnalysisErrorConfigSuccessor that = (MiningAnalysisErrorConfigSuccessor)o;
    return Objects.equals(id, that.id) && Objects.equals(successor, that.successor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, successor);
  }
}
