package com.appiancorp.designdeployments.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appiancorp.ix.Haul;
import com.google.common.base.Objects;

/**
 * An entity class to store a plugin associated with a deployment in RDBMS.
 */
@Entity
@Table(name = "deployment_plugin")
public class DeploymentPlugin {
  private Long id;
  private Deployment deployment;
  private String name;
  private String version;
  private String targetVersion;
  private Haul.ImportChangeStatus changeStatus;
  private String key;
  private String jarFileName;

  DeploymentPlugin() {}

  public DeploymentPlugin(DeploymentPlugin deploymentPlugin) {
    this.id = null;
    this.name = deploymentPlugin.getName();
    this.version = deploymentPlugin.getVersion();
    this.deployment = deploymentPlugin.getDeployment();
    this.targetVersion = deploymentPlugin.getTargetVersion();
    this.changeStatus = deploymentPlugin.getChangeStatus();
    this.key = deploymentPlugin.getKey();
    this.jarFileName = deploymentPlugin.getJarFileName();
  }

  public DeploymentPlugin(DeploymentPluginBuilder builder) {
    this.name = builder.getName();
    this.version = builder.getVersion();
    this.deployment = builder.getDeployment();
    this.targetVersion = builder.getTargetVersion();
    this.changeStatus = builder.getChangeStatus();
    this.key = builder.getKey();
    this.jarFileName = builder.getJarFileName();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, insertable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_id", nullable = false)
  public Deployment getDeployment() {
    return deployment;
  }

  public void setDeployment(Deployment deployment) {
    this.deployment = deployment;
  }

  @Column(name = "name", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "version", nullable = false)
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Column(name = "target_version")
  public String getTargetVersion() {
    return targetVersion;
  }

  public void setTargetVersion(String targetVersion) {
    this.targetVersion = targetVersion;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "change_status", nullable = false)
  public Haul.ImportChangeStatus getChangeStatus() {
    return changeStatus;
  }

  public void setChangeStatus(Haul.ImportChangeStatus changeStatus) {
    this.changeStatus = changeStatus;
  }

  @Column(name = "plugin_key", nullable = false)
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Column(name = "jar_file_name", nullable = true)
  public String getJarFileName() {
    return jarFileName;
  }

  public void setJarFileName(String jarFileName) {
    this.jarFileName = jarFileName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentPlugin that = (DeploymentPlugin)o;
    return Objects.equal(deployment, that.deployment) && Objects.equal(name, that.name) &&
        Objects.equal(version, that.version) && Objects.equal(targetVersion, that.targetVersion) &&
        Objects.equal(changeStatus, that.changeStatus) && Objects.equal(key, that.key) &&
        Objects.equal(jarFileName, that.jarFileName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(deployment, name, version, targetVersion, changeStatus, key, jarFileName);
  }

  @Override
  public String toString() {
    return "DeploymentPlugin{" + "id=" + id + ", deployment=" + deployment + ", name='" + name + '\'' +
        ", version='" + version + '\'' + ", targetVersion='" + targetVersion + '\'' + ", changeStatus=" +
        changeStatus + ", key='" + key + '\'' + ", jarFileName='" + jarFileName + '\'' + '}';
  }

  public static class DeploymentPluginBuilder {
    private String name;
    private String version;
    private Deployment deployment;
    private String targetVersion;
    private Haul.ImportChangeStatus changeStatus;
    private String key;
    private String jarFileName;

    public DeploymentPluginBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public DeploymentPluginBuilder setVersion(String version) {
      this.version = version;
      return this;
    }

    public DeploymentPluginBuilder setDeployment(Deployment deployment) {
      this.deployment = deployment;
      return this;
    }

    public DeploymentPluginBuilder setTargetVersion(String targetVersion) {
      this.targetVersion = targetVersion;
      return this;
    }

    public DeploymentPluginBuilder setChangeStatus(Haul.ImportChangeStatus changeStatus) {
      this.changeStatus = changeStatus;
      return this;
    }

    public DeploymentPluginBuilder setKey(String key) {
      this.key = key;
      return this;
    }

    public DeploymentPluginBuilder setJarFileName(String jarFileName) {
      this.jarFileName = jarFileName;
      return this;
    }

    private String getName() {
      return name;
    }

    private String getVersion() {
      return version;
    }

    private Deployment getDeployment() {
      return deployment;
    }

    private String getTargetVersion() {
      return targetVersion;
    }

    private Haul.ImportChangeStatus getChangeStatus() {
      return changeStatus;
    }

    public String getKey() {
      return key;
    }

    public String getJarFileName() {
      return jarFileName;
    }

    public DeploymentPlugin build() {
      return new DeploymentPlugin(this);
    }
  }
}
