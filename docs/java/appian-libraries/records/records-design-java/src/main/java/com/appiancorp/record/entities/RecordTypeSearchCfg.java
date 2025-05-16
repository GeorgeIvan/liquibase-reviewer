package com.appiancorp.record.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.record.domain.RecordTypePropertySource;
import com.appiancorp.record.recordtypesearch.ReadOnlyRecordTypeSearchField;
import com.appiancorp.record.recordtypesearch.RecordTypeSearch;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoRecordTypeSearchCfg;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Hidden
@Entity
@Table(name = "record_type_search_cfg")
@IgnoreJpa
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name="recordTypeSearchCfg")
@XmlType(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name= RecordTypeSearchCfg.LOCAL_PART,
    propOrder={"id", "searchFieldsSrc", "placeholderSrc", "placeholder",
        "recordTypeSearchFieldCfgs"})
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class RecordTypeSearchCfg implements Id<Long>, RecordTypeSearch {
  public static final String LOCAL_PART = "RecordTypeSearchCfg";
  public static final String PROP_ID = "id";
  public static final String PROP_SEARCH_FIELDS_SRC = "searchFieldsSrc";
  public static final String PROP_PLACEHOLDER_SRC = "placeholderSrc";
  public static final String PROP_PLACEHOLDER = "placeholder";
  private static final long serialVersionUID = 1L;

  private Long id;
  private RecordTypePropertySource searchFieldsSrc = RecordTypePropertySource.DEFAULT;
  private RecordTypePropertySource placeholderSrc = RecordTypePropertySource.DEFAULT;
  private String placeholder;
  private List<RecordTypeSearchFieldCfg> recordTypeSearchFieldCfgs = new ArrayList<>();
  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;

  public RecordTypeSearchCfg() {}

  public RecordTypeSearchCfg(DesignerDtoRecordTypeSearchCfg dto) {
    this.id = dto.getId();
    this.searchFieldsSrc = RecordTypePropertySource.fromText(dto.getSearchFieldsSrc());
    this.placeholderSrc = RecordTypePropertySource.fromText(dto.getPlaceholderSrc());
    this.placeholder = dto.getPlaceholder();
    this.recordTypeSearchFieldCfgs = dto.getSearchFields().stream()
        .map(RecordTypeSearchFieldCfg::new)
        .collect(Collectors.toList());
    this.expressionTransformationState = dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY;
  }

  @javax.persistence.Id
  @GeneratedValue
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  @Column(name = "id", nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Transient
  @XmlElement(name = "searchFieldsSrc")
  public RecordTypePropertySource getSearchFieldsSrc() {
    return searchFieldsSrc;
  }

  public void setSearchFieldsSrc(RecordTypePropertySource searchFieldsSrc) {
    this.searchFieldsSrc = searchFieldsSrc;
  }

  @XmlTransient
  @Column(name = "search_fields_src", nullable = false)
  public byte getSearchFieldsSrcByte() {
    return searchFieldsSrc.getCode();
  }

  public void setSearchFieldsSrcByte(byte searchFieldsSrc) {
    this.searchFieldsSrc = RecordTypePropertySource.valueOf(searchFieldsSrc);
  }

  @Transient
  @XmlElement(name = "placeholderSrc")
  public RecordTypePropertySource getPlaceholderSrc() {
    return placeholderSrc;
  }

  public void setPlaceholderSrc(RecordTypePropertySource placeholderSrc) {
    this.placeholderSrc = placeholderSrc;
  }

  @XmlTransient
  @Column(name = "placeholder_src", nullable = false)
  public byte getPlaceholderSrcByte() {
    return placeholderSrc.getCode();
  }

  public void setPlaceholderSrcByte(byte placeholderSrc) {
    this.placeholderSrc = RecordTypePropertySource.valueOf(placeholderSrc);
  }

  @Lob
  @XmlElement
  @Column(name = "placeholder")
  @ComplexForeignKey(nullable=true, breadcrumb = BreadcrumbText.recordTypeSearchCfgPlaceholder, variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  @XmlElement(name = "recordTypeSearchFieldCfgs")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "search_cfg_id", nullable = false)
  @Override
  public List<RecordTypeSearchFieldCfg> getRecordTypeSearchFieldCfgs() {
    return recordTypeSearchFieldCfgs;
  }

  @Override
  @Transient
  public ImmutableList<ReadOnlyRecordTypeSearchField> getRecordTypeSearchFieldCfgsReadOnly() {
    return ImmutableList.copyOf(getRecordTypeSearchFieldCfgs());
  }

  @VisibleForTesting
  public void setRecordTypeSearchFieldCfgs(List<RecordTypeSearchFieldCfg> recordTypeSearchFieldCfgs) {
    this.recordTypeSearchFieldCfgs = recordTypeSearchFieldCfgs;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeSearchFieldCfg)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_QUERY_INFO_LIST)
  public List<String> getSearchFieldQueryInfos() {
    return recordTypeSearchFieldCfgs.stream()
        .map(RecordTypeSearchFieldCfg::getQueryInfo)
        .collect(Collectors.toList());
  }

  @Transient
  @XmlTransient
  @Override
  public ExpressionTransformationState getExpressionTransformationState() {
    return expressionTransformationState;
  }

  public void setExpressionTransformationState(ExpressionTransformationState state) {
    this.expressionTransformationState = state;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof RecordTypeSearchCfg)) {
      return false;
    }

    RecordTypeSearchCfg that = (RecordTypeSearchCfg)o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(searchFieldsSrc, that.searchFieldsSrc)
        .append(placeholderSrc, that.placeholderSrc)
        .append(placeholder, that.placeholder)
        .append(recordTypeSearchFieldCfgs, that.recordTypeSearchFieldCfgs)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, searchFieldsSrc, placeholderSrc, placeholder, recordTypeSearchFieldCfgs);
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }
}

