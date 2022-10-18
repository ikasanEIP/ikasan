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

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduled.model.InternalEventDrivenJobInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.Application;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration.HousekeepLogFilesProcessConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.ootb.scheduler.agent.rest.cache.InboundJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.dto.*;
import org.ikasan.serialiser.model.JobExecutionContextDefaultImpl;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.bigqueue.IBigQueue;

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
public class ApplicationTest {
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
    }

    /**
     * Test simple invocation.
     */
    @Test
    @DirtiesContext
    public void test_create_module_start_and_stop_flow() throws Exception {
        Flow flow = moduleUnderTest.getFlow("Scheduler Flow 3");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("Scheduler Flow 1");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("Scheduler Flow 4");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("Scheduler Flow 2");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("Scheduled Process Event Outbound Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("Housekeep Log Files Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());
    }

    @Test
    @DirtiesContext
    public void test_housekeep_flow_success() throws IOException {
        flowTestRule.withFlow(moduleUnderTest.getFlow("Housekeep Log Files Flow"));
        flowTestRule.consumer("Scheduled Consumer")
            .producer("Log Files Process");

        HousekeepLogFilesProcessConfiguration configuration = flowTestRule
            .getComponentConfig("Log Files Process", HousekeepLogFilesProcessConfiguration.class);
        configuration.setLogFolder("./logs");

        flowTestRule.startFlow();
        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());
        flowTestRule.fireScheduledConsumer();

        flowTestRule.sleep(2000);

        flowTestRule.assertIsSatisfied();

        assertEquals(Flow.RUNNING, flowTestRule.getFlowState());

        assertEquals(0, outboundQueue.size());

        flowTestRule.stopFlow();
    }

    @After
    public void teardown() {
        // post-test teardown
    }
}
