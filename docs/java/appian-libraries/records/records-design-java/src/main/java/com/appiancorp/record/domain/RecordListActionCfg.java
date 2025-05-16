package com.appiancorp.record.domain;

import static com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType.EXPRESSION;
import static com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType.GUIDED;
import static com.appiancorp.record.domain.RecordActionDialogHeightByteToModalDialogLayoutHeightConverter.DEFAULT_DIALOG_HEIGHT;
import static com.appiancorp.record.domain.RecordActionDialogWidthByteToModalDialogLayoutWidthConverter.DEFAULT_DIALOG_WIDTH;
import static com.appiancorp.record.domain.RecordActionDialogWidthByteToModalDialogLayoutWidthConverter.convertToModalDialogLayoutWidth;
import static com.appiancorp.record.domain.RecordActionDialogWidthByteToModalDialogLayoutWidthConverter.convertToRecordActionModalWidthByte;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;

import com.appian.core.persist.Constants;
import com.appiancorp.config.xsd.TypeQNameUtil;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.cdt.ModalDialogLayoutHeight;
import com.appiancorp.core.expr.portable.cdt.ModalDialogLayoutWidth;
import com.appiancorp.core.expr.portable.cdt.RecordActionDialogSize;
import com.appiancorp.core.expr.portable.cdt.RecordUiSecurityType;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.record.ui.ListActionWithSecurity;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.DesignerDtoRecordListActionCfg;
import com.appiancorp.type.refs.ProcessModelRef;
import com.appiancorp.type.refs.ProcessModelRefImpl;
import com.appiancorp.type.refs.Ref;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents the configuration for a record list action on a record type.
 */
