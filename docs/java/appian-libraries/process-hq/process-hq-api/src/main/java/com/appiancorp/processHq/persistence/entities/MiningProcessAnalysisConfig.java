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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.miningdatasync.data.MiningProcess;

@Entity
@Table(name = "mining_process_analysis_cfg")
public class MiningProcessAnalysisConfig {
  public static final String PROP_MINING_PROCESS = "miningProcess";
  public static final String PROP_DEFAULT_SCOPE = "defaultScope";

  private Long id;
  private MiningProcess miningProcess;
  private String currency;
  private String costCustomFieldUuid;
  private Set<MiningAnalysisErrorConfig> miningAnalysisErrorConfigs = new HashSet<>();
  private MiningScope defaultScope;

  public MiningProcessAnalysisConfig() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "currency", nullable = true, length = 3)
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Column(name = "cost_custom_field_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  public String getCostCustomFieldUuid() {
    return costCustomFieldUuid;
  }

  public void setCostCustomFieldUuid(String costCustomFieldUuid) {
    this.costCustomFieldUuid = costCustomFieldUuid;
  }

  @OneToOne(fetch = FetchType.EAGER, cascade = {})
  @JoinColumn(name = "default_scope_id",  nullable = false)
  public MiningScope getDefaultScope() {
    return defaultScope;
  }

  public void setDefaultScope(MiningScope defaultScope) {
    this.defaultScope = defaultScope;
  }

  @OneToOne(fetch = FetchType.EAGER, cascade = {})
  @JoinColumn(name = "mining_process_id", nullable = false)
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  public void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
  }

  @OneToMany(mappedBy = "miningProcessAnalysisConfig", fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
  @OrderBy
  public Set<MiningAnalysisErrorConfig> getMiningErrorConfigs() {
    return miningAnalysisErrorConfigs;
  }

  public void setMiningErrorConfigs(Set<MiningAnalysisErrorConfig> miningAnalysisErrorConfigs) {
    this.miningAnalysisErrorConfigs = miningAnalysisErrorConfigs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningProcessAnalysisConfig)) {
      return false;
    }
    MiningProcessAnalysisConfig that = (MiningProcessAnalysisConfig)o;
    return Objects.equals(id, that.getId()) && Objects.equals(costCustomFieldUuid, that.getCostCustomFieldUuid()) &&
        Objects.equals(miningProcess, that.getMiningProcess()) && Objects.equals(currency, that.getCurrency());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, miningProcess, costCustomFieldUuid, currency);
  }

  @Override
  public String toString() {
    return "MiningProcessAnalysisConfig{" + "id=" + id + ", miningProcess=" + miningProcess + ", currency='" +
        currency + '\'' + ", costCustomFieldUuid='" + costCustomFieldUuid + '\'' + '}';
  }
}
