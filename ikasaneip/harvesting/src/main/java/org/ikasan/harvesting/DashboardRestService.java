package org.ikasan.harvesting;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.harvest.HarvestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.Response;
import java.util.Base64;
import java.util.List;

public class DashboardRestService
{
    Logger logger = LoggerFactory.getLogger(DashboardRestService.class);

    protected static final String MODULE_NAME_PROPERTY="module.name";
    protected static final String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.base.url";
    protected static final String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.username";
    protected static final String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.password";
    protected static final String HARVESTING_ENABLED_PROPERTY="ikasan.harvesting.enabled";


    private RestTemplate restTemplate;

    private String url;
    private String moduleName;
    private String username;
    private String password;
    private boolean isEnabled;

    public DashboardRestService(Environment environment, String path)
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
                logger.warn("Issue  while publishing [" +events.size()+ "] events to dashboard [" + username + "] with response [{}]", e);
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
