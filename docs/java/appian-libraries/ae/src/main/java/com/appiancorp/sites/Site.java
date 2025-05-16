package com.appiancorp.sites;

import static com.appiancorp.branding.BrandingCfgSource.DEFAULT;
import static com.appiancorp.branding.BrandingCfgSource.DOCUMENT;
import static com.appiancorp.branding.BrandingCfgSource.ENUM;
import static com.appiancorp.branding.BrandingCfgSource.EXPRESSION;
import static com.appiancorp.branding.BrandingCfgSource.STATIC;
import static com.appiancorp.branding.BrandingCfgSource.WEB_ADDRESS;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_FAVICON_UUID;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_LOGO_UUID;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_PRIMARY_NAV_LAYOUT_TYPE;
import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;
import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;
import static com.appiancorp.sites.SiteBrandingCfgKey.ACCENT_COLOR;
import static com.appiancorp.sites.SiteBrandingCfgKey.BUTTON_LABEL_CASE;
import static com.appiancorp.sites.SiteBrandingCfgKey.BUTTON_SHAPE;
import static com.appiancorp.sites.SiteBrandingCfgKey.DIALOG_SHAPE;
import static com.appiancorp.sites.SiteBrandingCfgKey.FAVICON;
import static com.appiancorp.sites.SiteBrandingCfgKey.HEADER_BKGD_COLOR;
import static com.appiancorp.sites.SiteBrandingCfgKey.INPUT_SHAPE;
import static com.appiancorp.sites.SiteBrandingCfgKey.LOADING_BAR_COLOR;
import static com.appiancorp.sites.SiteBrandingCfgKey.LOGO;
import static com.appiancorp.sites.SiteBrandingCfgKey.LOGO_ALT_TEXT;
import static com.appiancorp.sites.SiteBrandingCfgKey.NAV_BAR_STYLE;
import static com.appiancorp.sites.SiteBrandingCfgKey.PRIMARY_NAV_LAYOUT_TYPE;
import static com.appiancorp.sites.SiteBrandingCfgKey.SELECTED_TAB_COLOR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.base.MultilineToStringHelper;
import com.appian.core.base.ToStringFunction;
import com.appian.core.persist.Constants;
import com.appiancorp.branding.BrandingCfgSource;
import com.appiancorp.common.FieldVisibility;
import com.appiancorp.common.monitoring.ProductMetricsAggregatedDataCollector;
import com.appiancorp.core.data.ImmutableDictionary;
import com.appiancorp.core.data.Record;
import com.appiancorp.core.expr.AppianScriptContext;
import com.appiancorp.core.expr.AppianScriptContextBuilder;
import com.appiancorp.core.expr.Domain;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.ParseFactory;
import com.appiancorp.core.expr.exceptions.ScriptException;
import com.appiancorp.core.expr.portable.Type;
import com.appiancorp.core.expr.portable.Value;
import com.appiancorp.core.expr.portable.cdt.NavigationLayoutType;
import com.appiancorp.core.expr.portable.cdt.SitePageNameEvalDataConstants;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.locking.NeedsLockValidation;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.HasVisibility;
import com.appiancorp.security.Visibility;
import com.appiancorp.security.Visibility.VisibilityFlags;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.services.ServiceContext;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.ExtendedDataTypeProvider;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.NavigationNode;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.DocumentRefImpl;
import com.appiancorp.type.refs.GroupRef;
import com.appiancorp.type.refs.GroupRefImpl;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.SiteRef;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Hidden
@Entity
@Table(name = Site.SITE_TABLE)
@XmlRootElement(name = Site.SITE_TABLE, namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE)
@XmlType(name = Site.LOCAL_PART,
  namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE,
  propOrder = {
  Id.LOCAL_PART, Uuid.LOCAL_PART, Site.PROP_NAME,
  Site.PROP_DESCRIPTION, Site.PROP_URL_STUB, Site.PROP_PAGES,
      Site.PROP_VISIBILITY, Site.PROP_HEADER_BKGD_COLOR,
      Site.PROP_SELECTED_TAB_COLOR, Site.PROP_FAVICON_REF,
      Site.PROP_FAVICON_HREF, Site.PROP_LOGO_REF, Site.PROP_LOGO_HREF,
      Site.PROP_HEADER_BKGD_COLOR_EXPR, Site.PROP_SELECTED_TAB_COLOR_EXPR,
      Site.PROP_ACCENT_COLOR, Site.PROP_ACCENT_COLOR_EXPR,
      Site.PROP_LOGO_EXPR, Site.PROP_FAVICON_EXPR, Site.PROP_SHOW_NAME,
      Site.PROP_TEMPO_LINK_VISIBILITY, Site.PROP_TEMPO_LINK_VIEWERS,
      Site.PROP_SHOW_RECORD_NEWS, Site.PROP_TASKS_IN_SITES_VISIBILITY,
      Site.PROP_TASKS_IN_SITES_VIEWERS, Site.PROP_LOGO_ALT_TEXT_STATIC,
      Site.PROP_LOGO_ALT_TEXT_EXPR, Site.PROP_BUTTON_SHAPE,
      Site.PROP_LOADING_BAR_COLOR, Site.PROP_LOADING_BAR_COLOR_EXPR,
      Site.PROP_NAV_BAR_STYLE, Site.PROP_INPUT_SHAPE, Site.PROP_SYSTEM,
      Site.PROP_DISPLAY_NAME, Site.PROP_IS_STATIC_DISPLAY_NAME,
      Site.PROP_DISPLAY_NAME_IS_EVALUABLE,
      Site.PROP_PRIMARY_NAVIGATION_LAYOUT_TYPE,
      Site.PROP_BUTTON_LABEL_CASE,
      Site.PROP_DIALOG_SHAPE})
