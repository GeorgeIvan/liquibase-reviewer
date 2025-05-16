package com.appiancorp.security.external;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@Hidden
@Entity
@Table(name = SecuredAttribute.TBL_NAME)
@XmlRootElement(name = "externalSystemAttribute", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = SecuredAttribute.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "id",
    "key",
    "name",
    "showValueOnUi",
    "externalSystemId"
})
@XmlAccessorType(XmlAccessType.FIELD)
@IgnoreJpa
public class SecuredAttribute {
  public static final String LOCAL_PART = "ExternalSystemAttribute";

  public static final String TBL_NAME = "external_sys_attr";
  public static final String PROP_ID = "id";
  public static final String PROP_KEY = "key";
  public static final String PROP_NAME = "name";

  private Long id;
  private String key;
  private String name;
  private boolean showValueOnUi;
  private Long externalSystemId;

  public SecuredAttribute() {
  }

  public SecuredAttribute(String key, String name, boolean showValueOnUi) {
    this.key = key;
    this.name = name;
    this.showValueOnUi = showValueOnUi;
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

  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "unique_key", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  /**
   * returns true if in User Interface the value has to be secret
   *
   * @return
   */
  @Column(name = "is_show_value_on_ui", nullable = false)
  public boolean isShowValueOnUi() {
    return showValueOnUi;
  }

  public void setShowValueOnUi(boolean showValueOnUi) {
    this.showValueOnUi = showValueOnUi;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .add("key", key)
      .add("name", name)
      .add("showValueOnUi", showValueOnUi)
      .toString();
  }

  @Column(name = "external_sys_id", insertable = false, updatable = false)
  public Long getExternalSystemId() {
    return externalSystemId;
  }

  public void setExternalSystemId(Long id) {
    this.externalSystemId = id;
  }

  public static Equivalence<SecuredAttribute> ignoreIdEquivalence() {
    return new Equivalence<SecuredAttribute>() {
      @Override
      protected boolean doEquivalent(SecuredAttribute a, SecuredAttribute b) {
        return Objects.equal(a.getName(), b.getName()) &&
            Objects.equal(a.getKey(), b.getKey());
      }

      @Override
      protected int doHash(SecuredAttribute securedAttribute) {
        return Objects.hashCode(securedAttribute.getName(), securedAttribute.getKey());
      }
    };
  }
}
