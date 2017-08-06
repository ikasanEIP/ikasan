package org.ikasan.error.reporting.dao;

import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.solr.dao.SolrDaoBase;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrErrorReportingServiceDao extends SolrDaoBase implements ErrorReportingServiceDao<ErrorOccurrence<byte[]>, String>
{
    @Override
    public ErrorOccurrence<byte[]> find(String uri)
    {
        return null;
    }

    @Override
    public Map<String, ErrorOccurrence<byte[]>> find(List<String> uris)
    {
        return null;
    }

    @Override
    public List<ErrorOccurrence<byte[]>> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, int size)
    {
        return null;
    }

    @Override
    public List<ErrorOccurrence<byte[]>> find(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate, String action, String exceptionClass, int size)
    {
        return null;
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, List<String> flowElementname, Date startDate, Date endDate)
    {
        return null;
    }

    @Override
    public void save(ErrorOccurrence<byte[]> errorOccurrence)
    {

    }

    @Override
    public void deleteExpired()
    {

    }
}
