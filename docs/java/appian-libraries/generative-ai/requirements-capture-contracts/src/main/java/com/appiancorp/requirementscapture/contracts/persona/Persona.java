package com.appiancorp.requirementscapture.contracts.persona;

import com.appian.core.persist.Constants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "req_capture_persona")
public class Persona implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private transient Long id;
  private String uuid;
  private transient Long requirementsCaptureId;
  private String name;
  private String description;
  private List<PersonaDesignObjectRef> designObjectRefs = new ArrayList<>();

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Persona id(Long id) {
    this.id = id;
    return this;
  }

  @Column(name = "uuid", updatable = false, nullable = false, unique = true)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Persona uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Column(name="requirements_capture_id", updatable=false, nullable=false)
  public Long getRequirementsCaptureId() {
    return requirementsCaptureId;
  }

  public void setRequirementsCaptureId(Long requirementsCaptureId) {
    this.requirementsCaptureId = requirementsCaptureId;
  }

  public Persona requirementsCaptureId(Long requirementsCaptureId) {
    this.requirementsCaptureId = requirementsCaptureId;
    return this;
  }

  @Column(name = "persona", nullable=false, length=Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Persona name(String name) {
    this.name = name;
    return this;
  }

  @Column(name = "description", nullable=true, length=Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Persona description(String description) {
    this.description = description;
    return this;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "req_capture_persona_id", nullable = false)
  public List<PersonaDesignObjectRef> getDesignObjectRefs() {
    return designObjectRefs;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setDesignObjectRefs(List<PersonaDesignObjectRef> designObjectRefs) {
    this.designObjectRefs = designObjectRefs;
  }

  public Persona addDesignObjectRef(String designObjectUuid, String designObjectQName) {
    if (this.designObjectRefs == null) {
      this.designObjectRefs = new ArrayList<>();
    }
    this.designObjectRefs.add(new PersonaDesignObjectRef().designObjectUuid(designObjectUuid).designObjectQName(designObjectQName));
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
    Persona persona = (Persona) o;
    return Objects.equals(uuid, persona.uuid) && Objects.equals(name, persona.name) &&
        Objects.equals(description, persona.description) && Objects.equals(designObjectRefs, persona.designObjectRefs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, description, designObjectRefs);
  }

  @Override
  public String toString() {
    return "Persona{" +
            "id=" + id +
            ", uuid=" + uuid +
            ", requirementsCaptureId=" + requirementsCaptureId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", designObjectRefs='" + designObjectRefs + '\'' +
            '}';
  }
}
