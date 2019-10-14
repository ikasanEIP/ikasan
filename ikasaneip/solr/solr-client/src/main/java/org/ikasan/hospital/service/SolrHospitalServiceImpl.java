package org.ikasan.hospital.service;

import org.ikasan.hospital.dao.SolrHospitalDao;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.solr.SolrServiceBase;

import java.util.List;

public class SolrHospitalServiceImpl extends SolrServiceBase implements HospitalAuditService
{
    SolrHospitalDao solrHospitalDao;

    public SolrHospitalServiceImpl(SolrHospitalDao solrHospitalDao)
    {
        this.solrHospitalDao = solrHospitalDao;
        if(this.solrHospitalDao == null)
        {
            throw new IllegalArgumentException("SolrHospitalDao cannot be null!");
        }
    }

    @Override
    public void save(ExclusionEventAction exclusionEventAction)
    {
        solrHospitalDao.setSolrUsername(this.solrUsername);
        solrHospitalDao.setSolrPassword(this.solrPassword);
        solrHospitalDao.save(exclusionEventAction);
    }

    @Override
    public void save(List<ExclusionEventAction> exclusionEventActions)
    {
        solrHospitalDao.setSolrUsername(this.solrUsername);
        solrHospitalDao.setSolrPassword(this.solrPassword);
        solrHospitalDao.save(exclusionEventActions);
    }
}
