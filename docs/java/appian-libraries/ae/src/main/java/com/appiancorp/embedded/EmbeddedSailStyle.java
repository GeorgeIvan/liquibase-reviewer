package com.appiancorp.embedded;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.appiancorp.css.ConfigurableStyle;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.ExtendedDataTypeProvider;
import com.appiancorp.type.Id;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

/**
 * Represents a specific configured style, associated with a configured theme {@EmbeddedSailTheme} where
 * each object of this type represents a specific style attribute/value pair.
 */
@Hidden
@Entity
@Table(name = "embedded_sail_style")
@XmlRootElement(name = "embeddedSailStyle", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = EmbeddedSailStyle.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "name",
    "value",
    "valueExpr"
})
@IgnoreJpa
public class EmbeddedSailStyle implements Id<Long> {

  private static final long serialVersionUID = 1L;

  public static final String LOCAL_PART = "EmbeddedSailStyle";

  private Long id;
  private String name;
  private String value;
  private String valueExpr;

  public EmbeddedSailStyle() {
  }

  public EmbeddedSailStyle(String styleName, String styleValue) {
    this.name = styleName;
    this.value = styleValue;
    this.valueExpr = null;
  }

  public EmbeddedSailStyle(ConfigurableStyle style, String styleValue) {
    this.name = style.getKey();
    this.value = styleValue;
    this.valueExpr = null;
  }

  public EmbeddedSailStyle(String styleName, String styleValue, String styleValueExpr) {
    this.name = styleName;
    this.value = styleValue;
    this.valueExpr = styleValueExpr;
  }

  public EmbeddedSailStyle(ConfigurableStyle style, String styleValue, String styleValueExpr) {
    this.name = style.getKey();
    this.value = styleValue;
    this.valueExpr = styleValueExpr;
  }

  public EmbeddedSailStyle(final com.appiancorp.type.cdt.EmbeddedSailStyle styleCdt) {
    this.name = styleCdt.getName();
    this.value = styleCdt.getValue();
    this.valueExpr = styleCdt.getValueExpr();
  }

  public com.appiancorp.type.cdt.EmbeddedSailStyle toCdt() {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());
    com.appiancorp.type.cdt.EmbeddedSailStyle style = new com.appiancorp.type.cdt.EmbeddedSailStyle(dtp);
    style.setName(name);
    style.setValue(value);
    style.setValueExpr(valueExpr);
    return style;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
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

  @Column(name = "value", nullable = true, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Lob
  @Column(name = "value_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @ComplexForeignKey(nullable=false, breadcrumb= BreadcrumbText.valueExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getValueExpr() {
    return valueExpr;
  }

  public void setValueExpr(String valueExpr) {
    this.valueExpr = valueExpr;
  }

  @Override
  public boolean equals(Object arg0) {
    if(!(arg0 instanceof EmbeddedSailStyle)) {
      return false;
    } else if(this == arg0) {
      return true;
    }

    EmbeddedSailStyle otherStyle = (EmbeddedSailStyle)arg0;
    return Objects.equal(this.name, otherStyle.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  @Override
  public String toString() {
    return new StringBuilder("EmbeddedSailStyle[")
      .append("id: ").append(id)
      .append(", name: ").append(name)
      .append(", value: ").append(value)
      .append(", value_expr: ").append(valueExpr)
      .append("]")
      .toString();
  }

  static final Equivalence<EmbeddedSailStyle> equalDataCheckInstance = new EmbeddedSailStyleEquivalence();

  public boolean equivalentTo(final EmbeddedSailStyle site) {
    return equalDataCheckInstance.equivalent(this, site);
  }

  private static class EmbeddedSailStyleEquivalence extends Equivalence<EmbeddedSailStyle> {

    @Override
    protected boolean doEquivalent(EmbeddedSailStyle lhs, EmbeddedSailStyle rhs) {
      return Objects.equal(lhs.name, rhs.name) && Objects.equal(lhs.value, rhs.value)
          && Objects.equal(lhs.valueExpr, rhs.valueExpr);
    }

    @Override
    protected int doHash(EmbeddedSailStyle t) {
      return Objects.hashCode(t.name, t.value, t.valueExpr);
    }
  }
}
