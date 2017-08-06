package org.ikasan.solr.dao;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralSearchDaoImpl extends SolrDaoBase implements SolrGeneralSearchDao<IkasanSolrDocumentSearchResults>
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrGeneralSearchDaoImpl.class);

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int resultSize)
    {
        IkasanSolrDocumentSearchResults results = null;

        List<IkasanSolrDocument> beans = null;

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(CREATED_DATE_TIME + COLON).append("[")
                .append(startTime).append(TO).append(endTime).append("]");


        logger.info("queryString: " + queryBuffer);

        SolrQuery query = new SolrQuery();
        query.setQuery(searchString);
        query.setStart(0);
        query.setRows(resultSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.set("defType", "dismax");
        query.setFilterQueries(queryBuffer.toString());

        try
        {
            QueryResponse rsp = this.solrClient.query( query );

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
