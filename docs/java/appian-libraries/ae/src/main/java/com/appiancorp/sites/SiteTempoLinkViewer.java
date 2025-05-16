package com.appiancorp.sites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.appian.core.persist.Constants;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.type.Id;
import com.appiancorp.type.external.IgnoreJpa;

@Hidden
@Entity
@Table(name = "site_tempo_link_viewers")
@XmlTransient
@IgnoreJpa
public class SiteTempoLinkViewer implements Id<Long> {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String groupUuid;

  public SiteTempoLinkViewer() {}

  public SiteTempoLinkViewer(String groupUuid) {
    this.groupUuid = groupUuid;
  }

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

  @Column(name = "grp_uuid", length = Constants.COL_MAXLEN_UUID)
  @XmlTransient
  public String getGroupUuid() {
    return groupUuid;
  }

  public void setGroupUuid(String groupUuid) {
    this.groupUuid = groupUuid;
  }

  @Override
  public String toString() {
    return "SiteTempoLinkViewer [id=" + id + ", groupUuid=" + groupUuid + "]";
  }
}
