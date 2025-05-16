package com.appiancorp.common.config.persistence;

import static com.appian.core.persist.Constants.COL_MAXLEN_GENERATED_EXPRESSION;
import static com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.appiancorp.common.xml.TimestampAdapter;
import com.appiancorp.security.user.User;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Objects;

/**
 * This entity holds a {@code String} key-value pair. This allows persisting a set of properties
 * (like the data in a {@code .properties} file).
 * @author nikita.dubrovsky
 */
@Entity @Table(name=Config.CFG_TABLE)
public class Config {
  public static final String PROP_KEY = "prop_key";
  public static final String PROP_VALUE = "prop_value";
  public static final String CFG_TABLE = "cfg";
  public static final String UPDATED_TS = "updated_ts";
  public static final String IS_DEFAULT = "is_default";
  public static final String UPDATED_BY = "updated_by";

  private String key;
  private String value;
  private boolean isDefault;
  private Timestamp updatedTs;
  private User updatedBy;

  public Config() {}
  public Config(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Column(name=PROP_KEY, nullable=false, length=COL_MAXLEN_INDEXABLE)
  @Id
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Column(name=PROP_VALUE, nullable=true, length=COL_MAXLEN_GENERATED_EXPRESSION)
  @Lob
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name= IS_DEFAULT)
  public boolean getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Column(name= UPDATED_TS)
  private Long getUpdatedTsLong() {
    return updatedTs == null ? null : updatedTs.getTime();
  }

  private void setUpdatedTsLong(Long updatedTsLong) {
    this.updatedTs = updatedTsLong == null ? null : new Timestamp(updatedTsLong);
  }

  @Transient
  @XmlSchemaType(name="dateTime")
  @XmlJavaTypeAdapter(TimestampAdapter.class)
  public Timestamp getUpdatedTs() {
    if (updatedTs == null) {
      return null;
    }
    return new Timestamp(updatedTs.getTime());
  }

  public void setUpdatedTs(Timestamp updatedTs) {
    this.updatedTs = new Timestamp(updatedTs.getTime());
  }

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name= UPDATED_BY)
  private User getUpdatedByUser() {
    return updatedBy;
  }

  private void setUpdatedByUser(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Transient
  public UserRef getUpdatedBy() {
    // IMPORTANT: can't use this.updatedBy since it's lazy-loaded
    return updatedBy == null ? null : new UserRefImpl(this.updatedBy.getRdbmsId(), null);
  }

  public void setUpdatedBy(UserRef updatedBy) {
    this.updatedBy = updatedBy == null ? null : new User(updatedBy);
  }

  @Override
  public String toString() {
    return "Config["+key+"="+value+"]";
  }

  /* It's ok to use the key and value for the implementation of hashCode() and equals(), because
   * the key is assigned, not generated. See http://community.jboss.org/wiki/EqualsandHashCode. */
  @Override
  public final int hashCode() {
    return Objects.hashCode(key, value);
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Config)) {
      return false;
    }
    Config rhs = (Config)obj;
    return Objects.equal(key, rhs.key) && Objects.equal(value, rhs.value);
  }
}
