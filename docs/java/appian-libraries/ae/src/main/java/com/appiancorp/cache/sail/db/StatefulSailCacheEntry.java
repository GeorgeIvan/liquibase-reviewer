package com.appiancorp.cache.sail.db;

import static com.appiancorp.cache.sail.db.StatefulSailCacheEntry.STATEFUL_SAIL_CACHE_TABLE_NAME;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.appiancorp.cache.DatabaseAuxiliaryCache.DatabaseCacheEntry;

@Entity @Table(name=STATEFUL_SAIL_CACHE_TABLE_NAME)
public class StatefulSailCacheEntry extends DatabaseCacheEntry {
  public static final String STATEFUL_SAIL_CACHE_TABLE_NAME = "sail_state_cache";

  public StatefulSailCacheEntry() {
    super();
  }

  public StatefulSailCacheEntry(String key, byte[] serializedEntry) {
    super(key, serializedEntry);
  }

}
