package com.appiancorp.deploymentpackages.persistence.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.security.user.User;
import com.appiancorp.type.refs.UserRef;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = "dpkg_usr_settings")
public class UserPackageSettings {
  private Long id;
  private User user;
  private String appUuid;
  private Package pkg;

  public UserPackageSettings() {
  }

  public UserPackageSettings(UserRef userRef, Package pkg) {
    this.user = new User(userRef);
    this.appUuid = pkg.getAppUuid();
    this.pkg = pkg;
  }

  @javax.persistence.Id
  @Column(name = "id", updatable = false)
  @GeneratedValue
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "usr_id", nullable = false)
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Column(name = "app_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getAppUuid() {
    return appUuid;
  }

  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "package_id", nullable = false)
  public Package getPackage() {
    return pkg;
  }

  public void setPackage(Package pkg) {
    this.pkg = pkg;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("usrId", user.getRdbmsId())
        .add("appUuid", appUuid)
        .add("pkg", pkg.getUuid())
        .toString();
  }
}
