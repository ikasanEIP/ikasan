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
package org.ikasan.framework.component.sequencing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

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
    public List<Event> onEvent(Event event, String moduleName, String componentName) throws SequencerException
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
                for (int i=0;i<newPayloads.size();i++)
                {
                	Event newEvent = event.spawnChild(moduleName, componentName, i, newPayloads.get(i));
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
        int zippedFileCount = 0;
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
            Payload newPayload = payload.spawnChild(zippedFileCount++);
            newPayload.setAttribute(FilePayloadAttributeNames.FILE_NAME, newPayloadName);
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
