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
package org.ikasan.history.model;

import org.ikasan.spec.flow.FlowElementInvocation;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.ComponentHistoryEvent;
import org.ikasan.spec.history.MessageHistoryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating MessageHistoryEvents and ComponentHistoryEvents from FlowInvocationContext objects
 *
 * @author Ikasan Development Team
 */
public class HistoryEventFactory
{

    public MessageHistoryEvent<String> newEvent(final String moduleName, final String flowName, FlowInvocationContext flowInvocationContext)
    {
        return new MessageHistoryFlowEvent(moduleName, flowName,
                                           getIdentifier(flowInvocationContext.getInvocations().get(0).getIdentifier()),
                                           getIdentifier(flowInvocationContext.getInvocations().get(0).getRelatedIdentifier()),
                                           newComponentEvents(flowInvocationContext),
                                           flowInvocationContext.getFlowStartTimeMillis(), flowInvocationContext.getFlowEndTimeMillis(), 30); //TODO - move expiry to configurable aspect, where?
    }

    List<ComponentHistoryEvent> newComponentEvents(FlowInvocationContext flowInvocationContext)
    {
        List<ComponentHistoryEvent> componentHistoryEvents = new ArrayList<>();
        for (FlowElementInvocation flowElementInvocation : flowInvocationContext.getInvocations())
        {
            componentHistoryEvents.add(new ComponentHistoryFlowEvent(flowElementInvocation.getFlowElement().getComponentName(),
                    flowElementInvocation.getStartTimeMillis(), flowElementInvocation.getEndTimeMillis()));
        }
        return componentHistoryEvents;
    }

    String getIdentifier(Object object)
    {
        return object != null ? object.toString() : null;
    }
}
