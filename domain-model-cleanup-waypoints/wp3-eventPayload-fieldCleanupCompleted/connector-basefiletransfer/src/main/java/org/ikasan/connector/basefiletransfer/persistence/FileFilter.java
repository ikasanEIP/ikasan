/*
 * $Id$
 * $URL$
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
package org.ikasan.connector.basefiletransfer.persistence;

import java.util.Date;

import org.apache.log4j.Logger;

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
public class FileFilter
{
    /** Logger */
    private static Logger logger = Logger.getLogger(FileFilter.class);

    /** id */
    private int id;

    /** clientId */
    private String clientId;

    /** URI */
    private String uri;

    /** last modified */
    private Date lastModified;
    
    /** last accessed */
    private Date lastAccessed;
    
    /** size */
    private int size;
    
    /** created date time */
    private Date createdDateTime;

    /** Criteria to filter on */
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
        this.createdDateTime = new Date();
    }

    /**
     * @return the createdDateTime
     */
    public Date getCreatedDateTime()
    {
        logger.debug("Getting createdDateTime = [" + this.createdDateTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return createdDateTime;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param createdDateTime the createdDateTime to set
     */
    @SuppressWarnings("unused")
    private void setCreatedDateTime(Date createdDateTime)
    {
        logger.debug("Setting this.createdDateTime = [" + createdDateTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        logger.debug("Getting id = [" + this.id + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return id;
    }

    /**
     * Setter used by Hibernate
     * 
     * @param id the id to set
     */
    @SuppressWarnings("unused")
    private void setId(int id)
    {
        logger.debug("Setting this.id = [" + id + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.id = id;
    }

    /**
     * @return the lastAccessed
     */
    public Date getLastAccessed()
    {
        logger.debug("Getting lastAccessed = [" + this.lastAccessed + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
        logger.debug("Setting this.lastAccessed = [" + lastAccessed + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.lastAccessed = lastAccessed;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified()
    {
        logger.debug("Getting lastModified = [" + this.lastModified + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
        logger.debug("Setting this.lastModified = [" + lastModified + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.lastModified = lastModified;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        logger.debug("Getting size = [" + this.size + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
        logger.debug("Setting this.size = [" + size + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.size = size;
    }

    /**
     * @return the uri
     */
    public String getUri()
    {
        logger.debug("Getting uri = [" + this.uri + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
        logger.debug("Setting this.uri = [" + uri + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.uri = uri;
    }

    /**
     * @return the clientId
     */
    public String getClientId()
    {
        logger.debug("Getting clientId = [" + this.clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
        logger.debug("Setting this.clientId = [" + clientId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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
}