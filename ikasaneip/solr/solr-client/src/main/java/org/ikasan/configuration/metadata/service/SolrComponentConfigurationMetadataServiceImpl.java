package org.ikasan.configuration.metadata.service;

import org.ikasan.configuration.metadata.dao.SolrComponentConfigurationMetadataDao;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrComponentConfigurationMetadataServiceImpl extends SolrServiceBase implements BatchInsert<ConfigurationMetaData>, ConfigurationMetaDataService
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
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        dao.save(entities);
    }

    @Override
    public ConfigurationMetaData findById(String id)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return dao.findById(id);
    }

    @Override
    public List<ConfigurationMetaData> findAll()
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return dao.findAll();
    }

    @Override
    public List<ConfigurationMetaData> findByIdList(List<String> configurationIds)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return dao.findInIdList(configurationIds);
    }
}
