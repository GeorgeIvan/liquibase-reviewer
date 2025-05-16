package com.appiancorp.type.refs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.appian.core.persist.Constants;
import com.appiancorp.sites.Site;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.Id;
import com.appiancorp.type.Uuid;
import com.appiancorp.type.refs.XmlSiteRefAdapter.SiteRefImpl;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class XmlSiteRefAdapter extends XmlAdapter<SiteRefImpl,SiteRef> {
  @Override
  public SiteRef unmarshal(SiteRefImpl v) throws Exception {
    return v;
  }

  @Override
  public SiteRefImpl marshal(SiteRef v) throws Exception {
    if (v instanceof SiteRefImpl) {
      return (SiteRefImpl) v;
    } else {
      return v == null ? null : new SiteRefImpl(v);
    }
  }

  @Entity
  @Table(name = "site")
  @XmlType(name = Site.LOCAL_PART, namespace = Type.APPIAN_NAMESPACE,
           propOrder = {"id", "uuid"})
  @XmlAccessorType(XmlAccessType.FIELD)
  @VisibleForTesting
  public static class SiteRefImpl implements SiteRef {
    private static final long serialVersionUID = 1L;

    @javax.persistence.Id
    @Column(name = "id")
    @XmlAttribute(name = Id.LOCAL_PART, namespace = Id.NAMESPACE)
    private Long id;
    @Column(name = "uuid", updatable = false, nullable = false, unique = true, length = Constants.COL_MAXLEN_UUID)
    @XmlAttribute(name = Uuid.LOCAL_PART, namespace = Uuid.NAMESPACE)
    private String uuid;

    public SiteRefImpl() {}

    public SiteRefImpl(SiteRef o) {
      this.id = o == null ? null : o.getId();
      this.uuid = o == null ? null : o.getUuid();
    }

    public SiteRefImpl(Site o) {
      this.id = o == null ? null : o.getId();
      this.uuid = o == null ? null : o.getUuid();
    }

    public SiteRefImpl(Long id, String uuid) {
      this.id = id;
      this.uuid = uuid;
    }
    public SiteRefImpl(Long id) {
      this.id = id;
    }
    public SiteRefImpl(String uuid) {
      this.uuid = uuid;
    }

    @Override
    public Ref<Long,String> build(Long id, String uuid) {
      return new SiteRefImpl(id, uuid);
    }

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public String getUuid() {
      return uuid;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(SiteRef.class).add("id", id).add("uuid", uuid).toString();
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(id, uuid);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass())  {
        return false;
      }
      SiteRefImpl o = (SiteRefImpl) obj;
      return Objects.equal(uuid, o.uuid);
    }

  }
}
