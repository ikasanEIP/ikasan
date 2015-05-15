/*
 * $Id: EISConnectionSpec.java 38543 2014-08-15 11:20:04Z majean $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-base/src/main/java/org/ikasan/connector/base/outbound/EISConnectionSpec.java $
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
package org.ikasan.connector.base.outbound;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;
import org.ikasan.connector.ConnectorExceptionType;
import org.ikasan.connector.ConnectorRuntimeException;

/**
 * This is an abstract class representing the connection specific application
 * properties passed to the getConnection method.
 * 
 * This does not represent the physical EIS properties ie socket, port. This is
 * in the ra.xml and/or the ConnectionRequestInfo.
 * 
 * @author Ikasan Development Team
 */
public abstract class EISConnectionSpec implements ConnectionSpec, EISConnectionProperties
{
    /** logger */
    private static Logger logger = Logger.getLogger(EISConnectionSpec.class);

    /** Name of the default session properties file */
    private static final String DEFAULT_SESSION_PROPERTIES = "session"; //$NON-NLS-1$

    /** Assigned by client within the session */
    private String clientID;

    /** Default constructor */
    public EISConnectionSpec()
    {
        // empty constructor
    }

    /**
     * Constructor.
     * 
     * @param bundle - The resource bundle to use
     */
    public EISConnectionSpec(ResourceBundle bundle)
    {
        try
        {
            this.setClientID(bundle.getString(CLIENTID));
        }
        catch (MissingResourceException e)
        {
            logger.debug("[" + CLIENTID + "] not specified. " //$NON-NLS-1$//$NON-NLS-2$
                    + "Default from ra.xml will be used."); //$NON-NLS-1$
        }
    }

    /**
     * Getter for clientID
     * 
     * @return String - clientID
     */
    public String getClientID()
    {
        logger.debug("Getting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.clientID;
    }

    /**
     * Setter for clientID.
     * 
     * @param clientID - The client id to set
     */
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
        logger.debug("Setting clientID [" + this.clientID + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }


}
