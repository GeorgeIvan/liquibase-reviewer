package com.appiancorp.designdeployments.persistence;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.transaction.annotation.Transactional;

import com.appian.core.persist.Constants;
import com.appiancorp.common.AppianVersion;
import com.appiancorp.ix.transaction.ImportRollbackPolicy;
import com.appiancorp.ix.transaction.ImportRollbackType;
import com.appiancorp.object.action.export.ExportRequest;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An entity class to store deployment object in RDBMS.
 */
@Entity
@Table(name = "deployment")
public class Deployment implements CommonDeployment {
  public static final Long LOCAL_ENVIRONMENT_ID = -1L;
  public static final Long EXTERNAL_SYSTEM_ID = -2L;
  public static final Long LOCAL_ENVIRONMENT_ADMIN_CONSOLE_ID = -3L;
  public static final String EXTERNAL_REQUESTER_UUID = "EXTERNAL REQUEST";
  public static final String UUID = "uuid";
  public static final String NAME = "name";
  public static final String DEPLOYMENT_REQUESTER_UUID = "requesterUuid";
  public static final String DEPLOYMENT_TYPE = "type";
  public static final String DEPLOYMENT_REMOTE_ENV_ID = "remoteEnvId";
  public static final String DEPLOYMENT_CREATED_TIME = "createdTs";
  public static final String DEPLOYMENT_STATUS = "status";
  public static final String DEFAULT_AUDIT_UUID = "UNSPECIFIED_AUDIT_UUID";
  public static final String DEPLOYMENT_AUDIT_UUID = "auditUuid";
  public static final String DEPLOYMENT_UPDATED_TIME = "updatedTs";

  private Long id;
  private String uuid;
  private String name;
  private Long createdTs;
  private Long updatedTs;
  private String requesterUuid;
  private String description;
  private Set<DeploymentEvent> deploymentEvents = new HashSet<>();
  private Type type;
  private Long patchFileDoc;
  private Long secondaryPatchFileDoc;
  private Set<DeploymentDbScript> deploymentDbScripts = new HashSet<>();
  private Set<DeploymentApp> deploymentApps = new HashSet<>();
  private Long envConfigDoc;   // ICF
  private Long icfTemplateRefId;
  private Long remoteEnvId;
  private Long exportLogDocId;
  private Long deploymentLogDocId;
  private Status status;
  private Long inspectResultsWrapperDocId;
  private String reusedDeploymentUuid;
  private String auditUuid;
  private Integer versionId;
  private Long pluginJarsDocId;
  private Set<DeploymentPlugin> deploymentPlugins;
  private Set<DeploymentPackage> packages = new HashSet<>();
  private ImportRollbackPolicy importRollbackPolicy;
  private boolean hasExportErrors;
  private Set<DeploymentPortal> deploymentPortals = new HashSet<>();
  private ImportRollbackType importRollbackType = ImportRollbackType.DISABLED;
  private boolean isBackupSaved;
  private boolean isFailFast;
  private Long backupZipDocId;
  private Long secondaryBackupZipDocId;
  private Long backupIcfDocId;
  private ExportRequest exportRequest;

  /**
   * The enum deploymentType in appian-services.email-renderer.src.templates.shared-templates.DeploymentTemplate.js
   * and in appian-services.email-renderer.src.templates.shared-templates.DeploymentRequested.js are dependent
   * on the values of this enum.  If this enum is changed, please take care to also change the other enums as well
   */
  public enum Type {
    INCOMING(false, true, false, false),
    INCOMING_CMD_LINE(true, true, false, false),
    INCOMING_MANUAL(true, true, false, false),
    OUTGOING(false, false, false, false),
    OUTGOING_MANUAL(true, false, false, false),
    INCOMING_FROM_EXTERNAL_SYSTEM(false, true, true, false),
    INCOMING_MANUAL_ADMIN_SETTINGS(true, true, false, true),
    OUTGOING_FROM_EXTERNAL_SYSTEM(false, false, true, false),
    OUTGOING_MANUAL_ADMIN_SETTINGS(true, false, false, true);

    private final boolean isManual;
    private final boolean isIncoming;
    private final boolean isExternal;
    private final boolean isManualAdminWithoutPackage;

