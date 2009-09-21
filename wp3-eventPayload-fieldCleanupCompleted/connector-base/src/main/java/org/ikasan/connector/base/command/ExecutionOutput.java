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
