package org.ikasan.business.stream.metadata.service;

import org.ikasan.business.stream.metadata.dao.SolrBusinessStreamMetadataDao;
import org.ikasan.business.stream.metadata.model.SolrBusinessStream;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrBusinessStreamMetaDataServiceImpl extends SolrServiceBase implements BusinessStreamMetaDataService<BusinessStreamMetaData>
{
    private SolrBusinessStreamMetadataDao dao;

    public SolrBusinessStreamMetaDataServiceImpl(SolrBusinessStreamMetadataDao dao)
    {
        this.dao = dao;
    }

    @Override
    public BusinessStreamMetaData findById(String id)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return dao.findById(id);
    }

    @Override
    public List<BusinessStreamMetaData> findAll()
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        return this.dao.findAll();
    }

    @Override
    public void save(BusinessStreamMetaData metaData)
    {
        SolrBusinessStream solrBusinessStream = new SolrBusinessStream();
        solrBusinessStream.setId(metaData.getId());
        solrBusinessStream.setName(metaData.getName());
        solrBusinessStream.setRawBusinessStreamMetadata(metaData.getJson());

        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        this.dao.save(solrBusinessStream);
    }

    @Override
    public void delete(String id)
    {
        dao.setSolrUsername(super.solrUsername);
        dao.setSolrPassword(super.solrPassword);
        this.dao.delete(id);
    }
}
