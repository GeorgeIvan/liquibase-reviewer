package com.appiancorp.record.entities.queryperformancemonitor;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appiancorp.record.queryperformancemonitor.entities.ReadOnlyRecordQueryDetails;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.cdt.RecordQueryDetailsDto;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "record_query_details")
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name = "recordQueryDetails")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name = RecordQueryDetails.LOCAL_PART, propOrder = {
    "id", "queryDetails", "queryDetailsSha256"})
public class RecordQueryDetails implements ReadOnlyRecordQueryDetails {

  public static final String LOCAL_PART = "RecordQueryDetails";

  public static final String PROP_ID = "id";
  public static final String PROP_QUERY_DETAILS = "queryDetails";
  public static final String PROP_QUERY_DETAILS_SHA256 = "queryDetailsSha256";

  private Long id;
  private String queryDetails;

  private String queryDetailsSha256;

  public RecordQueryDetails() {
  }

  public RecordQueryDetails(Long id, String queryDetails, String queryDetailsSha256) {
    this.id = id;
    this.queryDetails = queryDetails;
    this.queryDetailsSha256 = queryDetailsSha256;
  }

  public RecordQueryDetails(RecordQueryDetailsDto recordQueryDetailsDto) {
    this.id = recordQueryDetailsDto.getId();
    this.queryDetails = recordQueryDetailsDto.getQueryDetails();
    this.queryDetailsSha256 = recordQueryDetailsDto.getQueryDetailsSha256();
  }

  public RecordQueryDetails(String queryDetails, String queryDetailsSha256) {
    this.queryDetails = queryDetails;
    this.queryDetailsSha256 = queryDetailsSha256;
  }

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  @Override
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "query_details", updatable = false, nullable = false)
  @Lob
  @Override
  public String getQueryDetails() {
    return queryDetails;
  }

  public void setQueryDetails(String queryDetails) {
    this.queryDetails = queryDetails;
  }

  @Column(name = "query_details_sha256", updatable = false, nullable = false, length = 64)
  @Override
  public String getQueryDetailsSha256() {
    return queryDetailsSha256;
  }

  public void setQueryDetailsSha256(String queryDetailsSha256) {
    this.queryDetailsSha256 = queryDetailsSha256;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecordQueryDetails that = (RecordQueryDetails)o;
    return Objects.equals(id, that.id) && Objects.equals(queryDetails, that.queryDetails) &&
        Objects.equals(queryDetailsSha256, that.queryDetailsSha256);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, queryDetails, queryDetailsSha256);
  }

  @Override
  public String toString() {
    return "RecordQueryDetails{" + "id=" + id + ", queryDetails='" + queryDetails + '\'' +
        ", queryDetailsSha256='" + queryDetailsSha256 + '\'' + '}';
  }
}
