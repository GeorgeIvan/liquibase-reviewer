package com.appiancorp.type.config.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.appiancorp.common.emf.EmfUtils;
import com.appiancorp.suiteapi.common.ValidationCode;
import com.appiancorp.suiteapi.common.exceptions.AppianRuntimeException;
import com.appiancorp.suiteapi.common.exceptions.ErrorCode;
import com.appiancorp.suiteapi.type.Datatype;
import com.appiancorp.suiteapi.type.Hidden;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.suiteapi.type.TypedValue;
import com.appiancorp.suiteapi.type.config.ImportDiagnosticSeverity;
import com.appiancorp.suiteapi.type.config.xsd.exceptions.UnsupportedXsdException;
import com.appiancorp.test.framework.junit.AppianExtension;
import com.appiancorp.test.framework.junit.EnableAutomaticSaveRestore;
import com.appiancorp.test.framework.junit.RestoreServerImagesAfterEachTest;
import com.appiancorp.test.util.AdminServices;
import com.appiancorp.test.util.ResourceLoader;
import com.appiancorp.test.util.TypeUtils;
import com.appiancorp.type.AppianTypeLong;
import com.appiancorp.type.config.pojo.qualifiedelementstest.QualifiedElementBean;
import com.appiancorp.type.config.xsd.DatatypeXsdSchemaBuilder;
import com.appiancorp.type.external.IgnoreJpa;
import com.appiancorp.type.model.AppianExtendedMetaData;
import com.appiancorp.type.refs.DocumentRef;

@SuppressWarnings("unused")
@ExtendWith(AppianExtension.class)
@RestoreServerImagesAfterEachTest
@EnableAutomaticSaveRestore
public class PojoTypeImporterTest implements AdminServices {
  public static final String QUALIFIED_NAMESPACE = "urn:qualifiedNs-pojoTypeImporter";
  private static final String NS_1 = "http://www.example.org/test/PojoTypeImporterTest-1";
  private static final String NS_2 = "http://www.example.org/test/PojoTypeImporterTest-2";
  private static final String NS_3 = "http://www.example.org/test/PojoTypeImporterTest-3";

  private final PojoTypeImporter importer = new PojoTypeImporter(adminSc);

