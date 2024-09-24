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
import org.ikasan.bigqueue.IBigQueue;
import org.apache.commons.lang3.SystemUtils;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.cache.InboundJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.SchedulerJobInitiationEventDto;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
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
public class JobProcessingFlowTest {

    @Value( "${module.name}" )
    String moduleName;

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private IBigQueue outboundQueue;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public IkasanFlowTestExtensionRule flowTestRule = new IkasanFlowTestExtensionRule();


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
        outboundQueue.removeAll();

        ContextInstanceCache.instance().put("contextInstanceId", new ContextInstanceImpl());
    }

    @After
    public void teardown() {
        // post-test teardown
        ContextInstanceCache.instance().removeAll();
    }


    @Test
    @DirtiesContext
    public void test_job_processing_flow_success() throws IOException {
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 1"));
        flowTestRule.consumer("Job Consumer")
            .converter("JobInitiationEvent to ScheduledStatusEvent")
            .router("Live instance SR Router")
            .broker("Job Starting Broker")
            .multiRecipientRouter("Job MR Router")
            .producer("Status Producer")
            .broker("Job Monitoring Broker")
            .producer("Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue bigQueue = InboundJobQueueCache.instance().get("scheduler-agent-Scheduler Flow 1-inbound-queue");
        bigQueue.removeAll();
        SchedulerJobInitiationEventDto schedulerJobInitiationEvent = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEvent.setContextName("contextId");
        schedulerJobInitiationEvent.setContextInstanceId("contextInstanceId");

        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobInstanceDto.setCommandLine("pwd");
        }
        internalEventDrivenJobInstanceDto.setContextName("contextId");
        internalEventDrivenJobInstanceDto.setIdentifier("identifier");
        internalEventDrivenJobInstanceDto.setMinExecutionTime(1000L);
        internalEventDrivenJobInstanceDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobInstanceDto.setWorkingDirectory(".");
        schedulerJobInitiationEvent.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);

        BigQueueMessage<SchedulerJobInitiationEventDto> bigQueueMessage
            = new BigQueueMessageBuilder<SchedulerJobInitiationEventDto>()
            .withMessage(schedulerJobInitiationEvent)
            .withMessageProperties(Map.of("contextName", "contextName", "contextInstanceId", "contextInstanceId"))
            .build();

        bigQueue.enqueue(objectMapper.writeValueAsBytes(bigQueueMessage));

        flowTestRule.sleep(2000);

        with().pollInterval(1, TimeUnit.SECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        with().pollInterval(1, TimeUnit.SECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(2, outboundQueue.size()));

        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        ContextualisedScheduledProcessEventImpl event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);

        assertEquals(true, event.isJobStarting());
        assertEquals(false, event.isSuccessful());

        dequeued = outboundQueue.dequeue();
        dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);
        assertEquals(false, event.isJobStarting());
        assertEquals(true, event.isSuccessful());
        assertEquals(false, event.isDryRun());

        bigQueue.removeAll();
        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_job_processing_flow_success_context_not_running() throws IOException {
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 1"));
        flowTestRule.consumer("Job Consumer")
            .converter("JobInitiationEvent to ScheduledStatusEvent")
            .router("Live instance SR Router")
            .producer("Context Instance Ended Route");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue bigQueue = InboundJobQueueCache.instance().get("scheduler-agent-Scheduler Flow 1-inbound-queue");
        bigQueue.removeAll();
        SchedulerJobInitiationEventDto schedulerJobInitiationEvent = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEvent.setContextName("contextId");
        schedulerJobInitiationEvent.setContextInstanceId("context instance id not in the cache");

        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobInstanceDto.setCommandLine("pwd");
        }
        internalEventDrivenJobInstanceDto.setContextName("contextId");
        internalEventDrivenJobInstanceDto.setIdentifier("identifier");
        internalEventDrivenJobInstanceDto.setMinExecutionTime(1000L);
        internalEventDrivenJobInstanceDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobInstanceDto.setWorkingDirectory(".");
        schedulerJobInitiationEvent.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);

        BigQueueMessage<SchedulerJobInitiationEventDto> bigQueueMessage
            = new BigQueueMessageBuilder<SchedulerJobInitiationEventDto>()
            .withMessage(schedulerJobInitiationEvent)
            .withMessageProperties(Map.of("contextName", "contextName", "contextInstanceId", "contextInstanceId"))
            .build();

        bigQueue.enqueue(objectMapper.writeValueAsBytes(bigQueueMessage));

        flowTestRule.sleep(2000);

        with().pollInterval(1, TimeUnit.SECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        with().pollInterval(1, TimeUnit.SECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(0, outboundQueue.size()));

        bigQueue.removeAll();
        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_job_processing_flow_success_dry_run() throws IOException {
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 1"));
        flowTestRule.consumer("Job Consumer")
            .converter("JobInitiationEvent to ScheduledStatusEvent")
            .router("Live instance SR Router")
            .broker("Job Starting Broker")
            .multiRecipientRouter("Job MR Router")
            .producer("Status Producer")
            .broker("Job Monitoring Broker")
            .producer("Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue bigQueue = InboundJobQueueCache.instance().get("scheduler-agent-Scheduler Flow 1-inbound-queue");
        SchedulerJobInitiationEventDto schedulerJobInitiationEvent = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEvent.setDryRun(true);
        schedulerJobInitiationEvent.setDryRunParameters(new DryRunParametersDto());
        schedulerJobInitiationEvent.setContextInstanceId("contextInstanceId");

        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobInstanceDto.setCommandLine("pwd");
        }
        internalEventDrivenJobInstanceDto.setContextName("contextId");
        internalEventDrivenJobInstanceDto.setIdentifier("identifier");
        internalEventDrivenJobInstanceDto.setMinExecutionTime(1000L);
        internalEventDrivenJobInstanceDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobInstanceDto.setWorkingDirectory(".");
        schedulerJobInitiationEvent.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);

        BigQueueMessage<SchedulerJobInitiationEventDto> bigQueueMessage
            = new BigQueueMessageBuilder<SchedulerJobInitiationEventDto>()
            .withMessage(schedulerJobInitiationEvent)
            .withMessageProperties(Map.of("contextName", "contextName", "contextInstanceId", "contextInstanceId"))
            .build();

        bigQueue.enqueue(objectMapper.writeValueAsBytes(bigQueueMessage));

        flowTestRule.sleep(2000);

        with().pollInterval(10, TimeUnit.SECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        with().pollInterval(10, TimeUnit.SECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(2, outboundQueue.size()));

        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        ContextualisedScheduledProcessEventImpl event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);

        assertEquals(true, event.isJobStarting());
        assertEquals(false, event.isSuccessful());

        dequeued = outboundQueue.dequeue();
        dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);
        assertEquals(false, event.isJobStarting());
        assertEquals(true, event.isSuccessful());
        assertEquals(true, event.isDryRun());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_job_processing_flow_success_skipped() throws IOException {
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 1"));
        flowTestRule.consumer("Job Consumer")
            .converter("JobInitiationEvent to ScheduledStatusEvent")
            .router("Live instance SR Router")
            .broker("Job Starting Broker")
            .multiRecipientRouter("Job MR Router")
            .producer("Status Producer")
            .broker("Job Monitoring Broker")
            .producer("Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        IBigQueue bigQueue = InboundJobQueueCache.instance().get("scheduler-agent-Scheduler Flow 1-inbound-queue");
        SchedulerJobInitiationEventDto schedulerJobInitiationEvent = new SchedulerJobInitiationEventDto();
        schedulerJobInitiationEvent.setSkipped(true);
        schedulerJobInitiationEvent.setContextInstanceId("contextInstanceId");
        InternalEventDrivenJobInstanceDto internalEventDrivenJobInstanceDto = new InternalEventDrivenJobInstanceDto();
        internalEventDrivenJobInstanceDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobInstanceDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobInstanceDto.setCommandLine("pwd");
        }
        internalEventDrivenJobInstanceDto.setContextName("contextId");
        internalEventDrivenJobInstanceDto.setIdentifier("identifier");
        internalEventDrivenJobInstanceDto.setMinExecutionTime(1000L);
        internalEventDrivenJobInstanceDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobInstanceDto.setWorkingDirectory(".");
        schedulerJobInitiationEvent.setInternalEventDrivenJob(internalEventDrivenJobInstanceDto);

        BigQueueMessage<SchedulerJobInitiationEventDto> bigQueueMessage
            = new BigQueueMessageBuilder<SchedulerJobInitiationEventDto>()
            .withMessage(schedulerJobInitiationEvent)
            .withMessageProperties(Map.of("contextName", "contextName", "contextInstanceId", "contextInstanceId"))
            .build();

        bigQueue.enqueue(objectMapper.writeValueAsBytes(bigQueueMessage));

        flowTestRule.sleep(2000);

        with().pollInterval(10, TimeUnit.SECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        with().pollInterval(10, TimeUnit.SECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(2, outboundQueue.size()));


        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        ContextualisedScheduledProcessEventImpl event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);

        assertEquals(true, event.isJobStarting());
        assertEquals(false, event.isSuccessful());

        dequeued = outboundQueue.dequeue();
        dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        event = objectMapper.readValue(messageAsString, ContextualisedScheduledProcessEventImpl.class);
        assertEquals(false, event.isJobStarting());
        assertEquals(true, event.isSuccessful());


        flowTestRule.stopFlow();
    }
}
