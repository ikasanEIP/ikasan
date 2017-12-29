/*
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

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.producer.FtpProducer;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.ftp.FtpRule;
import org.ikasan.testharness.flow.jms.MessageListenerVerifier;
import org.junit.*;
import org.springframework.aop.framework.Advised;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.SocketUtils;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
public class ApplicationTest
{
    private static String SAMPLE_MESSAGE = "Hello world!";

    private IkasanApplication ikasanApplication;

    @Rule public FtpRule ftp = new FtpRule("test", "test", null, 22999);

    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();

    @Before public void beforeTestSetup() throws Exception
    {
        broker.start();
        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000) };
        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class, args);
    }

    @After public void teardown() throws Exception
    {
        broker.stop();
        ikasanApplication.close();
    }

    /**
     * The FTP test does not work on windows
     */
    @Test public void test_ftpConsumer_flow() throws Exception
    {
        // Upload data to fake FTP
        ftp.putFile("testDownload.txt", "Sample File content");
        // you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("${sourceFlowName}");

        // Update Ftp Consumer config
        ScheduledConsumer scheduledConsumer = (ScheduledConsumer) ((Advised) flow.getFlowElement("Ftp Consumer")
            .getFlowComponent()).getTargetSource().getTarget();
        FtpConsumerConfiguration configuration = (FtpConsumerConfiguration) scheduledConsumer.getConfiguration();
        configuration.setSourceDirectory(ftp.getBaseDir());

        //Create test queue listener
        JmsListenerEndpointRegistry registry = ikasanApplication.getBean(JmsListenerEndpointRegistry.class);
        final MessageListenerVerifier messageListenerVerifier = new MessageListenerVerifier(broker.getVmURL(), "ftp.private.jms.queue", registry);
        messageListenerVerifier.start();

        //Wait for listener to start
        pause(1000);

        // start flow
        flow.start();
        pause(7000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());

        assertEquals(1, messageListenerVerifier.getCaptureResults().size());

    }

    /**
     * The FTP test does not work on windows
     */
    @Test public void test_ftpProducer_flow() throws Exception
    {
        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("${targetFlowName}");

        // Update Ftp Consumer config
        FtpProducer ftpProducer = (FtpProducer) (flow.getFlowElement("Ftp Producer")
        .getFlowComponent());
        FtpProducerConfiguration configuration = (FtpProducerConfiguration) ftpProducer.getConfiguration();
        configuration.setOutputDirectory(ftp.getBaseDir());
        configuration.setOverwrite(true);

        // Prepare test data
        JmsTemplate jmsTemplate = ikasanApplication.getBean(JmsTemplate.class);
        System.out.println("Sending a JMS message.[" + SAMPLE_MESSAGE + "]");
        jmsTemplate.send("ftp.private.jms.queue", new MessageCreator()
        {
            @Override public Message createMessage(Session session) throws JMSException
            {
                MapMessage mapMessage = new ActiveMQMapMessage();
                mapMessage.setString("content",SAMPLE_MESSAGE);
                mapMessage.setString("fileName","generatedFtpProducertest.out");
                return mapMessage;
            }
        });


        // start flow
        flow.start();
        pause(3000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());

        assertNotNull(ftp.getFile("generatedFtpProducertest.out"));
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