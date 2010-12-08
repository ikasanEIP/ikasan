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
package org.ikasan.connector.base.command;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class to capture the result of the execution of a command
 * 
 * @author Ikasan Development Team
 * 
 */
public class ExecutionOutput extends HashMap<String, Object>
{
    /** Unique UID */
    private static final long serialVersionUID = -7723907118124829388L;

    /** Logger */
    private static Logger logger = Logger.getLogger(ExecutionOutput.class);

    /** key for single object output */
    protected final String DEFAULT_SINGLE = "defaultSingle";

    /** key for single List output */
    protected final String DEFAULT_LIST = "defaultList";
    
    @Override
    public Object put(String arg0, Object arg1)
    {
        logger.debug("Setting [" + arg0 + "], with value: [" + arg1 + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return super.put(arg0, arg1);
    }

    /** No args Constructor */
    public ExecutionOutput()
    {
        // Do Nothing
    }

    /**
     * Convenience Constructor for a single object output
     * 
     * @param output
     */
    public ExecutionOutput(Object output)
    {
        logger.debug("constructor called with: [" + output + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (output instanceof List)
        {
            put(DEFAULT_LIST, output);
        }
        else
        {
            put(DEFAULT_SINGLE, output);
        }
    }

    /**
     * Convenience accessor for a single object output
     * 
     * @return Object or null;
     */
    public Object getResult()
    {
        return get(DEFAULT_SINGLE);
    }

    /**
     * Convenience accessor for a single list output
     * 
     * @return List or null
     */
    public List<?> getResultList()
    {
        return (List<?>) get(DEFAULT_LIST);
    }

}
