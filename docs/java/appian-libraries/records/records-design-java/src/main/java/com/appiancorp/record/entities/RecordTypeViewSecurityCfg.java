package com.appiancorp.record.entities;

import static com.appiancorp.recordlevelsecurity.ReadOnlyRecordTypeBaseSecurity.PROP_DATA_FILTER;
import static com.appiancorp.recordlevelsecurity.ReadOnlyRecordTypeBaseSecurity.PROP_ID;
import static com.appiancorp.recordlevelsecurity.ReadOnlyRecordTypeBaseSecurity.PROP_MEMBERSHIP_FILTER;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.recordlevelsecurity.ReadOnlyRecordUiSecurity;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordRowLevelSecurityCfg;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = ReadOnlyRecordUiSecurity.RECORD_TYPE_UI_SECURITY_TABLE_NAME)
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordTypeViewSecurityCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RecordTypeViewSecurityCfg.LOCAL_PART,
    propOrder={PROP_ID, PROP_DATA_FILTER, PROP_MEMBERSHIP_FILTER})
public class RecordTypeViewSecurityCfg implements Id<Long>, ReadOnlyRecordUiSecurity {
  public static final String LOCAL_PART = "RecordTypeViewSecurityCfg";

  private Long id;
  private String dataFilter;
  private String membershipFilter;

  public RecordTypeViewSecurityCfg() {}

  public RecordTypeViewSecurityCfg(DesignerDtoRecordRowLevelSecurityCfg dto) {
    this.id = dto.getId();
    this.membershipFilter = dto.getMembershipFilter();
    this.dataFilter = dto.getDataFilter();
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

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordTypeViewSecurityCfg)) {
      return false;
    }

    RecordTypeViewSecurityCfg that = (RecordTypeViewSecurityCfg)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(membershipFilter, that.membershipFilter)
        .append(dataFilter, that.dataFilter)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, dataFilter, membershipFilter);
  }
}
