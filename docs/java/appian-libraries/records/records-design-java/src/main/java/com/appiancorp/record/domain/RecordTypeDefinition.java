package com.appiancorp.record.domain;

import static com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType.DIALOG;
import static com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType.NEW_TAB;
import static com.appiancorp.recorddocuments.RecordDocumentConstants.DOCUMENT_QNAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.appiancorp.record.service.RecordTypeCapabilitiesService;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.collections.Iterables2;
import com.appian.core.persist.Constants;
import com.appiancorp.common.query.Filter;
import com.appiancorp.common.query.ReadOnlyFilter;
import com.appiancorp.common.query.TypedValueFilter;
import com.appiancorp.config.xsd.TypeQNameUtil;
import com.appiancorp.core.expr.Expression;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.exceptions.ObjectNotFoundException;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.ConditionalBindingsType;
import com.appiancorp.ix.binding.HasConditionalVariableBindings;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.record.entities.RecordEventsCfgEntity;
import com.appiancorp.record.entities.RecordLevelSecurityCfg;
import com.appiancorp.record.entities.RecordRelationshipCfg;
import com.appiancorp.record.entities.RecordSourceCfg;
import com.appiancorp.record.entities.RecordTypeSearchCfg;
import com.appiancorp.record.recordevents.ReadOnlyRecordEventsCfg;
import com.appiancorp.record.recordtypesearch.ReadOnlyRecordTypeSearch;
import com.appiancorp.record.relatedrecords.ReadOnlyRecordRelationship;
import com.appiancorp.record.sources.ReadOnlyRecordSource;
import com.appiancorp.record.sources.ReadOnlyRecordSourceField;
import com.appiancorp.recordlevelsecurity.ReadOnlyRecordLevelSecurity;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.common.exceptions.AppianRuntimeException;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.suiteapi.common.paging.SortInfo;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.suiteapi.type.TypedValue;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.DataStoreEntityRefImpl;
import com.appiancorp.type.refs.DatatypeRefImpl;
import com.appiancorp.type.refs.ProcessModelRefImpl;
import com.appiancorp.type.refs.RecordTypeDataType;
import com.appiancorp.type.refs.RecordTypeRef;
import com.appiancorp.type.refs.RecordsDataFabricUnsyncedRefImpl;
import com.appiancorp.type.refs.RecordsReplicaDataType;
import com.appiancorp.type.refs.RecordsReplicaRefImpl;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.TaskRefImpl;
import com.appiancorp.type.refs.XmlRuleRefAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents all persisted, design-time metadata required to configure a {@link RecordType}
 * (anything that is configured in Record Type Designer).
 * RecordTypeDefinitions are cached (see {@link com.appiancorp.record.service.RecordTypeDefinitionServiceImpl})
 * and must not contain any runtime information such as evaluated expression results – all runtime information
 * should be stored on {@link RecordType}.
 *
 * RecordTypeDefinition contains setters and getters for all metadata stored in the database and the necessary
 * methods to persist and read this information.
 *
 * Every SourceType should have a CDT class, and it must be referenced in the @XmlSeeAlso annotation.
 */
@Hidden
@Entity
@Table(name = "record_type")
@XmlRootElement(name = RecordTypeDefinition.SOURCE_DATA_EXPR_BINDING_NAME, namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = RecordTypeDataType.LOCAL_PART, namespace = RecordTypeDataType.NAMESPACE, propOrder = {
    Id.LOCAL_PART, Uuid.LOCAL_PART, "name", "pluralName", "description", "urlStub", "source",
    "listViewTemplateExpr", "fieldCfgs", "detailViewCfgs", "defaultFilters", "defaultSortInfo",
    "relatedActionCfgs", "isSystem", "dataSrcExpr", "facetsListExpr", "titleExpr", "defaultFiltersExpr",
    "layoutType", "security", "opaqueId", "hideLatestNews", "hideNewsView", "hideRelatedActionsView",
    "isExportable", "listViewSrcExpr", "recordViewSrcExpr", "recordListActionCfgs", "recordRelationshipCfgs",
    "recordLevelSecurityCfg", "recordTypeSearchCfg", "iconId", "iconColorExpr", "staticIconColor",
    "listAutoRefreshInterval", "sourceConfiguration", "enabledFeatures", "isVisibleInRecordTypeList",
    "recordActionLaunchType", "showSearchBox", "recordEventsConfig", "isVisibleInDataFabric", "usesRollingSyncLimit",
    "usesRecoverySync", "isScheduledIndexingEnabled"
})
@XmlSeeAlso({ProcessModelRefImpl.class, DataStoreEntityRefImpl.class, TaskRefImpl.class,
    XmlRuleRefAdapter.RuleRefImpl.class, DatatypeRefImpl.class, RecordsReplicaRefImpl.class,
    RecordsDataFabricUnsyncedRefImpl.class})
