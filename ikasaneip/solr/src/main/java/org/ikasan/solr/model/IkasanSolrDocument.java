package org.ikasan.solr.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.wiretap.WiretapEvent;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class IkasanSolrDocument
{
    @Field("id")
    private String id;

    @Field("payload")
    private String event;

    @Field("type")
    private String type;

    @Field("moduleName")
    private String moduleName;

    @Field("flowName")
    private String flowName;

    @Field("componentName")
    private String componentName;

    @Field("timestamp")
    private long timeStamp;

    @Field("expiry")
    private long expiry;

    @Field("event")
    private String eventId;

    public long getIdentifier()
    {
        return new Long(id);
    }

    public String getModuleName()
    {
        return this.moduleName;
    }

    public String getFlowName()
    {
        return this.flowName;
    }

    public String getComponentName()
    {
        return this.componentName;
    }

    public long getTimestamp()
    {
        return this.timeStamp;
    }

    public String getEvent()
    {
        return event;
    }

    public long getExpiry()
    {
        return this.getExpiry();
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    @Override
    public String toString()
    {
        return "SolrWiretapEvent{" +
                "id='" + id + '\'' +
                ", event='" + event + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", flowName='" + flowName + '\'' +
                ", componentName='" + componentName + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
