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

import com.github.stefanbirkner.fakesftpserver.rule.FakeSftpServerRule;
import jakarta.annotation.Resource;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.nonfunctional.test.util.FileTestUtil;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.ikasan.spec.flow.Flow.RECOVERING;
import static org.ikasan.spec.flow.Flow.RUNNING;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test Sftp To JMS Flow.
 *
 * @author Ikasan Development Team
 */
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JmsToSftpChunkingFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    String objectStoreDir = "./transaction-logs";

    @Resource
    public Module<Flow> moduleUnderTest;

    @Resource
    @Autowired
    @Qualifier("ikasan.xads")
    private DataSource ikasanxads;

    public IkasanFlowTestRule sftpToJmsChunkingFlowTestRule = new IkasanFlowTestRule( );
    public IkasanFlowTestRule jmsToSftpChunkingFlowTestRule = new IkasanFlowTestRule( );

    @Rule
    public FakeSftpServerRule sftp = new FakeSftpServerRule().addUser("test", "test");

    String homeDir;

    @BeforeEach
    void setup() throws IOException
    {
        FileTestUtil.deleteFile(new File(objectStoreDir));
        homeDir = "/home/" +System.getProperty("user.name");
        sftp.createDirectories("/source","/target");
        sftpToJmsChunkingFlowTestRule.withFlow(moduleUnderTest.getFlow("Sftp Chunking To Jms Flow"));
        jmsToSftpChunkingFlowTestRule.withFlow(moduleUnderTest.getFlow("Jms To Sftp Chunking Flow"));
    }

    @AfterEach
    void teardown() throws InterruptedException, SQLException, IOException
    {
        sftp.deleteAllFilesAndDirectories();

        String currentState = sftpToJmsChunkingFlowTestRule.getFlowState();
        if (currentState.equals(RECOVERING) || currentState.equals(RUNNING)){
            sftpToJmsChunkingFlowTestRule.stopFlow();
        }
        String currentState2 = jmsToSftpChunkingFlowTestRule.getFlowState();
        if (currentState2.equals(RECOVERING) || currentState2.equals(RUNNING)){
            jmsToSftpChunkingFlowTestRule.stopFlow();
        }
        new ActiveMqHelper().removeAllMessages();
        Thread.sleep(250);
        new DatabaseHelper(ikasanxads).clearExtendedDatabaseTables();

    }

    @AfterAll
    static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    void test_file_download() throws Exception
    {

        // Upload data to fake SFTP
        String bigFileStringContent = generateMassiveString();
        sftp.putFile("/source/bigTextFile.txt", bigFileStringContent.getBytes());
        //Update Sftp Consumer config
        SftpConsumerConfiguration consumerConfiguration = sftpToJmsChunkingFlowTestRule
            .getComponentConfig("Sftp Chunking Consumer",SftpConsumerConfiguration.class);
        consumerConfiguration.setSourceDirectory("/source");
        consumerConfiguration.setRemotePort(sftp.getPort());

        SftpProducerConfiguration producerConfiguration = jmsToSftpChunkingFlowTestRule
            .getComponentConfig("Sftp Producer", SftpProducerConfiguration.class);
        producerConfiguration.setOutputDirectory(".");
        producerConfiguration.setRemotePort(sftp.getPort());

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

        with().pollInterval(500, TimeUnit.MILLISECONDS)
              .pollDelay(5,TimeUnit.SECONDS)
              .and().await().atMost(60, TimeUnit.SECONDS)
              .untilAsserted(() ->  jmsToSftpChunkingFlowTestRule.assertIsSatisfied());

        assertEquals(bigFileStringContent,sftp.getFileContent(homeDir+"/bigTextFile.txt", Charset.defaultCharset()));
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
