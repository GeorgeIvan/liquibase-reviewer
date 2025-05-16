package com.appiancorp.record.domain;

import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;
import static com.appiancorp.core.expr.ExpressionTransformationState.STORED;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.appian.core.persist.Constants;
import com.appiancorp.core.expr.ExpressionTransformationState;
import com.appiancorp.core.expr.portable.cdt.RecordHeaderBillboardConfigConstants;
import com.appiancorp.core.expr.portable.cdt.RecordHeaderCardConfigConstants;
import com.appiancorp.ix.binding.Breadcrumb;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.binding.VariableBindings;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.cdt.DesignerDtoDetailViewHeaderCfg;
import com.appiancorp.type.cdt.RecordHeaderBillboardConfig;
import com.appiancorp.type.cdt.RecordHeaderCardConfig;
import com.appiancorp.type.external.IgnoreJpa;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

@Hidden
@Entity
@Table(name = "record_header_cfg")
@XmlRootElement(name = "detailViewHeaderCfg", namespace = Type.APPIAN_NAMESPACE)
@XmlType(name = DetailViewHeaderCfg.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    "id",
    "headerTypeByte",
    "headerConfigJson",
    "isHeaderFixed",
    "showDivider"
})
@IgnoreJpa
public class DetailViewHeaderCfg implements ReadOnlyDetailViewHeaderCfg {
  private static final long serialVersionUID = 1L;

