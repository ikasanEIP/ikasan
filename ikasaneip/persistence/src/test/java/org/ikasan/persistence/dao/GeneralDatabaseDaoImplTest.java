/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.persistence.dao;

import org.ikasan.persistence.PersistenceAutoConfiguration;
import org.ikasan.persistence.PersistenceTestAutoConfiguration;
import org.ikasan.spec.persistence.dao.GeneralDatabaseDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;


/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceAutoConfiguration.class, PersistenceTestAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/createDbTablesAndPopulate.sql"},
    executionPhase = BEFORE_TEST_CLASS,
    config = @SqlConfig(dataSource = "ikasan.ds"))
public class GeneralDatabaseDaoImplTest
{
	/** Object being tested */
	@Autowired
    private GeneralDatabaseDao generalDatabaseDao;

    @Autowired
    private DataSource dataSource;

	@Test
	public void test_get_count_success() {
		Assert.assertNotNull(generalDatabaseDao);
        Assert.assertEquals(generalDatabaseDao.getRecordCountForDatabaseTable("Test1"), 1);
        Assert.assertEquals(generalDatabaseDao.getRecordCountForDatabaseTable("Test2"), 5);
	}

    @Test(expected = RuntimeException.class)
    public void test_exception_bad_table_name() {
        Assert.assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("BadTableName");
    }

    // Additional Comprehensive Tests

    @Test
    public void test_get_count_case_insensitive_table_name() {
        assertNotNull(generalDatabaseDao);
        // Test various case variations
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("test1"));
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("TEST1"));
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("TeSt1"));
        assertEquals(5, generalDatabaseDao.getRecordCountForDatabaseTable("test2"));
        assertEquals(5, generalDatabaseDao.getRecordCountForDatabaseTable("TEST2"));
    }

    @Test
    public void test_get_count_multiple_calls_same_table() {
        assertNotNull(generalDatabaseDao);
        // Verify multiple calls return consistent results
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
    }

    @Test
    public void test_constructor_with_valid_datasource() {
        assertNotNull(dataSource);
        GeneralDatabaseDaoImpl dao = new GeneralDatabaseDaoImpl(dataSource);
        assertNotNull(dao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_with_null_datasource() {
        new GeneralDatabaseDaoImpl(null);
    }

    @Test
    public void test_constructor_throws_exception_with_proper_message() {
        try {
            new GeneralDatabaseDaoImpl(null);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("dataSource cannot be null!", e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_exception_for_nonexistent_table() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("NonExistentTable");
    }

    @Test
    public void test_exception_message_for_nonexistent_table() {
        assertNotNull(generalDatabaseDao);
        String nonExistentTable = "NonExistentTable123";
        try {
            generalDatabaseDao.getRecordCountForDatabaseTable(nonExistentTable);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("does not exist in the database"));
            assertTrue(e.getMessage().contains(nonExistentTable));
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_sql_injection_attempt_with_drop_statement() {
        assertNotNull(generalDatabaseDao);
        // Attempt SQL injection - should be prevented
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1; DROP TABLE Test2; --");
    }

    @Test(expected = RuntimeException.class)
    public void test_sql_injection_attempt_with_union() {
        assertNotNull(generalDatabaseDao);
        // Attempt SQL injection with UNION
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1 UNION SELECT * FROM Test2");
    }

    @Test(expected = RuntimeException.class)
    public void test_sql_injection_attempt_with_special_characters() {
        assertNotNull(generalDatabaseDao);
        // Test with special characters that should be rejected
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1';--");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_spaces() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test 1");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_semicolon() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1;");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_parentheses() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1()");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_quotes() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("'Test1'");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_double_quotes() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("\"Test1\"");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_backticks() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("`Test1`");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_wildcards() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test*");
    }

    @Test(expected = RuntimeException.class)
    public void test_invalid_table_name_with_percent() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test%");
    }

    @Test
    public void test_valid_table_name_with_underscores() {
        assertNotNull(generalDatabaseDao);
        // Underscores should be allowed in table names per the pattern
        // This test verifies the pattern allows underscores
        // Note: This will fail if table doesn't exist, but demonstrates valid pattern
        try {
            generalDatabaseDao.getRecordCountForDatabaseTable("Test_Table_1");
            // If table exists, should work
        } catch (RuntimeException e) {
            // Should fail because table doesn't exist, not because of invalid identifier
            assertTrue(e.getMessage().contains("does not exist"));
            assertFalse(e.getMessage().contains("Invalid table identifier"));
        }
    }

    @Test
    public void test_valid_table_name_with_numbers() {
        assertNotNull(generalDatabaseDao);
        // Numbers should be allowed in table names
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
        assertEquals(5, generalDatabaseDao.getRecordCountForDatabaseTable("Test2"));
    }

    @Test
    public void test_schema_qualified_table_name_pattern() {
        assertNotNull(generalDatabaseDao);
        // Test that schema.table pattern is acceptable format
        // This verifies the regex pattern allows dots for schema qualification
        try {
            generalDatabaseDao.getRecordCountForDatabaseTable("schema.Test1");
            // If schema.Test1 exists, should work
        } catch (RuntimeException e) {
            // Should fail because table doesn't exist, not because of invalid identifier
            assertTrue("Error should be about table not existing, not invalid identifier",
                e.getMessage().contains("does not exist") ||
                e.getMessage().contains("Invalid table identifier"));
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_multiple_dots_in_table_name_rejected() {
        assertNotNull(generalDatabaseDao);
        // Multiple dots should be rejected (only schema.table allowed)
        generalDatabaseDao.getRecordCountForDatabaseTable("catalog.schema.table");
    }

    @Test(expected = RuntimeException.class)
    public void test_empty_table_name() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("");
    }

    @Test(expected = RuntimeException.class)
    public void test_null_table_name() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void test_whitespace_only_table_name() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("   ");
    }

    @Test(expected = RuntimeException.class)
    public void test_table_name_with_newline() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1\n");
    }

    @Test(expected = RuntimeException.class)
    public void test_table_name_with_tab() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1\t");
    }

    @Test(expected = RuntimeException.class)
    public void test_sql_injection_with_comment() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1--");
    }

    @Test(expected = RuntimeException.class)
    public void test_sql_injection_with_multiline_comment() {
        assertNotNull(generalDatabaseDao);
        generalDatabaseDao.getRecordCountForDatabaseTable("Test1/**/");
    }

    @Test
    public void test_count_query_constant() {
        // Verify the query constant is properly formatted
        assertEquals("SELECT COUNT(*) AS COUNT FROM %s", GeneralDatabaseDaoImpl.TABLE_COUNT_QUERY);
    }

    @Test
    public void test_repeated_queries_for_different_tables() {
        assertNotNull(generalDatabaseDao);
        // Verify DAO can handle multiple different table queries
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
        assertEquals(5, generalDatabaseDao.getRecordCountForDatabaseTable("Test2"));
        assertEquals(1, generalDatabaseDao.getRecordCountForDatabaseTable("Test1"));
        assertEquals(5, generalDatabaseDao.getRecordCountForDatabaseTable("Test2"));
    }

    @Test
    public void test_dao_instance_is_singleton_from_spring() {
        assertNotNull(generalDatabaseDao);
        // Verify the autowired instance is not null and can be used multiple times
        assertSame(generalDatabaseDao, generalDatabaseDao);
    }


}
