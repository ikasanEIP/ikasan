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

import org.apache.activemq.command.ActiveMQMapMessage;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.ikasan.spec.flow.Flow.RECOVERING;
import static org.ikasan.spec.flow.Flow.RUNNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test Sftp To JMS Flow.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JmsToSftpChunkingFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    @Resource
    public Module<Flow> moduleUnderTest;


    @Resource
    public FileChunkDao fileChunkDao;


    public IkasanFlowTestRule sftpToJmsChunkingFlowTestRule = new IkasanFlowTestRule( );
    public IkasanFlowTestRule jmsToSftpChunkingFlowTestRule = new IkasanFlowTestRule( );

    public SftpRule sftpSource;
    public SftpRule sftpTarget;


    @Before
    public void setup(){
        sftpSource = new SftpRule("test", "test", null, SocketUtils.findAvailableTcpPort(20000, 21000));
        sftpSource.start();
        sftpTarget = new SftpRule("test", "test", null, SocketUtils.findAvailableTcpPort(20000, 21000));
        sftpTarget.start();

        sftpToJmsChunkingFlowTestRule.withFlow(moduleUnderTest.getFlow("Sftp Chunking To Jms Flow"));
        jmsToSftpChunkingFlowTestRule.withFlow(moduleUnderTest.getFlow("Jms To Sftp Chunking Flow"));
    }

    @After public void teardown()
    {
        sftpSource.stop();
        sftpTarget.stop();
        String currentState = sftpToJmsChunkingFlowTestRule.getFlowState();
        if (currentState.equals(RECOVERING) || currentState.equals(RUNNING)){
            sftpToJmsChunkingFlowTestRule.stopFlow();
        }
        String currentState2 = jmsToSftpChunkingFlowTestRule.getFlowState();
        if (currentState.equals(RECOVERING) || currentState.equals(RUNNING)){
            jmsToSftpChunkingFlowTestRule.stopFlow();
        }
        new ActiveMqHelper().removeAllMessages();
    }

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    public void test_file_download() throws Exception
    {

        // Upload data to fake SFTP
        String bigFileStringContent = generateMassiveString();
        sftpSource.putFile("bigTextFile.txt", bigFileStringContent.getBytes());

        //Update Sftp Consumer config
        SftpConsumerConfiguration consumerConfiguration = sftpToJmsChunkingFlowTestRule
            .getComponentConfig("Sftp Chunking Consumer",SftpConsumerConfiguration.class);
        consumerConfiguration.setSourceDirectory(sftpSource.getBaseDir());
        consumerConfiguration.setRemotePort(sftpSource.getPort());

        SftpProducerConfiguration producerConfiguration = jmsToSftpChunkingFlowTestRule
            .getComponentConfig("Sftp Producer", SftpProducerConfiguration.class);
        producerConfiguration.setOutputDirectory(sftpTarget.getBaseDir());
        producerConfiguration.setRemotePort(sftpTarget.getPort());

        //Setup component expectations

        sftpToJmsChunkingFlowTestRule.consumer("Sftp Chunking Consumer")
                    .converter("Sftp Payload to Map Converter")
                    .producer("Sftp Chunking Jms Producer");


        jmsToSftpChunkingFlowTestRule
            .consumer("Sftp Jms Consumer")
            .converter("MapMessage to SFTP Payload Converter")
            .producer("Sftp Producer");


        // start the flow and assert it runs
        jmsToSftpChunkingFlowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
              .untilAsserted(() -> assertEquals("running", jmsToSftpChunkingFlowTestRule.getFlowState()));


        sftpToJmsChunkingFlowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
              .untilAsserted(() -> assertEquals("running", sftpToJmsChunkingFlowTestRule.getFlowState()));

        sftpToJmsChunkingFlowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await().atMost(60, TimeUnit.SECONDS)
              .untilAsserted(() ->  jmsToSftpChunkingFlowTestRule.assertIsSatisfied());

        FileChunkHeader header = fileChunkDao.load(1l);
        Assert.assertEquals(Long.valueOf(11l),header.getSequenceLength());
        Assert.assertEquals("bigTextFile.txt",header.getFileName());

        assertEquals(bigFileStringContent,sftpTarget.getFileAsString("bigTextFile.txt"));
    }

    private String generateMassiveString()
    {
        StringBuffer bf = new StringBuffer();
        for(int i=0;i<90000; i++)
        {
            bf.append(SAMPLE_MESSAGE);
        }

        return bf.toString();
    }

}
