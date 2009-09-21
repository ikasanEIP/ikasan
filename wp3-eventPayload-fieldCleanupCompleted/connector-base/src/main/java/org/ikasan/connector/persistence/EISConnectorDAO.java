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
package org.ikasan.connector.persistence;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Java class supporting standard connector properties 
 * requiring persistence.
 * 
 * @author Ikasan Development Team
 */
abstract public class EISConnectorDAO
{
    /** Standard connector clientID */
    protected String clientID;

    /** Connector insert creation dateTime */
    protected Date createDateTime;

    /** Connector last update dateTime */
    protected Date updateDateTime;

    /** Logger */
    private static Logger logger = Logger.getLogger(EISConnectorDAO.class);

    /**
     * @return the createDateTime
     */
    public Date getCreateDateTime()
    {
        logger.debug("Getting createDateTime [" + this.createDateTime + "]");  //$NON-NLS-1$//$NON-NLS-2$
        return this.createDateTime;
    }

    /**
     * @param createDateTime the createDateTime to set
     */
    public void setCreateDateTime(final Date createDateTime)
    {
        this.createDateTime = createDateTime;
        logger.debug("Setting createDateTime [" + this.createDateTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return the updateDateTime
     */
    public Date getUpdateDateTime()
    {
        logger.debug("Getting updateDateTime [" + this.updateDateTime + "]");  //$NON-NLS-1$//$NON-NLS-2$
        return this.updateDateTime;
    }

    /**
     * @param updateDateTime the updateDateTime to set
     */
    public void setUpdateDateTime(final Date updateDateTime)
    {
        this.updateDateTime = updateDateTime;
        logger.debug("Setting updateDateTime [" + this.updateDateTime + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return the clientID
     */
    public String getClientID()
    {
        logger.debug("Getting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public void setClientID(final String clientID)
    {
        this.clientID = clientID;
        logger.debug("Setting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Creates a formatted String for this instance.
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("clientID [" + this.clientID + "] "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("createDateTime [" + this.createDateTime + "] "); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("updateDateTime [" + this.updateDateTime + "] "); //$NON-NLS-1$ //$NON-NLS-2$
        
        return new String(sb);
    }
    
}
