package com.appiancorp.sites;

import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;
import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.core.data.ImmutableDictionary;
import com.appiancorp.core.expr.AppianScriptContext;
import com.appiancorp.core.expr.AppianScriptContextBuilder;
import com.appiancorp.core.expr.Expression;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.ParseFactory;
import com.appiancorp.core.expr.exceptions.ExpressionRuntimeException;
import com.appiancorp.core.expr.exceptions.ScriptException;
import com.appiancorp.core.expr.portable.cdt.DataFabricInsightsHomeConstants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.BreadcrumbProperty;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.navigation.Page;
import com.appiancorp.navigation.PageWidth;
import com.appiancorp.services.ServiceContext;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.sites.exceptions.SitePageTitleEvaluationException;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.ExpressionState;
import com.appiancorp.type.ExtendedDataTypeProvider;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.SitePageNameEvalData;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.Ref;

@SuppressWarnings({"checkstyle:cyclomaticcomplexity"})
@Hidden
@Entity
@Table(name = "site_page")
@XmlRootElement(name = "site_page", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = SitePage.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    Id.LOCAL_PART,
    SitePage.PROP_UUID,
    SitePage.PROP_NAME_EXPR,
    SitePage.PROP_STATIC_NAME,
    SitePage.PROP_DESCRIPTION,
    SitePage.PROP_URL_STUB,
    SitePage.PROP_UI_OBJECT,
    SitePage.PROP_ICON_ID,
    SitePage.PROP_VISIBILITY,
    SitePage.PROP_PAGE_WIDTH,
    SitePage.PROP_CHILDREN,
    SitePage.PROP_OBJECT_INPUTS,
    SitePage.PROP_ARE_URL_PARAMS_ENCRYPTED,
    SitePage.PROP_ARE_AUTOMATIC_CONTEXT_UPDATES_ENABLED
})
@IgnoreJpa
@BreadcrumbProperty(value = SitePage.PROP_URL_STUB, formatProperty = SitePage.PROP_BREADCRUMB_FORMAT,
    breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_SEPARATE_NODES)
public class SitePage extends Page<SitePage, SitePageInput> implements Id<Long>, ExpressionState, Uuid<String> {
  public static final String LOCAL_PART = "SitePage";
  public SitePage() {
    isStaticName = true;
  }

  public SitePage(final SitePage sitePage, boolean includeIdInCopy) {
    if (includeIdInCopy) {
      this.id = sitePage.id;
    }
    this.uuid = sitePage.getUuid();
    this.name = sitePage.getName();
    this.description = sitePage.getDescription();
    this.urlStub = sitePage.getUrlStub();
    this.objectUuid = sitePage.getObjectUuid();
    this.objectType = sitePage.getObjectType();
    this.iconId = sitePage.getIconId();
    this.visibilityExpr = sitePage.visibilityExpr;
    this.expressionTransformationState = sitePage.expressionTransformationState;
    this.isStaticName = sitePage.isStaticName;
    this.pageWidth = sitePage.getPageWidth();
    this.children = sitePage.getChildren().stream()
        .map(child -> new SitePage(child, includeIdInCopy))
        .collect(Collectors.toList());
    this.objectInputs = Optional.ofNullable(sitePage.getObjectInputs())
        .orElseGet(ArrayList::new)
        .stream()
        .map(input -> new SitePageInput(input, includeIdInCopy))
        .collect(Collectors.toList());
    this.areUrlParamsEncrypted = sitePage.getAreUrlParamsEncrypted();
    this.autoContextUpdateEnabled = sitePage.getAutoContextUpdateEnabled();
    this.siteBrandingEnabled = sitePage.getSiteBrandingEnabled();
    this.darkThemeEnabled = sitePage.getDarkThemeEnabled();
  }

  public SitePage(final com.appiancorp.type.cdt.SitePageTemplate sitePageTemplateCdt) {
    this.id = sitePageTemplateCdt.getId();
    this.uuid = sitePageTemplateCdt.getUuid();
    this.name = sitePageTemplateCdt.getNameExpr();
    this.description = sitePageTemplateCdt.getDescription();
    this.urlStub = sitePageTemplateCdt.getUrlStub();
    this.objectUuid = sitePageTemplateCdt.getObjectUuid();
    this.objectType = sitePageTemplateCdt.getObjectType();
    this.iconId = sitePageTemplateCdt.getIcon();
    this.visibilityExpr = sitePageTemplateCdt.getVisibilityExpr();
    this.expressionTransformationState = getExpressionTransformationState(sitePageTemplateCdt.isExprsAreEvaluable());
    this.isStaticName = sitePageTemplateCdt.isStaticName();
    this.pageWidth = PageWidth.fromText(sitePageTemplateCdt.getPageWidth());
    this.children = sitePageTemplateCdt.getChildren().stream()
        .map(child -> new SitePage(child))
        .collect(Collectors.toList());
    this.objectInputs = Optional.ofNullable(sitePageTemplateCdt.getObjectInputs())
        .orElseGet(ArrayList::new)
        .stream()
        .map(SitePageInput::new)
        .collect(Collectors.toList());
    this.areUrlParamsEncrypted = sitePageTemplateCdt.isAreUrlParamsEncrypted();
    this.autoContextUpdateEnabled = sitePageTemplateCdt.isAutoContextUpdateEnabled();
    this.siteBrandingEnabled = sitePageTemplateCdt.isSiteBrandingEnabled();
    this.darkThemeEnabled = sitePageTemplateCdt.isDarkThemeEnabled();
  }

