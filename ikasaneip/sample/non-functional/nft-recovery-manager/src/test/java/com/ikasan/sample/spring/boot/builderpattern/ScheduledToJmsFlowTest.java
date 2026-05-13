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

import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.ScheduledRecoveryManager;
import org.ikasan.spec.component.endpoint.EndpointException;
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
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.ikasan.spec.flow.Flow.RECOVERING;
import static org.ikasan.spec.flow.Flow.RUNNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * This test Sftp To JMS Flow.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/cleanErrorTables.sql"}, executionPhase = AFTER_TEST_METHOD)
public class ScheduledToJmsFlowTest extends BaseRecoveryManagerFlowTest
{
    private Logger logger = LoggerFactory.getLogger(ScheduledToJmsFlowTest.class);

    private static String SAMPLE_MESSAGE = "Hello world!";

    @Autowired
    public Module<Flow> moduleUnderTest;

    @Autowired
    public JmsListenerEndpointRegistry registry;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    public MessageListenerVerifier messageListenerVerifier;

    @Rule
    public TestName name = new TestName();


    @Before
    public void setup(){
        messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "sftp.private.jms.queue", registry);
        messageListenerVerifier.start();
        logger.info("running test: " + name.getMethodName());
    }

    @After
    public void teardown()
    {
        ExceptionToggle.reset();
        messageListenerVerifier.stop();
        FakeDataProvider.reset();
        String currentState = flowTestRule.getFlowState();
        if (currentState.equals(RECOVERING) || currentState.equals(RUNNING)){
            flowTestRule.stopFlow();
        }
        new ActiveMqHelper().removeAllMessages();
    }


    @Test
    public void test_consume_recover() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Scheduled To Jms Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        ExceptionToggle.setThrowRetryException(true);

        //Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));


        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 19);

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // there should be no more recovery jobs after we go into stopped in error
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));

        // make sure no data has been published to the downstream JMS
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));

        // Make sure fake data provider has not had any data consumed
        Assert.assertEquals(3, FakeDataProvider.size());
        Assert.assertEquals("message 1", FakeDataProvider.get(0));
        Assert.assertEquals("message 2", FakeDataProvider.get(1));
        Assert.assertEquals("message 3", FakeDataProvider.get(2));
    }

    /**
     * Method to test consuming messages and recovering with successful recovery in a scheduled flow.
     * The test sets up expectations for messages in the FakeDataProvider, starts the flow, triggers the scheduled consumer,
     * simulates a recovery scenario, validates the flow state transitions, error handling, recovery process,
     * and overall flow behavior. It ensures that the recovery process is successful and that the flow returns to a
     * running state after encountering a recovery situation. Finally, it confirms the correctness of error logging
     * and the verification of state transitions in the flow.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void test_consume_goes_into_recovery_then_recovers_to_running_state() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule(true);
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Scheduled To Jms Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");


        ScheduledConsumer consumer = (ScheduledConsumer) this.flowTestRule.getComponent("Scheduled Consumer");
        consumer.getConfiguration().setCronExpression("0/2 * * * * ? *");
        consumer.getConfiguration().setPersistentRecovery(true);

        //Setup component expectations
        for(int i=0; i<20; i++) {
            FakeDataProvider.add("message "+i);
        }

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(1, messageListenerVerifier.getCaptureResults().size() ));

        ExceptionToggle.setThrowRetryException(true);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        // Let the flow attempt to recover 11 times
        with().pollDelay(Duration.ofMillis(100)).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 11);

        ExceptionToggle.setThrowRetryException(false);

        // make sure the flow goes back into running after recovering
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertTrue(messageListenerVerifier.getCaptureResults().size() > 5));

        // there should be no more recovery jobs after we go into stopped in error
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Method to test consuming messages and recovering with successful recovery in a scheduled flow.
     * The test sets up expectations for messages in the FakeDataProvider, starts the flow, triggers the scheduled consumer,
     * simulates a recovery scenario, validates the flow state transitions, error handling, recovery process,
     * and overall flow behavior. It ensures that the recovery process is successful and that the flow returns to a
     * running state after encountering a recovery situation. Finally, it confirms the correctness of error logging
     * and the verification of state transitions in the flow.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void test_consume_goes_into_recovery_then_recovers_to_running_state_non_persistent_recovery() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule(true);
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        ExceptionToggle.setShouldThrowRecoveryExceptionEveryNInvocations(true);
        ExceptionToggle.setNumberOfInvocationsBeforeRetry(2);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Scheduled To Jms Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        ScheduledConsumer consumer = (ScheduledConsumer) this.flowTestRule.getComponent("Scheduled Consumer");
        consumer.getConfiguration().setCronExpression("0/2 * * * * ? *");
        consumer.getConfiguration().setPersistentRecovery(false);

        //Setup component expectations
        for(int i=0; i<20; i++) {
            FakeDataProvider.add("message "+i);
        }

        flowTestRule.startFlow();

        with().pollInterval(10, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(1, messageListenerVerifier.getCaptureResults().size() ));

        with().pollInterval(10, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        with().pollInterval(10, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(1, messageListenerVerifier.getCaptureResults().size()));

        // make sure the flow goes back into running after recovering
        with().pollInterval(10, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertTrue(messageListenerVerifier.getCaptureResults().size() > 5));

        // there should be no more recovery jobs after we go into stopped in error
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Method to test consuming messages and recovering with successful recovery in a scheduled flow.
     * The test sets up expectations for messages in the FakeDataProvider, starts the flow, triggers the scheduled consumer,
     * simulates a recovery scenario, validates the flow state transitions, error handling, recovery process,
     * and overall flow behavior. It ensures that the recovery process is successful and that the flow returns to a
     * running state after encountering a recovery situation. Finally, it confirms the correctness of error logging
     * and the verification of state transitions in the flow.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void test_consume_recover_with_recovery_successful() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Scheduled To Jms Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // Setup component expectations
        FakeDataProvider.add("message 1");
        FakeDataProvider.add("message 2");
        FakeDataProvider.add("message 3");

        flowTestRule.consumer("Scheduled Consumer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        ExceptionToggle.setThrowRetryException(true);

        flowTestRule.fireScheduledConsumerPersistentRecovery();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering",flowTestRule.getFlowState()));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));

        ExceptionToggle.setThrowRetryException(false);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running",flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertTrue(messageListenerVerifier.getCaptureResults().size() > 0));

        // there should be no more recovery jobs after we go into stopped in error
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Method to test the stop behavior of message consumption in a scheduled flow.
     * The test sets up expectations for messages in the FakeDataProvider, starts the flow,
     * triggers the scheduled consumer, and checks the flow state after encountering a stop event.
     * It verifies that the errors are properly handled and logged, and confirms the flow state transitions.
     * Additionally, it ensures that the scheduled recovery jobs are stopped after encountering an error,
     * messages are processed correctly, and the test assertions are satisfied.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void test_consume_stop() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Scheduled To Jms Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        ExceptionToggle.setShouldThrowStoppedInErrorException(true);

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

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop"));
        });


        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() ->  assertEquals(0, messageListenerVerifier.getCaptureResults().size() ));
        flowTestRule.assertIsSatisfied();

        Assert.assertEquals(3, FakeDataProvider.size());
        Assert.assertEquals("message 1", FakeDataProvider.get(0));
        Assert.assertEquals("message 2", FakeDataProvider.get(1));
        Assert.assertEquals("message 3", FakeDataProvider.get(2));
    }

    /**
     * Method to test the exclusion flow when consuming messages from a scheduled consumer.
     * The test sets up expectations for messages in the FakeDataProvider, starts the flow,
     * fires the scheduled consumer, and asserts the flow state during and after the exclusion.
     * It also checks whether the exclusion event is properly stored in the database with the
     * corresponding details. Finally, it validates the behavior of the exclusion by verifying
     * the processed and excluded messages.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    public void test_consume_exclude() throws Exception
    {
        this.flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Scheduled To Jms Flow"));

        ExceptionToggle.setShouldThrowExclusionException(true);

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

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

}
