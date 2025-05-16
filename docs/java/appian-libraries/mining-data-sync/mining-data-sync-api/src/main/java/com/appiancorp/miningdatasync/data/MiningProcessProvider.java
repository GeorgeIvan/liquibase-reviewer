package com.appiancorp.miningdatasync.data;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.core.data.ImmutableDictionary;
import com.appiancorp.dataset.entities.DatasetCustomFieldInfoEntity;
import com.appiancorp.dataset.entities.DatasetEntity;
import com.appiancorp.dataset.entities.DatasetFieldEntity;

@Entity
@Table(name = "mining_process_provider")
public final class MiningProcessProvider {
  public static final String PROP_PROVIDER_TYPE_BYTE = "providerTypeByte";
  public static final String PROP_MINING_PROCESS_FIELDS = "miningProcessFields";
  public static final String PROP_MINING_PROCESS_FILTERS_CFG = "miningProcessProviderFiltersCfg";
  public static final String PROP_DATASET = "dataset";
  private Long id;
  private MiningProcess miningProcess;
  private MiningProcessProviderType providerType;
  private Set<MiningProcessField> miningProcessFields = new HashSet<>();
  private MiningProcessProviderFiltersCfg miningProcessProviderFiltersCfg;
  private DatasetEntity dataset;

  /**
   * Required for Hibernate
   */
  private MiningProcessProvider() { }

