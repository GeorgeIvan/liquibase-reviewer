package com.appiancorp.record.replicaloaderror;

import static com.appian.core.persist.Constants.COL_MAXLEN_GENERATED_EXPRESSION;
import static com.appian.core.persist.Constants.COL_MAXLEN_MAX_NON_CLOB;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_OBJECT_ARRAY;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.json.parsers.TransitEncoder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;

/**
 * Represents a single replica load error.
 */
@XmlTransient
@Entity
@IgnoreJpa
@Hidden
@Table(name = "replica_load_error")
public final class ReplicaLoadErrorImpl implements ReplicaLoadError {
  public static final String PROP_ID =                    "id";
  public static final String PROP_REPLICA_LOAD_EVENT_ID = "replicaLoadEventId";
  public static final String PROP_RECORD_TYPE_UUID =      "recordTypeUuid";
  public static final String PROP_ERROR_CODE =            "errorCode";
  public static final String PROP_TIME_MS =               "timeMs";

  private static final int ENCODING_OVERHEAD_SIZE = 16;
  // Size of liquibase mediumStringType. Note: json encoding will also consume some capacity.
  // 4000 (- 16 for constant db overhead) I've only found an overhead of 2 bytes so 16 is conservative
  private static final int MEDIUM_STRING_SIZE = COL_MAXLEN_MAX_NON_CLOB - ENCODING_OVERHEAD_SIZE;
  private static final int LARGE_STRING_SIZE = COL_MAXLEN_GENERATED_EXPRESSION - ENCODING_OVERHEAD_SIZE;
  // Allow for a 8 byte encoding.  The worst encoding I know of that is used by one of the supported databases
  // is 4.  500 (- 16 for constant db overhead) I've only found an overhead of 2 bytes so 16 is conservative
  private static final int PESSIMISTIC_NUM_CHARS_FOR_MEDIUM_STRING = 484;

  private Long id;
  private Long replicaLoadEventId;
  private String recordTypeUuid;
  private String errorCode;
  private String errorMsgParamsStr;
  private String causesStr;
  private Long timeMs;
  private Long processId;
  private String changedIds;
  private String fieldUuid;
  private ReplicaLoadErrorType type;

  private ReplicaLoadErrorImpl() {}

  /**
   * @param replicaLoadEventId the id of the load event during which this error occurred.
   * @param recordTypeUuid the uuid of the record type being loaded by the load event.  This exists to allow
   *                       querying for all load errors for a record type
   * @param errorCode the string representation of an {@link com.appiancorp.suiteapi.common.exceptions.ErrorCode}
   * @param errorMsgParams the parameters to the formatted error message for <tt>errorCode</tt>.  These parameters
   *                       should all be Object representations of simple types, that is primitives and time
   *                       related values.
   * @param causes the messages of the causing exceptions.  These are not localized; they have no parameters.
   * @param timeMs the time of the error as in {@link Date#getTime}
   * @param processId the ID of the process where the failure happened
   * @param changedIds the primary key IDs to identify the rows that failed
   * @param fieldUuid the UUID of the custom field that failed to evaluate
   * @param type the type of failure as defined by {@link ReplicaLoadErrorType}
   */
  private ReplicaLoadErrorImpl(Long replicaLoadEventId, String recordTypeUuid, String errorCode, Object[] errorMsgParams,
      String[] causes, Long timeMs, Long processId, String[] changedIds, String fieldUuid, ReplicaLoadErrorType type) {
    this.replicaLoadEventId = replicaLoadEventId;
    this.recordTypeUuid = recordTypeUuid;
    this.errorCode = errorCode;
    // Ignore the size for the message parameters.
    this.errorMsgParamsStr = encodeAsJson(errorMsgParams, Integer.MAX_VALUE);
    this.causesStr = encodeAsJson(causes, MEDIUM_STRING_SIZE);
    this.timeMs = timeMs;
    this.processId = processId;
    this.changedIds = encodeAsJson(changedIds, LARGE_STRING_SIZE);
    this.fieldUuid = fieldUuid;
    this.type = type;
  }

