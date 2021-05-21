package org.ikasan.rest.client;

import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.ikasan.spec.module.client.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.Resource;

/**
 * Module Rest Client configuration required by ikasan dashboard in order to communicate with modules.
 * This autoconfig should be excluded from dashboard.
 */
public class ModuleRestClientAutoConfiguration
{
    @Resource
    private JsonConfigurationMetaDataProvider jsonConfigurationMetaDataProvider;

    @Bean
    @ConfigurationProperties(prefix = "module.rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory()
    {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory
            = new HttpComponentsClientHttpRequestFactory();

        // all of the properties can be overwritten using spring properties.
        httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(5000);

        return httpComponentsClientHttpRequestFactory;
    }

    @Bean
    public ConfigurationService configurationRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new ConfigurationRestServiceImpl(environment, this.jsonConfigurationMetaDataProvider, httpComponentsClientHttpRequestFactory);
    }

    @Bean
    public ReplayService replayRestService(HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new ReplayRestServiceImpl(httpComponentsClientHttpRequestFactory);
    }

    @Bean
    public ResubmissionService resubmissionRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new ResubmissionRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);
    }

    @Bean
    public ModuleControlService moduleControlRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new ModuleControlRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);
    }

    @Bean
    public TriggerService triggerRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new TriggerRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);
    }

    @Bean
    public MetaDataService metaDataApplicationRestService(Environment environment
        , HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory){
        return new MetaDataRestServiceImpl(environment, httpComponentsClientHttpRequestFactory);
    }

}