package org.ikasan.rest.client;

import org.ikasan.rest.client.dto.ChangeFlowStartupModeDto;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationRestServiceImpl extends ModuleRestService
{
    Logger logger = LoggerFactory.getLogger(ConfigurationRestServiceImpl.class);

    protected final static String FLOW_CONFIGURATION_URL = "/rest/configuration/{moduleName}/{flowName}/flow";
    protected final static String FLOW_COMPONENTS_CONFIGURATION_URL = "/rest/configuration/{moduleName}/{flowName}/components";
    protected final static String COMPONENTS_CONFIGURATION_URL = "/rest/configuration/components";
    protected final static String FLOW_INVOKERS_CONFIGURATION_URL = "/rest/configuration/{moduleName}/{flowName}/invokers";
    protected final static String INVOKERS_CONFIGURATION_URL = "/rest/configuration/invokers";
    protected final static String PUT_CONFIGURATION_URL = "/rest/configuration";

    ConfigurationMetaDataProvider<String> configurationMetaDataProvider;

    public ConfigurationRestServiceImpl(Environment environment,
                                        ConfigurationMetaDataProvider<String> configurationMetaDataProvider)
    {
        super(environment);
        this.configurationMetaDataProvider = configurationMetaDataProvider;
    }

    public List<ConfigurationMetaData> getComponents(String contextUrl)
    {

        String url = contextUrl + COMPONENTS_CONFIGURATION_URL;
        return getConfigurations(url,null,null);
    }

    public List<ConfigurationMetaData> getFlowComponents(String contextUrl, String moduleName, String flowName)
    {

        String url = contextUrl + FLOW_COMPONENTS_CONFIGURATION_URL;
        return getConfigurations(url,moduleName,flowName);
    }


    public List<ConfigurationMetaData> getInvokers(String contextUrl)
    {

        String url = contextUrl + INVOKERS_CONFIGURATION_URL;
        return getConfigurations(url,null,null);
    }

    public List<ConfigurationMetaData> getFlowInvokers(String contextUrl, String moduleName, String flowName)
    {

        String url = contextUrl + FLOW_INVOKERS_CONFIGURATION_URL;
        return getConfigurations(url,moduleName,flowName);
    }

    public List<ConfigurationMetaData> getFlowConfigurations(String contextUrl, String moduleName, String flowName)
    {

        String url = contextUrl + FLOW_CONFIGURATION_URL;
        return getConfigurations(url,moduleName,flowName);
    }

    public boolean storeConfiguration(String contextUrl, ConfigurationMetaData configuration)
    {
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(configuration, headers);
        String url = contextUrl + PUT_CONFIGURATION_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Issue updating configuration [" + url + "] with module [" + configuration + "]");
            return false;
        }
    }

    private List<ConfigurationMetaData> getConfigurations(String url,String moduleName, String flowName)
    {

        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response;
            if(moduleName != null && flowName != null )
            {
                Map<String, String> parameters = new HashMap<String, String>()
                {{put("moduleName", moduleName);put("flowName", flowName);}};

                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class,parameters);
            }else{
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            }
            List<ConfigurationMetaData> data = configurationMetaDataProvider.deserialiseMetadataConfigurations(response.getBody());
            return data;
        }
        catch (RestClientException e)
        {
            logger.warn("Issue getting configuration for url [" + url + "]  with response [{" + e
                .getLocalizedMessage() + "}]");
            return Collections.emptyList();
        }
    }

}
