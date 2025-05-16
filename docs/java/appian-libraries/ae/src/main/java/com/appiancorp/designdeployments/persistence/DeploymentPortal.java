package com.appiancorp.designdeployments.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.google.common.base.Objects;

/**
 * An entity class to store Deployment Portal object in RDBMS.
 */
@Entity
@Table(name="deployment_portal")
public class DeploymentPortal {

  private Long id;
  private Deployment deployment;
  private String portalUuid;
  private String fullUrl;
  private String displayName;
  private DeploymentPortalStatus deploymentPortalStatus;

  DeploymentPortal() {}


  public DeploymentPortal(DeploymentPortalBuilder deploymentPortalBuilder) {
    this.portalUuid = deploymentPortalBuilder.getPortalUuid();
    this.displayName = deploymentPortalBuilder.getDisplayName();
    this.fullUrl = deploymentPortalBuilder.getFullUrl();
    this.deployment = deploymentPortalBuilder.getDeployment();
    this.deploymentPortalStatus = deploymentPortalBuilder.getPortalDeploymentStatus();
    this.fullUrl = deploymentPortalBuilder.getFullUrl();
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_id", nullable = false)
  public Deployment getDeployment() {
    return deployment;
  }

  public void setDeployment(Deployment deployment) {
    this.deployment = deployment;
  }

  @Column(name = "portal_uuid", nullable = false)
  public String getPortalUuid() {
    return portalUuid;
  }

  public void setPortalUuid(String portalUuid) {
    this.portalUuid = portalUuid;
  }

  @Column(name = "full_url", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getFullUrl() {
    return fullUrl;
  }

  public void setFullUrl(String fullUrl) {
    this.fullUrl = fullUrl;
  }

  @Column(name = "display_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Transient
  public DeploymentPortalStatus getDeploymentPortalStatus() {
    return deploymentPortalStatus;
  }

  public void setDeploymentPortalStatus(DeploymentPortalStatus deploymentPortalStatus) {
    this.deploymentPortalStatus = deploymentPortalStatus;
  }

  /* These methods will be used for hibernate. */
  @Column(name = "portal_deployment_status", nullable = false)
  public byte getPortalDeploymentStatusByte() {
    /* A null check is added to avoid erroring out when Hibernate calls this method for its internal
     initialization processes */
    return deploymentPortalStatus != null ? deploymentPortalStatus.getIndex() : -1;
  }

  public void setPortalDeploymentStatusByte(byte index) {
    setDeploymentPortalStatus(DeploymentPortalStatus.valueOf(index));
  }

  @Transient
  public boolean isErrored() {
    return deploymentPortalStatus == DeploymentPortalStatus.PUBLISH_ERRORED ||
        deploymentPortalStatus == DeploymentPortalStatus.UNPUBLISH_ERRORED ||
        deploymentPortalStatus == DeploymentPortalStatus.PUBLISHED_FAIL_WHALE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentPortal that = (DeploymentPortal)o;
    return Objects.equal(id, that.id) && Objects.equal(deployment, that.deployment) &&
        Objects.equal(portalUuid, that.portalUuid) &&
        Objects.equal(deploymentPortalStatus, that.deploymentPortalStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, deployment, portalUuid, deploymentPortalStatus);
  }

  @Override
  public String toString() {
    return "DeploymentPortal{" + "id=" + id + ", portal_uuid=" + portalUuid
        + ", deploymentStatus=" + deploymentPortalStatus + "}";
  }

  public static class DeploymentPortalBuilder {
    private Deployment deployment;
    private String portalUuid;
    private String displayName;
    private String fullUrl;
    private DeploymentPortalStatus portalDeploymentStatus;

    public DeploymentPortalBuilder setDeployment(Deployment deployment) {
      this.deployment = deployment;
      return this;
    }

    public DeploymentPortalBuilder setPortalUuid(String portalUuid) {
      this.portalUuid = portalUuid;
      return this;
    }

    public DeploymentPortalBuilder setDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public DeploymentPortalBuilder setFullUrl(String fullUrl) {
      this.fullUrl = fullUrl;
      return this;
    }

    public DeploymentPortalBuilder setPortalDeploymentStatus(DeploymentPortalStatus portalDeploymentStatus) {
      this.portalDeploymentStatus = portalDeploymentStatus;
      return this;
    }

    private String getPortalUuid() {
      return portalUuid;
    }

    private Deployment getDeployment() {
      return deployment;
    }

    private String getDisplayName() {
      return displayName;
    }

    private String getFullUrl() {
      return fullUrl;
    }

    private DeploymentPortalStatus getPortalDeploymentStatus() {
      return portalDeploymentStatus;
    }

    public DeploymentPortal build() {
      return new DeploymentPortal(this);
    }
  }
}
