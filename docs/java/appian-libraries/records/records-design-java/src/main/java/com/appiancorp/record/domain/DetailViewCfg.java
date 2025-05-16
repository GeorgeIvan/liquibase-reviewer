package com.appiancorp.record.domain;

import static com.appiancorp.core.expr.Expression.fromStoredForm;
import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;
import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;
import static com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType.DIALOG;
import static com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType.NEW_TAB;
import static com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType.EXPRESSION;
import static com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType.GUIDED;
import static com.appiancorp.record.domain.RecordsMetricsBundleKeys.INTERFACE_DEFINITION_NODE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.config.xsd.TypeQNameUtil;
import com.appiancorp.core.expr.Expression;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.cdt.RecordActionLaunchType;
import com.appiancorp.core.expr.portable.cdt.RecordHeaderBillboardConfigConstants;
import com.appiancorp.core.expr.portable.cdt.RecordHeaderCardConfigConstants;
import com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.record.KnownRecordType;
import com.appiancorp.record.entities.RecordTypeViewSecurityCfg;
import com.appiancorp.sail.WrapInMetricHelper;
import com.appiancorp.suiteapi.rules.RuleDataType;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoDetailViewCfg;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents the expressionable configuration for a persistent view of record details.
 */
@Hidden
@Entity
@Table(name = "record_detail_view_cfg")
@XmlRootElement(name = "detailViewCfg", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = DetailViewCfg.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {"id", "nameExpr",
  "uiExpr", "visibilityExpr", "urlStub", "headerExpr", "relatedActionCfgs", "recordActionLaunchType",
  "detailViewHeaderCfg", "recordUiSecurityType", "securityCfg"})
