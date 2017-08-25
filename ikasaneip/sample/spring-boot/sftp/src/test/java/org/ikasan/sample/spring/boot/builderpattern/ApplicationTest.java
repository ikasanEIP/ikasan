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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private SshServer sshd;

    @Before
    public void beforeTestSetup() throws Exception {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(22999);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("target/hostkey.ser"));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

            @Override
            public boolean authenticate(String s, String s1, ServerSession serverSession) {
                return true;
            }


        });
        CommandFactory myCommandFactory = new CommandFactory() {
            public Command createCommand(String command) {
                System.out.println("Command: " + command);
                return null;
            }
        };
        sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));
        List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);
        sshd.start();

        putFile();
    }

    public void putFile() throws Exception {
        // Make sure to delete all files created by sftp
        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txtb"));
        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txt"));
        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txt.tmp"));

        JSch jsch = new JSch();

        Hashtable config = new Hashtable();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);

        Session session = jsch.getSession("test", "localhost", 22999);
        session.setPassword("test");

        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp sftpChannel = (ChannelSftp) channel;

        final String testFileContents = "some file contents";

        String uploadedFileName = "test.txtb";
        sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);

        if (sftpChannel.isConnected()) {
            sftpChannel.exit();
        }

        if (session.isConnected()) {
            session.disconnect();
        }

    }

    @After
    public void teardown() throws Exception {
        sshd.stop();

        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txtb"));
        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txt"));
        Files.deleteIfExists(FileSystems.getDefault().getPath("test.txt.tmp"));

    }

    /**
     * The SFTP test does not work on windows
     */
    @Test
    public void test_sftpConsumer_flow() throws Exception {
        String[] args = {""};

        Application myApplication = new Application();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
        System.out.println("Check is module healthy.");


        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("sftpToLogFlow");

        // start flow
        flow.start();
        pause(20000);
        assertEquals("running", flow.getState());

        flow.stop();
        pause(2000);
        assertEquals("stopped", flow.getState());

    }


    /**
     * The SFTP test does not work on windows
     */
    @Ignore
    @Test
    public void test_sftpProducer_flow() throws Exception {
        String[] args = {""};

        Application myApplication = new Application();
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
        System.out.println("Check is module healthy.");


        // / you cannot lookup flow directly from context as only Module is injected through @Bean
        Module module = (Module) ikasanApplication.getBean(Module.class);
        Flow flow = (Flow) module.getFlow("timeGeneratorToSftpFlow");

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
