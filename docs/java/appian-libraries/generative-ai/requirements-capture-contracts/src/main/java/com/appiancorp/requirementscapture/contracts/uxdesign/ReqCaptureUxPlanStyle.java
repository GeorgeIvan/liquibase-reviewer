package com.appiancorp.requirementscapture.contracts.uxdesign;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.requirementscapture.contracts.RequirementsCapture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "req_capture_ux_plan_style")
public class ReqCaptureUxPlanStyle implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private Long id;
  private RequirementsCapture requirementsCapture;
  private String styleValueJson;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "requirements_capture_id", nullable = false, referencedColumnName = "id", unique = true)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public RequirementsCapture getRequirementsCapture() {
    return requirementsCapture;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setRequirementsCapture(RequirementsCapture rc) {
    this.requirementsCapture = rc;
  }

  @Column(name = "style_value_json", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getStyleValueJson() {
    return styleValueJson;
  }

  public void setStyleValueJson(String styleValueJson) {
    this.styleValueJson = styleValueJson;
  }

  @Override
  public String toString() {
    return "ReqCaptureUxPlanStyle{" +
        "id=" + id +
        '\'' + '}';
  }
}
