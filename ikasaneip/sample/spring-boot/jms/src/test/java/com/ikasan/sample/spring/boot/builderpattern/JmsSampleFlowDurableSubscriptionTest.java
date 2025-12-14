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
import org.apache.activemq.command.ActiveMQTopic;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Before
    public void setup() throws JMSException {
        flowTestRule = new IkasanFlowTestRule();
        flowTestRule.withFlow(moduleUnderTest.getFlow("Jms Durable Sample Flow"));
        errorReportingService = errorReportingServiceFactory.getErrorReportingService();
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrlNonPersistent, "target" );
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
        this.removeSubscription();
        this.restartBroker();
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
        jmsTemplate.setPubSubDomain(true);
        ActiveMQTopic activeMQTopic = new ActiveMQTopic("source");
        for (int i = 0; i < 10000; i++) {
            jmsTemplate.convertAndSend(activeMQTopic, message);
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
