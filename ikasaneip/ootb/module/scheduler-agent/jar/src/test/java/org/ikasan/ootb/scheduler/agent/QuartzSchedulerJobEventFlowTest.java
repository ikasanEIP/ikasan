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
package org.ikasan.ootb.scheduler.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * This test class supports the <code>vanilla integration module</code> application.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestConfiguration.class})
@Sql(scripts = {"/cleanDatabaseTables.sql"}, executionPhase = AFTER_TEST_METHOD)
public class QuartzSchedulerJobEventFlowTest {
    @Autowired
    private Module<Flow> moduleUnderTest;

    @Autowired
    private IBigQueue outboundQueue;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public IkasanFlowTestExtensionRule flowTestRule = new IkasanFlowTestExtensionRule();

    @MockitoBean
    AgentInstanceRecoveryManager agentInstanceRecoveryManager;

    @BeforeClass
    public static void setupObjectMapper() {
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterInstanceDto.class)
            .addAbstractTypeMapping(DryRunParameters.class, DryRunParametersDto.class)
            .addAbstractTypeMapping(InternalEventDrivenJobInstance.class, InternalEventDrivenJobInstanceImpl.class);

        objectMapper.registerModule(simpleModule);
    }

    @Before
    public void setup() throws IOException {
        ContextInstanceCache.instance().removeAll();
        outboundQueue.removeAll();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_success_start_agent_recovery_not_complete() {
        ContextInstanceCache.instance().setInitialisationComplete(false);
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        Awaitility.await()
            .atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() ->assertEquals(Flow.RECOVERING, flowTestRule.getFlowState()));

        flowTestRule.stopFlow();
        flowTestRule.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_success() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        String contextInstanceId = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("contextInstanceName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter")
            .splitter("Scheduled Status Event Correlation Identifier Splitter")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceId, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_success_2_instances_in_cache() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        createContextAndPutInCache();
        createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("contextInstanceName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter")
            .splitter("Scheduled Status Event Correlation Identifier Splitter")
            .router("Blackout Router")
            .producer("Scheduled Status Producer")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(2, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_event_filtered_context_instance_not_found() {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("anotherContextInstanceName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_outside_blackout_window_success() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        String contextInstanceId = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("0 15 10 * * ? 3000"));


        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("contextInstanceName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter")
            .splitter("Scheduled Status Event Correlation Identifier Splitter")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceId, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_filtered_due_to_outside_blackout_window_but_scheduler_event_not_dropped_success() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        String contextInstanceId = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration
            = flowTestRule.getComponentConfig("Publish Scheduled Status", ScheduledProcessEventFilterConfiguration.class);
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(false);


        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("contextInstanceName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter")
            .splitter("Scheduled Status Event Correlation Identifier Splitter")
            .router("Blackout Router")
            .filter("Publish Scheduled Status")
            .producer("Blackout Scheduled Status Producer");
        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceId, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_filtered_due_to_outside_blackout_window_but_scheduler_event_dropped_success() {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 4"));

        ContextualisedConverterConfiguration correlatedScheduledConsumerConfiguration
            = flowTestRule.getComponentConfig("JobExecution to ScheduledStatusEvent", ContextualisedConverterConfiguration.class);
        correlatedScheduledConsumerConfiguration.setContextName("contextInstanceName");

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration
            = flowTestRule.getComponentConfig("Publish Scheduled Status", ScheduledProcessEventFilterConfiguration.class);
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(true);

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to ScheduledStatusEvent")
            .filter("Context Instances Active Filter")
            .splitter("Scheduled Status Event Correlation Identifier Splitter")
            .router("Blackout Router")
            .filter("Publish Scheduled Status");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    private ContextualisedScheduledProcessEvent getEvent() throws IOException {
        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        return objectMapper.readValue(messageAsString
            , ContextualisedScheduledProcessEventImpl.class);
    }

    @After
    public void teardown() {
        // post-test teardown
    }

    private String createContextAndPutInCache() {
        String contextInstanceID = RandomStringUtils.randomAlphabetic(15);
        ContextInstanceImpl instance = new ContextInstanceImpl();
        instance.setId(contextInstanceID);
        instance.setName("contextInstanceName");
        ContextInstanceCache.instance().put(contextInstanceID, instance);
        return contextInstanceID;
    }
}
