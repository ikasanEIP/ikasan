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
