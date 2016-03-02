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
package org.ikasan.flow.visitorPattern.invoker;

import org.apache.log4j.Logger;
import org.ikasan.flow.event.FlowElementInvocationFactory;
import org.ikasan.spec.flow.*;

/**
 * An abstract implementation of the FlowElementInvoker
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractFlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = Logger.getLogger(AbstractFlowElementInvoker.class);

    /**
     * Helper method to notify listeners before a flow element is invoked
     *
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    void notifyListenersBeforeElement(FlowEventListener flowEventListener, String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.beforeFlowElement(moduleName, flowName, flowElement, flowEvent);
            }
            catch (Throwable t)
            {
                logger.error("flowEventListener caught throwable before flowElement [" + flowElement + "], exception is[" + t + "]", t);
            }
        }
    }

    /**
     * Helper method to notify listeners after a flow element is invoked
     *
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    void notifyListenersAfterElement(FlowEventListener flowEventListener, String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.afterFlowElement(moduleName, flowName, flowElement, flowEvent);
            }
            catch (Throwable t)
            {
                logger.error("flowEventListener caught throwable after flowElement [" + flowElement + "], exception is[" + t + "]", t);
            }
        }
    }

    /**
     * Retrieves the default transition if any for this flowElement
     *
     * @param flowElement The flow element we want the transition for
     * @return default transition
     */
    FlowElement getDefaultTransition(FlowElement flowElement)
    {
        return flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
    }

    /**
     * Creates a new FlowElementInvocation and adds it the FlowInvocationContext
     * @param flowInvocationContext the context
     * @param flowElement the current flow element being invoked
     * @param flowEvent the current flow event
     * @return the new FlowElementInvocation
     */
    @SuppressWarnings("unchecked")
    FlowElementInvocation beginFlowElementInvocation(FlowInvocationContext flowInvocationContext, FlowElement flowElement, FlowEvent flowEvent)
    {
        FlowElementInvocation flowElementInvocation = FlowElementInvocationFactory.newInvocation();
        flowElementInvocation.setIdentifier(flowEvent.getIdentifier());
        flowElementInvocation.beforeInvocation(flowElement);
        flowInvocationContext.addInvocation(flowElementInvocation);
        return flowElementInvocation;
    }

    /**
     * Ends the invocation
     * @param flowElementInvocation the invocation
     * @param flowElement the current flow element being invoked
     */
    void endFlowElementInvocation(FlowElementInvocation flowElementInvocation, FlowElement flowElement)
    {
        flowElementInvocation.afterInvocation(flowElement);
    }
}

