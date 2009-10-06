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
package org.ikasan.framework.exception;

/**
 * Ikasan interface defining all possible exception action types
 * 
 * @author Ikasan Development Team
 */
public enum IkasanExceptionActionType
{
    /** Rollback the operation and stop the event flow */
    ROLLBACK_STOP(true, "Operation will rollback and stop."),
    /** Rollback the operation and retry the flow on the same event */
    ROLLBACK_RETRY(false, "Operation will rollback and retry."),
    /** Skip over this event and continue with the next event. */
    EXCLUDE(false, "Operation will rollback and initiator will note to exclude the Event next time it is seen.");


    /** Flow action */
    private boolean stop;

    /** Descriptive text for this action - useful for log output */
    private String description;



    /**
     * Constructor
     * 
     * @param stop Stop flag
     * @param description description
     * @param precedence precedence
     */
    private IkasanExceptionActionType(final boolean stop, final String description)
    {
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
     * Get stop option for this action type.
     * 
     * @return boolean.
     */
    public boolean isStop()
    {
        return this.stop;
    }


}
