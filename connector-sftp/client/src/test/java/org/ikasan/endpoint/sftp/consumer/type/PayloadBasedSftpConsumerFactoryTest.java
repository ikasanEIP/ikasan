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
package org.ikasan.endpoint.sftp.consumer.type;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.InvalidPropertyException;

import junit.framework.Assert;

import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerAlternateConfiguration;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.consumer.type.PayloadBasedSftpConsumer;
import org.ikasan.endpoint.sftp.consumer.type.PayloadBasedSftpConsumerFactory;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link PayloadBasedSftpConsumerFactory}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class PayloadBasedSftpConsumerFactoryTest
{
    /** The mockery */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock connectionFactory */
    private final ConnectionFactory connectionFactory = this.mockery.mock(ConnectionFactory.class, "mockConnectionFactory");
    
    /** Mock sftpConfiguration */
    private final SftpConsumerConfiguration sftpConfiguration = this.mockery.mock(SftpConsumerConfiguration.class, "mockSftpConfiguration");

    /** mockSFTPConnectionSpec */
    private final SFTPConnectionSpec sftpConnectionSpec = this.mockery.mock(SFTPConnectionSpec.class, "mockSFTPConnectionSpec");

    /** Mock DirectoryURLFactory */
    private final DirectoryURLFactory directoryURLFactory = this.mockery.mock(DirectoryURLFactory.class, "mockDirectoryURLFactory");

    /** Instance on test */
    private EndpointFactory<Consumer<?>, SftpConsumerConfiguration> payloadBasedSftpConsumerFactory = new PayloadBasedSftpConsumerFactoryWithMockSpec(this.connectionFactory);

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new PayloadBasedSftpConsumerFactory(null);
    }

    /**
     * Test create consumer invocation.
     * 
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createConsumer() throws ResourceException
    {
        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).validate();

                one(sftpConfiguration).setSourceDirectoryURLFactory(null);

                one(sftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(sftpConnectionSpec).setClientID("clientID");

                one(sftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");

                one(sftpConfiguration).getKnownHostsFilename();will(returnValue("known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("known_host");

                one(sftpConfiguration).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(sftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(sftpConfiguration).getPrivateKeyFilename(); will(returnValue("private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("private.key");

                one(sftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(sftpConfiguration).getUsername(); will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                one(sftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

            }
        });

        // Test
        Consumer<?> createdConsumer = this.payloadBasedSftpConsumerFactory.createEndpoint(this.sftpConfiguration);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedSftpConsumer);
        Assert.assertNull(((PayloadBasedSftpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Creating consumer instance fails as incoming {@link SftpConsumerConfiguration} object
     * is invalid
     * 
     * @throws ResourceException if error creating endpoint
     */
    @Test(expected=ResourceException.class)
    public void test_createConsumer_fails_invalid_configuration() throws ResourceException
    {
        final InvalidPropertyException exception = new InvalidPropertyException();

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).validate(); will(throwException(exception));
            }
        });

        // Test
        Consumer<?> createdConsumer = this.payloadBasedSftpConsumerFactory.createEndpoint(this.sftpConfiguration);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedSftpConsumer);
        Assert.assertNull(((PayloadBasedSftpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test create consumer invocation.
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createConsumerWithDirectoryURLFactory() throws ResourceException
    {
        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).validate();

                one(sftpConfiguration).setSourceDirectoryURLFactory(directoryURLFactory);

                one(sftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(sftpConnectionSpec).setClientID("clientID");

                one(sftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");

                one(sftpConfiguration).getKnownHostsFilename();will(returnValue("known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("known_host");

                one(sftpConfiguration).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(sftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(sftpConfiguration).getPrivateKeyFilename(); will(returnValue("private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("private.key");

                one(sftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(sftpConfiguration).getUsername(); will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                one(sftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);
            }
        });

        // Test
        ((PayloadBasedSftpConsumerFactory)this.payloadBasedSftpConsumerFactory).setDirectoryURLFactory(this.directoryURLFactory);
        Consumer<?> createdConsumer = this.payloadBasedSftpConsumerFactory.createEndpoint(this.sftpConfiguration);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedSftpConsumer);
        Assert.assertNull(((PayloadBasedSftpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test create consumer invocation.
     * 
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createConsumer_with_alternate_connection() throws ResourceException
    {
        final SftpConsumerAlternateConfiguration mockAlternateConfig = this.mockery.mock(SftpConsumerAlternateConfiguration.class, "alternateConfig");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(mockAlternateConfig).validate();

                one(mockAlternateConfig).setSourceDirectoryURLFactory(null);

                exactly(2).of(mockAlternateConfig).getClientID(); will(returnValue("clientID"));
                exactly(2).of(sftpConnectionSpec).setClientID("clientID");

                one(mockAlternateConfig).getRemoteHost(); will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");
                one(mockAlternateConfig).getAlternateRemoteHost(); will(returnValue("alternate.hostname"));
                one(sftpConnectionSpec).setRemoteHostname("alternate.hostname");

                one(mockAlternateConfig).getKnownHostsFilename();will(returnValue("known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("known_host");
                one(mockAlternateConfig).getAlternateKnownHostsFilename();will(returnValue("alternate.known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("alternate.known_host");

                one(mockAlternateConfig).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));
                one(mockAlternateConfig).getAlternateMaxRetryAttempts(); will(returnValue(Integer.valueOf(5)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(5));

                one(mockAlternateConfig).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));
                one(mockAlternateConfig).getAlternateRemotePort(); will(returnValue(Integer.valueOf(20)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(20));

                one(mockAlternateConfig).getPrivateKeyFilename(); will(returnValue("private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("private.key");
                one(mockAlternateConfig).getAlternatePrivateKeyFilename(); will(returnValue("alternate.private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("alternate.private.key");

                one(mockAlternateConfig).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));
                one(mockAlternateConfig).getAlternateConnectionTimeout(); will(returnValue(Integer.valueOf(235)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(235));

                one(mockAlternateConfig).getUsername(); will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");
                one(mockAlternateConfig).getAlternateUsername(); will(returnValue("alternate.username"));
                one(sftpConnectionSpec).setUsername("alternate.username");

                exactly(2).of(mockAlternateConfig).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);
            }
        });

        // Test
        Consumer<?> createdConsumer = this.payloadBasedSftpConsumerFactory.createEndpoint(mockAlternateConfig);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedSftpConsumer);
        Assert.assertNotNull(((PayloadBasedSftpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test PayloadBasedSftpConsumerFactory instance to allow us to return a mock 
     * instance of the ConnectionSpec.
     * 
     * @author Ikasan Development Team
     *
     */
    private class PayloadBasedSftpConsumerFactoryWithMockSpec extends PayloadBasedSftpConsumerFactory
    {

        public PayloadBasedSftpConsumerFactoryWithMockSpec(ConnectionFactory connectionFactory)
        {
            super(connectionFactory);
        }

        @Override
        protected SFTPConnectionSpec getConnectionSpec()
        {
            return PayloadBasedSftpConsumerFactoryTest.this.sftpConnectionSpec;
        }
    }
    
}
