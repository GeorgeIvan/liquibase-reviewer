package com.appiancorp.rdbms.hb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="generic_dao_test_entity")
public class CrossDbTestEntity {
  public static final CrossDbTestEntity SHARED_ENTITY = new CrossDbTestEntity(5, "hello");

  @Id @Column(name="id")
  private Integer id;

  @Column(name="name")
  private String name;

  public CrossDbTestEntity() {}

  public CrossDbTestEntity(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
