/* 
 * $Id: WiretapEventHeader.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/event/wiretap/model/WiretapEventHeader.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
