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
