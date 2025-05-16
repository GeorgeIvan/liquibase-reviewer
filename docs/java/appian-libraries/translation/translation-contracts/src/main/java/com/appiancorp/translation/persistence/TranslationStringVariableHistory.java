package com.appiancorp.translation.persistence;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.suiteapi.type.Type;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;

/**
 * An entity class to store history of translation string variables in RDBMS. It holds history of translation string variables.
 */
@Entity
@Table(name = "ts_string_variable_history")
@SuppressWarnings({"checkstyle:anoninnerlength"})
public class TranslationStringVariableHistory {

  private Long id;
  private String uuid;
  private String name;

  public TranslationStringVariableHistory() {
  }

  public TranslationStringVariableHistory(
      Long id, String uuid, String name) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
  }

  public TranslationStringVariableHistory(String uuid, String name) {
    this.uuid = uuid;
    this.name = name;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false, unique = true)
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

  public boolean equivalentTo(final TranslationStringVariableHistory translationStringVariableHistory) {
    return EQUIVALENCE.equivalent(this, translationStringVariableHistory);
  }

  private static Equivalence<TranslationStringVariableHistory> EQUIVALENCE = new Equivalence<TranslationStringVariableHistory>() {
    @Override
    protected boolean doEquivalent(
        TranslationStringVariableHistory lhs, TranslationStringVariableHistory rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) && stringEquals(lhs.name, rhs.name);
    }

    @Override
    protected int doHash(TranslationStringVariableHistory variableHistory) {
      return Objects.hashCode(variableHistory.id);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return com.google.common.base.Objects.equal(lhs, rhs) ||
          Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("uuid", uuid).add("name", name).toString();
  }
}
