/*
 * $Id$
 * $URL$
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
package org.ikasan.testharness.flow.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * SftpSystemRule is a helper class to allow SFTP testing.
 *
 * @author Ikasan Development Team
 */
public class SftpRule extends ExternalResource
{
    Logger log = LoggerFactory.getLogger(this.getClass());

    private SshServer sshd;

    private List<Path> filesToCleanup = new ArrayList<>();

    private String user;

    private String password;

    private String baseDir;

    private int port;

    public SftpRule(String user, String password, String baseDir, Integer port)
    {
        this.user = user;
        this.password = password;
        this.port = port;
        if(baseDir!=null){
            this.baseDir = baseDir;
        }else{
            try
            {
                Path tempPath= Files.createTempDirectory("sftpTestBase");
                filesToCleanup.add(tempPath);
                this.baseDir = tempPath.toString();
            }
            catch (IOException e)
            {
                log.error("Unable to create temp Dir.", e);
                throw new IllegalArgumentException(e);
            }
        }

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(this.port);


        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("target/hostkey.ser"));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator()
        {
            @Override public boolean authenticate(String s, String s1, ServerSession serverSession)
            {
                return true;
            }
        });
        CommandFactory myCommandFactory = new CommandFactory()
        {
            public Command createCommand(String command)
            {
                System.out.println("Command: " + command);
                return null;
            }
        };
        sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));
        List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);
    }

    public SftpRule()
    {
        this("test", "test", null, SocketUtils.findAvailableTcpPort(8000, 9000));


    }

    public void putFile(String fileName, final String content) throws Exception
    {
        JSch jsch = new JSch();
        Hashtable config = new Hashtable();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);
        Session session = jsch.getSession(user, "localhost", sshd.getPort());
        session.setPassword(password);

        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(baseDir);

        sftpChannel.put(new ByteArrayInputStream(content.getBytes()), fileName);
        if (sftpChannel.isConnected())
        {
            sftpChannel.exit();
        }
        if (session.isConnected())
        {
            session.disconnect();
        }
        filesToCleanup.add(FileSystems.getDefault().getPath(baseDir+FileSystems.getDefault().getSeparator()+fileName));
    }

    public InputStream getFile(String fileName) throws Exception
    {
        JSch jsch = new JSch();
        Hashtable config = new Hashtable();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);
        Session session = jsch.getSession(user, "localhost", sshd.getPort());
        session.setPassword(password);

        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(baseDir);

        InputStream inputStream = sftpChannel.get(fileName);
        if (sftpChannel.isConnected())
        {
            sftpChannel.exit();
        }
        if (session.isConnected())
        {
            session.disconnect();
        }
        return inputStream;
    }

    public String getFileAsString(String fileName) throws Exception
    {
        return IOUtils.toString(getFile(fileName), "utf-8");
    }


    public int getPort()
    {
        return sshd.getPort();
    }

    public String getBaseDir()
    {
        return baseDir;
    }

    /**
     * Start the sshd.
     * <p/>
     * The ssh will normally be started by JUnit using the before() method.  This method allows the sshd to
     * be started manually to support advanced testing scenarios.
     */
    public void start()
    {
        try
        {
            sshd.start();
        }
        catch (IOException e)
        {
            log.error("Unable to start ssdh.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the sshd.
     * <p/>
     * The ssh demon will normally be stopped by JUnit using the after() method.  This method allows the sshd to
     * be stopped manually to support advanced testing scenarios.
     */
    public void stop()
    {
        try
        {

            filesToCleanup.forEach(file ->{
                        try
                        {
                            deletePathRecursivly(file);
                        }
                        catch (IOException e)
                        {
                            log.warn("Unable to delete [" + file.toString() + "]");
                        }
                    }
                );
            sshd.stop();
        }
        catch (InterruptedException e)
        {
            log.error("Unable to stop ssdh.", e);
        }

    }

    private void deletePathRecursivly(Path directory ) throws IOException
    {

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable {@code after}
     */
    @Override
    protected void before() throws Throwable
    {
        start();
    }

    /**
     * Override to tear down your specific external resource.
     */
    @Override
    protected void after()
    {
        stop();
    }
}
