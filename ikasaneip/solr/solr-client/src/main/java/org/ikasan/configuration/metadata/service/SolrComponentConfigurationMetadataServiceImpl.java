package org.ikasan.configuration.metadata.service;

import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrComponentConfigurationMetadataServiceImpl extends SolrServiceBase implements BatchInsert<ConfigurationMetaData>
{
    private SolrComponentConfigurationMetadataDao dao;

    /**
     * Constructor
     *
     * @param dao
     */
    public SolrComponentConfigurationMetadataServiceImpl(SolrComponentConfigurationMetadataDao dao)
    {
        this.dao = dao;
        if(this.dao == null)
        {
            throw new IllegalArgumentException("Dao cannot be null!");
        }
    }

    @Override
    public void insert(List<ConfigurationMetaData> entities)
    {
        dao.save(entities);
    }
}