@XmlSeeAlso({GroupRefImpl.class})
@IgnoreJpa
@Tracked
public class Site implements SiteWithPages, HasTypeQName, HasRoleMap, HasVisibility,
    NeedsLockValidation, HasVersionHistory {
  private static final long serialVersionUID = 1L;

  // This needs to match the name of the IX Type configured in K
  public static final String LOCAL_PART = "SiteDesignObject";
  public static final QName QNAME = new QName(com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String SITE_TABLE = "site";
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public static final String PROP_VERSION_UUID = "versionUuid";
  public static final String PROP_NAME = "name";
  public static final String PROP_IS_STATIC_DISPLAY_NAME = "isStaticDisplayName";
  public static final String PROP_DISPLAY_NAME = "displayName";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_URL_STUB = "urlStub";
  public static final String PROP_PAGES = "pages";
  public static final String PROP_VISIBILITY = "visibility";
  public static final String PROP_LOGO_REF = "logoRef";
  public static final String PROP_LOGO_HREF = "logoHref";
  public static final String PROP_LOGO_EXPR = "logoExpr";
  public static final String PROP_LOGO_ALT_TEXT_STATIC = "staticLogoAltText";
  public static final String PROP_LOGO_ALT_TEXT_EXPR = "logoAltTextExpr";
  public static final String PROP_FAVICON_REF = "faviconRef";
  public static final String PROP_FAVICON_HREF = "faviconHref";
  public static final String PROP_FAVICON_EXPR = "faviconExpr";
  public static final String PROP_HEADER_BKGD_COLOR = "headerBackgroundColor";
  public static final String PROP_HEADER_BKGD_COLOR_EXPR = "headerBackgroundColorExpr";
  public static final String PROP_SELECTED_TAB_COLOR = "selectedTabBackgroundColor";
  public static final String PROP_SELECTED_TAB_COLOR_EXPR = "selectedTabBackgroundColorExpr";
  public static final String PROP_ACCENT_COLOR = "accentColor";
  public static final String PROP_ACCENT_COLOR_EXPR = "accentColorExpr";
  public static final String PROP_LOADING_BAR_COLOR = "loadingBarColor";
  public static final String PROP_LOADING_BAR_COLOR_EXPR = "loadingBarColorExpr";
  public static final String PROP_SHOW_NAME = "showName";
  public static final String PROP_TEMPO_LINK_VISIBILITY = "tempoLinkVisibility";
  public static final String PROP_TEMPO_LINK_VIEWERS = "tempoLinkViewers";
  public static final String PROP_SHOW_RECORD_NEWS = "showRecordNews";
  public static final String PROP_TASKS_IN_SITES_VISIBILITY = "tasksInSitesVisibility";
  public static final String PROP_TASKS_IN_SITES_VIEWERS = "tasksInSitesViewers";
  public static final String PROP_TASKS_IN_SITES_VISIBILITY_CODE = "tasksInSitesVisibilityCode";
  public static final String PROP_BUTTON_SHAPE = "buttonShape";
  public static final String PROP_INPUT_SHAPE = "inputShape";
  public static final String PROP_NAV_BAR_STYLE = "navigationBarStyle";
  public static final String PROP_DISPLAY_NAME_IS_EVALUABLE = "nameExprIsEvaluable";
  public static final String PROP_PRIMARY_NAVIGATION_LAYOUT_TYPE = "primaryNavLayoutType";
  public static final String PROP_BUTTON_LABEL_CASE = "buttonLabelCase";
  public static final String PROP_DIALOG_SHAPE = "dialogShape";

  public static final String PROP_SYSTEM = "isSystem";

  public static final String DEFAULT_URL_STUB = "5Miphg"; //Used while pages un-exposed.

  private transient ExpressionTransformationState expressionTransformationState = STORED;

  private Long id;
  private String uuid;
  private String versionUuid;
  private String name;
  private boolean isStaticDisplayName;
  private String displayName;
  private String description;
  private String urlStub;
  private AuditInfo auditInfo = new AuditInfo();
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private List<SitePage> pages = new ArrayList<>();
  private int visibility = Visibility.DEFAULT_MASK;
  private boolean showName = false;
  private FieldVisibility tempoLinkVisibility = FieldVisibility.HIDDEN;
  private boolean showRecordNews = false;
  private List<SiteTempoLinkViewer> tempoLinkViewers_internal = new ArrayList<>();
  private List<Ref> tempoLinkViewers = new ArrayList<>();
  private FieldVisibility tasksInSitesVisibility = FieldVisibility.HIDDEN;
  private List<TasksInSitesViewer> tasksInSitesViewers_internal = new ArrayList<>();
  private List<Ref> tasksInSitesViewers = new ArrayList<>();
  private List<SiteBrandingCfg> siteBrandingCfgs = new ArrayList<>();
  private boolean isSystem = false;

  private boolean needsLockValidationOnUpdate = true;
  private transient volatile boolean isSiteInCache = false;
  private transient volatile boolean isNewSite = false;
  private Long versionOf;
  private transient volatile Long legacyId;

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES =  ImmutableSet.of(
      Roles.SITE_ADMIN,
      Roles.SITE_EDITOR,
      Roles.SITE_AUDITOR,
      Roles.SITE_VIEWER
  );

  public Site() {
  }

  public Site(String name, String description, String urlStub) {
    this.name = name;
    this.description = description;
    this.urlStub = urlStub;
  }

  public Site(SiteRef siteRef) {
    this.id = siteRef.getId();
    this.uuid = siteRef.getUuid();
  }

  public Site(final com.appiancorp.type.cdt.SiteTemplate templateCdt) {
    this.name = templateCdt.getName();
    this.urlStub = templateCdt.getUrlStub();
    this.description = templateCdt.getDescription();
    this.uuid = templateCdt.getUuid();
    this.versionUuid = templateCdt.getVersionUuid();
    this.displayName = templateCdt.getDisplayName();
    this.isStaticDisplayName = templateCdt.isIsStaticDisplayName();

    List<com.appiancorp.type.cdt.SitePageTemplate> cdtPageTemplates = templateCdt.getPages();
    for(com.appiancorp.type.cdt.SitePageTemplate page : cdtPageTemplates) {
      pages.add(new SitePage(page));
    }

    this.expressionTransformationState = templateCdt.isExprsAreEvaluable() ? STORED : DISPLAY;
    addLogoBrandingConfig(FAVICON, templateCdt.getFaviconUuid(), templateCdt.getFaviconHref(), templateCdt.getFaviconExpr(), DEFAULT_FAVICON_UUID);
    addLogoBrandingConfig(LOGO, templateCdt.getLogoUuid(), templateCdt.getLogoHref(), templateCdt.getLogoExpr(), DEFAULT_LOGO_UUID);
    updateSiteBranding(LOGO_ALT_TEXT, BrandingCfgSource.fromText(templateCdt.getLogoAltTextSource()), templateCdt.getLogoAltText());

    updateSiteBranding(BUTTON_SHAPE, ENUM, templateCdt.getButtonShape());
    updateSiteBranding(NAV_BAR_STYLE, ENUM, SiteNavBarStyle.friendlyNameToStoredName(templateCdt.getNavBarStyle()));
    updateSiteBranding(INPUT_SHAPE, ENUM, templateCdt.getInputShape());
    updateSiteBranding(PRIMARY_NAV_LAYOUT_TYPE, ENUM, templateCdt.getPrimaryNavLayoutType().value());
    updateSiteBranding(BUTTON_LABEL_CASE, ENUM, templateCdt.getButtonLabelCase());
    updateSiteBranding(DIALOG_SHAPE, ENUM, templateCdt.getDialogShape());

    addColorBrandingConfig(ACCENT_COLOR, templateCdt.getAccentColor(), templateCdt.getAccentColorExpr());
    addColorBrandingConfig(HEADER_BKGD_COLOR, templateCdt.getHeaderBackgroundColor(), templateCdt.getHeaderBackgroundColorExpr());
    addColorBrandingConfig(LOADING_BAR_COLOR, templateCdt.getLoadingBarColor(), templateCdt.getLoadingBarColorExpr());
    addColorBrandingConfig(SELECTED_TAB_COLOR, templateCdt.getSelectedTabBackgroundColor(), templateCdt.getSelectedTabBackgroundColorExpr());

    this.showName = templateCdt.isShowName();
    this.tempoLinkVisibility = FieldVisibility.fromText(templateCdt.getTempoLinkVisibility());
    for (String groupUuid : templateCdt.getTempoLinkViewerGroupUuids()) {
      this.tempoLinkViewers_internal.add(new SiteTempoLinkViewer(groupUuid));
    }

    this.showRecordNews = templateCdt.isShowRecordNews();
    this.tasksInSitesVisibility = FieldVisibility.fromText(templateCdt.getTasksInSitesVisibility());
    for (String groupUuid : templateCdt.getTasksInSitesViewerGroupUuids()) {
      this.tasksInSitesViewers_internal.add(new TasksInSitesViewer(groupUuid));
    }

    this.versionOf = templateCdt.getVersionOf();
  }

  public com.appiancorp.type.cdt.Site toCdt(ServiceContext sc) throws ScriptException {

    com.appiancorp.type.cdt.Site site = toCdtWithoutPages();
    List<NavigationNode> navigationNodeCdts = new ArrayList<>();
    ImmutableDictionary pageUuidToEvaluatedNameMap = getPageUuidToEvaluatedNameMap(sc);
    for(SitePage page : pages) {
      navigationNodeCdts.add(page.toRuntimeCdt(sc, pageUuidToEvaluatedNameMap));
    }
    site.setPages(navigationNodeCdts);

    return site;
  }

  public com.appiancorp.type.cdt.Site toCdtWithoutPages() {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());

    com.appiancorp.type.cdt.Site site = new com.appiancorp.type.cdt.Site(dtp);
    site.setId(id);
    site.setUuid(uuid);
    site.setName(name);
    site.setUrlStub(urlStub);
    site.setDescription(description);
    site.setDisplayName(displayName);
    site.setIsStaticDisplayName(isStaticDisplayName);

    site.setShowName(getShowName());
    site.setTempoLinkVisibility(getTempoLinkVisibility().getText());
    site.setTempoLinkViewerGroupUuids(FieldVisibility.GROUPS.equals(getTempoLinkVisibility()) ? getTempoLinkViewerGroupUuids() : new ArrayList<>());

    site.setShowRecordNews(getShowRecordNews());

    site.setTasksInSitesVisibility(getTasksInSitesVisibility().getText());
    site.setTasksInSitesViewerGroupUuids(FieldVisibility.GROUPS.equals(getTasksInSitesVisibility()) ? getTasksInSitesViewerGroupUuids() : new ArrayList<>());

    site.setButtonShape(getButtonShape().getText());
    site.setInputShape(getInputShape().getText());
    site.setNavBarStyle(getNavigationBarStyleEnum().getFriendlyName());
    site.setPrimaryNavLayoutType(getPrimaryNavLayoutType());

    site.setButtonLabelCase(getButtonLabelCase().getText());
    site.setDialogShape(getDialogShape().getText());

    return site;
  }

  @Transient
  private ImmutableDictionary getPageUuidToEvaluatedNameMap(ServiceContext sc) throws ScriptException {
    AppianScriptContext context = AppianScriptContextBuilder.init()
        .serviceContext(sc)
        .buildTop();

    Type listOfNameEvalDataType = Type.getType(SitePageNameEvalDataConstants.QNAME).listOf();
    Value pagesNameEvalData = listOfNameEvalDataType.valueOf(getPagesDataForNameEval());
    context.getBindings().set(Domain.ENVIRONMENT, "pagesNameEvalData", pagesNameEvalData);

    // NOTE: reason we do this in sail is because in sail, looping functions have a lot of logic set up
    // to determine 1) when to use parallel eval, and 2) spinning up the right number of threads
    return (ImmutableDictionary) ParseFactory.create(
        // language=sail
        "a!site_getPageUuidToEvaluatedNameMap(" +
            "pagesNameEvalData: env!pagesNameEvalData" +
        ")"
    ).eval(context).getValue();
  }

  @Transient
  private Record[] getPagesDataForNameEval() {
    return getFlattenedNestedPages(pages) // flatten the pages first so that we can parallelize better
        .map(page -> (Record) page.toNameEvalDataCdt().toValue_Value())
        .collect(Collectors.toList())
        .toArray(new Record[0]);
  }

  public static Stream<SitePage> getFlattenedNestedPages(List<SitePage> sitePages) {
    return sitePages.stream()
        .flatMap(page -> Stream.concat(
            Stream.of(page),
            getFlattenedNestedPages(page.getChildren())
        ));
  }

  public com.appiancorp.type.cdt.SiteTemplate toTemplateCdt() {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());
    com.appiancorp.type.cdt.SiteTemplate siteTemplate = new com.appiancorp.type.cdt.SiteTemplate(dtp);
    siteTemplate.setId(id);
    siteTemplate.setUuid(uuid);
    siteTemplate.setVersionUuid(versionUuid);
    siteTemplate.setName(name);
    siteTemplate.setUrlStub(urlStub);
    siteTemplate.setDescription(description);
    siteTemplate.setDisplayName(displayName);
    siteTemplate.setIsStaticDisplayName(isStaticDisplayName);

    siteTemplate.setLogoUuid(getLogoUuid());
    siteTemplate.setLogoHref(getLogoHref());
    siteTemplate.setLogoExpr(getLogoExpr());
    siteTemplate.setFaviconUuid(getFaviconUuid());
    siteTemplate.setFaviconHref(getFaviconHref());
    siteTemplate.setFaviconExpr(getFaviconExpr());

    String logoAltTextExpr = getLogoAltTextExpr();
    String staticLogoAltText = getStaticLogoAltText();
    if (logoAltTextExpr == null && staticLogoAltText == null){
      siteTemplate.setLogoAltTextSource(DEFAULT.getText());
      siteTemplate.setLogoAltText(null);
    } else if (logoAltTextExpr == null) {
      siteTemplate.setLogoAltTextSource(STATIC.getText());
      siteTemplate.setLogoAltText(staticLogoAltText);
    } else {
      siteTemplate.setLogoAltTextSource(EXPRESSION.getText());
      siteTemplate.setLogoAltText(logoAltTextExpr);
    }

    siteTemplate.setHeaderBackgroundColor(getHeaderBackgroundColor());
    siteTemplate.setHeaderBackgroundColorExpr(getHeaderBackgroundColorExpr());
    siteTemplate.setSelectedTabBackgroundColor(getSelectedTabBackgroundColor());
    siteTemplate.setSelectedTabBackgroundColorExpr(getSelectedTabBackgroundColorExpr());
    siteTemplate.setAccentColor(getAccentColor());
    siteTemplate.setAccentColorExpr(getAccentColorExpr());
    siteTemplate.setLoadingBarColor(getLoadingBarColor());
    siteTemplate.setLoadingBarColorExpr(getLoadingBarColorExpr());

    List<com.appiancorp.type.cdt.SitePageTemplate> sitePageTemplateCdts = new ArrayList<>();
    for(SitePage page : pages) {
      sitePageTemplateCdts.add(page.toTemplateCdt());
    }
    siteTemplate.setPages(sitePageTemplateCdts);

    siteTemplate.setShowName(getShowName());
    siteTemplate.setTempoLinkVisibility(getTempoLinkVisibility().getText());
    siteTemplate.setTempoLinkViewerGroupUuids(FieldVisibility.GROUPS.equals(getTempoLinkVisibility()) ? getTempoLinkViewerGroupUuids() : new ArrayList<>());

    siteTemplate.setShowRecordNews(getShowRecordNews());
    siteTemplate.setTasksInSitesVisibility(getTasksInSitesVisibility().getText());
    siteTemplate.setTasksInSitesViewerGroupUuids(FieldVisibility.GROUPS.equals(getTasksInSitesVisibility()) ? getTasksInSitesViewerGroupUuids() : new ArrayList<>());

    siteTemplate.setButtonShape(getButtonShape().getText());
    siteTemplate.setInputShape(getInputShape().getText());
    siteTemplate.setNavBarStyle(getNavigationBarStyleEnum().getFriendlyName());
    siteTemplate.setPrimaryNavLayoutType(getPrimaryNavLayoutType());

    siteTemplate.setButtonLabelCase(getButtonLabelCase().getText());
    siteTemplate.setDialogShape(getDialogShape().getText());

    siteTemplate.setVersionOf(versionOf);
    siteTemplate.setExprsAreEvaluable(!expressionTransformationState.requiresTransformationToStoredFormBeforeEvaluation());
    return siteTemplate;
  }

  public static Site copy(Site site) {
    Site siteCopy = new Site(site);
    siteCopy.setName(site.getName());
    siteCopy.setIsStaticDisplayName(site.getIsStaticDisplayName());
    siteCopy.setDisplayName(site.getDisplayName());
    siteCopy.setDescription(site.getDescription());
    siteCopy.setUrlStub(site.getUrlStub());
    siteCopy.setPages(site.getPages().stream()
        .map(sitePage -> new SitePage(sitePage, true))
        .collect(Collectors.toList()));
    siteCopy.setRoleMap(site.getRoleMap());
    siteCopy.setAuditInfo(site.getAuditInfo());
    siteCopy.setVisibility(site.getVisibility());
    siteCopy.setId(site.getId());
    siteCopy.setPublic(site.isPublic());
    siteCopy.setId(site.getId());
    siteCopy.setUuid(site.getUuid());
    siteCopy.setVersionUuid(site.getVersionUuid());

    siteCopy.setLogoUuid(site.getLogoUuid());
    siteCopy.setLogoHref(site.getLogoHref());
    siteCopy.setLogoExpr(site.getLogoExpr());

    siteCopy.setFaviconUuid(site.getFaviconUuid());
    siteCopy.setFaviconHref(site.getFaviconHref());
    siteCopy.setFaviconExpr(site.getFaviconExpr());

    siteCopy.setStaticLogoAltText(site.getStaticLogoAltText());
    siteCopy.setLogoAltTextExpr(site.getLogoAltTextExpr());

    siteCopy.setHeaderBackgroundColor(site.getHeaderBackgroundColor());
    siteCopy.setHeaderBackgroundColorExpr(site.getHeaderBackgroundColorExpr());
    siteCopy.setSelectedTabBackgroundColor(site.getSelectedTabBackgroundColor());
    siteCopy.setSelectedTabBackgroundColorExpr(site.getSelectedTabBackgroundColorExpr());
    siteCopy.setAccentColor(site.getAccentColor());
    siteCopy.setAccentColorExpr(site.getAccentColorExpr());
    siteCopy.setLoadingBarColor(site.getLoadingBarColor());
    siteCopy.setLoadingBarColorExpr(site.getLoadingBarColorExpr());

    siteCopy.setShowName(site.getShowName());
    siteCopy.setTempoLinkVisibility(site.getTempoLinkVisibility());
    siteCopy.setTempoLinkViewerGroupUuids(site.getTempoLinkViewerGroupUuids());
    siteCopy.setShowRecordNews(site.getShowRecordNews());
    siteCopy.setTasksInSitesVisibility(site.getTasksInSitesVisibility());
    siteCopy.setTasksInSitesViewerGroupUuids(site.getTasksInSitesViewerGroupUuids());
    siteCopy.setIsSystem(site.getIsSystem());
    siteCopy.setCached(site.isCached());

    siteCopy.setButtonShape(site.getButtonShape());
    siteCopy.setInputShape(site.getInputShape());
    siteCopy.setNavigationBarStyle(site.getNavigationBarStyle());
    siteCopy.setPrimaryNavLayoutType(site.getPrimaryNavLayoutType());

    siteCopy.setButtonLabelCase(site.getButtonLabelCase());
    siteCopy.setDialogShape(site.getDialogShape());

    siteCopy.setExpressionTransformationState(site.getExpressionTransformationState());

    return siteCopy;
  }

  @SuppressWarnings("unused")
  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @SuppressWarnings("unused")
  /**
   * Runs after deserializing a Site from XML. Converts the refs from XML to UUIDs for persistence.
   */
  private void afterUnmarshal(Unmarshaller u, Object parent) {
    setTempoLinkViewerGroupUuids(getTempoLinkViewerGroupUuidsFromRefs());
    setTasksInSitesViewerGroupUuids(getTasksInSitesViewerGroupUuidsFromRefs());
  }

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

  @Column(name = "uuid", updatable = false, nullable = false, unique = true,
      length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "is_static_display_name", nullable = false)
  public boolean getIsStaticDisplayName() {
    return isStaticDisplayName;
  }

  public void setIsStaticDisplayName(boolean staticDisplayName) {
    this.isStaticDisplayName = staticDisplayName;
  }

  @Column(name = "display_name", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.siteDisplayName)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDisplayNameExpr() {
    return isStaticDisplayName ? null : this.displayName;
  }

  public void setDisplayNameExpr(String displayNameExpr) {
    if (displayNameExpr != null || !getIsStaticDisplayName()) {
      setDisplayName(displayNameExpr);
      setIsStaticDisplayName(false);
    }
  }

  @Transient
  @XmlTransient
  public String getStaticDisplayName() {
    return isStaticDisplayName ? this.displayName : null;
  }

  public void setStaticDisplayName(String staticDisplayName) {
    if (staticDisplayName != null || getIsStaticDisplayName()) {
      setName(staticDisplayName);
      setIsStaticDisplayName(true);
    }
  }

  @Transient
  @XmlElement(name="nameExprIsEvaluable")
  public boolean getNameExprIsEvaluable() {
    return true;
  }

  public void setNameExprIsEvaluable(boolean ignored) {
    // the setter is only here because the IX framework requires there to be a matching setter
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "url_stub", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @Override
  @XmlElement(name = "page")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @HasForeignKeys(breadcrumb = BreadcrumbText.sitePages)
  @JoinColumn(name = "site_id")
  @OrderColumn(name = "order_idx")
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<SitePage> getPages() {
    return pages;
  }

  public void setPages(List<SitePage> pages) {
    this.pages = pages;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient
  @XmlTransient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      if (Roles.SITE_AUDITOR.equals(roleMapEntry.getRole())) {
        roleMapBuilder.users(Roles.SITE_VIEWER, roleMapEntry.getUsers());
        roleMapBuilder.groups(Roles.SITE_VIEWER, roleMapEntry.getGroups());
      } else {
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

  // clearRoleMap() call is needed when a SiteTemplate is updated in RDBMS. The code eventually
  // hits SiteTemplate.discardRoleMap() which sets the role map entries to a PersistentMap. This tells
  // Hibernate to ignore the field during an update (we do this intentionally to prevent wiping out role
  // map entries).
  //
  // Clearing the role map allows us to safely call copySiteTemplate(); otherwise, we would get a
  // LazyInitializationException exception.
  public void clearRoleMap() {
    this.roleMapEntries = new HashSet<>();
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "site_rm", joinColumns = @JoinColumn(name = "site_id"),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @XmlTransient
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Column(name = "visibility", nullable = false)
  public int getVisibility() {
    return visibility;
  }

  public void setVisibility(int visibility) {
    this.visibility = visibility;
  }

  @Transient
  @XmlElement(type = Object.class)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteLogo)
  @ForeignKeyCustomBinder(CustomBinderType.DOCUMENT_REF)
  public Ref<Long, String> getLogoRef() {
    String logoUuid = getLogoUuid();
    if (!Strings.isNullOrEmpty(logoUuid)) {
      return new DocumentRefImpl(getLogoUuid());
    } else {
      return null;
    }
  }

  public void setLogoRef(Ref<Long,String> objectRef) {
    if (objectRef != null) {
      setLogoUuid(objectRef.getUuid());
    } else {
      setLogoUuid(null);
    }
  }

  @Transient
  @XmlTransient
  public String getLogoUuid() {
    String logoDefault = getSiteBrandingConfig(LOGO, DEFAULT);
    return logoDefault == null ? getSiteBrandingConfig(LOGO, DOCUMENT) : logoDefault;
  }

  public void setLogoUuid(String logoUuid) {
    if (!Strings.isNullOrEmpty(logoUuid)) {
      updateSiteBranding(
          LOGO,
          DEFAULT_LOGO_UUID.equalsIgnoreCase(logoUuid) ? DEFAULT : DOCUMENT,
          logoUuid
      );
    }
  }

  @Transient
  public String getLogoHref() {
    return getSiteBrandingConfig(LOGO, WEB_ADDRESS);
  }

  public void setLogoHref(String logoHref) {
    if (!Strings.isNullOrEmpty(logoHref)) {
      updateSiteBranding(LOGO, WEB_ADDRESS, logoHref);
    }
  }

  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteLogo)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getLogoExpr() {
    return getSiteBrandingConfig(LOGO, EXPRESSION);
  }

  public void setLogoExpr(String logoExpr) {
    if (!Strings.isNullOrEmpty(logoExpr)) {
      updateSiteBranding(LOGO, EXPRESSION, logoExpr);
    }
  }

  @Transient
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.siteLogoAltText)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getLogoAltTextExpr() {
    return getSiteBrandingConfig(LOGO_ALT_TEXT, EXPRESSION);
  }

  public void setLogoAltTextExpr(String logoAltTextExpr) {
    if (!Strings.isNullOrEmpty(logoAltTextExpr)) {
      updateSiteBranding(LOGO_ALT_TEXT, EXPRESSION, logoAltTextExpr);
    }
  }

  @Transient
  public String getStaticLogoAltText() {
    return getSiteBrandingConfig(LOGO_ALT_TEXT, STATIC);
  }

  public void setStaticLogoAltText(String staticLogoAltText) {
    if (!Strings.isNullOrEmpty(staticLogoAltText)) {
      updateSiteBranding(LOGO_ALT_TEXT, STATIC, staticLogoAltText);
    }
  }

  @Transient
  @XmlElement(type = Object.class)
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteFavicon)
  @ForeignKeyCustomBinder(CustomBinderType.DOCUMENT_REF)
  public Ref<Long, String> getFaviconRef() {
    String faviconUuid = getFaviconUuid();
    if (!Strings.isNullOrEmpty(faviconUuid)) {
      return new DocumentRefImpl(getFaviconUuid());
    } else {
      return null;
    }
  }

  public void setFaviconRef(Ref<Long,String> objectRef) {
    if (objectRef != null) {
      setFaviconUuid(objectRef.getUuid());
    } else {
      setFaviconUuid(null);
    }
  }

  @Transient
  @XmlTransient
  public String getFaviconUuid() {
    String faviconUuid = getSiteBrandingConfig(FAVICON, DOCUMENT);
    String faviconDefault = getSiteBrandingConfig(FAVICON, DEFAULT);
    return faviconDefault == null ? faviconUuid : faviconDefault;
  }

  public void setFaviconUuid(String faviconUuid) {
    if (!Strings.isNullOrEmpty(faviconUuid)) {
      updateSiteBranding(
          FAVICON,
          DEFAULT_FAVICON_UUID.equalsIgnoreCase(faviconUuid) ? DEFAULT : DOCUMENT,
          faviconUuid
      );
    }
  }

  @Transient
  public String getFaviconHref() {
    return getSiteBrandingConfig(FAVICON, WEB_ADDRESS);
  }

  public void setFaviconHref(String faviconHref) {
    if (!Strings.isNullOrEmpty(faviconHref)) {
      updateSiteBranding(FAVICON, WEB_ADDRESS, faviconHref);
    }
  }

  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteFavicon)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getFaviconExpr() {
    return getSiteBrandingConfig(FAVICON, EXPRESSION);
  }

  public void setFaviconExpr(String faviconExpr) {
    if (!Strings.isNullOrEmpty(faviconExpr)) {
      updateSiteBranding(FAVICON, EXPRESSION, faviconExpr);
    }
  }

  @Transient
  public String getHeaderBackgroundColor() {
    return getSiteBrandingConfig(HEADER_BKGD_COLOR, STATIC);
  }

  public void setHeaderBackgroundColor(String headerBackgroundColor) {
    if (!Strings.isNullOrEmpty(headerBackgroundColor)) {
      updateSiteBranding(HEADER_BKGD_COLOR, STATIC, headerBackgroundColor);
    }
  }

  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteHeaderBackgroundColor)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getHeaderBackgroundColorExpr() {
    return getSiteBrandingConfig(HEADER_BKGD_COLOR, EXPRESSION);
  }

  public void setHeaderBackgroundColorExpr(String headerBackgroundColorExpr) {
    if (!Strings.isNullOrEmpty(headerBackgroundColorExpr)) {
      updateSiteBranding(HEADER_BKGD_COLOR, EXPRESSION, headerBackgroundColorExpr);
    }
  }

  @Transient
  public String getSelectedTabBackgroundColor() {
    return getSiteBrandingConfig(SELECTED_TAB_COLOR, STATIC);
  }

  public void setSelectedTabBackgroundColor(String selectedTabBackgroundColor) {
    if (!Strings.isNullOrEmpty(selectedTabBackgroundColor)) {
      updateSiteBranding(SELECTED_TAB_COLOR, STATIC, selectedTabBackgroundColor);
    }
  }

  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteSelectedTabBackgroundColor)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getSelectedTabBackgroundColorExpr() {
    return getSiteBrandingConfig(SELECTED_TAB_COLOR, EXPRESSION);
  }

  public void setSelectedTabBackgroundColorExpr(String selectedTabBackgroundColorExpr) {
    if (!Strings.isNullOrEmpty(selectedTabBackgroundColorExpr)) {
      updateSiteBranding(SELECTED_TAB_COLOR, EXPRESSION, selectedTabBackgroundColorExpr);
    }
  }

  @Transient
  public String getAccentColor() {
    return getSiteBrandingConfig(ACCENT_COLOR, STATIC);
  }

  public void setAccentColor(String accentColor) {
    if (!Strings.isNullOrEmpty(accentColor)) {
      updateSiteBranding(ACCENT_COLOR, STATIC, accentColor);
    }
  }


  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteAccentColor)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getAccentColorExpr() {
    return getSiteBrandingConfig(ACCENT_COLOR, EXPRESSION);
  }

  public void setAccentColorExpr(String accentColorExpr) {
    if (!Strings.isNullOrEmpty(accentColorExpr)) {
      updateSiteBranding(ACCENT_COLOR, EXPRESSION, accentColorExpr);
    }
  }

  @Transient
  public String getLoadingBarColor() {
    return getSiteBrandingConfig(LOADING_BAR_COLOR, STATIC);
  }

  public void setLoadingBarColor(String loadingBarColor) {
    if (!Strings.isNullOrEmpty(loadingBarColor)) {
      updateSiteBranding(LOADING_BAR_COLOR, STATIC, loadingBarColor);
    }
  }

  @Transient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.siteLoadingBarColor)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getLoadingBarColorExpr() {
    return getSiteBrandingConfig(LOADING_BAR_COLOR, EXPRESSION);
  }

  public void setLoadingBarColorExpr(String loadingBarColorExpr) {
    if (!Strings.isNullOrEmpty(loadingBarColorExpr)) {
      updateSiteBranding(LOADING_BAR_COLOR, EXPRESSION, loadingBarColorExpr);
    }
  }

  @Transient
  @XmlElement
  public NavigationLayoutType getPrimaryNavLayoutType() {
    String primaryNavLayoutType = getSiteBrandingConfig(PRIMARY_NAV_LAYOUT_TYPE, ENUM);
    return NavigationLayoutType.fromValue(primaryNavLayoutType == null ?
        DEFAULT_PRIMARY_NAV_LAYOUT_TYPE :
        primaryNavLayoutType);
  }

  public void setPrimaryNavLayoutType(NavigationLayoutType navLayoutType) {
    updateSiteBranding(PRIMARY_NAV_LAYOUT_TYPE, ENUM, navLayoutType.value());
  }

  private void addColorBrandingConfig(SiteBrandingCfgKey brandingKey, String staticValue, String exprValue) {
    boolean staticColorEmpty = Strings.isNullOrEmpty(staticValue);
    boolean exprColorEmpty = Strings.isNullOrEmpty(exprValue);

    if (staticColorEmpty && !exprColorEmpty) {
      updateSiteBranding(brandingKey, EXPRESSION, exprValue);
    } else if (exprColorEmpty && !staticColorEmpty) {
      updateSiteBranding(brandingKey, STATIC, staticValue);
    } else {
      updateSiteBranding(brandingKey, DEFAULT, null);
    }
  }

  private void addLogoBrandingConfig(SiteBrandingCfgKey brandingKey, String uuidValue, String hrefValue, String exprValue, String defaultValue) {
    boolean uuidColorEmpty = Strings.isNullOrEmpty(uuidValue);
    boolean uuidColorDefault = defaultValue.equalsIgnoreCase(uuidValue);
    boolean hrefColorEmpty = Strings.isNullOrEmpty(hrefValue);
    boolean exprColorEmpty = Strings.isNullOrEmpty(exprValue);

    if (!uuidColorEmpty && !uuidColorDefault && hrefColorEmpty && exprColorEmpty){
      updateSiteBranding(brandingKey, DOCUMENT, uuidValue);
    } else if (!hrefColorEmpty && uuidColorEmpty && exprColorEmpty){
      updateSiteBranding(brandingKey, WEB_ADDRESS, hrefValue);
    } else if (!exprColorEmpty && hrefColorEmpty && uuidColorEmpty){
      updateSiteBranding(brandingKey, EXPRESSION, exprValue);
    } else {
      updateSiteBranding(brandingKey, DEFAULT, defaultValue);
    }
  }

  private void updateSiteBranding(SiteBrandingCfgKey brandingKey, BrandingCfgSource source, String value) {
    boolean exist = false;
    String brandingKeyStr = brandingKey.getText();
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (currentBrandingConfig.getBrandingKey().equals(brandingKeyStr)) {
        currentBrandingConfig.setBrandingSourceByte(source.getCode());
        currentBrandingConfig.setBrandingValue(value);
        currentBrandingConfig.setExpressionTransformationState(expressionTransformationState);
        exist = true;
        break;
      }
    }
    if (!exist) {
      SiteBrandingCfg newBrandingConfig = new SiteBrandingCfg(brandingKeyStr, source, value);
      newBrandingConfig.setExpressionTransformationState(expressionTransformationState);
      siteBrandingCfgs.add(newBrandingConfig);
    }
  }

  private String getSiteBrandingConfig(SiteBrandingCfgKey brandingKey, BrandingCfgSource source) {
    Byte sourceByte = source.getCode();
    String brandingKeyStr = brandingKey.getText();
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (currentBrandingConfig.getBrandingKey().equals(brandingKeyStr)) {
        if (currentBrandingConfig.getBrandingSourceByte() == sourceByte) {
          return currentBrandingConfig.getBrandingValue();
        } else {
          return null;
        }
      }
    }
    return null;
  }

  public void setEnumValue(String key, String enumStr) {
    boolean exist = false;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (key.equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        currentBrandingConfig.setBrandingValue(enumStr);
        exist = true;
        break;
      }
    }
    if (!exist) {
      SiteBrandingCfg newBrandingConfig = new SiteBrandingCfg(key, ENUM, enumStr);
      siteBrandingCfgs.add(newBrandingConfig);
    }
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = SiteBrandingCfg.class)
  @JoinColumn(name = "site_id", nullable=false)
  @OrderBy
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  @XmlTransient
  public List<SiteBrandingCfg> getSiteBrandingCfgs() {
    return siteBrandingCfgs;
  }

  public void setSiteBrandingCfgs(List<SiteBrandingCfg> siteBrandingConfigList) {
    this.siteBrandingCfgs = siteBrandingConfigList;
  }

  @Override
  @Embedded
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public String toString() {
    return "Site [id=" + id + ", uuid=" + uuid + ", urlStub=" + urlStub + "]";
  }

  public static ToStringFunction<SiteSummary> multilineToString(final int indent) {
    return new ToStringFunction<SiteSummary>() {
      @Override
      protected String doToString(SiteSummary t) {
        return MultilineToStringHelper.of(t,indent)
            .add(PROP_ID, t.getId())
            .add(PROP_UUID, t.getUuid())
            .add(PROP_NAME, t.getName())
            .add(PROP_DESCRIPTION, t.getDescription())
            .add(PROP_URL_STUB, t.getUrlStub())
            .toString();
      }};
  }

  public boolean equivalentTo(final SiteSummary site) {
    return EQUIVALENCE.equivalent(this, site);
  }

  public boolean equivalentToWithPages(final SiteWithPages site) {
    boolean equivalent = EQUIVALENCE.equivalent(this, site);
    List<SitePage> inputPages = site.getPages();
    if (equivalent && (pages.size() == inputPages.size())) {
      for (int i = 0; i < pages.size(); i++) {
        if (!pages.get(i).equivalentTo(inputPages.get(i))) {
          equivalent = false;
          break;
        }
      }
    }
    return equivalent;
  }

  @Override
  public Ref<Long,String> build(Long id, String uuid) {
    final Site site = new Site();
    site.setUuid(uuid);
    site.setId(id);
    return site;
  }

  @XmlTransient
  @Transient
  @Override
  public QName getTypeQName() {
    return Site.QNAME;
  }

  // NOTE: setPublic is superfluous when calling setVisibility, as each will set
  // the same thing in the end.  We'll leave it in for now for legacy reasons.

  @Override
  @Transient
  @XmlTransient
  public boolean isPublic() {
    return Visibility.isSupported(visibility, VisibilityFlags.PUBLIC);
  }

  @Override
  public void setPublic(boolean isPublic) {
    visibility = Visibility.builder()
      .replaceMask(visibility)
      .setFlag(VisibilityFlags.PUBLIC, isPublic)
      .build();
  }

  @Override
  @Transient
  @XmlTransient
  public String getFallbackRoleName() {
    return Roles.SITE_VIEWER.getName();
  }

  @Override
  @Column(name = "show_name", nullable = false)
  @XmlElement(name = "showName")
  public boolean getShowName() {
    return showName;
  }

  public void setShowName(boolean showName) {
    this.showName = showName;
  }

  @Override
  @Transient
  @XmlElement(name = "tempoLinkVisibility")
  public FieldVisibility getTempoLinkVisibility() {
    return tempoLinkVisibility == null ? FieldVisibility.HIDDEN : tempoLinkVisibility;
  }

  public void setTempoLinkVisibility(FieldVisibility tempoLinkVisibility){
    this.tempoLinkVisibility = tempoLinkVisibility;
  }

  @SuppressWarnings("unused")
  @Column(name = "tempo_link_visibility", nullable = false)
  @XmlTransient
  private byte getTempoLinkVisibilityCode() {
    return getTempoLinkVisibility().getCode();
  }

  @SuppressWarnings("unused")
  private void setTempoLinkVisibilityCode(byte tempoLinkVisibilityCode) {
    setTempoLinkVisibility(FieldVisibility.fromCode(tempoLinkVisibilityCode));
  }

  @Fetch(FetchMode.SELECT)
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "site_id", nullable = false)
  @OrderColumn(name="order_idx", nullable = false)
  @BatchSize(size = 100)
  @XmlTransient
  public List<SiteTempoLinkViewer> getTempoLinkViewers_Internal() {
    return tempoLinkViewers_internal;
  }

  public void setTempoLinkViewers_Internal(List<SiteTempoLinkViewer> tempoLinkViewers_internal) {
    this.tempoLinkViewers_internal = tempoLinkViewers_internal;
  }

  @Override
  @Column(name = "show_record_news", nullable = false)
  @XmlElement(name = "showRecordNews")
  public boolean getShowRecordNews() {
    return showRecordNews;
  }

  public void setShowRecordNews(boolean showRecordNews) {
    this.showRecordNews = showRecordNews;
  }

  @Transient
  @XmlTransient
  public List<String> getTempoLinkViewerGroupUuids() {
    if (tempoLinkViewers_internal == null || !FieldVisibility.GROUPS.equals(tempoLinkVisibility)) {
      return new ArrayList<>();
    }
    return Lists.newArrayList(tempoLinkViewers_internal.stream()
        .map(entity -> entity.getGroupUuid())
        .collect(Collectors.toList()));
  }

  public void setTempoLinkViewerGroupUuids(List<String> tempoLinkViewerGroupUuids) {
    this.tempoLinkViewers_internal = new ArrayList<>();
    if (tempoLinkViewerGroupUuids != null) {
      for (String groupUuid : tempoLinkViewerGroupUuids) {
        this.tempoLinkViewers_internal.add(new SiteTempoLinkViewer(groupUuid));
      }
    }
  }

  @SuppressWarnings("unused")
  @Transient
  @XmlElement(name = "tempoLinkViewers", type = Object.class)
  public List<Ref> getTempoLinkViewers() {
    return tempoLinkViewers;
  }

  public void setTempoLinkViewers(List<Ref> tempoLinkViewers) {
    this.tempoLinkViewers = tempoLinkViewers;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable = false, breadcrumb = BreadcrumbText.siteTempoLinkViewers)
  @ForeignKeyCustomBinder(CustomBinderType.REF_LIST)
  public List<Ref> getTempoLinkViewerRefsFromUuids() {
    return Lists.newArrayList(getTempoLinkViewerGroupUuids().stream()
        .map(uuid -> new GroupRefImpl(null, uuid))
        .collect(Collectors.toList()));
  }

  @Transient
  @XmlTransient
  public List<String> getTempoLinkViewerGroupUuidsFromRefs() {
    if (tempoLinkViewers == null || !FieldVisibility.GROUPS.equals(tempoLinkVisibility)) {
      return new ArrayList<>();
    }
    return Lists.newArrayList(tempoLinkViewers.stream()
        .filter(ref -> ref instanceof GroupRef)
        .map(ref -> ((GroupRef) ref).getUuid())
        .collect(Collectors.toList()));
  }

  @Override
  @Transient
  @XmlElement(name = "tasksInSitesVisibility")
  public FieldVisibility getTasksInSitesVisibility() {
    return tasksInSitesVisibility == null ? FieldVisibility.HIDDEN : tasksInSitesVisibility;
  }

  public void setTasksInSitesVisibility(FieldVisibility tasksInSitesVisibility){
    this.tasksInSitesVisibility = tasksInSitesVisibility;
  }

  @SuppressWarnings("unused")
  @Column(name = "tasks_in_sites_visibility", nullable = false)
  @XmlTransient
  private byte getTasksInSitesVisibilityCode() {
    return getTasksInSitesVisibility().getCode();
  }

  @SuppressWarnings("unused")
  private void setTasksInSitesVisibilityCode(byte tasksInSitesVisibilityCode) {
    setTasksInSitesVisibility(FieldVisibility.fromCode(tasksInSitesVisibilityCode));
  }

  @Fetch(FetchMode.SELECT)
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "site_id", nullable = false)
  @OrderColumn(name="order_idx", nullable = false)
  @BatchSize(size = 100)
  @XmlTransient
  public List<TasksInSitesViewer> getTasksInSitesViewers_Internal() {
    return tasksInSitesViewers_internal;
  }

  public void setTasksInSitesViewers_Internal(List<TasksInSitesViewer> tasksInSitesViewers_internal) {
    this.tasksInSitesViewers_internal = tasksInSitesViewers_internal;
  }

  @Override
  @Transient
  @XmlTransient
  public List<String> getTasksInSitesViewerGroupUuids() {
    if (tasksInSitesViewers_internal == null || !FieldVisibility.GROUPS.equals(tasksInSitesVisibility)) {
      return new ArrayList<>();
    }
    return Lists.newArrayList(tasksInSitesViewers_internal.stream()
        .map(entity -> entity.getGroupUuid())
        .collect(Collectors.toList()));
  }

  public void setTasksInSitesViewerGroupUuids(List<String> tasksInSitesViewerGroupUuids) {
    this.tasksInSitesViewers_internal = new ArrayList<>();
    if (tasksInSitesViewerGroupUuids != null) {
      for (String groupUuid : tasksInSitesViewerGroupUuids) {
        this.tasksInSitesViewers_internal.add(new TasksInSitesViewer(groupUuid));
      }
    }
  }

  @SuppressWarnings("unused")
  @Transient
  @XmlElement(name = "tasksInSitesViewers", type = Object.class)
  public List<Ref> getTasksInSitesViewers() {
    return tasksInSitesViewers;
  }

  public void setTasksInSitesViewers(List<Ref> tasksInSitesViewers) {
    this.tasksInSitesViewers = tasksInSitesViewers;
    if (this.tasksInSitesVisibility == FieldVisibility.GROUPS) {
      ProductMetricsAggregatedDataCollector.recordData("mobileTaskList.Enabled.SelectGroups",  this.tasksInSitesViewers.size());
    }
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable = false, breadcrumb = BreadcrumbText.siteTasksInSitesViewers)
  @ForeignKeyCustomBinder(CustomBinderType.REF_LIST)
  public List<Ref> getTasksInSitesViewerRefsFromUuids() {
    return Lists.newArrayList(getTasksInSitesViewerGroupUuids().stream()
        .map(uuid -> new GroupRefImpl(null, uuid))
        .collect(Collectors.toList()));
  }

  @Transient
  @XmlTransient
  public List<String> getTasksInSitesViewerGroupUuidsFromRefs() {
    if (tasksInSitesViewers == null || !FieldVisibility.GROUPS.equals(tasksInSitesVisibility)) {
      return new ArrayList<>();
    }
    return Lists.newArrayList(tasksInSitesViewers.stream()
        .filter(ref -> ref instanceof GroupRef)
        .map(ref -> ((GroupRef) ref).getUuid())
        .collect(Collectors.toList()));
  }

  @Transient
  @XmlElement(name = "buttonShape")
  public ButtonShape getButtonShape() {
    ButtonShape buttonShape = ButtonShape.SQUARED;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (BUTTON_SHAPE.getText().equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        buttonShape = ButtonShape.fromText(currentBrandingConfig.getBrandingValue());
        break;
      }
    }
    return buttonShape;
  }

  public void setButtonShape(ButtonShape buttonShape) {
    String key = BUTTON_SHAPE.getText();
    String buttonShapeStr = buttonShape.getText();
    setEnumValue(key, buttonShapeStr);
  }

  @Transient
  @XmlElement(name = "inputShape")
  public InputShape getInputShape() {
    InputShape inputShape = InputShape.SQUARED;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (INPUT_SHAPE.getText().equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        inputShape = InputShape.fromText(currentBrandingConfig.getBrandingValue());
        break;
      }
    }
    return inputShape;
  }

  public void setInputShape(InputShape inputShape) {
    String key = INPUT_SHAPE.getText();
    String inputShapeStr = inputShape.getText();
    setEnumValue(key, inputShapeStr);
  }

  @Transient
  @XmlElement(name = "navigationBarStyle")
  public String getNavigationBarStyle() {
    return getNavigationBarStyleEnum().getStoredName();
  }

  public void setNavigationBarStyle(String navigationBarStyle) {
    SiteNavBarStyle navBarStyle = SiteNavBarStyle.fromStoredName(navigationBarStyle);
    setNavigationBarStyleEnum(navBarStyle);
  }

  @Transient
  @XmlTransient
  public SiteNavBarStyle getNavigationBarStyleEnum() {
    SiteNavBarStyle navBarStyle = SiteNavBarStyle.HELIUM;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (NAV_BAR_STYLE.getText().equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        navBarStyle = SiteNavBarStyle.fromStoredName(currentBrandingConfig.getBrandingValue());
        break;
      }
    }
    return navBarStyle;
  }

  public void setNavigationBarStyleEnum(SiteNavBarStyle siteNavBarStyle) {
    String key = NAV_BAR_STYLE.getText();
    String navBarStyleStr = siteNavBarStyle.getStoredName();
    setEnumValue(key, navBarStyleStr);
  }

  @Transient
  @XmlElement(name = "buttonLabelCase")
  public ButtonLabelCase getButtonLabelCase() {
    ButtonLabelCase buttonLabelCase = ButtonLabelCase.UPPERCASE;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (BUTTON_LABEL_CASE.getText().equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        buttonLabelCase = ButtonLabelCase.fromText(currentBrandingConfig.getBrandingValue());
        break;
      }
    }
    return buttonLabelCase;
  }

  public void setButtonLabelCase(ButtonLabelCase buttonLabelCase) {
    String key = BUTTON_LABEL_CASE.getText();
    String buttonLabelCaseStr = buttonLabelCase.getText();
    setEnumValue(key, buttonLabelCaseStr);
  }

  @Transient
  @XmlElement(name = "dialogShape")
  public DialogShape getDialogShape() {
    DialogShape dialogShape = DialogShape.SQUARED;
    for (SiteBrandingCfg currentBrandingConfig : siteBrandingCfgs) {
      if (DIALOG_SHAPE.getText().equalsIgnoreCase(currentBrandingConfig.getBrandingKey())) {
        dialogShape = DialogShape.fromText(currentBrandingConfig.getBrandingValue());
        break;
      }
    }
    return dialogShape;
  }

  public void setDialogShape(DialogShape dialogShape) {
    String key = DIALOG_SHAPE.getText();
    String dialogShapeStr = dialogShape.getText();
    setEnumValue(key, dialogShapeStr);
  }

  @Column(name = "is_system", nullable = false)
  public boolean getIsSystem() {
    return isSystem;
  }

  public void setIsSystem(boolean isSystem) {
    this.isSystem = isSystem;
  }

  /**
   * Controls whether 'this' site is configured to perform a design object lock validation
   * when it is next updated via {@link com.appiancorp.common.service.EntityServiceTxImpl}. Note that
   * this is a transient configuration that's not stored in the backing DB.
   */
  public void setNeedsLockValidationOnUpdate(boolean needsLockValidationOnUpdate) {
    this.needsLockValidationOnUpdate = needsLockValidationOnUpdate;
  }

  /** see {@link #setNeedsLockValidationOnUpdate(boolean)} */
  @Override
  public boolean needsLockValidationOnUpdate() {
    return needsLockValidationOnUpdate;
  }

  /**
   * Indicates if the site is cached in the
   * {@link com.appiancorp.sites.backend.SitesCacheManager memory}.
   *
   * @return <tt>true</tt> if the site is cached;
   *         otherwise - it is queried from the database.
   */
  @Transient
  @XmlTransient
  public boolean isCached() {
    return isSiteInCache;
  }

  /**
   * Sets the flag to indicate if the site is cached in the
   * {@link com.appiancorp.sites.backend.SitesCacheManager memory}.
   *
   * @param isCached <tt>true</tt> if the site is cached;
   *         otherwise - it is queried from the database.
   */
  public void setCached(boolean isCached) {
    isSiteInCache = isCached;
  }

  /**
   * Indicates if this is a new site.
   * This flag is only used for sites stored in ADS and should not be used
   * to determine if a legacy site (RDBMS one) is new.
   *
   * @return <tt>true</tt> if the site's UUID is <tt>null</tt> or empty, or the <tt>isNew</tt> flag
   * is explicitly set to <tt>true</tt>.
   */
  @Transient
  @XmlTransient
  public boolean isNew() {
    return isNewSite || Strings.isNullOrEmpty(getUuid());
  }

  /**
   * Sets the flag indicating if this is a new site.
   *
   * @param isNew <tt>true</tt> if the site's UUID is <tt>null</tt> or empty, which means it is a new site;
   * otherwise - the site is already written to the database.
   */
  public void setNew(boolean isNew) {
    this.isNewSite =  isNew;
  }

  /**
   * The id reference in rdbms.
   */
  @Transient
  @XmlTransient
  public Long getLegacyId() {
    return legacyId;
  }

  public void setLegacyId(Long legacyId) {
    this.legacyId =  legacyId;
  }


  /**
   * Returns the ADS ID of the latest Site entity that this entity is a version of.
   *
   * @return <tt>null</tt> if this Site entity is not a version entity; otherwise the ADS ID of the Site's
   * latest base entity
   */
  @Transient
  @XmlTransient
  public Long getVersionOf() {
    return versionOf;
  }

  /**
   * Sets the versionOf attribute on this Site Template.
   *
   * @param versionOfId the ID of the latest Site entity to make this entity a version of.
   */
  public void setVersionOf(Long versionOfId) {
    this.versionOf = versionOfId;
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

  public void setAllExpressionTransformationState(ExpressionTransformationState expressionTransformationState) {
    setExpressionTransformationState(expressionTransformationState);
    getPages().forEach(page -> {
      page.setAllExpressionTransformationState(expressionTransformationState);
    });
  }

  // Used by Java Serialization
  private Object readResolve() {
    this.expressionTransformationState = STORED;
    return this;
  }

}