    Type(
        boolean isManual,
        boolean isIncoming,
        boolean isExternal,
        boolean isManualAdminWithoutPackage) {
      this.isManual = isManual;
      this.isIncoming = isIncoming;
      this.isExternal = isExternal;
      this.isManualAdminWithoutPackage = isManualAdminWithoutPackage;
    }

    public boolean isManual() {
      return isManual;
    }

    public boolean isIncoming() {
      return isIncoming;
    }

    public boolean isExternal() {
      return isExternal;
    }

    /**
     * Returns true when it is a Manual deployment with admin settings but WITHOUT a design object
     * package (as per the document where "INCOMING_MANUAL_ADMIN_SETTINGS" is mentioned in
     * https://docs.google.com/document/d/1-KBM4-BaN7kVNt-dDanoi6YZ4VrNfssaeRUWrEHgzp8/edit#).
     *
     * Note that an External deployment with admin settings will return false for this method since it's
     * not classified as a Manual deployment, which may be surprising.
     *
     * To properly check for the presence of *any* admin settings in a deployment, use
     * {@link Deployment#hasAdminSettings()}.
     */
    public boolean isManualAdminWithoutPackage() {
      return isManualAdminWithoutPackage;
    }

    /** Whether this type of deployment is subject to a manual review process before it can proceed */
    public boolean canBeReviewed() {
      // Manual Import deployments currently do not participate in any review process
      return !isManual;
    }
  }

  public enum Status {
    COMPLETED(true, "20.1"),
    COMPLETED_WITH_IMPORT_ERRORS(true, "20.1"),
    COMPLETED_WITH_PUBLISH_ERRORS(true, "22.4", COMPLETED_WITH_IMPORT_ERRORS), // Set as the deployment status when there are no import/export errors, only portal publishing/unpublishing errors
    COMPLETED_WITH_EXPORT_ERRORS(true, "20.1"), // Set as the deployment status when there are no import errors, only export errors
    FAILED(true, "20.1"),
    IN_PROGRESS(false, "20.1"),
    PENDING_REVIEW(false, "20.1"),
    REJECTED(true, "20.1"),
    UNAVAILABLE(false, "20.4"),
    REVERTED(true, "20.4"),
    REVERTED_WITH_ERRORS(true, "20.4");

    public boolean isTerminal() {
      return isTerminal;
    }

    /**
     * @return The major and minor Appian version in which the enum was introduced (does not include the
     * micro/hotfix versions)
     */
    public AppianVersion getIntroducedAppianVersion() {
      return introducedAppianVersion;
    }

    /**
     * @return a fallback status in case the source environment is older than the target environment.
     * Otherwise return null if there is no fallback status.
     */
    public Status getFallbackStatus() {
      return fallbackStatus;
    }

    private final boolean isTerminal;
    private final AppianVersion introducedAppianVersion;
    private final Status fallbackStatus;

    Status(boolean isTerminal, String appianVersion) {
      this(isTerminal, appianVersion, null);
    }

    Status(boolean isTerminal, String appianVersion, Status fallbackStatus) {
      this.isTerminal = isTerminal;
      this.introducedAppianVersion = new AppianVersion(appianVersion);
      this.fallbackStatus = fallbackStatus;
    }
  }

  Deployment() {}

