package org.ikasan.solr.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.util.SolrSpecialCharacterEscapeUtil;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralDaoImpl extends SolrDaoBase<IkasanSolrDocument> implements SolrGeneralDao<IkasanSolrDocumentSearchResults>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrGeneralDaoImpl.class);

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes) throws IOException {
        return this.searchBase(null, null, null, null, searchString, startTime, endTime, 0, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes) throws IOException {
        return this.searchBase(null, null, null, null, searchString, startTime, endTime, offset, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize) throws IOException {
        return this.searchBase(moduleName, flowNames, null, null, searchString, startTime, endTime, 0, resultSize, null);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize, List<String> entityTypes) throws IOException {
        return this.searchBase(moduleName, flowNames, null, null, searchString, startTime, endTime, 0, resultSize, entityTypes);
    }

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes) throws IOException {
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
    protected IkasanSolrDocumentSearchResults searchBase(Set<String> moduleName, Set<String> flowNames, Set<String> componentNames, String eventId, String searchString, long startTime, long endTime, int offset, int resultSize, List<String> entityTypes) throws IOException {
        SolrQuery query = new SolrQuery();
        query.setStart(offset);
        query.setRows(resultSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);

        String queryFilter = super.buildQuery(moduleName, flowNames, componentNames, new Date(startTime), new Date(endTime), searchString, eventId, entityTypes);
        query.setQuery(queryFilter);

        try
        {
            logger.info("query: " + query);

            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            List<IkasanSolrDocument> beans = rsp.getBeans(IkasanSolrDocument.class);

            return new IkasanSolrDocumentSearchResults(beans, rsp.getResults().getNumFound(), rsp.getQTime());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Caught exception perform general ikasan search!", e);
        }
    }

    @Override
    public void saveOrUpdate(IkasanSolrDocument ikasanSolrDocument)
    {
        super.save(ikasanSolrDocument);
    }

    @Override
    public void saveOrUpdate(List<IkasanSolrDocument> ikasanSolrDocuments)
    {
        super.save(ikasanSolrDocuments);
    }

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, IkasanSolrDocument ikasanSolrDocument)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, ikasanSolrDocument.getId());
        document.addField(TYPE, ikasanSolrDocument.getType());
        document.addField(MODULE_NAME, ikasanSolrDocument.getModuleName());
        document.addField(FLOW_NAME, ikasanSolrDocument.getFlowName());
        document.addField(EVENT, ikasanSolrDocument.getEventId());
        document.addField(PAYLOAD_CONTENT, ikasanSolrDocument.getEvent());
        document.addField(PAYLOAD_CONTENT_RAW, ikasanSolrDocument.getPayloadRaw());
        document.addField(CREATED_DATE_TIME, ikasanSolrDocument.getTimeStamp());
        document.setField(EXPIRY, expiry);

        return document;
    }
}
