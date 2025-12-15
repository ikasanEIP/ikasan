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
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class JmsSampleFlowDurableSubscriptionTest extends JmsSampleFlowTestBase {

    @Value("${jms.provider.url.persistent}")
    private String brokerUrl;

    @Value("${jms.provider.url}")
    private String brokerUrlNonPersistent;

    @Resource
    @Qualifier("jmsTemplateDurable")
    protected JmsTemplate jmsTemplateDurable;

    private BrokerService broker;

    @Before
    public void setup() throws Exception {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Durable Sample Flow"));
        errorReportingService = errorReportingServiceFactory.getErrorReportingService();

        broker = new BrokerService();
        broker.addConnector(this.brokerUrl+"?allowLinkStealing=true");
        broker.start();
        broker.waitUntilStarted();
        new ActiveMqHelper.ActiveMQBrokerExtension(broker.getBroker()).clearAllMessages();

        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(this.brokerUrl, "dynamicQueues/target" );
        browseMessagesOnQueueVerifier.start();
    }

    @After
    public void teardown() throws Exception {
        browseMessagesOnQueueVerifier.stop();
        removeAllMessages();
        clearDatabase();
        resetExceptionGeneratingBroker();
        resetDelayGeneratingBroker();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError"});
        broker.stop();
        broker.waitUntilStopped();
    }

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    public void test_jms_sample_flow_durable_subscription() throws Exception {
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));

        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplateDurable.setPubSubDomain(true);
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        for (int i = 0; i < 10000; i++) {
            jmsTemplateDurable.convertAndSend(activeMQTopic, message);
        }

        flowTestRule.stopFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("stopped", flowTestRule.getFlowState()));
        flowTestRule.startFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));
        flowTestRule.stopFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("stopped", flowTestRule.getFlowState()));
        flowTestRule.startFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));
        flowTestRule.stopFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("stopped", flowTestRule.getFlowState()));
        flowTestRule.startFlow();
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(30, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));


        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(10000, browseMessagesOnQueueVerifier.getCaptureResults().size()));
        assertEquals(((TextMessage) browseMessagesOnQueueVerifier.getCaptureResults().get(0)).getText(), SAMPLE_MESSAGE);
    }

    @Test
    public void test_exclusion() {
        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations
        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        jmsTemplateDurable.convertAndSend(activeMQTopic, message);

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
    public void test_exclusion_followed_by_resubmission() {
        // update broker config to force exception throwing
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowExclusionException(true);

        //Setup component expectations
        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        jmsTemplateDurable.convertAndSend(activeMQTopic, message);

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

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", this.flowTestRule.getFlowState()));

        hospitalService.resubmit(this.moduleUnderTest.getName(),
            "Jms Durable Sample Flow", exclusionEvent.getErrorUri(), "username");

        exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
        assertEquals(0, exclusions.size());
    }

    @Test
    public void test_flow_in_recovery() throws JMSException {
        // setup custom broker to throw an exception
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule.getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.setShouldThrowRecoveryException(true);

        //Setup component expectations
        flowTestRule.consumer("JMS Consumer")
            .broker("Exception Generating Broker");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> Assert.assertEquals("running", flowTestRule.getFlowState()));

        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplateDurable.setPubSubDomain(true);
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        jmsTemplateDurable.convertAndSend(activeMQTopic, message);


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
    public void test_flow_stopped_in_error() {
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
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        jmsTemplateDurable.convertAndSend(activeMQTopic, message);

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

    /**
     * Removes a durable subscription from the specified ActiveMQ broker URL.
     * If an error occurs during the removal process, an error message is printed to the console.
     */
    private void removeSubscription()  {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);
            Session session = connectionFactory.createConnection().createSession();

            session.unsubscribe("test-sub");
            session.close();
        }
        catch (Exception e) {
            System.out.println("Error removing durable subscription!");
            e.printStackTrace();
        }
    }

}
