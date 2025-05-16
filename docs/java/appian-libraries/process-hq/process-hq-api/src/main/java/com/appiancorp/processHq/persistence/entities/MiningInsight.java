package com.appiancorp.processHq.persistence.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.collab.CollabGroup;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi;
import com.appiancorp.processHq.persistence.entities.finding.MiningInsightFinding;
import com.appiancorp.processHq.persistence.entities.savings.MiningInsightSavingsCfg;

@SuppressWarnings("checkstyle:CyclomaticComplexityCheck")
@Entity
@Table(name = "mining_insight")
@CopilotClass(name = "insight", description = "An insight is a collection of findings that can be evaluated and combined into an actionable conclusion")
public class MiningInsight {
  public static final String PROP_INSIGHT_ID = "id";
  public static final String PROP_SAVINGS_CFG = "savingsCfg";
  public static final String PROP_INSIGHT_FINDINGS ="insightFindings";
  public static final String PROP_COMMENT ="notes";

  private long id;
  private String name;
  private String notes;
  private String creatorUuid;
  private long modifyTs;
  private MiningKpi miningKpi;
  private List<MiningInsightFinding> insightFindings;
  private MiningInsightSavingsCfg savingsCfg;
  private List<MiningInsightMetric> insightMetrics;
  private WarnFlagOption warnFlagOption;
  private Long warnFlagTs;
  private CollabGroup collabGroup;
  private MiningAnalysisTimeframe miningAnalysisTimeframe;

  public MiningInsight() {}

  public MiningInsight(
      long id,
      String name,
      String notes,
      String creatorUuid,
      long modifyTs,
      MiningKpi miningKpi,
      List<MiningInsightFinding> insightFindings,
      CollabGroup collabGroup,
      MiningAnalysisTimeframe miningAnalysisTimeframe) {
    this.id = id;
    this.name = name;
    this.notes = notes;
    this.creatorUuid = creatorUuid;
    this.modifyTs = modifyTs;
    this.miningKpi = miningKpi;
    this.insightFindings = insightFindings;
    this.warnFlagOption = WarnFlagOption.NONE;
    this.collabGroup = collabGroup;
    this.miningAnalysisTimeframe = miningAnalysisTimeframe;
  }

