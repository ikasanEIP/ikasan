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
package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.connector.basefiletransfer.net.ClientConnectionException;
import org.ikasan.sample.converter.FilePayloadGeneratorConverter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
public class ApplicationTest {

    //private static final String HOME_DIR = "/";
    private static final String FTP_DIR = "/";
    private static final String FILE_1 = "sample1.txt";
    private static final String CONTENTS_1 = "abcdef 1234567890";
    private static final String FILE_2 = "sample22.txt";
    private static final String CONTENTS_2 = "abcdef121212 1234567890";
    private static final String HOST = "localhost";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    private int port;
    private FakeFtpServer fakeFtpServer;

    @Before
    public void setup() throws ClientConnectionException
    {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(22999);  // use any free port

        FileSystem fileSystem = new UnixFakeFileSystem();

     //   DirectoryEntry directoryEntry1 = new DirectoryEntry(HOME_DIR);
        DirectoryEntry directoryEntry2 = new DirectoryEntry(FTP_DIR);
        directoryEntry2.setPermissions(new Permissions("rwxrwxrwx"));

        //fileSystem.add(directoryEntry1);
        fileSystem.add(directoryEntry2);
        fileSystem.add(new FileEntry(FTP_DIR+FILE_1, CONTENTS_1));
       // fileSystem.add(new FileEntry(FTP_DIR+"/"+FILE_2, CONTENTS_2));

        fakeFtpServer.setFileSystem(fileSystem);

        UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, FTP_DIR);
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();
        port = fakeFtpServer.getServerControlPort();

    }

    @After
    public void tearDown() throws Exception {

        for(Object file:fakeFtpServer.getFileSystem().listFiles(FTP_DIR)){
            fakeFtpServer.getFileSystem().delete(((FileEntry)file).getPath());
        }
        fakeFtpServer.getFileSystem().delete(FTP_DIR);
        fakeFtpServer.stop();
    }

    /**
     *  You may encounter issue running this test on windows.
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void test_ftpConsumer_flow() throws Exception {
        String[] args = {""};

        Application myApplication = new Application();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
        System.out.println("Check is module healthy.");


        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("ftpToLogFlow");

        // start flow
        flow.start();
        pause(20000);
        assertEquals("running", flow.getState());

        pause(5000);
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
        String[] args = {""};

        Application myApplication = new Application();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
        System.out.println("Check is module healthy.");


        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        String expectedFileName ="testProducerFileName.txt";
        FilePayloadGeneratorConverter filePayloadGeneratorConverter = (FilePayloadGeneratorConverter) ikasanApplication.getBean(FilePayloadGeneratorConverter.class);
        filePayloadGeneratorConverter.setFileName(expectedFileName);
        Flow flow = (Flow) module.getFlow("timeGeneratorToFtpFlow");

        // start flow
        flow.start();
        pause(20000);
        assertEquals("running", flow.getState());

        pause(7000);
        flow.stop();
        pause(2000);
        assertEquals("stopped", flow.getState());
        assertGeneratedFile(expectedFileName);
    }

    private void assertGeneratedFile(String expectedFileName)
    {
        FileEntry producedFile = (FileEntry) fakeFtpServer.getFileSystem().getEntry(FTP_DIR+expectedFileName);
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
