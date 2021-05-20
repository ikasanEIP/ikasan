package org.ikasan.rest.client;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.binary.Base64;
import org.ikasan.rest.client.dto.ReplayRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class ReplayRestServiceImpl
{
    Logger logger = LoggerFactory.getLogger(ReplayRestServiceImpl.class);

    protected final static String REPLAY_URL = "/rest/replay";

    private RestTemplate restTemplate;

    public ReplayRestServiceImpl(HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory)
    {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
    }

    public boolean replay(String contextUrl, String username, String password, String moduleName, String flowName,
                          byte[] event)
    {
        ReplayRequestDto dto = new ReplayRequestDto(moduleName, flowName, event);
        HttpHeaders headers = createHttpHeaders(username, password);
        HttpEntity entity = new HttpEntity(dto, headers);
        String url = contextUrl + REPLAY_URL;
        try
        {
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return true;
        }
        catch (RestClientException e)
        {
            logger.warn(
                "Issue replaying event [" + new String(event) + "] [" + url + "] with module [" + moduleName + "] "
                    + "and flows [" + flowName + "]" + " with response [{" + e.getLocalizedMessage() + "}]");
            return false;
        }
    }

    private HttpHeaders createHttpHeaders(String username, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        if ( username != null && password != null )
        {
            String credentials = username + ":" + password;
            String basicToken = new String(Base64.encodeBase64(credentials.getBytes()));
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + basicToken);
        }
        return headers;
    }
}
