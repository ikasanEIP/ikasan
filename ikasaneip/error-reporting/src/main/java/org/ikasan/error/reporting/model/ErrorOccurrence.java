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


/**
 * This class represents an occurrence of an error in the system encapsulating as much as 
 * possible about the occurrence of that error
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrence<EVENT>
{
    /** unique identifier for this instance */
    private String uri;

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
     * the error message extracted from the errorDetail
     */
    private String errorMessage;
    
    /**
     * the exception class associated with the error
     */
    private String exceptionClass;

    /**
	 * Id of the event associated with this error, if it was event/flow related
	 */
	private String eventLifeIdentifier;

    /**
     * Related identifier
     */
    private String eventRelatedIdentifier;

    /** action to be taken on this error incident */
    private String action;

    /**
     * Representation of the Event at the time that the error took place
     */
    private EVENT event;

    /**
	 * Time that this error was logged
	 */
	private long timestamp;

    /**
     * useby date for the errorOccurrence, after which the system may delete it
     */
    private long expiry;

    /**
     * Constructor
     */
    private ErrorOccurrence()
    {
        // required by the ORM
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param flowElementName
     * @param errorDetail
     * @param event
     */
    public ErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive, EVENT event)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null");
        }

        this.flowName = flowName;
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null");
        }

        this.flowElementName = flowElementName;
        if(flowElementName == null)
        {
            throw new IllegalArgumentException("flowElementName cannot be 'null");
        }

        this.errorDetail = errorDetail;
        if(errorDetail == null)
        {
            throw new IllegalArgumentException("errorDetail cannot be 'null");
        }
        
        this.exceptionClass = exceptionClass;
        if(exceptionClass == null)
        {
            throw new IllegalArgumentException("exceptionClass cannot be 'null");
        }

        this.errorMessage = errorMessage;
        this.event = event;
        this.timestamp = System.currentTimeMillis();
        this.uri = String.valueOf(this.hashCode());
        this.expiry = System.currentTimeMillis() + timeToLive;
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param flowElementName
     * @param errorDetail
     */
    public ErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive)
    {
        this(moduleName, flowName, flowElementName, errorDetail, errorMessage, exceptionClass, timeToLive, null);
    }

    public String getModuleName() 
    {
        return moduleName;
    }

    private void setModuleName(String moduleName) 
    {
        this.moduleName = moduleName;
    }

    public String getFlowName() 
    {
        return flowName;
    }

    private void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public String getFlowElementName()
    {
        return flowElementName;
    }

    private void setFlowElementName(String flowElementName) 
    {
        this.flowElementName = flowElementName;
    }

    public String getErrorDetail() 
    {
        return errorDetail;
    }

    private void setErrorDetail(String errorDetail) 
    {
        this.errorDetail = errorDetail;
    }

    public String getEventLifeIdentifier()
    {
        return eventLifeIdentifier;
    }

    public void setEventLifeIdentifier(String eventLifeIdentifier) 
    {
        this.eventLifeIdentifier = eventLifeIdentifier;
    }

    public EVENT getEvent() 
    {
        return event;
    }

    private void setEvent(EVENT event) 
    {
        this.event = event;
    }

    public long getTimestamp() 
    {
        return timestamp;
    }

    private void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getExpiry() {
        return expiry;
    }

    private void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public String getEventRelatedIdentifier() 
    {
        return eventRelatedIdentifier;
    }

    public void setEventRelatedIdentifier(String eventRelatedIdentifier) 
    {
        this.eventRelatedIdentifier = eventRelatedIdentifier;
    }

    public String getUri()
    {
        return this.uri;
    }

    private void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getAction() 
    {
        return action;
    }

    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    private void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }
    
    /**
	 * @return the exceptionClass
	 */
	public String getExceptionClass()
	{
		return exceptionClass;
	}

	/**
	 * @param exceptionClass the exceptionClass to set
	 */
	public void setExceptionClass(String exceptionClass)
	{
		this.exceptionClass = exceptionClass;
	}

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorOccurrence that = (ErrorOccurrence) o;

        if (timestamp != that.timestamp) return false;
        if (!errorDetail.equals(that.errorDetail)) return false;
        if (event != null ? !event.equals(that.event) : that.event != null) return false;
        if (!flowElementName.equals(that.flowElementName)) return false;
        if (!flowName.equals(that.flowName)) return false;
        if (!moduleName.equals(that.moduleName)) return false;
        if (!uri.equals(that.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() 
    {
        int result = moduleName.hashCode();
        result = 31 * result + flowName.hashCode();
        result = 31 * result + flowElementName.hashCode();
        result = 31 * result + errorDetail.hashCode();
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() 
    {
        return "ErrorOccurrence{" +
                "uri='" + uri + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", flowName='" + flowName + '\'' +
                ", flowElementName='" + flowElementName + '\'' +
                ", errorDetail='" + errorDetail + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", exceptionClass='" + errorMessage + '\'' +
                ", eventLifeIdentifier='" + eventLifeIdentifier + '\'' +
                ", eventRelatedIdentifier='" + eventRelatedIdentifier + '\'' +
                ", action='" + action + '\'' +
                ", event=" + event +
                ", timestamp=" + timestamp +
                ", expiry=" + expiry +
                '}';
    }
}