  public com.appiancorp.type.cdt.NavigationNode toRuntimeCdt(ServiceContext sc, ImmutableDictionary pageUuidToEvaluatedNameMap) {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());
    com.appiancorp.type.cdt.NavigationNode page = new com.appiancorp.type.cdt.NavigationNode(dtp);
    page.setUuid(uuid);
    page.setName((String) pageUuidToEvaluatedNameMap.get(uuid).getValue());
    page.setDescription(description);
    page.setUrlStub(urlStub);
    page.setObjectUuid(objectUuid);
    page.setObjectType(objectType);
    page.setIcon(iconId);
    page.setPageWidth(pageWidth.getText());
    page.setChildren(getChildren().stream()
        .map(sitePage -> sitePage.toRuntimeCdt(sc, pageUuidToEvaluatedNameMap))
        .collect(Collectors.toList()));
    page.setIsGroup(isGroup());
    page.setObjectInputs(Optional.ofNullable(objectInputs)
        .orElseGet(ArrayList::new)
        .stream()
        .map(SitePageInput::toCdt)
        .collect(Collectors.toList()));
    page.setAreUrlParamsEncrypted(areUrlParamsEncrypted);
    page.setAutoContextUpdateEnabled(autoContextUpdateEnabled);
    page.setSiteBrandingEnabled(siteBrandingEnabled);
    page.setDarkThemeEnabled(darkThemeEnabled);
    return page;
  }

  public String evalSitePageName(ServiceContext sc) {
    AppianScriptContext context = AppianScriptContextBuilder.init()
        .serviceContext(sc)
        .buildTop();
    return evalSitePageName(context, isStaticName, name, expressionTransformationState, urlStub);
  }

  public static String evalSitePageName(AppianScriptContext context, SitePageNameEvalData nameEvalData) {
    return evalSitePageName(context, nameEvalData.isStaticName(), nameEvalData.getNameExpr(),
        getExpressionTransformationState(nameEvalData.isExprsAreEvaluable()), nameEvalData.getUrlStub());
  }

  public static String evalSitePageName(AppianScriptContext context, Boolean isStaticName, String name,
      ExpressionTransformationState expressionTransformationState, String urlStub) {
    if (isStaticName == null || isStaticName) {
      return name;
    } else {
      try {
        return String.valueOf(ParseFactory.create(getNameExpression(name, expressionTransformationState)).eval(context));
      } catch (ScriptException | ExpressionRuntimeException e) {
        throw new SitePageTitleEvaluationException(ErrorCode.SITE_TITLE_EVALUATION_ERROR, urlStub, e.getMessage());
      }
    }
  }

  public com.appiancorp.type.cdt.SitePageTemplate toTemplateCdt(){
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());
    com.appiancorp.type.cdt.SitePageTemplate page = new com.appiancorp.type.cdt.SitePageTemplate(dtp);
    page.setId(id);
    page.setUuid(uuid);
    page.setNameExpr(name);
    page.setDescription(description);
    page.setUrlStub(urlStub);
    page.setObjectUuid(objectUuid);
    page.setObjectType(objectType);
    page.setIcon(iconId);
    page.setVisibilityExpr(visibilityExpr);
    page.setStaticName(isStaticName);
    page.setPageWidth(pageWidth.getText());
    page.setExprsAreEvaluable(getExprAreEvaluable());
    page.setChildren(getChildren().stream()
        .map(childNode -> childNode.toTemplateCdt())
        .collect(Collectors.toList()));
    page.setIsGroup(isGroup());
    page.setObjectInputs(Optional.ofNullable(objectInputs)
        .orElseGet(ArrayList::new)
        .stream()
        .map(SitePageInput::toCdt)
        .collect(Collectors.toList()));
    page.setAreUrlParamsEncrypted(areUrlParamsEncrypted);
    page.setAutoContextUpdateEnabled(autoContextUpdateEnabled);
    page.setSiteBrandingEnabled(siteBrandingEnabled);
    page.setDarkThemeEnabled(darkThemeEnabled);
    return page;
  }

  public SitePageNameEvalData toNameEvalDataCdt(){
    SitePageNameEvalData page = new SitePageNameEvalData();
    page.setUuid(uuid);
    page.setNameExpr(name);
    page.setUrlStub(urlStub);
    page.setStaticName(isStaticName);
    page.setExprsAreEvaluable(getExprAreEvaluable());
    return page;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return super.getId();
  }

  @Column(name = "name", length = Constants.COL_MAXLEN_EXPRESSION)
  @XmlTransient
  @Lob
  public String getName() {
    return super.getName();
  }

  @Transient
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.sitePageNameExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getNameExpr() {
    return super.getNameExpr();
  }

  @Transient
  @XmlTransient
  public Expression getNameExpression() {
    return super.getNameExpression();
  }

  @Transient
  public String getStaticName() {
    return super.getStaticName();
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return super.getDescription();
  }

  @Column(name = "url_stub", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUrlStub() {
    return super.getUrlStub();
  }

  @Column(name = "appian_object_uuid", nullable = true, length = Constants.COL_MAXLEN_UUID)
  @XmlTransient
  public String getObjectUuid() {
    return super.getObjectUuid();
  }

  @Column(name = "appian_object_type", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlTransient
  public String getObjectType() {
    return super.getObjectType();
  }

  @Transient
  @XmlTransient
  public QName getObjectTypeQname() {
    return super.getObjectTypeQname();
  }

  @Transient
  @XmlElement(type = Object.class)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.sitePageUiObject)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<Long,String> getUiObject() {
    return super.getUiObject();
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "site_page_id")
  @XmlElement(name ="sitePageInput")
  @HasForeignKeys(breadcrumb = BreadcrumbText.sitePageInputs)
  @OrderColumn(name = "order_idx")
  public List<SitePageInput> getObjectInputs() {
    return objectInputs;
  }

  public void setObjectInputs(List<SitePageInput> objectInputs) {
    this.objectInputs = objectInputs;
  }

  @Column(name = "icon_id", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getIconId() {
    return super.getIconId();
  }

  @Column(name = "visibility", length = Constants.COL_MAXLEN_EXPRESSION)
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.sitePageVisibilityExpression)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Lob
  public String getVisibilityExpr() {
    return super.getVisibilityExpr();
  }

  @Transient
  @XmlTransient
  public Expression getVisibilityExpression() {
    return super.getVisibilityExpression();
  }

  @Column(name = "is_static_name")
  @XmlTransient
  public Boolean getIsStaticName() {
    return super.getIsStaticName();
  }

  @Transient
  @XmlElement(name = "pageWidth")
  public PageWidth getPageWidth() {
    return pageWidth == null ? PageWidth.STANDARD : pageWidth;
  }

  @SuppressWarnings("unused")
  @Column(name = "page_width", nullable = false)
  @XmlTransient
  public byte getPageWidthCode() {
    return super.getPageWidthCode();
  }

  @Transient
  @XmlTransient
  @Override
  public ExpressionTransformationState getExpressionTransformationState() {
    return super.getExpressionTransformationState();
  }

  /**
   * Updates the expression transformation state for self, and all descendent pages
   * @param state The expression transformation state to change to
   */
  public void setAllExpressionTransformationState(ExpressionTransformationState state) {
    this.expressionTransformationState = state;
    this.children.forEach(child -> {
      child.setAllExpressionTransformationState(expressionTransformationState);
    });
  }

  // Used by Java Serialization
  private Object readResolve() {
    this.expressionTransformationState = STORED;
    return this;
  }

  @Transient
  @XmlTransient
  public boolean getExprAreEvaluable() {
    return !expressionTransformationState.requiresTransformationToStoredFormBeforeEvaluation();
  }

  public static ExpressionTransformationState getExpressionTransformationState(boolean exprAreEvaluable) {
    return exprAreEvaluable ? STORED : DISPLAY;
  }

  @XmlElement(name = "page")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "parent_site_page_id")
  @OrderColumn(name = "order_idx")
  @HasForeignKeys(breadcrumb = BreadcrumbText.SKIP)
  public List<SitePage> getChildren() {
    return super.getChildren();
  }

  @Override
  public void setChildren(List<SitePage> children) {
    super.setChildren(children);
  }

  @Transient
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return super.getUuid();
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

  @Override
  @Column(name = "site_branding_enabled", nullable = false)
  @XmlTransient
  public boolean getSiteBrandingEnabled() {
    return super.getSiteBrandingEnabled();
  }

  @Override
  @Column(name = "dark_theme_enabled", nullable = false)
  @XmlTransient
  public boolean getDarkThemeEnabled() {
    return super.getDarkThemeEnabled();
  }

  @Transient
  public boolean isGroup() {
    // A SitePage is a group if it has no backing objectUuid but does have an iconId.
    // A dummy SitePage also has no backing objectUuid, but it doesn't have an iconId.
    // A Data Fabric Insight page doesn't have an icon and also will not have a backing objectUuid
    return Strings.isNullOrEmpty(objectUuid) && !Strings.isNullOrEmpty(iconId) &&
        !DataFabricInsightsHomeConstants.QNAME.equals(getObjectTypeQname());
  }

  @Transient
  public BreadcrumbText getBreadcrumbFormat() {
    return isGroup() ? BreadcrumbText.sitePagesGroupFormat : BreadcrumbText.sitePagesStubFormat;
  }

  public static final String generateUuidForPage(String siteUuid, String pageUrlStub) {
    String nameAsString = siteUuid + pageUrlStub;
    byte[] name = nameAsString.getBytes(StandardCharsets.UTF_8);
    return UUID.nameUUIDFromBytes(name).toString();
  }
}
