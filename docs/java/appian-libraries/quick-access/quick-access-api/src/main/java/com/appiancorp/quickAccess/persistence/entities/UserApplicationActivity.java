package com.appiancorp.quickAccess.persistence.entities;

import com.appiancorp.suiteapi.type.Hidden;
import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/**
 * An entity class to store user application activity in RDBMS.
 */
@Hidden
@Entity
@Table(name = "user_app_activity")
public class UserApplicationActivity {
  public static final Integer MAX_REMEMBERED_ACTIVITIES = 10;
  public static final String PROP_USER_UUID = "userUuid";

  private Long id;
  private String userUuid;
  private byte[] recentActivity;

 public UserApplicationActivity() {
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  @Column(name = "user_uuid", nullable = false)
  public String getUserUuid() {
    return userUuid;
  }

  @Lob
  @Column(name = "recent_activity", nullable = false)
  public byte[] getRecentActivity() {
    return recentActivity;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public void setRecentActivity(byte[] recentActivity) {
    this.recentActivity = recentActivity;
  }

  @Transient
  public ApplicationActivitiesWithVersion getRecentActivityAsObject() {
    String recentActivityString = new String(recentActivity, StandardCharsets.UTF_8);
    return new Gson().fromJson(recentActivityString, ApplicationActivitiesWithVersion.class);
  }

  public void setRecentActivityAsObject(ArrayList<ApplicationActivity> recentActivityAsObject) {
    ApplicationActivitiesWithVersion activitiesWithVersion = new ApplicationActivitiesWithVersion(
        recentActivityAsObject, ApplicationActivitiesWithVersion.VERSION_ONE);
    setRecentActivityAsObject(activitiesWithVersion);
  }

  public void setRecentActivityAsObject(ApplicationActivitiesWithVersion activitiesWithVersion) {
    this.recentActivity = new Gson().toJson(activitiesWithVersion).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserApplicationActivity that = (UserApplicationActivity) o;
    return new EqualsBuilder().append(userUuid, that.userUuid)
        .append(recentActivity, that.recentActivity)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return Objects.hash(userUuid, recentActivity);
  }
}
