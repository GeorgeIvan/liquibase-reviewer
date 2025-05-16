package com.appiancorp.enduserreporting.miningprocess.persistence;

import static com.appiancorp.enduserreporting.miningprocess.persistence.SsaMiningKpiDashboardItemDecoratorType.MINING_KPI_DECORATOR_TYPE;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.enduserreporting.entities.SsaDashboardItemDecoratorType;
import com.appiancorp.enduserreporting.entities.SsaLibraryObjectCfgImpl;
import com.appiancorp.enduserreporting.entities.SsaObjectType;
import com.appiancorp.enduserreporting.persistence.SsaLibraryObjectCfg;
import com.appiancorp.miningdatasync.data.MiningProcess;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "ssa_mining_dep")
public class SsaMiningKpiDecoratorImpl implements SsaMiningKpiDecorator, HasAuditInfo {
  private Long id;
  private String uuid;
  private String name;
  private String description;
  private SsaLibraryObjectCfgImpl dashboard;
  private MiningKpi miningKpi;
  private AuditInfo auditInfo = new AuditInfo();
  private SsaMiningKpiTimePeriodOption timePeriod;
  private boolean showThreshold;

  public static final String PROP_DASHBOARD = "dashboard";

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  public SsaMiningKpiDecoratorImpl() {}

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dashboard_id", nullable = false, updatable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public SsaLibraryObjectCfgImpl getDashboard() {
    return dashboard;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDashboard(SsaLibraryObjectCfg dashboard) {
    if (dashboard instanceof SsaLibraryObjectCfgImpl) {
      this.dashboard = (SsaLibraryObjectCfgImpl)dashboard;
    } else {
      throw new IllegalArgumentException("setDashboard method requires an instance of SsaLibraryObjectCfgImpl");
    }
  }

  @Transient
  public SsaObjectType getObjectType() {
    return MINING_KPI_DECORATOR_TYPE;
  }

  @Transient
  @Override
  public Long getMiningKpiId() {
    return miningKpi == null ? null : miningKpi.getId();
  }

  @Override
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_dep_id", nullable = false, updatable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public MiningKpi getMiningKpi() {
    return miningKpi;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setMiningKpi(MiningKpi miningKpi) {
    this.miningKpi = miningKpi;
  }

  @Override
  @Transient
  public MiningProcess getMiningProcess() {
    if(miningKpi == null || miningKpi.getMiningScope() == null) {
      return null;
    }
    return miningKpi.getMiningScope().getMiningProcess();
  }

  @Override
  @Embedded
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  @Transient
  public String getCreatedByUsername() {
    return getAuditInfo().getCreatedByUsername();
  }

  @Override
  @Transient
  public Timestamp getCreatedTs() {
    return getAuditInfo().getCreatedTs();
  }

  @Override
  @Transient
  public String getUpdatedByUsername() {
    return getAuditInfo().getUpdatedByUsername();
  }

  @Override
  @Transient
  public Timestamp getLastUpdatedTs() {
    return getAuditInfo().getUpdatedTs();
  }

  @Override
  @Transient
  public SsaDashboardItemDecoratorType getDashboardItemDecoratorType() {
    return MINING_KPI_DECORATOR_TYPE;
  }

  @Override
  @Transient
  public SsaMiningKpiTimePeriodOption getTimePeriod() {
    return timePeriod;
  }

  @Override
  public void setTimePeriod(SsaMiningKpiTimePeriodOption timePeriod) {
    this.timePeriod = timePeriod;
  }

  @Column(name = "time_period", nullable = false)
  private Byte getTimePeriodByte() {
    return timePeriod != null ? timePeriod.getCode() : SsaMiningKpiTimePeriodOption.LAST_7_DAYS.getCode();
  }

  private void setTimePeriodByte(Byte timePeriodByte) {
    setTimePeriod(SsaMiningKpiTimePeriodOption.valueOfFromByte(timePeriodByte));
  }

  @Column(name = "show_threshold", nullable = false)
  @Override
  public boolean isShowThreshold() {
    return showThreshold;
  }

  @Override
  public void setShowThreshold(boolean showThreshold) {
    this.showThreshold = showThreshold;
  }

  @Override
  @Transient
  public String getConfigJson() {
    //Config Json is deprecated
    return "";
  }

  @Override
  public void setConfigJson(String configJson) {
    //Config Json is deprecated
  }

  // ============================== Begin implementation of boilerplate methods ===============================
  // NOTE: Excluding dashboard and auditInfo from these methods because they are lazy loaded

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, description, miningKpi.getId(), timePeriod, showThreshold);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o){
      return true;
    }
    if (!(o instanceof SsaMiningKpiDecoratorImpl that)) {
      return false;
    }
    return Objects.equals(uuid, that.uuid) &&
        Objects.equals(name, that.name) &&
        Objects.equals(description, that.description) &&
        Objects.equals(miningKpi.getId(), that.miningKpi.getId()) &&
        timePeriod == that.timePeriod &&
        Objects.equals(showThreshold, that.showThreshold);
  }

  @Override
  public String toString() {
    return "SsaMiningKpiDecoratorImpl{" + "uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", miningKpi=" + miningKpi.getId() +
        ", showThreshold=" + showThreshold +
        ", timePeriod=" + timePeriod + '}';
  }
  // ============================== End implementation of boilerplate methods ===============================
}
