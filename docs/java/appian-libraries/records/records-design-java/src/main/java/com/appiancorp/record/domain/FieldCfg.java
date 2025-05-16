package com.appiancorp.record.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.common.collect.Comparators;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.annotations.VisibleForTesting;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.DesignerDtoUserFilter;
import com.appiancorp.type.cdt.DesignerFacetData;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents a User Filter configured by the designer on a Record Type.
 *
 * Previous description:
 * Represents a mapping between an underlying record source element and a record field. A
 * record source element may be something like a process variable if the record maps to a
 * process, or a dot-notation field element if the record source is an entity, for
 * example.<br/>
 * <br/>
 * This field definition contains also meta-information for display configuration in list
 * view such as whether the field is sortable, searchable, or a facet. If the field is a
 * facet, additional facet configuration info may also be provided.
 *
 * @author dan.mascenik
 * @see ReadOnlyRecordTypeDefinition
 */
@Hidden
@Entity
@Table(name = FieldCfg.TABLE_NAME)
@XmlRootElement(name = "fieldCfg", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = FieldCfg.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {"id", "name",
  "description", "sourceRef", "sortable", "searchable", "facet", "facetType", "exclusiveFacet",
  "facetLabelExpr", "facetExpr", "facetOptions", "sortOrderIndex", "facetOrderIndex", "visibilityExpr",
  "defaultOptionExpr", "allowMultipleSelections", "facetData", "uuid", "relatedRecordFieldUuid", "relatedRecordUserFilterSort"})