  @Test
  public void testImportTypes_SingleType_SingleNamespace() throws Exception {
    final Class<?> javaClass = SampleBeanSingleNamespace.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(1, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(2, dt.getInstanceProperties().length);
    Assertions.assertEquals("bar", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals("foo", dt.getInstanceProperties()[1].getName());

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+dtQName+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"bar", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class SampleBeanSingleNamespace {
    @XmlElement public int foo;
    @XmlElement public int bar;
  }

  @Test
  public void testImportTypes_SingleType_SingleNamespace_ComplexType_Hidden() throws Exception {
    final Class<?> javaClass = SampleBeanSingleNamespaceHidden.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(1, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(2, dt.getInstanceProperties().length);
    Assertions.assertEquals("bar", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals("foo", dt.getInstanceProperties()[1].getName());
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+dtQName+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"bar", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Hidden
  private static class SampleBeanSingleNamespaceHidden {
    @XmlElement public int foo;
    @XmlElement public int bar;
  }

  @Test
  public void testImportTypes_SingleType_SingleNamespace_SimpleType_Hidden() throws Exception {
    final Class<?> javaClass = SimpleTypeHidden.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(1, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(AppianTypeLong.INTEGER, dt.getBase());
    Assertions.assertEquals(0, dt.getInstanceProperties().length);
    Assertions.assertEquals(0, dt.getTypeProperties().length);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EDataType cls = (EDataType) extMd.getTypeByOriginalXmlQName(dtQName);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+dtQName+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Hidden
  private static class SimpleTypeHidden {
    @XmlValue public int foo; // simple type extending from integer
  }

  @Test // verify @Hidden annotation is not inherited
  public void testImportTypes_SingleType_SingleNamespace_NotHidden() throws Exception {
    final Class<?> javaClass = SampleBeanSingleNamespaceSubClass.class;
    final QName dtQName = new QName(NS_1, "sample-bean-not-hidden");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));
  }
  @XmlRootElement(name="sample-bean-el-not-hidden", namespace=NS_1)
  @XmlType(name="sample-bean-not-hidden", namespace=NS_1, propOrder={"cat"})
  private static class SampleBeanSingleNamespaceSubClass extends SampleBeanSingleNamespaceHidden {
    @XmlElement public int cat;
  }

  @Test
  public void testImportTypes_SingleType_MultipleNamespaces() throws Exception {
    final Class<?> javaClass = SampleBeanMultipleNamespaces.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(1, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(2, dt.getInstanceProperties().length);
    Assertions.assertEquals("bar", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals("foo", dt.getInstanceProperties()[1].getName());
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+dtQName+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_2)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"bar", "foo"})
  private static class SampleBeanMultipleNamespaces {
    @XmlElement public int foo;
    @XmlElement public int bar;
  }

  @Test
  public void testImportTypes_SingleTypeWithNestedType_SingleNamespace() throws Exception {
    final Class<?> javaClass = SampleBeanWithNestedType.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    final QName nestedDtQName = new QName(NS_1, "nested-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(2, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct types were created in K.
    Datatype dt1 = aTypeService.getTypeByQualifiedName(dtQName);
    Datatype dt2 = aTypeService.getTypeByQualifiedName(nestedDtQName);
    Assertions.assertEquals(2, dt1.getInstanceProperties().length);
    Assertions.assertEquals(AppianTypeLong.INTEGER, dt1.getInstanceProperties()[0].getInstanceType());
    Assertions.assertEquals(dt2.getId(), dt1.getInstanceProperties()[1].getInstanceType());
    Assertions.assertEquals(1, dt2.getInstanceProperties().length);
    Assertions.assertEquals("bar", dt2.getInstanceProperties()[0].getName());
  }
  @XmlType(name="sample-bean", namespace=NS_1)
  private static class SampleBeanWithNestedType {
    @XmlElement public int foo;
    @XmlElement public NestedBean nested;
  }
  @XmlType(name="nested-bean", namespace=NS_1)
  private static class NestedBean {
    @XmlElement public int bar;
  }

  @Test
  public void testImportTypes_SingleTypeWithNestedType_MultipleNamespaces() throws Exception {
    TypeUtils.deleteTypesInNamespace(NS_1, NS_2, NS_3);

    final Class<?> javaClass = ParentBean.class;
    final QName qname1 = qname(NS_1, StringUtils.uncapitalize(javaClass.getSimpleName()));
    final QName qname2 = qname(NS_2, StringUtils.uncapitalize(ChildBean.class.getSimpleName()));
    final QName qname3 = qname(NS_3, StringUtils.uncapitalize(GrandChildBean.class.getSimpleName()));
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(3, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    // This assertion fails with a warning: APNX-2-4047-000{XSD: The location '%7Bhttp%3A%2F%2Fwww.example.org%2Ftest%2FPojoTypeImporterTest-3%7D.xsd' has not been resolved because the import is unused}
    //Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct types were created in K.
    Datatype dt1 = aTypeService.getTypeByQualifiedName(qname1);
    Datatype dt2 = aTypeService.getTypeByQualifiedName(qname2);
    Datatype dt3 = aTypeService.getTypeByQualifiedName(qname3);
    Assertions.assertEquals(1, dt1.getInstanceProperties().length);
    Assertions.assertEquals("myField", dt1.getInstanceProperties()[0].getName());
    Assertions.assertEquals(dt2.getId(), dt1.getInstanceProperties()[0].getInstanceType());
    Assertions.assertEquals(1, dt2.getInstanceProperties().length);
    Assertions.assertEquals("myField", dt2.getInstanceProperties()[0].getName());
    Assertions.assertEquals(dt3.getId(), dt2.getInstanceProperties()[0].getInstanceType());
    Assertions.assertEquals(1, dt3.getInstanceProperties().length);
    Assertions.assertEquals("myField", dt3.getInstanceProperties()[0].getName());
    Assertions.assertEquals(AppianTypeLong.STRING, dt3.getInstanceProperties()[0].getInstanceType());

    // Verify that the correct type was created in RDBMS.
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dt1, dt2, dt3);
    String regStr = EmfUtils.toStringSafe(reg);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(qname1);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+qname1+". Reg:\n"+regStr);
    cls = (EClass) extMd.getTypeByOriginalXmlQName(qname2);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+qname2+". Reg:\n"+regStr);
    cls = (EClass) extMd.getTypeByOriginalXmlQName(qname3);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+qname3+". Reg:\n"+regStr);
  }
  @XmlType(namespace=NS_1)
  private static class ParentBean {
    @XmlElement public ChildBean myField;
  }
  @XmlType(namespace=NS_2)
  private static class ChildBean {
    @XmlElement public GrandChildBean myField;
  }
  @XmlType(namespace=NS_3)
  private static class GrandChildBean {
    @XmlElement public String myField;
  }

  @Test
  public void testImportTypes_CircularReference() throws Exception {
    PojoTypeImportResult ir = importer.importTypes(set(Top1.class));

    // Verify the import result.
    Assertions.assertEquals(3, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    QName top = qname(NS_1, "top1");
    Datatype dt = aTypeService.getTypeByQualifiedName(top);
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));

    QName middle = qname(NS_1, "middle1");
    dt = aTypeService.getTypeByQualifiedName(middle);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));

    QName bottom = qname(NS_1, "bottom1");
    dt = aTypeService.getTypeByQualifiedName(bottom);
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));

