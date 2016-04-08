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
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.event.ReplicationFactory;
import org.ikasan.spec.flow.*;

import java.util.List;

/**
 * A default implementation of the FlowElementInvoker for a multi-recipient router
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class MultiRecipientRouterFlowElementInvoker extends AbstractFlowElementInvoker implements FlowElementInvoker<MultiRecipientRouter>
{
    /** replication factory - requirement for flows where event can undergo a number of sequential routes */
    private ReplicationFactory<FlowEvent<?,?>> replicationFactory;

    /** allow the MRR invoker to be configured */
    private MultiRecipientRouterConfiguration configuration;

    /**
     * Constructor
     * @param replicationFactory
     */
    public MultiRecipientRouterFlowElementInvoker(ReplicationFactory<FlowEvent<?, ?>> replicationFactory, MultiRecipientRouterConfiguration configuration)
    {
        this.replicationFactory = replicationFactory;
        if(replicationFactory == null)
        {
            throw new IllegalArgumentException("replicationFactory cannot be 'null'");
        }

        this.configuration = configuration;
        if(configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }
    }

    /**
     * Constructor
     */
    public MultiRecipientRouterFlowElementInvoker()
    {
        // default constructor
    }

    @Override
    public FlowElement invoke(FlowEventListener flowEventListener, String moduleName, String flowName, FlowInvocationContext flowInvocationContext, FlowEvent flowEvent, FlowElement<MultiRecipientRouter> flowElement)
    {
        notifyListenersBeforeElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
        FlowElementInvocation<Object, ?> flowElementInvocation = beginFlowElementInvocation(flowInvocationContext, flowElement, flowEvent);

        MultiRecipientRouter router = flowElement.getFlowComponent();
        setInvocationOnComponent(flowElementInvocation, router);
        // we must unset the context whatever happens, so try/finally
        List<String> targetNames;
        try
        {
            targetNames = router.route(flowEvent.getPayload());
        }
        finally
        {
            unsetInvocationOnComponent(flowElementInvocation, router);
        }
        if (targetNames == null || targetNames.size() == 0)
        {
            throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName() + "] contains a Router without a valid transition. "
                    + "All Routers must result in at least one transition.");
        }
        endFlowElementInvocation(flowElementInvocation, flowElement, flowEvent);

        notifyListenersAfterElement(flowEventListener, moduleName, flowName, flowEvent, flowElement);
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

            return nextFlowElement;
        }
        else
        {
            if (replicationFactory == null)
            {
                throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                        + "] ReplicationFactory is required to replicate payloads for multiple recipients.");
            }

            int targetCount = 0;
            for (String targetName : targetNames) {
                FlowEvent routedFlowEvent = flowEvent;
                final FlowElement nextFlowElement = flowElement.getTransition(targetName);
                if (nextFlowElement == null) {
                    throw new InvalidFlowException("FlowElement [" + flowElement.getComponentName()
                            + "] contains a Router, but it does not have a transition mapped for that Router's target[" + targetName + "] "
                            + "All Router targets must be mapped to transitions in their enclosing FlowElement");
                }

                FlowElement nextFlowElementInRoute = nextFlowElement;

                targetCount++;

                // if we are at the last targetName then skip replication of the event as this is expensive
                if (configuration.isCloneEventPerRoute() && targetCount < targetNames.size())
                {
                    routedFlowEvent = replicationFactory.replicate(flowEvent);
                }

                while (nextFlowElementInRoute != null)
                {
                    nextFlowElementInRoute = nextFlowElementInRoute.getFlowElementInvoker().invoke(flowEventListener, moduleName, flowName, flowInvocationContext, routedFlowEvent, nextFlowElementInRoute);
                }
            }
        }
        return null;
    }
}