@IgnoreJpa
@BreadcrumbProperty(value = "urlStub", format = BreadcrumbText.recordTypeDetailViewCfgViewStub,
  breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_PEERS)
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class DetailViewCfg implements ReadOnlyDetailViewCfg {
  public static final String DEFAULT_RECORD_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_defaultRecordHeader\"(" +
      "  isHeaderFixed: %s," +
      "  showDivider: %s" +
      ")";
  public static final String DEFAULT_RULE_BACKED_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_defaultRuleBackedRecordHeader\"(" +
      "  isHeaderFixed: %s," +
      "  showDivider: %s" +
      ")";
  public static final String DEFAULT_USER_HEADER_EXPRESSION = "#\"%s\"(" +
      "  record: fn!if(" +
      "    rt!supportsFieldReferences," +
      "    rv!record," +
      "    fn!null()" +
      "  )" +
      ")";

  private static final String NULL = "fn!null()";
  private static final String TRUE = "fn!true()";
  private static final String FALSE = "fn!false()";
  public static final String DEFAULT_IS_HEADER_FIXED_EXPR = FALSE;

  public static final String FALLBACK_EXPRESSION = "fn!try(%s, %s)";

  public static final Expression DEFAULT_RECORD_HEADER_EXPRESSION_WITH_UNFIXED_HEADER = Expression.fromStoredForm(
      String.format(
          DEFAULT_RECORD_HEADER_EXPRESSION,
          FALSE,
          TRUE
      )
  );
  public static final Expression DEFAULT_RULE_BACKED_HEADER_EXPRESSION_WITH_UNFIXED_HEADER = Expression.fromStoredForm(
      String.format(
          DEFAULT_RULE_BACKED_HEADER_EXPRESSION,
          FALSE,
          TRUE
      )
  );
  public static final String CARD_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_cardRecordHeader\"(" +
      "  colorSource: \"%s\"," +
      "  style: %s," +
      "  showFollowButton: %s," +
      "  isHeaderFixed: %s" +
      ")";
  public static final String DEFAULT_STYLE_CARD_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_cardRecordHeader\"(" +
      "  style: \"#efefef\"," +
      "  showFollowButton: %s," +
      "  isHeaderFixed: %s" +
      ")";

  public static final String BILLBOARD_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_billboardRecordHeader\"(" +
      "  imageType: \"%s\"," +
      "  backgroundMedia: %s," +
      "  height: \"%s\"," +
      "  overlayColor: \"%s\"," +
      "  overlayPosition: \"%s\"," +
      "  overlayStyle: \"%s\"," +
      "  backgroundColor: \"%s\"," +
      "  showFollowButton: %s," +
      "  isHeaderFixed: %s" +
      ")";
  public static final String DEFAULT_MEDIA_BILLBOARD_HEADER_EXPRESSION = "#\"SYSTEM_SYSRULES_billboardRecordHeader\"(" +
      "  backgroundMedia: fn!null()," +
      "  height: \"%s\"," +
      "  overlayColor: \"%s\"," +
      "  overlayPosition: \"%s\"," +
      "  overlayStyle: \"%s\"," +
      "  backgroundColor: \"%s\"," +
      "  showFollowButton: %s," +
      "  isHeaderFixed: %s" +
      ")";
  public static final String BILLBOARD_BACKGROUND_MEDIA_DOCUMENT_CONVERSION = "a!reference_appian_internal(" +
      "  uuid: %s," +
      "  type: 'type!{http://www.appian.com/ae/types/2009}CollaborationDocument'" +
      ")";
  public static final String BILLBOARD_DOCUMENT_IMAGE_TYPE = "DOCUMENT";
  public static final String NO_HEADER_EXPRESSION = "=fn!null()";
  public static final String XRAY_RECORD_HEADER_WRAPPER = "#\"SYSTEM_SYSRULES_xray_recordHeaderWrapper\"(" +
      "  recordHeaderInvocation: %s," +
      "  recordTypeUuid: \"%s\"," +
      "  recordTypeName: \"%s\"" +
      ")";

  /* "contents" MUST be last because the designer can save an interface with
   * trailing comma. SAIL allows trailing commas before closing ) and } */
  public static final String RECORD_BODY_WRAPPER_EXPRESSION = "#\"SYSTEM_SYSRULES_recordBodyWrapper\"(" +
      "  recordTypeUuid: \"%s\"," +
      "  contentsExpression: { %s }," +
      "  wrappedExpression: { %s }," +
      "  expressionTransformationState: \"%s\"," +
      "  contents: %s" +
      ")";

  //language=sail
  public static final String USER_PROFILE_UI_TEMPLATE = "#\"SYSTEM_SYSRULES_recordBodyWrapper\"(%n" +
      "  contents: #\"%s\"(%n" +
      "    record: fn!if(" +
      "      rt!supportsFieldReferences," +
      "      rv!record," +
      "      fn!null()" +
      "    )," +
      "    identifier: fn!if(" +
      "      rt!supportsFieldReferences," +
      "      rv!identifier," +
      "      fn!null()" +
      "    )," +
      "    getCustomUi: fn!lambda_appian_internal(%n" +
      "      fn!with(%n" +
      "        local!customUi: %s,%n" +
      "        fn!try(%n" +
      "          #\"SYSTEM_SYSRULES_tag\"({isDesignerTopLevel: \"true\"}, local!customUi),%n" +
      "          local!customUi%n" +
      "        )%n" +
      "      )%n" +
      "    )%n" +
      "  ),%n" +
      "  recordTypeUrlStub: \"%s\",%n" +
      "  contentsExpression: { %s },%n" +
      "  isSystemRecordBody: fn!true(),%n" +
      "  expressionTransformationState: \"%s\",%n" +
      "  recordTypeUuid: \"%s\"" +
      ")";

  public static final String LOCAL_PART = "DetailViewCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = "id";
  public static final String PROP_NAME_EXPR = "nameExpr";
  public static final String PROP_EXPRESSION = "uiExpr";
  public static final String PROP_RELATED_ACTIONS = "relatedActionCfg";
  public static final String PROP_RECORD_ACTION_LAUNCH_TYPE = "recordActionLaunchType";
  public static final String PROP_HEADER_CFG = "detailViewHeaderCfg";
  public static final String PROP_RECORD_UI_SECURITY_TYPE = "recordUiSecurityType";
  public static final String PROP_VIEW_SECURITY_CFG = "viewSecurityCfg";

  public static final String TBL_DETAIL_VIEW_CFG_RELATED_ACTION_CONFIG = "record_dvc_rac";
  public static final String TBL_DETAIL_VIEW_CFG_HEADER_CONFIG = "record_dvc_hc";
  public static final String JOIN_COL_DETAIL_VIEW_CFG_ID = "record_detail_view_cfg_id";
  public static final String COL_SECURITY_CFG_ID = "security_cfg_id";
  public static final String COL_ID = "id";

  private static final String RECORD_TYPE_ID = "record_type_id";


  private static final long serialVersionUID = 1L;
  public static final byte EXPRESSION_SECURITY_BYTE = 0;
  public static final byte GUIDED_SECURITY_BYTE = 1;
  private static final QName RULE_DATATYPE_QNAME = TypeQNameUtil.getQName(RuleDataType.class);

  private Long id;

  private Long recordTypeId;
  private String nameExpr;
  private String uiExpr;
  private String headerExpr;
  private String visibilityExpr;
  private String urlStub;
  private List<RelatedActionCfg> relatedActionCfgs = new ArrayList<>();
  private RecordActionLaunchType recordActionLaunchType;
  private DetailViewHeaderCfg detailViewHeaderCfg;
  private RecordUiSecurityType recordUiSecurityType;
  private RecordTypeViewSecurityCfg securityCfg;

  private transient ExpressionTransformationState expressionTransformationState = STORED;

  /* This setting allows for batching of collection fetches. Since a record type may have several detail view
   * cfgs, each with a collection of related action cfgs, we want to initialize many related action cfg
   * collections at once, instead of one by one. */
  private static final int PREFETCH_BATCH_SIZE = 100;

  /**
   * Default constructor for JAXB compatibility and testing only
   */
  @VisibleForTesting
  public DetailViewCfg() {}

  /**
   * Constructs a non-persisted DetailViewCfg (id is not specified)
   */
  public DetailViewCfg(String nameExpr, String uiExpr, String visibilityExpr) {
    this(nameExpr, uiExpr, visibilityExpr, DEFAULT_URL_STUB);
  }

  /**
   * Constructs a non-persisted DetailViewCfg (id is not specified)
   */
  public DetailViewCfg(String nameExpr, String uiExpr, String visibilityExpr, String urlStub) {
    this(nameExpr, uiExpr, visibilityExpr, urlStub, ReadOnlyRecordTypeDefinition.DEFAULT_RECORD_ACTION_LAUNCH_TYPE, null, EXPRESSION);
  }

  /**
   * Constructs a non-persisted DetailViewCfg with custom header (id is not specified)
   */
  public DetailViewCfg(String nameExpr, String uiExpr, String visibilityExpr, String urlStub, DetailViewHeaderCfg detailViewHeaderCfg) {
    this(nameExpr, uiExpr, visibilityExpr, urlStub, ReadOnlyRecordTypeDefinition.DEFAULT_RECORD_ACTION_LAUNCH_TYPE, detailViewHeaderCfg, EXPRESSION);
  }


  public DetailViewCfg(String nameExpr, String uiExpr, String visibilityExpr, String urlStub,
      RecordActionLaunchType recordActionLaunchType, DetailViewHeaderCfg detailViewHeaderCfg, RecordUiSecurityType recordUiSecurityType) {
    setNameExpr(nameExpr);
    setUiExpr(uiExpr);
    setVisibilityExpr(visibilityExpr);
    setUrlStub(urlStub);
    setRelatedActionCfgs(relatedActionCfgs);
    setRecordActionLaunchType(recordActionLaunchType);
    setDetailViewHeaderCfg(detailViewHeaderCfg);
    setRecordUiSecurityType(recordUiSecurityType);
  }

  @VisibleForTesting
  public DetailViewCfg(String nameExpr, String uiExpr, String urlStub,
      RecordUiSecurityType recordUiSecurityType, RecordTypeViewSecurityCfg securityCfg) {
    this(nameExpr, uiExpr, null, urlStub, ReadOnlyRecordTypeDefinition.DEFAULT_RECORD_ACTION_LAUNCH_TYPE, null, recordUiSecurityType);
    setSecurityCfg(securityCfg);
  }

  /**
   * This overload constructs a transient instance of a persisted DetailViewCfg by
   * allowing the persisted id to be specified.
   */
  public DetailViewCfg(long id, String nameExpr, String uiExpr, String visibilityExpr) {
    this(nameExpr, uiExpr, visibilityExpr);
    setId(id);
  }

  public DetailViewCfg(DesignerDtoDetailViewCfg dto, List<RelatedActionCfg> ras) {
    setId(dto.getId());
    setVisibilityExpr(dto.getVisibilityExpr());
    setHeaderExpr(dto.getHeaderExpr());
    setNameExpr(dto.getNameExpr());
    setUiExpr(dto.getUiExpr());
    setUrlStub(dto.getUrlStub());
    List<Boolean> craFlags = dto.getCraFlags();
    List<RelatedActionCfg> cras = IntStream.range(0, dto.getCraFlags().size())
        .filter(i -> craFlags.get(i))
        .mapToObj(i -> ras.get(i))
        .collect(Collectors.toList());
    setRelatedActionCfgs(cras);
    setRecordActionLaunchType(dto.getRecordActionLaunchType());
    setDetailViewHeaderCfg(dto.getDetailViewHeaderCfg() == null ? null : new DetailViewHeaderCfg(dto.getDetailViewHeaderCfg()));
    setExpressionTransformationState(dto.isExprsAreEvaluable() ? STORED : DISPLAY);
    setRecordUiSecurityType(dto.getRecordUiSecurityType());
    setSecurityCfg(dto.getSecurityCfg() == null ? null : new RecordTypeViewSecurityCfg(dto.getSecurityCfg()));
  }

  @Override
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = COL_ID)
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @XmlTransient
  @Column(name = RECORD_TYPE_ID, insertable = false, updatable = false)
  public Long getRecordTypeId() {
    return recordTypeId;
  }

  public void setRecordTypeId(Long recordTypeId) {
    this.recordTypeId = recordTypeId;
  }

  /**
   * The expression that defines the user-visible name of the detail view.
   */
  @XmlElement
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeDetailViewNameExpr, variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Column(name = "name_expr", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getNameExpr() {
    return nameExpr;
  }
  public void setNameExpr(String nameExpr) {
    this.nameExpr = checkNotNull(nameExpr);
  }

  /**
   * The expression that defines the layout of the detail view.
   */
  @XmlElement
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeDetailViewUiExpr, variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Column(name = "ui_expr", nullable=true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getUiExpr() {
    return uiExpr;
  }
  public void setUiExpr(String uiExpr) {
    this.uiExpr = uiExpr;
  }

  /**
   * The expression that defines the header of the detail view.
   */
  @XmlElement
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeDetailViewHeaderExpr, variableBindings = VariableBindings.RECORD_TYPE,
    prependedBreadcrumbs = BreadcrumbText.recordTypeDetailViewCfg)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Column(name = "header_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getHeaderExpr() {
    return headerExpr;
  }
  public void setHeaderExpr(String headerExpr) {
    this.headerExpr = headerExpr;
  }

  /**
   * The boolean expression that indicates whether the current user can see this detail view.
   */
  @XmlElement
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeDetailViewVisibilityExpr, variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Column(name = "visibility_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getVisibilityExpr() {
    return visibilityExpr;
  }
  public void setVisibilityExpr(String visibilityExpr) {
    this.visibilityExpr = visibilityExpr;
  }

  /**
   * A unique (to a specific {@link ReadOnlyRecordTypeDefinition}) URL component that can be used to reference instances of this
   * {@link DetailViewCfg}
   */
  @XmlElement
  @Column(name = "url_stub", length = Constants.COL_MAXLEN_INDEXABLE, nullable = false)
  public String getUrlStub() {
    return urlStub;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<ReadOnlyRecordAction> getRelatedActionCfgsReadOnly() {
    return ImmutableList.copyOf(getRelatedActionCfgs());
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @XmlElement(name = DetailViewCfg.PROP_RELATED_ACTIONS)
  @XmlIDREF // When serializing to XML, this field references RelatedActionCfgs via the RefId field (tagged @XmlID)
  @ManyToMany(targetEntity=RelatedActionCfg.class, fetch=FetchType.LAZY)
  @JoinTable(name=TBL_DETAIL_VIEW_CFG_RELATED_ACTION_CONFIG,
    joinColumns=@JoinColumn(name=JOIN_COL_DETAIL_VIEW_CFG_ID, nullable=false),
    inverseJoinColumns=@JoinColumn(name=RelatedActionCfg.JOIN_COL_RELATED_ACTION_CFG_ID, nullable=false))
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = PREFETCH_BATCH_SIZE)
  public List<RelatedActionCfg> getRelatedActionCfgs() {
    return relatedActionCfgs == null ? Collections.<RelatedActionCfg>emptyList() : relatedActionCfgs;
  }

  public void setRelatedActionCfgs(List<RelatedActionCfg> relatedActionCfgs) {
    this.relatedActionCfgs = relatedActionCfgs;
  }

  /**
   * The method in which an action on a {@link ReadOnlyRecordTypeDefinition} will be launched.
   */
  @Transient
  @XmlElement(name = DetailViewCfg.PROP_RECORD_ACTION_LAUNCH_TYPE)
  public RecordActionLaunchType getRecordActionLaunchType() {
    return recordActionLaunchType;
  }

  public void setRecordActionLaunchType(RecordActionLaunchType recordActionLaunchType) {
    this.recordActionLaunchType = recordActionLaunchType;
  }

  @Transient
  @XmlElement(name = DetailViewCfg.PROP_RECORD_UI_SECURITY_TYPE)
  public RecordUiSecurityType getRecordUiSecurityType() {
    return recordUiSecurityType;
  }

  public void setRecordUiSecurityType(RecordUiSecurityType recordUiSecurityType) {
    this.recordUiSecurityType = recordUiSecurityType;
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
    RecordActionLaunchType recordActionLaunchType = ReadOnlyRecordTypeDefinition.DEFAULT_RECORD_ACTION_LAUNCH_TYPE;

    if (index == 2) {
      recordActionLaunchType = DIALOG;
    } else if (index == 1) {
      recordActionLaunchType = NEW_TAB;
    }

    setRecordActionLaunchType(recordActionLaunchType);
  }

  @Column(name = "security_type", nullable = false)
  private byte getRecordUiSecurityTypeByte() {
    if (recordUiSecurityType == null) {
      return EXPRESSION_SECURITY_BYTE;
    }

    switch (recordUiSecurityType) {
      case EXPRESSION:
        return EXPRESSION_SECURITY_BYTE;
      case GUIDED:
        return GUIDED_SECURITY_BYTE;
      default:
        return EXPRESSION_SECURITY_BYTE;
    }
  }

  private void setrecordUiSecurityTypeByte(byte index) {
    if (index == 0) {
      recordUiSecurityType = EXPRESSION;
    } else {
      recordUiSecurityType = GUIDED;
    }

    setRecordUiSecurityType(recordUiSecurityType);
  }

  @XmlElement(name = PROP_HEADER_CFG)
  @OneToOne(cascade = CascadeType.ALL, targetEntity = DetailViewHeaderCfg.class)
  @JoinTable(name = TBL_DETAIL_VIEW_CFG_HEADER_CONFIG,
      joinColumns = @JoinColumn(name = JOIN_COL_DETAIL_VIEW_CFG_ID),
      inverseJoinColumns = @JoinColumn(name = DetailViewHeaderCfg.JOIN_COL_HEADER_CFG_ID))
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = PREFETCH_BATCH_SIZE)
  @HasForeignKeys(breadcrumb=BreadcrumbText.SKIP)
  // HasForeignKeys is needed to make all the DetailViewHeaderCfgs go through the correct Binder for IX.
  // Note: this is technically doing more work than is strictly necessary since we're only using the first header, and
  //       we're also visiting the first header in RecordTypeDefinition.
  public DetailViewHeaderCfg getDetailViewHeaderCfg() {
    return detailViewHeaderCfg;
  }

  public void setDetailViewHeaderCfg(DetailViewHeaderCfg detailViewHeaderCfg) {
    this.detailViewHeaderCfg = detailViewHeaderCfg;
  }

  @XmlElement(name = PROP_VIEW_SECURITY_CFG)
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
  @JoinColumn(name = COL_SECURITY_CFG_ID, referencedColumnName = COL_ID)
  @HasForeignKeys(breadcrumb=BreadcrumbText.recordTypeViewsAndActionsSecurityCfg)
  public RecordTypeViewSecurityCfg getSecurityCfg() {
    return securityCfg;
  }

  public void setSecurityCfg(RecordTypeViewSecurityCfg securityCfg) {
    this.securityCfg = securityCfg;
  }

  // Leaves out "id" and "relatedActionCfgs" intentionally, autogenerated by Eclipse 3.7
  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((uiExpr == null) ? 0 : uiExpr.hashCode());
    result = prime * result + ((nameExpr == null) ? 0 : nameExpr.hashCode());
    result = prime * result + ((headerExpr == null) ? 0 : headerExpr.hashCode());
    result = prime * result + ((visibilityExpr == null) ? 0 : visibilityExpr.hashCode());
    result = prime * result + ((urlStub == null) ? 0 : urlStub.hashCode());
    result = prime * result + ((recordActionLaunchType == null) ? 0 : recordActionLaunchType.hashCode());
    result = prime * result + ((detailViewHeaderCfg == null) ? 0 : detailViewHeaderCfg.hashCode());
    result = prime * result + ((recordUiSecurityType == null) ? 0 : recordUiSecurityType.hashCode());
    result = prime * result + ((securityCfg == null) ? 0 : securityCfg.hashCode());
    return result;
  }

  // Leaves out "id" and "relatedActionCfgs" intentionally, autogenerated by Eclipse 3.7
  // (relatedActionCfgs will be set to a PersistentBag by Hibernate, which has a strict object equivalence check for equals)
  //
  // Marked "final" so that we can use the instanceof check (to avoid violating
  // the symmetry of the equals contract) while still allowing trivial
  // subclasses (e.g., lazy proxies) to be equal to actual instances of
  // DetailView
  @Override
  public final boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof DetailViewCfg))
      return false;
    DetailViewCfg other = (DetailViewCfg) obj;
    if (uiExpr == null) {
      if (other.uiExpr != null)
        return false;
    } else if (!uiExpr.equals(other.uiExpr))
      return false;
    if (nameExpr == null) {
      if (other.nameExpr != null)
        return false;
    } else if (!nameExpr.equals(other.nameExpr))
      return false;
    if (headerExpr == null) {
      if (other.headerExpr != null)
        return false;
    } else if (!headerExpr.equals(other.headerExpr))
      return false;
    if (visibilityExpr == null) {
      if (other.visibilityExpr != null)
        return false;
    } else if (!visibilityExpr.equals(other.visibilityExpr))
      return false;
    if (urlStub == null) {
      if (other.urlStub != null)
        return false;
    } else if (!urlStub.equals(other.urlStub))
      return false;
    if (recordActionLaunchType == null) {
      if (other.recordActionLaunchType != null)
        return false;
    } else if (!recordActionLaunchType.equals(other.recordActionLaunchType))
      return false;
    if (detailViewHeaderCfg == null) {
      if (other.detailViewHeaderCfg != null)
        return false;
    } else if (!detailViewHeaderCfg.equals(other.detailViewHeaderCfg))
      return false;
    if (recordUiSecurityType == null) {
      if (other.recordUiSecurityType != null)
        return false;
    } else if (!recordUiSecurityType.equals(other.recordUiSecurityType))
      return false;
    if (securityCfg == null) {
      if (other.securityCfg != null)
        return false;
    } else if (!securityCfg.equals(other.securityCfg))
      return false;
    return true;
  }

  // Used by Java Serialization
  private Object readResolve() {
    this.expressionTransformationState = STORED;
    return this;
  }

  public static Function<DetailViewCfg, String> selectName() {
    return DetailViewCfg::getNameExpr;
  }

  /**
   * Returns the final UI expression to be shown for the detail view, which includes any system modifications
   * to the expression provided by the designer.
   */
  public Expression getFinalUiExpression(ReadOnlyRecordTypeSummary recordType) {
    Expression persistedExpression = getUiConfigWrappedUiExpression(uiExpr, expressionTransformationState);
    return getFinalUiExpression(recordType, persistedExpression, persistedExpression);
  }

  /**
   * Returns the final UI expression to be shown for the detail view. This is purely what the designer
   * provides and the expression does NOT contain any system modifications.
   */
  // Hibernate and JAXB will try to treat this as a field since this method is seen as a getter on the class
  @Transient
  @XmlTransient
  public Expression getUserFinalUiExpression() {
    Expression persistedExpression = getUiConfigWrappedUiExpression(uiExpr, expressionTransformationState);
    return fromStoredForm(persistedExpression.getEvaluableExpression());
  }

  /**
   * Returns the final UI expression to be shown for the detail view, which includes any system modifications
   * to the expression provided by the designer. This will instrument the expression for collecting metrics
   * in the context of the performance view.
   */
  public Expression getFinalUiExpressionForMetrics(ReadOnlyRecordTypeSummary recordType) {
    final Expression persistedExpression = getUiConfigWrappedUiExpression(uiExpr, expressionTransformationState);
    final Expression wrappedExpression = WrapInMetricHelper.toWrapInMetricExpression(
        INTERFACE_DEFINITION_NODE, ImmutableList.of(), true, persistedExpression);
    return getFinalUiExpression(recordType, persistedExpression, wrappedExpression);
  }

  private Expression getFinalUiExpression(ReadOnlyRecordTypeSummary recordType, Expression baseExpression, Expression wrappedExpression) {
    java.util.function.Function<String, String> wrapInHeredoc = expr -> "***\n" + expr + "\n***\n";
    // TODO: have the templates already in stored
    if (KnownRecordType.USERS_RECORD_TYPE.getUuid().equals(recordType.getUuid()) &&
        "summary".equals(urlStub)) {
      String expression = String.format(USER_PROFILE_UI_TEMPLATE,
          recordType.getIsReplicaEnabled() ?
              "SYSTEM_SYSRULES_syncedUserRecordSummaryDashboard": "SYSTEM_SYSRULES_userRecordSummaryDashboard",
          wrappedExpression.isNullOrEmpty() ? "{}" : wrappedExpression.getEvaluableExpression(),
          recordType.getUrlStub(),
          wrapInHeredoc.apply(baseExpression.isNullOrEmpty() ? "{}" : baseExpression.getEvaluableExpression()),
          STORED.toString(),
          recordType.getUuid());
      return fromStoredForm(expression);
    }

    String expression = wrappedExpression.isNullOrEmpty() ?
        "" :
        String.format(RECORD_BODY_WRAPPER_EXPRESSION,
            recordType.getUuid(),
            wrapInHeredoc.apply(baseExpression.getEvaluableExpression()),
            wrapInHeredoc.apply(wrappedExpression.getEvaluableExpression()),
            STORED.name(),
            wrappedExpression.getEvaluableExpression());
    return fromStoredForm(expression);
  }

  private RecordHeaderType getRecordHeaderType(DetailViewHeaderCfg detailViewHeaderCfg) {
    if (detailViewHeaderCfg != null) {
      return detailViewHeaderCfg.getHeaderType();
    }
    return RecordHeaderType.STANDARD;
  }

  /**
   * Returns the final header expression to be shown for the detail view, which includes any system
   * modifications to the expression provided by the designer
   * This method specifically handles proper defaulting for the following cases:
   * <dl>
   * <dt>null (NULL in the DB column) or "" (empty string):</dt>
   * <dd>return default expression based on record properties</dd>
   * <dt>"=null()":</dt>
   * <dd>return null, indicating no header</dd>
   * </dl>
   */
  public Expression getFinalHeaderExpression(ReadOnlyRecordTypeSummary rt) {
    String rtUuid = null;
    QName qName = null;
    if (rt != null) {
      rtUuid = rt.getUuid();
      qName = rt.getSourceType();
    }

    boolean hideFollow = RULE_DATATYPE_QNAME.equals(qName);
    String showFollowExpr = hideFollow ? FALSE : TRUE;

    RecordHeaderType recordHeaderType = getRecordHeaderType(detailViewHeaderCfg);
    String isHeaderFixedExpr =
        detailViewHeaderCfg != null && detailViewHeaderCfg.getIsHeaderFixed() ? TRUE : FALSE;
    String showDividerExpr = detailViewHeaderCfg == null || detailViewHeaderCfg.isShowDivider() ? TRUE : FALSE;

    if (KnownRecordType.USERS_RECORD_TYPE.getUuid().equals(rtUuid)) {
      String userRecordHeaderRule = getUserRecordHeaderRule(rt);
      return getHeaderExprWithDefaulting(
          Expression.fromStoredForm(String.format(DEFAULT_USER_HEADER_EXPRESSION, userRecordHeaderRule)));
    } else if (detailViewHeaderCfg != null) {
      if (RecordHeaderType.CARD.equals(recordHeaderType)) {
        return getCardHeaderExpression(showFollowExpr, isHeaderFixedExpr);
      } else if (RecordHeaderType.BILLBOARD.equals(recordHeaderType)) {
        return getBillboardHeaderExpression(showFollowExpr, isHeaderFixedExpr);
      }
    }

    String defaultHeaderStr = String.format(
        hideFollow ? DEFAULT_RULE_BACKED_HEADER_EXPRESSION : DEFAULT_RECORD_HEADER_EXPRESSION,
        isHeaderFixedExpr, showDividerExpr);
    return getHeaderExprWithDefaulting(Expression.fromStoredForm(defaultHeaderStr));
  }

  private String getUserRecordHeaderRule(ReadOnlyRecordTypeSummary rt) {
    if (rt == null) {
      return "SYSTEM_SYSRULES_defaultUserRecordHeader";
    }
    return rt.getIsReplicaEnabled() ? "SYSTEM_SYSRULES_syncedDefaultUserRecordHeader" :
        "SYSTEM_SYSRULES_defaultUserRecordHeader";
  }

  private Expression getHeaderExprWithDefaulting(Expression defaultHeaderExpr) {
    if (Strings.isNullOrEmpty(headerExpr)) {
      return defaultHeaderExpr;
    }
    String expression = NO_HEADER_EXPRESSION.equals(headerExpr) ? null : headerExpr;
    return Expression.of(expression, getExpressionTransformationState());
  }

  private Expression getUiConfigWrappedUiExpression(
      String uiExpr, ExpressionTransformationState expressionTransformationState) {
    if (uiExpr == null) {
      return Expression.EMPTY;
    }
    if (uiExpr.startsWith("=")) {
      uiExpr = uiExpr.substring(1);
    }
    return Expression.of(uiExpr, expressionTransformationState);
  }

  private Expression getCardHeaderExpression(String showFollowExpr, String isHeaderFixedExpr) {
    Map<String,String> cardConfig = detailViewHeaderCfg.getCardConfig();
    String colorSource = cardConfig.get(RecordHeaderCardConfigConstants.COLOR_SOURCE);
    String styleExpr = cardConfig.get(RecordHeaderCardConfigConstants.STYLE_EXPR);
    styleExpr = styleExpr != null && styleExpr.length() == 0 ? NULL : styleExpr;

    // Since CARD_HEADER_EXPRESSION will always be in display form, we need to ensure its contents are also in display form
    styleExpr = Expression.of(styleExpr, getExpressionTransformationState()).getEvaluableExpression();

    String cardHeader = String.format(CARD_HEADER_EXPRESSION, colorSource, styleExpr, showFollowExpr,
        isHeaderFixedExpr);
    String defaultCardHeader = String.format(DEFAULT_STYLE_CARD_HEADER_EXPRESSION, showFollowExpr,
        isHeaderFixedExpr);

    String expression = String.format(FALLBACK_EXPRESSION, cardHeader, defaultCardHeader);
    return fromStoredForm(expression);
  }

  private Expression getBillboardHeaderExpression(String showFollowExpr, String isHeaderFixedExpr) {
    Map<String,String> billboardConfig = detailViewHeaderCfg.getBillboardConfig();

    String mediaExpr = billboardConfig.get(RecordHeaderBillboardConfigConstants.BACKGROUND_MEDIA_EXPR);
    mediaExpr = mediaExpr != null && mediaExpr.length() == 0 ? NULL : mediaExpr;
    String imageType = billboardConfig.get(RecordHeaderBillboardConfigConstants.IMAGE_TYPE);
    if (BILLBOARD_DOCUMENT_IMAGE_TYPE.equalsIgnoreCase(imageType) && !mediaExpr.equalsIgnoreCase(NULL)) {
      mediaExpr = String.format(BILLBOARD_BACKGROUND_MEDIA_DOCUMENT_CONVERSION, mediaExpr);
    }

    String height = billboardConfig.get(RecordHeaderBillboardConfigConstants.HEIGHT);
    String overlayColor = billboardConfig.get(RecordHeaderBillboardConfigConstants.OVERLAY_COLOR);
    String overlayPosition = billboardConfig.get(RecordHeaderBillboardConfigConstants.OVERLAY_POSITION);
    String overlayStyle = billboardConfig.get(RecordHeaderBillboardConfigConstants.OVERLAY_STYLE);

    // Sanitize backgroundColor by removing all quote characters since it will be wrapped in quotes when building the header expression
    String backgroundColor = billboardConfig.get(RecordHeaderBillboardConfigConstants.BACKGROUND_COLOR);
    String sanitizedBackgroundColor = backgroundColor == null ? "" : backgroundColor.replaceAll("\"", "");

    // Since BILLBOARD_HEADER_EXPRESSION will always be in display form, we need to ensure its contents are also in display form
    // TODO: is this in display or stored form
    mediaExpr = Expression.of(mediaExpr, getExpressionTransformationState()).getEvaluableExpression();

    String billboardHeader = String.format(BILLBOARD_HEADER_EXPRESSION, imageType, mediaExpr, height,
        overlayColor, overlayPosition, overlayStyle, sanitizedBackgroundColor, showFollowExpr,
        isHeaderFixedExpr);
    String defaultBillboardHeader = String.format(DEFAULT_MEDIA_BILLBOARD_HEADER_EXPRESSION, height,
        overlayColor, overlayPosition, overlayStyle, sanitizedBackgroundColor, showFollowExpr,
        isHeaderFixedExpr);

    String expression = String.format(FALLBACK_EXPRESSION, billboardHeader, defaultBillboardHeader);
    return fromStoredForm(expression);
  }
}
