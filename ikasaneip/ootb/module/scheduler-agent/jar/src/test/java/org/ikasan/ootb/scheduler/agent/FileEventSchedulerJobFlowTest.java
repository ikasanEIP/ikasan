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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.job.orchestration.model.context.ContextParameterInstanceImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.serialiser.FileWatcherJobEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.ContextParameterInstanceDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
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
public class FileEventSchedulerJobFlowTest {
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private IBigQueue outboundQueue;

    @Resource
    private DryRunModeService dryRunModeService;

    @Resource
    private ErrorReportingService errorReportingService;

    @MockitoBean
    AgentInstanceRecoveryManager agentInstanceRecoveryManager;

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
        ContextInstanceCache.instance().removeAll();
        outboundQueue.removeAll();
        if(new File("src/test/resources/data/archive/test.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
                , new File("src/test/resources/data"), true);
        }
        if(new File("src/test/resources/data/archive/test_abc.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test_abc.txt")
                , new File("src/test/resources/data"), true);
        }
        if(new File("src/test/resources/data/archive/test1.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test1.txt")
                , new File("src/test/resources/data"), true);
        }
    }

    @After
    public void teardown() throws IOException {
        outboundQueue.removeAll();
        ContextInstanceCache.instance().removeAll();
        if(new File("src/test/resources/data/archive/test.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
                , new File("src/test/resources/data"), true);
        }
        if(new File("src/test/resources/data/archive/test1.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test1.txt")
                , new File("src/test/resources/data"), true);
        }
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setMoveDirectory("src/test/resources/data/archive");
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);


        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_dynamic_filename() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        ContextParameterInstanceImpl contextParameter = new ContextParameterInstanceImpl();
        contextParameter.setName("filename_replacement");
        contextParameter.setValue("abc");
        ContextInstance contextInstance = ((Map<String, ContextInstance>)ReflectionTestUtils.getField(ContextInstanceCache.instance(), "contextInstanceMap"))
            .values().stream().findFirst().get();
        contextInstance.setContextParameters(List.of(contextParameter));

        String contextInstanceIdentifier = contextInstance.getId();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setMoveDirectory("src/test/resources/data/archive");
        fileWatcherJobEvent.setFilename("src/test/resources/data/test_xxx.txt");
        fileWatcherJobEvent.setFileNameSpelExpression("#fileNamePattern.replace('xxx', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filename_replacement'))");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_dynamic_file_path() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        ContextParameterInstanceImpl contextParameter = new ContextParameterInstanceImpl();
        contextParameter.setName("filepath_replacement");
        contextParameter.setValue("data");
        ContextInstance contextInstance = ((Map<String, ContextInstance>)ReflectionTestUtils.getField(ContextInstanceCache.instance(), "contextInstanceMap"))
            .values().stream().findFirst().get();
        contextInstance.setContextParameters(List.of(contextParameter));

        String contextInstanceIdentifier = contextInstance.getId();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setMoveDirectory("src/test/resources/data/archive");
        fileWatcherJobEvent.setFilename("test.txt");
        fileWatcherJobEvent.setFilePath("src/test/resources/<path-replacement>");
        fileWatcherJobEvent.setFilePathSpelExpression("#filePathPattern.replace('<path-replacement>', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filepath_replacement'))");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));
        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_dynamic_filename_and_file_path() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        ContextParameterInstanceImpl contextParameter = new ContextParameterInstanceImpl();
        contextParameter.setName("filename_replacement");
        contextParameter.setValue("abc");
        ContextParameterInstanceImpl contextParameter2 = new ContextParameterInstanceImpl();
        contextParameter2.setName("filepath_replacement");
        contextParameter2.setValue("data");
        ContextInstance contextInstance = ((Map<String, ContextInstance>)ReflectionTestUtils.getField(ContextInstanceCache.instance(), "contextInstanceMap"))
            .values().stream().findFirst().get();
        contextInstance.setContextParameters(List.of(contextParameter, contextParameter2));

        String contextInstanceIdentifier = contextInstance.getId();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("test_xxx.txt");
        fileWatcherJobEvent.setFilePath("src/test/resources/<path-replacement>");
        fileWatcherJobEvent.setFileNameSpelExpression("#fileNamePattern.replace('xxx', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filename_replacement'))");
        fileWatcherJobEvent.setFilePathSpelExpression("#filePathPattern.replace('<path-replacement>', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filepath_replacement'))");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());


        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_dynamic_filename_and_file_path_no_context_parameters_so_replaced_with_empty_string() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        ContextInstance contextInstance = ((Map<String, ContextInstance>)ReflectionTestUtils.getField(ContextInstanceCache.instance(), "contextInstanceMap"))
            .values().stream().findFirst().get();

        String contextInstanceIdentifier = contextInstance.getId();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("test.txt");
        fileWatcherJobEvent.setFilePath("src/test/resources/data");
        fileWatcherJobEvent.setFileNameSpelExpression("#fileNamePattern.replace('xxx', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filename_replacement'))");
        fileWatcherJobEvent.setFilePathSpelExpression("#filePathPattern.replace('<path-replacement>', T(org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache)" +
            ".getContextParameter(#correlatingIdentifier, 'filepath_replacement'))");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_changing_correlating_id() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        String contextInstanceIdentifier2 = UUID.randomUUID().toString();

        fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier2);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(2, outboundQueue.size()));

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier2, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_same_correlating_id() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer")
            .consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);


        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.assertIsSatisfied();
        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_outside_blackout_window_success() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        fileWatcherJobEvent.setBlackoutWindowCronExpressions(List.of("0 15 10 * * ? 3000"));
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration
            = flowTestRule.getComponentConfig("Publish Scheduled Status", ScheduledProcessEventFilterConfiguration.class);
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(false);


        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .filter("Publish Scheduled Status")
            .producer("Blackout Scheduled Status Producer");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        fileWatcherJobEvent.setBlackoutWindowCronExpressions(List.of("*/1 * * * * ?"));

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success_event_filtered() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .filter("Publish Scheduled Status");

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");
        fileWatcherJobEvent.setBlackoutWindowCronExpressions(List.of("*/1 * * * * ?"));

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.sleep(2000);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_with_filter() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();


        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .router("Blackout Router")
            .converter("FileWatcherJobEvent to ScheduledStatusEvent")
            .producer("Scheduled Status Producer")
            .consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setFilename("src/test/resources/data/test1.txt");
        fileWatcherJobEvent.setMoveDirectory("src/test/resources/data/archive");
        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        flowTestRule.sleep(2000);

        FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test1.txt")
            , new File("src/test/resources/data"), true);

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);
        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, outboundQueue.size()));

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();

        dryRunModeService.setDryRunMode(false);
    }

    @Test
    @DirtiesContext
    public void test_exclusion_bad_file_name_directory() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setMoveDirectory("src/test/resources/data/archive");
        fileWatcherJobEvent.setFilename("////\\\\////\\\\bad-file");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);


        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(0, outboundQueue.size()));

        List<ErrorOccurrence> errors = errorReportingService.find(List.of(this.moduleUnderTest.getName())
            , List.of(), List.of(), new Date(0L), new Date(), 1000);

        assertEquals(1, errors.size());
        assertEquals("ExcludeEvent", errors.get(0).getAction());
        assertEquals("org.ikasan.ootb.scheduler.agent.module.component.broker.exception.CorrelatingFileMatcherBrokerException"
            , errors.get(0).getExceptionClass());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_exclusion_bad_file_move_directory() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("File Watcher Job Event Processing Flow 1"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        flowTestRule.consumer("File Event BigQueue Consumer")
            .broker("File Matching Broker")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker");

        IBigQueue queue1 = InternalFileWatcherJobQueueCache.instance()
            .get("scheduler-agent-file-watcher-job-event-processing-flow-1-file-watcher-inbound-queue");
        queue1.removeAll();

        FileWatcherJobEvent fileWatcherJobEvent = new FileWatcherJobEvent();
        fileWatcherJobEvent.setContextName("contextName");
        fileWatcherJobEvent.setCorrelationIdentifier(contextInstanceIdentifier);
        fileWatcherJobEvent.setMoveDirectory("///\\\\\\//## << BAD DIRECTORY! >> ##");
        fileWatcherJobEvent.setFilename("src/test/resources/data/test.txt");

        this.publishBigQueueMessage(fileWatcherJobEvent, queue1);


        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(0, outboundQueue.size()));

        List<ErrorOccurrence> errors = errorReportingService.find(List.of(this.moduleUnderTest.getName())
            , List.of(), List.of(), new Date(0L), new Date(), 1000);

        assertEquals(1, errors.size());
        assertEquals("ExcludeEvent", errors.get(0).getAction());
        assertEquals("org.ikasan.ootb.scheduler.agent.module.component.broker.exception.MoveFileBrokerException"
            , errors.get(0).getExceptionClass());

        flowTestRule.stopFlow();
    }

    /**
     * Retrieves a ContextualisedScheduledProcessEvent from the outbound queue.
     *
     * @throws IOException if an I/O error occurs during processing
     * @return the ContextualisedScheduledProcessEvent retrieved from the outbound queue
     */
    private ContextualisedScheduledProcessEvent getEvent() throws IOException {
        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        return objectMapper.readValue(messageAsString
            , ContextualisedScheduledProcessEventImpl.class);
    }


    /**
     * Generates a new context instance ID using RandomStringUtils and creates a new ContextInstanceImpl object.
     * The generated context instance is then stored in the ContextInstanceCache.
     *
     * @return The context instance ID generated and stored in the cache
     */
    private String createContextAndPutInCache() {
        String contextInstanceID = RandomStringUtils.randomAlphabetic(15);
        ContextInstanceImpl instance = new ContextInstanceImpl();
        instance.setId(contextInstanceID);
        instance.setName("contextInstanceName");
        ContextInstanceCache.instance().put(contextInstanceID, instance);
        return contextInstanceID;
    }

    /**
     * Publishes a message to a BigQueue after creating a BigQueueMessage from a FileWatcherJobEvent.
     *
     * @param event the FileWatcherJobEvent to use for creating the BigQueueMessage
     * @param queue the IBigQueue to enqueue the message to
     */
    private void publishBigQueueMessage(FileWatcherJobEvent event, IBigQueue queue) throws IOException {
        Map<String, String> properties = new HashMap<>();
        if (event.getContextName() != null) {
            properties.put("contextName", event.getContextName());
        }
        if (event.getCorrelationIdentifier() != null) {
            properties.put("contextInstanceId", event.getCorrelationIdentifier());
        }

        queue.enqueue(new FileWatcherJobEventToBigQueueMessageSerialiser().serialise(event));
    }

}
