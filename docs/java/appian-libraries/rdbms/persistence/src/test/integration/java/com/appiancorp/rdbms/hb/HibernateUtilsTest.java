package com.appiancorp.rdbms.hb;

import static com.appiancorp.rdbms.hb.HibernateUtils.getInOperatorMaxListSize;
import static com.appiancorp.rdbms.hb.HibernateUtils.splitForInConstraint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.appiancorp.rdbms.common.schema.SchemaHandlingOption;
import com.appiancorp.rdbms.crossdb.CrossDbTestCaseImpl;
import com.appiancorp.rdbms.crossdb.CrossDbTestExtension;
import com.appiancorp.test.util.rdbms.HibernateSqlLogHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class HibernateUtilsTest {

  @RegisterExtension public static CrossDbTestExtension crossDbTestExtension = CrossDbTestExtension.builder()
      .withEntities(ImmutableSet.of(Blog.class))
      .withSchemaHandlingOption(SchemaHandlingOption.CREATE_DROP)
      .build();

  private static HibernateSqlLogHelper hbSqlLogHelper;
  private static int OBJECT_CREATION_COUNT = 2200;


  @BeforeAll
  public static void setupDb() {
    hbSqlLogHelper = new HibernateSqlLogHelper();
  }

  @Entity
  @Table(name = "BLOG")
  public static class Blog {
    @Id @GeneratedValue @Column(name = "ID") public Long id;

    @Column(name = "CATEGORYID", nullable = true) public Long categoryId;
  }

  /* AN-50073: The purpose of this test is to capture two potential failure points:
   * 1. Oracle's limit of 1000 values in the IN operator of the WHERE clause
   * 2. SQL Server's limit of 2100 parameters (com.microsoft.sqlserver.jdbc.SQLServerException: The incoming
   * tabular data stream (TDS)
   * remote procedure call (RPC) protocol stream is incorrect. Too many parameters were provided in this RPC
   * request. The maximum is 2100.)
   * The value of OBJECT_CREATION_COUNT is high enough to ensure full coverage. */

  @TestTemplate
  public void testHibernateUtils_SqlBatchUpdateWithNull(CrossDbTestCaseImpl testCase) throws Exception {
    SessionFactory sessionFactory = testCase.getDataSourceManager().getSessionFactory();
    Session session = sessionFactory.getCurrentSession();
    Transaction transaction = session.beginTransaction();
    // Populate data for the test.
    try {
      List<Long> ids = new ArrayList<Long>();
      Blog tbl;
      for (int i = 0; i < OBJECT_CREATION_COUNT; i++) {
        tbl = new Blog();
        // NOTE: We don't actually care what the categoryID is, just that it gets set to null.
        tbl.categoryId = Long.valueOf(i);
        ids.add(Long.valueOf(i));
        session.save(tbl);
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      transaction.commit();

      session = sessionFactory.getCurrentSession();
      transaction = session.beginTransaction();

      HibernateUtils.sqlBatchUpdateToNull(session, "BLOG", "CATEGORYID", "CATEGORYID", ids);

      transaction.commit();
      session = sessionFactory.getCurrentSession();
      transaction = session.beginTransaction();

      Criteria c = session.createCriteria(Blog.class);
      @SuppressWarnings("unchecked")
      List<Blog> list = c.list();
      transaction.commit();
      for (Blog entry : list) {
        assertNull(entry.categoryId);
      }
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }


  @TestTemplate
  public void testHibernateUtils_SelectWithInConstraint(CrossDbTestCaseImpl testCase) {
    SessionFactory sessionFactory = testCase.getDataSourceManager().getSessionFactory();
    Session session = sessionFactory.getCurrentSession();
    Transaction transaction = session.beginTransaction();
    // Populate data for the test.
    try {
      List<Long> ids = new ArrayList<Long>();
      Blog tbl;
      for (int i = 0; i < OBJECT_CREATION_COUNT; i++) {
        tbl = new Blog();
        // NOTE: We don't actually care what the categoryID is, just that it gets set to null.
        tbl.categoryId = Long.valueOf(i);
        ids.add(Long.valueOf(i));
        session.save(tbl);
        if (i % 20 == 0) {
          session.flush();
          session.clear();
        }
      }
      transaction.commit();

      session = sessionFactory.getCurrentSession();
      transaction = session.beginTransaction();

      hbSqlLogHelper.startCapture();
      List<Blog> queryResults = HibernateUtils.selectWithInConstraint(session, Blog.class, "categoryId", ids);
      hbSqlLogHelper.stopCapture();

      transaction.commit();

      // One query for the first 1000, one for the second 1000, and one for the remaining 200
      hbSqlLogHelper.assertNumSqlStatements(3);
      // Should get back all Blog objects
      Assertions.assertEquals(OBJECT_CREATION_COUNT, queryResults.size());
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }

  @Test
  public void testHibernateUtils_SplitForInConstraint() {
    try {
      splitForInConstraint(null);
      Assertions.fail("Expected " + NullPointerException.class);
    } catch (NullPointerException e) {}

    List<List<Integer>> r = splitForInConstraint(generateList(0));
    assertEquals(0, r.size());

    r = splitForInConstraint(generateList(1));
    assertEquals(1, r.size());
    assertEquals(generateList(1), r.get(0));

    r = splitForInConstraint(generateList(getInOperatorMaxListSize()));
    assertEquals(1, r.size());
    assertEquals(generateList(getInOperatorMaxListSize()), r.get(0));

    r = splitForInConstraint(generateList(getInOperatorMaxListSize() + 1));
    assertEquals(2, r.size());
    assertEquals(generateList(getInOperatorMaxListSize()), r.get(0));
    assertEquals(Lists.newArrayList(getInOperatorMaxListSize()), r.get(1));

    r = splitForInConstraint(generateList(2 * getInOperatorMaxListSize()));
    assertEquals(2, r.size());
    assertEquals(generateList(getInOperatorMaxListSize()), r.get(0));
    assertEquals(generateList(getInOperatorMaxListSize(), getInOperatorMaxListSize()), r.get(1));

    r = splitForInConstraint(generateList(2 * getInOperatorMaxListSize() + 1));
    assertEquals(3, r.size());
    assertEquals(generateList(getInOperatorMaxListSize()), r.get(0));
    assertEquals(generateList(getInOperatorMaxListSize(), getInOperatorMaxListSize()), r.get(1));
    assertEquals(Lists.newArrayList(2 * getInOperatorMaxListSize()), r.get(2));
  }

  @Test
  public void testGenerateSafeAndOr() {
    SimpleExpression se1 = Restrictions.eq("A", 5);
    SimpleExpression se2 = Restrictions.eq("B", 7);

    Pair<SimpleExpression, SimpleExpression> goodPair = Pair.of(se1, se2);
    List<Pair<SimpleExpression, SimpleExpression>> conditionList = new ArrayList<Pair<SimpleExpression, SimpleExpression>>();
    List<Disjunction> orList;
    Disjunction goodCriteria;

    // simple test - (A = 5) AND (b = 7)
    goodCriteria = Restrictions.disjunction();
    conditionList.add(goodPair);
    goodCriteria.add(Restrictions.and(goodPair.getLeft(), goodPair.getRight()));

    orList = HibernateUtils.generateSafeAndOr(conditionList);
    assertEquals(1, orList.size(), "Simple case should have one AND condition block");
    assertEquals(goodCriteria.toString(), orList.get(0).toString());

    // medium size test - ((A = 5) AND (b = 7)) ^ 100
    conditionList.clear();
    goodCriteria = Restrictions.disjunction();
    for (int i = 0; i < 100; ++i) {
      conditionList.add(goodPair);
      goodCriteria.add(Restrictions.and(goodPair.getLeft(), goodPair.getRight()));
    }

    orList = HibernateUtils.generateSafeAndOr(conditionList);
    assertEquals(1, orList.size(), "Medium case should have 1 AND condition block");
    assertEquals(goodCriteria.toString(), orList.get(0).toString());


    // large size test - ((A = 5) AND (b = 7)) ^ 10000
    conditionList.clear();
    for (int i = 0; i < 10000; ++i) {
      conditionList.add(goodPair);
    }

    orList = HibernateUtils.generateSafeAndOr(conditionList);
    assertEquals(21, orList.size(), "Large case should have 21 AND condition blocks (499 * 20 + change)");
  }


  // =============================================================
  // Utilities.
  // =============================================================
  private List<Integer> generateList(int size) {
    return generateList(size, 0);
  }

  private List<Integer> generateList(int size, int startValue) {
    List<Integer> list = Lists.newArrayListWithCapacity(size);
    for (int i = 0; i < size; i++) {
      list.add(startValue + i);
    }
    return list;
  }
}
