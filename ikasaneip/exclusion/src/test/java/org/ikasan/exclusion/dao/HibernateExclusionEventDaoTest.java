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
package org.ikasan.exclusion.dao;

import junit.framework.Assert;
import org.ikasan.exclusion.model.BlackListEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/exclusion-hibernate-dao-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
    })

public class HibernateExclusionEventDaoTest
{
    @Resource
    BlackListDao<String,BlackListEvent> blackListDao;

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_contains_add_remove_operations()
    {
        BlackListEvent blackListEvent = new BlackListEvent("moduleName", "flowName", "123456");
        Assert.assertFalse("Should not be found", blackListDao.contains("moduleName", "flowName", "123456") );

        blackListDao.add(blackListEvent);
        Assert.assertTrue("Should be found", blackListDao.contains("moduleName", "flowName", "123456"));

        blackListDao.remove("moduleName", "flowName", "123456");
        Assert.assertFalse("Should not be found", blackListDao.contains("moduleName", "flowName", "123456"));
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_deleteExpired_operation()
    {
        // new event with 1 milli expiry
        BlackListEvent blackListEvent = new BlackListEvent("moduleName", "flowName", "123456");
        BlackListEvent blackListEventExpired = new BlackListEvent("moduleName", "flowName", "1234567", 1L);
        Assert.assertFalse("Non expired should not be found", blackListDao.contains("moduleName", "flowName", "123456") );
        Assert.assertFalse("Expired should not be found", blackListDao.contains("moduleName", "flowName", "1234567") );

        blackListDao.add(blackListEvent);
        blackListDao.add(blackListEventExpired);
        Assert.assertTrue("Non expired should be found", blackListDao.contains("moduleName", "flowName", "123456"));
        Assert.assertTrue("Expired should be found", blackListDao.contains("moduleName", "flowName", "1234567"));

        try
        {
            Thread.sleep(100);
        }
        catch(InterruptedException e)
        {
            Assert.fail("sleep woken early!");
        }

        blackListDao.deleteExpired();
        Assert.assertTrue("Should be found after deleteAll", blackListDao.contains("moduleName", "flowName", "123456"));
        Assert.assertFalse("Should not be found after deleteAll", blackListDao.contains("moduleName", "flowName", "123457"));
    }
}