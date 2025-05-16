package com.appiancorp.tempo.rdbms;

import static com.appiancorp.common.collect.Collections3.transformIntoNewArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import com.appian.core.base.Classes;
import com.appiancorp.suiteapi.knowledge.DocumentDataType;
import com.appiancorp.type.refs.DocumentRef;
import com.appiancorp.type.refs.DocumentRefImpl;
import com.appiancorp.type.refs.RecordReferenceDataType;
import com.appiancorp.type.refs.RecordReferenceRef;
import com.appiancorp.type.refs.RecordReferenceRefImpl;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "tp_feed_entry_linked_obj")
public class FeedEntryLinkedObject extends LinkedObject {
  public static final String PROP_FEED_ENTRY_ID = "feedEntryId";

  private Long feedEntryId;
  private Long commentId;

  // for JAXB and Hibernate
  private FeedEntryLinkedObject() {
    super();
  }
  /**
   * To create an instance, use one of the static builder methods: {@link #toRecordLinkedObject()},
   * {@link #toFileAttachment()}, etc.
   */
  private FeedEntryLinkedObject(ObjectSet objectSet, String objectTypeStr, String objectIdStr) {
    super(objectSet.getByteValue(), objectTypeStr, objectIdStr);
  }

  private FeedEntryLinkedObject(ObjectSet objectSet, String objectTypeStr, String objectIdStr, Long commentId) {
    super(objectSet.getByteValue(), objectTypeStr, objectIdStr);
    this.commentId = commentId;
  }

  private FeedEntryLinkedObject(FeedEntryLinkedObject base) {
    this(base.getObjectSet(),base.getTargetTypeStr(),base.getTargetIdStr(), base.getCommentId());
  }

  @Transient
  public ObjectSet getObjectSet() {
    return ObjectSet.valueOfByte(super.getObjectSetKey());
  }
  public void setObjectSet(ObjectSet objSet) {
    super.setObjectSetKey(objSet.getByteValue());
  }

  /* This column is marked as non-insertable and non-updatable, because the writing is controlled by the
   * @JoinColumn annotation on EventFeedEntry.getLinkedObjects(). */
  @Column(name = "tp_feed_entry_id", nullable = false, insertable = false, updatable = false)
  public Long getFeedEntryId() {
    return feedEntryId;
  }
  @SuppressWarnings("unused")
  private void setFeedEntryId(Long feedEntryId) {
    this.feedEntryId = feedEntryId;
  }

