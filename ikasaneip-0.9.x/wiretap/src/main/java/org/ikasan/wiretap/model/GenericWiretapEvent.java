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
public class GenericWiretapEvent implements WiretapEvent
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
    private byte[] event;

    /** expiry time in millis */
    private long expiry;

    /**
     * Silly requirement from the ORM-that-shall-not-be-named!!
     */
    @SuppressWarnings("unused")
    private GenericWiretapEvent()
    {
        // No implementation
    }

    /**
     * Constructor
     * @param identifier2
     */
    public GenericWiretapEvent(final String moduleName, final String flowName, final String componentName,
            final byte[] event, final Long expiry)
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

    public byte[] getEvent()
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
    private void setIdentifier(long identifier)
    {
        this.identifier = identifier;
    }

    /**
     * @param timestamp the timestamp to set
     */
    private void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @param moduleName the moduleName to set
     */
    private void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @param flowName the flowName to set
     */
    private void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * @param componentName the componentName to set
     */
    private void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * @param event the event to set
     */
    private void setEvent(byte[] event)
    {
        this.event = event;
    }

    /**
     * @param expiry the expiry to set
     */
    private void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    //TODO Do we need to override equals contract?
}