@IgnoreJpa
@BreadcrumbProperty(format = BreadcrumbText.recordTypeFieldCfgName, value = "name",
  breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_PEERS)
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public final class FieldCfg implements ReadOnlyFieldCfg {
  private static final long serialVersionUID = 1L;

  public static final String TABLE_NAME = "record_fld_cfg";
  public static final String LOCAL_PART = "FieldCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = "id";
  public static final String PROP_NAME = "name";
  public static final String PROP_SOURCE_REF = "sourceRef";
  public static final String PROP_RECORD_TYPE = "recordType";
  public static final String PROP_IS_SORTABLE = "isSortable";
  public static final String PROP_IS_SEARCHABLE = "isSearchable";
  public static final String PROP_IS_FACET = "isFacet";
  public static final String PROP_FACET_OPTIONS = "facetOptions";
  public static final String PROP_FACET_UUID = "uuid";
  public static final String PROP_RELATED_RECORD_FIELD_UUID = "relatedRecordFieldUuid";
  public static final String PROP_RELATED_RECORD_USER_FILTER_SORT = "relatedRecordUserFilterSort";


  /* This setting allows for batching of collection fetches. Since a record type may have several field cfgs,
   * each with a collection of facet options, we want to initialize many facet option collections at once,
   * instead of one by one. */
  private static final int PREFETCH_BATCH_SIZE = 100;

  private Long id;
  private String name;
  private int sortOrderIndex;
  private int facetOrderIndex;
  private String sourceRef;
  private String description;
  private RecordTypeDefinition recordType;
  private List<FacetOptionCfg> facetOptions = new ArrayList<>();
  private boolean isSortable = false;
  private boolean isSearchable = false;
  private boolean isFacet = false;
  private FacetType facetType = FacetType.CUSTOM_BUCKETS_CLOSED;
  private boolean allowMultipleSelections = false;
  private boolean isExclusiveFacet = true;
  private String facetLabelExpr;
  private String facetExpr;
  private String defaultOptionExpr;
  private String facetData;
  private String visibilityExpr;
  private String uuid;
  private String relatedRecordFieldUuid;
  private RelatedRecordUserFilterSort relatedRecordUserFilterSort = RelatedRecordUserFilterSort.UNSORTED;

  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;

  public FieldCfg() {}

  public FieldCfg(DesignerDtoUserFilter dto) {
    Objects.requireNonNull(dto, "User Filter DTO cannot be null");
    setName(dto.getName());
    setDescription(dto.getDescription());
    setExclusiveFacet(dto.isIsExclusiveFacet());
    setSourceRef(dto.getSourceRef());
    setFacet(dto.isIsFacet());
    setFacetType(FacetType.valueOf(dto.getFacetType()));
    setFacetExpr(dto.getFacetExpr());
    setFacetLabelExpr(dto.getFacetLabelExpr());
    setSortOrderIndex(dto.getSortOrderIndex());
    setFacetOrderIndex(dto.getFacetOrderIndex());
    setId(dto.getId());
    setSortable(dto.isIsSortable());
    List<FacetOptionCfg> options = new ArrayList<>(dto.getFacetOption().size());
    dto.getFacetOption().stream().forEachOrdered(option -> options.add(new FacetOptionCfg(option)));
    setFacetOptions(options);
    setDefaultOptionExpr(dto.getDefaultOptionExpr());
    setVisibilityExpr(dto.getVisibilityExpr());
    setAllowMultipleSelections(dto.isAllowMultipleSelections());
    setUuid(dto.getUuid());
    setRelatedRecordFieldUuid(dto.getRelatedRecordFieldUuid());
    setRelatedRecordUserFilterSort(
        RelatedRecordUserFilterSort.getBySortString(dto.getRelatedRecordUserFilterSort()));
    setExpressionTransformationState(dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY);

    Object facetData = dto.getFacetData();
    initializeFacetData(dto, facetData);
  }

  private void initializeFacetData(DesignerDtoUserFilter dto, Object facetData) {
    if(facetData != null && dto.getFacetType().equals(FacetType.DATE_RANGE.name())) {
      /* dto.getFacetData() would  be a DesignerFacetData CDT. Read values from the cdt and convert that into JSON
        for storing it into the database
      */
      DesignerFacetData dateRangeDesignerFacetData = (DesignerFacetData)facetData;
      String startDateExpression = dateRangeDesignerFacetData.getStartDateExpression();
      String endDateExpression = dateRangeDesignerFacetData.getEndDateExpression();
      Map<String, String> defaultValuesMap = new HashMap<>();
      if ("".equals(startDateExpression) && "".equals(endDateExpression)) {
        setFacetData(null);
      } else {
        if(!("".equals(startDateExpression))) {
          defaultValuesMap.put("startDateExpression", startDateExpression);
        }
        if(!("".equals(endDateExpression))) {
          defaultValuesMap.put("endDateExpression", endDateExpression);
        }
        Gson gson = new Gson();
        setFacetData(gson.toJson(defaultValuesMap));
      }
    } else {
      setFacetData(null);
    }
  }

  @Override
  @Transient
  @XmlElement
  public FacetType getFacetType() {
    return facetType;
  }

  public void setFacetType(FacetType facetType) {
    this.facetType = facetType;
  }

  @PreUpdate
  @PrePersist
  @SuppressWarnings("unused")
  private void onPrePersistOrUpdate() {
    validateFacetConfig();
  }

  private void validateFacetConfig() {
    if (isFacet && facetType == FacetType.CUSTOM_BUCKETS_CLOSED) {
      Preconditions.checkNotNull(facetLabelExpr,
        "A facet field requires a non-null label expression");
    }
  }

  @Override
  @XmlElement
  @Column(name = "sort_order_idx", nullable=false)
  public int getSortOrderIndex() {
    return sortOrderIndex;
  }

  public void setSortOrderIndex(int sortOrderIndex) {
    this.sortOrderIndex = sortOrderIndex;
  }

  @XmlElement
  @Column(name = "facet_order_idx", nullable=false)
  public int getFacetOrderIndex() {
    return facetOrderIndex;
  }

  public void setFacetOrderIndex(int facetOrderIndex) {
    this.facetOrderIndex = facetOrderIndex;
  }

  @XmlElement(name = "facetOption")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeFacetOption)
  @JoinColumn(name = "record_fld_cfg_id", nullable = false)
  @OrderColumn(name="order_idx", nullable=false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = PREFETCH_BATCH_SIZE)
  public List<FacetOptionCfg> getFacetOptions() {
    return facetOptions;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyFacetOptionCfg> getFacetOptionsReadOnly() {
    List<FacetOptionCfg> facetOptions = getFacetOptions();
    if (facetOptions != null) {
      return ImmutableList.copyOf(facetOptions);
    }
    return null;
  }

  public void setFacetOptions(List<FacetOptionCfg> facetOptions) {
    this.facetOptions = facetOptions;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  @Column(name = "source_ref", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetSourceRef)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_QUERY_INFO)
  public String getSourceRef() {
    return sourceRef;
  }

  public void setSourceRef(String sourceRef) {
    this.sourceRef = sourceRef;
  }

  @Override
  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @XmlTransient
  @ManyToOne
  @JoinColumn(name = "record_type_id", nullable = false)
  public RecordTypeDefinition getRecordType() {
    return recordType;
  }

  public void setRecordType(RecordTypeDefinition recordTypeDefinition) {
    this.recordType = recordTypeDefinition;
  }

  @Override
  @Column(name = "facet_label_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetLabelExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getFacetLabelExpr() {
    return facetLabelExpr;
  }

  public void setFacetLabelExpr(String facetLabelExpr) {
    this.facetLabelExpr = facetLabelExpr;
  }

  @Override
  @Column(name = "is_sortable", nullable = false)
  @XmlElement(name = "isSortable")
  public boolean isSortable() {
    return isSortable;
  }

  public void setSortable(boolean isSortable) {
    this.isSortable = isSortable;
  }

  @Override
  @Column(name = "is_searchable", nullable = false)
  @XmlElement(name = "isSearchable")
  public boolean isSearchable() {
    return isSearchable;
  }

  public void setSearchable(boolean isSearchable) {
    this.isSearchable = isSearchable;
  }

  @Override
  @Column(name = "is_facet", nullable = false)
  @XmlElement(name = "isFacet")
  public boolean isFacet() {
    return isFacet;
  }

  public void setFacet(boolean isFacet) {
    this.isFacet = isFacet;
  }

  @Override
  @Column(name = "facet_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetExpr)
  @ForeignKeyCustomBinder(CustomBinderType.FIELD_CFG_FACET_EXPRESSION)
  public String getFacetExpr() {
    return facetExpr;
  }

  public void setFacetExpr(String facetExpr) {
    this.facetExpr = facetExpr;
  }

  @Override
  @Column(name = "visibility_expr", length = Constants.COL_MAXLEN_EXPRESSION, nullable = true)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetVisibilityExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getVisibilityExpr() {
    return visibilityExpr;
  }

  public void setVisibilityExpr(String visibilityExpr) {
    this.visibilityExpr = visibilityExpr;
  }

  @Override
  @Column(name = "default_opt_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetDefaultOptionExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDefaultOptionExpr() {
    return defaultOptionExpr;
  }

  public void setDefaultOptionExpr(String defaultOptionExpr) {
    this.defaultOptionExpr = defaultOptionExpr;
  }

  @XmlTransient
  @SuppressWarnings("unused")
  @Column(name = "facet_type", nullable = false)
  private byte getFacetTypeByte() {
    return facetType.getId();
  }

  @SuppressWarnings("unused")
  private void setFacetTypeByte(byte facetTypeByte) {
    this.facetType = FacetType.getById(facetTypeByte);
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

  /**
   * Facet options on this field are expected to be mutually exclusive. Selecting a facet
   * option should therefore remove this field from the remaining facet set.
   */
  @Override
  @Column(name = "is_excl_facet", nullable = false)
  @XmlElement(name = "isExclusiveFacet")
  public boolean isExclusiveFacet() {
    return isExclusiveFacet;
  }

  public void setExclusiveFacet(boolean isExclusiveFacet) {
    this.isExclusiveFacet = isExclusiveFacet;
  }

  @Override
  @Column(name = "facet_multi_select", nullable = false)
  @XmlElement(name = "allowMultipleSelections")
  public boolean getAllowMultipleSelections() {
    return allowMultipleSelections;
  }

  @VisibleForTesting
  public void setAllowMultipleSelections(boolean allow) {
    this.allowMultipleSelections = allow;
  }

  @Override
  @Column(name = "facet_data", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetFacetData)
  @ForeignKeyCustomBinder(CustomBinderType.JSON_EXPRESSION_MAP)
  public String getFacetData() {
    return facetData;
  }

  public void setFacetData(String facetData) {
    this.facetData = facetData;
  }

  @Override
  @Column(name = "uuid")
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = StringUtils.isBlank(uuid) ? null : uuid;
  }

  @Override
  @Column(name = "related_record_field_uuid")
  @XmlElement(name = "relatedRecordFieldUuid")
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetRelatedRecordField)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_QUERY_INFO)
  public String getRelatedRecordFieldUuid() {
    return relatedRecordFieldUuid;
  }

  public void setRelatedRecordFieldUuid(String relatedRecordFieldUuid) {
    this.relatedRecordFieldUuid = StringUtils.isBlank(relatedRecordFieldUuid) ? null : relatedRecordFieldUuid;
  }

  @Override
  @Transient
  public RelatedRecordUserFilterSort getRelatedRecordUserFilterSort() {
    return relatedRecordUserFilterSort;
  }

  public void setRelatedRecordUserFilterSort(RelatedRecordUserFilterSort relatedRecordUserFilterSort) {
    this.relatedRecordUserFilterSort = relatedRecordUserFilterSort;
  }

  @Column(name = "related_record_guided_sort", nullable = false)
  @XmlTransient
  @SuppressWarnings("unused")
  private byte getRelatedRecordUserFilterSortByte() {
    return relatedRecordUserFilterSort.getSortByte();
  }

  @SuppressWarnings("unused")
  private void setRelatedRecordUserFilterSortByte(byte sort) {
    setRelatedRecordUserFilterSort(RelatedRecordUserFilterSort.getBySortByte(sort));
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((facetLabelExpr == null) ? 0 : facetLabelExpr.hashCode());
    result = prime * result + ((facetType == null) ? 0 : facetType.hashCode());
    result = prime * result + (isExclusiveFacet ? 1231 : 1237);
    result = prime * result + (isFacet ? 1231 : 1237);
    result = prime * result + (isSearchable ? 1231 : 1237);
    result = prime * result + (isSortable ? 1231 : 1237);
    result = prime * result + (allowMultipleSelections ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + sortOrderIndex;
    result = prime * result + facetOrderIndex;
    result = prime * result + ((sourceRef == null) ? 0 : sourceRef.hashCode());
    result = prime * result + ((visibilityExpr == null) ? 0 : visibilityExpr.hashCode());
    result = prime * result + ((defaultOptionExpr == null) ? 0 : defaultOptionExpr.hashCode());
    result = prime * result + ((facetOptions == null) ? 0 : facetOptions.hashCode());
    result = prime * result + ((facetExpr == null) ? 0 : facetExpr.hashCode());
    result = prime * result + ((facetData == null) ? 0 : facetData.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    result = prime * result + ((relatedRecordFieldUuid == null) ? 0 : relatedRecordFieldUuid.hashCode());
    result = prime * result + ((relatedRecordUserFilterSort == null) ? 0 : relatedRecordUserFilterSort.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof FieldCfg))
      return false;
    FieldCfg f = (FieldCfg) obj;

    return Objects.equals(this.description, f.description) &&
      Objects.equals(this.facetLabelExpr, f.facetLabelExpr) &&
      Objects.equals(this.facetType, f.facetType) &&
      Objects.equals(this.isExclusiveFacet, f.isExclusiveFacet) &&
      Objects.equals(this.isFacet, f.isFacet) &&
      Objects.equals(this.isSearchable, f.isSearchable) &&
      Objects.equals(this.isSortable, f.isSortable) &&
      Objects.equals(this.allowMultipleSelections, f.allowMultipleSelections) &&
      Objects.equals(this.name, f.name) &&
      Objects.equals(this.sortOrderIndex, f.sortOrderIndex) &&
      Objects.equals(this.facetOrderIndex, f.facetOrderIndex) &&
      Objects.equals(this.sourceRef, f.sourceRef) &&
      Objects.equals(this.defaultOptionExpr, f.defaultOptionExpr) &&
      Objects.equals(this.visibilityExpr, f.visibilityExpr) &&
      Objects.equals(this.facetOptions, f.facetOptions) &&
      Objects.equals(this.facetExpr, f.facetExpr) &&
      Objects.equals(this.facetData, f.facetData) &&
      Objects.equals(this.uuid, f.uuid) &&
      Objects.equals(this.relatedRecordFieldUuid, f.relatedRecordFieldUuid) &&
      Objects.equals(this.relatedRecordUserFilterSort, f.relatedRecordUserFilterSort);
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

  public static abstract class FieldSortOrderComparitorBase<T extends ReadOnlyFieldCfg> implements  Comparator<T> {
    private Comparator<Integer> intComparator = Comparators.<Integer>fromComparable();
    private Comparator<String> stringComparator = Comparators.<String>fromComparable();
    @Override
    public int compare(T o1, T o2) {
      if (o1 == null && o2 == null) {
        return 0;
      }
      if (o1 == null) {
        return -1;
      }
      int c = intComparator.compare(o1.getSortOrderIndex(), o2.getSortOrderIndex());
      if (c == 0) {
        return stringComparator.compare(o1.getName(), o2.getName());
      }
      return c;
    }
  }

  public static class FieldSortOrderComparitor extends FieldSortOrderComparitorBase<FieldCfg> {}
  public static class ReadOnlyFieldSortOrderComparitor extends FieldSortOrderComparitorBase<ReadOnlyFieldCfg> {}

  public static class FieldFacetOrderComparator implements Comparator<FieldCfg> {
    private Comparator<Integer> intComparator = Comparators.<Integer>fromComparable();
    private Comparator<String> stringComparator = Comparators.<String>fromComparable();
    @Override
    public int compare(FieldCfg o1, FieldCfg o2) {
      if (o1 == null && o2 == null) {
        return 0;
      }
      if (o1 == null) {
        return -1;
      }
      int c = intComparator.compare(o1.getFacetOrderIndex(), o2.getFacetOrderIndex());
      if (c == 0) {
        return stringComparator.compare(o1.getName(), o2.getName());
      }
      return c;
    }
  }
}
