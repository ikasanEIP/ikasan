package org.ikasan.module.metadata.service;

import org.ikasan.module.metadata.dao.SolrModuleMetadataDao;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrModuleMetadataServiceImpl extends SolrServiceBase implements BatchInsert<ModuleMetaData>
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
        dao.save(entities);
    }
}
