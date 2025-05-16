package com.appiancorp.security.auth.saml.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.appian.core.persist.Constants;
import com.appiancorp.suite.cfg.SamlConfiguration;
import com.appiancorp.suite.cfg.adminconsole.AdminConsoleAuditKey;
import com.appiancorp.suiteapi.type.Hidden;

@Hidden
@Entity
@Table(name = SamlSettings.TABLE_NAME)
public class SamlSettings implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String TABLE_NAME = "saml_settings";
  public static final String PROP_IDP_ENTITY_ID = "idpEntityId";
  public static final String PROP_PRIORITY = "priority";
  public static final int MAX_URI_LENGTH = 1024;

  private Long id;
  private String idpEntityId = "";
  private String idpMetadataUuid = SamlConfiguration.SamlProperty.IdpMetadata.getDefaultValue();
  private String spCertificate = SamlConfiguration.SamlProperty.SpCertificate.getDefaultValue();
  private String spCertificateFileName = SamlConfiguration.SamlProperty.SpCertificateFileName.getDefaultValue();
  private String spRequestSignatureHashMethod = SamlConfiguration.SamlProperty.SpRequestSignatureHashMethod.getDefaultValue();
  private String spEntityId = SamlConfiguration.SamlProperty.SpIdentity.getDefaultValue();
  private String spName = SamlConfiguration.SamlProperty.SpName.getDefaultValue();
  private String friendlyName = SamlConfiguration.SamlProperty.FriendlyName.getDefaultValue();
  private String groupUuid = SamlConfiguration.SamlProperty.GroupUuid.getDefaultValue();

  private boolean autoCreateUsers = SamlConfiguration.SamlProperty.AutoCreateUsers.getDefaultValue();
  private boolean autoReactivateUsers = SamlConfiguration.SamlProperty.AutoReactivateUsers.getDefaultValue();
  private boolean autoUpdateUsers = false;
  private boolean useUsernameAttribute = SamlConfiguration.SamlProperty.UseUsernameAttribute.getDefaultValue();
  private String usernameAttribute = SamlConfiguration.SamlProperty.UsernameAttribute.getDefaultValue();
  private String firstNameAttribute = SamlConfiguration.SamlProperty.FirstNameAttribute.getDefaultValue();
  private String lastNameAttribute = SamlConfiguration.SamlProperty.LastNameAttribute.getDefaultValue();
  private String emailAttribute = SamlConfiguration.SamlProperty.EmailAttribute.getDefaultValue();
  private String nicknameAttribute = "";
  private String homePhoneAttribute = "";
  private String mobilePhoneAttribute = "";
  private String officePhoneAttribute = "";
  private String address1Attribute = "";
  private String address2Attribute = "";
  private String address3Attribute = "";
  private String cityAttribute = "";
  private String stateAttribute = "";
  private String zipCodeAttribute = "";
  private String countryAttribute = "";

  private String customField1Attribute = "";
  private String customField2Attribute = "";
  private String customField3Attribute = "";
  private String customField4Attribute = "";
  private String customField5Attribute = "";
  private String customField6Attribute = "";
  private String customField7Attribute = "";
  private String customField8Attribute = "";
  private String customField9Attribute = "";
  private String customField10Attribute = "";

  private boolean autoUpdateUserGroups = false;
  private String groupNamesAttribute = "";
  private String appianGroupAttributeName = "";
  private String groupTypeUuid = "";

  private boolean allowLowercaseUsername = SamlConfiguration.SamlProperty.LowercaseUsername.getDefaultValue();
  private String minimumAuthenticationMethod = SamlConfiguration.SamlProperty.RequestAuthnContext.getDefaultValue();
  private int priority = SamlSettingsService.HIGHEST_PRIORITY;
  private String description = "";
  private boolean mobileAuthPopup = SamlConfiguration.SamlProperty.MobileAuthPopup.getDefaultValue();
  private boolean rememberIdp = false;
  private int rememberMeTokenExpirationWeb = 0;
  private int rememberMeTokenExpirationMobile = 0;

  public SamlSettings() {}

  SamlSettings(
      Long id,
      String idpEntityId,
      String idpMetadataUuid,
      String spCertificate,
      String spCertificateFileName,
      String spRequestSignatureHashMethod,
      String spEntityId,
      String spName,
      String friendlyName,
      String groupUuid,
      boolean autoCreateUsers,
      boolean autoReactivateUsers,
      boolean autoUpdateUsers,
      boolean useUsernameAttribute,
      String usernameAttribute,
      String firstNameAttribute,
      String lastNameAttribute,
      String emailAttribute,
      String nicknameAttribute,
      String homePhoneAttribute,
      String mobilePhoneAttribute,
      String officePhoneAttribute,
      String address1Attribute,
      String address2Attribute,
      String address3Attribute,
      String cityAttribute,
      String stateAttribute,
      String zipCodeAttribute,
      String countryAttribute,
      String customField1Attribute,
      String customField2Attribute,
      String customField3Attribute,
      String customField4Attribute,
      String customField5Attribute,
      String customField6Attribute,
      String customField7Attribute,
      String customField8Attribute,
      String customField9Attribute,
      String customField10Attribute,
      boolean allowLowercaseUsername,
      String minimumAuthenticationMethod,
      int priority,
      String description,
      boolean mobileAuthPopup,
      boolean rememberIdp,
      int rememberMeTokenExpirationWeb,
      int rememberMeTokenExpirationMobile,
      boolean autoUpdateUserGroups,
      String groupNamesAttribute,
      String appianGroupAttributeName ,
      String groupTypeUuid
      ) {
    this.id = id;
    this.idpEntityId = idpEntityId;
    this.idpMetadataUuid = idpMetadataUuid;
    this.spCertificate = spCertificate;
    this.spCertificateFileName = spCertificateFileName;
    this.spRequestSignatureHashMethod = spRequestSignatureHashMethod;
    this.spEntityId = spEntityId;
    this.spName = spName;
    this.friendlyName = friendlyName;
    this.groupUuid = groupUuid;
    this.autoCreateUsers = autoCreateUsers;
    this.autoReactivateUsers = autoReactivateUsers;
    this.autoUpdateUsers = autoUpdateUsers;
    this.useUsernameAttribute = useUsernameAttribute;
    this.usernameAttribute = usernameAttribute;
    this.firstNameAttribute = firstNameAttribute;
    this.lastNameAttribute = lastNameAttribute;
    this.emailAttribute = emailAttribute;
    this.nicknameAttribute = nicknameAttribute;
    this.homePhoneAttribute = homePhoneAttribute;
    this.mobilePhoneAttribute = mobilePhoneAttribute;
    this.officePhoneAttribute = officePhoneAttribute;
    this.address1Attribute = address1Attribute;
    this.address2Attribute = address2Attribute;
    this.address3Attribute = address3Attribute;
    this.cityAttribute = cityAttribute;
    this.stateAttribute = stateAttribute;
    this.zipCodeAttribute = zipCodeAttribute;
    this.countryAttribute = countryAttribute;
    this.customField1Attribute = customField1Attribute;
    this.customField2Attribute = customField2Attribute;
    this.customField3Attribute = customField3Attribute;
    this.customField4Attribute = customField4Attribute;
    this.customField5Attribute = customField5Attribute;
    this.customField6Attribute = customField6Attribute;
    this.customField7Attribute = customField7Attribute;
    this.customField8Attribute = customField8Attribute;
    this.customField9Attribute = customField9Attribute;
    this.customField10Attribute = customField10Attribute;
    this.allowLowercaseUsername = allowLowercaseUsername;
    this.minimumAuthenticationMethod = minimumAuthenticationMethod;
    this.priority = priority;
    this.description = description;
    this.mobileAuthPopup = mobileAuthPopup;
    this.rememberIdp = rememberIdp;
    this.rememberMeTokenExpirationWeb = rememberMeTokenExpirationWeb;
    this.rememberMeTokenExpirationMobile = rememberMeTokenExpirationMobile;
    this.autoUpdateUserGroups = autoUpdateUserGroups;
    this.groupNamesAttribute = groupNamesAttribute;
    this.appianGroupAttributeName = appianGroupAttributeName;
    this.groupTypeUuid = groupTypeUuid;
  }

  private SamlSettings(SamlSettings original) {
    this.id = original.id;
    this.idpEntityId = original.idpEntityId;
    this.idpMetadataUuid = original.idpMetadataUuid;
    this.spCertificate = original.spCertificate;
    this.spCertificateFileName = original.spCertificateFileName;
    this.spRequestSignatureHashMethod = original.spRequestSignatureHashMethod;
    this.spEntityId = original.spEntityId;
    this.spName = original.spName;
    this.friendlyName = original.friendlyName;
    this.groupUuid = original.groupUuid;
    this.autoCreateUsers = original.autoCreateUsers;
    this.autoReactivateUsers = original.autoReactivateUsers;
    this.autoUpdateUsers = original.autoUpdateUsers;
    this.useUsernameAttribute = original.useUsernameAttribute;
    this.usernameAttribute = original.usernameAttribute;
    this.firstNameAttribute = original.firstNameAttribute;
    this.lastNameAttribute = original.lastNameAttribute;
    this.emailAttribute = original.emailAttribute;
    this.nicknameAttribute = original.nicknameAttribute;
    this.homePhoneAttribute = original.homePhoneAttribute;
    this.mobilePhoneAttribute = original.mobilePhoneAttribute;
    this.officePhoneAttribute = original.officePhoneAttribute;
    this.address1Attribute = original.address1Attribute;
    this.address2Attribute = original.address2Attribute;
    this.address3Attribute = original.address3Attribute;
    this.cityAttribute = original.cityAttribute;
    this.stateAttribute = original.stateAttribute;
    this.zipCodeAttribute = original.zipCodeAttribute;
    this.countryAttribute = original.countryAttribute;
    this.customField1Attribute = original.customField1Attribute;
    this.customField2Attribute = original.customField2Attribute;
    this.customField3Attribute = original.customField3Attribute;
    this.customField4Attribute = original.customField4Attribute;
    this.customField5Attribute = original.customField5Attribute;
    this.customField6Attribute = original.customField6Attribute;
    this.customField7Attribute = original.customField7Attribute;
    this.customField8Attribute = original.customField8Attribute;
    this.customField9Attribute = original.customField9Attribute;
    this.customField10Attribute = original.customField10Attribute;
    this.allowLowercaseUsername = original.allowLowercaseUsername;
    this.minimumAuthenticationMethod = original.minimumAuthenticationMethod;
    this.priority = original.priority;
    this.description = original.description;
    this.mobileAuthPopup = original.mobileAuthPopup;
    this.rememberIdp = original.rememberIdp;
    this.rememberMeTokenExpirationWeb = original.rememberMeTokenExpirationWeb;
    this.rememberMeTokenExpirationMobile = original.rememberMeTokenExpirationMobile;
    this.autoUpdateUserGroups = original.autoUpdateUserGroups;
    this.groupNamesAttribute = original.groupNamesAttribute;
    this.appianGroupAttributeName = original.appianGroupAttributeName;
    this.groupTypeUuid = original.groupTypeUuid;
  }

  @AdminConsoleAuditKey(key = "id")
  @Column(name="id")
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @AdminConsoleAuditKey(key = "idp.entity.id")
  @Column(name="idp_entity_id", length=MAX_URI_LENGTH)
  public String getIdpEntityId() {
    return idpEntityId;
  }

  public void setIdpEntityId(String idpEntityId) {
    this.idpEntityId = idpEntityId;
  }

  @AdminConsoleAuditKey(key = "idp.metadata")
  @Column(name="idp_metadata_uuid", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getIdpMetadataUuid() {
    return idpMetadataUuid;
  }

  public void setIdpMetadataUuid(String idpMetadataUuid) {
    this.idpMetadataUuid = idpMetadataUuid;
  }

  @AdminConsoleAuditKey(key = "sp.certificate")
  @Column(name="sp_certificate", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getSpCertificate() {
    return spCertificate;
  }

  public void setSpCertificate(String spCertificate) {
    this.spCertificate = spCertificate;
  }

  @AdminConsoleAuditKey(key = "sp.certificate.filename")
  @Column(name="sp_certificate_filename", length=1000)
  public String getSpCertificateFileName() {
    return spCertificateFileName;
  }

  public void setSpCertificateFileName(String spCertificateFileName) {
    this.spCertificateFileName = spCertificateFileName;
  }

  @AdminConsoleAuditKey(key = "sp.signature.hash")
  @Column(name="sp_req_signature_hash_method", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getSpRequestSignatureHashMethod() {
    return spRequestSignatureHashMethod;
  }

  public void setSpRequestSignatureHashMethod(String spRequestSignatureHashMethod) {
    this.spRequestSignatureHashMethod = spRequestSignatureHashMethod;
  }

  @AdminConsoleAuditKey(key = "sp.identity")
  @Column(name="sp_entity_id", length=MAX_URI_LENGTH)
  public String getSpEntityId() {
    return spEntityId;
  }

  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }

  @AdminConsoleAuditKey(key = "sp.name")
  @Column(name="sp_name", length=1000)
  public String getSpName() {
    return spName;
  }

  public void setSpName(String spName) {
    this.spName = spName;
  }

  @AdminConsoleAuditKey(key = "idp.friendly.name")
  @Column(name="friendly_name", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getFriendlyName() {
    return friendlyName;
  }

  public void setFriendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
  }

  @AdminConsoleAuditKey(key = "groupuuid")
  @Column(name="group_uuid", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getGroupUuid() {
    return groupUuid;
  }

  public void setGroupUuid(String groupUuid) {
    this.groupUuid = groupUuid;
  }

  @AdminConsoleAuditKey(key = "autocreate.users")
  @Column(name="auto_create_users")
  public boolean getAutoCreateUsers() {
    return autoCreateUsers;
  }

  public void setAutoCreateUsers(boolean autoCreateUsers) {
    this.autoCreateUsers = autoCreateUsers;
  }

  @AdminConsoleAuditKey(key = "autoreactivate.users")
  @Column(name="auto_reactivate_users")
  public boolean getAutoReactivateUsers() {
    return autoReactivateUsers;
  }

  public void setAutoReactivateUsers(boolean autoReactivateUsers) {
    this.autoReactivateUsers = autoReactivateUsers;
  }

  @AdminConsoleAuditKey(key = "autoupdate.users")
  @Column(name="auto_update_users")
  public boolean isAutoUpdateUsers() {
    return autoUpdateUsers;
  }

  public void setAutoUpdateUsers(boolean autoUpdateUserGroups) {
    this.autoUpdateUsers = autoUpdateUserGroups;
  }

  @AdminConsoleAuditKey(key = "autoupdate.user.groups")
  @Column(name="auto_update_user_groups")
  public boolean isAutoUpdateUserGroups() {
    return autoUpdateUserGroups;
  }

  public void setAutoUpdateUserGroups(boolean autoUpdateUserGroups) {
    this.autoUpdateUserGroups = autoUpdateUserGroups;
  }

  @AdminConsoleAuditKey(key = "use.username.attribute")
  @Column(name="use_username_attr")
  public boolean getUseUsernameAttribute() {
    return useUsernameAttribute;
  }

  public void setUseUsernameAttribute(boolean useUsernameAttribute) {
    this.useUsernameAttribute = useUsernameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.username")
  @Column(name="username_attr", length=1000)
  public String getUsernameAttribute() {
    return usernameAttribute;
  }

  public void setUsernameAttribute(String usernameAttribute) {
    this.usernameAttribute = usernameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.firstname")
  @Column(name="first_name_attr", length=1000)
  public String getFirstNameAttribute() {
    return firstNameAttribute;
  }

  public void setFirstNameAttribute(String firstNameAttribute) {
    this.firstNameAttribute = firstNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.lastname")
  @Column(name="last_name_attr", length=1000)
  public String getLastNameAttribute() {
    return lastNameAttribute;
  }

  public void setLastNameAttribute(String lastNameAttribute) {
    this.lastNameAttribute = lastNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.email")
  @Column(name="email_attr", length=1000)
  public String getEmailAttribute() {
    return emailAttribute;
  }

  public void setEmailAttribute(String emailAttribute) {
    this.emailAttribute = emailAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.nickname")
  @Column(name="nickname_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getNicknameAttribute() {
    return nicknameAttribute;
  }

  public void setNicknameAttribute(String nicknameAttribute) {
    this.nicknameAttribute = nicknameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.home.phone")
  @Column(name="home_phone_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getHomePhoneAttribute() {
    return homePhoneAttribute;
  }

  public void setHomePhoneAttribute(String homePhoneAttribute) {
    this.homePhoneAttribute = homePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.mobile.phone")
  @Column(name="mobile_phone_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getMobilePhoneAttribute() {
    return mobilePhoneAttribute;
  }

  public void setMobilePhoneAttribute(String mobilePhoneAttribute) {
    this.mobilePhoneAttribute = mobilePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.office.phone")
  @Column(name="office_phone_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getOfficePhoneAttribute() {
    return officePhoneAttribute;
  }

  public void setOfficePhoneAttribute(String officePhoneAttribute) {
    this.officePhoneAttribute = officePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address1")
  @Column(name="address1_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress1Attribute() {
    return address1Attribute;
  }

  public void setAddress1Attribute(String address1Attribute) {
    this.address1Attribute = address1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address2")
  @Column(name="address2_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress2Attribute() {
    return address2Attribute;
  }

  public void setAddress2Attribute(String address2Attribute) {
    this.address2Attribute = address2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address3")
  @Column(name="address3_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress3Attribute() {
    return address3Attribute;
  }

  public void setAddress3Attribute(String address3Attribute) {
    this.address3Attribute = address3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.city")
  @Column(name="city_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCityAttribute() {
    return cityAttribute;
  }

  public void setCityAttribute(String cityAttribute) {
    this.cityAttribute = cityAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.state")
  @Column(name="state_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getStateAttribute() {
    return stateAttribute;
  }

  public void setStateAttribute(String stateAttribute) {
    this.stateAttribute = stateAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.zipcode")
  @Column(name="zipcode_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getZipCodeAttribute() {
    return zipCodeAttribute;
  }

  public void setZipCodeAttribute(String zipCodeAttribute) {
    this.zipCodeAttribute = zipCodeAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.country")
  @Column(name="country_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCountryAttribute() {
    return countryAttribute;
  }

  public void setCountryAttribute(String countryAttribute) {
    this.countryAttribute = countryAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield1")
  @Column(name="customfield1_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField1Attribute() {
    return customField1Attribute;
  }

  public void setCustomField1Attribute(String customField1Attribute) {
    this.customField1Attribute = customField1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield2")
  @Column(name="customfield2_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField2Attribute() {
    return customField2Attribute;
  }

  public void setCustomField2Attribute(String customField2Attribute) {
    this.customField2Attribute = customField2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield3")
  @Column(name="customfield3_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField3Attribute() {
    return customField3Attribute;
  }

  public void setCustomField3Attribute(String customField3Attribute) {
    this.customField3Attribute = customField3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield4")
  @Column(name="customfield4_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField4Attribute() {
    return customField4Attribute;
  }

  public void setCustomField4Attribute(String customField4Attribute) {
    this.customField4Attribute = customField4Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield5")
  @Column(name="customfield5_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField5Attribute() {
    return customField5Attribute;
  }

  public void setCustomField5Attribute(String customField5Attribute) {
    this.customField5Attribute = customField5Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield6")
  @Column(name="customfield6_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField6Attribute() {
    return customField6Attribute;
  }

  public void setCustomField6Attribute(String customField6Attribute) {
    this.customField6Attribute = customField6Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield7")
  @Column(name="customfield7_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField7Attribute() {
    return customField7Attribute;
  }

  public void setCustomField7Attribute(String customField7Attribute) {
    this.customField7Attribute = customField7Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield8")
  @Column(name="customfield8_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField8Attribute() {
    return customField8Attribute;
  }

  public void setCustomField8Attribute(String customField8Attribute) {
    this.customField8Attribute = customField8Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield9")
  @Column(name="customfield9_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField9Attribute() {
    return customField9Attribute;
  }

  public void setCustomField9Attribute(String customField9Attribute) {
    this.customField9Attribute = customField9Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield10")
  @Column(name="customfield10_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField10Attribute() {
    return customField10Attribute;
  }

  public void setCustomField10Attribute(String customField10Attribute) {
    this.customField10Attribute = customField10Attribute;
  }

  @AdminConsoleAuditKey(key = "lowercaseusername")
  @Column(name="allow_lowercase_username")
  public boolean getAllowLowercaseUsername() {
    return allowLowercaseUsername;
  }

  public void setAllowLowercaseUsername(boolean allowLowercaseUsername) {
    this.allowLowercaseUsername = allowLowercaseUsername;
  }

  @AdminConsoleAuditKey(key = "min.auth.method")
  @Column(name="min_authn_method", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getMinimumAuthenticationMethod() {
    return minimumAuthenticationMethod;
  }

  public void setMinimumAuthenticationMethod(String minimumAuthenticationMethod) {
    this.minimumAuthenticationMethod = minimumAuthenticationMethod;
  }

  @AdminConsoleAuditKey(key = "priority")
  @Column(name="priority")
  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  @AdminConsoleAuditKey(key = "description")
  @Column(name="description", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @AdminConsoleAuditKey(key = "rememberIdp")
  @Column(name="remember_idp")
  public boolean isRememberIdp() {
    return rememberIdp;
  }

  public void setRememberIdp(boolean rememberIdp) {
    this.rememberIdp = rememberIdp;
  }

  @AdminConsoleAuditKey(key = "rememberMeTokenExpirationWeb")
  @Column(name="remember_me_token_exp_web")
  public int getRememberMeTokenExpirationWeb() {
    return rememberMeTokenExpirationWeb;
  }

  public void setRememberMeTokenExpirationWeb(int expiration) {
    this.rememberMeTokenExpirationWeb = expiration;
  }

  @AdminConsoleAuditKey(key = "rememberMeTokenExpirationMobile")
  @Column(name="remember_me_token_exp_mobile")
  public int getRememberMeTokenExpirationMobile() {
    return rememberMeTokenExpirationMobile;
  }

  public void setRememberMeTokenExpirationMobile(int expiration) {
    this.rememberMeTokenExpirationMobile = expiration;
  }

  @AdminConsoleAuditKey(key = "mobile.authPopup")
  @Column(name="mobile_auth_popup")
  public boolean isMobileAuthPopup() {
    return mobileAuthPopup;
  }

  public void setMobileAuthPopup(boolean mobileAuthPopup) {
    this.mobileAuthPopup = mobileAuthPopup;
  }

  @AdminConsoleAuditKey(key = "grouptypeuuid")
  @Column(name="grouptype_uuid", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getGroupTypeUuid() {
    return groupTypeUuid;
  }

  public void setGroupTypeUuid(String groupTypeUuid) {
    this.groupTypeUuid = groupTypeUuid;
  }

  @AdminConsoleAuditKey(key = "attribute.groupnames")
  @Column(name="groupnames_attr", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getGroupNamesAttribute() {
    return groupNamesAttribute;
  }

  public void setGroupNamesAttribute(String groupNamesAttribute) {
    this.groupNamesAttribute = groupNamesAttribute;
  }

  @AdminConsoleAuditKey(key = "appianGroupAttributeName")
  @Column(name="appian_group_attribute_name", length=Constants.COL_MAXLEN_INDEXABLE)
  public String getAppianGroupAttributeName() {
    return appianGroupAttributeName;
  }

  public void setAppianGroupAttributeName(String appianGroupAttributeName) {
    this.appianGroupAttributeName = appianGroupAttributeName;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id)
        .append("idpEntityId", idpEntityId)
        .append("idpMetadataUuid", idpMetadataUuid)
        .append("spCertificate", spCertificate)
        .append("spCertificateFileName", spCertificateFileName)
        .append("spRequestSignatureHashMethod", spRequestSignatureHashMethod)
        .append("spEntityId", spEntityId)
        .append("spName", spName)
        .append("friendlyName", friendlyName)
        .append("groupUuid", groupUuid)
        .append("autoCreateUsers", autoCreateUsers)
        .append("autoReactivateUsers", autoReactivateUsers)
        .append("autoUpdateUsers", autoUpdateUsers)
        .append("useUsernameAttribute", useUsernameAttribute)
        .append("usernameAttribute", usernameAttribute)
        .append("firstNameAttribute", firstNameAttribute)
        .append("lastNameAttribute", lastNameAttribute)
        .append("emailAttribute", emailAttribute)
        .append("nicknameAttribute", nicknameAttribute)
        .append("homePhoneAttribute", homePhoneAttribute)
        .append("mobilePhoneAttribute", mobilePhoneAttribute)
        .append("officePhoneAttribute", officePhoneAttribute)
        .append("address1Attribute", address1Attribute)
        .append("address2Attribute", address2Attribute)
        .append("address3Attribute", address3Attribute)
        .append("cityAttribute", cityAttribute)
        .append("stateAttribute", stateAttribute)
        .append("zipCodeAttribute", zipCodeAttribute)
        .append("countryAttribute", countryAttribute)
        .append("customField1Attribute", customField1Attribute)
        .append("customField2Attribute", customField2Attribute)
        .append("customField3Attribute", customField3Attribute)
        .append("customField4Attribute", customField4Attribute)
        .append("customField5Attribute", customField5Attribute)
        .append("customField6Attribute", customField6Attribute)
        .append("customField7Attribute", customField7Attribute)
        .append("customField8Attribute", customField8Attribute)
        .append("customField9Attribute", customField9Attribute)
        .append("customField10Attribute", customField10Attribute)
        .append("allowLowercaseUsername", allowLowercaseUsername)
        .append("minimumAuthenticationMethod", minimumAuthenticationMethod)
        .append("priority", priority)
        .append("description", description)
        .append("mobileAuthPopup", mobileAuthPopup)
        .append("rememberIdp", rememberIdp)
        .append("rememberMeTokenExpirationWeb", rememberMeTokenExpirationWeb)
        .append("rememberMeTokenExpirationMobile", rememberMeTokenExpirationMobile)
        .append("autoUpdateUserGroups", autoUpdateUserGroups)
        .append("groupTypeUuid", groupTypeUuid)
        .append("groupNamesAttribute", groupNamesAttribute)
        .append("appianGroupAttributeName", appianGroupAttributeName)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SamlSettings that = (SamlSettings)o;

    return new EqualsBuilder().append(autoCreateUsers, that.autoCreateUsers)
        .append(autoReactivateUsers, that.autoReactivateUsers)
        .append(autoUpdateUsers, that.autoUpdateUsers)
        .append(useUsernameAttribute, that.useUsernameAttribute)
        .append(allowLowercaseUsername, that.allowLowercaseUsername)
        .append(priority, that.priority)
        .append(mobileAuthPopup, that.mobileAuthPopup)
        .append(rememberIdp, that.rememberIdp)
        .append(rememberMeTokenExpirationWeb, that.rememberMeTokenExpirationWeb)
        .append(rememberMeTokenExpirationMobile, that.rememberMeTokenExpirationMobile)
        .append(id, that.id)
        .append(idpEntityId, that.idpEntityId)
        .append(idpMetadataUuid, that.idpMetadataUuid)
        .append(spCertificate, that.spCertificate)
        .append(spCertificateFileName, that.spCertificateFileName)
        .append(spRequestSignatureHashMethod, that.spRequestSignatureHashMethod)
        .append(spEntityId, that.spEntityId)
        .append(spName, that.spName)
        .append(friendlyName, that.friendlyName)
        .append(groupUuid, that.groupUuid)
        .append(usernameAttribute, that.usernameAttribute)
        .append(firstNameAttribute, that.firstNameAttribute)
        .append(lastNameAttribute, that.lastNameAttribute)
        .append(emailAttribute, that.emailAttribute)
        .append(nicknameAttribute, that.nicknameAttribute)
        .append(homePhoneAttribute, that.homePhoneAttribute)
        .append(mobilePhoneAttribute, that.mobilePhoneAttribute)
        .append(officePhoneAttribute, that.officePhoneAttribute)
        .append(address1Attribute, that.address1Attribute)
        .append(address2Attribute, that.address2Attribute)
        .append(address3Attribute, that.address3Attribute)
        .append(cityAttribute, that.cityAttribute)
        .append(stateAttribute, that.stateAttribute)
        .append(zipCodeAttribute, that.zipCodeAttribute)
        .append(countryAttribute, that.countryAttribute)
        .append(customField1Attribute, that.customField1Attribute)
        .append(customField2Attribute, that.customField2Attribute)
        .append(customField3Attribute, that.customField3Attribute)
        .append(customField4Attribute, that.customField4Attribute)
        .append(customField5Attribute, that.customField5Attribute)
        .append(customField6Attribute, that.customField6Attribute)
        .append(customField7Attribute, that.customField7Attribute)
        .append(customField8Attribute, that.customField8Attribute)
        .append(customField9Attribute, that.customField9Attribute)
        .append(customField10Attribute, that.customField10Attribute)
        .append(minimumAuthenticationMethod, that.minimumAuthenticationMethod)
        .append(description, that.description)
        .append(autoUpdateUserGroups, that.autoUpdateUserGroups)
        .append(groupTypeUuid, that.groupTypeUuid)
        .append(groupNamesAttribute, that.groupNamesAttribute)
        .append(appianGroupAttributeName, that.appianGroupAttributeName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id)
        .append(idpEntityId)
        .append(idpMetadataUuid)
        .append(spCertificate)
        .append(spCertificateFileName)
        .append(spRequestSignatureHashMethod)
        .append(spEntityId)
        .append(spName)
        .append(friendlyName)
        .append(groupUuid)
        .append(autoCreateUsers)
        .append(autoReactivateUsers)
        .append(autoUpdateUsers)
        .append(useUsernameAttribute)
        .append(usernameAttribute)
        .append(firstNameAttribute)
        .append(lastNameAttribute)
        .append(emailAttribute)
        .append(nicknameAttribute)
        .append(homePhoneAttribute)
        .append(mobilePhoneAttribute)
        .append(officePhoneAttribute)
        .append(address1Attribute)
        .append(address2Attribute)
        .append(address3Attribute)
        .append(cityAttribute)
        .append(stateAttribute)
        .append(zipCodeAttribute)
        .append(countryAttribute)
        .append(customField1Attribute)
        .append(customField2Attribute)
        .append(customField3Attribute)
        .append(customField4Attribute)
        .append(customField5Attribute)
        .append(customField6Attribute)
        .append(customField7Attribute)
        .append(customField8Attribute)
        .append(customField9Attribute)
        .append(customField10Attribute)
        .append(allowLowercaseUsername)
        .append(minimumAuthenticationMethod)
        .append(priority)
        .append(description)
        .append(mobileAuthPopup)
        .append(rememberIdp)
        .append(rememberMeTokenExpirationWeb)
        .append(rememberMeTokenExpirationMobile)
        .append(autoUpdateUserGroups)
        .append(groupNamesAttribute)
        .append(appianGroupAttributeName)
        .append(groupTypeUuid)
        .toHashCode();
  }

  public SamlSettings copy() {
    return new SamlSettings(this);
  }
}
