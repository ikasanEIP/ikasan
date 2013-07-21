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
 * @author Ikasan Development Team
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
