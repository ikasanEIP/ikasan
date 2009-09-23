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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * Sequencer implementation which tokenises an incoming event's payloads into individual multiple outgoing events,
 * containing a single payload, based on the tokenising criteria.
 * 
 * The regular expression for pattern matching tokenising and an optional character set encoding are passed to the
 * constructor of this class.
 * 
 * @author Ikasan Development Team
 */
public class TokenizingSplitter implements Sequencer
{
    /** Logger instance */
    private static Logger logger = Logger.getLogger(TokenizingSplitter.class);

    /** Delimiter regular expression */
    private String delimiterRegex;

    /** Encoding */
    private String encoding;

    /**
     * Constructor
     * 
     * @param delimiterRegex A regular expression to delimit the incoming event on.
     */
    public TokenizingSplitter(final String delimiterRegex)
    {
        this(delimiterRegex, null);
    }

    /**
     * Constructor
     * 
     * @param delimiterRegex A regular expression to delimit the incoming event on.
     * @param encoding Event's content encoding.
     */
    public TokenizingSplitter(final String delimiterRegex, final String encoding)
    {
        this.delimiterRegex = delimiterRegex;
        if (this.delimiterRegex == null)
        {
            throw new IllegalArgumentException("Delimiter expression cannot be 'null'.");
        }
        // this is optional
        this.encoding = encoding;
    }

    /**
     * Implementation of the onEvent TokenisingSplitter
     * 
     * @param event The incoming event to be split.
     * @return List of Events
     * @throws SequencerException Wrapper for CloneNotSupportedException thrown when cloning <code>Event</code>/
     *             <code>Payload</code>
     */
    public List<Event> onEvent(Event event) throws SequencerException
    {
        List<Event> returnedEvents = new ArrayList<Event>();
        // TODO - we may need to pop the parent id on each of the spawned events.
        // To be decided on review of event, payload and associated concepts.
        String parentId = event.getId();
        if (logger.isDebugEnabled())
        {
            logger.debug("Splitting event " + event.idToString() + " delimiter expression [" + this.delimiterRegex
                    + "]");
        }
        // Iterate over each payload in the incoming event
        for (Payload payload : event.getPayloads())
        {
            try
            {
                // Get the tokenized list
                List<Payload> newPayloads = tokenizeToPayloads(payload);
                for (Payload newPayload : newPayloads)
                {
                    // Create new independent event and clear existing payloads
                    Event newEvent = event.spawn();
                    newEvent.getPayloads().clear();
                    newEvent.setPayload(newPayload);
                    // Event to the Event list to be returned
                    returnedEvents.add(newEvent);
                    if (logger.isInfoEnabled())
                    {
                        logger.debug("Incoming event id [" + parentId + "] split to event id [" + newEvent.getId()
                                + "]");
                    }
                }
            }
            catch (CloneNotSupportedException e)
            {
                throw new SequencerException(e);
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Returning [" + returnedEvents.size() + "] new events.");
            logger.debug("Splitting event based on a token completed successfully.");
        }
        return returnedEvents;
    }

    /**
     * Tokenize the incoming payload
     * 
     * @param payload The incoming payload to be tokenized.
     * @return List of payloads
     * @throws CloneNotSupportedException Thrown when cloning <code>Event</code>/<code>Payload</code>
     */
    private List<Payload> tokenizeToPayloads(Payload payload) throws CloneNotSupportedException
    {
        List<String> tokens = new ArrayList<String>();
        List<Payload> newPayloads = new ArrayList<Payload>();
        byte[] data = payload.getContent();
        logger.info(new String(data));
        ByteArrayInputStream bain = new ByteArrayInputStream(data);
        Pattern pattern = Pattern.compile(this.delimiterRegex);
        if (data != null && data.length > 0)
        {
            Scanner scanner;
            if (this.encoding != null && this.encoding.length() > 0)
            {
                scanner = new Scanner(bain, this.encoding);
            }
            else
            {
                scanner = new Scanner(bain);
            }
            scanner.useDelimiter(pattern);
            while (scanner.hasNext())
            {
                tokens.add(scanner.next());
            }
        }
        if (tokens.size() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.info("No tokens found, returning payload as is.");
            }
            newPayloads.add(payload);
            return newPayloads;
        }
        for (String token : tokens)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating payload for token [" + token + "]");
            }
            Payload newPayload = payload.spawn();
            newPayload.setContent(token.getBytes());
            newPayloads.add(newPayload);
            if (logger.isDebugEnabled())
            {
                logger.debug("Payload id [" + payload.getId() + "] split to payload id [" + newPayload.getId() + "]");
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Returning [" + newPayloads.size() + "] new payloads.");
            logger.debug("Tokenizing payload completed successfully.");
        }
        return newPayloads;
    }
}