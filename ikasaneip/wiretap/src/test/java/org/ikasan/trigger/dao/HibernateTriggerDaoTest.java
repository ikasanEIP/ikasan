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
package org.ikasan.trigger.dao;

import org.ikasan.WiretapAutoConfiguration;
import org.ikasan.WiretapTestAutoConfiguration;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.trigger.model.TriggerImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for the HibernateMessageHistoryDao
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WiretapAutoConfiguration.class, WiretapTestAutoConfiguration.class})
@DirtiesContext
@Sql(scripts = "/alter-flow-event-trigger-remove-hibernate-auto-create-enum-constraint.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class HibernateTriggerDaoTest
{
    @Resource
    private TriggerDao triggerDao;

    @Autowired
    DataSource xaDataSource;

    JdbcTemplate jdbcTemplate;

    String insertSql = """
        insert\s
            into
                FlowEventTrigger
                (  FlowName, JobName, ModuleName, Relationship)\s
            values
                (  ?, ?, ?, ?)\
        """;

    String deleteAllSql = "delete from FlowEventTrigger";

    @Before
    public void setup()
    {
        jdbcTemplate = new JdbcTemplate(xaDataSource);
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_bad_relationship_name_exception(){
        new TriggerImpl("testModule","testFlow","BAD_RELATIONSHIP","test");
    }

    @Test
    public void test_save_find_success_lower_case_relationship(){
        Trigger trigger =  new TriggerImpl("testModule","testFlow","after","test");
        triggerDao.save(trigger);

        Assert.assertTrue(trigger.getId() > 0);

        List<Trigger> all = triggerDao.findAll();

        Assert.assertEquals(1,all.size());
        Assert.assertEquals(TriggerRelationship.AFTER,all.get(0).getRelationship());
    }

    @Test
    public void test_save_find_success_upper_case_relationship(){
        Trigger trigger =  new TriggerImpl("testModule","testFlow","AFTER","test");
        triggerDao.save(trigger);

        Assert.assertTrue(trigger.getId() > 0);

        List<Trigger> all = triggerDao.findAll();

        Assert.assertEquals(1,all.size());
        Assert.assertEquals(TriggerRelationship.AFTER,all.get(0).getRelationship());
    }

    @Test
    public void test_insert_relationship_lower_case_success(){

        Object[] params = new Object[] { "testFlow", "test", "testModule", "after"
        };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR
        };
        // add data to FileFilter table
        jdbcTemplate.update(insertSql, params, types);


        List<Trigger> all = triggerDao.findAll();

        Assert.assertEquals(1,all.size());
        Assert.assertEquals(TriggerRelationship.AFTER,all.get(0).getRelationship());
    }

    @Test
    public void test_insert_relationship_upper_case_success(){

        Object[] params = new Object[] { "testFlow", "test", "testModule", "AFTER"
        };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR
        };
        // add data to FileFilter table
        jdbcTemplate.update(insertSql, params, types);


        List<Trigger> all = triggerDao.findAll();

        Assert.assertEquals(1,all.size());
        Assert.assertEquals(TriggerRelationship.AFTER,all.get(0).getRelationship());
    }

    @After
    public void teardown()
    {
        jdbcTemplate.update(deleteAllSql, new Object[] {}, new int[] {});
    }
}
