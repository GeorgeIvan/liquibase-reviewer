package com.appiancorp.type.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

// Facts:
// 1. Here we shouldn't use any additional indexing instead of PK-ID. Since the only query we will run
//    is to based on the id column. Additional indexing will be waste of resource.
// 2. Since ChangeManagement will make the NAME column unique, UniqueConstraint should be added solely
//    for the NAME column.
//
// Tricky Part:
// 1. Setting Name column unique will work fine for Oracle, SQLServer and some other DBMSs. But MySQL only
//    support max key length to be 767 bytes (i.e. 255 chars for UTF8), and it will give
//    "Specified key was too long" error when the key is longer than 255 chars.
// 2. Keep in mind that we don't want to do any special handling for any specific RDBMS.
//    So for now (name column max length is 255 chars), the uniqueness flag is on. If we decide to limit
//    max length for "Name" to be greater than 256 chars. This flag will turn off automatically.
//
// Ugly Part:
//    We have to rename the "model" field to "xmodel". It turns out Oracle wants the large LOB column
//      to be the last one in an insert statement, while Hibernate (after version 3.2.1) decided to
//      order the columns for us and put the LOB column ahead of other varchar columns. And this
//      left us a famous "ORA-24816: Expanded non LONG bind data supplied after actual LONG or LOB column".
//    Please see:
//      http://www.odi.ch/weblog/posting.php?posting=496
//      http://forums.oracle.com/forums/thread.jspa?threadID=415560&tstart=15
//      https://forum.hibernate.org/viewtopic.php?f=6&t=974532
//      https://forum.hibernate.org/viewtopic.php?f=6&t=974532
//    Since there is no way right now to turn off hibernate ordering feature or specify orders through
//      annotations, we have to rename the field to "xmodel" so it will be ordered in the last.
@Entity
@Table(name="DT_MODEL")
public class DatatypeModel {
  public static final String PROP_NAMESPACE_URI = "namespaceUri";
  protected static final int MINIMAL_SETTING_OF_MAX_KEY_LENGTH_SUPPORTED = 255;
  protected static final int MAX_LENGTH_NAMESPACE_URI = 1024;
  protected static final int MAX_LENGTH_NAME = 255;
  protected static final int MAX_LENGTH_XMODEL = 32768;

  private Long id;
  private String namespaceUri; // keep name in sync with PROP_NAMESPACE_URI
  private String name;
  private String xmodel;
  private Timestamp createdTs;

  public DatatypeModel() {}

  public DatatypeModel(QName qname, String xmodel) {
    this.namespaceUri = qname.getNamespaceURI();
    this.name = qname.getLocalPart();
    this.xmodel = xmodel;
    this.createdTs = new Timestamp(System.currentTimeMillis());
  }

  @Id @GeneratedValue
  @Column(name="ID")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @Column(name="NAMESPACE_URI", length=MAX_LENGTH_NAMESPACE_URI, nullable=false)
  public String getNamespaceUri() {
    return namespaceUri;
  }
  public void setNamespaceUri(String namespaceUri) {
    this.namespaceUri = namespaceUri;
  }

  @Column(name="NAME", length=MAX_LENGTH_NAME, nullable=false,
      unique=(MAX_LENGTH_NAME > MINIMAL_SETTING_OF_MAX_KEY_LENGTH_SUPPORTED? false: true))
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Transient
  public QName getQName() {
    return new QName(namespaceUri, name);
  }
  public void setQName(QName qname) {
    setNamespaceUri(qname.getNamespaceURI());
    setName(qname.getLocalPart());
  }

  @Column(name="XMODEL", length=MAX_LENGTH_XMODEL, nullable=false) @Lob
  public String getXmodel() {
    return xmodel;
  }
  public void setXmodel(String xmodel) {
    this.xmodel = xmodel;
  }

  @Transient
  public Timestamp getCreatedTs() {
    return createdTs;
  }
  public void setCreatedTs(Timestamp createdTs) {
    this.createdTs = createdTs;
  }

  @Column(name="CREATED_TS", nullable=false, updatable=false)
  private Long getCreatedTsLong() {
    return createdTs == null ? null : createdTs.getTime();
  }
  @SuppressWarnings("unused")
  private void setCreatedTsLong(Long createdTsLong) {
    this.createdTs = createdTsLong == null ? null : new Timestamp(createdTsLong);
  }

  @Override
  public String toString() {
    return "DatatypeModel[id=" + id + ", nsUri=" + namespaceUri + ", name=" + name + "]";
  }

  public static List<Long> getIds(List<DatatypeModel> dtms) {
    List<Long> ids = new ArrayList<Long>();
    for (DatatypeModel dtm : dtms) {
      ids.add(dtm.getId());
    }
    return ids;
  }

  public static Set<Long> getIds(Set<DatatypeModel> dtms) {
    Set<Long> ids = new HashSet<Long>();
    for (DatatypeModel dtm : dtms) {
      ids.add(dtm.getId());
    }
    return ids;
  }

  public static Map<Long, DatatypeModel> getIdToDatatypeModelMap(List<DatatypeModel> dtms) {
    Map<Long, DatatypeModel> m = new HashMap<Long, DatatypeModel>();
    for (DatatypeModel dtm : dtms) {
      m.put(dtm.getId(), dtm);
    }
    return m;
  }

  public static Map<QName, DatatypeModel> getQNameToDatatypeModelMap(List<DatatypeModel> dtms) {
    Map<QName, DatatypeModel> m = new HashMap<QName, DatatypeModel>();
    for (DatatypeModel dtm : dtms) {
      m.put(dtm.getQName(), dtm);
    }
    return m;
  }
}
