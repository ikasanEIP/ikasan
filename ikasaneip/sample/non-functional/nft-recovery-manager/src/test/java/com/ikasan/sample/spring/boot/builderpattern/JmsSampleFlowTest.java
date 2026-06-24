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
public class JmsSampleFlowTest extends BaseRecoveryManagerFlowTest {

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(JmsSampleFlowTest.class);

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
    public void setup() throws JMSException {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Sample Flow"));
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, "target" );
        browseMessagesOnQueueVerifier.start();
        logger.info("running test: " + name.getMethodName());
    }

    @After
    public void teardown() throws Exception {
        browseMessagesOnQueueVerifier.stop();
        removeAllMessages();
        clearDatabase();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError","running","recovering"});
        ExceptionToggle.reset();
    }



    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }


    /**
     * This method tests the flow behavior when it stops due to an error condition.
     * It prepares test data by sending a JMS message and setting up the flow with specific error conditions to trigger a stopped flow state.
     * The method asserts the occurrence of errors and validates the flow state after the error condition.
     * It also verifies that there are no recovery jobs scheduled once the flow transitions to a stopped state.
     */
    @Test
    public void test_flow_stopped_in_error() {
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        ExceptionToggle.setShouldThrowStoppedInErrorException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

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
    }

    /**
     * This method tests the flow behavior when excluding a message in the flow execution.
     * It prepares test data by sending a JMS message and setting up specific error conditions to trigger the exclusion of the message in the flow.
     * The method asserts the occurrence of errors related to the excluded message and validates the flow state after the exclusion.
     * It also verifies that there are no recovery jobs scheduled once the flow transitions to a stopped state.
     */
    @Test
    public void test_flow_excludes_message() {
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        ExceptionToggle.setShouldThrowExclusionException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
                assertTrue(errorOccurrence.getExceptionClass().equals(SampleGeneratedException.class.getName()));
                assertTrue(errorOccurrence.getAction().equals("ExcludeEvent"));
        });

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(500)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertTrue(exclusionManagementService.find(null, null
                , null, null, null, 100).size() > 0));

        super.assertExclusionsWithWait(1);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * This method tests the flow behavior when it stops due to an error condition.
     * It prepares test data by sending a JMS message and setting up the flow with specific error conditions to trigger a stopped flow state.
     * The method asserts the occurrence of errors and validates the flow state after the error condition.
     * It also verifies that there are no recovery jobs scheduled once the flow transitions to a stopped state.
     */
    @Test
    public void test_flow_in_recovery_and_exceeds_retries_flow_stopped_in_error() {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        ExceptionToggle.setThrowRetryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ofMillis(100)).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 20);

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
    }

    /**
     * Method to test the flow behavior in a scenario where the flow is in recovery mode and exceeds the allowed retries,
     * then successfully recovers from the error condition.
     * Sends a JMS message, sets up error conditions, and validates the recovery and retry mechanism of the flow.
     * Asserts the occurrence of errors, validates flow states during recovery and after successful recovery.
     * Also checks for the absence of recovery jobs once the flow has recovered.
     */
    @Test
    public void test_flow_in_recovery_and_exceeds_retries_flow_then_flow_recovers() {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        ExceptionToggle.setThrowRetryException(true);

        // Get a handle to a few things to inspect
        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // Let the flow attempt to recover 11 times
        with().pollDelay(Duration.ofMillis(100)).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 11);

        // Asset flow is recovering
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        // stop throwing the retry exception
        ExceptionToggle.setThrowRetryException(false);

        // Asset flow is running again
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // all errors are endpoint exceptions with retries
            assertTrue(errorOccurrence.getExceptionClass().equals(EndpointException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Retry (delay=100, maxRetries=20)"));
        });

        super.assertExclusionsWithWait(0);

        // there should be no more recovery jobs after flow recovers
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Method to test the flow behavior in a scenario where the flow is in scheduled recovery mode
     * and exceeds the allowed retries, leading to the flow being stopped due to an error.
     *
     * This method sets up the necessary test data by sending a JMS message and configuring
     * the flow with specific error conditions to trigger a stopped state. It verifies that the
     * flow runs and handles errors as expected, asserting the occurrence of errors and validating
     * the flow state after the error condition. Additionally, it ensures that there are no recovery
     * jobs scheduled once the flow transitions to a stopped state.
     */
    @Test
    public void test_flow_in_scheduled_recovery_and_exceed_retries_flow_stopped_in_error() {
        String message = SAMPLE_MESSAGE;

        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        with().pollDelay(Duration.ofMillis(100)).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 10);

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // For the most part we have EndpointExceptions except for a RuntimeException when the retries are exhausted.
            assertTrue(errorOccurrence.getExceptionClass().equals(RuntimeException.class.getName()) ||
                errorOccurrence.getExceptionClass().equals(SampleScheduledRecoveryGeneratedException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("Stop") ||
                errorOccurrence.getAction().equals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)"));
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // there should be no more recovery jobs after flow goes into stopped in error!
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Method to test the flow behavior in a scenario where the flow is in scheduled recovery mode
     * and exceeds the allowed retries, leading to the flow being stopped due to an error.
     *
     * This method sets up the necessary test data by sending a JMS message and configuring
     * the flow with specific error conditions to trigger a stopped state. It verifies that the
     * flow runs and handles errors as expected, asserting the occurrence of errors and validating
     * the flow state after the error condition. Additionally, it ensures that there are no recovery
     * jobs scheduled once the flow transitions to a stopped state.
     */
    @Test
    public void test_flow_in_scheduled_recovery_and_exceed_retries_flow_flow_recovers() {
        String message = SAMPLE_MESSAGE;

        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("Jms Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // now let's recover up until 6 attempts
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 6);

        // make sure we're recovering
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        // Make the recovery exception go away.
        ExceptionToggle.setShouldThrowScheduledRecoveryException(false);

        // We'll now recover and go back to a running state
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        // Let's make sure the errors are all as we expect them to be.
        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            // All error are EndpointExceptions with a scheduled retry.
            assertTrue(errorOccurrence.getExceptionClass().equals(SampleScheduledRecoveryGeneratedException.class.getName()));
            assertTrue(errorOccurrence.getAction().equals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)"));
        });

        super.assertExclusionsWithWait(0);

        // there should be no more recovery jobs after flow recovers
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
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
