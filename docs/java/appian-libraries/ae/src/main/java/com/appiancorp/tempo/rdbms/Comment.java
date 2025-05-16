package com.appiancorp.tempo.rdbms;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.security.user.User;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.tempo.common.AbstractFeedEntry;
import com.appiancorp.tempo.common.FeedEntryCategory;
import com.appiancorp.tempo.common.FeedEntrySorter;
import com.appiancorp.tempo.common.FeedEntryWithAttachments;
import com.appiancorp.tempo.common.InvalidFeedDataException;
import com.appiancorp.tempo.common.SortBy;
import com.appiancorp.type.refs.DocumentRef;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/*
 * Represents a comment or a hazard in the database.  See comments on
 * the BusinessEvent class for more information about the relationship
 * between the two.
 * @author ed.peters
 */
@Entity
@Table(name=Comment.TBL_TEMPO_COMMENT)
public class Comment extends AbstractFeedEntry implements FeedEntryWithAttachments,Serializable {

  public static final String TBL_TEMPO_COMMENT = "tp_comment";
  public static final String PROP_ID = "id";
  public static final String PROP_PARENT_ID = "parentId";
  public static final String PROP_PUB_TIME = "publishedTimeLong";
  public static final String PROP_TYPE = "categoryCode";
  public static final String PROP_AUTHOR = "author";
  private User author;

  public Comment() {
    this(null, null);
  }

  public Comment(String author, String bodyText) {
    this(author, bodyText, false);
  }

  public Comment(String author, String bodyText, List<DocumentRef> fileAttachments) {
    this(author, bodyText, false);
    this.fileAttachments = fileAttachments;
  }

  public Comment(String author, String bodyText, boolean isHazard) {
    this(author, bodyText, isHazard, (Long) null);
  }

  public Comment(String author, String bodyText, boolean isHazard, String parentUniqueId) {
    this(author, bodyText, isHazard, extractParentNumericId(parentUniqueId));
  }

  public Comment(String author, String bodyText, boolean isHazard, String parentUniqueId, List<DocumentRef> fileAttachments) {
    this(author, bodyText, isHazard, extractParentNumericId(parentUniqueId));
    this.fileAttachments = fileAttachments;
  }

  public Comment(String author, String bodyText, boolean isHazard, Long parentId) {
    super(isHazard ? FeedEntryCategory.HAZARD : FeedEntryCategory.COMMENT);
    setSingleAuthor(author);
    setParentId(parentId);
    setBodyText(bodyText);
  }

  // copy-constructor
  public Comment(Comment c) {
    super(c);
    this.author = c.author == null ? null : new User(c.author);
    this.fileAttachments = c.fileAttachments;
  }

  // ==============================================================
  // PERSISTENT PROPS
  // ==============================================================

  @Column(name="id") @Id @GeneratedValue
  @Override
  public Long getId() {
    return super.getId();
  }

  @Column(name="parent_id", nullable=false)
  @Override
  public Long getParentId() {
    return parentId;
  }

  @Column(name="type", nullable=false)
  public byte getCategoryCode() {
    return getCategory().getCode();
  }

  @Column(name="created_ts", nullable=false)
  @Override
  public Long getPublishedTimeLong() {
    return super.getPublishedTimeLong();
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="author_id", insertable=true, updatable=false, nullable=false)
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

  @Column(name="body", length=Validations.MAX_BODYTEXT_LEN)
  @Override
  public String getBodyText() {
    return super.getBodyText();
  }

  public void setCategoryCode(byte code) {
    this.category = FeedEntryCategory.valueOf(code);
  }

  // ==============================================================
  // OTHER ACCESSORS
  // ==============================================================

  @Transient
  public boolean isHazard() {
    return getCategory() == FeedEntryCategory.HAZARD;
  }

  @Override
  @Transient
  public Timestamp getModifiedTime() {
    return getPublishedTime();
  }

  public void setHazard(boolean hazard) {
    category = hazard ? FeedEntryCategory.HAZARD : FeedEntryCategory.COMMENT;
  }

  @Override
  public void setModifiedTime(Timestamp modTime) {
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

  // ================================================================
  // HELPERS
  // ================================================================

  /*
   * Pre-insert validation (updates the published time, if it's not
   * already set)
   */
  public void validateForInsert(Long parentId) {
    if (getId() != null) {
      throw new InvalidFeedDataException(ErrorCode.TEMPO_ENGINE_ALREADY_PERSISTED, getCategory(), getId());
    }
    if (getParentId() != null && !getParentId().equals(parentId)) {
      throw new InvalidFeedDataException(ErrorCode.TEMPO_ENGINE_ALREADY_PARENTED, getId(),getParentId(), parentId);
    }
    if (getPublishedTime() == null) {
      setPublishedTime(new Timestamp(System.currentTimeMillis()));
    } else {
      Validations.validateTimestamp(getPublishedTime());
    }
    Validations.validateAuthor(getCategory(), getSingleAuthor());
    Validations.validateBodyText(getCategory(), getBodyText());
  }

  /*
   * Comments are always sorted by pub time, oldest first
   */
  public static SortedSet<Comment> newSortedSet() {
    return (SortedSet) FeedEntrySorter.newSet(SortBy.PUB_TIME_OLDEST_FIRST);
  }

  public static Long extractParentNumericId(String parentUniqueId) {
    FeedEntryCategory parentCategory = FeedEntryCategory.fromEntryId(parentUniqueId);
    if (!parentCategory.isSupportsChildren()) {
      throw new IllegalArgumentException(
        "Entry being commented on must be either a broadcast, business event or analytic event");
    }
    return parentCategory.extractNumericId(parentUniqueId);
  }

  public static Predicate<Comment> isHazard = new Predicate<Comment>() {
    @Override
    public boolean apply(@Nullable Comment input) {
      return input == null ? false : input.isHazard();
    }
  };

  static final Function<Comment, UserRef> selectAuthorRdbmsRef = new Function<Comment, UserRef>() {
    @Override
    public UserRef apply(Comment input) {
      return input.getAuthorRdbmsRef();
    }
  };

  static final Function<Comment,String> selectAuthorUsername = new Function<Comment, String>() {
    @Override
    public String apply(Comment input) {
      return input.getSingleAuthor();
    }
  };
}
