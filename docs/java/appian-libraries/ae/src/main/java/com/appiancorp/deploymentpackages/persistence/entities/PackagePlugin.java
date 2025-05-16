package com.appiancorp.deploymentpackages.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import com.appiancorp.deploymentpackages.persistence.service.PackagePluginBuilder;
import com.google.common.base.MoreObjects;

@Entity
@Table(name="dpkg_plugin")
public class PackagePlugin {
  private Long id;
  private String pluginKey;
  private Long packageId;

  /** for Hibernate to call during queries */
  PackagePlugin() {}

  public PackagePlugin(PackagePluginBuilder builder) {
    this.id = builder.getId();
    this.packageId = builder.getPackageId();
    this.pluginKey = builder.getPluginKey();
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

  @Column(name = "plugin_key", nullable = false)
  public String getPluginKey() {
    return pluginKey;
  }

  public void setPluginKey(String pluginKey) {
    this.pluginKey = pluginKey;
  }

  @Column(name = "package_id", nullable = false, insertable = false, updatable = false )
  public Long getPackageId() {
    return packageId;
  }

  void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("pluginKey", pluginKey)
        .add("packageId", packageId)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagePlugin that = (PackagePlugin)o;
    return Objects.equals(pluginKey, that.pluginKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pluginKey);
  }
}
