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
package org.ikasan.endpoint.sftp.producer;

import javax.resource.ResourceException;

import junit.framework.Assert;

import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerEndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.Producer;
import org.ikasan.spec.endpoint.EndpointFactory;
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

    /** mock producerFactory */
    final EndpointFactory producerFactory = mockery.mock(EndpointFactory.class,
            "mockProducerFactory");

    /** mock sftpConfiguration */
    final SftpProducerConfiguration sftpConfiguration = mockery.mock(
            SftpProducerConfiguration.class, "mockSftpConfiguration");

    /** mock producer */
    final Producer<?> producer = mockery.mock(Producer.class, "mockProducer");

    /** mock producerEndpointActivator */
    final ProducerEndpointActivator producerWithEndpointActivator = mockery.mock(
            ProducerEndpointActivator.class, "mockProducerEndpointActivator");

    /** instance on test */
    SftpProducerEndpointManager sftpProducerEndpointManager;

    /**
     * Test failed constructor due to null producerFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullProducerFactory()
    {
        new SftpProducerEndpointManager(null, null);
    }

    /**
     * Test failed constructor due to null sftpConfiguration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullSftpConfiguration()
    {
        new SftpProducerEndpointManager(producerFactory, null);
    }

    /**
     * Create a clean test instance prior to each test.
     */
    @Before
    public void setUp()
    {
        this.sftpProducerEndpointManager = new SftpProducerEndpointManager(
                producerFactory, sftpConfiguration);
    }

    /**
     * Test configuration mutator.
     */
    @Test
    public void test_sftpEndpointManager_configuration_mutator()
    {
        // test getConfiguration
        Assert.assertTrue("sftpConfiguration", sftpProducerEndpointManager
                .getConfiguration().equals(sftpConfiguration));

        // test setConfiguration
        sftpProducerEndpointManager.setConfiguration(null);
        Assert.assertNull("sftpConfiguration",
                sftpProducerEndpointManager.getConfiguration());
        sftpProducerEndpointManager.setConfiguration(sftpConfiguration);
        Assert.assertTrue("sftpConfiguration", sftpProducerEndpointManager
                .getConfiguration().equals(sftpConfiguration));
    }

    /**
     * Test start and stop invocations for an endpoint without the EndpointActivator contract.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_sftpEndpointManager_start_and_stop() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(producerFactory).createEndpoint(sftpConfiguration);
                will(returnValue(producer));
            }
        });

        // test
        sftpProducerEndpointManager.start();
        sftpProducerEndpointManager.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Test start and stop invocations for an endpoint with the EndpointActivator contract.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_sftpEndpointManager_start_and_stop_with_endpointActivator() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(producerFactory).createEndpoint(sftpConfiguration);
                will(returnValue(producerWithEndpointActivator));

                exactly(1).of(producerWithEndpointActivator).activate();
                exactly(1).of(producerWithEndpointActivator).deactivate();
            }
        });

        // test
        sftpProducerEndpointManager.start();
        sftpProducerEndpointManager.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Test getEndpoint invocation.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_sftpEndpointManager_getEndpoint() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(producerFactory).createEndpoint(sftpConfiguration);
                will(returnValue(producer));
            }
        });

        // test
        sftpProducerEndpointManager.start();
        Assert.assertEquals(producer, sftpProducerEndpointManager.getEndpoint());
        mockery.assertIsSatisfied();
    }

    /**
     * Implementation of a producer implementing the EndpointActivator
     * 
     * @author Ikasan Development Team
     * 
     */
    private class ProducerEndpointActivator implements Producer,
            EndpointActivator
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
