package org.ikasan.dashboard.dto;

import org.ikasan.spec.search.model.IkasanESBDocument;

/**
 * Implementation of IkasanESBDocument for REST client deserialization.
 */
public class IkasanESBDocumentImpl implements IkasanESBDocument {

    private String id;
    private String event;
    private String type;
    private String moduleName;
    private String flowName;
    private String componentName;
    private long timestamp;
    private long expiry;
    private String eventId;
    private String errorAction;
    private String errorUri;
    private String errorDetail;
    private String errorMessage;
    private String exceptionClass;
    private byte[] payloadRaw;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    @Override
    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiry() {
        return expiry;
    }

    @Override
    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getErrorAction() {
        return errorAction;
    }

    @Override
    public void setErrorAction(String errorAction) {
        this.errorAction = errorAction;
    }

    @Override
    public String getErrorUri() {
        return errorUri;
    }

    @Override
    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    @Override
    public String getErrorDetail() {
        return errorDetail;
    }

    @Override
    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getExceptionClass() {
        return exceptionClass;
    }

    @Override
    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    @Override
    public byte[] getPayloadRaw() {
        return payloadRaw;
    }

    @Override
    public void setPayloadRaw(byte[] payloadRaw) {
        this.payloadRaw = payloadRaw;
    }
}
