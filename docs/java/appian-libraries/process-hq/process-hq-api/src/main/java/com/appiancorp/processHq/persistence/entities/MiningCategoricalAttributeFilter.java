package com.appiancorp.processHq.persistence.entities;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.appian.core.persist.Constants;
import com.appiancorp.copilot.annotation.CopilotClass;
import com.appiancorp.copilot.annotation.CopilotField;
import com.appiancorp.core.expr.portable.annotations.VisibleForTesting;
import com.appiancorp.miningdatasync.data.MiningProcessField;

@Entity
@Table(name = "mining_categ_attr_fltr")
@CopilotClass(name = "attributeFilter", description = "A filter for selecting Cases with the selected attribute's value.")
public class MiningCategoricalAttributeFilter implements MiningAttributeFilter {
  public static final String PROP_ID = "id";
  public static final String PROP_NUMBER_OF_VALUES = "numberOfValues";
  private static final EscapeSequence BACKSLASH = new EscapeSequence("\\", "\\\\");
  private static final EscapeSequence TWO_BACKSLASHES = new EscapeSequence("\\\\", "\\\\\\\\");
  private static final String SEMI_COLON = ";";
  @VisibleForTesting public static final String DELIMITER = "; ";
  // reg-exes need to be doubled, so to find a single \, we need to use the doubled backlash here
  @VisibleForTesting public static final Pattern BACKSLASH_PATTERN = Pattern.compile(BACKSLASH.doubled);

  private long id;
  private MiningFilterGroup miningFilterGroup;
  private String attributeName;
  private MiningProcessField miningProcessField;
  @Access(AccessType.FIELD)
  @Column(name = "inverted", nullable = false)
  private boolean inverted;
  private byte[] values;
  private Long numberOfValues;
  private ProcessMiningFilterOperator operator = ProcessMiningFilterOperator.IN;
  @Access(AccessType.FIELD)
  @Column(name = "created_ts", nullable = false)
  private Long createdTs;

  public MiningCategoricalAttributeFilter() {}

  public MiningCategoricalAttributeFilter(
    Long id, MiningFilterGroup miningFilterGroup,
      MiningProcessField miningProcessField, byte[] values,
      ProcessMiningFilterOperator operator) {
    this.id = id;
    this.miningFilterGroup = miningFilterGroup;
    this.miningProcessField = miningProcessField;
    this.values = values;
    this.operator = operator;
  }

  @javax.persistence.Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mining_filter_group_id", referencedColumnName = "id", nullable = false)
  public MiningFilterGroup getFilterGroup() {
    return miningFilterGroup;
  }

  public void setFilterGroup(MiningFilterGroup miningFilterGroup) {
    this.miningFilterGroup = miningFilterGroup;
  }

