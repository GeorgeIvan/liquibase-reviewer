package com.appiancorp.miningdatasync.data;

import static com.appiancorp.miningdatasync.data.MiningProcessAdHocEvent.PROP_MINING_PROCESS_FIELD_IN_AD_HOC_EVENT;
import static com.appiancorp.miningdatasync.data.MiningProcessFieldAlert.PROP_MINING_PROCESS_FIELD_IN_PROCESS_ALERT;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.dataset.entities.DatasetFieldEntity;

@Entity
@Table(name = "mining_process_field")
public class MiningProcessField {
  public static final String PROP_SEMANTIC_BYTE = "semanticByte";
  public static final String PROP_DATASET_FIELD = "datasetField";
  private Long id;
  private DatasetFieldEntity datasetField;
  private MiningProcessProvider miningProcessProvider;
  private MiningDataSemanticType semantic;
  private String nameInMining;
  private MiningDataSemanticType semanticInMining;
  private String descriptionInMining;
  private Set<MiningProcessFieldAlert> miningProcessFieldAlerts = new HashSet<>();
  private Set<MiningProcessAdHocEvent> miningProcessAdHocEvents = new HashSet<>();

  /**
   * Required for Hibernate
   */
  private MiningProcessField() {
  }

  public MiningProcessField(
      MiningDataSemanticType semantic,
      MiningProcessProvider miningProcessProvider) {
    this(semantic, miningProcessProvider, null);
  }

  public MiningProcessField(
      MiningDataSemanticType semantic,
      MiningProcessProvider miningProcessProvider,
      DatasetFieldEntity datasetField) {
    // We set both semantic and semanticInMining because in the happy path they should be synced
    // When semantic is deprecated, we can switch to only using semanticInMining
    this.semantic = semantic;
    this.semanticInMining = semantic;
    this.miningProcessProvider = miningProcessProvider;
    this.datasetField = datasetField;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_process_provider_id", nullable = false)
  public MiningProcessProvider getMiningProcessProvider() {
    return miningProcessProvider;
  }

  public void setMiningProcessProvider(MiningProcessProvider miningProcessProvider) {
    this.miningProcessProvider = miningProcessProvider;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dataset_field_id", nullable = false, updatable = false)
  public DatasetFieldEntity getDatasetField() {
    return datasetField;
  }

  public void setDatasetField(DatasetFieldEntity datasetField) {
    this.datasetField = datasetField;
  }

  @Column(name = "semantic", nullable = false)
  public Byte getSemanticByte() {
    if (semantic == null) {
      return null;
    }
    return semantic.getValue();
  }

  public void setSemanticByte(Byte semantic) {
    if (semantic == null) {
      this.semantic = null;
      return;
    }
    this.semantic = MiningDataSemanticType.valueOf(semantic);
  }

  @Transient
  public MiningDataSemanticType getSemantic() {
    return semantic;
  }

  public void setSemantic(MiningDataSemanticType semantic) {
    this.semantic = semantic;
  }

  @Column(name = "name_in_mining", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getNameInMining() {
    return nameInMining;
  }

  public void setNameInMining(String nameInMining) {
    this.nameInMining = nameInMining;
  }

  @Column(name = "semantic_in_mining", nullable = true)
  public Byte getSemanticByteInMining() {
    if (semanticInMining == null) {
      return null;
    }
    return semanticInMining.getValue();
  }

  public void setSemanticByteInMining(Byte semanticInMining) {
    if (semanticInMining == null) {
      this.semanticInMining = null;
      return;
    }
    this.semanticInMining = MiningDataSemanticType.valueOf(semanticInMining);
  }

  @Transient
  public MiningDataSemanticType getSemanticInMining() {
    return semanticInMining;
  }

  public void setSemanticInMining(MiningDataSemanticType semanticInMining) {
    this.semanticInMining = semanticInMining;
  }

  @Column(name = "description_in_mining", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescriptionInMining() {
    return descriptionInMining;
  }

  public void setDescriptionInMining(String descriptionInMining) {
    this.descriptionInMining = descriptionInMining;
  }

  /**
   * The set of alerts associated with this field. Changes to alerts are
   * persisted through this field.
   */
  @OneToMany(mappedBy = PROP_MINING_PROCESS_FIELD_IN_PROCESS_ALERT, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<MiningProcessFieldAlert> getMiningProcessAlerts() {
    return miningProcessFieldAlerts;
  }

  public void setMiningProcessAlerts(Set<MiningProcessFieldAlert> miningProcessFieldAlerts) {
    this.miningProcessFieldAlerts = miningProcessFieldAlerts;
  }

  public void addAlertIfNotPresent(MiningProcessAlertType alertType) {
    for(MiningProcessFieldAlert miningProcessFieldAlert : miningProcessFieldAlerts) {
      if (miningProcessFieldAlert.getAlertType() == alertType) {
        return;
      }
    }
    MiningProcessFieldAlert createdAlert = new MiningProcessFieldAlert(
        miningProcessProvider.getMiningProcess(), this, alertType);
    miningProcessFieldAlerts.add(createdAlert);
  }

  public void removeAlertIfPresent(MiningProcessAlertType alertType) {
    miningProcessFieldAlerts.removeIf(alert -> alert.getAlertType() == alertType);
  }

  @OneToMany(mappedBy = PROP_MINING_PROCESS_FIELD_IN_AD_HOC_EVENT, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<MiningProcessAdHocEvent> getMiningProcessAdHocEvents() {
    return miningProcessAdHocEvents;
  }

  public void setMiningProcessAdHocEvents(Set<MiningProcessAdHocEvent> miningProcessAdHocEvents) {
    this.miningProcessAdHocEvents = miningProcessAdHocEvents;
  }

  @Override
  public int hashCode() {
    return Objects.hash(semantic, nameInMining, datasetField, miningProcessFieldAlerts, miningProcessAdHocEvents);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MiningProcessField other = (MiningProcessField)obj;
    return Objects.equals(this.semantic, other.semantic) &&
        Objects.equals(this.nameInMining, other.nameInMining) &&
        Objects.equals(this.datasetField, other.datasetField) &&
        Objects.equals(miningProcessFieldAlerts, other.miningProcessFieldAlerts) &&
        Objects.equals(miningProcessAdHocEvents, other.miningProcessAdHocEvents);
  }

  @Override
  public String toString() {
    return "MiningProcessField [semantic=" + semantic +
        ", nameInMining=" + nameInMining + ", datasetField=" + datasetField +", alert=" +
        miningProcessFieldAlerts + ", adHocEvents= " + miningProcessAdHocEvents +"]";
  }
}
