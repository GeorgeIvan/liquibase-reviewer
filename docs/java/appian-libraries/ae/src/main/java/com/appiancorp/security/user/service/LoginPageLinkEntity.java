package com.appiancorp.security.user.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "login_page_links")
public class LoginPageLinkEntity {
  public static final String PROP_INDEX= "index";

  public static final int MAX_URL_LENGTH = 2_038;
  public static final int MAX_TEXT_LENGTH = 100;

  private int index;
  private String text;
  private String link;

  public LoginPageLinkEntity() {}

  public LoginPageLinkEntity(int index, String text, String link) {
    this.index = index;
    this.text = text;
    this.link = link;
  }

  @Column(name = "order_idx", nullable = false)
  @Id
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Column(name = "text", nullable = false, length = MAX_TEXT_LENGTH)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Column(name = "link", nullable = false, length = MAX_URL_LENGTH)
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public String toString() {
    return "LoginPageLinkEntity{" + "index=" + index + ", text='" + text + '\'' + ", link='" + link + '\'' +
        '}';
  }
}
