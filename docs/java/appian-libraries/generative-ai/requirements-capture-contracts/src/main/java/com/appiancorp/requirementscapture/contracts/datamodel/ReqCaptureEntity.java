package com.appiancorp.requirementscapture.contracts.datamodel;

import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;
import static com.appian.core.persist.Constants.COL_MAXLEN_UUID;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.appiancorp.requirementscapture.contracts.RequirementsCapture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "req_capture_entity")
public class ReqCaptureEntity implements Serializable {
  public static final String PROP_REQUIREMENTS_CAPTURE = "requirementsCapture";
  public static final String PROP_NAME = "name";
  public static final String PROP_UUID = "uuid";

  private Long id;
  private String uuid;
  private RequirementsCapture requirementsCapture;
  private String name;
  private Set<ReqCaptureEntityAttribute> attributes;
  private Set<ReqCaptureEntityDesignObjectRef> designObjectRefs = new HashSet<>();

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @ManyToOne
  @JoinColumn(name = "requirements_capture_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public RequirementsCapture getRequirementsCapture() {
    return requirementsCapture;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setRequirementsCapture(RequirementsCapture requirementsCapture) {
    this.requirementsCapture = requirementsCapture;
  }

  @Column(name = "name", nullable = false, length = COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "req_capture_entity_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<ReqCaptureEntityAttribute> getAttributes() {
    return attributes;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setAttributes(Set<ReqCaptureEntityAttribute> attributes) {
    this.attributes = attributes;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "req_capture_entity_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<ReqCaptureEntityDesignObjectRef> getDesignObjectRefs() {
    return designObjectRefs;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDesignObjectRefs(Set<ReqCaptureEntityDesignObjectRef> designObjectRefs) {
    this.designObjectRefs = designObjectRefs;
  }

  @Override
  public String toString() {
    return "ReqCaptureEntity{" + "name='" + name + '\'' + ", requirementsCapture=" + requirementsCapture +
        ", uuid='" + uuid + '\'' + ", id=" + id + '}';
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }
}
