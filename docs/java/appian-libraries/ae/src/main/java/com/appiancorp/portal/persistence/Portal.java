package com.appiancorp.portal.persistence;

import static com.appiancorp.branding.BrandingCfgSource.DEFAULT;
import static com.appiancorp.branding.BrandingCfgSource.DOCUMENT;
import static com.appiancorp.branding.BrandingCfgSource.ENUM;
import static com.appiancorp.branding.BrandingCfgSource.STATIC;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_FAVICON_UUID;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_LOGO_UUID;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_PRIMARY_NAV_LOGO_UUID;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_PRIMARY_NAV_LAYOUT_TYPE;
import static com.appiancorp.core.ContentCustomBrandingConstants.DEFAULT_PWA_ICON_UUID;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.ACCENT_COLOR;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.BUTTON_SHAPE;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.DISPLAY_NAME;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.FAVICON;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.INPUT_SHAPE;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.LOADING_BAR_COLOR;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_BACKGROUND_COLOR;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_LOGO_ALT_TEXT;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_LOGO_UUID;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_LAYOUT_TYPE;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_SELECTED_BACKGROUND_COLOR;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PRIMARY_NAV_SHOW_DISPLAY_NAME;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PWA_ENABLED;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PWA_ICON;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.PWA_SHORT_NAME;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.BUTTON_LABEL_CASE;
import static com.appiancorp.portal.persistence.PortalBrandingCfgKey.DIALOG_SHAPE;
import static com.appiancorp.publicportal.PublicPortalConfiguration.DEFAULT_PORTAL_HOSTNAME_PLACEHOLDER;

import java.nio.charset.StandardCharsets;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.appian.core.persist.Constants;
import com.appiancorp.ag.ExtendedUserProfileService;
import com.appiancorp.branding.BrandingCfgSource;
import com.appiancorp.common.config.ApplicationContextHolder;
import com.appiancorp.core.expr.portable.cdt.NavigationLayoutType;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.navigation.PageHolder;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.locking.NeedsLockValidation;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.sites.ButtonLabelCase;
import com.appiancorp.sites.ButtonShape;
import com.appiancorp.sites.DialogShape;
import com.appiancorp.sites.InputShape;
import com.appiancorp.suiteapi.common.exceptions.InvalidUserException;
import com.appiancorp.suiteapi.personalization.UserProfile;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.NavigationLevelRenderOptions;
import com.appiancorp.type.cdt.value.PortalDto;
import com.appiancorp.type.cdt.value.PortalPageDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.ConnectedSystemRefImpl;
import com.appiancorp.type.refs.DocumentRefImpl;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.UserRefImpl;
import com.appiancorp.type.refs.XmlRuleRefAdapter.RuleRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

