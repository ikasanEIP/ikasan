package org.ikasan.wiretap.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.wiretap.WiretapEvent;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrWiretapEvent implements WiretapEvent<String>
{
    @Field("id")
    private String id;

    @Field("payload")
    private String event;

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


    @Override
    public long getIdentifier()
    {
        return new Long(id);
    }

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public String getFlowName()
    {
        return this.flowName;
    }

    @Override
    public String getComponentName()
    {
        return this.componentName;
    }

    @Override
    public long getTimestamp()
    {
        return this.timeStamp;
    }

    @Override
    public String getEvent()
    {
        return event;
    }

    @Override
    public long getExpiry()
    {
        return this.getExpiry();
    }

    @Override
    public String getEventId()
    {
        return this.eventId;
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
