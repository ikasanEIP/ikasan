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


import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.error.reporting.ErrorOccurrence;

/**
 * This class represents an occurrence of an error in the system encapsulating as much as 
 * possible about the occurrence of that error
 * 
 * @author Ikasan Development Team
 *
 */
public class SolrErrorOccurrence implements ErrorOccurrence<byte[]>, HarvestEvent
{
    /** unique identifier for this instance */
	@Field("errorUri")
    private String uri;

    /**
     * name of the module where this error occurred
     */
	@Field("moduleName")
    private String moduleName;

    /**
     * name of the flow where this error occurred, if it was event/flow related
     */
	@Field("flowName")
    private String flowName;

    /**
     * name of the flow element where this error occurred, if it was event/flow related
     */
	@Field("componentName")
    private String flowElementName;

    /**
     * raw dump of the error as it occurred
     */
	@Field("errorDetail")
    private String errorDetail;

    /**
     * the error message extracted from the errorDetail
     */
	@Field("errorMessage")
    private String errorMessage;

    /**
     * the exception class associated with the error
     */
	@Field("exceptionClass")
    private String exceptionClass;

    /**
	 * Id of the event associated with this error, if it was event/flow related
	 */
	@Field("event")
	private String eventLifeIdentifier;

    /**
     * Related identifier
     */
	@Field("relatedEventId")
    private String eventRelatedIdentifier;

    /**
     * Representation of the Event as a String at the time that the error took place
     */
	@Field("payload")
    private String eventAsString;

    /**
	 * Time that this error was logged
	 */
	@Field("timestamp")
	private long timestamp;


    /**
     * Constructor
     */
    public SolrErrorOccurrence()
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
    public SolrErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive, byte[] event, String eventAsString)
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
        this.eventAsString = eventAsString;
        this.timestamp = System.currentTimeMillis();
        this.uri = String.valueOf(this.hashCode());
    }

    /**
     * Constructor
     * @param moduleName
     * @param flowName
     * @param flowElementName
     * @param errorDetail
     */
    public SolrErrorOccurrence(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive)
    {
        this(moduleName, flowName, flowElementName, errorDetail, errorMessage, exceptionClass, timeToLive, null, null);
    }

    public String getModuleName() 
    {
        return moduleName;
    }

	public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getFlowName() 
    {
        return flowName;
    }

	public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public String getFlowElementName()
    {
        return flowElementName;
    }

	public void setFlowElementName(String flowElementName)
    {
        this.flowElementName = flowElementName;
    }

    public String getErrorDetail() 
    {
        return errorDetail;
    }

	public void setErrorDetail(String errorDetail)
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

    public byte[] getEvent()
    {
        if(this.eventAsString != null)
        {
            return this.eventAsString.getBytes();
        }

        return "".getBytes();
    }

	public void setEvent(byte[] event)
    {
		throw new UnsupportedOperationException();
    }

    public long getTimestamp() 
    {
        return timestamp;
    }

	public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getExpiry() {
		throw new UnsupportedOperationException();
    }

	public void setExpiry(long expiry)
    {
		throw new UnsupportedOperationException();
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

	public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getAction()
    {
		throw new UnsupportedOperationException();
    }

    public void setAction(String action) 
    {
		throw new UnsupportedOperationException();
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

	public void setErrorMessage(String errorMessage)
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
	
	/**
	 * @return the userAction
	 */
	public String getUserAction()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @param userAction the userAction to set
	 */
	public void setUserAction(String userAction)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the actionedBy
	 */
	public String getActionedBy()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @param actionedBy the actionedBy to set
	 */
	public void setActionedBy(String actionedBy)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the userActionTimestamp
	 */
	public long getUserActionTimestamp()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @param userActionTimestamp the userActionTimestamp to set
	 */
	public void setUserActionTimestamp(long userActionTimestamp)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the eventAsString
	 */
	public String getEventAsString()
	{
		return eventAsString;
	}

	/**
	 * @param eventAsString the eventAsString to set
	 */
	public void setEventAsString(String eventAsString)
	{
		this.eventAsString = eventAsString;
	}

	public boolean isHarvested()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHarvested(boolean harvested)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer("SolrErrorOccurrenceImpl{");
		sb.append("uri='").append(uri).append('\'');
		sb.append(", moduleName='").append(moduleName).append('\'');
		sb.append(", flowName='").append(flowName).append('\'');
		sb.append(", flowElementName='").append(flowElementName).append('\'');
		sb.append(", errorDetail='").append(errorDetail).append('\'');
		sb.append(", errorMessage='").append(errorMessage).append('\'');
		sb.append(", exceptionClass='").append(exceptionClass).append('\'');
		sb.append(", eventLifeIdentifier='").append(eventLifeIdentifier).append('\'');
		sb.append(", eventRelatedIdentifier='").append(eventRelatedIdentifier).append('\'');
		sb.append(", eventAsString='").append(eventAsString).append('\'');
		sb.append(", timestamp=").append(timestamp);
		sb.append('}');
		return sb.toString();
	}
}
