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

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.TestSocketUtils;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertNotNull;

/**
 * This test Sftp To Log Flow.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JmsToSftpFlowTest
{
    private static String SAMPLE_MESSAGE = "Hello world!";

    private Logger logger = LoggerFactory.getLogger(JmsToSftpFlowTest.class);

    @Resource
    public Module<Flow> moduleUnderTest;

    @Resource
    public JmsTemplate jmsTemplate;

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule( );

    public SftpRule sftp;

    @Before
    public void setup(){
        sftp = new SftpRule("test", "test", null, TestSocketUtils.findAvailableTcpPort());
        sftp.start();

        flowTestRule.withFlow(moduleUnderTest.getFlow("jmsToFileSystemFlow"));
    }

    @After
    public void teardown()
    {
        flowTestRule.stopFlow();
        sftp.stop();
    }

    @Test
    public void test_file_upload() throws Exception
    {
        //Update Sftp Consumer config
        SftpProducerConfiguration consumerConfiguration = flowTestRule
            .getComponentConfig("Sftp Producer", SftpProducerConfiguration.class);
        consumerConfiguration.setOutputDirectory(sftp.getBaseDir());
        consumerConfiguration.setRemotePort(sftp.getPort());
        consumerConfiguration.setOverwrite(true);

        //Setup component expectations
        flowTestRule
            .consumer("Sftp Jms Consumer")
            .converter("MapMessage to SFTP Payload Converter")
            .producer("Sftp Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();

        // Prepare test data
        logger.info("Sending a JMS message.[" + SAMPLE_MESSAGE + "]");
        jmsTemplate.send("sftp.private.jms.queue", new MessageCreator()
        {
            @Override public Message createMessage(Session session) throws JMSException
            {
                MapMessage mapMessage = new ActiveMQMapMessage();
                mapMessage.setString("content",SAMPLE_MESSAGE);
                mapMessage.setString("fileName","generatedSftpProducertest.out");
                return mapMessage;
            }
        });

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await()
            .atMost(5, TimeUnit.SECONDS).untilAsserted(()->
                flowTestRule.assertIsSatisfied()
            );

        assertNotNull(sftp.getFile("generatedSftpProducertest.out"));
    }
}
