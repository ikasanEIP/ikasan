/*
 * $Id: IkasanExceptionActionImpl.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/exception/IkasanExceptionActionImpl.java $
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
 * Concrete implementation of the Ikasan Exception Definition interface.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionActionImpl implements IkasanExceptionAction
{
    /** Type of action to take */
    private IkasanExceptionActionType type;

    /** Delay in milliseconds before invoking above action type - default is no delay (0) */
    private Long delay;

    /**
     * maximum number of attempts of the action type before resorting to an emergencyAction (usually stop) - default
     * (-1) is infinite
     */
    private Integer maxAttempts;

    /**
     * Constructor
     * 
     * @param type The type of exception action
     */
    public IkasanExceptionActionImpl(IkasanExceptionActionType type)
    {
        this.type = type;
        this.delay = new Long(IkasanExceptionAction.DEFAULT_DELAY);
        this.maxAttempts = new Integer(IkasanExceptionAction.DEFAULT_MAX_ATTEMPTS);
    }

    /**
     * Constructor
     * 
     * @param type The type of exception action
     * @param delay The delay before we perform the action
     * @param maxAttempts The maximum number of attempts we try the action for
     */
    public IkasanExceptionActionImpl(IkasanExceptionActionType type, Long delay, Integer maxAttempts)
    {
        this.type = type;
        this.delay = delay;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Get the type
     * 
     * @return the type
     */
    public IkasanExceptionActionType getType()
    {
        return this.type;
    }

    /**
     * Set the type of action
     * 
     * @param type The type of action
     */
    public void setType(IkasanExceptionActionType type)
    {
        this.type = type;
    }

    /**
     * Get the delay
     * 
     * @return the delay
     */
    public Long getDelay()
    {
        return this.delay;
    }

    /**
     * Set the delay
     * 
     * @param delay the delay to set
     */
    public void setDelay(Long delay)
    {
        this.delay = delay;
    }

    /**
     * Get the max attempts
     * 
     * @return the maxAttempts
     */
    public Integer getMaxAttempts()
    {
        return this.maxAttempts;
    }

    /**
     * Set the max attempts
     * 
     * @param maxAttempts the maxAttempts to set
     */
    public void setMaxAttempts(Integer maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }
}
