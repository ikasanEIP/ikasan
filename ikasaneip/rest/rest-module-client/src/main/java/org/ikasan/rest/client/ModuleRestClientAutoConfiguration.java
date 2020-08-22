package org.ikasan.rest.client;

import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataProvider;
import org.ikasan.spec.module.client.*;
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
    public ConfigurationService configurationRestService(Environment environment){
        return new ConfigurationRestServiceImpl(environment, this.jsonConfigurationMetaDataProvider);
    }

    @Bean
    public ReplayService replayRestService(){
        return new ReplayRestServiceImpl();
    }

    @Bean
    public ResubmissionService resubmissionRestService(Environment environment){
        return new ResubmissionRestServiceImpl(environment);
    }

    @Bean
    public ModuleControlService moduleControlRestService(Environment environment){
        return new ModuleControlRestServiceImpl(environment);
    }

    @Bean
    public TriggerService triggerRestService(Environment environment){
        return new TriggerRestServiceImpl(environment);
    }

    @Bean
    public MetaDataService metaDataApplicationRestService(Environment environment){
        return new MetaDataRestServiceImpl(environment);
    }

}