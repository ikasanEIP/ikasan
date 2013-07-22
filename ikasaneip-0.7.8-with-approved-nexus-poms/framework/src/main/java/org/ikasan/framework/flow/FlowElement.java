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

import java.util.Map;

/**
 * FlowElement represents a particular unique usage of a <code>FlowComponent</code> within a flow.
 * <code>FlowElement</code>s wrap <code>FlowComponent</code>s, providing them with a context specific name. They also
 * define any transitions from this point in flow, this is a mapping of the various potential results that a
 * FlowComponent may return to subsequent (downstream) <code>FlowElement<code>s
 * 
 * @author Ikasan Development Team
 */
public interface FlowElement
{
    /** Name of the default transition for <code>FlowComponent</code>s that have a unique result */
    public static final String DEFAULT_TRANSITION_NAME = "default";

    /**
     * Accessor for the wrapped <code>FlowComponent</code>
     * 
     * @return FlowComponent
     */
    public abstract FlowComponent getFlowComponent();

    /**
     * Accessor for the componentName. This is the unique identifier for a <code>FlowElement</code>
     * 
     * @return componentName
     */
    public abstract String getComponentName();

    /**
     * Retrieves the subsequent FlowElement (if any) representing the next node in the flow
     * 
     * @param transitionName - this value should be a member of the set of possible results returnable from the wrapped
     *            flowComponent
     * @return FlowElement representing the next node in the flow
     */
    public abstract FlowElement getTransition(String transitionName);

    /**
     * Retrieves a Map of all this FlowElement's transitions
     * 
     * @return a Map of all this FlowElement's transitions
     */
    public Map<String, FlowElement> getTransitions();
    
    /**
     * Returns a human readable description of this FlowElement
     * 
     * @return String description
     */
    public String getDescription();
}
