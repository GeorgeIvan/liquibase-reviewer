package com.appiancorp.rdbms.hb.track;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.dl.core.base.ToStringFunction;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Metadata about an RDBMS transaction. Includes a unique id, the timestamp at which the transaction occurred,
 * and metadata about the changes to tracked objects that occurred as part of the transaction.
 */
@Entity
@Table(name = "tx")
public class RdbmsTxnMetadata {
  public static final String PROP_ID = "id";
  public static final String PROP_TS = "tsLong";
  public static final String PROP_ENTITY_MODS = "entityMods";

  private Long id;
  private Timestamp ts;
  private List<EntityMod> mods;

  public RdbmsTxnMetadata() {
  }

  public RdbmsTxnMetadata(final Timestamp ts, final EntityMod... mods) {
    this(ts, Lists.newArrayList(mods));
  }

  public RdbmsTxnMetadata(final Timestamp ts, final List<EntityMod> mods) {
    this.ts = ts;
    this.mods = new ArrayList<>(mods);
  }

  public RdbmsTxnMetadata(final Timestamp ts, final Collection<EntityMod> mods) {
    this.ts = ts;
    this.mods = new ArrayList<>(mods);
  }

  /**
   * Generate new RdbmsTxnMetadata at current timestamp.
   *
   * @param mods
   */
  public RdbmsTxnMetadata(final Collection<EntityMod> mods) {
    this(new Timestamp(System.currentTimeMillis()), mods);
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public long getId() {
    return id == null ? 0 : id;
  }

  @SuppressWarnings("unused")
  private void setId(long id) {
    this.id = id;
  }

  @Transient
  public Timestamp getTs() {
    return ts;
  }

  @Column(name = "ts", nullable = false)
  private Long getTsLong() {
    return ts == null ? null : ts.getTime();
  }

  @SuppressWarnings("unused")
  private void setTsLong(Long ts) {
    this.ts = ts == null ? null : new Timestamp(ts);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "tx_id", nullable = false)
  @OrderColumn(name = "order_idx", nullable = false)
  @Fetch(FetchMode.SELECT)
  @BatchSize(size = 1000)
  public List<EntityMod> getEntityMods() {
    return mods;
  }

  @SuppressWarnings("unused")
  private void setEntityMods(List<EntityMod> mods) {
    this.mods = mods;
  }

  /**
   * Returns a String that doesn't include any lazy-loaded fields, to avoid loading them unintentionally. This
   * method is safe to call at any time.
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RdbmsTxnMetadata{id=")
        .append(id)
        .append(", ts=")
        .append(ts)
        .append("}");
    return builder.toString();
  }

  /**
   * Returns a String that includes all fields (including fields that may be lazy-loaded). Only call this
   * method when you know that lazy-loaded fields have already been populated.
   */
  public String toStringWithAllFields() {
    StringBuilder builder = new StringBuilder();
    builder.append("RdbmsTxnMetadata{id=")
        .append(id)
        .append(", ts=")
        .append(ts);
    builder.append(", mods=");
    ToStringFunction.append(builder, mods, 3);
    builder.append("}");
    return builder.toString();
  }

  public static Function<RdbmsTxnMetadata,Long> selectId = new Function<RdbmsTxnMetadata,Long>() {
    @Override
    public Long apply(RdbmsTxnMetadata input) {
      return input.getId();
    }
  };
}
