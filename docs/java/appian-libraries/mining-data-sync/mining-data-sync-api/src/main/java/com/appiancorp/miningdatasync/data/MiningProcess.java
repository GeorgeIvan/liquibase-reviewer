package com.appiancorp.miningdatasync.data;

import static com.appian.core.persist.Constants.COL_MAXLEN_MAX_NON_CLOB;
import static com.appiancorp.miningdatasync.data.MiningProcess.MINING_PROCESS_TABLE;
import static com.appiancorp.miningdatasync.data.MiningProcessAdHocEvent.PROP_MINING_PROCESS_IN_AD_HOC_EVENT;
import static com.appiancorp.miningdatasync.data.MiningProcessFieldAlert.PROP_MINING_PROCESS_IN_PROCESS_ALERT;
import static com.appiancorp.miningdatasync.schedule.MiningScheduleFrequencyType.WEEKLY;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.datafabricdependency.entities.DataFabricDependencyEntity;
import com.appiancorp.miningdatasync.schedule.MiningScheduleFrequencyType;
import com.appiancorp.miningdatasync.schedule.MiningSyncSchedule;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.google.common.collect.ImmutableSet;

@Entity
@Table(name = MINING_PROCESS_TABLE)
@CopilotClass(name = "process", description = "A process represents the data to be analyzed by process insights.")
@SuppressWarnings("checkstyle:CyclomaticComplexity")
public class MiningProcess implements HasRoleMap {
  public static final String PROP_LOG_ID = "logId";
  public static final String PROP_ID = "id";
  public static final String PROP_PROCESS_NAME = "name";
  public static final String PROP_PROCESS_UUID = "uuid";
  public static final String PROP_PROCESS_DESCRIPTION = "description";
  public static final String PROP_PROCESS_AVG_CASE_DURATION = "averageCaseDuration";
  public static final String PROP_PROCESS_MEDIAN_CASE_DURATION = "medianCaseDuration";
  public static final String PROP_PROCESS_NUMBER_OF_CASES = "numberOfCases";
  public static final String PROP_PROCESS_NUMBER_OF_EVENTS = "numberOfEvents";
  public static final String PROP_PROCESS_NUMBER_OF_DISTINCT_ACTIONS = "numberOfDistinctActions";
  public static final String PROP_MINING_PROCESS_PROVIDERS = "miningProcessProviders";
  public static final String PROP_PROCESS_SYNC_STATUS_BYTE = "syncStatusByte";
  public static final String PROP_PROCESS_LAST_SUCCESSFUL_UPDATE_TIME = "lastSuccessfulUpdateMs";
  public static final String PROP_PROCESS_LAST_SUCCESSFUL_UPDATE_BY_USER = "lastSuccessfulUpdateByUserUuid";
  public static final String PROP_PROCESS_LAST_FAILED_UPDATE_TIME = "failedUpdateMs";
  public static final String PROP_PROCESS_LAST_FAILED_UPDATE_BY_USER = "failedUpdateByUserUuid";
  public static final String PROP_MINING_PROCESS_DEP = "miningProcessDependencies";
  public static final String PROP_SYNC_SCHEDULE = "syncSchedule";
  public static final String PROP_MINING_PROCESS_FIELD_ALERTS = "miningProcessFieldAlerts";
  public static final String PROP_MINING_PROCESS_AD_HOC_EVENTS = "miningProcessAdhocEvents";
  static final String MINING_PROCESS_TABLE = "mining_process";
  private static final String MINING_PROCESS_DEP_TABLE = "mining_process_dep";
  private static final String MINING_PROCESS_ID = "mining_process_id";

