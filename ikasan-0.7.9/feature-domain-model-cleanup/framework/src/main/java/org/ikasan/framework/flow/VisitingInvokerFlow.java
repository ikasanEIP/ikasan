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
