package org.ikasan.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextParametersRestServiceImpl extends AbstractRestServiceImpl {

    public static final String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.base.url";
    public static final String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.rest.username";
    public static final String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.rest.password";
    public static final String DASHBOARD_REST_USERAGENT ="ikasan.dashboard.rest.useragent";

    private String userAgent;

    private ObjectMapper mapper;

    public ContextParametersRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory,
                                            String path)
    {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + path;
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        this.userAgent = environment.getProperty(DASHBOARD_REST_USERAGENT);

        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Map<String, List<ContextParameterInstance>> getAll(){
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return this.mapper.readValue(response.getBody(), Map.class);
        }
        catch (RestClientException | JsonProcessingException e)
        {
            logger.warn("Issue getting context parameters for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]");
            return Collections.emptyMap();
        }
    }

    public Map<String, List<ContextParameterInstance>> getByContextName(String contextName){
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);
        try
        {
            Map<String, String> parameters = new HashMap()
            {{
                put("contextName", contextName);
            }};

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, parameters);

            return this.mapper.readValue(response.getBody(), Map.class);
        }
        catch (RestClientException | JsonProcessingException e)
        {
            logger.warn("Issue getting context parameters for url [" + url + "]  with response [{" + e.getLocalizedMessage() + "}]");
            return Collections.emptyMap();
        }
    }

}
