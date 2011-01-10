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

import javax.resource.cci.ConnectionFactory;

import junit.framework.Assert;

import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link MapBasedFtpProducerFactory}
 * 
 * @author Ikasan Development Team
 *
 */
public class MapBasedFtpProducerFactoryTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock connectionFactory */
    final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");
    
    /** mock ftpConfiguration */
    final FtpProducerConfiguration ftpConfiguration = mockery.mock(FtpProducerConfiguration.class, "mockFtpConfiguration");

    /** mock SFTPConnectionSpec */
    final FTPConnectionSpec ftpConnectionSpec = mockery.mock(FTPConnectionSpec.class, "mockFTPConnectionSpec");

    /** mock producer */
    final Producer<?> producer = mockery.mock(Producer.class, "mockProducer");

    /** instance on test */
    MapBasedFtpProducerFactory mapBasedFtpProducerFactory;

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new MapBasedFtpProducerFactory(null);
    }

    /**
     * Create a clean test instance prior to each test.
     */
    @Before
    public void setUp()
    {
        this.mapBasedFtpProducerFactory = new MapBasedFtpProducerFactoryWithMockSpec(connectionFactory);
    }
    
    /**
     * Test create producer invocation.
     */
    @Test
    public void test_createProducer()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                one(ftpConfiguration).validate();

                exactly(1).of(ftpConfiguration).getClientID();
                will(returnValue("clientID"));
                one(ftpConnectionSpec).setClientID("clientID");

                exactly(1).of(ftpConfiguration).getRemoteHost();
                will(returnValue("hostname"));
                one(ftpConnectionSpec).setRemoteHostname("hostname");

                exactly(1).of(ftpConfiguration).getMaxRetryAttempts();
                will(returnValue(1));
                one(ftpConnectionSpec).setMaxRetryAttempts(1);

                exactly(1).of(ftpConfiguration).getRemotePort();
                will(returnValue(23));
                one(ftpConnectionSpec).setRemotePort(23);

                exactly(1).of(ftpConfiguration).getConnectionTimeout();
                will(returnValue(234));
                one(ftpConnectionSpec).setConnectionTimeout(234);

                one(ftpConfiguration).getSocketTimeout(); will(returnValue(234));
                one(ftpConnectionSpec).setSocketTimeout(234);

                one(ftpConfiguration).getDataTimeout(); will(returnValue(234));
                one(ftpConnectionSpec).setDataTimeout(234);

                one(ftpConfiguration).getActive();will(returnValue(Boolean.FALSE));
                one(ftpConnectionSpec).setActive(Boolean.FALSE);

                exactly(1).of(ftpConfiguration).getUsername();
                will(returnValue("username"));
                one(ftpConnectionSpec).setUsername("username");

                one(ftpConfiguration).getPassword();will(returnValue("password"));
                one(ftpConnectionSpec).setPassword("password");

                exactly(1).of(ftpConfiguration).getCleanupJournalOnComplete();
                will(returnValue(Boolean.TRUE));
                one(ftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);

                one(ftpConfiguration).getSystemKey();will(returnValue(""));
                one(ftpConnectionSpec).setSystemKey("");
            }
        });
                
        // test
        Assert.assertTrue(mapBasedFtpProducerFactory.createEndpoint(ftpConfiguration) instanceof MapBasedFtpProducer);
        mockery.assertIsSatisfied();
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
            return ftpConnectionSpec;
        }
    }
    
}
