package org.ikasan.module.metadata.dao;

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

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrModuleMetadataDao extends SolrDaoBase
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
                super.removeById(MODULE_METADATA, moduleMetaData.getName() + "_" + moduleMetaData.getVersion());

                SolrInputDocument document = new SolrInputDocument();
                document.addField(ID, moduleMetaData.getName() + "_" + moduleMetaData.getVersion());
                document.addField(TYPE, MODULE_METADATA);
                document.addField(PAYLOAD_CONTENT, objectMapper.writeValueAsString(moduleMetaData));
                document.addField(CREATED_DATE_TIME, System.currentTimeMillis());

                req.add(document);

                logger.debug("Adding document: " + document);
            }

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Solr Response: " + rsp.toString());

            req.commit(solrClient, SolrConstants.CORE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write a module metadata to Solr", e);
        }
    }

    public ModuleMetaData findById(String id)
    {
        String queryString = "id:\"" + id + "\"";

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrModule> beans;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            beans = rsp.getBeans(SolrModule.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr module meta data by id [" + id + "] from the ikasan solr index!", e);
        }

        if(beans.size() > 0 && beans.get(0).getRawConfigurationMetadata() != null)
        {
            try
            {
                SolrModuleMetaDataImpl solrConfigurationMetaData
                    = objectMapper.readValue(beans.get(0).getRawConfigurationMetadata(), SolrModuleMetaDataImpl.class);

                return solrConfigurationMetaData;
            }
            catch (Exception e)
            {
                throw new RuntimeException(String.format("Unable to deserialise ModuleMetaData [%s]"
                    , beans.get(0).getRawConfigurationMetadata()), e);
            }
        }
        else
        {
            return null;
        }
    }

}
