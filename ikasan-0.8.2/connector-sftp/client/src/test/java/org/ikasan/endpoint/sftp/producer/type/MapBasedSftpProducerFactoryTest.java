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
package org.ikasan.endpoint.sftp.producer.type;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import junit.framework.Assert;

import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.endpoint.sftp.producer.SftpProducerAlternateConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.endpoint.sftp.producer.type.MapBasedSftpProducer;
import org.ikasan.endpoint.sftp.producer.type.MapBasedSftpProducerFactory;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link MapBasedSftpProducerFactory}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class MapBasedSftpProducerFactoryTest
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

    /** Mock SFTPConnectionSpec */
    private final SFTPConnectionSpec sftpConnectionSpec = this.mockery.mock(SFTPConnectionSpec.class, "mockFTPConnectionSpec");

    /** Instance on test */
    private EndpointFactory<Producer<?>, SftpProducerConfiguration> mapBasedFtpProducerFactory = new MapBasedSftpProducerFactoryWithMockSpec(this.connectionFactory);

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new MapBasedSftpProducerFactory(null);
    }

    /**
     * Test create producer invocation.
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createProducer() throws ResourceException
    {
        /** Mock sftpConfiguration */
        final SftpProducerConfiguration sftpConfiguration = this.mockery.mock(SftpProducerConfiguration.class, "mockSftpConfiguration");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(sftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(sftpConnectionSpec).setClientID("clientID");

                one(sftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");

                one(sftpConfiguration).getMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(sftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(sftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(sftpConfiguration).getUsername();will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                one(sftpConfiguration).getPrivateKeyFilename();will(returnValue("private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("private.key");

                one(sftpConfiguration).getKnownHostsFilename();will(returnValue("known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("known_host");

                one(sftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);
            }
        });

        // Test
        Producer<?> createdProducer = this.mapBasedFtpProducerFactory.createEndpoint(sftpConfiguration);
        Assert.assertTrue(createdProducer instanceof MapBasedSftpProducer);
        Assert.assertNull(((MapBasedSftpProducer)createdProducer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test create producer invocation.
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createProducer_with_alternate_connection_details() throws ResourceException
    {
        /** Mock sftpConfiguration */
        final SftpProducerAlternateConfiguration sftpConfiguration = this.mockery.mock(SftpProducerAlternateConfiguration.class, "mockSftpConfiguration");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                exactly(2).of(sftpConfiguration).getClientID(); will(returnValue("clientID"));
                exactly(2).of(sftpConnectionSpec).setClientID("clientID");

                one(sftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");

                one(sftpConfiguration).getAlternateRemoteHost(); will(returnValue("alternate.hostname"));
                one(sftpConnectionSpec).setRemoteHostname("alternate.hostname");

                one(sftpConfiguration).getMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(sftpConfiguration).getAlternateMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(sftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(sftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(sftpConfiguration).getAlternateRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(sftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(sftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(sftpConfiguration).getAlternateConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(sftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(sftpConfiguration).getUsername();will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                one(sftpConfiguration).getAlternateUsername();will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                one(sftpConfiguration).getKnownHostsFilename();will(returnValue("known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("known_host");

                one(sftpConfiguration).getAlternateKnownHostsFilename();will(returnValue("alternate.known_host"));
                one(sftpConnectionSpec).setKnownHostsFilename("alternate.known_host");

                one(sftpConfiguration).getPrivateKeyFilename();will(returnValue("private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("private.key");

                one(sftpConfiguration).getAlternatePrivateKeyFilename();will(returnValue("alternate.private.key"));
                one(sftpConnectionSpec).setPrivateKeyFilename("alternate.private.key");

                exactly(2).of(sftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                exactly(2).of(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

            }
        });

        // Test
        Producer<?> createdProducer = this.mapBasedFtpProducerFactory.createEndpoint(sftpConfiguration);
        Assert.assertTrue(createdProducer instanceof MapBasedSftpProducer);
        Assert.assertNotNull(((MapBasedSftpProducer)createdProducer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test MapBasedSftpProducerFactory instance to allow us to return a mock 
     * instance of the ConnectionSpec.
     * @author Ikasan Development Team
     *
     */
    private class MapBasedSftpProducerFactoryWithMockSpec extends MapBasedSftpProducerFactory
    {

        public MapBasedSftpProducerFactoryWithMockSpec(ConnectionFactory connectionFactory)
        {
            super(connectionFactory);
        }

        @Override
        protected SFTPConnectionSpec getConnectionSpec()
        {
            return MapBasedSftpProducerFactoryTest.this.sftpConnectionSpec;
        }
    }
}
