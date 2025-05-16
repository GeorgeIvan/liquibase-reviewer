package com.appiancorp.record.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.common.query.FacetOption;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.rdbms.common.StringsSerializer;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.suiteapi.type.TypedValue;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoUserFilterOption;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A facetable record field may be auto-faceted, or faceted according to some static
 * criteria. This class encapsulates those criteria.
 *
 * @author dan.mascenik
 * @see FieldCfg
 */
@Hidden
@Entity
@Table(name = "record_facet_opt_cfg")
@XmlRootElement(namespace = Type.APPIAN_NAMESPACE, name="facetOptionCfg")
@XmlType(name = FacetOptionCfg.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder =
  {"id", "labelExpr", "facetOperator", "lowerLimitExpr", "upperLimitExpr", "values"})
@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public final class FacetOptionCfg implements ReadOnlyFacetOptionCfg {
  private static final long serialVersionUID = 1L;

  public static final String LOCAL_PART = "FacetOptionCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String PROP_ID = "id";

  private Long id;
  private String labelExpr;
  private byte facetOperatorByte = FacetOperator.IN.getId();
  private String lowerLimitExpr;
  private String upperLimitExpr;
  private List<String> values = new ArrayList<String>();

  private transient ExpressionTransformationState expressionTransformationState = ExpressionTransformationState.STORED;

  public FacetOptionCfg() {}

  public FacetOptionCfg(DesignerDtoUserFilterOption dto) {
    setFacetOperator(FacetOperator.valueOf(dto.getFacetOperator()));
    setLabelExpr(dto.getLabelExpr());
    setLowerLimitExpr(dto.getLowerLimitExpr());
    setUpperLimitExpr(dto.getUpperLimitExpr());
    setValues(dto.getValues());
    setId(dto.getId());
    setExpressionTransformationState(dto.isExprsAreEvaluable() ? ExpressionTransformationState.STORED : ExpressionTransformationState.DISPLAY);
  }

  @XmlElement
  @Transient
  public FacetOperator getFacetOperator() {
    return FacetOperator.getById(facetOperatorByte);
  }
  public void setFacetOperator(FacetOperator operator) {
    this.facetOperatorByte = operator.getId();
  }
  @SuppressWarnings("unused")
  @XmlTransient
  @Column(name = "crit_oper", nullable = false)
  private byte getFacetOperatorByte() {
    return facetOperatorByte;
  }
  @SuppressWarnings("unused")
  private void setFacetOperatorByte(byte facetOperatorByte) {
    this.facetOperatorByte = facetOperatorByte;
  }

  @XmlTransient
  @Column(name = "value_list", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  String getValuesStr() {
    return StringsSerializer.serialize(values);
  }

  @SuppressWarnings("unused")
  private void setValuesStr(String valuesStr) {
    values = StringsSerializer.deserializeToList(valuesStr);
  }

  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetOptionExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION_LIST)
  @Transient
  public List<String> getValues() {
    return values;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableList<String> getValuesReadOnly() {
    List<String> values = getValues();
    if (values != null) {
      return ImmutableList.copyOf(values);
    }
    return null;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public void addValue(String value) {
    values.add(value);
  }

  public void removeValue(String value) {
    values.remove(value);
  }

  public static final class FacetOptionCfgCompositeId {

    private static final Joiner JOINER = Joiner.on('-');

    private final Object fieldCfgId;
    private final Object facetOptCfgId;

    private FacetOptionCfgCompositeId(FieldCfg fieldCfg, FacetOptionCfg facetOptCfg) {
      this.fieldCfgId = checkNotNull(fieldCfg.getId());
      this.facetOptCfgId = checkNotNull(facetOptCfg.getId());
    }

    private FacetOptionCfgCompositeId(Long fieldCfgId, FacetOption facetOption) {
      this.fieldCfgId = checkNotNull(fieldCfgId);
      this.facetOptCfgId = checkNotNull(facetOption.getId());
    }

    public static  String from(FieldCfg fieldCfg, FacetOptionCfg facetOptCfg) {
      return new FacetOptionCfgCompositeId(fieldCfg, facetOptCfg).getCompositeId();
    }

    public static String from(Long fieldCfgId, FacetOption<TypedValue> facetOption) {
      return new FacetOptionCfgCompositeId(fieldCfgId, facetOption).getCompositeId();
    }

    public String getCompositeId() {
      return JOINER.join(fieldCfgId, facetOptCfgId);
    }

    @Override
    public String toString() {
      return "FacetCompositeId["+getCompositeId()+"]";
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(fieldCfgId, facetOptCfgId);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) { return true; }
      if (!(obj instanceof FacetOptionCfgCompositeId)) { return false; }
      FacetOptionCfgCompositeId rhs = (FacetOptionCfgCompositeId) obj;
      return Objects.equal(fieldCfgId, rhs.fieldCfgId) && Objects.equal(facetOptCfgId, rhs.facetOptCfgId);
    }
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "low_lim_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetOptionExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getLowerLimitExpr() {
    return lowerLimitExpr;
  }

  public void setLowerLimitExpr(String lowerLimitExpr) {
    this.lowerLimitExpr = lowerLimitExpr;
  }

  @Column(name = "top_lim_expr", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetOptionExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getUpperLimitExpr() {
    return upperLimitExpr;
  }

  public void setUpperLimitExpr(String upperLimitExpr) {
    this.upperLimitExpr = upperLimitExpr;
  }

  @Column(name = "label_expr", nullable = false, length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  @ComplexForeignKey(nullable=true, breadcrumb=BreadcrumbText.recordTypeFacetOptionLabelExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getLabelExpr() {
    return labelExpr;
  }

  public void setLabelExpr(String labelExpr) {
    this.labelExpr = labelExpr;
  }

  @Transient
  @XmlTransient
  @Override
  public ExpressionTransformationState getExpressionTransformationState() {
    return expressionTransformationState;
  }

  public void setExpressionTransformationState(ExpressionTransformationState state) {
    this.expressionTransformationState = state;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + facetOperatorByte;
    result = prime * result + ((labelExpr == null) ? 0 : labelExpr.hashCode());
    result = prime * result + ((lowerLimitExpr == null) ? 0 : lowerLimitExpr.hashCode());
    result = prime * result + ((upperLimitExpr == null) ? 0 : upperLimitExpr.hashCode());
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FacetOptionCfg other = (FacetOptionCfg) obj;
    if (facetOperatorByte != other.facetOperatorByte)
      return false;
    if (labelExpr == null) {
      if (other.labelExpr != null)
        return false;
    } else if (!labelExpr.equals(other.labelExpr))
      return false;
    if (lowerLimitExpr == null) {
      if (other.lowerLimitExpr != null)
        return false;
    } else if (!lowerLimitExpr.equals(other.lowerLimitExpr))
      return false;
    if (upperLimitExpr == null) {
      if (other.upperLimitExpr != null)
        return false;
    } else if (!upperLimitExpr.equals(other.upperLimitExpr))
      return false;
    if (values == null) {
      if (other.values != null)
        return false;
    } else if (!values.equals(other.values))
      return false;
    return true;
  }

  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }

  public static Function<FacetOptionCfg, String> selectLabel() {
    return FacetOptionCfg::getLabelExpr;
  }
}
