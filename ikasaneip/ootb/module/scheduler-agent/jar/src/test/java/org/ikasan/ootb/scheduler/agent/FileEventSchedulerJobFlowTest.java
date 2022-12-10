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
import liquibase.pro.packaged.C;
import org.ikasan.bigqueue.IBigQueue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
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
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;
import org.junit.*;
import org.junit.runner.RunWith;
import org.quartz.Trigger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.quartz.TriggerBuilder.newTrigger;

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
public class FileEventSchedulerJobFlowTest {
    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private IBigQueue outboundQueue;

    @Resource
    private DryRunModeService dryRunModeService;

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
        if(new File("src/test/resources/data/archive/test.txt").exists()) {
            FileUtils.moveFileToDirectory(new File("src/test/resources/data/archive/test.txt")
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
    public void test_file_flow_success_without_aspect() throws IOException {
        String contextName = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());


        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_outside_blackout_window_success() throws IOException {
        String contextName = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("0 15 10 * * ? 3000"));

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success() throws IOException {
        String contextName = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        String contextInstanceIdentifier = UUID.randomUUID().toString();

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_quartz_flow_not_filtered_due_to_inside_blackout_window_success_event_filtered() throws IOException {
        String contextName = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(UUID.randomUUID().toString()));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        BlackoutRouterConfiguration blackoutRouterConfiguration
            = flowTestRule.getComponentConfig("Blackout Router", BlackoutRouterConfiguration.class);
        blackoutRouterConfiguration.setCronExpressions(List.of("*/1 * * * * ?"));

        ScheduledProcessEventFilterConfiguration scheduledProcessEventFilterConfiguration
            = flowTestRule.getComponentConfig("Publish Scheduled Status", ScheduledProcessEventFilterConfiguration.class);
        scheduledProcessEventFilterConfiguration.setDropOnBlackout(true);

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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

    @Test
    @DirtiesContext
    // TODO need to think about this case as may not be necessary.
    public void test_file_flow_recovery_no_instance_in_cache() {
        // do not put it in the cache
        String contextName = RandomStringUtils.randomAlphabetic(11);

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(UUID.randomUUID().toString()));

        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");
        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumerWithExistingTriggerEnhanced();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RECOVERING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_with_aspect() throws IOException {
        String contextName = createContextAndPutInCache();

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

        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_success_with_aspect_job_dry_run() throws IOException {
        String contextName = createContextAndPutInCache();

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

        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        event = this.getEvent();

        // Confirm that the correlating identifier has been carried through.
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();
    }

    @Test
    @DirtiesContext
    public void test_file_flow_with_filter() throws IOException {
        String contextName = createContextAndPutInCache();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduler Flow 2"));

        ContextInstanceFilterConfiguration contextInstanceFilterConfiguration = flowTestRule.getComponentConfig("Context Instance Active Filter"
            , ContextInstanceFilterConfiguration.class);
        contextInstanceFilterConfiguration.setContextName(contextName);


        String contextInstanceIdentifier = UUID.randomUUID().toString();
        CorrelatedFileConsumerConfiguration fileConsumerConfiguration = flowTestRule.getComponentConfig("File Consumer"
            , CorrelatedFileConsumerConfiguration.class);
        fileConsumerConfiguration.setFilenames(List.of("src/test/resources/data/test1.txt"));
        fileConsumerConfiguration.setCorrelatingIdentifiers(List.of(contextInstanceIdentifier));


        MoveFileBrokerConfiguration moveFileBrokerConfiguration = flowTestRule.getComponentConfig("File Move Broker"
            , MoveFileBrokerConfiguration.class);
        moveFileBrokerConfiguration.setMoveDirectory("src/test/resources/data/archive");

        flowTestRule.consumer("File Consumer")
            .filter("Context Instance Active Filter")
            .filter("File Age Filter")
            .filter("Duplicate Message Filter")
            .broker("File Move Broker")
            .converter("JobExecution to ScheduledStatusEvent")
            .router("Blackout Router")
            .producer("Scheduled Status Producer")
            .consumer("File Consumer")
            .filter("Context Instance Active Filter")
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
        Assert.assertEquals(contextInstanceIdentifier, event.getContextInstanceId());

        flowTestRule.stopFlow();

        dryRunModeService.setDryRunMode(false);
    }

    @Test
    @DirtiesContext
    public void test_file_aspect() {
        JobExecutionContextDefaultImpl context = new JobExecutionContextDefaultImpl();
        Trigger trigger = newTrigger().withIdentity("Job Name", "Job Group").build();
        context.setTrigger(trigger);

        // will get an error as the file message provider has nothing wired in etc
        FileMessageProvider fileMessageProvider = new FileMessageProvider();

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

        List<File> files = fileMessageProvider.invoke(context);
        assertEquals(1, files.size());
        assertEquals("/my/bogus/file3.txt", files.get(0).getAbsolutePath());

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
        String contextName = RandomStringUtils.randomAlphabetic(15);
        ContextInstanceImpl instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextInstanceCache.instance().put(contextName, instance);
        return contextName;
    }
}
