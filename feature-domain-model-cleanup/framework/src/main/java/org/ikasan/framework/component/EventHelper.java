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
package org.ikasan.framework.component;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.common.ServiceLocator;
import org.ikasan.common.component.PayloadHelper;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.Spec;
import org.ikasan.framework.ResourceLoader;

/**
 * EventHelper class.
 *
 * @author Ikasan Development Team
 */
public class EventHelper
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** The vent to manipulate */
    private Event event;

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(EventHelper.class);

    /**
     * Creates a new instance of <code>EventHelper </code>
     * with the specified event.
     *
     * @param event
     */
    public EventHelper(final Event event)
    {
        this.event = event;
    }

    /** EventHelper Default Constructor */
    public EventHelper()
    {
        // Do Nothing
    }

    /**
     * Helper method to return the content of each payload in the event as
     * byte[] entries within a list
     *
     * @return List
     * @throws PayloadOperationException
     */
    public List<byte[]> getPayloadsContent()
        throws PayloadOperationException
    {
        return PayloadHelper.getPayloadsContent(this.event.getPayloads());
    }





    /**
     * Set encoding on all payloads
     * @param encoding
     */
    public void setPayloadEncoding(String encoding)
    {
        this.setPayloadEncoding(this.event.getPayloads(), encoding);
    }

    /**
     * Set encoding on all payloads
     *
     * @param payloadList
     * @param encoding
     */
    public void setPayloadEncoding(List<Payload> payloadList, String encoding)
    {
        for (Payload payload : payloadList)
        {
            payload.setEncoding(encoding);
        }
    }

    /**
     * Set srcSystem on all payloads
     * @param srcSystem
     */
    public void setPayloadSrcSystem(String srcSystem)
    {
        this.setPayloadSrcSystem(this.event.getPayloads(), srcSystem);
    }

    /**
     * Set srcSystem on all payloads
     * @param payloadList
     * @param srcSystem
     */
    public void setPayloadSrcSystem(List<Payload> payloadList, String srcSystem)
    {
        for (Payload payload : payloadList)
        {
            payload.setSrcSystem(srcSystem);
        }
    }

    /**
     * Remove specific range of payload entries
     *
     * @param payloadList
     * @throws PayloadOperationException
     */
    public void removePayload(List<Payload> payloadList)
        throws PayloadOperationException
    {

        logger.debug("Current payload list size is [" //$NON-NLS-1$
                   + event.getPayloads().size() + "]."); //$NON-NLS-1$

        try
        {
            if(!this.event.getPayloads().removeAll(payloadList))
            {
                throw new PayloadOperationException("Failed to remove Payload List"); //$NON-NLS-1$
            }
        }
        catch (RuntimeException e)
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Failed to remove Payload list! "); //$NON-NLS-1$

            throw new PayloadOperationException(
                "Failed to remove Payload List", e); //$NON-NLS-1$
        }

        if (logger.isDebugEnabled())
        {
            String payload_s = "payload"; //$NON-NLS-1$

            // Singular or plural - good English looks nicer
            if (payloadList.size() > 1)
            {
                payload_s = "payloads"; //$NON-NLS-1$
            }

            logger.debug("Adjusted payload list size is [" //$NON-NLS-1$
                       + event.getPayloads().size() + "], removed [" //$NON-NLS-1$
                       + payloadList.size() + "] " + payload_s + "."); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    /**
     * Return the highest priority Payload from the list passed in
     *
     * @param payloadList
     * @return the highest priority Payload from the list passed in
     */
    public static Payload getPayloadHighestPriority(List<Payload> payloadList)
    {
        Payload priorityPayload = null;

        // Iterate over payloads
        for (Payload payload : payloadList)
        {
            // Populate first time through
            if (priorityPayload == null)
            {
                priorityPayload = payload;
            }

            // Update only if we find a higher priority
            if (payload.getPriority().compareTo(priorityPayload.getPriority()) > 0)
            {
                priorityPayload = payload;
            }
        }

        return priorityPayload;
    }





}
