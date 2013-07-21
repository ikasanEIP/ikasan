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
package org.ikasan.spec.endpoint;

import javax.resource.ResourceException;

import junit.framework.Assert;

import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.Producer;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DefaultEndpointManager}
 * 
 * @author Ikasan Development Team
 * 
 */
public class DefaultEndpointManagerTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock endpointFactory */
    final EndpointFactory endpointFactory = mockery.mock(EndpointFactory.class,
            "mockEndpointFactory");

    /** mock producer - could just as well have ben a broker or consumer! */
    final Producer<?> producer = mockery.mock(Producer.class, "mockProducer");

    /** mock producerEndpointActivator */
    final ProducerEndpointActivator producerWithEndpointActivator = mockery.mock(
            ProducerEndpointActivator.class, "mockProducerEndpointActivator");

    /** mock configuration */
    final ExampleConfiguration exampleConfiguration = mockery.mock(ExampleConfiguration.class, "mockConfiguration");
    
    /** instance on test */
    DefaultEndpointManager<Producer,ExampleConfiguration> defaultEndpointManager;

    /**
     * Test failed constructor due to null endpointFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullFactory()
    {
        new DefaultEndpointManager(null, null);
    }

    /**
     * Test failed constructor due to null configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructor_nullConfiguration()
    {
        new DefaultEndpointManager(endpointFactory, null);
    }

    /**
     * Create a clean test instance prior to each test.
     */
    @Before
    public void setUp()
    {
        this.defaultEndpointManager = new DefaultEndpointManager(
                endpointFactory, exampleConfiguration);
    }

    /**
     * Test configuration mutator.
     */
    @Test
    public void test_defaultEndpointManager_configuration_mutator()
    {
        // test getConfiguration
        Assert.assertTrue("Configuration", defaultEndpointManager
                .getConfiguration().equals(exampleConfiguration));

        // test setConfiguration
        defaultEndpointManager.setConfiguration(null);
        Assert.assertNull("Configuration", defaultEndpointManager.getConfiguration());
        
        defaultEndpointManager.setConfiguration(exampleConfiguration);
        Assert.assertTrue("Configuration", defaultEndpointManager
                .getConfiguration().equals(exampleConfiguration));
    }

    /**
     * Test start and stop invocations for an endpoint without the EndpointActivator contract.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_defaultEndpointManager_start_and_stop() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(endpointFactory).createEndpoint(exampleConfiguration);
                will(returnValue(producer));
            }
        });

        // test
        defaultEndpointManager.start();
        defaultEndpointManager.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Test start and stop invocations for an endpoint with the EndpointActivator contract.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_defaultEndpointManager_start_and_stop_with_endpointActivator() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(endpointFactory).createEndpoint(exampleConfiguration);
                will(returnValue(producerWithEndpointActivator));

                exactly(1).of(producerWithEndpointActivator).activate();
                exactly(1).of(producerWithEndpointActivator).deactivate();
            }
        });

        // test
        defaultEndpointManager.start();
        defaultEndpointManager.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Test getEndpoint invocation.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_defaultEndpointManager_getEndpoint() throws ResourceException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(endpointFactory).createEndpoint(exampleConfiguration);
                will(returnValue(producer));
            }
        });

        // test
        defaultEndpointManager.start();
        Assert.assertEquals(producer, defaultEndpointManager.getEndpoint());
        mockery.assertIsSatisfied();
    }

    /**
     * Use an example configuration for test cases.
     * @author Ikasan Development Team
     *
     */
    private class ExampleConfiguration
    {
        // test configuration class
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
