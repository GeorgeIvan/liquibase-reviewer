package com.appiancorp.translation.persistence;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.annotations.Immutable;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ForeignKey;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.object.locking.NeedsLockValidation;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.common.LocaleString;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * An entity class to store translation Strings in RDBMS. It holds a collection of strings that belong to a translation set.
 */
@Entity
@IgnoreJpa
@Table(name = "ts_string")
@XmlRootElement(name = "translationString", namespace = Type.APPIAN_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "TranslationString", namespace = Type.APPIAN_NAMESPACE, propOrder = {
    com.appiancorp.type.Id.LOCAL_PART, Uuid.LOCAL_PART, "translationSetUuid", "description",
    "translatorNotes", "translatedTexts", "translationStringVariables"})
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class TranslationString
    implements HasAuditInfo, HasRoleMap, com.appiancorp.type.Id<Long>, Name, Uuid<String>, HasVersionHistory,
    HasTypeQName, NeedsLockValidation {
  public final static String LOCAL_PART = "TranslationString";
  public final static QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  private static final long serialVersionUID = 986876L;
  private Long id;
  private String uuid;
  private String description;
  private TranslationSet translationSet;
  private String translatorNotes;
  private String versionUuid;

  private Set<TranslatedText> translatedTexts = new HashSet<>();

  private AuditInfo auditInfo = new AuditInfo();

  public static final String TRANS_SET_ID = "ts_set_id";

  private boolean needsLockValidationOnUpdate = true;

  private Set<TranslationStringVariable> translationStringVariables = new HashSet<>();

  public TranslationString() {
  }

  public TranslationString(String description, TranslationSet translationSet, String translatorNotes, String versionUuid) {
    this.description = description;
    this.translationSet = translationSet;
    this.translatorNotes = translatorNotes;
    this.versionUuid = versionUuid;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  @XmlAttribute( name = com.appiancorp.type.Id.LOCAL_PART, namespace = com.appiancorp.type.Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "uuid", nullable = false, unique = true)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB, nullable = true)
  @XmlElement(name = "description", namespace = Type.APPIAN_NAMESPACE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Immutable
  @JoinColumn(name = TRANS_SET_ID, nullable = false)
  @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
  public TranslationSet getTranslationSet() {
    return translationSet;
  }

  public void setTranslationSet(TranslationSet translationSet) {
    this.translationSet = translationSet;
  }

  @Transient
  @ForeignKey(type="translationSet", uuidField="translationSetUuid", nullable=false, breadcrumb=BreadcrumbText.translationSet)
  public Long getTranslationSetId() {
    return this.translationSet == null ? null : translationSet.getId();
  }

  public void setTranslationSetId(Long translationSetId) {
    if (translationSet == null) {
      translationSet = new TranslationSet();
    }
    translationSet.setId(translationSetId);
  }

  @Transient
  @XmlElement(name = "translationSetUuid", namespace = Type.APPIAN_NAMESPACE)
  public String getTranslationSetUuid() {
    return this.translationSet == null ? null : translationSet.getUuid();
  }

  public void setTranslationSetUuid(String translationSetUuid) {
    if (translationSet == null) {
      translationSet = new TranslationSet();
    }
    translationSet.setUuid(translationSetUuid);
  }

  @Column(name = "translator_notes", length = Constants.COL_MAXLEN_MAX_NON_CLOB, nullable = true)
  @XmlElement(name = "translatorNotes", namespace = Type.APPIAN_NAMESPACE)
  public String getTranslatorNotes() {
    return translatorNotes;
  }

  public void setTranslatorNotes(String translatorNotes) {
    this.translatorNotes = translatorNotes;
  }

  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "ts_string_to_ts_text", joinColumns = @JoinColumn(name = "ts_string_id"), inverseJoinColumns = @JoinColumn(name = "ts_translated_text_id"))
  @XmlElementWrapper(name = "translationTexts")
  @XmlElement(name = "translatedText")
  public Set<TranslatedText> getTranslatedTexts() {
    return translatedTexts;
  }

  public void setTranslatedTexts(Set<TranslatedText> translatedTexts) {
    this.translatedTexts = translatedTexts;
  }

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
  @JoinColumn(name = "ts_string_id", nullable = false, referencedColumnName = "id")
  @OrderBy
  @XmlElementWrapper(name = "translationStringVariables")
  @XmlElement(name = "translationStringVariable")
  public Set<TranslationStringVariable> getTranslationStringVariables() {
    return translationStringVariables;
  }

  public void setTranslationStringVariables(Set<TranslationStringVariable> translationStringVariables) {
    this.translationStringVariables = translationStringVariables;
  }

  @PrePersist
  private void onPrePersist() {
    if (Strings.isNullOrEmpty(uuid)) {
      uuid = UUID.randomUUID().toString();
    }
  }

  public boolean equivalentTo(final TranslationString translationString) {
    return EQUIVALENCE.equivalent(this, translationString);
  }

  public boolean importEquivalentTo(final TranslationString translationString) {
    return EQUIVALENCE_IMPORT.equivalent(this, translationString);
  }

  @SuppressWarnings({"checkstyle:booleanexpressioncomplexity", "checkstyle:anoninnerlength"})
  private static Equivalence<TranslationString> EQUIVALENCE = new Equivalence<TranslationString>() {
    @Override
    protected boolean doEquivalent(TranslationString lhs, TranslationString rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.description, rhs.description) &&
          stringEquals(lhs.translatorNotes, rhs.translatorNotes) &&
          stringEquals(lhs.versionUuid, rhs.versionUuid) &&
          lhs.translationSet.equivalentTo(rhs.translationSet) &&
          areTranslatedTextsEqual(lhs, rhs) &&
          areTranslationStringVariablesEqual(lhs, rhs);
    }

    private boolean areTranslationStringVariablesEqual(TranslationString lhs, TranslationString rhs) {
      if (lhs.translationStringVariables.size() != rhs.translationStringVariables.size()) {
        return false;
      }
      Set<TranslationStringVariable> equivalentTranslationStringVariables = lhs.translationStringVariables.stream()
          .filter(leftVariable -> {
            for (TranslationStringVariable rightVariable : rhs.translationStringVariables) {
              if (leftVariable.equivalentTo(rightVariable)) {
                return true;
              }
            }
            return false;
          })
          .collect(Collectors.toSet());
      if (lhs.translationStringVariables.size() == equivalentTranslationStringVariables.size()) {
        return true;
      }
      return false;
    }

    @Override
    protected int doHash(TranslationString translationString) {
      return Objects.hashCode(translationString.uuid, translationString.description, translationString.translationSet,
          translationString.translatorNotes, translationString.versionUuid);
    }

    private boolean areTranslatedTextsEqual(TranslationString lhs, TranslationString rhs) {
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

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  private static Equivalence<TranslationString> EQUIVALENCE_IMPORT = new Equivalence<TranslationString>() {
    @Override
    protected boolean doEquivalent(TranslationString lhs, TranslationString rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.description, rhs.description) &&
          stringEquals(lhs.translatorNotes, rhs.translatorNotes) &&
          stringEquals(lhs.versionUuid, rhs.versionUuid) &&
          lhs.translationSet.equivalentTo(rhs.translationSet) &&
          areTranslatedTextsEqual(lhs, rhs) &&
          areTranslationStringVariablesEqual(lhs, rhs);
    }

    private boolean areTranslationStringVariablesEqual(TranslationString lhs, TranslationString rhs) {
      if (lhs.translationStringVariables.size() != rhs.translationStringVariables.size()) {
        return false;
      }
      Set<TranslationStringVariable> equivalentTranslationStringVariables = lhs.translationStringVariables.stream()
          .filter(leftVariable -> {
            for (TranslationStringVariable rightVariable : rhs.translationStringVariables) {
              if (stringEquals(leftVariable.getName(), rightVariable.getName())) {
                return true;
              }
            }
            return false;
          })
          .collect(Collectors.toSet());
      if (lhs.translationStringVariables.size() == equivalentTranslationStringVariables.size()) {
        return true;
      }
      return false;
    }

    @Override
    protected int doHash(TranslationString translationString) {
      return Objects.hashCode(translationString.uuid, translationString.description, translationString.translationSet,
          translationString.translatorNotes, translationString.versionUuid);
    }

    private boolean areTranslatedTextsEqual(TranslationString lhs, TranslationString rhs) {
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

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };

  @Override
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
        .add("translatedTexts", translatedTexts)
        .add("versionUuid", versionUuid)
        .add("auditInfo", auditInfo)
        .add("translationStringVariables", translationStringVariables)
        .toString();
  }

  @Override
  @Transient
  public boolean isPublic() {
    return translationSet.isPublic();
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return translationSet.getFallbackRoleName();
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return translationSet.getRoles();
  }

  @Override
  @Transient
  public RoleMap getRoleMap() {
    return translationSet.getRoleMap();
  }

  @Override
  public void setRoleMap(RoleMap roleMap) {
    translationSet.setRoleMap(roleMap);
  }

  @Override
  public void discardRoleMap() {
    translationSet.discardRoleMap();
  }

  @Override
  public void setPublic(boolean isPublic) {
    // Translation Strings are never public (visible to all users).
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

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  public void setNeedsLockValidationOnUpdate(boolean needsLockValidationOnUpdate) {
    this.needsLockValidationOnUpdate = needsLockValidationOnUpdate;
  }

  @Override
  public boolean needsLockValidationOnUpdate() {
    return needsLockValidationOnUpdate;
  }

  public LocaleString toLocaleString() {
    LocaleString localeString = new LocaleString();
    for (TranslatedText text : getTranslatedTexts()) {
      localeString.put(text.getTranslationLocale().getJavaLocale(), text.getTranslatedText());
    }
    return localeString;
  }
}
