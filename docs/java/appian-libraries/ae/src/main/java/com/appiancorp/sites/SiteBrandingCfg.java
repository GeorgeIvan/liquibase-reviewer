package com.appiancorp.sites;

import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

import com.appian.core.base.MultilineToStringHelper;
import com.appian.core.base.ToStringFunction;
import com.appian.core.persist.Constants;
import com.appiancorp.branding.BrandingCfgSource;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.ExpressionState;
import com.appiancorp.type.Id;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

@Hidden
@Entity
@Table(name = "site_branding_cfg")
@XmlTransient
@IgnoreJpa
public class SiteBrandingCfg implements Id<Long>, ExpressionState {

  private static final long serialVersionUID = 1L;
  public static final String LOCAL_PART = "SiteBrandingCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_BRANDING_VALUE = "brandingValue";
  public static final String PROP_BRANDING_SOURCE = "brandingSource";
  public static final String PROP_BRANDING_SOURCE_BYTE = "brandingSourceByte";
  public static final String PROP_BRANDING_KEY = "brandingKey";

  private transient ExpressionTransformationState expressionTransformationState = STORED;

  private Long id;
  private String brandingKey;
  private BrandingCfgSource brandingSource = BrandingCfgSource.DEFAULT;
  private String brandingValue;

  public SiteBrandingCfg() {
  }

  public SiteBrandingCfg(String brandingKey, BrandingCfgSource brandingSource, String brandingValue) {
    this.brandingKey = brandingKey;
    this.brandingSource = brandingSource;
    this.brandingValue = brandingValue;
  }

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

  @Column(name = "branding_key", length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlTransient
  public String getBrandingKey() {
    return brandingKey;
  }

  public void setBrandingKey(String brandingKey) {
    this.brandingKey = brandingKey;
  }

  @Column(name = "branding_value", length = Constants.COL_MAXLEN_EXPRESSION)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @XmlTransient
  @Lob
  public String getBrandingValue() {
    return brandingValue;
  }

  public void setBrandingValue(String brandingValue) {
    this.brandingValue = brandingValue;
  }

  @Transient
  @XmlTransient
  public BrandingCfgSource getBrandingSource() {
    return brandingSource;
  }

  public void setBrandingSource(BrandingCfgSource source) {
    this.brandingSource = source;
  }

  @Column(name = "branding_source", nullable = false)
  @XmlTransient
  public byte getBrandingSourceByte() {
    return getBrandingSource().getCode();
  }

  public void setBrandingSourceByte(byte brandingSourceByte) {
    setBrandingSource(BrandingCfgSource.valueOf(brandingSourceByte));
  }

  @Override
  public String toString() {
    return "SitePage [id=" + id + ", brandingKey=" + brandingKey + ", brandingSource=" + brandingSource + ", brandingValue=" +
        brandingValue + "]";
  }

  public static ToStringFunction<SiteBrandingCfg> multilineToString(final int indent) {
    return new ToStringFunction<SiteBrandingCfg>() {
      @Override
      protected String doToString(SiteBrandingCfg t) {
        return MultilineToStringHelper.of(t,indent)
            .add(PROP_ID, t.id)
            .add(PROP_BRANDING_KEY, t.brandingKey)
            .add(PROP_BRANDING_SOURCE, t.brandingSource)
            .add(PROP_BRANDING_VALUE, t.brandingValue)
            .toString();
      }};
  }

  private static final Equivalence<SiteBrandingCfg> equalDataCheckInstance = new SitePageEquivalence();

  public static Equivalence<SiteBrandingCfg> equalityForNonGeneratedFields() {
    return equalDataCheckInstance;
  }

  public boolean equivalentTo(final SiteBrandingCfg site) {
    return equalDataCheckInstance.equivalent(this, site);
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

  // Used by Java Serialization
  private Object readResolve() {
    this.expressionTransformationState = STORED;
    return this;
  }

  private static class SitePageEquivalence extends Equivalence<SiteBrandingCfg> {

    @Override
    protected boolean doEquivalent(SiteBrandingCfg lhs, SiteBrandingCfg rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (null == rhs || null == lhs) {
        return false;
      }
      return Objects.equal(lhs.brandingKey, rhs.brandingKey) &&
          Objects.equal(lhs.brandingSource, rhs.brandingSource) &&
          Objects.equal(lhs.brandingValue, rhs.brandingValue);
    }

    @Override
    protected int doHash(SiteBrandingCfg t) {
      return Objects.hashCode(t.brandingKey, t.brandingSource, t.brandingValue);
    }
  }
}
