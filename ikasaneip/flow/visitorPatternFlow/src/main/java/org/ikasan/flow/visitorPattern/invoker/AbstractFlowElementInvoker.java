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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.flow.event.FlowElementInvocationFactory;
import org.ikasan.spec.flow.*;

import java.util.List;

/**
 * An abstract implementation of the FlowElementInvoker
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractFlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = LoggerFactory.getLogger(AbstractFlowElementInvoker.class);

    protected Boolean ignoreContextInvocation = false;
    protected List<FlowInvocationContextListener> flowInvocationContextListeners;

    /** flag to control invocation of the context listeners at runtime, defaults to true */
    protected volatile boolean invokeContextListeners = true;

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
     * @return the new FlowElementInvocation, null if <code>ignoreContextInvocation</code> is true
     */
    @SuppressWarnings("unchecked")
    FlowElementInvocation<Object, ?> beginFlowElementInvocation(FlowInvocationContext flowInvocationContext, FlowElement flowElement, FlowEvent flowEvent)
    {
        if (ignoreContextInvocation)
        {
            // the last invoked component name is always needed in case the recovery manager is invoked
            // whilst the invoker is ignoring the context invocation calls
            flowInvocationContext.setLastComponentName(flowElement.getComponentName());
            return null;
        }
        // blank out the last component, the invoker is now using context invocations
        flowInvocationContext.setLastComponentName(null);
        FlowElementInvocation<Object, ?> flowElementInvocation = FlowElementInvocationFactory.newInvocation();
        flowElementInvocation.setBeforeIdentifier(flowEvent.getIdentifier());
        flowElementInvocation.setBeforeRelatedIdentifier(flowEvent.getRelatedIdentifier());
        flowElementInvocation.beforeInvocation(flowElement);
        flowInvocationContext.addElementInvocation(flowElementInvocation);
        return flowElementInvocation;
    }

    /**
     * Ends the invocation if present
     * @param flowElementInvocation the invocation
     * @param flowElement the current flow element being invoked
     */
    void endFlowElementInvocation(FlowElementInvocation<Object, ?> flowElementInvocation, FlowElement flowElement, FlowEvent flowEvent)
    {
        if (flowElementInvocation != null)
        {
            flowElementInvocation.afterInvocation(flowElement);
            flowElementInvocation.setAfterIdentifier(flowEvent.getIdentifier());
            flowElementInvocation.setAfterRelatedIdentifier(flowEvent.getRelatedIdentifier());
        }
    }

    /**
     * Notify any FlowInvocationContextListeners that to snap an event
     */
    protected void notifyFlowInvocationContextListenersSnapEvent(FlowElement flowElement, FlowEvent flowEvent)
    {
        if(flowElement.getConfiguration() != null && ((FlowElementConfiguration)flowElement.getConfiguration()).getSnapEvent() &&
                ((FlowElementConfiguration)flowElement.getConfiguration()).getCaptureMetrics())
        {
            if (flowInvocationContextListeners != null && this.invokeContextListeners)
            {
                for (FlowInvocationContextListener listener : flowInvocationContextListeners)
                {
                    try
                    {
                        listener.snapEvent(flowElement, flowEvent);
                    }
                    catch (RuntimeException e)
                    {
                        logger.warn("Unable to invoke FlowInvocationContextListener snap event, continuing", e);
                    }
                }
            }
        }
    }

    public void setIgnoreContextInvocation(boolean ignoreContextInvocation)
    {
        this.ignoreContextInvocation = ignoreContextInvocation;
    }

    void setInvocationOnComponent(FlowElementInvocation flowElementInvocation, Object component)
    {
        if (component instanceof InvocationAware)
        {
            ((InvocationAware) component).setFlowElementInvocation(flowElementInvocation);
        }
    }

    void unsetInvocationOnComponent(FlowElementInvocation flowElementInvocation, Object component)
    {
        if (component instanceof InvocationAware)
        {
            ((InvocationAware) component).unsetFlowElementInvocation(flowElementInvocation);
        }
    }

    public void setFlowInvocationContextListeners(List<FlowInvocationContextListener> flowInvocationContextListeners)
    {
        this.flowInvocationContextListeners = flowInvocationContextListeners;
    }

    public void setInvokeContextListeners(boolean invokeContextListeners)
    {
        this.invokeContextListeners = invokeContextListeners;
    }
}

