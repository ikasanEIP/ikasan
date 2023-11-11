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

import jakarta.annotation.Resource;
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
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.env.MockEnvironment;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import javax.sql.DataSource;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 *
 * @author Ikasan Development Team
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JmsSampleFlowTest {
    protected final static String MODULE_REST_USERNAME_PROPERTY = "rest.module.username";
    protected final static String MODULE_REST_PASSWORD_PROPERTY = "rest.module.password";

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(JmsSampleFlowTest.class);

    
    public String name;

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    @Resource
    private ErrorReportingServiceFactory errorReportingServiceFactory;

    @Resource
    private HospitalService hospitalService;

    private ErrorReportingService errorReportingService;

    @Resource
    private ExclusionManagementService exclusionManagementService;

    private IkasanFlowTestRule flowTestRule;

    @Resource
    @Autowired
    @Qualifier("ikasan.xads")
    private DataSource ikasanxads;

    @LocalServerPort
    private int randomServerPort;

    private BrowseMessagesOnQueueVerifier browseMessagesOnQueueVerifier;


    @BeforeEach
    void setup(TestInfo testInfo) throws JMSException {
        Optional<Method> testMethod = testInfo.getTestMethod();
        if(testMethod.isPresent()) {
            this.name = testMethod.get().getName();
        }
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Sample Flow"));
        errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, "target" );
        browseMessagesOnQueueVerifier.start();
    }

    @AfterEach
    void teardown() throws Exception {
        System.out.println("In teardown method for test " + name);
        browseMessagesOnQueueVerifier.stop();
        removeAllMessages();
        clearDatabase();
        resetExceptionGeneratingBroker();
        resetDelayGeneratingBroker();
        flowTestRule.stopFlowWithAwait( name, new String[]{"stopped","stoppedInError"});

    }


    @AfterAll
    static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }


    @Test
    void test_Jms_Sample_Flow() throws Exception {
        System.out.println("test_Jms_Sample_Flow");
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker")
            .broker("Delay Generating Broker")
            .producer("JMS Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, browseMessagesOnQueueVerifier.getCaptureResults().size()));
        assertEquals(((TextMessage) browseMessagesOnQueueVerifier.getCaptureResults().get(0)).getText(), SAMPLE_MESSAGE);

        flowTestRule.assertIsSatisfied();

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

    @Test
    void test_exclusion() {
        System.out.println("test_exclusion");

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

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
    }


    @Test
    void test_exclusion_followed_by_resubmission() {
        System.out.println("test_exclusion_followed_by_resubmission");

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

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

        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty(MODULE_REST_USERNAME_PROPERTY, "admin");
        mockEnvironment.setProperty(MODULE_REST_PASSWORD_PROPERTY, "admin");

        // Prevent the exclusion from being thrown when resubmitting and restart the flow.
        exceptionGeneratingBroker.setShouldThrowExclusionException(false);
        this.flowTestRule.stopFlow();
        this.flowTestRule.startFlow();

        hospitalService.resubmit(this.moduleUnderTest.getName(),
            "Jms Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());
    }

    private void assertErrorsWithWait(int expectedNumberOfErrors) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfErrors, errors.size());
            });
    }


    private void assertExclusionsWithWait(int expectedNumberOfExclusions) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfExclusions, exclusions.size());
            });
    }


    @Test
    void test_exclusion_followed_by_ignore() {
        System.out.println("test_exclusion_followed_by_ignore");
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> flowTestRule.assertIsSatisfied());

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

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

        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty(MODULE_REST_USERNAME_PROPERTY, "admin");
        mockEnvironment.setProperty(MODULE_REST_PASSWORD_PROPERTY, "admin");

        // Prevent the exclusion from being thrown when resubmitting and restart the flow.
        exceptionGeneratingBroker.setShouldThrowExclusionException(false);
        this.flowTestRule.stopFlow();
        this.flowTestRule.startFlow();

        hospitalService.ignore(this.moduleUnderTest.getName(),
            "Jms Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());
    }


    @Test
    void test_flow_in_recovery() {
        System.out.println("test_flow_in_recovery");

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        assertErrorsWithWait(1);
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=10)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());


    }


    @Test
    void test_flow_in_scheduled_recovery() {
        System.out.println("test_flow_in_scheduled_recovery");

        // Prepare test data
        String message = SAMPLE_MESSAGE;


        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowScheduledRecoveryException(true);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("recovering", flowTestRule.getFlowState()));

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

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
        assertEquals("ScheduledRetry (cronExpression=0/1 * * * * ?, maxRetries=10)", error.getAction());

        // Verify the exclusion was not stored to DB
        List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());

        exceptionGeneratingBroker.setShouldThrowScheduledRecoveryException(false);

        // Decrease this time
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("running", flowTestRule.getFlowState()));

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, browseMessagesOnQueueVerifier.getCaptureResults().size()));


    }


    @Test
    void test_flow_stopped_in_error() {
        System.out.println("test_flow_stopped_in_error");

        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowStoppedInErrorException(true);

        //Setup component expectations

        flowTestRule
            .withErrorEndState()
            .consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();


        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("stoppedInError", flowTestRule.getFlowState()));

        flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

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
    void test_transaction_timeout_stopped_in_error() {
        System.out.println("test_transaction_timeout_stopped_in_error");
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("source", message);

        // update broker config to force exception throwing
        DelayGenerationBroker delayGenerationBroker = (DelayGenerationBroker) flowTestRule.getComponent("Delay Generating Broker");
        delayGenerationBroker.setBrokerDelay(10000l);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker")
            .broker("Delay Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(15000L);

        // flowTestRule.assertIsSatisfied();

        //verify no messages were published
        assertEquals(0, browseMessagesOnQueueVerifier.getCaptureResults().size());

        // Verify the error was stored in DB
        List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
        assertEquals(1, errors.size());
        ErrorOccurrence error = (ErrorOccurrence) errors.get(0);
        assertEquals(EndpointException.class.getName(), error.getExceptionClass());
        assertEquals("Retry (delay=10000, maxRetries=10)", error.getAction());


    }

}
