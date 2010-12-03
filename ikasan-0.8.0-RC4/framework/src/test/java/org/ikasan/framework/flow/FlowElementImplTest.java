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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class FlowElementImplTest
{
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    
    /**
     * Mocked FlowComponent
     */
    private FlowComponent flowComponent = mockery.mock(FlowComponent.class);
    
    /**
     * Mocked downstream FlowElement
     */
    private FlowElement downstreamFlowElementA = mockery.mock(FlowElement.class);
    
    /**
     * Mocked downstream FlowElement
     */
    private FlowElement downstreamFlowElementB = mockery.mock(FlowElement.class);
    
    
    /**
     * Name of component
     */
    private String componentName = "componentName";
    
    /**
     * Test method for {@link org.ikasan.framework.flow.FlowElementImpl#FlowElementImpl(java.lang.String, org.ikasan.framework.flow.FlowComponent, java.util.Map)}.
     */
    @Test
    public void testConstructor_withTransitionMap_setsAllPropertiesCorrectly()
    {
        Map<String, FlowElement> transitions = new HashMap<String, FlowElement>();
        final String transitionAName = "transitionA";
        final String transitionBName = "transitionB";
        
        transitions.put(transitionAName, downstreamFlowElementA);
        transitions.put(transitionBName, downstreamFlowElementB);
        
        //call the constructor
        FlowElement flowElement = new FlowElementImpl(componentName, flowComponent, transitions);
    
        //test the properties
        assertEquals("componentName should match that set on constructor", componentName, flowElement.getComponentName());
        assertEquals("flowComponent should match that set on constructor", flowComponent, flowElement.getFlowComponent());
        assertEquals("getTransition should index into transitions map set on constructor", downstreamFlowElementA, flowElement.getTransition(transitionAName));
        assertEquals("getTransition should index into transitions map set on constructor", downstreamFlowElementB, flowElement.getTransition(transitionBName));
        assertNull("getTransition with default transition name should return null", flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));

    }

    /**
     * Test method for {@link org.ikasan.framework.flow.FlowElementImpl#FlowElementImpl(java.lang.String, org.ikasan.framework.flow.FlowComponent, org.ikasan.framework.flow.FlowElement)}.
     */
    @Test
    public void testConstructor_withSingleFlowElement_setsAllPropertiesCorrectly()
    {
        //call the constructor
        FlowElement flowElement = new FlowElementImpl(componentName, flowComponent, downstreamFlowElementB);
    
        //test the properties
        assertEquals("componentName should match that set on constructor", componentName, flowElement.getComponentName());
        assertEquals("flowComponent should match that set on constructor", flowComponent, flowElement.getFlowComponent());
        assertEquals("getTransition with default transition name should return flowElement set on constructor",downstreamFlowElementB, flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));
    }
    
    /**
     * Test method for {@link org.ikasan.framework.flow.FlowElementImpl#FlowElementImpl(java.lang.String, org.ikasan.framework.flow.FlowComponent, org.ikasan.framework.flow.FlowElement)}.
     */
    @Test
    public void testConstructor_withNoTransitions_setsAllPropertiesCorrectly()
    {
        //call the constructor
        FlowElement flowElement = new FlowElementImpl(componentName, flowComponent);
    
        //test the properties
        assertEquals("componentName should match that set on constructor", componentName, flowElement.getComponentName());
        assertEquals("flowComponent should match that set on constructor", flowComponent, flowElement.getFlowComponent());
        assertNull("getTransition with default transition name should return null", flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME));
   }
}
