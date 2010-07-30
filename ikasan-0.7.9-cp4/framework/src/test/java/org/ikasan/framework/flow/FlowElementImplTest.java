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

import static org.junit.Assert.*;

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
