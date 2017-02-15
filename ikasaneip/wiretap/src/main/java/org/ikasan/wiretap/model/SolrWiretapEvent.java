package org.ikasan.wiretap.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.wiretap.WiretapEvent;

/**
 * Created by stewmi on 14/02/2017.
 */
public class SolrWiretapEvent
{
    @Field("id")
    private String id;

    @Field("PayloadContent")
    private String event;


    public String getIdentifier()
    {
        return id;
    }


    public String getEvent()
    {
        return this.event;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }
}
