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
package org.ikasan.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ikasan.spec.wiretap.WiretapEvent;

import java.io.Serializable;

/**
 * Implementation of a flowEvent based on payload being of any generic type.
 * 
 * @author Ikasan Development Team
 *
 */

@JsonIgnoreProperties
public class MetricEventImpl implements WiretapEvent<String>, Serializable
{
    private String identifier;

    private String event;

    private String moduleName;

    private String flowName;

    private String componentName;

    private long timestamp;

    private long expiry;

    private String eventId;

    private String relatedEventId;


    @Override
    public long getIdentifier()
    {
        return Long.valueOf(identifier);
    }

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public String getFlowName()
    {
        return this.flowName;
    }

    @Override
    public String getComponentName()
    {
        return this.componentName;
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public String getEvent()
    {
        return event;
    }

    @Override
    public long getExpiry()
    {
        return this.expiry;
    }

    @Override
    public String getEventId()
    {
        return this.eventId;
    }

    public void setId(String id)
    {
        this.identifier = id;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getRelatedEventId()
    {
        return relatedEventId;
    }

    public void setRelatedEventId(String relatedEventId)
    {
        this.relatedEventId = relatedEventId;
    }

    @Override
    public String toString()
    {
        return "MetricEventImpl{" +
            "id='" + identifier + '\'' +
            ", event='" + event + '\'' +
            ", moduleName='" + moduleName + '\'' +
            ", flowName='" + flowName + '\'' +
            ", componentName='" + componentName + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}