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
import org.apache.commons.io.FileUtils;
import org.h2.tools.Server;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.trigger.model.Trigger;
import org.ikasan.trigger.model.TriggerRelationship;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Stress testing of Ikasan persistence.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private WiretapService<WiretapEvent,PagedSearchResult> wiretapService;

    @Resource
    private JobAwareFlowEventListener jobAwareFlowEventListener;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    public IkasanFlowTestRule flow1TestRule = new IkasanFlowTestRule();
    public IkasanFlowTestRule flow2TestRule = new IkasanFlowTestRule();
    public IkasanFlowTestRule flow3TestRule = new IkasanFlowTestRule();

    // h2 server instance
    static Server server;

    SimpleTimer stopWatch;

    // AMQ Broker
    BrokerService broker;

    // AMQ persistence
    String amqPersistenceBaseDir = "./activemq-data";
    String amqPersistenceDir = amqPersistenceBaseDir + "/localhost/KahaDB";

    @BeforeClass
    public static void setup() throws SQLException
    {
        // TODO can we use a random port and tie back to the application.properties url?
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

    @Before
    public void start() throws Exception
    {
        stopWatch = SimpleTimer.getInstance();

        // start a AMQ broker
        broker = BrokerFactory.createBroker(new URI("broker:(" + brokerUrl + ")"));
        broker.getPersistenceAdapter().setDirectory( new File(amqPersistenceDir) );
        broker.start();
    }

    @After
    public void stop() throws Exception
    {
        flow1TestRule.stopFlow();
        flow2TestRule.stopFlow();
        flow3TestRule.stopFlow();

        Thread.sleep(1000);
        broker.stop();
        Thread.sleep(1000);
        File amqPersistenceBase = new File(amqPersistenceBaseDir);
        FileUtils.deleteDirectory(amqPersistenceBase);
        Assert.assertTrue("Failed to delete AMQ Persistence " + amqPersistenceBase.getAbsolutePath(), !amqPersistenceBase.exists() );
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

        // Setup component expectations
        for (int i = 0; i < ModuleConfig.EVENT_GENERATOR_COUNT; i++)
        {
            flow1TestRule.consumer("Event Generating Consumer")
                    .producer("JMS Producer");
            flow2TestRule.consumer("Event Generating Consumer")
                    .broker("Configuration Updater")
                    .producer("Dev Null Producer");
            flow3TestRule.consumer("JMS Consumer")
                    .producer("Dev Null Producer");
        }

        // one iteration of rollback retry
        flow3TestRule.consumer("JMS Consumer").producer("Dev Null Producer");
        flow3TestRule.consumer("JMS Consumer").producer("Dev Null Producer");

        addWiretapTrigger("Transaction Test Module", "eventGeneratorToJMSFlow", TriggerRelationship.AFTER, "Event Generating Consumer");
        addWiretapTrigger("Transaction Test Module", "configurationUpdaterFlow", TriggerRelationship.AFTER, "Configuration Updater");
        addWiretapTrigger("Transaction Test Module", "jmsToDevNullFlow1", TriggerRelationship.AFTER, "JMS Consumer");

        // start flows right to left
        flow3TestRule.startFlow();
//        flow3TestRule.pauseFlow();
        flow2TestRule.startFlow();
        flow1TestRule.startFlow();

        // wait for event generating flows to complete
        stopWatch.start();
        logger.info("Waiting for 'configurationUpdaterFlow' flow to complete (circa 70 seconds).");
        while (flow2TestRule.getFlowState().equals(Flow.RUNNING))
        {
            // log if it takes longer than 70 seconds
            if(stopWatch.elapsedInSeconds() > 70)
            {
                logger.info("Still waiting for 'configurationUpdaterFlow' flow to complete. Waiting for " + stopWatch.elapsedInSeconds() + " seconds");
            }
            Thread.sleep(2000);
        }

        // wait for event generating flows to complete

        stopWatch.reset();
        stopWatch.start();
        logger.info("Waiting for 'eventGeneratorToJMSFlow' flow to complete (circa 24 seconds).");
        while (flow1TestRule.getFlowState().equals(Flow.RUNNING))
        {
            // log if it takes longer than 24 seconds
            if(stopWatch.elapsedInSeconds() > 24)
            {
                logger.info("Still waiting for 'eventGeneratorToJMSFlow' flow to complete (circa 24 seconds). Waiting for " + stopWatch.elapsedInSeconds() + " seconds");
            }
            Thread.sleep(2000);
        }

        //
        // check the results of the test
        assertWiretaps("Transaction Test Module", "eventGeneratorToJMSFlow", TriggerRelationship.AFTER, "Event Generating Consumer", ModuleConfig.EVENT_GENERATOR_COUNT);
        assertWiretaps("Transaction Test Module", "configurationUpdaterFlow", TriggerRelationship.AFTER, "Configuration Updater", ModuleConfig.EVENT_GENERATOR_COUNT);

        // resume JMS flow
//        flow3TestRule.resumeFlow();

        // wait for JMS flows to catch up for a max of 10 seconds
        int waitCounter = 0;
        while( waitCounter < 10 &&
                getWiretaps(
                "Transaction Test Module",
                "jmsToDevNullFlow1",
                TriggerRelationship.AFTER,
                "JMS Consumer",
                ModuleConfig.EVENT_GENERATOR_COUNT).getResultSize() != ModuleConfig.EVENT_GENERATOR_COUNT)
        {
            waitCounter=waitCounter+2;
            logger.info("Waiting for jmsToDevNullFlow1 flow to complete (circa 10 seconds). Waiting for " + waitCounter + " seconds");
            Thread.sleep(2000);
        }
        assertWiretaps("Transaction Test Module", "jmsToDevNullFlow1", TriggerRelationship.AFTER, "JMS Consumer", ModuleConfig.EVENT_GENERATOR_COUNT);

        flow1TestRule.assertIsSatisfied();
        flow2TestRule.assertIsSatisfied();
        flow3TestRule.assertIsSatisfied();
    }

    /**
     * Assert wiretap expectations.
     *
     * @param moduleName
     * @param flowName
     * @param relationship
     * @param componentName
     * @param expectedWiretapCount
     */
    protected void assertWiretaps(String moduleName, String flowName, TriggerRelationship relationship, String componentName, int expectedWiretapCount)
    {
        Set<String> moduleNames = new HashSet<String>();
        moduleNames.add(moduleName);
        String location = relationship.name().toLowerCase() + " " + componentName;

        PagedSearchResult<WiretapEvent> wiretaps = wiretapService.findWiretapEvents(0, expectedWiretapCount, null,
                true, moduleNames, flowName, location, null, null,
                null,null, null );

        assertTrue("Expected " + flowName + " flow wiretap count "
                + expectedWiretapCount + " but found " + wiretaps.getResultSize(), wiretaps.getResultSize() == expectedWiretapCount);
    }

    protected PagedSearchResult<WiretapEvent> getWiretaps(String moduleName, String flowName, TriggerRelationship relationship, String componentName, int pageSize)
    {
        Set<String> moduleNames = new HashSet<String>();
        moduleNames.add(moduleName);
        String location = relationship.name().toLowerCase() + " " + componentName;

        return wiretapService.findWiretapEvents(0, pageSize, null,
                true, moduleNames, flowName, location, null, null,
                null,null, null );
    }

    /**
     * Add wiretap listeners with default time to live of 10,0000 minutes.
     * @param moduleName
     * @param flowName
     * @param relationship
     * @param componentName
     */
    protected void addWiretapTrigger(String moduleName, String flowName, TriggerRelationship relationship, String componentName)
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("timeToLive", "100000");
        addWiretapTrigger(moduleName, flowName, relationship, componentName, params);
    }

    /**
     * Add wiretap listeners
     * @param moduleName
     * @param flowName
     * @param relationship
     * @param componentName
     * @param params
     */
    protected void addWiretapTrigger(String moduleName, String flowName, TriggerRelationship relationship, String componentName, Map<String,String> params)
    {
        jobAwareFlowEventListener.addDynamicTrigger( new Trigger(moduleName, flowName, relationship.name(), "wiretapJob", componentName, params) );
    }
}