  @Override
  @Column(name = "attribute_name", nullable = false, length = Constants.COL_MAXLEN_MAX_NON_CLOB)
  @CopilotField(name = "attribute", description = "The name of the attribute selected for this filter.")
  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "mining_process_field_id", referencedColumnName = "id")
  public MiningProcessField getMiningProcessField() {
    return miningProcessField;
  }

  public void setMiningProcessField(MiningProcessField miningProcessField) {
    this.miningProcessField = miningProcessField;
  }

  @Override
  @CopilotField(name = "inverted", description = "Indicates if the filter should equal or not equal the activity")
  public boolean isInverted() {
    return operator.isInverted();
  }

  @Column(name = "filter_values")
  @Lob
  public byte[] getValues() {
    return values;
  }

  @Override
  @Transient
  public List<String> getValuesList() {
    return decodeFilterValuesFromBytes(values);
  }

  public void setValues(byte[] values) {
    this.values = values;
  }

  @Transient
  @CopilotField(name = "filterValues", description = "The list of categorical attribute values that this filter applies to")
  public List<String> getFilterValues() {
    byte[] values = getValues();
    return values == null ? null : decodeFilterValuesFromBytes(values);
  }

  public static List<String> decodeFilterValuesFromBytes(byte[] attributeValues) {
    return decodeFilterValues(new String(attributeValues, StandardCharsets.UTF_8));
  }

  @VisibleForTesting
  public static List<String> decodeFilterValues(String joinedFilterValues) {
    if (joinedFilterValues.isEmpty()) {
      // ASSUMPTION: The only way the attribute values in joinedFilterValues is empty is when we encoded a List with a single empty string
      return Collections.singletonList("");
    }

    List<String> splitFilterValues = new ArrayList<>();
    StringBuilder currentFilter = new StringBuilder();
    int i = 0;
    while (i < joinedFilterValues.length()) {
      if (i + 2 <= joinedFilterValues.length() && joinedFilterValues.startsWith(BACKSLASH.single + SEMI_COLON, i)) {
        currentFilter.append(SEMI_COLON);
        i += 2;
      } else if (i + 2 <= joinedFilterValues.length() && joinedFilterValues.startsWith(DELIMITER, i)) {
        splitFilterValues.add(currentFilter.toString());
        currentFilter = new StringBuilder();
        i += 2;
      } else if (i + 1 < joinedFilterValues.length() && joinedFilterValues.startsWith(BACKSLASH.doubled, i)) {
        currentFilter.append(BACKSLASH.single);
        i += 2;
      } else {
        currentFilter.append(joinedFilterValues.charAt(i));
        i += 1;
      }
    }
    splitFilterValues.add(currentFilter.toString());
    return splitFilterValues;
  }

  public static byte[] encodeFilterValuesToByte(List<String> attributeValues) {
    return encodeFilterValues(attributeValues).getBytes(StandardCharsets.UTF_8);
  }

  @VisibleForTesting
  public static String encodeFilterValues(List<String> filterValues) {
    return filterValues.stream()
        .map(MiningCategoricalAttributeFilter::escapeFilterValue)
        .collect(Collectors.joining(DELIMITER));
  }

  static String escapeFilterValue(String filterValue) {
    String s = BACKSLASH_PATTERN.matcher(filterValue).replaceAll(TWO_BACKSLASHES.doubled);
    return s.replace(SEMI_COLON, BACKSLASH.single + SEMI_COLON);
  }

  @Transient
  public ProcessMiningFilterOperator getOperator() {
    return operator;
  }

  public void setOperator(ProcessMiningFilterOperator operator) {
    checkNotNull(operator);
    this.operator = operator;
  }

  @Column(name = "operator", nullable = false)
  private Byte getOperatorByte() {
    return operator.getCode();
  }

  private void setOperatorByte(Byte type) {
    setOperator(ProcessMiningFilterOperator.valueOf(type));
  }

  @Column(name = "number_of_values")
  public Long getNumberOfValues() {
    return numberOfValues;
  }

  public void setNumberOfValues(Long numberOfValues) {
    this.numberOfValues = numberOfValues;
  }

  @Override
  public Long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(Long createdTs) {
    this.createdTs = createdTs;
  }

  @PrePersist
  @PreUpdate
  private void onPrePersistOrUpdate() {
    this.numberOfValues = values == null ? null : decodeFilterValuesFromBytes(values).stream().count();
    this.inverted = operator.isInverted();
  }

  public boolean contentsEquals(Object object) {
    if (!(object instanceof MiningCategoricalAttributeFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningCategoricalAttributeFilter other = ((MiningCategoricalAttributeFilter) object);
    return equal(this.getAttributeName(), other.getAttributeName()) &&
        equal(this.getMiningProcessField(), other.getMiningProcessField()) &&
        equal(this.getOperator(), other.getOperator()) &&
        Arrays.equals(this.getValues(), other.getValues());
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof MiningCategoricalAttributeFilter)) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final MiningCategoricalAttributeFilter other = ((MiningCategoricalAttributeFilter) object);
    return equal(this.getAttributeName(), other.getAttributeName()) &&
        equal(this.getMiningProcessField(), other.getMiningProcessField()) &&
        equal(this.getOperator(), other.getOperator()) &&
        Arrays.equals(this.getValues(), other.getValues()) &&
        equal(this.getCreatedTs(), other.getCreatedTs());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getAttributeName(), Arrays.hashCode(this.getValues()), this.getOperator(), this.getMiningProcessField(), this.getCreatedTs());
  }

  @Override
  public String toString() {
    return "MiningCategoricalAttributeFilter{" + "id=" + id + ", attributeName='" + attributeName + '\'' +
        ", miningProcessField=" + miningProcessField + ", operator=" + operator + ", values=" +
        Arrays.toString(values) + ", createdTs=" + createdTs + "}";
  }

  private record EscapeSequence(String single, String doubled) {}
}
