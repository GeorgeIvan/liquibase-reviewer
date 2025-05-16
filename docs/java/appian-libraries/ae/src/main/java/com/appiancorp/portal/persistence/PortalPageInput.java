package com.appiancorp.portal.persistence;

import static com.appiancorp.core.expr.ExpressionTransformationState.DISPLAY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.appian.core.persist.Constants;
import com.appiancorp.ix.binding.BreadcrumbText;
import com.appiancorp.ix.refs.ComplexForeignKey;
import com.appiancorp.ix.refs.CustomBinderType;
import com.appiancorp.ix.refs.ForeignKeyCustomBinder;
import com.appiancorp.navigation.PageInput;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.ExpressionState;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.cdt.value.PortalPageInputDto;

@Hidden
@Entity
@Table(name = "navigation_node_rule_input")
@XmlRootElement(name = "navigationNodeRuleInput", namespace = Type.APPIAN_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(name = PortalPageInput.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    Uuid.LOCAL_PART,
    PortalPageInput.PROP_INPUT_NAME,
    PortalPageInput.PROP_ALLOW_QUERY_PARAMETER,
    PortalPageInput.PROP_DEFAULT_VALUE,
    PortalPageInput.PROP_URL_PARAM_NAME
})
public class PortalPageInput extends PageInput implements Id<Long>, Uuid<String>, ExpressionState {
  public static final String LOCAL_PART = "NavigationNodeRuleInput";

  public PortalPageInput() {}

  /**
   * Given the PortalRuleInput design time CDT, constructs a PortalPageInput.
   */
  public PortalPageInput(PortalPageInputDto portalPageInputDto) {
    this.id = portalPageInputDto.getId();
    this.uuid = portalPageInputDto.getUuid();
    this.inputName = portalPageInputDto.getInputName();
    this.allowQueryParameter = portalPageInputDto.isAllowQueryParameter();
    this.defaultValue = portalPageInputDto.getDefaultValue();
    this.urlParamName = portalPageInputDto.getUrlParamName();
    this.expressionTransformationState = DISPLAY;

    if (this.id.intValue() == com.appiancorp.core.Constants.INTEGER_NULL) {
      this.id = null;
    }
  }

  /**
   * Converts the PortalPageInput to a PortalRuleInput design time CDT.
   */
  public PortalPageInputDto toCdt() {
    final PortalPageInputDto ruleInputCDT = new PortalPageInputDto();

    ruleInputCDT.setId(this.id);
    ruleInputCDT.setUuid(this.uuid);
    ruleInputCDT.setInputName(this.inputName);
    ruleInputCDT.setAllowQueryParameter(this.allowQueryParameter);
    ruleInputCDT.setDefaultValue(this.defaultValue);
    ruleInputCDT.setUrlParamName(this.urlParamName);

    return ruleInputCDT;
  }

  @Override
  @Column(name = "uuid", updatable = false, nullable = false, unique = true)
  @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
  public String getUuid() {
    return super.getUuid();
  }

  @Override
  @javax.persistence.Id
  @GeneratedValue
  public Long getId() {
    return super.getId();
  }

  @Lob
  @Column(name = "default_value_expr", nullable = true, length = Constants.COL_MAXLEN_EXPRESSION)
  @XmlElement(name = PROP_DEFAULT_VALUE)
  @ComplexForeignKey(nullable = false, breadcrumb = BreadcrumbText.navigationNodeRuleInputDefaultValueExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDefaultValue() {
    return super.getDefaultValue();
  }

  @Column(name = "allow_query_parameter", nullable = false)
  @XmlElement(name = PROP_ALLOW_QUERY_PARAMETER)
  public boolean getAllowQueryParameter() {
    return super.getAllowQueryParameter();
  }

  @Column(name = "rule_input_name", nullable = false)
  @XmlElement(name = "ruleInputName") // NOTE: this is ruleInputName and not inputName because of backwards compatibility
  public String getInputName() {
    return super.getInputName();
  }

  @Column(name = "url_parameter_name", nullable = false)
  @XmlElement(name = PROP_URL_PARAM_NAME)
  public String getUrlParamName() {
    return super.getUrlParamName();
  }
}
