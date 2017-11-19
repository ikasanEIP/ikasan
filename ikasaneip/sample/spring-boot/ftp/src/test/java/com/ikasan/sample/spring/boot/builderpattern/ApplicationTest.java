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

import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import com.ikasan.sample.converter.FilePayloadGeneratorConverter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.ftp.FtpRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.springframework.util.SocketUtils;

import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
public class ApplicationTest {


    IkasanApplication ikasanApplication;

    @Rule
    public FtpRule ftp  = new FtpRule("test","test",null,22999);

    @Before
    public void setup() throws ClientConnectionException
    {

        String[] args = { "--server.port="+ SocketUtils.findAvailableTcpPort(8000,9000)};

        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(Application.class, args);

    }

    @After
    public void tearDown() throws Exception {

        ikasanApplication.close();
    }

    /**
     *  You may encounter issue running this test on windows.
     *
     * @throws Exception
     */
    @Test
    public void test_ftpConsumer_flow() throws Exception {

        // Upload data to fake SFTP
        ftp.putFile("testDownload.txt","Sample Test Message");


        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("Ftp To Log Flow");

        // start flow
        flow.start();
        pause(15000);
        assertEquals("running", flow.getState());

        flow.stop();
        pause(2000);
        assertEquals("stopped", flow.getState());

    }

    /**
     *  You may encounter issue running this test on windows.
     *
     * @throws Exception
     */
    @Test
    public void test_ftpProducer_flow() throws Exception {

        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        String expectedFileName ="testProducerFileName.txt";
        FilePayloadGeneratorConverter filePayloadGeneratorConverter = (FilePayloadGeneratorConverter) ikasanApplication.getBean(FilePayloadGeneratorConverter.class);
        filePayloadGeneratorConverter.setFileName(expectedFileName);
        Flow flow = (Flow) module.getFlow("TimeGenerator To Ftp Flow");

        // start flow
        flow.start();
        pause(15000);
        assertEquals("running", flow.getState());

        flow.stop();
        pause(2000);
        assertEquals("stopped", flow.getState());
        assertGeneratedFile(expectedFileName);
    }

    private void assertGeneratedFile(String expectedFileName) throws Exception
    {
        FileSystemEntry producedFile = ftp.getFile(expectedFileName);
        assertEquals(expectedFileName,producedFile.getName());
    }
    /**
     * Sleep for value in millis
     *
     * @param value
     */
    private void pause(long value) {
        try {
            Thread.sleep(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
