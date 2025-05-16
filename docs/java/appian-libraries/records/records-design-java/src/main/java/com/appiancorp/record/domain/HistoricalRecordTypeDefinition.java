package com.appiancorp.record.domain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.common.io.Compression;
import com.appiancorp.common.io.CompressionExceptionProvider;
import com.appiancorp.common.io.CompressionExceptionProviderImpl;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.suiteapi.type.Hidden;

@Hidden
@Entity
@Table(name = "record_type_history")
public class HistoricalRecordTypeDefinition implements ReadOnlyHistoricalRecordTypeDefinition {

  public static final String PROP_RECORD_TYPE_ID = "recordTypeId";
  public static final String PROP_ID = "id";
  public static final String PROP_UUID = "uuid";
  private static CompressionExceptionProvider compressionExceptionProvider = new CompressionExceptionProviderImpl();

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private Long recordTypeId;
  private byte[] serializedRecordTypeDefinition;
  private AuditInfo auditInfo;

  // Setters/Getters directly backed by DB columns ===========================================================

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", updatable = false, nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "record_type_id", nullable = false)
  public Long getRecordTypeId() {
    return recordTypeId;
  }

  public void setRecordTypeId(Long recordTypeId) {
    this.recordTypeId = recordTypeId;
  }

  @Lob
  @Column(name = "record_type_definition")
  private byte[] getSerializedRecordTypeDefinition() {
    return serializedRecordTypeDefinition;
  }

  private void setSerializedRecordTypeDefinition(byte[] serializedRecordTypeDefinition) {
    this.serializedRecordTypeDefinition = serializedRecordTypeDefinition;
  }

  @Transient
  public String getRecordTypeDefinitionAsXml() throws IOException {
    return new String(Compression.decompressWithLimit(serializedRecordTypeDefinition), StandardCharsets.UTF_8);
  }

  public void setRecordTypeDefinitionAsXml(String recordTypeDefinitionAsXml) throws IOException {
    this.serializedRecordTypeDefinition = Compression.compressWithLimit(recordTypeDefinitionAsXml.getBytes(
        StandardCharsets.UTF_8), compressionExceptionProvider);
  }

  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(final AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }
}
