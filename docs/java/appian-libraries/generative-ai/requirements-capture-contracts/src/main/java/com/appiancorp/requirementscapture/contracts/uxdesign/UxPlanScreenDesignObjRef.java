package com.appiancorp.requirementscapture.contracts.uxdesign;

import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name="rc_screen_design_obj_ref",
    uniqueConstraints = @UniqueConstraint(columnNames =  {"design_object_uuid", "req_capture_ux_plan_screen_id"})
)
public class UxPlanScreenDesignObjRef implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private transient Long id;
  private String designObjectUuid;
  private String designObjectQName;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "design_object_uuid", nullable = false, length = COL_MAXLEN_INDEXABLE)
  public String getDesignObjectUuid() {
    return designObjectUuid;
  }

  public void setDesignObjectUuid(String designObjectUuid) {
    this.designObjectUuid = designObjectUuid;
  }

  public UxPlanScreenDesignObjRef designObjectUuid(String designObjectUuid) {
    this.designObjectUuid = designObjectUuid;
    return this;
  }

  @Column(name = "design_object_qname", nullable = false, length = COL_MAXLEN_INDEXABLE)
  public String getDesignObjectQName() {
    return designObjectQName;
  }

  public void setDesignObjectQName(String designObjectQName) {
    this.designObjectQName = designObjectQName;
  }

  public UxPlanScreenDesignObjRef designObjectQName(String designObjectQName) {
    this.designObjectQName = designObjectQName;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UxPlanScreenDesignObjRef that = (UxPlanScreenDesignObjRef)o;
    return Objects.equals(designObjectUuid, that.designObjectUuid) &&
        Objects.equals(designObjectQName, that.designObjectQName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(designObjectUuid, designObjectQName);
  }

  @Override
  public String toString() {
    return "UxPlanScreenDesignObjRef{" + "id=" + id + ", designObjectUuid='" + designObjectUuid + '\'' +
        ", designObjectQName='" + designObjectQName + '\'' + '}';
  }
}
