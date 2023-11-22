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
package org.ikasan.history.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.GenericWiretapEvent;

import java.io.Serializable;

/**
 * Implementation of a flowEvent based on payload being of any generic type.
 * 
 * @author Ikasan Development Team
 *
 */
@Entity
@Table(name = "MetricEvent")
public class MetricEvent extends GenericWiretapEvent implements WiretapEvent<String>, Serializable
{
    /** event id */
    @Column(name="EventId", nullable = false)
    private String eventId;

    /** related event id */
    @Column(name="RelatedEventId", nullable = false)
    private String relatedEventId;

    /** event created date/time */
    @Column(name="EventTimestamp", nullable = false)
    private long eventTimestamp;

    public MetricEvent()
    {
    }

    public MetricEvent(final String moduleName, final String flowName, final String componentName,
                       final String eventId, final String relatedEventId, final long eventTimestamp, final String event, final Long expiry)
    {
        super(moduleName, flowName, componentName, event, expiry);
        this.eventId = eventId;
        this.relatedEventId = relatedEventId;
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public long getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public String getEventId()
    {
        return eventId;
    }

    protected void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getRelatedEventId()
    {
        return relatedEventId;
    }

    protected void setRelatedEventId(String relatedEventId)
    {
        this.relatedEventId = relatedEventId;
    }

    public long getEventTimestamp()
    {
        return eventTimestamp;
    }

    protected void setEventTimestamp(long eventTimestamp)
    {
        this.eventTimestamp = eventTimestamp;
    }

    @Override
    public String toString() {
        return "MetricEvent{" +
                "eventId='" + eventId + '\'' +
                ", relatedEventId='" + relatedEventId + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                "} " + super.toString();
    }
}