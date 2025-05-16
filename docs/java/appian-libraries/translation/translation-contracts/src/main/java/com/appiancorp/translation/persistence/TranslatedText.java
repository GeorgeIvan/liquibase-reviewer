package com.appiancorp.translation.persistence;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.cdt.value.TranslatedTextDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * An entity class to store a translated text in multiple locales corresponding to a translation string
 */
@Entity
@IgnoreJpa
@Table(name = "ts_translated_text")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name = "translatedText")
@XmlType(name = "TranslatedText", namespace = Type.APPIAN_NAMESPACE, propOrder = {"id", "translationLocale",
    "translatedText"})
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings("checkstyle:anoninnerlength")
public class TranslatedText implements Serializable {

  private static final long serialVersionUID = 9904812977L;
  private Long id;
  private TranslationLocale translationLocale;

  private String translatedText;

  public TranslatedText() {
  }

  public TranslatedText(TranslationLocale translationLocale, String translatedText) {
    this.translationLocale = translationLocale;
    this.translatedText = translatedText;
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

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  @JoinColumn(name = "locale_id", nullable = false)
  @XmlElement(name = "translationLocale", namespace = Type.APPIAN_NAMESPACE)
  public TranslationLocale getTranslationLocale() {
    return translationLocale;
  }

  public void setTranslationLocale(TranslationLocale translationLocale) {
    this.translationLocale = translationLocale;
  }

  @Column(name = "translated_text")
  @XmlElement(name = "translatedText", namespace = Type.APPIAN_NAMESPACE)
  public String getTranslatedText() {
    return translatedText;
  }

  public void setTranslatedText(String translatedText) {
    this.translatedText = translatedText;
  }

  public boolean equivalentTo(final TranslatedText translatedText) {
    return EQUIVALENCE.equivalent(this, translatedText);
  }

  private static Equivalence<TranslatedText> EQUIVALENCE = new Equivalence<TranslatedText>() {
    @Override
    protected boolean doEquivalent(TranslatedText lhs, TranslatedText rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return localeEquals(lhs.translationLocale, rhs.translationLocale) &&
          stringEquals(lhs.translatedText, rhs.translatedText);
    }

    @Override
    protected int doHash(TranslatedText translatedText) {
      return Objects.hashCode(translatedText.translationLocale,
          translatedText.translatedText);
    }

    private boolean localeEquals(TranslationLocale lhsTranslationLocale, TranslationLocale rhsTranslationLocale) {
      if (lhsTranslationLocale == null || rhsTranslationLocale == null) {
        return false;
      }
      return lhsTranslationLocale.equivalentTo(rhsTranslationLocale);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("translationLocale", translationLocale)
        .add("translatedText", translatedText)
        .toString();
  }

  public TranslatedTextDto toCdt() {
    TranslatedTextDto translatedTextDto = new TranslatedTextDto();
    translatedTextDto.setId(this.id);
    translatedTextDto.setLocale(this.translationLocale.toCdt());
    translatedTextDto.setTranslatedText(this.translatedText);
    return translatedTextDto;
  }
}
