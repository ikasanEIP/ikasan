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
package org.ikasan.flow.visitorPattern;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowComponent;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.event.FlowEvent;

/**
 * A default implementation of the FlowElementInvoker
 * 
 * @author Ikasan Development Team
 */
public class VisitingFlowElementInvoker implements FlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = Logger.getLogger(VisitingFlowElementInvoker.class);


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


    /*
     * (non-Javadoc)
     * 
     * @see flow.visitinginvoker.FlowElementInvoker#invoke(event.Event, flow.FlowElement)
     */
    public void invoke(FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, String moduleName, String flowName, FlowElement flowElement)
    {
        while (flowElement != null)
        {
           flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
            if (logger.isDebugEnabled())
            {
                logger.debug("Invoking [" + flowElement.getComponentName() + "] of [" + flowName + "] " + flowEvent.getIdentifier());
            }
            notifyListenersBeforeElement(flowEvent, moduleName, flowName, flowElement);
            FlowComponent flowComponent = flowElement.getFlowComponent();

            try
            {
                if (flowComponent instanceof Translator)
                {
                    handleTranslator(flowEvent, moduleName, flowName, flowElement);

                    // sort out the next element
                    FlowElement previousFlowElement = flowElement;
                    flowElement = getDefaultTransition(flowElement);
                    if (flowElement == null)
                    {
                        logger.error("translator is last element in flow!");
                        throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                                + "] contains a Transfomer, but it has no default transition! "
                                + "Transformers should never be the last component in a flow");
                    }
                }
                else if (flowComponent instanceof Converter)
                {
                    handleConverter(flowEvent, moduleName, flowName, flowElement);

                    // sort out the next element
                    FlowElement previousFlowElement = flowElement;
                    flowElement = getDefaultTransition(flowElement);
                    if (flowElement == null)
                    {
                        logger.error("converter is last element in flow!");
                        throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                                + "] contains a Transfomer, but it has no default transition! "
                                + "Transformers should never be the last component in a flow");
                    }
                }
                else if (flowComponent instanceof Producer)
                {
                    handleProducer(flowEvent, moduleName, flowName, flowElement);

                    // TODO - producer should be last element in the flow
                    flowElement = getDefaultTransition(flowElement);
                }
                else if (flowComponent instanceof Consumer)
                {
                    handleConsumer(flowEvent, moduleName, flowName, flowElement);

                    // TODO - producer should be last element in the flow
                    flowElement = getDefaultTransition(flowElement);
                }
                else if (flowComponent instanceof Broker)
                {
                    handleBroker(flowEvent, moduleName, flowName, flowElement);

                    // TODO - producer should be last element in the flow
                    flowElement = getDefaultTransition(flowElement);
                }
                else if (flowComponent instanceof Router)
                {
                    handleRouter(flowInvocationContext, flowEvent, moduleName, flowName, flowElement);
                    break;
                }
                else if (flowComponent instanceof Sequencer)
                {
                    handleSequencer(flowInvocationContext, flowEvent, moduleName, flowName, flowElement);
                    break;
                }
                else
                {
                    throw new RuntimeException("Unhandled FlowComponent type:" + flowComponent.getClass());
                }
            }
            catch(ClassCastException e)
            {
                throw new RuntimeException("Unable to find method signature on component ["
                		+ flowElement.getComponentName()
                		+ "] for payload class ["
                		+ flowEvent.getPayload().getClass().getName() + "]", e);
            }
        }

    }

    /**
     * The behaviour for visiting a <code>Sequencer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleSequencer(FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Sequencer sequencer = (Sequencer) flowElement.getFlowComponent();
        List payloads = sequencer.sequence(flowEvent.getPayload());

        FlowElement nextFlowElement = getDefaultTransition(flowElement);
        if (nextFlowElement == null)
        {
            logger.error("sequencer is last element in flow!");
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                    + "] contains a Sequencer, but it has no default transition! "
                    + "Sequencers should never be the last component in a flow");
        }
        if(payloads != null)
    	{
        	for(Object payload:payloads)
            {
            	flowEvent.setPayload(payload);
            	notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
                invoke(flowInvocationContext, flowEvent, moduleName, flowName, nextFlowElement);
            }
    	}
        
//        List<FlowEvent> events = sequencer.onEvent(flowEvent, moduleName, flowElement.getComponentName());
//        if (events!=null){
//            notifyListenersAfterSequencerElement(events, moduleName, flowName, flowElement);
//        }
//        if (events != null)
//        {
//            for (FlowEvent constituentEvent : events)
//            {
//                invoke(flowInvocationContext, spawnEvent(constituentEvent), moduleName, flowName, nextFlowElement);
//
//            }
//        }
    }

    /**
     * Helper method to notify listeners before a flow element is invoked
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void notifyListenersBeforeElement(FlowEvent flowEvent, String moduleName, String flowName, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.beforeFlowElement(moduleName, flowName, flowElement, flowEvent);
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
    private void notifyListenersAfterElement(FlowEvent flowEvent, String moduleName, String flowName, FlowElement flowElement)
    {
        if (flowEventListener != null)
        {
            try
            {
                flowEventListener.afterFlowElement(moduleName, flowName, flowElement, flowEvent);
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

//    /**
//     * Helper method to notify listeners after a sequencer element is invoked
//     * 
//     * @param events The list of events we're passing on
//     * @param moduleName The name of the module
//     * @param flowName The name of the flow
//     * @param flowElement The flow element we're dealing with
//     */
//    private void notifyListenersAfterSequencerElement(List<FlowEvent> flowEvents, String moduleName, String flowName,
//            FlowElement flowElement)
//    {
//        for (FlowEvent flowEvent : flowEvents)
//        {
//            notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
//        }
//    }

    /**
     * The behaviour for visiting a <code>Router</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleRouter(FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, String moduleName, String flowName, FlowElement flowElement)
    {
        Router router = (Router) flowElement.getFlowComponent();

        List<String> targetNames = router.route(flowEvent.getPayload());
        if(targetNames.size() > 1 && !(flowEvent.getPayload() instanceof Cloneable))
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                    + "] trying to route a non-cloneable payload to multiple targets "
                    + "To do this payload [" + flowEvent.getPayload().getClass().getName() 
                    + "] must implement Cloneable");
        }
        	
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
        for (String targetName : targetNames)
        {
            final FlowElement nextFlowElement = flowElement.getTransition(targetName);
            if (nextFlowElement == null)
            {
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] contains a Router, but it does not have a transition mapped for that Router's target["
                        + targetName + "] "
                        + "All Router targets must be mapped to transitions in their enclosing FlowElement");
            }
//            Object obj = flowEvent.getPayload();
//            ((Cloneable)obj).clone();
//            invoke(flowInvocationContext, spawnEvent(flowEvent), moduleName, flowName, nextFlowElement);
            // TODO !!! IMPORTANT - need to determine how best to clone payloads
            invoke(flowInvocationContext, flowEvent, moduleName, flowName, nextFlowElement);
        }
        
    }

    /**
     * The behaviour for visiting a <code>Producer</code> component endpoint
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleProducer(FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Producer producer = (Producer) flowElement.getFlowComponent();
        producer.invoke(flowEvent.getPayload());
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
    }

    // TODO - consumer needs to really pass enough for event generation
    private void handleConsumer(FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Consumer consumer = (Consumer) flowElement.getFlowComponent();
        flowEvent.setPayload(consumer.invoke());
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
    }

    private void handleBroker(FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Broker broker = (Broker) flowElement.getFlowComponent();
        flowEvent.setPayload(broker.invoke(flowEvent.getPayload()));
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleTranslator(FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Translator translator = (Translator) flowElement.getFlowComponent();
        translator.translate(flowEvent.getPayload());
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void handleConverter(FlowEvent flowEvent, String moduleName, String flowName,
            FlowElement flowElement)
    {
        Converter converter = (Converter) flowElement.getFlowComponent();
        flowEvent.setPayload( converter.convert(flowEvent.getPayload()) );
        notifyListenersAfterElement(flowEvent, moduleName, flowName, flowElement);
    }

    /**
     * Creates a new Event representing the specified Event. This ensures that any downstream changes to the spawned
     * event do not affect the original
     * 
     * @param originalEvent The original event
     * @return new Event, representing the original
     * @throws CloneNotSupportedException Exception if we could not clone the event
     */
//    private FlowEvent spawnEvent(FlowEvent originalEvent) 
//    {
//    	FlowEvent clone = null;
//        
//        try {
//			clone=originalEvent.clone();
//		} catch (CloneNotSupportedException e) {
//			throw new RuntimeException(e);
//		}
//		return clone;
//    }

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