  private Deployment(DeploymentBuilder deploymentBuilder) {
    this.id = deploymentBuilder.getId();
    this.uuid = deploymentBuilder.getUuid();
    this.name = deploymentBuilder.getName();
    this.createdTs = deploymentBuilder.getCreatedTs();
    this.updatedTs = deploymentBuilder.getUpdatedTs();
    this.description = deploymentBuilder.getDescription();
    Set<DeploymentEvent> newEvents = deploymentBuilder.getDeploymentEvents();
    if (newEvents != null) {
      this.deploymentEvents = newEvents;
    }
    this.type = deploymentBuilder.getType();
    this.patchFileDoc = deploymentBuilder.getPatchFileDoc();
    this.secondaryPatchFileDoc = deploymentBuilder.getSecondaryPatchFileDoc();
    Set<DeploymentDbScript> newScripts = deploymentBuilder.getDeploymentDbScripts();
    if (newScripts != null) {
      this.deploymentDbScripts = newScripts;
    }
    Set<DeploymentApp> newApps = deploymentBuilder.getDeploymentApps();
    if (newApps != null) {
      this.deploymentApps = newApps;
    }
    this.envConfigDoc = deploymentBuilder.getEnvConfigFileDoc();
    this.remoteEnvId = deploymentBuilder.getRemoteEnvId();
    this.requesterUuid = deploymentBuilder.getRequesterUuid();
    this.exportLogDocId = deploymentBuilder.getExportLogDocId();
    this.icfTemplateRefId = deploymentBuilder.getIcfTemplateRefId();
    this.deploymentLogDocId = deploymentBuilder.getDeploymentLogDocId();
    this.status = deploymentBuilder.getStatus();
    this.inspectResultsWrapperDocId = deploymentBuilder.getInspectResultsWrapperDocId();
    this.reusedDeploymentUuid = deploymentBuilder.getReusedDeploymentUuid();
    this.auditUuid = deploymentBuilder.getAuditUuid();
    this.versionId = deploymentBuilder.getVersionId();
    this.pluginJarsDocId = deploymentBuilder.getPluginJarsDocId();
    Set<DeploymentPlugin> newPlugins = deploymentBuilder.getDeploymentPlugins();
    if (newPlugins != null) {
      this.deploymentPlugins = newPlugins;
    } else {
      this.deploymentPlugins = new HashSet<>();
    }
    Set<DeploymentPackage> pkgs = deploymentBuilder.getPackages();
    if (pkgs != null) {
      this.packages = pkgs;
    }
    this.hasExportErrors = deploymentBuilder.getHasExportErrors();
    Set<DeploymentPortal> newPortals = deploymentBuilder.getDeploymentPortals();
    this.deploymentPortals = newPortals != null ? newPortals : new HashSet<>();
  }

  @PrePersist
  private void onPrePersist() {
    if (updatedTs == null) {
      updatedTs = System.currentTimeMillis();
    }
  }

  //This is included for testing. Follow up in AN-164629
  @PreUpdate
  private void onPreUpdate() {
    if (updatedTs == null) {
      updatedTs = System.currentTimeMillis();
    }
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

  @Column(name = "uuid", nullable = false)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "created_ts", nullable = false)
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @Column(name = "updated_ts", nullable = false)
  public Long getUpdatedTs() {
    return updatedTs;
  }

  public void setUpdatedTs(Long updatedTs) {
    this.updatedTs = updatedTs;
  }

  @Column(name = "requester_uuid", nullable = false)
  public String getRequesterUuid() {
    // FYI Might return the default value "UNSPECIFIED_REQUESTER" for deployments that were persisted before
    // db-changelog-......-deployment-user-uuids
    return requesterUuid;
  }

  public void setRequesterUuid(String requesterUuid) {
    this.requesterUuid = requesterUuid;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "remote_env_id", nullable = false)
  public Long getRemoteEnvId() {
    return remoteEnvId;
  }

