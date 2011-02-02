/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.flow.visitorPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.event.ReplicationFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.routing.RouterException;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.sequencing.SequencerException;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.rits.cloning.Cloner;

/**
 * This test class supports the <code>IonMarketDataConsumer</code> class.
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlowTest
{
    /** Logger instance */
    private Logger logger = Logger.getLogger(VisitingInvokerFlowTest.class);
	
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery();

    /** Mock flowElementInvoker */
    final FlowElementInvoker mockFlowElementInvoker = mockery.mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /** Mock flowElement */
    final FlowElement mockFlowElement = mockery.mock(FlowElement.class, "mockFlowElement");

    /** Mock flowInvocationContext */
    final FlowInvocationContext mockFlowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** Mock flowEvent */
    final FlowEvent mockFlowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock transition map */
    final Map<String,FlowElement> mockTransitions = mockery.mock(Map.class, "mockMapTransitions");
    
    /** stubbed translator component */
	Object stubTranslatorComponent;

    /** stubbed converter component */
	Object stubConverterComponent;

	/** stubbed router component */
	Object stubRouterComponent;

	/** stubbed sequencer component */
	Object stubSequencerComponent;

	/** stubbed producer component */
	Object stubProducerStringBuilderComponent;

	/** stubbed producer component */
	Object stubProducerStringComponent;

	/** stubbed broker component */
	Object stubBrokerComponent;

	/** stubbed producer component */
	Object stubConsumerComponent;
    
	/** real flow context */
	FlowInvocationContext flowInvocationContext;
	
	/** flow flow element invoker */
	FlowElementInvoker flowElementInvoker;
	
	/** event factory */
	EventFactory<FlowEvent<?>> eventFactory;
	
	/** replication factory */
	ReplicationFactory<FlowEvent<?>> replicationFactory;
	
	/**
     * Test failed constructor due to null configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConfiguration()
    {
        new VisitingInvokerFlow(null, null, null, null);
    }

    /**
     * Create all stubbed component types
     */
    @Before
    public void setup()
    {
    	// initialise stubbed components
    	stubTranslatorComponent = new StubTranslatorComponent();
    	stubConverterComponent = new StubConverterComponent();
    	stubRouterComponent = new StubRouterComponent();
    	stubSequencerComponent = new StubSequencerComponent();
    	stubProducerStringBuilderComponent = new StubProducerStringBuilderComponent();
    	stubProducerStringComponent = new StubProducerStringComponent();
    	stubBrokerComponent = new StubBrokerComponent();
    	stubConsumerComponent = new StubConsumerComponent();

    	// flow context instance
    	flowInvocationContext = new DefaultFlowInvocationContext();

    	// replication factory
    	replicationFactory = new DefaultReplicationFactory<FlowEvent<?>>(new Cloner());
    	
    	// element invoker instance
    	flowElementInvoker = new VisitingFlowElementInvoker();
    	
    	// event factory implementation
    	eventFactory = new FlowEventFactory();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with translator as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>consumer
     * producer<String>
     * 
     */
    @Test
    public void test_flow_consumer_producer()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", "payload");
    	FlowElement consumerFlowElement = new FlowElementImpl("consumerComponentName", stubConsumerComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", consumerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("payload", (String)flowEvent.getPayload());
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>consumer
     * producer<String>
     * 
     */
    @Test(expected = InvalidFlowException.class)
    public void test_flow_consumer_not_other_flowElement()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", "payload");
    	FlowElement consumerFlowElement = new FlowElementImpl("consumerComponentName", stubConsumerComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", consumerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(null));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with translator as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * translator<StringBuilder>
     * producer<StringBuilder>
     * 
     */
    @Test
    public void test_flow_translator_producer()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("before translator"));
    	FlowElement translatorFlowElement = new FlowElementImpl("translatorComponentName", stubTranslatorComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringBuilderComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", translatorFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("after translator", new String((StringBuilder)flowEvent.getPayload()));
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * translator<StringBuilder>
     * 
     */
    @Test(expected = InvalidFlowException.class)
    public void test_flow_translator_no_other_flowElement()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("before translator"));
    	FlowElement translatorFlowElement = new FlowElementImpl("translatorComponentName", stubTranslatorComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", translatorFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(null));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with converter as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * converter<StringBuilder>
     * producer<String>
     * 
     */
    @Test
    public void test_flow_converter_producer()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("before converter"));
    	FlowElement converterFlowElement = new FlowElementImpl("converterComponentName", stubConverterComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", converterFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("after converter", (String)flowEvent.getPayload());
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>converter<StringBuilder>
     * 
     */
    @Test(expected = InvalidFlowException.class)
    public void test_flow_converter_no_other_flowElement()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("before translator"));
    	FlowElement converterFlowElement = new FlowElementImpl("converterComponentName", stubConverterComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", converterFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(null));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with broker as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>broker<StringBuilder>
     * producer<String>
     * 
     */
    @Test
    public void test_flow_broker_producer()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("broker payload"));
    	FlowElement brokerFlowElement = new FlowElementImpl("brokerComponentName", stubBrokerComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", brokerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("broker payload", (String)flowEvent.getPayload());
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>broker<StringBuilder>
     * 
     */
    @Test
    public void test_flow_broker_no_other_flowElement()
    {
    	// setup
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("broker payload"));
    	FlowElement brokerFlowElement = new FlowElementImpl("brokerComponentName", stubBrokerComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", brokerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(null));
            }
        });

        // run test
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("broker payload", (String)flowEvent.getPayload());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with sequencer as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>sequencer<StringBuilder>
     * producer<String>
     * 
     */
    @Test
    public void test_flow_sequencer_producer()
    {
    	// setup
    	FlowElement sequencerFlowElement = new FlowElementImpl("sequencerComponentName", stubSequencerComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringBuilderComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", sequencerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(mockTransitions).get("default");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("second"));
        flow.invoke(flowInvocationContext, flowEvent);

    	flowEvent.setPayload(new StringBuilder("first"));
        flow.invoke(flowInvocationContext, flowEvent);
    	
        // test assertions
        Assert.assertEquals("second", new String((StringBuilder)flowEvent.getPayload()));
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>sequencer<StringBuilder>
     * 
     */
    @Test(expected = InvalidFlowException.class)
    public void test_flow_sequencer_no_other_flowElement()
    {
    	// setup
    	FlowElement sequencerFlowElement = new FlowElementImpl("sequencerComponentName", stubSequencerComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringBuilderComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", sequencerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("default");
                will(returnValue(null));
            }
        });

        // run test
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("second"));
        flow.invoke(flowInvocationContext, flowEvent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // tests with router as the head flow 
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>router (single recipient)<StringBuilder>
     * producer<String>
     * 
     */
    @Test
    public void test_flow_singleRecipientRouter_producer()
    {
    	// setup
    	FlowElement routerFlowElement = new FlowElementImpl("routerComponentName", stubRouterComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringBuilderComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", routerFlowElement, flowElementInvoker);
    	
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("valid route");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("payload to single valid route"));
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("payload to single valid route", new String((StringBuilder)flowEvent.getPayload()));
    }

    /**
     * Test flow invoker based on the following component order.
     * 
     * <String>router (single result)<StringBuilder>
     * producer<String>
     * 
     */
    @Test
    public void test_flow_multiRecipientRouter_producer()
    {
    	// setup
    	FlowElement routerFlowElement = new FlowElementImpl("routerComponentName", stubRouterComponent, mockTransitions);
    	final FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", stubProducerStringBuilderComponent, mockTransitions);
        flowElementInvoker = new VisitingFlowElementInvoker(replicationFactory);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", routerFlowElement, flowElementInvoker);
    	

        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockTransitions).get("valid route one");
                will(returnValue(producerFlowElement));
                exactly(1).of(mockTransitions).get("valid route two");
                will(returnValue(producerFlowElement));
                exactly(1).of(mockTransitions).get("valid route three");
                will(returnValue(producerFlowElement));
            }
        });

        // run test
    	FlowEvent flowEvent = eventFactory.newEvent("id", new StringBuilder("payload to multiple valid route"));
        flow.invoke(flowInvocationContext, flowEvent);

        // test assertions
        Assert.assertEquals("payload to multiple valid route", new String((StringBuilder)flowEvent.getPayload()));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // general runtime flow failures
    //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Test conditions where the conponent does not have a matching method 
     * signature for the incoming payload class.
     * 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_componentMethodMatcher()
    {
        // setup a payload class which is not supported by component method
    	FlowEvent flowEvent = eventFactory.newEvent("id", new String("this translator method doesnt exist"));
    	FlowElement translatorFlowElement = new FlowElementImpl("componentName", stubTranslatorComponent, mockTransitions);
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", translatorFlowElement, flowElementInvoker);

    	// test
    	flow.invoke(flowInvocationContext, flowEvent);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // Stubbed Component classes
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Stubbed Translator to 'change' the content of incoming StringBuilder
     */
    private class StubTranslatorComponent implements Translator<StringBuilder>
    {
		public void translate(StringBuilder payload) throws TransformationException
		{
			payload.delete(0, payload.length());
			payload.append("after translator");
		}
    }

    /**
     * Stubbed Converter to convert the incoming StringBuilder to a String
     */
    private class StubConverterComponent implements Converter<StringBuilder,String>
    {
		public String convert(StringBuilder payload) throws TransformationException
		{
			return new String("after converter");
		}
    }

    /**
     * Stubbed Router routes based on incoming payload content
     */
    private class StubRouterComponent implements Router<StringBuilder>
    {
		public List<String> route(StringBuilder payload) throws RouterException 
		{
			List<String> routes = new ArrayList<String>();
			String payloadStr = new String(payload);
			if(payloadStr.equals("payload to single valid route"))
			{
				routes.add("valid route");
			}
			else if(payloadStr.equals("payload to multiple valid route"))
			{
				routes.add("valid route one");
				routes.add("valid route two");
				routes.add("valid route three");
			}
			else if(payloadStr.equals("payload to invalid route"))
			{
				return null;
			}
			
			return routes;
		}
    }

    /**
     * Stubbed sequencer which sequences incoming payloads of "second" and "first"
     * into order of "first" and "second".
     *
     */
    private class StubSequencerComponent implements Sequencer<StringBuilder>
    {
		List<StringBuilder> sequenced = new ArrayList<StringBuilder>(2);
    	
		public List<StringBuilder> sequence(StringBuilder payload) throws SequencerException 
		{
			if(new String(payload).equals("first"))
			{
				sequenced.add(0,payload);
			}
			else if(new String(payload).equals("second"))
			{
				sequenced.add(payload);
			}

			if(sequenced.size() > 1)
			{
				return sequenced;
			}
			
			return null;
		}
    }

    /**
     * Stubbed consumer based on returning a String
     */
    private class StubConsumerComponent implements Consumer<String>
    {
		public String invoke() throws EndpointException 
		{
			return "payload";
		}
    }

    /**
     * Stubbed broker based on incoming StringBuilder payload and a returned String
     */
    private class StubBrokerComponent implements Broker<StringBuilder,String>
    {
		public String invoke(StringBuilder payload) throws EndpointException 
		{
			return new String(payload);
		}
    }

    /**
     * Stubbed producer based on StringBuilder payload
     */
    private class StubProducerStringBuilderComponent implements Producer<StringBuilder>
    {
    	public void invoke(StringBuilder payload) throws EndpointException 
		{
    		logger.info("Producer.invoke(StringBuilder payload)");
		}
    }

    /**
     * Stubbed producer based on String payload
     */
    private class StubProducerStringComponent implements Producer<String>
    {
		public void invoke(String payload) throws EndpointException 
		{
    		logger.info("Producer.invoke(String payload)");
		}
    }

}
