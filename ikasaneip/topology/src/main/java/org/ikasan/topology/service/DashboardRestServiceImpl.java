package org.ikasan.topology.service;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

public class DashboardRestServiceImpl implements DashboardRestService
{
    Logger logger = LoggerFactory.getLogger(DashboardRestServiceImpl.class);

    private ModuleMetaDataProvider<String> moduleMetaDataProvider;

    private RestTemplate restTemplate;

    private String url;
    private String moduleName;
    private String username;
    private String password;
    private boolean isEnabled;

    public DashboardRestServiceImpl(Environment environment, String path, ModuleMetaDataProvider<String> moduleMetaDataProvider)
    {
        this(environment,path);
        this.moduleMetaDataProvider = moduleMetaDataProvider;
    }
    public DashboardRestServiceImpl(Environment environment, String path)
    {
        restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        isEnabled = Boolean.valueOf(environment.getProperty(HARVESTING_ENABLED_PROPERTY,"false"));


        if(isEnabled)
        {
            this.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + path;
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);
            this.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
            this.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        }
    }

    public boolean publish(List<HarvestEvent> events)
    {
        if(isEnabled && events!=null)
        {
            logger.debug("Pushing [{}] events to dashboard [{}]", events.size(), url);

            HttpHeaders headers = createHttpHeaders();
            HttpEntity<List<HarvestEvent>> entity = new HttpEntity<>(events, headers);

            try
            {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                logger.debug("Successfully published [{}] events to dashboard [{}] with response [{}]", events.size(), url, response);
                return true;
            }
            catch (RestClientException e)
            {
                logger.warn("Issue  while publishing [" +events.size()+ "] events to dashboard [" + url + "] with response [{}]", e);
                return false;
            }
        }
        return false;
    }

    public boolean publish(Module<Flow> module)
    {
        if(isEnabled && module!=null)
        {
            logger.debug("Pushing [{}] module to dashboard [{}]", module.getName(), url);

            HttpHeaders headers = createHttpHeaders();

            HttpEntity<String> entity = new HttpEntity<>(moduleMetaDataProvider.describeModule(module), headers);

            try
            {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
                logger.debug("Successfully published [{}] module to dashboard [{}] with response [{}]", module.getName(), url, response);
                return true;
            }
            catch (RestClientException e)
            {
                logger.warn("Issue  while publishing [" +module.getName()+ "] module to dashboard [" + url + "] with response [{}]", e);
                return false;
            }
        }
        return false;
    }


    private HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.USER_AGENT,moduleName);
        if(username!=null && password!=null)
        {
            String notEncoded = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
            headers.add("Authorization", "Basic " + encodedAuth);
        }
        return headers;
    }
}
