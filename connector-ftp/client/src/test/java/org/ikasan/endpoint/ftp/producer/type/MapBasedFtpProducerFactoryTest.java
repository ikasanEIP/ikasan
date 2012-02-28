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
package org.ikasan.endpoint.ftp.producer.type;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import junit.framework.Assert;

import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.endpoint.ftp.producer.FtpProducerAlternateConfiguration;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link MapBasedFtpProducerFactory}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class MapBasedFtpProducerFactoryTest
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
    private final FTPConnectionSpec ftpConnectionSpec = this.mockery.mock(FTPConnectionSpec.class, "mockFTPConnectionSpec");

    /** Instance on test */
    private EndpointFactory<Producer<?>, FtpProducerConfiguration> mapBasedFtpProducerFactory = new MapBasedFtpProducerFactoryWithMockSpec(this.connectionFactory);

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new MapBasedFtpProducerFactory(null);
    }

    /**
     * Test create producer invocation.
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createProducer() throws ResourceException
    {
        /** Mock ftpConfiguration */
        final FtpProducerConfiguration ftpConfiguration = this.mockery.mock(FtpProducerConfiguration.class, "mockFtpConfiguration");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).validate();

                one(ftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(ftpConnectionSpec).setClientID("clientID");

                one(ftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");

                one(ftpConfiguration).getMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(ftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(ftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getSocketTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getDataTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getActive();will(returnValue(Boolean.FALSE));
                one(ftpConnectionSpec).setActive(Boolean.FALSE);

                one(ftpConfiguration).getUsername();will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getPassword();will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                one(ftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(ftpConfiguration).getSystemKey();will(returnValue(""));
                one(ftpConnectionSpec).setSystemKey("");
            }
        });

        // Test
        Producer<?> createdProducer = this.mapBasedFtpProducerFactory.createEndpoint(ftpConfiguration);
        Assert.assertTrue(createdProducer instanceof MapBasedFtpProducer);
        Assert.assertNull(((MapBasedFtpProducer)createdProducer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test create producer invocation.
     * @throws ResourceException if error creating endpoint
     */
    @Test
    public void test_createProducer_with_alternate_connection_details() throws ResourceException
    {
        /** Mock ftpConfiguration */
        final FtpProducerAlternateConfiguration ftpConfiguration = this.mockery.mock(FtpProducerAlternateConfiguration.class, "mockFtpConfiguration");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).validate();

                exactly(2).of(ftpConfiguration).getClientID(); will(returnValue("clientID"));
                exactly(2).of(ftpConnectionSpec).setClientID("clientID");

                one(ftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");

                one(ftpConfiguration).getAlternateRemoteHost(); will(returnValue("alternate.hostname"));
                one(ftpConnectionSpec).setRemoteHostname("alternate.hostname");

                one(ftpConfiguration).getMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(ftpConfiguration).getAlternateMaxRetryAttempts();will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(ftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(ftpConfiguration).getAlternateRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(ftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getAlternateConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getSocketTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getAlternateSocketTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getDataTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getAlternateDataTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getActive();will(returnValue(Boolean.FALSE));
                one(ftpConnectionSpec).setActive(Boolean.FALSE);

                one(ftpConfiguration).getAlternateActive();will(returnValue(Boolean.FALSE));
                one(ftpConnectionSpec).setActive(Boolean.FALSE);

                one(ftpConfiguration).getUsername();will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getAlternateUsername();will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getPassword();will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                one(ftpConfiguration).getAlternatePassword();will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                exactly(2).of(ftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                exactly(2).of(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(ftpConfiguration).getSystemKey();will(returnValue(""));
                one(ftpConnectionSpec).setSystemKey("");

                one(ftpConfiguration).getAlternateSystemKey();will(returnValue(""));
                one(ftpConnectionSpec).setSystemKey("");

                
            }
        });

        // Test
        Producer<?> createdProducer = this.mapBasedFtpProducerFactory.createEndpoint(ftpConfiguration);
        Assert.assertTrue(createdProducer instanceof MapBasedFtpProducer);
        Assert.assertNotNull(((MapBasedFtpProducer)createdProducer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test MapBasedSftpProducerFactory instance to allow us to return a mock 
     * instance of the ConnectionSpec.
     * @author Ikasan Development Team
     *
     */
    private class MapBasedFtpProducerFactoryWithMockSpec extends MapBasedFtpProducerFactory
    {

        public MapBasedFtpProducerFactoryWithMockSpec(ConnectionFactory connectionFactory)
        {
            super(connectionFactory);
        }

        @Override
        protected FTPConnectionSpec getConnectionSpec()
        {
            return MapBasedFtpProducerFactoryTest.this.ftpConnectionSpec;
        }
    }
    
}
