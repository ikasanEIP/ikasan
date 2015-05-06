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
import org.ikasan.exclusion.model.BlackListLinkedHashMap;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for ListExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/exclusion-service-conf.xml",
        "/h2db-datasource-conf.xml"
        })

public class ListExclusionServiceDaoTest
{
    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_contains_add_remove_operations()
    {
        ExclusionServiceDao exclusionServiceDao = new ListExclusionServiceDao( new BlackListLinkedHashMap(2) );

        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "123456");
        Assert.assertFalse("Should not be found", exclusionServiceDao.contains("moduleName", "flowName", "123456"));

        exclusionServiceDao.add(exclusionEvent);
        Assert.assertTrue("Should be found", exclusionServiceDao.contains("moduleName", "flowName", "123456"));

        exclusionServiceDao.remove("moduleName", "flowName", "123456");
        Assert.assertFalse("Should not be found", exclusionServiceDao.contains("moduleName", "flowName", "123456"));
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_deleteExpired_operation()
    {
        ExclusionServiceDao exclusionServiceDao = new ListExclusionServiceDao( new BlackListLinkedHashMap(2) );

        // new event with 1 milli expiry
        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "123456");
        ExclusionEvent exclusionEventExpired = new ExclusionEvent("moduleName", "flowName", "1234567", -1L);
        Assert.assertFalse("Non expired should not be found", exclusionServiceDao.contains("moduleName", "flowName", "123456") );
        Assert.assertFalse("Expired should not be found", exclusionServiceDao.contains("moduleName", "flowName", "1234567") );

        exclusionServiceDao.add(exclusionEvent);
        exclusionServiceDao.add(exclusionEventExpired);
        Assert.assertTrue("Non expired should be found", exclusionServiceDao.contains("moduleName", "flowName", "123456"));
        Assert.assertTrue("Expired should be found", exclusionServiceDao.contains("moduleName", "flowName", "1234567"));

        exclusionServiceDao.deleteExpired();
        Assert.assertTrue("Should be found after deleteAll", exclusionServiceDao.contains("moduleName", "flowName", "123456"));
        Assert.assertFalse("Should not be found after deleteAll", exclusionServiceDao.contains("moduleName", "flowName", "1234567"));
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_roll_operation()
    {
        ListExclusionServiceDao exclusionServiceDao = new ListExclusionServiceDao( new BlackListLinkedHashMap(5) );

        // new event with 1 milli expiry
        ExclusionEvent exclusionEvent1 = new ExclusionEvent("moduleName", "flowName", "1234561");
        exclusionServiceDao.add(exclusionEvent1);
        ExclusionEvent exclusionEvent2 = new ExclusionEvent("moduleName", "flowName", "1234562");
        exclusionServiceDao.add(exclusionEvent2);
        ExclusionEvent exclusionEvent3 = new ExclusionEvent("moduleName", "flowName", "1234563");
        exclusionServiceDao.add(exclusionEvent3);
        ExclusionEvent exclusionEvent4 = new ExclusionEvent("moduleName", "flowName", "1234564");
        exclusionServiceDao.add(exclusionEvent4);
        ExclusionEvent exclusionEvent5 = new ExclusionEvent("moduleName", "flowName", "1234565");
        exclusionServiceDao.add(exclusionEvent5);
        ExclusionEvent exclusionEvent6 = new ExclusionEvent("moduleName", "flowName", "1234566");
        exclusionServiceDao.add(exclusionEvent6);
        ExclusionEvent exclusionEvent7 = new ExclusionEvent("moduleName", "flowName", "1234567");
        exclusionServiceDao.add(exclusionEvent7);

        Assert.assertFalse("blacklisted should not contain exclusionEvent1", exclusionServiceDao.contains("moduleName", "flowName", "1234561"));
        Assert.assertFalse("blacklisted should not contain exclusionEvent2", exclusionServiceDao.contains("moduleName", "flowName", "1234562"));
        Assert.assertTrue("blacklisted should still contain exclusionEvent3", exclusionServiceDao.contains("moduleName", "flowName", "1234563"));
        Assert.assertTrue("blacklisted should still contain exclusionEvent4", exclusionServiceDao.contains("moduleName", "flowName", "1234564"));
        Assert.assertTrue("blacklisted should still contain exclusionEvent5", exclusionServiceDao.contains("moduleName", "flowName", "1234565"));
    }

}
