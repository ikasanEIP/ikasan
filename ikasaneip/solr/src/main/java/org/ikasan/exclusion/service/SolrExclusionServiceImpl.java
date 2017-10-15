package org.ikasan.exclusion.service;

import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.solr.SolrService;

/**
 * Created by Ikasan Development Team on 23/09/2017.
 */
public class SolrExclusionServiceImpl implements SolrService<ExclusionEvent> {

    private ExclusionEventDao<String,ExclusionEvent> exclusionEventDao;

    public SolrExclusionServiceImpl(ExclusionEventDao<String,ExclusionEvent> exclusionEventDao)
    {
        this.exclusionEventDao = exclusionEventDao;
        if(this.exclusionEventDao == null)
        {
            throw new IllegalArgumentException("exclusionEventDao cannot be null!");
        }
    }
    @Override
    public void save(ExclusionEvent save)
    {
        this.exclusionEventDao.save(save);
    }

}
