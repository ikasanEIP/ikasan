package org.ikasan.business.stream.metadata.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.solr.SolrDaoBase;

public class SolrBusinessStream
{
    @Field(SolrDaoBase.ID)
    private String id;

    @Field(SolrDaoBase.MODULE_NAME)
    private String name;

    @Field(SolrDaoBase.PAYLOAD_CONTENT)
    private String businessStreamMetadata;


    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return name;
    }

    public String getBusinessStreamMetaData()
    {
        return this.businessStreamMetadata;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRawBusinessStreamMetadata(String businessStreamMetadata)
    {
        this.businessStreamMetadata = businessStreamMetadata;
    }
}
