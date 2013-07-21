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
