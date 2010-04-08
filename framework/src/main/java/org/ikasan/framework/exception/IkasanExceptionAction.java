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
 * Interface for the Ikasan Exception Action.
 * 
 * @author Ikasan Development Team
 */
public interface IkasanExceptionAction
{
    /** Default delay is 5000 milliseconds (5 seconds) */
    public final static String DEFAULT_DELAY = "5000";

    /** Default maxAttempts is infinite (-1) */
    public final static String DEFAULT_MAX_ATTEMPTS = "-1";
    
    /** Retry Indefinitely */
    public static final int RETRY_INFINITE = -1;

    /**
     * Get the action type which will dictate the outcome of the transactional flow.
     * 
     * @return IkasanExceptionActionType - action to take
     */
    public IkasanExceptionActionType getType();

    /**
     * Get the delay period. This is a delay time in milliseconds before the above action type is 'actioned'.
     * 
     * @return Long - period to wait before taking the action
     */
    public Long getDelay();

    /**
     * Get the maximum number of attempts of this action before we call it a day and go off and do something more
     * interesting. i.e. stopping the flow.
     * 
     * @return Integer - maximum attempts at the action before trying something else
     */
    public Integer getMaxAttempts();
}
