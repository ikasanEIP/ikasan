/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc and individual contributors as indicated
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
package org.ikasan.common.component;

import org.apache.log4j.Logger;

/**
 * Class used to report the status of a generic runtime component.
 * 
 * @author Ikasan Development Team
 */
public enum ComponentState
{
    /** Component state cannot be identified */
    UNKNOWN(new Integer(0), "Unknown", "Cannot determine component status"), //$NON-NLS-1$ //$NON-NLS-2$
    /** Component stopped */
    STOPPED(new Integer(10), "Stopped", "Component is not currently running"), //$NON-NLS-1$ //$NON-NLS-2$
    /** Component running */
    RUNNING(new Integer(20), "Running", "Component is running and happy"), //$NON-NLS-1$ //$NON-NLS-2$
    /** Component running suspiciously */
    RUNNING_SUSPICIOUS(new Integer(30), "Running Suspicious", //$NON-NLS-1$
            "Component is running, but acting suspiciously (ie. high number of exceptions)"), //$NON-NLS-1$
    /** Component recovering */
    RECOVERING(new Integer(40), "Recovering", "Component is in a retry/recovery state"), //$NON-NLS-1$//$NON-NLS-2$
    /** Component error */
    ERROR(new Integer(50), "Error", //$NON-NLS-1$
            "Component is running, but in an error state. It needs attention"), //$NON-NLS-1$
    ;
    /** Logger */
    private static Logger logger = Logger.getLogger(ComponentState.class);

    /** unique id of the state */
    private final Integer id;

    /** name of the state */
    private final String state;

    /** short state description for logging purposes */
    private final String description;

    /**
     * Default Constructor
     * 
     * @param id - The state id
     * @param state - The state
     * @param description - The description of the state
     */
    private ComponentState(final Integer id, final String state, final String description)
    {
        this.id = id;
        this.state = state;
        this.description = description;
    }

    /**
     * Getter for id
     * 
     * @return the id
     */
    public Integer getId()
    {
        logger.debug("Getting id [" + this.id + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.id;
    }

    /**
     * Getter for state
     * 
     * @return the state
     */
    public String getState()
    {
        logger.debug("Getting state [" + this.state + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.state;
    }

    /**
     * Getter for description
     * 
     * @return the description
     */
    public String getDescription()
    {
        logger.debug("Getting description [" + this.description + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        return this.description;
    }

    /**
     * Instance comparison
     * 
     * @param status - The status
     * @return boolean true if they are equal
     */
    public boolean equals(ComponentState status)
    {
        if (status == null)
        {
            return false;
        }
        return this.id.equals(status.id);
    }

}
