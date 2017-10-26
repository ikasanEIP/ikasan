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
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorOccurrenceAction
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
    private byte[] event;

    /**
	 * Time that this error was logged
	 */
	private long timestamp;

    /**
     * useby date for the ActionedErrorOccurrence, after which the system may delete it
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
     * Constructor
     */
    private ErrorOccurrenceAction()
    {
        // required by the ORM	
    }

   /**
    * Constructor
    * 
    * @param errorOccurrence
    * @param userAction
    * @param actionedBy
    * @param timeToLive
    */
    public ErrorOccurrenceAction(ErrorOccurrenceImpl errorOccurrence, String userAction, String actionedBy,
                                 long timeToLive)
    {
    	if(errorOccurrence == null)
    	{
    		throw new IllegalArgumentException("errorOccurrence cannot be 'null");	
    	}
    	
        this.moduleName = errorOccurrence.getModuleName();
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null");
        }

        this.flowName = errorOccurrence.getFlowName();
        if(flowName == null)
        {
            throw new IllegalArgumentException("flowName cannot be 'null");
        }

        this.flowElementName = errorOccurrence.getFlowElementName();
        if(flowElementName == null)
        {
            throw new IllegalArgumentException("flowElementName cannot be 'null");
        }
        
        this.actionedBy = actionedBy;
        if(this.actionedBy == null)
        {
            throw new IllegalArgumentException("actionedBy cannot be 'null");
        }
        
        this.userAction = userAction;
        if(this.userAction == null)
        {
            throw new IllegalArgumentException("actiouserActionnedBy cannot be 'null");
        }


        this.errorDetail = errorOccurrence.getErrorDetail();
        this.exceptionClass = errorOccurrence.getExceptionClass();        
        this.action = errorOccurrence.getAction();        
        this.errorMessage = errorOccurrence.getErrorMessage();
        this.event = errorOccurrence.getEvent();
        this.timestamp = errorOccurrence.getTimestamp();
        this.userActionTimestamp = System.currentTimeMillis();
        this.uri = errorOccurrence.getUri();
        this.expiry = System.currentTimeMillis() + timeToLive;
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

    public byte[] getEvent()
    {
        return event;
    }

    private void setEvent(byte[] event)
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
		ErrorOccurrenceAction other = (ErrorOccurrenceAction) obj;
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
		return "ErrorOccurrenceAction [uri=" + uri + ", moduleName="
				+ moduleName + ", flowName=" + flowName + ", flowElementName="
				+ flowElementName + ", errorDetail=" + errorDetail
				+ ", errorMessage=" + errorMessage + ", exceptionClass="
				+ exceptionClass + ", eventLifeIdentifier="
				+ eventLifeIdentifier + ", eventRelatedIdentifier="
				+ eventRelatedIdentifier + ", action=" + action + ", event="
				+ event + ", timestamp=" + timestamp + ", expiry=" + expiry
				+ ", userAction=" + userAction + ", actionedBy=" + actionedBy
				+ ", userActionTimestamp=" + userActionTimestamp + "]";
	}

	

}
