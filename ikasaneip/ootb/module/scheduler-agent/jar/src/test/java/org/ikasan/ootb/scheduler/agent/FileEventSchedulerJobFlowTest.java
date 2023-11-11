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
import jakarta.annotation.Resource;
import org.ikasan.bigqueue.IBigQueue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileList;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatingFileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ScheduledProcessEventFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.*;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This test class supports the <code>vanilla integration module</code> application.
 *
 * @author Ikasan Development Team
 */
@SpringBootTest(classes = {Application.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestConfiguration.class})
public class FileEventSchedulerJobFlowTest {
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private IBigQueue outboundQueue;

    @Resource
    private DryRunModeService dryRunModeService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public IkasanFlowTestExtensionRule flowTestRule = new IkasanFlowTestExtensionRule();


    @BeforeAll
    static void setupObjectMapper() {
        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class)
            .addAbstractTypeMapping(ContextParameter.class, ContextParameterInstanceDto.class)
            .addAbstractTypeMapping(DryRunParameters.class, DryRunParametersDto.class)
            .addAbstractTypeMapping(InternalEventDrivenJobInstance.class, InternalEventDrivenJobInstanceImpl.class);

        objectMapper.registerModule(simpleModule);
    }

    @BeforeEach
    void setup() throws IOException {
        outboundQueue.removeAll();
        if(new File("src/test/resources/data/archive/test.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
                , new File("src/test/resources/data"), true);
        }
        if(new File("src/test/resources/data/archive/test1.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test1.txt")
                , new File("src/test/resources/data"), true);
        }
    }

    @AfterEach
    void teardown() throws IOException {
        outboundQueue.removeAll();
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
    void test_file_flow_success_without_aspect() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_success_without_aspect_changing_correlating_id() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setDynamicFileName(true);
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        flowTestRule.assertIsSatisfied();

        String contextInstanceIdentifier2 = UUID.randomUUID().toString();

        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test1.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier2));
        flowTestRule.stopFlow();

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(2, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier2, event.getContextInstanceId());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_success_without_aspect_same_correlating_id() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setDynamicFileName(true);
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        flowTestRule.assertIsSatisfied();


        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test1.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));
        flowTestRule.stopFlow();

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(2, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_success_without_aspect_no_correlating_identifier() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        flowTestRule.consumer("File Consumer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(5000);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();

        assertEquals(Flow.STOPPED, flowTestRule.getFlowState());
    }

    @Test
    @DirtiesContext
    void test_quartz_flow_not_filtered_due_to_outside_blackout_window_success() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("0 15 10 * * ? 3000"));

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .filter("Publish Scheduled Status")
            .producer("Blackout Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success_event_filtered() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(UUID.randomUUID().toString()));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration
            = flowTestRule.getComponentConfig("Publish Scheduled Status", ScheduledProcessEventFilterConfiguration.class);
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(true);

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .filter("Publish Scheduled Status");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    // TODO need to think about this case as may not be necessary.
    @Test
    @DirtiesContext
    void test_file_flow_recovery_no_instance_in_cache() {
        // do not create the cache

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(UUID.randomUUID().toString()));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();
        // Note, since the ContextInstanceFilter no longer requires plan name, and the reacts to a
        // JobExecutionContextImpl not a CorrelatedFileList, the flow will continue. This test could be expended with
        // a more complex scenario or make use of JobExecutionContext later
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(1, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_success_with_aspect() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        dryRunModeService.setDryRunMode(true);

        DryRunFileListJobParameter jobs = new DryRunFileListJobParameterDto();
        jobs.setJobName("Flow 2 Job Name");
        jobs.setFileName("/some/bogus/file/bogus1.txt");
        dryRunModeService.addDryRunFileList(List.of(jobs));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));
        fileConsumerConfiguration.setJobName("Flow 2 Job Name");

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        jobs = new DryRunFileListJobParameterDto();
        jobs.setJobName("Flow 2 Job Name");
        jobs.setFileName("/some/bogus/file/bogus1.txt");
        dryRunModeService.addDryRunFileList(List.of(jobs));
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(2, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_success_with_aspect_job_dry_run() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        dryRunModeService.setDryRunMode(false);
        dryRunModeService.setJobDryRun("Scheduler Flow 2", true);

        SchedulerFileFilterConfiguration schedulerFileFilterConfiguration = flowTestRule.getComponentConfig("Duplicate Message Filter"
            , SchedulerFileFilterConfiguration.class);
        schedulerFileFilterConfiguration.setJobName("Scheduler Flow 2");

        FileAgeFilterConfiguration fileAgeFilterConfiguration = flowTestRule.getComponentConfig("File Age Filter"
            , FileAgeFilterConfiguration.class);
        fileAgeFilterConfiguration.setJobName("Scheduler Flow 2");

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));
        fileConsumerConfiguration.setJobName("Scheduler Flow 2");

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .producer("Scheduled Status Producer");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        dryRunModeService.setDryRunMode(false);
        dryRunModeService.setJobDryRun("Scheduler Flow 2", true);

        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(2, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    void test_file_flow_with_filter() throws IOException {
        createContextAndPutInCache();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();
        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test1.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));


        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        flowTestRule.consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer")
            .consumer("File Consumer")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test1.txt")
            , new File("src/test/resources/data"), true);

        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();
        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(1, outboundQueue.size());

        ContextualisedScheduledProcessEvent event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();

        dryRunModeService.setDryRunMode(false);
    }

    @Test
    @DirtiesContext
    void test_file_aspect() {
        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);

        context.setJobDataMap(new JobDataMap());
        context.getMergedJobDataMap().put(CorrelatingScheduledConsumer.CORRELATION_ID
            , UUID.randomUUID().toString());

        // will get an error as the file message provider has nothing wired in etc
        CorrelatingFileMessageProvider fileMessageProvider = new CorrelatingFileMessageProvider();

        try {
            fileMessageProvider.invoke(context);
            fail("should not get here");
        } catch (Exception e) {
        }

        dryRunModeService.setDryRunMode(true);
        DryRunFileListJobParameterDto dto = new DryRunFileListJobParameterDto();
        dto.setJobName("Job Name");
        dto.setFileName("/my/bogus/file3.txt");
        dryRunModeService.addDryRunFileList(List.of(dto));

        CorrelatedFileList files = fileMessageProvider.invoke(context);
        assertEquals(1, files.getFileList().size());
        assertEquals("/my/bogus/file3.txt", files.getFileList().get(0).getAbsolutePath());

        dryRunModeService.setDryRunMode(false);
    }

    private ContextualisedScheduledProcessEvent getEvent() throws IOException {
        byte[] dequeued = outboundQueue.dequeue();
        BigQueueMessageImpl dequeuedMessage = objectMapper.readValue(dequeued, BigQueueMessageImpl.class);
        String messageAsString = new String(objectMapper.writeValueAsBytes(dequeuedMessage.getMessage()));
        return objectMapper.readValue(messageAsString
            , ContextualisedScheduledProcessEventImpl.class);
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
