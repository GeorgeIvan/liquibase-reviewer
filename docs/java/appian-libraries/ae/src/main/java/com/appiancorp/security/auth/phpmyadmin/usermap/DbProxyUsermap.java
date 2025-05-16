package com.appiancorp.security.auth.phpmyadmin.usermap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The DbProxyUserMap exists in the primary schema as a mapping from
 * Appian proxied "users" in the database, to the underlying database users.
 * This functionality only applies to Appian Cloud.
 *
 * To support multiple schemas, Appian cloud database users can assume multiple roles,
 * which can take different underlying database users. This table exists to allow users
 * to see their stored procedures.
 *
 * In the cloud database, we can not grant fine-grained control of viewing routine definitions
 * for stored procedures in specific schemas. So, we implement a stored procedure that runs
 * as 'root' and can show a routine definition based on a user's permissions.
 * However, to check a user's permissions, we only have access to the user's proxied user,
 * which is not a real user in the database but merely a concept in Appian's authentication flow.
 *
 * To be able to properly check permissions, we keep a mapping of the proxied users (what we can see
 * from a stored procedure) to the real database users underneath, which is calculated in the
 * phpMyAdmin authentication flow. The real database user's permissions are checked against
 * the permissions in the mysql.db table.
 */
@Entity
@Table(name = "db_proxy_user_map")
public class DbProxyUsermap {

  public static final String PROP_PROXY_USERNAME = "dbProxyUsername";

  private Long id;
  private String dbProxyUsername;
  private String dbUsername;

  public DbProxyUsermap() { }

  public DbProxyUsermap(String dbProxyUsername, String dbUsername) {
    this.dbProxyUsername = dbProxyUsername;
    this.dbUsername = dbUsername;
  }

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "db_proxy_username", nullable = false, unique = true)
  public String getDbProxyUsername() {
    return dbProxyUsername;
  }

  public void setDbProxyUsername(String dbProxyUsername) {
    this.dbProxyUsername = dbProxyUsername;
  }

  @Column(name = "db_username", nullable = false)
  public String getDbUsername() {
    return dbUsername;
  }

  public void setDbUsername(String dbUsername) {
    this.dbUsername = dbUsername;
  }
}
