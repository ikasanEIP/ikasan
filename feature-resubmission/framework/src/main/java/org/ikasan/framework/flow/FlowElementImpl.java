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
import java.util.Map;

/**
 * Simple implementation of <code>FlowElement</code>
 * 
 * @author Ikasan Development Team
 */
public class FlowElementImpl implements FlowElement
{
    /** <code>FlowComponent</code> being wrapped and given flow context */
    private FlowComponent flowComponent;

    /** Flow context specific name for the wrapped component */
    private String componentName;

    /** <code>Map</code> of all flowComponent results to downstream <code>FlowElement</code>s */
    private Map<String, FlowElement> transitions;
    
    /**
     * Human readable description of this FlowElement
     */
    private String description;


    /**
     * Constructor for when there are more than one subsequent <code>FlowElement</code>s
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     * @param transitions A map of transitions
     */
    public FlowElementImpl(String componentName, FlowComponent flowComponent, Map<String, FlowElement> transitions)
    {
        this.componentName = componentName;
        this.flowComponent = flowComponent;
        this.transitions = transitions;
    }

    /**
     * Overloaded constructor for when there is at most one subsequent <code>FlowElement</code>
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     * @param defaultTransition The default transition
     */
    public FlowElementImpl(String componentName, FlowComponent flowComponent, FlowElement defaultTransition)
    {
        this(componentName, flowComponent, createTransitionMap(defaultTransition));
    }

    /**
     * Overloaded constructor for a <code>FlowElement</code> with no downstream
     * 
     * @param componentName The name of the component
     * @param flowComponent The FlowComponent
     */
    public FlowElementImpl(String componentName, FlowComponent flowComponent)
    {
        this(componentName, flowComponent, (Map<String, FlowElement>) null);
    }

    /**
     * Creates the transition map when there is just the default transition
     * 
     * @param defaultTransition The default transition
     * @return Map<String, FlowElement> mapping "default" to the specified <code>FlowElement</code>
     */
    private static Map<String, FlowElement> createTransitionMap(FlowElement defaultTransition)
    {
        Map<String, FlowElement> defaultTransitions = new HashMap<String, FlowElement>();
        defaultTransitions.put(DEFAULT_TRANSITION_NAME, defaultTransition);
        return defaultTransitions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getFlowComponent()
     */
    public FlowComponent getFlowComponent()
    {
        return flowComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getComponentName()
     */
    public String getComponentName()
    {
        return componentName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.FlowElement#getTransition(java.lang.String)
     */
    public FlowElement getTransition(String transitionName)
    {
        FlowElement result = null;
        if (transitions != null)
        {
            result = transitions.get(transitionName);
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.flow.FlowElement#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Setter for description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getName() + " [");
        sb.append("name=");
        sb.append(componentName);
        sb.append(",");
        sb.append("flowComponent=");
        sb.append(flowComponent);
        sb.append(",");
        sb.append("transitions=");
        sb.append(transitions);
        sb.append("]");
        return sb.toString();
    }

    public Map<String, FlowElement> getTransitions()
    {
        Map<String, FlowElement> result = new HashMap<String, FlowElement>();
        if (transitions != null)
        {
            result.putAll(transitions);
        }
        return result;
    }
}
