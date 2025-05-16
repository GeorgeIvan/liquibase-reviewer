package com.appiancorp.designdeployments.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.google.common.base.Objects;

/**
 * An entity class to store Deployment Database Description Language(DDL) object in RDBMS.
 */
@Entity
@Table(name="deployment_db_script")
public class DeploymentDbScript {

  private Long id;
  private Long orderId;
  private Long documentId;
  private String dataSourceUuid;

  DeploymentDbScript() {}

  private DeploymentDbScript(DeploymentDbScriptBuilder builder) {
    this.id = builder.getId();
    this.orderId = builder.getOrderId();
    this.documentId = builder.getDocumentId();
    this.dataSourceUuid = builder.getDataSourceUuid();
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

  @Column(name = "order_id", nullable = false)
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "data_source_uuid", updatable = false, nullable = false)
  public String getDataSourceUuid() {
    return dataSourceUuid;
  }

  public void setDataSourceUuid(String dataSourceUuid) {
    this.dataSourceUuid = dataSourceUuid;
  }

  @Column(name = "document_id", nullable = false)
  public Long getDocumentId() {
    return documentId;
  }

  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentDbScript that = (DeploymentDbScript)o;
    return Objects.equal(orderId, that.orderId) && Objects.equal(dataSourceUuid, that.dataSourceUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(orderId, dataSourceUuid);
  }

  @Override
  public String toString() {
    return "DeploymentDbScript{" + "id=" + id + ", orderId=" + orderId + ", dataSourceUuid='" +
        dataSourceUuid + "', documentId=" + documentId + '}';
  }

  public static class DeploymentDbScriptBuilder {
    private Long id;
    private Long orderId;
    private Long documentId;
    private String dataSourceUuid;

    public DeploymentDbScriptBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public DeploymentDbScriptBuilder setOrderId(Long orderId) {
      this.orderId = orderId;
      return this;
    }

    public DeploymentDbScriptBuilder setDataSourceUuid(String dataSourceUuid) {
      this.dataSourceUuid = dataSourceUuid;
      return this;
    }

    public DeploymentDbScriptBuilder setDocumentId(Long documentId) {
      this.documentId = documentId;
      return this;
    }

    public Long getId() {
      return id;
    }

    public Long getOrderId() {
      return orderId;
    }

    public String getDataSourceUuid() {
      return dataSourceUuid;
    }

    public Long getDocumentId() {
      return documentId;
    }

    public DeploymentDbScript build() {
      return new DeploymentDbScript(this);
    }
  }
}
