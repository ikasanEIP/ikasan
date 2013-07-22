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
package org.ikasan.framework.component.endpoint;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.payload.service.PayloadPublisher;

/**
 * <code>Endpoint</code> implementation that delegates in order all the <code>Event</code>'s <code>Payload</code>'s to a
 * <code>PayloadPublisher</code>
 * 
 * @author Ikasan Development Team
 */
public class PayloadPublisherEndpoint implements Endpoint
{
    /** The payload publisher */
    protected PayloadPublisher payloadPublisher;

    /**
     * Constructor
     * 
     * @param payloadPublisher The payload publisher
     */
    public PayloadPublisherEndpoint(PayloadPublisher payloadPublisher)
    {
        super();
        this.payloadPublisher = payloadPublisher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.endpoint.Endpoint#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws EndpointException
    {
        for (Payload payload : event.getPayloads())
        {
            try
            {
                payloadPublisher.publish(payload);
            }
            catch (ResourceException e)
            {
                throw new EndpointException(e);
            }
        }
    }
}
