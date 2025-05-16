package com.appiancorp.processHq.persistence.entities.customKpi;

import static com.appiancorp.processHq.persistence.entities.customKpi.MiningKpi.COL_MINING_KPI_ID;
import static com.appiancorp.processHq.persistence.entities.customKpi.MiningKpiSubscriber.SUBSCRIBER_TABLE;
import static com.appiancorp.security.user.User.JOIN_COL_USR_ID;

import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.appiancorp.security.user.User;

@Entity
@Table(name = SUBSCRIBER_TABLE)
public class MiningKpiSubscriber {
  public static final String SUBSCRIBER_TABLE = "mining_kpi_subscriber";

  private MiningKpiSubscriberId id;
  private MiningKpi miningKpi;
  private User subscriber;

  public MiningKpiSubscriber() {}

  @EmbeddedId
  public MiningKpiSubscriberId getId() {
    return id;
  }

  public void setId(MiningKpiSubscriberId id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = COL_MINING_KPI_ID, nullable = false, insertable = false, updatable = false)
  public MiningKpi getMiningKpi() {
    return miningKpi;
  }

  public void setMiningKpi(MiningKpi miningKpi) {
    this.miningKpi = miningKpi;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = JOIN_COL_USR_ID, nullable = false, insertable = false, updatable = false)
  public User getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(User subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MiningKpiSubscriber that = (MiningKpiSubscriber)o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
