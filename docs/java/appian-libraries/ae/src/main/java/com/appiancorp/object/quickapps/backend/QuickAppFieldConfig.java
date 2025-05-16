package com.appiancorp.object.quickapps.backend;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appian.core.persist.Constants;
import com.appiancorp.type.Id;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

/**
 * QuickAppFieldConfig is the bean that represents a custom configuration for a QuickApp Field.
 *  The data contents are dependent on {@link QuickAppField#type}
 **/
@Entity
@Table(name = "quickapp_fld_cfg")
public class QuickAppFieldConfig implements Id<Long> {
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_DATA = "data";

  private Long id;
  private String data;
  private Long lookupKey;

  public QuickAppFieldConfig() {}

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * The data returned is the data used for a single configuration for a single field. For example:
   * For {@link QuickAppFieldType#SINGLE_SELECT} the data would be one of the possible selections.
   * For {@link QuickAppFieldType#RECORD} the data would be the recordType uuid used to populate a picker.
   */
  @Column(name = "data", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  /**
   * The value returned is the key used to identify the entry in the business data source. The lookup
   * table will use this key instead of the data above as primary key. We are not using foreign key
   * setup since this value is used across the data source.
   */
  @Column(name = "lookup_key", nullable = true)
  public Long getLookupKey() {
    return lookupKey;
  }

  public void setLookupKey(Long lookupKey) {
    this.lookupKey = lookupKey;
  }

  /**
   * Equivalence implies the data are equal.
   */
  public boolean equivalentTo(final QuickAppFieldConfig field) {
    return EQUIVALENCE.equivalent(this, field);
  }

  private static final Equivalence<QuickAppFieldConfig> EQUIVALENCE = new Equivalence<QuickAppFieldConfig>() {
    @Override
    protected boolean doEquivalent(QuickAppFieldConfig firstConfig, QuickAppFieldConfig secondConfig) {
       return Objects.equal(firstConfig.getData(), secondConfig.getData()) &&
           (Objects.equal(firstConfig.getLookupKey(), secondConfig.getLookupKey()));
    }

    @Override
    protected int doHash(QuickAppFieldConfig quickApp) {
      return Objects.hashCode(quickApp.getData(), quickApp.getLookupKey());
    }
  };

  @Override
  public String toString() {
    return "QuickAppFieldConfig [id=" + id + ", data=" + data + ", lookupKey=" + lookupKey + "]";
  }
}
