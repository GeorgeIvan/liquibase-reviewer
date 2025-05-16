package com.appiancorp.translation.persistence;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.type.Name;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;


/**
 * An entity class to store history of translation strings in RDBMS. It holds history of translation strings that belong to a translation set.
 */
@Entity
@Table(name = "ts_string_history")
public class TranslationStringHistory implements Name {

  private Long id;
  private String uuid;
  private String description;
  private TranslationSet translationSet;
  private String translatorNotes;
  private String versionUuid;
  private Long translationStringId;

  private Set<TranslatedText> translatedTexts = new HashSet<>();

  private AuditInfo auditInfo;

  private Set<TranslationStringVariableHistory> translationStringVariablesHistory = new HashSet<>();

  private static final String TRANS_STRING_TO_TEXT_HISTORY = "ts_string_to_text_history";
  private static final String TRANS_STRING_HISTORY_ID = "ts_string_history_id";
  private static final String TRANS_TEXT_ID = "ts_translated_text_id";

  TranslationStringHistory() {
  }

  public TranslationStringHistory(TranslationString ts) {
    this.uuid = ts.getUuid();
    this.description = ts.getDescription();
    this.translatorNotes = ts.getTranslatorNotes();
    this.versionUuid = ts.getVersionUuid();
    this.translationStringId = ts.getId();
    this.translationSet = ts.getTranslationSet();
    this.translatedTexts = ts.getTranslatedTexts()
        .stream()
        .collect(Collectors.toSet());
    this.auditInfo = new AuditInfo(requireNonNull(ts.getAuditInfo()));
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

  @Column(name = "uuid", length = Constants.COL_MAXLEN_UUID, nullable = false)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB, nullable = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Immutable
  @JoinColumn(name = "ts_set_id", nullable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
  public TranslationSet getTranslationSet() {
    return translationSet;
  }

  public void setTranslationSet(TranslationSet translationSet) {
    this.translationSet = translationSet;
  }

  @Column(name = "translator_notes", length = Constants.COL_MAXLEN_MAX_NON_CLOB, nullable = true)
  public String getTranslatorNotes() {
    return translatorNotes;
  }

  public void setTranslatorNotes(String translatorNotes) {
    this.translatorNotes = translatorNotes;
  }

  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = false, unique = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Column(name = "ts_string_id", nullable = false)
  public Long getTranslationStringId() {
    return translationStringId;
  }

  public void setTranslationStringId(Long translationStringId) {
    this.translationStringId = translationStringId;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = TRANS_STRING_TO_TEXT_HISTORY, joinColumns = @JoinColumn(name = TRANS_STRING_HISTORY_ID), inverseJoinColumns = @JoinColumn(name = TRANS_TEXT_ID))
  public Set<TranslatedText> getTranslatedTexts() {
    return translatedTexts;
  }

  public void setTranslatedTexts(Set<TranslatedText> translatedTexts) {
    this.translatedTexts = translatedTexts;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(name = "ts_str_his_to_var_his", joinColumns = @JoinColumn(name = "ts_string_history_id"), inverseJoinColumns = @JoinColumn(name= "ts_string_variable_history_id"))
  public Set<TranslationStringVariableHistory> getTranslationStringVariablesHistory() {
    return translationStringVariablesHistory;
  }

  public void setTranslationStringVariablesHistory(Set<TranslationStringVariableHistory> translationStringVariablesHistory) {
    this.translationStringVariablesHistory = translationStringVariablesHistory;
  }

  @Override
  @Transient
  public String getName() {
    Optional<TranslatedText> first = Optional.empty();
    if (translationSet.getDefaultLocale() != null && translationSet.getDefaultLocale().getId() != null) {
      Long defaultLocaleId = translationSet.getDefaultLocale().getId();
      first = translatedTexts.stream()
          .filter(text -> defaultLocaleId.equals(text.getTranslationLocale().getId()))
          .findFirst();
    }
    return first.map(TranslatedText::getTranslatedText).orElse("");
  }

  public boolean equivalentTo(final TranslationStringHistory translationStringHistory) {
    return EQUIVALENCE.equivalent(this, translationStringHistory);
  }

  @SuppressWarnings({"checkstyle:booleanexpressioncomplexity", "checkstyle:anoninnerlength"})
  private static Equivalence<TranslationStringHistory> EQUIVALENCE = new Equivalence<TranslationStringHistory>() {
    @Override
    protected boolean doEquivalent(TranslationStringHistory lhs, TranslationStringHistory rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.description, rhs.description) &&
          Objects.equal(lhs.translationSet, rhs.translationSet) &&
          stringEquals(lhs.translatorNotes, rhs.translatorNotes) &&
          stringEquals(lhs.versionUuid, rhs.versionUuid) &&
          Objects.equal(lhs.translationStringId, rhs.translationStringId) &&
          areTranslatedTextsEqual(lhs, rhs) &&
          areTranslationStringVariablesHistoryEquals(lhs, rhs);
    }

    private boolean areTranslationStringVariablesHistoryEquals(TranslationStringHistory lhs, TranslationStringHistory rhs) {
      if (lhs.translationStringVariablesHistory.size() != rhs.translationStringVariablesHistory.size()) {
        return false;
      }
      Set<TranslationStringVariableHistory> equivalentTranslationStringVariablesHistory = lhs.translationStringVariablesHistory.stream()
          .filter(leftVariable -> {
            for (TranslationStringVariableHistory rightVariable : rhs.translationStringVariablesHistory) {
              if (leftVariable.equivalentTo(rightVariable)) {
                return true;
              }
            }
            return false;
          })
          .collect(Collectors.toSet());
      if (lhs.translationStringVariablesHistory.size() == equivalentTranslationStringVariablesHistory.size()) {
        return true;
      }
      return false;
    }

    @Override
    protected int doHash(TranslationStringHistory translationStringHistory) {
      return Objects.hashCode(translationStringHistory.uuid, translationStringHistory.description, translationStringHistory.translationSet,
          translationStringHistory.translatorNotes, translationStringHistory.versionUuid, translationStringHistory.translationStringId);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }

    private boolean areTranslatedTextsEqual(TranslationStringHistory lhs, TranslationStringHistory rhs) {
      if (lhs.translatedTexts.size() != rhs.translatedTexts.size()) {
        return false;
      }
      Set<TranslatedText> equivalentTranslatedTexts = lhs.translatedTexts.stream().filter(leftEnabledLocale -> {
        for (TranslatedText rightEnabledLocale : rhs.translatedTexts) {
          if (leftEnabledLocale.equivalentTo(rightEnabledLocale)) {
            return true;
          }
        }
        return false;
      }).collect(Collectors.toSet());
      if (lhs.translatedTexts.size() == equivalentTranslatedTexts.size()) {
        return true;
      }
      return false;
    }
  };

  @Embedded
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("uuid", uuid)
            .add("description", description)
            .add("translationSet", translationSet)
            .add("translatorNotes", translatorNotes)
            .add("versionUuid", versionUuid)
            .add("translationStringId", translationStringId)
            .add("auditInfo", auditInfo)
            .toString();
  }
}
