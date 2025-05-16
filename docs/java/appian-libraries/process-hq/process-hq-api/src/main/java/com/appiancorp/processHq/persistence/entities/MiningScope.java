package com.appiancorp.processHq.persistence.entities;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.miningdatasync.data.MiningProcess;

@Entity
@Table(name = "mining_scope")
@CopilotClass(
    name = "view",
    exampleInputTemplate = "miningView_example_input_v1",
    description = "A subset of the data in a process that has been filtered and refined to focus on a specific attribute or quality (for example, closed cases or invalid cases)."
)
public class MiningScope {
  public static final String PROP_SCOPE_ID = "id";
  public static final String PROP_MODIFY_TS = "modifyTs";
  public static final String PROP_NAME = "name";
  public static final String PROP_INVALID = "invalid";
  public static final String PROP_MINING_PROCESS = "miningProcess";
  public static final String PROP_PINNED = "pinned";

  private long id;
  private MiningProcess miningProcess;
  private MiningFilterGroup miningFilterGroup;
  private String name;
  private String creatorUuid;
  private long modifyTs;
  private boolean invalid;
  private boolean pinned;
  private String description;

  public MiningScope() {}

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
  @CopilotField(name = "title")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "creator_uuid", length = Constants.COL_MAXLEN_UUID)
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
  @JoinColumn(name = "mining_process_id", nullable = false)
  @CopilotField(name = "parentMiningProcess", description = "The process is a parent of this View.")
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  public void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
  }

  @OneToOne(mappedBy = "miningScope", cascade = CascadeType.ALL, orphanRemoval = true)
  @CopilotField(name = "viewFilter", description = "The filter group contains activities and attributes included into this View.")
  public MiningFilterGroup getMiningFilterGroup() {
    return miningFilterGroup;
  }

  public void setMiningFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.miningFilterGroup = miningFilterGroup;
  }

  @Column(name = "invalid", nullable = false)
  @CopilotField(name = "isValid", description = "The flag indicates if the View is valid and not obsolete.")
  public boolean isInvalid() {
    return invalid;
  }

  public void setInvalid(boolean invalid) {
    this.invalid = invalid;
  }

  @Column(name = "pinned", nullable = false)
  @CopilotField(name = "pinned", description = "The flag indicates if the View is pinned on the process page.")
  public boolean isPinned() {
    return pinned;
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningScope)) {
      return false;
    }
    MiningScope that = (MiningScope)o;
    return id == that.getId() && Objects.equals(name, that.getName()) &&
        Objects.equals(miningProcess, that.getMiningProcess()) &&
        Objects.equals(miningFilterGroup, that.getMiningFilterGroup()) &&
        Objects.equals(creatorUuid, that.getCreatorUuid()) &&
        Objects.equals(modifyTs, that.getModifyTs()) &&
        Objects.equals(description, that.description) &&
        Objects.equals(invalid, that.isInvalid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, creatorUuid, modifyTs, invalid, description);
  }

  @Override
  public String toString() {
    return "MiningScope{" + "id=" + id + ", miningProcessId=" +
        (miningProcess == null ? null : miningProcess.getId()) + ", miningFilterGroup=" +
        (miningFilterGroup == null ? null : miningFilterGroup.getId()) + ", name='" + name + '\'' +
        ", description='" + description + '\'' + ", creatorUuid='" + creatorUuid + '\'' +
        ", modifyTs=" + modifyTs + ", numInvestigations=" + ", invalid=" + invalid + '}';
  }
}
