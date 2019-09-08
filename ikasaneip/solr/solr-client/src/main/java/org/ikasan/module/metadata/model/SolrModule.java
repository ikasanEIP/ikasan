package org.ikasan.module.metadata.model;

import org.apache.solr.client.solrj.beans.Field;
import org.ikasan.spec.solr.SolrDaoBase;

public class SolrModule
{
    @Field(SolrDaoBase.ID)
    private String id;

    @Field(SolrDaoBase.PAYLOAD_CONTENT)
    private String rawConfigurationMetadata;


    public String getId()
    {
        return this.id;
    }

    public String getModuleMetaData()
    {
        return this.rawConfigurationMetadata;
    }
}