@Hidden
@Entity
@Table(name = "record_list_action_cfg")
@XmlRootElement(name = "recordListActionCfg", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = RecordListActionCfg.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    RecordListActionCfg.PROP_ID,
    RecordListActionCfg.PROP_STATIC_TITLE,
    RecordListActionCfg.PROP_TITLE_EXPR,
    RecordListActionCfg.PROP_STATIC_DESCRIPTION,
    RecordListActionCfg.PROP_DESCRIPTION_EXPR,
    RecordListActionCfg.PROP_ICON_ID,
    RecordListActionCfg.PROP_TARGET,
    RecordListActionCfg.PROP_VISIBILITY_EXPR,
    RecordListActionCfg.PROP_UUID,
    RecordListActionCfg.PROP_REFERENCE_KEY,
    RecordListActionCfg.PROP_DIALOG_SIZE_KEY,
    RecordListActionCfg.PROP_SHOW_IN_RECORD_LIST_KEY,
    RecordListActionCfg.PROP_RECORD_UI_SECURITY_TYPE,
    RecordListActionCfg.PROP_ACTION_SECURITY_CFG,
    RecordListActionCfg.PROP_DIALOG_WIDTH_KEY,
    RecordListActionCfg.PROP_DIALOG_HEIGHT_KEY
})
@BreadcrumbProperty(value = RecordListActionCfg.PROP_REFERENCE_KEY, format = BreadcrumbText.recordTypeListActionKey,
    breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_PEERS)
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class RecordListActionCfg implements Id<Long>, ReadOnlyRecordAction, ListActionWithSecurity {
  public static final String LOCAL_PART = "RecordListActionCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String JOIN_COL_RECORD_LIST_ACTION_CFG_ID = "record_list_action_cfg_id";

  public static final String PROP_ID = "id";
  public static final String PROP_UUID = "uuid";
  public static final String PROP_STATIC_TITLE = "staticTitle";
  public static final String PROP_TITLE_EXPR = "titleExpr";
  public static final String PROP_STATIC_DESCRIPTION = "staticDescription";
  public static final String PROP_DESCRIPTION_EXPR = "descriptionExpr";
  public static final String PROP_ICON_ID = "iconId";
  public static final String PROP_TARGET = "target";
  public static final String PROP_VISIBILITY_EXPR = "visibilityExpr";
  public static final String PROP_REFERENCE_KEY = "referenceKey";
  public static final String PROP_DIALOG_SIZE_KEY = "dialogSize";
  public static final String PROP_SHOW_IN_RECORD_LIST_KEY = "showInRecordList";
  public static final String PROP_RECORD_UI_SECURITY_TYPE = "recordUiSecurityType";
  public static final String PROP_ACTION_SECURITY_CFG = "securityCfg";
  public static final String PROP_DIALOG_WIDTH_KEY = "dialogWidth";
  public static final String PROP_DIALOG_HEIGHT_KEY = "dialogHeight";
  public static final String COL_ID = "id";
  public static final String COL_SECURITY_CFG_ID = "security_cfg_id";
  public static final String RECORD_TYPE_ID = "record_type_id";

  private static final long serialVersionUID = 1L;

  private Long id;
  private String uuid;
  private String targetTypeStr;
  private String targetUuid;
  private String visibilityExpr = "=true()";
  private String iconId;
  private String title;
  private String description;
  private String referenceKey;
  private RecordTypePropertySource titleSource = RecordTypePropertySource.STATIC;
  private RecordTypePropertySource descriptionSource = RecordTypePropertySource.STATIC;
  private RecordTypeListActionSecurityCfg securityCfg;
  private RecordUiSecurityType recordUiSecurityType;
  private boolean showInRecordList = true;
  private Long recordTypeId;
  private ModalDialogLayoutWidth dialogWidth = DEFAULT_DIALOG_WIDTH;
  private ModalDialogLayoutHeight dialogHeight = DEFAULT_DIALOG_HEIGHT;

  public static final Function<RecordListActionCfg,Long> RECORD_LIST_ACTION_TO_ID = input -> input.getId();

  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;

  /**
   * Default constructor for JAXB compatibility and testing only
   */
  @VisibleForTesting
  public RecordListActionCfg() {
  }

  /**
   * Constructs a non-persisted RecordListActionCfg (id is not specified)
   */
  public RecordListActionCfg(
      Ref target,
      String visibilityExpr,
      String iconId,
      String title,
      String description,
      RecordTypePropertySource titleSource,
      RecordTypePropertySource descriptionSource) {
    setTarget(target);
    setVisibilityExpr(visibilityExpr);
    setIconId(iconId);
    setTitle(title);
    setDescription(description);
    setTitleSource(titleSource);
    setDescriptionSource(descriptionSource);
    setRecordUiSecurityType(EXPRESSION);
  }

  public RecordListActionCfg(DesignerDtoRecordListActionCfg dto) {
    setId(dto.getId());
    setUuid(dto.getUuid());
    setVisibilityExpr(dto.getVisibilityExpr());
    ProcessModelRef processModelRef = new ProcessModelRefImpl(dto.getProcessModelUuid());
    setTarget(processModelRef);
    setIconId(dto.getIconId());
    setTitle(dto.getTitle());
    setTitleSource(RecordTypePropertySource.fromText(dto.getTitleSource()));
    setDescription(dto.getDescription());
    setDescriptionSource(RecordTypePropertySource.fromText(dto.getDescriptionSource()));
    setReferenceKey(dto.getReferenceKey());
    setExpressionTransformationState(dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY);
    setDialogSize(dto.getDialogSize());
    setShowInRecordList(dto.isShowInRecordList());
    setSecurityCfg(dto.getSecurityCfg() == null ? null : new RecordTypeListActionSecurityCfg(dto.getSecurityCfg()));
    setRecordUiSecurityType(dto.getRecordUiSecurityType());
  }

  @Override
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  @Column(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  @VisibleForTesting
  public void setUuid(String uuid) {
    this.uuid = StringUtils.isBlank(uuid) ? null : uuid;
  }

  @Override
  @XmlElement
  @Column(name = "icon_id", nullable = false)
  public String getIconId() {
    return iconId;
  }

  public void setIconId(String iconId) {
    this.iconId = iconId;
  }

  @Override
  @Column(name = "title", length = Constants.COL_MAXLEN_EXPRESSION)
  @XmlTransient
  @Lob
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  @Transient
  @XmlTransient
  public RecordTypePropertySource getTitleSource() {
    return titleSource;
  }

  public void setTitleSource(RecordTypePropertySource titleSource){
    this.titleSource = titleSource;

    // Safety mechanism to ensure that we only allow valid description source / title source combos
    if(RecordTypePropertySource.DERIVED.equals(titleSource)){
      this.descriptionSource = titleSource;
    }
  }

  @Column(name = "title_src", nullable = false)
  @XmlTransient
  private byte getTitleSourceByte() {
    return getTitleSource().getCode();
  }

  private void setTitleSourceByte(byte sourceByte) {
    setTitleSource(RecordTypePropertySource.valueOf(sourceByte));
  }

  @Override
  @Transient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeActionTitleExpression,
    breadcrumbFlags=Breadcrumb.BREADCRUMB_GUIDANCE_SKIP_FOR_GUIDANCE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getTitleExpr() {
    return RecordTypePropertySource.EXPRESSION.equals(titleSource) ? this.title : null;
  }

  public void setTitleExpr(String titleExpr) {
    if (titleExpr != null) {
      setTitle(titleExpr);
    }
    if (!RecordTypePropertySource.EXPRESSION.equals(titleSource)) {
      setTitleSource(RecordTypePropertySource.EXPRESSION);
    }
  }

  @Override
  @Transient
  public String getStaticTitle() {
    return RecordTypePropertySource.STATIC.equals(titleSource) ? this.title : null;
  }

  public void setStaticTitle(String staticTitle) {
    if (staticTitle != null) {
      setTitle(staticTitle);
    }
    if (!RecordTypePropertySource.STATIC.equals(titleSource)) {
      setTitleSource(RecordTypePropertySource.STATIC);
    }
  }

  /**
   * The description of the record list action in the record list actions list
   */
  @Override
  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @XmlTransient
  @Lob
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  @Transient
  @XmlTransient
  public RecordTypePropertySource getDescriptionSource() {
    // Safety mechanism to ensure that we only allow valid description source / title source combos
    if(RecordTypePropertySource.DERIVED.equals(titleSource)){
      return RecordTypePropertySource.DERIVED;
    } else {
      return RecordTypePropertySource.DERIVED.equals(descriptionSource) ? RecordTypePropertySource.STATIC : descriptionSource;
    }
  }

  public void setDescriptionSource(RecordTypePropertySource descriptionSource){
    this.descriptionSource = descriptionSource;
  }

  @Column(name = "description_src", nullable = false)
  private byte getDescriptionSourceByte() {
    return getDescriptionSource().getCode();
  }

  private void setDescriptionSourceByte(byte sourceByte) {
    setDescriptionSource(RecordTypePropertySource.valueOf(sourceByte));
  }

  @Override
  @Transient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeActionDescriptionExpression,
      breadcrumbFlags=Breadcrumb.BREADCRUMB_GUIDANCE_SKIP_FOR_GUIDANCE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDescriptionExpr() {
    return RecordTypePropertySource.EXPRESSION.equals(descriptionSource) ? this.description : null;
  }

  public void setDescriptionExpr(String descriptionExpr) {
    if (descriptionExpr != null) {
      setDescription(descriptionExpr);
    }
    if (!RecordTypePropertySource.EXPRESSION.equals(descriptionSource)) {
      setDescriptionSource(RecordTypePropertySource.EXPRESSION);
    }
  }

  @Override
  @Transient
  public String getStaticDescription() {
    return RecordTypePropertySource.STATIC.equals(descriptionSource) ? this.description : null;
  }

  public void setStaticDescription(String staticDescription) {
    if (staticDescription != null) {
      setDescription(staticDescription);
    }
    if (!RecordTypePropertySource.STATIC.equals(descriptionSource)) {
      setDescriptionSource(RecordTypePropertySource.STATIC);
    }
  }

  @Override
  @XmlElement
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeActionVisibilityExpr,
      breadcrumbFlags=Breadcrumb.BREADCRUMB_GUIDANCE_SKIP_FOR_GUIDANCE)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Column(name = "visibility_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getVisibilityExpr() {
    return visibilityExpr;
  }

  public void setVisibilityExpr(String visibilityExpr) {
    this.visibilityExpr = visibilityExpr;
  }

  @Override
  @XmlElement(type = Object.class)
  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeActionTarget)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref getTarget() {
    QName qName = getTargetType();
    if (qName == null) {
      return null;
    }
    if (TypeQNameUtil.getQName(ProcessModelRef.class).equals(qName)) {
      return new ProcessModelRefImpl(getTargetUuid());
    } else {
      throw new IllegalStateException("No default JAXB class found for type " + qName);
    }
  }

  public void setTarget(Ref target) {
    Preconditions.checkNotNull(target, "Target is required");
    if (!(target instanceof ProcessModelRef)) {
      throw new UnsupportedOperationException("Unsupported target type " + target.getClass().getName());
    }
    Object targetUuidObj = target.getUuid();
    Preconditions.checkNotNull(targetUuidObj, "Uuid is required");
    if (!(targetUuidObj instanceof String)) {
      throw new UnsupportedOperationException("Unsupported uuid type " +
        targetUuidObj.getClass().getSimpleName());
    }
    setTargetUuid((String)targetUuidObj);
    QName qName = TypeQNameUtil.getQName(target.getClass());
    setTargetType(qName);
  }

  @Column(name = "target_type", nullable = false)
  private String getTargetTypeStr() {
    return targetTypeStr;
  }

  @SuppressWarnings("unused")
  private void setTargetTypeStr(String targetTypeStr) {
    this.targetTypeStr = targetTypeStr;
  }

  @Override
  @Column(name = "target_uuid", nullable = false, length = Constants.COL_MAXLEN_UUID)
  public String getTargetUuid() {
    return targetUuid;
  }

  private void setTargetUuid(String targetUuid) {
    this.targetUuid = targetUuid;
  }

  @Transient
  QName getTargetType() {
    return targetTypeStr == null ? null : QName.valueOf(targetTypeStr);
  }

  private void setTargetType(QName targetType) {
    Preconditions.checkNotNull(targetType);
    this.targetTypeStr = targetType.toString();
  }

  @Override
  @XmlElement
  @Column(name = "reference_key")
  public String getReferenceKey() {
    return referenceKey;
  }

  public void setReferenceKey(String referenceKey) {
    this.referenceKey = StringUtils.isBlank(referenceKey) ? null : referenceKey;
  }

  @Transient
  public RecordActionDialogSize getDialogSize() {
    return RecordActionDialogSizeToModalDialogLayoutSizesConverter.convertToRecordActionDialogSize(dialogHeight, dialogWidth);
  }

  public void setDialogSize(RecordActionDialogSize dialogSize) {
    // split dialogSize into width and height
    this.dialogWidth = RecordActionDialogSizeToModalDialogLayoutSizesConverter.convertToModalDialogLayoutWidth(dialogSize);
    this.dialogHeight = RecordActionDialogSizeToModalDialogLayoutSizesConverter.convertToModalDialogLayoutHeight(dialogSize);
  }

  @Transient
  public ModalDialogLayoutWidth getDialogWidth() {
    return dialogWidth;
  }

  public void setDialogWidth(ModalDialogLayoutWidth dialogWidth) {
    this.dialogWidth = dialogWidth;
  }

  @Transient
  public ModalDialogLayoutHeight getDialogHeight() {
    return dialogHeight;
  }

  public void setDialogHeight(ModalDialogLayoutHeight dialogHeight) {
    this.dialogHeight = dialogHeight;
  }

  @Column(name = "dialog_width", nullable = false)
  private byte getDialogWidthByte() {
    return convertToRecordActionModalWidthByte(dialogWidth);
  }

  private void setDialogWidthByte(byte index) {
    setDialogWidth(convertToModalDialogLayoutWidth(index));
  }

  @Column(name = "dialog_height", nullable = false)
  private byte getDialogHeightByte() {
    return RecordActionDialogHeightByteToModalDialogLayoutHeightConverter.convertToRecordActionDialogHeightByte(dialogHeight);
  }

  private void setDialogHeightByte(byte index) {
    setDialogHeight(RecordActionDialogHeightByteToModalDialogLayoutHeightConverter.convertToModalDialogLayoutHeight(index));
  }

  @XmlElement(name = PROP_ACTION_SECURITY_CFG)
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
  @JoinColumn(name = COL_SECURITY_CFG_ID, referencedColumnName = COL_ID)
  @HasForeignKeys(breadcrumb = BreadcrumbText.recordTypeViewsAndActionsSecurityCfg)
  public RecordTypeListActionSecurityCfg getSecurityCfg() {
    return securityCfg;
  }

  public void setSecurityCfg(RecordTypeListActionSecurityCfg actionSecurityCfg) {
    this.securityCfg = actionSecurityCfg;
  }

  @Transient
  public RecordUiSecurityType getRecordUiSecurityType() {
    return recordUiSecurityType;
  }

  public void setRecordUiSecurityType(RecordUiSecurityType recordUiSecurityType) {
    this.recordUiSecurityType = recordUiSecurityType;
  }

  @XmlTransient
  @Column(name = "security_type", nullable = false)
  private byte getRecordUiSecurityTypeByte() {
    if (recordUiSecurityType == null) {
      return 0;
    }

    switch (recordUiSecurityType) {
      case EXPRESSION:
        return 0;
      case GUIDED:
        return 1;
      default:
        return 0;
    }
  }

  private void setRecordUiSecurityTypeByte(byte index) {
    if (index == 0) {
      recordUiSecurityType = EXPRESSION;
    } else {
      recordUiSecurityType = GUIDED;
    }

    setRecordUiSecurityType(recordUiSecurityType);
  }

  private static final Function<RecordListActionCfg,String> selectTargetUuid = input -> input.getTargetUuid();

  public static Function<RecordListActionCfg,String> selectTargetUuid() {
    return selectTargetUuid;
  }

  @Override
  @Transient
  @XmlTransient
  public String getContextExpr() {
    return null;
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

  @Column(name = "show_in_record_list", nullable = false)
  public boolean getShowInRecordList() {
    return showInRecordList;
  }

  public void setShowInRecordList(boolean showInRecordList) {
    this.showInRecordList = showInRecordList;
  }

  @XmlTransient
  @Column(name = RECORD_TYPE_ID, insertable = false, updatable = false)
  public Long getRecordTypeId() {
    return recordTypeId;
  }

  public void setRecordTypeId(Long recordTypeId) {
    this.recordTypeId = recordTypeId;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  // Overriden Object Methods
  ///////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("targetTypeStr", targetTypeStr)
        .add("targetUuid", targetUuid)
        .add("visibilityExpr", visibilityExpr)
        .add("iconId", iconId)
        .add("title", title)
        .add("titleSource", titleSource)
        .add("description", description)
        .add("descriptionSource", descriptionSource)
        .add("uuid", uuid)
        .add("referenceKey", referenceKey)
        .add("showInRecordList", showInRecordList)
        .add("securityCfg", securityCfg)
        .add("recordUiSecurityType", recordUiSecurityType)
        .toString();
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((targetTypeStr == null) ? 0 : targetTypeStr.hashCode());
    result = prime * result + ((targetUuid == null) ? 0 : targetUuid.hashCode());
    result = prime * result + ((visibilityExpr == null) ? 0 : visibilityExpr.hashCode());
    result = prime * result + ((iconId == null) ? 0 : iconId.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + (titleSource == null ? 0 : titleSource.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (descriptionSource == null ? 0 : descriptionSource.hashCode());
    result = prime * result + (uuid == null ? 0 : uuid.hashCode());
    result = prime * result + (referenceKey == null ? 0 : referenceKey.hashCode());
    result = prime * result + (showInRecordList ? 1 : 0);
    result = prime * result + ((recordUiSecurityType == null) ? 0 : recordUiSecurityType.hashCode());
    result = prime * result + ((securityCfg == null) ? 0 : securityCfg.hashCode());
    result = prime * result + ((dialogWidth == null) ? 0 : dialogWidth.hashCode());
    result = prime * result + ((dialogHeight == null) ? 0 : dialogHeight.hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RecordListActionCfg)) {
      return false;
    }
    RecordListActionCfg other = (RecordListActionCfg)obj;
    return checkExpressionEquality(other) && checkDisplayPropertyEquality(other) && checkTargetEquality(other) && checkSecurityEquality(other);
  }

  private boolean checkSecurityEquality(RecordListActionCfg other) {
    if (recordUiSecurityType == null) {
      if (other.recordUiSecurityType != null) {
        return false;
      }
    } else if (!recordUiSecurityType.equals(other.recordUiSecurityType)) {
      return false;
    }
    if (securityCfg == null) {
      if (other.securityCfg != null) {
        return false;
      }
    } else if (!securityCfg.equals(other.securityCfg)) {
      return false;
    }
    return true;
  }

  public boolean checkExpressionEquality(RecordListActionCfg other) {
    if (visibilityExpr == null) {
      if (other.visibilityExpr != null) {
        return false;
      }
    } else if (!visibilityExpr.equals(other.visibilityExpr)) {
      return false;
    }
    return true;
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

  public boolean checkDisplayPropertyEquality(RecordListActionCfg other) {
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (iconId == null) {
      if (other.iconId != null) {
        return false;
      }
    } else if (!iconId.equals(other.iconId)) {
      return false;
    }
    if (title == null) {
      if (other.title != null) {
        return false;
      }
    } else if (!title.equals(other.title)) {
      return false;
    }
    if (titleSource != other.titleSource) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (descriptionSource != other.descriptionSource) {
      return false;
    }
    if (uuid == null) {
      if (other.uuid != null) {
        return false;
      }
    } else if (!uuid.equals(other.uuid)) {
      return false;
    }
    if (referenceKey == null) {
      if (other.referenceKey != null) {
        return false;
      }
    } else if (!referenceKey.equals(other.referenceKey)) {
      return false;
    }
    if (dialogWidth == null) {
      if (other.dialogWidth != null) {
        return false;
      }
    } else if (!dialogWidth.equals(other.dialogWidth)) {
      return false;
    }
    if (dialogHeight == null) {
      if (other.dialogHeight != null) {
        return false;
      }
    } else if (!dialogHeight.equals(other.dialogHeight)) {
      return false;
    }
    return showInRecordList == other.showInRecordList;
  }

  public boolean checkTargetEquality(RecordListActionCfg other) {
    if (targetTypeStr == null) {
      if (other.targetTypeStr != null) {
        return false;
      }
    } else if (!targetTypeStr.equals(other.targetTypeStr)) {
      return false;
    }
    if (targetUuid == null) {
      if (other.targetUuid != null) {
        return false;
      }
    } else if (!targetUuid.equals(other.targetUuid)) {
      return false;
    }
    return true;
  }
}
