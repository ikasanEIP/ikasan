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

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.serialiser.BigQueueMessageJsonSerialiser;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow;
import org.ikasan.recovery.ScheduledRecoveryManager;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
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
public class BigQueueSampleFlowTest extends BaseRecoveryManagerFlowTest {

    private Synchroniser synchroniser = new Synchroniser();

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
            setThreadingPolicy(synchroniser);
        }
    };

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(BigQueueSampleFlowTest.class);

    @Rule
    public TestName name = new TestName();

    @Autowired
    private Module<Flow> moduleUnderTest;

    @Autowired
    private IBigQueue outboundQueue;

    @Autowired
    private IBigQueue inboundQueue;

    private final Notifier notifier = mockery.mock(Notifier.class, "mockNotifier");

    @Before
    public void setup() {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("BigQueue Sample Flow"));
        logger.info("running test: " + name.getMethodName());
    }

    @After
    public void teardown() throws Exception {
        System.out.println("In teardown method for test " + name.getMethodName());
        removeAllMessages();
        clearDatabase();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError"});
        ExceptionToggle.reset();
    }


    private void removeAllMessages() throws Exception {
        this.inboundQueue.removeAll();
        this.outboundQueue.removeAll();
    }

    /**
     * Test the flow behavior when it stops in error.
     * <p>
     * This test method simulates the sending of a BigQueue message, enqueues the message,
     * and then forces the flow to throw a stopped in error exception by setting a flag.
     * It verifies that the flow stops in error state and performs certain validations on the error occurrences
     * and recovery job count.
     * </p>
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_stopped_in_error() throws Exception {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        ExceptionToggle.setShouldThrowStoppedInErrorException(true);

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
     * Test the flow behavior when it excludes a message.
     * <p>
     * This test method simulates the sending of a BigQueue message, enqueues the message,
     * and forces the flow to exclude the message by setting a flag. It verifies that the exclusion
     * is handled correctly and performs validations on the error occurrences and recovery job count.
     * </p>
     *
     * @throws IOException if an error occurs during the test
     */
    @Test
    public void test_flow_excludes_message() throws IOException {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        ExceptionToggle.setShouldThrowExclusionException(true);

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
     * Test the flow behavior when an exception is thrown in the start method of the flow,
     * causing the flow to enter the recovery process.
     * <p>
     * This test method sets up a flow with a BigQueue message, forces the flow to throw an exception
     * in the start method, and verifies that the flow goes into recovery by performing various validations.
     * </p>
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_recovery_due_to_exception_in_start_method() throws Exception {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        ExceptionToggle.setThrowStartRetryException(true);

        // start the flow and assert it runs
        try {
            flowTestRule.startFlow();
        }
        catch (Exception e) {
            // we ignore this endpoint exception because we want this flow to go into recovery.
        }

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) == 20);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(EndpointException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("Retry (delay=100, maxRetries=20)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // Asset no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * Test the recovery flow behavior when an exception is thrown in the start method of the flow.
     * The test verifies that the flow goes into recovery mode and properly notifies the necessary components.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_recovery_due_to_exception_in_start_method_recovers_and_notifies() throws Exception {
        mockery.checking(new Expectations() {
            {
                exactly(6).of(notifier).isNotifyStateChangesOnly();
                will(returnValue(true));
                exactly(1).of(notifier).invoke(with("Undefined Environment"), with("nft-recovery-manager")
                    , with("BigQueue Sample Flow"), with("recovering"));
                exactly(1).of(notifier).invoke(with("Undefined Environment"), with("nft-recovery-manager")
                    , with("BigQueue Sample Flow"), with("running"));
            }
        });

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");
        Monitor monitor = (Monitor) ReflectionTestUtils.getField(flow, "monitor");
        monitor.setNotifiers(List.of(notifier));

        ExceptionToggle.setThrowStartRetryException(true);

        // start the flow and assert it runs
        try {
            flowTestRule.startFlow();
        }
        catch (Exception e) {
            // we ignore this endpoint exception because we want this flow to go into recovery.
        }

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 1);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        ExceptionToggle.setThrowStartRetryException(false);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        super.assertExclusionsWithWait(0);

        // Asset no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));

        // we remove the notifier as we end up with some thread synchronisation issues
        // with JMock when asserting the expectations.
        monitor.setNotifiers(List.of());

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_flow_in_recovery_due_to_exception_in_start_method_and_subsequently_recovers() throws Exception {
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        ExceptionToggle.setThrowStartRetryException(true);

        // start the flow and assert it runs
        try {
            flowTestRule.startFlow();
        }
        catch (Exception e) {
            // we ignore this endpoint exception because we want this flow to go into recovery.
        }

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) >= 10);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        ExceptionToggle.setThrowStartRetryException(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));


        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(EndpointException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("Retry (delay=100, maxRetries=20)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        // We should have received our message!
        assertEquals(1, outboundQueue.size());

        // Asset no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * This method tests the flow behavior during recovery process.
     * It simulates sending a BigQueue message, enqueues the message, and triggers a recovery scenario.
     * Various validations are performed on error occurrences, recovery attempts, message queues, and flow state.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_recovery() throws Exception {
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");


        ExceptionToggle.setThrowRetryException(true);


        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) == 20);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(EndpointException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("Retry (delay=100, maxRetries=20)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // Assert no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

    /**
     * This method tests the flow behavior when recovering from an error state.
     * It prepares test data, enqueues a message, starts the flow with exceptions thrown during recovery,
     * and verifies the state transitions and error handling mechanisms.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_recovery_then_recovers() throws Exception {
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();

        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");


        ExceptionToggle.setThrowRetryException(true);

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) == 10);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        ExceptionToggle.setThrowRetryException(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(EndpointException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("Retry (delay=100, maxRetries=20)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        // We should have received our message!
        assertEquals(1, outboundQueue.size());

        // Assert no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }


    /**
     * Test the flow behavior in scheduled recovery scenario.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_scheduled_recovery() throws Exception {
        // Prepare test data
        String message = SAMPLE_MESSAGE;

        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();
        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) == 10);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(SampleScheduledRecoveryGeneratedException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        // Assert no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }


    /**
     * Test the flow behavior by simulating a scheduled recovery scenario followed by a successful recovery.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void test_flow_in_scheduled_recovery_then_recovers() throws Exception {
        // Prepare test data
        String message = SAMPLE_MESSAGE;

        ExceptionToggle.setShouldThrowScheduledRecoveryException(true);

        VisitingInvokerFlow flow = (VisitingInvokerFlow)moduleUnderTest.getFlow("BigQueue Sample Flow");
        ScheduledRecoveryManager recoveryManager = (ScheduledRecoveryManager) ReflectionTestUtils.getField(flow, "recoveryManager");
        Scheduler scheduler = (Scheduler) ReflectionTestUtils.getField(recoveryManager, "scheduler");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        logger.info("Sending a BigQueue message.[" + message + "]");
        BigQueueMessageJsonSerialiser<String> serialiser = new BigQueueMessageJsonSerialiser();
        BigQueueMessageBuilder<String> builder = new BigQueueMessageBuilder<>();
        inboundQueue.enqueue(serialiser.serialise(builder
            .withMessageId("messageId")
            .withMessage(SAMPLE_MESSAGE)
            .build()));

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(60))
            .until(() -> ((Integer) ReflectionTestUtils.getField(recoveryManager, "recoveryAttempts")) == 5);

        //verify no messages were published
        assertEquals(0, outboundQueue.size());

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        ExceptionToggle.setShouldThrowScheduledRecoveryException(false);

        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        List<ErrorOccurrence> errors = errorReportingService.find(null, null, null, null, null, 1000);

        logger.info("Number of errors: " + errors.size());

        errors.forEach(errorOccurrence -> {
            assertEquals(SampleScheduledRecoveryGeneratedException.class.getName(), errorOccurrence.getExceptionClass());
            assertEquals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)", errorOccurrence.getAction());
        });

        super.assertExclusionsWithWait(0);

        // We should have received our message!
        assertEquals(1, outboundQueue.size());

        // Assert no more recovery jobs scheduled
        with().pollDelay(Duration.ZERO).pollInterval(Duration.ofMillis(10)).await().atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertEquals(0, this.getNumberOfCurrentScheduledRecoveryJobs(scheduler)));
    }

}
