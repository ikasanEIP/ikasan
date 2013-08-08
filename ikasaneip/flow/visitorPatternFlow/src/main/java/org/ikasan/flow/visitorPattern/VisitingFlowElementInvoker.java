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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.Router;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContext;

/**
 * A default implementation of the FlowElementInvoker
 * 
 * @author Ikasan Development Team
 */
public class VisitingFlowElementInvoker implements FlowElementInvoker
{
    /** logger instance */
    private static final Logger logger = Logger.getLogger(VisitingFlowElementInvoker.class);

    /** replication factory - requirement for flows where event can undergo a number of sequential routes */
    private ReplicationFactory<FlowEvent<?,?>> replicationFactory;

    /** The flow event listener */
    private FlowEventListener flowEventListener;

    /** cache whether the producer invocation requires full event */
    private Map<String,Boolean> requiresFullEvent = new HashMap<String,Boolean>();

    /**
     * Constructor with a ReplicationFactory for replicating events for specific
     * flow component requirements i.e. multi-recipient router.
     * 
     * @param replicationFactory
     */
    public VisitingFlowElementInvoker(ReplicationFactory<FlowEvent<?,?>> replicationFactory)
    {
        this.replicationFactory = replicationFactory;
    }

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
     * @see flow.visitinginvoker.FlowElementInvoker#invoke(event.Event,
     * flow.FlowElement)
     */
    public void invoke(String moduleName, String flowName, FlowInvocationContext flowInvocationContext, 
            FlowEvent flowEvent, FlowElement flowElement)
    {
        while (flowElement != null)
        {
            flowInvocationContext.addInvokedComponentName(flowElement.getComponentName());
            notifyListenersBeforeElement(moduleName, flowName, flowEvent, flowElement);
            Object flowComponent = flowElement.getFlowComponent();
            try
            {
                if (flowComponent instanceof Consumer)
                {
                    flowElement = handleConsumer(moduleName, flowName, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Translator)
                {
                    flowElement = handleTranslator(moduleName, flowName, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Converter)
                {
                    flowElement = handleConverter(moduleName, flowName, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Producer)
                {
                    flowElement = handleProducer(moduleName, flowName, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Broker)
                {
                    flowElement = handleBroker(moduleName, flowName, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Router)
                {
                    flowElement = handleRouter(moduleName, flowName, flowInvocationContext, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Sequencer)
                {
                    flowElement = handleSequencer(moduleName, flowName, flowInvocationContext, flowEvent, flowElement);
                }
                else if (flowComponent instanceof Filter)
                {
                    flowElement = handleFilter(moduleName, flowName, flowEvent, flowElement);
                }
                else
                {
                    throw new RuntimeException("Unknown FlowComponent type[" + flowComponent.getClass() + "]");
                }
            }
            catch (ClassCastException e)
            {
                throw new RuntimeException("Unable to find method signature in module[" 
                    + moduleName + "] flow[" + flowName + "] on component [" 
                    + flowElement.getComponentName() + "] for payload class ["
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
    private FlowElement handleSequencer(String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement flowElement)
    {
        Sequencer sequencer = (Sequencer) flowElement.getFlowComponent();
        List payloads = sequencer.sequence(flowEvent.getPayload());
        FlowElement nextFlowElement = getDefaultTransition(flowElement);
        if (nextFlowElement == null)
        {
            logger.error("sequencer is last element in flow!");
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Sequencer, but it has no default transition! "
                    + "Sequencers should never be the last component in a flow");
        }
        if (payloads != null)
        {
            for (Object payload : payloads)
            {
                flowEvent.setPayload(payload);
                notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
                invoke(moduleName, flowName, flowInvocationContext, flowEvent, nextFlowElement);
            }
        }
        return null;
    }

    /**
     * Helper method to notify listeners before a flow element is invoked
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private void notifyListenersBeforeElement(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
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
    private void notifyListenersAfterElement(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
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
                for (StackTraceElement stackTraceElement : t.getStackTrace())
                {
                    logger.error(stackTraceElement);
                }
            }
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
    private FlowElement handleRouter(String moduleName, String flowName, 
            FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement flowElement)
    {
        Router router = (Router) flowElement.getFlowComponent();
        List<String> targetNames = router.route(flowEvent.getPayload());
        if (targetNames == null || targetNames.size() == 0)
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Router without a valid transition. "
                    + "All Routers must result in at least one transition.");
        }
        
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        if (targetNames.size() == 1)
        {
            String targetName = targetNames.get(0);
            final FlowElement nextFlowElement = flowElement.getTransition(targetName);
            if (nextFlowElement == null)
            {
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] contains a Router, but it does not have a transition mapped for that Router's target[" + targetName + "] "
                        + "All Router targets must be mapped to transitions in their enclosing FlowElement");
            }
            invoke(moduleName, flowName, flowInvocationContext, flowEvent, nextFlowElement);
        }
        else
        {
            if (replicationFactory == null)
            {
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] ReplicationFactory is required to replicate payloads for multiple recipients.");
            }
            for (String targetName : targetNames)
            {
                final FlowElement nextFlowElement = flowElement.getTransition(targetName);
                if (nextFlowElement == null)
                {
                    throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                            + "] contains a Router, but it does not have a transition mapped for that Router's target[" + targetName + "] "
                            + "All Router targets must be mapped to transitions in their enclosing FlowElement");
                }
                invoke(moduleName, flowName, flowInvocationContext, replicationFactory.replicate(flowEvent), nextFlowElement);
            }
        }
        return null;
    }

    /**
     * The behaviour for visiting a <code>Producer</code> component endpoint
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private FlowElement<?> handleProducer(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        Producer producer = (Producer) flowElement.getFlowComponent();
        Boolean requiresFullEventForInvocation = this.requiresFullEvent.get(moduleName + flowName + flowElement.getComponentName());
        if(requiresFullEventForInvocation == null)
        {
            try
            {
                // try with flowEvent and if successful mark this producer
                producer.invoke(flowEvent);
                this.requiresFullEvent.put(moduleName + flowName + flowElement.getComponentName(), Boolean.TRUE);
            }
            catch(java.lang.ClassCastException e)
            {
                producer.invoke(flowEvent.getPayload());
                this.requiresFullEvent.put(moduleName + flowName + flowElement.getComponentName(), Boolean.FALSE);
            }
        }
        else
        {
            if(requiresFullEventForInvocation.booleanValue())
            {
                producer.invoke(flowEvent);
            }
            else
            {
                producer.invoke(flowEvent.getPayload());
            }
        }
        
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        // producer is last in the flow
        return null;
    }
   
    private FlowElement<?> handleBroker(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        Broker broker = (Broker) flowElement.getFlowComponent();
        flowEvent.setPayload(broker.invoke(flowEvent.getPayload()));
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        // we may or may not have a transition out
        return getDefaultTransition(flowElement);
    }

    /**
     * For consumers we simply want to notify the listeners then get the default transition.
     * @param moduleName
     * @param flowName
     * @param flowEvent
     * @param flowElement
     * @return flowElement
     */
    private FlowElement<?> handleConsumer(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        return getDefaultTransition(flowElement);
    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private FlowElement<?> handleTranslator(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        Translator translator = (Translator) flowElement.getFlowComponent();
        translator.translate(flowEvent.getPayload());
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Translator, but it has no default transition! " + "Translators should never be the last component in a flow");
        }
        return flowElement;
    }

    /**
     * The behaviour for visiting a <code>Transformer</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private FlowElement handleConverter(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        Converter converter = (Converter) flowElement.getFlowComponent();
        flowEvent.setPayload(converter.convert(flowEvent.getPayload()));
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);
        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Converter, but it has no default transition! " + "Converters should never be the last component in a flow");
        }
        return flowElement;
    }

    /**
     * The behaviour for visiting a <code>Filter</code>
     * 
     * @param event The event we're passing on
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param flowElement The flow element we're dealing with
     */
    private FlowElement handleFilter(String moduleName, String flowName, FlowEvent flowEvent, FlowElement flowElement)
    {
        Filter filter = (Filter) flowElement.getFlowComponent();
        if(filter.filter(flowEvent.getPayload()) == null)
        {
            return null;
        }
        
        notifyListenersAfterElement(moduleName, flowName, flowEvent, flowElement);

        // sort out the next element
        FlowElement previousFlowElement = flowElement;
        flowElement = getDefaultTransition(flowElement);
        if (flowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + previousFlowElement.getComponentName()
                    + "] contains a Filter, but it has no default transition! " + "Filters should never be the last component in a flow");
        }
        return flowElement;
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
