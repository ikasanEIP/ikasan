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
package org.ikasan.wiretap.model;

import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapSerialiser;

import java.util.HashMap;
import java.util.Map;

/**
 * Default Iimplementation of the WiretapEventFactory based on the creation
 * of a WiretapEvent.
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapEventFactoryDefaultImpl implements WiretapEventFactory
{
    private WiretapSerialiser<Object,String> serialiser;

    private Map<String,WiretapSerialiser> serialisers = new HashMap<String,WiretapSerialiser>();

    /**
     * Constructor
     * @param serialiser
     */
    public WiretapEventFactoryDefaultImpl(WiretapSerialiser<Object,String> serialiser)
    {
        this.serialiser = serialiser;
        if(serialiser == null)
        {
            throw new IllegalArgumentException("serialiser cannot be 'null'");
        }
    }
    
    /**
     * Factory method to create a new FlowEvent instance.
     *
     * @param moduleName
     * @param flowName
     * @param componentName
     * @param event
     * @param expiry
     * @return
     */
    public WiretapEvent newEvent(final String moduleName, final String flowName, final String componentName,
            final FlowEvent<String,Object> event, final long expiry)
    {
        return new WiretapFlowEvent(moduleName, flowName, componentName, event.getIdentifier(), event.getRelatedIdentifier(), event.getTimestamp(),
                getSerialiser(componentName).serialise(event.getPayload()), expiry);
    }

    /**
     * Override default serialiser
     * @param serialiser
     */
    public void setSerialiser(WiretapSerialiser<Object,String> serialiser)
    {
        this.serialiser = serialiser;
    }

    /**
     * Set a specific serialiser for a component name
     * @param componentName
     * @param serialiser
     */
    public void setSerialiser(String componentName, WiretapSerialiser<Object,String> serialiser)
    {
        this.serialisers.put(componentName, serialiser);
    }

    /**
     * Get the required serialiser
     * @param componentName
     * @return
     */
    protected WiretapSerialiser<Object,String> getSerialiser(String componentName)
    {
        // if no overrides then return default serialiser
        if(this.serialisers.isEmpty()) return this.serialiser;

        // do we have a serialiser for a specific component name?
        if(this.serialisers.containsKey(componentName))
        {
            return serialisers.get(componentName);
        }

        return this.serialiser;
    }
}
