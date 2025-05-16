package com.appiancorp.processHq.persistence.entities.customKpi;

import static com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiSubscriber.SUBSCRIBER_TABLE;
import static com.appiancorp.security.user.User.JOIN_COL_USR_ID;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.MiningFilterGroup;
import com.appiancorp.processHq.persistence.entities.MiningScope;
import com.appiancorp.processHq.persistence.entities.customKpi.alert.MiningKpiThreshold;
import com.appiancorp.processHq.persistence.entities.customKpi.field.MiningKpiField;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.security.user.User;

@Entity
@Table(name = "mining_kpi")
@CopilotClass(name = "miningKpi", description = "A kpi is used to measure the most important aspects of a business process. A KPI helps users focus on the performance of their process and identify potential areas for improvement.")
public class MiningKpi implements HasAuditInfo {
  public static final String PROP_AUDIT_INFO = "auditInfo";
  public static final String PROP_CREATED_TS = "createdTsLong";
  public static final String PROP_ID = "id";
  public static final String PROP_KPI_NAME = "name";
  public static final String PROP_FILTER_GROUP = "miningFilterGroup";
  public static final String COL_MINING_KPI_ID = "mining_kpi_id";
  private long id;
  private MiningScope miningScope;
  private MiningFilterGroup miningFilterGroup;
  private String name;
  private String description;
  private KpiDisplayType displayType;
  private MiningKpiType kpiType;
  private boolean defaultKpi;
  private boolean pinned;
  private Set<MiningKpiField> fields = new HashSet<>();
  private AuditInfo auditInfo = new AuditInfo();
  private Set<MiningKpiThreshold> thresholds = new HashSet<>();
  private Set<User> subscribers = new HashSet<>();
  private KpiAggregationType defaultAggregation;

  public MiningKpi() {
    // For JPA
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mining_scope_id", referencedColumnName = "id", nullable = false)
  public MiningScope getMiningScope() {
    return this.miningScope;
  }

  public void setMiningScope(MiningScope miningScope) {
    this.miningScope = miningScope;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @CopilotField(name = "title")
  public String getName() { return name;}

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Transient
  @CopilotField(name = "displayType", description = "Indicates if the count, or the percentage, or both is shown the KPI display.")
  public KpiDisplayType getDisplayType() {
    return displayType;
  }

  public void setDisplayType(KpiDisplayType displayType) {
    this.displayType = displayType;
  }

  @Column(name = "display_type")
  private Byte getDisplayTypeByte() {
    return displayType != null ? displayType.getCode() : KpiDisplayType.ABSOLUTE.getCode();
  }

  private void setDisplayTypeByte(Byte type) {
    setDisplayType(KpiDisplayType.valueOf(type));
  }

  @OneToOne(mappedBy = "miningKpi", cascade = CascadeType.ALL, orphanRemoval = true)
  @CopilotField(name = "kpiFilter")
  public MiningFilterGroup getMiningFilterGroup() {
    return miningFilterGroup;
  }

  public void setMiningFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.miningFilterGroup = miningFilterGroup;
  }

  @OneToOne(mappedBy = "miningKpi", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @CopilotField(name = "kpiType")
  public MiningKpiType getKpiType() {
    return kpiType;
  }

  public void setKpiType(MiningKpiType kpiType) {
    this.kpiType = kpiType;
  }

  @OneToMany(mappedBy = "miningKpi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @OrderBy
  @CopilotField(name = "kpiFields")
  public Set<MiningKpiField> getFields() {
    return fields;
  }

  public void setFields(Set<MiningKpiField> fields) {
    this.fields = fields;
  }

  @ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
  @Fetch(FetchMode.JOIN)
  @JoinTable(
      name = SUBSCRIBER_TABLE,
      joinColumns = @JoinColumn(name = COL_MINING_KPI_ID, updatable = false, insertable = false),
      inverseJoinColumns = @JoinColumn(name = JOIN_COL_USR_ID, updatable = false, insertable = false)
  )
  public Set<User> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(Set<User> subscribers) {
    this.subscribers = subscribers;
  }

  @Column(name = "is_default_kpi")
  public Boolean isDefaultKpi() {
    return defaultKpi;
  }

  public void setDefaultKpi(Boolean defaultKpi) {
    this.defaultKpi = defaultKpi;
  }

  @Column(name = "is_pinned")
  public Boolean isPinned() {
    return pinned;
  }

  public void setPinned(Boolean value) {
    this.pinned = value;
  }

  @OneToMany(mappedBy = "miningKpi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  public Set<MiningKpiThreshold> getThresholds() {
    return thresholds;
  }

  public void setThresholds(Set<MiningKpiThreshold> thresholds) {
    this.thresholds = thresholds;
  }

  @Column(name = "default_aggregation", nullable = true)
  private Byte getDefaultAggregationByte() {
    return defaultAggregation == null ? null : defaultAggregation.getValue();
  }

  private void setDefaultAggregationByte(Byte value) {
    if (value == null) {
      this.defaultAggregation = null;
    } else {
      this.defaultAggregation = KpiAggregationType.getByValue(value);
    }
  }

  @Transient
  public KpiAggregationType getDefaultAggregation() {
    return defaultAggregation;
  }

  public void setDefaultAggregation(KpiAggregationType defaultAggregation) {
    this.defaultAggregation = defaultAggregation;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MiningKpi)) {
      return false;
    }
    MiningKpi miningKpi = (MiningKpi)o;
    boolean miningFilterGroupsMatch = true;
    if (miningFilterGroup != null && ((MiningKpi)o).getMiningFilterGroup() != null) {
      miningFilterGroupsMatch = miningFilterGroup.contentsEquals(miningKpi.getMiningFilterGroup());
    }
    return id == miningKpi.id && Objects.equals(miningScope, miningKpi.miningScope) &&
        Objects.equals(name, miningKpi.name) && Objects.equals(description, miningKpi.description) &&
        displayType == miningKpi.displayType && Objects.equals(fields, miningKpi.fields) &&
        Objects.equals(auditInfo, miningKpi.auditInfo) &&
        Objects.equals(pinned, miningKpi.pinned) && miningFilterGroupsMatch &&
        Objects.equals(defaultKpi, miningKpi.defaultKpi) && Objects.equals(
        defaultAggregation, miningKpi.defaultAggregation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, miningScope, name, description, displayType, fields, auditInfo, defaultKpi,
        pinned, defaultAggregation);
  }

  @Override
  public String toString() {
    return "MiningKpi{" + "id=" + id + ", miningScope=" + miningScope.getName() + ", name='" + name +
        '\'' + ", description='" + description + '\'' + ", displayType=" + displayType + ", defaultKpi= " +
        defaultKpi + ", pinned= " + pinned + ", auditInfo='" + auditInfo + ", defaultAggregation=" +
        defaultAggregation + '}';
  }
}
