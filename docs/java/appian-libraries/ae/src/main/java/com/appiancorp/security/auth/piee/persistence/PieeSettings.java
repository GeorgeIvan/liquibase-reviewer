package com.appiancorp.security.auth.piee.persistence;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suite.cfg.adminconsole.AdminConsoleAuditKey;
import com.appiancorp.type.Id;

/**
 * Piee IdP level settings
 */
@Entity
@Table(name = "piee_settings")
public class PieeSettings implements Id<Long>, HasAuditInfo {

  public static final String TABLE_NAME = "piee_settings";

  public static final String FRIENDLY_NAME_KEY = "friendly_name";

  private Long id;
  private String clientId;
  private String clientSecret;
  private String authorizationEndpoint;
  private String tokenEndpoint;
  private String userDataEndpoint;
  private String usernameAttribute;
  private String authenticationGroupUuid;
  private String friendlyName;
  private boolean allowLowercaseUsername;
  private boolean autoCreateUsers;
  private boolean autoUpdateUsers;
  private boolean autoUpdateUserGroups;
  private String groupTypeUuid; // Group Type
  private String appianGroupAttributeName; // Group Type Attribute
  private String firstNameAttribute;
  private String lastNameAttribute;
  private String nicknameAttribute;
  private String emailAttribute;
  private String homePhoneAttribute;
  private String mobilePhoneAttribute;
  private String officePhoneAttribute;
  private String address1Attribute;
  private String address2Attribute;
  private String address3Attribute;
  private String cityAttribute;
  private String stateAttribute;
  private String zipCodeAttribute;
  private String countryAttribute;
  private String customField1Attribute;
  private String customField2Attribute;
  private String customField3Attribute;
  private String customField4Attribute;
  private String customField5Attribute;
  private String customField6Attribute;
  private String customField7Attribute;
  private String customField8Attribute;
  private String customField9Attribute;
  private String customField10Attribute;
  private AuditInfo auditInfo = new AuditInfo();

  @Override
  @javax.persistence.Id
  @Column(name = "id", nullable = false)
  @GeneratedValue
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @AdminConsoleAuditKey(key = "client.id")
  @Column(name = "client_id", nullable = false)
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @AdminConsoleAuditKey(key = "client.secret")
  @Column(name = "client_secret", nullable = false)
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @AdminConsoleAuditKey(key = "authorization.endpoint")
  @Column(name = "authorization_endpoint", nullable = false)
  public String getAuthorizationEndpoint() {
    return authorizationEndpoint;
  }

  public void setAuthorizationEndpoint(String authorizationEndpoint) {
    this.authorizationEndpoint = authorizationEndpoint;
  }

  @AdminConsoleAuditKey(key = "token.endpoint")
  @Column(name = "token_endpoint", nullable = false)
  public String getTokenEndpoint() {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(String tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
  }

  @AdminConsoleAuditKey(key = "user.data.endpoint")
  @Column(name = "user_data_endpoint", nullable = false)
  public String getUserDataEndpoint() {
    return userDataEndpoint;
  }

  public void setUserDataEndpoint(String userDataEndpoint) {
    this.userDataEndpoint = userDataEndpoint;
  }

  @AdminConsoleAuditKey(key = "attribute.username")
  @Column(name = "username_attr", nullable = false)
  public String getUsernameAttribute() {
    return usernameAttribute;
  }

  public void setUsernameAttribute(String usernameAttribute) {
    this.usernameAttribute = usernameAttribute;
  }

  @AdminConsoleAuditKey(key = "authentication.group.uuid")
  @Column(name = "authentication_group_uuid", nullable = true)
  public String getAuthenticationGroupUuid() {
    return authenticationGroupUuid;
  }

  public void setAuthenticationGroupUuid(String authenticationGroupUuid) {
    this.authenticationGroupUuid = authenticationGroupUuid;
  }

  @AdminConsoleAuditKey(key = "friendly.name")
  @Column(name = "friendly_name", nullable = false)
  public String getFriendlyName() {
    return friendlyName;
  }

  public void setFriendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
  }


  @AdminConsoleAuditKey(key = "allow.lowercase.username")
  @Column(name = "allow_lowercase_username", nullable = true)
  public boolean isAllowLowercaseUsername() {
    return allowLowercaseUsername;
  }

  public void setAllowLowercaseUsername(boolean allowLowercaseUsername) {
    this.allowLowercaseUsername = allowLowercaseUsername;
  }

  @AdminConsoleAuditKey(key = "autocreate.users")
  @Column(name = "auto_create_users", nullable = true)
  public boolean isAutoCreateUsers() {
    return autoCreateUsers;
  }

  public void setAutoCreateUsers(boolean autoCreateUsers) {
    this.autoCreateUsers = autoCreateUsers;
  }

