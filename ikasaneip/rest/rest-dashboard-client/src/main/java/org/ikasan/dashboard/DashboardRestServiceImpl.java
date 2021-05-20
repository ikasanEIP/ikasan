package org.ikasan.dashboard;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.dashboard.model.JwtRequest;
import org.ikasan.dashboard.model.JwtResponse;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class DashboardRestServiceImpl<T> implements DashboardRestService<T>
{
    Logger logger = LoggerFactory.getLogger(DashboardRestServiceImpl.class);

    private Converter converter;

    private RestTemplate restTemplate;

    private String url;

    private String authenticateUrl;

    private String moduleName;

    private String username;

    private String password;

    private String token;

    private boolean isEnabled;

    public DashboardRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory,
                                    String path, Converter converter)
    {
        this(environment, httpComponentsClientHttpRequestFactory, path);
        this.converter = converter;
    }

    public DashboardRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory,
                                    String path)
    {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
        isEnabled = Boolean.valueOf(environment.getProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if ( isEnabled )
        {
            this.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + path;
            this.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);
            this.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
            this.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        }
    }

    public boolean publish(T events)
    {
        if ( isEnabled && events != null )
        {
            return callHttp(events, true);
        }
        return false;
    }

    private boolean callHttp(T events, boolean isFirst)
    {
        logger.debug("Pushing events [{}] to dashboard [{}]", events, url);
        HttpHeaders headers = createHttpHeaders();
        HttpEntity<List<HarvestEvent>> entity;
        if ( converter != null )
        {
            entity = new HttpEntity(converter.convert(events), headers);
        }
        else
        {
            entity = new HttpEntity(events, headers);
        }
        try
        {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            logger.debug("Successfully published [{}] events to dashboard [{}] with response [{}]", events, url,
                response
                        );
            return true;
        }
        catch (HttpClientErrorException e)
        {
            if ( e.getRawStatusCode() == 401 && isFirst )
            {
                this.token = null;
                if ( authenticate() )
                { return callHttp(events, false); }
            }
            logger.warn("Issue while publishing events to dashboard [{}] with response [{}] [{}]", url,
                e.getRawStatusCode(), e.getResponseBodyAsString());
            return false;
        }
        catch (RestClientException e)
        {
            logger.warn("Issue while publishing events to dashboard [{}] with response [{}]", url,
                 e.getLocalizedMessage());

            return false;
        }
    }

    private boolean authenticate()
    {
        HttpEntity<JwtRequest> entity = new HttpEntity(new JwtRequest(username, password), createHttpHeaders());
        try
        {
            if ( username != null && password != null )
            {
                ResponseEntity<JwtResponse> response = restTemplate
                    .exchange(authenticateUrl, HttpMethod.POST, entity, JwtResponse.class);
                this.token = response.getBody().getToken();
                return true;
            }
            return false;
        }
        catch (HttpClientErrorException e)
        {
            logger.warn("Issue while authenticating to dashboard [" + authenticateUrl + "] with response [{" + e
                .getResponseBodyAsString() + "}]", e);
            return false;
        }
    }

    private HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.USER_AGENT, moduleName);
        if ( token != null )
        {
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }
}
