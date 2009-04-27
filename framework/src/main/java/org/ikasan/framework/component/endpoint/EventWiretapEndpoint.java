/* 
 * $Id: EventWiretapEndpoint.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/endpoint/EventWiretapEndpoint.java $
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

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Simple <code>Endpoint</code> implemetation that logs the <code>Event</code>
 * 
 * Could be useful as a pass through Event dumper
 * 
 * @author Ikasan Development Team
 * @deprecated - replaced with org.ikasan.framework.component.endpoint.wiretap.WiretapEndpoint
 */
@Deprecated
public class EventWiretapEndpoint implements Endpoint
{
    /**
     * Logger instance
     */
    private static Logger logger = Logger.getLogger(EventWiretapEndpoint.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.component.endpoint.Endpoint#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event)
    {
        logger.info(event);
        List<Payload> payloads = event.getPayloads();
        for (Payload payload : payloads)
        {
            logger.info("payload content [" + new String(payload.getContent()) + "]");
        }
    }
}
