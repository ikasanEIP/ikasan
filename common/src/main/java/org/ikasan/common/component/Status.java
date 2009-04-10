/*
 * $Id: Status.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/component/Status.java $
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
package org.ikasan.common.component;

import java.io.Serializable;
import java.util.Date;

/**
 * Status class for setting and getting the component status
 * at runtime.
 * 
 * @author Jeff Mitchell
 */
public class Status
    implements Serializable
{
    /** Serial ID */
    private static final long serialVersionUID = -327855826132075594L;
    
    /** componentState enum */
    private final ComponentState componentState;
    
    /** status last update time */
    private final Long lastUpdateTime;

    /**
     * Creates a new instance of <code>Spec</code>
     * with the specified component status and last updated time.
     * @param componentState 
     * @param lastUpdateTime 
     */
    public Status(final ComponentState componentState,
                  final Long lastUpdateTime)
    {
        this.componentState = componentState;
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * Getter for component state
     * @return componentState enum instance
     */
    public ComponentState getComponentState()
    {
        return this.componentState;
    }

    /**
     * Getter for status last update time
     * @return long last update time in millis
     */
    public Long getLastUpdateTime()
    {
        return this.lastUpdateTime;
    }

    /**
     * Compare instances of Status
     * @param status
     * @return boolean
     */
    public boolean equals(Status status)
    {
        if (status == null) return false;
        
        if (this.componentState.equals(status.componentState) &&
           this.lastUpdateTime.equals(status.lastUpdateTime) )
            return true;
        
        return false;
    }

    /**
     * Utility method for diffing status update times.
     * @param status
     * @return Long
     */
    public Long diffLastUpdateTime(Status status)
    {
        return this.lastUpdateTime - status.lastUpdateTime; 
    }

    /**
     * Runs this class for test.
     * @param args 
     */
    public static void main(String args[])
    {
        Status currentStatus = new Status(ComponentState.UNKNOWN,
                                          new Date().getTime());

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            System.out.println("Thread was woken early");
            e.printStackTrace();
        }
        
        Status futureStatus = new Status(ComponentState.UNKNOWN,
                                         new Date().getTime());
        if(currentStatus.equals(futureStatus))
            System.out.println("currentCS"
                             + currentStatus.toString()
                             + " EQUALS futureCS"
                             + futureStatus.toString());
        else
            System.out.println("CurrentCS"
                    + currentStatus.toString()
                    + " DOES NOT EQUAL futureCS"
                    + futureStatus.toString());

        System.out.println(futureStatus.diffLastUpdateTime(currentStatus));
        
    }

}
