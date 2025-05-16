package com.appiancorp.record.entities;

import static com.appiancorp.record.sources.schedule.RefreshFrequency.DAILY;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.record.sources.schedule.ReadOnlyRecordSourceRefreshSchedule;
import com.appiancorp.record.sources.schedule.RefreshFrequency;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordSourceRefreshSchedule;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "record_source_rf_schedule")
@IgnoreJpa
@XmlRootElement(name = "recordSourceRefreshScheduleCfg", namespace = Type.APPIAN_NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType( name = "RecordSourceRefreshScheduleCfg", namespace = Type.APPIAN_NAMESPACE, propOrder = {"frequency", "value", "activated"})
public class RecordSourceRefreshScheduleCfg implements Id<Long>, ReadOnlyRecordSourceRefreshSchedule {
  private Long id;
  private RefreshFrequency frequency = DAILY;
  private String value;
  private boolean activated;

  public RecordSourceRefreshScheduleCfg() {

  }

  public RecordSourceRefreshScheduleCfg(DesignerDtoRecordSourceRefreshSchedule refreshSchedule) {
    // use the dto refresh schedule, ui currently has the schedule set to defaults
    this.id = refreshSchedule.getId();
    this.frequency = RefreshFrequency.valueOf(refreshSchedule.getFrequency());
    this.value = refreshSchedule.getValue();
    this.activated = refreshSchedule.isActivated();
  }

  public RecordSourceRefreshScheduleCfg(RecordSourceRefreshScheduleCfg refreshSchedule) {
    this.id = refreshSchedule.id;
    this.frequency = refreshSchedule.frequency;
    this.value = refreshSchedule.value;
    this.activated = refreshSchedule.activated;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Column(name = "frequency", nullable = false)
  private byte getFrequencyByte() {
    return frequency.getCode();
  }

  private void setFrequencyByte(Byte code) {
    this.frequency = RefreshFrequency.getByCode(code);
  }

  @Transient
  public RefreshFrequency getFrequency() {
    return frequency;
  }

  public void setFrequency(RefreshFrequency frequency) {
    this.frequency = frequency;
  }

  @Column(name = "value")
  @Lob
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "activated")
  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordSourceRefreshScheduleCfg)) {
      return false;
    }

    RecordSourceRefreshScheduleCfg that = (RecordSourceRefreshScheduleCfg)o;

    return new EqualsBuilder().append(id, that.getId())
        .append(frequency, that.frequency)
        .append(value, that.value)
        .append(activated, that.activated)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(
        id,
        frequency,
        value,
        activated
    );
  }
}
