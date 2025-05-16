package com.appiancorp.translation.persistence;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Immutable;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.Type;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.type.cdt.value.TranslationLocaleDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

/**
 * An entity class to store translation locales in RDBMS.
 */
@Entity
@Immutable
@IgnoreJpa
@Table(name = "ts_locale")
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name = "translationLocale")
@XmlType(name = "TranslationLocale", namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, propOrder = {
    "id", "localeLanguageTag"})
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings("checkstyle:anoninnerlength")
public class TranslationLocale implements Serializable {

  private static final long serialVersionUID = 7554812977L;

  private Long id;
  private String locale;

  static final String TRANS_LOCALE_LANG_TAG = "locale_language_tag";

  public TranslationLocale() {
  }

  public TranslationLocale(Locale locale) {
    setJavaLocale(locale);
  }

  public TranslationLocale(TranslationLocaleDto translationLocaleDto) {
    if (translationLocaleDto.getId() != null &&
        translationLocaleDto.getId().intValue() != Type.INTEGER.nullOf()) {
      this.id = translationLocaleDto.getId();
    }
    this.locale = translationLocaleDto.getLocale();
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  @XmlAttribute(name = com.appiancorp.type.Id.LOCAL_PART, namespace = com.appiancorp.type.Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = TRANS_LOCALE_LANG_TAG, nullable = false, unique = true, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name = "localeLanguageTag")
  String getLocaleLanguageTag() {
    return locale;
  }

  void setLocaleLanguageTag(String locale) {
    this.locale = locale;
  }

  @Transient
  public Locale getJavaLocale() {
    return Locale.forLanguageTag(locale);
  }

  public void setJavaLocale(Locale locale) {
    this.locale = locale != null ? locale.toLanguageTag() : "";
  }

  public boolean equivalentTo(final TranslationLocale translationLocale) {
    return EQUIVALENCE.equivalent(this, translationLocale);
  }

  private static Equivalence<TranslationLocale> EQUIVALENCE = new Equivalence<TranslationLocale>() {
    @Override
    protected boolean doEquivalent(TranslationLocale lhs, TranslationLocale rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.locale, rhs.locale);
    }

    @Override
    protected int doHash(TranslationLocale translationLocale) {
      return Objects.hashCode(translationLocale.id, translationLocale.locale);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) ||
          Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  public TranslationLocaleDto toCdt() {
    TranslationLocaleDto translationLocaleCdt = new TranslationLocaleDto();
    translationLocaleCdt.setId(id);
    translationLocaleCdt.setLocale(locale);
    return translationLocaleCdt;
  }
}
