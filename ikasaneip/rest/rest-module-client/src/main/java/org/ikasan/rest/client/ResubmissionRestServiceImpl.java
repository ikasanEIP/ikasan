package org.ikasan.rest.client;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.rest.client.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResubmissionRestServiceImpl
{
    Logger logger = LoggerFactory.getLogger(ResubmissionRestServiceImpl.class);

    protected final static String RESUBMSSION_URL = "/rest/resubmission";

    private RestTemplate restTemplate;

    public ResubmissionRestServiceImpl()
    {
        restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
    }

    public boolean resubmit(String contextUrl, String moduleName, String flowName, String action, String errorUri)
    {
        ResubmissionRequestDto dto = new ResubmissionRequestDto(moduleName, flowName, errorUri, action);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity entity = new HttpEntity(dto, headers);
        String url = contextUrl + RESUBMSSION_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (RestClientException e)
        {
            logger.warn("Issue resubmitting event [" + errorUri + "] [" + url + "] with module [" + moduleName + "] "
                            + "and flows [" + flowName + "]" + "and action [" + action + "]" + " with response [{" + e
                .getLocalizedMessage() + "}]");
            return false;
        }
    }

    private HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
