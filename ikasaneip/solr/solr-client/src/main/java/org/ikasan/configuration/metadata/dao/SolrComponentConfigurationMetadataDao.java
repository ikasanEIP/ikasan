package org.ikasan.configuration.metadata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.configuration.metadata.model.SolrComponentConfiguration;
import org.ikasan.configuration.metadata.model.SolrConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrComponentConfigurationMetadataDao extends SolrDaoBase
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrComponentConfigurationMetadataDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String COMPONENT_CONFIGURATION = "componentConfiguration";

    private ObjectMapper objectMapper;

    public SolrComponentConfigurationMetadataDao()
    {
        this.objectMapper = new ObjectMapper();
    }

    public void save(List<ConfigurationMetaData> configurationMetaDataList)
    {
        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for(ConfigurationMetaData configurationMetaData: configurationMetaDataList)
            {
                super.removeById(COMPONENT_CONFIGURATION, configurationMetaData.getConfigurationId());

                SolrInputDocument document = new SolrInputDocument();
                document.addField(ID, configurationMetaData.getConfigurationId());
                document.addField(TYPE, COMPONENT_CONFIGURATION);
                document.addField(PAYLOAD_CONTENT, objectMapper.writeValueAsString(configurationMetaData));
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
            throw new RuntimeException("An exception has occurred attempting to write a component configuration to Solr", e);
        }
    }

    public ConfigurationMetaData findById(String id)
    {
        String queryString = "id:\"" + id + "\" AND type: \"" + COMPONENT_CONFIGURATION + "\"";

        logger.debug("queryString: " + queryString);

        List<SolrComponentConfiguration> beans = this.findByQuery(queryString);

        if(beans.size() > 0 && beans.get(0).getRawConfigurationMetadata() != null)
        {
            return this.convert(beans.get(0).getRawConfigurationMetadata());
        }
        else
        {
            return null;
        }
    }

    public List<ConfigurationMetaData> findAll()
    {
        String queryString = "type: \"" + COMPONENT_CONFIGURATION + "\"";

        logger.debug("queryString: " + queryString);

        List<SolrComponentConfiguration> beans = this.findByQuery(queryString);

        return beans.stream().map(bean -> convert(bean.getRawConfigurationMetadata())).collect(Collectors.toList());
    }

    public List<ConfigurationMetaData> findInIdList(List<String> configurationIds)
    {
        StringBuffer queryString = new StringBuffer("type: \"").append(COMPONENT_CONFIGURATION).append("\"");
        queryString.append(" AND id:(");

        configurationIds.forEach(id -> {
            queryString.append("\"").append(id).append("\",");
        });

        queryString.append(")");

        logger.debug("queryString: " + queryString);

        List<SolrComponentConfiguration> beans = this.findByQuery(queryString.toString());

        return beans.stream().map(bean -> convert(bean.getRawConfigurationMetadata())).collect(Collectors.toList());
    }

    private SolrConfigurationMetaData convert(String solrComponentConfiguration)
    {
        try
        {
            SolrConfigurationMetaData solrConfigurationMetaData
                = objectMapper.readValue(solrComponentConfiguration, SolrConfigurationMetaData.class);

            return solrConfigurationMetaData;
        }
        catch (Exception e)
        {
            throw new RuntimeException(String.format("Unable to deserialise ConfigurationMetaData [%s]"
                , solrComponentConfiguration), e);
        }
    }

    /**
     * Helper method to perform query.
     *
     * @param queryString
     * @return
     */
    private List<SolrComponentConfiguration> findByQuery(String queryString)
    {
        logger.debug("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrComponentConfiguration> beans = null;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            return rsp.getBeans(SolrComponentConfiguration.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr component configuration by query [" + queryString + "] from the ikasan solr index!", e);
        }
    }
}