@Entity
@Table(name = "portal")
@IgnoreJpa
@Tracked
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(propOrder = { // Explicitly control which getter/setters are serialized, and in what order
    "uuid", "name", "description", "shouldPublish", "displayName", "urlStub",
    "serviceAccountUserRef", "buttonShape", "inputShape", "accentColor", "loadingBarColor", "faviconRef",
    "recaptchaConnectedSystemRef", "pwaEnabled", "pwaShortName", "pwaIconRef", "showNavigation", "primaryNavLayoutType",
    "primaryNavBackgroundColor", "primaryNavSelectedBackgroundColor", "primaryNavLogoRef", "primaryNavLogoAltText",
    "primaryNavShowDisplayName", "pages", "buttonLabelCase", "dialogShape", "hostname"
})
@XmlSeeAlso({DocumentRefImpl.class, RuleRefImpl.class, UserRefImpl.class, ConnectedSystemRefImpl.class})
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class Portal implements HasRoleMap, HasTypeQName, HasAuditInfo, Id<Long>, Name, Uuid<String>,
    HasVersionHistory, NeedsLockValidation, PageHolder<PortalPage>, PortalDescriptor {

  private static final long serialVersionUID = 1L;

  public static final String LOCAL_PART = "Portal";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_UUID = "uuid";
  public static final String PROP_NAME = "name";
  public static final String PROP_ID = "id";
  public static final String PROP_URL_STUB = "urlStub";
  public static final String PROP_SHOULD_PUBLISH = "shouldPublish";
  public static final String PROP_VERSION_UUID = "versionUuid";
  public static final String PROP_SERVICE_ACCOUNT_UUID = "serviceAccountUuid";
  public static final String PROP_RECAPTCHA_CONNECTED_SYSTEM_UUID = "recaptchaConnectedSystemUuid";
  public static final String PROP_SHOW_NAVIGATION = "showNavigation";
  public static final String PROP_PAGES = "pages";
  public static final String SUFFIX_FOR_PAGE_UUID_GENERATION = "-portal-page";

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.PORTAL_ADMIN,
      Roles.PORTAL_EDITOR,
      Roles.PORTAL_VIEWER
  );

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String urlStub;
  private String versionUuid;
  private boolean shouldPublish;
  private AuditInfo auditInfo = new AuditInfo();
  private boolean needsLockValidationOnUpdate = true;
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();
  private List<PortalBrandingCfg> portalBrandingCfgs = new ArrayList<>();
  private PortalPublishInfo publishInfo;
  private String serviceAccountUuid;
  private String recaptchaConnectedSystemUuid;
  private List<PortalPage> portalPages = new ArrayList<>();
  private boolean showNavigation;
  private String hostname = DEFAULT_PORTAL_HOSTNAME_PLACEHOLDER;

  /** Only set transiently during an IX Import operation */
  private String serviceAccountUsernameFromImportData;

  public Portal() {
  }

  public Portal(String name, String description, String urlStub) {
    this.name = name;
    this.description = description;
    this.urlStub = urlStub;
  }

  public Portal(PortalDto portalDto) {
    // A null PortalDTO ID is a large negative number, so this checks for that number
    if (portalDto.getId().intValue() ==  com.appiancorp.core.Constants.INTEGER_NULL) {
      this.id = null;
    } else {
      this.id = portalDto.getId();
    }
    this.name = portalDto.getName();
    this.urlStub = portalDto.getUrlStub();
    this.description = portalDto.getDescription();
    this.uuid = portalDto.getUuid();
    this.versionUuid = portalDto.getVersionUuid();
    this.serviceAccountUuid = portalDto.getServiceAccountUuid();
    this.shouldPublish = portalDto.isShouldPublish();
    this.recaptchaConnectedSystemUuid = portalDto.getRecaptchaConnectedSystemUuid();
    this.showNavigation = portalDto.isShowNavigation();
    this.hostname = portalDto.getHostname();

    for (PortalPageDto portalPageDto : portalDto.getPages()) {
      this.portalPages.add(new PortalPage(portalPageDto));
    }

    // Create/update PortalBrandingCfgs based on individual fields in DTO
    addImageBrandingConfig(FAVICON, portalDto.getFaviconUuid(), DEFAULT_FAVICON_UUID);
    addImageBrandingConfig(PWA_ICON, portalDto.getPwaIconUuid(), DEFAULT_PWA_ICON_UUID);
    updatePortalBranding(BUTTON_SHAPE, ENUM, portalDto.getButtonShape());
    updatePortalBranding(INPUT_SHAPE, ENUM, portalDto.getInputShape());
    addStaticOrDefaultBrandingConfig(ACCENT_COLOR, portalDto.getAccentColor());
    addStaticOrDefaultBrandingConfig(LOADING_BAR_COLOR, portalDto.getLoadingBarColor());
    updatePortalBranding(DISPLAY_NAME, STATIC, portalDto.getDisplayName());
    addStaticOrDefaultBrandingConfig(PWA_SHORT_NAME, portalDto.getPwaShortName());
    updatePortalBranding(PWA_ENABLED, STATIC, Boolean.toString(portalDto.isPwaEnabled()));
    updatePortalBranding(BUTTON_LABEL_CASE, ENUM, portalDto.getButtonLabelCase());
    updatePortalBranding(DIALOG_SHAPE, ENUM, portalDto.getDialogShape());
    addNavigationConfigurationBrandingConfig(portalDto);
  }

  private void addNavigationConfigurationBrandingConfig(PortalDto portalDto) {
    if (portalDto.getNavigationConfiguration() == null ||
        portalDto.getNavigationConfiguration().getPrimaryNavigationRenderOptions() == null ||
        !portalDto.isShowNavigation()) {
      return;
    }

    final NavigationLevelRenderOptions navigationRenderOptions = portalDto.getNavigationConfiguration().getPrimaryNavigationRenderOptions();

    final BrandingCfgSource layoutTypeSource = navigationRenderOptions.getLayoutType() == null ||
        NavigationLayoutType.TOPBAR.value().equalsIgnoreCase(navigationRenderOptions.getLayoutType().value()) ? DEFAULT : ENUM;
    updatePortalBranding(PRIMARY_NAV_LAYOUT_TYPE,
        layoutTypeSource,
        navigationRenderOptions.getLayoutType() == null
            ? DEFAULT_PRIMARY_NAV_LAYOUT_TYPE
            : navigationRenderOptions.getLayoutType().value());

    addStaticOrDefaultBrandingConfig(PRIMARY_NAV_BACKGROUND_COLOR, navigationRenderOptions.getBackgroundColor());

    addStaticOrDefaultBrandingConfig(PRIMARY_NAV_SELECTED_BACKGROUND_COLOR, navigationRenderOptions.getSelectedTabBackgroundColor());

    addImageBrandingConfig(PRIMARY_NAV_LOGO_UUID, navigationRenderOptions.getLogoUuid(), DEFAULT_LOGO_UUID);

    addStaticOrDefaultBrandingConfig(PRIMARY_NAV_LOGO_ALT_TEXT, navigationRenderOptions.getLogoAltText());

    addStaticOrDefaultBrandingConfig(PRIMARY_NAV_SHOW_DISPLAY_NAME, Boolean.toString(navigationRenderOptions.isShowDisplayName()));
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
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
  @Column(name = "name", nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "url_stub", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  @XmlElement
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    this.urlStub = urlStub;
  }

  @Column(name = "should_publish", nullable = false)
  @XmlElement(name = "published")
  public boolean getShouldPublish() {
    return shouldPublish;
  }

  public void setShouldPublish(boolean shouldPublish) {
    this.shouldPublish = shouldPublish;
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "portal_rm", joinColumns = @JoinColumn(name = "portal_id"),
      inverseJoinColumns = @JoinColumn(name = "rm_entry_id"))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
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
  @Transient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      roleMapBuilder.entries(roleMapEntry);
    }

    return roleMapBuilder.build();
  }

  /**
   * Controls whether 'this' Portal is configured to perform a design object lock validation
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

  // --------------------

  @Transient
  @Override
  public QName getTypeQName() {
    return QNAME;
  }

  @Override
  @Transient
  public boolean isPublic() {
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // Portals are never public (visible to all users).
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.PORTAL_VIEWER.getName();
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Column(name="recaptcha_cs_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getRecaptchaConnectedSystemUuid() {
    return recaptchaConnectedSystemUuid;
  }

  public void setRecaptchaConnectedSystemUuid(String recaptchaConnectedSystemUuid) {
    this.recaptchaConnectedSystemUuid = recaptchaConnectedSystemUuid;
  }

  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = PortalBrandingCfg.class)
  @JoinColumn(name = "portal_id", nullable=false)
  @OrderBy
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<PortalBrandingCfg> getPortalBrandingCfgs() {
    return portalBrandingCfgs;
  }

  public void setPortalBrandingCfgs(List<PortalBrandingCfg> portalBrandingCfgs) {
    this.portalBrandingCfgs = portalBrandingCfgs;
  }

  /**
   * Returns the PortalPublishInfo for this Portal, which is eagerly fetched by Hibernate so we always have
   * access to it whenever we query for a Portal.
   *
   * Note the use of a subset of cascade types instead of the more typical ALL type. We do this to avoid
   * avoid cascade-deleting the publish info when a Portal is deleted. See {@link PortalPublishInfo} for an
   * explanation of why this is necessary.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "publish_info_id")
  @Cascade({
      org.hibernate.annotations.CascadeType.PERSIST
  })
  public PortalPublishInfo getPublishInfo() {
    return publishInfo;
  }

  public void setPublishInfo(PortalPublishInfo publishInfo) {
    this.publishInfo = publishInfo;
  }

  @Column(name = "service_acct_user_uuid", nullable = true)
  public String getServiceAccountUuid() {
    return serviceAccountUuid;
  }

  public void setServiceAccountUuid(String serviceAccountUuid) {
    this.serviceAccountUuid = serviceAccountUuid;
  }

  public boolean equivalentTo(final Portal portal) {
    return EQUIVALENCE.equivalent(this, portal);
  }

  private static Equivalence<Portal> EQUIVALENCE = new Equivalence<Portal>() {
    @Override
    protected boolean doEquivalent(Portal lhs, Portal rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return Objects.equal(lhs.id, rhs.id) &&
          stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.name, rhs.name) &&
          stringEquals(lhs.description, rhs.description) &&
          stringEquals(lhs.urlStub, rhs.urlStub);
    }

    @Override
    protected int doHash(Portal portal) {
      return Objects.hashCode(portal.id, portal.uuid, portal.name, portal.description,
          portal.urlStub, portal.versionUuid, portal.getRoleMap());
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
 };

  private void updatePortalBranding(PortalBrandingCfgKey brandingKey, BrandingCfgSource source, String value) {
    boolean exist = false;
    for (PortalBrandingCfg currentBrandingConfig : portalBrandingCfgs) {
      if (currentBrandingConfig.getBrandingKey() == brandingKey) {
        currentBrandingConfig.setBrandingSource(source);
        currentBrandingConfig.setBrandingValue(value);
        exist = true;
        break;
      }
    }
    if (!exist) {
      PortalBrandingCfg newBrandingConfig = new PortalBrandingCfg(brandingKey, source, value);
      portalBrandingCfgs.add(newBrandingConfig);
    }
  }

  private void addImageBrandingConfig(PortalBrandingCfgKey brandingKey, String uuidValue, String defaultValue) {
    boolean uuidEmpty = Strings.isNullOrEmpty(uuidValue);
    boolean uuidDefault = defaultValue.equalsIgnoreCase(uuidValue);
    if (!uuidEmpty && !uuidDefault){
      updatePortalBranding(brandingKey, DOCUMENT, uuidValue);
    } else {
      updatePortalBranding(brandingKey, DEFAULT, defaultValue);
    }
  }

  private void addStaticOrDefaultBrandingConfig(PortalBrandingCfgKey brandingKey, String staticValue) {
    addStaticOrDefaultBrandingConfig(brandingKey, staticValue, null);
  }

  private void addStaticOrDefaultBrandingConfig(PortalBrandingCfgKey brandingKey, String staticValue, String defaultValue) {
    if (Strings.isNullOrEmpty(staticValue) || staticValue.equals(defaultValue)) {
      updatePortalBranding(brandingKey, DEFAULT, defaultValue);
    } else {
      updatePortalBranding(brandingKey, STATIC, staticValue);
    }
  }

  /**
   * Returns null if the default favicon is being used
   */
  @Transient
  public String getFaviconUuid() {
    String faviconUuid = getPortalBrandingConfig(FAVICON, DOCUMENT);
    String faviconDefault = getPortalBrandingConfig(FAVICON, DEFAULT);
    return faviconDefault == null ? faviconUuid : faviconDefault;
  }

  @Transient
  @XmlElement
  public ButtonShape getButtonShape() {
    String buttonShapeText = getPortalBrandingConfig(BUTTON_SHAPE, ENUM);
    return buttonShapeText == null ? ButtonShape.SQUARED : ButtonShape.fromText(buttonShapeText);
  }

  public void setButtonShape(ButtonShape buttonShape) {
    updatePortalBranding(BUTTON_SHAPE, ENUM, buttonShape.getText());
  }

  @Transient
  @XmlElement
  public InputShape getInputShape() {
    String inputShapeText = getPortalBrandingConfig(INPUT_SHAPE, ENUM);
    return inputShapeText == null ? InputShape.SQUARED : InputShape.fromText(inputShapeText);
  }

  public void setInputShape(InputShape inputShape) {
    updatePortalBranding(INPUT_SHAPE, ENUM, inputShape.getText());
  }

  /**
   * Only intended to be called during IX operations. By returning a Ref based on the PWA UUID,
   * we empower IX to generate appropriate Missing Precedents errors during both Export and Import.
   */
  @Transient
  @XmlElement(type = Object.class, name = "pwaIcon")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.portalPwaIcon,
    prependedBreadcrumbs = {BreadcrumbText.portalPwa})
  @ForeignKeyCustomBinder(CustomBinderType.DOCUMENT_REF)
  public Ref<Long, String> getPwaIconRef() {
    String pwaIconUuid = getPwaIconUuid();
    if (Strings.isNullOrEmpty(pwaIconUuid) || DEFAULT_PWA_ICON_UUID.equals(pwaIconUuid)) {
      return null;
    } else {
      return new DocumentRefImpl(pwaIconUuid);
    }
  }

  /**
   * Returns null if the default PWA icon is being used
   */
  @Transient
  public String getPwaIconUuid() {
    String pwaIconUuid = getPortalBrandingConfig(PWA_ICON, DOCUMENT);
    String pwaIconDefault = getPortalBrandingConfig(PWA_ICON, DEFAULT);
    return pwaIconDefault == null ? pwaIconUuid : pwaIconDefault;
  }

  public void setPwaIconRef(Ref<Long,String> objectRef) {
    updatePortalBranding(PWA_ICON, DOCUMENT, objectRef.getUuid());
  }

  @Transient
  @XmlElement
  public String getPwaShortName() {
    return getPortalBrandingConfig(PWA_SHORT_NAME, STATIC);
  }

  public void setPwaShortName(String pwaShortName) {
    updatePortalBranding(PWA_SHORT_NAME, STATIC, Strings.isNullOrEmpty(pwaShortName) ? null : pwaShortName);
  }

  @Transient
  @XmlElement
  public boolean getPwaEnabled() {
    String pwaEnabledAsString = getPortalBrandingConfig(PWA_ENABLED, STATIC);
    return Boolean.parseBoolean(pwaEnabledAsString);
  }

  public void setPwaEnabled(boolean pwaEnabled) {
    updatePortalBranding(PWA_ENABLED, STATIC, Boolean.toString(pwaEnabled));
  }

  /**
   * Only intended to be called during IX operations. By returning a Ref based on our favicon UUID,
   * we empower IX to generate appropriate Missing Precedents errors during both Export and Import.
   */
  @Transient
  @XmlElement(type = Object.class, name = "favicon")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.portalFavicon,
    prependedBreadcrumbs = {BreadcrumbText.portalBranding})
  @ForeignKeyCustomBinder(CustomBinderType.DOCUMENT_REF)
  public Ref<Long, String> getFaviconRef() {
    String faviconUuid = getFaviconUuid();
    if (Strings.isNullOrEmpty(faviconUuid) || DEFAULT_FAVICON_UUID.equals(faviconUuid)) {
      return null;
    } else {
      return new DocumentRefImpl(faviconUuid);
    }
  }

  public void setFaviconRef(Ref<Long,String> objectRef) {
    updatePortalBranding(FAVICON, DOCUMENT, objectRef.getUuid());
  }

  @Transient
  @XmlElement
  public String getAccentColor() {
    return getPortalBrandingConfig(ACCENT_COLOR, STATIC);
  }

  public void setAccentColor(String accentColor) {
    updatePortalBranding(ACCENT_COLOR, STATIC, accentColor);
  }

  @Transient
  @XmlElement
  public String getLoadingBarColor() {
    return getPortalBrandingConfig(LOADING_BAR_COLOR, STATIC);
  }

  public void setLoadingBarColor(String loadingBarColor) {
    updatePortalBranding(LOADING_BAR_COLOR, STATIC, loadingBarColor);
  }

  @Transient
  @XmlElement
  public String getDisplayName() {
    return getPortalBrandingConfig(DISPLAY_NAME, STATIC);
  }

  public void setDisplayName(String displayName) {
    updatePortalBranding(DISPLAY_NAME, STATIC, Strings.isNullOrEmpty(displayName) ? null : displayName);
  }

  @Transient
  @XmlElement
  public NavigationLayoutType getPrimaryNavLayoutType() {
    final String layoutTypeDefault = getPortalBrandingConfig(PRIMARY_NAV_LAYOUT_TYPE, DEFAULT);
    final String layoutType = layoutTypeDefault == null
        ? getPortalBrandingConfig(PRIMARY_NAV_LAYOUT_TYPE, ENUM)
        : layoutTypeDefault;
    return NavigationLayoutType.fromValue(layoutType == null
        ? DEFAULT_PRIMARY_NAV_LAYOUT_TYPE
        : layoutType);
  }

  public void setPrimaryNavLayoutType(NavigationLayoutType primaryNavLayoutType) {
    updatePortalBranding(PRIMARY_NAV_LAYOUT_TYPE, ENUM, primaryNavLayoutType.value());
  }

  @Transient
  @XmlElement
  public String getPrimaryNavBackgroundColor() {
    return getPortalBrandingConfig(PRIMARY_NAV_BACKGROUND_COLOR, STATIC);
  }

  public void setPrimaryNavBackgroundColor(String backgroundColor) {
    updatePortalBranding(PRIMARY_NAV_BACKGROUND_COLOR, STATIC, backgroundColor);
  }

  @Transient
  @XmlElement
  public String getPrimaryNavSelectedBackgroundColor() {
    return getPortalBrandingConfig(PRIMARY_NAV_SELECTED_BACKGROUND_COLOR, STATIC);
  }

  public void setPrimaryNavSelectedBackgroundColor(String selectedBackgroundColor) {
    updatePortalBranding(PRIMARY_NAV_SELECTED_BACKGROUND_COLOR, STATIC, selectedBackgroundColor);
  }

  /**
   * Only intended to be called during IX operations. By returning a Ref based on the primary nav logo UUID,
   * we empower IX to generate appropriate Missing Precedents errors during both Export and Import.
   */
  @Transient
  @XmlElement(type = Object.class, name = "primaryNavLogo")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.portalPrimaryNavLogo,
      prependedBreadcrumbs = {BreadcrumbText.portalBranding})
  @ForeignKeyCustomBinder(CustomBinderType.DOCUMENT_REF)
  public Ref<Long, String> getPrimaryNavLogoRef() {
    String primaryNavLogoUuid = getPrimaryNavLogoUuid();
    if (Strings.isNullOrEmpty(primaryNavLogoUuid) || DEFAULT_PRIMARY_NAV_LOGO_UUID.equals(primaryNavLogoUuid)) {
      return null;
    } else {
      return new DocumentRefImpl(primaryNavLogoUuid);
    }
  }

  public void setPrimaryNavLogoRef(Ref<Long,String> primaryNavLogoRef) {
    updatePortalBranding(PRIMARY_NAV_LOGO_UUID, DOCUMENT, primaryNavLogoRef.getUuid());
  }

  @Transient
  public String getPrimaryNavLogoUuid() {
    String logoUuid = getPortalBrandingConfig(PRIMARY_NAV_LOGO_UUID, DOCUMENT);
    String logoDefault = getPortalBrandingConfig(PRIMARY_NAV_LOGO_UUID, DEFAULT);
    return logoDefault == null ? logoUuid : logoDefault;
  }

  @Transient
  @XmlElement
  public String getPrimaryNavLogoAltText() {
    return getPortalBrandingConfig(PRIMARY_NAV_LOGO_ALT_TEXT, STATIC);
  }

  public void setPrimaryNavLogoAltText(String logoAltText) {
    updatePortalBranding(PRIMARY_NAV_LOGO_ALT_TEXT, STATIC, logoAltText);
  }

  @Transient
  @XmlElement
  public boolean isPrimaryNavShowDisplayName() {
    return Boolean.parseBoolean(getPortalBrandingConfig(PRIMARY_NAV_SHOW_DISPLAY_NAME, STATIC));
  }

  public void setPrimaryNavShowDisplayName(boolean showDisplayName) {
    updatePortalBranding(PRIMARY_NAV_SHOW_DISPLAY_NAME, STATIC, Boolean.toString(showDisplayName));
  }

  private String getPortalBrandingConfig(PortalBrandingCfgKey brandingKey, BrandingCfgSource source) {
    return portalBrandingCfgs.stream()
        .filter(cfg -> brandingKey == cfg.getBrandingKey() && source == cfg.getBrandingSource())
        .map(cfg -> cfg.getBrandingValue())
        .findFirst()
        .orElse(null);
  }

  @Transient
  @XmlElement(type = Object.class, name = "recaptchaConnectedSystem")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.portalRecaptcha,
    prependedBreadcrumbs = {BreadcrumbText.portalServiceAccess})
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<Long, String> getRecaptchaConnectedSystemRef() {
    return Strings.isNullOrEmpty(recaptchaConnectedSystemUuid)
        ? null : new ConnectedSystemRefImpl(recaptchaConnectedSystemUuid);
  }

  public void setRecaptchaConnectedSystemRef(Ref<Long,String> recaptchaConnectedSystemRef) {
    recaptchaConnectedSystemUuid = recaptchaConnectedSystemRef == null
        ? null : recaptchaConnectedSystemRef.getUuid();
  }

  @Override
  @XmlElement(name = "navigationNode") // LATER (AN-230742): rename to page
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @HasForeignKeys(breadcrumb = BreadcrumbText.portalPages)
  @JoinColumn(name = "portal_id")
  @OrderColumn(name = "order_idx")
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<PortalPage> getPages() {
    return portalPages;
  }

  public void setPages(final List<PortalPage> pages) {
    portalPages = pages;
  }

  @Transient
  @XmlElement
  public ButtonLabelCase getButtonLabelCase() {
    String buttonLabelCaseText = getPortalBrandingConfig(BUTTON_LABEL_CASE, ENUM);
    return buttonLabelCaseText == null ? ButtonLabelCase.UPPERCASE : ButtonLabelCase.fromText(buttonLabelCaseText);
  }

  public void setButtonLabelCase(ButtonLabelCase buttonLabelCase) {
    updatePortalBranding(BUTTON_LABEL_CASE, ENUM, buttonLabelCase.getText());
  }

  @Transient
  @XmlElement
  public DialogShape getDialogShape() {
    String dialogShapeText = getPortalBrandingConfig(DIALOG_SHAPE, ENUM);
    return dialogShapeText == null ? DialogShape.SQUARED : DialogShape.fromText(dialogShapeText);
  }

  public void setDialogShape(DialogShape dialogShape) {
    updatePortalBranding(DIALOG_SHAPE, ENUM, dialogShape.getText());
  }

  @Column(name = "show_navigation", nullable = false)
  @XmlElement
  public boolean isShowNavigation() {
    return showNavigation;
  }

  public void setShowNavigation(boolean showNavigation) {
    this.showNavigation = showNavigation;
  }

  @Column(name = "hostname", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement(defaultValue = DEFAULT_PORTAL_HOSTNAME_PLACEHOLDER)
  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  /**
   * Only intended to be called during IX operations. By returning a Ref based on the _username_
   * derived from our serviceAccountUuid property, we empower IX to generate appropriate Missing
   * Precedents errors during both Export and Import.
   */
  @Transient
  @XmlElement(type = Object.class, name = "serviceAccountUser")
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.portalServiceAccountUser)
  @ForeignKeyCustomBinder(CustomBinderType.REF)
  public Ref<Object,String> getServiceAccountUserRef() {
    String usernameToGiveToIx = null;

    if (!Strings.isNullOrEmpty(serviceAccountUsernameFromImportData)) {
      // =================================== Called during Import ==================================
      usernameToGiveToIx = serviceAccountUsernameFromImportData;
    } else if (!Strings.isNullOrEmpty(serviceAccountUuid)) {
      // =================================== Called during Export ==================================
      usernameToGiveToIx = getUsernameOrNullFromUserUuid(serviceAccountUuid);
      if (usernameToGiveToIx == null) {
        // Couldn't find the user, so we'll return a UserRefImpl whose username is the UUID we
        // failed to look up. This is not technically a proper UserRefImpl since the "uuid" for
        // those is supposed to be a username, not a User UUID. However, passing the UUID will let
        // IX throw an UnresolvedReferenceException that at least correlates with the bad UUID.
        usernameToGiveToIx = serviceAccountUuid;
      }
    }
    if (usernameToGiveToIx == null) {
      return null;
    }
    // Weirdly below, we're passing a username in as the so-called "uuid" parameter of the
    // UserRefImpl. This is actually correct, since IX employs the username as the
    // system-independent identifier for a User.
    return new UserRefImpl(null, usernameToGiveToIx);
  }

  public void setServiceAccountUserRef(Ref<Object,String> serviceAccountUserRef) {
    serviceAccountUsernameFromImportData = serviceAccountUserRef.getUuid();
    // The Ref getter above says "UUID" but it's really the username, since that's what IX always uses
    // as the system-independent identifier for Users

    serviceAccountUuid = getUserUuidOrNullFromUsername(serviceAccountUsernameFromImportData);
    // If the UUID is null because we failed to look up the user, we just ignore it. This can occur when
    // someone is trying to Import a Portal that refers to a user who doesn't exist on the target system.
    // It's not our responsibility to flag this error, rather that will be done by the
    // IX machinery that eventually calls this property's getter.
  }

  /**
   * To minimize the amount of data we have to live with in the future, we are deliberately avoiding
   * writing default values to our XML representation-- instead we omit them entirely. This means
   * that after XML unmarshalling is finished, some of our branding configurations might not be set.
   * This method adds any missing branding configs once we're entirely unmarshalled. The defaults we
   * provide here match the defaults set in our normal {@link Portal#Portal(PortalDto)} constructor.
   */
  public void afterUnmarshall() {
    Set<PortalBrandingCfgKey> brandingCfgKeys = portalBrandingCfgs.stream()
        .map(cfg -> cfg.getBrandingKey())
        .collect(Collectors.toSet());
    if (!brandingCfgKeys.contains(ACCENT_COLOR)) {
      updatePortalBranding(ACCENT_COLOR, DEFAULT, null);
    }
    if (!brandingCfgKeys.contains(LOADING_BAR_COLOR)) {
      updatePortalBranding(LOADING_BAR_COLOR, DEFAULT, null);
    }
    if (!brandingCfgKeys.contains(FAVICON)) {
      updatePortalBranding(FAVICON, DEFAULT, DEFAULT_FAVICON_UUID);
    }
    if (!brandingCfgKeys.contains(PWA_ICON)) {
      updatePortalBranding(PWA_ICON, DEFAULT, DEFAULT_PWA_ICON_UUID);
    }
    if (showNavigation && !brandingCfgKeys.contains(PRIMARY_NAV_LOGO_UUID)) {
      updatePortalBranding(PRIMARY_NAV_LOGO_UUID, DEFAULT, DEFAULT_LOGO_UUID);
    }
  }

  /**
   * Given a User UUID, returns the name of the user or null if no such user exists
   */
  private String getUsernameOrNullFromUserUuid(String userUuid) {
    ExtendedUserProfileService userProfileService =
        ApplicationContextHolder.getBean(ExtendedUserProfileService.class);
    UserProfile user;
    try {
      user = userProfileService.getUserByUuid(userUuid);
    } catch (Exception e) {
      // The spec for `getUserByUuid()` claims it throws InvalidUserException when you pass a bogus UUID,
      // but in fact it throws a different exception. We don't really care what exactly the problem was,
      // we just know the User UUID we got is no good:
      return null;
    }
    return user.getUsername();
  }

  /**
   * Given a User name, returns its UUID or null if no such user exists
   */
  private String getUserUuidOrNullFromUsername(String username) {
    try {
      ExtendedUserProfileService userProfileService =
          ApplicationContextHolder.getBean(ExtendedUserProfileService.class);

      UserProfile user = userProfileService.getUser(username);
      return user.getUuid();
    } catch (InvalidUserException | NoSuchBeanDefinitionException e) {
      return null;
    }
  }

  public static final String generateUuidForPage(String uuid) {
    String nameAsString = uuid + Portal.SUFFIX_FOR_PAGE_UUID_GENERATION;
    byte[] name = nameAsString.getBytes(StandardCharsets.UTF_8);
    return UUID.nameUUIDFromBytes(name).toString();
  }

  public static Stream<PortalPage> getFlattenedNestedPages(List<PortalPage> portalPages) {
    return portalPages.stream()
        .flatMap(page -> Stream.concat(
            Stream.of(page),
            getFlattenedNestedPages(page.getChildren())
        ));
  }
}