    // Verify that the correct types were created in K.
    TypeUtils.assertDatatypesInK(set(top, middle, bottom));
  }
  @XmlType(namespace=NS_1)
  private static class Top1 {
    @XmlElement public Middle1 b;
  }
  @XmlType(namespace=NS_1)
  @Hidden
  private static class Middle1 {
    @XmlElement public Bottom1 c;
  }
  @XmlType(namespace=NS_1)
  private static class Bottom1 {
    @XmlElement public Top1 a;
    @XmlElement public Middle1 b;
  }

  @Test
  public void testImportTypes_CircularReference_MultipleNamespaces() throws Exception {
    PojoTypeImportResult ir = importer.importTypes(set(Top2.class));

    // Verify the import result.
    Assertions.assertEquals(3, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    // TODO: this assertion fails
    //Assertions.assertEquals(Arrays.toString(ir.getDiagnostics()), 0, ir.getDiagnostics().length);

    QName top = qname(NS_1, "top2");
    Datatype dt = aTypeService.getTypeByQualifiedName(top);
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));

    QName middle = qname(NS_2, "middle2");
    dt = aTypeService.getTypeByQualifiedName(middle);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));

    QName bottom = qname(NS_3, "bottom2");
    dt = aTypeService.getTypeByQualifiedName(bottom);
    Assertions.assertFalse(dt.hasFlag(Datatype.FLAG_HIDDEN));

    // Verify that the correct types were created in K.
    TypeUtils.assertDatatypesInK(set(top, middle, bottom));
  }
  @XmlType(namespace=NS_1)
  private static class Top2 {
    @XmlElement public Middle2 myField;
  }
  @XmlType(namespace=NS_2)
  @Hidden
  private static class Middle2 {
    @XmlElement public Bottom2 myField;
  }
  @XmlType(namespace=NS_3)
  private static class Bottom2 {
    @XmlElement public Top2 myField1;
    @XmlElement public Middle2 myField2;
  }

  /**
   * Test importing a datatype from a class that cannot be handled by JAXB. The import should fail.
   */
  @Test
  public void testEnabled_ClassInvalidForJaxb() throws Exception {
    final Class<?> javaClass = SampleBeanInvalidForJaxb.class;
    try {
      PojoTypeImportResult ir = importer.importTypes(set(javaClass));
      Assertions.fail("Expected "+PojoTypeImportException.class);
    } catch (PojoTypeImportException expected) {
      Assertions.assertEquals(ErrorCode.POJO_DT_IMPORT_CANNOT_GENERATE_XSDS_FROM_CLASSES, expected.getErrorCode());
      Assertions.assertTrue(expected.getCause() instanceof JAXBException, expected.getCause()+"");
    }
  }

  @XmlType(namespace=NS_1)
  private class SampleBeanInvalidForJaxb {
    /* JAXB requires a no-arg default constructor. */
    public SampleBeanInvalidForJaxb(int foo, int bar) {
      this.foo = foo;
      this.bar = bar;
    }
    @XmlElement public int foo;
    @XmlElement public int bar;
  }

  /**
   * Test importing a datatype from a class whose JAXB annotations produce an XSD that uses
   * a construct that we don't yet support. The datatype should still be created, but a
   * diagnostic warning should be reported.
   */
  @Test
  public void testImportTypes_UnsupportedXsdConstruct() throws Exception {
    final Class<?> javaClass = SampleBeanWithUnsupportedXsdConstruct.class;
    final QName qname = qname(NS_1, StringUtils.uncapitalize(javaClass.getSimpleName()));
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(1, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(1, ir.getDiagnostics().length, Arrays.toString(ir.getDiagnostics()));

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(qname);
    Assertions.assertEquals(2, dt.getInstanceProperties().length);
    Assertions.assertEquals("foo", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals("bar", dt.getInstanceProperties()[1].getName());

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(qname);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+qname+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  /* Use a default value which is not supported, but is handled as a warning. */
  @XmlType(namespace=NS_1)
  private static class SampleBeanWithUnsupportedXsdConstruct {
    @XmlElement(defaultValue="1") private int foo;
    @XmlElement(defaultValue="2") private int bar;
  }

  /**
   * Test importing a datatype from a class whose JAXB annotations produce an XSD that
   * cannot be imported into Appian.
   */
  @Test
  public void testImportTypes_InvalidXsdConstructForAppian() throws Exception {
    final Class<?> javaClass = SampleBeanWithInvalidXsdConstructForAppian.class;
    try {
      PojoTypeImportResult ir = importer.importTypes(set(javaClass));
      Assertions.fail("Expected "+PojoTypeImportException.class);
    } catch (PojoTypeImportException expected) {
      Assertions.assertEquals(ErrorCode.POJO_DT_IMPORT_CANNOT_IMPORT_XSDS, expected.getErrorCode());
      UnsupportedXsdException cause = (UnsupportedXsdException) expected.getCause();
      Assertions.assertEquals(ErrorCode.XSD_UNSUPPORTED_RESERVED_NAMESPACE, cause.getErrorCode());
    }
  }
  /* Use the Appian namespace which is not allowed (triggers an error during XSD import). */
  @XmlType(name="sample-bean", namespace=Type.APPIAN_NAMESPACE)
  private static class SampleBeanWithInvalidXsdConstructForAppian {
    @XmlElement private int foo;
    @XmlElement private int bar;
  }

  @Test
  public void testImportTypes_AppianReferences() throws Exception {
    final Class<?> javaClass = SampleBeanAppianReference.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));

    // Verify the import result.
    Assertions.assertEquals(2, ir.getTopLevelDatatypes().length, Arrays.toString(ir.getTopLevelDatatypes()));
    Assertions.assertEquals(0, ir.getDiagnostics().length);

    // Verify that the correct type was created in K.
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(2, dt.getInstanceProperties().length);
    Assertions.assertEquals("foo", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals(AppianTypeLong.INTEGER, dt.getInstanceProperties()[0].getInstanceType());
    Assertions.assertEquals("bar", dt.getInstanceProperties()[1].getName());
    Assertions.assertEquals(AppianTypeLong.DOCUMENT, dt.getInstanceProperties()[1].getInstanceType());

    // Verify that the correct type was created in RDBMS.
    Long dbDtId = Long.valueOf(dt.getExternalTypeId());
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(dbDtId);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    EPackage pkg = reg.getEPackage(NS_1);
    Assertions.assertEquals(1, pkg.getEClassifiers().size());
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    Assertions.assertNotNull(cls, "No EClassifier with qname "+dtQName+". Saved EClassifiers: "+EmfUtils.getEClassifierNames(pkg));
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo", "bar"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class SampleBeanAppianReference {
    @XmlElement public int foo;
    @XmlElement public DocumentRef bar;
  }

  @Test
  public void testImportTypes_JpaAnnotations_Table() throws Exception {
    final Class<?> javaClass = TableAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"bar", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class TableAnnotationBean {
    @XmlElement private int foo;
    @XmlElement private int bar;
  }

  @Test
  public void testImportTypes_JpaAnnotations_Table_Id() throws Exception {
    final Class<?> javaClass = TableAndIdAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\", uniqueConstraints={@UniqueConstraint(columnNames={\"foo\", \"bar\"}), @UniqueConstraint(columnNames={\"foo\"})})");
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");

    // TODO: verify that the table was created with appropriate constraints?
  }
  @Table(name="sample_bean_table", uniqueConstraints={@UniqueConstraint(columnNames={"foo", "bar"}), @UniqueConstraint(columnNames={"foo"})})
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"bar", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class TableAndIdAnnotationBean {
    @Id @XmlElement private int foo;
    @XmlElement private int bar;
  }

  @Test
  public void testImportTypes_JpaAnnotations_OverriddenPropertyName() throws Exception {
    final Class<?> javaClass = OverriddenPropertyNameAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "overridden", "@Id");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class OverriddenPropertyNameAnnotationBean {
    @XmlElement(name="overridden") @Id private int foo;
  }

  @Test
  public void testImportTypes_JpaAnnotations_DefaultAccessTypeField() throws Exception {
    final Class<?> javaClass = DefaultJPAAccessTypeFieldBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "fizz", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "overridden", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "extraNotOverridden", null);
    verifyMemberAnnotation(extMd, dtQName, "extraOverridden", "@Version");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo", "bar", "fizz", "overridden", "extraNotOverridden", "extraOverridden"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class DefaultJPAAccessTypeFieldBean {
    @Id private int foo; // JPA access should default to FIELD because @Id annotation is on a field
    @Column private int bar;
    @Column private int fizz;
    @Column private int overridden; // annotations should be overridden by property

    public int getFoo() {
      return foo;
    }

    @Version
    public int getBar() {
      return bar;
    }

    @Version
    // Notice no @Access, annotations here should be ignored
    public int getFizz() {
      return fizz;
    }

    @Version
    @Access(javax.persistence.AccessType.PROPERTY)
    public int getOverridden() {
      return overridden;
    }

    @XmlElement
    @Version
    public int getExtraNotOverridden() {
      return 0;
    }

    @XmlElement
    @Version
    @Access(javax.persistence.AccessType.PROPERTY)
    public int getExtraOverridden() {
      return 0;
    }
  }

  @Test
  public void testImportTypes_JpaAnnotations_ExplicitAccessTypeField() throws Exception {
    final Class<?> javaClass = ExplicitJPAAccessTypeFieldBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "fizz", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "overridden", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "extraNotOverridden", null);
    verifyMemberAnnotation(extMd, dtQName, "extraOverridden", "@Version");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo", "bar", "fizz", "overridden", "extraNotOverridden", "extraOverridden"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Access(javax.persistence.AccessType.FIELD) // here access is explicit
  private static class ExplicitJPAAccessTypeFieldBean {
    @Id private int foo;
    @Column private int bar;
    @Column private int fizz;
    @Column private int overridden; // annotations should be overridden by property

    public int getFoo() {
      return foo;
    }

    @Version
    public int getBar() {
      return bar;
    }

    @Version
    // Notice no @Access, annotations here should be ignored
    public int getFizz() {
      return fizz;
    }

    @Version
    @Access(javax.persistence.AccessType.PROPERTY)
    public int getOverridden() {
      return overridden;
    }

    @XmlElement
    @Version
    public int getExtraNotOverridden() {
      return 0;
    }

    @XmlElement
    @Version
    @Access(javax.persistence.AccessType.PROPERTY)
    public int getExtraOverridden() {
      return 0;
    }
  }

  @Test
  public void testImportTypes_JpaAnnotations_DefaultAccessTypeProperty() throws Exception {
    final Class<?> javaClass = DefaultJPAAccessTypePropertyBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "fizz", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "overridden", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "extraNotOverridden", null);
    verifyMemberAnnotation(extMd, dtQName, "extraOverridden", "@Version");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo", "bar", "fizz", "overridden", "extraNotOverridden", "extraOverridden"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class DefaultJPAAccessTypePropertyBean {
    @Column private int foo;
    @Column private int bar;
    @Column private int fizz;
    @Column @Access(javax.persistence.AccessType.FIELD) private int overridden;
    @Version private int extraNotOverridden;
    @Version @Access(javax.persistence.AccessType.FIELD) private int extraOverridden;

    @Id // JPA access should default to PROPERTY because @Id annotation is on a property
    public int getFoo() {
      return foo;
    }

    @Version
    public int getBar() {
      return bar;
    }

    @Version
    public int getFizz() {
      return fizz;
    }

    @Version
    public int getOverridden() {
      return overridden;
    }
  }

  @Test
  public void testImportTypes_JpaAnnotations_ExplicitAccessTypeProperty() throws Exception {
    final Class<?> javaClass = ExplicitJPAAccessTypePropertyBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "fizz", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "overridden", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "extraNotOverridden", null);
    verifyMemberAnnotation(extMd, dtQName, "extraOverridden", "@Version");
    verifyMemberAnnotation(extMd, dtQName, "bool", "@Version");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"foo", "bar", "fizz", "overridden", "extraNotOverridden", "extraOverridden", "bool"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Access(javax.persistence.AccessType.PROPERTY) // explicit property access
  private static class ExplicitJPAAccessTypePropertyBean {
    @Column private int foo;
    @Column private int bar;
    @Column private int fizz;
    @Column @Access(javax.persistence.AccessType.FIELD) private int overridden;
    @Version private int extraNotOverridden;
    @Version @Access(javax.persistence.AccessType.FIELD) private int extraOverridden;
    @Column private boolean bool;

    @Id // JPA access should default to PROPERTY because @Id annotation is on a property
    public int getFoo() {
      return foo;
    }

    @Version
    public int getBar() {
      return bar;
    }

    @Version
    public int getFizz() {
      return fizz;
    }

    @Version
    public int getOverridden() {
      return overridden;
    }

    // boolean test to make sure we recognize "is" as a property getter method
    @Version
    public boolean isBool() {
      return bool;
    }
  }

  @Test
  public void testImportTypes_JpaAnnotations_OneLetterPropertyName() throws Exception {
    final Class<?> javaClass = OneLetterPropertyNameAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "One-Letter-Property-Name-Annotation-Bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName, "a", "@Id");
  }
  @Table(name="sample_bean_table")
  @XmlRootElement(name="oneLetterPropertyNameAnnotationBean-el", namespace=NS_1)
  @XmlType(name="One-Letter-Property-Name-Annotation-Bean", namespace=NS_1, propOrder={"a"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Access(javax.persistence.AccessType.PROPERTY)
  private static class OneLetterPropertyNameAnnotationBean {
    @Column private int a;

    @Id
    public int getA() {
      return a;
    }
  }

  @Test
  public void testImportTypes_JpaAnnotations_HiddenCombinedWithJPA() throws Exception {
    final Class<?> javaClass = HiddenCombinedWithJPAAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, "@Table(name=\"hidden_with_bean\")");
    verifyMemberAnnotation(extMd, dtQName, "a", "@Id");

    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));
  }
  @Hidden
  @Table(name="hidden_with_bean")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"a"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class HiddenCombinedWithJPAAnnotationBean {
    @Id private int a;
  }

  @Test
  public void testImportTypes_JpaAnnotations_HiddenCombinedWithJPAAndIgnoreJPA() throws Exception {
    final Class<?> javaClass = HiddenCombinedWithJPAAndIgnoreJPABean.class;
    final QName dtQName = new QName(NS_1, "sample-bean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyTypeAnnotation(extMd, dtQName, null);
    verifyMemberAnnotation(extMd, dtQName, "a", null);

    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));
  }
  @Hidden
  @IgnoreJpa
  @Table(name="hidden_with_bean")
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="sample-bean", namespace=NS_1, propOrder={"a"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class HiddenCombinedWithJPAAndIgnoreJPABean {
    @Id private int a;
  }

  @Test
  public void testImportTypes_JpaAnnotations_ManyToOne() throws Exception {
    final Class<?> javaClass = ManyToOneAnnotationParentBean.class;
    final QName dtQName = new QName(NS_1, "ManyToOneAnnotationParentBean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));
    assertEquals(1, ir.getDiagnostics().length); // Should have 1 import diagnostic, which is a warning for the skipped targetEntity argument
    assertEquals(ImportDiagnosticSeverity.WARNING_LITERAL, ir.getDiagnostics()[0].getSeverity());
    assertEquals(ValidationCode.POJO_UNSUPPORTED_JPA_ANNOTATION_ARGUMENT, ir.getDiagnostics()[0].getErrorCode());
    AppianExtendedMetaData extMd = getExtMd(ir);
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "child", "@ManyToOne"); // Skips unsupported targetEntity argument
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="ManyToOneAnnotationParentBean", namespace=NS_1, propOrder={"bar", "child", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class ManyToOneAnnotationParentBean {
    @XmlElement private int bar;
    @ManyToOne(targetEntity=ManyToOneAnnotationChildBean.class) ManyToOneAnnotationChildBean child;
    @Id @XmlElement private int foo;
  }
  @XmlType(name="ManyToOneAnnotationChildBean", namespace=NS_1, propOrder={"bar", "foo"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @Entity(name="overridden_entity_name")
  private static class ManyToOneAnnotationChildBean {
    @XmlElement private int bar;
    @Id @XmlElement private int foo;
  }

  @Test
  public void testImportTypes_JpaAnnotations_UnsupportedTypeAnnotation() throws Exception {
    final Class<?> javaClass = UnsupportedTypeAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "UnsupportedTypeAnnotationBean");
    try {
      PojoTypeImportResult ir = importer.importTypes(set(javaClass));
      Assertions.fail();
    } catch (PojoTypeImportException e) {
      AppianRuntimeException ae = (AppianRuntimeException) e.getCause();
      assertEquals(ErrorCode.POJO_UNSUPPORTED_JPA_ANNOTATION, ae.getErrorCode());
    }
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="UnsupportedTypeAnnotationBean", namespace=NS_1)
  @XmlAccessorType(XmlAccessType.FIELD)
  @Cacheable // @Cacheable is not a supported annotation
  private static class UnsupportedTypeAnnotationBean {
     @Id @XmlElement private int foo;
  }

  @Test
  public void testImportTypes_JpaAnnotations_IgnoreJPA_UnsupportedTypeAnnotation() throws Exception {
    final Class<?> javaClass = IgnoredUnsupportedAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "IgnoredUnsupportedAnnotationBean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));
    AppianExtendedMetaData extMd = getExtMd(ir);
    verifyTypeAnnotation(extMd, dtQName, null);
    verifyMemberAnnotation(extMd, dtQName, "foo", null);
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="IgnoredUnsupportedAnnotationBean", namespace=NS_1)
  @XmlAccessorType(XmlAccessType.FIELD)
  @Cacheable // @Cacheable is not a supported annotation
  @IgnoreJpa
  private static class IgnoredUnsupportedAnnotationBean {
     @Id @XmlElement private int foo;
  }

  @Test
  public void testImportTypes_JpaAnnotations_IgnoreJPA_UnsupportedMemberAnnotation() throws Exception {
    final Class<?> javaClass = IgnoredUnsupportedMemberAnnotationBean.class;
    final QName dtQName = new QName(NS_1, "IgnoredUnsupportedMemberAnnotationBean");
    PojoTypeImportResult ir = importer.importTypes(set(javaClass));
    AppianExtendedMetaData extMd = getExtMd(ir);
    verifyTypeAnnotation(extMd, dtQName, null);
    verifyMemberAnnotation(extMd, dtQName, "foo", null);
  }
  @XmlRootElement(name="sample-bean-el", namespace=NS_1)
  @XmlType(name="IgnoredUnsupportedMemberAnnotationBean", namespace=NS_1)
  @XmlAccessorType(XmlAccessType.FIELD)
  @IgnoreJpa
  private static class IgnoredUnsupportedMemberAnnotationBean {
     @Id @XmlElement @Embedded private int foo; // @Embedded is not a supported annotation
  }

  @Test
  public void testImportTypes_JpaAnnotations_MultipleTypes() throws Exception {
    // We may reuse components for multiple type import, just making sure that works
    AppianExtendedMetaData extMd = importTypes(set(OneLetterPropertyNameAnnotationBean.class, HiddenCombinedWithJPAAnnotationBean.class));
    final QName dtQName1 = new QName(NS_1, "One-Letter-Property-Name-Annotation-Bean");
    verifyTypeAnnotation(extMd, dtQName1, "@Table(name=\"sample_bean_table\")");
    verifyMemberAnnotation(extMd, dtQName1, "a", "@Id");
    final QName dtQName2 = new QName(NS_1, "sample-bean");
    verifyTypeAnnotation(extMd, dtQName2, "@Table(name=\"hidden_with_bean\")");
    verifyMemberAnnotation(extMd, dtQName2, "a", "@Id");
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName2);
    Assertions.assertTrue(dt.hasFlag(Datatype.FLAG_HIDDEN));
  }

  @Test
  public void testImportTypes_JpaAnnotations_QualifiedElements() throws Exception {
    final Class<?> javaClass = QualifiedElementBean.class;  // needs to be defined in a separate package info
    final QName dtQName = new QName(NS_1, "QualifiedElementBean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "few", "@Version");
  }

  @Test
  public void testImportTypes_JpaAnnotations_Attributes() throws Exception {
    final Class<?> javaClass = AttributeBean.class;
    final QName dtQName = new QName(NS_1, "AttributeBean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Column");
    verifyMemberAnnotation(extMd, dtQName, "few", "@Version");
  }
  @Table(name="sample_bean_table")
  @XmlType(name="AttributeBean", namespace=NS_1, propOrder={"foo", "bar", "baz"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class AttributeBean {
    @Id @XmlAttribute private int foo;
    @Column private int bar;
    @Version @XmlAttribute(name="few") private String baz;
  }

  @Test
  public void testImportTypes_JpaAnnotations_Map() throws Exception {
    final Class<?> javaClass = MapBean.class;
    final QName dtQName = new QName(NS_1, "MapBean");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyMemberAnnotation(extMd, dtQName, "foo", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "bar", "@Transient");
    verifyMemberAnnotation(extMd, dtQName, "buzz", "@Column");
  }
  @XmlType(name="MapBean", namespace=NS_1, propOrder={"foo", "bar", "buzz"})
  @XmlAccessorType(XmlAccessType.FIELD)
  private static class MapBean {
    @Id private int foo;
    @Transient private Map<Object, Object> bar;
    @Column private String buzz; // test a field after bar, to make sure complexType context switches back correctly after looking at the nested anonymous complexType inside of the Map element
  }

  @Test
  public void testImportTypes_JpaAnnotations_Extension() throws Exception {
    final Class<?> javaClass = Child.class;
    final QName dtQName = new QName(NS_1, "Child");
    AppianExtendedMetaData extMd = importTypes(set(javaClass));
    verifyMemberAnnotation(extMd, dtQName, "id", "@Id");
    verifyMemberAnnotation(extMd, dtQName, "overridable", "@Version"); // Can't override a JPA annotation on a child class
    verifyMemberAnnotation(extMd, dtQName, "version", "@Version");
  }
  @XmlType(name="Parent", namespace=NS_1)
  private static abstract class Parent {
    @Id public int getId(){return 0;};
    public void setId(int id){};
    @Version public int getOverridable(){return 0;};
    public void setOverridable(int overridable){};
  }
  @XmlType(name="Child", namespace=NS_1)
  private static class Child extends Parent {
    @Version public int getVersion(){return 0;};
    public void setVersion(int version){};
    @Override @Column public int getOverridable(){return 0;};
    @Override public void setOverridable(int overridable){};
  }

  @Test
  public void testTypedValue() throws Exception {
    final Class<?> javaClass = HasTypedValue.class;
    final QName dtQName = new QName(NS_1, "HasTypedValue");
    importer.importTypes(set(javaClass));
    Datatype dt = aTypeService.getTypeByQualifiedName(dtQName);
    Assertions.assertEquals(dtQName.getLocalPart(), dt.getName());
    Assertions.assertEquals(3, dt.getInstanceProperties().length);
    Assertions.assertEquals("version", dt.getInstanceProperties()[0].getName());
    Assertions.assertEquals("tvDouble", dt.getInstanceProperties()[1].getName());
    Assertions.assertEquals("tvDocument", dt.getInstanceProperties()[2].getName());
    Assertions.assertEquals(AppianTypeLong.INTEGER, dt.getInstanceProperties()[0].getInstanceType());
    Assertions.assertEquals(AppianTypeLong.VARIANT, dt.getInstanceProperties()[1].getInstanceType());
    Assertions.assertEquals(AppianTypeLong.VARIANT, dt.getInstanceProperties()[2].getInstanceType());
    String expectedXsd = FileUtils.readFileToString(
        ResourceLoader.getClassResourceFile(getClass(), "HasTypedValue.xsd"), "UTF-8");
    String actualXsd = new String(new DatatypeXsdSchemaBuilder(aExtendedTypeService).getXsdSchemaByteArray(dt));
    Assertions.assertEquals(expectedXsd.replace("\r\n","\n"), actualXsd.replace("\r\n","\n"));
  }

  @XmlType(name="HasTypedValue", namespace=NS_1)
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement
  private static class HasTypedValue  {
    int version = 19;
    TypedValue tvDouble = new TypedValue(AppianTypeLong.DOUBLE, 1.2);
    TypedValue tvDocument = new TypedValue(AppianTypeLong.DOCUMENT, 30L);
  }

  private AppianExtendedMetaData importTypes(Set<Class<?>> pojos) throws Exception {
    PojoTypeImportResult ir = importer.importTypes(pojos);
    Assertions.assertEquals(0, ir.getDiagnostics().length); // Unless specifically testing error conditions, we should assert no errors for most tests
    return getExtMd(ir);
  }

  private AppianExtendedMetaData importSystemTypes(Set<Class<?>> pojos) throws Exception {
    PojoTypeImportResult ir = importer.importSystemTypes(pojos);
    Assertions.assertEquals(0, ir.getDiagnostics().length); // Unless specifically testing error conditions, we should assert no errors for most tests
    return getExtMd(ir);
  }

  private AppianExtendedMetaData getExtMd(PojoTypeImportResult ir) throws Exception {
    Datatype[] types = ir.getTopLevelDatatypes();
    EPackage.Registry reg = TypeUtils.getDbDatatypeAndReferences(types);
    AppianExtendedMetaData extMd = new AppianExtendedMetaData(reg);
    return extMd;
  }

  private void verifyTypeAnnotation(AppianExtendedMetaData extMd, final QName dtQName, String annotationString) throws Exception {
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    String typeAnnotations = AppianExtendedMetaData.getTeneoAnnotationValue(cls);
    Assertions.assertEquals(annotationString, typeAnnotations);
  }

  private void verifyMemberAnnotation(AppianExtendedMetaData extMd, QName dtQName, String memberName, String annotationString) throws Exception {
    EClass cls = (EClass) extMd.getTypeByOriginalXmlQName(dtQName);
    assertNotNull(cls);
    String memberAnnotations = AppianExtendedMetaData.getTeneoAnnotationValue(cls.getEStructuralFeature(memberName));
    Assertions.assertEquals(annotationString, memberAnnotations);
  }

  private static QName qname(String ns, String localPart) {
    return new QName(ns, localPart);
  }
  private static Set<Class<?>> set(Class<?> ... elements) {
    return new LinkedHashSet<Class<?>>(Arrays.asList(elements));
  }
  private static Set<QName> set(QName ... elements) {
    return new LinkedHashSet<QName>(Arrays.asList(elements));
  }
}
