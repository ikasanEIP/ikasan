package org.ikasan.rest.client;

import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.module.client.MetaDataService;
import org.ikasan.topology.metadata.JsonFlowMetaDataProvider;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MetaDataRestServiceImpl extends ModuleRestService implements MetaDataService {
    Logger logger = LoggerFactory.getLogger(MetaDataRestServiceImpl.class);

    public static final String FLOW_METADATA_URL = "/rest/metadata/flow/{moduleName}/{flowName}";
    public static final String MODULE_METADATA_URL = "/rest/metadata/module/{moduleName}";

    private JsonFlowMetaDataProvider jsonFlowMetaDataProvider;
    private JsonModuleMetaDataProvider jsonModuleMetaDataProvider;

    public MetaDataRestServiceImpl(Environment environment,
                                   HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory)
    {
        super(environment, httpComponentsClientHttpRequestFactory);
        this.jsonFlowMetaDataProvider = new JsonFlowMetaDataProvider();
        this.jsonModuleMetaDataProvider = new JsonModuleMetaDataProvider(this.jsonFlowMetaDataProvider);
    }

    @Override
    public Optional<FlowMetaData> getFlowMetadata(String contextUrl, String moduleName, String flowName)
    {

        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl+FLOW_METADATA_URL;
        Map<String, String> parameters = new HashMap(){{put("moduleName",moduleName);put("flowName",flowName);}};
        try
        {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class ,parameters);

            return Optional.of(jsonFlowMetaDataProvider.deserialiseFlow(response.getBody()));
        }
        catch(RestClientException e){
            logger.warn("Issue getting flow meta data from module [" + url
                + "]  and param ["+parameters+"] with response [{"+e.getLocalizedMessage()+"}]");
            return Optional.empty();
        }
    }

    @Override
    public Optional<ModuleMetaData> getModuleMetadata(String contextUrl, String moduleName)
    {

        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl+MODULE_METADATA_URL;
        Map<String, String> parameters = new HashMap(){{put("moduleName",moduleName);}};
        try
        {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class ,parameters);

            return Optional.of(this.jsonModuleMetaDataProvider.deserialiseModule(response.getBody()));
        }
        catch(RestClientException e){
            logger.warn("Issue getting flow meta data from module [" + url
                + "]  and param ["+parameters+"] with response [{"+e.getLocalizedMessage()+"}]");
            return Optional.empty();
        }
    }

}
