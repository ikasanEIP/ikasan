/* 
 * $Id: 
 * $URL: 
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

package org.ikasan.spec.error.reporting;

/**
 * This contract represents a platform level service for the heavyweight logging of
 * Errors
 * 
 * @author Ikasan Development Team
 * 
 */
public interface ErrorReportingService<EVENT>
{
    /** one week default time to live */
    public static final long DEFAULT_TIME_TO_LIVE = new Long(1000 * 60 * 60 * 24 * 7);

    /**
     * Logs an Error where there is an inflight Event involved in a Flow
     * 
     * @param flowElementName
     * @param event
     * @param throwable
     * @return uri for this reported error instance
     */
    public String notify(String flowElementName, EVENT event, Throwable throwable);

    /**
     * Logs an Error where no inflight Event was present.
     *
     * @param flowElementName
     * @param throwable
     * @return uri for this reported error instance
     */
    public String notify(String flowElementName, Throwable throwable);

    /**
     * Allow entities blacklisted to be marked with a timeToLive.
     * On expiry of the timeToLive the entity will no longer be blacklisted.
     *
     * @param timeToLive
     */
    public void setTimeToLive(Long timeToLive);

    /**
     * Housekeep expired exclusionEvents.
     */
    public void housekeep();

}
