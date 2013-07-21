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
package org.ikasan.spec.error;

import org.ikasan.spec.exclusion.ExcludedEvent;
import java.util.Date;

/**
 * This interface represents a contract for the occurrence of an error in the system encapsulating
 * as much as possible about the occurrence of that error
 * 
 * 
 * @author Ikasan Development Team
 * 
 */
public interface ErrorOccurrence<T>
{
    /**
     * @return the errorDetail
     */
    public String getErrorDetail();

    /**
     * @return the eventId
     */
    public String getEventId();

    /**
     * @return the flowElementName
     */
    public String getFlowElementName();

    /**
     * @return the flowName
     */
    public String getFlowName();

    /**
     * @return the logTime
     */
    public Date getLogTime();

    /**
     * @return the moduleName
     */
    public String getModuleName();

    /**
     * @param errorDetail the errorDetail to set
     */
    public void setErrorDetail(String errorDetail);

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId);

    /**
     * @param flowElementName the flowElementName to set
     */
    public void setFlowElementName(String flowElementName);

    /**
     * @param flowName the flowName to set
     */
    public void setFlowName(String flowName);

    /**
     * @param logTime the logTime to set
     */
    public void setLogTime(Date logTime);

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName);

    /**
     * @return the id
     */
    public Long getId();

    /**
     * @return maximum of first 100 characters of errorDetail
     */
    public String getErrorSummary();

    /**
     * @param id the id to set
     */
    public void setId(Long id);

    /**
     * @param expiry the expiry to set
     */
    public void setExpiry(Date expiry);

    /**
     * @return the expiry
     */
    public Date getExpiry();

    /**
     * Accessor for ExcludedEvent
     * 
     * @return excluded event if one exists
     */
    public ExcludedEvent<T> getExcludedEvent();

    /**
     * Setter for excluded Event TODO make this non public
     * 
     * @param excludedEvent
     */
    public void setExcludedEvent(ExcludedEvent<T> excludedEvent);

    /**
     * Mutator for url
     * 
     * @param url
     */
    public void setUrl(String url);

    /**
     * Accessor for url
     * 
     * @return
     */
    public String getUrl();

    /**
     * Accessor for actionTaken
     * 
     * @return actionTaken
     */
    public String getActionTaken();

    /**
     * Mutator for actionTaken
     * 
     * @param actionTaken
     */
    public void setActionTaken(String actionTaken);

    /**
     * Accessor for ErrorEvent
     * 
     * @return errorEvent if any
     */
    public T getErrorEvent();

}
