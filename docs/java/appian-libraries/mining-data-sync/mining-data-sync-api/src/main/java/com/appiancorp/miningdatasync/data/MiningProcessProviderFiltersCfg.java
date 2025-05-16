package com.appiancorp.miningdatasync.data;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mining_prc_prv_filters_cfg")
public class MiningProcessProviderFiltersCfg {
  public static final String PROP_FILTERS_CFG_JSON = "filtersConfigJson";
  private Long id;
  private MiningProcessProvider miningProcessProvider;
  private String filtersConfigJson;

  /**
   * For Hibernate only
   */
  @SuppressWarnings("unused")
  public MiningProcessProviderFiltersCfg() {
  }

  public MiningProcessProviderFiltersCfg(
      MiningProcessProvider miningProcessProvider,
      String filtersConfigJson) {
    this.miningProcessProvider = miningProcessProvider;
    this.filtersConfigJson = filtersConfigJson;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mining_process_provider_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
  public MiningProcessProvider getMiningProcessProvider() {
    return miningProcessProvider;
  }

  public void setMiningProcessProvider(MiningProcessProvider miningProcessProvider) {
    this.miningProcessProvider = miningProcessProvider;
  }

  @Column(name = "filters_cfg_json", nullable = false)
  @Lob
  public String getFiltersConfigJson() {
    return filtersConfigJson;
  }

  public void setFiltersConfigJson(String filtersConfigJson) {
    this.filtersConfigJson = filtersConfigJson;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, miningProcessProvider, filtersConfigJson);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningProcessProviderFiltersCfg other = (MiningProcessProviderFiltersCfg)o;
    return filtersConfigJson.equals(other.getFiltersConfigJson());
  }

  @Override
  public String toString() {
    return "MiningProcessProviderFiltersCfg [id=" + id + ", filtersConfigJson=" + filtersConfigJson + "]";
  }
}
