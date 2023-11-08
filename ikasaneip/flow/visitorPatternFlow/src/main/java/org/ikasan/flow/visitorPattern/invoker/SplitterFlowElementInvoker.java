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

import org.ikasan.flow.visitorPattern.InvalidFlowException;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of the FlowElementInvoker for a splitter
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class SplitterFlowElementInvoker extends AbstractFlowElementInvoker<SplitterInvokerConfiguration> implements FlowElementInvoker<Splitter>
{
    /** does this component require the full flowEvent or just the payload */
    Boolean requiresFullEventForInvocation;

    /** default event factory */
    private EventFactory<FlowEvent<?,?>> eventFactory;

    /**
     * Constructor
     */
    public SplitterFlowElementInvoker(EventFactory<FlowEvent<?,?>> eventFactory)
    {
        super( new SplitterInvokerConfiguration() );
        this.eventFactory = eventFactory;
        if(eventFactory == null)
        {
            throw new IllegalArgumentException("eventFactory cannot be 'null");
        }
    }

    @Override
    public String getInvokerType()
    {
        return FlowElementInvoker.SPLITTER;
    }

    @Override
    public FlowElement invoke(List<FlowEventListener> flowEventListeners, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<Splitter> flowElement)
    {
        notifyListenersBeforeElement(flowEventListeners, moduleName, flowName, flowEvent, flowElement);
        FlowElementInvocation flowElementInvocation = beginFlowElementInvocation(flowInvocationContext, flowElement, flowEvent);

        Splitter splitter = flowElement.getFlowComponent();
        setInvocationOnComponent(flowElementInvocation, splitter);
        // we must unset the context whatever happens, so try/finally
        List payloads;
        try
        {
            notifyFlowInvocationContextListenersSnapEvent(flowElement, flowEvent);

            if (requiresFullEventForInvocation == null)
            {
                try
                {
                    // try with flowEvent and if successful mark this component
                    payloads = splitter.split(flowEvent);
                    requiresFullEventForInvocation = Boolean.TRUE;
                }
                catch (java.lang.ClassCastException e)
                {
                    payloads = splitter.split(flowEvent.getPayload());
                    requiresFullEventForInvocation = Boolean.FALSE;
                }
            }
            else
            {
                if (requiresFullEventForInvocation)
                {
                    payloads = splitter.split(flowEvent);
                }
                else
                {
                    payloads = splitter.split(flowEvent.getPayload());
                }
            }
        }
        finally
        {
            unsetInvocationOnComponent(flowElementInvocation, splitter);
            endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);
        }

        FlowElement nextFlowElement = getDefaultTransition(flowElement);
        if (nextFlowElement == null)
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Splitter, but it has no default transition! "
                + "Splitters should never be the last component in a flow");
        }

        if (payloads == null || payloads.size() == 0)
        {
            throw new SplitterException("FlowElement [" + flowElement.getComponentName() + "] contains a Splitter. "
                + "Splitters must return at least one payload.");
        }

        if(this.configuration.isSplitEventToListOfPayloads())
        {
            // we need to send the split payloads as a list of payloads in a single event
            List<Object> payloadList = new ArrayList<Object>();

            // put whatever payload is returned into the list, be this FlowEvent or Payload
            for (Object payload : payloads)
            {
                payloadList.add(payload);
            }

            // create new event (with ids from incoming event) for the payload list and send downstream
            FlowEvent newFlowEvent = eventFactory.newEvent(flowEvent.getIdentifier(), flowEvent.getRelatedIdentifier(), flowEvent.getTimestamp(), payloadList);
            notifyListenersAfterElement(flowEventListeners, moduleName, flowName, newFlowEvent, flowElement);

            FlowElement nextFlowElementInRoute = nextFlowElement;
            while (nextFlowElementInRoute != null)
            {
                notifyFlowInvocationContextListenersSnapEvent(nextFlowElementInRoute, newFlowEvent);
                nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListeners, moduleName, flowName, flowInvocationContext, newFlowEvent, nextFlowElementInRoute);
            }
        }
        else if(this.configuration.isSplitEventToListOfEvents())
        {
            // we need to send the split payloads as a list of events (one per payload) in a single event
            List<FlowEvent> eventList = new ArrayList<FlowEvent>();

            Object id = flowEvent.getIdentifier();
            Object relatedId = flowEvent.getRelatedIdentifier();

            // Create a new event for each payload; if the payload is a FlowEvent then use that rather than creating another
            for (Object payload : payloads)
            {
                if (payload instanceof FlowEvent event)
                {
                    eventList.add(event);
                }
                else
                {
                    eventList.add(eventFactory.newEvent(id, relatedId, flowEvent.getTimestamp(), payload));
                }
            }

            // create new event (with ids from the incoming event) for the flow event list and send downstream
            FlowEvent newFlowEvent = eventFactory.newEvent(id, relatedId, flowEvent.getTimestamp(), eventList);
            notifyListenersAfterElement(flowEventListeners, moduleName, flowName, newFlowEvent, flowElement);

            FlowElement nextFlowElementInRoute = nextFlowElement;
            while (nextFlowElementInRoute != null)
            {
                notifyFlowInvocationContextListenersSnapEvent(nextFlowElementInRoute, newFlowEvent);
                nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListeners, moduleName, flowName, flowInvocationContext, newFlowEvent, nextFlowElementInRoute);
            }
        }
        else // default is to send split payloads as Individual events
        {
            if(payloads.size() == 1)
            {
                if (payloads.get(0) instanceof FlowEvent)
                {
                    flowEvent = ((FlowEvent)payloads.get(0));
                }
                else
                {
                    flowEvent = eventFactory.newEvent(flowEvent.getIdentifier(), flowEvent.getRelatedIdentifier(),
                        flowEvent.getTimestamp(), payloads.get(0));
                }

                // nothing has been split so dont create any new events, use the existing and just pass it through
                //flowEvent = flowEvent.getPayload();
                notifyListenersAfterElement(flowEventListeners, moduleName, flowName, flowEvent, flowElement);
                FlowElement nextFlowElementInRoute = nextFlowElement;
                while (nextFlowElementInRoute != null)
                {
                    notifyFlowInvocationContextListenersSnapEvent(nextFlowElementInRoute, flowEvent);
                    nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListeners, moduleName, flowName, flowInvocationContext, flowEvent, nextFlowElementInRoute);
                }
            }
            else
            {
                List<FlowEvent> eventList = new ArrayList<FlowEvent>();

                // Create a new event for each payload; if the payload is a FlowEvent then use that rather than creating another
                for (Object payload : payloads)
                {
                    FlowEvent flowEventForPayload;
                    if (payload instanceof FlowEvent event)
                    {
                        flowEventForPayload = event;
                    }
                    else
                    {
                        flowEventForPayload = eventFactory.newEvent(flowEvent.getIdentifier(), flowEvent.getRelatedIdentifier(),
                            flowEvent.getTimestamp(), payload);
                    }

                    // send each flow event down stream
                    // Note this is done lazily ie. as each payload is dealt with rather than creating all payload flow events up front as this is more efficient
                    notifyListenersAfterElement(flowEventListeners, moduleName, flowName, flowEventForPayload, flowElement);

                    FlowElement nextFlowElementInRoute = nextFlowElement;
                    while (nextFlowElementInRoute != null)
                    {
                        notifyFlowInvocationContextListenersSnapEvent(nextFlowElementInRoute, flowEventForPayload);
                        nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListeners, moduleName, flowName, flowInvocationContext, flowEventForPayload, nextFlowElementInRoute);
                    }
                }
            }
        }

        return null;
    }

}