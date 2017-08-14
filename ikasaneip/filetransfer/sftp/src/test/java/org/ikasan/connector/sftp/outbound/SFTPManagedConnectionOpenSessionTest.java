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
package org.ikasan.connector.sftp.outbound;

import org.ikasan.connector.sftp.ssh.SftpServerWithPasswordAuthenticator;
import org.ikasan.connector.sftp.ssh.SftpServerWithPublickeyAuthenticator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.resource.ResourceException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test class for the <code>SFTPManagedConnection</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class SFTPManagedConnectionOpenSessionTest
{
    // Mock the
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };
    //private  TemporaryFolder testFolder = new TemporaryFolder();
    private static final int SFTP_PORT_PASSWORD=3001;
    private static final int SFTP_PORT_PUBLICKEY=3003;
    private SftpServerWithPasswordAuthenticator server;
    private SftpServerWithPublickeyAuthenticator serverPublic = new SftpServerWithPublickeyAuthenticator(SFTP_PORT_PUBLICKEY);


    @Before
    public void setup() throws IOException
    {
        Path tempDir = Files.createTempDirectory("tempfiles");
        server= new SftpServerWithPasswordAuthenticator(SFTP_PORT_PASSWORD,tempDir);
        server.start();
        serverPublic.start();
    }

    @After
    public void teardown()
    {
        server.stop();
        serverPublic.stop();
    }

    @Test
    public void openSession_when_user_password_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                exactly(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
                atLeast(1).of(connectionRequestInfo).getPreferredKeyExchangeAlgorithm();
                will(returnValue(null));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test
    public void openSession_when_user_password_provided_and_recursive() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);


        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                exactly(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
                atLeast(1).of(connectionRequestInfo).getPreferredKeyExchangeAlgorithm();
                will(returnValue("diffie-hellman-group1-sha1"));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }
    @Test(expected = ResourceException.class)
    public void openSession_when_user_not_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);


        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                exactly(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test(expected = ResourceException.class)
    public void openSession_when_host_not_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                exactly(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test(expected = ResourceException.class)
    public void openSession_when_password_not_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                atLeast(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test(expected = ResourceException.class)
    public void openSession_when_port_not_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test(expected = ResourceException.class)
    public void openSession_when_max_retry_not_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                atLeast(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue(null));
                exactly(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue("password"));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }

    @Test
    @Ignore
    public void openSession_when_user_privateKey_provided() throws ResourceException
    {
        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery.mock(SFTPConnectionRequestInfo.class);

        classMockery.checking(new Expectations()
        {
            {
                // Dont care what this returns
                atLeast(1).of(connectionRequestInfo).getClientID();
                will(returnValue("testClientId"));
                atLeast(1).of(connectionRequestInfo).getRemoteHostname();
                will(returnValue("localhost"));
                atLeast(1).of(connectionRequestInfo).getRemotePort();
                will(returnValue(SFTP_PORT_PASSWORD));
                atLeast(1).of(connectionRequestInfo).getPrivateKeyFilename();
                will(returnValue("src/test/resources/id_rsa_test"));
                atLeast(1).of(connectionRequestInfo).getKnownHostsFilename();
                will(returnValue("src/test/resources/known_hosts.test"));
                atLeast(1).of(connectionRequestInfo).getMaxRetryAttempts();
                will(returnValue(1));
                atLeast(1).of(connectionRequestInfo).getUsername();
                will(returnValue("username"));
                atLeast(1).of(connectionRequestInfo).getPassword();
                will(returnValue(null));
                atLeast(1).of(connectionRequestInfo).getPreferredAuthentications();
                will(returnValue("publickey,password,gssapi-with-mic"));
                atLeast(1).of(connectionRequestInfo).getConnectionTimeout();
                will(returnValue(300000));
            }
        });

        SFTPManagedConnection managedConnection = new SFTPManagedConnection(
                 connectionRequestInfo);

        managedConnection.openSession();

        classMockery.assertIsSatisfied();
    }
}

