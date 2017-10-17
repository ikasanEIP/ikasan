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
public class ErrorOccurrenceImpl implements ErrorOccurrence<byte[]>, HarvestEvent
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

    /** action to be taken on this error incident */
    private String action;

    /**
     * Representation of the Event at the time that the error took place
     */
    private byte[] event;
    
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
     * useby date for the errorOccurrence, after which the system may delete it
     */
    private long expiry;
    
    /**
     * Action performed by the user
     */
    private String userAction;
    
    /**
     * Who performed the action
     */
    private String actionedBy;
    
    /**
     * When the action was performed
     */
    private long userActionTimestamp;

	/**
	 * Flag to indicate if the entity has been harvested.
     */
	private boolean harvested;

    /**
     * Constructor
     */
    public ErrorOccurrenceImpl()
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
    public ErrorOccurrenceImpl(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive, byte[] event, String eventAsString)
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
        this.eventAsString = eventAsString;
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
    public ErrorOccurrenceImpl(String moduleName, String flowName, String flowElementName, String errorDetail, String errorMessage, String exceptionClass, long timeToLive)
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
        return event;
    }

	public void setEvent(byte[] event)
    {
        this.event = event;
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
        return expiry;
    }

	public void setExpiry(long expiry)
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

	public void setUri(String uri)
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
		return userAction;
	}

	/**
	 * @param userAction the userAction to set
	 */
	public void setUserAction(String userAction)
	{
		this.userAction = userAction;
	}

	/**
	 * @return the actionedBy
	 */
	public String getActionedBy()
	{
		return actionedBy;
	}

	/**
	 * @param actionedBy the actionedBy to set
	 */
	public void setActionedBy(String actionedBy)
	{
		this.actionedBy = actionedBy;
	}

	/**
	 * @return the userActionTimestamp
	 */
	public long getUserActionTimestamp()
	{
		return userActionTimestamp;
	}

	/**
	 * @param userActionTimestamp the userActionTimestamp to set
	 */
	public void setUserActionTimestamp(long userActionTimestamp)
	{
		this.userActionTimestamp = userActionTimestamp;
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
		return harvested;
	}

	@Override
	public void setHarvested(boolean harvested)
	{
		this.harvested = harvested;
	}

	/* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((actionedBy == null) ? 0 : actionedBy.hashCode());
		result = prime * result
				+ ((errorDetail == null) ? 0 : errorDetail.hashCode());
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result
				+ ((eventAsString == null) ? 0 : eventAsString.hashCode());
		result = prime
				* result
				+ ((eventLifeIdentifier == null) ? 0 : eventLifeIdentifier
						.hashCode());
		result = prime
				* result
				+ ((eventRelatedIdentifier == null) ? 0
						: eventRelatedIdentifier.hashCode());
		result = prime * result
				+ ((exceptionClass == null) ? 0 : exceptionClass.hashCode());
		result = prime * result + (int) (expiry ^ (expiry >>> 32));
		result = prime * result
				+ ((flowElementName == null) ? 0 : flowElementName.hashCode());
		result = prime * result
				+ ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result
				+ ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result
				+ ((userAction == null) ? 0 : userAction.hashCode());
		result = prime * result
				+ (int) (userActionTimestamp ^ (userActionTimestamp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorOccurrenceImpl other = (ErrorOccurrenceImpl) obj;
		if (action == null)
		{
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (actionedBy == null)
		{
			if (other.actionedBy != null)
				return false;
		} else if (!actionedBy.equals(other.actionedBy))
			return false;
		if (errorDetail == null)
		{
			if (other.errorDetail != null)
				return false;
		} else if (!errorDetail.equals(other.errorDetail))
			return false;
		if (errorMessage == null)
		{
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (event == null)
		{
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (eventAsString == null)
		{
			if (other.eventAsString != null)
				return false;
		} else if (!eventAsString.equals(other.eventAsString))
			return false;
		if (eventLifeIdentifier == null)
		{
			if (other.eventLifeIdentifier != null)
				return false;
		} else if (!eventLifeIdentifier.equals(other.eventLifeIdentifier))
			return false;
		if (eventRelatedIdentifier == null)
		{
			if (other.eventRelatedIdentifier != null)
				return false;
		} else if (!eventRelatedIdentifier.equals(other.eventRelatedIdentifier))
			return false;
		if (exceptionClass == null)
		{
			if (other.exceptionClass != null)
				return false;
		} else if (!exceptionClass.equals(other.exceptionClass))
			return false;
		if (expiry != other.expiry)
			return false;
		if (flowElementName == null)
		{
			if (other.flowElementName != null)
				return false;
		} else if (!flowElementName.equals(other.flowElementName))
			return false;
		if (flowName == null)
		{
			if (other.flowName != null)
				return false;
		} else if (!flowName.equals(other.flowName))
			return false;
		if (moduleName == null)
		{
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (uri == null)
		{
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (userAction == null)
		{
			if (other.userAction != null)
				return false;
		} else if (!userAction.equals(other.userAction))
			return false;
		if (userActionTimestamp != other.userActionTimestamp)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ErrorOccurrence [uri=" + uri + ", moduleName=" + moduleName
				+ ", flowName=" + flowName + ", flowElementName="
				+ flowElementName + ", errorDetail=" + errorDetail
				+ ", errorMessage=" + errorMessage + ", exceptionClass="
				+ exceptionClass + ", eventLifeIdentifier="
				+ eventLifeIdentifier + ", eventRelatedIdentifier="
				+ eventRelatedIdentifier + ", action=" + action + ", event="
				+ event + ", eventAsString=" + eventAsString + ", timestamp="
				+ timestamp + ", expiry=" + expiry + ", userAction="
				+ userAction + ", actionedBy=" + actionedBy
				+ ", userActionTimestamp=" + userActionTimestamp + "]";
	}

	
}