  public static final String LOCAL_PART = "DetailViewHeaderCfg";
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);
  public static final String JOIN_COL_HEADER_CFG_ID = "record_header_cfg_id";

  public static final String DOCUMENT = "DOCUMENT";
  public static final String EXPRESSION = "EXPRESSION";
  public static final String HEX = "HEX";
  public static final String URL = "URL";

  public static final String PROP_ID = "id";

  private Long id;
  private RecordHeaderType headerType = RecordHeaderType.STANDARD;
  private String headerConfigJson;
  private boolean isHeaderFixed;
  private boolean showDivider = true;

  private transient ExpressionTransformationState expressionTransformationState = STORED;

  public DetailViewHeaderCfg() {}

  public DetailViewHeaderCfg(DesignerDtoDetailViewHeaderCfg dto) {
    setId(dto.getId());
    setHeaderType(RecordHeaderType.fromText(dto.getHeaderType()));
    setCardConfig(dto.getCardConfig());
    setBillboardConfig(dto.getBillboardConfig());
    setExpressionTransformationState(dto.isExprsAreEvaluable() ? STORED : DISPLAY);
    setIsHeaderFixed(dto.isIsHeaderFixed());
    setShowDivider(dto.isShowDivider());
  }

  @Override
  @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "header_type")
  public byte getHeaderTypeByte() {
    return getHeaderType().getCode();
  }

  public void setHeaderTypeByte(byte code) {
    setHeaderType(RecordHeaderType.valueOf(code));
  }

  @Transient
  @XmlTransient
  public RecordHeaderType getHeaderType() {
    return headerType;
  }

  public void setHeaderType(RecordHeaderType headerType) {
    this.headerType = headerType;
  }

  @Column(name = "header_config", length = Constants.COL_MAXLEN_EXPRESSION)
  @Lob
  public String getHeaderConfigJson() {
    return headerConfigJson;
  }

  public void setHeaderConfigJson(String headerConfigJson) {
    this.headerConfigJson = headerConfigJson;
  }

  @Column(name = "is_fixed", nullable = false)
  public boolean getIsHeaderFixed() {
    return isHeaderFixed;
  }

  public void setIsHeaderFixed(boolean isHeaderFixed) {
    this.isHeaderFixed = isHeaderFixed;
  }

  @Column(name = "show_divider", nullable = false)
  public boolean isShowDivider() {
    return showDivider;
  }

  public void setShowDivider(boolean showDivider) {
    this.showDivider = showDivider;
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeDetailViewHeaderExprStyleExpr,
      breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_CHILDREN,
      prependedBreadcrumbs = {BreadcrumbText.recordTypeDetailViewHeaderExpr},
      variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_VIEW_HEADER_CARD)
  public Map<String, String> getCardConfig() {
    if (this.headerType.equals(RecordHeaderType.CARD) && this.headerConfigJson != null) {
      try {
        return new Gson().fromJson(this.headerConfigJson, new TypeToken<Map<String,String>>() {}.getType());
      } catch (JsonSyntaxException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableMap<String,String> getCardConfigReadOnly() {
    Map<String,String> cardConfig = getCardConfig();
    if (cardConfig != null) {
      return ImmutableMap.copyOf(cardConfig);
    }
    return null;
  }

  public void setCardConfig(Map<String,String> cardConfigMap) {
    if (cardConfigMap == null) {
      return;
    }

    this.headerConfigJson = new Gson().toJson(cardConfigMap);

    if (!RecordHeaderType.CARD.equals(this.headerType)) {
      this.headerType = RecordHeaderType.CARD;
    }
  }

  public void setCardConfig(RecordHeaderCardConfig cardConfig) {
    if (cardConfig != null) {
      Map<String,String> cardConfigMap = new HashMap<>();
      cardConfigMap.put(RecordHeaderCardConfigConstants.COLOR_SOURCE, cardConfig.getColorSource());
      cardConfigMap.put(RecordHeaderCardConfigConstants.STYLE_EXPR, cardConfig.getStyleExpr());

      setCardConfig(cardConfigMap);
    }
  }

  @Transient
  @XmlTransient
  @ComplexForeignKey(nullable = true, breadcrumb = BreadcrumbText.recordTypeDetailViewHeaderExprBackgroundMediaExpr,
      breadcrumbFlags = Breadcrumb.BREADCRUMB_GUIDANCE_COMBINE_CHILDREN,
      prependedBreadcrumbs = {BreadcrumbText.recordTypeDetailViewHeaderExpr},
      variableBindings = VariableBindings.RECORD_TYPE)
  @ForeignKeyCustomBinder(CustomBinderType.RECORD_VIEW_HEADER_BILLBOARD)
  public Map<String, String> getBillboardConfig() {
    if (this.headerType.equals(RecordHeaderType.BILLBOARD) && this.headerConfigJson != null) {
      try {
        return new Gson().fromJson(this.headerConfigJson, new TypeToken<Map<String,String>>() {}.getType());
      } catch (JsonSyntaxException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  @Transient
  @XmlTransient
  public ImmutableMap<String,String> getBillboardConfigReadOnly() {
    Map<String,String> billboardConfig = getBillboardConfig();
    if (billboardConfig != null) {
      return ImmutableMap.copyOf(billboardConfig);
    }
    return null;
  }

  public void setBillboardConfig(Map<String,String> billboardConfigMap) {
    if (billboardConfigMap == null) {
      return;
    }

    this.headerConfigJson = new Gson().toJson(billboardConfigMap);

    if (!RecordHeaderType.BILLBOARD.equals(this.headerType)) {
      this.headerType = RecordHeaderType.BILLBOARD;
    }
  }

  public void setBillboardConfig(RecordHeaderBillboardConfig billboardConfig) {
    if (billboardConfig != null) {
      Map<String,String> billboardConfigMap = new HashMap<>();
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.IMAGE_TYPE, billboardConfig.getImageType());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.BACKGROUND_MEDIA_EXPR, billboardConfig.getBackgroundMediaExpr());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.HEIGHT, billboardConfig.getHeight());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.OVERLAY_STYLE, billboardConfig.getOverlayStyle());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.OVERLAY_POSITION, billboardConfig.getOverlayPosition());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.OVERLAY_COLOR, billboardConfig.getOverlayColor());
      billboardConfigMap.put(RecordHeaderBillboardConfigConstants.BACKGROUND_COLOR, billboardConfig.getBackgroundColor());

      setBillboardConfig(billboardConfigMap);
    }
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
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((headerType == null) ? 0 : headerType.hashCode());
    result = prime * result + ((headerConfigJson == null) ? 0 : headerConfigJson.hashCode());
    result = prime * result + (Boolean.valueOf(isHeaderFixed).hashCode());
    result = prime * result + (Boolean.valueOf(showDivider).hashCode());
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DetailViewHeaderCfg)) {
      return false;
    }
    DetailViewHeaderCfg other = (DetailViewHeaderCfg)obj;
    return Objects.equals(id, other.id) && Objects.equals(headerType, other.headerType) &&
        Objects.equals(headerConfigJson, other.headerConfigJson) &&
        isHeaderFixed == other.isHeaderFixed &&
        showDivider == other.showDivider;
  }

  // Used by Java Serialization
  private Object readResolve() {
    this.expressionTransformationState = ExpressionTransformationState.STORED;
    return this;
  }
}
