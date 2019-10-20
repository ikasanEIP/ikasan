package org.ikasan.module.metadata.service;

import org.ikasan.module.metadata.dao.SolrModuleMetadataDao;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrModuleMetadataServiceImpl extends SolrServiceBase implements BatchInsert<ModuleMetaData>, ModuleMetaDataService
{
    private SolrModuleMetadataDao dao;

    /**
     * Constructor
     *
     * @param dao
     */
    public SolrModuleMetadataServiceImpl(SolrModuleMetadataDao dao)
    {
        this.dao = dao;
        if(this.dao == null)
        {
            throw new IllegalArgumentException("Dao cannot be null!");
        }
    }

    @Override
    public void insert(List<ModuleMetaData> entities)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        dao.save(entities);
    }

    @Override
    public ModuleMetaData findById(String id)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return this.dao.findById(id);
    }

    @Override
    public List<ModuleMetaData> findAll()
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return this.dao.findAll();
    }
}
