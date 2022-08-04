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

import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryService;
import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryServiceFactory;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest
{
    private static String FILE_PRODUCER_FILE_NAME = "testProducer.out";

    private static String FILE_CONSUMER_FILE_NAME = "testConsumer.txt";

    @Resource
    private Module<Flow> moduleUnderTest;

    @Resource
    private ConfigurationManagement configurationManagement;

    @Resource
    private JmsListenerEndpointRegistry registry;

    @Resource
    private JmsTemplate jmsTemplate;

    @Value("${jms.provider.url}")
    private String brokerUrl;

    ScheduledJobRecoveryService scheduledJobRecoveryService = ScheduledJobRecoveryServiceFactory.getInstance();

    public IkasanFlowTestRule flowTestRule = new IkasanFlowTestRule();

    @Before
    public void setup()
    {
        // clean up recovery persistence
        scheduledJobRecoveryService.removeAllRecoveries();
    }

    @After public void shutdown() throws IOException
    {
        flowTestRule.stopFlow();

        Files.deleteIfExists(FileSystems.getDefault().getPath(FILE_PRODUCER_FILE_NAME));
        Files.deleteIfExists(FileSystems.getDefault().getPath(FILE_CONSUMER_FILE_NAME));
    }

    @Test
    @DirtiesContext
    public void sourceFileFlow_flow() throws Exception
    {
        flowTestRule.withFlow(moduleUnderTest.getFlow("fileSystemToJMSFlow"));

        // create file to be consumed
        String content = "Hello World !!";
        Files.write(Paths.get(FILE_CONSUMER_FILE_NAME), content.getBytes());

        ConfiguredResource configuredResource = (ConfiguredResource)flowTestRule.getComponent("File Consumer");
        Object configuration = configurationManagement.createConfiguration(configuredResource);
        configurationManagement.saveConfiguration(configuration);

        // Get MessageListenerVerifier and start the listner
        final MessageListenerVerifier messageListenerVerifierTarget = new MessageListenerVerifier(brokerUrl, "jms.topic.test", registry);
        messageListenerVerifierTarget.start();

        //Setup component expectations
        flowTestRule.consumer("File Consumer")
            .filter("My Filter")
            .converter("File Converter")
            .producer("JMS Producer");

        flowTestRule.startFlow();
        flowTestRule.sleep(1000L);

        flowTestRule.fireScheduledConsumer();

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        flowTestRule.assertIsSatisfied();

        // Set expectation
        assertTrue(messageListenerVerifierTarget.getCaptureResults().size()>=1);
        assertEquals(((TextMessage)messageListenerVerifierTarget.getCaptureResults().get(0)).getText(),
            FILE_CONSUMER_FILE_NAME);
    }

    @Test
    @DirtiesContext
    public void sourceFileFlow_flow_with_scheduled_persistent_recovery() throws Exception
    {
        Flow flowOnTest = moduleUnderTest.getFlow("fileSystemToJMSFlow");
        flowTestRule.withFlow(flowOnTest);

        // create file to be consumed
        String content = "Hello World !!";
        Files.write(Paths.get(FILE_CONSUMER_FILE_NAME), content.getBytes());

        ConfiguredResource configuredResource = (ConfiguredResource)flowTestRule.getComponent("File Consumer");
        Object configuration = configurationManagement.createConfiguration(configuredResource);
        configurationManagement.saveConfiguration(configuration);
        Set<Trigger> triggers = ((ScheduledConsumer)configuredResource).getTriggers();

        // set the next fire time of the scheduled job to force a persistent recovery scenario
        ScheduledJobRecoveryService scheduledJobRecoveryService = ScheduledJobRecoveryServiceFactory.getInstance();
        Date nextFireTime = new Date();
        String jobNameIteration = "sampleFileConsumer" + "_" + "0 0/5 * * * ? *".hashCode();
        scheduledJobRecoveryService.setNextFireTime(jobNameIteration, "sampleFile",  nextFireTime);

        // Get MessageListenerVerifier and start the listener
        final MessageListenerVerifier messageListenerVerifierTarget = new MessageListenerVerifier(brokerUrl, "jms.topic.test", registry);
        messageListenerVerifierTarget.start();

        //Setup component expectations
        flowTestRule.consumer("File Consumer")
            .filter("My Filter")
            .converter("File Converter")
            .producer("JMS Producer");


        flowTestRule.startFlow();
        flowTestRule.sleep(1000L);

        // wait for a brief while to let the flow complete
        flowTestRule.sleep(2000L);

        flowTestRule.assertIsSatisfied();

        // Set expectation
        assertTrue(messageListenerVerifierTarget.getCaptureResults().size()>=1);
        assertEquals(((TextMessage)messageListenerVerifierTarget.getCaptureResults().get(0)).getText(),
            FILE_CONSUMER_FILE_NAME);
    }

    @Test
    @DirtiesContext
    public void targetFileFlow_test_file_delivery() throws Exception
    {
        flowTestRule.withFlow(moduleUnderTest.getFlow("jmsToFileSystemFlow"));

        // update producer with file producer name
        FileProducerConfiguration producerConfiguration =  flowTestRule.getComponentConfig("File Producer",FileProducerConfiguration.class);
        producerConfiguration.setFilename(FILE_PRODUCER_FILE_NAME);

        // update flow consumer  with file producer name
        SpringMessageConsumerConfiguration jmsConfiguration = flowTestRule.getComponentConfig("JMS Consumer",SpringMessageConsumerConfiguration.class);
        jmsConfiguration.setDestinationJndiName("private.file.queue.test");

        //Setup component expectations
        flowTestRule.consumer("JMS Consumer")
            .producer("File Producer");

        flowTestRule.startFlow();
        flowTestRule.sleep(1000L);

        // Prepare test data
        String message = "Random Text";
        System.out.println("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("private.file.queue.test", message);

        flowTestRule.sleep(1000L);

        flowTestRule.assertIsSatisfied();

        File result = FileSystems.getDefault().getPath(FILE_PRODUCER_FILE_NAME).toFile();

        assertTrue("File does not exist.", result.exists());
        assertEquals("Generated file, has different content.", message,
            new String(Files.readAllBytes(result.toPath())));
    }

}
