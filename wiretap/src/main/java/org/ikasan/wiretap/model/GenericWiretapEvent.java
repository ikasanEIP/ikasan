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

import org.ikasan.spec.wiretap.WiretapEvent;

/**
 * Implementation of a flowEvent based on payload being of any generic type.
 * 
 * @author Ikasan Development Team
 *
 */
public class GenericWiretapEvent implements WiretapEvent<String>
{
    /** immutable identifier */
    private long identifier;

    /** immutable event creation timestamp */
    private long timestamp;

    /** module name */
    private String moduleName;
    
    /** flow name */
    private String flowName;
    
    /** component name */
    private String componentName;

    /** tapped event */
    private String event;

    /** expiry time in millis */
    private long expiry;

    /** Next GenericWiretapEvent (if any) for this event */
    private transient Long nextByEventId;

    /** Previous GenericWiretapEvent (if any) for this event */
    private transient Long previousByEventId;

    /**
     * Silly requirement from the ORM-that-shall-not-be-named!!
     */
    @SuppressWarnings("unused")
    protected GenericWiretapEvent()
    {
        // No implementation
    }

    /**
     * Constructor
     * @param identifier2
     */
    public GenericWiretapEvent(final String moduleName, final String flowName, final String componentName,
            final String event, final Long expiry)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.componentName = componentName;
        this.event = event;
        this.timestamp = System.currentTimeMillis();
        this.expiry = expiry;
    }

    public long getIdentifier()
    {
        return this.identifier;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public String getModuleName()
    {
        return this.moduleName;
    }

    public String getFlowName()
    {
        return this.flowName;
    }

    public String getComponentName()
    {
        return this.componentName;
    }

    public String getEvent()
    {
        return this.event;
    }

    public long getExpiry()
    {
        return this.expiry;
    }

    /**
     * @param identifier the identifier to set
     */
    protected void setIdentifier(long identifier)
    {
        this.identifier = identifier;
    }

    /**
     * @param timestamp the timestamp to set
     */
    protected void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @param moduleName the moduleName to set
     */
    protected void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @param flowName the flowName to set
     */
    protected void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * @param componentName the componentName to set
     */
    protected void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * @param event the event to set
     */
    protected void setEvent(String event)
    {
        this.event = event;
    }

    /**
     * @param expiry the expiry to set
     */
    protected void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public void setPreviousByEventId(Long previousByEventId)
    {
        this.previousByEventId = previousByEventId;
    }

    public void setNextByEventId(Long nextByEventId)
    {
        this.nextByEventId = nextByEventId;
    }

    public Long getPreviousByEventId()
    {
        return previousByEventId;
    }

    public Long getNextByEventId()
    {
        return nextByEventId;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GenericWiretapEvent [identifier=");
        builder.append(identifier);
        builder.append(", timestamp=");
        builder.append(timestamp);
        builder.append(", moduleName=");
        builder.append(moduleName);
        builder.append(", flowName=");
        builder.append(flowName);
        builder.append(", componentName=");
        builder.append(componentName);
        builder.append(", event=");
        builder.append(event);
        builder.append(", expiry=");
        builder.append(expiry);
        builder.append(", nextByEventId=");
        builder.append(nextByEventId);
        builder.append(", previousByEventId=");
        builder.append(previousByEventId);
        builder.append("]");
        return builder.toString();
    }
    
    //TODO Do we need to override equals contract?
}