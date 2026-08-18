package org.ikasan.spec.search.model;

/**
 * Interface for Ikasan document model representing events stored in the persistence layer.
 * This interface defines the contract for accessing event data, metadata, and error information.
 */
public interface IkasanESBDocument {

    /**
     * Get the unique identifier for this document.
     *
     * @return the document ID
     */
    String getId();

    /**
     * Get the identifier for this document.
     * This is an alias for getId().
     *
     * @return the document identifier
     */
    String getIdentifier();

    /**
     * Get the event payload.
     *
     * @return the event payload as a string
     */
    String getEvent();

    /**
     * Get the type of this document.
     *
     * @return the document type
     */
    String getType();

    /**
     * Get the module name.
     *
     * @return the module name
     */
    String getModuleName();

    /**
     * Get the flow name.
     *
     * @return the flow name
     */
    String getFlowName();

    /**
     * Get the component name.
     *
     * @return the component name
     */
    String getComponentName();

    /**
     * Get the timestamp when this event occurred.
     *
     * @return the timestamp in milliseconds
     */
    long getTimestamp();

    /**
     * Get the timestamp (alias for getTimestamp).
     *
     * @return the timestamp in milliseconds
     */
    long getTimeStamp();

    /**
     * Get the expiry time for this document.
     *
     * @return the expiry time in milliseconds
     */
    long getExpiry();

    /**
     * Get the event ID.
     *
     * @return the event ID
     */
    String getEventId();

    /**
     * Get the error action if this is an error event.
     *
     * @return the error action
     */
    String getErrorAction();

    /**
     * Get the error URI if this is an error event.
     *
     * @return the error URI
     */
    String getErrorUri();

    /**
     * Get the error detail if this is an error event.
     *
     * @return the error detail
     */
    String getErrorDetail();

    /**
     * Get the error message if this is an error event.
     *
     * @return the error message
     */
    String getErrorMessage();

    /**
     * Get the exception class name if this is an error event.
     *
     * @return the exception class name
     */
    String getExceptionClass();

    /**
     * Get the raw payload as bytes.
     *
     * @return the raw payload bytes
     */
    byte[] getPayloadRaw();

    /**
     * Set the unique identifier for this document.
     *
     * @param id the document ID
     */
    void setId(String id);

    /**
     * Set the event payload.
     *
     * @param event the event payload
     */
    void setEvent(String event);

    /**
     * Set the type of this document.
     *
     * @param type the document type
     */
    void setType(String type);

    /**
     * Set the module name.
     *
     * @param moduleName the module name
     */
    void setModuleName(String moduleName);

    /**
     * Set the flow name.
     *
     * @param flowName the flow name
     */
    void setFlowName(String flowName);

    /**
     * Set the component name.
     *
     * @param componentName the component name
     */
    void setComponentName(String componentName);

    /**
     * Set the timestamp when this event occurred.
     *
     * @param timeStamp the timestamp in milliseconds
     */
    void setTimeStamp(long timeStamp);

    /**
     * Set the expiry time for this document.
     *
     * @param expiry the expiry time in milliseconds
     */
    void setExpiry(long expiry);

    /**
     * Set the event ID.
     *
     * @param eventId the event ID
     */
    void setEventId(String eventId);

    /**
     * Set the error action if this is an error event.
     *
     * @param errorAction the error action
     */
    void setErrorAction(String errorAction);

    /**
     * Set the error URI if this is an error event.
     *
     * @param errorUri the error URI
     */
    void setErrorUri(String errorUri);

    /**
     * Set the error detail if this is an error event.
     *
     * @param errorDetail the error detail
     */
    void setErrorDetail(String errorDetail);

    /**
     * Set the error message if this is an error event.
     *
     * @param errorMessage the error message
     */
    void setErrorMessage(String errorMessage);

    /**
     * Set the exception class name if this is an error event.
     *
     * @param exceptionClass the exception class name
     */
    void setExceptionClass(String exceptionClass);

    /**
     * Set the raw payload as bytes.
     *
     * @param payloadRaw the raw payload bytes
     */
    void setPayloadRaw(byte[] payloadRaw);
}
