/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */
package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.ikasan.spec.flow.Flow.RECOVERING;
import static org.ikasan.spec.flow.Flow.RUNNING;
import static org.junit.Assert.assertEquals;

/**
 * This test Sftp To JMS Flow.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScheduledToJmsFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    @Resource
    public Module<Flow> moduleUnderTest;

    @Resource
    public JmsListenerEndpointRegistry registry;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule( );


    public MessageListenerVerifier messageListenerVerifier;

    @Resource
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    @Resource
    private ExclusionManagementService exclusionManagementService;

    private ErrorReportingService errorReportingService;

    @Before
    public void setup(){
        messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "sftp.private.jms.queue", registry);
        messageListenerVerifier.start();

        errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));
    }

    @After public void teardown()
    {
        messageListenerVerifier.stop();
        FakeDataProvider.reset();
        String currentState = flowTestRule.getFlowState();
        if (currentState.equals(RECOVERING) || currentState.equals(RUNNING)){
            flowTestRule.stopFlow();
        }
        new ActiveMqHelper().removeAllMessages();
    }

    @Test
    public void test_consume_success()
    {
        //Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer")
            .broker("Exception Generating Broker")
            .producer("Scheduled Jms Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
              .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
              .untilAsserted(() ->  assertEquals(1, messageListenerVerifier.getCaptureResults().size() ));
        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(2, FakeDataProvider.size());
        Assert.assertEquals("message 2", FakeDataProvider.get(0));
        Assert.assertEquals("message 3", FakeDataProvider.get(1));
    }

    @Test
    public void test_consume_recover() throws Exception
    {
        ExceptionGeneratingBroker broker = (ExceptionGeneratingBroker) this.flowTestRule.getComponent("Exception Generating Broker");
        broker.setShouldThrowRecoveryException(true);

        //Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));
        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(3, FakeDataProvider.size());
        Assert.assertEquals("message 1", FakeDataProvider.get(0));
        Assert.assertEquals("message 2", FakeDataProvider.get(1));
        Assert.assertEquals("message 3", FakeDataProvider.get(2));
    }

    @Test
    public void test_consume_recover_with_recovery_successful() throws Exception
    {
        ExceptionGeneratingBroker broker = (ExceptionGeneratingBroker) this.flowTestRule.getComponent("Exception Generating Broker");
        broker.setShouldThrowRecoveryException(true);

        // Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer")
            .broker("Exception Generating Broker")
            .consumer("Scheduled Consumer")
            .broker("Exception Generating Broker")
            .producer("Scheduled Jms Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumerPersistentRecovery();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(2, FakeDataProvider.size());
        Assert.assertEquals("message 2", FakeDataProvider.get(0));
        Assert.assertEquals("message 3", FakeDataProvider.get(1));
    }

    @Test
    public void test_consume_stop() throws Exception
    {
        ExceptionGeneratingBroker broker = (ExceptionGeneratingBroker) this.flowTestRule.getComponent("Exception Generating Broker");
        broker.setShouldThrowStoppedInErrorException(true);

        //Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("stoppedInError",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));
        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(3, FakeDataProvider.size());
        Assert.assertEquals("message 1", FakeDataProvider.get(0));
        Assert.assertEquals("message 2", FakeDataProvider.get(1));
        Assert.assertEquals("message 3", FakeDataProvider.get(2));
    }

    @Test
    public void test_consume_exclude() throws Exception
    {
        ExceptionGeneratingBroker broker = (ExceptionGeneratingBroker) this.flowTestRule.getComponent("Exception Generating Broker");
        broker.setShouldThrowExclusionException(true);

        //Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(2, FakeDataProvider.size()));

        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(2, FakeDataProvider.size());
        Assert.assertEquals("message 2", FakeDataProvider.get(0));
        Assert.assertEquals("message 3", FakeDataProvider.get(1));

        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ExcludeEvent", error.getAction());

        // Verify the exclusion was stored to DB was stored in DB
        assertExclusionsWithWait(1);
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(1, exclusions.size());
        ExclusionEvent exclusionEvent = (ExclusionEvent) exclusions.get(0);
        assertEquals(error.getUri(), exclusionEvent.getErrorUri());
        assertEquals("message 1", error.getEventAsString());
    }

    private void assertExclusionsWithWait(int expectedNumberOfExclusions) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfExclusions, exclusions.size());
            });
    }

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

}
