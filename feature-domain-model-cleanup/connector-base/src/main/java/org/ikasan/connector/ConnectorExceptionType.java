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
