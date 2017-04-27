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
import org.ikasan.spec.component.routing.SingleRecipientRouter;
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

import java.util.ArrayList;
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
    final SingleRecipientRouter singleRecipientRouter = mockery.mock(SingleRecipientRouter.class, "mockSingleRecipientRouter");
    
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
		Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
    	    .withDescription("flowDescription")
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withFlowInvocationContextListener(flowInvocationContextListener)
            .withExclusionServiceFactory(exclusionServiceFactory)
            .withSerialiserFactory(serialiserFactory)
            .consumer("consumer", consumer)
    	    .converter("converter", converter)
    	    .translator("translator", translator)
    	    .broker("broker", broker)
    	    .producer("producer", producer).build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
		Assert.assertTrue("Should be 5 flow elements", flowElements.size() == 5);
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
    public void test_successful_simple_router_transitions()
    {
        Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", BuilderFactory.routeBuilder().producer("whenPublisher", producer))
                .otherwise(BuilderFactory.routeBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer)).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
		Assert.assertTrue("Should be 5 flow elements", flowElements.size() == 5);
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 2 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 2);

        // when route
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'whenPublisher'", "whenPublisher".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     *
     *  Consumer --> SRR --route1-->    producer
     *                   --route2-->    SRR (route2)    --nestedRoute1--> producer
     *                                                  --nestedRoute2--> producer
     *                                                  --  otherwise --> producer
     *
     *                   --otherwise--> translator  --> producer
     */
    @Test
    public void test_successful_nested_router_transitions()
    {
        Route nestedRoute1 = BuilderFactory.routeBuilder().producer("nestedRoute1-publisher1", producer);
        Route nestedRoute2 = BuilderFactory.routeBuilder().producer("nestedRoute2-publisher2", producer);
        Route nestedRoute3 = BuilderFactory.routeBuilder().producer("nestedRoute3-publisher3", producer);
        Route route2 = BuilderFactory.routeBuilder().singleRecipientRouter("nestedSRR", singleRecipientRouter)
                .when("nestedRoute1", nestedRoute1)
                .when("nestedRoute2", nestedRoute2)
                .otherwise(nestedRoute3).build();

        Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", BuilderFactory.routeBuilder().producer("whenPublisher1", producer))
                .when("route2", route2)
                .otherwise(BuilderFactory.routeBuilder().translator("otherwiseTranslator", translator).producer("otherwisePublisher", producer)).build();

        Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
        Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
        List<FlowElement<?>> flowElements = flow.getFlowElements();
        Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
		Assert.assertTrue("Should be 9 flow elements", flowElements.size() == 9);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

        // Consumer
        FlowElement fe = flowElements.get(0);
        Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
        Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // SRR
        fe = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
        Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

        // when route1
        FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
        Assert.assertTrue("flow element name should be 'whenPublisher1'", "whenPublisher1".equals(feRoute1.getComponentName()));
        Assert.assertTrue("flow element transition should be to 0 for producer", feRoute1.getTransitions().size() == 0);

        // when route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
        Assert.assertTrue("flow element name should be 'nestedSRR'", "nestedSRR".equals(feRoute2.getComponentName()));
		Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

		// route2 nested components
		FlowElement nestedFeRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
		Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(nestedFeRoute1.getComponentName()));
		Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute1.getTransitions().size() == 0);
		FlowElement nestedFeRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
		Assert.assertTrue("flow element name should be 'nestedRoute2-publisher2'", "nestedRoute2-publisher2".equals(nestedFeRoute2.getComponentName()));
		Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeRoute2.getTransitions().size() == 0);
		FlowElement nestedFeOtherwise = (FlowElement)feRoute2.getTransitions().get("default");
		Assert.assertTrue("flow element name should be 'nestedRoute3-publisher3'", "nestedRoute3-publisher3".equals(nestedFeOtherwise.getComponentName()));
		Assert.assertTrue("flow element transition should be to 0 for producer", nestedFeOtherwise.getTransitions().size() == 0);

        // otherwise route
        FlowElement feOtherwise = (FlowElement)fe.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwiseTranslator'", "otherwiseTranslator".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feOtherwise.getFlowComponent() instanceof Translator);
        Assert.assertTrue("flow element transition should be to producer", feOtherwise.getTransitions().size() == 1);
        feOtherwise = (FlowElement)feOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'otherwisePublisher'", "otherwisePublisher".equals(feOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feOtherwise.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element transition should be 0 for a producer", feOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     * deprecated with the test above
     */
    @Test
    public void test_successful_router_transitions()
    {
		Route route1 = BuilderFactory.routeBuilder().producer("producer", producer);
		Route nestedRoute1 = BuilderFactory.routeBuilder().translator("nestedRoute1-name1",translator).producer("nestedRoute1-publisher1", producer);
		Route nestedRoute2 = BuilderFactory.routeBuilder().translator("nestedRoute2-name2",translator).producer("nestedRoute2-publisher2", producer);
		Route nestedRoute3 = BuilderFactory.routeBuilder().translator("nestedRoute3-name3",translator).producer("nestedRoute3-publisher3", producer);
		Route route2 = BuilderFactory.routeBuilder().singleRecipientRouter("nestedSRR", singleRecipientRouter)
				.when("nestedRoute1", nestedRoute1)
				.when("nestedRoute2", nestedRoute2)
				.otherwise(nestedRoute3).build();

        Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
                .withDescription("flowDescription")
                .withFlowInvocationContextListeners(Collections.singletonList(flowInvocationContextListener))
                .withExclusionServiceFactory(exclusionServiceFactory)
                .withSerialiserFactory(serialiserFactory)
                .consumer("consumer", consumer)
                .singleRecipientRouter("router", singleRecipientRouter)
                .when("route1", route1)
                .when("route2", route2)
                .otherwise(BuilderFactory.routeBuilder().translator("name4", translator).producer("publisher4", producer)).build();

		Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
		Assert.assertTrue("Should be 12 flow elements", flowElements.size() == 12);
       	Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);

        Assert.assertTrue("Should have one FlowInvocationContextListener", flow.getFlowInvocationContextListeners().size() == 1);

		// Consumer
       	FlowElement fe = flowElements.get(0);
       	Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
    	Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

		// SRR
    	fe = (FlowElement)fe.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'router'", "router".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of SRR", fe.getFlowComponent() instanceof SingleRecipientRouter);
    	Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + fe.getTransitions().size() + "]", fe.getTransitions().size() == 3);

		// when route1
    	FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("route1");
       	Assert.assertTrue("flow element name should be 'producer'", "producer".equals(feRoute1.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);

		// when route2
		FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("route2");
       	Assert.assertTrue("flow element name should be 'nestedSRR'", "nestedSRR".equals(feRoute2.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of SRR", feRoute2.getFlowComponent() instanceof SingleRecipientRouter);
		Assert.assertTrue("flow element SRR shuold have 3 transitions not [" + feRoute2.getTransitions().size() + "]", feRoute2.getTransitions().size() == 3);

		// route2 nested components
        // route2 nestedRoute1
        FlowElement feNestedRoute1 = (FlowElement)feRoute2.getTransitions().get("nestedRoute1");
        Assert.assertTrue("flow element name should be 'nestedRoute1-name1'", "nestedRoute1-name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute1.getFlowComponent() instanceof Translator);
        feNestedRoute1 = (FlowElement)feNestedRoute1.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute1-publisher1'", "nestedRoute1-publisher1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);

        // route2 nestedRoute2
        FlowElement feNestedRoute2 = (FlowElement)feRoute2.getTransitions().get("nestedRoute2");
        Assert.assertTrue("flow element name should be 'nestedRoute2-name2'", "nestedRoute2-name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedRoute2.getFlowComponent() instanceof Translator);
        feNestedRoute2 = (FlowElement)feNestedRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute2-publisher2'", "nestedRoute2-publisher2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute2.getFlowComponent() instanceof Producer);

        // route2 nestedOtherwise
        FlowElement feNestedOtherwise = (FlowElement)feRoute2.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute3-name3'", "nestedRoute3-name3".equals(feNestedOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Translator", feNestedOtherwise.getFlowComponent() instanceof Translator);
        feNestedOtherwise = (FlowElement)feNestedOtherwise.getTransitions().get("default");
        Assert.assertTrue("flow element name should be 'nestedRoute3-publisher3'", "nestedRoute3-publisher3".equals(feNestedOtherwise.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedOtherwise.getFlowComponent() instanceof Producer);

        // otherwise
		FlowElement feRouteOtherwise = (FlowElement)fe.getTransitions().get("default");
		Assert.assertTrue("flow element name should be 'name4'", "name4".equals(feRouteOtherwise.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Translator", feRouteOtherwise.getFlowComponent() instanceof Translator);
		Assert.assertTrue("flow element transition should be to prodcuer", feRouteOtherwise.getTransitions().size() == 1);
		feRouteOtherwise = (FlowElement)feRouteOtherwise.getTransitions().get("default");
		Assert.assertTrue("flow element name should be 'publisher4'", "publisher4".equals(feRouteOtherwise.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Producer", feRouteOtherwise.getFlowComponent() instanceof Producer);
		Assert.assertTrue("flow element transition should be to producer", feRouteOtherwise.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow creation.
     */
    @Test
    public void test_successful_sequencer_transitions()
    {
    	Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
    	.withDescription("flowDescription")
        .withExclusionServiceFactory(exclusionServiceFactory)
        .withSerialiserFactory(serialiserFactory)
        .consumer("consumer", consumer)
    	.sequencer("sequencer", sequencer)
    		.route("sequence name 1", BuilderFactory.routeBuilder().producer("name1", producer))
    		.route("sequence name 2", BuilderFactory.routeBuilder().producer("name2", producer)).build();

    	Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
    	Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
       	List<FlowElement<?>> flowElements = flow.getFlowElements();
       	Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 4 flow elements", flowElements.size() == 4);

        // Consumer
       	FlowElement fe = flowElements.get(0);
       	Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
    	Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // Sequencer
    	fe = (FlowElement)fe.getTransitions().get("default");
       	Assert.assertTrue("flow element name should be 'sequencer'", "sequencer".equals(fe.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
    	Assert.assertTrue("flow element should have two transitions", fe.getTransitions().size() == 2);

        // ensure sequence order
        ArrayList<String> sequenceNames = new ArrayList<String>(fe.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'sequence name 1'..." + " returned order is " + sequenceNames, sequenceNames.get(0).equals("sequence name 1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + sequenceNames, sequenceNames.get(1).equals("sequence name 2"));

        // Producer 1
    	FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("sequence name 1");
       	Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feRoute1.getComponentName()));
    	Assert.assertTrue("flow element component should be an instance of Producer", feRoute1.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be null", feRoute1.getTransitions().size() == 0);

        // Producer 2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("sequence name 2");
       	Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feRoute2.getComponentName()));
       	Assert.assertTrue("flow element component should be an instance of Producer", feRoute2.getFlowComponent() instanceof Producer);
    	Assert.assertTrue("flow element transition should be null", feRoute2.getTransitions().size() == 0);

        mockery.assertIsSatisfied();
    }

	/**
	 * Test successful flow creation.
	 */
	@Test
	public void test_successful_sequencer_nested_transitions()
	{
		Route nestedRoute1 = BuilderFactory.routeBuilder().producer("name1", producer);
		Route nestedRoute2 = BuilderFactory.routeBuilder().producer("name2", producer);

		Flow flow = BuilderFactory.flowBuilder("flowName", "moduleName")
				.withDescription("flowDescription")
				.withExclusionServiceFactory(exclusionServiceFactory)
				.withSerialiserFactory(serialiserFactory)
				.consumer("consumer", consumer)
				.sequencer("sequencer", sequencer)
					.route("sequence name 1",
							BuilderFactory.routeBuilder().sequencer("sequencerNested1",sequencer)
								.route("nestedSeq1", nestedRoute1)
								.route("nestedSeq2", nestedRoute2).build())
					.route("sequence name 2", BuilderFactory.routeBuilder().producer("name3", producer))
				.build();

		Assert.assertTrue("flow name is incorrect", "flowName".equals(flow.getName()));
		Assert.assertTrue("module name is incorrect", "moduleName".equals(flow.getModuleName()));
		List<FlowElement<?>> flowElements = flow.getFlowElements();
		Assert.assertNotNull("Flow elements cannot be 'null'", flowElements);
        Assert.assertTrue("Should be 6 flow elements", flowElements.size() == 6);

        // consumer
		FlowElement fe = flowElements.get(0);
		Assert.assertTrue("flow element name should be 'consumer'", "consumer".equals(fe.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Consumer", fe.getFlowComponent() instanceof Consumer);
		Assert.assertTrue("flow element should have a single transition", fe.getTransitions().size() == 1);

        // sequencer
		fe = (FlowElement)fe.getTransitions().get("default");
		Assert.assertTrue("flow element name should be 'sequencer'", "sequencer".equals(fe.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Sequencer", fe.getFlowComponent() instanceof Sequencer);
		Assert.assertTrue("flow element should have two transitions", fe.getTransitions().size() == 2);

        // ensure sequence order
        ArrayList<String> sequenceNames = new ArrayList<String>(fe.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'sequence name 1'..." + " returned order is " + sequenceNames, sequenceNames.get(0).equals("sequence name 1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + sequenceNames, sequenceNames.get(1).equals("sequence name 2"));

        // nested sequencer
		FlowElement feRoute1 = (FlowElement)fe.getTransitions().get("sequence name 1");
		Assert.assertTrue("flow element name should be 'sequencerNested1'", "sequencerNested1".equals(feRoute1.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Sequencer", feRoute1.getFlowComponent() instanceof Sequencer);
		Assert.assertTrue("flow element should have two transitions", feRoute1.getTransitions().size() == 2);

        // ensure sequence order
        ArrayList<String> nestedSequenceNames = new ArrayList<String>(feRoute1.getTransitions().keySet());
        Assert.assertTrue("flow element transition sequence should be in order of 'nestedSeq1'..." + " returned order is " + nestedSequenceNames, nestedSequenceNames.get(0).equals("nestedSeq1"));
        Assert.assertTrue("flow element transition sequence should be in order of ...'sequence name 2'"  + " returned order is " + nestedSequenceNames, nestedSequenceNames.get(1).equals("nestedSeq2"));

        // nested sequencer1 nestedSeq1
        FlowElement feNestedRoute1 = (FlowElement)feRoute1.getTransitions().get("nestedSeq1");
        Assert.assertTrue("flow element name should be 'name1'", "name1".equals(feNestedRoute1.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element should have null transitions", feNestedRoute1.getTransitions().size() == 0);

        // nested sequencer1 nestedSeq2
        FlowElement feNestedRoute2 = (FlowElement)feRoute1.getTransitions().get("nestedSeq2");
        Assert.assertTrue("flow element name should be 'name2'", "name2".equals(feNestedRoute2.getComponentName()));
        Assert.assertTrue("flow element component should be an instance of Producer", feNestedRoute1.getFlowComponent() instanceof Producer);
        Assert.assertTrue("flow element should have null transitions", feNestedRoute1.getTransitions().size() == 0);

        // sequencer route2
        FlowElement feRoute2 = (FlowElement)fe.getTransitions().get("sequence name 2");
		Assert.assertTrue("flow element name should be 'name3'", "name3".equals(feRoute2.getComponentName()));
		Assert.assertTrue("flow element component should be an instance of Producer", feRoute2.getFlowComponent() instanceof Producer);
		Assert.assertTrue("flow element transition should be null", feRoute2.getTransitions().size() == 0);

		mockery.assertIsSatisfied();
	}
}
