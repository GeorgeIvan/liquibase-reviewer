package com.appiancorp.record.entities.queryperformancemonitor;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appiancorp.common.query.RecordQuerySource;
import com.appiancorp.record.queryperformancemonitor.entities.ReadOnlyRecordQuerySummary;
import com.appiancorp.record.query.RecordQueryOrigin;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.cdt.value.RecordQuerySummaryDto;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "record_query_summary")
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordQuerySummary")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= "RecordQuerySummary",
    propOrder={"queryUuid", "startTime", "executionTime", "waitTime", "errorCode", "issuer", "traceId",
    "topDesignObjectUuid", "currentDesignObjectUuid", "recordTypeUuid", "querySource", "queryDetailsId"})
public class RecordQuerySummary implements ReadOnlyRecordQuerySummary {
    public static final String PROP_QUERY_UUID = "queryUuid";
    public static final String PROP_START_TIME = "startTime";
    public static final String PROP_EXECUTION_TIME = "executionTime";
    public static final String PROP_WAIT_TIME = "waitTime";
    public static final String PROP_ERROR_CODE = "errorCode";
    public static final String PROP_ISSUER = "issuer";
    public static final String PROP_TRACE_ID = "traceId";
    public static final String PROP_TOP_DESIGN_OBJECT_UUID = "topDesignObjectUuid";
    public static final String PROP_CURRENT_DESIGN_OBJECT_UUID = "currentDesignObjectUuid";
    public static final String PROP_RECORD_TYPE_UUID = "recordTypeUuid";
    public static final String PROP_QUERY_SOURCE_BYTE = "querySourceByte";
    public static final String PROP_QUERY_DETAILS_ID = "queryDetailsId";
    private String queryUuid;
    private Long startTime;
    private Integer executionTime;
    private Integer waitTime;
    private String errorCode;
    private String issuer;
    private String traceId;
    private String topDesignObjectUuid;
    private String currentDesignObjectUuid;
    private String recordTypeUuid;

    private String querySource;
    private Integer queryDetailsId;
    private RecordQueryOrigin queryOrigin;

    public RecordQuerySummary() {}

    @SuppressWarnings({"checkstyle:ParameterNumber"})
    public RecordQuerySummary(
        String queryUuid,
        Long startTime,
        Integer executionTime,
        Integer waitTime,
        String errorCode,
        String issuer,
        String traceId,
        String topDesignObjectUuid,
        String currentDesignObjectUuid,
        String recordTypeUuid,
        String querySource,
        Integer queryDetailsId,
        RecordQueryOrigin queryOrigin
    ) {
        this.queryUuid = queryUuid;
        this.startTime = startTime;
        this.executionTime = executionTime;
        this.waitTime = waitTime;
        this.errorCode = errorCode;
        this.issuer = issuer;
        this.traceId = traceId;
        this.topDesignObjectUuid = topDesignObjectUuid;
        this.currentDesignObjectUuid = currentDesignObjectUuid;
        this.recordTypeUuid = recordTypeUuid;
        this.querySource = querySource;
        this.queryDetailsId = queryDetailsId;
        this.queryOrigin = queryOrigin;
    }

    public RecordQuerySummary(RecordQuerySummaryDto dto) {
        this.queryUuid = dto.getQueryUuid();
        this.startTime = dto.getStartTime() == null ? null : dto.getStartTime().longValue();
        this.executionTime = dto.getExecutionTime();
        this.waitTime = dto.getWaitTime();
        this.errorCode = dto.getErrorCode();
        this.issuer = dto.getIssuer();
        this.traceId = dto.getTraceId();
        this.topDesignObjectUuid = dto.getTopDesignObjectUuid();
        this.currentDesignObjectUuid = dto.getCurrentDesignObjectUuid();
        this.recordTypeUuid = dto.getRecordTypeUuid();
        this.querySource = dto.getQuerySource();
        this.queryDetailsId = dto.getQueryDetailsId();
    }

