package com.appiancorp.object.quickapps.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.security.user.Group;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.google.common.base.Objects;

/**
 * QuickApp is the bean that is used to store data about a QuickApp when it is created from the QuickApps interface.
 * QuickApps define record fields in the form of {@link QuickAppField}
 */
@Entity
@Table(name = "quickapp")
public class QuickApp implements Id<Long>, Uuid<String>, HasAuditInfo, QuickAppDescriptor {
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public static final String PROP_NAME = "name";
  public static final String PROP_PREFIX = "prefix";
  public static final String PROP_DATABASE_PREFIX = "databasePrefix";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_APP_UUID = "appUuid";
  public static final String PROP_FIELDS = "fields";
  public static final String PROP_COLLAB_GROUP = "collabGroup";
  public static final String PROP_ADMIN_GROUP = "adminGroup";
  public static final String PROP_RECORD_NAME = "recordName";
  public static final String PROP_RECORD_PLURAL_NAME = "recordPluralName";
  public static final String PROP_HIDDEN = "hidden";
  public static final String PROP_SITE_ENABLED = "siteEnabled";
  public static final String PROP_SITE_ICON = "siteIcon";

  private Long id;
  private String uuid;
  private String name;
  private String prefix;
  private String description;
  private String appUuid;
  private String recordName;
  private String recordPluralName;
  private Group adminGroup;
  private Group collabGroup;
  private AuditInfo auditInfo = new AuditInfo();
  private List<QuickAppField> fields = new ArrayList<>();
  private String mainTableName;
  private String activityTableName;
  private String databasePrefix;
  private Boolean hidden;
  private boolean siteEnabled;
  private String siteIcon;

  public QuickApp(){}

  public QuickApp(QuickApp quickApp){
    this.id = quickApp.getId();
    this.uuid = quickApp.getUuid();
    this.name = quickApp.getName();
    this.prefix = quickApp.getPrefix();
    this.description = quickApp.getDescription();
    this.appUuid = quickApp.getAppUuid();
    this.recordName = quickApp.getRecordName();
    this.recordPluralName = quickApp.getRecordPluralName();
    this.collabGroup = quickApp.getCollabGroup();
    this.adminGroup = quickApp.getAdminGroup();
    this.auditInfo = quickApp.getAuditInfo();
    this.fields = quickApp.getFields();
    this.mainTableName = quickApp.getMainTableName();
    this.activityTableName = quickApp.getActivityTableName();
    this.databasePrefix = quickApp.getDatabasePrefix();
    this.hidden = quickApp.isHidden();
    this.siteEnabled = quickApp.isSiteEnabled();
    this.siteIcon = quickApp.getSiteIcon();
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "prefix", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  @Column(name = "description", nullable = true, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "record_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getRecordName() {
    return recordName;
  }

  public void setRecordName(String recordName) {
    this.recordName = recordName;
  }

  @Column(name = "record_plural_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getRecordPluralName() {
    return recordPluralName;
  }

  public void setRecordPluralName(String recordPluralName) {
    this.recordPluralName = recordPluralName;
  }

  /**
   * @return the UUID of the Application associated with the QuickApp
   */
  @Column(name = "app_uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  public String getAppUuid() {
    return appUuid;
  }

  /* Sets the uuid of the app associated with this QuickApp */
  public void setAppUuid(String appUuid) {
    this.appUuid = appUuid;
  }

  /**
   * @return the record fields associated with the quick app.
   */

  @OneToMany(cascade = CascadeType.ALL, fetch= FetchType.LAZY, orphanRemoval=true)
  @JoinColumn(name = "quickapp_id", nullable=false)
  @OrderColumn(name= "order_idx", nullable=false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  public List<QuickAppField> getFields() {
    return fields;
  }

  public void setFields(List<QuickAppField> fields) {
    this.fields = fields;
  }

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "admin_grp_id", unique=true)
  public Group getAdminGroup() {
    return adminGroup;
  }

  public void setAdminGroup(Group adminGroup) {
    this.adminGroup = adminGroup;
  }

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "collab_grp_id", unique=true, nullable=false)
  public Group getCollabGroup() {
    return collabGroup;
  }

