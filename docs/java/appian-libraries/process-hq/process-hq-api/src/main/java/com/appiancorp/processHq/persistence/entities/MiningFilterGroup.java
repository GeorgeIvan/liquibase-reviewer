package com.appiancorp.processHq.persistence.entities;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi;

@Entity
@Table(name = "mining_filter_group")
@CopilotClass(name = "filterGroup", description = "A group of filters for selecting Cases covered by the View this filter group belongs to.")
public class MiningFilterGroup {
  public static final String PROP_ID = "id";
  public static final String PROP_ATTRIBUTE_FILTERS = "categoricalAttributeFilters";
  public static final String PROP_MINING_SCOPE = "miningScope";

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private long id;

  @OneToOne
  @JoinColumn(name = "mining_scope_id", referencedColumnName = "id", nullable = true)
  private MiningScope miningScope;

  @OneToOne
  @JoinColumn(name = "mining_kpi_id", referencedColumnName = "id", nullable = true)
  private MiningKpi miningKpi;

  @OneToMany(mappedBy = "filterGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  private List<MiningActivityFilter> activityFilters = new ArrayList<>();

  @OneToMany(mappedBy = "filterGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MiningSequenceFilter> sequenceFilters = new HashSet<>();

  @OneToMany(mappedBy = "filterGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MiningDurationFilter> durationFilters = new HashSet<>();

  @OneToMany(mappedBy = "filterGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MiningCategoricalAttributeFilter> categoricalAttributeFilters = new HashSet<>();

  public MiningFilterGroup() {}

  public MiningFilterGroup(MiningScope scope) {
    this.miningScope = scope;
  }

  public MiningFilterGroup(MiningKpi kpi) {
    this.miningKpi = kpi;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public MiningScope getMiningScope() { return miningScope; }

  public void setMiningScope(MiningScope miningScope) {
    if (miningKpi != null) {
      throw new IllegalStateException("Cannot set a view since the KPI '" + miningKpi.getName() +
          "' already exists for this filter group");
    }
    this.miningScope = miningScope;
  }

  public MiningKpi getMiningKpi() { return miningKpi; }

  public void setMiningKpi(MiningKpi miningKpi) {
    if (miningScope != null) {
      throw new IllegalStateException("Cannot set a KPI since the view '" + miningScope.getName() +
          "' already exists for this filter group");
    }
    this.miningKpi = miningKpi;
  }

  @CopilotField(name = "activityFilters")
  public List<MiningActivityFilter> getActivityFilters() {
    return activityFilters;
  }

  public void setActivityFilters(List<MiningActivityFilter> miningActivityFilters) {
    this.activityFilters.clear();
    if (miningActivityFilters != null) {
      this.activityFilters.addAll(miningActivityFilters);
    }
  }

  @CopilotField(name = "sequenceFilters")
  public Set<MiningSequenceFilter> getSequenceFilters() {
    return sequenceFilters;
  }

  public void setSequenceFilters(Set<MiningSequenceFilter> miningSequenceFilters) {
    this.sequenceFilters.clear();
    if (miningSequenceFilters != null) {
      this.sequenceFilters.addAll(miningSequenceFilters);
    }
  }

  @CopilotField(name = "durationFilters")
  public Set<MiningDurationFilter> getDurationFilters() {
    return durationFilters;
  }

  public void setDurationFilters(Set<MiningDurationFilter> miningDurationFilters) {
    this.durationFilters.clear();
    if (miningDurationFilters != null) {
      this.durationFilters.addAll(miningDurationFilters);
    }
  }

  @CopilotField(name = "attributeFilters")
  public List<MiningCategoricalAttributeFilter> getCategoricalAttributeFilters() {
    // To avoid changing the public contract on the entity we must sort this list
    // on the way out to mimic the default Hibernate List semantics, which is to
    // order by the Pkey of the entity. This keeps tests from being fragile and reduces
    // blast radius of changing the backing field to Set semantics.
    return categoricalAttributeFilters.stream()
        .sorted(Comparator.comparing(MiningCategoricalAttributeFilter::getId))
        .toList();
  }

  public void setCategoricalAttributeFilters(List<MiningCategoricalAttributeFilter> miningCategoricalAttributeFilters) {
    this.categoricalAttributeFilters.clear();
    if (miningCategoricalAttributeFilters != null) {
      this.categoricalAttributeFilters.addAll(miningCategoricalAttributeFilters);
    }
  }

  @SuppressWarnings({"checkstyle:NPathComplexity"})
  public boolean contentsEquals(Object o) {
    if (!(o instanceof MiningFilterGroup)) {
      return false;
    }
    if (this == o) {
      return true;
    }
    MiningFilterGroup that = (MiningFilterGroup)o;
    if ((activityFilters.size() != that.activityFilters.size()) ||
        (sequenceFilters.size() != that.sequenceFilters.size()) ||
        (durationFilters.size() != that.durationFilters.size()) ||
        (categoricalAttributeFilters.size() != that.categoricalAttributeFilters.size())) {
      return false;
    }
    List<MiningCategoricalAttributeFilter> copyOfTargetAttributeFilters = new ArrayList<>(
        that.categoricalAttributeFilters);
    List<MiningActivityFilter> copyOfTargetActivityFilters = new ArrayList<>(that.activityFilters);
    activityFilters.forEach(sourceFilter -> that.activityFilters.forEach(targetFilter -> {
      if (sourceFilter.contentsEquals(targetFilter)) {
        copyOfTargetActivityFilters.remove(targetFilter);
      }
    }));

    List<MiningSequenceFilter> copyOfTargetSequenceFilters = new ArrayList<>(that.sequenceFilters);
    sequenceFilters.forEach(sourceFilter -> that.sequenceFilters.forEach(targetFilter -> {
      if (sourceFilter.equals(targetFilter)) {
        copyOfTargetSequenceFilters.remove(targetFilter);
      }
    }));

    List<MiningDurationFilter> copyOfTargetDurationFilters = new ArrayList<>(that.durationFilters);
    durationFilters.forEach(sourceFilter -> that.durationFilters.forEach(targetFilter -> {
      if (sourceFilter.equals(targetFilter)) {
        copyOfTargetDurationFilters.remove(targetFilter);
      }
    }));

    categoricalAttributeFilters.forEach(
        sourceFilter -> that.categoricalAttributeFilters.forEach(targetFilter -> {
          if (sourceFilter.contentsEquals(targetFilter)) {
            copyOfTargetAttributeFilters.remove(targetFilter);
          }
        }));
    return copyOfTargetActivityFilters.isEmpty() && copyOfTargetSequenceFilters.isEmpty() &&
        copyOfTargetDurationFilters.isEmpty() && copyOfTargetAttributeFilters.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MiningFilterGroup)) {
      return false;
    }
    if (this == o)
      return true;
    MiningFilterGroup that = (MiningFilterGroup)o;
    return getId() == that.getId() &&
        equal(activityFilters, that.activityFilters) &&
        equal(sequenceFilters, that.sequenceFilters) &&
        equal(durationFilters, that.durationFilters) &&
        equal(categoricalAttributeFilters, that.categoricalAttributeFilters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), activityFilters, sequenceFilters, durationFilters, categoricalAttributeFilters);
  }

  @Override
  public String toString() {
    return "MiningFilterGroup{" + "id=" + id + ", scopeId=" + (miningScope == null ? "''" : miningScope.getId()) + ", kpiId="
        + (miningKpi == null ? "''" : miningKpi.getId()) + ",activityFilters=" + activityFilters + ",sequenceFilters=" + sequenceFilters
        + ",durationFilters=" + durationFilters + ", categoricalAttributeFilters=" + categoricalAttributeFilters + '}';
  }
}
