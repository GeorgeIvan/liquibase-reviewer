package com.appiancorp.tempo.rdbms;

import static com.appiancorp.suiteapi.common.exceptions.ErrorCode.TEMPO_ENGINE_ALREADY_PERSISTED;
import static com.appiancorp.type.refs.UserOrGroup.fromUser;
import static com.appiancorp.type.refs.UserRefs.fromUsername;
import static com.google.common.collect.Collections2.transform;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckReturnValue;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.common.search.LastModifiedProperty;
import com.appiancorp.rdbms.common.StringsSerializer;
import com.appiancorp.rdbms.hb.track.Tracked;
import com.appiancorp.security.acl.Role;
import com.appiancorp.security.acl.RoleMap;
import com.appiancorp.security.acl.RoleMapEntry;
import com.appiancorp.security.acl.Roles;
import com.appiancorp.security.user.User;
import com.appiancorp.suite.SuiteConfiguration;
import com.appiancorp.suite.cfg.ConfigurationFactory;
import com.appiancorp.suiteapi.common.exceptions.AppianRuntimeException;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.tempo.common.AbstractFeedEntry;
import com.appiancorp.tempo.common.FeedEntry;
import com.appiancorp.tempo.common.FeedEntryCategory;
import com.appiancorp.tempo.common.FeedEntrySorter;
import com.appiancorp.tempo.common.FeedEntryWithAttachments;
import com.appiancorp.tempo.common.FeedEntryWithRecordTags;
import com.appiancorp.tempo.common.InvalidFeedDataException;
import com.appiancorp.tempo.common.SortBy;
import com.appiancorp.type.HasTypeQName;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.refs.DocumentRef;
import com.appiancorp.type.refs.GroupRef;
import com.appiancorp.type.refs.GroupRefImpl;
import com.appiancorp.type.refs.GroupRefs;
import com.appiancorp.type.refs.RecordReferenceRef;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.UserOrGroup;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.appiancorp.type.refs.UserRefs;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/*
 * Represents a business event and a collection of related comments.
 *
 * IMPL NOTE 1 (Relationships):
 *
 * We don't explicitly model the event-to-comment relationship using
 * Hibernate, because that prevents us from being able to use one
 * without the other (e.g. to fetch events without their comments, or
 * vice versa).  Why would we want to do that?  Well, I'm glad you
 * asked ...
 *
 * Our main use case is fetching N business events, plus all their
 * comments.  The trouble with using normal Hibernate relationships to
 * do this is as follows:
 *
 * (a) If you enable lazy fetching and cap results at N rows with
 * Hibernate's "setMaxResults" API, you get back exactly N business
 * events, but performance is horrible when you go to fetch comments
 * (total of N+1 select statements).
 *
 * (b) If you use join fetching, the resulting SQL result set has one
 * (event, comment) pair for each comment on each event.  If you use
 * Hibernate's "setMaxResults" API to limit the query results, it caps
 * the number of rows in the result set, so you get M business events
 * with a total of N comments (where typically M < N).
 *
 * (c) If you use join fetching and use Hibernate's "setFetchSize" to
 * page through the results until you get N business events, the MySQL
 * driver will blow up on you.  This is because it loads the results
 * of a query into memory entirely, regardless of what you specify in
 * that parameter.
 *
 * So we just make the association between the two classes implicit,
 * and manage it in our app logic.
 *
 * @author ed.peters
 */
