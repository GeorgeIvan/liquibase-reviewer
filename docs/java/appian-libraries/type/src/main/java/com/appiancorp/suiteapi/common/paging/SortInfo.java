package com.appiancorp.suiteapi.common.paging;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.type.Constants;
import com.appiancorp.suiteapi.common.Preview;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;

/**
 * This class is available as a preview of functionality that will be added to
 * the supported public API in a future release. While it is in the preview
 * phase, it is subject to change or removal without deprecation or notice.
 * Although notice of change is not guaranteed, we will try to let developers
 * know of major changes through announcements in release notes.
 *
 * This class is used as an argument for functions that sort a data set.
 *
 * @since 6.6
 */
@Preview
@Entity
@Table(name = SortInfo.TABLE_NAME)
@XmlRootElement(namespace=Constants.TYPE_APPIAN_NAMESPACE, name=SortInfo.LOCAL_PART)
@XmlType(namespace=Constants.TYPE_APPIAN_NAMESPACE, name=SortInfo.LOCAL_PART, propOrder={"field", "ascending"})
@XmlAccessorType(XmlAccessType.FIELD)
@GwtCompatible
public final class SortInfo implements ReadOnlySortInfo, Serializable {

  private static final long serialVersionUID = 1L;
  public static final String FIELD_FIELD_NAME = "field";
  public static final String ASCENDING_FIELD_NAME = "ascending";

  public static final String TABLE_NAME = "sort_info";
  public static final String LOCAL_PART = "SortInfo";
  public static final QName QNAME = new QName(Constants.TYPE_APPIAN_NAMESPACE, LOCAL_PART);

  @XmlTransient
  private Long id;
  private String field;
  private boolean ascending;

  /**
   * Should not be used, only exposed for serializers that require a public
   * parameterless constructor.
   */
  public SortInfo() {}

  /**
   * @param field
   *          the field to sort by. May be a simple field name, or dot notation
   *          to specify a subfield (e.g., "complexField.subField")
   * @param ascending
   *          if true, indicates that the sort on {@link #getField()} should be in
   *          ascending order. If false, indicates that the sort should be in
   *          descending order.
   */

  public SortInfo(String field, boolean ascending) {
    this.field = field;
    this.ascending = ascending;
  }

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long getId() {
    return id;
  }
  @SuppressWarnings("unused")
  private void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the field to sort by. May be a simple field name, or dot notation
   * to specify a subfield (e.g., "complexField.subField")
   */
  @Column(name = "field", nullable = false, length = com.appian.core.persist.Constants.COL_MAXLEN_INDEXABLE)
  public String getField() {
    return field;
  }
  @SuppressWarnings("unused")
  private void setField(String field) {
    this.field = field;
  }

  /**
   * Returns a boolean indicating whether sort on {@link #getField()} should be
   * in ascending or descending order. If true, indicates that the sort should
   * be in ascending order. If false, indicates that the sort should be in
   * descending order.
   */
  @Column(name = "ascending", nullable = false)
  public boolean isAscending() {
    return ascending;
  }
  @SuppressWarnings("unused")
  private void setAscending(boolean ascending) {
    this.ascending = ascending;
  }
  /**
   * Convenience method for returning a SortInfo with ascending sort.
   */
  public static SortInfo asc(String field) {
    return new SortInfo(field, true);
  }

  /**
   * Convenience method for returning a SortInfo with descending sort.
   */
  public static SortInfo desc(String field) {
    return new SortInfo(field, false);
  }

  /**
   * Returns a string representation of the <code>SortInfo</code>
   */
  @Override
  public String toString() {
    return "Sort[" + field + " " + (ascending ? "asc" : "desc") + "]";
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   * @since 6.6.2.1
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (ascending ? 1231 : 1237);
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    return result;
  }
  /**
   * Returns a boolean value indicating whether the given object is equal to
   * this object.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if both objects are equal, otherwise
   * <code>false</code>
   * @since 6.6.2.1
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SortInfo other = (SortInfo) obj;
    if (ascending != other.ascending) {
      return false;
    }
    if (field == null) {
      if (other.field != null) {
        return false;
      }
    } else if (!field.equals(other.field)) {
      return false;
    }
    return true;
  }

  /**
   * Returns a predicate that will return {@code true} when the {@link #getField() field} of a
   * {@link SortInfo} object equals the given field name.
   * @since 6.6.2.5
   */
  public static Predicate<SortInfo> fieldEqualsPredicate(final String targetFieldName) {
    return new Predicate<SortInfo>() {
      @Override
      public boolean apply(SortInfo input) {
        return targetFieldName.equals(input.getField());
      }
    };
  }
}
