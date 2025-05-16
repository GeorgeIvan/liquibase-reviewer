package com.appiancorp.dataset.entities;

import static com.appiancorp.dataset.entities.DatasetCustomFieldInfoEntity.DATASET_FIELD_NAME;

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
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.appian.core.persist.Constants;
import com.appiancorp.dataset.DatasetField;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "dataset_field")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public final class DatasetFieldEntity implements DatasetField<DatasetCustomFieldInfoEntity,DatasetEntity> {
  public static final String PROP_DATASET_CUSTOM_FIELD_INFO = "datasetCustomFieldInfo";
  public static final String PROP_REL_PATH_FROM_ROOT = "relPathFromRoot";
  public static final String PROP_RECORD_FIELD_UUID = "recordFieldUuid";
  public static final int DATASET_FIELD_DISPLAY_NAME_MAX_LENGTH = 300;
  static final String DATASET_ID_COLUMN_NAME = "dataset_id";
  static final String DATASET_FIELD_ID_COLUMN_NAME = "id";

  private Long id;
  private String uuid;
  private DatasetEntity dataset;
  private String relPathFromRoot;
  private String recordFieldUuid;
  private String displayName;
  private String description;
  private DatasetCustomFieldInfoEntity datasetCustomFieldInfo;
  private String recordTypeUuid;

  public DatasetFieldEntity() {

  }

  private DatasetFieldEntity(
      Long id,
      String uuid,
      DatasetEntity dataset,
      String relPathFromRoot,
      String recordFieldUuid,
      String displayName,
      String description,
      DatasetCustomFieldInfoEntity datasetCustomFieldInfo,
      String recordTypeUuid) {
    ensureDisplayNameDoesNotExceedMaxLength(displayName);
    this.id = id;
    // Don't try to autopopulate mandatory fields; it is more risky approach when
    // the database has unique constraints on those fields.
    this.uuid = uuid;
    this.dataset = dataset;
    this.relPathFromRoot = relPathFromRoot;
    this.recordFieldUuid = recordFieldUuid;
    this.displayName = displayName;
    this.description = description;
    this.datasetCustomFieldInfo = datasetCustomFieldInfo;
    this.recordTypeUuid = recordTypeUuid;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = DATASET_FIELD_ID_COLUMN_NAME, nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", nullable = false, updatable = false, unique = true)
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = DATASET_ID_COLUMN_NAME, nullable = false, insertable = false, updatable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public DatasetEntity getDataset() {
    return dataset;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDataset(DatasetEntity dataset) {
    this.dataset = dataset;
  }

  @Override
  @Column(name = "rel_path_from_root", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getRelPathFromRoot() {
    return relPathFromRoot;
  }

  public void setRelPathFromRoot(String relPathFromRoot) {
    this.relPathFromRoot = relPathFromRoot;
  }

  @Column(name = "record_field_uuid", nullable = true, unique = true)
  public String getRecordFieldUuid() {
    return recordFieldUuid;
  }

  public void setRecordFieldUuid(String recordFieldUuid) {
    this.recordFieldUuid = recordFieldUuid;
  }

  @Column(name = "display_name", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    ensureDisplayNameDoesNotExceedMaxLength(displayName);
    this.displayName = displayName;
  }

  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @OneToOne(fetch = FetchType.EAGER, mappedBy = DATASET_FIELD_NAME, cascade = CascadeType.ALL)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public DatasetCustomFieldInfoEntity getDatasetCustomFieldInfo() {
    return datasetCustomFieldInfo;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDatasetCustomFieldInfo(DatasetCustomFieldInfoEntity datasetCustomFieldInfo) {
    this.datasetCustomFieldInfo = datasetCustomFieldInfo;
  }

  @Column(name = "record_type_uuid", updatable = false, nullable = true, length = Constants.COL_MAXLEN_UUID)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  public void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, dataset, relPathFromRoot, recordFieldUuid, displayName, description,
        datasetCustomFieldInfo, recordTypeUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DatasetFieldEntity other = (DatasetFieldEntity)obj;
    return Objects.equals(this.uuid, other.uuid) && Objects.equals(this.dataset, other.dataset) &&
        Objects.equals(this.relPathFromRoot, other.relPathFromRoot) &&
        Objects.equals(this.recordFieldUuid, other.recordFieldUuid) &&
        Objects.equals(this.displayName, other.displayName) &&
        Objects.equals(this.description, other.description) &&
        Objects.equals(this.datasetCustomFieldInfo, other.datasetCustomFieldInfo) &&
        Objects.equals(this.recordTypeUuid, other.recordTypeUuid);
  }

  @Override
  public String toString() {
    return "DatasetField [uuid=" + uuid + ", dataset=" + dataset + ", relPathFromRoot=" + relPathFromRoot +
        ", recordFieldUuid=" + recordFieldUuid + ", displayName=" + displayName + ", description=" +
        description + ", datasetCustomFieldInfo=" + datasetCustomFieldInfo +
        ", recordTypeUuid=" + recordTypeUuid + "]";
  }

  @Transient
  public String getFieldPath() {
    if (StringUtils.isNotBlank(this.relPathFromRoot)) {
      return this.relPathFromRoot + "/" + this.recordFieldUuid;
    } else {
      return this.recordFieldUuid;
    }
  }

  public static class DatasetFieldBuilder {
    private Long id;
    private String uuid;
    private DatasetEntity dataset;
    private String relPathFromRoot;
    private String recordFieldUuid;
    private String displayName;
    private String description;
    private DatasetCustomFieldInfoEntity datasetCustomFieldInfo;
    private String recordTypeUuid;

    public DatasetFieldBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public DatasetFieldBuilder uuid(String uuid) {
      this.uuid = uuid;
      return this;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DatasetFieldBuilder dataset(DatasetEntity dataset) {
      this.dataset = dataset;
      return this;
    }

    public DatasetFieldBuilder relPathFromRoot(String relPathFromRoot) {
      this.relPathFromRoot = relPathFromRoot;
      return this;
    }

    public DatasetFieldBuilder recordFieldUuid(String recordFieldUuid) {
      this.recordFieldUuid = recordFieldUuid;
      return this;
    }

    public DatasetFieldBuilder displayName(String displayName) {
      ensureDisplayNameDoesNotExceedMaxLength(displayName);
      this.displayName = displayName;
      return this;
    }

    public DatasetFieldBuilder description(String description) {
      this.description = description;
      return this;
    }

    public DatasetFieldBuilder datasetCustomFieldInfo(DatasetCustomFieldInfoEntity datasetCustomFieldInfo) {
      this.datasetCustomFieldInfo = datasetCustomFieldInfo;
      return this;
    }

    public DatasetFieldBuilder recordTypeUuid(String recordTypeUuid) {
      this.recordTypeUuid = recordTypeUuid;
      return this;
    }

    public DatasetFieldEntity build() {
      return new DatasetFieldEntity(id, uuid, dataset, relPathFromRoot, recordFieldUuid, displayName, description,
          datasetCustomFieldInfo, recordTypeUuid);
    }
  }

  /**
   * Throws an IllegalArgumentException if the provided displayName has a length greater than {@link #DATASET_FIELD_DISPLAY_NAME_MAX_LENGTH} characters
   */
  private static void ensureDisplayNameDoesNotExceedMaxLength(String displayName) {
    if (displayName != null) {
      int displayNameLength = displayName.length();
      if (displayNameLength > DATASET_FIELD_DISPLAY_NAME_MAX_LENGTH) {
        throw new IllegalArgumentException("displayName must not exceed " + DATASET_ID_COLUMN_NAME +
            " characters, but " + displayNameLength + " characters were provided");
      }
    }
  }
}
