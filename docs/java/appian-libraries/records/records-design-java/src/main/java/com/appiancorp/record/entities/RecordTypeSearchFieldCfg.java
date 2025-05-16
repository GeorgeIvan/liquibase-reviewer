package com.appiancorp.record.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.record.recordtypesearch.RecordTypeSearchField;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordTypeSearchFieldCfg;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "rt_search_field_cfg")
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordTypeSearchFieldCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RecordTypeSearchFieldCfg.LOCAL_PART,
    propOrder={"id", "queryInfo", "orderIdx"})
public class RecordTypeSearchFieldCfg implements Id<Long>, RecordTypeSearchField {
  public static final String LOCAL_PART = "RecordTypeSearchFieldCfg";
  public static final String PROP_ID = "id";
  public static final String PROP_QUERY_INFO = "queryInfo";
  public static final String PROP_ORDER_IDX = "orderIdx";
  private static final long serialVersionUID = 1L;

  private Long id;
  private String queryInfo;
  private Long orderIdx;

  public RecordTypeSearchFieldCfg() {}

  public RecordTypeSearchFieldCfg(DesignerDtoRecordTypeSearchFieldCfg dto) {
    this.id = dto.getId();
    this.queryInfo = dto.getQueryInfo();
    this.orderIdx = dto.getOrderIdx();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @XmlElement
  @Column(name = "query_info", nullable = false)
  public String getQueryInfo() {
    return queryInfo;
  }

  public void setQueryInfo(String queryInfo) {
    this.queryInfo = queryInfo;
  }

  @XmlElement
  @Column(name = "order_idx", nullable = false)
  public Long getOrderIdx() {
    return orderIdx;
  }

  public void setOrderIdx(Long orderIdx) {
    this.orderIdx = orderIdx;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordTypeSearchFieldCfg)) {
      return false;
    }

    RecordTypeSearchFieldCfg that = (RecordTypeSearchFieldCfg)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(queryInfo, that.queryInfo)
        .append(orderIdx, that.orderIdx)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, queryInfo, orderIdx);
  }
}

