package org.ikasan.configurationService.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ConfigurationMetaDataProvider that provides a JSON representation of a ConfigurationResource.
 *
 * This provider implements methods helpful in conversion:
 *  - from configuration resource to JSON String
 *  - from String to configuration meta data
 *
 * @author Ikasan Development Team
 */
public class JsonConfigurationMetaDataProvider implements ConfigurationMetaDataProvider<String>
{

    private ObjectMapper mapper;

    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    /**
     * Constructor
     */
    public JsonConfigurationMetaDataProvider(ConfigurationManagement configurationManagement)
    {
        this.configurationManagement = configurationManagement;
        mapper = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ConfigurationParameterMetaData.class,ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class,ConfigurationMetaDataImpl.class);
        this.mapper.registerModule(m);
    }


    @Override
    public ConfigurationMetaData deserialiseMetadataConfiguration(String metaData) {
        ConfigurationMetaDataImpl result;

        try
        {
            //JSON file to Java object
            result = this.mapper.readValue(metaData, ConfigurationMetaDataImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow meta data object!", e);
        }

        return result;

    }

    @Override
    public List<ConfigurationMetaData> deserialiseMetadataConfigurations(String metaData) {
        List result;

        try
        {
            //JSON file to Java object
            result = this.mapper.readValue(metaData, List.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow meta data object!", e);
        }

        return result;

    }

    @Override
    public String describeConfiguredResources(List<ConfiguredResource> configuredResource)
    {
        List<ConfigurationMetaData> metadataConfigurations = configuredResource.stream()
            .map(r -> convert(r))
            .collect(Collectors.toList());
        try
        {
            return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataConfigurations);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }
    }

    @Override
    public String describeConfiguredResource(ConfiguredResource configuredResource){


        ConfigurationMetaData metadataConfiguration = convert(configuredResource);
        try
        {
             return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataConfiguration);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }
    }

    private ConfigurationMetaData convert(ConfiguredResource configuredResource){

        Configuration<List<ConfigurationParameter>> configuration = this.configurationManagement.getConfiguration(configuredResource.getConfiguredResourceId());

        if (configuration == null) {
            configuration = this.configurationManagement.createConfiguration(configuredResource);
        }

        ConfigurationMetaDataImpl configurationMetaDataImpl = new ConfigurationMetaDataImpl(configuration.getConfigurationId(),
            configuration.getDescription(),configuredResource.getConfiguration() != null ?
            configuredResource.getConfiguration().getClass().getName() : configuration.getClass().getName(),
            getParameters(configuration.getParameters()));

        return configurationMetaDataImpl;

    }

    private List<ConfigurationParameterMetaData> getParameters(List<ConfigurationParameter> parameters){

        return parameters.stream().map( p -> convert(p)).collect(Collectors.toList());
    }

    private ConfigurationParameterMetaData convert(ConfigurationParameter p)
    {
        return new ConfigurationParameterMetaDataImpl(p.getId(),p.getName(),p.getValue(),p.getDescription(),p.getClass().getName());
    }


}
