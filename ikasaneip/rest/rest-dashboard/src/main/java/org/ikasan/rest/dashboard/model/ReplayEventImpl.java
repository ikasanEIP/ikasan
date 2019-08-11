package org.ikasan.rest.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.replay.ReplayEvent;

@JsonIgnoreProperties(value = { "harvested",  "harvestedDateTime"})
public class ReplayEventImpl implements ReplayEvent
{
    @Field("id")
    private String id;

    @Field("moduleName")
    private String moduleName;

    @Field("flowName")
    private String flowName;

    @Field("event")
    private String eventId;

    @Field("payloadRaw")
    private byte[] payloadRaw;

    @Field("payload")
    private String eventAsString;

    @Field("timestamp")
    private long timestamp;

    @Field("expiry")
    private long expiry;


    /**
     * Default constructor
     */
    public ReplayEventImpl()
    {

    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return new Long(id);
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id.toString();
    }


    /**
     * @return the event
     */
    public byte[] getEvent()
    {
        return this.payloadRaw;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(byte[] event)
    {
        this.payloadRaw = event;
    }

    /**
     * @return the moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @return the flowName
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * @param flowName the flowName to set
     */
    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * @return the eventId
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * @return the expiry
     */
    public long getExpiry()
    {
        return expiry;
    }

    /**
     * @param expiry the expiry to set
     */
    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }


    public String getEventAsString()
    {
        return eventAsString;
    }

    public void setEventAsString(String eventAsString)
    {
        this.eventAsString = eventAsString;
    }
}
