package com.appiancorp.processinsightsgeneration.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.processHq.persistence.entities.MiningScope;
import com.appiancorp.processinsightsgeneration.service.MiningSuggestion;

@Entity
@Table(name = "mining_suggestions_kpi")
public class MiningKpiSuggestion implements MiningSuggestion {
  public static final String PROP_SUGGESTION_ID = "id";
  public static final String PROP_MINING_SCOPE = "miningScope";

  private Long id;
  private MiningScope miningScope;
  private Long createdTime;
  private String suggestionsJson;

  public MiningKpiSuggestion() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "scope_id", nullable = false, updatable = false)
  public MiningScope getMiningScope() {
    return miningScope;
  }

  public void setMiningScope(MiningScope miningScope) {
    this.miningScope = miningScope;
  }

  @Override
  @Column(name = "created_time", nullable = false)
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Override
  @Lob
  @Column(name = "suggestions_json", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  public String getSuggestionsJson() {
    return suggestionsJson;
  }

  public void setSuggestionsJson(String suggestionsJson) {
    this.suggestionsJson = suggestionsJson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpiSuggestion)) {
      return false;
    }
    MiningKpiSuggestion that = (MiningKpiSuggestion)o;
    MiningScope otherMiningScope = that.getMiningScope();
    boolean miningScopeEquals = (miningScope == null && otherMiningScope == null) ||
        (miningScope != null && otherMiningScope != null && Objects.equals(miningScope.getId(), otherMiningScope.getId()));
    return Objects.equals(id, that.getId()) && miningScopeEquals &&
        Objects.equals(createdTime, that.getCreatedTime()) &&
        Objects.equals(suggestionsJson, that.getSuggestionsJson());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createdTime, suggestionsJson);
  }

  @Override
  public String toString() {
    String scopeId = miningScope == null ? "null" : String.valueOf(miningScope.getId());
    return "MiningKpiSuggestion{" + "id=" + id + ", scopeId=" + scopeId +
        ", createdTime=" + createdTime + ", suggestionsJson=" + suggestionsJson + '}';
  }
}
