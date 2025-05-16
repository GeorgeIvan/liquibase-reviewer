package com.appiancorp.miningdatasync.schedule;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.type.cdt.value.ProcessMiningSyncScheduleDto;

@Entity
@Table(name = "mining_process_sync_sched")
public final class MiningSyncSchedule {
  public static final String PROP_ACTIVATED = "activated";
  public static final String PROP_FREQUENCY_BYTE = "frequencyByte";
  private Long id;
  private MiningScheduleFrequencyType frequency;
  private MiningScheduleWeekDay dayOfWeek;
  private String scheduleTimeValue;
  private boolean activated;

  MiningSyncSchedule() {
  }

  private MiningSyncSchedule(
      Long id,
      MiningScheduleFrequencyType frequency,
      MiningScheduleWeekDay weekDay,
      String scheduleTimeValue,
      boolean activated) {
    this.id = id;
    this.frequency = frequency;
    this.dayOfWeek = weekDay;
    this.scheduleTimeValue = scheduleTimeValue;
    this.activated = activated;
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

  @Column(name = "frequency", nullable = false)
  private Byte getFrequencyByte() {
    return frequency == null ? null : frequency.getValue();
  }

  private void setFrequencyByte(byte code) {
    this.frequency = MiningScheduleFrequencyType.getByValue(code);
  }

  @Transient
  public MiningScheduleFrequencyType getFrequency() {
    return frequency;
  }

  public void setFrequency(MiningScheduleFrequencyType frequency) {
    this.frequency = frequency;
  }

  @Column(name = "day_of_week")
  private Byte getDayOfWeekByte() {
    return dayOfWeek == null ? null : dayOfWeek.getValue();
  }

  private void setDayOfWeekByte(Byte value) {
    if (value == null) {
      this.dayOfWeek = null;
      return;
    }
    this.dayOfWeek = MiningScheduleWeekDay.getByValue(value);
  }

  @Transient
  public MiningScheduleWeekDay getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(MiningScheduleWeekDay dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  /**
   * Returns the schedule_time_value JSON string in following format below
   * <pre>{@code
   * "{
   *   "hour": "12",
   *   "minute": "05",
   *   "meridiem": "AM",
   *   "timeZone": "GMT"      // As accepted by Java's TimeZone.getTimeZone()
   * }"
   * }</pre>
   */
  @Column(name = "schedule_time_value", nullable = false)
  @Lob
  public String getScheduleTimeValue() {
    return scheduleTimeValue;
  }

  public void setScheduleTimeValue(String scheduleTimeValue) {
    this.scheduleTimeValue = scheduleTimeValue;
  }

  @Column(name = "activated", nullable = false)
  public boolean getActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof MiningSyncSchedule)) {
      return false;
    }

    MiningSyncSchedule that = (MiningSyncSchedule)o;

    return new EqualsBuilder().append(id, that.getId())
        .append(frequency, that.frequency)
        .append(dayOfWeek, that.dayOfWeek)
        .append(scheduleTimeValue, that.scheduleTimeValue)
        .append(activated, that.activated)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, frequency, dayOfWeek, scheduleTimeValue, activated);
  }

  public static class MiningSyncScheduleBuilder {
    private Long id;
    private MiningScheduleFrequencyType frequency;
    private MiningScheduleWeekDay weekDay;
    private String scheduleTimeValue;
    private boolean activated;

    public MiningSyncScheduleBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public MiningSyncScheduleBuilder frequency(MiningScheduleFrequencyType frequency) {
      this.frequency = frequency;
      return this;
    }

    public MiningSyncScheduleBuilder dayOfWeek(MiningScheduleWeekDay weekDay) {
      this.weekDay = weekDay;
      return this;
    }

    public MiningSyncScheduleBuilder scheduleTimeValue(String scheduleTimeValue) {
      this.scheduleTimeValue = scheduleTimeValue;
      return this;
    }

    public MiningSyncScheduleBuilder activated(boolean activated) {
      this.activated = activated;
      return this;
    }

    public MiningSyncSchedule build() {
      return new MiningSyncSchedule(id, frequency, weekDay, scheduleTimeValue, activated);
    }

    public MiningSyncScheduleBuilder fromCdt(ProcessMiningSyncScheduleDto syncScheduleDto) {
      /* A ProcessMiningSyncScheduleDto class gets passed from SAIL to Java, with the id being null until the entity
       * has been created in the RDBMS. A null integer in the Dto will be represented as Integer.MIN_VALUE. */
      this.id = Long.valueOf(Integer.MIN_VALUE).equals(syncScheduleDto.getId()) ? null : syncScheduleDto.getId();
      this.frequency = MiningScheduleFrequencyType.valueOf(syncScheduleDto.getFrequency());
      this.weekDay = (syncScheduleDto.getDayOfWeek() == null || syncScheduleDto.getDayOfWeek().isEmpty()) ? null :
          MiningScheduleWeekDay.valueOf(syncScheduleDto.getDayOfWeek());
      this.scheduleTimeValue = syncScheduleDto.getScheduleTimeValue();
      this.activated = syncScheduleDto.isActivated();
      return this;
    }
  }
}
