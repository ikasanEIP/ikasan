package org.ikasan.spec.metadata;

import java.util.List;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class ModuleMetadataSearchResults
{
    private List<ModuleMetaData> resultList;
    private long totalNumberOfResults;
    private long queryResponseTime;

    /**
     * Constructor
     *
     * @param resultList
     * @param totalNumberOfResults
     * @param queryResponseTime
     */
    public ModuleMetadataSearchResults(List<ModuleMetaData> resultList, long totalNumberOfResults, long queryResponseTime)
    {
        this.resultList = resultList;
        this.totalNumberOfResults = totalNumberOfResults;
        this.queryResponseTime = queryResponseTime;
    }

    public List<ModuleMetaData> getResultList()
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
