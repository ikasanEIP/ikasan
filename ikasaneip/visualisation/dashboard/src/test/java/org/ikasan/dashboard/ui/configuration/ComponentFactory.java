package org.ikasan.dashboard.ui.configuration;

import org.ikasan.spec.metadata.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("test")
@Configuration
public class ComponentFactory
{

    @Bean({"moduleMetadataService"})
    public ModuleMetaDataService moduleMetadataService()
    {
        return new ModuleMetaDataService()
        {
            @Override
            public ModuleMetaData findById(String id)
            {
                return null;
            }

            @Override
            public List<ModuleMetaData> findAll()
            {
                return null;
            }

            @Override
            public ModuleMetadataSearchResults find(List<String> modulesNames, Integer startOffset, Integer resultSize)
            {
                return null;
            }
        };
    }

    @Bean({"configurationMetadataService"})
    public ConfigurationMetaDataService configurationMetadataService()
    {
        return new ConfigurationMetaDataService()
        {

            @Override
            public ConfigurationMetaData findById(String id)
            {
                return null;
            }

            @Override
            public List<ConfigurationMetaData> findAll()
            {
                return null;
            }

            @Override
            public List<ConfigurationMetaData> findByIdList(List<String> configurationIds)
            {
                return null;
            }
        };
    }

}
