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
import jakarta.jms.TextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class JmsSampleFlowDurableSubscriptionTest {
    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(JmsSampleFlowDurableSubscriptionTest.class);

    @Rule
    public TestName name = new TestName();

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url.persistent}")
    private String brokerUrl;

    @Value("${jms.provider.url}")
    private String brokerUrl2;

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



    @Before
    public void setup() throws JMSException {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Sample Flow"));
        errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl2, "target" );
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
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
//        connectionFactory.createConnection().createSession().unsubscribe("test-sub");
    }



    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    public void test_jms_sample_flow_durable_subscription() throws Exception {
        // Prepare test dat

//        try {
            JmsContainerConsumer consumer = (JmsContainerConsumer) this.flowTestRule.getComponent("JMS Consumer");

            SpringMessageConsumerConfiguration configuration = this.flowTestRule
                .getComponentConfig("JMS Consumer", SpringMessageConsumerConfiguration.class);

            configuration.setDurable(true);
            configuration.setDurableSubscriptionName("test-sub");
            configuration.setMaxConcurrentConsumers(1);
            configuration.setConcurrentConsumers(1);
            configuration.setPubSubDomain(true);
            configuration.setSessionTransacted(true);
            configuration.setDestinationJndiName("dynamicTopics/source");
            configuration.setCacheLevel(1);
            configuration.setDestinationJndiProperties(Map.of(
                "java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                "naming.provider.url", brokerUrl));
            configuration.setConnectionFactoryJndiProperties(Map.of(
                "java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                "naming.provider.url", brokerUrl));
            configuration.setConnectionFactoryName("XAConnectionFactory");

            //Setup component expectations
            // start the flow and assert it runs
            flowTestRule.startFlow();
            flowTestRule.stopFlow();

            String message = SAMPLE_MESSAGE;
            logger.info("Sending a JMS message.[" + message + "]");
            jmsTemplate.setPubSubDomain(true);
            ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
            for (int i = 0; i < 10000; i++) {
                jmsTemplate.convertAndSend(activeMQTopic, message);
            }

//        ActiveMqHelper activeMqHelper = new ActiveMqHelper();
//        activeMqHelper.shutdownBroker();
//        activeMqHelper.startBroker(brokerUrl);
//
//        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl2, "target" );
//        browseMessagesOnQueueVerifier.start();

            flowTestRule.startFlow();

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

            // wait for a brief while to let the flow complete
//        Thread.sleep(10000);

            with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(10000, browseMessagesOnQueueVerifier.getCaptureResults().size()));
            assertEquals(((TextMessage) browseMessagesOnQueueVerifier.getCaptureResults().get(0)).getText(), SAMPLE_MESSAGE);

//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

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

}
