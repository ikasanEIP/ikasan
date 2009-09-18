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
package org.ikasan.connector;

import org.apache.log4j.Logger;
import org.ikasan.common.ExceptionType;

/**
 * This is an enumeration of connector exception types providing categories
 * within the ConnectorException and ConnectorRuntimeException.
 * 
 * NOTE: The important definition in the enum is the name of the enum which is
 * instantiated. This name is used to match in the exceptionResolver.xml for the
 * associated component group. The enum description and id are there for
 * potential logging and future use.
 * 
 * @author Ikasan Development Team
 */
public enum ConnectorExceptionType implements ExceptionType
{
    /** Missing Resource */
    MISSING_RESOURCE("The connector is missing a resource required at runtime, ie Session.properties", 3000), //$NON-NLS-1$
    /** Missing Resource */
    UNKNOWN_ERROR("The connector was not able to recognise this error type", 3001), //$NON-NLS-1$
    /** Connection reset by peer */
    CONNECTION_RESET_BY_PEER("The connector was reset by peer", 3002), //$NON-NLS-1$
    /** Connection reset by connector */
    CONNECTION_RESET_BY_CONNECTOR("The connector reset this connection", 3003), //$NON-NLS-1$
    /** Connection timed out */
    CONNECTION_TIMED_OUT("The connection timed out", 3004), //$NON-NLS-1$
    ;
    /** Logger */
    private Logger logger = Logger.getLogger(ConnectorExceptionType.class);

    /** exception type description */
    private String description = null;

    /** exception type unique identifier */
    private int id = 0;

    /**
     * Creates a new instance of <code>ConnectorExceptionType</code> with the
     * specified exception description and id.
     * 
     * @param description - The description for this ConnectorExceptionType
     * @param id - The id of this ConnectorExceptionType
     */
    private ConnectorExceptionType(final String description, final int id)
    {
        this.description = description;
        this.id = id;
        logger.debug("ConnectorExceptionType constructor created id [" + this.id + "]."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns ConnectorExceptionType description.
     * 
     * @return String description
     */
    public String getDescription()
    {
        logger.debug("Returning description [" + this.description + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.description;
    }

    /**
     * Returns ConnectorExceptionType id.
     * 
     * @return int id
     */
    public int getId()
    {
        logger.debug("Returning ID [" + this.id + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.id;
    }

    /**
     * Returns an array of current all ConnectorExceptionType.
     * 
     * @return ConnectorExceptionType[] these exception types
     */
    public static ExceptionType[] getExceptionTypes()
    {
        return ConnectorExceptionType.values();
    }

    /**
     * Get actual name of the enum instance
     * 
     * @return String
     */
    public String getName()
    {
        return this.name();
    }

    /**
     * Comparison for enum equality
     * 
     * @param exceptionType - The exception type to compare against
     * @return boolean
     */
    public boolean equals(ExceptionType exceptionType)
    {
        if (exceptionType == null)
        {
            return false;
        }
        if (exceptionType.getName().equals(this.name()))
        {
            return true;
        }
        return false;
    }

    /**
     * Runs this class for testing.
     * 
     * TODO replace with unit test
     * 
     * @param args - Arguments for main method, not used
     */
    public static void main(String args[])
    {
        for (ExceptionType exceptionType : getExceptionTypes())
        {
            System.out.println(exceptionType);
        }
        System.exit(0);
    }
}
