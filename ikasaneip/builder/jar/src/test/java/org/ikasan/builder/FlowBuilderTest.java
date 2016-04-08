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
package org.ikasan.builder;

import org.ikasan.exclusion.service.ExclusionServiceFactory;
import org.ikasan.flow.visitorPattern.invoker.*;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * This test class supports the <code>FlowBuilder</code> class.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class FlowBuilderTest
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
    
    // Endpoints  
    /** Mock Consumer */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");
    /** Mock Producer */
    final Producer producer = mockery.mock(Producer.class, "mockProducer");
    /** Mock Broker */
    final Broker broker = mockery.mock(Broker.class, "mockBroker");

    // Transformers
    /** Mock Translator */
    final Translator translator = mockery.mock(Translator.class, "mockTranslator");
    /** Mock Converter */
    final Converter converter = mockery.mock(Converter.class, "mockConverter");

    // Routers
    /** Mock Router */
    final Router router = mockery.mock(Router.class, "mockRouter");
    
    // Sequencers
    /** Mock Sequencer */
    final Sequencer sequencer = mockery.mock(Sequencer.class, "mockSequencingRouter");

    /** Context Listener */
    final FlowInvocationContextListener flowInvocationContextListener = mockery.mock(FlowInvocationContextListener.class, "flowInvocationContextListener");

    final ExclusionServiceFactory exclusionServiceFactory = mockery.mock(ExclusionServiceFactory.class, "exclusionServiceFactory");

    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "exclusionService");

    final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "serialiserFactory");

    @Before
    public void setup()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(consumer).setEventFactory(with(any(EventFactory.class)));
                oneOf(exclusionServiceFactory).getExclusionService("moduleName", "flowName");
                will(returnValue(exclusionService));
            }
        });
    }
    
    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_simple_transitions() 
    {
    	Flow flow = FlowBuilder.newFlow("flowName", "moduleName")
    	    .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
    	    .converter("converter", converter)
    	    .translater("translator", translator)
    	    .broker("broker", broker)
    	    .publisher("producer", producer)
    	    .build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
       	Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have two FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 2);

       	FlowElement fe = flowElements.get(0);
       	Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element invoker should be an instance of ConsumerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConsumerFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 1);
       	
       	fe = flowElements.get(1);
       	Assert.assertTrue("flow element name should be 'converter'", "converter".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Converter", fe.getFlowComponent() instanceof Converter);
        Assert.assertTrue("flow element invoker should be an instance of ConverterFlowElementInvoker", fe.getFlowElementInvoker() instanceof ConverterFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to translator", fe.getTransitions().size() == 1);
       	
       	fe = flowElements.get(2);
       	Assert.assertTrue("flow element name should be 'translator'", "translator".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Translator", fe.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element invoker should be an instance of TranslatorFlowElementInvoker", fe.getFlowElementInvoker() instanceof TranslatorFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to broker", fe.getTransitions().size() == 1);
       	
       	fe = flowElements.get(3);
       	Assert.assertTrue("flow element name should be 'broker'", "broker".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Broker", fe.getFlowComponent() instanceof Broker);
        Assert.assertTrue("flow element invoker should be an instance of BrokerFlowElementInvoker", fe.getFlowElementInvoker() instanceof BrokerFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to producer", fe.getTransitions().size() == 1);
       	
       	fe = flowElements.get(4);
       	Assert.assertTrue("flow element name should be 'producer'", "producer".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Producer", fe.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element invoker should be an instance of ProducerFlowElementInvoker", fe.getFlowElementInvoker() instanceof ProducerFlowElementInvoker);
    	Assert.assertTrue("flow element transition should be to 'null", fe.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_router_transitions() 
    {
    	Flow flow = FlowBuilder.newFlow("flowName", "moduleName")
    	.withDescription("flowDescription")
        .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
        .withExclusionServiceFactory(exclusionServiceFactory)
        .withSerialiserFactory(serialiserFactory)
        .consumer("consumer", consumer)
    	.router("router", router)
    		.when("result").translater("name1",translator).publisher("publisher1", producer)
    		.otherise().translater("name2", translator).publisher("publisher2", producer)
    	.build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
       	Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

       	FlowElement fe = flowElements.get(0);
       	Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
    	Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);
       	
    	fe = (FlowElement)fe.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Router", fe.getFlowComponent() instanceof Router);
    	Assert.assertTrue("flow element transition should be to coverter", fe.getTransitions().size() == 2);
       	
    	FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("result");
    	FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feRoute1.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Translator", feRoute1.getFlowComponent() instanceof Translator);
    	Assert.assertTrue("flow element transition should be to producer", feRoute1.getTransitions().size() == 1);
       	
       	feRoute1 = (FlowElement)feRoute1.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'publisher1'", "publisher1".equals(feRoute1.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Publisher", feRoute1.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be to producer", feRoute1.getTransitions().size() == 0);
       	
       	Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feRoute2.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Translator", feRoute2.getFlowComponent() instanceof Translator);
    	Assert.assertTrue("flow element transition should be to producer", feRoute2.getTransitions().size() == 1);
       	
       	feRoute2 = (FlowElement)feRoute2.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'publisher2'", "publisher2".equals(feRoute2.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Publisher", feRoute2.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be to producer", feRoute2.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_sequencer_transitions() 
    {
    	Flow flow = FlowBuilder.newFlow("flowName", "moduleName")
    	.withDescription("flowDescription")
        .withExclusionServiceFactory(exclusionServiceFactory)
        .withSerialiserFactory(serialiserFactory)
        .consumer("consumer", consumer)
    	.sequencer("sequencer", sequencer)
    		.sequence("sequence name 1").publisher("name1", producer)
    		.sequence("sequence name 2").publisher("name2", producer)
    	.build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
       	Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

       	FlowElement fe = flowElements.get(0);
       	Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
    	Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);
       	
    	fe = (FlowElement)fe.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'sequencer'", "sequencer".equals(fe.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
    	Assert.assertTrue("flow element should have two transitions", fe.getTransitions().size() == 2);
       	
    	FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("sequence name 1");
    	FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("sequence name 2");
       	Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feRoute1.getComponentName()));       		
    	Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be null", feRoute1.getTransitions().size() == 0);
       	
       	Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feRoute2.getComponentName()));       		
       	Assert.assertTrue("flow element component should be an instance of Producer", feRoute2.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be null", feRoute2.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }
}
