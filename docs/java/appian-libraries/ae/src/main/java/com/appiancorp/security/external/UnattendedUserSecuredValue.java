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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.appian.core.persist.Constants;
import com.appiancorp.common.xml.TimestampAdapter;
import com.appiancorp.security.user.User;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = UnattendedUserSecuredValue.TBL_NAME)
public class UnattendedUserSecuredValue {
  public static final String TBL_NAME = "external_sys_attr_una_val";
  public static final String PROP_USER = "user";
  public static final String PROP_ATTRIBUTE = "attribute";
  public static final String PROP_VALUE = "value";
  public static final String PROP_EXPIRES_AT = "expiresAtLong";

  private Long id;
  private User user;
  private SecuredAttribute attribute;
  private String value;
  private Long expiresAtLong;

  @Column(name = "id")
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "usr_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @OneToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "external_sys_attr_id")
  public SecuredAttribute getAttribute() {
    return attribute;
  }

  public void setAttribute(SecuredAttribute attribute) {
    this.attribute = attribute;
  }

  @Column(name = "val", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "exp", nullable = true)
  @XmlJavaTypeAdapter(TimestampAdapter.class)
  public Long getExpiresAtLong() {
    return expiresAtLong;
  }

  public void setExpiresAtLong(Long expiresAt) {
    this.expiresAtLong = expiresAt;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("user", user)
        .add("attribute", attribute)
        .toString();
  }
}
