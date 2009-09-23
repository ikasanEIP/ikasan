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
     * Set spec on all payloads
     * @param spec
     */
    public void setPayloadSpec(String spec)
    {
        this.setPayloadSpec(this.event.getPayloads(), spec);
    }

    /**
     * Set spec on all payloads
     *
     * @param payloadList
     * @param spec
     */
    public void setPayloadSpec(List<Payload> payloadList, String spec)
    {
        for (Payload payload : payloadList)
        {
            payload.setSpec(spec);
        }
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

//    /**
//     * Get the list of payload attribs
//     *
//     * @return List of Payload attribs
//     * @throws PayloadOperationException
//     */
//    public List<Map> getPayloadsAttributes()
//        throws PayloadOperationException
//    {
//        return PayloadHelper.getPayloadsAttributes(this.event.getPayloads());
//    }

    /**
     * Runs this class for test.
     *
     * TODO Should be a Unit test
     *
     * @param args
     */
    public static void main(String args[])
    {
        // TODO global service locator
        ServiceLocator serviceLocator = ResourceLoader.getInstance();
        serviceLocator.getPayloadFactory().newPayload("test", Spec.TEXT_PLAIN,
                "testSrcSystem", "hello".getBytes());
    }

}
