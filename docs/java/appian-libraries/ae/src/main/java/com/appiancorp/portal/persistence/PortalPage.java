package com.appiancorp.portal.persistence;

import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;
import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.Expression;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.navigation.Page;
import com.appiancorp.navigation.PageWidth;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.ExpressionState;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.PortalPageDto;
import com.appiancorp.type.refs.Ref;
import com.google.common.base.Strings;

@SuppressWarnings({"checkstyle:classfanoutcomplexity", "checkstyle:cyclomaticcomplexity"})
@Hidden
@Entity
@Table(name = "navigation_node") // LATER (AN-230730): rename table to portal_page
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(propOrder = {
    Id.LOCAL_PART,
    PortalPage.PROP_UUID,
    PortalPage.PROP_NAME_EXPR,
    PortalPage.PROP_STATIC_NAME,
    PortalPage.PROP_DESCRIPTION,
    PortalPage.PROP_URL_STUB,
    PortalPage.PROP_UI_OBJECT,
    PortalPage.PROP_ICON_ID,
    PortalPage.PROP_VISIBILITY,
    PortalPage.PROP_PAGE_WIDTH,
    PortalPage.PROP_CHILDREN,
    PortalPage.PROP_OBJECT_INPUTS,
    PortalPage.PROP_ARE_URL_PARAMS_ENCRYPTED,
    PortalPage.PROP_ARE_AUTOMATIC_CONTEXT_UPDATES_ENABLED
})
@BreadcrumbProperty(value = PortalPage.PROP_URL_STUB, formatProperty = PortalPage.FORMAT_PROPERTY,
    breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_PEERS)
public class PortalPage extends Page<PortalPage,PortalPageInput> implements Id<Long>, Uuid<String>, ExpressionState {
  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_ICON_ID = "f016";
  public static final String DEFAULT_VISIBILITY_EXPR = "true";
  public static final String FORMAT_PROPERTY = "breadcrumbFormat";

  private transient ExpressionTransformationState expressionTransformationState = STORED;

  public PortalPage() {
    this.isStaticName = true;
  }

  /**
   * Given the PortalPageDto design time CDT, constructs a PortalPage.
   * The NavigationNode CDT is the corresponding runtime CDT.
   */
  public PortalPage(PortalPageDto portalPageDto) {
    this.id = portalPageDto.getId();
    this.uuid = portalPageDto.getUuid();
    this.name = portalPageDto.getNameExpr();
    this.description = portalPageDto.getDescription();
    this.urlStub = portalPageDto.getUrlStub();
    this.objectType = portalPageDto.getObjectType();
    this.objectUuid = portalPageDto.getObjectUuid();
    this.iconId = portalPageDto.getIcon();
    this.visibilityExpr = DEFAULT_VISIBILITY_EXPR;
    this.isStaticName = portalPageDto.isStaticName();
    this.expressionTransformationState = portalPageDto.isExprsAreEvaluable() ? STORED : DISPLAY;
    this.pageWidth = PageWidth.fromText(portalPageDto.getPageWidth());
    this.areUrlParamsEncrypted = portalPageDto.isAreUrlParamsEncrypted();
    this.autoContextUpdateEnabled = portalPageDto.isAutoContextUpdateEnabled();

    this.objectInputs = Optional.ofNullable(portalPageDto.getObjectInputs())
        .orElseGet(ArrayList::new)
        .stream()
        .map(PortalPageInput::new)
        .collect(Collectors.toList());

    for (com.appiancorp.type.cdt.value.PortalPageDto childCDT : portalPageDto.getChildren()) {
      PortalPage childPage = new PortalPage(childCDT);
      children.add(childPage);
    }

    if (this.id.intValue() == com.appiancorp.core.Constants.INTEGER_NULL) {
      this.id = null;
    }
  }

  /**
   * Converts the PortalPage to a PortalPageDto design time CDT.
   */
  public PortalPageDto toCdt() {
    PortalPageDto portalPageDto = new PortalPageDto();
    portalPageDto.setId(id);
    portalPageDto.setUuid(uuid);
    portalPageDto.setNameExpr(name);
    portalPageDto.setDescription(description);
    portalPageDto.setUrlStub(urlStub);
    portalPageDto.setObjectType(objectType);
    portalPageDto.setObjectUuid(objectUuid);
    portalPageDto.setIcon(iconId);
    portalPageDto.setVisibilityExpr(visibilityExpr);
    portalPageDto.setStaticName(isStaticName);
    portalPageDto.setAreUrlParamsEncrypted(areUrlParamsEncrypted);
    portalPageDto.setExprsAreEvaluable(
        !expressionTransformationState.requiresTransformationToStoredFormBeforeEvaluation());
    portalPageDto.setPageWidth(pageWidth.getText());
    portalPageDto.setIsGroup(isGroup());
    portalPageDto.setAutoContextUpdateEnabled(autoContextUpdateEnabled);

    portalPageDto.setObjectInputs(Optional.ofNullable(objectInputs)
        .orElseGet(ArrayList::new)
        .stream()
        .map(PortalPageInput::toCdt)
        .collect(Collectors.toList()));

    List<PortalPageDto> childrenCdts = new ArrayList<>();
    for (PortalPage childPage : getChildren()) {
      childrenCdts.add(childPage.toCdt());
    }
    portalPageDto.setChildren(childrenCdts);
    return portalPageDto;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return super.getId();
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return super.getUuid();
  }