@Entity @Table(name=EventFeedEntry.TBL_TEMPO_FEED_ENTRY)
@Tracked
public class EventFeedEntry extends AbstractFeedEntry implements FeedEntryWithRecordTags,
    FeedEntryWithAttachments, HasTypeQName, Uuid<String>, Serializable {
  private static final long serialVersionUID = 1L;

  public static final String LOCAL_PART = "FeedEntry";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  /* Only add new COL_ constants here if you need to write a native SQL query. If you are just adding a new
   * column use a string literal in the @Column annotation */
  public static final String TBL_TEMPO_FEED_ENTRY = "tp_feed_entry";
  public static final String COL_ID = "id";
  public static final String COL_CREATED_TS = "created_ts";
  public static final String COL_UPDATED_TS = "updated_ts";
  public static final String COL_UPDATED_TS_FOR_SYNC = "updated_ts_for_sync";
  public static final String COL_AUTHOR = "author_id";
  public static final String COL_TYPE = "type";
  public static final String COL_FEED_ID = "feed_id";
  public static final String COL_BODY = "body";
  public static final String COL_ASSOCIATED_DATA = "assoc_data";
  public static final String COL_ASSOCIATED_GROUP_UUID = "assoc_group_uuid";

  public static final String TBL_TEMPO_FEED_ENTRY_RM = "tp_feed_entry_rm";
  public static final String JOIN_COL_TEMPO_FEED_ENTRY_ID = "tp_feed_entry_id";
  public static final Role ROLE_VIEWER = Roles.TEMPO_FEED_ENTRY_VIEWER;

  public static final String PROP_ID = "id";
  public static final String PROP_PUB_TIME = "publishedTimeLong";
  public static final String PROP_MOD_TIME = "modifiedTimeLong";
  public static final String PROP_AUTHOR = "author";
  public static final String PROP_RECIPIENT = "recipient";
  public static final String PROP_COMMENT_AUTHORS = "commentAuthors";
  public static final String PROP_ENTRY_TYPE = "entryType";
  public static final String PROP_CATEGORY = "category";
  public static final String PROP_FEED = "feed";
  public static final String PROP_ASSOCIATED_GROUP = "associatedGroupUuid";
  public static final String PROP_WATCHING_USERS = "watchingUsers";
  public static final String PROP_ASSOCIATED_DATA = "associatedData";
  public static final String PROP_ACTION_PM = "actionProcessModelUuid";
  public static final String PROP_CLOSED_TIME = "closedTime";
  public static final String PROP_BODY_AND_COMMENTS_TEXT = "bodyAndCommentsText";
  public static final String PROP_LINKED_OBJECTS = "linkedObjects";

  protected Feed feed;
  protected FeedSubscription.Type subscriptionType = null;
  protected boolean isFavorite = false;
  protected int commentCount;
  protected boolean isTargeted = false;
  protected boolean isUnsecured = false;
  protected boolean isRoleMapLocked = false;
  protected transient Set<RoleMapEntry> roleMapEntries = new HashSet<>(); // GWT-Transient
  protected SortedSet<Comment> comments;
  protected String actionProcessModelUuid;
  protected String actionLabel;
  protected String actionInstructions;
  protected String actionSummary;
  protected String iconDocUuid;
  protected String source;
  protected String associatedData;
  protected String associatedGroupUuid;
  private transient List<FeedEntryLinkedObject> linkedObjects; // GWT-Transient
  protected User recipient;
  protected Long closedTime;
  private List<UserOrGroup> originalRecipients;
  private User author;
  private Set<UserRef> commentAuthorRdbmsRefs;
  private boolean hasFollowedLinkedObjects;

  @Deprecated
  public EventFeedEntry() {
    this(FeedEntryCategory.BUSINESS_EVENT);
  }
  @Deprecated
  public EventFeedEntry(FeedEntryCategory category) {
    super(category);
    setComments(null);
    setGroupUuid(null);
  }
  @Deprecated
  public EventFeedEntry(FeedEntryCategory category, String author, String bodyText, Timestamp pubTime) {
    super(category);
    this.setSingleAuthor(author);
    this.bodyText = bodyText;
    this.pubTime = pubTime;
    setGroupUuid(null);
  }
  @Deprecated
  public EventFeedEntry(FeedEntryCategory category, String author, String bodyText, String groupUuid,
    Timestamp pubTime) {
    super(category);
    this.setSingleAuthor(author);
    this.bodyText = bodyText;
    this.pubTime = pubTime;
    setGroupUuid(groupUuid);
  }
  public EventFeedEntry(FeedEntryCategory category, String author, String bodyText, Timestamp pubTime,
      boolean isUnsecured, RoleMap rm) {
    super(category);
    setSingleAuthor(author);
    this.bodyText = bodyText;
    this.pubTime = pubTime;
    this.isUnsecured = isUnsecured;
    setRoleMap(rm);
  }

  // copy-constructor
  public EventFeedEntry(EventFeedEntry e) {
    super(e);

    this.feed = e.feed;
    this.subscriptionType = e.subscriptionType;
    this.isFavorite = e.isFavorite;
    this.commentCount = e.commentCount;
    this.isTargeted = e.isTargeted;
    this.isUnsecured = e.isUnsecured;
    this.isRoleMapLocked = e.isRoleMapLocked;

    if (e.roleMapEntries != null) {
      this.roleMapEntries = new HashSet<>(e.roleMapEntries);
    }

    if (e.originalRecipients != null) {
      this.originalRecipients = Lists.newArrayList(e.originalRecipients);
    }

    if (e.comments != null) {
      this.comments = Comment.newSortedSet();
      for (Comment c : e.comments) {
        this.comments.add(new Comment(c));
      }
    }

    if (e.commentAuthorRdbmsRefs != null) {
      this.commentAuthorRdbmsRefs = Sets.newLinkedHashSet(e.commentAuthorRdbmsRefs);
    }

    if (e.linkedObjects != null) {
      this.linkedObjects = Lists.newArrayList(e.linkedObjects);
    }
    if (e.recordTags != null) {
      this.recordTags = Lists.newArrayList(e.recordTags);
    }
    if (e.fileAttachments != null) {
      this.fileAttachments = Lists.newArrayList(e.fileAttachments);
    }

    this.actionProcessModelUuid = e.actionProcessModelUuid;
    this.actionLabel = e.actionLabel;
    this.actionInstructions = e.actionInstructions;
    this.actionSummary = e.actionSummary;
    this.iconDocUuid = e.iconDocUuid;
    this.source = e.source;
    this.associatedData = e.associatedData;
    this.associatedGroupUuid = e.associatedGroupUuid;
    this.author = e.author == null ? null : new User(e.author);
    this.recipient = e.recipient == null ? null : new User(e.recipient);
    this.closedTime = e.closedTime;
  }

  @Override
  @Transient
  public QName getTypeQName() {
    return QNAME;
  }

  @Override
  @Transient
  public String getUuid() {
    return getUniqueId();
  }

  // ================================================================
  // PERSISTENT PROPS
  // ================================================================

  @Id @GeneratedValue
  @Column(name=COL_ID)
  @Override
  public Long getId() {
    return super.getId();
  }

  @Override
  @Transient
  public FeedEntryCategory getCategory() {
    return super.getCategory();
  }

  @Column(name=COL_TYPE, nullable=false)
  public byte getEntryType() {
    return getCategory().getCode();
  }
  public void setEntryType(byte code) {
    setCategory(FeedEntryCategory.valueOf(code));
  }

  @Transient
  public Set<String> getViewerUsernames() {
    RoleMap rm = getRoleMap();
    if (rm == null) {
      return Collections.EMPTY_SET;
    }
    return Collections.unmodifiableSet((Set)rm.getUserIds(ROLE_VIEWER));
  }

  @Transient
  public Set<String> getViewerGroupUuids() {
    RoleMap rm = getRoleMap();
    if (rm == null) {
      return Collections.EMPTY_SET;
    }
    return Collections.unmodifiableSet(rm.getGroupUuids(ROLE_VIEWER));
  }

  @Transient
  public List<UserOrGroup> getViewers() {
    RoleMap rm = getRoleMap();
    if (rm == null) {
      return Collections.EMPTY_LIST;
    }
    List<UserOrGroup> people = Lists.newArrayList();
    for (GroupRef g : rm.getGroups(ROLE_VIEWER)) {
      people.add(new UserOrGroup(g));
    }
    for (UserRef u : rm.getUsers(ROLE_VIEWER)) {
      people.add(new UserOrGroup(u));
    }
    return people;
  }

  public void addViewers(List<UserOrGroup> viewers) {
    RoleMap rm = RoleMap.builder(getRoleMap()).usersAndGroups(ROLE_VIEWER, viewers).build();
    setRoleMap(rm);
  }

  @Transient
  public List<UserOrGroup> getParticipants() {
    Set<UserOrGroup> uog = Sets.newLinkedHashSet(getViewers());
    uog.addAll(transform(transform(getCommentAuthorUsernames(), fromUsername), fromUser));
    return Lists.newArrayList(uog);
  }

  @Transient
  public Set<String> getUserParticipants() {
    Set<String> usernames = Sets.newLinkedHashSet();

    RoleMap rm = getRoleMap();
    if (rm != null) {
      for (UserRef u : rm.getUsers(ROLE_VIEWER)) {
        usernames.add(u.getUsername());
      }
    }
    usernames.addAll(getCommentAuthorUsernames());
    return usernames;
  }

  @Deprecated
  @Transient
  public String getGroupUuid() {
    return getSingleSecurityGroupUuid(getRoleMap());
  }
  @Deprecated
  public void setGroupUuid(String groupUuid) {
    if (Strings.isNullOrEmpty(groupUuid)) {
      setRoleMap(null);
      setUnsecured(true);
    } else {
      setRoleMap(RoleMap.builder().groups(ROLE_VIEWER, new GroupRefImpl(null, groupUuid)).build());
      setUnsecured(false);
    }
  }
  @Deprecated
  private static String getSingleSecurityGroupUuid(RoleMap rm) {
    if (rm == null) {
      return null;
    }
    Set<String> groupUuids = rm.getGroupUuids(ROLE_VIEWER);
    return groupUuids.isEmpty() ? null : Iterables.getOnlyElement(groupUuids);
  }

  public static final String PROP_IS_TARGETED = "targeted";
  @Column(name="is_targeted")
  public boolean isTargeted() {
    return isTargeted;
  }
  public void setTargeted(boolean isTargeted) {
    this.isTargeted = isTargeted;
  }

  public static final String PROP_IS_UNSECURED = "unsecured";
  @Column(name="is_unsecured")
  public boolean isUnsecured() {
    return isUnsecured;
  }
  public void setUnsecured(boolean isUnsecured) {
    this.isUnsecured = isUnsecured;
  }

  public static final String PROP_IS_ROLEMAP_LOCKED = "roleMapLocked";
  @Column(name="is_rolemap_locked", nullable=false)
  public boolean isRoleMapLocked() {
    return isRoleMapLocked;
  }
  public void setRoleMapLocked(boolean isRoleMapLocked) {
    this.isRoleMapLocked = isRoleMapLocked;
  }

  @Transient
  public RoleMap getRoleMap() {
    return roleMapEntries == null ? null : RoleMap.builder().entries(roleMapEntries).build();
  }
  public void setRoleMap(RoleMap roleMap) {
    /* It's not possible to simply overwrite this.roleMapEntries, as Hibernate will throw up:
     * org.hibernate.HibernateException: A collection with cascade="all-delete-orphan" was no longer
     *  referenced by the owning entity instance: com.appiancorp.tempo.rdbms.EventFeedEntry.roleMapEntries */
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
  void replaceRoleMap(RoleMap rm) {
    this.roleMapEntries = new HashSet<>(rm.getEntriesByRole().values());
  }
  public static final String PROP_ROLE_MAP_ENTRIES = "roleMapEntries";
  @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
  @JoinTable(name=TBL_TEMPO_FEED_ENTRY_RM,
    joinColumns=@JoinColumn(name=JOIN_COL_TEMPO_FEED_ENTRY_ID),
    inverseJoinColumns=@JoinColumn(name=RoleMapEntry.JOIN_COL_RM_ENTRY_ID))
  Set<RoleMapEntry> getRoleMapEntries() {
    return roleMapEntries;
  }

  @SuppressWarnings("unused")
  private void setRoleMapEntries(Set<RoleMapEntry> roleMapEntries) {
    this.roleMapEntries = roleMapEntries;
  }

  private static final String GROUP_PREFIX = "g", USER_PREFIX = "u";
  @Column(name="original_recipients", length=Constants.COL_MAXLEN_MAX_NON_CLOB,
      insertable=true, updatable=false, nullable=true)
  private String getOriginalRecipientsStr() {
    List<String> originalRecipientsStrs = Lists.newArrayList();
    if (originalRecipients != null) {
      for (UserOrGroup id : originalRecipients) {
        Ref ref = (Ref)id.getValue();
        if (ref instanceof GroupRef) {
          originalRecipientsStrs.add(GROUP_PREFIX + ref.getId());
        } else if (ref instanceof UserRef) {
          originalRecipientsStrs.add(USER_PREFIX + ref.getId());
        }
      }
    }
    return StringsSerializer.serialize(originalRecipientsStrs);
  }
  @SuppressWarnings("unused")
  private void setOriginalRecipientsStr(String originalRecipientsSerialized) {
    if (Strings.isNullOrEmpty(originalRecipientsSerialized)) {
      this.originalRecipients = Collections.emptyList();
      return;
    }
    List<String> originalRecipientsStrs = StringsSerializer.deserializeToList(originalRecipientsSerialized);
    List<UserOrGroup> uogList = Lists.newArrayList();
    for (String s : originalRecipientsStrs) {
      final String id;
      if (s.startsWith(GROUP_PREFIX)) {
        id = s.substring(GROUP_PREFIX.length());
        uogList.add(new UserOrGroup(GroupRefs.fromGroupId.apply(Long.parseLong(id))));
      } else if (s.startsWith(USER_PREFIX)) {
        id = s.substring(USER_PREFIX.length());
        uogList.add(new UserOrGroup(UserRefs.fromRdbmsId.apply(Long.parseLong(id))));
      }
    }
    this.originalRecipients = uogList;
  }
  @Transient
  public List<UserOrGroup> getOriginalRecipients() {
    return originalRecipients;
  }
  void setOriginalRecipients(List<UserOrGroup> originalRecipients) {
    this.originalRecipients = originalRecipients;
  }

  @Column(name=COL_CREATED_TS, nullable=false)
  @Override
  public Long getPublishedTimeLong() {
    return super.getPublishedTimeLong();
  }

  @Column(name=COL_UPDATED_TS, nullable=false)
  @Override
  public Long getModifiedTimeLong() {
    return super.getModifiedTimeLong();
  }

  /**
   * This reflects the last change to the entry, not just to comments.
   */
  @Column(name=COL_UPDATED_TS_FOR_SYNC, nullable=false) @LastModifiedProperty
  @Override
  public Long getModifiedTimeForSyncLong() {
    return super.getModifiedTimeForSyncLong();
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_AUTHOR, insertable=true, updatable=false, nullable=false)
  private User getAuthor() {
    return author;
  }
  @SuppressWarnings("unused")
  private void setAuthor(User author) {
    this.author = author;
  }
  @Transient
  UserRef getAuthorRdbmsRef() {
    // IMPORTANT: can't use this.author since it's lazy-loaded
    return author == null ? null : new UserRefImpl(author.getRdbmsId(), null);
  }
  void setAuthorRdbmsRef(UserRef author) {
    this.author = author == null ? null : new User(author);
  }
  @Override
  @Transient
  public String getSingleAuthor() {
    return author == null ? null : author.getUsername();
  }
  @Override
  public void setSingleAuthor(String un) {
    super.setSingleAuthor(un);
    author = Strings.isNullOrEmpty(un) ? null : new User(un);
  }
  @VisibleForTesting
  public void setSingleAuthor(Long rdbmsId, String un) {
    super.setSingleAuthor(un);
    author = Strings.isNullOrEmpty(un) ? null : new User(rdbmsId, un);
  }

  @Column(name=COL_BODY, length=Validations.MAX_BODYTEXT_LEN, nullable=false)
  @Override
  public String getBodyText() {
    return super.getBodyText();
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_FEED_ID, nullable=true)
  public Feed getFeed() {
    return feed;
  }
  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  @Transient
  public Long getFeedId() {
    /* The feed object is lazy-loaded, but its PK (id) is always available -- it does not
     * trigger the loading of the whole object from the DB. */
    return feed == null ? null : feed.getId();
  }

  /**
   * Returns the subscription type for this entry's feed for the context user. If this entry is
   * not associated with a feed, or if the context user doesn't have any type of subscription
   * for this entry's feed, returns {@code null}.
   */
  @Transient
  public FeedSubscription.Type getSubscriptionType() {
    return subscriptionType;
  }
  public void setSubscriptionType(FeedSubscription.Type subscriptionType) {
    this.subscriptionType = subscriptionType;
  }

  /**
   * Returns {@code true} if this entry is a favorite for the context user.
   */
  @Override
  @Transient
  public boolean isFavorite() {
    return isFavorite;
  }
  public void setFavorite(boolean isFavorite) {
    this.isFavorite = isFavorite;
  }

  @Column(name=COL_ASSOCIATED_DATA, length=Validations.MAX_ASSOCIATED_DATA_LEN, nullable=true)
  public String getAssociatedData() {
    return associatedData;
  }
  public void setAssociatedData(String associatedData) {
    this.associatedData = associatedData;
  }

  @Column(name=COL_ASSOCIATED_GROUP_UUID, length=Constants.COL_MAXLEN_UUID, nullable=true)
  public String getAssociatedGroupUuid() {
    return associatedGroupUuid;
  }
  public void setAssociatedGroupUuid(String associatedGroupUuid) {
    this.associatedGroupUuid = associatedGroupUuid;
  }

  /**
   * Contract for Linked Objects
   * <ul>
   * <li>When passing an entry to the backend service for persistence, any data in the generic
   * {@link #getLinkedObjects() linkedObjects} field is ignored. The data in the specific fields
   * ({@link #getRecordTags() recordTags}, {@link #getFileAttachments() fileAttachments}, etc) is taken
   * and written into the linkedObjects field for persistence.
   * <li>If an invalid linked object id is passed in to the backend service for persistence, an exception is
   * thrown.
   * <li>When retrieving one or more entries, both the generic linkedObjects field, and all the specific
   * fields ({@link #getRecordTags() recordTags}, {@link #getFileAttachments() fileAttachments}, etc)
   * are populated.
   * <li>When retrieving one or more entries, if the entry has some invalid linked objects, these objects
   * will still be returned, but the reference will contain either a {@code null} or invalid id/uuid.
   * </ul>
   */
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "tp_feed_entry_id", nullable = false)
  @OrderColumn(name = "order_idx", nullable = false)
  List<FeedEntryLinkedObject> getLinkedObjects() {
    return linkedObjects;
  }
  void setLinkedObjects(List<FeedEntryLinkedObject> linkedObjects) {
    this.linkedObjects = linkedObjects;
  }

  @Transient
  public boolean hasFollowedLinkedObjects() {
    return hasFollowedLinkedObjects;
  }

  public void setHasFollowedLinkedObjects(boolean hasFollowedLinkedObjects) {
    this.hasFollowedLinkedObjects = hasFollowedLinkedObjects;
  }

  private transient List<RecordReferenceRef> recordTags; // GWT-Transient

  @Transient
  @CheckReturnValue
  public List<RecordReferenceRef> getRecordTags() {
    return recordTags;
  }
  public void setRecordTags(List<RecordReferenceRef> recordTags) {
    this.recordTags = recordTags;
  }

  private transient List<DocumentRef> fileAttachments; // GWT-Transient

  @Transient
  @Override
  public List<DocumentRef> getFileAttachments() {
    return fileAttachments;
  }
  @Override
  public void setFileAttachments(List<DocumentRef> fileAttachments) {
    this.fileAttachments = fileAttachments;
  }

  private transient List<DocumentRef> fileAttachmentsForEntryAndComments; // GWT-Transient

  /**
   * Returns the attachments for the entry and comments.
   * @return the attachments for the entry and comments.
   */
  @Transient
  public List<DocumentRef> getFileAttachmentsForEntryAndComments() {
    return fileAttachmentsForEntryAndComments;
  }

  /**
   * Sets the attachments for the entry and comments.
   */
  public void setFileAttachmentsForEntryAndComments(List<DocumentRef> allEntryRelatedFileAttachments) {
    this.fileAttachmentsForEntryAndComments = allEntryRelatedFileAttachments;
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="recipient", insertable=true, updatable=true)
  private User getRecipient() {
    return recipient;
  }
  @SuppressWarnings("unused")
  private void setRecipient(User recipient) {
    this.recipient = recipient;
  }
  @Transient
  UserRef getRecipientRdbmsRef() {
    return recipient == null ? null : new UserRefImpl(recipient.getRdbmsId(), null);
  }
  void setRecipientRdbmsRef(UserRef recipient) {
    this.recipient = recipient == null ? null : new User(recipient);
  }

  @Transient
  public String getRecipientUsername() {
    return recipient == null ? null : recipient.getUsername();
  }
  public void setRecipientUsername(String un) {
    recipient = Strings.isNullOrEmpty(un) ? null : new User(un);
  }

  @Override
  @Column(name="comment_count")
  public int getChildCount() {
    return commentCount;
  }
  public void setChildCount(int count) {
    this.commentCount = count;
  }

  @Column(name="action_pm", length=Constants.COL_MAXLEN_UUID)
  public String getActionProcessModelUuid() {
    return actionProcessModelUuid;
  }
  public void setActionProcessModelUuid(String actionProcessModelUuid) {
    this.actionProcessModelUuid = actionProcessModelUuid;
  }

  @Column(name="action_label", length=Validations.MAX_ACTION_LABEL_LEN)
  public String getActionLabel() {
    return actionLabel;
  }
  public void setActionLabel(String actionLabel) {
    this.actionLabel = actionLabel;
  }

  @Column(name="action_summary", length=Validations.MAX_ACTION_SUMMARY_LEN)
  public String getActionSummary() {
    return actionSummary;
  }
  public void setActionSummary(String actionSummary) {
    this.actionSummary = actionSummary;
  }

  @Column(name="action_instr", length=Validations.MAX_ACTION_INSTRUCTIONS_LEN)
  public String getActionInstructions() {
    return actionInstructions;
  }
  public void setActionInstructions(String actionInstructions) {
    this.actionInstructions = actionInstructions;
  }

  @Column(name="icon_doc_uuid", length=Constants.COL_MAXLEN_UUID)
  public String getIconDocumentUuid() {
    return iconDocUuid;
  }
  public void setIconDocumentUuid(String iconDocUuid) {
    this.iconDocUuid = iconDocUuid;
  }

  @Column(name="source", length=Validations.MAX_SOURCE_LEN)
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }

  @Column(name = "closed_ts")
  public Long getClosedTime() {
    return closedTime;
  }

  public void setClosedTime(Long closedTime) {
    this.closedTime = closedTime;
  }

  @Transient
  public boolean isClosed() {
    return null != closedTime;
  }

  public void setCommentAuthorRdbmsRefs(Set<UserRef> commentAuthorRdbmsRefs) {
    this.commentAuthorRdbmsRefs = commentAuthorRdbmsRefs;
  }

  @Transient
  public Set<UserRef> getCommentAuthorRdbmsRefs() {
    if (commentAuthorRdbmsRefs == null) {
      commentAuthorRdbmsRefs = Sets.newLinkedHashSet();
    }
    return commentAuthorRdbmsRefs;
  }

  // ================================================================
  // OTHER ACCESSORS
  // ================================================================

  @Override
  @Transient
  public SortedSet<FeedEntry> getChildren() {
    return (SortedSet) comments;
  }

  /**
   * @return a List of comments for the specified feed entry, capped
   * using the maxComments property for this query (but always including
   * at least one hazard, if there is one)
   */
  @Override
  public SortedSet<FeedEntry> getChildren(int max) {

    // no comments, no worries
    if (getChildCount() == 0 || max == 0) {
      return NO_KIDS;
    }

    List<FeedEntry> before = new ArrayList<FeedEntry>(getChildren());
    // not too many comments, no worries
    if (max < 0 || before.size() <= max) {
      return getChildren();
    }

    // copy the last <maxComments> comments from the list
    List<FeedEntry> after = new ArrayList<FeedEntry>(max+1);
    if (max > 0) {
      after.addAll(before.subList(before.size() - max, before.size()));
    }

    // if this includes at least one hazard, we're done
    for (FeedEntry comment : after) {
      if (((Comment)comment).isHazard()) {
        return getSortedSet(after);
      }
    }

    // otherwise, go through the comments we didn't copy, and
    // look for the first hazard among them.  we're done as
    // soon as we find the first hazard, or we run out of
    // comments to check
    for (int i=before.size()-max; i>=0; i--) {
      FeedEntry comment = before.get(i);
      if (((Comment)comment).isHazard()) {
        after.add(0, comment);
        break;
      }
    }
    return getSortedSet(after);
  }

  private static SortedSet<FeedEntry> getSortedSet(List<FeedEntry> after) {
    SortedSet<FeedEntry> children = new TreeSet<FeedEntry>(new FeedEntrySorter(
      SortBy.MOD_TIME_OLDEST_FIRST));
    children.addAll(after);
    return children;
  }

  @Transient
  public String getCommentText() {
    StringBuilder sb = new StringBuilder();
    for(Comment c : getComments()) {
      sb.append(c.getBodyText()).append("\n");
    }
    return sb.toString();
  }

  @Transient
  public String getBodyAndCommentsText() {
    StringBuilder sb = new StringBuilder(getBodyText());
    sb.append("\n\n");
    for(Comment c : getComments()) {
      sb.append(c.getBodyText()).append("\n\n");
    }
    return sb.toString();
  }

  @Transient
  public SortedSet<Comment> getComments() {
    if (comments == null) {
      comments = Comment.newSortedSet();
    }
    return comments;
  }
  public void setComments(SortedSet<Comment> comments) {
    if (comments == null) {
      comments = Comment.newSortedSet();
    }
    this.comments = comments;
    updateComments();
  }

  /**
   * This method should only be called from either newly created EventFeedEntries or EventFeedEntries
   * returned from the FeedEntryDao - it requires the entry's commentAuthorRdbmsRefs to have null UUIDs,
   * or else the commentAuthorRdbmsRefs may be populated with duplicate users
   */
  public void addComment(Comment comment) {
    if (comments == null) {
      comments = Comment.newSortedSet();
    }
    this.comments.add(comment);

    if (!comment.isHazard() && comment.getAuthorRdbmsRef() != null) {
      getCommentAuthorRdbmsRefs().add(comment.getAuthorRdbmsRef());
    }

    updateComments();
  }

  @Override
  public void setParentId(Long parentId) {}

  @Transient
  private Set<String> getCommentAuthorUsernames() {
    Set<String> usernames = Sets.newLinkedHashSet();
    for (UserRef ref : getCommentAuthorRdbmsRefs()) {
      if (ref.getUuid() != null) {
        usernames.add(ref.getUuid());
      }
    }
    return usernames;
  }

  // ================================================================
  // HELPERS
  // ================================================================

  /*
   * Updates the commentCount and modTime fields to keep them consistent
   * with the current set of comments.
   */
  private void updateComments() {
    if (comments == null || comments.size() == 0) {
      this.commentCount = 0;
      this.modTime = pubTime;
    } else {
      this.commentCount = comments.size();
      this.modTime = latest(modTime, comments.last().getPublishedTime());
    }
  }

  /*
   * Returns the latest of the two specified timestamps
   */
  private Timestamp latest(Timestamp t1, Timestamp t2) {
    if (t1 == null) {
      return t2;
    }
    if (t2 == null) {
      return t1;
    }
    return t1.before(t2) ? t2 : t1;
  }

  /*
   * Pre-insert validation (sets timestamps if they aren't already
   * set)
   */
  public void validateForInsert() {
    if (getId() != null) {
      throw new InvalidFeedDataException(TEMPO_ENGINE_ALREADY_PERSISTED, getCategory(), getId());
    }

    boolean postingRestricted = !ConfigurationFactory.getConfiguration(SuiteConfiguration.class).isCollaborationOpen();
    if (postingRestricted) {
      FeedEntryCategory category = getCategory();
      if (FeedEntryCategory.POST.equals(category)) {
        throw new AppianRuntimeException(ErrorCode.FEATURE_TOGGLED_OFF);
      }
      if (FeedEntryCategory.MESSAGE.equals(category) && !isRoleMapLocked) {
        throw new AppianRuntimeException(ErrorCode.FEATURE_TOGGLED_OFF);
      }
    }

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    if (getPublishedTime() == null) {
      setPublishedTime(currentTime);
    } else {
      Validations.validateTimestamp(getPublishedTime());
    }
    if (getModifiedTime() == null) {
      setModifiedTime(getPublishedTime());
    } else {
      Validations.validateTimestamp(getModifiedTime());
    }
    if (getModifiedTimeForSync() == null) {
      setModifiedTimeForSync(currentTime);
    } else {
      Validations.validateTimestamp(getModifiedTimeForSync());
    }
    Validations.validateAuthor(getCategory(), getSingleAuthor());
    Validations.validateBodyText(getCategory(), getBodyText());
    Validations.validateSource(category, getSource());
    Validations.validateActionLabel(getCategory(), getActionLabel());
    Validations.validateActionSummary(getCategory(), getActionSummary());
    Validations.validateActionInstructions(getCategory(), getActionInstructions());
    for (Comment comment : getComments()) {
      comment.validateForInsert(getId());
    }
    if (FeedEntryCategory.SOCIAL_TASK.equals(getCategory())) {
      setTargeted(false);
      setUnsecured(false);
      Validations.validateSocialTaskRecipient(getRecipientUsername());
    }
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EventFeedEntry[");
    builder.append("id=").append(id);
    if (getFeedId() != null) {
      builder.append(", feedId=").append(getFeedId());
    }
    builder.append(", authorId=").append(author == null ? null : author.getRdbmsId());
    if (subscriptionType != null) {
      builder.append(", subscriptionType=").append(subscriptionType);
    }
    builder.append(", isFavorite=").append(isFavorite);
    builder.append(", commentCount=").append(commentCount);
    builder.append(", isTargeted=").append(isTargeted);
    builder.append(", isUnsecured=").append(isUnsecured);
    builder.append(", isRoleMapLocked=").append(isRoleMapLocked);
    if (actionProcessModelUuid != null) {
      builder.append(", actionProcessModelUuid=").append(actionProcessModelUuid);
    }
    if (iconDocUuid != null) {
      builder.append(", iconDocUuid=").append(iconDocUuid);
    }
    if (source != null) {
      builder.append(", source=").append(source);
    }
    if (associatedGroupUuid != null) {
      builder.append(", associatedGroupUuid=").append(associatedGroupUuid);
    }
    if (closedTime != null) {
      builder.append(", closedTime=").append(closedTime);
    }
    builder.append("]");
    return builder.toString();
  }

  // ================================================================
  // Useful methods that operate on collections of entries.
  // ================================================================

  public static List<Long> getIds(Collection<EventFeedEntry> entries) {
    List<Long> ids = new ArrayList<Long>(entries.size());
    for (EventFeedEntry e : entries) {
      ids.add(e.getId());
    }
    return ids;
  }
  public static Set<Long> getFeedIds(Collection<EventFeedEntry> entries) {
    Set<Long> ids = new HashSet<Long>(entries.size());
    for (EventFeedEntry e : entries) {
      if (e.getFeedId() != null) {
        ids.add(e.getFeedId());
      }
    }
    return ids;
  }

  public static final Function<EventFeedEntry, Long> selectId = new Function<EventFeedEntry, Long>() {
    @Override
    public Long apply(EventFeedEntry input) {
      return input.getId();
    }
  };
}
