/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.messagedriven.jca.jboss;

import javax.jms.Session;
import javax.resource.spi.ResourceAdapter;

import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.core.flow.Flow;
import org.ikasan.framework.event.serialisation.JmsMessageEventSerialiser;
import org.ikasan.framework.initiator.messagedriven.jca.JmsMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.RawMessageDrivenInitiator;
import org.ikasan.framework.initiator.messagedriven.jca.spring.JmsMessageEndpointManager;
import org.ikasan.framework.initiator.messagedriven.jca.spring.JtaTransactionManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;

/**
 * This test class supports the <code>JBossJmsEndpointManagerFactoryBean</code> class.
 * 
 * @author Ikasan Development Team
 */
public class JBossJmsEndpointManagerFactoryBeanTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mock objects
     */

    // Spring Flow
    private final Flow flow = mockery.mock(Flow.class);
    
    // Spring Initiator
    private final JmsMessageDrivenInitiator initiator = mockery.mock(JmsMessageDrivenInitiator.class);
    
    /**
     * Test successful creation of an endpoint manager instance.
     * @throws Exception 
     */
//    @Test
    public void test_successul_creation_endpointManager() 
        throws Exception
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).getName();
                will(returnValue("flowName"));
            }
        });
        
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setDestinationName("anyDestinationName");
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setModuleName("moduleName");
        endpointManagerFactoryBean.setFlow(flow);
        endpointManagerFactoryBean.setInitiator(initiator);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed creation of an endpoint manager instance due to missing
     * mandatory destinationName attribute.
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_creation_endpointManager_missing_DestinationName() 
        throws Exception
    {
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setModuleName("moduleName");
        endpointManagerFactoryBean.setFlow(flow);
        endpointManagerFactoryBean.setInitiator(initiator);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test failed creation of an endpoint manager instance due to missing
     * mandatory moduleName attribute.
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_creation_endpointManager_missing_ModuleName() 
        throws Exception
    {
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setDestinationName("anyDestinationName");
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setFlow(flow);
        endpointManagerFactoryBean.setInitiator(initiator);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed creation of an endpoint manager instance due to missing
     * mandatory name attribute.
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_creation_endpointManager_missing_Name() 
        throws Exception
    {
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setDestinationName("anyDestinationName");
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setFlow(flow);
        endpointManagerFactoryBean.setInitiator(initiator);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed creation of an endpoint manager instance due to missing
     * mandatory name attribute.
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_creation_endpointManager_missing_Flow() 
        throws Exception
    {
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setDestinationName("anyDestinationName");
        endpointManagerFactoryBean.setModuleName("moduleName");
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setInitiator(initiator);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed creation of an endpoint manager instance due to missing
     * mandatory name attribute.
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_creation_endpointManager_missing_Initiator() 
        throws Exception
    {
        //
        // run test
        JBossJmsEndpointManagerFactoryBean endpointManagerFactoryBean = new JBossJmsEndpointManagerFactoryBean();
        endpointManagerFactoryBean.setDestinationName("anyDestinationName");
        endpointManagerFactoryBean.setModuleName("moduleName");
        endpointManagerFactoryBean.setName("name");
        endpointManagerFactoryBean.setFlow(flow);
        JmsMessageEndpointManager jmsMessageEndpointManager = (JmsMessageEndpointManager)endpointManagerFactoryBean.getObject();
        
        //
        // everything happy?
        mockery.assertIsSatisfied();
    }}
