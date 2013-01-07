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
