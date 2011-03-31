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

import java.util.Map;

import org.apache.log4j.Logger;

import org.ikasan.flow.event.DefaultReplicationFactory;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.event.ReplicationFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.endpoint.RecoveryManager;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.configuration.service.ConfigurationService;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.exceptionHandler.ExceptionHandler;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
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

    /** Mock exceptionHandler */
    final ExceptionHandler exceptionHandler = mockery.mock(ExceptionHandler.class, "mockExceptionHandler");

    /** Mock recoveryManager */
    final RecoveryManager recoveryManager = mockery.mock(RecoveryManager.class, "mockRecoveryManager");

    /** Mock flowElement */
    final FlowElement mockFlowElement = mockery.mock(FlowElement.class, "mockFlowElement");

    /** Mock flowInvocationContext */
    final FlowInvocationContext mockFlowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** Mock configurationService */
    final ConfigurationService configurationService = mockery.mock(ConfigurationService.class, "mockConfigurationService");

    /** Mock flowEvent */
    final FlowEvent mockFlowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock transition map */
    final Map<String,FlowElement> mockTransitions = mockery.mock(Map.class, "mockMapTransitions");
    
	/** real flow context */
	FlowInvocationContext flowInvocationContext;
	
	/** flow flow element invoker */
	FlowElementInvoker flowElementInvoker;
	
	/** event factory */
	EventFactory eventFactory;
	
	/** replication factory */
	ReplicationFactory<FlowEvent<?>> replicationFactory;
	
	/**
     * Test failed constructor due to null configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConfiguration()
    {
        new VisitingInvokerFlow(null, null, null, null, null, null);
    }

    /**
     * Create all stubbed component types
     */
    @Before
    public void setup()
    {
    	// flow context instance
    	flowInvocationContext = new DefaultFlowInvocationContext();

    	// replication factory
    	replicationFactory = new DefaultReplicationFactory<FlowEvent<?>>(new Cloner());
    }
    
    @Test
    public void test_flow_consumer_translator_producer()
    {
        // create producer component
        Producer producer = new StubProducerStringBuilderComponent();
        FlowElement producerFlowElement = new FlowElementImpl("producerComponentName", producer);

        // create translator
        Translator translator = new StubTranslatorComponent();
        FlowElement translatorFlowElement = new FlowElementImpl("translatorComponentName", translator, producerFlowElement);

        // create consumer component
        Consumer consumer = new StubConsumerComponent(new StubbedTech(), new FlowEventFactory());
        FlowElement consumerFlowElement = new FlowElementImpl("consumerComponentName", consumer, translatorFlowElement);

//        // flow exception handler
//        int delay = 1000;
//        int retries = 10;
//        ExceptionAction retryAction = new RetryAction(delay, retries);
//        ExceptionAction stopAction = StopAction.instance();
//        ExceptionAction excludeAction = ExcludeEventAction.instance();
//        
//        IsInstanceOf instanceOfException = new org.hamcrest.core.IsInstanceOf(Exception.class);
//        MatcherBasedExceptionGroup matcher = new MatcherBasedExceptionGroup(instanceOfException, stopAction);
//        
//        List<ExceptionGroup> matchers = new ArrayList<ExceptionGroup>();
//        matchers.add(matcher);
//        
//        ExceptionHandler ikasanExceptionHandler = new MatchingExceptionHandler(matchers);
//        
        // flow configuration wiring
        FlowConfiguration flowConfiguration = new DefaultFlowConfiguration(consumerFlowElement, configurationService);

        // iterator over each flow element
        FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();

        // container for the complete flow
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, exceptionHandler, recoveryManager);
        
        // run test
        flow.start();

        // test assertions
//        Assert.assertEquals("payload", (String)flowEvent.getPayload());
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
			payload.append(" with added value");
		}
    }

    /**
     * Stubbed Converter to convert the incoming StubbedTechMessage to a String
     */
    private class StubConverterComponent implements Converter<StubbedTechMessage,StringBuilder>
    {
		public StringBuilder convert(StubbedTechMessage payload) throws TransformationException
		{
			return new StringBuilder(payload.getData());
		}
    }

    // ========================================================================
    // Producer for StringBuilder
    // ========================================================================
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

    // ========================================================================
    // Consumer for the Stubbed Tech
    // ========================================================================
    private class StubConsumerComponent implements Consumer<EventListener>, StubbedTechListener
    {
        private StubbedTech stubbedTech;
        private FlowEventFactory flowEventFactory;

        private EventListener eventListener;

        private Thread techThread;
        
        public StubConsumerComponent(StubbedTech stubbedTech, FlowEventFactory flowEventFactory)
        {
            this.stubbedTech = stubbedTech;
            this.flowEventFactory = flowEventFactory;
            this.stubbedTech.setListener(this);
        }
        
        public void start()
        {
            techThread = new Thread(this.stubbedTech);
            techThread.start();
        }

        public void stop()
        {
            techThread.interrupt();
        }

        public boolean isRunning()
        {
            return false;
        }

        public void setListener(EventListener eventListener)
        {
            this.eventListener = eventListener;
        }

        public void onMessage(StubbedTechMessage message)
        {
            FlowEvent<?> flowEvent = flowEventFactory.newEvent("identifier", message);
            this.eventListener.invoke(flowEvent);
        }
    }

    // ========================================================================
    // Stubbed Tech implementation
    // ========================================================================

    /** Tech listener interface */
    interface StubbedTechListener
    {
        public void onMessage(StubbedTechMessage message);
    }
    
    /** Tech data model */
    private class StubbedTechMessage
    {
        private String data = "data content";

        public String getData()
        {
            return data;
        }
    }

    /** Tech implementation */
    private class StubbedTech implements Runnable
    {
        private StubbedTechListener stubbedTechListener;
        
        public void setListener(StubbedTechListener stubbedTechListener)
        {
            this.stubbedTechListener = stubbedTechListener;
        }
        
        public void run()
        {
            StubbedTechMessage message = new StubbedTechMessage();
            this.stubbedTechListener.onMessage(message);
        }
    }
}