  private MiningProcessProvider(
      MiningProcessProviderType providerType,
      DatasetEntity dataset) {
    this.providerType = providerType;
    this.dataset = dataset;
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
  @JoinColumn(name = "mining_process_id", nullable = false)
  public MiningProcess getMiningProcess() {
    return miningProcess;
  }

  public void setMiningProcess(MiningProcess miningProcess) {
    this.miningProcess = miningProcess;
  }

  @OneToMany(mappedBy = "miningProcessProvider", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  public Set<MiningProcessField> getMiningProcessFields() {
    return miningProcessFields;
  }

  public void setMiningProcessFields(Set<MiningProcessField> miningProcessFields) {
    this.miningProcessFields = miningProcessFields;
  }

  @OneToOne(mappedBy = "miningProcessProvider", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  public MiningProcessProviderFiltersCfg getMiningProcessProviderFiltersCfg() {
    return miningProcessProviderFiltersCfg;
  }

  public void setMiningProcessProviderFiltersCfg(MiningProcessProviderFiltersCfg miningProcessProviderFiltersCfg) {
    this.miningProcessProviderFiltersCfg = miningProcessProviderFiltersCfg;
  }

  @Transient
  public MiningProcessProviderType getProviderType() {
    return providerType;
  }

  public void setProviderType(MiningProcessProviderType providerType) {
    this.providerType = providerType;
  }

  @Column(name = "provider_type", nullable = false)
  private Byte getProviderTypeByte() {
    if (providerType == null) {
      return null;
    }
    return providerType.getValue();
  }

  private void setProviderTypeByte(Byte providerType) {
    if (providerType == null) {
      this.providerType = null;
      return;
    }
    this.providerType = MiningProcessProviderType.valueOf(providerType);
  }

  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
  @JoinColumn(name = "dataset_id", nullable = false, updatable = false)
  public DatasetEntity getDataset() {
    return dataset;
  }

  public void setDataset(DatasetEntity dataset) {
    this.dataset = dataset;
  }

  /**
   * Creates {@link DatasetFieldEntity} and {@link MiningProcessField} associated with a normal
   * Record Type's field.
   *
   * @param fieldPath the path to Record Type's field
   * @param semantic  the {@link MiningDataSemanticType} of Record Type's field
   * @param recordTypeUuid the UUID of the Record Type which contains the field at the end of fieldPath
   * @param fieldPathToFieldRenameMap the fieldPath(s) (that is, if applicable,
   *                                  the relationshipPath to the field with the fieldUuid as the tail)
   *                                  to the respective overridden fieldRename(s)
   * @param fieldPathToDescriptionOverrideMap the fieldPath(s) (that is, if applicable,
   *                                  the relationshipPath to the field with the fieldUuid as the tail)
   *                                  to the respective overridden fieldDescription(s)
   */
  public void addOrReplaceMiningProcessFieldByPathUuid(
      String fieldPath,
      MiningDataSemanticType semantic,
      String recordTypeUuid,
      ImmutableDictionary fieldPathToFieldRenameMap,
      ImmutableDictionary fieldPathToDescriptionOverrideMap) {
    String displayName = (fieldPathToFieldRenameMap != null && fieldPathToFieldRenameMap.containsKey(fieldPath)) ?
      fieldPathToFieldRenameMap.get(fieldPath).toString().trim() : null;
    String description = (fieldPathToDescriptionOverrideMap != null && fieldPathToDescriptionOverrideMap.containsKey(fieldPath)) ?
        fieldPathToDescriptionOverrideMap.get(fieldPath).toString().trim() : null;
    MiningProcessField foundField = miningProcessFields.stream()
       .filter(field -> fieldPath.equals(field.getDatasetField().getFieldPath()))
       .findFirst()
       .orElse(null);
    if (foundField != null) {
      foundField.setSemantic(semantic);
      foundField.getDatasetField().setDisplayName(displayName);
      foundField.getDatasetField().setDescription(description);
    } else {
      int indexOfRecordUuid = fieldPath.lastIndexOf("/");
      String recordFieldUuid = fieldPath;
      String relPathFromRoot = "";
      if (indexOfRecordUuid != -1) {
        recordFieldUuid = fieldPath.substring(indexOfRecordUuid + 1);
        relPathFromRoot = fieldPath.substring(0, indexOfRecordUuid);
      }

      DatasetFieldEntity datasetField = new DatasetFieldEntity.DatasetFieldBuilder()
          .dataset(dataset)
          .uuid(UUID.randomUUID().toString())
          .relPathFromRoot(relPathFromRoot)
          .recordFieldUuid(recordFieldUuid)
          .recordTypeUuid(recordTypeUuid)
          .displayName(displayName)
          .description(description)
          .build();
      dataset.getDatasetFields().add(datasetField);
      MiningProcessField field = new MiningProcessFieldBuilder()
          .setSemantic(semantic)
          .setMiningProcessProvider(this)
          .setDatasetField(datasetField)
          .build();
      miningProcessFields.add(field);
    }
  }

  /**
   * Creates {@link DatasetFieldEntity} and {@link MiningProcessField} for the Analyst Custom Field
   * identified by the specified  {@link DatasetCustomFieldInfoEntity}.
   *
   * @param datasetFieldUuid       the <tt>uuid</tt> of {@link DatasetFieldEntity} when the update happens
   * @param semantic               the {@link MiningDataSemanticType} of Analyst Custom Field
   * @param datasetCustomFieldInfo the {@link DatasetCustomFieldInfoEntity} stores the information
   *                               specific for Analyst Custom Field
   * @param displayName            the field name of Analyst Custom Field
   * @param description            the description of Analyst Custom Field
   */
  public void createOrUpdateMiningProcessFieldForCustomField(
      String datasetFieldUuid,
      MiningDataSemanticType semantic,
      DatasetCustomFieldInfoEntity datasetCustomFieldInfo,
      String displayName,
      String description) {
    MiningProcessField miningProcessField = miningProcessFields.stream()
        .filter(field -> datasetFieldUuid.equals(field.getDatasetField().getUuid()))
        .findFirst()
        .orElse(null);
    DatasetFieldEntity datasetField;
    if (miningProcessField == null) {
      datasetField = new DatasetFieldEntity.DatasetFieldBuilder()
          .dataset(dataset)
          .uuid(datasetFieldUuid)
          .datasetCustomFieldInfo(datasetCustomFieldInfo)
          .build();
      dataset.getDatasetFields().add(datasetField);
      miningProcessField = new MiningProcessFieldBuilder()
          .setMiningProcessProvider(this)
          .setDatasetField(datasetField)
          .build();
      miningProcessFields.add(miningProcessField);
    } else {
      datasetField = miningProcessField.getDatasetField();
      datasetField.setDatasetCustomFieldInfo(datasetCustomFieldInfo);
    }
    datasetField.setDisplayName(displayName);
    datasetField.setDescription(description);
    datasetCustomFieldInfo.setDatasetField(datasetField);
    miningProcessField.setSemantic(semantic);
  }

  public MiningProcessField getMiningProcessFieldByFieldPath(String fieldPath) {
    if(fieldPath == null){
      return null;
    }
    return getMiningProcessFields().stream()
        .filter(miningProcessField -> fieldPath.equals(
            miningProcessField.getDatasetField().getFieldPath()))
        .findFirst()
        .orElse(null);
  }

  public void removeMiningFieldsNotInSetByPathUuid(
      Collection<String> miningProcessFieldPaths,
      Collection<String> analystCustomFieldUuids) {
    // Once MiningProcessField is removed, we have to explicitly remove DatasetFieldEntity in Dataset
    // associated with that MiningProcessField.
    Iterator<MiningProcessField> miningProcessFieldIterator = miningProcessFields.iterator();
    Set<DatasetFieldEntity> datasetFields = new HashSet<>(dataset.getDatasetFields());
    while (miningProcessFieldIterator.hasNext()) {
      MiningProcessField miningProcessField = miningProcessFieldIterator.next();
      DatasetFieldEntity datasetField = miningProcessField.getDatasetField();
      DatasetCustomFieldInfoEntity datasetCustomFieldInfo = datasetField.getDatasetCustomFieldInfo();
      boolean hasField = datasetCustomFieldInfo == null ?
          miningProcessFieldPaths.contains(datasetField.getFieldPath()) :
          analystCustomFieldUuids.contains(datasetField.getUuid());
      if (!hasField) {
        datasetFields.remove(datasetField);
        miningProcessFieldIterator.remove();
      }
    }
    dataset.setDatasetFields(datasetFields);
  }

  public static MiningProcessProviderBuilder builder() {
    return new MiningProcessProviderBuilder();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMiningProcessUuid(), providerType, dataset);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MiningProcessProvider other = (MiningProcessProvider)obj;
    return Objects.equals(this.getMiningProcessUuid(), other.getMiningProcessUuid()) &&
        Objects.equals(this.providerType, other.providerType) && Objects.equals(this.dataset, other.dataset) &&
        Objects.equals(this.miningProcessProviderFiltersCfg, other.miningProcessProviderFiltersCfg);
  }

  @Override
  public String toString() {
    return "MiningProcessProvider [miningProcess=" + getMiningProcessUuid()
        + ", providerType=" + providerType
        + ", processFields=" + miningProcessFields
        + ", dataset=" + dataset
        + ", filtersConfig=" + miningProcessProviderFiltersCfg
        + "]";
  }

  /**
   * The utility method for obtaining <tt>uuid</tt> of {@link MiningProcess}.
   *
   * @return the <tt>uuid</tt> of {@link MiningProcess} or <tt>null</tt>
   */
  @Transient
  private String getMiningProcessUuid() {
    return miningProcess == null ? null : miningProcess.getUuid();
  }

  public enum MiningProcessProviderType {
    CASE((byte)1, "recordsCaseAttributesMiningDataProviderFactoryUuid"),
    EVENT((byte)2, "recordsEventLogMiningDataProviderFactoryUuid");

    private final byte value;
    private final String dataProviderFactoryUuid;

    MiningProcessProviderType(byte value, String dataProviderFactoryUuid) {
      this.value = value;
      this.dataProviderFactoryUuid = dataProviderFactoryUuid;
    }

    public byte getValue() {
      return value;
    }

    public String getDataProviderFactoryUuid() {
      return dataProviderFactoryUuid;
    }

    public static MiningProcessProviderType valueOf(byte value) {
      for (MiningProcessProviderType providerType : values()) {
        if (providerType.getValue() == value) {
          return providerType;
        }
      }
      throw new IllegalArgumentException("unknown provider type value ["+value+"]");
    }
  }

  public static class MiningProcessProviderBuilder {
    private MiningProcessProviderType providerType;
    private DatasetEntity dataset;

    public MiningProcessProviderBuilder providerType(MiningProcessProviderType providerType) {
      this.providerType = providerType;
      return this;
    }

    public MiningProcessProviderBuilder dataset(DatasetEntity dataset) {
      this.dataset = dataset;
      return this;
    }

    public MiningProcessProvider build() {
      return new MiningProcessProvider(providerType, dataset);
    }
  }
}
