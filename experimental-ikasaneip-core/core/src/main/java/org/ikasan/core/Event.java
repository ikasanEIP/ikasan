/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.core;

/**
 * Ikasan's domain object.
 * 
 * @author Ikasan Development Team
 * 
 * @param <PAYLOAD> Content type
 *
 */
public class Event<PAYLOAD>
{
    /** An identifier. Immutable throughout the object's life time */
    private final String identifier;

    /**
     * date-time stamp of creation of this event. Defaults to
     * System time: current time in milliseconds measured from epoc
     */
    private final long timestamp = System.currentTimeMillis();

    /** Content carried by this event */
    private PAYLOAD payload;

    /**
     * Constructor
     * @param identifier An identifier. Immutable throughout the object's life time 
     */
    public Event(final String identifier)
    {
        this.identifier = identifier;
        if (this.identifier == null) //TODO Well what about '' or ' ' cases? others?
        {
            throw new IllegalArgumentException("The event's unique identifier cannot be null.");
        }
    }

    /**
     * @return the identifier
     */
    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return the payload
     */
    public PAYLOAD getPayload()
    {
        return this.payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(PAYLOAD payload)
    {
        this.payload = payload;
    }

}
