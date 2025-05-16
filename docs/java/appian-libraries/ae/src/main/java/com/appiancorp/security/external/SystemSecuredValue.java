package com.appiancorp.security.external;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = SystemSecuredValue.TBL_NAME)
public class SystemSecuredValue {

  public static final String TBL_NAME = "external_sys_attr_sys_val";
  public static final String PROP_ATTRIBUTE = "attribute";
  public static final String PROP_VALUE = "value";
  public static final String PROP_EXPIRATION = "expires_in";

  private Long id;
  private SecuredAttribute attribute;
  private String value;
  private String expiresIn;

  public SystemSecuredValue() {
  }

  public SystemSecuredValue(SecuredAttribute attribute, String value) {
    this.attribute = attribute;
    this.value = value;
  }

  public SystemSecuredValue(SecuredAttribute attribute, String value, String expiresIn) {
    this.attribute = attribute;
    this.value = value;
    this.expiresIn = expiresIn;
  }

  public SystemSecuredValue(Long attributeId, String value) {
    SecuredAttribute attr = new SecuredAttribute();
    attr.setId(attributeId);

    this.attribute = attr;
    this.value = value;
  }

  public SystemSecuredValue(Long attributeId, String value, String expiresIn) {
    SecuredAttribute attr = new SecuredAttribute();
    attr.setId(attributeId);

    this.attribute = attr;
    this.value = value;
    this.expiresIn = expiresIn;
  }

  @Column(name = "id")
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "val", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "expires_in", length = Constants.COL_MAXLEN_INDEXABLE)
  public String getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(String expiresIn) {
    this.expiresIn = expiresIn;
  }

  @OneToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "external_sys_attr_id")
  public SecuredAttribute getAttribute() {
    return attribute;
  }

  public void setAttribute(SecuredAttribute attribute) {
    this.attribute = attribute;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("attribute", attribute).toString();
  }
}
