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

import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.ftp.FtpRule;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.TestSocketUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;

/**
 * This test Sftp To JMS Flow.
 *
 * @author Ikasan Development Team
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FtpToJmsFlowTest
{

    private static String SAMPLE_MESSAGE = "Hello world!";

    @Resource
    public Module<Flow> moduleUnderTest;

    @Resource
    public JmsListenerEndpointRegistry registry;

    @Value("${jms.provider.url}")
    private String brokerUrl;


    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    public FtpRule ftp;

   @Before
    public void setup(){
        ftp  = new FtpRule("test","test",null, TestSocketUtils.findAvailableTcpPort());
        ftp.start();

        flowTestRule.withFlow(moduleUnderTest.getFlow("Ftp To Jms Flow"));
    }

    @After
    public void teardown()
    {
        flowTestRule.stopFlow();
        ftp.stop();

    }

    @AfterClass
    public static void shutdownBroker(){
        new ActiveMqHelper().shutdownBroker();
    }

    @Test
    public void test_file_download() throws Exception
    {

        // Upload data to fake FTP
        ftp.putFile("testDownload.txt",SAMPLE_MESSAGE);

        //Update Ftp Consumer config
        FtpConsumerConfiguration consumerConfiguration = flowTestRule.getComponentConfig("Ftp Consumer",FtpConsumerConfiguration.class);
        consumerConfiguration.setSourceDirectory(ftp.getBaseDir());
        consumerConfiguration.setRemotePort(ftp.getPort());

        //Create test queue listener
        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(brokerUrl, "ftp.private.jms.queue", registry);
        messageListenerVerifier.start();

        //Setup component expectations

        flowTestRule.consumer("Ftp Consumer")
            .converter("Ftp Payload to Map Converter")
            .producer("Ftp Jms Producer");

        // start the flow and assert it runs
        flowTestRule.startFlow();
        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await()
              .atMost(5, TimeUnit.SECONDS).untilAsserted(()->
            assertEquals("running",flowTestRule.getFlowState()));

        flowTestRule.fireScheduledConsumer();

        with().pollInterval(500, TimeUnit.MILLISECONDS).and().await()
              .atMost(60, TimeUnit.SECONDS).untilAsserted(()->
                 assertEquals(1, messageListenerVerifier.getCaptureResults().size())
              );

        flowTestRule.assertIsSatisfied();
    }

}