@IgnoreJpa
@Tracked
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class RecordTypeDefinition implements ReadOnlyRecordTypeDefinition, HasAuditInfo, HasRoleMap, HasVersionHistory,
    RecordTypeRef, HasConditionalVariableBindings {

  // STATIC FIELDS ===========================================================================================

  private static final long serialVersionUID = 1L;

  //Used for IA and serialization
  public static final String SOURCE_DATA_EXPR_BINDING_NAME = "recordType";

  //DB table names/columns
  public static final String TBL_RECORD_TYPE_RM = "record_type_rm";
  public static final String JOIN_COL_RECORD_TYPE_ID = "record_type_id";
  public static final String PROP_RECORD_EVENTS_CONFIG = "recordEventsConfig";
  public static final String PROP_ROLE_MAP_ENTRIES = "roleMapEntries";
  public static final String PROP_ROLE_MAP_ENTRY = "roleMapEntry";
  public static final String PROP_RECORD_LEVEL_SECURITY_CFG = "recordLevelSecurityCfg";


  /* Number of record actions and number of record views are weighted twice as heavily as number of
  related records when determining importance score */
  private static final int IMPORTANCE_WEIGHT = 2;

  private static final String[] exprNames = new String[]{
          "dataSrcExpr",
          "facetsListExpr",
          "titleExpr",
          "defaultFiltersExpr",
          "listViewSrcExpr",
          "recordViewSrcExpr",
          "iconColorExpr",
          "listViewTemplateExpr"
  };

  // IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.RECORD_TYPE_ADMIN,
      Roles.RECORD_TYPE_EDITOR,
      Roles.RECORD_TYPE_AUDITOR,
      Roles.RECORD_TYPE_VIEWER,
      Roles.RECORD_TYPE_PROD_DATA_STEWARD,
      Roles.RECORD_TYPE_DATA_STEWARD
  );

  /**
   * Used to identify users with DATA_STEWARD role access.
   */
  public static final ImmutableSet<Role> ALL_DATA_STEWARD_ROLES = ImmutableSet.of(
      Roles.RECORD_TYPE_PROD_DATA_STEWARD,
      Roles.RECORD_TYPE_DATA_STEWARD
  );


  // NON-STATIC FIELDS (IN ALPHABETICAL ORDER) ===============================================================
  // Every class used here must be serializable! =============================================================
  // This recursively includes the fields of those classes, all the way down the class reference tree. =======

  private AuditInfo auditInfo = new AuditInfo();
  private String dataSrcExpr;
  private List<Filter<TypedValue>> defaultFilters = new ArrayList<>();
  private String defaultFiltersExpr;

  @SuppressFBWarnings("SE_BAD_FIELD")
  private SortInfo defaultSortInfo;
  private String description;
  private List<DetailViewCfg> detailViewCfgs = new ArrayList<>();
  private long enabledFeatures;
  private String facetsListExpr;
  private Set<FieldCfg> fieldCfgs = new LinkedHashSet<>();
  private boolean hideLatestNews = false;
  private boolean hideNewsView = false;
  private boolean hideRelatedActionsView = false;
  private String iconColor;
  private RecordTypePropertySource iconColorSource = RecordTypePropertySource.DEFAULT;
  private String iconId;
  private RecordTypePropertySource iconIdSource = RecordTypePropertySource.DEFAULT;
  private Long id;
  private int importanceScore;
  private boolean isExportable = true;
  private boolean isSystem = false;
  private boolean isVisibleInRecordTypeList = true;
  private boolean isVisibleInDataFabric = false;
  private RecordLayoutConfig layoutConfigType;
  private double listAutoRefreshInterval;
  private String listViewSrcExpr;
  private String listViewTemplateExpr;
  private String name;
  private boolean needsLockValidationOnUpdate = true;
  private String opaqueId;
  private String pluralName;
  private RecordActionLaunchType recordActionLaunchType;
  private List<RecordListActionCfg> recordListActionCfgs = new ArrayList<>();
  private List<RecordRelationshipCfg> recordRelationshipCfgs = new ArrayList<>();
  private Collection<RecordLevelSecurityCfg> recordLevelSecurityCfg = new ArrayList<>();
  private List<RecordTypeSearchCfg> recordTypeSearchCfg = new ArrayList<>();
  private String recordViewSrcExpr;
  private List<RelatedActionCfg> relatedActionCfgs = new ArrayList<>();
  private Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private short security = RecordTypeSecurity.getDefaultValidSecurity();
  private boolean showSearchBox = true;
  private List<RecordSourceCfg> sourceCfgs = new ArrayList<>();
  private String sourceTypeStr;
  private String sourceUuidStr;
  private String titleExpr;
  private String urlStub;
  private String uuid;
  private String versionUuid;
  private RecordEventsCfgEntity recordEventsConfig;
  private boolean usesRollingSyncLimit = false;
  private boolean usesRecoverySync = false;
  private boolean isScheduledIndexingEnabled = false;


  // TRANSIENT FIELDS ========================================================================================

  /**
   * Used during Jaxb serialization during export
   * When the object is retrieved by Hibernate, fieldCfgs is a PersistentSet, which is backed
   * by a HashSet. The {@link #beforeMarshal} call saves the original {@link #fieldCfgs} and replaces it with a
   * LinkedHashSet. The {@link #afterMarshal} call restores {@link #fieldCfgs} with this value
   */
  private transient Set<FieldCfg> persistedFieldCfgs;

  private transient ExpressionTransformationState listViewExpressionState = ExpressionTransformationState.STORED;
  private transient ExpressionTransformationState otherExpressionState = ExpressionTransformationState.STORED;

  // Setters/Getters directly backed by DB columns ===========================================================

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid;
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
  @XmlTransient
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  @Override
  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Override
  @Column(name = "plural_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getPluralName() {
    return pluralName;
  }

  public void setPluralName(String pluralName) {
    this.pluralName = pluralName;
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
  @Column(name = "is_system", nullable = false)
  public boolean getIsSystem() {
    return isSystem;
  }

  public void setIsSystem(boolean isSystem) {
    this.isSystem = isSystem;
  }

  @Override
  @Column(name = "is_exportable", nullable = false)
  public boolean getIsExportable() {
    return isExportable;
  }

  public void setIsExportable(boolean isExportable) {
    this.isExportable = isExportable;
  }

  /**
   * A unique URL component that can be used to reference instances of this
   * RecordTypeDefinition
   */
  @Override
  @Column(name = "url_stub", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @Override
  @Column(name = "icon_id")
  public String getIconId() {
    return iconId;
  }

  public void setIconId(String iconId) {
    this.iconId = iconId;

    if (!RecordTypePropertySource.STATIC.equals(iconIdSource) && !Strings.isNullOrEmpty(iconId)) {
      setIconIdSource(RecordTypePropertySource.STATIC);
    }
  }

  @Override
  @Lob
  @Column(name = "icon_color")
  @XmlTransient
  public String getIconColor() {
    return iconColor;
  }

  public void setIconColor(String iconColor) {
    this.iconColor = iconColor;
  }

  @Override
  @Column(name = "list_auto_refresh_interval", nullable = false)
  public double getListAutoRefreshInterval() {
    return listAutoRefreshInterval;
  }

  public void setListAutoRefreshInterval(double listAutoRefreshInterval) {
    this.listAutoRefreshInterval = listAutoRefreshInterval;
  }

  @Override
  @Column(name = "data_src_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb= BreadcrumbText.recordTypeDataSourceExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDataSrcExpr() {
    return dataSrcExpr;
  }

  public void setDataSrcExpr(String dataSrcExpr) {
    this.dataSrcExpr = dataSrcExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getDataSrcExpression() {
    return Expression.of(dataSrcExpr, otherExpressionState);
  }

  @Override
  @Column(name = "list_view_src_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeListViewSourceExpression)
  @ForeignKeyCustomBinder(CustomBinderType.LIST_VIEW_SOURCE_EXPRESSION)
  public String getListViewSrcExpr() {
    return listViewSrcExpr;
  }

  public void setListViewSrcExpr(String listViewSrcExpr) {
    this.listViewSrcExpr = listViewSrcExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getListViewSrcExpression() {
    return Expression.of(listViewSrcExpr, otherExpressionState);
  }

  @Override
  @Column(name = "record_view_src_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeRecordViewSourceExpression)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_VIEW_SOURCE_SOURCE_EXPRESSION)
  public String getRecordViewSrcExpr() {
    return recordViewSrcExpr;
  }

  public void setRecordViewSrcExpr(String recordViewSrcExpr) {
    this.recordViewSrcExpr = recordViewSrcExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getRecordViewSrcExpression() {
    return Expression.of(recordViewSrcExpr, otherExpressionState);
  }

  @Override
  @Column(name = "facets_list_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetsExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getFacetsListExpr() {
    return facetsListExpr;
  }

  public void setFacetsListExpr(String facetsListExpr) {
    this.facetsListExpr = facetsListExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getFacetsListExpression() {
    return Expression.of(facetsListExpr, otherExpressionState);
  }

  @Override
  @Column(name = "default_filters_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeDefaultFiltersExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDefaultFiltersExpr() {
    return defaultFiltersExpr;
  }

  public void setDefaultFiltersExpr(String defaultFiltersExpr) {
    this.defaultFiltersExpr = defaultFiltersExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getDefaultFiltersExpression() {
    return Expression.of(defaultFiltersExpr, otherExpressionState);
  }

  @Column(name = "layout_cfg", nullable = false)
  private byte getLayoutConfig() {
    return getLayoutType().getCode();
  }

  private void setLayoutConfig(byte layoutConfig) {
    setLayoutType(RecordLayoutConfig.valueOf(layoutConfig));
  }

  @Column(name = "icon_id_src", nullable = false)
  private byte getIconIdSourceByte() {
    return getIconIdSource().getCode();
  }

  private void setIconIdSourceByte(byte sourceByte) {
    setIconIdSource(RecordTypePropertySource.valueOf(sourceByte));
  }

  @Column(name = "icon_color_src", nullable = false)
  private byte getIconColorSourceByte() {
    return getIconColorSource().getCode();
  }

  private void setIconColorSourceByte(byte sourceByte) {
    setIconColorSource(RecordTypePropertySource.valueOf(sourceByte));
  }

  /**
   * Use {@link RecordTypeCapabilitiesService#isReplicaEnabled(SupportsReadOnlyReplicatedRecordType)} instead
   */
  @Override
  @Transient
  @Deprecated
  public boolean getIsReplicaEnabled() {
    return RecordsReplicaDataType.QNAME.equals(getSourceType());
  }

  @Override
  @Column(name = "src_type", nullable = true)
  public String getSourceTypeStr() {
    return sourceTypeStr;
  }

  @SuppressWarnings("unused")
  void setSourceTypeStr(String sourceTypeStr) {
    this.sourceTypeStr = sourceTypeStr;
  }

  @Column(name = "src_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  String getSourceUuidStr() {
    return sourceUuidStr;
  }

  @SuppressWarnings("unused")
  void setSourceUuidStr(String sourceUuidStr) {
    this.sourceUuidStr = sourceUuidStr;
  }

  @Override
  @Column(name = "lv_tmpt_expr", nullable = true, length = Constants.COL_MAXLEN_GENERATED_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeListViewTemplateExpr,
    prependedBreadcrumbs = {BreadcrumbText.recordTypeListView},
    variableBindings = VariableBindings.FEED_CONDITIONAL_RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getListViewTemplateExpr() {
    return listViewTemplateExpr;
  }

  public void setListViewTemplateExpr(String listViewTemplateExpr) {
    this.listViewTemplateExpr = listViewTemplateExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getListViewTemplateExpression() {
    return Expression.of(listViewTemplateExpr, listViewExpressionState);
  }

  @Override
  @Column(name = "instance_title_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeInstanceTitleExpression, variableBindings = VariableBindings.RECORD_TYPE,
    prependedBreadcrumbs = {BreadcrumbText.recordTypeDetailViewCfg, BreadcrumbText.recordTypeDetailViewHeaderExpr},
    breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_CHILDREN)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getTitleExpr() {
    return titleExpr;
  }

  public void setTitleExpr(String titleExpr) {
    this.titleExpr = titleExpr;
  }

  @Override
  @Transient
  @XmlTransient
  public Expression getTitleExpression() {
    return Expression.of(titleExpr, otherExpressionState);
  }

  @Override
  @Column(name = "security", nullable = false)
  public short getSecurity() {
    return security;
  }

  public void setSecurity(short security) {
    this.security = security;
  }

  @Override
  @Column(name = "hide_latest_news")
  public boolean getHideLatestNews() {
    return hideLatestNews;
  }

  public void setHideLatestNews(boolean hideLatestNews) {
    this.hideLatestNews = hideLatestNews;
  }

  @Override
  @Column(name = "hide_news_view")
  public boolean getHideNewsView() {
    return hideNewsView;
  }

  public void setHideNewsView(boolean hideNewsView) {
    this.hideNewsView = hideNewsView;
  }

  @Override
  @Column(name = "hide_related_actions_view")
  public boolean getHideRelatedActionsView() {
    return hideRelatedActionsView;
  }

  public void setHideRelatedActionsView(boolean hideRelatedActionsView) {
    this.hideRelatedActionsView = hideRelatedActionsView;
  }

  @Override
  @Column(name = "enabled_features")
  public long getEnabledFeatures() {
    return enabledFeatures;
  }

  public void setEnabledFeatures(long enabledFeatures) {
    this.enabledFeatures = enabledFeatures;
  }

  @Override
  @Column(name = "is_visible_in_record_type_list", nullable = false)
  public boolean getIsVisibleInRecordTypeList() {
    return isVisibleInRecordTypeList;
  }

  public void setIsVisibleInRecordTypeList(boolean isVisibleInRecordTypeList) {
    this.isVisibleInRecordTypeList = isVisibleInRecordTypeList;
  }

  @Override
  @Column(name = "is_visible_in_data_fabric", nullable = false)
  public boolean getIsVisibleInDataFabric() {
    return isVisibleInDataFabric;
  }

  public void setIsVisibleInDataFabric(boolean isVisibleInDataFabric) {
    this.isVisibleInDataFabric = isVisibleInDataFabric;
  }

  @Override
  @Column(name = "show_search_box", nullable = false)
  public boolean getShowSearchBox() {
    return showSearchBox;
  }

  public void setShowSearchBox(boolean showSearchBox) {
    this.showSearchBox = showSearchBox;
  }

  @Override
  @Transient
  public RecordActionLaunchType getRecordActionLaunchType() {
    return recordActionLaunchType;
  }

  public void setRecordActionLaunchType(RecordActionLaunchType recordActionLaunchType) {
    this.recordActionLaunchType = recordActionLaunchType;
  }

  @Column(name = "record_action_launch_type", nullable = false)
  private byte getRecordActionLaunchTypeByte() {
    if (recordActionLaunchType == null) {
      return 0;
    }

    switch (recordActionLaunchType) {
    case DIALOG:
      return 2;
    case NEW_TAB:
      return 1;
    case SAME_TAB:
    default:
      return 0;
    }
  }

  private void setRecordActionLaunchTypeByte(byte index) {
    RecordActionLaunchType recordActionLaunchType = DEFAULT_RECORD_ACTION_LAUNCH_TYPE;

    if (index == 2) {
      recordActionLaunchType = DIALOG;
    } else if (index == 1) {
      recordActionLaunchType = NEW_TAB;
    }

    setRecordActionLaunchType(recordActionLaunchType);
  }

  @Override
  @Column(name = "uses_rolling_sync_limit", nullable = false)
  public boolean getUsesRollingSyncLimit() {
    return usesRollingSyncLimit;
  }

  public void setUsesRollingSyncLimit(boolean usesRollingSyncLimit) {
    this.usesRollingSyncLimit = usesRollingSyncLimit;
  }

  @Override
  @Column(name = "uses_recovery_sync", nullable = false)
  public boolean getUsesRecoverySync() {
    return usesRecoverySync;
  }

  public void setUsesRecoverySync(boolean usesRecoverySync) {
    this.usesRecoverySync = usesRecoverySync;
  }

  @Override
  @Column(name = "is_scheduled_idx_enabled", nullable = false)
  public boolean getIsScheduledIndexingEnabled() {
    return isScheduledIndexingEnabled;
  }

  public void setIsScheduledIndexingEnabled(boolean isScheduledIndexingEnabled) {
    this.isScheduledIndexingEnabled = isScheduledIndexingEnabled;
  }

  // Setters/Getters for fields joined with other tables =====================================================

  @XmlElement(name = "recordEventsConfig")
  @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
  @JoinColumn(name="record_events_config_id", nullable = true)
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeEventsRef)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_EVENTS_CFG)
  public RecordEventsCfgEntity getRecordEventsConfig(){
    return recordEventsConfig;
  }

  public void setRecordEventsConfig(RecordEventsCfgEntity recordEventsConfig){
    this.recordEventsConfig = recordEventsConfig;
  }

  @XmlElement(name = "fieldCfg")
  @OneToMany(mappedBy = "recordType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeFieldCfg)
  public Set<FieldCfg> getFieldCfgs() {
    return fieldCfgs;
  }

  @Override
  @Transient
  public ImmutableSet<ReadOnlyFieldCfg> getFieldCfgsReadOnly() {
    return ImmutableSet.copyOf(getFieldCfgs());
  }

  @VisibleForTesting
  public void setFieldCfgs(Set<FieldCfg> fieldCfgs) {
    this.fieldCfgs = fieldCfgs;
  }

  @XmlElement(name = "detailViewCfg")
  @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeDetailViewCfg)
  @JoinColumn(name="record_type_id", nullable=false)
  @OrderColumn(name="order_idx", nullable=false)
  public List<DetailViewCfg> getDetailViewCfgs() {
    return detailViewCfgs;
  }

  public void setDetailViewCfgs(List<DetailViewCfg> detailViewCfgs) {
    this.detailViewCfgs = detailViewCfgs;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyDetailViewCfg> getDetailViewCfgsReadOnly() {
    return ImmutableList.copyOf(getDetailViewCfgs());
  }

  @Override
  @Transient
  @XmlTransient
  public ReadOnlyRecordEventsCfg getRecordEventsCfgReadOnly() {
    return getRecordEventsConfig();
  }

  @Override
  public Optional<ReadOnlyRecordRelationship> getReadOnlyRecordRelationshipByUuid(String uuid) {
    if (uuid == null || uuid.isEmpty()) {
      return Optional.empty();
    }
    return getRecordRelationshipCfgsReadOnly().stream()
        .filter(relationship -> uuid.equals(relationship.getUuid()))
        .findFirst();
  }

  @XmlElement(name = "defaultFilter", type= TypedValueFilter.class)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeDefaultFilterFormat,
      breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_PEERS)
  @ForeignKeyCustomBinder(CustomBinderType.FILTER_LIST)
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity=TypedValueFilter.class)
  @JoinTable(name = "record_type_filters", joinColumns = @JoinColumn(name = "record_type_id"), inverseJoinColumns = @JoinColumn(name = "filter_id"))
  @OrderColumn(name = "order_idx", nullable = false)
  public List<Filter<TypedValue>> getDefaultFilters() {
    return defaultFilters;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyFilter> getDefaultFiltersReadOnly() {
    List<Filter<TypedValue>> defaultFiltersEditable = getDefaultFilters();
    return defaultFiltersEditable == null ? ImmutableList.of() : ImmutableList.copyOf(getDefaultFilters());
  }

  public void setDefaultFilters(List<Filter<TypedValue>> defaultFilters) {
    this.defaultFilters = defaultFilters;
  }

  @XmlElement(name = "defaultSortInfo")
  @OneToOne(optional=true, cascade={CascadeType.ALL})
  @JoinColumn(name="default_sort_info_id")
  public SortInfo getDefaultSortInfo(){
    return defaultSortInfo;
  }

  public void setDefaultSortInfo(SortInfo defaultSortInfo){
    this.defaultSortInfo = defaultSortInfo;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFeedListDefaultSort,
      prependedBreadcrumbs={BreadcrumbText.recordTypeListView, BreadcrumbText.recordTypeListViewTemplateExpr})
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_FIELD_QUERY_INFO_LIST)
  public List<String> getDefaultSortInfoAsQueryInfos() {
    return new ArrayList<String>() {{
      if (defaultSortInfo != null) {
        add(defaultSortInfo.getField());
      }
    }};
  }

  @XmlElement(name = "relatedActionCfg")
  @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
  @HasForeignKeys(breadcrumb = BreadcrumbText.SKIP, prependedBreadcrumbs = BreadcrumbText.recordActions)
  @JoinColumn(name="record_type_id", nullable=false)
  @OrderColumn(name="order_idx", nullable=false)
  @BatchSize(size = 100)
  public List<RelatedActionCfg> getRelatedActionCfgs() {
    return relatedActionCfgs;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRelatedAction> getRelatedActionCfgsReadOnly() {
    return ImmutableList.copyOf(getRelatedActionCfgs());
  }

  public void setRelatedActionCfgs(List<RelatedActionCfg> relatedActionCfgs) {
    this.relatedActionCfgs = relatedActionCfgs;
  }

  @XmlElement(name = "recordListActionCfg")
  @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.SKIP,
      prependedBreadcrumbs = BreadcrumbText.recordActions)
  @JoinColumn(name="record_type_id", nullable=false)
  @OrderColumn(name="order_idx", nullable=false)
  @BatchSize(size = 100)
  public List<RecordListActionCfg> getRecordListActionCfgs() {
    return recordListActionCfgs;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordAction> getRecordListActionCfgsReadOnly() {
    return ImmutableList.copyOf(getRecordListActionCfgs());
  }

  public void setRecordListActionCfgs(List<RecordListActionCfg> recordListActionCfgs) {
    this.recordListActionCfgs = recordListActionCfgs;
  }

  @XmlElement(name = "recordRelationshipCfg")
  @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeRelationshipCfg)
  @JoinColumn(name = "src_rt_id", nullable = false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<RecordRelationshipCfg> getRecordRelationshipCfgs() {
    return recordRelationshipCfgs;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordRelationship> getRecordRelationshipCfgsReadOnly() {
    return ImmutableList.copyOf(getRecordRelationshipCfgs());
  }

  public void setRecordRelationshipCfgs(List<RecordRelationshipCfg> recordRelationshipCfgs) {
    this.recordRelationshipCfgs = recordRelationshipCfgs;
  }

  @Override
  public ReadOnlyRecordRelationship getRecordRelationship(String uuid) {
    return getRecordRelationshipCfgsReadOnly().stream()
        .filter(relationship -> uuid.equals(relationship.getUuid()))
        .findFirst()
        .orElse(null);
  }

  @XmlElement(name = "recordTypeSearchCfg")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeSearchCfg, prependedBreadcrumbs = {BreadcrumbText.recordTypeFilters})
  @JoinColumn(name = "rt_id", nullable = false)
  public List<RecordTypeSearchCfg> getRecordTypeSearchCfg() {
    return recordTypeSearchCfg;
  }

  @Override
  @Transient
  public ImmutableList<ReadOnlyRecordTypeSearch> getRecordTypeSearchCfgReadOnly() {
    return ImmutableList.copyOf(getRecordTypeSearchCfg());
  }

  @VisibleForTesting
  public void setRecordTypeSearchCfg(List<RecordTypeSearchCfg> recordTypeSearchCfg) {
    this.recordTypeSearchCfg = recordTypeSearchCfg;
  }

  @XmlElement(name = "recordRowLevelSecurityCfg")
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeViewerCfg)
  @JoinColumn(name = "rt_id", nullable = false)
  @BatchSize(size = 100)
  public Collection<RecordLevelSecurityCfg> getRecordLevelSecurityCfg() {
    return recordLevelSecurityCfg;
  }

  @Transient
  @XmlTransient
  public List<RecordLevelSecurityCfg> getRecordLevelSecurityCfgOrderByOrderIdx() {
    if (recordLevelSecurityCfg == null) {
      return null;
    }
    return recordLevelSecurityCfg.stream()
        .sorted(Comparator.comparingLong(RecordLevelSecurityCfg::getOrderIdx))
        .collect(Collectors.toList());
  }

  public void setRecordLevelSecurityCfg(Collection<RecordLevelSecurityCfg> recordLevelSecurityCfg) {
    this.recordLevelSecurityCfg = recordLevelSecurityCfg;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordLevelSecurity> getRecordLevelSecurityCfgsReadOnly() {
    return ImmutableList.copyOf(getRecordLevelSecurityCfgOrderByOrderIdx());
  }

  @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
  @JoinTable(name=RecordTypeDefinition.TBL_RECORD_TYPE_RM,
      joinColumns=@JoinColumn(name=RecordTypeDefinition.JOIN_COL_RECORD_TYPE_ID),
      inverseJoinColumns=@JoinColumn(name=RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  @XmlTransient
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  // Convenience getters/setters =============================================================================

  @Override
  @Transient
  @XmlElement
  public RecordLayoutConfig getLayoutType() {
    return layoutConfigType;
  }

  public void setLayoutType(RecordLayoutConfig layoutType){
    layoutConfigType = layoutType;
  }

  @Override
  @Transient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeIconColorExpression,
    prependedBreadcrumbs = {BreadcrumbText.recordTypeTempoView})
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getIconColorExpr() {
    return RecordTypePropertySource.EXPRESSION.equals(iconColorSource) ? this.iconColor : null;
  }

  public void setIconColorExpr(String iconColorExpr) {
    if (iconColorExpr != null) {
      setIconColor(iconColorExpr);
    }

    if (!RecordTypePropertySource.EXPRESSION.equals(iconColorSource) && !Strings.isNullOrEmpty(iconColorExpr)) {
      setIconColorSource(RecordTypePropertySource.EXPRESSION);
    }
  }

  @Override
  @Transient
  @XmlTransient
  public QName getSourceType() {
    return sourceTypeStr == null ? null : QName.valueOf(sourceTypeStr);
  }

  public void setSourceType(QName sourceType) {
    this.sourceTypeStr = sourceType == null ? null : sourceType.toString();
  }

  @Override
  @Transient
  public String getSourceUuid() {
    return sourceUuidStr;
  }

  <T> void setSourceUuid(T sourceUuid) {
    if (RecordTypeCapabilitiesService.isDataFabric(this)) {
      return;
    }
    Preconditions.checkNotNull(sourceUuid, "Uuid is required");
    if (sourceUuid instanceof String) {
      if (RecordTypeType.ExpressionBacked.equals(getType()) && ((String)sourceUuid).contains("^")) {
        throw new IllegalArgumentException("Uuid's that represent outdated types are not allowed for expression-backed records");
      }
      this.sourceUuidStr = (String) sourceUuid;
    } else {
      throw new UnsupportedOperationException(
          "Uuid type is not supported " + sourceUuid.getClass()
              .getSimpleName());
    }
  }

  @XmlElement(type = Object.class)
  @Transient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeSource)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<?,String> getSource() {
    QName qName = getSourceType();
    if (qName == null) {
      return null;
    }
    return getSourceRef(qName, getSourceUuid());
  }

  public void setSource(Ref<?,String> source) {
    if (source == null) {
      return;
    }
    if (!RecordTypeType.isSupportedSource(source)) {
      throw new IllegalArgumentException("Unsupported datasource type " +
          source.getClass().getName());
    }
    QName qName = TypeQNameUtil.getQName(source.getClass());
    setSourceType(qName);
    setSourceUuid(source.getUuid());
  }

  @Override
  @Transient
  public RecordTypeType getType() {
    QName qName = getSourceType();
    return RecordTypeType.getTypeByQName(qName);
  }

  @Override
  @Transient
  @XmlTransient
  public RecordTypePropertySource getIconIdSource() {
    return iconIdSource;
  }

  public void setIconIdSource(RecordTypePropertySource iconIdSource) {
    this.iconIdSource = iconIdSource;
  }

  @Override
  @Transient
  @XmlTransient
  public RecordTypePropertySource getIconColorSource() {
    return iconColorSource;
  }

  public void setIconColorSource(RecordTypePropertySource iconColorSource) {
    this.iconColorSource = iconColorSource;
  }

  // Required for HasAuditInfo ===============================================================================

  // Marked XmlTransient because we need to implement binding between k/rdbms
  // userIds before this can be serialized. Unfinished impl in
  // AuditInfoPrimaryDsBinder. When this annotation is removed, add auditInfo
  // back into propOrder above and un-ignore test in RecordTypeTest
  @Override
  @Embedded
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  // Required for HasRoleMap =================================================================================

  @Override
  @XmlTransient
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  /**
   * <p>Returns {@link RoleMap} without  {@link Roles#RECORD_TYPE_PROD_DATA_STEWARD}.</p>
   *
   * @return {@link RoleMap}
   * @see #getRoleMap(boolean)
   */
  @Override
  @Transient
  @XmlTransient
  public RoleMap getRoleMap() {
    return getRoleMapBasedOnProdDataSteward(false);
  }

  /**
   * <p>Returns {@link RoleMap} with all {@link Role}s when <tt>withSpecialRoles</tt> is <tt>true</tt>;
   * otherwise, it returns {@link RoleMap} without {@link Roles#RECORD_TYPE_PROD_DATA_STEWARD}
   * when <tt>withSpecialRoles</tt>  is <tt>false</tt>.</p>
   *
   * @param withSpecialRoles <tt>true</tt> indicates if the returned {@link Role}
   *                         includes the special {@link Roles#RECORD_TYPE_PROD_DATA_STEWARD Role}.
   * @return {@link RoleMap}
   * @see #getRoleMap()
   */

  @Override
  public RoleMap getRoleMap(boolean withSpecialRoles) {
    return getRoleMapBasedOnProdDataSteward(withSpecialRoles);
  }

  private RoleMap getRoleMapBasedOnProdDataSteward(boolean includeProdDataStewardRole) {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      if (Roles.RECORD_TYPE_AUDITOR.equals(roleMapEntry.getRole())) {
        roleMapBuilder.users(Roles.RECORD_TYPE_VIEWER, roleMapEntry.getUsers());
        roleMapBuilder.groups(Roles.RECORD_TYPE_VIEWER, roleMapEntry.getGroups());
      } else if (includeProdDataStewardRole || !Roles.RECORD_TYPE_PROD_DATA_STEWARD.equals(roleMapEntry.getRole())) {
        roleMapBuilder.entries(roleMapEntry);
      }
    }
    return roleMapBuilder.build();
  }

  @Override
  public void setRoleMap(RoleMap roleMap) {
    if (this.roleMapEntries != null) {
      this.roleMapEntries.clear();
    }
    if (roleMap != null) {
      if (this.roleMapEntries == null) {
        this.roleMapEntries = new HashSet<>();
      }
      this.roleMapEntries.addAll(roleMap.getEntriesByRole().values());
    }
  }

  @Override
  public void discardRoleMap() {
    this.roleMapEntries = new PersistentSet(); // This tells Hibernate to ignore this field during the update.
  }

  @Override
  @Transient
  @XmlTransient
  public boolean isPublic() {
    return RecordTypeSecurity.isSupported(RecordTypeSecurity.RecordTypeSecurityFlags.PUBLIC, getSecurity());
  }

  @Override
  public void setPublic(boolean isPublic) {
    short security = RecordTypeSecurity.builder(getSecurity())
        .setPublic(isPublic)
        .build();
    setSecurity(security);
  }

  @Override
  @Transient
  @XmlTransient
  public String getFallbackRoleName() {
    return Roles.RECORD_TYPE_VIEWER.getName();
  }

  // Required for IX/IA ======================================================================================

  @Override
  @Transient
  public String getOpaqueId() {
    return opaqueId;
  }

  public void setOpaqueId(String opaqueId) {
    this.opaqueId = opaqueId;
  }

  public static Ref<?,String> getSourceRef(QName qName, String uuid) {
    RecordTypeType rtt = RecordTypeType.getTypeByQName(qName);
    return switch (rtt) {
      case EntityBacked -> new DataStoreEntityRefImpl(null, uuid);
      case ProcessBacked -> new ProcessModelRefImpl(uuid);
      case RuleBacked -> new XmlRuleRefAdapter.RuleRefImpl(uuid);
      case TaskBacked -> new TaskRefImpl(uuid);
      case ExpressionBacked -> new DatatypeRefImpl(uuid);
      case ReplicaBacked -> new RecordsReplicaRefImpl();
      case RecordsDataFabricUnsynced -> new RecordsDataFabricUnsyncedRefImpl();
      default -> throw new IllegalArgumentException("Invalid Record Type Ref");
    };
  }

  // Required by HasTypeQName ================================================================================

  @Override
  @XmlTransient
  @Transient
  public QName getTypeQName() {
    return RecordTypeInfo.QNAME;
  }

  // Object toString method ==================================================================================

  @Override
  public String toString() {
    return "RecordType [id=" + id + ", uuid=" + uuid + ", name=" + name
        + ", sourceTypeStr=" + sourceTypeStr + ", sourceUuidStr="
        + sourceUuidStr + "]";
  }

  @XmlTransient
  @Transient
  @Override
  public ExpressionTransformationState getListViewExpressionState() {
    return listViewExpressionState;
  }

  public void setListViewExpressionState(ExpressionTransformationState state) {
    this.listViewExpressionState = state;
  }

  @XmlTransient
  @Transient
  @Override
  public ExpressionTransformationState getOtherExpressionState() {
    return otherExpressionState;
  }

  public void setOtherExpressionState(ExpressionTransformationState state) {
    this.otherExpressionState = state;
  }

  @XmlTransient
  @Transient
  @Override
  public Optional<Map<String, ExpressionTransformationState>> getExpressionStates() {
    Map<String, ExpressionTransformationState> states = new HashMap<>();
    for (String name : exprNames) {
      states.put(name, otherExpressionState);
    }
    states.put("listViewTemplateExpr", listViewExpressionState);
    return Optional.of(states);
  }

  // Persistence, marshalling, and unmarshalling =============================================================

  @Override
  public Ref<Long,String> build(Long id, String uuid) {
    RecordTypeDefinition definition = new RecordTypeDefinition();
    definition.setId(id);
    definition.setUuid(uuid);
    return definition;
  }

  @PrePersist
  void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  private Object readResolve() {
    this.listViewExpressionState = ExpressionTransformationState.STORED;
    this.otherExpressionState = ExpressionTransformationState.STORED;
    return this;
  }

  /**
   * Sort fieldCfgs before serializing to XML so elements are
   * not shuffled during Import/Export. Set layoutConfig for import/export.
   */
  void beforeMarshal(Marshaller m) {
    persistedFieldCfgs = fieldCfgs;
    LinkedHashSet<FieldCfg> cfgs = Sets.newLinkedHashSet(getFieldCfgsOrdered(new FieldCfg.FieldFacetOrderComparator()));
    setFieldCfgs(cfgs);
  }

  @Transient
  public List<FieldCfg> getFieldCfgsOrdered(Comparator<FieldCfg> comparator) {
    return getFieldCfgsOrdered0(comparator);
  }

  @Override
  public ImmutableList<ReadOnlyFieldCfg> getFieldCfgsOrderedReadOnly(Comparator<ReadOnlyFieldCfg> comparator) {
    return ImmutableList.copyOf(getFieldCfgsOrdered0(comparator));
  }

  private <F extends ReadOnlyFieldCfg> List<F> getFieldCfgsOrdered0(Comparator<F> comparator) {
    List<F> fds = new ArrayList<>((Collection<F>)getFieldCfgs());
    Collections.sort(fds, comparator);
    return fds;
  }

  /*
   * Note that this method is private and stay as such. It's only for hibernate.
   *
   * Hibernate does not allow us to use a OneToOne mapping in combination with JoinColumn's
   * "referencedColumnName" attribute. This means for us to leave the foreign key on the RecordSourceCfg side
   * of the relationship, we're leaving this as a OneToMany relationship. In the future, assuming we support
   * multiple record sources, this will be needed anyway.
   */
  @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "record_type_id", nullable = false)
  @OrderColumn(name="order_idx", nullable = false)
  @XmlTransient
  private List<RecordSourceCfg> getSourceCfgs() {
    return sourceCfgs;
  }

  private void setSourceCfgs(List<RecordSourceCfg> sourceCfgs) {
    this.sourceCfgs = sourceCfgs;
  }

  @Override
  @Transient
  @HasForeignKeys(breadcrumb=BreadcrumbText.SKIP)
  public RecordSourceCfg getSourceConfiguration() {
    return sourceCfgs == null || sourceCfgs.isEmpty() ? null : sourceCfgs.get(0);
  }

  @Override
  @Transient
  @HasForeignKeys(breadcrumb=BreadcrumbText.SKIP)
  public ReadOnlyRecordSource getReadOnlyRecordSource() {
    return getSourceConfiguration();
  }

  public void setSourceConfiguration(RecordSourceCfg sourceCfg) {
    sourceCfgs.clear();
    if (sourceCfg != null) {
      sourceCfgs.add(sourceCfg);
    }
  }

  @Transient
  @XmlTransient
  public List<ReadOnlyRecordSourceField> getRecordFields() {
    RecordSourceCfg sourceCfg = this.getSourceConfiguration();
    if (sourceCfg == null) {
      return Collections.emptyList();
    }

    return sourceCfg.getSourceAndCustomFieldsReadOnly();
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordSourceField> getRecordFieldsReadOnly() {
    return ImmutableList.copyOf(getRecordFields());
  }

  @Override
  public Optional<ReadOnlyRecordSourceField> getReadOnlyRecordFieldByUuid(String uuid) {
    return getRecordFieldsReadOnly().stream()
        .filter(recordField -> uuid.equals(recordField.getUuid()))
        .findFirst();
  }

  @VisibleForTesting
  public ReadOnlyRecordSourceField getRecordFieldByName(String name) {
    return getRecordFields().stream()
        .filter(recordField -> name.equals(recordField.getFieldName()))
        .findFirst()
        .orElseThrow(() ->
            new IllegalArgumentException(String.format("Target RecordSourceField[name=%s] does not exist on RecordType[uuid=%s]", name, getUuid())));
  }

  @Override
  @Transient
  @XmlTransient
  public ReadOnlyRecordSourceField getRecordIdSourceField() {
    return getRecordFields().stream()
        .filter(ReadOnlyRecordSourceField::getIsRecordId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No record ID found for Record Type"));
  }

  @SuppressWarnings("unused")
  void afterMarshal(Marshaller m) {
    fieldCfgs = persistedFieldCfgs;
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller u, Object parent) {
    for (FieldCfg fd : getFieldCfgs()) {
      fd.setRecordType(this);
    }
    if (getSourceType() == null && getIsReplicaEnabled()) {
      setSourceType(RecordsReplicaDataType.QNAME);
      setSourceUuid(null);
    }
  }

  public void setNeedsLockValidationOnUpdate(boolean checkLockOnUpdate) {
    this.needsLockValidationOnUpdate = checkLockOnUpdate;
  }

  @Override
  public boolean needsLockValidationOnUpdate() {
    return needsLockValidationOnUpdate;
  }

  // General helper methods ==================================================================================

  @Override
  @Transient
  public String getStaticIconColor() {
    return RecordTypePropertySource.STATIC.equals(getIconColorSource()) ? this.getIconColor() : null;
  }

  public void setStaticIconColor(String staticIconColor) {
    if (staticIconColor != null) {
      setIconColor(staticIconColor);
    }

    if (!RecordTypePropertySource.STATIC.equals(getIconColorSource()) && !Strings.isNullOrEmpty(staticIconColor)) {
      setIconColorSource(RecordTypePropertySource.STATIC);
    }
  }

  public void addAllFieldCfgs(Set<FieldCfg> fieldCfgs) {
    for (FieldCfg fd : fieldCfgs) {
      addFieldCfg(fd);
    }
  }

  public void addFieldCfg(FieldCfg fieldCfg) {
    fieldCfg.setRecordType(this);
    getFieldCfgs().add(fieldCfg);
  }

  public void removeFieldCfg(FieldCfg fieldCfg) {
    getFieldCfgs().remove(fieldCfg);
  }

  public void removeAllFieldCfgs(Set<FieldCfg> fieldCfgs) {
    for (FieldCfg fd : fieldCfgs) {
      removeFieldCfg(fd);
    }
  }

  /**
   * @return the RelatedActionCfg whose id matches the passed relatedActionId.
   * @throws AppianRuntimeException (wrapping ObjectNotFoundException) if the RelatedActionCfg with id
   * relatedActionId is not found.
   */
  @Override
  public RelatedActionCfg getRelatedActionCfg(long relatedActionId) {
    List<RelatedActionCfg> raCfgs = getRelatedActionCfgs();
    Long boxedRelatedActionId = Long.valueOf(relatedActionId);
    for (RelatedActionCfg raCfg : raCfgs) {
      if (boxedRelatedActionId.equals(raCfg.getId())) {
        return raCfg;
      }
    }
    throw new AppianRuntimeException(new ObjectNotFoundException(relatedActionId,
        ErrorCode.RECORD_TYPE_RELATED_ACTION_NOT_FOUND, getName()));
  }

  @Override
  public ReadOnlyRecordAction getRecordAction(String uuid) throws ObjectNotFoundException {
    List<RecordListActionCfg> recordListActionCfgs = getRecordListActionCfgs();
    for (RecordListActionCfg rlaCfg : recordListActionCfgs) {
      if (uuid.equals(rlaCfg.getUuid())) {
        return rlaCfg;
      }
    }
    List<RelatedActionCfg> relatedActionCfgs = getRelatedActionCfgs();
    for (RelatedActionCfg raCfg : relatedActionCfgs) {
      if (uuid.equals(raCfg.getUuid())) {
        return raCfg;
      }
    }
    throw new ObjectNotFoundException(uuid, ErrorCode.RECORD_TYPE_ACTION_NOT_FOUND, getName());
  }

  @Override
  public ReadOnlyRecordAction getRecordActionByReferenceKey(String referenceKey) throws ObjectNotFoundException {
    List<RecordListActionCfg> recordListActionCfgs = getRecordListActionCfgs();
    for (RecordListActionCfg rlaCfg : recordListActionCfgs) {
      if (referenceKey.equals(rlaCfg.getReferenceKey())) {
        return rlaCfg;
      }
    }
    List<RelatedActionCfg> relatedActionCfgs = getRelatedActionCfgs();
    for (RelatedActionCfg raCfg : relatedActionCfgs) {
      if (referenceKey.equals(raCfg.getReferenceKey())) {
        return raCfg;
      }
    }
    throw new ObjectNotFoundException(referenceKey, ErrorCode.RECORD_TYPE_ACTION_NOT_FOUND, getName());
  }

  @Override
  public FieldCfg getFieldCfg(String uuid) throws ObjectNotFoundException {
    for (FieldCfg fieldCfg : fieldCfgs) {
      if (uuid.equals(fieldCfg.getUuid())) {
        return fieldCfg;
      }
    }
    throw new ObjectNotFoundException(uuid, ErrorCode.RECORD_TYPE_USER_FILTER_NOT_FOUND, getName());
  }

  public DetailViewCfg getDetailViewCfgByUrlStub(String urlStub) {
    for(DetailViewCfg dvc : getDetailViewCfgs()) {
      if(dvc.getUrlStub().equals(urlStub)) {
        return dvc;
      }
    }

    return null;
  }

  @XmlTransient
  @Transient
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeDetailViewCfg)
  // This method is not invoked directly from anywhere, it is only used as a marker for IX.
  public DetailViewHeaderCfg getDefaultDetailViewHeaderCfg() {
    List<DetailViewCfg> detailViewConfigs = getDetailViewCfgs();
    if (detailViewConfigs == null || detailViewConfigs.isEmpty()) {
      return null;
    }
    return detailViewConfigs.get(0).getDetailViewHeaderCfg();
  }

  public void setImportanceScore(int importanceScore) {
    this.importanceScore = importanceScore;
  }

  @Override
  @XmlTransient
  @Column(name = "importance_score", nullable = false)
  public int getImportanceScore() {
    return importanceScore;
  }

  public void setAndCalculateImportanceScore() {
    if (Hibernate.isInitialized(relatedActionCfgs) && Hibernate.isInitialized(recordListActionCfgs) &&
        Hibernate.isInitialized(recordRelationshipCfgs) && Hibernate.isInitialized(detailViewCfgs)) {
      int actionSize = (relatedActionCfgs == null ? 0 : relatedActionCfgs.size()) + (recordListActionCfgs == null ? 0 : recordListActionCfgs.size());

      Set<String> relatedRecordUuids = new LinkedHashSet<>();
      if (recordRelationshipCfgs != null) {
        for (RecordRelationshipCfg relationship : recordRelationshipCfgs) {
          relatedRecordUuids.add(relationship.getTargetRecordTypeUuid());
        }
      }

      int relatedRecordSize = relatedRecordUuids.size();
      int viewSize = detailViewCfgs == null ? 0 : detailViewCfgs.size();
      this.importanceScore = IMPORTANCE_WEIGHT * (actionSize + viewSize) + relatedRecordSize;
    }
  }

  @Transient
  @Override
  public ReadOnlyRecordTypeDefinition copyWithNewRecordSourceCfg(ReadOnlyRecordSource newRecordSourceCfg) {
    RecordTypeDefinition clone = SerializationUtils.clone(this);
    clone.setSourceConfiguration((RecordSourceCfg)newRecordSourceCfg);
    return clone;
  }

  /**
   * @deprecated This has not been kept up-to-date.
   */
  @Deprecated
  public static final Equivalence<RecordTypeDefinition> LEGACY_EQUIVALENCE = new Equivalence<RecordTypeDefinition>() {
    @Override
    protected boolean doEquivalent(RecordTypeDefinition a, RecordTypeDefinition b) {
      return Objects.equal(a.getUuid(), b.getUuid()) && Objects.equal(a.getName(), b.getName()) &&
          Objects.equal(a.getPluralName(), b.getPluralName()) &&
          Objects.equal(a.getDescription(), b.getDescription()) &&
          Objects.equal(a.getUrlStub(), b.getUrlStub()) &&
          Objects.equal(a.getSourceUuid(), b.getSourceUuid()) &&
          Objects.equal(a.getSourceType(), b.getSourceType()) &&
          Objects.equal(a.getIsSystem(), b.getIsSystem()) && Objects.equal(a.getType(), b.getType()) &&
          Objects.equal(a.getIconIdSource(), b.getIconIdSource()) &&
          Objects.equal(a.getIconId(), b.getIconId()) &&
          Objects.equal(a.getIconColorSource(), b.getIconColorSource()) &&
          Objects.equal(a.getIconColor(), b.getIconColor()) &&
          Objects.equal(a.getListAutoRefreshInterval(), b.getListAutoRefreshInterval()) &&
          Objects.equal(a.getListViewTemplateExpr(), b.getListViewTemplateExpr()) &&
          Iterables2.equal(a.getDetailViewCfgs(), b.getDetailViewCfgs()) &&
          Iterables2.equal(a.getFieldCfgs(), b.getFieldCfgs()) &&
          Objects.equal(a.getDefaultFiltersExpr(), b.getDefaultFiltersExpr()) &&
          Objects.equal(a.getFacetsListExpr(), b.getFacetsListExpr()) &&
          Objects.equal(a.getDataSrcExpr(), b.getDataSrcExpr()) && a.getSecurity() == b.getSecurity() &&
          Objects.equal(a.getRecordEventsConfig(), b.getRecordEventsConfig()) &&
          Objects.equal(a.getUsesRollingSyncLimit(), b.getUsesRollingSyncLimit()) &&
          Objects.equal(a.getUsesRecoverySync(), b.getUsesRecoverySync()) &&
          Objects.equal(a.getIsScheduledIndexingEnabled(), b.getIsScheduledIndexingEnabled());
    }

    @Override
    protected int doHash(RecordTypeDefinition t) {
      return Objects.hashCode(t.getUuid(), t.getName(), t.getPluralName(), t.getDescription(), t.getUrlStub(),
          t.getSourceType(), t.getSourceUuid(), t.getIsSystem(), t.getType(), t.getIconIdSource(),
          t.getIconId(), t.getIconColorSource(), t.getIconColor(), t.getListAutoRefreshInterval(),
          t.getListViewTemplateExpr(), t.getDetailViewCfgs(), t.getFieldCfgs(), t.getDefaultFiltersExpr(),
          t.getFacetsListExpr(), t.getDataSrcExpr(), t.getLayoutType(), t.getTitleExpr(), t.getSecurity(),
          t.getRecordEventsConfig(), t.getUsesRollingSyncLimit(), t.getUsesRecoverySync(),
          t.getIsScheduledIndexingEnabled());
    }
  };

  @Override
  public boolean shouldUseConditionalVariableBindings(ConditionalBindingsType conditionalBindingsType) {
    if (conditionalBindingsType == ConditionalBindingsType.recordListViewTypeIsFeed) {
      return RecordLayoutConfig.FEED.equals(getLayoutType());
    }
    return false;
  }

  @Override
  @Transient
  @XmlTransient
  public ReadOnlyRecordSourceField getDocumentField() {
    List<ReadOnlyRecordSourceField> recordFields = getRecordFields();
    if (recordFields == null) {
      return null;
    } else {
      for (ReadOnlyRecordSourceField recordField : recordFields) {
        if (DOCUMENT_QNAME.equals(recordField.getType())) {
          return recordField;
        }
      }
    }
    return null;
  }

  @Override
  @Transient
  @XmlTransient
  public boolean isDocumentRecordType() {
    return getDocumentField() != null;
  }

  public void setAllExpressionsTransformationState(ExpressionTransformationState expressionsTransformationState) {
    setOtherExpressionState(expressionsTransformationState);
    setListViewExpressionState(expressionsTransformationState);
    getDetailViewCfgs().forEach(detailViewCfg -> {
      detailViewCfg.setExpressionTransformationState(expressionsTransformationState);
      DetailViewHeaderCfg headerCfg = detailViewCfg.getDetailViewHeaderCfg();
      if (headerCfg != null) {
        headerCfg.setExpressionTransformationState(expressionsTransformationState);
      }
    });
    getFieldCfgs().forEach(fieldCfg -> {
      fieldCfg.setExpressionTransformationState(expressionsTransformationState);
      fieldCfg.getFacetOptions().forEach(facetOptionCfg -> facetOptionCfg.setExpressionTransformationState(expressionsTransformationState));
    });
    getRecordListActionCfgs().forEach(recordListActionCfg -> recordListActionCfg.setExpressionTransformationState(expressionsTransformationState));
    getRelatedActionCfgs().forEach(relatedActionCfg -> relatedActionCfg.setExpressionTransformationState(expressionsTransformationState));
    getSourceCfgs().forEach(recordSourceCfg -> {
      recordSourceCfg.setExpressionTransformationState(expressionsTransformationState);
      recordSourceCfg.getSourceAndCustomFields()
          .forEach(y -> y.setExpressionTransformationState(expressionsTransformationState));
      recordSourceCfg.getSourceFields()
          .forEach(sourceField -> sourceField.getFieldMetaData()
              .forEach(metaDataCfg -> metaDataCfg.setExpressionTransformationState(
                  expressionsTransformationState)));
    });
    getRecordTypeSearchCfg().forEach(recordTypeSearchCfg -> recordTypeSearchCfg.setExpressionTransformationState(expressionsTransformationState));
  }
}
