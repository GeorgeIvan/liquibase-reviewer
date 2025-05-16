package com.appiancorp.requirementscapture.contracts.uxdesign;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.requirementscapture.contracts.RequirementsCapture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "req_capture_ux_plan_screen")
public class ReqCaptureUxPlanScreen implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private transient Long id;
  private String uuid;
  private RequirementsCapture requirementsCapture;
  private String screenValueJson;
  private List<UxPlanScreenDesignObjRef> designObjectRefs;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @ManyToOne(fetch =  FetchType.EAGER)
  @JoinColumn(name = "requirements_capture_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public RequirementsCapture getRequirementsCapture() {
    return requirementsCapture;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setRequirementsCapture(RequirementsCapture requirementsCapture) {
    this.requirementsCapture = requirementsCapture;
  }

  @Lob
  @Column(name = "screen_value_json", nullable = false, length=Constants.COL_MAXLEN_EXPRESSION)
  public String getScreenValueJson() {
    return screenValueJson;
  }

  public void setScreenValueJson(String screenValueJson) {
    this.screenValueJson = screenValueJson;
  }


  @SuppressFBWarnings("EI_EXPOSE_REP")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "req_capture_ux_plan_screen_id", nullable = false)
  public List<UxPlanScreenDesignObjRef> getDesignObjectRefs() {
    return designObjectRefs;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDesignObjectRefs(List<UxPlanScreenDesignObjRef> designObjectRefs) {
    this.designObjectRefs = designObjectRefs;
  }

  public ReqCaptureUxPlanScreen addUxScreenDesignObjectRef(String designObjectUuid, String designObjectQName) {
    if (this.designObjectRefs == null) {
      this.designObjectRefs = new ArrayList<>();
    }
    this.designObjectRefs.add(new UxPlanScreenDesignObjRef().designObjectUuid(designObjectUuid).designObjectQName(designObjectQName));
    return this;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    ReqCaptureUxPlanScreen uxPlanScreen = (ReqCaptureUxPlanScreen) o;
    return Objects.equals(uuid, uxPlanScreen.uuid) && Objects.equals(screenValueJson, uxPlanScreen.screenValueJson)
        && Objects.equals(designObjectRefs, uxPlanScreen.designObjectRefs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, screenValueJson, designObjectRefs);
  }

  @Override
  public String toString() {
    return "ReqCaptureUxPlanScreen{" +
        "id=" + id +
        ", uuid=" + uuid +
        '}';
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }
}
