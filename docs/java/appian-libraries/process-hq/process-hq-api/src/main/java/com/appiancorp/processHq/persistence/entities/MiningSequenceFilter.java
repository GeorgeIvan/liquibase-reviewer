package com.appiancorp.processHq.persistence.entities;

import static com.google.common.base.Objects.equal;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;

@Entity
@Table(name = "mining_seq_filter")
@CopilotClass(name = "sequenceFilter", description = "A filter for selecting Cases with the selected sequence.")
public class MiningSequenceFilter {

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private long id;

  @Column(name = "pre_activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  private String preActivityName;

  @Column(name = "succ_activity_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  private String succActivityName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mining_filter_group_id", referencedColumnName = "id", nullable = false)
  private MiningFilterGroup filterGroup;

  @Column(name = "inverted", nullable = false)
  private boolean inverted;

  @Column(name = "is_direct", nullable = false)
  private boolean isDirect;

  @Column(name = "created_ts", nullable = false)
  private Long createdTs;

  public MiningSequenceFilter() {}

  public MiningSequenceFilter(
      Long id,
      MiningFilterGroup filterGroup,
      String preActivityName,
      String succActivityName,
      boolean inverted,
      boolean isDirect,
      Long createdTs
  ) {
    this.id = id;
    this.filterGroup = filterGroup;
    this.inverted = inverted;
    this.isDirect = isDirect;
    this.preActivityName = preActivityName;
    this.succActivityName = succActivityName;
    this.createdTs = createdTs;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @CopilotField(name = "pre_activity", description = "The name of selected predecessor activity.")
  public String getPreActivityName() {
    return preActivityName;
  }

  public void setPreActivityName(String preActivityName) {
    this.preActivityName = preActivityName;
  }

  @CopilotField(name = "succ_activity", description = "The name of selected successor activity.")
  public String getSuccActivityName() {
    return succActivityName;
  }

  public void setSuccActivityName(String succActivityName) {
    this.succActivityName = succActivityName;
  }

  public void setFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.filterGroup = miningFilterGroup;
  }

  @CopilotField(name = "inverted", description = "Indicates if the filter should equal or not equal the sequence")
  public boolean getInverted() {
    return inverted;
  }

  public void setInverted(boolean inverted) {
    this.inverted = inverted;
  }

  @CopilotField(name = "isDirect", description = "The flag indicates if the filter set to consider a direct sequence.")
  public boolean isDirect() {
    return isDirect;
  }

  public void setIsDirect(boolean isDirect) {
    this.isDirect = isDirect;
  }

  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof MiningSequenceFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningSequenceFilter other = (MiningSequenceFilter) object;
    return equal(this.getPreActivityName(), other.getPreActivityName()) &&
        equal(this.getSuccActivityName(), other.getSuccActivityName()) &&
        equal(this.getInverted(), other.getInverted()) &&
        equal(this.isDirect(), other.isDirect()) &&
        equal(this.getCreatedTs(), other.getCreatedTs());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getPreActivityName(), this.getSuccActivityName(),
        this.isDirect(), this.getInverted(), this.getCreatedTs());
  }

  @Override
  public String toString() {
    return "MiningSequenceFilter{" + "id=" + id + ", preActivityName='" + preActivityName + '\'' +
        ", succActivityName='" + succActivityName + '\'' + ", inverted=" +
        inverted + ", isDirect=" + isDirect + '\'' + ", createdTs=" + createdTs +'}';
  }
}
