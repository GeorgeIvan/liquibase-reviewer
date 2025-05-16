package com.appiancorp.usersettings;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "usr_settings")
public final class UserSetting implements Serializable {
  private Long id;
  private String userUuid;
  private String property;
  private String value;

  public UserSetting() {
  }

  public UserSetting(
      String userUuid,
      String property,
      String value) {
    this.userUuid = checkNotNull(userUuid);
    this.property = checkNotNull(property);
    this.value = checkNotNull(value);
  }

  @javax.persistence.Id
  @Column(name = "id", updatable = false)
  @GeneratedValue
  public Long getId() {
    return this.id;
  }

  @SuppressWarnings("unused")
  private void setId(Long id) {
    this.id = id;
  }

  @Column(name = "usr_uuid")
  public String getUserUuid() {
    return this.userUuid;
  }

  @SuppressWarnings("unused")
  private void setUserUuid(String userUuid) {
    this.userUuid = checkNotNull(userUuid);
  }

  @Column(name = "prop")
  public String getProperty() {
    return this.property;
  }

  @SuppressWarnings("unused")
  private void setProperty(String property) {
    this.property = checkNotNull(property);
  }

  @Column(name = "value")
  public String getValue() {
    return this.value;
  }

  @SuppressWarnings("unused")
  private void setValue(String value) {
    this.value = checkNotNull(value);
  }

  @Override
  public String toString() {
    return String.format("[userUuid=%s, property=%s, value=%s]", userUuid, property, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || !(o instanceof UserSetting)) {
      return false;
    }

    UserSetting other = (UserSetting)o;
    return Objects.equals(userUuid, other.userUuid) &&
        Objects.equals(property, other.property) &&
        Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    int result = userUuid.hashCode();
    result = 31 * result + property.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }
}
