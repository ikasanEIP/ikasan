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

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.component.endpoint.filesystem.producer.FileProducer;
import org.ikasan.component.endpoint.filesystem.producer.FileProducerConfiguration;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.consumer.SpringMessageConsumerConfiguration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.SocketUtils;

import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
public class ApplicationTest
{
    private static String FILE_PRODUCER_FILE_NAME = "testProducer.out";

    private static String FILE_CONSUMER_FILE_NAME = "testConsumer.txt";

    private EmbeddedActiveMQBroker broker;

    private IkasanApplication ikasanApplication;

    @Before public void setup()
    {
        broker = new EmbeddedActiveMQBroker();
        broker.start();
        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000) };
        Application myApplication = new Application();
        ikasanApplication = myApplication.boot(args);
        System.out.println("Check is module healthy.");
    }

    @After public void shutdown() throws IOException
    {
        Files.deleteIfExists(FileSystems.getDefault().getPath(FILE_PRODUCER_FILE_NAME));
        Files.deleteIfExists(FileSystems.getDefault().getPath(FILE_CONSUMER_FILE_NAME));
        ikasanApplication.close();
        broker.stop();
    }

    @Test public void sourceFileFlow_flow() throws Exception
    {
        // create file to be consumed
        String content = "Hello World !!";
        Files.write(Paths.get(FILE_CONSUMER_FILE_NAME), content.getBytes());
        List<Module> modules = ikasanApplication.getModules();
        assertTrue("There should only be 1 module in this application, but found " + modules.size(),
            modules.size() == 1);
        Module<Flow> module = modules.get(0);
        Flow flow = module.getFlow("sourceFileFlow");

        ConfigurationManagement configurationManagement = ikasanApplication.getBean(ConfigurationManagement.class);
        ConfiguredResource configuredResource = ((ConfiguredResource)flow.getFlowElement("File Consumer").getFlowComponent());
        Object configuration = configurationManagement.createConfiguration(configuredResource);
        configurationManagement.saveConfiguration(configuration);

        // Get MessageListenerVerifier and start the listner
        MessageListenerVerifier messageListenerVerifierTarget = ikasanApplication
            .getBean("messageListenerVerifierTarget", MessageListenerVerifier.class);
        messageListenerVerifierTarget.start();


        // start flow
        flow.start();
        // give flow time
        pause(7000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());
        // Set expectation
        assertTrue(messageListenerVerifierTarget.getCaptureResults().size()>=1);
        assertEquals(((TextMessage)messageListenerVerifierTarget.getCaptureResults().get(0)).getText(),
            FILE_CONSUMER_FILE_NAME);
    }

    @Test public void targetFileFlow_test_file_delivery() throws Exception
    {
        // Prepare test data
        JmsTemplate jmsTemplate = ikasanApplication.getBean(JmsTemplate.class);
        String message = "Random Text";
        System.out.println("Sending a JMS message.[" + message + "]");
        jmsTemplate.convertAndSend("private.file.queue.test", message);

        // get targetFileFlow from context
        List<Module> modules = ikasanApplication.getModules();
        assertTrue("There should only be 1 module in this application, but found " + modules.size(),
            modules.size() == 1);
        Module<Flow> module = modules.get(0);
        Flow flow = module.getFlow("targetFileFlow");

        // update producer with file producer name
        FileProducerConfiguration producerConfiguration = ((FileProducer) flow.getFlowElement("File Producer")
            .getFlowComponent()).getConfiguration();
        producerConfiguration.setFilename(FILE_PRODUCER_FILE_NAME);

        // update flow consumer  with file producer name
        JmsContainerConsumer containerConsumer = (JmsContainerConsumer) ((Advised) flow.getFlowElement("JMS Consumer")
            .getFlowComponent()).getTargetSource().getTarget();
        SpringMessageConsumerConfiguration jmsConfiguration = containerConsumer.getConfiguration();
        jmsConfiguration.setDestinationJndiName("private.file.queue.test");

        // start flow
        flow.start();

        // give flow time
        pause(4000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());

        File result = FileSystems.getDefault().getPath(FILE_PRODUCER_FILE_NAME).toFile();

        assertTrue("File does not exist.", result.exists());
        assertEquals("Generated file, has different content.", message,
            new String(Files.readAllBytes(result.toPath())));
    }

    /**
     * Sleep for value in millis
     *
     * @param value
     */
    private void pause(long value)
    {
        try
        {
            Thread.sleep(value);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
