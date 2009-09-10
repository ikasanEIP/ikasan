 /* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.invoker.FlowElementInvoker;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class VisitingInvokerFlowTest
{
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    /**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /**
     * Mocked Event object
     */
    Event event = classMockery.mock(Event.class);
    
    
    
    /**
     * Mocked invoker
     */
    FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class);
    
    /**
     * Mocked element for the head
     */
    FlowElement flowElement = mockery.mock(FlowElement.class);
    
    /**
     * Name of this flow
     */
    String flowName = "flowName";
    
    String moduleName = "moduleName";
    
    FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
    

    /**
     * Class under test
     */
    private VisitingInvokerFlow visitingInvokerFlow = new VisitingInvokerFlow(flowName, moduleName, flowElement, flowElementInvoker);
    
    /**
     * Mocked Exeption Action
     */
    IkasanExceptionAction ikasanExceptionAction = mockery.mock(IkasanExceptionAction.class);
    
    
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#invoke(org.ikasan.framework.component.Event)}.
     */
    @Test
    public void testInvoke()
    {
    	
    	
        mockery.checking(new Expectations()
        {
            {
                one(flowElementInvoker).invoke(flowInvocationContext, event, moduleName, flowName, flowElement);
            }
        });
        
        
        visitingInvokerFlow.invoke(flowInvocationContext, event);
    }
    /**
     * Creates a sufficiently complex graph of flow elements and tests that the getFlowElements
     * method returns a listing of all the elements discovered in a breadth first search
     */
    @Test
    public void testGetFlowElements(){
    	final FlowElement flowElementA = mockery.mock(FlowElement.class, "flowElementA");
    	final FlowElement flowElementB = mockery.mock(FlowElement.class, "flowElementB");
    	final FlowElement flowElementC = mockery.mock(FlowElement.class, "flowElementC");
    	final FlowElement flowElementD = mockery.mock(FlowElement.class, "flowElementD");
    	final FlowElement flowElementE = mockery.mock(FlowElement.class, "flowElementE");
    	final FlowElement flowElementF = mockery.mock(FlowElement.class, "flowElementF");
    	
    	final Map<String, FlowElement> flowElementATransitions = new HashMap<String, FlowElement>();
    	flowElementATransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementB );
    	
    	final Map<String, FlowElement> flowElementBTransitions = new HashMap<String, FlowElement>();
    	flowElementBTransitions.put("transitionToC",flowElementC );
    	flowElementBTransitions.put("transitionToD",flowElementD );

    	final Map<String, FlowElement> flowElementCTransitions = new HashMap<String, FlowElement>();
    	flowElementCTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementE );

    	final Map<String, FlowElement> flowElementDTransitions = new HashMap<String, FlowElement>();
    	flowElementDTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementF );

    	final Map<String, FlowElement> flowElementETransitions = new HashMap<String, FlowElement>();

    	final Map<String, FlowElement> flowElementFTransitions = new HashMap<String, FlowElement>();
    	flowElementFTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementB );
    	
        mockery.checking(new Expectations()
        {
            {
                one(flowElementA).getTransitions();
                will(returnValue(flowElementATransitions));
                
                one(flowElementB).getTransitions();
                will(returnValue(flowElementBTransitions));
                
                one(flowElementC).getTransitions();
                will(returnValue(flowElementCTransitions));
                
                one(flowElementD).getTransitions();
                will(returnValue(flowElementDTransitions));
                
                one(flowElementE).getTransitions();
                will(returnValue(flowElementETransitions));
                
                one(flowElementF).getTransitions();
                will(returnValue(flowElementFTransitions));
            }
        });
    	
        VisitingInvokerFlow visitingInvokerFlow = new VisitingInvokerFlow(null,null, flowElementA, null);
    	
        List<FlowElement> flowElements = visitingInvokerFlow.getFlowElements();
        
        Assert.assertEquals("each flowElement existing in the listing once only", 6, flowElements.size());
    	
        Assert.assertEquals("first Element is A", flowElementA, flowElements.get(0));
        Assert.assertEquals("first Element is B", flowElementB, flowElements.get(1));
        Assert.assertEquals("first Element is C", flowElementC, flowElements.get(2));
        Assert.assertEquals("first Element is D", flowElementD, flowElements.get(3));
        Assert.assertEquals("first Element is E", flowElementE, flowElements.get(4));
        Assert.assertEquals("first Element is F", flowElementF, flowElements.get(5));
        
    }
}
