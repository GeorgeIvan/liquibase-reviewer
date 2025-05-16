package com.appiancorp.designdeployments.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.core.persist.Constants;

/**
 * An entity class to store applications related to deployments in RDBMS
 */
@Entity
@Table(name="deployment_app")
public class DeploymentApp implements CommonDeploymentApp {

  public static final String APPLICATION_DEPLOYMENT_ID = "deploymentId";
  public static final String APPLICATION_ID = "id";
  public static final String APPLICATION_NAME = "appName";
  public static final String APPLICATION_UUID = "appUuid";

  private Long id;
  private String appUuid;
  private String appName;
  private Long deploymentId;
  private boolean targetApp;
  private Long processInstanceId;

  DeploymentApp() {}

  public DeploymentApp(DeploymentApp deploymentApp) {
    this.id = null;
    this.appUuid = deploymentApp.appUuid;
    this.appName = deploymentApp.appName;
    this.targetApp = deploymentApp.isTargetApplication();
  }

  private DeploymentApp(DeploymentAppBuilder builder) {
    this.id = null;
    this.appName = builder.getAppName();
    this.appUuid = builder.getAppUuid();
    this.targetApp = builder.isTargetApplication();
    this.processInstanceId = builder.getProcessInstanceId();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @Column(updatable = false, name = "app_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @Override
  public String getAppName() {
    return appName;
  }
  @Override
  public void setAppName(String appName) {
    this.appName = appName;
  }

  @Column(updatable = false, name = "app_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  @Override
  public String getAppUuid() {
    return appUuid;
  }
  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  @Column(updatable = false, name = "deployment_id", insertable = false)
  public Long getDeploymentId() {
    return deploymentId;
  }
  public void setDeploymentId(Long deploymentId) {
    this.deploymentId = deploymentId;
  }

  @Column(updatable = false, name = "is_target_app", nullable = false)
  public boolean isTargetApplication() {
    return targetApp;
  }
  public void setTargetApplication(boolean targetApp) {
    this.targetApp = targetApp;
  }

  @Column(name = "process_instance_id")
  public Long getProcessInstanceId() {
    return processInstanceId;
  }
  public void setProcessInstanceId(Long processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentApp that = (DeploymentApp)o;
    return Objects.equals(id, that.id) && Objects.equals(appUuid, that.appUuid) &&
        Objects.equals(appName, that.appName) && targetApp == that.targetApp &&
        Objects.equals(processInstanceId, that.processInstanceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, appUuid, appName, targetApp, processInstanceId);
  }

  @Override
  public String toString() {
    return "DeploymentApp{" + "id=" + id + ", appUuid='" + appUuid + '\'' + ", appName='" + appName +
        ", targetApp='" + targetApp + '\'' + ", processInstanceId=" + processInstanceId + '}';
  }

  /**
   * Creates a {@link Set} with a single {@link DeploymentApp} with the specified name and uuid.
   *
   * @param appName the application's name
   * @param appUuid the application's uuid
   * @return the {@link Set} with the single {@link DeploymentApp}
   */
  public static Set<DeploymentApp> buildDeploymentApp(final String appName, final String appUuid) {
    return buildDeploymentApp(appName, appUuid, false);
  }

  /**
   * Creates a {@link Set} with a single {@link DeploymentApp} with the specified name, uuid,
   * and whether it is a target application.
   *
   * @param appName   the application's name
   * @param appUuid   the application's uuid
   * @param targetApp <tt>true</tt> indicates a target application (an application includes all
   *                  imported objects) to (an application that will receive all imported objects)
   * @return the {@link Set} with the single {@link DeploymentApp}
   */
  public static Set<DeploymentApp> buildDeploymentApp(
      final String appName, final String appUuid, final boolean targetApp) {
    Set<DeploymentApp> deploymentApps = new HashSet<>();
    DeploymentApp newApp = new DeploymentApp.DeploymentAppBuilder().setAppName(appName)
        .setAppUuid(appUuid)
        .setTargetApplication(targetApp)
        .build();
    deploymentApps.add(newApp);

    return deploymentApps;
  }

  public static class DeploymentAppBuilder {
    private String appUuid;
    private String appName;
    private boolean targetApp = false;
    private Long processInstanceId;

    public DeploymentAppBuilder setAppUuid(String appUuid) {
      this.appUuid = appUuid;
      return this;
    }

    public DeploymentAppBuilder setAppName(String appName) {
      this.appName = appName;
      return this;
    }

    public DeploymentAppBuilder setTargetApplication(boolean targetApp) {
      this.targetApp = targetApp;
      return this;
    }

    public DeploymentAppBuilder setProcessInstanceId(Long processInstanceId) {
      this.processInstanceId = processInstanceId;
      return this;
    }

    private String getAppUuid() {
      return appUuid;
    }

    private String getAppName() {
      return appName;
    }

    private boolean isTargetApplication() {
      return targetApp;
    }

    public Long getProcessInstanceId() {
      return processInstanceId;
    }

    public DeploymentApp build() {
      return new DeploymentApp(this);
    }
  }
}
