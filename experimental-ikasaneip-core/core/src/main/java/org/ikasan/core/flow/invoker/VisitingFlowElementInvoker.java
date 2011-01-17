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
package org.ikasan.core.flow.invoker;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.core.domain.Event;
import org.ikasan.core.flow.FlowElement;
import org.ikasan.core.flow.InvalidFlowException;
import org.ikasan.core.flow.listener.FlowListener;
import org.ikasan.spec.routing.Router;
import org.ikasan.spec.sequencing.Sequencer;
import org.ikasan.spec.transformation.Translator;

/**
 * A default implementation of the FlowElementInvoker
 * 
 * @author Ikasan Development Team
 */
public class VisitingFlowElementInvoker implements FlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = Logger.getLogger(VisitingFlowElementInvoker.class);


    /** The flow listener */
    private FlowListener flowListener;

    /**
     * Set the flow event listener
     * 
     * @param flowEventListener The flow event listener to set
     */
    public void setFlowEventListener(FlowListener flowListener)
    {
        this.flowListener = flowListener;
    }

    /* (non-Javadoc)
     * @see org.ikasan.core.flow.invoker.FlowElementInvoker#invoke(org.ikasan.core.flow.invoker.FlowInvocationContext, org.ikasan.core.Event, java.lang.String, java.lang.String, org.ikasan.core.flow.FlowElement)
     */
    public void invoke(FlowInvocationContext flowInvocationContext, Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        while (flowElement != null)
        {
           flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
            notifyListenersBeforeElement(event, moduleName, flowName, flowElement);
            Object flowComponent = flowElement.getFlowComponent();
            if (flowComponent instanceof Translator)
            {
                handleTransformer(event, moduleName, flowName, flowElement);

                // sort out the next element
                FlowElement previousFlowElement = flowElement;
                flowElement = this.getDefaultTransition(flowElement);
                if (flowElement == null)
                {
                    logger.error("transformer is last element in flow!");
                    throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                            + "] contains a Transfomer, but it has no default transition! "
                            + "Transformers should never be the last component in a flow");
                }
            }
//            else if (flowComponent instanceof Endpoint)
//            {
//                handleEndpoint(event, moduleName, flowName, flowElement);
//
//                flowElement = getDefaultTransition(flowElement);
//            }
            else if (flowComponent instanceof Router)
            {
                handleRouter(flowInvocationContext, event, moduleName, flowName, flowElement);
                break;
            }
            else if (flowComponent instanceof Sequencer)
            {
                handleSequencer(flowInvocationContext, event, moduleName, flowName, flowElement);
                break;
            }
            else
            {
                throw new RuntimeException("Unhandled FlowComponent type:" + flowComponent.getClass());
            }
        }

    }

    /**
     * The behaviour for visiting a <code>Sequencer</code>
     * @param flowInvocationContext 
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleSequencer(FlowInvocationContext flowInvocationContext, Event event, 
            String moduleName, String flowName,
            FlowElement flowElement)
    {
            Sequencer sequencer = (Sequencer) flowElement.getFlowComponent();

            List events = sequencer.sequence(event.getPayload());
            if (events != null)
            {
                notifyListenersAfterSequencerElement(events, moduleName, flowName, flowElement);
            }
            FlowElement nextFlowElement = this.getDefaultTransition(flowElement);
            if (nextFlowElement == null)
            {
                logger.error("sequencer is last element in flow!");
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] contains a Sequencer, but it has no default transition! "
                        + "Sequencers should never be the last component in a flow");
            }
            if (events != null)
            {
                for (Object constituentEvent : events)
                {
                    invoke(flowInvocationContext, spawnEvent(constituentEvent), moduleName, flowName, nextFlowElement);

                }
            }
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
        if (flowListener != null)
        {
            try
            {
            	flowListener.beforeFlowElement(moduleName, flowName, flowElement, event);
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
        if (flowListener != null)
        {
            try
            {
            	flowListener.afterFlowElement(moduleName, flowName, flowElement, event);
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
     */
    private void handleRouter(FlowInvocationContext flowInvocationContext, Event event, String moduleName, String flowName, FlowElement flowElement)
    {
        Router router = (Router) flowElement.getFlowComponent();


        List<String> targetNames = router.route(event);
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
            invoke(flowInvocationContext, spawnEvent(event), moduleName, flowName, nextFlowElement);

        }
        
    }

    /**
     * The behaviour for visiting a <code>Endpoint</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
//    private void handleEndpoint(Event event, String moduleName, String flowName,
//            FlowElement flowElement)
//    {
//        Endpoint endpoint = (Endpoint) flowElement.getFlowComponent();
//        endpoint.route(event);
//        notifyListenersAfterElement(event, moduleName, flowName, flowElement);
//
//    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleTransformer(Event event, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Translator translator = (Translator) flowElement.getFlowComponent();

        translator.translate(event.getPayload());
        notifyListenersAfterElement(event, moduleName, flowName, flowElement);

    }

    /**
     * Creates a new Event representing the specified Event. This ensures that any downstream changes to the spawned
     * event do not affect the original
     * 
     * @param originalEvent The original event
     * @return new Event, representing the original
     * @throws CloneNotSupportedException Exception if we could not clone the event
     */
    private Event spawnEvent(Object originalEvent) 
    {
        Event clone = new Event("id"); //TODO
		clone.setPayload(originalEvent);
		return clone;
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
