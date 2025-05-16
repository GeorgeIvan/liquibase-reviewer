package com.appiancorp.process.engine.db;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "arc_proc_ind")
public class ArchiveProcessIndex {

  public static final String LOCAL_PART = "ArchiveProcessIndex";

  public static final String ID_FIELD = "id";
  public static final String PROCESS_ID_FIELD = "pid";
  public static final String MODEL_UUID_FIELD = "modelUuid";
  public static final String CREATED_TIMESTAMP_FIELD = "createdTimestamp";
  public static final String FILE_PATH_FIELD = "filePath";

  private Long id;
  private Long pid;
  private String modelUuid;
  private Long createdTimestamp;

  // This filePath is relative to the archived-process directory.
  // <EXEC_ENGINE_ID>/<PARTITION>/<FILENAME.l>
  // Example: 02/0/process_0002eabc-483c-8000-4261-054d98054d98_opt_2097152.l
  private String filePath;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "pid", nullable = false)
  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  @Column(name = "model_uuid", nullable = false)
  public String getModelUuid() {
    return modelUuid;
  }

  public void setModelUuid(String modelUuid) {
    this.modelUuid = modelUuid;
  }

  @Column(name = "created_ts", nullable = false)
  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  @Column(name = "file_path", nullable = false)
  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public ArchiveProcessIndex(Long pid, String modelUuid, Long createdTimestamp, String filePath) {
    this.pid = pid;
    this.modelUuid = modelUuid;
    this.createdTimestamp = createdTimestamp;
    this.filePath = filePath;
  }

  public ArchiveProcessIndex() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchiveProcessIndex that = (ArchiveProcessIndex)o;
    return Objects.equals(id, that.id) && Objects.equals(pid, that.pid) &&
        Objects.equals(modelUuid, that.modelUuid) &&
        Objects.equals(createdTimestamp, that.createdTimestamp) &&
        Objects.equals(filePath, that.filePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, pid, modelUuid, createdTimestamp, filePath);
  }

  @Override
  public String toString() {
    return "ArchiveProcessIndex{" + "id=" + id + ", pid='" + pid + '\'' +
        ", modelUuid='" + modelUuid + '\'' +
        ", createdTimestamp='" + createdTimestamp + '\'' +
        ", filePath='" + filePath + '\'' + '}';
  }
}
