package com.appiancorp.processHq.persistence.entities.collab;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.appian.core.persist.Constants;

@Entity
@Table(name = "collab_comment")
public class CollabComment {

  private long id;
  private CollabGroup collabGroup;
  private String createdByUuid;
  private long createdTs;
  private String body;
  private boolean isDeleted;
  private List<CollabAttachment> collabAttachments;

  public CollabComment() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collab_group_id", nullable = false)
  public CollabGroup getCollabGroup() {
    return collabGroup;
  }

  public void setCollabGroup(CollabGroup collabGroup) {
    this.collabGroup = collabGroup;
  }

  @Column(name = "created_by_uuid", length = Constants.COL_MAXLEN_UUID)
  public String getCreatedByUuid() {
    return createdByUuid;
  }

  public void setCreatedByUuid(String createdByUuid) {
    this.createdByUuid = createdByUuid;
  }

  @Column(name = "created_ts", nullable = false)
  public long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(long createdTs) {
    this.createdTs = createdTs;
  }

  @Column(name = "body")
  @Lob
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Column(name = "is_deleted")
  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  @OneToMany(mappedBy = "collabComment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy
  public List<CollabAttachment> getCollabAttachments() {
    return collabAttachments;
  }

  public void setCollabAttachments(List<CollabAttachment> collabAttachments) {
    this.collabAttachments = collabAttachments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CollabComment)) {
      return false;
    }
    CollabComment that = (CollabComment)o;
    return id == that.getId() && collabGroup.equals(that.getCollabGroup()) &&
        Objects.equals(createdByUuid, that.getCreatedByUuid()) && Objects.equals(createdTs, that.getCreatedTs()) &&
        Objects.equals(body, that.getBody()) && Objects.equals(isDeleted, that.getIsDeleted()) &&
        Objects.equals(collabAttachments, that.getCollabAttachments());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, collabGroup, createdByUuid, createdTs, body, isDeleted, collabAttachments);
  }

  @Override
  public String toString() {
    return "CollabComment{" + "id=" + id + ", collabGroup=" + collabGroup.getId() +
        ", createdByUuid='" + createdByUuid + '\'' + ", createdTs=" + createdTs +
        ", body=" + body + ", isDeleted=" + isDeleted +
        ", numCollabAttachments=" + collabAttachments.size() +'}';
  }

}
