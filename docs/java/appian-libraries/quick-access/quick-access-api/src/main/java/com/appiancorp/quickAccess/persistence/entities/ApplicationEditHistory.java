package com.appiancorp.quickAccess.persistence.entities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.suiteapi.type.Hidden;
import com.google.gson.Gson;

/**
 * An entity class to store an application edit history in RDBMS.
 */
@Hidden
@Entity
@Table(name = "app_edit_history")
public class ApplicationEditHistory {

  public static final String PROP_APP_UUID = "appUuid";
  private Long id;
  private String appUuid;
  private byte[] editEvents;

  public ApplicationEditHistory() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "app_uuid", nullable = false)
  public String getAppUuid() {
    return appUuid;
  }

  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  @Column(name = "edit_events", nullable = false)
  @Lob
  public byte[] getEditEvents() {
    return editEvents;
  }

  public void setEditEvents(byte[] editEvents) {
    this.editEvents = editEvents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationEditHistory that = (ApplicationEditHistory) o;
    return new EqualsBuilder()
        .append(appUuid, that.appUuid)
        .append(editEvents, that.editEvents)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return Objects.hash(appUuid, editEvents);
  }

  public void setEditEventsAsObject(ApplicationEditEventsWithVersion applicationEditEventsWithVersion) {
    this.editEvents = new Gson().toJson(applicationEditEventsWithVersion).getBytes(StandardCharsets.UTF_8);
  }

  public void setEditEventsAsObject(ArrayList<ApplicationEditEvent> editEvents) {
    ApplicationEditEventsWithVersion applicationEditEventsWithVersion = new ApplicationEditEventsWithVersion(
        ApplicationEditEventsWithVersion.VERSION_ONE, editEvents);
    setEditEventsAsObject(applicationEditEventsWithVersion);
  }

  @Transient
  public ArrayList<ApplicationEditEvent> getEditEventsAsList() {
    String recentEditAsString;
    recentEditAsString = new String(editEvents, StandardCharsets.UTF_8);
    return new Gson().fromJson(recentEditAsString, ApplicationEditEventsWithVersion.class).getEditEvents();
  }

}
