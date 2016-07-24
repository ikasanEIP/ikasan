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

import java.util.ArrayList;

import javax.annotation.Resource;

import org.ikasan.exclusion.model.ExclusionEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/exclusion-service-conf.xml",
        "/h2db-datasource-conf.xml",
        "/substitute-components.xml"
    })

public class HibernateExclusionEventDaoTest
{
    @Resource
    ExclusionEventDao<String,ExclusionEvent> exclusionEventDao;

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_contains_save_find_delete_operations()
    {
        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
        Assert.assertNull("Should not be found", exclusionEventDao.find("moduleName", "flowName", "lifeIdentifier"));

        exclusionEventDao.save(exclusionEvent);
        Assert.assertTrue("Should be found", exclusionEventDao.find("moduleName", "flowName", "lifeIdentifier").equals(exclusionEvent));

        exclusionEventDao.delete("moduleName", "flowName", "lifeIdentifier");
        Assert.assertNull("Should not be found", exclusionEventDao.find("moduleName", "flowName", "lifeIdentifier"));
    }
    
    
    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_find_by_various_criteria()
    {
        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
        exclusionEventDao.save(exclusionEvent);
        
        
        exclusionEvent = new ExclusionEvent("moduleName1", "flowName1", "lifeIdentifier1", "event".getBytes(), "errorUri1");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEvent("moduleName5", "flowName5", "lifeIdentifier5", "event".getBytes(), "errorUri2");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEvent("moduleName2", "flowName2", "lifeIdentifier2", "event".getBytes(), "errorUri3");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEvent("moduleName3", "flowName3", "lifeIdentifier3", "event".getBytes(), "errorUri4");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEvent("moduleName4", "flowName4", "lifeIdentifier4", "event".getBytes(), "errorUri5");
        exclusionEventDao.save(exclusionEvent);
       
        ArrayList<String> moduleNames = new ArrayList<String>();
        moduleNames.add("moduleName1");
        moduleNames.add("moduleName2");
        
        Assert.assertTrue("Should be found size == 2", exclusionEventDao.find(moduleNames, null, null, null, null, 100).size() == 2);
        
        ArrayList<String> flowNames = new ArrayList<String>();
        flowNames.add("flowName1");
        
        Assert.assertEquals("Should be found size == 1", 1, exclusionEventDao.find(moduleNames, flowNames, null, null, null, 100).size());
        
        Assert.assertEquals("Should be found size == 1", 1, exclusionEventDao.find(moduleNames, flowNames, null, null, "lifeIdentifier1", 100).size());
        
        Assert.assertEquals("Should be found size == 0", 0, exclusionEventDao.find(moduleNames, flowNames, null, null, "lifeIdentifier2", 100).size());

    }

}