  @AdminConsoleAuditKey(key = "autoupdate.users")
  @Column(name = "auto_update_users", nullable = true)
  public boolean isAutoUpdateUsers() {
    return autoUpdateUsers;
  }

  public void setAutoUpdateUsers(boolean autoUpdateUsers) {
    this.autoUpdateUsers = autoUpdateUsers;
  }

  @AdminConsoleAuditKey(key = "autoupdate.user.groups")
  @Column(name = "auto_update_user_groups", nullable = true)
  public boolean isAutoUpdateUserGroups() {
    return autoUpdateUserGroups;
  }

  public void setAutoUpdateUserGroups(boolean autoUpdateUserGroups) {
    this.autoUpdateUserGroups = autoUpdateUserGroups;
  }

  @AdminConsoleAuditKey(key = "grouptype.uuid")
  @Column(name = "grouptype_uuid")
  public String getGroupTypeUuid() {
    return groupTypeUuid;
  }

  public void setGroupTypeUuid(String groupTypeUuid) {
    this.groupTypeUuid = groupTypeUuid;
  }

  @AdminConsoleAuditKey(key = "appian.group.attribute.name")
  @Column(name = "appian_group_attribute_name", nullable = true)
  public String getAppianGroupAttributeName() {
    return appianGroupAttributeName;
  }

  public void setAppianGroupAttributeName(String appianGroupAttributeName) {
    this.appianGroupAttributeName = appianGroupAttributeName;
  }

  @AdminConsoleAuditKey(key = "attribute.firstname")
  @Column(name = "first_name_attr", nullable = true)
  public String getFirstNameAttribute() {
    return firstNameAttribute;
  }

