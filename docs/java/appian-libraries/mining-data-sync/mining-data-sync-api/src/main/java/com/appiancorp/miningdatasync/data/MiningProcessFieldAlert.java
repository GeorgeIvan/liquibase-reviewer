package com.appiancorp.miningdatasync.data;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = MiningProcessFieldAlert.MINING_PROCESS_WARNING_ALERT)
public class MiningProcessFieldAlert {

  static final String MINING_PROCESS_WARNING_ALERT = "mining_process_fld_alert";
  static final String PROP_MINING_PROCESS_IN_PROCESS_ALERT = "miningProcess";
  static final String PROP_MINING_PROCESS_FIELD_IN_PROCESS_ALERT = "miningProcessField";

  private Long id;
  private MiningProcess miningProcess;
  private MiningProcessField miningProcessField;
  private MiningProcessAlertType miningProcessAlertType;

  /**
   * For Hibernate only
   */
  @SuppressWarnings("unused")
  private MiningProcessFieldAlert() { }

  public MiningProcessFieldAlert(MiningProcess miningProcess, MiningProcessField miningProcessField, MiningProcessAlertType alertType) {
    this.miningProcess = miningProcess;
    this.miningProcessField = miningProcessField;
    this.miningProcessAlertType = alertType;
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

  /* Don't cascade anything to the parent object */
  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "mining_process_id", nullable = false, updatable = false)
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  @SuppressWarnings("unused")
  private void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "mining_process_field_id", updatable = false)
  public MiningProcessField getMiningProcessField() {
    return miningProcessField;
  }

  @SuppressWarnings("unused")
  private void setMiningProcessField(MiningProcessField miningProcessField) {
    this.miningProcessField = miningProcessField;
  }

  @Column(name = "alert_type", nullable = false, updatable = false)
  @SuppressWarnings("unused")
  private Byte getAlertTypeByte() {
    if (miningProcessAlertType == null) {
      return null;
    }
    return miningProcessAlertType.getValue();
  }

  @SuppressWarnings("unused")
  private void setAlertTypeByte(Byte alertTypeByte) {
    if (alertTypeByte == null) {
      this.miningProcessAlertType = null;
      return;
    }
    this.miningProcessAlertType = MiningProcessAlertType.valueOf(alertTypeByte);
  }

  @Transient
  public boolean isEventAlert() {
    return getMiningProcessField().getMiningProcessProvider().getProviderType() ==
        MiningProcessProvider.MiningProcessProviderType.EVENT;
  }

  @Transient
  public MiningProcessAlertType getAlertType() {
    return miningProcessAlertType;
  }

  @Override
  public int hashCode() {
    // We expect that a particular field will not have the same alert type more than once.
    // Hence we only hash the id and type properties.
    return Objects.hash(id, miningProcessAlertType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MiningProcessFieldAlert other = (MiningProcessFieldAlert)obj;
    // We expect that a particular field will not have the same alert type more than once.
    // Hence we only compare the id and type properties.
    return Objects.equals(id, other.id) &&
        Objects.equals(miningProcessAlertType, other.miningProcessAlertType);
  }

  @Override
  public String toString() {
    return "MiningProcessWarningField [id=" + id + ", miningProcessAlertType=" + miningProcessAlertType +
      ", miningProcessId=" + miningProcess.getId() + ", miningProcessFieldId=" + miningProcessField.getId() + "]";
  }
}
