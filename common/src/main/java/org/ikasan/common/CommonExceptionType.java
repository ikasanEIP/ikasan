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
package org.ikasan.common;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This is an enumeration of common exception types providing 
 * categories within the CommonException and CommonRuntimeException.
 *
 * NOTE: The important definition in the enum is the name of the enum which is 
 * instantiated. This name is used to match in the exceptionResolver.xml 
 * for the associated component group. The enum description and id are there for
 * potential logging and future use.
 * 
 * @author Ikasan Development Team
 */
public enum CommonExceptionType 
    implements ExceptionType
{
    /** Exception type not defined */
    UNDEFINED("Exception type was not defined", 0),
    /** Failure to instantiate an object within the ResourceLoader class */
    RESOURCE_LOADER_FAILED("Failed on invocation within ResourceLoader", 1),
    /** Failure to instantiate an Envelope */
    ENVELOPE_INSTANTIATION_FAILED("Failed on instantiate an Envelope", 2),
    /** Failure to instantiate a Payload */
    PAYLOAD_INSTANTIATION_FAILED("Failed on instantiate a Payload", 3),
    /** We must ensure each payload has a name */
    INVALID_PAYLOAD_NAME("Payload name was undefined or invalid", 4),
    /** We must ensure each payload has a valid spec */
    INVALID_PAYLOAD_SPEC("Payload spec was undefined or invalid", 5),
    /** We must ensure each payload has a source system */
    INVALID_PAYLOAD_SRC_SYSTEM("Payload srcSystem was undefined or invalid", 6),
    /** Failure on validation of an XML document */
    FAILED_XML_VALIDATION("Failed on validation of an XML document", 7),
    ;

    /** Logger */
    private Logger logger = Logger.getLogger(CommonExceptionType.class);

    /** exception type description */
    private String description = null;
    /** exception type unique identifier */
    private int id = 0;

    /**
     * Creates a new instance of <code>CommonExceptionType</code> with 
     * the specified exception description and id.
     * @param description 
     * @param id 
     */
    private CommonExceptionType(final String description, final int id)
    {
        this.description = description;
        this.id = id;
        logger.debug("CommonExceptionType constructor created id [" + this.id + "]."); //$NON-NLS-1$
    }

    /**
     * Returns CommonExceptionType description.
     * @return String description
     */
    public String getDescription()
    {
        logger.debug("Returning description [" + this.description + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.description;
    }

    /**
     * Returns CommonExceptionType id.
     * @return int id
     */
    public int getId()
    {
        logger.debug("Returning ID ["+ this.id + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.id;
    }

    /**
     * Returns an array of current all CommonExceptionTypes.
     * @return CommonExceptionType[] these exception types
     */
    public static ExceptionType[] getExceptionTypes()
    {
        return CommonExceptionType.values();
    }

    /**
     * Get actual name of the enum instance
     * @return String
     */
    public String getName()
    {
        return this.name();
    }
    
    /**
     * Comparison for enum equality
     * @param exceptionType
     * @return boolean
     */
    public boolean equals(ExceptionType exceptionType) 
    {
        if(exceptionType == null)
            return false;

        if(exceptionType.getName().equals(this.name()))
            return true;

        return false;
    }
    

// main() method
// /////////////////////////////////////////////////////////////////////////////

    /**
     * Runs this class for testing.
     * @param args 
     */
    public static void main(String args[])
    {
        for (ExceptionType exceptionType: CommonExceptionType.getExceptionTypes())
        {
            System.out.println(exceptionType);
        }

        System.exit(0);
    }
}
