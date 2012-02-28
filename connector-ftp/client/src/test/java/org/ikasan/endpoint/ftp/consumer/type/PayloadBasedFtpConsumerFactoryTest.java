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
package org.ikasan.endpoint.ftp.consumer.type;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.InvalidPropertyException;

import junit.framework.Assert;

import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerAlternateConfiguration;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.consumer.type.PayloadBasedFtpConsumer;
import org.ikasan.endpoint.ftp.consumer.type.PayloadBasedFtpConsumerFactory;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test class for {@link PayloadBasedFtpConsumerFactory}
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
public class PayloadBasedFtpConsumerFactoryTest
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
    
    /** Mock ftpConfiguration */
    private final FtpConsumerConfiguration ftpConfiguration = this.mockery.mock(FtpConsumerConfiguration.class, "mockFtpConfiguration");

    /** mockFTPConnectionSpec */
    private final FTPConnectionSpec ftpConnectionSpec = this.mockery.mock(FTPConnectionSpec.class, "mockFTPConnectionSpec");

    /** Mock DirectoryURLFactory */
    private final DirectoryURLFactory directoryURLFactory = this.mockery.mock(DirectoryURLFactory.class, "mockDirectoryURLFactory");

    /** Instance on test */
    private EndpointFactory<Consumer<?>, FtpConsumerConfiguration> payloadBasedFtpConsumerFactory = new PayloadBasedFtpConsumerFactoryWithMockSpec(this.connectionFactory);

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new PayloadBasedFtpConsumerFactory(null);
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
                one(ftpConfiguration).validate();

                one(ftpConfiguration).setSourceDirectoryURLFactory(null);

                one(ftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(ftpConnectionSpec).setClientID("clientID");

                one(ftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");

                one(ftpConfiguration).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(ftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(ftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getUsername(); will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(ftpConfiguration).getActive(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setActive(Boolean.TRUE);

                one(ftpConfiguration).getDataTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(100));

                one(ftpConfiguration).getSocketTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(100));

                one(ftpConfiguration).getPassword(); will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                one(ftpConfiguration).getSystemKey(); will(returnValue("systemKey"));
                one(ftpConnectionSpec).setSystemKey("systemKey");
            }
        });

        // Test
        Consumer<?> createdConsumer = this.payloadBasedFtpConsumerFactory.createEndpoint(this.ftpConfiguration);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedFtpConsumer);
        Assert.assertNull(((PayloadBasedFtpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Creating a consumer endpoint fails as {@link FtpConsumerConfiguration} instance
     * was invalid
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
                one(ftpConfiguration).validate();will(throwException(exception));
            }
        });

        // Test
        this.payloadBasedFtpConsumerFactory.createEndpoint(this.ftpConfiguration);
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
                one(ftpConfiguration).validate();

                one(ftpConfiguration).setSourceDirectoryURLFactory(directoryURLFactory);

                one(ftpConfiguration).getClientID(); will(returnValue("clientID"));
                one(ftpConnectionSpec).setClientID("clientID");

                one(ftpConfiguration).getRemoteHost(); will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");

                one(ftpConfiguration).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));

                one(ftpConfiguration).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));

                one(ftpConfiguration).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));

                one(ftpConfiguration).getUsername(); will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(ftpConfiguration).getActive(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setActive(Boolean.TRUE);

                one(ftpConfiguration).getDataTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(100));

                one(ftpConfiguration).getSocketTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(100));

                one(ftpConfiguration).getPassword(); will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                one(ftpConfiguration).getSystemKey(); will(returnValue("systemKey"));
                one(ftpConnectionSpec).setSystemKey("systemKey");
            }
        });

        // Test
        ((PayloadBasedFtpConsumerFactory)this.payloadBasedFtpConsumerFactory).setDirectoryURLFactory(this.directoryURLFactory);
        Consumer<?> createdConsumer = this.payloadBasedFtpConsumerFactory.createEndpoint(this.ftpConfiguration);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedFtpConsumer);
        Assert.assertNull(((PayloadBasedFtpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
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
        final FtpConsumerAlternateConfiguration mockAlternateConfig = this.mockery.mock(FtpConsumerAlternateConfiguration.class, "alternateConfig");

        // Expectations
        this.mockery.checking(new Expectations()
        {
            {
                one(mockAlternateConfig).validate();

                one(mockAlternateConfig).setSourceDirectoryURLFactory(null);

                exactly(2).of(mockAlternateConfig).getClientID(); will(returnValue("clientID"));
                exactly(2).of(ftpConnectionSpec).setClientID("clientID");

                one(mockAlternateConfig).getRemoteHost(); will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");
                one(mockAlternateConfig).getAlternateRemoteHost(); will(returnValue("alternate.hostname"));
                one(ftpConnectionSpec).setRemoteHostname("alternate.hostname");

                one(mockAlternateConfig).getMaxRetryAttempts(); will(returnValue(Integer.valueOf(1)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(1));
                one(mockAlternateConfig).getAlternateMaxRetryAttempts(); will(returnValue(Integer.valueOf(3)));
                one(ftpConnectionSpec).setMaxRetryAttempts(Integer.valueOf(3));

                one(mockAlternateConfig).getRemotePort(); will(returnValue(Integer.valueOf(23)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(23));
                one(mockAlternateConfig).getAlternateRemotePort(); will(returnValue(Integer.valueOf(21)));
                one(ftpConnectionSpec).setRemotePort(Integer.valueOf(21));

                one(mockAlternateConfig).getConnectionTimeout(); will(returnValue(Integer.valueOf(234)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(234));
                one(mockAlternateConfig).getAlternateConnectionTimeout(); will(returnValue(Integer.valueOf(235)));
                one(ftpConnectionSpec).setConnectionTimeout(Integer.valueOf(235));

                one(mockAlternateConfig).getUsername(); will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");
                one(mockAlternateConfig).getAlternateUsername(); will(returnValue("altetrnateUsername"));
                one(ftpConnectionSpec).setUsername("altetrnateUsername");

                exactly(2).of(mockAlternateConfig).getCleanupJournalOnComplete(); will(returnValue(Boolean.TRUE));
                exactly(2).of(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(mockAlternateConfig).getActive(); will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setActive(Boolean.TRUE);
                one(mockAlternateConfig).getAlternateActive(); will(returnValue(Boolean.FALSE));
                one(ftpConnectionSpec).setActive(Boolean.FALSE);

                one(mockAlternateConfig).getDataTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(100));
                one(mockAlternateConfig).getAlternateDataTimeout(); will(returnValue(Integer.valueOf(200)));
                one(ftpConnectionSpec).setDataTimeout(Integer.valueOf(200));

                one(mockAlternateConfig).getSocketTimeout(); will(returnValue(Integer.valueOf(100)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(100));
                one(mockAlternateConfig).getAlternateSocketTimeout(); will(returnValue(Integer.valueOf(500)));
                one(ftpConnectionSpec).setSocketTimeout(Integer.valueOf(500));

                one(mockAlternateConfig).getPassword(); will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");
                one(mockAlternateConfig).getAlternatePassword(); will(returnValue("alternatePassword"));
                one(ftpConnectionSpec).setPassword("alternatePassword");

                one(mockAlternateConfig).getSystemKey(); will(returnValue("systemKey"));
                one(ftpConnectionSpec).setSystemKey("systemKey");
                one(mockAlternateConfig).getAlternateSystemKey(); will(returnValue("alternateSystemKey"));
                one(ftpConnectionSpec).setSystemKey("alternateSystemKey");
            }
        });

        // Test
        Consumer<?> createdConsumer = this.payloadBasedFtpConsumerFactory.createEndpoint(mockAlternateConfig);
        Assert.assertTrue(createdConsumer instanceof PayloadBasedFtpConsumer);
        Assert.assertNotNull(((PayloadBasedFtpConsumer)createdConsumer).getAlternateFileTransferConnectionTemplate());
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test PayloadBasedSftpConsumerFactory instance to allow us to return a mock 
     * instance of the ConnectionSpec.
     * 
     * @author Ikasan Development Team
     *
     */
    private class PayloadBasedFtpConsumerFactoryWithMockSpec extends PayloadBasedFtpConsumerFactory
    {

        public PayloadBasedFtpConsumerFactoryWithMockSpec(ConnectionFactory connectionFactory)
        {
            super(connectionFactory);
        }

        @Override
        protected FTPConnectionSpec getConnectionSpec()
        {
            return PayloadBasedFtpConsumerFactoryTest.this.ftpConnectionSpec;
        }
    }
    
}
