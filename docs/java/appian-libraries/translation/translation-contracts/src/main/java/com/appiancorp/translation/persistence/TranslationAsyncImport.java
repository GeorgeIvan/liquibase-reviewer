package com.appiancorp.translation.persistence;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appian.core.persist.Constants;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.translation.enums.TranslationAsyncImportStatus;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An entity class to store translation async import in RDBMS. It holds the status of import file.
 */
@Entity
@Table(name = "ts_async_import")
public class TranslationAsyncImport implements HasAuditInfo {

  private Long id;
  private String uuid;
  private Long translationSetId;
  private String importFileName;
  private TranslationAsyncImportStatus translationAsyncImportStatus;
  private boolean userDismissed;
  private String errorMessage;

  private AuditInfo auditInfo = new AuditInfo();

  public static final String TRANS_SET_ID = "ts_set_id";

  public TranslationAsyncImport() {
  }

  public TranslationAsyncImport(
      Long translationSetId,
      String importFileName,
      TranslationAsyncImportStatus translationAsyncImportStatus,
      boolean userDismissed) {
    this.translationSetId = translationSetId;
    this.importFileName = importFileName;
    this.translationAsyncImportStatus = translationAsyncImportStatus;
    this.userDismissed = userDismissed;
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

  @Column(name = "uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = TRANS_SET_ID, nullable = false)
  public Long getTranslationSetId() {
    return translationSetId;
  }

  public void setTranslationSetId(Long translationSetId) {
    this.translationSetId = translationSetId;
  }

  @Column(name = "import_file_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getImportFileName() {
    return importFileName;
  }

  public void setImportFileName(String importFileName) {
    this.importFileName = importFileName;
  }

  @Transient
  public TranslationAsyncImportStatus getTranslationAsyncImportStatus() {
    return translationAsyncImportStatus;
  }

  public void setTranslationAsyncImportStatus(TranslationAsyncImportStatus translationAsyncImportStatus) {
    this.translationAsyncImportStatus = translationAsyncImportStatus;
  }

  /* These methods will be used for hibernate. */
  @Column(name = "status", nullable = false)
  public byte getTranslationAsyncImportStatusByte() {
    /* A null check is added to avoid erroring out when Hibernate calls this method for its internal
     initialization processes */
    return translationAsyncImportStatus != null ? translationAsyncImportStatus.getIndex() : -1;
  }

  public void setTranslationAsyncImportStatusByte(byte index) {
    setTranslationAsyncImportStatus(TranslationAsyncImportStatus.valueOf(index));
  }

  @Column(name = "user_dismissed", nullable = false)
  public boolean isUserDismissed() {
    return userDismissed;
  }

  public void setUserDismissed(boolean userDismissed) {
    this.userDismissed = userDismissed;
  }

  @Column(name = "error_message", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  public boolean equivalentTo(final TranslationAsyncImport translationAsyncImport) {
    return EQUIVALENCE.equivalent(this, translationAsyncImport);
  }

  @SuppressWarnings({"checkstyle:anoninnerlength"})
  private static Equivalence<TranslationAsyncImport> EQUIVALENCE = new Equivalence<TranslationAsyncImport>() {
    @Override
    protected boolean doEquivalent(TranslationAsyncImport lhs, TranslationAsyncImport rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return new EqualsBuilder().append(lhs.translationSetId, rhs.translationSetId)
          .append(lhs.uuid, rhs.uuid)
          .append(lhs.importFileName, rhs.importFileName)
          .append(lhs.translationAsyncImportStatus, rhs.translationAsyncImportStatus)
          .append(lhs.userDismissed, rhs.userDismissed)
          .append(lhs.auditInfo, rhs.auditInfo)
          .append(lhs.errorMessage, rhs.errorMessage)
          .isEquals();
    }

    @Override
    protected int doHash(TranslationAsyncImport translationAsyncImport) {
      return Objects.hashCode(translationAsyncImport.translationSetId, translationAsyncImport.uuid,
          translationAsyncImport.translationAsyncImportStatus, translationAsyncImport.importFileName,
          translationAsyncImport.userDismissed, translationAsyncImport.errorMessage,
          translationAsyncImport.auditInfo);
    }
  };

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("translationSet", translationSetId)
        .add("uuid", uuid)
        .add("importFileName", importFileName)
        .add("translationAsyncImportStatus", translationAsyncImportStatus)
        .add("userDismissed", userDismissed)
        .add("errorMessage", errorMessage)
        .add("auditInfo", auditInfo)
        .toString();
  }
}
