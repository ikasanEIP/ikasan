package org.ikasan.rest.module;

import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.module.ModuleService;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ConfigurationApplicationConfiguration
{
    @Bean
    public Mockery mockery() {
        return new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
    }

    @Bean
    public ModuleService moduleService(Mockery mockery) {

        return mockery.mock(ModuleService.class);
    }
    @Bean
    public ConfigurationMetaDataExtractor configurationMetaDataExtractor(Mockery mockery) {

        return mockery.mock(ConfigurationMetaDataExtractor.class);
    }
    @Bean
    public ConfigurationManagement configurationManagement(Mockery mockery) {

        return mockery.mock(ConfigurationManagement.class);
    }

}
