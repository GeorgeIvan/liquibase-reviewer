package com.appiancorp.requirementscapture.contracts.datamodel;

import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;
import static com.appian.core.persist.Constants.COL_MAXLEN_UUID;
import static com.appiancorp.requirementscapture.contracts.datamodel.ReqCaptureRelationship.PROP_SOURCE_ATTRIBUTE;
import static com.appiancorp.requirementscapture.contracts.datamodel.ReqCaptureRelationship.PROP_TARGET_ATTRIBUTE;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "req_cap_entity_attribute")
public class ReqCaptureEntityAttribute implements Serializable {
  public static final String PROP_UUID = "uuid";

  private Long id;
  private String uuid;
  private String name;
  private ReqCaptureEntityAttributeType type;
  private boolean isPrimaryKey;
  private boolean isUnique;
  private Set<ReqCaptureRelationship> outboundRelationships;
  private Set<ReqCaptureRelationship> inboundRelationships;
  private int sortOrder;

  public ReqCaptureEntityAttribute() {}

  public ReqCaptureEntityAttribute(ReqCaptureEntityAttributeDto dto) {
    this.uuid = dto.uuid() == null ? null : dto.uuid().toString();
    this.name = dto.name();
    this.type = dto.type();
    this.isPrimaryKey = dto.isPrimaryKey();
    this.isUnique = dto.isUnique();
    this.outboundRelationships = new HashSet<>();
    this.inboundRelationships = new HashSet<>();
    this.sortOrder = dto.sortOrder();
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

  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Transient
  public ReqCaptureEntityAttributeType getType() {
    return type;
  }

  public void setType(ReqCaptureEntityAttributeType type) {
    this.type = type;
  }

  @Column(name = "is_primary_key", nullable = false)
  public boolean isPrimaryKey() {
    return isPrimaryKey;
  }

  public void setPrimaryKey(boolean isPrimaryKey) {
    this.isPrimaryKey = isPrimaryKey;
  }

  @Column(name = "is_unique", nullable = false)
  public boolean isUnique() {
    return isUnique;
  }

  public void setUnique(boolean isUnique) {
    this.isUnique = isUnique;
  }

  @OneToMany(mappedBy = PROP_SOURCE_ATTRIBUTE, fetch = FetchType.LAZY, cascade = CascadeType.ALL,
      orphanRemoval = true)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<ReqCaptureRelationship> getOutboundRelationships() {
    return outboundRelationships;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setOutboundRelationships(Set<ReqCaptureRelationship> outboundRelationships) {
    this.outboundRelationships = outboundRelationships;
  }

  @OneToMany(mappedBy = PROP_TARGET_ATTRIBUTE, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public Set<ReqCaptureRelationship> getInboundRelationships() {
    return inboundRelationships;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public void setInboundRelationships(Set<ReqCaptureRelationship> inboundRelationships) {
    this.inboundRelationships = inboundRelationships;
  }

  @Column(name = "sort_order", nullable = false)
  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public String toString() {
    return "ReqCaptureEntityAttribute{" + "isUnique=" + isUnique + ", isPrimaryKey=" + isPrimaryKey +
        ", type=" + type + ", name='" + name + '\'' + ", uuid='" + uuid + '\'' + ", id=" + id + '\'' +
        ", sortOrder=" + sortOrder + '}';
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  /* This method will be used by hibernate */
  @Column(name = "attribute_type", nullable = false)
  private byte getTypeByte() {
    return type != null ? type.getCode() : ReqCaptureEntityAttributeType.TEXT.getCode();
  }

  /* This method will be used by hibernate */
  private void setTypeByte(byte type) {
    setType(ReqCaptureEntityAttributeType.valueOf(type));
  }
}
