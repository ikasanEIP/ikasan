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
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.exclusion.ExclusionAutoConfiguration;
import org.ikasan.exclusion.ExclusionTestAutoConfiguration;
import org.ikasan.exclusion.model.ExclusionEventImpl;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
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
@ContextConfiguration(classes = {ExclusionAutoConfiguration.class, ExclusionTestAutoConfiguration.class})
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
        ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
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
    public void test_contains_batch_save_find_delete_operations()
    {
        ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
        Assert.assertNull("Should not be found", exclusionEventDao.find("moduleName", "flowName", "lifeIdentifier"));

        List<ExclusionEvent> events = new ArrayList<>();
        events.add(exclusionEvent);
        exclusionEventDao.save(events);

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
        ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
        exclusionEventDao.save(exclusionEvent);
        
        
        exclusionEvent = new ExclusionEventImpl("moduleName1", "flowName1", "lifeIdentifier1", "event".getBytes(), "errorUri1");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEventImpl("moduleName5", "flowName5", "lifeIdentifier5", "event".getBytes(), "errorUri2");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEventImpl("moduleName2", "flowName2", "lifeIdentifier2", "event".getBytes(), "errorUri3");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEventImpl("moduleName3", "flowName3", "lifeIdentifier3", "event".getBytes(), "errorUri4");
        exclusionEventDao.save(exclusionEvent);
        
        exclusionEvent = new ExclusionEventImpl("moduleName4", "flowName4", "lifeIdentifier4", "event".getBytes(), "errorUri5");
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

    @Test
    @DirtiesContext
    public void test_harvest_success()
    {
        this.exclusionEventDao.setHarvestQueryOrdered(true);
        List<ExclusionEvent> exclusionEvents = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
            exclusionEventDao.save(exclusionEvent);

            exclusionEvents.add(exclusionEvent);
        }

        Assert.assertEquals("Harvestable records == 1000", this.exclusionEventDao.getHarvestableRecords(5000).size(), 1000);

        this.exclusionEventDao.updateAsHarvested(exclusionEvents);

        Assert.assertEquals("Harvestable records == 0", this.exclusionEventDao.getHarvestableRecords(5000).size(), 0);
    }

    @Test
    @DirtiesContext
    public void test_harvest_success_no_order_by()
    {
        this.exclusionEventDao.setHarvestQueryOrdered(false);
        List<ExclusionEvent> exclusionEvents = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
            exclusionEventDao.save(exclusionEvent);

            exclusionEvents.add(exclusionEvent);
        }

        Assert.assertEquals("Harvestable records == 1000", this.exclusionEventDao.getHarvestableRecords(5000).size(), 1000);

        this.exclusionEventDao.updateAsHarvested(exclusionEvents);

        Assert.assertEquals("Harvestable records == 0", this.exclusionEventDao.getHarvestableRecords(5000).size(), 0);
    }

    @Test
    @DirtiesContext
    public void test_harvest_success_no_order_by_with_gap()
    {
        this.exclusionEventDao.setHarvestQueryOrdered(false);
        List<ExclusionEvent> exclusionEvents = new ArrayList<>();

        for(int i=0; i<1000; i++)
        {
            ExclusionEvent exclusionEvent = new ExclusionEventImpl("moduleName", "flowName", "lifeIdentifier", "event".getBytes(), "errorUri");
            exclusionEventDao.save(exclusionEvent);

            exclusionEvents.add(exclusionEvent);
        }

        List<ExclusionEvent> events = this.exclusionEventDao.getHarvestableRecords(3);

        this.exclusionEventDao.updateAsHarvested(List.of(events.get(1)));

        events = this.exclusionEventDao.getHarvestableRecords(3);
        Assert.assertEquals("ID equals", Long.valueOf(1L), events.get(0).getId());
        Assert.assertEquals("ID equals", Long.valueOf(3L), events.get(1).getId());
        Assert.assertEquals("ID equals", Long.valueOf(4L), events.get(2).getId());
    }

}