    public RecordQuerySummary(ReadOnlyRecordQuerySummary readOnlyRecordQuerySummary) {
        this.queryUuid = readOnlyRecordQuerySummary.getQueryUuid();
        this.startTime = readOnlyRecordQuerySummary.getStartTime();
        this.executionTime = readOnlyRecordQuerySummary.getExecutionTime();
        this.waitTime = readOnlyRecordQuerySummary.getWaitTime();
        this.errorCode = readOnlyRecordQuerySummary.getErrorCode();
        this.issuer = readOnlyRecordQuerySummary.getIssuer();
        this.traceId = readOnlyRecordQuerySummary.getTraceId();
        this.topDesignObjectUuid = readOnlyRecordQuerySummary.getTopDesignObjectUuid();
        this.currentDesignObjectUuid = readOnlyRecordQuerySummary.getCurrentDesignObjectUuid();
        this.recordTypeUuid = readOnlyRecordQuerySummary.getRecordTypeUuid();
        this.querySource = readOnlyRecordQuerySummary.getQuerySource();
        this.queryDetailsId = readOnlyRecordQuerySummary.getQueryDetailsId();
    }

    public static RecordQuerySummaryBuilder builder() {
        return new RecordQuerySummaryBuilder();
    }

    @Id
    @Column(name = "query_uuid", nullable = false, updatable = false, length = 36)
    public String getQueryUuid() {
        return queryUuid;
    }

    public void setQueryUuid(String queryUuid) {
        this.queryUuid = queryUuid;
    }

    @Column(name = "start_time")
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @Column(name = "execution_time")
    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    @Column(name = "wait_time")
    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    @Column(name = "error_code", length = 16)
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Column(name = "issuer")
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @Column(name = "trace_id", length = 32)
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Column(name = "top_design_object_uuid", length = 36)
    public String getTopDesignObjectUuid() {
        return topDesignObjectUuid;
    }

    public void setTopDesignObjectUuid(String topDesignObjectUuid) {
        this.topDesignObjectUuid = topDesignObjectUuid;
    }

    @Column(name = "current_design_object_uuid", length = 36)
    public String getCurrentDesignObjectUuid() {
        return currentDesignObjectUuid;
    }

    public void setCurrentDesignObjectUuid(String currentDesignObjectUuid) {
        this.currentDesignObjectUuid = currentDesignObjectUuid;
    }

    @Column(name = "record_type_uuid", length = 36)
    public String getRecordTypeUuid() {
        return recordTypeUuid;
    }

    public void setRecordTypeUuid(String recordTypeUuid) {
        this.recordTypeUuid = recordTypeUuid;
    }

    @Column(name = "query_source")
    public byte getQuerySourceByte() {
        if (querySource == null) {
            return 0;
        }

        RecordQuerySource recordQuerySource = RecordQuerySource.getRecordQuerySource(querySource);
        if (recordQuerySource == null) {
            return 0;
        }

        Byte querySourceByte = recordQuerySource.getSourceByte();
        return querySourceByte.byteValue();
    }

    public void setQuerySourceByte(byte querySourceByte) {
        RecordQuerySource recordQuerySource = RecordQuerySource.getRecordQuerySource(querySourceByte);
        if (recordQuerySource != null) {
            setQuerySource(recordQuerySource.getSourceString());
        } else {
            setQuerySource(RecordQuerySource.OTHER.getSourceString());
        }
    }

    @Transient
    public String getQuerySource() {
        return querySource;
    }

    public void setQuerySource(String querySource) {
        this.querySource = querySource;
    }

    @Column(name = "query_details_id", nullable = false)
    public Integer getQueryDetailsId() {
        return queryDetailsId;
    }

    public void setQueryDetailsId(Integer queryDetailsId) {
        this.queryDetailsId = queryDetailsId;
    }

    @Transient
    public RecordQueryOrigin getQueryOrigin() {
        return queryOrigin;
    }

    public void setQueryOrigin(RecordQueryOrigin queryOrigin) {
        this.queryOrigin = queryOrigin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecordQuerySummary that = (RecordQuerySummary)o;
        return Objects.equals(queryUuid, that.queryUuid) && Objects.equals(startTime, that.startTime) &&
            Objects.equals(executionTime, that.executionTime) && Objects.equals(waitTime, that.waitTime) &&
            Objects.equals(errorCode, that.errorCode) && Objects.equals(issuer, that.issuer) &&
            Objects.equals(traceId, that.traceId) &&
            Objects.equals(topDesignObjectUuid, that.topDesignObjectUuid) &&
            Objects.equals(currentDesignObjectUuid, that.currentDesignObjectUuid) &&
            Objects.equals(recordTypeUuid, that.recordTypeUuid) &&
            Objects.equals(querySource, that.querySource) &&
            Objects.equals(queryDetailsId, that.queryDetailsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryUuid, startTime, executionTime, waitTime, errorCode, issuer, traceId,
            topDesignObjectUuid, currentDesignObjectUuid, recordTypeUuid, querySource, queryDetailsId);
    }
}
