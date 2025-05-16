package com.appiancorp.requirementscapture.contracts;

import com.appiancorp.requirementscapture.contracts.datamodel.ReqCaptureEntity;
import com.appiancorp.requirementscapture.contracts.persona.Persona;
import com.appiancorp.requirementscapture.contracts.uxdesign.ReqCaptureUxPlanStyle;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;
import static com.appiancorp.requirementscapture.contracts.datamodel.ReqCaptureEntity.PROP_REQUIREMENTS_CAPTURE;

@Entity
@Table(name = "requirements_capture")
public class RequirementsCapture implements Serializable {

  private Long id;

  private String applicationUuid;
  private String requirementsCaptureString;
  private Set<Persona> personas;
  private Set<ReqCaptureEntity> entities;
  private Status status;
  private Long reqCaptureLastModifiedTs;
  private Long dataModelLastModifiedTs;
  private ReqCaptureUxPlanStyle uxPlanStyle;

  public enum Status {
    NEW, INIT_FROM_TEMPLATE, EDITING
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

  @Column(name = "application_uuid", nullable = false, unique = true, length = COL_MAXLEN_INDEXABLE)
  public String getApplicationUuid() {
    return applicationUuid;
  }

  public void setApplicationUuid(String applicationUuid) {
    this.applicationUuid = applicationUuid;
  }

  @Lob
  @Column(name = "requirements_capture_string", nullable = false)
  public String getRequirementsCaptureString() {
    return requirementsCaptureString;
  }

  public void setRequirementsCaptureString(String requirementsCaptureString) {
    this.requirementsCaptureString = requirementsCaptureString;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = COL_MAXLEN_INDEXABLE)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Transient
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<Persona> getPersonas() {
    return personas;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setPersonas(Set<Persona> personas) {
    this.personas = personas;
  }

  @OneToMany(mappedBy = PROP_REQUIREMENTS_CAPTURE, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<ReqCaptureEntity> getEntities() {
    return entities;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setEntities(Set<ReqCaptureEntity> entities) {
    this.entities = entities;
  }

  @Column(name = "req_capture_last_modified_ts", nullable = false)
  public Long getReqCaptureLastModifiedTs() {
    return reqCaptureLastModifiedTs;
  }

  public void setReqCaptureLastModifiedTs(Long reqCaptureLastModifiedTs) {
    this.reqCaptureLastModifiedTs = reqCaptureLastModifiedTs;
  }

  @Column(name = "data_model_last_modified_ts", nullable = false)
  public Long getDataModelLastModifiedTs() {
    return dataModelLastModifiedTs;
  }

  public void setDataModelLastModifiedTs(Long dataModelLastModifiedTs) {
    this.dataModelLastModifiedTs = dataModelLastModifiedTs;
  }

  @OneToOne(mappedBy = "requirementsCapture", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ReqCaptureUxPlanStyle getUxPlanStyle() {
    return uxPlanStyle;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setUxPlanStyle(ReqCaptureUxPlanStyle uxPlanStyle) {
    this.uxPlanStyle = uxPlanStyle;
  }

  public RequirementsCapture copy() {
    RequirementsCapture copy = new RequirementsCapture();
    copy.setRequirementsCaptureString(this.getRequirementsCaptureString());
    copy.setStatus(this.getStatus());
    copy.setPersonas(new HashSet<>(this.getPersonas()));
    copy.setEntities(new HashSet<>(this.getEntities()));
    copy.setApplicationUuid(this.getApplicationUuid());
    copy.setReqCaptureLastModifiedTs(this.getReqCaptureLastModifiedTs());
    copy.setDataModelLastModifiedTs(this.getDataModelLastModifiedTs());
    return copy;
  }

  @Override
  public String toString() {
    return "RequirementsCapture{" + "requirementsCaptureString='" + requirementsCaptureString + '\'' +
        ", applicationUuid='" + applicationUuid + '\'' + ", id=" + id + '\'' +
        ", requirementsStringLastModifiedDate=" + reqCaptureLastModifiedTs + '\'' +
        ", dataModelLastModifiedDate=" + dataModelLastModifiedTs + '}';
  }
}
