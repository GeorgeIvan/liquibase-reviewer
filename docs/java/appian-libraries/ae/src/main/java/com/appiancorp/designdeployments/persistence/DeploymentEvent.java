package com.appiancorp.designdeployments.persistence;

import static com.appiancorp.object.action.ExportDeploymentHelper.IX_GSON;

import java.io.UnsupportedEncodingException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.applications.ImportDetails;
import com.appiancorp.designdeployments.service.DeploymentService;
import com.appiancorp.object.action.export.ExportDetails;
import com.appiancorp.object.action.export.ExportRequest;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.google.common.base.Objects;

/**
 * An entity class to store deployment event object in RDBMS. Example events include import and statusSent.
 * This is used as audit trail of a {@link Deployment} object.
 */
@Entity
@Table(name = "deployment_event")
public class DeploymentEvent {
  static final String PROP_DEPLOYMENT = "deployment";
  static final String PROP_NAME = "name";
  public static final String PROP_STATUS = "status";
  private Long id;
  private Deployment deployment;
  private String eventPerformerUuid;
  private String errorCode;
  private Long timestamp;
  private EventName name;
  private EventStatus status;
  private ImportDetails importDetails;
  private ExportDetails exportDetails;
  private ExportRequest exportRequest;
  private String description;
  private Integer versionId;
  private Long eventInfoVersionId;

  private static final String DPL_EVENT_SUCCESSFUL_KEY = "deployAcrossEnvironments.log.eventStatus.successful";
  private static final String DPL_EVENT_FAILED_KEY = "deployAcrossEnvironments.log.eventStatus.failed";
  private static final String DPL_EVENT_SKIPPED_KEY = "deployAcrossEnvironments.log.eventStatus.skipped";

  public enum EventName {
    REQUESTED,
    DATABASE_UPDATED,
    IMPORTED,
    STATUS_QUEUED,
    FINISHED,
    REVIEWED,
    PLUGIN_INSTALLED,
    EXPORTED,
    PORTAL_PUBLISHED
  }

  public enum EventStatus {
    SUCCESSFUL,
    FAILED,
    SKIPPED,
    REJECTED,
    AVAILABLE
  }

  DeploymentEvent() {
  }

  public DeploymentEvent(DeploymentEvent deploymentEvent) {
    this.id = null;
    this.deployment = deploymentEvent.deployment;
    this.errorCode = deploymentEvent.errorCode;
    this.timestamp = deploymentEvent.timestamp;
    this.name = deploymentEvent.name;
    this.eventPerformerUuid = deploymentEvent.eventPerformerUuid;
    this.status = deploymentEvent.status;
    this.importDetails = deploymentEvent.importDetails;
    if (importDetails != null) {
      eventInfoVersionId = ImportDetails.serialVersionUID;
    }
    this.description = deploymentEvent.description;
    this.versionId = deploymentEvent.versionId;
  }

  private DeploymentEvent(DeploymentEventBuilder builder) {
    this.id = builder.getId();
    this.deployment = builder.getDeployment();
    this.errorCode = builder.getErrorCode();
    this.timestamp = builder.getTimestamp();
    this.name = builder.getName();
    this.eventPerformerUuid = builder.getEventPerformerUuid();
    this.status = builder.getStatus();
    this.importDetails = builder.getImportDetails();
    if (importDetails != null) {
      eventInfoVersionId = ImportDetails.serialVersionUID;
    }
    this.description = builder.getDescription();
    this.versionId = builder.getVersionId();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_id", nullable = false)
  public Deployment getDeployment() {
    return deployment;
  }

  public void setDeployment(Deployment deployment) {
    this.deployment = deployment;
  }

  @Column(name = "error_code")
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  @Column(name = "time_stamp", nullable = false)
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "name", nullable = false)
  public EventName getName() {
    return name;
  }

  public void setName(EventName name) {
    this.name = name;
  }

  @Column(name = "event_performer_uuid")
  public String getEventPerformerUuid() {
    return eventPerformerUuid;
  }

