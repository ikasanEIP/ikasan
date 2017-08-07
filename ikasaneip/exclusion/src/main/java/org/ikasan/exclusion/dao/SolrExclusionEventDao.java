package org.ikasan.exclusion.dao;

import org.apache.solr.common.SolrInputDocument;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.solr.dao.SolrDaoBase;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class SolrExclusionEventDao extends SolrDaoBase implements ExclusionEventDao<String, ExclusionEvent>
{
    @Override
    public void save(ExclusionEvent exclusionEvent)
    {

    }

    @Override
    public void delete(String moduleName, String flowName, String s)
    {

    }

    @Override
    public void delete(String errorUri)
    {

    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String s)
    {
        return null;
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String identifier)
    {
        return null;
    }

    @Override
    public List<ExclusionEvent> findAll()
    {
        return null;
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date starteDate, Date endDate, String s, int size)
    {
        return null;
    }

    @Override
    public ExclusionEvent find(String errorUri)
    {
        return null;
    }
}
