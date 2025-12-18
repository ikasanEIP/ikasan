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
import org.awaitility.Awaitility;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

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
@Sql(scripts = {"/cleanDatabaseTables.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class QuartzSchedulerFileEventJobFlowTest {
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private IBigQueue outboundQueue;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public IkasanFlowTestExtensionRule flowTestRule = new IkasanFlowTestExtensionRule();

    @MockitoBean
    AgentInstanceRecoveryManager agentInstanceRecoveryManager;

    @Resource
    ErrorReportingService errorReportingService;

    @BeforeClass
    public static void setupObjectMapper() {
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterInstanceDto.class)
            .addAbstractTypeMapping(DryRunParameters.class, DryRunParametersDto.class)
            .addAbstractTypeMapping(InternalEventDrivenJobInstance.class, InternalEventDrivenJobInstanceImpl.class);

        objectMapper.registerModule(simpleModule);

        InternalFileWatcherJobQueueCache.instance().removeAll();
    }

    @Before
    public void setup() throws IOException {
        outboundQueue.removeAll();
        ContextInstanceCache.instance().removeAll();
    }

    @After
    public void teardown() {

    }

    @Test
    @DirtiesContext
    public void test_flow_success_start_context_cache_NOT_initialised() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(false);

        this.clearAllInternalQueues();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to FileWatcherJobEvent")
            .filter("Context Instances Active Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.fireScheduledConsumerWithExistingTrigger();

        flowTestRule.sleep(2000);

        Awaitility.await().atMost(30, TimeUnit.SECONDS).
            untilAsserted(() -> assertEquals(Flow.RECOVERING, flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        List<ErrorOccurrence> errors = errorReportingService.find(List.of(this.moduleUnderTest.getName())
            , List.of(), List.of(), new Date(0L), new Date(), 1000);

        assertEquals(1, errors.size());
        assertEquals("org.ikasan.ootb.scheduler.agent.module.component.router.AgentRecoveryNotCompleteException", errors.get(0).getExceptionClass());

        flowTestRule.stopFlow();

        assertEquals(Flow.STOPPED, flowTestRule.getFlowState());
    }

    @Test
    @DirtiesContext
    public void test_flow_success_start_context_cache_initialised_but_no_active_instances() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);

        this.clearAllInternalQueues();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to FileWatcherJobEvent")
            .filter("Context Instances Active Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.fireScheduledConsumerWithExistingTrigger();

        flowTestRule.sleep(2000);

        flowTestRule.stopFlow();

        assertEquals(Flow.STOPPED, flowTestRule.getFlowState());

        flowTestRule.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void test_flow_success_start_context_cache_initialised_with_one_active_instance_with_one_downstream_file_watcher_processing_flows() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        ContextInstance contextInstance = new ContextInstanceImpl();
        contextInstance.setId("correlationIdentifier");
        contextInstance.setName("contextName");

        ContextInstanceCache.instance().put(contextInstance.getId(), contextInstance);

        this.clearAllInternalQueues();

        moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1").stop();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));
        FileWatcherJobConverterConfiguration configuration = flowTestRule.getComponentConfig("JobExecution to FileWatcherJobEvent"
            , FileWatcherJobConverterConfiguration.class);
        configuration.setContextName("contextName");
        configuration.setJobName("jobName");
        configuration.setFilename("filename");
        configuration.setFilePath("filepath");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to FileWatcherJobEvent")
            .filter("Context Instances Active Filter")
            .splitter("File Watcher Event Correlation Identifier Splitter")
            .router("File Watcher Job Queue Size Weighted Router")
            .producer("File Watcher Event Producer - scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.fireScheduledConsumerWithExistingTrigger();

        flowTestRule.sleep(2000);

        Awaitility.await().atMost(30, TimeUnit.SECONDS).
            untilAsserted(() -> assertEquals(Flow.RUNNING, flowTestRule.getFlowState()));


        List<ErrorOccurrence> errors = errorReportingService.find(List.of(this.moduleUnderTest.getName())
            , List.of(), List.of(), new Date(0L), new Date(), 1000);

        assertEquals(0, errors.size());

        flowTestRule.stopFlow();

        assertEquals(Flow.STOPPED, flowTestRule.getFlowState());

        flowTestRule.assertIsSatisfied();

        IBigQueue bigQueue = InternalFileWatcherJobQueueCache.instance().get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");

        Assert.assertEquals(1, bigQueue.size());
    }

    @Test
    @DirtiesContext
    public void test_flow_success_start_context_cache_initialised_with_two_active_instances_with_one_downstream_file_watcher_processing_flows() throws IOException {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        ContextInstance contextInstance1 = new ContextInstanceImpl();
        contextInstance1.setId("correlationIdentifier1");
        contextInstance1.setName("contextName");

        ContextInstance contextInstance2 = new ContextInstanceImpl();
        contextInstance2.setId("correlationIdentifier2");
        contextInstance2.setName("contextName");

        ContextInstanceCache.instance().put(contextInstance1.getId(), contextInstance1);
        ContextInstanceCache.instance().put(contextInstance2.getId(), contextInstance1);

        this.clearAllInternalQueues();

        moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1").stop();

        IBigQueue bigQueue1 = InternalFileWatcherJobQueueCache.instance().get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));
        FileWatcherJobConverterConfiguration configuration = flowTestRule.getComponentConfig("JobExecution to FileWatcherJobEvent"
            , FileWatcherJobConverterConfiguration.class);
        configuration.setContextName("contextName");
        configuration.setJobName("jobName");

        flowTestRule.consumer("Scheduled Consumer")
            .converter("JobExecution to FileWatcherJobEvent")
            .filter("Context Instances Active Filter")
            .splitter("File Watcher Event Correlation Identifier Splitter")
            .router("File Watcher Job Queue Size Weighted Router")
            .producer("File Watcher Event Producer - scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue")
            .router("File Watcher Job Queue Size Weighted Router")
            .producer("File Watcher Event Producer - scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.fireScheduledConsumerWithExistingTrigger();

        flowTestRule.sleep(2000);

        Awaitility.await().atMost(30, TimeUnit.SECONDS).
            untilAsserted(() -> assertEquals(Flow.RUNNING, flowTestRule.getFlowState()));


        List<ErrorOccurrence> errors = errorReportingService.find(List.of(this.moduleUnderTest.getName())
            , List.of(), List.of(), new Date(0L), new Date(), 1000);

        assertEquals(0, errors.size());

        flowTestRule.stopFlow();

        assertEquals(Flow.STOPPED, flowTestRule.getFlowState());

        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(2, bigQueue1.size());
    }

    /**
     * Clears all internal queues stored in the InternalFileWatcherJobQueueCache.
     * Loops through each key in the cache, attempts to retrieve the IBigQueue object,
     * and removes all elements from it. If an IOException occurs during the removal process,
     * a RuntimeException is thrown.
     */
    private void clearAllInternalQueues() {
        InternalFileWatcherJobQueueCache.instance().keys().forEach(key -> {
            try {
                InternalFileWatcherJobQueueCache.instance().get(key).removeAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
