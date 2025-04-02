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

import jakarta.jms.JMSException;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.ScheduledRecoveryManager;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MultiThreadedJmsSampleFlowTest extends BaseRecoveryManagerFlowTest {

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(MultiThreadedJmsSampleFlowTest.class);

    @Rule
    public TestName name = new TestName();
    @Resource
    private Module<Flow> moduleUnderTest;
    @Resource
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    private BrowseMessagesOnQueueVerifier browseMessagesOnQueueVerifier;

    @Before
    public void setup() throws JMSException, SQLException {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow"));
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, "target" );
        browseMessagesOnQueueVerifier.start();
        logger.info("Running test: " + name.getMethodName());
        ExceptionToggle.reset();
    }

    @After
    public void teardown() throws Exception {
        browseMessagesOnQueueVerifier.stop();
        removeAllMessages();
        clearDatabase();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError", "running"});
        ExceptionToggle.reset();
    }



    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    /**
     * Test method for verifying that the flow stops in error state.
     * The method sets a flag to indicate that an error should be thrown when the flow runs. It then retrieves necessary components
     * for testing from the flow and scheduler.
     * It sends staggered messages, starts the flow, waits for it to transition to a 'stoppedInError' state,
     * asserts any errors that occurred during the flow execution, and checks for any exclusions.
     * Finally, it asserts that there are no further recovery jobs scheduled after the flow encounters an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_stopped_in_error() throws InterruptedException {
        ExceptionToggle.setShouldThrowStoppedInErrorException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("stoppedInError"));

//        super.assertErrorsWithWait(5);

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop"));
        });

        super.assertExclusionsWithWait(0);

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test method for verifying the flow excluding events.
     * The method sets a flag to indicate that an exclusion exception should be thrown when the flow runs.
     * It retrieves necessary components for testing from the flow and scheduler.
     * Sends staggered messages, starts the flow, and asserts any errors that occurred during the flow execution.
     * It checks for exclusions to ensure specific conditions are met, and verifies no further recovery jobs scheduled after encountering an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_exclude_events() throws InterruptedException {
        ExceptionToggle.setShouldThrowExclusionException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        super.assertExclusionsWithWait(5);
//        super.assertErrorsGreaterThanWithWait(4);

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertTrue(errorOccurrence.getExceptionClass().equals(SampleGeneratedException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("ExcludeEvent"));
        });

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("running"));

        Assert.assertEquals(0, this.browseMessagesOnQueueVerifier.getCaptureResults().size());

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test the flow behavior in recovery and retries until it stops in an error state.
     * This method sets a flag to indicate that a retry exception should be thrown when the flow runs.
     * It retrieves necessary components for testing from the flow and scheduler, sends staggered messages,
     * starts the flow, waits for it to transition to a 'stoppedInError' state, asserts any errors that occurred during the flow execution,
     * and checks for any exclusions. Finally, it asserts that there are no further recovery jobs scheduled after the flow encounters an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_in_recovery_and_expires_retries_until_stopped_in_error() throws InterruptedException {
        ExceptionToggle.setThrowRetryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 1);

//        super.assertErrorsGreaterThanWithWait(0);

        // Verify the error was stored in DB
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

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("stoppedInError"));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test the flow behavior in recovery and recovers after encountering exceptions.
     * This method simulates flow recovery by setting a flag to throw retry exceptions when the flow runs.
     * It retrieves necessary components for testing from the flow and scheduler, sends staggered messages,
     * starts the flow, waits for it to transition through different states, and asserts the flow state changes.
     * It also validates error occurrences, checks for exclusions, and ensures no further recovery jobs are scheduled after an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_in_recovery_and_recovers() throws InterruptedException {
        ExceptionToggle.setThrowRetryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 1);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("recovering"));

        ExceptionToggle.setThrowRetryException(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));

//        super.assertErrorsGreaterThanWithWait(0);

        // Verify the error was stored in DB
        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        // Verify the exclusion was not stored to DB
        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(5, browseMessagesOnQueueVerifier.getCaptureResults().size()));

        // As we have recovered and all looks good, sending some more messages for good measure.
        this.sendStaggeredMessages(5);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(10, browseMessagesOnQueueVerifier.getCaptureResults().size()));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));

    }

    /**
     * Test the flow behavior in recovery with a recovery exception thrown every third invocation and recovers.
     * The method configures the test by setting the necessary flags and components to simulate the flow recovery scenario.
     * Staggered messages are sent, the flow is started, and the test awaits the transition of the flow to a specific state.
     * It asserts error occurrences, validates error types and actions, checks for exclusions, and verifies successful recovery.
     * Finally, the method ensures no further recovery jobs are scheduled after the flow encounters an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_in_recovery_with_recovery_exception_thrown_every_third_invocation_and_recovers() throws InterruptedException {
        ExceptionToggle.setShouldThrowRecoveryExceptionEveryNInvocations(true);
        ExceptionToggle.setNumberOfInvocationsBeforeRetry(3);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 1);

        ExceptionToggle.setShouldThrowRecoveryExceptionEveryNInvocations(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("running"));

//        super.assertErrorsGreaterThanWithWait(0);

        // Verify the error was stored in DB
        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null
            , null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        // Verify the exclusion was not stored to DB
        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(5, browseMessagesOnQueueVerifier.getCaptureResults().size()));

        // As we have recovered and all looks good, sending some more messages for good measure.
        this.sendStaggeredMessages(5);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(10, browseMessagesOnQueueVerifier.getCaptureResults().size()));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test method for verifying the flow behavior in scheduled recovery and retries until it stops in an error state.
     * The method sets a flag to indicate that an exception should be thrown during scheduled recovery.
     * It retrieves necessary components for testing from the flow and scheduler.
     * Staggered messages are sent, the flow is started, and then it waits for the flow to transition to a 'stoppedInError' state.
     * The method asserts any errors that occurred during the flow execution and checks for exclusions.
     * Additionally, it ensures that there are no further recovery jobs scheduled after encountering an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_in_scheduled_recovery_and_expires_retries_until_stopped_in_error() throws InterruptedException {
        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ofMillis(100)).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 5);

//        super.assertErrorsGreaterThanWithWait(0);

        // Verify the error was stored in DB
        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(SampleScheduledRecoveryGeneratedException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)"));
        });

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null
            , null, null, null, 100);
        assertEquals(0, exclusions.size());

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("stoppedInError"));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test the flow behavior in scheduled recovery and recovers after encountering exceptions.
     * This method simulates flow recovery by setting a flag to throw retry exceptions when the flow runs.
     * It retrieves necessary components for testing from the flow and scheduler, sends staggered messages,
     * starts the flow, waits for it to transition through different states, and asserts the flow state changes.
     * It also validates error occurrences, checks for exclusions, and ensures no further recovery jobs
     * are scheduled after an error state.
     *
     * @throws InterruptedException if the test is interrupted during execution
     */
    @Test
    public void test_flow_in_scheduled_recovery_and_recovers() throws InterruptedException {
        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Multi Threaded Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        this.sendStaggeredMessages(5);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 1);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("recovering"));

        ExceptionToggle.setShouldThrowScheduledRecoveryException(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .until(() -> flowTestRule.getFlowState().equals("running"));

//        super.assertErrorsGreaterThanWithWait(0);

        // Verify the error was stored in DB
        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have SampleScheduledRecoveryGeneratedException except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(SampleScheduledRecoveryGeneratedException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(5, browseMessagesOnQueueVerifier.getCaptureResults().size()));

        logger.info("recovery jobs" + ReflectionTestUtils.invokeMethod(recoveryManager, "getCurrentScheduledRecoveryJobs"));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));

        // As we have recovered and all looks good, sending some more messages for good measure.
        this.sendStaggeredMessages(5);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals(10, browseMessagesOnQueueVerifier.getCaptureResults().size()));
    }

    private void sendStaggeredMessages(int numMessages) throws InterruptedException {
        // a message for each thread
        for(int i=0; i<numMessages; i++) {
            jmsTemplate.convertAndSend("source", "message"+i);
            Thread.sleep(1000);
        }
    }

    /**
     * On retry the original message is rolled back - this leaves the message on the consumer destination, this can interfere
     * with tests that follow if they are waiting for messages to be *produced* on that destination -
     * contrary to popular belief the AMQBroker is outside the control of Spring
     * so there is no AMQ restart between tests regardless what DirtiesContext is set to.
     *
     * @throws JMSException
     */
    private void removeAllMessages() throws Exception {
        new ActiveMqHelper().removeAllMessages();
    }
}
