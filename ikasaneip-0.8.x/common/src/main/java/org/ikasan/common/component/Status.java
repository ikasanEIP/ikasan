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

import java.io.Serializable;
import java.util.Date;

/**
 * Status class for setting and getting the component status
 * at runtime.
 * 
 * @author Ikasan Development Team
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