  private Long id;
  private String uuid;
  private String logId;
  private String name;
  private String description;
  private Double averageCaseDuration;
  private Double medianCaseDuration;
  private Long numberOfCases;
  private Long numberOfEvents;
  private Integer numberOfDistinctActions;
  private Set<MiningProcessProvider> miningProcessProviders;
  private MiningSyncStatus syncStatus;
  private Long lastSuccessfulUpdateMs;
  private String lastSuccessfulUpdateByUserUuid;
  private Long failedUpdateMs;
  private String failedUpdateByUserUuid;
  private String mostRecentErrorJson;
  private Set<DataFabricDependencyEntity> miningProcessDependencies = new HashSet<>();
  private MiningSyncSchedule syncSchedule;

  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  private Set<MiningProcessFieldAlert> miningProcessFieldAlerts;
  private Set<MiningProcessAdHocEvent> miningProcessAdHocEvents;

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.MINING_PROCESS_ANALYST,
      Roles.MINING_PROCESS_VIEWER
  );

  /**
   * <p>Required for Hibernate: Needs to be public only by the object returned by
   * {@link org.hibernate.transform.Transformers#aliasToBean(Class)} and used to get a list of {@link MiningProcess}
   * objects when using a {@link org.hibernate.criterion.ProjectionList} to select individual fields to populate.</p>
   */
  public MiningProcess() { }

  public MiningProcess(
      String logId,
      String name,
      String description,
      List<MiningProcessProvider> providers) {
    this(UUID.randomUUID().toString(), logId, name, description, providers, null);
  }

  public MiningProcess(
      String uuid,
      String logId,
      String name,
      String description,
      List<MiningProcessProvider> providers,
      Set<DataFabricDependencyEntity> miningProcessDependencies) {
    if (StringUtils.isBlank(uuid)) {
      throw new IllegalArgumentException("UUID of Mining Process cannot be null");
    }
    this.logId = logId;
    this.name = name;
    this.description = description;
    this.uuid = uuid;
    if (providers != null) {
      this.miningProcessProviders = new HashSet<>();
      providers.forEach(provider -> {
        provider.setMiningProcess(this);
        this.miningProcessProviders.add(provider);
      });
    } else {
      this.miningProcessProviders = new HashSet<>();
    }

    if (miningProcessDependencies == null) {
      this.miningProcessDependencies = Collections.emptySet();
    } else {
      this.miningProcessDependencies = miningProcessDependencies;
    }
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

  @Column(name = "uuid", nullable = false, updatable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "log_id", nullable = false)
  public String getLogId() {
    return logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
  }

  @Column(name = "name", nullable = false, unique = true)
  @CopilotField(name = "miningProcessName")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", nullable = true, length = COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "reasoning", description = "Describes the goal of this process.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the average case duration in milliseconds. A null return value means that the Mining Log
   * creation is in progress with no statistics to return.
   */
  @Column(name = "average_case_duration_ms")
  public Double getAverageCaseDuration() {
    return averageCaseDuration;
  }

  public void setAverageCaseDuration(Double averageCaseDuration) {
    this.averageCaseDuration = averageCaseDuration;
  }

  /**
   * @return the median case duration in milliseconds. A null return value means that the Mining Log
   * creation is in progress with no statistics to return.
   */
  @Column(name = "median_case_duration_ms")
  public Double getMedianCaseDuration() {
    return medianCaseDuration;
  }

  public void setMedianCaseDuration(Double medianCaseDuration) {
    this.medianCaseDuration = medianCaseDuration;
  }

  /**
   * @return the number of cases. A null return value means that the Mining Log
   * creation is in progress with no statistics to return.
   */
  @Column(name = "number_of_cases")
  public Long getNumberOfCases() {
    return numberOfCases;
  }

  public void setNumberOfCases(Long numberOfCases) {
    this.numberOfCases = numberOfCases;
  }

  /**
   * @return the number of events. A null return value means that the Mining Log
   * creation is in progress with no statistics to return.
   */
  @Column(name = "number_of_events")
  public Long getNumberOfEvents() {
    return numberOfEvents;
  }

  public void setNumberOfEvents(Long numberOfEvents) {
    this.numberOfEvents = numberOfEvents;
  }

  /**
   * @return the number of distinct {@link MiningDataSemanticType#ACTION} values referenced by `this`
   *   process. A null return value means that the Mining Log creation is in progress with no statistics
   *   defined yet
   */
  @Column(name = "number_of_distinct_actions")
  public Integer getNumberOfDistinctActions() {
    return numberOfDistinctActions;
  }

  public void setNumberOfDistinctActions(Integer numberOfDistinctActions) {
    this.numberOfDistinctActions = numberOfDistinctActions;
  }

  // The miningProcessProviders property is deliberately set to package private access because it's lazy loaded
  // and not every instance is initialized. If you need to access this property and guarantee that it's been
  // initialized, use the appropriate method on the MiningProcessService class to retrieve them.
  @OneToMany(mappedBy = "miningProcess", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  Set<MiningProcessProvider> getMiningProcessProviders() {
    return miningProcessProviders;
  }

  public void setMiningProcessProviders(Set<MiningProcessProvider> miningProcessProviders) {
    this.miningProcessProviders = miningProcessProviders;
  }

  @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
  @JoinTable(
      name = MINING_PROCESS_DEP_TABLE,
      joinColumns = @JoinColumn(name = MINING_PROCESS_ID),
      inverseJoinColumns = @JoinColumn(name = "dependency_id")
  )
  Set<DataFabricDependencyEntity> getMiningProcessDependencies() {
    return miningProcessDependencies;
  }

  public void setMiningProcessDependencies(Set<DataFabricDependencyEntity> miningProcessDependencies) {
    this.miningProcessDependencies = miningProcessDependencies;
  }

  /**
   * The syncSchedule property is deliberately set to package private access because it's lazy loaded
   * and not every instance is initialized. If you need to access this property and guarantee that it's been
   * initialized, use the appropriate method on the MiningProcessService class to retrieve them.
   * @see MiningProcessService#getProcessSyncSchedule(Long)
   */
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "sync_schedule_id", referencedColumnName = "id")
  MiningSyncSchedule getSyncSchedule() {
    return syncSchedule;
  }

  /**
   * The dayOfWeek property of the {@link MiningSyncSchedule} should only be persisted if the
   * frequency property of the {@link MiningSyncSchedule} is {@link MiningScheduleFrequencyType#WEEKLY}.
   * Otherwise, null is persisted.
   */
  public void setSyncSchedule(MiningSyncSchedule syncSchedule) {
    if (syncSchedule != null && WEEKLY != syncSchedule.getFrequency()) {
      syncSchedule.setDayOfWeek(null);
    }
    this.syncSchedule = syncSchedule;
  }

  /**
   * Used primarily count the number of alerts associated with this object,
   * delete alerts by removing from the mining process field that it references.
   * This property is lazy loaded but it is sometimes initialized when fetching. If it is not initialized, use the
   * appropriate method on the MiningProcessService class to retrieve them.
   * @see MiningProcessService#getMiningProcessFieldAlerts(Long)
   */
  @OneToMany(mappedBy = PROP_MINING_PROCESS_IN_PROCESS_ALERT, fetch = FetchType.LAZY)
  public Set<MiningProcessFieldAlert> getMiningProcessFieldAlerts() {
    return miningProcessFieldAlerts;
  }

  @SuppressWarnings("unused")
  public void setMiningProcessFieldAlerts(Set<MiningProcessFieldAlert> miningProcessFieldAlerts) {
    this.miningProcessFieldAlerts = miningProcessFieldAlerts;
  }

  /**
   * This property is lazy loaded but it is sometimes initialized when fetching. If it is not initialized, use the
   * appropriate method on the MiningProcessService class to retrieve them.
   * @see MiningProcessService#getMiningProcessAdHocEvents(Long)
   */
  @OneToMany(mappedBy = PROP_MINING_PROCESS_IN_AD_HOC_EVENT, fetch = FetchType.LAZY)
  public Set<MiningProcessAdHocEvent> getMiningProcessAdhocEvents() {
    return miningProcessAdHocEvents;
  }

  public void setMiningProcessAdhocEvents(Set<MiningProcessAdHocEvent> miningProcessAdHocEvents) {
    this.miningProcessAdHocEvents = miningProcessAdHocEvents;
  }

  @Column(name="sync_status", nullable = false)
  private byte getSyncStatusByte() {
    if (syncStatus == null) {
      return MiningSyncStatus.NOT_SYNCED.getIndex();
    }
    return syncStatus.getIndex();
  }

  @SuppressWarnings("unused")
  private void setSyncStatusByte(byte index) {
    setSyncStatus(MiningSyncStatus.valueOf(index));
  }

  @Transient
  public MiningSyncStatus getSyncStatus() {
    if (syncStatus == null) {
      return MiningSyncStatus.NOT_SYNCED;
    }
    return syncStatus;
  }

  public void setSyncStatus(MiningSyncStatus syncStatus) {
    this.syncStatus = syncStatus;
  }

  // =============================================================
  // last_successful_update_ms
  // =============================================================

  @Column(name = "last_successful_update_ms")
  public Long getLastSuccessfulUpdateMs() {
    return lastSuccessfulUpdateMs;
  }

  public void setLastSuccessfulUpdateMs(Long lastSuccessfulUpdateMs) {
    this.lastSuccessfulUpdateMs = lastSuccessfulUpdateMs;
  }

  // =============================================================
  // last_successful_update_by
  // =============================================================

  @Column(name = "last_successful_update_by")
  public String getLastSuccessfulUpdateByUserUuid() {
    return lastSuccessfulUpdateByUserUuid;
  }

  public void setLastSuccessfulUpdateByUserUuid(String lastSuccessfulUpdateByUserUuid) {
    this.lastSuccessfulUpdateByUserUuid = lastSuccessfulUpdateByUserUuid;
  }

  // =============================================================
  // failed_update_ms
  // =============================================================

  @Column(name = "failed_update_ms")
  public Long getFailedUpdateMs() {
    return failedUpdateMs;
  }

  public void setFailedUpdateMs(Long failedUpdateMs) {
    this.failedUpdateMs = failedUpdateMs;
  }

  // =============================================================
  // failed_update_by
  // =============================================================

  @Column(name = "failed_update_by")
  public String getFailedUpdateByUserUuid() {
    return failedUpdateByUserUuid;
  }

  public void setFailedUpdateByUserUuid(String failedUpdateByUserUuid) {
    this.failedUpdateByUserUuid = failedUpdateByUserUuid;
  }

  // =============================================================
  // most_recent_error
  // =============================================================
  @Column(name = "most_recent_error", nullable = true)
  @Lob
  public String getMostRecentErrorJson() {
    return mostRecentErrorJson;
  }

  public void setMostRecentErrorJson(String mostRecentErrorJson) {
    this.mostRecentErrorJson = mostRecentErrorJson;
  }

  // =============================================================
  // mining_process_rm
  // =============================================================

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "mining_process_rm",
      joinColumns = @JoinColumn(name = MINING_PROCESS_ID),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  // ============================= BEGIN IMPLEMENTATION OF HasRoleMap ==============================
  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      roleMapBuilder.entries(roleMapEntry);
    }

    return roleMapBuilder.build();
  }

  /**
   * @deprecated Although this method is required to be public to implement an interface, casual devs should
   * not call it unless they know exactly what they're doing, since this method doesn't ensure the Group
   * ids in `roleMap` are converted to the proper RDBMS representation, instead - call
   * {@link MiningProcessService#setRoleMap(Long, RoleMap, boolean)}.
   */
  @Override
  @Deprecated
  public void setRoleMap(RoleMap roleMap) {
    if (this.roleMapEntries != null) {
      this.roleMapEntries.clear();
    }
    if (roleMap != null) {
      if (this.roleMapEntries == null) {
        this.roleMapEntries = new HashSet<>();
      }
      this.roleMapEntries.addAll(roleMap.getEntriesByRole().values());
    }
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.MINING_PROCESS_VIEWER.getName();
  }

  @Override
  @Transient
  public boolean isPublic() {
    // MiningProcesses are never public (visible to all users).
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // MiningProcesses are never public (visible to all users).
  }
  // ============================== END IMPLEMENTATION OF HasRoleMap ===============================

  @Override
  public int hashCode() {
    return Objects.hash(logId, uuid, name, averageCaseDuration, medianCaseDuration, numberOfCases,
        numberOfEvents, numberOfDistinctActions, syncStatus, lastSuccessfulUpdateMs,
        lastSuccessfulUpdateByUserUuid, failedUpdateMs, failedUpdateByUserUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MiningProcess other = (MiningProcess)obj;
    return Objects.equals(this.logId, other.logId) &&
        Objects.equals(this.uuid, other.uuid) &&
        Objects.equals(this.name, other.name) &&
        Objects.equals(this.averageCaseDuration, other.averageCaseDuration) &&
        Objects.equals(this.medianCaseDuration, other.medianCaseDuration) &&
        Objects.equals(this.numberOfCases, other.numberOfCases) &&
        Objects.equals(this.numberOfEvents, other.numberOfEvents) &&
        Objects.equals(this.numberOfDistinctActions, other.numberOfDistinctActions) &&
        Objects.equals(this.syncStatus, other.syncStatus) &&
        Objects.equals(this.lastSuccessfulUpdateMs, other.lastSuccessfulUpdateMs) &&
        Objects.equals(this.lastSuccessfulUpdateByUserUuid, other.lastSuccessfulUpdateByUserUuid) &&
        Objects.equals(this.failedUpdateMs, other.failedUpdateMs) &&
        Objects.equals(this.failedUpdateByUserUuid, other.failedUpdateByUserUuid);
  }

  @Override
  public String toString() {
    return "MiningProcess [uuid=" + uuid +
        ", name=" + name +
        ", logId=" + logId +
        ", averageCaseDuration=" + averageCaseDuration +
        ", medianCaseDuration=" + medianCaseDuration +
        ", numberOfCases=" + numberOfCases +
        ", numberOfEvents=" + numberOfEvents +
        ", numberOfDistinctActions=" + numberOfDistinctActions +
        ", miningProcessProviders=" + miningProcessProviders +
        ", syncStatus=" + syncStatus +
        ", lastSuccessfulUpdateMs=" + lastSuccessfulUpdateMs +
        ", lastSuccessfulUpdateByUserUuid=" + lastSuccessfulUpdateByUserUuid +
        ", failedUpdateMs=" + failedUpdateMs +
        ", failedUpdateByUserUuid=" + failedUpdateByUserUuid + "]";
  }
}
