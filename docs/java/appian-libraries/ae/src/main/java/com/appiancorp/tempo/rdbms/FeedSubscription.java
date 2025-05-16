package com.appiancorp.tempo.rdbms;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appiancorp.security.user.User;
import com.google.common.collect.ImmutableMap;

@Entity @Table(name=FeedSubscription.TBL_TEMPO_FEED_USR_SUBS)
public class FeedSubscription implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String TBL_TEMPO_FEED_USR_SUBS = "tp_feed_usr_subs";

  public static final String COL_FEED_ID = "feed_id";
  public static final String COL_USR_ID = "usr_id";
  public static final String COL_TYPE = "type";

  public static final String PROP_ID = "id";
  public static final String PROP_FEED = "feed";
  public static final String PROP_USER = "user";
  public static final String PROP_TYPE = "typeByteValue";

  private Id id = new Id();
  private Feed feed;
  private User user;
  private Type type;

  public FeedSubscription() {}
  public FeedSubscription(Feed feed, User user, Type type) {
    this(feed, type);
    this.user = user;
    this.id.userId = user.getRdbmsId();
  }
  public FeedSubscription(Feed feed, Type type) {
    this.feed = feed;
    this.type = type;
    this.id.feedId = feed.getId();
  }

  @EmbeddedId
  public Id getId() {
    return id;
  }
  public void setId(Id id) {
    this.id = id;
  }

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_FEED_ID, insertable=false, updatable=false)
  public Feed getFeed() {
    return feed;
  }
  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  /* This property is private to prevent accidental unnecessary loading of the data. */
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name=COL_USR_ID, insertable=false, updatable=false)
  @SuppressWarnings("unused")
  private User getUser() {
    return user;
  }
  @SuppressWarnings("unused")
  private void setUser(User user) {
    this.user = user;
  }

  @Transient
  public Type getType() {
    return type;
  }
  public void setType(Type type) {
    this.type = type;
  }
  @SuppressWarnings("unused")
  @Column(name=COL_TYPE)
  private byte getTypeByteValue() {
    return type == null ? 0 : type.getByteValue();
  }
  @SuppressWarnings("unused")
  private void setTypeByteValue(byte typeByteValue) {
    this.type = Type.valueOfByte(typeByteValue);
  }

  @Override
  public String toString() {
    return "FeedSubscription[" + id + ", type=" + type + "]";
  }


  public static enum Type {
    OPT_OUT((byte)1),
    OPT_IN_PERSONALIZED((byte)2);

    private final byte byteValue;
    private Type(byte byteValue) {
      this.byteValue = byteValue;
    }
    public byte getByteValue() {
      return byteValue;
    }
    public static Type valueOfByte(byte byteValue) {
      Type t = BYTE_TO_ENUM.get(byteValue);
      if (t != null) {
        return t;
      } else {
        throw new IllegalArgumentException("Invalid byte value " + byteValue+". Valid values: "+BYTE_TO_ENUM);
      }
    }
    private static final Map<Byte,Type> BYTE_TO_ENUM;
    static {
      ImmutableMap.Builder<Byte,Type> b = ImmutableMap.builder();
      for (Type t : Type.values()) {
        b.put(t.getByteValue(), t);
      }
      BYTE_TO_ENUM = b.build();
    }
  }

  @Embeddable
  public static class Id implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PROP_FEED_ID = "feedId";
    public static final String PROP_USER_ID = "userId";

    private Long feedId;
    private Long userId;

    public Id() {}

    public Id(Feed feed, User user) {
      this(feed.getId(), (Long)user.getId());
    }
    public Id(Long feedId, Long userId) {
      super();
      this.feedId = feedId;
      this.userId = userId;
    }

    @Column(name=COL_FEED_ID)
    public Long getFeedId() {
      return feedId;
    }
    public void setFeedId(Long feedId) {
      this.feedId = feedId;
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
      if (feedId == null) {
        if (other.feedId != null)
          return false;
      } else if (!feedId.equals(other.feedId))
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
      result = prime * result + ((feedId == null) ? 0 : feedId.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
      return result;
    }

    @Override
    public String toString() {
      return "feedId=" + feedId + ", userId=" + userId;
    }
  }

  // ================================================================
  // Useful methods that operate on collections of feed subscriptions.
  // ================================================================

  public static Set<Feed> getFeeds(Collection<? extends FeedSubscription> subs) {
    return getFeeds(subs, null);
  }
  public static Set<Feed> getFeeds(Collection<? extends FeedSubscription> subs, Type targetType) {
    if (subs == null || subs.isEmpty()) {
      return Collections.<Feed>emptySet();
    }
    Set<Feed> feeds = new LinkedHashSet<Feed>(subs.size());
    for (FeedSubscription s : subs) {
      if (targetType == null || s.getType() == targetType) {
        feeds.add(s.getFeed());
      }
    }
    return feeds;
  }

  public static Set<Long> getFeedIds(Collection<? extends FeedSubscription> subs) {
    return getFeedIds(subs, null);
  }
  public static Set<Long> getFeedIds(Collection<? extends FeedSubscription> subs, Type targetType) {
    if (subs == null || subs.isEmpty()) {
      return Collections.<Long>emptySet();
    }
    Set<Long> ids = new LinkedHashSet<Long>(subs.size());
    for (FeedSubscription s : subs) {
      if (targetType == null || s.getType() == targetType) {
        ids.add(s.getId().getFeedId());
      }
    }
    return ids;
  }

  public static Map<Long,FeedSubscription> getFeedIdToSubscriptionMap(Collection<? extends FeedSubscription> subs) {
    if (subs == null || subs.isEmpty()) {
      return Collections.<Long,FeedSubscription>emptyMap();
    }
    Map<Long,FeedSubscription> m = new LinkedHashMap<Long,FeedSubscription>(subs.size());
    for (FeedSubscription s : subs) {
      Long feedId = s.getId().getFeedId();
      if (m.containsKey(feedId)) {
        throw new IllegalArgumentException("The given collection of subscriptions contained multiple" +
          " subscription objects for the same feed id. s1="+m.get(feedId)+", s2="+s);
      }
      m.put(feedId, s);
    }
    return m;
  }
}
