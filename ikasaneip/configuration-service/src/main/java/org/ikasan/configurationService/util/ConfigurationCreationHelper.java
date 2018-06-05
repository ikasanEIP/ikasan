package org.ikasan.configurationService.util;

import org.apache.commons.codec.binary.Base64;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by Ikasan Development Team on 20/12/2016.
 */
public class ConfigurationCreationHelper
{
    private static Logger logger = LoggerFactory.getLogger(ConfigurationCreationHelper.class);

    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
    private PlatformConfigurationService platformConfigurationService;

    private RestTemplate restTemplate;

    public ConfigurationCreationHelper(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
                                       PlatformConfigurationService platformConfigurationService)
    {
        this.configurationManagement = configurationManagement;
        if(this.configurationManagement == null)
        {
            throw new IllegalArgumentException("configurationManagement cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        restTemplate = new RestTemplate();

        restTemplate.setMessageConverters(
                Arrays.asList(
                        new ByteArrayHttpMessageConverter()
                        ,new StringHttpMessageConverter()));
    }

    public Configuration createConfiguration(Component component)
    {
        Server server = component.getFlow().getModule().getServer();

        String url = server.getUrl() + ":" + server.getPort()
                + component.getFlow().getModule().getContextRoot()
                + "/rest/configuration/createConfiguration/"
                + component.getFlow().getModule().getName().replace(" ", "%20")
                + "/"
                + component.getFlow().getName().replace(" ", "%20")
                + "/"
                + component.getName().replace(" ", "%20");

        String username = platformConfigurationService.getWebServiceUsername();
        String password = platformConfigurationService.getWebServicePassword();



        ResponseEntity<String> response = null;
        try {
            HttpEntity request = initRequest(component.getFlow().getModule().getContextRoot(),username,password );
            response = restTemplate.exchange(new URI(url), HttpMethod.GET,request,String.class);


            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("An error was received trying to create configured resource '" + component.getConfigurationId() + "': "
                        + response.getBody());
            }
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(),e);
        }

        return this.configurationManagement.getConfiguration(component.getConfigurationId());
    }

    private HttpEntity initRequest(String module ,String user, String password){
        HttpHeaders headers = new HttpHeaders();
        if(user!=null && password !=null){
            String credentials = user + ":" +password;
            String encodedCridentials =  new String(Base64.encodeBase64(credentials.getBytes()));
            headers.set(HttpHeaders.AUTHORIZATION, "Basic "+encodedCridentials);
        }
        headers.set(HttpHeaders.USER_AGENT,module);
        return new HttpEntity(headers);

    }
}
