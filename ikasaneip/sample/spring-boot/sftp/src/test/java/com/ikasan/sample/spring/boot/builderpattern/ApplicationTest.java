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

import com.ikasan.sample.converter.FilePayloadGeneratorConverter;
import org.apache.sshd.SshServer;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.sftp.SftpRule;
import org.junit.*;
import org.springframework.aop.framework.Advised;
import org.springframework.util.SocketUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
public class ApplicationTest
{
    private SshServer sshd;

    private IkasanApplication ikasanApplication;

    @Rule public SftpRule sftp = new SftpRule("test", "test", null, 22999);

    @Before public void beforeTestSetup() throws Exception
    {
        String[] args = { "--server.port=" + SocketUtils.findAvailableTcpPort(8000, 9000) };
        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class, args);
    }

    @After public void teardown() throws Exception
    {
        ikasanApplication.close();
    }

    /**
     * The SFTP test does not work on windows
     */
    @Test public void test_sftpConsumer_flow() throws Exception
    {
        // Upload data to fake SFTP
        sftp.putFile("testDownload.txt", "Sample File content");
        // you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("Sftp To Log Flow");

        // Update Sftp Consumer config
        ScheduledConsumer scheduledConsumer = (ScheduledConsumer) ((Advised) flow.getFlowElement("Sftp Consumer")
            .getFlowComponent()).getTargetSource().getTarget();
        SftpConsumerConfiguration configuration = (SftpConsumerConfiguration) scheduledConsumer.getConfiguration();
        configuration.setSourceDirectory(sftp.getBaseDir());

        // start flow
        flow.start();
        pause(7000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());
    }

    /**
     * The SFTP test does not work on windows
     */
    @Test public void test_sftpProducer_flow() throws Exception
    {
        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("TimeGenerator To Sftp Flow");

        // Update Sftp Consumer config
        SftpProducer sftpProducer = (SftpProducer) (flow.getFlowElement("Sftp Producer")
        .getFlowComponent());
        SftpProducerConfiguration configuration = (SftpProducerConfiguration) sftpProducer.getConfiguration();
        configuration.setOutputDirectory(sftp.getBaseDir());
        configuration.setOverwrite(true);

        FilePayloadGeneratorConverter filePayloadGeneratorConverter = (FilePayloadGeneratorConverter) (flow.getFlowElement("Random String Generator")
            .getFlowComponent());
        filePayloadGeneratorConverter.setGeneratedFileName("generatedSftpProducertest.out");


        // start flow
        flow.start();
        pause(7000);
        assertEquals("running", flow.getState());
        flow.stop();
        assertEquals("stopped", flow.getState());

        assertNotNull(sftp.getFile("generatedSftpProducertest.out"));
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