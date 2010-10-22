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
package org.ikasan.connector.sftp.producer;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import junit.framework.Assert;

import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.Producer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link SftpProducerEndpointManager}
 * 
 * @author Ikasan Development Team
 *
 */
public class SftpProducerEndpointManagerTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock connectionFactory */
    final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");
    
    /** mock sftpConfiguration */
    final SftpProducerConfiguration sftpConfiguration = mockery.mock(SftpProducerConfiguration.class, "mockSftpConfiguration");

    /** mock SFTPConnectionSpec */
    final SFTPConnectionSpec sftpConnectionSpec = mockery.mock(SFTPConnectionSpec.class, "mockSFTPConnectionSpec");

    /** mock producer */
    final Producer<?> producer = mockery.mock(Producer.class, "mockProducer");

    /** mock producerEndpointActivator */
    final ProducerEndpointActivator producerEndpointActivator = mockery.mock(ProducerEndpointActivator.class, "mockProducerEndpointActivator");

    /** instance on test */
    SftpProducerEndpointManager sftpProducerEndpointManager;

    /**
     * Test failed constructor due to null connectionFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConnectionFactory()
    {
        new SftpProducerEndpointManager(null, null);
    }

    /**
     * Test failed constructor due to null sftpConfiguration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSftpConfiguration()
    {
        new SftpProducerEndpointManager(connectionFactory, null);
    }

    /**
     * Create a clean test instance prior to each test.
     */
    @Before
    public void setUp()
    {
        this.sftpProducerEndpointManager = new SftpProducerEndpointManagerWithMockedSpecAndProducer(connectionFactory, sftpConfiguration);
    }
    
    /**
     * Test configuration mutator.
     */
    @Test
    public void test_sftpEndpointManager_configuration_mutator()
    {
        // test getConfiguration
        Assert.assertTrue("sftpConfiguration", sftpProducerEndpointManager.getConfiguration().equals(sftpConfiguration));

        // test setConfiguration
        sftpProducerEndpointManager.setConfiguration(null);
        Assert.assertNull("sftpConfiguration", sftpProducerEndpointManager.getConfiguration());
        sftpProducerEndpointManager.setConfiguration(sftpConfiguration);
        Assert.assertTrue("sftpConfiguration", sftpProducerEndpointManager.getConfiguration().equals(sftpConfiguration));
    }

    /**
     * Test start invocation.
     * @throws ResourceException 
     */
    @Test
    public void test_sftpEndpointManager_start() throws ResourceException
    {
        // common expectations
        setConfigurationAndSpecExpectations();
        
        // test
        sftpProducerEndpointManager.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test start on producer which implements the EndpointActivator.
     * @throws ResourceException 
     */
    @Test
    public void test_sftpEndpointManager_start_for_producer_which_implements_EndpointActivator() throws ResourceException
    {
        this.sftpProducerEndpointManager = new SftpProducerEndpointManagerWithMockedSpecAndProducerEndpointActivator(connectionFactory, sftpConfiguration);

        // common expectations
        setConfigurationAndSpecExpectations();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(producerEndpointActivator).activate();
            }
        });
        
        // test
        sftpProducerEndpointManager.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test getEndpoint returns a producer which is SftpMapProducer.
     * @throws ResourceException 
     */
    @Test
    public void test_sftpEndpointManager_getEndpoint() throws ResourceException
    {
        this.sftpProducerEndpointManager = new SftpEndpointManagerWithMockedSpec(connectionFactory, sftpConfiguration);

        // common expectations
        setConfigurationAndSpecExpectations();
        
        // setup
        sftpProducerEndpointManager.start();

        // test
        Assert.assertTrue("Producer is an SftpMapProducer", sftpProducerEndpointManager.getEndpoint() instanceof SftpMapProducer);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test stop invocation.
     * @throws ResourceException 
     */
    @Test
    public void test_sftpEndpointManager_stop() throws ResourceException
    {
        // common expectations
        setConfigurationAndSpecExpectations();
        
        // setup
        sftpProducerEndpointManager.start();

        // test
        sftpProducerEndpointManager.stop();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test stop invocation.
     * @throws ResourceException 
     */
    @Test
    public void test_sftpEndpointManager_stop_for_producer_which_implements_EndpointActivator() throws ResourceException
    {
        this.sftpProducerEndpointManager = new SftpProducerEndpointManagerWithMockedSpecAndProducerEndpointActivator(connectionFactory, sftpConfiguration);

        // common expectations
        setConfigurationAndSpecExpectations();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(producerEndpointActivator).activate();
                exactly(1).of(producerEndpointActivator).deactivate();
            }
        });
        
        // setup
        sftpProducerEndpointManager.start();

        // test
        sftpProducerEndpointManager.stop();
        mockery.assertIsSatisfied();
    }
    
    private void setConfigurationAndSpecExpectations()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sftpConfiguration).getClientID();
                will(returnValue("clientID"));
                one(sftpConnectionSpec).setClientID("clientID");

                exactly(1).of(sftpConfiguration).getRemoteHost();
                will(returnValue("hostname"));
                one(sftpConnectionSpec).setRemoteHostname("hostname");

                exactly(1).of(sftpConfiguration).getKnownHostsFilename();
                will(returnValue("knownhosts"));
                one(sftpConnectionSpec).setKnownHostsFilename("knownhosts");

                exactly(1).of(sftpConfiguration).getMaxRetryAttempts();
                will(returnValue(1));
                one(sftpConnectionSpec).setMaxRetryAttempts(1);

                exactly(1).of(sftpConfiguration).getRemotePort();
                will(returnValue(23));
                one(sftpConnectionSpec).setRemotePort(23);

                exactly(1).of(sftpConfiguration).getPrivateKeyFilename();
                will(returnValue("PrivateKeyFilename"));
                one(sftpConnectionSpec).setPrivateKeyFilename("PrivateKeyFilename");

                exactly(1).of(sftpConfiguration).getConnectionTimeout();
                will(returnValue(234));
                one(sftpConnectionSpec).setConnectionTimeout(234);

                exactly(1).of(sftpConfiguration).getUsername();
                will(returnValue("username"));
                one(sftpConnectionSpec).setUsername("username");

                exactly(1).of(sftpConfiguration).getCleanupJournalOnComplete();
                will(returnValue(Boolean.TRUE));
                one(sftpConnectionSpec).setCleanupJournalOnComplete(Boolean.TRUE);
            }
        });
    }
    
    /**
     * Test SftpEndpointManager instance to allow us to provide mock instances of 
     * classes created inside the life of the manager.
     * @author Ikasan Development Team
     *
     */
    private class SftpEndpointManagerWithMockedSpec extends SftpProducerEndpointManager
    {

        public SftpEndpointManagerWithMockedSpec(ConnectionFactory connectionFactory, SftpProducerConfiguration sftpConfiguration)
        {
            super(connectionFactory, sftpConfiguration);
        }

        @Override
        protected SFTPConnectionSpec getConnectionSpec()
        {
            return sftpConnectionSpec;
        }
    }
    
    /**
     * Test SftpEndpointManager instance to allow us to provide mock instances of 
     * classes created inside the life of the manager.
     * @author Ikasan Development Team
     *
     */
    private class SftpProducerEndpointManagerWithMockedSpecAndProducer extends SftpProducerEndpointManager
    {

        public SftpProducerEndpointManagerWithMockedSpecAndProducer(ConnectionFactory connectionFactory, SftpProducerConfiguration sftpConfiguration)
        {
            super(connectionFactory, sftpConfiguration);
        }

        @Override
        protected SFTPConnectionSpec getConnectionSpec()
        {
            return sftpConnectionSpec;
        }

        @Override
        protected Producer<?> getProducer(ConnectionSpec spec)
        {
            return producer;
        }
    }
    
    /**
     * Test SftpEndpointManager instance to allow us to provide mock instances of 
     * classes created inside the life of the manager.
     * @author Ikasan Development Team
     *
     */
    private class SftpProducerEndpointManagerWithMockedSpecAndProducerEndpointActivator extends SftpProducerEndpointManagerWithMockedSpecAndProducer
    {

        public SftpProducerEndpointManagerWithMockedSpecAndProducerEndpointActivator(ConnectionFactory connectionFactory, SftpProducerConfiguration sftpConfiguration)
        {
            super(connectionFactory, sftpConfiguration);
        }

        @Override
        protected Producer<?> getProducer(ConnectionSpec spec)
        {
            return producerEndpointActivator;
        }
    }
    
    /**
     * Implementation of a producer implementing the EndpointActivator
     * @author Ikasan Development Team
     *
     */
    private class ProducerEndpointActivator implements Producer, EndpointActivator
    {

        public void activate() throws ResourceException
        {
            // dont care - class is for testing implementing interface only
        }

        public void deactivate() throws ResourceException
        {
            // dont care - class is for testing implementing interface only
        }

        public void invoke(Object deliverable) throws ResourceException
        {
            // dont care - class is for testing implementing interface only
        }
        
    }

}
