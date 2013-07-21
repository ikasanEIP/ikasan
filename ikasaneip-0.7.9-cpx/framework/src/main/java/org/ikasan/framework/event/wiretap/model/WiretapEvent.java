/* 
 * $Id$
 * $URL$
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

import java.util.Date;

/**
 * Ikasan WiretapEvent Value Object.
 * 
 * @author Ikasan Development Team
 */
public class WiretapEvent extends WiretapEventHeader
{
    /** Serial UID */
    private static final long serialVersionUID = 4991890753115157079L;

    /** Payload content */
    private String payloadContent;

    /**
     * Private constructor
     * 
     * SuppressWarnings We don't ever want the default constructor to be called
     */
    @SuppressWarnings("unused")
    private WiretapEvent()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param flowName The name of the flow
     * @param componentName The name of the component
     * @param eventId The event id
     * @param payloadId The payload id
     * @param payloadContent The payload content
     * @param expiry The time to expire
     */
    public WiretapEvent(final String moduleName, final String flowName, final String componentName,
            final String eventId, final String payloadId, final String payloadContent, final Date expiry)
    {
        super(moduleName, flowName, componentName, eventId, payloadId, expiry);
        this.payloadContent = payloadContent;
    }

    /**
     * Setter for payload content
     * 
     * @param payloadContent - The payload content to set
     */
    protected void setPayloadContent(String payloadContent)
    {
        this.payloadContent = payloadContent;
    }

    /**
     * Getter for payloadContent
     * 
     * @return payloadContent
     */
    public String getPayloadContent()
    {
        return payloadContent;
    }
}
