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
package org.ikasan.framework.exception;

/**
 * Ikasan interface defining all possible exception action types
 * 
 * @author Ikasan Development Team
 */
public enum IkasanExceptionActionType
{
    /** Rollback the operation and stop the event flow */
    ROLLBACK_STOP(true, true, "Operation will rollback and stop."),
    /** Rollback the operation and retry the flow on the same event */
    ROLLBACK_RETRY(true, false, "Operation will rollback and retry."),
    /** Skip over this event and continue with the next event. */
    SKIP_EVENT(false, false, "Operation will skip the event causing this exception and move on to the next event.");

    /** Rollback flag */
    private boolean rollback;

    /** Flow action */
    private boolean stop;

    /** Descriptive text for this action - useful for log output */
    private String description;



    /**
     * Constructor
     * 
     * @param rollback Rollback flag
     * @param stop Stop flag
     * @param description description
     * @param precedence precedence
     */
    private IkasanExceptionActionType(final boolean rollback, final boolean stop, final String description)
    {
        this.rollback = rollback;
        this.stop = stop;
        this.description = description;

    }

    /**
     * Get the action's descriptive text.
     * 
     * @return the action's descriptive text.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get rollback option for this action type.
     * 
     * @return boolean.
     */
    public boolean isRollback()
    {
        return this.rollback;
    }

    /**
     * Get stop option for this action type.
     * 
     * @return boolean.
     */
    public boolean isStop()
    {
        return this.stop;
    }


}
