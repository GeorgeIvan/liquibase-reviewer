package com.appiancorp.enduserreporting.entities;

import static com.appiancorp.security.user.User.JOIN_COL_USR_ID;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.appiancorp.enduserreporting.persistence.SsaLibraryObjectCfg;
import com.appiancorp.enduserreporting.persistence.SsaLibraryObjectUsrFavorite;
import com.appiancorp.security.user.User;

@Entity
@Table(name = "ssa_library_object_usr_fav")
public class SsaLibraryObjectUsrFavoriteImpl implements SsaLibraryObjectUsrFavorite {
  private Long id;
  private SsaLibraryObjectCfgImpl ssaLibraryObjectCfg;
  private User usr;

  public SsaLibraryObjectUsrFavoriteImpl() {}

  public SsaLibraryObjectUsrFavoriteImpl(SsaLibraryObjectCfgImpl ssaLibraryObjectCfg, User usr) {
    this.ssaLibraryObjectCfg = ssaLibraryObjectCfg;
    this.usr = usr;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ssa_library_object_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  public SsaLibraryObjectCfgImpl getSsaLibraryObject() {
    return ssaLibraryObjectCfg;
  }

  public void setSsaLibraryObject(SsaLibraryObjectCfg ssaLibraryObjectCfg) {
    if (ssaLibraryObjectCfg instanceof SsaLibraryObjectCfgImpl) {
      this.ssaLibraryObjectCfg = (SsaLibraryObjectCfgImpl)ssaLibraryObjectCfg;
    } else {
      throw new IllegalArgumentException(
          "setSsaLibraryObject method requires an instance of SsaLibraryObjectCfgImpl");
    }
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = JOIN_COL_USR_ID, nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  public User getUsr() {
    return usr;
  }

  public void setUsr(User usr) {
    this.usr = usr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SsaLibraryObjectUsrFavoriteImpl that = (SsaLibraryObjectUsrFavoriteImpl)o;
    return Objects.equals(id, that.getId()) && Objects.equals(ssaLibraryObjectCfg, that.ssaLibraryObjectCfg)
        && Objects.equals(usr, that.usr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ssaLibraryObjectCfg, usr);
  }

  @Override
  public String toString() {
    return "SsaLibraryObjectUsrFavorite{" + "id=" + id + ", ssaLibraryObjectCfg='" +
        ssaLibraryObjectCfg.toString() + ", usr='" + usr.toString() + '}';
  }
}
