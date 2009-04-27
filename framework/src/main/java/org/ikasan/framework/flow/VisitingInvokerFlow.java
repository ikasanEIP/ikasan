/* 
 * $Id: VisitingInvokerFlow.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/VisitingInvokerFlow.java $
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

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.invoker.FlowElementInvoker;

/**
 * Default implementation of a Flow
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlow implements Flow
{
    /**
     * Name of this flow
     */
    private String name;

    /**
     * Name of the module within which this flowExists
     */
    private String moduleName;

    /**
     * The first element in this flow
     */
    private FlowElement headElement;

    /**
     * Invoker for invoking this flow
     */
    private FlowElementInvoker flowElementInvoker;

    /**
     * Constructor
     * 
     * @param name - name of this flow
     * @param moduleName - name of the module containing this flow
     * @param headElement - first element in the flow
     * @param visitingInvoker - invoker for this flow
     */
    public VisitingInvokerFlow(String name, String moduleName, FlowElement headElement,
            FlowElementInvoker visitingInvoker)
    {
        super();
        this.name = name;
        this.moduleName = moduleName;
        this.headElement = headElement;
        this.flowElementInvoker = visitingInvoker;
    }

    public String getName()
    {
        return name;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.Flow#invoke(org.ikasan.framework.component.Event)
     */
    public IkasanExceptionAction invoke(Event event)
    {
        return flowElementInvoker.invoke(event, moduleName, name, headElement);
    }

    /**
     * Returns a breadth first listing of the flowElements within this flow
     * 
     * @return List<FlowElement>
     */
    public List<FlowElement> getFlowElements()
    {
        List<FlowElement> result = new ArrayList<FlowElement>();
        List<FlowElement> elementsToVisit = new ArrayList<FlowElement>();
        elementsToVisit.add(headElement);
        while (!elementsToVisit.isEmpty())
        {
            FlowElement thisFlowElement = elementsToVisit.get(0);
            elementsToVisit.remove(0);
            if (!result.contains(thisFlowElement))
            {
                result.add(thisFlowElement);
            }
            for (FlowElement subsequentElement : thisFlowElement.getTransitions().values())
            {
                if (!result.contains(subsequentElement))
                {
                    elementsToVisit.add(subsequentElement);
                }
            }
        }
        return result;
    }

    /**
     * Set the module name
     * 
     * @param moduleName The name of the module to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getModuleName()
    {
        return moduleName;
    }
}
