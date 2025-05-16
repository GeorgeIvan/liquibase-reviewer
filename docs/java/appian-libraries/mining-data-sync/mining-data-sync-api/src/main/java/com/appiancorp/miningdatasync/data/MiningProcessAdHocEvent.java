package com.appiancorp.miningdatasync.data;

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
@Table(name = MiningProcessAdHocEvent.MINING_PROCESS_AD_HOC_EVENT)
public class MiningProcessAdHocEvent {
  static final String MINING_PROCESS_AD_HOC_EVENT = "mining_prc_adhoc_event";
  static final String PROP_MINING_PROCESS_IN_AD_HOC_EVENT = "miningProcess";
  static final String PROP_MINING_PROCESS_FIELD_IN_AD_HOC_EVENT = "miningProcessField";
  public static final String PROP_EVENT_NAME = "eventName";


  private Long id;
  private MiningProcess miningProcess;
  private MiningProcessField miningProcessField;
  private boolean isDismissed;
  private String eventName;

  /**
   * For Hibernate only
   */
  @SuppressWarnings("unused")
  private MiningProcessAdHocEvent() {
  }

  public MiningProcessAdHocEvent(
      MiningProcess miningProcess,
      MiningProcessField miningProcessField,
      boolean isDismissed,
      String eventName) {
    this.miningProcess = miningProcess;
    this.miningProcessField = miningProcessField;
    this.isDismissed = isDismissed;
    this.eventName = eventName;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "mining_process_id", nullable = false, updatable = false)
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  @SuppressWarnings("unused")
  private void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
  }

  /** If you intend to fetch the mining process field to modify it, fetch it from the
   * {@link MiningProcess} instead. If you edit the fields from this method, set them to the process, and run
   * miningProcessService.update(id), the fields will not update as intended
   */
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "mining_process_field_id", updatable = false)
  public MiningProcessField getMiningProcessField() {
    return miningProcessField;
  }

  @SuppressWarnings("unused")
  private void setMiningProcessField(MiningProcessField miningProcessField) {
    this.miningProcessField = miningProcessField;
  }

  @Column(name = "is_dismissed", nullable = false)
  @SuppressWarnings("unused")
  public boolean getIsDismissed() {
    return isDismissed;
  }

  public void setIsDismissed(boolean isDismissed) {
    this.isDismissed = isDismissed;
  }

  @Column(name = "event_name", nullable = false, updatable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @SuppressWarnings("unused")
  public String getEventName() {
    return eventName;
  }

  @SuppressWarnings("unused")
  private void setEventName(String eventName) {
    this.eventName = eventName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventName, isDismissed);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MiningProcessAdHocEvent other = (MiningProcessAdHocEvent)obj;
    return Objects.equals(id, other.id) && Objects.equals(isDismissed, other.getIsDismissed()) &&
        Objects.equals(eventName, other.getEventName());
  }

  @Override
  public String toString() {
    return "MiningProcessAdHocEvent [id=" + id + ", isDismissed=" + isDismissed + ", eventName= " +
        eventName + ", miningProcessId=" + (miningProcess == null ? "" : miningProcess.getId()) +
        ", miningProcessFieldId=" + miningProcessField.getId() + "]";
  }
}
