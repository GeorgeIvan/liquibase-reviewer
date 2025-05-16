package com.appiancorp.security.acl;

import static com.appian.core.collections.Maps2.newImmutableMap;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Sets.intersection;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.appian.core.collections.Iterables2;
import com.appian.core.collections.Iterables2.Function3;
import com.appian.core.collections.Maps2;
import com.appiancorp.security.user.Group;
import com.appiancorp.security.user.User;
import com.appiancorp.suiteapi.type.Type;
import com.appiancorp.type.refs.GroupRef;
import com.appiancorp.type.refs.GroupRefImpl;
import com.appiancorp.type.refs.Ref;
import com.appiancorp.type.refs.UserRef;
import com.appiancorp.type.refs.UserRefImpl;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Entity
@Table(name=RoleMapEntry.TBL_ROLE_MAP_ENTRY)
@XmlType(namespace=Type.APPIAN_NAMESPACE, name=RoleMapEntry.LOCAL_PART)  // TODO: is this necessary with an adapter?
@XmlJavaTypeAdapter(XmlRoleMapEntryAdapter.class)
@XmlRootElement(namespace=Type.APPIAN_NAMESPACE, name="rolemapEntry") // casing determined by CDT spec (might need to be uppercase to match CDT serialization?)
@XmlSeeAlso({UserRefImpl.class, GroupRefImpl.class})
public class RoleMapEntry implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String LOCAL_PART = "RolemapEntry";  // casing from Cody's spec
  public static final QName QNAME = new QName(Type.APPIAN_NAMESPACE, LOCAL_PART);

  public static final String TBL_ROLE_MAP_ENTRY = "rm_entry";
  public static final String TBL_ROLE_MAP_ENTRY_USERS = "rm_entry_users";
  public static final String TBL_ROLE_MAP_ENTRY_GROUPS = "rm_entry_groups";

  public static final String COL_ID = "id";
  public static final String JOIN_COL_RM_ENTRY_ID = "rm_entry_id";
  public static final String JOIN_COL_ROLE_ID = "role_id";

  public static final String PROP_ID = "id";
  public static final String PROP_ROLE = "role";

  private Long id;
  private Role role;
  private Set<UserRef> users = new LinkedHashSet<UserRef>();
  private Set<GroupRef> groups = new LinkedHashSet<GroupRef>();

  public RoleMapEntry() {}

  public RoleMapEntry(Role role, Iterable<? extends UserRef> users, Iterable<? extends GroupRef> groups) {
    super();
    setRole(role);
    if (users != null) {
      addAll(this.users, users);
    }
    if (groups != null) {
      addAll(this.groups, groups);
    }
  }

  // copy-constructor
  public RoleMapEntry(RoleMapEntry rme) {
    Set<GroupRef> groups = Sets.newLinkedHashSet();
    for (GroupRef ref : rme.getGroups()) {
      groups.add(new GroupRefImpl(ref));
    }
    Set<UserRef> users = Sets.newLinkedHashSet();
    for (UserRef ref : rme.getUsers()) {
      users.add(new UserRefImpl(ref));
    }
    this.id = rme.id;
    this.role = rme.role;
    this.users = users;
    this.groups = groups;
  }

  /**
   * Constructor for a persisted instance.
   */
  public RoleMapEntry(Long id, Role role, Iterable<? extends UserRef> users, Iterable<? extends GroupRef> groups) {
    this(role, users, groups);
    setId(id);
  }

  @Column(name="id")
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional=false, cascade={CascadeType.REFRESH})
  @JoinColumn(name=JOIN_COL_ROLE_ID)
  public Role getRole() {
    return role;
  }
  public void setRole(Role role) {
    this.role = Preconditions.checkNotNull(role);
  }

  @ManyToMany(targetEntity=User.class, fetch=FetchType.EAGER) @Fetch(FetchMode.JOIN)
  @JoinTable(name=TBL_ROLE_MAP_ENTRY_USERS,
    joinColumns=@JoinColumn(name=JOIN_COL_RM_ENTRY_ID, nullable=false),
    inverseJoinColumns=@JoinColumn(name=User.JOIN_COL_USR_ID, nullable=false))
  public Set<UserRef> getUsers() {
    return users;
  }
  public void setUsers(Set<UserRef> users) {
    this.users = users;
  }

  @ManyToMany(targetEntity=Group.class, fetch=FetchType.EAGER) @Fetch(FetchMode.JOIN)
  @JoinTable(name=TBL_ROLE_MAP_ENTRY_GROUPS,
    joinColumns=@JoinColumn(name=JOIN_COL_RM_ENTRY_ID, nullable=false),
    inverseJoinColumns=@JoinColumn(name=Group.JOIN_COL_GROUP_ID, nullable=false))
  public Set<GroupRef> getGroups() {
    return groups;
  }
  public void setGroups(Set<GroupRef> groups) {
    this.groups = groups;
  }

  @Transient
  public Set<Object> getUserIds() {
    return getUsers().stream()
        .map(UserRef::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Transient
  public Set<String> getUserUuids() {
    return getUsers().stream()
        .map(UserRef::getUuid)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Transient
  public Set<Long> getGroupIds() {
    return getGroups().stream()
        .map(GroupRef::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Transient
  public Set<String> getGroupUuids() {
    return getGroups().stream()
        .map(GroupRef::getUuid)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Transient
  public boolean isEmpty() {
    return (users == null || users.isEmpty()) && (groups == null || groups.isEmpty());
  }

  @Override
  public String toString() {
    return role+"={users="+users+", groups="+groups+"}";
  }

  private static final Equivalence<RoleMapEntry> equalDataCheckInstance = new Equivalence<RoleMapEntry>() {
    @Override
    protected boolean doEquivalent(RoleMapEntry lhs, RoleMapEntry rhs) {
      if (!equal(lhs.role, rhs.role)) {
        return false;
      }
      return (equal(lhs.users, rhs.users) ||
          Iterables2.equal(lhs.users, rhs.users, refEqualDataCheck) ||
          Sets.symmetricDifference(lhs.users, rhs.users).size() == 0) &&
          (equal(lhs.groups, rhs.groups) ||
              Iterables2.equal(lhs.groups, rhs.groups, refEqualDataCheck) ||
              Sets.symmetricDifference(lhs.groups, rhs.groups).size() == 0);
    }
    @Override
    protected int doHash(RoleMapEntry t) {
      return com.google.common.base.Objects.hashCode(t.role, t.users, t.groups);
    }
  };
  public static Equivalence<RoleMapEntry> equalDataCheck() {
    return equalDataCheckInstance;
  }

  public static Predicate<RoleMapEntry> matchesRole(final Role r) {
    return new Predicate<RoleMapEntry>() {
      @Override public boolean apply(RoleMapEntry rme) {
        return r.equals(rme.getRole());
      }
    };
  }

  public static final Function<RoleMapEntry, Iterable<UserRef>> asUsers = new Function<RoleMapEntry, Iterable<UserRef>>() {
    @Override public Iterable<UserRef> apply(RoleMapEntry rme) {
      return rme.getUsers();
    }
  };
  public static final Function<RoleMapEntry, Iterable<Object>> asUserIds = new Function<RoleMapEntry, Iterable<Object>>() {
    @Override public Iterable<Object> apply(RoleMapEntry rme) {
      return rme.getUserIds();
    }
  };
  public static final Function<RoleMapEntry, Iterable<String>> asUserUuids = new Function<RoleMapEntry, Iterable<String>>() {
    @Override public Iterable<String> apply(RoleMapEntry rme) {
      return rme.getUserUuids();
    }
  };

  public static final Function<RoleMapEntry, Iterable<GroupRef>> asGroups = new Function<RoleMapEntry, Iterable<GroupRef>>() {
    @Override public Iterable<GroupRef> apply(RoleMapEntry rme) {
      return rme.getGroups();
    }
  };
  public static final Function<RoleMapEntry, Iterable<Long>> asGroupIds = new Function<RoleMapEntry, Iterable<Long>>() {
    @Override public Iterable<Long> apply(RoleMapEntry rme) {
      return rme.getGroupIds();
    }
  };
  public static final Function<RoleMapEntry, Iterable<String>> asGroupUuids = new Function<RoleMapEntry, Iterable<String>>() {
    @Override public Iterable<String> apply(RoleMapEntry rme) {
      return rme.getGroupUuids();
    }
  };

  public static final Function<RoleMapEntry, Role> asRole = new Function<RoleMapEntry, Role>() {
    @Override public Role apply(RoleMapEntry rme) {
      return rme.getRole();
    }
  };

  public static final Predicate<RoleMapEntry> isEmpty = new Predicate<RoleMapEntry>() {
      @Override public boolean apply(RoleMapEntry e) {
        return e.isEmpty();
      }
  };

  /**
   * @param userUuid
   * @param userGroupUuids
   *          the UUIDs of all groups that the user belongs to (the hierarchy of
   *          groups should already be flattened, no expansion of groups is done
   *          by this predicate).
   * @return a Predicate that evaluates to true if the user with the passed
   *         userGroupUuids and userUuid belongs to a given role. The Predicate
   *         evaluates to false otherwise.
   */
  public static Predicate<RoleMapEntry> hasPermission(final String userUuid, final Set<String> userGroupUuids) {
    return new Predicate<RoleMapEntry>() {
      @Override public boolean apply(RoleMapEntry r) {
        return r.getUserUuids().contains(userUuid) || intersects(r.getGroupUuids(), userGroupUuids);
      }
    };
  }

  /**
   * @param username
   * @param userGroupIds
   *          the ids of all groups that the user belongs to (the hierarchy of
   *          groups should already be flattened, no expansion of groups is done
   *          by this predicate).
   * @return true if the user or a group the user belongs to is part of the role map entry
   */
  public boolean hasPermissionInRole(final String username, final Set<Long> userGroupIds) {
    for (UserRef user : getUsers()) {
      if (username.equals(user.getUsername())) {
        return true;
      }
    }

    if (intersects(userGroupIds, getGroupIds())) {
      return true;
    }
    return false;
  }

  /**
   * @return true if the intersection of sets {@code a} and {@code b} is not
   *         empty. False otherwise.
   */
  private static <E> boolean intersects(Set<E> a, Set<?> b) {
    if (a == null || b == null) {
      return false;
    }
    return !intersection(a, b).isEmpty();
  }

  public static Iterable<RoleMapEntry> mergeEntries(Set<RoleMapEntry> existingEntries, Set<RoleMapEntry> newEntries, PermissionMergeStrategy strategy) {
    Map<Role, RoleMapEntry> existingEntriesByRole = newImmutableMap(existingEntries, asRole);
    Map<Role, RoleMapEntry> newEntriesByRole = newImmutableMap(newEntries, asRole);
    Map<Role, RoleMapEntry> appliedRoleMap = Maps2.merge(existingEntriesByRole, newEntriesByRole, RoleMapEntry.roleMapEntryMerger(strategy));
    return appliedRoleMap.values();
  }

  public static Function3<Role, RoleMapEntry, RoleMapEntry, RoleMapEntry> roleMapEntryMerger(final PermissionMergeStrategy strategy) {
    return new Function3<Role, RoleMapEntry, RoleMapEntry, RoleMapEntry>() {
      @Override public RoleMapEntry apply(Role r, RoleMapEntry existingEntry, RoleMapEntry newEntry) {
        return applyRole(existingEntry, newEntry, strategy);
      }
    };
  }

  /**
   * Returns a new {@code RoleMapEntry} that is the result of applying the
   * {@code strategy} to {@code existingRoleMapEntry} and {@code newRoleMapEntry}. For example,
   * if the strategy was {@code ADDING_STRATEGY}, this would return a new
   * {@code RoleMapEntry} with the groups and users of both {@code newRoleMapEntry} and
   * {@code existingRoleMapEntry}.
   */
  public static RoleMapEntry applyRole(RoleMapEntry existingRoleMapEntry, RoleMapEntry newRoleMapEntry, PermissionMergeStrategy strategy) {
    if (null == newRoleMapEntry && null == existingRoleMapEntry) {
      throw new IllegalArgumentException("At least one passed roleMapEntry must not be null.");
    }
    Role r = firstNonNull(newRoleMapEntry, existingRoleMapEntry).getRole();

    Set<UserRef> newUsers = ImmutableSet.of();
    Set<GroupRef> newGroups = ImmutableSet.of();
    if (null != newRoleMapEntry) {
      newUsers = newRoleMapEntry.getUsers();
      newGroups = newRoleMapEntry.getGroups();
    }

    Set<UserRef> existingUsers = ImmutableSet.of();
    Set<GroupRef> existingGroups = ImmutableSet.of();
    if (null != existingRoleMapEntry) {
      existingUsers = existingRoleMapEntry.getUsers();
      existingGroups = existingRoleMapEntry.getGroups();
    }

    Set<UserRef> finalUsers = applyStrategy(existingUsers, newUsers, strategy);
    Set<GroupRef> finalGroups = applyStrategy(existingGroups, newGroups, strategy);
    return new RoleMapEntry(r, finalUsers, finalGroups);
  }

  private static <T> Set<T> applyStrategy(Set<T> existingSet, Set<T> passedSet, PermissionMergeStrategy strategy) {
    if (null == existingSet) {
      existingSet = ImmutableSet.of();
    }
    return strategy.apply(existingSet, passedSet);
  }

  /**
   * Takes a set of existing permissions and a set of new permissions and
   * decides how to combine them into a result set.
   *
   * Uses hashCode/equals for set operations.
   *
   * Implementors of this interface should not modify the input sets. Instead,
   * return a new set or a set that represents a new view of the inputs.
   */
  public static interface PermissionMergeStrategy {
    <T> Set<T> apply(Set<T> existingPermissions, Set<T> newPermissions);
  }

  /**
   * Returns the result of adding {@code newPermissions} to
   * {@code existingPermissions}. If any new permissions already exist, they are
   * ignored.
   */
  public static final PermissionMergeStrategy ADDING_STRATEGY = new PermissionMergeStrategy() {
    @Override public <T> Set<T> apply(Set<T> existingPermissions, Set<T> newPermissions) {
      return Sets.union(existingPermissions, newPermissions);
    }
  };

  /**
   * Returns the result of removing {@code newPermissions} from {@code existingPermissions}.
   */
  public static final PermissionMergeStrategy REMOVING_STRATEGY = new PermissionMergeStrategy() {
    @Override public <T> Set<T> apply(Set<T> existingPermissions, Set<T> newPermissions) {
      return Sets.difference(existingPermissions, newPermissions);
    }
  };

  /**
   * Returns {@code newPermissions}.
   */
  public static final PermissionMergeStrategy REPLACING_STRATEGY = new PermissionMergeStrategy() {
    @Override public <T> Set<T> apply(Set<T> existingPermissions, Set<T> newPermissions) {
      return newPermissions;
    }
  };

  private static final Equivalence<Ref> refEqualDataCheck = new Equivalence<Ref>() {
    @Override
    protected boolean doEquivalent(Ref lhs, Ref rhs) {
      return equal(lhs.getId(), rhs.getId()) && equal(lhs.getUuid(), rhs.getUuid());
    }

    @Override
    protected int doHash(Ref ref) {
      final int prime = 31;
      int result = 1;
      result = prime * result + (ref.getId() == null ? 0 : ref.getId().hashCode());
      result = prime * result + (ref.getUuid() == null ? 0 : ref.getUuid().hashCode());
      return result;
    }
  };
}
