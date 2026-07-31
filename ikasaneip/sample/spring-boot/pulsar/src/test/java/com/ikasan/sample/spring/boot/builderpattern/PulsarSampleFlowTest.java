package com.ikasan.sample.spring.boot.builderpattern;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.*;

/**
 * This test class supports the Pulsar Sample Flow.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PulsarSampleFlowTest {

    private static String SAMPLE_MESSAGE = "Hello Pulsar!";
    private static String INBOUND_TOPIC = "test-inbound-topic";
    private static String OUTBOUND_TOPIC = "test-outbound-topic";

    private Logger logger = LoggerFactory.getLogger(PulsarSampleFlowTest.class);

    @ClassRule
    public static PulsarContainer pulsarContainer
        = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:3.0.0"))
        .withExposedPorts(6650, 8080);

    private org.apache.pulsar.client.api.Consumer<byte[]> cleanupConsumer;

    @Rule
    public TestName name = new TestName();

    @Autowired
    private Module<Flow> moduleUnderTest;

    @Autowired
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    @Autowired
    private HospitalService hospitalService;

    private ErrorReportingService errorReportingService;

    @Autowired
    private ExclusionManagementService exclusionManagementService;

    private IkasanFlowTestRule flowTestRule;

    @Autowired
    @Qualifier("ikasan.xads")
    private DataSource ikasanxads;

    private PulsarClient pulsarClient;
    private Producer<byte[]> testProducer;
    private Consumer<byte[]> testConsumer;

    @DynamicPropertySource
    static void pulsarProperties(DynamicPropertyRegistry registry) {
        pulsarContainer.start();
        registry.add("pulsar.service.url", pulsarContainer::getPulsarBrokerUrl);
        registry.add("pulsar.inbound.topic", () -> INBOUND_TOPIC);
        registry.add("pulsar.outbound.topic", () -> OUTBOUND_TOPIC);
    }

    @Before
    public void setup() throws Exception {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Pulsar Sample Flow"));

        // Create Pulsar client for testing
        pulsarClient = PulsarClient.builder()
            .serviceUrl(pulsarContainer.getPulsarBrokerUrl())
            .build();

        this.clearMessages(INBOUND_TOPIC, "test-subscription");

        // Create test producer to send messages to inbound topic
        testProducer = pulsarClient.newProducer()
            .topic(INBOUND_TOPIC)
            .create();

        // Create test consumer to receive messages from outbound topic
        testConsumer = pulsarClient.newConsumer()
            .topic(OUTBOUND_TOPIC)
            .subscriptionName("test-subscription")
            .subscriptionType(SubscriptionType.Shared)
            .subscribe();

        // Create topics using Pulsar Admin
        try (PulsarAdmin admin = PulsarAdmin.builder()
            .serviceHttpUrl(pulsarContainer.getHttpServiceUrl())
            .build()) {

            try {
                admin.topics().createNonPartitionedTopic(INBOUND_TOPIC);
            } catch (Exception e) {
                logger.debug("Topic already exists or creation failed: " + e.getMessage());
            }

            try {
                admin.topics().createNonPartitionedTopic(OUTBOUND_TOPIC);
            } catch (Exception e) {
                logger.debug("Topic already exists or creation failed: " + e.getMessage());
            }
        }

        this.errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        this.resetDelayGeneratingBroker();
        this.resetExceptionGeneratingBroker();
    }

    @After
    public void teardown() throws Exception {
        System.out.println("In teardown method for test " + name.getMethodName());
        
        if (testProducer != null) {
            testProducer.close();
        }
        if (testConsumer != null) {
            testConsumer.close();
        }

        if (cleanupConsumer != null) {
            cleanupConsumer.close();
        }

        if (pulsarClient != null) {
            pulsarClient.close();
        }

        clearDatabase();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError"});
    }

    @Test
    public void test_Pulsar_Sample_Flow() throws Exception {
        //Setup component expectations
        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker")
            .broker("Delay Generating Broker")
            .producer("Pulsar Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        // Wait for message to be received on outbound topic
        with().pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNotNull("Should receive message", receivedMessage);
                String receivedContent = new String(receivedMessage.getData(), StandardCharsets.UTF_8);
                assertEquals("Message content should match", SAMPLE_MESSAGE, receivedContent);
                testConsumer.acknowledge(receivedMessage);
            });

        flowTestRule.assertIsSatisfied();
    }

    @Test
    public void test_Pulsar_Flow_Multiple_Messages() throws Exception {
        int messageCount = 10;

        // start the flow
        flowTestRule.startFlow();

        // Send multiple messages
        for (int i = 0; i < messageCount; i++) {
            String message = SAMPLE_MESSAGE + " " + i;
            testProducer.send(message.getBytes(StandardCharsets.UTF_8));
            logger.info("Sent message: " + message);
        }

        // Wait for all messages to be received
        with().pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int receivedCount = 0;
                Message<byte[]> message;
                while ((message = testConsumer.receive(1, TimeUnit.SECONDS)) != null && receivedCount < messageCount) {
                    testConsumer.acknowledge(message);
                    receivedCount++;
                }
                assertEquals("Should receive all messages", messageCount, receivedCount);
            });

    }

    @Test
    public void test_Pulsar_Flow_With_Compressed_Messages() throws Exception {
        // This test verifies that compression works correctly
        String largeMessage = SAMPLE_MESSAGE.repeat(100); // Create a larger message for compression
        
        //Setup component expectations
        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker")
            .broker("Delay Generating Broker")
            .producer("Pulsar Producer");

        // start the flow
        flowTestRule.startFlow();

        // Send message
        testProducer.send(largeMessage.getBytes(StandardCharsets.UTF_8));

        // Wait for message to be received
        with().pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNotNull("Should receive message", receivedMessage);
                String receivedContent = new String(receivedMessage.getData(), StandardCharsets.UTF_8);
                assertEquals("Message content should match", largeMessage, receivedContent);
                testConsumer.acknowledge(receivedMessage);
            });

        flowTestRule.assertIsSatisfied();
    }

    @Test
    public void test_exclusion() throws Exception {
        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
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


        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });
    }


    @Test
    public void test_exclusion_followed_by_resubmission() throws Exception {
        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ExcludeEvent", error.getAction());

        // Verify the exclusion was stored to DB was stored in DB
        assertExclusionsWithWait(1);
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(1, exclusions.size());
        ExclusionEvent exclusionEvent = (ExclusionEvent) exclusions.get(0);
        assertEquals(error.getUri(), exclusionEvent.getErrorUri());

        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });

        // Prevent the exclusion from being thrown when resubmitting and restart the flow.
        exceptionGeneratingBroker.setShouldThrowExclusionException(false);
        this.flowTestRule.stopFlow();
        this.flowTestRule.startFlow();

        hospitalService.resubmit(this.moduleUnderTest.getName(),
            "Pulsar Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());

        // make sure that the resubmitted message has reached its target
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNotNull("Should receive message", receivedMessage);
            });
    }

    @Test
    public void test_exclusion_followed_by_ignore() throws Exception {
        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
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

        // make sure that message was excluded
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });

        // Prevent the exclusion from being thrown when resubmitting and restart the flow.
        exceptionGeneratingBroker.setShouldThrowExclusionException(false);
        this.flowTestRule.stopFlow();
        this.flowTestRule.startFlow();

        hospitalService.ignore(this.moduleUnderTest.getName(),
            "Jms Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());

        // make sure that message was ignored
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });
    }


    @Test
    public void test_flow_in_recovery() throws Exception {

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        //Setup component expectations

        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        // wait for a brief while to let the flow complete

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=10)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null
            , null, null, null, 100);
        assertEquals(0, exclusions.size());
    }


    @Test
    public void test_flow_in_scheduled_recovery() throws Exception {
        System.out.println("test_flow_in_scheduled_recovery");

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowScheduledRecoveryException(true);

        //Setup component expectations
        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        //verify no messages were published
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });

        // Verify the error was stored in DB
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
                assertTrue(errors.size() >= 1);
            });

        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertTrue(errors.size() >= 1);
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(SampleScheduledRecoveryGeneratedException.class.getName(), error.getExceptionClass());
        assertEquals("ScheduledRetry (cronExpression=0/10 * * * * ?, maxRetries=10)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null
            , null, null, null, 100);
        assertEquals(0, exclusions.size());

        exceptionGeneratingBroker.setShouldThrowScheduledRecoveryException(false);

        // Decrease this time
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNotNull("Should receive message", receivedMessage);
            });
    }


    @Test
    public void test_flow_stopped_in_error() throws Exception {
        System.out.println("test_flow_stopped_in_error");

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowStoppedInErrorException(true);

        //Setup component expectations

        flowTestRule
            .withErrorEndState()
            .consumer("Pulsar Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();


        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        with().pollDelay(1, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Message<byte[]> receivedMessage = testConsumer.receive(1, TimeUnit.SECONDS);
                assertNull("Should NOT receive message", receivedMessage);
            });

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(RuntimeException.class.getName(), error.getExceptionClass());
        assertEquals("Stop", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());
    }


    @Test
    public void test_transaction_timeout_stopped_in_error() throws Exception {
        System.out.println("test_transaction_timeout_stopped_in_error");

        // update broker config to force exception throwing
        DelayGenerationBroker delayGenerationBroker = (DelayGenerationBroker) flowTestRule.getComponent("Delay Generating Broker");
        delayGenerationBroker.setBrokerDelay(10000l);

        //Setup component expectations
        flowTestRule.consumer("Pulsar Consumer")
            .broker("Exception Generating Broker")
            .broker("Delay Generating Broker")
            .producer("Pulsar Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a Pulsar message: [" + message + "]");

        // Send message to inbound topic
        testProducer.send(message.getBytes(StandardCharsets.UTF_8));

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(15000L);

        flowTestRule.assertIsSatisfied();


        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=10)", error.getAction());
    }

    private void assertErrorsWithWait(int expectedNumberOfErrors) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(1000, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfErrors, errors.size());
            });
    }


    private void assertExclusionsWithWait(int expectedNumberOfExclusions) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(2000, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfExclusions, exclusions.size());
            });
    }

    /**
     * Clear all messages from a topic/subscription to ensure clean state between tests
     */
    private void clearMessages(String topic, String subscription) throws Exception {
        try {
            this.cleanupConsumer = pulsarClient.newConsumer(Schema.BYTES)
                .topic(topic)
                .subscriptionName(subscription)
                .subscriptionType(org.apache.pulsar.client.api.SubscriptionType.Exclusive)
                .subscribe();

            // Drain all messages with a short timeout
            org.apache.pulsar.client.api.Message<byte[]> msg;
            while ((msg = this.cleanupConsumer.receive(100, TimeUnit.MILLISECONDS)) != null) {
                this.cleanupConsumer.acknowledge(msg);
            }

            this.cleanupConsumer.close();
            this.cleanupConsumer = null;
        } catch (Exception e) {
            // If subscription doesn't exist yet, that's fine - no messages to clear
            if (this.cleanupConsumer != null) {
                try {
                    this.cleanupConsumer.close();
                } catch (Exception ex) {
                    // Ignore
                }
                this.cleanupConsumer = null;
            }
        }
    }

    private void clearDatabase() throws SQLException {
        new DatabaseHelper(ikasanxads).clearDatabase();
    }

    private void resetExceptionGeneratingBroker() {
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.reset();
    }

    public void resetDelayGeneratingBroker(){
        DelayGenerationBroker delayGenerationBroker = (DelayGenerationBroker) flowTestRule
            .getComponent("Delay Generating Broker");
        delayGenerationBroker.reset();
    }
}
