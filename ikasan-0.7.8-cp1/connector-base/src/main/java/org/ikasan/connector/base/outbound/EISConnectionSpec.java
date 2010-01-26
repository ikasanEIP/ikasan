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
package org.ikasan.connector.base.outbound;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.naming.NamingException;
import javax.resource.cci.ConnectionSpec;

import org.apache.log4j.Logger;

import org.ikasan.connector.ConnectorContext;
import org.ikasan.connector.ConnectorExceptionType;
import org.ikasan.connector.ConnectorRuntimeException;
import org.ikasan.connector.ResourceLoader;

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

    /** connector context */
    protected ConnectorContext context = ResourceLoader.getInstance().newContext();

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

    /**
     * load the session properties.
     * 
     * @param sessionPropertiesHandle - Handle to the session properties we want
     *            to load
     * @return ResourceBundle of session properties
     */
    public java.util.ResourceBundle loadSessionProps(String sessionPropertiesHandle)
    {
        String sessionProperties = null;
        // First try to get the session properties handle from the JNDI
        try
        {
            // Try loading properties handle from the JNDI
            sessionProperties = (String) this.context.lookup(sessionPropertiesHandle);
            // Make sure we don't have null or empty session props
            if (sessionProperties == null || sessionProperties.trim().length() == 0)
            {
                throw new NamingException("Context SessionPropertiesHandle [" + sessionPropertiesHandle + "] is is empty.");
            }
            return ResourceBundle.getBundle(sessionProperties, Locale.getDefault(), this.getClass().getClassLoader());
        }
        catch (NamingException e)
        {
            // Not found in the JNDI, try loading as a straight resource bundle
            // Use a different class loader as we want to pick up the 'nearest'
            // resource bundle on our classpath
            try
            {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                return ResourceBundle.getBundle(sessionPropertiesHandle, Locale.getDefault(), classLoader);
            }
            catch (MissingResourceException e1)
            {
                // Not found on the classpath, try the default sessionProperties
                // name
                try
                {
                    return ResourceBundle.getBundle(DEFAULT_SESSION_PROPERTIES, Locale.getDefault(), this.getClass().getClassLoader());
                }
                catch (java.util.MissingResourceException e2)
                {
                    throw new ConnectorRuntimeException(e2, ConnectorExceptionType.MISSING_RESOURCE);
                }
            }
        }
    }
}
