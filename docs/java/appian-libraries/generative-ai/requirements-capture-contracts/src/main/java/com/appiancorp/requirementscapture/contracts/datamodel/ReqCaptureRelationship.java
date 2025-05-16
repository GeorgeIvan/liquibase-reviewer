package com.appiancorp.requirementscapture.contracts.datamodel;

import static com.appian.core.persist.Constants.COL_MAXLEN_UUID;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "req_cap_entity_relationship")
public class ReqCaptureRelationship implements Serializable {
  public static final String PROP_SOURCE_ATTRIBUTE = "sourceAttribute";
  public static final String PROP_TARGET_ATTRIBUTE = "targetAttribute";

  private Long id;
  private String uuid;
  private ReqCaptureEntityAttribute sourceAttribute;
  private ReqCaptureEntityAttribute targetAttribute;
  private ReqCaptureRelationshipType type;

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

  @Transient
  public ReqCaptureRelationshipType getType() {
    return type;
  }

  public void setType(ReqCaptureRelationshipType type) {
    this.type = type;
  }

  @ManyToOne
  @JoinColumn(name = "source_attribute_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ReqCaptureEntityAttribute getSourceAttribute() {
    return sourceAttribute;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setSourceAttribute(ReqCaptureEntityAttribute sourceAttribute) {
    this.sourceAttribute = sourceAttribute;
  }

  @ManyToOne
  @JoinColumn(name = "target_attribute_id", nullable = false)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ReqCaptureEntityAttribute getTargetAttribute() {
    return targetAttribute;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setTargetAttribute(ReqCaptureEntityAttribute targetAttribute) {
    this.targetAttribute = targetAttribute;
  }

  @Override
  public String toString() {
    return "ReqCaptureRelationship{" + "id=" + id + ", uuid='" + uuid + '\'' + ", sourceAttribute=" +
        sourceAttribute + ", targetAttribute=" + targetAttribute + ", type=" + type + '}';
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  /* This method will be used by hibernate */
  @Column(name = "relationship_type", nullable = false)
  private byte getTypeByte() {
    return type != null ? type.getCode() : ReqCaptureRelationshipType.ONE_TO_MANY.getCode();
  }

  /* This method will be used by hibernate */
  private void setTypeByte(byte type) {
    setType(ReqCaptureRelationshipType.valueOf(type));
  }
}
