package com.appiancorp.uicontainer;

import static com.appian.core.base.MultilineToStringHelper.INDENT_INCREMENT;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.hibernate.collection.internal.PersistentSet;

import com.appian.core.base.MultilineToStringHelper;
import com.appian.core.base.ToStringFunction;
import com.appian.core.collections.Iterables2;
import com.appian.core.persist.Constants;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.object.HasVersionHistory;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.HasRoleMap;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.audit.AuditInfo;
import com.appiancorp.security.audit.HasAuditInfo;
import com.appiancorp.suiteapi.common.exceptions.AppianRuntimeException;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Id;
import com.appiancorp.type.Name;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.refs.Ref;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 *
 * Contains UI Definition Metadata
 *
 * @author johnny.debrodt
 */
@Hidden
@Entity
@Table(name = UiContainer.TABLE_NAME)
@XmlRootElement(name = "uiContainer", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = UiContainer.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {Id.LOCAL_PART, Uuid.LOCAL_PART,
  "name", "description", "uiExpr", "urlStub"})
@IgnoreJpa
@Tracked
public final class UiContainer implements Id<Long>, Uuid<String>, Name, HasAuditInfo, HasRoleMap,
    UiContainerRef, HasTypeQName, HasVersionHistory {
  private static final long serialVersionUID = 1L;

  public static final String TABLE_NAME = "ui_container";
  public static final String TBL_UI_CONTAINER_RM = "ui_container_rm";
  public static final String JOIN_COL_UI_CONTAINER_ID = "ui_container_id";
  public static final String LOCAL_PART = "UiContainer";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = Id.LOCAL_PART;
  public static final String PROP_UUID = Uuid.LOCAL_PART;
  public static final String PROP_URL_STUB = "urlStub";
  public static final String PROP_NAME = "name";
  public static final String PROP_DESCRIPTION = "description";
  public static final String PROP_CONTENT = "content";
  public static final String PROP_IS_TASK_REPORT = "taskReport";
  public static final String PROP_VERSION_UUID = "versionUuid";

  public static final ImmutableSet<String> NON_DATATYPE_COLUMNS = ImmutableSet.of(
      Id.LOCAL_PART, Uuid.LOCAL_PART, "name", "description", "urlStub", "task_report", PROP_CREATED_BY,
      PROP_CREATED_BY_USER_ID, PROP_CREATED_TS, PROP_UPDATED_BY, PROP_UPDATED_BY_USER_ID, PROP_UPDATED_TS,
      PROP_VERSION_UUID);

  // IMPORTANT: The order is significant (from highest to lowest privileges).
  public static final ImmutableSet<Role> ALL_ROLES = ImmutableSet.of(
    Roles.REPORT_ADMIN,
    Roles.REPORT_EDITOR,
    Roles.REPORT_AUDITOR,
    Roles.REPORT_VIEWER
  );

  private Long id;
  private String uuid;
  private String versionUuid;
  private String name;
  private String description;
  private String uiExpr;
  private String urlStub;
  private AuditInfo auditInfo = new AuditInfo();
  /*For type look at FeedEntryCategory.java and com.appiancorp.tempo.rdbms.Comment.getCategoryCode()
  private byte type;
  */

  private transient Set<RoleMapEntry> roleMapEntries = new HashSet<>(); // GWT=Transient
  private boolean isTaskReport;
  private boolean isPublic;

  /**
   * Default constructor for JAXB compatibility and testing only
   */
  public UiContainer() {}

  public UiContainer(String name, String contentExpr, String urlStub) {
    this.name = name;
    this.uiExpr = contentExpr;
    this.urlStub = urlStub;
  }

  public UiContainer(UiContainerRef uiContainerRef) {
    this.id = uiContainerRef.getId();
    this.uuid = uiContainerRef.getUuid();
  }

  @Override
  public Ref<Long,String> build(Long id, String uuid) {
    UiContainer uc = new UiContainer();
    uc.id = id;
    uc.uuid = uuid;
    return uc;
  }

  @PrePersist
  private void onPrePersist() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  //Static function for search
  public static final Function<Id<Long>,Long> selectId = new Function<Id<Long>,Long>() {
    @Override
    public Long apply(Id<Long> input) {
      return input == null ? null : input.getId();
    }
  };

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return uuid;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  @XmlTransient
  @Column(name = "version_uuid", length = Constants.COL_MAXLEN_UUID, nullable = true)
  public String getVersionUuid() {
    return versionUuid;
  }
  @Override
  public void setVersionUuid(String versionUuid) {
    this.versionUuid = versionUuid;
  }

  @Override
  @Column(name = "name", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE)
  @XmlAttribute(name = Name.LOCAL_PART, namespace = Name.NAMESPACE)
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

 //Static function for search testing
  public static final Function<Name,String> selectName = new Function<Name,String>() {
    @Override
    public String apply(Name input) {
      return input == null ? null : input.getName();
    }
  };

  @Column(name = "description", length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "ui_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @ComplexForeignKey(nullable=false, breadcrumb=BreadcrumbText.uiContainerExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  @Lob
  public String getUiExpr() {
    return uiExpr;
  }

  public void setUiExpr(String uiExpr) {
    this.uiExpr = uiExpr;
  }

  @Override
  @Embedded
  // Marked XmlTransient because we need to implement binding between k/rdbms
  // userIds before this can be serialized. Unfinished impl in
  // AuditInfoPrimaryDsBinder. When this annotation is removed, add auditInfo
  // back into propOrder above and un-ignore test in UiContainerTest
  @XmlTransient
  public AuditInfo getAuditInfo() {
    return auditInfo;
  }
  public void setAuditInfo(AuditInfo auditInfo) {
    this.auditInfo = auditInfo;
  }

  /**
   * A unique URL component that can be used to reference instances of this
   * {@link UiContainer}
   */
  @Column(name = "url_stub", nullable = false, length = Constants.COL_MAXLEN_INDEXABLE, unique = true)
  public String getUrlStub() {
    return urlStub;
  }

  public void setUrlStub(String urlStub) {
    if (urlStub != null) {
      if (!urlStub.matches("[\\w\\-]+") || urlStub.length() > Constants.COL_MAXLEN_INDEXABLE) {
        // Matches one or more word characters or a hyphen [a-Z0-9_\-]
        throw new AppianRuntimeException(ErrorCode.UI_CONTAINER_INVALID_URL_STUB, urlStub,
          Constants.COL_MAXLEN_INDEXABLE);
      }
    }
    this.urlStub = urlStub;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id",id).add("uuid",uuid)
                  .add("name",name).toString();
  }

  public static ToStringFunction<UiContainer> multilineToString(final int indent) {
    return new ToStringFunction<UiContainer>() {
      @Override
      public String doToString(UiContainer t) {
        return MultilineToStringHelper.of(t, indent)
          .add(PROP_ID, t.id)
          .add(PROP_UUID, t.uuid)
          .add(PROP_NAME, t.name)
          .add(PROP_URL_STUB, t.urlStub)
          .add(PROP_CONTENT, t.uiExpr)
          .toString();
      }
    };
  }

  private static final Equivalence<UiContainer> equalDataCheckInstance = new Equivalence<UiContainer>() {
    @Override
    protected boolean doEquivalent(UiContainer lhs, UiContainer rhs) {
      if(lhs==rhs) return true;
      if(null==lhs) return null==rhs;
      if(null==rhs) return false;
      return Objects.equal(lhs.name,rhs.name) && Objects.equal(lhs.description,rhs.description)
        && Objects.equal(lhs.urlStub,rhs.urlStub) && Objects.equal(lhs.uiExpr,rhs.uiExpr)
        && Objects.equal(lhs.isTaskReport,rhs.isTaskReport);

    }
    @Override
    protected int doHash(UiContainer t) {
      return Objects.hashCode(t.name, t.description, t.urlStub, t.uiExpr);
    }
  };

  /**
   * Test equivalence in all fields except for autogenerated fields.
   * Id is an autogenerated field.
   * AuditInfo is autogenerated.
   *
   * @return {@link Equivalence}
   */
  public static Equivalence<UiContainer> equalityForNonGeneratedFields() {
    return equalDataCheckInstance;
  }

  private static final Function<UiContainer,Timestamp> selectUpdatedTs = new Function<UiContainer,Timestamp>() {
    @Override
    public Timestamp apply(UiContainer input) {
      return input.getAuditInfo().getUpdatedTs();
    }
  };
  public static Function<UiContainer,Timestamp> selectUpdatedTs() {
    return selectUpdatedTs;
  }

  /**
   * Wrapper type so JAXB can marshal a list
   *
   * @author johnny.debrodt
   */
  @XmlRootElement(name = "uiContainers", namespace = Type.APPIAN_NAMESPACE)
  @XmlType(name = UiContainerList.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {"uiContainers"})
  public static class UiContainerList {

    public static final String LOCAL_PART = "UiContainerList";

    private List<UiContainer> uiContainers;

    public UiContainerList() {
    }

    public UiContainerList(List<UiContainer> uiContainers) {
      this.uiContainers = uiContainers;
    }

    @XmlElement(name = "uiContainer")
    public List<UiContainer> getUiContainers() {
      return uiContainers;
    }

    public void setUiContainers(List<UiContainer> uiContainers) {
      this.uiContainers = uiContainers;
    }

    public static ToStringFunction<UiContainerList> multilineToString(final int indent) {
      return new ToStringFunction<UiContainerList>() {
        @Override
        public String doToString(UiContainerList t) {
          return MultilineToStringHelper.of(t, indent)
            .add("uiContainers", t.uiContainers, UiContainer.multilineToString(indent+INDENT_INCREMENT))
            .toString();
        }
      };
    }

    private static final Equivalence<UiContainerList> equalDataCheckInstance = new Equivalence<UiContainerList>() {
      @Override
      protected boolean doEquivalent(UiContainerList lhs, UiContainerList rhs) {
        if(lhs==rhs) return true;
        if(null==lhs) return null==rhs;
        if(null==rhs) return false;
        return Iterables2.equal(lhs.uiContainers, rhs.uiContainers, UiContainer.equalityForNonGeneratedFields());
      }
      @Override
      protected int doHash(UiContainerList t) {
        return Iterables2.hash(t.uiContainers, UiContainer.equalityForNonGeneratedFields());
      }
    };

    /**
     * Test equivalence in all fields except for autogenerated fields.
     *
     * @return {@link Equivalence}
     */
    public static Equivalence<UiContainerList> equalityForNonGeneratedFields() {
      return equalDataCheckInstance;
    }
  }

  @Override
  @Transient @XmlTransient
  public ImmutableSet<Role> getRoles() {
    return ALL_ROLES;
  }

  @Override
  @Transient @XmlTransient
  public RoleMap getRoleMap() {
    if (roleMapEntries == null) {
      return null;
    }

    RoleMap.Builder roleMapBuilder = RoleMap.builder();
    for (RoleMapEntry roleMapEntry : roleMapEntries) {
      if (Roles.REPORT_AUDITOR.equals(roleMapEntry.getRole())) {
        roleMapBuilder.users(Roles.REPORT_VIEWER, roleMapEntry.getUsers());
        roleMapBuilder.groups(Roles.REPORT_VIEWER, roleMapEntry.getGroups());
      } else {
        roleMapBuilder.entries(roleMapEntry);
      }
    }

    return roleMapBuilder.build();
  }
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
  public void discardRoleMap() {
    this.roleMapEntries = new PersistentSet(); // This tells Hibernate to ignore this field during the update.
  }

  @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
  @JoinTable(name=TBL_UI_CONTAINER_RM,
    joinColumns=@JoinColumn(name=JOIN_COL_UI_CONTAINER_ID),
    inverseJoinColumns=@JoinColumn(name=RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  @XmlTransient
  private Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  @Override
  @Transient
  @XmlTransient
  public boolean isPublic() {
    return isPublic;
  }

  @Override
  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  @Override
  @Transient
  @XmlTransient
  public String getFallbackRoleName() {
    return Roles.REPORT_VIEWER.getName();
  }

  @Column(name="task_report", nullable=false, updatable=false)
  @XmlTransient
  public boolean isTaskReport() {
    return isTaskReport;
  }
  private void setTaskReport(boolean isTaskReport) {
    this.isTaskReport = isTaskReport;
  }

  @Transient @XmlTransient
  public boolean isTempoReport() {
    return !isTaskReport;
  }
  private void setTempoReport(boolean isTempoReport) {
    this.isTaskReport = !isTempoReport;
  }

  public static class UiContainerFactory {
    public static UiContainer asTempoReport(UiContainer uic) {
      uic.setTaskReport(false);
      return uic;
    }
    public static UiContainer asTaskReport(UiContainer uic) {
      uic.setTaskReport(true);
      return uic;
    }

    public static UiContainer newTempoReport(String name, String contentExpr, String urlStub) {
      return asTempoReport(new UiContainer(name, contentExpr, urlStub));
    }

    public static UiContainer newTaskReport(String name, String contentExpr, String urlStub) {
      return asTaskReport(new UiContainer(name, contentExpr, urlStub));
    }
  }

  @XmlTransient
  @Transient
  @Override
  public QName getTypeQName() {
    return isTaskReport() ? TaskReport.QNAME : TempoReport.QNAME;
  }
}
