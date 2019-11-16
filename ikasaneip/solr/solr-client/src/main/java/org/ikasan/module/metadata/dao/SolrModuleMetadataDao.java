package org.ikasan.module.metadata.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.module.metadata.model.*;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrModuleMetadataDao extends SolrDaoBase<ModuleMetaData>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrModuleMetadataDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String MODULE_METADATA = "moduleMetaData";

    private ObjectMapper objectMapper;

    public SolrModuleMetadataDao()
    {
        this.objectMapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ModuleMetaData.class, SolrModuleMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowMetaData.class, SolrFlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, SolrFlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, SolrTransitionImpl.class);

        objectMapper.registerModule(m);
    }

    public void save(List<ModuleMetaData> moduleMetaDataList)
    {
        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for(ModuleMetaData moduleMetaData: moduleMetaDataList)
            {
                super.removeById(MODULE_METADATA, moduleMetaData.getName());

                SolrInputDocument document = getSolrInputFields(null,moduleMetaData);
                req.add(document);

                logger.debug("Adding document: " + document);
            }

            commitSolrRequest(req);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write a module metadata to Solr", e);
        }
    }

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, ModuleMetaData moduleMetaData)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, moduleMetaData.getName());
        document.addField(TYPE, MODULE_METADATA);
        try
        {
            document.addField(PAYLOAD_CONTENT, objectMapper.writeValueAsString(moduleMetaData));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("Unable to convert ["+moduleMetaData+"] to json format.");
        }

        document.addField(CREATED_DATE_TIME, System.currentTimeMillis());

        return document;
    }

    public ModuleMetaData findById(String id)
    {
        String queryString = "id:\"" + id + "\" AND type:\"" + MODULE_METADATA + "\"";

        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrModule> beans = this.findByQuery(queryString);

        if(beans.size() > 0 && beans.get(0).getModuleMetaData() != null)
        {
            return this.convert(beans.get(0).getModuleMetaData());
        }
        else
        {
            return null;
        }
    }

    public List<ModuleMetaData> findAll()
    {
        String queryString = "type:\"" + MODULE_METADATA + "\"";

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrModule> beans = this.findByQuery(queryString);

        List<ModuleMetaData> results = beans.stream().map(bean -> convert(bean.getModuleMetaData())).collect(Collectors.toList());

        return results;
    }

    /**
     * Helper method to find by query.
     *
     * @param queryString
     */
    private List<SolrModule> findByQuery(String queryString)
    {
        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            return rsp.getBeans(SolrModule.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr module meta data by query [" + queryString + "] from the ikasan solr index!", e);
        }
    }

    /**
     * Helper method to convert raw module metadata.
     *
     * @param rawModuleMetaData
     * @return
     */
    private ModuleMetaData convert(String rawModuleMetaData)
    {
        try
        {
            SolrModuleMetaDataImpl solrConfigurationMetaData
                = objectMapper.readValue(rawModuleMetaData, SolrModuleMetaDataImpl.class);

            return solrConfigurationMetaData;
        }
        catch (Exception e)
        {
            throw new RuntimeException(String.format("Unable to deserialise ModuleMetaData [%s]"
                , rawModuleMetaData), e);
        }
    }

}
