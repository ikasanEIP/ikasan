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
package org.ikasan.framework.event.service;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.spec.flow.event.EventFactory;
import org.ikasan.spec.flow.event.FlowEvent;
import org.ikasan.framework.payload.service.PayloadProvider;

/**
 * Implementation of <code>FlowEventProvider</code>.<br>
 * 
 * This implementation returns a single FlowEvent containing all sourced payloads.<br>
 * The purpose of this is to keep 'related' payloads within the logical unit of a single FlowEvent.<br>
 * If no payloads are present then the FlowEvent is not created and 'null' is returned.
 * 
 * @author Ikasan Development Team
 */
public class MultiPayloadPerEventProvider implements EventProvider
{
    /** Payload provider */
    private PayloadProvider payloadProvider;

    /** Module name */
    private String moduleName;

    /** Component name that created the event */
    private String componentName;

    /** TODO pass eventFactory */
    private EventFactory<FlowEvent> eventFactory;
    
    /**
     * Constructor
     * 
     * @param payloadProvider The payload provider
     * @param moduleName The name of the module
     * @param componentName The name of the component
     */
    public MultiPayloadPerEventProvider(final PayloadProvider payloadProvider, final String moduleName,
            final String componentName)
    {
        this.payloadProvider = payloadProvider;
        if (this.payloadProvider == null)
        {
            throw new IllegalArgumentException("'payloadProvider' cannot be 'null'.");
        }
        this.moduleName = moduleName;
        this.componentName = componentName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.event.service.FlowEventProvider#getFlowEvents()
     */
    public List<FlowEvent> getEvents() throws ResourceException
    {
        List<Payload> payloads = this.payloadProvider.getNextRelatedPayloads();
        if (payloads == null || payloads.isEmpty())
        {
            return null;
        }
 
//        FlowEvent event = new FlowEvent(this.moduleName, this.componentName, hashPayloadIds(payloads), payloads);
        FlowEvent event = eventFactory.newEvent(hashPayloadIds(payloads), payloads);
        List<FlowEvent> events = new ArrayList<FlowEvent>();
        events.add(event);
        return events;
    }

	private String hashPayloadIds(List<Payload> payloads) {
		StringBuffer aggregatedId = new StringBuffer();
		for (Payload payload : payloads){
			aggregatedId.append(payload.getId());
		}
		return ""+aggregatedId.toString().hashCode();
	}
}
