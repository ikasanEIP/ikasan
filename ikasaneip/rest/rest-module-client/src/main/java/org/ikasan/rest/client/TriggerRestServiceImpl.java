package org.ikasan.rest.client;

import org.ikasan.rest.client.dto.TriggerDto;
import org.ikasan.spec.module.client.TriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

public class TriggerRestServiceImpl extends ModuleRestService implements TriggerService<TriggerDto>
{
    Logger logger = LoggerFactory.getLogger(TriggerRestServiceImpl.class);

    protected final static String PUT_TRIGGER_URL = "/rest/wiretap/trigger";
    protected final static String DELETE_TRIGGER_URL = "/rest/wiretap/trigger/{triggerId}";

    public TriggerRestServiceImpl(Environment environment,
                                  HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        super(environment, httpComponentsClientHttpRequestFactory);
    }

    public boolean create(String contextUrl, TriggerDto triggerDto)
    {
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(triggerDto, headers);
        String url = contextUrl + PUT_TRIGGER_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Issue creating trigger [" + url + "] with dto [" + triggerDto + "]");
            return false;
        }
    }

    public boolean delete(String contextUrl, String triggerId)
    {
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        String url = contextUrl + DELETE_TRIGGER_URL;
        try
        {
            Map<String, String> parameters = new HashMap<String, String>()
            {{put("triggerId", triggerId);}};

            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class,parameters);

            return true;
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Issue Deleting trigger [" + url + "] with module [" + triggerId + "]");
            return false;
        }
    }



}
