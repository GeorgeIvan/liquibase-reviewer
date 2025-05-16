package com.appiancorp.translation.persistence;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Immutable;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Uuid;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@Entity
@Table(name = "ts_string_variable")
@XmlRootElement(namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, name = "translationStringVariable")
@XmlType(name = "TranslationStringVariable", namespace = com.appiancorp.suiteapi.type.Type.APPIAN_NAMESPACE, propOrder = {
    com.appiancorp.type.Id.LOCAL_PART, Uuid.LOCAL_PART, "name"})
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings({"checkstyle:anoninnerlength"})
public class TranslationStringVariable implements Serializable {

  public static final String TRANSLATION_STRING_ID_PROP_NAME = "ts_string_id";
  private static final long serialVersionUID = 7447687827392055619L;
  private Long id;
  private String uuid;
  private String name;
  private TranslationString translationString;

  public TranslationStringVariable() {
  }

  public TranslationStringVariable(
      String uuid, String name) {
    this.uuid = uuid;
    this.name = name;
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

  @Column(name = "uuid", nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "name", length = Constants.COL_MAXLEN_MAX_NON_CLOB, nullable = false)
  @XmlElement(name = "name", namespace = Type.APPIAN_NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Immutable
  @JoinColumn(name = "ts_string_id", nullable = false, insertable = false, updatable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
  public TranslationString getTranslationString() {
    return translationString;
  }

  public void setTranslationString(TranslationString translationString) {
    this.translationString = translationString;
  }

  public boolean equivalentTo(final TranslationStringVariable translationStringVariable) {
    return EQUIVALENCE.equivalent(this, translationStringVariable);
  }

  private static Equivalence<TranslationStringVariable> EQUIVALENCE = new Equivalence<TranslationStringVariable>() {
    @Override
    protected boolean doEquivalent(TranslationStringVariable lhs, TranslationStringVariable rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) && stringEquals(lhs.name, rhs.name);
    }

    @Override
    protected int doHash(TranslationStringVariable translationStringVariable) {
      return Objects.hashCode(translationStringVariable.id, translationStringVariable.uuid);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  @PrePersist
  private void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("uuid", uuid).add("name", name).toString();
  }
}
