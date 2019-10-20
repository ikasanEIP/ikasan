package org.ikasan.solr.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralDaoImpl extends SolrDaoBase implements SolrGeneralDao<IkasanSolrDocumentSearchResults>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrGeneralDaoImpl.class);

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes)
    {
        return this.searchBase(null, null, null, null, searchString, startTime, endTime, 0, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes)
    {
        return this.searchBase(null, null, null, null, searchString, startTime, endTime, offset, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize)
    {
        return this.searchBase(moduleName, flowNames, null, null, searchString, startTime, endTime, 0, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes)
    {
        return this.searchBase(moduleName, flowNames, null, null, searchString, startTime, endTime, 0, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes)
    {
        return this.searchBase(moduleName, flowNames, componentNames, eventId, searchString, startTime, endTime, offset, resultSize, entityTypes);
    }

    /**
     * Utility search method.
     * 
     * @param moduleName
     * @param flowNames
     * @param searchString
     * @param startTime
     * @param endTime
     * @param offset
     * @param resultSize
     * @param entityTypes
     * @return
     */
    protected IkasanSolrDocumentSearchResults searchBase(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes)
    {
        IkasanSolrDocumentSearchResults results = null;

        List<IkasanSolrDocument> beans = null;

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(CREATED_DATE_TIME + COLON).append("[")
                .append(startTime).append(TO).append(endTime).append("]");


        logger.debug("queryString: " + queryBuffer);

        SolrQuery query = new SolrQuery();
        query.setQuery(searchString);
        query.setStart(offset);
        query.setRows(resultSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.set("defType", "dismax");
        query.setFilterQueries(queryBuffer.toString());
        query.set("qf", ID, MODULE_NAME, FLOW_NAME, COMPONENT_NAME, CREATED_DATE_TIME, EVENT, PAYLOAD_CONTENT, PAYLOAD_CONTENT_RAW,
                ERROR_URI, TYPE, RELATED_EVENT, ERROR_DETAIL, ERROR_MESSAGE, EXCEPTION_CLASS);
        

        String queryFilter = super.buildQuery(moduleName, flowNames, componentNames, null, null, null, eventId, entityTypes);

        if(queryFilter != null && !queryFilter.isEmpty())
        {
            query.setFilterQueries(queryBuffer.toString() + " AND " + queryFilter);
        }

        try
        {
            logger.debug("query: " + query);

            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            beans = rsp.getBeans(IkasanSolrDocument.class);

            results = new IkasanSolrDocumentSearchResults(beans, rsp.getResults().getNumFound(), rsp.getQTime());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Caught exception perform general ikasan search!", e);
        }

        return results;
    }
}
