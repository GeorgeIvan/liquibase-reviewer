package com.appiancorp.security.auth.oidc.persistence.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.common.logging.adminconsole.AdminConsoleAuditKey;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;

/**
 * OIDC IdP level settings
 */
@SuppressWarnings("java:S1448")
@Entity
@Table(name = "oidc_settings")
public class OidcSettingsCfg implements OidcSettings, HasAuditInfo, Serializable {

  public static final String TABLE_NAME = "oidc_settings";

  private Long id;
  private String clientId;
  private String clientSecret;
  private String issuerUri;
  private String usernameAttribute;
  private boolean isDynamic;
  private boolean isJwtBasedClaims;
  private String authorizationEndpoint;
  private String tokenEndpoint;
  private String userInfoEndpoint;
  private String discoveryEndpoint;
  private String jwksUri;
  private Long lastFetchedTs;
  private String authenticationGroupUuid;
  private String friendlyName;
  private boolean allowLowercaseUsername;
  private boolean autoCreateUsers;
  private boolean autoUpdateUsers;
  private boolean autoUpdateUserGroups;
  private String groupTypeUuid; // Group Type
  private String appianGroupAttributeName; // Group Type Attribute
  private String groupMappingAttribute; // Group Mapping Attribute
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
  private String scopes;
  private String endSessionEndpoint;
  private int priority;
  private String description;
  private int rememberMeTokenExpirationWeb;
  private int rememberMeTokenExpirationMobile;
  private boolean autoReactivateUsers;
  private boolean rememberIdp;

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
  @Column(name = "client_id", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @AdminConsoleAuditKey(key = "client.secret")
  @Column(name = "client_secret", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @AdminConsoleAuditKey(key = "issuer.uri")
  @Column(name = "issuer_uri", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getIssuerUri() {
    return issuerUri;
  }

  public void setIssuerUri(String issuerUri) {
    this.issuerUri = issuerUri;
  }

  @AdminConsoleAuditKey(key = "username.attribute")
  @Column(name = "username_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUsernameAttribute() {
    return usernameAttribute;
  }

  public void setUsernameAttribute(String usernameAttribute) {
    this.usernameAttribute = usernameAttribute;
  }

  @AdminConsoleAuditKey(key = "dynamic")
  @Column(name = "is_dynamic", nullable = false)
  public boolean isDynamic() {
    return isDynamic;
  }

  public void setDynamic(boolean dynamic) {
    isDynamic = dynamic;
  }

  @AdminConsoleAuditKey(key = "jwt.based.claims")
  @Column(name = "has_jwt_based_claims", nullable = true)
  public boolean isJwtBasedClaims() {
    return isJwtBasedClaims;
  }

  public void setJwtBasedClaims(boolean jwtBasedClaims) {
    this.isJwtBasedClaims = jwtBasedClaims;
  }

  @AdminConsoleAuditKey(key = "authorization.endpoint")
  @Column(name = "authorization_endpoint", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAuthorizationEndpoint() {
    return authorizationEndpoint;
  }

  public void setAuthorizationEndpoint(String authorizationEndpoint) {
    this.authorizationEndpoint = authorizationEndpoint;
  }

  @AdminConsoleAuditKey(key = "discovery.endpoint")
  @Column(name = "discovery_endpoint", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getDiscoveryEndpoint() {
    return discoveryEndpoint;
  }

  public void setDiscoveryEndpoint(String discoveryEndpoint) {
    this.discoveryEndpoint = discoveryEndpoint;
  }

  @AdminConsoleAuditKey(key = "token.endpoint")
  @Column(name = "token_endpoint", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getTokenEndpoint() {
    return tokenEndpoint;
  }

  public void setTokenEndpoint(String tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
  }

  @AdminConsoleAuditKey(key = "userinfo.endpoint")
  @Column(name = "userinfo_endpoint", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getUserInfoEndpoint() {
    return userInfoEndpoint;
  }

  public void setUserInfoEndpoint(String userDataEndpoint) {
    this.userInfoEndpoint = userDataEndpoint;
  }

  @AdminConsoleAuditKey(key = "jwks.uri")
  @Column(name = "jwks_uri", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getJwksUri() {
    return jwksUri;
  }

  public void setJwksUri(String jwksUri) {
    this.jwksUri = jwksUri;
  }

  @AdminConsoleAuditKey(key = "last.fetched.ts")
  @Column(name = "last_fetched_ts", nullable = true)
  public Long getLastFetchedTs() {
    return lastFetchedTs;
  }

  public void setLastFetchedTs(Long lastFetchedTs) {
    this.lastFetchedTs = lastFetchedTs;
  }

  @AdminConsoleAuditKey(key = "authentication.group.uuid")
  @Column(name = "authentication_group_uuid", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAuthenticationGroupUuid() {
    return authenticationGroupUuid;
  }

  public void setAuthenticationGroupUuid(String authenticationGroupUuid) {
    this.authenticationGroupUuid = authenticationGroupUuid;
  }

  @AdminConsoleAuditKey(key = "friendly.name")
  @Column(name = "friendly_name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
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
  @Column(name = "grouptype_uuid", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getGroupTypeUuid() {
    return groupTypeUuid;
  }

  public void setGroupTypeUuid(String groupTypeUuid) {
    this.groupTypeUuid = groupTypeUuid;
  }

  @AdminConsoleAuditKey(key = "appian.group.attribute.name")
  @Column(name = "appian_group_attribute_name", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAppianGroupAttributeName() {
    return appianGroupAttributeName;
  }

  public void setAppianGroupAttributeName(String appianGroupAttributeName) {
    this.appianGroupAttributeName = appianGroupAttributeName;
  }

  @AdminConsoleAuditKey(key = "attribute.group.mapping")
  @Column(name = "group_mapping_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getGroupMappingAttribute() {
    return groupMappingAttribute;
  }

  public void setGroupMappingAttribute(String groupMappingAttribute) {
    this.groupMappingAttribute = groupMappingAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.first.name")
  @Column(name = "first_name_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getFirstNameAttribute() {
    return firstNameAttribute;
  }

  public void setFirstNameAttribute(String firstNameAttribute) {
    this.firstNameAttribute = firstNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.last.name")
  @Column(name = "last_name_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getLastNameAttribute() {
    return lastNameAttribute;
  }

  public void setLastNameAttribute(String lastNameAttribute) {
    this.lastNameAttribute = lastNameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.nickname")
  @Column(name = "nickname_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getNicknameAttribute() {
    return nicknameAttribute;
  }

  public void setNicknameAttribute(String nicknameAttribute) {
    this.nicknameAttribute = nicknameAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.email")
  @Column(name = "email_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getEmailAttribute() {
    return emailAttribute;
  }

  public void setEmailAttribute(String emailAttribute) {
    this.emailAttribute = emailAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.home.phone")
  @Column(name = "home_phone_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getHomePhoneAttribute() {
    return homePhoneAttribute;
  }

  public void setHomePhoneAttribute(String homePhoneAttribute) {
    this.homePhoneAttribute = homePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.mobile.phone")
  @Column(name = "mobile_phone_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getMobilePhoneAttribute() {
    return mobilePhoneAttribute;
  }

  public void setMobilePhoneAttribute(String mobilePhoneAttribute) {
    this.mobilePhoneAttribute = mobilePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.office.phone")
  @Column(name = "office_phone_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getOfficePhoneAttribute() {
    return officePhoneAttribute;
  }

  public void setOfficePhoneAttribute(String officePhoneAttribute) {
    this.officePhoneAttribute = officePhoneAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address1")
  @Column(name = "address1_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress1Attribute() {
    return address1Attribute;
  }

  public void setAddress1Attribute(String address1Attribute) {
    this.address1Attribute = address1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address2")
  @Column(name = "address2_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress2Attribute() {
    return address2Attribute;
  }

  public void setAddress2Attribute(String address2Attribute) {
    this.address2Attribute = address2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.address3")
  @Column(name = "address3_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getAddress3Attribute() {
    return address3Attribute;
  }

  public void setAddress3Attribute(String address3Attribute) {
    this.address3Attribute = address3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.city")
  @Column(name = "city_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCityAttribute() {
    return cityAttribute;
  }

  public void setCityAttribute(String cityAttribute) {
    this.cityAttribute = cityAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.state")
  @Column(name = "state_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getStateAttribute() {
    return stateAttribute;
  }

  public void setStateAttribute(String stateAttribute) {
    this.stateAttribute = stateAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.zip.code")
  @Column(name = "zip_code_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getZipCodeAttribute() {
    return zipCodeAttribute;
  }

  public void setZipCodeAttribute(String zipCodeAttribute) {
    this.zipCodeAttribute = zipCodeAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.country")
  @Column(name = "country_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCountryAttribute() {
    return countryAttribute;
  }

  public void setCountryAttribute(String countryAttribute) {
    this.countryAttribute = countryAttribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield1")
  @Column(name = "customfield1_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField1Attribute() {
    return customField1Attribute;
  }

  public void setCustomField1Attribute(String customField1Attribute) {
    this.customField1Attribute = customField1Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield2")
  @Column(name = "customfield2_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField2Attribute() {
    return customField2Attribute;
  }

  public void setCustomField2Attribute(String customField2Attribute) {
    this.customField2Attribute = customField2Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield3")
  @Column(name = "customfield3_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField3Attribute() {
    return customField3Attribute;
  }

  public void setCustomField3Attribute(String customField3Attribute) {
    this.customField3Attribute = customField3Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield4")
  @Column(name = "customfield4_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField4Attribute() {
    return customField4Attribute;
  }

  public void setCustomField4Attribute(String customField4Attribute) {
    this.customField4Attribute = customField4Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield5")
  @Column(name = "customfield5_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField5Attribute() {
    return customField5Attribute;
  }

  public void setCustomField5Attribute(String customField5Attribute) {
    this.customField5Attribute = customField5Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield6")
  @Column(name = "customfield6_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField6Attribute() {
    return customField6Attribute;
  }

  public void setCustomField6Attribute(String customField6Attribute) {
    this.customField6Attribute = customField6Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield7")
  @Column(name = "customfield7_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField7Attribute() {
    return customField7Attribute;
  }

  public void setCustomField7Attribute(String customField7Attribute) {
    this.customField7Attribute = customField7Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield8")
  @Column(name = "customfield8_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField8Attribute() {
    return customField8Attribute;
  }

  public void setCustomField8Attribute(String customField8Attribute) {
    this.customField8Attribute = customField8Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield9")
  @Column(name = "customfield9_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField9Attribute() {
    return customField9Attribute;
  }

  public void setCustomField9Attribute(String customField9Attribute) {
    this.customField9Attribute = customField9Attribute;
  }

  @AdminConsoleAuditKey(key = "attribute.customfield10")
  @Column(name = "customfield10_attr", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getCustomField10Attribute() {
    return customField10Attribute;
  }

  public void setCustomField10Attribute(String customField10Attribute) {
    this.customField10Attribute = customField10Attribute;
  }

  @AdminConsoleAuditKey(key = "scopes")
  @Column(name = "scopes", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  @AdminConsoleAuditKey(key = "end.session_endpoint")
  @Column(name = "end_session_endpoint", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getEndSessionEndpoint() {
    return endSessionEndpoint;
  }

  public void setEndSessionEndpoint(String endSessionEndpoint) {
    this.endSessionEndpoint = endSessionEndpoint;
  }

  @AdminConsoleAuditKey(key = "priority")
  @Column(name = "priority", nullable = false)
  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  @AdminConsoleAuditKey(key = "description")
  @Column(name = "description", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

  @AdminConsoleAuditKey(key = "rememberMeTokenExpirationWeb")
  @Column(name = "remember_me_token_exp_web", nullable = false)
  public int getRememberMeTokenExpirationWeb() {
    return rememberMeTokenExpirationWeb;
  }

  public void setRememberMeTokenExpirationWeb(int rememberMeTokenExpirationWeb){
    this.rememberMeTokenExpirationWeb = rememberMeTokenExpirationWeb;
  }

  @AdminConsoleAuditKey(key = "rememberMeTokenExpirationMobile")
  @Column(name = "remember_me_token_exp_mobile", nullable = false)
  public int getRememberMeTokenExpirationMobile() {
    return rememberMeTokenExpirationMobile;
  }

  public void setRememberMeTokenExpirationMobile(int rememberMeTokenExpirationMobile){
    this.rememberMeTokenExpirationMobile = rememberMeTokenExpirationMobile;
  }

  @AdminConsoleAuditKey(key = "autoreactivate.users")
  @Column(name = "auto_reactivate_users", nullable = false)
  public boolean isAutoReactivateUsers() {
    return autoReactivateUsers;
  }

  public void setAutoReactivateUsers(boolean autoReactivateUsers) {
    this.autoReactivateUsers = autoReactivateUsers;
  }

  @AdminConsoleAuditKey(key = "rememberIdp")
  @Column(name = "remember_idp", nullable = false)
  public boolean isRememberIdp() {
    return rememberIdp;
  }

  public void setRememberIdp(boolean rememberIdp) {
    this.rememberIdp = rememberIdp;
  }

  private AuditInfo auditInfo = new AuditInfo();

  @Override
  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public OidcSettingsCfg copy() {
    return OidcSettingsCfgBuilder.from(this).build();
  }
}