  @Column(name = "tp_comment_id", nullable = true)
  public Long getCommentId() {
    return commentId;
  }
  @SuppressWarnings("unused")
  private void setCommentId(Long commentId) {
    this.commentId = commentId;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Classes.simpleName(getClass()))
        .append("[entryId=").append(feedEntryId)
        .append(", id=").append(getId())
        .append(", objectSetKey=").append(getObjectSetKey())
        .append(", objectTypeStr=").append(getTargetTypeStr())
        .append(", objectIdStr=").append(getTargetIdStr())
        .append(", orderIdx=").append(getOrderIdx())
        .append(", commentId=").append(commentId)
        .append("]");
    return sb.toString();
  }

  public static enum ObjectSet {
    records((byte)1), fileAttachments((byte)2);

    private final byte byteValue;
    private ObjectSet(byte byteValue) {
      this.byteValue = byteValue;
    }
    public byte getByteValue() {
      return byteValue;
    }
    public static ObjectSet valueOfByte(byte byteValue) {
      ObjectSet enumValue = BYTE_TO_ENUM.get(byteValue);
      if (enumValue != null) {
        return enumValue;
      } else {
        throw new IllegalArgumentException("Invalid byte value " + byteValue+". Valid values: "+BYTE_TO_ENUM);
      }
    }
    private static final Map<Byte,ObjectSet> BYTE_TO_ENUM;
    static {
      ImmutableMap.Builder<Byte,ObjectSet> b = ImmutableMap.builder();
      for (ObjectSet enumValue : ObjectSet.values()) {
        b.put(enumValue.getByteValue(), enumValue);
      }
      BYTE_TO_ENUM = b.build();
    }
  }

  public static Predicate<FeedEntryLinkedObject> hasObjectSet(final ObjectSet targetObjectSet) {
    return new Predicate<FeedEntryLinkedObject>() {
      @Override
      public boolean apply(FeedEntryLinkedObject input) {
        return input.getObjectSet() == targetObjectSet;
      }
    };
  }

  // =============================================================
  // Related records
  // =============================================================

  public static FeedEntryLinkedObject newLinkedObjectFromRecord(RecordReferenceRef recordRef) {
    return toRecordLinkedObject().apply(recordRef);
  }
  public static List<FeedEntryLinkedObject> newLinkedObjectsFromRecords(Collection<RecordReferenceRef> recordRefs) {
    return recordRefs == null ? Lists.<FeedEntryLinkedObject>newArrayList() :
      transformIntoNewArrayList(recordRefs, toRecordLinkedObject());
  }
  private static Function<RecordReferenceRef,FeedEntryLinkedObject> toRecordLinkedObject() {
    return toRecordLinkedObject;
  }
  private static Function<RecordReferenceRef,FeedEntryLinkedObject> toRecordLinkedObject = new Function<RecordReferenceRef,FeedEntryLinkedObject>() {
    @Override
    public FeedEntryLinkedObject apply(RecordReferenceRef input) {
      String id = input.getId();
      Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "record id cannot be null");
      return new FeedEntryLinkedObject(ObjectSet.records, RECORD_DT_QNAME_STR, id);
    }
  };
  private static final String RECORD_DT_QNAME_STR = new QName(RecordReferenceDataType.NAMESPACE, RecordReferenceDataType.LOCAL_PART).toString();

  public static List<RecordReferenceRef> getRecordTags(Collection<FeedEntryLinkedObject> linkedObjects) {
    List<RecordReferenceRef> recordRefs = Lists.newArrayList();
    if (linkedObjects != null) {
      for (FeedEntryLinkedObject lo : linkedObjects) {
        if (lo.getObjectSet() == ObjectSet.records) {
          recordRefs.add(new RecordReferenceRefImpl(lo.getTargetIdStr(), null));
        }
      }
    }
    return recordRefs;
  }

  // =============================================================
  // File attachments
  // =============================================================

  public static List<FeedEntryLinkedObject> newLinkedObjects(List<FeedEntryLinkedObject> base){
    if(null==base || base.isEmpty()) { return Collections.<FeedEntryLinkedObject>emptyList(); }
    ArrayList<FeedEntryLinkedObject> result = Lists.<FeedEntryLinkedObject>newArrayList();
    for(FeedEntryLinkedObject lo:base) {
      result.add(new FeedEntryLinkedObject(lo));
    }
    return result;
  }

  public static FeedEntryLinkedObject newFileAttachment(DocumentRef docRef) {
    return toFileAttachment().apply(docRef);
  }
  public static List<FeedEntryLinkedObject> newFileAttachments(Collection<DocumentRef> docRefs) {
    return docRefs == null ? Lists.<FeedEntryLinkedObject>newArrayList() :
      transformIntoNewArrayList(docRefs, toFileAttachment());
  }
  private static Function<DocumentRef,FeedEntryLinkedObject> toFileAttachment() {
    return toFileAttachment;
  }
  private static Function<DocumentRef,FeedEntryLinkedObject> toFileAttachment = new Function<DocumentRef,FeedEntryLinkedObject>() {
    @Override
    public FeedEntryLinkedObject apply(DocumentRef input) {
      String uuid = input.getUuid();
      Preconditions.checkArgument(!Strings.isNullOrEmpty(uuid), "document uuid cannot be null");
      return new FeedEntryLinkedObject(ObjectSet.fileAttachments, DOC_DT_QNAME_STR, uuid);
    }
  };
  private static final String DOC_DT_QNAME_STR = new QName(DocumentDataType.NAMESPACE, DocumentDataType.LOCAL_PART).toString();

  public static List<DocumentRef> getFileAttachments(Collection<FeedEntryLinkedObject> linkedObjects) {
    List<DocumentRef> docRefs = Lists.newArrayList();
    if (linkedObjects != null) {
      for (FeedEntryLinkedObject lo : linkedObjects) {
        if (lo.getObjectSet() == ObjectSet.fileAttachments) {
          docRefs.add(new DocumentRefImpl(null, lo.getTargetIdStr()));
        }
      }
    }
    return docRefs;
  }

  public static List<DocumentRef> getFeedEntryFileAttachments(Collection<FeedEntryLinkedObject> linkedObjects) {
    List<DocumentRef> docRefs = Lists.newArrayList();
    if (linkedObjects != null) {
      for (FeedEntryLinkedObject lo : linkedObjects) {
        if (null==lo.getCommentId() && lo.getObjectSet() == ObjectSet.fileAttachments) {
          docRefs.add(new DocumentRefImpl(null, lo.getTargetIdStr()));
        }
      }
    }
    return docRefs;
  }

  public static Map<Long,List<DocumentRef>> getCommentFeedEntryFileAttachments(Collection<FeedEntryLinkedObject> linkedObjects) {
    Map<Long,List<DocumentRef>> commentsAtts = Maps.newLinkedHashMap();
    if (linkedObjects != null) {
      for (FeedEntryLinkedObject lo : linkedObjects) {
        if (null!=lo.getCommentId() && lo.getObjectSet() == ObjectSet.fileAttachments) {
          List<DocumentRef> refs = commentsAtts.get(lo.getCommentId());
          if(null==refs) {
            refs = Lists.newArrayList();
            commentsAtts.put(lo.getCommentId(), refs);
          }
          refs.add(new DocumentRefImpl(null, lo.getTargetIdStr()));
        }
      }
    }
    return commentsAtts;
  }

  public static FeedEntryLinkedObject newCommentFileAttachment(Long commentId, DocumentRef docRef) {
    String uuid = docRef.getUuid();
    Preconditions.checkArgument(!Strings.isNullOrEmpty(uuid), "document uuid cannot be null");
    return new FeedEntryLinkedObject(ObjectSet.fileAttachments, DOC_DT_QNAME_STR, uuid,commentId);
  }

  public static List<FeedEntryLinkedObject> newCommentFileAttachments(Long commentId, List<DocumentRef> docRefs) {
    if(null == docRefs) { return Collections.<FeedEntryLinkedObject>emptyList(); }
    List<FeedEntryLinkedObject> los = Lists.newArrayListWithExpectedSize(docRefs.size());
    for(DocumentRef ref:docRefs) {
      los.add(newCommentFileAttachment(commentId,ref));
    }
    return los;
  }

}
