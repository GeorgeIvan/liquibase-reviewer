package com.appiancorp.tempo.rdbms;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appiancorp.security.user.User;

@Entity @Table(name=FeedEntryFavorite.TBL_TEMPO_FEED_ENTRY_USR_FAVS)
public class FeedEntryFavorite implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String TBL_TEMPO_FEED_ENTRY_USR_FAVS = "tp_feed_entry_usr_favs";
  public static final String COL_FEED_ENTRY_ID = "tp_feed_entry_id";
  public static final String COL_USR_ID = "usr_id";

  public static final String PROP_ID = "id";
  public static final String PROP_FEED_ENTRY = "feedEntry";
  public static final String PROP_USER = "user";

  private Id id = new Id();
  private EventFeedEntry feedEntry;
  private User user;

  public FeedEntryFavorite() {}
  public FeedEntryFavorite(EventFeedEntry feedEntry, User user) {
    this(feedEntry);
    this.user = user;
    this.id.userId = (Long)user.getId();
  }
  public FeedEntryFavorite(EventFeedEntry feedEntry) {
    this.feedEntry = feedEntry;
    this.id.feedEntryId = feedEntry.getId();
  }

  @EmbeddedId
  public Id getId() {
    return id;
  }
  public void setId(Id id) {
    this.id = id;
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_FEED_ENTRY_ID, insertable=false, updatable=false)
  public EventFeedEntry getFeedEntry() {
    return feedEntry;
  }
  public void setFeedEntry(EventFeedEntry feedEntry) {
    this.feedEntry = feedEntry;
  }

  /* This property is private to prevent accidental unnecessary loading of the data. */
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_USR_ID, insertable=false, updatable=false)
  private User getUser() {
    return user;
  }
  @SuppressWarnings("unused")
  private void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "FeedEntryFavorite[" + id + "]";
  }

  @Embeddable
  public static class Id implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PROP_FEED_ENTRY_ID = "feedEntryId";
    public static final String PROP_USER_ID = "userId";

    private Long feedEntryId;
    private Long userId;

    public Id() {}

    public Id(EventFeedEntry feedEntry, User user) {
      this(feedEntry.getId(), (Long)user.getId());
    }
    public Id(Long feedEntryId, Long userId) {
      super();
      this.feedEntryId = feedEntryId;
      this.userId = userId;
    }

    @Column(name=COL_FEED_ENTRY_ID)
    public Long getFeedEntryId() {
      return feedEntryId;
    }
    public void setFeedEntryId(Long feedEntryId) {
      this.feedEntryId = feedEntryId;
    }
    @Column(name=COL_USR_ID)
    public Long getUserId() {
      return userId;
    }
    public void setUserId(Long userId) {
      this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Id other = (Id) obj;
      if (feedEntryId == null) {
        if (other.feedEntryId != null)
          return false;
      } else if (!feedEntryId.equals(other.feedEntryId))
        return false;
      if (userId == null) {
        if (other.userId != null)
          return false;
      } else if (!userId.equals(other.userId))
        return false;
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((feedEntryId == null) ? 0 : feedEntryId.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
      return result;
    }

    @Override
    public String toString() {
      return "feedEntryId=" + feedEntryId + ", userId=" + userId;
    }
  }

  // ================================================================
  // Useful methods that operate on collections of favorites.
  // ================================================================

  public static Set<EventFeedEntry> getEntries(Collection<? extends FeedEntryFavorite> favs) {
    if (favs == null || favs.isEmpty()) {
      return Collections.<EventFeedEntry>emptySet();
    }
    Set<EventFeedEntry> feedEntries = new LinkedHashSet<EventFeedEntry>(favs.size());
    for (FeedEntryFavorite fav : favs) {
      feedEntries.add(fav.getFeedEntry());
    }
    return feedEntries;
  }

  public static Set<Long> getEntryIds(Collection<? extends FeedEntryFavorite> favs) {
    if (favs == null || favs.isEmpty()) {
      return Collections.<Long>emptySet();
    }
    Set<Long> ids = new LinkedHashSet<Long>(favs.size());
    for (FeedEntryFavorite f : favs) {
      ids.add(f.getId().getFeedEntryId());
    }
    return ids;
  }
}
