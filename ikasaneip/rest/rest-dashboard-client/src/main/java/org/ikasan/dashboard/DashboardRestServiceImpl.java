package org.ikasan.dashboard;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class DashboardRestServiceImpl<T> extends AbstractRestServiceImpl implements DashboardRestService<T>
{
    Logger logger = LoggerFactory.getLogger(DashboardRestServiceImpl.class);

    protected Converter converter;
    protected String moduleName;
    protected boolean bubbleExceptionsUpToCaller;

    public DashboardRestServiceImpl(RestTemplate restTemplate, Environment environment,
                                    String path, Converter converter)
    {
        this(restTemplate, environment, path);
        this.converter = converter;
    }

    public DashboardRestServiceImpl(RestTemplate restTemplate, Environment environment, String path)
    {
        super(restTemplate, environment, path);
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
    }

    @Override
    protected void initialise(Environment environment, String path) {
        this.isEnabled = Boolean.valueOf(environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_EXTRACT_ENABLED_PROPERTY, "false"));
        if (this.isEnabled)
        {
            if(path != null) {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY) + path;
            }
            else {
                this.url = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY);
            }
            this.authenticateUrl = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
            this.username = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_USERNAME_PROPERTY);
            this.password = environment.getProperty(LoadBalancedDashboardRestServiceImpl.DASHBOARD_PASSWORD_PROPERTY);
            this.moduleName = environment.getProperty(MODULE_NAME_PROPERTY);
            this.bubbleExceptionsUpToCaller = Boolean.valueOf(environment.getProperty(DASHBOARD_EXTRACT_EXCEPTIONS_PROPERTY, "false"));
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
        HttpHeaders headers = createHttpHeaders(this.moduleName);
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
                if ( authenticate(this.moduleName) )
                { return callHttp(events, false); }
            }
            logger.warn("Issue while publishing events to dashboard [{}] with response [{}] [{}]", url,
                e.getRawStatusCode(), e.getResponseBodyAsString());

            if(bubbleExceptionsUpToCaller) {
                throw new RuntimeException(String.format("Issue while publishing events to dashboard [%s] with response [%s] [%s]", url,
                    e.getRawStatusCode(), e.getResponseBodyAsString()), e);
            }

            return false;
        }
        catch (RestClientException e)
        {
            logger.warn("Issue while publishing events to dashboard [{}] with response [{}]", url,
                 e.getLocalizedMessage());

            if(bubbleExceptionsUpToCaller) {
                throw new RuntimeException(String.format("Issue while publishing events to dashboard [%s] with response [%s]", url,
                    e.getLocalizedMessage()), e);
            }

            return false;
        }
    }
}
