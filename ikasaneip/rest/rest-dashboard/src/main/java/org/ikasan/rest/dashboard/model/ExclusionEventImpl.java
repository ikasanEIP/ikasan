package org.ikasan.rest.dashboard.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.exclusion.ExclusionEvent;

public class ExclusionEventImpl implements ExclusionEvent
{
    /** surrogate id assigned from ORM */
    @Field("id")
    private String id;

    /** module name */
    @Field("moduleName")
    String moduleName;

    /** flowName */
    @Field("flowName")
    String flowName;

    /** identifier for this event */
    @Field("event")
    String identifier;

    /** original form of the event being excluded */
    @Field("payload")
    String event;

    /** timestamp indicating when this event was created */
    @Field("timestamp")
    long timestamp;

    /** error uri reported as part of this excluded event */
    @Field("id")
    String errorUri;

    /** flag to indicate if the record has been harvested */
    boolean harvested;

    /**
     * Constructor
     */
    public ExclusionEventImpl(){}

    public long getId() {
        return new Long(id);
    }

    public void setId(long id) {
        this.id = new Long(id).toString();
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public byte[] getEvent()
    {
        if(event != null)
        {
            return event.getBytes();
        }
        return "".getBytes();
    }

    public void setEvent(byte[] event) {
        this.event = new String(event);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorUri() {
        return errorUri;
    }

    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    public boolean isHarvested()
    {
        return harvested;
    }

    public void setHarvested(boolean harvested)
    {
        this.harvested = harvested;
    }

    @Override
    public String toString() {
        return "ExclusionEvent{" +
            "id='" + id + '\'' +
            ", moduleName='" + moduleName + '\'' +
            ", flowName='" + flowName + '\'' +
            ", identifier='" + identifier + '\'' +
            ", event=" + event +
            ", timestamp=" + timestamp +
            ", errorUri='" + errorUri + '\'' +
            '}';
    }
}