  public void setFirstNameAttribute(String firstNameAttribute) {
    this.firstNameAttribute = firstNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.lastname")
  @Column(name = "last_name_attr", nullable = true)
  public String getLastNameAttribute() {
    return lastNameAttribute;
  }

  public void setLastNameAttribute(String lastNameAttribute) {
    this.lastNameAttribute = lastNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.nickname")
  @Column(name = "nickname_attr", nullable = true)
  public String getNicknameAttribute() {
    return nicknameAttribute;
  }

  public void setNicknameAttribute(String nicknameAttribute) {
    this.nicknameAttribute = nicknameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.email")
  @Column(name = "email_attr", nullable = true)
  public String getEmailAttribute() {
    return emailAttribute;
  }

  public void setEmailAttribute(String emailAttribute) {
    this.emailAttribute = emailAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.home.phone")
  @Column(name = "home_phone_attr", nullable = true)
  public String getHomePhoneAttribute() {
    return homePhoneAttribute;
  }

  public void setHomePhoneAttribute(String homePhoneAttribute) {
    this.homePhoneAttribute = homePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.mobile.phone")
  @Column(name = "mobile_phone_attr", nullable = true)
  public String getMobilePhoneAttribute() {
    return mobilePhoneAttribute;
  }

  public void setMobilePhoneAttribute(String mobilePhoneAttribute) {
    this.mobilePhoneAttribute = mobilePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.office.phone")
  @Column(name = "office_phone_attr", nullable = true)
  public String getOfficePhoneAttribute() {
    return officePhoneAttribute;
  }

  public void setOfficePhoneAttribute(String officePhoneAttribute) {
    this.officePhoneAttribute = officePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address1")
  @Column(name = "address1_attr", nullable = true)
  public String getAddress1Attribute() {
    return address1Attribute;
  }

  public void setAddress1Attribute(String address1Attribute) {
    this.address1Attribute = address1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address2")
  @Column(name = "address2_attr", nullable = true)
  public String getAddress2Attribute() {
    return address2Attribute;
  }

  public void setAddress2Attribute(String address2Attribute) {
    this.address2Attribute = address2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address3")
  @Column(name = "address3_attr", nullable = true)
  public String getAddress3Attribute() {
    return address3Attribute;
  }

  public void setAddress3Attribute(String address3Attribute) {
    this.address3Attribute = address3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.city")
  @Column(name = "city_attr", nullable = true)
  public String getCityAttribute() {
    return cityAttribute;
  }

  public void setCityAttribute(String cityAttribute) {
    this.cityAttribute = cityAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.state")
  @Column(name = "state_attr", nullable = true)
  public String getStateAttribute() {
    return stateAttribute;
  }

  public void setStateAttribute(String stateAttribute) {
    this.stateAttribute = stateAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.zipcode")
  @Column(name = "zip_code_attr", nullable = true)
  public String getZipCodeAttribute() {
    return zipCodeAttribute;
  }

  public void setZipCodeAttribute(String zipCodeAttribute) {
    this.zipCodeAttribute = zipCodeAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.country")
  @Column(name = "country_attr", nullable = true)
  public String getCountryAttribute() {
    return countryAttribute;
  }

  public void setCountryAttribute(String countryAttribute) {
    this.countryAttribute = countryAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield1")
  @Column(name = "customfield1_attr", nullable = true)
  public String getCustomField1Attribute() {
    return customField1Attribute;
  }

  public void setCustomField1Attribute(String customField1Attribute) {
    this.customField1Attribute = customField1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield2")
  @Column(name = "customfield2_attr", nullable = true)
  public String getCustomField2Attribute() {
    return customField2Attribute;
  }

  public void setCustomField2Attribute(String customField2Attribute) {
    this.customField2Attribute = customField2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield3")
  @Column(name = "customfield3_attr", nullable = true)
  public String getCustomField3Attribute() {
    return customField3Attribute;
  }

  public void setCustomField3Attribute(String customField3Attribute) {
    this.customField3Attribute = customField3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield4")
  @Column(name = "customfield4_attr", nullable = true)
  public String getCustomField4Attribute() {
    return customField4Attribute;
  }

  public void setCustomField4Attribute(String customField4Attribute) {
    this.customField4Attribute = customField4Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield5")
  @Column(name = "customfield5_attr", nullable = true)
  public String getCustomField5Attribute() {
    return customField5Attribute;
  }

  public void setCustomField5Attribute(String customField5Attribute) {
    this.customField5Attribute = customField5Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield6")
  @Column(name = "customfield6_attr", nullable = true)
  public String getCustomField6Attribute() {
    return customField6Attribute;
  }

  public void setCustomField6Attribute(String customField6Attribute) {
    this.customField6Attribute = customField6Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield7")
  @Column(name = "customfield7_attr", nullable = true)
  public String getCustomField7Attribute() {
    return customField7Attribute;
  }

  public void setCustomField7Attribute(String customField7Attribute) {
    this.customField7Attribute = customField7Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield8")
  @Column(name = "customfield8_attr", nullable = true)
  public String getCustomField8Attribute() {
    return customField8Attribute;
  }

  public void setCustomField8Attribute(String customField8Attribute) {
    this.customField8Attribute = customField8Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield9")
  @Column(name = "customfield9_attr", nullable = true)
  public String getCustomField9Attribute() {
    return customField9Attribute;
  }

  public void setCustomField9Attribute(String customField9Attribute) {
    this.customField9Attribute = customField9Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield10")
  @Column(name = "customfield10_attr", nullable = true)
  public String getCustomField10Attribute() {
    return customField10Attribute;
  }

  public void setCustomField10Attribute(String customField10Attribute) {
    this.customField10Attribute = customField10Attribute;
  }

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @SuppressWarnings({"checkstyle:ExecutableStatementCount"})
  public PieeSettings copy() {
      return PieeSettingsBuilder.builder()
          .id(this.id)
          .clientId(this.clientId)
          .clientSecret(this.clientSecret)
          .authorizationEndpoint(this.authorizationEndpoint)
          .tokenEndpoint(this.tokenEndpoint)
          .userDataEndpoint(this.userDataEndpoint)
          .usernameAttribute(this.usernameAttribute)
          .authenticationGroupUuid(this.authenticationGroupUuid)
          .friendlyName(this.friendlyName)
          .allowLowercaseUsername(this.allowLowercaseUsername)
          .autoCreateUsers(this.autoCreateUsers)
          .autoUpdateUsers(this.autoUpdateUsers)
          .autoUpdateUserGroups(this.autoUpdateUserGroups)
          .groupTypeUuid(this.groupTypeUuid)
          .appianGroupAttributeName(this.appianGroupAttributeName)
          .firstNameAttribute(this.firstNameAttribute)
          .lastNameAttribute(this.lastNameAttribute)
          .nicknameAttribute(this.nicknameAttribute)
          .emailAttribute(this.emailAttribute)
          .homePhoneAttribute(this.homePhoneAttribute)
          .mobilePhoneAttribute(this.mobilePhoneAttribute)
          .officePhoneAttribute(this.officePhoneAttribute)
          .address1Attribute(this.address1Attribute)
          .address2Attribute(this.address2Attribute)
          .address3Attribute(this.address3Attribute)
          .cityAttribute(this.cityAttribute)
          .stateAttribute(this.stateAttribute)
          .zipCodeAttribute(this.zipCodeAttribute)
          .countryAttribute(this.countryAttribute)
          .customField1Attribute(this.customField1Attribute)
          .customField2Attribute(this.customField2Attribute)
          .customField3Attribute(this.customField3Attribute)
          .customField4Attribute(this.customField4Attribute)
          .customField5Attribute(this.customField5Attribute)
          .customField6Attribute(this.customField6Attribute)
          .customField7Attribute(this.customField7Attribute)
          .customField8Attribute(this.customField8Attribute)
          .customField9Attribute(this.customField9Attribute)
          .customField10Attribute(this.customField10Attribute)
          .build();
    }
}
