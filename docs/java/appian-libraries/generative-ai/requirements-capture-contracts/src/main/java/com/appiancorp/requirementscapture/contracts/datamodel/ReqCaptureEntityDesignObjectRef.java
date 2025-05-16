package com.appiancorp.requirementscapture.contracts.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "rc_entity_design_object_ref", uniqueConstraints = @UniqueConstraint(columnNames = {
    "design_object_uuid", "req_capture_entity_id"}))
public class ReqCaptureEntityDesignObjectRef implements Serializable {

  private Long id;
  private String designObjectUuid;
  private String designObjectTypeQname;

  public ReqCaptureEntityDesignObjectRef() {}

  public ReqCaptureEntityDesignObjectRef(ReqCaptureEntityDesignObjectRefDto dto) {
    this.designObjectUuid = dto.designObjectUuid();
    this.designObjectTypeQname = dto.designObjectTypeQname();
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "design_object_uuid", nullable = false, length = 255)
  public String getDesignObjectUuid() {
    return designObjectUuid;
  }

  public void setDesignObjectUuid(String designObjectUuid) {
    this.designObjectUuid = designObjectUuid;
  }

  @Column(name = "design_object_type_qname", nullable = false, length = 255)
  public String getDesignObjectTypeQname() {
    return designObjectTypeQname;
  }

  public void setDesignObjectTypeQname(String designObjectTypeQname) {
    this.designObjectTypeQname = designObjectTypeQname;
  }

  @Override
  public String toString() {
    return "ReqCaptureEntityDesignObjectRef{" + "id=" + id + ", designObjectUuid=" + designObjectUuid +
        ", designObjectTypeQname=" + designObjectTypeQname + '}';
  }
}
