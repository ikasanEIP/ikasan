package org.ikasan.systemevent.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.systemevent.SystemEvent;

import java.util.Date;
import java.util.StringJoiner;

/**
 * Created by Ikasan Development Team.
 */
public class SolrSystemEvent implements SystemEvent
{

    @Field("id")
    private String id;

    @Field("moduleName")
    private String moduleName;

    @Field("actor")
    private String actor;

    @Field("action")
    private String action;

    @Field("subject")
    private String subject;

    @Field("timestamp")
    private long timestampLong;

    @Field("expiry")
    private long expiryLong;



    /**
     * Used by solr to create results.
     */
    public SolrSystemEvent()
    {

    }

    /**
     * Constructor
     *
     * @param moduleName
     * @param eventTimestamp
     */
    public SolrSystemEvent(Long id, final String moduleName, final String actor,
                           final String action,final String subject, final long eventTimestamp)
    {
        this.id = id.toString();
        this.moduleName = moduleName;
        this.actor = actor;
        this.action = action;
        this.subject = subject;
        this.timestampLong = eventTimestamp;
    }


    public String getModuleName()
    {
        return this.moduleName;
    }


    @Override
    public String getAction()
    {
        return action;
    }

    @Override
    public String getActor()
    {
        return actor;
    }

    @Override
    public Long getId()
    {
        return new Long(id);
    }

    @Override
    public String getSubject()
    {
        return subject;
    }

    public long getTimestampLong()
    {
        return this.timestampLong;
    }


    public long getExpiryLong()
    {
        return expiryLong;
    }

    @Override
    public Date getTimestamp(){
        return new Date(this.getTimestampLong());
    }

    @Override
    public Date getExpiry(){
        return new Date(this.getExpiryLong());
    }


    public void setId(String id)
    {
        this.id = id;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setActor(String actor)
    {
        this.actor = actor;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void setTimestampLong(long timestamp)
    {
        this.timestampLong = timestamp;
    }

    public void setExpiryLong(long expiry)
    {
        this.expiryLong = expiry;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", SolrSystemEvent.class.getSimpleName() + "[", "]").add("id='" + id + "'").add(
            "moduleName='" + moduleName + "'").add("actor='" + actor + "'").add("action='" + action + "'")
                                                                                       .add("subject='" + subject + "'")
                                                                                       .add("timestamp=" + timestampLong)
                                                                                       .add("expiry=" + expiryLong)
                                                                                       .toString();
    }
}
