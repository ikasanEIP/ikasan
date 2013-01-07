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
package org.ikasan.framework.event.wiretap.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The Header for a Wiretap event
 * @author Ikasan Development Team
 */
public class WiretapEventHeader implements Comparable<WiretapEventHeader>, Serializable
{
    /** SerialVersionUID */
    private static final long serialVersionUID = -9100792189633886601L;

    /** Dao assigned id */
    protected Long id;

    /** Module name */
    protected String moduleName;

    /** Flow name */
    protected String flowName;

    /** Wiretap component name */
    protected String componentName;

    /** Event id */
    protected String eventId;

    /** Payload id */
    protected String payloadId;

    /** Created date/time */
    protected Date created;

    /** Last updated date/time */
    protected Date updated;

    /** Time after which this WiretapEvent should be cleaned up */
    protected Date expiry;

    /** Next WiretapEvent (if any) for this payload */
    protected transient Long nextByPayload;

    /** Previous WiretapEvent (if any) for this payload */
    protected transient Long previousByPayload;

    /**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param componentName The name of the component
     * @param eventId The event id
     * @param payloadId The payload id
     * @param expiry The time to expire
     */
    public WiretapEventHeader(final String moduleName, final String flowName, final String componentName,
            final String eventId, final String payloadId, final Date expiry)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.componentName = componentName;
        this.eventId = eventId;
        this.payloadId = payloadId;
        this.expiry = expiry;
        Date now = new Date();
        this.created = now;
        this.updated = now;
    }

    /** Constructor */
    protected WiretapEventHeader()
    {
        // Empty constructor
    }

    /**
     * Getter for componentName
     * 
     * @return componentName
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Getter for date/time created
     * 
     * @return date/time created
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * Getter for eventId
     * 
     * @return eventId
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * Getter for flowName
     * 
     * @return flowName
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * Getter for id
     * 
     * @return id
     */
    public Long getId()
    {
        return this.id;
    }

    /**
     * Getter for moduleName
     * 
     * @return moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * Getter for payloadId
     * 
     * @return payloadId
     */
    public String getPayloadId()
    {
        return payloadId;
    }

    /**
     * Getter for date/time updated
     * 
     * @return date/time updated
     */
    public Date getUpdated()
    {
        return updated;
    }

    /**
     * Getter for expiry
     * 
     * @return expiry date
     */
    public Date getExpiry()
    {
        return expiry;
    }

    /**
     * Setter for component name
     * 
     * @param componentName - The name of the component to set
     */
    protected void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * Setter for created date/time
     * 
     * @param created - The created date time to set
     */
    protected void setCreated(Date created)
    {
        this.created = created;
    }

    /**
     * Setter for event id
     * 
     * @param eventId - The Event id to set
     */
    protected void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * Setter for flow name
     * 
     * @param flowName - The name of the flow to set
     */
    protected void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * Setter for id
     * 
     * @param id - The wire tap header id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Setter for module name
     * 
     * @param moduleName - The module name to set
     */
    protected void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Setter for payload id
     * 
     * @param payloadId - The payload id to set
     */
    protected void setPayloadId(String payloadId)
    {
        this.payloadId = payloadId;
    }

    /**
     * Setter for date/time updated
     * 
     * @param updated - The last updated date to set
     */
    protected void setUpdated(Date updated)
    {
        this.updated = updated;
    }

    /**
     * Setter for expiry
     * 
     * @param expiry - The time to expire
     */
    protected void setExpiry(Date expiry)
    {
        this.expiry = expiry;
    }

    public int compareTo(WiretapEventHeader other)
    {
        if (this.id == null)
        {
            return -1;
        }
        return this.id.compareTo(other.getId());
    }

    /**
     * Accessor for nextByPayload
     * 
     * @return id for next WiretapEvent related by payload
     */
    public Long getNextByPayload()
    {
        return nextByPayload;
    }

    /**
     * Setter for nextByPayload
     * 
     * @param nextByPayload id for next WiretapEvent related by payload
     */
    public void setNextByPayload(Long nextByPayload)
    {
        this.nextByPayload = nextByPayload;
    }

    /**
     * Accessor for previousByPayload
     * 
     * @return id for previous WiretapEvent related by payload
     */
    public Long getPreviousByPayload()
    {
        return previousByPayload;
    }

    /**
     * Setter for previousByPayload
     * 
     * @param previousByPayload id for previous WiretapEvent related by payload
     */
    public void setPreviousByPayload(Long previousByPayload)
    {
        this.previousByPayload = previousByPayload;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(this.getClass().getName() + " [");
        sb.append("id=");
        sb.append(id);
        sb.append(",");
        sb.append("moduleName=");
        sb.append(moduleName);
        sb.append(",");
        sb.append("flowName=");
        sb.append(flowName);
        sb.append(",");
        sb.append("componentName=");
        sb.append(componentName);
        sb.append(",");
        sb.append("eventId=");
        sb.append(eventId);
        sb.append(",");
        sb.append("payloadId=");
        sb.append(payloadId);
        sb.append(",");
        sb.append("expiry=");
        sb.append(expiry);
        sb.append("]");
        return sb.toString();
    }
}
