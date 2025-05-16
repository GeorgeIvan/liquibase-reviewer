package com.appiancorp.processHq.persistence.entities.collab;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "collab_attachment")
public class CollabAttachment {

  private long id;
  private CollabComment collabComment;
  private long attachmentDocId;

  public CollabAttachment() {}

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "collab_comment_id", nullable = false)
  public CollabComment getCollabComment() {
    return collabComment;
  }

  public void setCollabComment(CollabComment collabComment) {
    this.collabComment = collabComment;
  }

  @Column(name = "attachment_doc_id", nullable = false)
  public long getAttachmentDocId() {
    return attachmentDocId;
  }

  public void setAttachmentDocId(long attachmentDocId) {
    this.attachmentDocId = attachmentDocId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CollabAttachment)) {
      return false;
    }
    CollabAttachment that = (CollabAttachment)o;
    return id == that.getId() && collabComment.equals(that.getCollabComment()) &&
        Objects.equals(attachmentDocId, that.getAttachmentDocId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, collabComment, attachmentDocId);
  }

  @Override
  public String toString() {
    return "CollabAttachment{" + "id=" + id + ", collabComment=" + collabComment.toString() +
        ", attachmentDocId='" + attachmentDocId + '}';
  }

}
