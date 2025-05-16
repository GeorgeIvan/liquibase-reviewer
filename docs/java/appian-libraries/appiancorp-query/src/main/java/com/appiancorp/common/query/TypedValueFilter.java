package com.appiancorp.common.query;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.appian.type.Constants;
import com.appiancorp.common.query.TypedValueQuery.TypedValueBuilder;
import com.appiancorp.core.expr.portable.PortableTypedValue;
import com.appiancorp.suiteapi.common.Preview;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.TypedValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;

/**
 * <p>Immutable bean representing a configured {@link Filter} on a column.
 *
 * <p>An instance can only be obtain by using the helper classes at {@link TypedValueBuilder}
 *  <ul><li>For value expressions use - {@link TypedValueBuilder.FilterOpExpr}
 *      <li>For literal values use - {@link TypedValueBuilder.FilterOpLiteral}
 * </ul>
 * @author johnny.debrodt
 * @since 7.1.0.5
 */
@Hidden
@Preview
@Entity
@Table(name = Filter.TABLE_NAME)
@XmlRootElement(namespace=Constants.TYPE_APPIAN_NAMESPACE, name=Filter.XML_ROOT_ELEMENT)
@XmlType(namespace=Constants.TYPE_APPIAN_NAMESPACE, name=Filter.LOCAL_PART,
  propOrder={"field", "operator", "valueExpression", "value", "validated"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(TypedValue.class)
@GwtCompatible
public final class TypedValueFilter extends Filter<TypedValue> {

  @SuppressWarnings("unused")
  private TypedValueFilter(){
    super(); // Needed by Hibernate
  }

  /**
   * Constructs a new <code>TypedValueFilter</code> using the given {@link TypedValue} as a constraint
   *
   * @param field the field or alias name
   * @param operator the filter type define by {@link FilterOperator}
   * @param value {@link TypedValue} use to constraint the column or alias
   */
  @VisibleForTesting
  public TypedValueFilter(String field, FilterOperator operator, TypedValue value) {
    super(field, operator, value);
  }
  /**
   * Constructs a new <code>TypedValueFilter</code> using the given expression as a constraint.
   * This expression is expected to evaluate to a {@link TypedValue}.
   *
   * <p>The constructor that takes {@code TypedValue value} is
   * preferred over this constructor because evaluation of an expression to obtain
   * the value is unnecessary except in cases of deferred evaluation, which are not applicable
   * to cases of constructing a Filter for plug-ins.
   *
   * @param field the field or alias name
   * @param operator the filter type define by {@link FilterOperator}
   * @param valueExpression
   */
  protected TypedValueFilter(String field, FilterOperator operator, String valueExpression) {
    super(field, operator, valueExpression);
  }

  private Boolean validated;
  private TypedValue value;

  /**
   * @return whether the filter has been validated within SAIL.
   */
  @Transient
  @XmlElement(namespace=Constants.TYPE_APPIAN_NAMESPACE)
  public Boolean getValidated() {
    return this.validated;
  }
  @SuppressWarnings("unused")
  protected void setValidated(Boolean validated) {
    this.validated = validated;
  }

  @Override
  @Transient
  @XmlElement(namespace=Constants.TYPE_APPIAN_NAMESPACE)
  public TypedValue getValue() {
    return value;
  }

  @Override
  protected void setValue(TypedValue value) {
    this.value = value;
  }

  @Override
  public Criteria copy() {
    TypedValue valueCopy = new TypedValue(value);
    return new TypedValueFilter(this.getField(), this.getOperator(), valueCopy);
  }

  @Override
  @Transient
  @XmlTransient
  public PortableTypedValue getValueReadOnly() {
    return (PortableTypedValue)getValue();
  }
}
