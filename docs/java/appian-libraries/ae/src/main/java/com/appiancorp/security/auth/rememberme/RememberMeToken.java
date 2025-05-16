package com.appiancorp.security.auth.rememberme;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.security.user.User;
import com.google.common.base.Strings;

@Entity
@Table(name = "usr_remember_me_token")
public class RememberMeToken implements IRememberMeToken {
  static final String PROP_USER = "user";
  static final String PROP_UPDATED_TS = "updated_ts";
  static final String PROP_SERIES = "series";
  static final String PROP_TOKEN = "token";

  private User user;
  private String series;
  private String token;
  private Timestamp createdTs;
  private Timestamp updatedTs;
  private boolean isLocalAuth;

  protected RememberMeToken() {}

  public RememberMeToken(String username, String series, String token, boolean isLocalAuth) {
    this.user = new User(null, username);
    this.series = series;
    this.token = token;
    this.createdTs = new Timestamp(System.currentTimeMillis());
    this.updatedTs = this.createdTs;
    this.isLocalAuth = isLocalAuth;
  }

  @Transient
  public String getUsername() {
    return user == null ? null : user.getUsername();
  }
  public void setUsername(String username) {
    if (Strings.isNullOrEmpty(username)) {
      user = null;
    } else {
      user = new User(null, username);
    }
  }

  @Column(name="is_local", nullable=false, insertable=true, updatable=false)
  public boolean isLocalAuth() {
    return isLocalAuth;
  }

  @SuppressWarnings("unused")
  private void setLocalAuth(boolean isLocalAuth) {
    this.isLocalAuth = isLocalAuth;
  }

  /* This getter/setter pair must not be exposed publicly, because the data is lazy loaded.*/
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="usr_id", insertable=true, updatable=false, nullable=false)
  User getUser() {
    return user;
  }
  void setUser(User user) {
    this.user = user;
  }

  @javax.persistence.Id
  @Column(name = PROP_SERIES, insertable=true, updatable=false, nullable=false, length=Constants.COL_MAXLEN_INDEXABLE)
  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  @Column(name = PROP_TOKEN, insertable=true, updatable=true, nullable=false, length=Constants.COL_MAXLEN_INDEXABLE)
  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Column(name="created_ts", nullable=false, insertable=true, updatable=false)
  private Long getCreatedTsLong() {
    return createdTs == null ? null : createdTs.getTime();
  }
  @SuppressWarnings("unused")
  private void setCreatedTsLong(Long createdTsLong) {
    this.createdTs = createdTsLong == null ? null : new Timestamp(createdTsLong);
  }
  @Transient
  public Timestamp getCreatedTs() {
    return createdTs;
  }

  @Column(name=PROP_UPDATED_TS, nullable=false, insertable=true, updatable=true)
  private Long getUpdatedTsLong() {
    return updatedTs == null ? null : updatedTs.getTime();
  }
  @SuppressWarnings("unused")
  private void setUpdatedTsLong(Long updatedTsLong) {
    this.updatedTs = updatedTsLong == null ? null : new Timestamp(updatedTsLong);
  }
  @Transient
  public Timestamp getUpdatedTs() {
    return updatedTs;
  }
  public void setUpdatedTs(Timestamp updatedTs) {
    this.updatedTs = updatedTs;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RememberMeToken[user=").append(user.getRdbmsId())
        .append(", series=").append(series)
        .append(", token=").append(token)
        .append(", createdTs=").append(createdTs)
        .append(", updatedTs=").append(updatedTs)
        .append(", isLocalAuth=").append(isLocalAuth)
        .append("]").toString();
  }
}
