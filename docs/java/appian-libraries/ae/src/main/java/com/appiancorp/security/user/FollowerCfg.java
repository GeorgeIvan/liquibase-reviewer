package com.appiancorp.security.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Function;
import com.google.common.base.Objects;

@Entity
@Table(name = FollowerCfg.TBL_USR_FOLLOWERS)
public final class FollowerCfg {
  public static final String LOCAL_PART = "FollowerCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String TBL_USR_FOLLOWERS = "usr_followers";
  public static final String COL_USR_ID = "usr_id";
  public static final String COL_USR_FLLWR_ID = "follower_usr_id";

  public static final String PROP_ID = "id";
  public static final String PROP_USER = "user";
  public static final String PROP_FOLLOWER = "follower";

  private Id id = new Id();
  private User user;
  private User follower;

  public FollowerCfg() {
  }

  public FollowerCfg(UserRef user, UserRef follower) {
    this.user = new User(user);
    this.follower = new User(follower);
    this.id.userId = this.user.getRdbmsId();
    this.id.followerId = this.follower.getRdbmsId();
  }

  @EmbeddedId
  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = COL_USR_ID, insertable = false, updatable = false)
  User getUser() {
    return user;
  }

  @SuppressWarnings("unused")
  private void setUser(User user) {
    this.user = user;
  }

  @Transient
  private UserRef getUserRef() {
    return user == null ? null : new UserRefImpl(this.user.getRdbmsId(), null);
  }

  @SuppressWarnings("unused")
  private void setUserRef(UserRef user) {
    this.user = user == null ? null : new User(user);
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = COL_USR_FLLWR_ID, insertable = false, updatable = false)
  User getFollower() {
    return follower;
  }

  @SuppressWarnings("unused")
  private void setFollower(User follower) {
    this.follower = follower;
  }

  @Transient
  private UserRef getFollowerRef() {
    return follower == null ? null : new UserRefImpl(this.follower.getRdbmsId(), null);
  }

  @SuppressWarnings("unused")
  private void setFollowerRef(UserRef follower) {
    this.follower = follower == null ? null : new User(follower);
  }

  @Override
  public int hashCode() {
    // IMPORTANT: use the public getters to avoid triggering hibernate lazy-loading of the data
    return Objects.hashCode(getUserRef(), getFollowerRef());
  }

  @Override
  public String toString() {
    // IMPORTANT: use the public getters to avoid triggering hibernate lazy-loading of the data
    return "Follower [user=" + getUserRef() + ", follower=" + getFollowerRef() + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FollowerCfg)) {
      return false;
    }
    FollowerCfg f = (FollowerCfg)obj;
    // IMPORTANT: use the public getters to avoid triggering hibernate lazy-loading of the data
    return Objects.equal(getUserRef(), f.getUserRef()) && Objects.equal(getFollowerRef(), f.getFollowerRef());
  }

  @Embeddable
  public static final class Id implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PROP_USER_ID = "userId";
    public static final String PROP_FOLLOWER_ID = "followerId";

    private Long userId;
    private Long followerId;

    public Id() {
    }

    public Id(User user, User follower) {
      this.userId = user.getRdbmsId();
      this.followerId = follower.getRdbmsId();
    }

    @Column(name = COL_USR_ID)
    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    @Column(name = COL_USR_FLLWR_ID)
    public Long getFollowerId() {
      return followerId;
    }

    public void setFollowerId(Long followerId) {
      this.followerId = followerId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Id id = (Id)o;
      return userId.equals(id.userId) && followerId.equals(id.followerId);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(userId, followerId);
    }
  }

  // ------------
  // Utils
  // ------------

  public static final Function<FollowerCfg,UserRef> selectFollower = new Function<FollowerCfg,UserRef>() {
    @Override
    public UserRef apply(FollowerCfg input) {
      return input.getFollowerRef();
    }
  };

  public static final Function<FollowerCfg,UserRef> selectFollowing = new Function<FollowerCfg,UserRef>() {
    @Override
    public UserRef apply(FollowerCfg input) {
      return input.getUserRef();
    }
  };
}
