package com.appiancorp.record.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appian.core.persist.Constants;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.recordlevelsecurity.ReadOnlyRecordLevelSecurity;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordRowLevelSecurityCfg;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = RecordLevelSecurityCfg.RECORD_LEVEL_SECURITY_TABLE_NAME)
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordRowLevelSecurityCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RecordLevelSecurityCfg.LOCAL_PART,
    propOrder={"id", "uuid", "membershipFilter", "dataFilter", "orderIdx", "isActive"})
public class RecordLevelSecurityCfg implements Id<Long>, ReadOnlyRecordLevelSecurity {
  public static final String RECORD_LEVEL_SECURITY_TABLE_NAME = "record_type_rls_rules";
  public static final String LOCAL_PART = "RecordRowLevelSecurityCfg";
  public static final String PROP_ID = "id";
  public static final String PROP_RLS_UUID = "uuid";
  public static final String PROP_DATA_FILTER = "dataFilter";
  public static final String PROP_MEMBERSHIP_FILTER = "membershipFilter";
  public static final String PROP_RECORD_TYPE_ID = "recordTypeId";

  public static final String RT_ID_COLUMN_NAME = "rt_id";

  private Long id;
  private String uuid;
  private String dataFilter;
  private String membershipFilter;
  private boolean isActive = true;
  private Long orderIdx;
  private Long rtId;

  public RecordLevelSecurityCfg() {}

  public RecordLevelSecurityCfg(DesignerDtoRecordRowLevelSecurityCfg dto) {
    this.id = dto.getId();
    this.uuid = dto.getUuid();
    this.membershipFilter = dto.getMembershipFilter();
    this.dataFilter = dto.getDataFilter();
    this.orderIdx = dto.getOrderIdx();
    this.isActive = dto.isIsActive();
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @XmlElement
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Lob
  @XmlElement
  @Column(name = "data_filter", nullable = false)
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRowLevelSecurityCfg)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_ROW_LEVEL_SECURITY_CFG_DATA)
  public String getDataFilter() {
    return dataFilter;
  }

  public void setDataFilter(String dataFilter) {
    this.dataFilter = dataFilter;
  }

  @Lob
  @XmlElement
  @Column(name = "membership_filter", nullable = false)
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRowLevelSecurityCfg)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_ROW_LEVEL_SECURITY_CFG_DATA)
  public String getMembershipFilter() {
    return membershipFilter;
  }

  public void setMembershipFilter(String membershipFilter) {
    this.membershipFilter = membershipFilter;
  }

  @Column(name = "order_idx", nullable = false)
  @XmlElement
  public Long getOrderIdx() {
    return orderIdx;
  }

  public void setOrderIdx(Long orderIdx) {
    this.orderIdx = orderIdx;
  }

  @XmlTransient
  @Column(name = RT_ID_COLUMN_NAME, insertable = false, updatable = false)
  public Long getRecordTypeId() {
    return rtId;
  }

  public void setRecordTypeId(Long rtId) {
    this.rtId = rtId;
  }

  @Column(name = "is_active", nullable = false)
  @XmlElement
  public boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordLevelSecurityCfg)) {
      return false;
    }

    RecordLevelSecurityCfg that = (RecordLevelSecurityCfg)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(uuid, that.uuid)
        .append(membershipFilter, that.membershipFilter)
        .append(dataFilter, that.dataFilter)
        .append(orderIdx, that.orderIdx)
        .append(isActive, that.isActive)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, uuid, dataFilter, membershipFilter, orderIdx, isActive);
  }
}
