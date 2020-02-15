package org.ikasan.spec.metadata;

import org.ikasan.spec.metadata.BusinessStreamMetaData;

import java.util.List;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class BusinessStreamMetadataSearchResults
{
    private List<BusinessStreamMetaData> resultList;
    private long totalNumberOfResults;
    private long queryResponseTime;

    /**
     * Constructor
     *
     * @param resultList
     * @param totalNumberOfResults
     * @param queryResponseTime
     */
    public BusinessStreamMetadataSearchResults(List<BusinessStreamMetaData> resultList, long totalNumberOfResults, long queryResponseTime)
    {
        this.resultList = resultList;
        this.totalNumberOfResults = totalNumberOfResults;
        this.queryResponseTime = queryResponseTime;
    }

    public List<BusinessStreamMetaData> getResultList()
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
