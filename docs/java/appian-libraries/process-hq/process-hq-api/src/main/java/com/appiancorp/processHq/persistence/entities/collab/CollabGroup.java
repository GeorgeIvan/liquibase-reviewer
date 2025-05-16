package com.appiancorp.processHq.persistence.entities.collab;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "collab_group")
public class CollabGroup {

  private long id;
  private List<CollabComment> collabComments;

  public CollabGroup() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @OneToMany(mappedBy = "collabGroup", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  public List<CollabComment> getCollabComments() {
    return collabComments;
  }

  public void setCollabComments(List<CollabComment> collabComments) {
    this.collabComments = collabComments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CollabGroup)) {
      return false;
    }
    CollabGroup that = (CollabGroup)o;
    return id == that.id && Objects.equals(collabComments, that.getCollabComments());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, collabComments);
  }

  @Override
  public String toString() {
    return "CollabGroup{" + "id=" + id + ", collabComment=" + collabComments + '}';
  }

}
