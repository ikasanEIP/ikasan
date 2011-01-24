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

import junit.framework.Assert;

import org.ikasan.flow.event.GenericFlowEvent;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowComponent;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.event.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.mizuho.cmi2.ion.marketDataSrc.util.IonMarketDataUtils;

/**
 * This test class supports the <code>IonMarketDataConsumer</code> class.
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlowTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery();

    /** Mock flowElementInvoker */
    final FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /** Mock flowElement */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "mockFlowElement");

    /** Mock flowInvocationContext */
    final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** Mock flowEvent */
    final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock transition map */
    final Map<String,FlowElement> transitions = mockery.mock(Map.class, "mockMapTransitions");
    
    /** Mock flowComponent */
    final FlowComponent flowComponent = mockery.mock(FlowComponent.class, "mockFlowComponent");

    /**
     * Test failed constructor due to null configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConfiguration()
    {
        new VisitingInvokerFlow(null, null, null, null);
    }
    
    /**
     * Test translator.
     * 
     */
    @Test
    public void test_successful_translator()
    {
    	FlowEvent flowEvent = new GenericFlowEvent("id");
    	flowEvent.setPayload(new StringBuilder("before translator"));
    	FlowComponent translatorComponent = new StubTranslatorComponent();
    	FlowElement flowElement = new FlowElementImpl("componentName", translatorComponent, transitions);
    	FlowInvocationContext flowInvocationContext = new DefaultFlowInvocationContext();
    	
    	FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", flowElement, flowElementInvoker);
        flow.invoke(flowInvocationContext, flowEvent);

        Assert.assertEquals("after translator", flowEvent.getPayload());
    }

    /**
     * Test translator.
     * 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_componentMethodMatcher()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(transitions).get("default");
                will(returnValue("default"));
            }
        });

    	FlowEvent flowEvent = new GenericFlowEvent("id");
    	flowEvent.setPayload(new String("this translator method doesnt exist"));
    	FlowComponent translatorComponent = new StubTranslatorComponent();
    	FlowElement flowElement = new FlowElementImpl("componentName", translatorComponent, transitions);
    	FlowInvocationContext flowInvocationContext = new DefaultFlowInvocationContext();
    	
    	FlowElementInvoker flowElementInvoker = new VisitingFlowElementInvoker();
    	Flow flow = new VisitingInvokerFlow("flowName", "moduleName", flowElement, flowElementInvoker);
        flow.invoke(flowInvocationContext, flowEvent);

        Assert.assertEquals("before translator", flowEvent.getPayload());
        mockery.assertIsSatisfied();
    }

    private class StubTranslatorComponent implements Translator<StringBuilder>, FlowComponent
    {
		public void translate(StringBuilder payload) throws TransformationException
		{
			payload.delete(0, payload.length());
			payload.append("after translator");
		}
    }

    private class StubIntegerConverterComponent implements Converter<String,Integer>, FlowComponent
    {
		public Integer convert(String payload) throws TransformationException
		{
			return new Integer(payload);
		}
    }
}
