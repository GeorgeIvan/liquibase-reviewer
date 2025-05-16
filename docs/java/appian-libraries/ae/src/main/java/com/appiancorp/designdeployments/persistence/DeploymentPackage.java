package com.appiancorp.designdeployments.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.google.common.base.Objects;

/**
 * Entity class to store a shared/collaborative package that is associated with a specific deployment. A user
 * can deploy 0 or more shared packages in Appian Designer, which results in a deployment that contains 0 or
 * more instances of DeploymentPackage.
 */
@Entity
@Table(name = "deployment_dpkg")
@XmlType(propOrder = {"url"})
public class DeploymentPackage {
  private Long id;
  private transient Deployment deployment;
  private String url;
  private String appUuid;

  DeploymentPackage() {
  }

  public DeploymentPackage(DeploymentPackageBuilder builder) {
    this.deployment = builder.getDeployment();
    this.url = builder.getUrl();
    this.appUuid = builder.getAppUuid();
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, insertable = false, nullable = false)
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_id", nullable = false)
  @XmlTransient
  public Deployment getDeployment() {
    return deployment;
  }

  public void setDeployment(Deployment deployment) {
    this.deployment = deployment;
  }

  @Column(name = "proj_mgmt_url", nullable = true, length = 255)
  @XmlElement
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "app_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  @XmlTransient
  public String getAppUuid() {
    return appUuid;
  }

  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentPackage that = (DeploymentPackage)o;
    return Objects.equal(deployment, that.deployment) && Objects.equal(url, that.url) &&
        Objects.equal(appUuid, that.appUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(deployment, url);
  }

  @Override
  public String toString() {
    return "DeploymentPackage{id=" + id + ", appUuid=" + appUuid + ", deployment=" + deployment + ", url='"
        + url + '\'' + '}';
  }

  public static class DeploymentPackageBuilder {
    private Deployment deployment;
    private String url;
    private String appUuid;

    public DeploymentPackageBuilder setDeployment(Deployment deployment) {
      this.deployment = deployment;
      return this;
    }

    public DeploymentPackageBuilder setUrl(String url) {
      this.url = url;
      return this;
    }

    public DeploymentPackageBuilder setAppUuid(String appUuid) {
      this.appUuid = appUuid;
      return this;
    }

    private Deployment getDeployment() {
      return deployment;
    }

    private String getUrl() {
      return url;
    }

    private String getAppUuid() {
      return appUuid;
    }

    public DeploymentPackage build() {
      return new DeploymentPackage(this);
    }
  }
}
