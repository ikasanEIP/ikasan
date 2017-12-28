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

import java.util.*;

import javax.annotation.Resource;

import org.ikasan.history.model.CustomMetric;
import org.ikasan.history.model.FlowInvocationMetricImpl;
import org.ikasan.history.model.ComponentInvocationMetricImpl;
import org.ikasan.history.model.MetricEvent;
import org.ikasan.spec.history.FlowInvocationMetric;
import org.ikasan.spec.history.ComponentInvocationMetric;
import org.ikasan.spec.search.PagedSearchResult;
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
        Set<ComponentInvocationMetricImpl> events = new HashSet<ComponentInvocationMetricImpl>();

        for(int j=0; j<5; j++)
        {
            ComponentInvocationMetricImpl event1 = new ComponentInvocationMetricImpl("componentName",
                    "lifeId" + j, "relatedLifeId" + j, "lifeId" + j, "relatedLifeId" + j,
                    System.currentTimeMillis() - 500L, System.currentTimeMillis());

            Set<CustomMetric> metrics = new HashSet<CustomMetric>();

            for(int i=0; i<6; i++)
            {
                CustomMetric cm = new CustomMetric("name", "value");
                cm.setComponentInvocationMetricImpl(event1);
                metrics.add(cm);
            }

            event1.setMetrics(metrics);

            MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                    "lifeId" + j, "relatedLifeId" + j, System.currentTimeMillis(), "payload", 30L);

            messageHistoryDao.save(wiretapEvent);

            events.add(event1);
        }

        FlowInvocationMetric<ComponentInvocationMetricImpl> flowInvocationMetric = new FlowInvocationMetricImpl("moduleName", "flowName",
                System.currentTimeMillis()-500L, System.currentTimeMillis(), "ACTION", events, 0l, null);

        flowInvocationMetric.setHarvested(true);


        messageHistoryDao.save(flowInvocationMetric);

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
    public void test_search_lifeId()
    {
        PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, null, null, null, "lifeId1", null, null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_search_relatedLifeId()
    {
        PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, null, null, null, null, "relatedLifeId1", null, null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_get_lifeId()
    {
        PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.getMessageHistoryEvent(0, 10, null, true, "lifeId1", null);
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }

    @Test
    @DirtiesContext
    public void test_get_relatedLifeId()
    {
        PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.getMessageHistoryEvent(0, 10, null, true, "lifeId1", "lifeId1");
        Assert.assertTrue(results.getPagedResults().size() == 1);
    }
    
    @Test
    @DirtiesContext
    public void bulkDeleteTest()
    {
        List<FlowInvocationMetric> flowInvocationMetrics = new ArrayList<>();
    	for(int i=0; i<1000; i++)
    	{
            Set<ComponentInvocationMetricImpl> events = new HashSet<ComponentInvocationMetricImpl>();

    	    for(int j=0; j<5; j++)
            {
                ComponentInvocationMetricImpl event1 = new ComponentInvocationMetricImpl("componentName",
                        "lifeId" + i, "relatedLifeId" + i, "lifeId" + i, "relatedLifeId" + i,
                        System.currentTimeMillis() - 500L, System.currentTimeMillis());

                Set<CustomMetric> metrics = new HashSet<CustomMetric>();
                CustomMetric cm = new CustomMetric("name", "value");
                cm.setComponentInvocationMetricImpl(event1);


                metrics.add(cm);

                event1.setMetrics(metrics);

                MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                        "lifeId" + i, "relatedLifeId" + i, System.currentTimeMillis(), "payload", 30L);

                messageHistoryDao.save(wiretapEvent);

                events.add(event1);
            }

            FlowInvocationMetric<ComponentInvocationMetricImpl> flowInvocationMetric = new FlowInvocationMetricImpl("moduleName", "flowName",
                    System.currentTimeMillis()-500L, System.currentTimeMillis(), "ACTION", events, 0l, null);

            flowInvocationMetric.setHarvested(false);

            this.messageHistoryDao.save(flowInvocationMetric);

            flowInvocationMetrics.add(flowInvocationMetric);
    	}

    	this.messageHistoryDao.updateAsHarvested(flowInvocationMetrics);

        List<FlowInvocationMetric> events =  messageHistoryDao.getHarvestedRecords(50);

        Assert.assertTrue(events.size() == 50);

        for(FlowInvocationMetric<ComponentInvocationMetric> event: events)
        {
            for(ComponentInvocationMetric messageHistoryEvent: event.getFlowInvocationEvents())
            {
                Assert.assertTrue(messageHistoryEvent.getWiretapFlowEvent() != null);
            }
        }


    	PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        messageHistoryDao.deleteHarvestableRecords(events);

        System.out.println("Starting to delete records: " + System.currentTimeMillis());
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);
        events =  messageHistoryDao.getHarvestedRecords(50);
        messageHistoryDao.deleteHarvestableRecords(events);

        System.out.println("Completed deleting records: " + System.currentTimeMillis());

    	results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        System.out.println("Delete completed records: " + results.getResultSize());

        Assert.assertTrue(results.getPagedResults().size() == 0);
    }

    @Test
    @DirtiesContext
    public void bulkDelete2Test()
    {
        for(int i=0; i<1000; i++)
        {
            Set<ComponentInvocationMetricImpl> events = new HashSet<ComponentInvocationMetricImpl>();

            for(int j=0; j<5; j++)
            {
                ComponentInvocationMetricImpl event1 = new ComponentInvocationMetricImpl("componentName",
                        "lifeId" + i, "relatedLifeId" + i, "lifeId" + i, "relatedLifeId" + i,
                        System.currentTimeMillis() - 500L, System.currentTimeMillis());

                Set<CustomMetric> metrics = new HashSet<CustomMetric>();
                CustomMetric cm = new CustomMetric("name", "value");
                cm.setComponentInvocationMetricImpl(event1);


                metrics.add(cm);

                event1.setMetrics(metrics);

                MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                        "lifeId" + i, "relatedLifeId" + i, System.currentTimeMillis(), "payload", 30L);

                messageHistoryDao.save(wiretapEvent);

                events.add(event1);
            }

            FlowInvocationMetric<ComponentInvocationMetricImpl> flowInvocationMetric = new FlowInvocationMetricImpl("moduleName", "flowName",
                    System.currentTimeMillis()-500L, System.currentTimeMillis(), "ACTION", events, 0l, null);

            flowInvocationMetric.setHarvested(true);


            messageHistoryDao.save(flowInvocationMetric);
        }

        System.out.println("Started deleting message history records: " + System.currentTimeMillis());

        messageHistoryDao.setHousekeepingBatchSize(500);
        messageHistoryDao.setTransactionBatchSize(10500);
        messageHistoryDao.setBatchHousekeepDelete(true);

        messageHistoryDao.deleteAllExpired();

        System.out.println("Completed deleting message history records: " + System.currentTimeMillis());

        PagedSearchResult<ComponentInvocationMetric> results = messageHistoryDao.findMessageHistoryEvents(0, 10, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        System.out.println("Delete completed records: " + results.getResultSize());

        Assert.assertTrue(results.getPagedResults().size() == 0);
    }

    @After
    public void tear_down()
    {
        messageHistoryDao.deleteAllExpired();
        
    }

}
