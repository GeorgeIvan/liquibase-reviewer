package com.appiancorp.translation.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.portable.string.Strings;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.TranslationSetDto;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.GroupRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An entity class to store a translation set in RDBMS.
 */
@Entity
@IgnoreJpa
@Tracked
@Table(name = "ts_set")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name = "translationSet")
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(name = "TranslationSet", namespace = Type.APPIAN_NAMESPACE, propOrder = {
    com.appiancorp.type.Id.LOCAL_PART, Uuid.LOCAL_PART, "name", "description", "enabledLocales", "defaultLocale"})
@XmlSeeAlso({GroupRefImpl.class})
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:anoninnerlength"})
public class TranslationSet implements HasRoleMap, HasAuditInfo, com.appiancorp.type.Id<Long>, Name,
    Uuid<String>, HasVersionHistory, HasTypeQName {
  private static final long serialVersionUID = 876876L;

  private Long id;
  private String uuid;
  private String name;
  private String description;
  private String versionUuid;
  private TranslationLocale defaultLocale;
  private Set<TranslationLocale> enabledLocales;
  private AuditInfo auditInfo = new AuditInfo();
  private static final String TRANS_SET_DEFAULT_LOCALE_ID = "default_locale_id";
  private static final String TRANS_SET_TO_TRANS_LOCALE = "ts_set_to_ts_locale";
  private static final String TRANS_LOCALE_ID = "ts_locale_id";

  private static final String TRANS_SET_ROLE_MAP = "ts_rm";
  private static final String TRANS_SET_ID = "ts_set_id";
  private static final String ROLE_MAP_ENTRY_ID = "rm_entry_id";
  public final static String LOCAL_PART = "TranslationSetDesignObject";
  public final static QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  //IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
      Roles.TRANSLATION_SET_ADMIN, Roles.TRANSLATION_SET_EDITOR, Roles.TRANSLATION_SET_VIEWER);
  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>();

  //This field is used only for IX. Never be populated when querying from the DB.
  private List<TranslationString> translationString = new ArrayList<>();

  public TranslationSet() {
  }

  public TranslationSet(String name, String description, TranslationLocale defaultLocale) {
    this.name = name;
    this.description = description;
    this.defaultLocale = defaultLocale;
    this.enabledLocales = new HashSet<>();
  }

  public TranslationSet(TranslationSetDto translationSetDto) {
    if (translationSetDto.getId() != null && translationSetDto.getId().intValue() != com.appiancorp.core.expr.portable.Type.INTEGER.nullOf()) {
      this.id = translationSetDto.getId();
    }
    this.uuid = translationSetDto.getUuid();
    this.name = translationSetDto.getName();
    this.description = translationSetDto.getDescription();
    this.versionUuid = translationSetDto.getVersionUuid();
    this.defaultLocale = new TranslationLocale(translationSetDto.getDefaultLocale());
    this.enabledLocales = translationSetDto.getEnabledLocales()
        .stream()
        .map(TranslationLocale::new)
        .collect(Collectors.toSet());
  }

  @Id
  @GeneratedValue
  @XmlAttribute(name = com.appiancorp.type.Id.LOCAL_PART, namespace = com.appiancorp.type.Id.NAMESPACE)
  @Column(name = "id", updatable = false, nullable = false)
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

  @Column(name = "name", nullable = false, unique = true)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement(name = "description", namespace = Type.APPIAN_NAMESPACE)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }

  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
  @JoinColumn(name = TRANS_SET_DEFAULT_LOCALE_ID, nullable = false)
  @XmlElement(name = "defaultLocale", namespace = Type.APPIAN_NAMESPACE)
  public TranslationLocale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(TranslationLocale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
  @JoinTable(name = TRANS_SET_TO_TRANS_LOCALE, joinColumns = @JoinColumn(name = TRANS_SET_ID), inverseJoinColumns = @JoinColumn(name = TRANS_LOCALE_ID))
  @XmlElement(name = "enabledLocales", namespace = Type.APPIAN_NAMESPACE)
  public Set<TranslationLocale> getEnabledLocales() {
    return enabledLocales;
  }

  public void setEnabledLocales(Set<TranslationLocale> enabledLocales) {
    this.enabledLocales = enabledLocales;
  }

  @Override
  public void discardRoleMap() {
    // This tells Hibernate to ignore this field during the update.
    this.roleMapEntries = new PersistentSet();
  }

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = TRANS_SET_ROLE_MAP, joinColumns = @JoinColumn(name = TRANS_SET_ID), inverseJoinColumns = @JoinColumn(name = ROLE_MAP_ENTRY_ID))
  @MapKey(name = RoleMapEntry.PROP_ROLE)
  public Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(final Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  public void setRoleMap(RoleMap roleMap) {
    if (this.roleMapEntries != null) {
      this.roleMapEntries.clear();
    }
    if (roleMap != null) {
      if (this.roleMapEntries == null) {
        this.roleMapEntries = new HashSet<>();
      }
      this.roleMapEntries.addAll(roleMap.getEntriesByRole().values());
    }
  }

  @Override
  @Transient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      roleMapBuilder.entries(roleMapEntry);
    }

    return roleMapBuilder.build();
  }

  @Override
  @Transient
  public boolean isPublic() {
    return false;
  }

  @Override
  public void setPublic(boolean isPublic) {
    // Translation Sets are never public (visible to all users).
  }

  @Override
  @Transient
  public String getFallbackRoleName() {
    return Roles.TRANSLATION_SET_VIEWER.getName();
  }

  @Override
  @Transient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

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
        .add("name", name)
        .add("description", description)
        .add("versionUuid", versionUuid)
        .add("defaultLocale", defaultLocale)
        .add("enabledLocales", enabledLocales)
        .add("auditInfo", auditInfo)
        .add("roleMapEntries", roleMapEntries)
        .toString();
  }

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  public boolean equivalentTo(final TranslationSet translationSet) {
    return EQUIVALENCE.equivalent(this, translationSet);
  }

  private static Equivalence<TranslationSet> EQUIVALENCE = new Equivalence<TranslationSet>() {
    @Override
    protected boolean doEquivalent(TranslationSet lhs, TranslationSet rhs) {
      if (lhs == rhs) {
        return true;
      }
      if (lhs.getClass() != rhs.getClass()) {
        return false;
      }
      return stringEquals(lhs.uuid, rhs.uuid) &&
          stringEquals(lhs.name, rhs.name) &&
          stringEquals(lhs.description, rhs.description) &&
          lhs.defaultLocale.equivalentTo(rhs.defaultLocale) &&
          areEnabledLocalesEqual(lhs, rhs);
    }

    private boolean areEnabledLocalesEqual(TranslationSet lhs, TranslationSet rhs) {
      if (lhs.enabledLocales.size() != rhs.enabledLocales.size()) {
        return false;
      }
      Set<TranslationLocale> equivalentTranslationLocale = lhs.enabledLocales.stream().filter(leftEnabledLocale -> {
            for (TranslationLocale rightEnabledLocale : rhs.enabledLocales) {
              if (leftEnabledLocale.equivalentTo(rightEnabledLocale)) {
                return true;
              }
            }
            return false;
          }).collect(Collectors.toSet());
      if (lhs.enabledLocales.size() == equivalentTranslationLocale.size()) {
        return true;
      }
      return false;
    }

    @Override
    protected int doHash(TranslationSet translationSet) {
      return Objects.hashCode(translationSet.uuid, translationSet.name, translationSet.description,
          translationSet.defaultLocale, translationSet.enabledLocales);
    }

    private boolean stringEquals(String lhs, String rhs) {
      return Objects.equal(lhs, rhs) || Strings.isNullOrEmpty(lhs) && Strings.isNullOrEmpty(rhs);
    }
  };
}