  public MiningInsight(MiningInsight miningInsight) {
    this.id = miningInsight.getId();
    this.name = miningInsight.getName();
    this.notes = miningInsight.getNotes();
    this.creatorUuid = miningInsight.getCreatorUuid();
    this.modifyTs = miningInsight.getModifyTs();
    this.miningKpi = miningInsight.getMiningKpi();
    this.insightFindings = miningInsight.getInsightFindings();
    this.savingsCfg = miningInsight.getSavingsCfg();
    this.insightMetrics = miningInsight.getInsightMetrics();
    this.warnFlagOption = miningInsight.getWarnFlagOption();
    this.warnFlagTs = miningInsight.getWarnFlagTs();
    this.collabGroup = miningInsight.getCollabGroup();
    this.miningAnalysisTimeframe = miningInsight.getMiningAnalysisTimeframe();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @CopilotField(name = "insightName")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Updating the notes should only happen through the {@code MiningInsightDao.updateDescription}
   * and not directly via the generic DAO update.
   * <p><tt>updatable = false</tt> is needed to skip updating the summary, which can be generated and
   * updated concurrently to the Insight update.</p>
   *
   * @return the Insight's summary
   */
  @Column(name = "notes", length = Constants.COL_MAXLEN_MAX_NON_CLOB, updatable = false)
  @Lob
  @CopilotField(name = "insightSummary")
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  @Column(name = "creator_uuid", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCreatorUuid() {
    return creatorUuid;
  }

  public void setCreatorUuid(String creatorUuid) {
    this.creatorUuid = creatorUuid;
  }

  @Column(name = "modify_ts", nullable = false)
  public long getModifyTs() {
    return modifyTs;
  }

  public void setModifyTs(long modifyTs) {
    this.modifyTs = modifyTs;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_kpi_id", referencedColumnName = "id", nullable = false)
  @CopilotField(name = "miningKpi")
  public MiningKpi getMiningKpi() {
    return miningKpi;
  }

  public void setMiningKpi(MiningKpi miningKpi) {
    this.miningKpi = miningKpi;
  }

  @OneToMany(mappedBy = "insight", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @CopilotField(name = "insightFindings", description = "The list of findings detected in this insight.")
  public List<MiningInsightFinding> getInsightFindings() {
    return insightFindings;
  }

  public void setInsightFindings(List<MiningInsightFinding> insightFindings) {
    this.insightFindings = insightFindings;
  }

  public void sortInsightFindingsByOrder() {
    insightFindings.sort(Comparator.comparingInt(MiningInsightFinding::getOrderIndex));
  }

  @OneToOne(mappedBy = "insight", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @CopilotField(name = "potentialSavings", description = "The amount of time that could be potentially saved if the process issues identified in the insight are resolved.")
  public MiningInsightSavingsCfg getSavingsCfg() {
    return savingsCfg;
  }

  public void setSavingsCfg(MiningInsightSavingsCfg savingsCfg) {
    this.savingsCfg = savingsCfg;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "mining_insight_metric",
      joinColumns = @JoinColumn(name = "mining_insight_id")
  )
  public List<MiningInsightMetric> getInsightMetrics() {
    return insightMetrics;
  }

  public void setInsightMetrics(List<MiningInsightMetric> insightMetrics) {
    this.insightMetrics = insightMetrics;
  }

  @Transient
  @CopilotField(name = "warningType", description = "Enums used to represent the possible states of the Insight for the purpose of checking its validity.")
  public WarnFlagOption getWarnFlagOption() {
    return warnFlagOption;
  }

  public void setWarnFlagOption(WarnFlagOption warnFlagOption) {
    this.warnFlagOption = warnFlagOption;
  }

  @Column(name = "warn_option")
  private Byte getWarnFlagOptionByte() {
    return warnFlagOption != null ? warnFlagOption.getCode() : WarnFlagOption.NONE.getCode();
  }

  private void setWarnFlagOptionByte(Byte type) {
    setWarnFlagOption(WarnFlagOption.valueOf(type));
  }

  @Column(name = "warn_ts")
  public Long getWarnFlagTs() {
    return warnFlagTs;
  }

  public void setWarnFlagTs(Long warnFlagTs) {
    this.warnFlagTs = warnFlagTs;
  }

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "collab_group_id", referencedColumnName = "id")
  public CollabGroup getCollabGroup() {
    return collabGroup;
  }

  public void setCollabGroup(CollabGroup collabGroup) {
    this.collabGroup = collabGroup;
  }

  @OneToOne(mappedBy = "insight", cascade = CascadeType.ALL, orphanRemoval = true)
  public MiningAnalysisTimeframe getMiningAnalysisTimeframe() {
    return miningAnalysisTimeframe;
  }

  public void setMiningAnalysisTimeframe(MiningAnalysisTimeframe miningAnalysisTimeframe) {
    this.miningAnalysisTimeframe = miningAnalysisTimeframe;
  }

  @Override
  public String toString() {
    return "MiningInsight{" + "id=" + id + ", name='" + name + '\'' +
        ", notes='" + notes + '\'' + ", creatorUuid='" + creatorUuid + '\'' +
        ", modifyTs=" + modifyTs + ", insightFindings=" + insightFindings + ", savingsCfg=" + savingsCfg +
        ", insightMetrics=" + insightMetrics + ", warnFlagOption=" + warnFlagOption +
        ", warnFlagTs=" + warnFlagTs + ", collabGroup=" + collabGroup + ", miningAnalysisTimeframe=" +
        miningAnalysisTimeframe.toString() + ", miningKpiId=" + miningKpi.getId() + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningInsight that)) {
      return false;
    }
    if(this.getClass() != o.getClass()){
      return false;
    }
    return id == that.id && modifyTs == that.modifyTs && Objects.equals(name, that.name) &&
        Objects.equals(notes, that.notes) && Objects.equals(creatorUuid, that.creatorUuid) &&
        Objects.equals(insightFindings, that.insightFindings) &&
        Objects.equals(savingsCfg, that.savingsCfg) && Objects.equals(insightMetrics, that.insightMetrics) &&
        Objects.equals(warnFlagOption, that.warnFlagOption) && Objects.equals(warnFlagTs, that.warnFlagTs) &&
        collabGroup.equals(that.collabGroup) && Objects.equals(miningAnalysisTimeframe, that.miningAnalysisTimeframe) &&
        Objects.equals(miningKpi, that.miningKpi);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, notes, creatorUuid, modifyTs, insightFindings, savingsCfg, insightMetrics,
        warnFlagOption, warnFlagTs, collabGroup, miningAnalysisTimeframe, miningKpi);
  }
}
