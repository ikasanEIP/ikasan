package org.ikasan.solr.model;

import java.util.List;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class IkasanSolrDocumentSearchResults
{
    private List<IkasanSolrDocument> resultList;
    private long totalNumberOfResults;
    private long queryResponseTime;

    /**
     * Constructor
     * 
     * @param resultList
     * @param totalNumberOfResults
     * @param queryResponseTime
     */
    public IkasanSolrDocumentSearchResults(List<IkasanSolrDocument> resultList, long totalNumberOfResults, long queryResponseTime)
    {
        this.resultList = resultList;
        this.totalNumberOfResults = totalNumberOfResults;
        this.queryResponseTime = queryResponseTime;
    }

    public List<IkasanSolrDocument> getResultList()
    {
        return resultList;
    }

    public long getTotalNumberOfResults()
    {
        return totalNumberOfResults;
    }

    public long getQueryResponseTime()
    {
        return queryResponseTime;
    }
}
