package org.ikasan.business.stream.metadata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.business.stream.metadata.model.BusinessStreamMetaDataImpl;
import org.ikasan.spec.metadata.BusinessStreamMetadataSearchResults;
import org.ikasan.business.stream.metadata.model.SolrBusinessStream;
import org.ikasan.module.metadata.model.SolrFlowElementMetaDataImpl;
import org.ikasan.module.metadata.model.SolrFlowMetaDataImpl;
import org.ikasan.module.metadata.model.SolrModuleMetaDataImpl;
import org.ikasan.module.metadata.model.SolrTransitionImpl;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrBusinessStreamMetadataDao extends SolrDaoBase<SolrBusinessStream>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrBusinessStreamMetadataDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String BUSINESS_STREAM_METADATA = "businessStreamMetaData";

    private ObjectMapper objectMapper;

    public SolrBusinessStreamMetadataDao()
    {
        this.objectMapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

        objectMapper.registerModule(m);
    }

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, SolrBusinessStream businessStreamMetaData)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "businessStream-"+businessStreamMetaData.getId());
        document.addField(TYPE, BUSINESS_STREAM_METADATA);
        document.addField(MODULE_NAME, businessStreamMetaData.getName());
        document.addField(FLOW_NAME, businessStreamMetaData.getDescription());
        document.addField(PAYLOAD_CONTENT, businessStreamMetaData.getBusinessStreamMetaData());
        document.addField(CREATED_DATE_TIME, System.currentTimeMillis());

        return document;
    }

    public BusinessStreamMetaData findById(String id)
    {
        String queryString = "id:\"" + id + "\" AND type:\"" + BUSINESS_STREAM_METADATA + "\"";

        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrBusinessStream> beans = this.findByQuery(query);

        if(beans.size() > 0 && beans.get(0) != null)
        {
            return convert(beans.get(0));
        }
        else
        {
            return null;
        }
    }

    public BusinessStreamMetadataSearchResults find(List<String> businessStreamNames, Integer startOffset, Integer resultSize)
    {
        String queryString = "type:\"" + BUSINESS_STREAM_METADATA + "\"";

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(startOffset);
        query.setRows(resultSize);

        StringBuffer businessStreamNamesBuffer = new StringBuffer();

        if(businessStreamNames != null && businessStreamNames.size() > 0)
        {
            businessStreamNamesBuffer.append(this.buildPredicate(MODULE_NAME, businessStreamNames));
        }

        query.setFilterQueries(businessStreamNamesBuffer.toString());

        BusinessStreamMetadataSearchResults results;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            List<SolrBusinessStream> beans = rsp.getBeans(SolrBusinessStream.class);

            results = new BusinessStreamMetadataSearchResults(beans.stream()
                .map(solrBusinessStream -> convert(solrBusinessStream))
                .collect(Collectors.toList()), rsp.getResults().getNumFound(), rsp.getQTime());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr business stream meta data by query [" + query + "] from the ikasan solr index!", e);
        }

       return results;
    }

    public List<BusinessStreamMetaData> findAll(Integer startOffset, Integer resultSize)
    {
        String queryString = "type:\"" + BUSINESS_STREAM_METADATA + "\"";

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(startOffset);
        query.setRows(resultSize);

        List<SolrBusinessStream> beans = this.findByQuery(query);

        return beans.stream()
                    .map(solrBusinessStream -> convert(solrBusinessStream))
                    .collect(Collectors.toList());
    }

    public void delete(String id)
    {
        this.removeById(BUSINESS_STREAM_METADATA, id);
    }

    /**
     * Helper method to find by query.
     *
     * @param query
     */
    private List<SolrBusinessStream> findByQuery(SolrQuery query)
    {
        logger.debug("queryString: " + query);

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            return rsp.getBeans(SolrBusinessStream.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr module meta data by query [" + query + "] from the ikasan solr index!", e);
        }
    }

    /**
     * Helper method to convert raw business stream metadata.
     *
     * @param businessStreamDocument
     * @return
     */
    private BusinessStreamMetaData convert(SolrBusinessStream businessStreamDocument)
    {
        BusinessStreamMetaData businessStreamMetaData = new BusinessStreamMetaDataImpl();
        businessStreamMetaData.setId(businessStreamDocument.getId());
        businessStreamMetaData.setName(businessStreamDocument.getName());
        businessStreamMetaData.setDescription(businessStreamDocument.getDescription());
        businessStreamMetaData.setJson(businessStreamDocument.getBusinessStreamMetaData());

        return businessStreamMetaData;
    }

}