  /**
   * @return a copy of <tt>orig</tt> with causes truncated to fit in its DB cell even with an impossibly
   * wide character encoding.
   */
  public static ReplicaLoadErrorImpl copyWithTruncatedCauses(ReplicaLoadErrorImpl orig) {
    final ReplicaLoadErrorImpl retval = new ReplicaLoadErrorImpl();
    retval.replicaLoadEventId = orig.replicaLoadEventId;
    retval.recordTypeUuid = orig.recordTypeUuid;
    retval.errorCode = orig.errorCode;
    retval.errorMsgParamsStr = orig.errorMsgParamsStr;
    retval.causesStr = encodeAsJson(orig.getCauses(), PESSIMISTIC_NUM_CHARS_FOR_MEDIUM_STRING);
    retval.timeMs = orig.timeMs;
    retval.processId = orig.processId;
    retval.changedIds = orig.changedIds;
    retval.fieldUuid = orig.fieldUuid;
    retval.type = orig.type;
    return retval;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  private void setId(Long id) {
    this.id = id;
  }

  @Column(name = "replica_load_event_id", updatable = false)
  public Long getReplicaLoadEventId() {
    return replicaLoadEventId;
  }

  private void setReplicaLoadEventId(Long replicaLoadEventId) {
    this.replicaLoadEventId = replicaLoadEventId;
  }

  @Column(name = "record_type_uuid", updatable = false, nullable = false)
  public String getRecordTypeUuid() {
    return recordTypeUuid;
  }

  private void setRecordTypeUuid(String recordTypeUuid) {
    this.recordTypeUuid = recordTypeUuid;
  }

  @Column(name = "error_code", updatable = false, nullable = false)
  public String getErrorCode() {
    return errorCode;
  }

  private void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  // It is ok to have no params so nullable
  @Column(name = "error_msg_params", updatable = false)
  @VisibleForTesting
  String getErrorMsgParamsStr() {
    return errorMsgParamsStr;
  }

  @VisibleForTesting
  private void setErrorMsgParamsStr(String errorMsgParams) {
    errorMsgParamsStr = errorMsgParams;
  }

  @Transient
  public Object[] getErrorMsgParams() {
    if (errorMsgParamsStr == null) {
      return EMPTY_OBJECT_ARRAY; // Don't make client deal with null
    }
    List<?> list = (List<?>)TransitEncoder.fromJson(errorMsgParamsStr);
    return list.toArray();
  }

  // It is ok to have no cause so nullable
  @Column(name = "causes", updatable = false)
  @VisibleForTesting
  public String getCausesStr() {
    return causesStr;
  }

  @Transient
  public String[] getCauses() {
    if (causesStr == null) {
      return EMPTY_STRING_ARRAY; // Don't make client deal with null
    }
    List<String> list = (List<String>)TransitEncoder.fromJson(causesStr);
    return list.toArray(EMPTY_STRING_ARRAY);
  }

  @VisibleForTesting
  private void setCausesStr(String causes) {
    this.causesStr = causes;
  }

  // Note there is no field to be used for ordering the errors.  We anticipate loads will one day be
  // massively parallel, in which case there can be no total order.
  @Column(name = "time_ms", updatable = false, nullable = false)
  public Long getTimeMs() {
    return timeMs;
  }

  @VisibleForTesting
  private void setTimeMs(Long timeMs) {
    this.timeMs = timeMs;
  }

  @Column(name = "process_id", updatable = false)
  public Long getProcessId() {
    return processId;
  }

  public void setProcessId(Long processId) {
    this.processId = processId;
  }

  @Column(name = "changed_ids", length = COL_MAXLEN_GENERATED_EXPRESSION, updatable = false)
  @Lob
  public String getChangedIdsStr() {
    return changedIds;
  }

  @Transient
  public String[] getChangedIds() {
    if (changedIds == null) {
      return EMPTY_STRING_ARRAY; // Don't make client deal with null
    }
    List<String> list = (List<String>)TransitEncoder.fromJson(changedIds);
    return list.toArray(EMPTY_STRING_ARRAY);
  }

  public void setChangedIdsStr(String changedIds) {
    this.changedIds = changedIds;
  }

  @Column(name = "field_uuid", updatable = false)
  public String getFieldUuid() {
    return fieldUuid;
  }

  public void setFieldUuid(String fieldUuid) {
    this.fieldUuid = fieldUuid;
  }

  @Column(name = "type", updatable = false, nullable = false)
  private Byte getTypeByte() {
    return type.getCode();
  }

  private void setTypeByte(Byte status) {
    this.type = ReplicaLoadErrorType.getByCode(status);
  }

  @Transient
  public ReplicaLoadErrorType getType() {
    return type;
  }

  public void setType(ReplicaLoadErrorType type) {
    this.type = type;
  }

  /**
   * @param objects encode these
   * @param maxEncodedLength the max encoded string length
   * @return a Json encoding of <tt>objects</tt>.  If the encoded length exceeds <tt>maxEncodedLength</tt>
   * then repeatedly remove the 0th element until it fits.  In the extreme case where the 0th element is too
   * big then the decoded array will be null.
   */
  private static String encodeAsJson(Object[] objects, int maxEncodedLength) {
    if (objects == null) {
      return null;
    }

    while (objects.length > 0){
      final String encoded = TransitEncoder.toJson(objects);
      if (encoded.length() < maxEncodedLength) {
        return encoded;
      }
      // For consistency we want to keep objects as an array, not a list, hence the copying.
      // We expect this to occur very rarely, so successive copying is ok.
      objects = Arrays.copyOfRange(objects, 1, objects.length);
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReplicaLoadErrorImpl that = (ReplicaLoadErrorImpl)o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public int compareTo(ReplicaLoadError o) {
    return this.getId().compareTo(o.getId());
  }

  @VisibleForTesting
  @SuppressWarnings("checkstyle:booleanexpressioncomplexity")
  public boolean fieldsEqual(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ReplicaLoadErrorImpl that = (ReplicaLoadErrorImpl)other;
    return Objects.equal(id, that.id) && Objects.equal(replicaLoadEventId, that.replicaLoadEventId) &&
        Objects.equal(recordTypeUuid, that.recordTypeUuid) && Objects.equal(errorCode, that.errorCode) &&
        Objects.equal(errorMsgParamsStr, that.errorMsgParamsStr) &&
        Objects.equal(causesStr, that.causesStr) && Objects.equal(timeMs, that.timeMs) &&
        Objects.equal(processId, that.processId) && Objects.equal(changedIds, that.changedIds) &&
        Objects.equal(fieldUuid, that.fieldUuid) && Objects.equal(type, that.type);
  }

  public static ReplicaLoadErrorBuilder builder() {
    return new ReplicaLoadErrorBuilder();
  }

  public static final class ReplicaLoadErrorBuilder {
    private Long replicaLoadEventId;
    private String recordTypeUuid;
    private String errorCode;
    private Object[] errorMsgParams;
    private String[] causes;
    private Long timeMs;
    private Long processId;
    private String[] changedIds;
    private String fieldUuid;
    private ReplicaLoadErrorType type;

    private ReplicaLoadErrorBuilder() {}

    public ReplicaLoadErrorBuilder replicaLoadEventId(Long replicaLoadEventId) {
      this.replicaLoadEventId = replicaLoadEventId;
      return this;
    }

    public ReplicaLoadErrorBuilder recordTypeUuid(String recordTypeUuid) {
      this.recordTypeUuid = recordTypeUuid;
      return this;
    }

    public ReplicaLoadErrorBuilder errorCode(String errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public ReplicaLoadErrorBuilder errorMsgParams(Object[] errorMsgParams) {
      this.errorMsgParams = errorMsgParams;
      return this;
    }

    public ReplicaLoadErrorBuilder causes(String[] causes) {
      this.causes = causes;
      return this;
    }

    public ReplicaLoadErrorBuilder timeMs(Long timeMs) {
      this.timeMs = timeMs;
      return this;
    }

    public ReplicaLoadErrorBuilder processId(Long processId) {
      this.processId = processId;
      return this;
    }

    public ReplicaLoadErrorBuilder changedIds(String[] changedIds) {
      this.changedIds = changedIds;
      return this;
    }

    public ReplicaLoadErrorBuilder fieldUuid(String fieldUuid) {
      this.fieldUuid = fieldUuid;
      return this;
    }

    public ReplicaLoadErrorBuilder type(ReplicaLoadErrorType type) {
      this.type = type;
      return this;
    }

    public ReplicaLoadErrorImpl build() {
      return new ReplicaLoadErrorImpl(replicaLoadEventId, recordTypeUuid, errorCode, errorMsgParams, causes, timeMs,
          processId, changedIds, fieldUuid, type);
    }
  }
}