  public void setRemoteEnvId(Long remoteEnvId) {
    this.remoteEnvId = remoteEnvId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  /**
   * Don't use this getter outside of a {@link Transactional} method where
   * a persisted deployment is accessed or modified; otherwise it might cause
   * a hibernate failure because of {@link FetchType#LAZY}.
   * LATER: AN-263503 - Switch to package-protected/private access modifier
   *
   * @return the set of {@link DeploymentEvent}
   */
  @OneToMany(mappedBy = "deployment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy
  public Set<DeploymentEvent> getDeploymentEvents() {
    return deploymentEvents;
  }

  public void setDeploymentEvents(Set<DeploymentEvent> deploymentEvents) {
    this.deploymentEvents = deploymentEvents;
  }

  @Column(name = "patch_file_doc")
  public Long getPatchFileDoc() {
    return patchFileDoc;
  }

  public void setPatchFileDoc(Long patchFileDoc) {
    this.patchFileDoc = patchFileDoc;
  }

  @Column(name = "secondary_patch_file_doc")
  public Long getSecondaryPatchFileDoc() {
    return secondaryPatchFileDoc;
  }

  public void setSecondaryPatchFileDoc(Long secondaryPatchFileDoc) {
    this.secondaryPatchFileDoc = secondaryPatchFileDoc;
  }

  @Column(name = "export_log_doc")
  public Long getExportLogDocId() {
    return exportLogDocId;
  }

  public void setExportLogDocId(Long exportLogDocId) {
    this.exportLogDocId = exportLogDocId;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "deployment_id", nullable = false)
  public Set<DeploymentDbScript> getDeploymentDbScripts() {
    return deploymentDbScripts;
  }

  public void setDeploymentDbScripts(Set<DeploymentDbScript> deploymentDbScripts) {
    this.deploymentDbScripts = deploymentDbScripts;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "deployment_id", nullable = false)
  public Set<DeploymentApp> getDeploymentApps() {
    return deploymentApps;
  }

  public void setDeploymentApps(Set<DeploymentApp> deploymentApps) {
    this.deploymentApps = deploymentApps;
  }

  @Column(name = "env_config_doc")
  public Long getEnvConfigDoc() {
    return envConfigDoc;
  }

  public void setEnvConfigDoc(Long envConfigDoc) {
    this.envConfigDoc = envConfigDoc;
  }

  @Column(name = "icf_template_doc")
  public Long getIcfTemplateRefId() {
    return icfTemplateRefId;
  }

  public void setIcfTemplateRefId(Long icfTemplateRefId) {
    this.icfTemplateRefId = icfTemplateRefId;
  }

  @Column(name = "deployment_log_doc")
  public Long getDeploymentLogDocId() {
    return deploymentLogDocId;
  }

  public void setDeploymentLogDocId(Long deploymentLogDocId) {
    this.deploymentLogDocId = deploymentLogDocId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "inspect_results_wrapper_doc")
  public Long getInspectResultsWrapperDocId() {
    return inspectResultsWrapperDocId;
  }

  public void setInspectResultsWrapperDocId(Long inspectResultsWrapperDocId) {
    this.inspectResultsWrapperDocId = inspectResultsWrapperDocId;
  }

  @Column(name = "reused_deployment_uuid")
  public String getReusedDeploymentUuid() {
    return reusedDeploymentUuid;
  }

  public void setReusedDeploymentUuid(String reusedDeploymentUuid) {
    this.reusedDeploymentUuid = reusedDeploymentUuid;
  }

  @Column(name = "audit_uuid", nullable = false)
  public String getAuditUuid() {
    // FYI Might return the default value "UNSPECIFIED_AUDIT_UUID" for deployments that were persisted before
    // db-changelog-000271-add-audit-uuid-column.yaml
    return auditUuid;
  }

  public void setAuditUuid(String auditUuid) {
    this.auditUuid = auditUuid;
  }

  @Column(name = "version_id", nullable = false)
  public Integer getVersionId() {
    return versionId;
  }

  public void setVersionId(Integer versionId) {
    this.versionId = versionId;
  }

  @Column(name = "plugin_jars_doc")
  public Long getPluginJarsDocId() {
    return pluginJarsDocId;
  }

  public void setPluginJarsDocId(Long docId) {
    this.pluginJarsDocId = docId;
  }

  @OneToMany(mappedBy = "deployment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy
  public Set<DeploymentPlugin> getDeploymentPlugins() {
    return deploymentPlugins;
  }

  public void setDeploymentPlugins(Set<DeploymentPlugin> deploymentPlugins) {
    this.deploymentPlugins = deploymentPlugins;
  }

  @OneToMany(mappedBy = "deployment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy
  public Set<DeploymentPackage> getPackages() {
    return packages;
  }

  public void setPackages(Set<DeploymentPackage> packages) {
    this.packages = packages;
  }

  @Transient
  public boolean isOutgoing() {
    return !type.isIncoming();
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "import_rollback_type")
  public ImportRollbackType getImportRollbackType() {
    return importRollbackType;
  }

  public void setImportRollbackType(ImportRollbackType importRollbackType) {
    this.importRollbackType = importRollbackType;
  }

  @Column(name = "is_backup_saved")
  public boolean isBackupSaved() {
    return isBackupSaved;
  }

  public void setBackupSaved(boolean isBackupSaved) {
    this.isBackupSaved = isBackupSaved;
  }

  @Column(name = "is_fail_fast")
  public boolean isFailFast() {
    return isFailFast;
  }

  public void setFailFast(boolean isFailFast) {
    this.isFailFast = isFailFast;
  }

  @Column(name = "backup_zip_doc")
  public Long getBackupZipDocId() {
    return backupZipDocId;
  }

  public void setBackupZipDocId(Long backupZipDocId) {
    this.backupZipDocId = backupZipDocId;
  }

  @Column(name = "secondary_backup_zip_doc")
  public Long getSecondaryBackupZipDocId() {
    return secondaryBackupZipDocId;
  }

  public void setSecondaryBackupZipDocId(Long secondaryBackupZipDocId) {
    this.secondaryBackupZipDocId = secondaryBackupZipDocId;
  }

  @Column(name = "backup_icf_doc")
  public Long getBackupIcfDocId() {
    return backupIcfDocId;
  }

  public void setBackupIcfDocId(Long backupIcfDocId) {
    this.backupIcfDocId = backupIcfDocId;
  }

  @Column(name = "has_export_errors")
  public boolean getHasExportErrors() {
    return hasExportErrors;
  }

  public void setHasExportErrors(boolean hasExportErrors) {
    this.hasExportErrors = hasExportErrors;
  }

  @OneToMany(mappedBy = "deployment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy
  public Set<DeploymentPortal> getDeploymentPortals() {
    return deploymentPortals;
  }

  public void setDeploymentPortals(Set<DeploymentPortal> deploymentPortals) {
    this.deploymentPortals = deploymentPortals;
  }

  @Transient
  public Set<String> getDeploymentPortalsAsUuids() {
    return deploymentPortals.stream()
        .map(DeploymentPortal::getPortalUuid)
        .collect(Collectors.toSet());
  }

  @Transient
  public boolean hasPublishErrors() {
    return deploymentPortals.stream().anyMatch(DeploymentPortal::isErrored);
  }

  @Transient
  public boolean isComplete() {
    Collection<Status> completedStasus = Arrays.asList(Status.COMPLETED, Status.COMPLETED_WITH_PUBLISH_ERRORS,
        Status.COMPLETED_WITH_IMPORT_ERRORS, Status.COMPLETED_WITH_EXPORT_ERRORS);
    return completedStasus.contains(status);
  }

  @Transient
  public void setExportRequest(ExportRequest exportRequest) {
    this.exportRequest = exportRequest;
  }

  @Transient
  public ExportRequest getExportRequest() {
    return exportRequest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Deployment that = (Deployment)o;
    return Objects.equal(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uuid);
  }

  @Override
  public String toString() {
    // Don't display list/set objects because they are lazily initialized, - see the error:
    // ERROR org.hibernate.LazyInitializationException - failed to lazily initialize a collection of
    // role: com.appiancorp.designdeployments.persistence.Deployment.deploymentEvents, no session
    // or session was closed
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("uuid", uuid)
        .add("name", name)
        .add("createdTs", createdTs)
        .add("updatedTs", updatedTs)
        .add("requesterUuid", requesterUuid)
        .add("description", description)
        .add("remoteEnvId", remoteEnvId)
        .add("type", type)
        .add("patchFileDoc", patchFileDoc)
        .add("secondaryPatchFileDoc", secondaryPatchFileDoc)
        .add("deploymentLogDocId", deploymentLogDocId)
        .add("envConfigDoc", envConfigDoc)
        .add("reusedDeploymentUuid", reusedDeploymentUuid)
        .add("auditUuid", auditUuid)
        .add("versionId", versionId)
        .add("hasExportErrors", hasExportErrors)
        .add("hasExportErrors", hasExportErrors)
        .toString();
  }

  @Override
  @Transient
  public Set<? extends CommonDeploymentApp> getCommonApps() {
    return deploymentApps;
  }

  /* A builder class for building deployment object */
  public static class DeploymentBuilder {
    private Long id;
    private String uuid;
    private String name;
    private Long createdTs;
    private Long updatedTs;
    private String requesterUuid;
    private String description;
    private Set<DeploymentEvent> deploymentEvents;
    private Deployment.Type type;
    private Long patchFileDoc;
    private Long secondaryPatchFileDoc;
    private Set<DeploymentDbScript> deploymentDbScripts;
    private Set<DeploymentApp> deploymentApps;
    private Long envConfigFileDoc;
    private Long exportLogDocId;
    private Long icfTemplateRefId;
    private Long remoteEnvId;
    private Long deploymentLogDocId;
    private Deployment.Status status;
    private Long inspectResultsWrapperDocId;
    private String reusedDeploymentUuid;
    private String auditUuid;
    private Integer versionId;
    private Long pluginJarsDocId;
    private Set<DeploymentPlugin> deploymentPlugins;
    private Set<DeploymentPackage> packages;
    private boolean hasExportErrors = false;
    private Long exportRequestJsonDocId;
    private Set<DeploymentPortal> deploymentPortals;

    public DeploymentBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public DeploymentBuilder setUuid(String uuid) {
      this.uuid = uuid;
      return this;
    }

    public DeploymentBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public DeploymentBuilder setCreatedTs(Long createdTs) {
      this.createdTs = createdTs;
      return this;
    }

    public DeploymentBuilder setUpdatedTs(Long updatedTs) {
      this.updatedTs = updatedTs;
      return this;
    }

    public DeploymentBuilder setRequesterUuid(String requesterUuid) {
      this.requesterUuid = requesterUuid;
      return this;
    }

    public DeploymentBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    public DeploymentBuilder setDeploymentEvents(Set<DeploymentEvent> deploymentEvents) {
      this.deploymentEvents = deploymentEvents;
      return this;
    }

    public DeploymentBuilder setType(Deployment.Type type) {
      this.type = type;
      return this;
    }

    public DeploymentBuilder setPatchFileDoc(Long patchFileDoc) {
      this.patchFileDoc = patchFileDoc;
      return this;
    }

    public DeploymentBuilder setSecondaryPatchFileDoc(Long secondaryPatchFileDoc) {
      this.secondaryPatchFileDoc = secondaryPatchFileDoc;
      return this;
    }

    public DeploymentBuilder setExportLogDocId(Long exportLogDocId) {
      this.exportLogDocId = exportLogDocId;
      return this;
    }

    public DeploymentBuilder setDeploymentDbScripts(Set<DeploymentDbScript> deploymentDbScripts) {
      this.deploymentDbScripts = deploymentDbScripts;
      return this;
    }

    public DeploymentBuilder setDeploymentApps(Set<DeploymentApp> deploymentApps) {
      this.deploymentApps = deploymentApps;
      return this;
    }

    public DeploymentBuilder setEnvConfigFileDoc(Long envConfigFileDoc) {
      this.envConfigFileDoc = envConfigFileDoc;
      return this;
    }

    public DeploymentBuilder setIcfTemplateRefId(Long icfTemplateRefId) {
      this.icfTemplateRefId = icfTemplateRefId;
      return this;
    }

    public DeploymentBuilder setRemoteEnvId(Long remoteEnvId) {
      this.remoteEnvId = remoteEnvId;
      return this;
    }

    public DeploymentBuilder setDeploymentLogDocId(Long deploymentLogDocId) {
      this.deploymentLogDocId = deploymentLogDocId;
      return this;
    }

    public DeploymentBuilder setDeploymentStatus(Deployment.Status status) {
      this.status = status;
      return this;
    }

    public DeploymentBuilder setInspectResultsWrapperDocId(Long inspectResultsWrapperDocId) {
      this.inspectResultsWrapperDocId = inspectResultsWrapperDocId;
      return this;
    }

    public DeploymentBuilder setReusedDeploymentUuid(String reusedDeploymentUuid) {
      this.reusedDeploymentUuid = reusedDeploymentUuid;
      return this;
    }

    public DeploymentBuilder setAuditUuid(String auditUuid) {
      this.auditUuid = auditUuid;
      return this;
    }

    public DeploymentBuilder setVersionId(Integer versionId) {
      this.versionId = versionId;
      return this;
    }

    public DeploymentBuilder setPluginJarsDocId(Long docId) {
      this.pluginJarsDocId = docId;
      return this;
    }

    public DeploymentBuilder setDeploymentPlugins(Set<DeploymentPlugin> deploymentPlugins) {
      this.deploymentPlugins = deploymentPlugins;
      return this;
    }

    public DeploymentBuilder setPackages(Set<DeploymentPackage> packages) {
      this.packages = packages;
      return this;
    }

    public DeploymentBuilder setHasExportErrors(boolean hasExportErrors) {
      this.hasExportErrors = hasExportErrors;
      return this;
    }

    public DeploymentBuilder setExportRequestJsonDocId(Long docId) {
      this.exportRequestJsonDocId = docId;
      return this;
    }

    public DeploymentBuilder setDeploymentPortals(Set<DeploymentPortal> deploymentPortals) {
      this.deploymentPortals = deploymentPortals;
      return this;
    }

    public Long getId() {
      return id;
    }

    public String getUuid() {
      return uuid;
    }

    public String getName() {
      return name;
    }

    public Long getCreatedTs() {
      return createdTs;
    }

    public Long getUpdatedTs() {
      return updatedTs;
    }

    public String getRequesterUuid() {
      return requesterUuid;
    }

    public String getDescription() {
      return description;
    }

    public Set<DeploymentEvent> getDeploymentEvents() {
      return deploymentEvents;
    }

    public Deployment.Type getType() {
      return type;
    }

    public Long getPatchFileDoc() {
      return patchFileDoc;
    }

    public Long getSecondaryPatchFileDoc() {
      return secondaryPatchFileDoc;
    }

    public Long getExportLogDocId() {
      return exportLogDocId;
    }

    public Set<DeploymentDbScript> getDeploymentDbScripts() {
      return deploymentDbScripts;
    }

    public Set<DeploymentApp> getDeploymentApps() {
      return deploymentApps;
    }

    public Long getEnvConfigFileDoc() {
      return envConfigFileDoc;
    }

    public Deployment build() {
      return new Deployment(this);
    }

    public Long getIcfTemplateRefId() {
      return icfTemplateRefId;
    }

    public Long getRemoteEnvId() {
      return remoteEnvId;
    }

    public Long getDeploymentLogDocId() {
      return deploymentLogDocId;
    }

    public Deployment.Status getStatus() {
      return status;
    }

    public Long getInspectResultsWrapperDocId() {
      return inspectResultsWrapperDocId;
    }

    public String getReusedDeploymentUuid() {
      return reusedDeploymentUuid;
    }

    public String getAuditUuid() {
      return auditUuid;
    }

    public Integer getVersionId() {
      return versionId;
    }

    public Long getPluginJarsDocId() {
      return pluginJarsDocId;
    }

    public Set<DeploymentPlugin> getDeploymentPlugins() {
      return deploymentPlugins;
    }

    public Set<DeploymentPackage> getPackages() {
      return packages;
    }

    public boolean getHasExportErrors() {
      return hasExportErrors;
    }

    public Long getExportRequestJsonDocId() {
      return exportRequestJsonDocId;
    }

    public Set<DeploymentPortal> getDeploymentPortals() {
      return deploymentPortals;
    }
  }

  /**
   * A method to determine whether a deployment contains admin settings (both for manual & external).
   *
   * @return true if contains admin settings, false if not.
   */
  public boolean hasAdminSettings() {
    return getAdminSettingsDocId() != null;
  }

  /**
   * Returns the Admin Console settings document in `this` deployment or null if `this` doesn't include
   * any Admin Console settings
   */
  @Transient
  public Long getAdminSettingsDocId() {
    return getAdminSettingsDocIdStatically(createAdapterForDeployment());
  }

  /**
   * An adapter interface used by {@link #getAdminSettingsDocIdStatically(DeploymentAdapterForAdminSettings)}
   * to allow a {@link Deployment} from Java and a {@link com.appiancorp.type.cdt.value.DeploymentDto} from
   * SAIL to both provide the same information.
   */
  public interface DeploymentAdapterForAdminSettings {
    Type getType();
    Long getPatchFileDoc();
    Long getSecondaryPatchFileDoc();
  }

  /**
   * Like {@link #getAdminSettingsDocId()} but does not require an instance of {@link Deployment}
   *
   * @param adapter an adapter object that offers the necessary information to find the Admin Console
   *                settings document in a logical deployment
   */
  public static Long getAdminSettingsDocIdStatically(DeploymentAdapterForAdminSettings adapter) {
    // A few types know intrinsically whether they have admin settings, and they use the main patch doc:
    if (adapter.getType().isManualAdminWithoutPackage()) {
      return adapter.getPatchFileDoc();
    }
    // Otherwise deployments use the secondaryPatchFileDoc for admin settings:
    return adapter.getSecondaryPatchFileDoc();
  }

  /**
   * Returns an adapter that represents `this` Deployment in a way that the
   * {@link Deployment#getAdminSettingsDocIdStatically(DeploymentAdapterForAdminSettings)} method
   * can understand
   */
  private DeploymentAdapterForAdminSettings createAdapterForDeployment() {
    return new DeploymentAdapterForAdminSettings() {
      @Override
      public Type getType() {
        return Deployment.this.getType();
      }

      @Override
      public Long getPatchFileDoc() {
        return Deployment.this.getPatchFileDoc();
      }

      @Override
      public Long getSecondaryPatchFileDoc() {
        return Deployment.this.getSecondaryPatchFileDoc();
      }
    };
  }
}
