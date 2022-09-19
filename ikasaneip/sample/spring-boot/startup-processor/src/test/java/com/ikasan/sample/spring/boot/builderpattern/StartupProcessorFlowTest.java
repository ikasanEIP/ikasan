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

import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StartupProcessorFlowTest {

    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(StartupProcessorFlowTest.class);

    @Rule
    public TestName name = new TestName();

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private StartupControlDao startupControlDao;

    @Value("${jms.provider.url}")
    private String brokerUrl;

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
        flowTestRule.withFlow(moduleUnderTest.getFlow("Flow One"));
        browseMessagesOnQueueVerifier = new BrowseMessagesOnQueueVerifier(brokerUrl, "destinationTwo" );
        browseMessagesOnQueueVerifier.start();
    }

    @After
    public void teardown() throws Exception {
        System.out.println("In teardown method for test " + name.getMethodName());
        browseMessagesOnQueueVerifier.stop();
        removeAllMessages();
        clearDatabase();
        flowTestRule.stopFlowWithAwait(name.getMethodName(), new String[]{"stopped","stoppedInError"});

    }

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    public void testFlowOneAndStartupConfigurationSet() throws Exception {
        System.out.println("test_Jms_Sample_Flow");
        // Prepare test data
        String message = SAMPLE_MESSAGE;
        logger.info("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("destinationOne", message);

        //Setup component expectations

        flowTestRule.consumer("JMS Consumer One")
            .producer("JMS Producer One");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // wait for a brief while to let the flow complete

        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals(1, browseMessagesOnQueueVerifier.getCaptureResults().size()));
        assertEquals(((TextMessage) browseMessagesOnQueueVerifier.getCaptureResults().get(0)).getText(), SAMPLE_MESSAGE);

        flowTestRule.assertIsSatisfied();

        // test the module configuration startup has been applied
        List<StartupControl> startupControls =
            startupControlDao.getStartupControls("sample-boot-startup-processor");
        StartupControl flowOneStartupControl =
            startupControls.stream().filter(s -> s.getFlowName().equals("Flow One")).findFirst().get();
        StartupControl flowTwoStartupControl =
            startupControls.stream().filter(s -> s.getFlowName().equals("Flow Two")).findFirst().get();

        assertEquals("MANUAL", flowOneStartupControl.getStartupType().toString());
        assertEquals("MANUAL", flowTwoStartupControl.getStartupType().toString());
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




}
