package org.ikasan.rest.client;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public abstract class ModuleRestService
{
    protected final static String MODULE_REST_USERNAME_PROPERTY = "rest.module.username";

    protected final static String MODULE_REST_PASSWORD_PROPERTY = "rest.module.password";

    protected RestTemplate restTemplate;

    protected String basicToken;

    public ModuleRestService(Environment environment)
    {
        restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        String username = environment.getProperty(MODULE_REST_USERNAME_PROPERTY);
        String password = environment.getProperty(MODULE_REST_PASSWORD_PROPERTY);

        if(username!=null && password !=null)
        {
            String credentials = username + ":" + password;
            basicToken = new String(Base64.encodeBase64(credentials.getBytes()));
        }

    }

    protected HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        if ( basicToken != null )
        {
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + basicToken);
        }
        return headers;
    }
}
