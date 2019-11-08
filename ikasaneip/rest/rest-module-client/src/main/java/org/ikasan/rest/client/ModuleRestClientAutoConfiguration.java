package org.ikasan.rest.client;

import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

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
    public ConfigurationRestServiceImpl configurationRestService(Environment environment){
        return new ConfigurationRestServiceImpl(environment, this.jsonConfigurationMetaDataProvider);
    }

    @Bean
    public ReplayRestServiceImpl replayRestService(){
        return new ReplayRestServiceImpl();
    }

    @Bean
    public ResubmissionRestServiceImpl resubmissionRestService(Environment environment){
        return new ResubmissionRestServiceImpl(environment);
    }

    @Bean
    public ModuleControlRestServiceImpl moduleControlRestService(Environment environment){
        return new ModuleControlRestServiceImpl(environment);
    }

}