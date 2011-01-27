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
package org.ikasan.framework.payload.model;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model class representing a <code>Payload</code> in a database persistent form
 * 
 * @author Ikasan Development Team
 */
public class DatabasePayload
{
    /** Id */
    private Long id;

    /** Created Date */
    private Date created;

    /** The body of the payload */
    private String event;

    /** Has this FlowEvent been consumed */
    private boolean consumed;

    /** Last time this FlowEvent was updated */
    private Date lastUpdated;

    /** Constructor */
    protected DatabasePayload()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param event - poorly named. Really the body of the payload
     * @param created - date this payload was created
     */
    public DatabasePayload(String event, Date created)
    {
        this.event = event;
        this.created = created;
        this.lastUpdated = created;
    }

    /**
     * Accessor for Id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Setter for id
     * 
     * @param id - The id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Accessor for created
     * 
     * @return created
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * Setter for created
     * 
     * @param created - created date to set
     */
    public void setCreated(Date created)
    {
        this.created = created;
    }

    /**
     * Accessor for event
     * 
     * @return event
     */
    public String getFlowEvent()
    {
        return event;
    }

    /**
     * Setter for event
     * 
     * @param event - The event to set
     */
    public void setFlowEvent(String event)
    {
        this.event = event;
    }

    /**
     * Accessor for consumed
     * 
     * @return consumed
     */
    public boolean isConsumed()
    {
        return consumed;
    }

    /**
     * Setter for consumed
     * 
     * @param consumed - consumed flag to set
     */
    public void setConsumed(boolean consumed)
    {
        this.consumed = consumed;
    }

    /**
     * Accessor for lastUpdated
     * 
     * @return lastUpdated
     */
    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    /**
     * Setter for lastUpdated
     * 
     * @param lastUpdated - The last updated date to set
     */
    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof DatabasePayload))
        {
            return false;
        }
        DatabasePayload rhs = (DatabasePayload) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(this.created, rhs.created).append(
            this.consumed, rhs.consumed).append(this.lastUpdated, rhs.lastUpdated).append(this.event, rhs.event)
            .append(this.id, rhs.id).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(310152561, 1666595659).appendSuper(super.hashCode()).append(this.created).append(
            this.consumed).append(this.lastUpdated).append(this.event).append(this.id).toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("created", this.created).append("consumed", this.consumed).append(
            "lastUpdated", this.lastUpdated).append("id", this.id).toString();
    }
}
