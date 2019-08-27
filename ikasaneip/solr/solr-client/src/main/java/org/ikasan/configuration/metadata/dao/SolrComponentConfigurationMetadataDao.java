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
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

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
        String queryString = "id:\"" + id + "\"";

        logger.info("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        List<SolrComponentConfiguration> beans = null;

        try
        {
            QueryRequest req = new QueryRequest(query);
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            QueryResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            beans = rsp.getBeans(SolrComponentConfiguration.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error resolving solr component configuration by id [" + id + "] from the ikasan solr index!", e);
        }

        if(beans.size() > 0 && beans.get(0).getRawConfigurationMetadata() != null)
        {
            try
            {
                SolrConfigurationMetaData solrConfigurationMetaData
                    = objectMapper.readValue(beans.get(0).getRawConfigurationMetadata(), SolrConfigurationMetaData.class);

                return solrConfigurationMetaData;
            }
            catch (Exception e)
            {
                throw new RuntimeException(String.format("Unable to deserialise ConfigurationMetaData [%s]"
                    , beans.get(0).getRawConfigurationMetadata()), e);
            }
        }
        else
        {
            return null;
        }
    }

}
