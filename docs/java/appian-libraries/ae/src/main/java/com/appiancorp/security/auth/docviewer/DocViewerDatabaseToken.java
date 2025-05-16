package com.appiancorp.security.auth.docviewer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Database Entity that stores Document Viewer tokens that have been used. The `token_hash` column is marked as unique
 * (see add-doc-viewer-token-table changelog), so attempting to insert a token with the same hash value will result
 * in a failure.
 */
@Entity
@Table(name = DocViewerDatabaseToken.TABLE_NAME)
public class DocViewerDatabaseToken {
  public static final String TABLE_NAME = "doc_viewer_token";
  public static final String ID = "id";
  public static final String TOKEN_HASH = "token_hash";
  public static final String CREATION_TIME = "creation_time";

  private Long id;
  private String tokenHash;
  private Long creationTimeMs;

  // No-arg constructor is used by Hibernate
  public DocViewerDatabaseToken() {
  }

  public DocViewerDatabaseToken(String tokenHash, Long creationTimeMs) {
    this.tokenHash = tokenHash;
    this.creationTimeMs = creationTimeMs;
  }

  @Id
  @GeneratedValue
  @Column(name = ID, updatable = false, nullable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = TOKEN_HASH, updatable = false, nullable = false)
  public String getTokenHash() {
    return tokenHash;
  }

  public void setTokenHash(String tokenHash) {
    this.tokenHash = tokenHash;
  }

  @Column(name = CREATION_TIME, updatable = false, nullable = false)
  public Long getCreationTimeMs() {
    return creationTimeMs;
  }

  public void setCreationTimeMs(Long creationTimeMs) {
    this.creationTimeMs = creationTimeMs;
  }
}
