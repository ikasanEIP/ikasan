/*
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.h2.tools.Server;
import org.ikasan.nonfunctional.test.util.AMQTestUtil;
import org.ikasan.nonfunctional.test.util.FileTestUtil;
import org.ikasan.nonfunctional.test.util.WiretapTestUtil;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Transaction failure test.
 *
 * Test summary
 * Process message 1 successfully.
 * Process message 2, but throw an exception on the precommit of the H2 DB resource.
 * Process message 3 successfully.
 *
 * @author Ikasan Development Team
 */
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private WiretapService<WiretapEvent,PagedSearchResult, Long> wiretapService;

    @Resource
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    // Arjuna transaction logs
    String objectStoreDir = "./transaction-logs";

    // AMQ persistence
    String amqPersistenceBaseDir = "./activemq-data";
    String amqPersistenceDir = amqPersistenceBaseDir + "/localhost/KahaDB";

    // test message count
    int testMessageCount = 3;

    public IkasanFlowTestRule flow3TestRule = new IkasanFlowTestRule();

    // test utils
    WiretapTestUtil wiretapTestUtil;
    AMQTestUtil amqTestUtil;

    // h2 server instance
    static Server server;

    // AMQ Broker
    BrokerService broker;

    @BeforeAll
    static void setup() throws SQLException
    {
        // TODO can we use a random port and tie back to the application.properties url?
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers","-ifNotExists").start();
    }

    @BeforeEach
    void start() throws Exception
    {
        // clean up any previous failures that left persisted state
        FileTestUtil.deleteFile(new File(amqPersistenceBaseDir));
        FileTestUtil.deleteFile(new File(objectStoreDir));

        // start a AMQ broker
        broker = BrokerFactory.createBroker(new URI("broker:(" + brokerUrl + ")"));
        broker.getPersistenceAdapter().setDirectory( new File(amqPersistenceDir) );
        broker.setKeepDurableSubsActive(false);

        broker.start();

        wiretapTestUtil = new WiretapTestUtil(wiretapService, jobAwareFlowEventListener);
        amqTestUtil = new AMQTestUtil(brokerUrl);
    }

    @AfterEach
    void stop() throws Exception
    {
        flow3TestRule.stopFlow();

        Thread.sleep(1000);
        broker.stop();
    }

    @AfterAll
    static void teardown()
    {
        server.shutdown();
    }

    @Test
    @DirtiesContext
    void test_happy_flows_with_concurrent_db_updates() throws Exception
    {
        // jms consumer to the topic to dev null
        flow3TestRule.withFlow(moduleUnderTest.getFlow("jmsToDevNullFlow1"));

        // Setup component expectations
        for (int i = 0; i < testMessageCount; i++)
        {
            flow3TestRule.consumer("JMS Consumer")
                    .producer("Dev Null Producer");
        }

        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "configurationUpdaterFlow", TriggerRelationship.AFTER, "Configuration Updater");
        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "jmsToDevNullFlow1", TriggerRelationship.AFTER, "JMS Consumer");

        // start flows right to left
        flow3TestRule.startFlow();
        Thread.sleep(500);

        // publish 10 events
        List<String> messages = new ArrayList<>();
        for(int i = 1; i <=testMessageCount; i++)
        {
            messages.add("Test Message " + i);
        }

        amqTestUtil.publish(messages, "jms.topic.test");

        // wait for JMS flows to catch up for a max of 10 seconds
        int waitCounter = 0;
        while( waitCounter < 10 &&
                wiretapTestUtil.getWiretaps(
                "Transaction Test Module",
                "jmsToDevNullFlow1",
                TriggerRelationship.AFTER,
                "JMS Consumer",
                        testMessageCount).getResultSize() != testMessageCount)
        {
            waitCounter=waitCounter+2;
            logger.info("Waiting for jmsToDevNullFlow1 flow to complete (circa 10 seconds). Waiting for " + waitCounter + " seconds");
            Thread.sleep(2000);
        }

        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
              .atMost(60, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedSearchResult<WiretapEvent> wiretaps = wiretapTestUtil
                .getWiretaps("Transaction Test Module",
                    "jmsToDevNullFlow1",
                    TriggerRelationship.AFTER,
                    "JMS Consumer", testMessageCount
                            );
            logger.info("Expected jmsToDevNullFlow1 flow wiretap count {} but found {}",
                testMessageCount-1, wiretaps.getResultSize()
                       );
            assertEquals(wiretaps.getResultSize(), testMessageCount - 1, "Expected jmsToDevNullFlow1 flow wiretap count " + testMessageCount
                    + " but found " + wiretaps.getResultSize());
        });


        Destination dlq = broker.getBroker().getDestinationMap().get(new ActiveMQQueue("ActiveMQ.DLQ"));
        if(dlq != null)
        {
            assertEquals(1, dlq.getDestinationStatistics().getMessages().getCount(), "DLQ should contain 1 message");
            org.apache.activemq.command.Message[] dlqMessages = dlq.browse();
            org.apache.activemq.command.Message dlqMessage = dlqMessages[0];
            String content = new String(dlqMessage.getContent().getData());
            assertTrue(content.substring(4).endsWith("Test Message 2"), "DLQ should contain Test Message 2, but found " + content.substring(4));
        }
        else
        {
            fail("dlq should exist");
        }

        flow3TestRule.assertIsSatisfied();
    }

}

