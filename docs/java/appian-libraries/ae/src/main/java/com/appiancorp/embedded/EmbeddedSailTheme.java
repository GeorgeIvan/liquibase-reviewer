package com.appiancorp.embedded;

import static com.appiancorp.css.ConfigurableStyleValueSource.ROOT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.persist.Constants;
import com.appiancorp.css.ConfigurableStyle;
import com.appiancorp.css.ConfigurableStyleEnvironment;
import com.appiancorp.embedded.backend.EmbeddedSailThemeDao.View;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.HasForeignKeys;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.suiteapi.type.TypedValue;
import com.appiancorp.type.AppianTypeLong;
import com.appiancorp.type.ExtendedDataTypeProvider;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;

/**
 * Represents a custom theme used to style embedded SAIL as represented in RDBMS.
 */
@Hidden
@Entity
@XmlRootElement(name = EmbeddedSailTheme.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = EmbeddedSailTheme.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {"uuid",
    "themeIdentifier", "name", "description", "isSystem", "embeddedSailStyles"})
@Table(name = "embedded_sail_theme")
@IgnoreJpa
public class EmbeddedSailTheme implements EmbeddedSailThemeSummary, HasAuditInfo {

  private static final long serialVersionUID = 1L;
  public static final String LOCAL_PART = "EmbeddedSailTheme";
  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_THEME_ID = "themeIdentifier";
  public static final String PROP_NAME = Name.LOCAL_PART;
  public static final String PROP_IS_SYSTEM = "isSystem";

  private Long id;
  private String uuid;
  private String themeIdentifier;
  private String name;
  private String description;
  private boolean isSystem;
  private Set<EmbeddedSailStyle> embeddedSailStyles = new HashSet<>();
  @XmlTransient
  private AuditInfo auditInfo = new AuditInfo();

  public EmbeddedSailTheme() {
  }

  public EmbeddedSailTheme(String themeIdentifier, String name) {
    this.themeIdentifier = themeIdentifier;
    this.name = name;
  }

  public EmbeddedSailTheme(String themeIdentifier, String name, String description) {
    this.themeIdentifier = themeIdentifier;
    this.name = name;
    this.description = description;
  }

  public EmbeddedSailTheme(
      final com.appiancorp.type.cdt.EmbeddedSailTheme themeCdt,
      ExtendedDataTypeProvider datatypeProvider) {
    this.themeIdentifier = themeCdt.getThemeIdentifier();
    this.name = themeCdt.getName();
    this.description = themeCdt.getDescription();
    final Integer id = themeCdt.getId_Nullable();
    this.id = id == null ? 0 : id.longValue();
    TypedValue styles = themeCdt.getStyles();
    HashSet<EmbeddedSailStyle> styleBeans = new HashSet<EmbeddedSailStyle>(ConfigurableStyle.values().length);
    if (styles != null && AppianTypeLong.DICTIONARY.equals(styles.getInstanceType())) {
      Map<TypedValue,TypedValue> styleMap = (Map<TypedValue,TypedValue>)styles.getValue();
      for (Entry<TypedValue,TypedValue> entry : styleMap.entrySet()) {
        com.appiancorp.type.cdt.EmbeddedSailStyle style = new com.appiancorp.type.cdt.EmbeddedSailStyle(
            entry.getValue(), datatypeProvider);
        ConfigurableStyle configStyle = ConfigurableStyle.fromKey(style.getName());
        if (configStyle != null && !configStyle.getRawDefaultValue().equals(style.getValue()) &&
            configStyle.getSource() == ROOT) {
          styleBeans.add(new EmbeddedSailStyle(style));
        }
      }
      this.embeddedSailStyles = styleBeans;
    }
  }

