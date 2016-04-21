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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.ikasan.spec.flow.FlowElementInvocation;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;

/**
 * Factory for creating MessageHistoryEvents from FlowInvocationContext objects
 *
 * @author Ikasan Development Team
 */
public class HistoryEventFactory
{

    public List<MessageHistoryEvent<String, CustomMetric>> newEvent(final String moduleName, final String flowName, FlowInvocationContext flowInvocationContext)
    {

        List<MessageHistoryEvent<String, CustomMetric>> messageHistoryEvents 
        	= new ArrayList<MessageHistoryEvent<String, CustomMetric>>();

        for ( FlowElementInvocation<Object, List<AbstractMap.SimpleImmutableEntry<String, String>>> invocation : flowInvocationContext.getElementInvocations())
        {
        	MessageHistoryFlowEvent event = new MessageHistoryFlowEvent(moduleName, flowName,
                    invocation.getFlowElement().getComponentName(),
                    Objects.toString(invocation.getBeforeIdentifier(), null),
                    Objects.toString(invocation.getBeforeRelatedIdentifier(), null),
                    Objects.toString(invocation.getAfterIdentifier(), null),
                    Objects.toString(invocation.getAfterRelatedIdentifier(), null),
                    invocation.getStartTimeMillis(), invocation.getEndTimeMillis(),
                    30);
        	//TODO - move expiry to configurable aspect, where?
        	
        	event.setMetrics(this.getMetrics(invocation, event));
        	
            messageHistoryEvents.add(event);            
        }
        return messageHistoryEvents;
    }
    
    private Set<CustomMetric> getMetrics(FlowElementInvocation<Object, List<AbstractMap.SimpleImmutableEntry<String, String>>> invocation,
    		MessageHistoryFlowEvent event)
    {
    	Set<CustomMetric> metrics = new HashSet<CustomMetric>();
    	
    	for(AbstractMap.SimpleImmutableEntry<String, String> nvp: invocation.getCustomMetrics())
    	{
    		CustomMetric cm = new CustomMetric();
    		cm.setName(nvp.getKey());
    		cm.setValue(nvp.getValue());
    		cm.setMessageHistoryFlowEvent(event);
    		
    		metrics.add(cm);
    	}
    	
    	return metrics;
    }
}
