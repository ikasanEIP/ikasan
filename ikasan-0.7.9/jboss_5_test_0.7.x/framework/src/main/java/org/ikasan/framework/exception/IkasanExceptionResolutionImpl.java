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
 * Concrete implementation of the Ikasan Exception Resolution interface.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionResolutionImpl implements IkasanExceptionResolution
{
    /** Emergency id constant */
    private static String EMERGENCY_ID = "emergencyResolution";

    /** Static emergency action */
    private static IkasanExceptionAction emergencyAction = new IkasanExceptionActionImpl(
        IkasanExceptionActionType.ROLLBACK_STOP, new Long(0), new Integer(0));

    /** Static emergency resolution */
    private static IkasanExceptionResolution emergencyResolution = new IkasanExceptionResolutionImpl(EMERGENCY_ID,
        emergencyAction);

    /** Resolution identifier */
    private String id;

    /** Resolution action */
    private IkasanExceptionAction action;

    /** Default constructor */
    public IkasanExceptionResolutionImpl()
    {
        // Default constructor
    }

    /**
     * Constructor
     * 
     * @param id The id of this resolution
     * @param action The action to take
     */
    public IkasanExceptionResolutionImpl(final String id, final IkasanExceptionAction action)
    {
        this.id = id;
        this.action = action;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the action
     */
    public IkasanExceptionAction getAction()
    {
        return this.action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(IkasanExceptionAction action)
    {
        this.action = action;
    }

    /**
     * Get fixed emergency resolution. This is required if things have really gone wrong to the extent the exception
     * handling process cannot take the normal route to resolving an exception to a resolution.
     * 
     * @return IkasanExceptionResolution - required as a last resort to stop when things have gone critically wrong.
     */
    public static IkasanExceptionResolution getEmergencyResolution()
    {
        return emergencyResolution;
    }
}