  @Override
  public com.appiancorp.type.cdt.EmbeddedSailTheme toCdt(View view) {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(
        ServiceContextFactory.getAdministratorServiceContext());
    com.appiancorp.type.cdt.EmbeddedSailTheme theme = new com.appiancorp.type.cdt.EmbeddedSailTheme(dtp);
    theme.setThemeIdentifier(themeIdentifier);
    theme.setName(name);
    theme.setDescription(description);
    if (id != null) {
      theme.setId(id.intValue());
    }
    if (view == View.Full || view == View.Root) {
      HashMap<TypedValue,TypedValue> styles = new HashMap<TypedValue,TypedValue>(
          ConfigurableStyle.values().length);
      for (EmbeddedSailStyle style : embeddedSailStyles) {
        styles.put(new TypedValue(AppianTypeLong.STRING, style.getName()), style.toCdt().toTypedValue());
      }
      for (ConfigurableStyle configStyle : ConfigurableStyle.values()) {
        EmbeddedSailStyle style = new EmbeddedSailStyle(configStyle.getKey(),
            configStyle.getRawDefaultValue());
        styles.putIfAbsent(new TypedValue(AppianTypeLong.STRING, configStyle.getKey()),
            style.toCdt().toTypedValue());
      }
      theme.setStyles(new TypedValue(AppianTypeLong.DICTIONARY, styles));
    }
    return theme;
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlTransient
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = java.util.UUID.randomUUID().toString();
    }
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlElement(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlElement(name = "name")
  public String getName() {
    return name;
  }

  @Override
  @Column(name = "theme_identifier", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  @XmlElement(name = "themeIdentifier")
  public String getThemeIdentifier() {
    return themeIdentifier;
  }

  public void setThemeIdentifier(String themeIdentifier) {
    this.themeIdentifier = themeIdentifier;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @XmlElement(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "is_system")
  @XmlElement(name = "isSystem")
  public boolean getIsSystem() {
    return isSystem;
  }

  public void setIsSystem(boolean isSystem) {
    this.isSystem = isSystem;
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "theme_id", nullable = false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 100)
  @XmlElement(name = "sailStyle", nillable = true)
  @HasForeignKeys(breadcrumb = BreadcrumbText.SKIP)
  public Set<EmbeddedSailStyle> getEmbeddedSailStyles() {
    return embeddedSailStyles;
  }

  public void setEmbeddedSailStyles(Set<EmbeddedSailStyle> embeddedSailStyles) {
    this.embeddedSailStyles = embeddedSailStyles;
  }

  @Embedded
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }

  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  public boolean equivalentTo(final EmbeddedSailThemeSummary theme) {
    return EQUIVALENCE.equivalent(this, theme);
  }

  public boolean equivalentToWithStyles(final EmbeddedSailTheme other) {
    boolean equivalent = EQUIVALENCE.equivalent(this, other);
    Set<EmbeddedSailStyle> otherStyles = other.getEmbeddedSailStyles();
    ConfigurableStyleEnvironment environment = ConfigurableStyleEnvironment.EMBEDDED_V2;
    if (equivalent) {
      for (EmbeddedSailStyle style : embeddedSailStyles) {
        ConfigurableStyle configurableStyle = ConfigurableStyle.fromKey(style.getName());
        if (configurableStyle.containsEnvironment(environment) && !otherStyles.contains(style)) {
          equivalent = false;
          break;
        }
      }
    }
    return equivalent;
  }

  @Override
  public String toString() {
    return new StringBuilder("EmbeddedSailTheme[").append("uuid: ")
        .append(uuid)
        .append(", id: ")
        .append(id)
        .append(", theme_identifier: ")
        .append(themeIdentifier)
        .append(", name: ")
        .append(name)
        .append(", description: ")
        .append(description)
        .append("]")
        .toString();
  }

  public static Equivalence<EmbeddedSailTheme> ignoreIdAndAuditEquivalence() {
    return new Equivalence<EmbeddedSailTheme>() {
      @Override
      protected boolean doEquivalent(EmbeddedSailTheme a, EmbeddedSailTheme b) {
        return Objects.equal(a.getUuid(), b.getUuid()) &&
            Objects.equal(a.getThemeIdentifier(), b.getThemeIdentifier()) &&
            Objects.equal(a.getName(), b.getName()) &&
            Objects.equal(a.getDescription(), b.getDescription()) &&
            EmbeddedSailStyle.equalDataCheckInstance.pairwise().equivalent(a.getEmbeddedSailStyles(), b.getEmbeddedSailStyles());
      }

      @Override
      protected int doHash(EmbeddedSailTheme embeddedSailTheme) {
        return Objects.hashCode(embeddedSailTheme.getUuid(),
            embeddedSailTheme.getThemeIdentifier(),
            embeddedSailTheme.getName(),
            embeddedSailTheme.getDescription(),
            EmbeddedSailStyle.equalDataCheckInstance.pairwise()
                .hash(embeddedSailTheme.getEmbeddedSailStyles()));
      }
    };
  }
}
