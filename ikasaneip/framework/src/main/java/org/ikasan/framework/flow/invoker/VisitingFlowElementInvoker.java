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
package org.ikasan.framework.flow.invoker;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.component.endpoint.Endpoint;
import org.ikasan.framework.component.routing.Router;
import org.ikasan.framework.component.sequencing.Sequencer;
import org.ikasan.framework.component.transformation.Transformer;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.FlowComponent;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.InvalidFlowException;
import org.ikasan.framework.flow.event.listener.FlowEventListener;

/**
 * A default implementation of the FlowElementInvoker
 * 
 * @author Ikasan Development Team
 */
public class VisitingFlowElementInvoker implements FlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = Logger.getLogger(VisitingFlowElementInvoker.class);

    /** Exception Handler for interpreting any exceptions thrown during component invocation */
    private IkasanExceptionHandler exceptionHandler;

    /** The flow event listener */
    private FlowEventListener flowEventListener;

    /**
     * Set the flow event listener
     * 
     * @param flowEventListener The flow event listener to set
     */
    public void setFlowEventListener(FlowEventListener flowEventListener)
    {
        this.flowEventListener = flowEventListener;
    }

    /**
     * Constructor
     * 
     * @param exceptionHandler The Exception Handler to use for this flow
     */
    public VisitingFlowElementInvoker(IkasanExceptionHandler exceptionHandler)
    {
        this.exceptionHandler = exceptionHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flow.visitinginvoker.FlowElementInvoker#invoke(event.Event, flow.FlowElement)
     */
    public IkasanExceptionAction invoke(Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        while (flowElement != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Invoking [" + flowElement.getComponentName() + "] of [" + flowName + "] " + event.idToString());
            }
            notifyListenersBeforeElement(event, moduleName, flowName, flowElement);
            FlowComponent flowComponent = flowElement.getFlowComponent();
            if (flowComponent instanceof Transformer)
            {
                IkasanExceptionAction result = handleTransformer(event, moduleName, flowName, flowElement);
                if (result != null)
                {
                    return result;
                }
                // sort out the next element
                FlowElement previousFlowElement = flowElement;
                flowElement = getDefaultTransition(flowElement);
                if (flowElement == null)
                {
                    logger.error("transformer is last element in flow!");
                    throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                            + "] contains a Transfomer, but it has no default transition! "
                            + "Transformers should never be the last component in a flow");
                }
            }
            else if (flowComponent instanceof Endpoint)
            {
                IkasanExceptionAction result = handleEndpoint(event, moduleName, flowName, flowElement);
                if (result != null)
                {
                    return result;
                }
                flowElement = getDefaultTransition(flowElement);
            }
            else if (flowComponent instanceof Router)
            {
                IkasanExceptionAction result = handleRouter(event, moduleName, flowName, flowElement);
                if (result != null)
                {
                    return result;
                }
                break;
            }
            else if (flowComponent instanceof Sequencer)
            {
                IkasanExceptionAction result = handleSequencer(event, moduleName, flowName, flowElement);
                if (result != null)
                {
                    return result;
                }
                break;
            }
            else
            {
                logger.error("Unhandled FlowComponent type:" + flowComponent.getClass());
                return new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
            }
        }
        // if we get this far, then there were no problems, and thus not
        // IkasanExceptionAction to return
        return null;
    }

    /**
     * The behaviour for visiting a <code>Sequencer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     * @return IkasanExceptionAction if any problem occurs
     */
    private IkasanExceptionAction handleSequencer(Event event, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Sequencer sequencer = (Sequencer) flowElement.getFlowComponent();
        IkasanExceptionAction result = null;
        try
        {
            List<Event> events = sequencer.onEvent(event, moduleName, flowElement.getComponentName());
            if (events!=null){
                notifyListenersAfterSequencerElement(events, moduleName, flowName, flowElement);
            }
            FlowElement nextFlowElement = getDefaultTransition(flowElement);
            if (nextFlowElement == null)
            {
                logger.error("sequencer is last element in flow!");
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] contains a Sequencer, but it has no default transition! "
                        + "Sequencers should never be the last component in a flow");
            }
            if (events != null)
            {
                for (Event constituentEvent : events)
                {
                    result = this.invoke(spawnEvent(constituentEvent), moduleName, flowName, nextFlowElement);
                    if (result != null)
                    {
                        break;
                    }
                }
            }
        }
        catch (Throwable throwable)
        {
            result = exceptionHandler.invoke(flowElement.getComponentName(), event, throwable);
        }
        return result;
    }

    /**
     * Helper method to notify listeners before a flow element is invoked
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void notifyListenersBeforeElement(Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.beforeFlowElement(moduleName, flowName, flowElement, event);
            }
            catch (Throwable t)
            {
                logger.error("flowEventListener caught throwable before flowElement [" + flowElement
                        + "], exception is[" + t + "]", t);
                for (StackTraceElement stackTraceElement : t.getStackTrace())
                {
                    logger.error(stackTraceElement);
                }
            }
        }
    }

    /**
     * Helper method to notify listeners after a flow element is invoked
     *  
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void notifyListenersAfterElement(Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.afterFlowElement(moduleName, flowName, flowElement, event);
            }
            catch (Throwable t)
            {
                logger.error("flowEventListener caught throwable after flowElement [" + flowElement
                        + "], exception is[" + t + "]", t);
                for (StackTraceElement stackTraceElement : t.getStackTrace())
                {
                    logger.error(stackTraceElement);
                }
            }
        }
    }

    /**
     * Helper method to notify listeners after a sequencer element is invoked
     * 
     * @param events The list of events we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void notifyListenersAfterSequencerElement(List<Event> events, String moduleName, String flowName,
            FlowElement flowElement)
    {
        for (Event event : events)
        {
            notifyListenersAfterElement(event, moduleName, flowName, flowElement);
        }
    }

    /**
     * The behaviour for visiting a <code>Router</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     * @return IkasanExceptionAction if any problem occurs
     */
    private IkasanExceptionAction handleRouter(Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        Router router = (Router) flowElement.getFlowComponent();
        IkasanExceptionAction result = null;
        try
        {
            List<String> targetNames = router.onEvent(event);
            notifyListenersAfterElement(event, moduleName, flowName, flowElement);
            for (String targetName : targetNames)
            {
                final FlowElement nextFlowElement = flowElement.getTransition(targetName);
                if (nextFlowElement == null)
                {
                    logger.error("router is last element in flow!");
                    throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                            + "] contains a Router, but it does not have a transition mapped for that Router's target["
                            + targetName + "] "
                            + "All Router targets must be mapped to transitions in their enclosing FlowElement");
                }
                result = this.invoke(spawnEvent(event), moduleName, flowName, nextFlowElement);
                if (result != null)
                {
                    break;
                }
            }
        }
        catch (Throwable throwable)
        {
            result = exceptionHandler.invoke(flowElement.getComponentName(), event, throwable);
        }
        return result;
    }

    /**
     * The behaviour for visiting a <code>Endpoint</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     * @return IkasanExceptionAction if any problem occurs
     */
    private IkasanExceptionAction handleEndpoint(Event event, String moduleName, String flowName,
            FlowElement flowElement)
    {
        IkasanExceptionAction result = null;
        Endpoint endpoint = (Endpoint) flowElement.getFlowComponent();
        try
        {
            endpoint.onEvent(event);
            notifyListenersAfterElement(event, moduleName, flowName, flowElement);
        }
        catch (Throwable throwable)
        {
            IkasanExceptionAction ikasanExceptionAction = exceptionHandler.invoke(flowElement.getComponentName(),
                event, throwable);
            if (!ikasanExceptionAction.getType().equals(IkasanExceptionActionType.CONTINUE))
            {
                result = ikasanExceptionAction;
            }
        }
        return result;
    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     * @return IkasanExceptionAction if any problem occurs
     */
    private IkasanExceptionAction handleTransformer(Event event, String moduleName, String flowName,
            FlowElement flowElement)
    {
        IkasanExceptionAction result = null;
        Transformer transformer = (Transformer) flowElement.getFlowComponent();
        try
        {
            transformer.onEvent(event);
            notifyListenersAfterElement(event, moduleName, flowName, flowElement);
        }
        catch (Throwable throwable)
        {
            IkasanExceptionAction ikasanExceptionAction = exceptionHandler.invoke(flowElement.getComponentName(),
                event, throwable);
            if (!ikasanExceptionAction.getType().equals(IkasanExceptionActionType.CONTINUE))
            {
                result = ikasanExceptionAction;
            }
        }
        return result;
    }

    /**
     * Creates a new Event representing the specified Event. This ensures that any downstream changes to the spawned
     * event do not affect the original
     * 
     * @param originalEvent The original event
     * @return new Event, representing the original
     * @throws CloneNotSupportedException Exception if we could not clone the event
     */
    private Event spawnEvent(Event originalEvent) throws CloneNotSupportedException
    {
        return originalEvent.clone();
    }

    /**
     * Retrieves the default transition if any for this flowElement
     * 
     * @param flowElement The flow element we want the transition for
     * @return default transition
     */
    private FlowElement getDefaultTransition(FlowElement flowElement)
    {
        return flowElement.getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
    }
}
