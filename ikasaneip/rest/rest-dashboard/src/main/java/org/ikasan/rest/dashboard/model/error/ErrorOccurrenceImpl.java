package org.ikasan.rest.dashboard.model.error;

import org.ikasan.spec.error.reporting.ErrorOccurrence;

public class ErrorOccurrenceImpl implements ErrorOccurrence<byte[]>
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
     * Representation of the Event as a String at the time that the error took place
     */
    private String eventAsString;

    /**
     * Time that this error was logged
     */
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

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    @Override
    public String getFlowName()
    {
        return this.flowName;
    }

    @Override
    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    @Override
    public String getFlowElementName()
    {
        return this.flowElementName;
    }

    @Override
    public void setFlowElementName(String flowElementName)
    {
        this.flowElementName = flowElementName;
    }

    @Override
    public String getErrorDetail()
    {
        return this.errorDetail;
    }

    @Override
    public void setErrorDetail(String errorDetail)
    {
        this.errorDetail = errorDetail;
    }

    @Override
    public String getEventLifeIdentifier()
    {
        return this.eventLifeIdentifier;
    }

    @Override
    public void setEventLifeIdentifier(String eventLifeIdentifier)
    {
        this.eventLifeIdentifier = eventLifeIdentifier;
    }

    @Override
    public byte[] getEvent()
    {
        return this.event;
    }

    @Override
    public void setEvent(byte[] event)
    {
        this.event = event;
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiry()
    {
        return this.expiry;
    }

    @Override
    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    @Override
    public String getEventRelatedIdentifier()
    {
        return this.eventRelatedIdentifier;
    }

    @Override
    public void setEventRelatedIdentifier(String eventRelatedIdentifier)
    {
        this.eventRelatedIdentifier = eventRelatedIdentifier;
    }

    @Override
    public String getUri()
    {
        return this.uri;
    }

    @Override
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    @Override
    public String getAction()
    {
        return this.action;
    }

    @Override
    public void setAction(String action)
    {
        this.action = action;
    }

    @Override
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getExceptionClass()
    {
        return this.exceptionClass;
    }

    @Override
    public void setExceptionClass(String exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }

    @Override
    public String getUserAction()
    {
        return this.userAction;
    }

    @Override
    public void setUserAction(String userAction)
    {
        this.userAction = userAction;
    }

    @Override
    public String getActionedBy()
    {
        return this.actionedBy;
    }

    @Override
    public void setActionedBy(String actionedBy)
    {
        this.actionedBy = actionedBy;
    }

    @Override
    public long getUserActionTimestamp()
    {
        return this.userActionTimestamp;
    }

    @Override
    public void setUserActionTimestamp(long userActionTimestamp)
    {
        this.userActionTimestamp = userActionTimestamp;
    }

    @Override
    public String getEventAsString()
    {
        return this.eventAsString;
    }

    @Override
    public void setEventAsString(String eventAsString)
    {
        this.eventAsString = eventAsString;
    }
}
