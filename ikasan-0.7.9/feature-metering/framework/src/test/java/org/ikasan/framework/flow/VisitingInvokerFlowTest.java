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
                one(flowElementInvoker).invoke(event, moduleName, flowName, flowElement);
                will(returnValue(ikasanExceptionAction));
            }
        });
        
        
        IkasanExceptionAction exceptionAction = visitingInvokerFlow.invoke(event);
        Assert.assertEquals("ExceptionAction returned by the flow should be the same one returned internally by the invoker", ikasanExceptionAction, exceptionAction);
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
