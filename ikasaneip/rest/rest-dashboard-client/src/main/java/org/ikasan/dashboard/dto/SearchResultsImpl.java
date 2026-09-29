package org.ikasan.dashboard.dto;

import org.ikasan.spec.search.SearchResults;

import java.util.List;

/**
 * Implementation of SearchResults for REST client deserialization.
 */
public class SearchResultsImpl<RESULT> implements SearchResults<RESULT> {

    private List<RESULT> resultList;
    private long totalNumberOfResults;
    private long queryResponseTime;

    /**
     * Default constructor
     */
    public SearchResultsImpl() {
    }

    /**
     * Constructor with all fields
     *
     * @param resultList the list of documents
     * @param totalNumberOfResults the total number of results
     * @param queryResponseTime the query response time in milliseconds
     */
    public SearchResultsImpl(List<RESULT> resultList, long totalNumberOfResults, long queryResponseTime) {
        this.resultList = resultList;
        this.totalNumberOfResults = totalNumberOfResults;
        this.queryResponseTime = queryResponseTime;
    }

    @Override
    public List<RESULT> getResultList() {
        return resultList;
    }

    public void setResultList(List<RESULT> resultList) {
        this.resultList = resultList;
    }

    @Override
    public long getTotalNumberOfResults() {
        return totalNumberOfResults;
    }

    public void setTotalNumberOfResults(long totalNumberOfResults) {
        this.totalNumberOfResults = totalNumberOfResults;
    }

    @Override
    public long getQueryResponseTime() {
        return queryResponseTime;
    }

    public void setQueryResponseTime(long queryResponseTime) {
        this.queryResponseTime = queryResponseTime;
    }
}
