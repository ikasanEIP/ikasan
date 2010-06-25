/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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

package org.ikasan.filter.duplicate.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Default implementation of {@link FilterEntry}. This covers all
 * duplicates filtering requirement. Developers should have a very good
 * reason to provide an alternative implementation; don't abuse
 * filtering function to solve requirements it wasn't intended for (like audit trail
 * for example!!)
 *
 * @author Summer
 *
 */
public class DefaultFilterEntry implements FilterEntry, Serializable
{
    /**
     * Auto generated serial id.
     */
    private static final long serialVersionUID = 8566995902951806081L;

    /** A hash unique to message to be filtered. Together with {@link #clientId}
     * it identifies the FilterEntry uniquely. */
    private Integer criteria;

    /** The client id */
    private String clientId;

    /** {@link Date} of insertion */
    private Date createdDateTime;

    /** Expiry {@link Date}*/
    private Date expiry;

    @SuppressWarnings("unused")
    private DefaultFilterEntry()
    {
        //required by Hibernate
    }

    /**
     * Constructor
     * @param criteria
     * @param clientId
     * @param timeToLive Time in <b>days</b> to keep keep {@link FilterEntry} persistend before
     *                   it is removed.
     */
    public DefaultFilterEntry(Integer criteria, String clientId, int timeToLive)
    {
        this.criteria = criteria;
        this.clientId = clientId;
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.expiry = new Date(now + (timeToLive * 24 * 3600 * 1000));

    }

    /**
     * Setter for {@link #expiry}
     * @param expiry
     */
    public void setExpiry(Date expiry)
    {
        this.expiry = expiry;
    }

    /**
     * Getter for {@link #expiry}
     */
    public Date getExpiry()
    {
        return this.expiry;
    }

    /**
     * Setter for {@link #criteria}
     * @param criteria
     */
    public void setCriteria(Integer criteria)
    {
        this.criteria = criteria;
    }

    /**
     * Getter for {@link #criteria}
     */
    public Integer getCriteria()
    {
        return this.criteria;
    }

    /**
     * Setter  for {@link #clientId}
     * @param clientId
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Getter for {@link #clientId}
     */
    public String getClientId()
    {
        return this.clientId;
    }

    /**
     * Setter for {@link #createdDateTime}
     * @param createdDateTime
     */
    public void setCreatedDateTime(Date createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Getter for {@link #createdDateTime}
     */
    public Date getCreatedDateTime()
    {
        return this.createdDateTime;
    }


    /**
     * For a {@link DefaultFilterEntry} object to be equal to another, both should have
     * the same {@link #clientId} and {@link #criteria}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof DefaultFilterEntry)
        {
            DefaultFilterEntry entry = (DefaultFilterEntry) obj;
            if (this.criteria.equals(entry.getCriteria()) &&
                this.clientId.equals(entry.getClientId()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        throw new ClassCastException("Object is not instance of DefaultFilterEntry.");
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = 31 * hash + this.criteria.hashCode();
        hash = 31 * hash + this.clientId.hashCode();
        return hash;
    }
}
