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
package org.ikasan.history.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.ikasan.history.model.CustomMetric;
import org.ikasan.history.model.MessageHistoryFlowEvent;
import org.ikasan.history.model.MetricEvent;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test cases for the HibernateMessageHistoryDao
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/hsqldb-config.xml",
        "/substitute-components.xml",
})
@DirtiesContext
public class HibernateMessageHistoryDaoTest
{
    @Resource
    private MessageHistoryDao messageHistoryDao;

    @Before
    public void setup()
    {
    	
        MessageHistoryFlowEvent event1 = new MessageHistoryFlowEvent("moduleName", "flowName", "componentName",
                "lifeId", "relatedLifeId", "lifeId", "relatedLifeId",
                System.currentTimeMillis()-500L, System.currentTimeMillis(), System.currentTimeMillis()-1000000000L);

        event1.setHarvested(true);
        Set<CustomMetric> metrics = new HashSet<CustomMetric>();
        
        for(int i=0; i<6; i++)
        {
        	CustomMetric cm = new CustomMetric("name", "value");
        	cm.setMessageHistoryFlowEvent(event1);
        	metrics.add(cm);
        }
        
        event1.setMetrics(metrics);

        MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                "lifeId", "relatedLifeId", System.currentTimeMillis(), "payload", 30L);

        messageHistoryDao.save(wiretapEvent);
        messageHistoryDao.save(event1);

    }

    @Test
    @DirtiesContext
    public void test_housekeepablesExist()
    {
        Assert.assertTrue(messageHistoryDao.housekeepablesExist());
    }

    @Test
    @DirtiesContext
    public void test_deleteAllExpired()
    {
        messageHistoryDao.deleteAllExpired();
        Assert.assertFalse(messageHistoryDao.housekeepablesExist());
    }

    @Test
    @DirtiesContext
    public void test_search_moduleName()
    {
        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
        Assert.assertTrue(results.getPagedResults().get(0).getMetrics().size() == 6);
        Assert.assertTrue(results.getPagedResults().get(0).getMetrics().size() == 6);
    }

    @Test
    @DirtiesContext
    public void test_search_flowName()
    {
        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, null, "flowName", null, null, null, null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_search_lifeId()
    {
        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, null, null, null, "lifeId", null, null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_search_relatedLifeId()
    {
        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, null, null, null, null, "relatedLifeId", null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_get_lifeId()
    {
        // add another event in that does not match
        MessageHistoryFlowEvent event2 = new MessageHistoryFlowEvent("moduleName", "flowName", "componentName",
                "lifeIdX", "relatedLifeIdY", "lifeIdX", "relatedLifeIdY",
                System.currentTimeMillis()-500L, System.currentTimeMillis(), System.currentTimeMillis()-1000000000L);

        messageHistoryDao.save(event2);

        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.getMessageHistoryEvent(0, 10, null, true, "lifeId", null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_get_relatedLifeId()
    {
        // add another event in that does not match
        MessageHistoryFlowEvent event2 = new MessageHistoryFlowEvent("moduleName", "flowName", "componentName",
                "lifeIdX", "relatedLifeIdY", "lifeIdX", "relatedLifeIdY",
                System.currentTimeMillis()-500L, System.currentTimeMillis(), System.currentTimeMillis()-1000000000L);
        messageHistoryDao.save(event2);

        // add another event in that matches on the relatedId
        MessageHistoryFlowEvent event3 = new MessageHistoryFlowEvent("moduleName", "flowName", "componentName",
                "newModuleLifeId", "lifeId", "newModuleLifeId", "lifeId",
                System.currentTimeMillis()-500L, System.currentTimeMillis(), System.currentTimeMillis()-1000000000L);
        messageHistoryDao.save(event3);

        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.getMessageHistoryEvent(0, 10, null, true, "lifeId", "lifeId");
        Assert.assertTrue(results.getPagedResults().size() == 2);
    }
    
    @Test
    @DirtiesContext
    public void bulkDeleteTest()
    {
    	for(int i=0; i<10000; i++)
    	{	    	
	        MessageHistoryFlowEvent event1 = new MessageHistoryFlowEvent("moduleName", "flowName" , "componentName",
	                "lifeId" + i, "relatedLifeId" + i, "lifeId" + i, "relatedLifeId" + i,
	                System.currentTimeMillis()-500L, System.currentTimeMillis(), System.currentTimeMillis()-1000000000L);

            event1.setHarvested(true);
	        
	        Set<CustomMetric> metrics = new HashSet<CustomMetric>();
	        CustomMetric cm = new CustomMetric("name", "value");
	        cm.setMessageHistoryFlowEvent(event1);

	        
	    	metrics.add(cm);
	        
	        event1.setMetrics(metrics);

            MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                    "lifeId" + i, "relatedLifeId" + i, System.currentTimeMillis(), "payload", 30L);

            messageHistoryDao.save(wiretapEvent);
	        messageHistoryDao.save(event1);
    	}

        List<MessageHistoryEvent> events =  messageHistoryDao.getHarvestedRecords(500);

        Assert.assertTrue(events.size() == 500);

        for(MessageHistoryEvent event: events)
        {
            Assert.assertTrue(event.getWiretapFlowEvent() != null);
        }

    	
    	PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        messageHistoryDao.deleteHarvestableRecords(events);

        System.out.println("Starting to delete records: " + System.currentTimeMillis());
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(500);
        messageHistoryDao.deleteHarvestableRecords(events);

        System.out.println("Completed deleting records: " + System.currentTimeMillis());

    	results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        System.out.println("Delete completed records: " + results.getResultSize());

        Assert.assertTrue(results.getPagedResults().size() == 0);
    }

    @After
    public void tear_down()
    {
        messageHistoryDao.deleteAllExpired();
        
    }

}
