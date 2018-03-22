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
package org.ikasan.testharness.flow.ftp;


import org.junit.rules.ExternalResource;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.*;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FtpSystemRule is a helper class to allow FTP testing.
 *
 * @author Ikasan Development Team
 */
public class FtpRule extends ExternalResource
{
    Logger log = LoggerFactory.getLogger(this.getClass());

    private FakeFtpServer fakeFtpServer;

    private List<Path> filesToCleanup = new ArrayList<>();

    private String user;

    private String password;

    private String baseDir;

    private int port;

    public FtpRule(String user, String password, String baseDir, int port)
    {
        this.user = user;
        this.password = password;
        this.port = port;
        if (baseDir != null)
        {
            this.baseDir = baseDir;
        }
        else
        {
            try
            {
                Path tempPath = Files.createTempDirectory("ftpTestBase");
                this.baseDir = tempPath.toString();
            }
            catch (IOException e)
            {
                log.error("Unable to create temp Dir.", e);
                throw new IllegalArgumentException(e);
            }
        }
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(port);  // use any free port
        org.mockftpserver.fake.filesystem.FileSystem fileSystem = new UnixFakeFileSystem();
        DirectoryEntry directoryEntry = new DirectoryEntry(this.baseDir);
        directoryEntry.setPermissions(new Permissions("rwxrwxrwx"));
        fileSystem.add(directoryEntry);
        fakeFtpServer.setFileSystem(fileSystem);
        UserAccount userAccount = new UserAccount(user, password, this.baseDir);
        fakeFtpServer.addUserAccount(userAccount);
    }

    public FtpRule()
    {
        this("test", "test", null, SocketUtils.findAvailableTcpPort(20000, 30000));
    }

    public void putFile(String fileName, final String content) throws Exception
    {
        FileSystem fileSystem = fakeFtpServer.getFileSystem();
        fileSystem.add(new FileEntry(baseDir + "/" + fileName, content));
    }

    public FileSystemEntry getFile(String fileName) throws Exception
    {
        FileSystem fileSystem = fakeFtpServer.getFileSystem();
        return fileSystem.getEntry(baseDir + "/" + fileName);
    }

    public int getPort()
    {
        return fakeFtpServer.getServerControlPort();
    }

    public String getBaseDir()
    {
        return baseDir;
    }

    /**
     * Start the fakeFtpServer.
     * <p/>
     * The ssh will normally be started by JUnit using the before() method.  This method allows the sshd to
     * be started manually to support advanced testing scenarios.
     */
    public void start()
    {
        fakeFtpServer.start();
    }

    /**
     * Stop the fakeFtpServer. Remove all temp files from the baseDir
     * <p/>
     * The fakeFtpServer demon will normally be stopped by JUnit using the after() method.  This method allows the sshd to
     * be stopped manually to support advanced testing scenarios.
     */
    public void stop()
    {
        for(Object file:fakeFtpServer.getFileSystem().listFiles(baseDir)){
            fakeFtpServer.getFileSystem().delete(((FileEntry)file).getPath());
        }
        fakeFtpServer.getFileSystem().delete(baseDir);

        fakeFtpServer.stop();
    }

    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable {@code after}
     */
    @Override protected void before() throws Throwable
    {
        start();
    }

    /**
     * Override to tear down your specific external resource.
     */
    @Override protected void after()
    {
        stop();
    }
}
