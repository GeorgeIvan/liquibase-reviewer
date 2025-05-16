package com.appiancorp.security.user;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.appian.core.persist.Constants;

@Entity
@Table(name = "grp_landing_page")
public class GroupLandingPage {
  public static final String PROP_GROUP_ID = "groupId";
  public static final String PROP_ORDER_IDX = "orderIdx";
  public static final String PROP_URL = "url";

  private Long groupId;
  private String url;
  private int orderIdx;

  public GroupLandingPage() {
  }

  public GroupLandingPage(Long groupId, String url, int orderIdx) {
    this.groupId = Objects.requireNonNull(groupId, "Provided Group ID is null");
    this.url = Objects.requireNonNull(url, "Provided url is null");
    this.orderIdx = orderIdx;
  }

  @Column(name = "grp_id", nullable = false)
  @Id
  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  @Column(name = "url", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "order_idx", nullable = false)
  public int getOrderIdx() {
    return orderIdx;
  }

  public void setOrderIdx(int orderIdx) {
    this.orderIdx = orderIdx;
  }

  @Override
  public String toString() {
    return "GroupLandingPage[" + groupId + "=" + url + ", " + orderIdx + "]";
  }

}
