package com.appiancorp.portal.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.branding.BrandingCfgSource;
import com.appiancorp.type.Id;

@Entity
@Table(name = "portal_branding_cfg")
public class PortalBrandingCfg implements Id<Long> {
  public static final String PROP_BRANDING_KEY = "brandingKeyText";
  public static final String PROP_BRANDING_VALUE = "brandingValue";
  public static final String PROP_BRANDING_SOURCE_BYTE = "brandingSourceByte";
  private Long id;
  private PortalBrandingCfgKey brandingKey;
  private BrandingCfgSource brandingSource = BrandingCfgSource.DEFAULT;
  private String brandingValue;

  public PortalBrandingCfg() {}

  public PortalBrandingCfg(PortalBrandingCfgKey brandingKey, BrandingCfgSource brandingSource, String brandingValue) {
    this.brandingKey = brandingKey;
    this.brandingSource = brandingSource;
    this.brandingValue = brandingValue;
  }


  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Transient
  public PortalBrandingCfgKey getBrandingKey() {
    return brandingKey;
  }

  public void setBrandingKey(PortalBrandingCfgKey brandingKey) {
    this.brandingKey = brandingKey;
  }

  @Column(name = "branding_key", length = Constants.COL_MAXLEN_INDEXABLE, nullable = false)
  public String getBrandingKeyText() {
    if (brandingKey != null) {
      return brandingKey.name();
    }
    return null;
  }

  public void setBrandingKeyText(String brandingKeyText) {
    this.brandingKey = PortalBrandingCfgKey.fromText(brandingKeyText);
  }

  @Transient
  public BrandingCfgSource getBrandingSource() {
    return brandingSource;
  }

  public void setBrandingSource(BrandingCfgSource brandingSource) {
    this.brandingSource = brandingSource;
  }

  @Column(name = "branding_source", nullable = false)
  public byte getBrandingSourceByte() {
    return brandingSource.getCode();
  }

  public void setBrandingSourceByte(byte brandingSourceByte) {
    this.brandingSource = BrandingCfgSource.valueOf(brandingSourceByte);
  }

  @Column(name = "branding_value", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getBrandingValue() {
    return brandingValue;
  }

  public void setBrandingValue(String brandingValue) {
    this.brandingValue = brandingValue;
  }
}
