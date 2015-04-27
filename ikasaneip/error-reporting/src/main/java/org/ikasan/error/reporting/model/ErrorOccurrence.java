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
package org.ikasan.error.reporting.model;

import java.util.Date;

/**
 * This class represents an occurrence of an error in the system encapsulating as much as 
 * possible about the occurrence of that error
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrence<EVENT>
{
	/**
	 * Unique identifier, populated by persistence mechanism
	 */
	private long id;

    /**
     * name of the module where this error occurred
     */
    private String moduleName;

    /**
     * name of the flow where this error occurred, if it was event/flow related
     */
    private String flowName;

    /**
     * name of the flow element where this error occurred, if it was event/flow related
     */
    private String flowElementName;

    /**
	 * raw dump of the error as it occurred
	 */
	private String errorDetail;

	/**
	 * Id of the event associated with this error, if it was event/flow related
	 */
	private String eventIdentifier;

    /**
     * Representation of the Event at the time that the error took place
     */
    private EVENT event;

    /**
	 * Time that this error was logged
	 */
	private long createdDateTime;

	/**
	 * useby date for the errorOccurrence, after which the system may delete it
	 */
	private long expiry;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowElementName() {
        return flowElementName;
    }

    public void setFlowElementName(String flowElementName) {
        this.flowElementName = flowElementName;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getEventIdentifier() {
        return eventIdentifier;
    }

    public void setEventIdentifier(String eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public EVENT getEvent() {
        return event;
    }

    public void setEvent(EVENT event) {
        this.event = event;
    }

    public long getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(long createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    /**
	 * @param throwable
	 * @return
	 */
	private String flattenThrowable(Throwable throwable) {
		StringBuffer flattenedBuffer = new StringBuffer();
		
		Throwable cause = throwable;
		while (cause!=null){
			flattenedBuffer.append(throwable.toString());
			flattenedBuffer.append("\n");
			for (StackTraceElement stackTraceElement : cause.getStackTrace()){
				flattenedBuffer.append(stackTraceElement.toString());
				flattenedBuffer.append("\n");
			}
			if (cause.getCause()!=null){
				flattenedBuffer.append("caused by ...\n");
			}
			cause = cause.getCause();
		}
		return flattenedBuffer.toString();
	}
}
