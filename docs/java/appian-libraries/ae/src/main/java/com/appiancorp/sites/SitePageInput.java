package com.appiancorp.sites;

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
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.ExpressionState;
import com.appiancorp.type.ExtendedDataTypeProvider;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;

@Entity
@Table(name = "site_page_input")
@XmlRootElement(name = "sitePageInput", namespace = Type.APPIAN_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE) // Properties must explicitly opt-in to XML serialization
@XmlType(name = SitePageInput.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE, propOrder = {
    Uuid.LOCAL_PART,
    SitePageInput.PROP_INPUT_NAME,
    SitePageInput.PROP_ALLOW_QUERY_PARAMETER,
    SitePageInput.PROP_DEFAULT_VALUE,
    SitePageInput.PROP_URL_PARAM_NAME
})
public class SitePageInput extends PageInput implements Id<Long>, Uuid<String>, ExpressionState {
  public static final String LOCAL_PART = "SitePageInput";

  public SitePageInput() {

  }

  public SitePageInput(final SitePageInput sitePageInput, boolean includeIdInCopy) {
    if (includeIdInCopy) {
      this.id = sitePageInput.id;
    }
    this.uuid = sitePageInput.getUuid();
    this.inputName = sitePageInput.getInputName();
    this.allowQueryParameter = sitePageInput.getAllowQueryParameter();
    this.defaultValue = sitePageInput.getDefaultValue();
    this.urlParamName = sitePageInput.getUrlParamName();
    this.expressionTransformationState = sitePageInput.getExpressionTransformationState();
  }

  public SitePageInput(com.appiancorp.type.cdt.SitePageInput sitePageInput) {
    this.id = sitePageInput.getId();
    this.uuid = sitePageInput.getUuid();
    this.inputName = sitePageInput.getInputName();
    this.allowQueryParameter = sitePageInput.isAllowQueryParameter();
    this.defaultValue = sitePageInput.getDefaultValue();
    this.urlParamName = sitePageInput.getUrlParamName();
    this.expressionTransformationState = DISPLAY;

    if (this.id != null && this.id.intValue() == com.appiancorp.core.Constants.INTEGER_NULL) {
      this.id = null;
    }
  }

  public com.appiancorp.type.cdt.SitePageInput toCdt() {
    ExtendedDataTypeProvider dtp = ServiceLocator.getTypeService(ServiceContextFactory.getAdministratorServiceContext());
    final com.appiancorp.type.cdt.SitePageInput sitePageInput = new com.appiancorp.type.cdt.SitePageInput(dtp);

    sitePageInput.setId(this.id);
    sitePageInput.setUuid(this.uuid);
    sitePageInput.setInputName(this.inputName);
    sitePageInput.setAllowQueryParameter(this.allowQueryParameter);
    sitePageInput.setDefaultValue(this.defaultValue);
    sitePageInput.setUrlParamName(this.urlParamName);

    return sitePageInput;
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
  @ComplexForeignKey(nullable = false, breadcrumb = BreadcrumbText.sitePageInputDefaultValueExpr)
  @ForeignKeyCustomBinder(CustomBinderType.EXPRESSION)
  public String getDefaultValue() {
    return super.getDefaultValue();
  }

  @Column(name = "allow_query_parameter", nullable = false)
  @XmlElement(name = PROP_ALLOW_QUERY_PARAMETER)
  public boolean getAllowQueryParameter() {
    return super.getAllowQueryParameter();
  }

  @Column(name = "input_name", nullable = false)
  @XmlElement(name = PROP_INPUT_NAME)
  public String getInputName() {
    return super.getInputName();
  }

  @Column(name = "url_parameter_name", nullable = false)
  @XmlElement(name = PROP_URL_PARAM_NAME)
  public String getUrlParamName() {
    return super.getUrlParamName();
  }
}
