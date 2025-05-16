package com.appiancorp.rdbms.hb;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.QueryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.appiancorp.common.persistence.GenericDao;
import com.appiancorp.common.persistence.PropertiesSubset;
import com.appiancorp.common.query.Criteria;
import com.appiancorp.common.query.Query;
import com.appiancorp.common.query.TypedValueQuery;
import com.appiancorp.common.query.TypedValueQuery.TypedValueBuilder.FilterOpLiteral;
import com.appiancorp.common.query.TypedValueQuery.TypedValueBuilder.LogicalOp;
import com.appiancorp.rdbms.common.schema.SchemaHandlingOption;
import com.appiancorp.rdbms.crossdb.CrossDbTestCase;
import com.appiancorp.rdbms.crossdb.CrossDbTestExtension;
import com.appiancorp.suiteapi.common.paging.PagingInfo;
import com.appiancorp.suiteapi.common.paging.SortInfo;
import com.google.common.collect.ImmutableSet;

public class GenericDaoHbImplSortTest {

  @RegisterExtension public static CrossDbTestExtension crossDbTestExtension = CrossDbTestExtension.builder()
      .withEntities(ImmutableSet.of(TestEntity.class))
      .withSchemaHandlingOption(SchemaHandlingOption.CREATE_DROP)
      .withSpringConfigs(ImmutableSet.of(TestEntitySpringCfg.class))
      .build();

  private static final String LONG_FIELD_NAME = "longIdField";
  private static final String STRING_FIELD_NAME = "stringField";
  private static final String NAME_FIELD_NAME = "name";
  private static final TestEntity ENTITY1 = new TestEntity(8L, "Stringvalue1", "first");
  private static final TestEntity ENTITY2 = new TestEntity(9L, "stringvalue3", "second");
  private static final TestEntity ENTITY3 = new TestEntity(10L, "stringValue2", "third");

  @BeforeAll
  public static void setupDb(Collection<CrossDbTestCase> testCases) {
    for (CrossDbTestCase testCase : testCases) {
      TestEntityDao testDao = testCase.getBean(TestEntityDao.class);
      testDao.create(ENTITY1);
      testDao.create(ENTITY2);
      testDao.create(ENTITY3);
    }
  }

  @TestTemplate
  public void testQuery_invalidField(CrossDbTestCase testCase) throws Exception {
    try {
      query("invalid", testCase);
      Assertions.fail("Expected a QueryException, but no exception was thrown");
    } catch (QueryException expected) {}
  }

  @TestTemplate
  public void testQuery_nullField(CrossDbTestCase testCase) throws Exception {
    try {
      query(null, testCase);
      Assertions.fail("Expected a QueryException, but no exception was thrown");
    } catch (QueryException expected) {}
  }

  @TestTemplate
  public void testQuery_sortLongField(CrossDbTestCase testCase) throws Exception {
    List<Object[]> results = query(LONG_FIELD_NAME, testCase);
    Assertions.assertEquals(ENTITY1.name, results.get(0)[0]);
    Assertions.assertEquals(ENTITY2.name, results.get(1)[0]);
    Assertions.assertEquals(ENTITY3.name, results.get(2)[0]);
  }

  @TestTemplate
  public void testQuery_sortStringField(CrossDbTestCase testCase) throws Exception {
    List<Object[]> results = query(STRING_FIELD_NAME, testCase);
    Assertions.assertEquals(ENTITY1.name, results.get(0)[0]);
    Assertions.assertEquals(ENTITY3.name, results.get(1)[0]);
    Assertions.assertEquals(ENTITY2.name, results.get(2)[0]);
  }

  private List<Object[]> query(String sortField, CrossDbTestCase testCase) throws Exception {
    PagingInfo pagingInfo = new PagingInfo(0, PagingInfo.UNLIMITED_BATCH_SIZE);
    pagingInfo.getSort().add(new SortInfo(sortField, true));
    Criteria criteria = LogicalOp.and(FilterOpLiteral.isNotNull(NAME_FIELD_NAME));

    Query query = TypedValueQuery.builder()
        .select(NAME_FIELD_NAME, LONG_FIELD_NAME, STRING_FIELD_NAME)
        .criteria(criteria)
        .page(pagingInfo)
        .build();

    TestEntityDao testDao = testCase.getBean(TestEntityDao.class);
    PropertiesSubset entities = testDao.query(query);
    return entities.getResults();
  }

  @Configuration
  @EnableTransactionManagement
  public static class TestEntitySpringCfg {
    @Bean
    public TestEntityDao testEntityDao(DaoContext daoContext) {
      return new TestEntityDaoHbImpl(daoContext);
    }
  }

  public interface TestEntityDao extends GenericDao<TestEntity,Long> {
    PropertiesSubset query(Query query);
  }

  private static class TestEntityDaoHbImpl extends GenericDaoHbImpl<TestEntity,Long>
      implements TestEntityDao {
    public TestEntityDaoHbImpl(DaoContext daoContext) {
      super(daoContext);
    }

    @Override
    @Transactional
    public PropertiesSubset query(Query query) {
      Supplier criteraSupplier = () -> getSession().createCriteria(TestEntity.class);
      return super.query(criteraSupplier, criteraSupplier, query);
    }

    @Override
    @Transactional
    public Long create(TestEntity testEntity) {
      return super.create(testEntity);
    }
  }

  @Entity
  @Table(name="generic_dao_test_entity")
  private static final class TestEntity {
    @Id @Column(name = LONG_FIELD_NAME)
    private Long longIdField;

    @Column(name = STRING_FIELD_NAME)
    private String stringField;

    @Column(name = NAME_FIELD_NAME)
    private String name;

    private TestEntity(Long longIdValue, String stringValue, String name) {
      this.longIdField = longIdValue;
      this.stringField = stringValue;
      this.name = name;
    }
  }
}
