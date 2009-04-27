/*
 * $Id: DatabasePayload.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/payload/model/DatabasePayload.java $
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

    /** Has this Event been consumed */
    private boolean consumed;

    /** Last time this Event was updated */
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
    public String getEvent()
    {
        return event;
    }

    /**
     * Setter for event
     * 
     * @param event - The event to set
     */
    public void setEvent(String event)
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
