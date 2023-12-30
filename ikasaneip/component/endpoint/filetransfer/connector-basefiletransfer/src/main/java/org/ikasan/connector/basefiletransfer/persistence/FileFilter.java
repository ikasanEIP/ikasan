/*
 * $Id:$
 * $URL:$
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
package org.ikasan.connector.basefiletransfer.persistence;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

/**
 * Object to be used for persistence/filtering. It reflects the basic fields
 * from the <code>ClientListEntry</code> object with the addition of two
 * fields only relevant to persistence.
 * 
 * <p>
 * Related config consists of <code>FileFilter.hbm.xml</code> and the
 * <code>FileFilter</code> DDL file.
 * </p>
 * 
 * @author Ikasan Development Team
 */
@Entity
@Table(name="FTFileFilter")
public class FileFilter
{

    /** id */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /** clientId */
    @Column(name = "ClientId", nullable = false)
    private String clientId;

    /** URI */
    @Transient
    private String uri;

    /** last modified */
    @Column(name = "LastModified", nullable = false)
    private Date lastModified;
    
    /** last accessed */
    @Column(name = "LastAccessed", nullable = false)
    private Date lastAccessed;
    
    /** size */
    @Column(name = "Size", nullable = false)
    private int size;

    /** immutable event creation timestamp */
    @Column(name = "CreatedDateTime", nullable = false)
    private long createdDateTime;

    /** Criteria to filter on */
    @Column(name = "Criteria", nullable = false)
    private String criteria;

    /** Default constructor */
    public FileFilter()
    {
        // Do Nothing
    }

    /**
     * @param criteria
     * @param clientId
     * @param lastModified
     * @param lastAccessed
     * @param size
     */
    public FileFilter(String clientId, String criteria, Date lastModified, Date lastAccessed, int size)
    {
        this.clientId = clientId;
        this.criteria = criteria;
        this.lastModified = lastModified;
        this.lastAccessed = lastAccessed;
        this.size = size;
        long now = System.currentTimeMillis();
        this.createdDateTime = now;
    }

    /**
     * @return the createdDateTime
     */
    public long getCreatedDateTime()
    {
        return createdDateTime;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param createdDateTime the createdDateTime to set
     */
    @SuppressWarnings("unused")
    public void setCreatedDateTime(long createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param id the id to set
     */
    @SuppressWarnings("unused")
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return the lastAccessed
     */
    public Date getLastAccessed()
    {
        return lastAccessed;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param lastAccessed the lastAccessed to set
     */
    @SuppressWarnings("unused")
    private void setLastAccessed(Date lastAccessed)
    {
        this.lastAccessed = lastAccessed;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified()
    {
        return lastModified;
    }

    /**
     * Setter method required by Hibernate
     * 
     * @param lastModified the lastModified to set
     */
    @SuppressWarnings("unused")
    private void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param size the size to set
     */
    @SuppressWarnings("unused")
    private void setSize(int size)
    {
        this.size = size;
    }

    /**
     * @return the uri
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param uri the uri to set
     */
    @SuppressWarnings("unused")
    private void setUri(String uri)
    {
        this.uri = uri;
    }

    /**
     * @return the clientId
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param clientId the clientId to set
     */
    @SuppressWarnings("unused")
    private void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Set the criteria to filter on
     * @param criteria
     */
    public void setCriteria(String criteria)
    {
        this.criteria = criteria;
    }

    /**
     * Get the criteria to filter on
     * @return criteria
     */
    public String getCriteria()
    {
        return this.criteria;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o )
        { return true; }
        if ( o == null || getClass() != o.getClass() )
        { return false; }
        FileFilter that = (FileFilter) o;
        return size == that.size && createdDateTime == that.createdDateTime && Objects.equals(id, that.id) && Objects
            .equals(clientId, that.clientId) && Objects.equals(uri, that.uri) && Objects
            .equals(lastModified, that.lastModified) && Objects.equals(lastAccessed, that.lastAccessed) && Objects
            .equals(criteria, that.criteria);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, clientId, uri, lastModified, lastAccessed, size, createdDateTime, criteria);
    }
}