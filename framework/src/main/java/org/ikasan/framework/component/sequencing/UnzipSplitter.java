/*
 * $Id: UnzipSplitter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/sequencing/UnzipSplitter.java $
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
package org.ikasan.framework.component.sequencing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.sequencing.Sequencer;
import org.ikasan.framework.component.sequencing.SequencerException;

/**
 * Implementation of @see {@link org.ikasan.framework.component.sequencing.Sequencer}.
 * <p>
 * An incoming <code>Event</code> will have a zip file entry as its <code>Payload</code>(s).<br>
 * The <code>UnzipSplitter</code> will unzip each of the <code>Event</code>'s payloads,<br>
 * such that each compressed file within the the zipped entry will be split into a new <code>Payload</code>.<br>
 * A new <code>Event</code> will be created for each new <code>Payload</code>.
 * </p>
 * <p>
 * Examples: @see {@link org.ikasan.framework.component.sequencing.UnzipSplitterTest}
 * </p>
 * 
 * @author Ikasan Development Team
 */
public class UnzipSplitter implements Sequencer
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(UnzipSplitter.class);

    /** Constant representing end-of-file is reached. */
    private static final int END_OF_FILE = -1;

    /**
     * Implementation of {@link org.ikasan.framework.component.sequencing.Sequencer#onEvent(Event)}
     * 
     * @param event - The incoming event with payload containing a zip file
     * @throws SequencerException Wrapper exception thrown when cloning and/or transforming the<br>
     *         <code>Event</code>/<code>Payload</code>
     */
    public List<Event> onEvent(Event event) throws SequencerException
    {
        List<Event> newEvents = new ArrayList<Event>();
        List<Payload> payloads = event.getPayloads();
        if (logger.isDebugEnabled())
        {
            logger.debug("Unzipping event " + event.idToString() + "].");
        }
        for (Payload payload : payloads)
        {
            try
            {
                List<Payload> newPayloads = this.unzipPayload(payload);
                for (Payload newPayload : newPayloads)
                {
                    /* 
                     * Get a new Event instance, that is identical to original Event.
                     * The new instance will have a different id, timestamp, and payloads.
                     * See org.ikasan.framework.Event.spawn() for more details on Event 
                     * spawning/cloning method.
                     */
                    Event newEvent = event.spawn();
                    // Remove all old payloads, before adding the new one.
                    newEvent.getPayloads().clear();
                    newEvent.setPayload(newPayload);
                    newEvents.add(newEvent);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Incoming event [" + event.getId() + "] split into event [" + newEvent.getId()
                                + "].");
                    }
                }
            }
            catch (CloneNotSupportedException e)
            {
                throw new SequencerException(e);
            }
            catch (IOException e)
            {
                throw new SequencerException(e);
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Returning [" + newEvents.size() + "] new events.");
            logger.debug("Splitting event compelted successfully.");
        }
        // List of events, each having one payload.
        return newEvents;
    }

    /**
     * Unzip incoming payload event into a list of payloads each representing a single file.
     * 
     * @param payload The incoming payload containing BYTE_ZIP data
     * @return List of payloads representing one unzipped file.
     * @throws IOException Thrown if ZipEntry cannot be read from payload.
     * @throws CloneNotSupportedException Thrown if error cloning a payload.
     */
    private List<Payload> unzipPayload(Payload payload) throws IOException, CloneNotSupportedException
    {
        List<Payload> newPayloads = new ArrayList<Payload>();
        // The zipped file in byte[] format.
        byte[] payloadDataContent = payload.getContent();
        // Read data from payloadDataContent an input stream.
        ByteArrayInputStream inputDataInByteArrayFormat = new ByteArrayInputStream(payloadDataContent);
        // Open an input stream for reading data in ZIP file format.
        ZipInputStream inputDataInZippedFormat = new ZipInputStream(inputDataInByteArrayFormat);
        // A compressed file within a zip file
        ZipEntry zippedEntry = null;
        // Extract data
        while ((zippedEntry = inputDataInZippedFormat.getNextEntry()) != null)
        {
            if (zippedEntry.isDirectory())
            {
                logger.debug("Ignoring directory entry [" + zippedEntry.getName() + "]");
                continue;
            }
            // Open an output stream from writing data in byte[] format
            ByteArrayOutputStream outputDataInByteArrayFormat = new ByteArrayOutputStream();
            // Read data one byte at a time until end-of-file is reached
            for (int c = inputDataInZippedFormat.read(); c != END_OF_FILE; c = inputDataInZippedFormat.read())
            {
                // Write each byte into the output stream.
                outputDataInByteArrayFormat.write(c);
            }
            // The byte[] will be the new payload content.
            byte[] newPayloadDataContent = outputDataInByteArrayFormat.toByteArray();
            String newPayloadName = zippedEntry.getName().toLowerCase();
            /*
             * Get a new Payload instance, that is identical to original Payload
             * The new instance will have a different id, timestamp, name, and
             * content.
             * See org.ikasan.common.Payload.spawn() for more details on Payload
             * spawning/cloning method.
             */
            Payload newPayload = payload.spawn();
            newPayload.setName(newPayloadName);
            newPayload.setContent(newPayloadDataContent);
            newPayloads.add(newPayload);
            if (logger.isDebugEnabled())
            {
                logger.debug("Incomding event's payload [" + payload.getId() + "] split to payload ["
                        + newPayload.getId() + "].");
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Returning [" + newPayloads.size() + "] new payloads.");
            logger.info("Splitting payload to its individual unzipped files completed successfully.");
        }
        return newPayloads;
    }
}
