package com.appiancorp.connectedenvironments.persistence;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.google.common.base.Objects;

@Entity
@Table(name = "connected_env")
public class ConnectedEnvironment implements Id<Long>, Comparable<ConnectedEnvironment> {
  private static final long serialVersionUID = 1L;
  public static final String PROP_URL = "url";

  private Long id;
  private String name;
  private String url;
  private boolean canSendDirectDeployment;
  private boolean enabled;
  private boolean remoteEnabled;
  private Long createdDate;
  private ActionType lastActionType;
  private String lastActionActorName;
  private String lastActionActorUsername;
  private Long lastActionDate;
  private String lastActionIp;
  private boolean deleted;

  ConnectedEnvironment(){
  }

  ConnectedEnvironment(ConnectedEnvironmentBuilder connectedEnvironmentBuilder){
    this.id = connectedEnvironmentBuilder.getId();
    this.name = connectedEnvironmentBuilder.getName();
    this.url = connectedEnvironmentBuilder.getUrl();
    this.canSendDirectDeployment = connectedEnvironmentBuilder.canSendDirectDeployment();
    this.enabled = connectedEnvironmentBuilder.isEnabled();
    this.remoteEnabled = connectedEnvironmentBuilder.isRemoteEnabled();
    this.createdDate = connectedEnvironmentBuilder.getCreatedDate();
    this.lastActionType = connectedEnvironmentBuilder.getLastActionType();
    this.lastActionActorName = connectedEnvironmentBuilder.getLastActionActorName();
    this.lastActionActorUsername = connectedEnvironmentBuilder.getLastActionActorUsername();
    this.lastActionDate = connectedEnvironmentBuilder.getLastActionDate();
    this.lastActionIp = connectedEnvironmentBuilder.getLastActionIp();
    this.deleted = connectedEnvironmentBuilder.deleted();
  }

  @Override
  public int compareTo(ConnectedEnvironment o) {
    if (this.name == null) {
      return o.name == null ? this.url.compareTo(o.url) : -1;
    } else if (o.name == null) {
      return 1;
    } else {
      final int comparatorInt = this.name.compareTo(o.name);
      return comparatorInt == 0 ? this.url.compareTo(o.url) : comparatorInt;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()){
      return false;
    }
    ConnectedEnvironment that = (ConnectedEnvironment)o;
    return canSendDirectDeployment == that.canSendDirectDeployment && enabled == that.enabled && remoteEnabled == that.remoteEnabled &&
        Objects.equal(id, that.id) &&
        Objects.equal(name, that.name) && Objects.equal(url, that.url) &&
        Objects.equal(createdDate, that.createdDate) && lastActionType == that.lastActionType &&
        Objects.equal(lastActionActorName, that.lastActionActorName) &&
        Objects.equal(lastActionActorUsername, that.lastActionActorUsername) &&
        Objects.equal(lastActionDate, that.lastActionDate) && Objects.equal(lastActionIp, that.lastActionIp);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, url, canSendDirectDeployment, enabled, remoteEnabled, createdDate, lastActionType,
        lastActionActorName, lastActionActorUsername, lastActionDate, lastActionIp);
  }

  public enum ActionType {
    APPROVED, DISABLED, ENABLED, DISABLED_REMOTE, ENABLED_REMOTE, DELETED, DELETED_REMOTE, BOOTSTRAPPED,
    DEPLOYMENT_ENABLED, DEPLOYMENT_DISABLED
  }

  @Override
  @javax.persistence.Id
  @Column(name = "id")
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "url", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "can_send_dir_deployment", nullable = false)
  public boolean isCanSendDirectDeployment() {
    return canSendDirectDeployment;
  }

  public void setCanSendDirectDeployment(boolean canSendDirectDeployment) {
    this.canSendDirectDeployment = canSendDirectDeployment;
  }

  @Column(name = "enabled", nullable = false)
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Column(name = "remote_enabled", nullable = false)
  public boolean isRemoteEnabled() {
    return remoteEnabled;
  }

  public void setRemoteEnabled(boolean remoteEnabled) {
    this.remoteEnabled = remoteEnabled;
  }

  @Column(name = "created_date", nullable = false)
  private Long getCreatedDateAsLong() {
    return createdDate;
  }

  @SuppressWarnings("unused")
  private void setCreatedDateAsLong(Long createdDateAsLong) {
    this.createdDate = createdDateAsLong;
  }

  @Transient
  public Date getCreatedDate() {
    return createdDate == null ? null : new Date(createdDate);
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate == null ? null : createdDate.getTime();
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "last_action_type", nullable = false)
  public ActionType getLastActionType() {
    return lastActionType;
  }

  public void setLastActionType(ActionType lastActionType) {
    this.lastActionType = lastActionType;
  }

  @Column(name = "last_action_actor_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getLastActionActorName() {
    return lastActionActorName;
  }

  public void setLastActionActorName(String lastActionActorName) {
    this.lastActionActorName = lastActionActorName;
  }

  @Column(name = "last_action_actor_username", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getLastActionActorUsername() {
    return lastActionActorUsername;
  }

  public void setLastActionActorUsername(String lastActionActorUsername) {
    this.lastActionActorUsername = lastActionActorUsername;
  }

  @Column(name = "last_action_date", nullable = false)
  private Long getLastActionDateAsLong() {
    return lastActionDate;
  }

  @SuppressWarnings("unused")
  private void setLastActionDateAsLong(Long lastActionDateAsLong) {
    this.lastActionDate = lastActionDateAsLong;
  }

  @Transient
  public Date getLastActionDate() {
    return lastActionDate == null ? null : new Date(lastActionDate);
  }

  public void setLastActionDate(Date lastActionDate) {
    this.lastActionDate = lastActionDate == null ? null : lastActionDate.getTime();
  }

  @Column(name = "last_action_ip", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getLastActionIp() {
    return lastActionIp;
  }

  public void setLastActionIp(String lastActionIp) {
    this.lastActionIp = lastActionIp;
  }

  @Column(name = "deleted", nullable = false)
  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