  public void setCollabGroup(Group collabGroup) {
    this.collabGroup = collabGroup;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  /**
   * Name of the table for the "main" CDT (ie, the CDT used for the Quick App record).
   * This name is stored with the prefix already attached.
   */
  @Column(name = "main_tbl_name", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getMainTableName() {
    return this.mainTableName;
  }

  public void setMainTableName(String mainTableName) {
    this.mainTableName = mainTableName;
  }

  /**
   * Name of the table holding the history for Quick App records.
   * This name is stored with the prefix already attached.
   * Note. "Change" has been renamed to "Activity" for all part of quick app code base,
   * except here. We do not have time to change our primary DB table, hence the discrepancy
   */
  @Column(name = "change_tbl_name", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getActivityTableName() {
    return this.activityTableName;
  }

  public void setActivityTableName(String activityTableName) {
    this.activityTableName = activityTableName;
  }


  /**
   * Prefix that allows us to avoid collisions across different Quick Apps.
   * This will be pre-pended to every table that we create.
   */
  @Column(name = "db_prefix", updatable = false, nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getDatabasePrefix() {
    return this.databasePrefix;
  }

  public void setDatabasePrefix(String databasePrefix) {
    this.databasePrefix = databasePrefix;
  }

  /**
   * The hidden flag on the Quick App Bean. It is always true for apps created with the wizard in /design
   */
  @Column(name = "hidden", updatable = true, nullable = true)
  public Boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * The site_enabled flag on the Quick App Bean. TRUE if the Quick App should contain a site object
   */
  @Column(name = "site_enabled", updatable = true, nullable = false)
  public boolean isSiteEnabled() {
    return this.siteEnabled;
  }

  public void setSiteEnabled(boolean siteEnabled) {
    this.siteEnabled = siteEnabled;
  }

  /**
   * The site_icon string on the Quick App Bean. Contains a font-awesome string identifier, formatted like
   * f000, f001, etc., that represents the site's main icon.
   */
  @Column(name = "site_icon", updatable = true, nullable = true)
  public String getSiteIcon() {
    return this.siteIcon;
  }

  public void setSiteIcon(String siteIcon) {
    this.siteIcon = siteIcon;
  }

  /**
   * Equivalent to implies name, description, and appUuid are equal
   */
  public boolean equivalentTo(final QuickApp quickApp) {
    return QuickAppDescriptor.EQUIVALENCE.equivalent(this, quickApp);
  }

  /**
   * Equivalent to with fields implies:
   * - name, description, recordName, recordPluralName and appUuid are equal {@link #equivalentTo(QuickApp)}
   * - the Group id and uuid are equal
   * - the fields on the QuickApps are equivalent based on {@link QuickAppField#equivalentTo(QuickAppField)}
   */
  public boolean equivalentToWithFields(final QuickApp inputApp) {
    boolean equivalent = equivalentTo(inputApp);

    //compare groups
    Group thisGroup = getCollabGroup();
    Group inputGroup = inputApp.getCollabGroup();
    equivalent &= Objects.equal(thisGroup.getId(), inputGroup.getId()) &&
        Objects.equal(thisGroup.getUuid(), inputGroup.getUuid());

    Group thisAdminGroup = getAdminGroup();
    Group inputAdminGroup = inputApp.getAdminGroup();
    if (equivalent && thisAdminGroup != null && inputAdminGroup != null) {
      equivalent &= Objects.equal(thisAdminGroup.getId(), inputAdminGroup.getId()) &&
          Objects.equal(thisAdminGroup.getUuid(), inputAdminGroup.getUuid());
    } else {
      equivalent &= Objects.equal(thisAdminGroup, inputAdminGroup);
  }

  List<QuickAppField> inputFields = inputApp.getFields();

    if (equivalent && (fields.size() == inputFields.size())) {
      for (int i = 0; i < fields.size(); i++) {
        equivalent &= fields.get(i).equivalentToWithConfigs(inputFields.get(i));
      }
    }
    return equivalent;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    return sb.append("QuickApp [id=")
      .append(id)
      .append(", uuid=")
      .append(uuid)
      .append(", appUuid=")
      .append(appUuid)
      .append(", name=")
      .append(name)
      .append(", prefix=")
      .append(prefix)
      .append(", hidden=")
      .append(hidden)
      .append(", isSiteEnabled=")
      .append(siteEnabled)
      .append(", siteIcon=")
      .append(siteIcon)
      .append("]")
      .toString();
  }
}
