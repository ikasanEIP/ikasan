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
package org.ikasan.history.service;

import java.util.*;

import org.ikasan.history.dao.MessageHistoryDao;
import org.ikasan.history.model.*;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.FlowInvocation;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapSerialiser;
import org.ikasan.wiretap.model.WiretapEventFactory;
import org.ikasan.wiretap.serialiser.WiretapSerialiserServiceTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test cases for MessageHistoryService
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/hsqldb-config.xml",
        "/substitute-components.xml",
})
public class MessageHistoryServiceImplTest
{
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    MessageHistoryDao mockMessageHistoryDao = mockery.mock(MessageHistoryDao.class);
    WiretapEventFactory wiretapEventFactory = mockery.mock(WiretapEventFactory.class);
    FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class);
    HistoryEventFactory historyEventFactory = mockery.mock(HistoryEventFactory.class);
    MessageHistoryEvent messageHistoryEvent = mockery.mock(MessageHistoryEvent.class);
    WiretapSerialiser wiretapSerialiser = mockery.mock(WiretapSerialiser.class);
    FlowInvocation flowInvocation = mockery.mock(FlowInvocation.class);

    MessageHistoryServiceImpl mockMessageHistoryService = new MessageHistoryServiceImpl(mockMessageHistoryDao, wiretapSerialiser);

    @Resource
    private MessageHistoryDao messageHistoryDao;

    MessageHistoryServiceImpl messageHistoryService;

    @Before
    public void setup()
    {
        messageHistoryService = new MessageHistoryServiceImpl(messageHistoryDao, wiretapSerialiser);
        mockMessageHistoryService.setHistoryEventFactory(historyEventFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_null_dao()
    {
        new MessageHistoryServiceImpl(null, wiretapSerialiser);
    }

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_null_event_factory()
    {
        new MessageHistoryServiceImpl(mockMessageHistoryDao, null);
    }


    @Test
    @DirtiesContext
    public void test_save()
    {
        mockery.checking(new Expectations(){{
            oneOf(historyEventFactory).newEvent("moduleName", "flowName", flowInvocationContext, 7);
            will(returnValue(flowInvocation));
            oneOf(mockMessageHistoryDao).save(flowInvocation);
        }});
        mockMessageHistoryService.save(flowInvocationContext, "moduleName", "flowName");
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_housekeep()
    {
        mockery.checking(new Expectations(){{
            oneOf(mockMessageHistoryDao).deleteAllExpired();
        }});
        mockMessageHistoryService.housekeep();
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_housekeepablesExist()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(mockMessageHistoryDao).housekeepablesExist();
                will(returnValue(true));
            }});
        Assert.assertTrue(mockMessageHistoryService.housekeepablesExist());
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_findMessageHistoryEvents()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(mockMessageHistoryDao).findMessageHistoryEvents(0, 0, "orderBy", true, Collections.<String>emptySet(),
                        "flowName", "componentName", "lifeId", "relatedLifeId", new Date(0L), new Date(0L));
                will(returnValue(null));
            }});
        mockMessageHistoryService.findMessageHistoryEvents(0, 0, "orderBy", true, Collections.<String>emptySet(),
                "flowName", "componentName", "lifeId", "relatedLifeId", new Date(0L), new Date(0L));
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_getMessageHistoryEvents_with_relatedId()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(mockMessageHistoryDao).getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", "lifeId");
                will(returnValue(null));
            }});
        mockMessageHistoryService.getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", true);
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_getMessageHistoryEvents_without_relatedId()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(mockMessageHistoryDao).getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", null);
                will(returnValue(null));
            }});
        mockMessageHistoryService.getMessageHistoryEvent(0, 0, "orderBy", true, "lifeId", false);
        mockery.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_harvest()
    {
        for(int i=0; i<1000; i++)
        {
            Set<MessageHistoryFlowEvent> events = new HashSet<MessageHistoryFlowEvent>();

            for(int j=0; j<5; j++)
            {
                MessageHistoryFlowEvent event1 = new MessageHistoryFlowEvent("componentName",
                        "lifeId" + i, "relatedLifeId" + i, "lifeId" + i, "relatedLifeId" + i,
                        System.currentTimeMillis() - 500L, System.currentTimeMillis());

                Set<CustomMetric> metrics = new HashSet<CustomMetric>();
                CustomMetric cm = new CustomMetric("name", "value");
                cm.setMessageHistoryFlowEvent(event1);


                metrics.add(cm);

                event1.setMetrics(metrics);

                MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                        "lifeId" + i, "relatedLifeId" + i, System.currentTimeMillis(), "payload", 30L);

                messageHistoryDao.save(wiretapEvent);

                events.add(event1);
            }

            FlowInvocation<MessageHistoryFlowEvent> flowInvocation = new FlowInvocationImpl("moduleName", "flowName",
                    System.currentTimeMillis()-500L, System.currentTimeMillis(), "ACTION", events, 0l);

            flowInvocation.setHarvested(false);


            messageHistoryDao.save(flowInvocation);
        }

        System.out.println("Started deleting message history records: " + System.currentTimeMillis());

        Assert.assertEquals("Harvestable records exist!", true, this.messageHistoryService.harvestableRecordsExist());

        this.messageHistoryService.setTransactionBatchSize(10500);
        this.messageHistoryService.setHousekeepingBatchSize(500);

        List events = this.messageHistoryService.harvest(1000);

        Assert.assertEquals("Harvestable events should equal!", events.size(), 1000);

        this.messageHistoryService.housekeep();

        System.out.println("Completed deleting message history records: " + System.currentTimeMillis());

        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 100000, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        System.out.println("Delete completed records: " + results.getResultSize());

        Assert.assertEquals("After housekeeping events should equal!", results.getPagedResults().size(), 0);
    }

    @Test
    @DirtiesContext
    public void test_harvest_batch_delete_false()
    {
        for(int i=0; i<1000; i++)
        {
            Set<MessageHistoryFlowEvent> events = new HashSet<MessageHistoryFlowEvent>();

            for(int j=0; j<5; j++)
            {
                MessageHistoryFlowEvent event1 = new MessageHistoryFlowEvent("componentName",
                        "lifeId" + i, "relatedLifeId" + i, "lifeId" + i, "relatedLifeId" + i,
                        System.currentTimeMillis() - 500L, System.currentTimeMillis());

                Set<CustomMetric> metrics = new HashSet<CustomMetric>();
                CustomMetric cm = new CustomMetric("name", "value");
                cm.setMessageHistoryFlowEvent(event1);


                metrics.add(cm);

                event1.setMetrics(metrics);

                MetricEvent wiretapEvent = new MetricEvent("moduleName", "flowName", "componentName",
                        "lifeId" + i, "relatedLifeId" + i, System.currentTimeMillis(), "payload", 30L);

                messageHistoryDao.save(wiretapEvent);

                events.add(event1);
            }

            FlowInvocation<MessageHistoryFlowEvent> flowInvocation = new FlowInvocationImpl("moduleName", "flowName",
                    System.currentTimeMillis()-500L, System.currentTimeMillis(), "ACTION", events, 0l);

            flowInvocation.setHarvested(false);


            messageHistoryDao.save(flowInvocation);
        }

        System.out.println("Started deleting message history records: " + System.currentTimeMillis());

        messageHistoryDao.setBatchHousekeepDelete(false);

        Assert.assertEquals("Harvestable records exist!", true, this.messageHistoryService.harvestableRecordsExist());

        this.messageHistoryService.setTransactionBatchSize(10500);
        this.messageHistoryService.setHousekeepingBatchSize(500);

        List events = this.messageHistoryService.harvest(1000);

        Assert.assertEquals("Harvestable events should equal!", events.size(), 1000);

        this.messageHistoryService.housekeep();

        System.out.println("Completed deleting message history records: " + System.currentTimeMillis());

        PagedSearchResult<MessageHistoryEvent> results = messageHistoryDao.findMessageHistoryEvents(0, 100000, null, true, Collections.singleton("moduleName"), null, null, null, null, null, null);

        System.out.println("Delete completed records: " + results.getResultSize());

        Assert.assertEquals("After housekeeping events should equal!", results.getPagedResults().size(), 0);
    }

}
