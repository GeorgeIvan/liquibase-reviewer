package com.appiancorp.processHq.persistence.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;

@Entity
@Table(name = "mining_anlys_error_cfg")
public class MiningAnalysisErrorConfig {
  public static final String PROP_ANALYSIS_CONFIG = "miningProcessAnalysisConfig";
  private Long id;
  private MiningProcessAnalysisConfig miningProcessAnalysisConfig;
  private String predecessor;
  private MiningAnalysisErrorConfigType configType;
  private Set<MiningAnalysisErrorConfigSuccessor> successors = new HashSet<>();

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  public MiningAnalysisErrorConfig setId(Long id) {
    this.id = id;
    return this;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_analysis_config_id", nullable = false)
  public MiningProcessAnalysisConfig getMiningProcessAnalysisConfig() {
    return miningProcessAnalysisConfig;
  }

  public void setMiningProcessAnalysisConfig(MiningProcessAnalysisConfig miningProcessAnalysisConfig) {
    this.miningProcessAnalysisConfig = miningProcessAnalysisConfig;
  }

  @Column(name = "predecessor", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getPredecessor() {
    return predecessor;
  }

  public MiningAnalysisErrorConfig setPredecessor(String predecessor) {
    this.predecessor = predecessor;
    return this;
  }

  @Transient
  public MiningAnalysisErrorConfigType getConfigType() {
    return configType;
  }

  public void setConfigType(MiningAnalysisErrorConfigType configType) {
    this.configType = configType;
  }

  @Column(name="config_type", nullable = true)
  private byte getErrorConfigByte() {
    if (configType == null) {
      return MiningAnalysisErrorConfigType.ANY_ACTIVITY.getIndex();
    }
    return configType.getIndex();
  }

  private void setErrorConfigByte(byte errorConfigByte) {
    setConfigType(MiningAnalysisErrorConfigType.valueOf(errorConfigByte));
  }

  @OneToMany(mappedBy = "miningAnalysisErrorConfig", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
  @OrderBy
  public Set<MiningAnalysisErrorConfigSuccessor> getSuccessors() {
    return successors;
  }

  public void setSuccessors(Set<MiningAnalysisErrorConfigSuccessor> successors) {
    this.successors = successors;
  }

  @Override
  public String toString() {
    return "MiningAnalysisErrorConfig{" + "id=" + id + ", miningProcessAnalysisConfig=" +
        miningProcessAnalysisConfig + ", predecessor='" + predecessor + '\'' + ", configType=" + configType +
        ", successors=" + successors + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningAnalysisErrorConfig that = (MiningAnalysisErrorConfig)o;
    return Objects.equals(id, that.id) &&
        Objects.equals(predecessor, that.predecessor) && configType == that.configType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, predecessor, configType);
  }
}
