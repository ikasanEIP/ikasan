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

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.h2.tools.Server;
import org.ikasan.nonfunctional.test.util.FileTestUtil;
import org.ikasan.nonfunctional.test.util.WiretapTestUtil;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertTrue;

/**
 * Ikasan component failure.
 *
 * Publish 20 messages and have a broker compoent fail on the 10th and the 20th.
 * Flow should rollback and replay on failure.
 * Result should be 20 messages wiretapped successfully.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration( initializers = { ApplicationTest.PropertiesInitializer.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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

    @Resource
    @Autowired
    @Qualifier("ikasan.xads")
    private DataSource ikasanxads;

    public IkasanFlowTestRule flow1TestRule = new IkasanFlowTestRule();
    public IkasanFlowTestRule flow2TestRule = new IkasanFlowTestRule();
    public IkasanFlowTestRule flow3TestRule = new IkasanFlowTestRule();
    public IkasanFlowTestRule flow4TestRule = new IkasanFlowTestRule();

    // h2 server instance
    static Server server;

    // test utils
    WiretapTestUtil wiretapTestUtil;

    // AMQ Broker
    BrokerService broker;

    // Arjuna transaction logs
    String objectStoreDir = "./transaction-logs";

    // AMQ persistence
    String amqPersistenceBaseDir = "./activemq-data";
    String amqPersistenceDir = amqPersistenceBaseDir + "/localhost/KahaDB";
    static int h2Port;
    @BeforeClass
    public static void setup() throws SQLException
    {
        // TODO can we use a random port and tie back to the application.properties url?
        h2Port = SocketUtils.findAvailableTcpPort();
        server =  Server.createTcpServer("-tcpPort", String.valueOf(h2Port), "-tcpAllowOthers","-ifNotExists").start();
    }

    @Before
    public void start() throws Exception
    {

        // clean up any previous failures that left persisted state
        FileTestUtil.deleteFile(new File(amqPersistenceBaseDir));
        FileTestUtil.deleteFile(new File(objectStoreDir));

        // start a AMQ broker
        broker = BrokerFactory.createBroker(new URI("broker:(" + brokerUrl + ")"));
        broker.getPersistenceAdapter().setDirectory( new File(amqPersistenceDir) );
        broker.start();

        wiretapTestUtil = new WiretapTestUtil(wiretapService, jobAwareFlowEventListener);
    }

    @After
    public void stop() throws Exception
    {
        flow1TestRule.stopFlow();
        flow2TestRule.stopFlow();
        flow3TestRule.stopFlow();
        flow4TestRule.stopFlow();

        removeAllMessages();

        Thread.sleep(1000);
        broker.stop();
        clearDatabase();
    }

    @AfterClass
    public static void teardown()
    {
        server.shutdown();
    }

    @Test
    @DirtiesContext
    public void test_happy_flows_with_concurrent_db_updates() throws Exception
    {
        // event generator publishing to JMS topic
        flow1TestRule.withFlow(moduleUnderTest.getFlow("eventGeneratorToJMSFlow"));

        // jms consumer flow continually updating configuration in DB for dynamic configuration
        flow2TestRule.withFlow(moduleUnderTest.getFlow("configurationUpdaterFlow"));

        // two additional jms consumers to the topic to dev null
        flow3TestRule.withFlow(moduleUnderTest.getFlow("jmsToDevNullFlow1"));
        flow4TestRule.withFlow(moduleUnderTest.getFlow("jmsToDevNullFlow2"));

        // Setup component expectations
        for (int i = 0; i < ModuleConfig.EVENT_GENERATOR_COUNT; i++)
        {
            flow1TestRule.consumer("Event Generating Consumer")
                    .producer("JMS Producer");
            flow3TestRule.consumer("JMS Consumer")
                    .producer("Dev Null Producer");
            flow4TestRule.consumer("JMS Consumer")
                    .producer("Dev Null Producer");
        }

        // expectations on flow that is throwing exceptions
        for(int x = 0; x < ModuleConfig.REPEAT; x++)
        {
            // first 9 invocations of cycle is all components
            for (int i = 0; i < ModuleConfig.EVENTS_PER_CYCLE-1; i++)
            {
                flow2TestRule.consumer("Event Generating Consumer")
                        .broker("Configuration Updater")
                        .producer("Dev Null Producer");
            }

            // 10th invocation has exception on broker
            flow2TestRule.consumer("Event Generating Consumer")
                    .broker("Configuration Updater");

            // then we have retry of the exception rollback
            flow2TestRule.consumer("Event Generating Consumer")
                    .broker("Configuration Updater")
                    .producer("Dev Null Producer");
        }

        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "eventGeneratorToJMSFlow", TriggerRelationship.AFTER, "Event Generating Consumer");
        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "configurationUpdaterFlow", TriggerRelationship.AFTER, "Configuration Updater");
        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "jmsToDevNullFlow1", TriggerRelationship.AFTER, "JMS Consumer");
        wiretapTestUtil.addWiretapTrigger("Transaction Test Module", "jmsToDevNullFlow2", TriggerRelationship.AFTER, "JMS Consumer");

        // start flows right to left
        flow4TestRule.startFlow();
        flow3TestRule.startFlow();
        flow2TestRule.startFlow();
        flow1TestRule.startFlow();

        // wait for event generating flows to complete
        logger.info("Waiting for 'configurationUpdaterFlow' flow to complete (circa 70 seconds).");

        logger.info("Waiting for 'eventGeneratorToJMSFlow' flow to complete (circa 24 seconds).");

        with().pollInterval(2, TimeUnit.SECONDS).and().with().pollDelay(5, TimeUnit.SECONDS).await()
              .atMost(120, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedSearchResult<WiretapEvent> wiretaps = wiretapTestUtil.getWiretaps("Transaction Test Module", "eventGeneratorToJMSFlow", TriggerRelationship.AFTER, "Event Generating Consumer", ModuleConfig.EVENT_GENERATOR_COUNT);

            logger.info("Expected eventGeneratorToJMSFlow flow wiretap count {} but found {}",
                ModuleConfig.EVENT_GENERATOR_COUNT, wiretaps.getResultSize()
                       );
            assertTrue("Expected " + "eventGeneratorToJMSFlow" + " flow wiretap count "
                    + ModuleConfig.EVENT_GENERATOR_COUNT + " but found " + wiretaps.getResultSize(),
                wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT
                      );
            assertTrue("Expected " + "eventGeneratorToJMSFlow" + " flow wiretap count "
                + ModuleConfig.EVENT_GENERATOR_COUNT + " but found " + wiretaps.getResultSize(),
                wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT );

        });

        logger.info("Waiting for 'configurationUpdaterFlow' flow to complete (circa 30 seconds).");

        with().pollInterval(2, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
              .atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedSearchResult<WiretapEvent> wiretaps = wiretapTestUtil.getWiretaps("Transaction Test Module",
                "configurationUpdaterFlow", TriggerRelationship.AFTER,
                "Configuration Updater", ModuleConfig.EVENT_GENERATOR_COUNT);

            logger.info("Expected configurationUpdaterFlow flow wiretap count {} but found {}",
                ModuleConfig.EVENT_GENERATOR_COUNT, wiretaps.getResultSize()
                       );
            assertTrue("Expected configurationUpdaterFlow flow wiretap count " + ModuleConfig.EVENT_GENERATOR_COUNT
                    + " but found " + wiretaps.getResultSize(),
                wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT
                      );

        });


        // wait for JMS flows to catch up for a max of 10 seconds
        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
              .atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedSearchResult<WiretapEvent> wiretaps = wiretapTestUtil
                .getWiretaps("Transaction Test Module", "jmsToDevNullFlow1", TriggerRelationship.AFTER,
                    "JMS Consumer", ModuleConfig.EVENT_GENERATOR_COUNT
                            );
            logger.info("Expected jmsToDevNullFlow1 flow wiretap count {} but found {}",
                ModuleConfig.EVENT_GENERATOR_COUNT, wiretaps.getResultSize()
                       );
            assertTrue("Expected jmsToDevNullFlow1 flow wiretap count " + ModuleConfig.EVENT_GENERATOR_COUNT
                    + " but found " + wiretaps.getResultSize(),
                wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT
                      );

        });

        // wait for JMS flows to catch up for a max of 10 seconds
        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
              .atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedSearchResult<WiretapEvent> wiretaps = wiretapTestUtil
                .getWiretaps("Transaction Test Module", "jmsToDevNullFlow2",
                    TriggerRelationship.AFTER,
                    "JMS Consumer", ModuleConfig.EVENT_GENERATOR_COUNT
                            );
            logger.info("Expected jmsToDevNullFlow2 flow wiretap count {} but found {}",
                ModuleConfig.EVENT_GENERATOR_COUNT, wiretaps.getResultSize()
                       );
            assertTrue("Expected jmsToDevNullFlow2 flow wiretap count " + ModuleConfig.EVENT_GENERATOR_COUNT
                    + " but found " + wiretaps.getResultSize(),
                wiretaps.getResultSize() == ModuleConfig.EVENT_GENERATOR_COUNT
                      );

        });

        flow1TestRule.assertIsSatisfied();
        flow2TestRule.assertIsSatisfied();
        flow3TestRule.assertIsSatisfied();
        flow4TestRule.assertIsSatisfied();
    }

    private void removeAllMessages() throws Exception {
        new ActiveMqHelper().removeAllMessages();
    }

    private void clearDatabase() throws SQLException {
        new DatabaseHelper(ikasanxads).clearDatabase();
    }

    public static class PropertiesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
    {
        public PropertiesInitializer(){}
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext)
        {
            TestPropertyValues.of(new String[]{"h2.db.port="+h2Port}).applyTo(applicationContext.getEnvironment());
        }
    }
}

