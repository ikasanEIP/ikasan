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
package org.ikasan.framework.plugins;

import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadProvider;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Plugin that retrieves any available <code>Payload</code>s from a <code>PayloadProvider</code> and sets these on the
 * Event, after setting the source system on each, if known
 * 
 * @author Ikasan Development Team
 */
public class PayloadProviderPlugin implements EventInvocable
{
    /** Payload Provider, source of Payloads */
    private PayloadProvider payloadProvider;

    /**
     * Constructor
     * 
     * @param payloadProvider The payload provider to use
     */
    public PayloadProviderPlugin(PayloadProvider payloadProvider)
    {
        super();
        this.payloadProvider = payloadProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.plugins.EventInvocable#invoke(org.ikasan.framework.component.Event,
     * org.ikasan.framework.component.target.TargetParams)
     */
    public void invoke(Event event) throws PluginInvocationException
    {
        List<Payload> relatedPayloads;
        try
        {
            relatedPayloads = payloadProvider.getNextRelatedPayloads();
        }
        catch (ResourceException e)
        {
            throw new PluginInvocationException("ResourceException thrown by PayloadProvider", e);
        }
        if (relatedPayloads != null)
        {
            event.setPayloads(relatedPayloads);
        }
    }
}
