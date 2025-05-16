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
import com.appiancorp.miningdatasync.data.MiningProcess;
import com.appiancorp.processinsightsgeneration.service.MiningSuggestion;

@Entity
@Table(name = "mining_suggestions_scope")
public class MiningScopeSuggestion implements MiningSuggestion {
  public static final String PROP_MINING_PROCESS = "miningProcess";
  public static final String PROP_SUGGESTION_ID = "id";

  private Long id;
  private MiningProcess miningProcess;
  private Long createdTime;
  private String suggestionsJson;

  public MiningScopeSuggestion() {}

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
  @JoinColumn(name = "process_id", nullable = false, updatable = false)
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  public void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
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
    if (!(o instanceof MiningScopeSuggestion)) {
      return false;
    }
    MiningScopeSuggestion that = (MiningScopeSuggestion)o;
    MiningProcess otherMiningProcess = that.getMiningProcess();
    boolean miningProcessEquals = (miningProcess == null && otherMiningProcess == null) ||
        (miningProcess != null && otherMiningProcess != null && Objects.equals(miningProcess.getId(), otherMiningProcess.getId()));
    return Objects.equals(id, that.getId()) && miningProcessEquals &&
        Objects.equals(createdTime, that.getCreatedTime()) &&
        Objects.equals(suggestionsJson, that.getSuggestionsJson());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createdTime, suggestionsJson);
  }

  @Override
  public String toString() {
    String processId = miningProcess == null ? "null" : String.valueOf(miningProcess.getId());
    return "MiningScopeSuggestion{" + "id=" + id + ", processId=" + processId +
        ", createdTime=" + createdTime + ", suggestionsJson=" + suggestionsJson + '}';
  }
}
