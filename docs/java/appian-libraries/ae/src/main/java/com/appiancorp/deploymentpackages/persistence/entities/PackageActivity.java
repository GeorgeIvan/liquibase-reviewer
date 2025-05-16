package com.appiancorp.deploymentpackages.persistence.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.assertions.Preconditions;
import com.appiancorp.deploymentpackages.persistence.service.PackageService;
import com.appiancorp.deploymentpackages.persistence.service.PackageActivityBuilder;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store the PackageActivity object in RDBMS.
 */
@Entity
@Table(name = "dpkg_activity")
public class PackageActivity {
  public static final String PROP_PACKAGE_ID = "packageId";

  private Long id;
  private Long packageId;
  private String userUuid;
  private Long activityTs;
  private PackageActivityAction action;
  private byte actionAsByte;
  private Set<PackageActivityObject> packageActivityObjects = new HashSet<>();

  /** for Hibernate to call during queries */
  PackageActivity() {}

  public PackageActivity(PackageActivityBuilder builder) {
    id = builder.getId();
    packageId = builder.getPackageId();
    userUuid = builder.getUserUuid();
    activityTs = builder.getActivityTs();
    action = builder.getAction();
    if (action != null) {
      actionAsByte = action.getAsByte();
    }
    Set<PackageActivityObject> newPackageActivityObjects = builder.getPackageActivityObjects();
    if (newPackageActivityObjects != null) {
      packageActivityObjects.addAll(newPackageActivityObjects);
    }
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "package_id", insertable = false, updatable = false, nullable = false)
  public Long getPackageId() {
    return packageId;
  }

  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  @Column(name = "user_uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  @Column(name = "activity_ts", updatable = false, nullable = false)
  public Long getActivityTs() {
    return activityTs;
  }

  public void setActivityTs(Long activityTs) {
    this.activityTs = activityTs;
  }

  @Transient
  public PackageActivityAction getAction() {
    return action;
  }

  public void setAction(PackageActivityAction packageActivityAction) {
    action = packageActivityAction;
    if (action != null) {
      actionAsByte = action.getAsByte();
    }
  }

  /**
   * DO NOT USE THIS METHOD. It exists to let Hibernate store our {@link PackageActivityAction} as an
   * integer, but everybody else should stick to using {@link #getAction()}.
   */
  @Column(name = "action", updatable = false, nullable = false)
  byte getActionAsByte() {
    return actionAsByte;
  }

  /**
   * DO NOT USE THIS METHOD. It exists to let Hibernate store our {@link PackageActivityAction} as an
   * integer, but everybody else should stick to using {@link #setAction(PackageActivityAction)}.
   */
  void setActionAsByte(byte actionAsByte) {
    this.actionAsByte = actionAsByte;
  }

  /**
   * DO NOT USE THIS METHOD. It exists to allow the JPA implementation to understand the schema, but in
   * general it will fail with LazyInitializationException. Instead you can call a service method such as
   * {@link PackageService#getPackageActivityObjects(Long)}.
   */
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "package_activity_id", nullable = false)
  @OrderBy
  Set<PackageActivityObject> getPackageActivityObjects() {
    return packageActivityObjects;
  }

  void setPackageActivityObjects(Set<PackageActivityObject> packageActivityObjects) {
    Preconditions.checkNotNull(packageActivityObjects, "Illegal null packageActivityObjects");
    this.packageActivityObjects = packageActivityObjects;
  }

  @PostLoad
  void postLoad() {
    action = PackageActivityAction.valueOf(actionAsByte);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("packageId", packageId)
        .add("userUuid", userUuid)
        .add("activityTs", new Date(activityTs))
        .add("action", action)
        .add("packageActivityObjects", Hibernate.isInitialized(packageActivityObjects)
            ? packageActivityObjects : "<uninitialized>")
        .toString();
  }
}