  public void setEventPerformerUuid(String eventPerformerUuid) {
    this.eventPerformerUuid = eventPerformerUuid;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  public EventStatus getStatus() {
    return status;
  }

  public static String getInternationalizedStatusKey(EventStatus status) {
    if (status == EventStatus.SUCCESSFUL) {
      return DPL_EVENT_SUCCESSFUL_KEY;
    } else if (status == EventStatus.FAILED) {
      return DPL_EVENT_FAILED_KEY;
    } else if (status == EventStatus.SKIPPED) {
      return DPL_EVENT_SKIPPED_KEY;
    }
    throw new IllegalArgumentException("No supported EventStatus: " + status.name());
  }

  public void setStatus(EventStatus status) {
    this.status = status;
  }

  @Lob
  @Column(name = "event_info")
  private byte[] getSerializedEventInfo() {
    if(name == EventName.IMPORTED) {
      return importDetails == null ? null : encodeEventInfo(importDetails);
    } else if (name == EventName.EXPORTED) {
      return exportDetails == null ? null : encodeEventInfo(exportDetails);
    } else if (name == EventName.REQUESTED) {
      return exportRequest == null ? null : encodeEventInfo(exportRequest);
    }
    return null;
  }

  @SuppressWarnings("unused")
  private void setSerializedEventInfo(byte[] eventInfo) {
    if (eventInfo == null) {
      return;
    }
    try {
      if (name == EventName.IMPORTED) {
        this.importDetails = ImportDetails.convertJsonToImportDetails(getEventInfoVersionId(), eventInfo);
      } else if (name == EventName.EXPORTED) {
        this.exportDetails = ExportDetails.convertJsonToExportDetails(getEventInfoVersionId(), eventInfo);
      } else if (name == EventName.REQUESTED) {
        this.exportRequest = ExportRequest.convertJsonToExportRequest(getEventInfoVersionId(), eventInfo);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Column(name = "event_info_version_id")
  public Long getEventInfoVersionId() {
    return eventInfoVersionId;
  }

  @SuppressWarnings("unused")
  private void setEventInfoVersionId(Long eventInfoVersionId) {
    this.eventInfoVersionId = eventInfoVersionId;
  }

  public static byte[] encodeEventInfo(Object eventInfo) {
    try {
      // TO CODE MAINTAINERS: DO NOT COPY THE PATTERN SHOWN HERE since it results in fragile serialization
      // that can easily cause BWC breaks.
      return IX_GSON.toJson(eventInfo).getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static ImportDetails decodeImportDetails(byte[] serializedImportDetails) {
    return decodeEventInfo(serializedImportDetails, ImportDetails.class);
  }

  public static <T> T decodeEventInfo(
      byte[] eventInfoBytes,
      Class<T> eventInfoClass) {
    try {
      String eventInfoString = new String(eventInfoBytes, "UTF-8");
      return IX_GSON.fromJson(eventInfoString, eventInfoClass);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Transient
  public ImportDetails getImportDetails() {
    return importDetails;
  }

  public void setImportDetails(ImportDetails importDetails) {
    this.importDetails = importDetails;
    if (importDetails != null) {
      eventInfoVersionId = ImportDetails.serialVersionUID;
    }
  }

  @Transient
  public ExportDetails getExportDetails() {
    return exportDetails;
  }

  public void setExportDetails(ExportDetails exportDetails) {
    this.exportDetails = exportDetails;
  }

  @Transient
  public ExportRequest getExportRequest() {
    return exportRequest;
  }

  public void setExportRequest(ExportRequest exportRequest) {
    this.exportRequest = exportRequest;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "version_id", nullable = false)
  public Integer getVersionId() {
    return versionId;
  }

  public void setVersionId(Integer versionId) {
    this.versionId = versionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentEvent that = (DeploymentEvent)o;
    return Objects.equal(name, that.name) && Objects.equal(status, that.status) &&
        Objects.equal(timestamp, that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, status, timestamp);
  }

  @Override
  public String toString() {
    return "DeploymentEvent{" + "id=" + id + ", errorCode=" + errorCode + ", timestamp=" + timestamp +
        ", name='" + name + "', eventPerformerUuid=" + eventPerformerUuid + ", status=" + status +
        ", description=" + description + ", importDetails=" + importDetails + ", versionId=" + versionId +
        "}";
  }

  /**
   * Returns null when the current {@link DeploymentEvent} does not map directly to a {@link Deployment.Status}
   */
  public Deployment.Status calculateDeploymentStatus(Deployment deployment, DeploymentService deploymentService) {
    Deployment.Status deploymentStatus = null;
    DeploymentEvent.EventStatus currentStatus = getStatus();
    if (currentStatus == DeploymentEvent.EventStatus.FAILED) {
      // The failed STATUS_QUEUED event should NOT impact the status of deployment.
      // The notification of remote connected environment is optional; and its state does not affect
      // the state of imported objects and executed DDLs.
      if (!EventName.STATUS_QUEUED.equals(this.name)) {
        deploymentStatus = Deployment.Status.FAILED;
      }
    } else if (name == EventName.REQUESTED &&
        (currentStatus == EventStatus.AVAILABLE || currentStatus == EventStatus.SUCCESSFUL)) {
      deploymentStatus = deployment.getType().canBeReviewed() ? Deployment.Status.PENDING_REVIEW :
          Deployment.Status.IN_PROGRESS;
    } else if (name == DeploymentEvent.EventName.REVIEWED) {
      if (currentStatus == EventStatus.AVAILABLE) {
        deploymentStatus = Deployment.Status.PENDING_REVIEW;
      } else if (currentStatus == DeploymentEvent.EventStatus.REJECTED) {
        deploymentStatus = Deployment.Status.REJECTED;
      } else if (currentStatus == EventStatus.SKIPPED || currentStatus == EventStatus.SUCCESSFUL) {
        deploymentStatus = Deployment.Status.IN_PROGRESS;
      }
    } else if (currentStatus == EventStatus.SUCCESSFUL && name == DeploymentEvent.EventName.FINISHED && !deployment.getStatus().isTerminal()) {
      deploymentStatus = resolveDeploymentStatusForFinish(deployment, deploymentService);
    }
    return deploymentStatus;
  }

  private Deployment.Status resolveDeploymentStatusForFinish(Deployment deployment, DeploymentService deploymentService) {
    Deployment.Status deploymentStatus = null;
    DeploymentEvent importedEvent = deploymentService.getDeploymentEventByName(deployment.getId(),
        EventName.IMPORTED);
    boolean wereThereImportErrors = false;
    ImportDetails importedEventImportDetails = null;
    if (importedEvent != null) {
      // For an export, the imported event will be null
      importedEventImportDetails = importedEvent.getImportDetails();
      wereThereImportErrors = importedEventImportDetails != null && importedEventImportDetails.getNumFailed() > 0;
    }
    if (importedEventImportDetails != null && importedEventImportDetails.isRolledBack()) {
      deploymentStatus = resolveDeploymentStatusForImportRollback(importedEventImportDetails);
    } else if (wereThereImportErrors) {
      deploymentStatus = Deployment.Status.COMPLETED_WITH_IMPORT_ERRORS;
    } else if (deployment.getHasExportErrors()) {
      deploymentStatus = Deployment.Status.COMPLETED_WITH_EXPORT_ERRORS;
    } else if (deployment.hasPublishErrors()) {
      deploymentStatus = Deployment.Status.COMPLETED_WITH_PUBLISH_ERRORS;
    } else {
      deploymentStatus = Deployment.Status.COMPLETED;
    }
    return deploymentStatus;
  }

  private Deployment.Status resolveDeploymentStatusForImportRollback(ImportDetails importDetails) {
    Deployment.Status deploymentStatus = null;
    ImportDetails rollbackImportDetails = importDetails.getRollbackImportDetails();
    if (rollbackImportDetails != null) {
      deploymentStatus = rollbackImportDetails.getState() == ImportDetails.State.Success ?
          Deployment.Status.REVERTED :
          Deployment.Status.REVERTED_WITH_ERRORS;
    } else {
      deploymentStatus = Deployment.Status.REVERTED_WITH_ERRORS;
    }
    return deploymentStatus;
  }

  public static DeploymentEventBuilder failedEventBuilder(EventName eventName, ErrorCode errorCode) {
    return new DeploymentEventBuilder().setName(eventName)
        .setErrorCode(errorCode.toString())
        .setStatus(EventStatus.FAILED);
  }

  public static DeploymentEventBuilder successEventBuilder(EventName eventName, Deployment deployment) {
    return new DeploymentEventBuilder().setName(eventName)
        .setDeployment(deployment)
        .setStatus(EventStatus.SUCCESSFUL);
  }

  public static DeploymentEventBuilder availableEventBuilder(EventName eventName, Deployment deployment) {
    return new DeploymentEventBuilder().setName(eventName)
        .setDeployment(deployment)
        .setStatus(EventStatus.AVAILABLE);
  }

  public static DeploymentEventBuilder skippedEventBuilder(EventName eventName, Deployment deployment) {
    return new DeploymentEventBuilder().setName(eventName)
        .setDeployment(deployment)
        .setStatus(EventStatus.SKIPPED);
  }

  public static class DeploymentEventBuilder {
    private Long id;
    private Deployment deployment;
    private String errorCode;
    private Long timestamp;
    private EventName name;
    private String eventPerformerUuid;
    private EventStatus status;
    private ImportDetails importDetails;
    private String description;
    private Integer versionId;

    public DeploymentEventBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public DeploymentEventBuilder setDeployment(Deployment deployment) {
      this.deployment = deployment;
      return this;
    }

    public DeploymentEventBuilder setErrorCode(String errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public DeploymentEventBuilder setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public DeploymentEventBuilder setName(EventName name) {
      this.name = name;
      return this;
    }

    public DeploymentEventBuilder setEventPerformerUuid(String eventPerformerUuid) {
      this.eventPerformerUuid = eventPerformerUuid;
      return this;
    }

    public DeploymentEventBuilder setStatus(EventStatus status) {
      this.status = status;
      return this;
    }

    public DeploymentEventBuilder setImportDetails(ImportDetails importDetails) {
      this.importDetails = importDetails;
      return this;
    }

    public DeploymentEventBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    public DeploymentEventBuilder setVersionId(Integer versionId) {
      this.versionId = versionId;
      return this;
    }

    public Long getId() {
      return id;
    }

    public Deployment getDeployment() {
      return deployment;
    }

    public String getErrorCode() {
      return errorCode;
    }

    public Long getTimestamp() {
      return timestamp;
    }

    public EventName getName() {
      return name;
    }

    public String getEventPerformerUuid() {
      return eventPerformerUuid;
    }

    public EventStatus getStatus() {
      return status;
    }

    public ImportDetails getImportDetails() {
      return importDetails;
    }

    public String getDescription() {
      return description;
    }

    public Integer getVersionId() {
      return versionId;
    }

    public DeploymentEvent build() {
      if (this.getTimestamp() == null) {
        this.setTimestamp(System.currentTimeMillis());
      }
      if (this.versionId == null) {
        this.versionId = 1; /* To be removed as part of AN-148713 */
      }
      return new DeploymentEvent(this);
    }
  }
}