  // LATER (AN-230730): rename column to name
  @Column(name = "name_expr", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @XmlTransient
  public String getName() {
    return super.getName();
  }

  @Transient
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.navigationNodeNameExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @XmlElement
  public String getNameExpr() {
    return super.getNameExpr();
  }

  @Transient
  @XmlElement
  public String getStaticName() {
    return super.getStaticName();
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement
  public String getDescription() {
    return super.getDescription();
  }

  @Column(name = "url_stub", nullable = false)
  @XmlElement
  public String getUrlStub() {
    return super.getUrlStub();
  }

  @Column(name = "appian_object_type")
  public String getObjectType() {
    return super.getObjectType();
  }

  @Column(name = "appian_object_uuid")
  public String getObjectUuid() {
    return super.getObjectUuid();
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "navigation_node_id")
  @XmlElement(name ="navigationNodeRuleInput")
  @HasForeignKeys(breadcrumb = BreadcrumbText.navigationNodeRuleInputs)
  @OrderColumn(name = "order_idx")
  public List<PortalPageInput> getObjectInputs() {
    return super.getObjectInputs();
  }

  public void setObjectInputs(List<PortalPageInput> objectInputs) {
    super.setObjectInputs(objectInputs);
  }

  @Column(name = "icon_id", nullable = false)
  @XmlElement
  public String getIconId() {
    return super.getIconId();
  }

  @Column(name = "visibility_expr", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.navigationNodeVisibilityExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Lob
  @XmlElement
  public String getVisibilityExpr() {
    return super.getVisibilityExpr();
  }

  @Transient
  public Expression getVisibilityExpression() {
    return super.getVisibilityExpression();
  }

  @Column(name = "is_static_name", nullable = false)
  @XmlTransient
  public Boolean getIsStaticName() {
    return super.getIsStaticName();
  }

  @Column(name = "are_url_params_encrypted", nullable = false)
  @XmlElement(defaultValue = "true")
  public boolean getAreUrlParamsEncrypted() {
    return super.getAreUrlParamsEncrypted();
  }

  @Column(name = "auto_context_update_enabled", nullable = false)
  @XmlElement(defaultValue = "false")
  public boolean getAutoContextUpdateEnabled() {
    return super.getAutoContextUpdateEnabled();
  }

  @Transient
  @XmlElement
  public PageWidth getPageWidth() {
    return super.getPageWidth();
  }

  @Column(name = "page_width", nullable = false)
  public byte getPageWidthCode() {
    return super.getPageWidthCode();
  }

  @XmlElement(name = "navigationNode") // // LATER (AN-230742): rename to page
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "parent_navigation_node_id") // LATER (AN-230730): rename column to parent_page_id
  @OrderColumn(name = "order_idx")
  @HasForeignKeys(breadcrumb = BreadcrumbText.SKIP)
  public List<PortalPage> getChildren() {
    return super.getChildren();
  }

  @Override
  public void setChildren(List<PortalPage> children) {
    super.setChildren(children);
  }

  @Transient
  public QName getObjectTypeQname() {
    if (Strings.isNullOrEmpty(objectType)) {
      return null;
    }

    return QName.valueOf(objectType);
  }

  @Transient
  @XmlElement(type = Object.class)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.navigationNodeUiObject)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<Long,String> getUiObject() {
    return super.getUiObject();
  }

  @Transient
  /**
   * Returns which format the page should show up in breadcrumb
   * IMPORTANT: this method's name must be "get" + {@link com.appiancorp.portal.persistence.PortalPage#FORMAT_PROPERTY} constant defined above
   */
  public BreadcrumbText getBreadcrumbFormat() {
    return isGroup() ?  BreadcrumbText.navigationNodeGroupStubFormat: BreadcrumbText.navigationNodeStubFormat;
  }
}
