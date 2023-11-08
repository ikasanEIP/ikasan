package org.ikasan.configurationService.metadata;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationParameterMetaData;
import org.ikasan.spec.module.Module;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of FlowMetaDataProvider that provides a JSON representation of a flow.
 *
 * @author Ikasan Development Team
 */
public class JsonConfigurationMetaDataExtractor implements ConfigurationMetaDataExtractor<ConfigurationMetaData>
{
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

    /**
     * Constructor
     */
    public JsonConfigurationMetaDataExtractor(
        ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
    {
        this.configurationManagement = configurationManagement;
    }

    @Override
    public ConfigurationMetaData getConfiguration(ConfiguredResource configuredResource)
    {

        Configuration<List<ConfigurationParameter>> configuration = this.configurationManagement
            .getConfiguration(configuredResource.getConfiguredResourceId());

        if ( configuration == null )
        {
            configuration = this.configurationManagement.createConfiguration(configuredResource);
        }

        ConfigurationMetaDataImpl configurationMetaDataImpl = new ConfigurationMetaDataImpl(
            configuration.getConfigurationId(), configuration.getDescription(),
            configuredResource.getConfiguration() != null ?
                configuredResource.getConfiguration().getClass().getName() :
                configuration.getClass().getName(), getParameters(configuration.getParameters())
        );

        return configurationMetaDataImpl;

    }


    @Override
    public List<ConfigurationMetaData> getComponentsConfiguration(Flow flow)
    {
        List<ConfigurationMetaData> result;

        try
        {
            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = flow.getFlowElements().stream()
                                                               // extract all components from ever flow stream to
                                                               // single stream
                                                               .map(flowElement -> flowElement.getFlowComponent())
                                                               .filter(
                                                                   flowElementComponent -> flowElementComponent instanceof ConfiguredResource)
                                                               // convert all ConfiguredResource components
                                                               .map(
                                                                   flowElementComponent -> (ConfiguredResource) flowElementComponent)

                                                               // filter distinctive set of Configurations using
                                                               // configurationId
                                                               .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                                                               .collect(Collectors.toList());

            result = this.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating configuredResource configuration meta data json!", e);
        }

        return result;
    }


    @Override
    public List<ConfigurationMetaData> getInvokersConfiguration(Flow flow)
    {
        List<ConfigurationMetaData> result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = flow.getFlowElements().stream()
                                                               .map(flowElement -> flowElement.getFlowElementInvoker())
                                                               .filter(
                                                                   flowElementInvoker -> flowElementInvoker instanceof ConfiguredResource)
                                                               .map(
                                                                   flowElementInvoker -> (ConfiguredResource) flowElementInvoker)
                                                               // filter distinctive set of Configurations using
                                                               // configurationId
                                                               .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                                                               .collect(Collectors.toList());

            result = this.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }

        return result;
    }


    @Override
    public List<ConfigurationMetaData> getComponentsConfiguration(Module<Flow> module)
    {
        List<ConfigurationMetaData> result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                                                                 .map(flow -> flow.getFlowElements())
                                                                 .flatMap(Collection::stream)
                                                                 .map(flowElement -> flowElement.getFlowComponent())
                                                                 .filter(
                                                                     flowElementComponent -> flowElementComponent instanceof ConfiguredResource)
                                                                 .map(
                                                                     flowElementComponent -> (ConfiguredResource) flowElementComponent)
                                                                 // filter distinctive set of Configurations using
                                                                 // configurationId
                                                                 .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                                                                 .collect(Collectors.toList());

            result = this.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating components configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public List<ConfigurationMetaData> getInvokersConfiguration(Module<Flow> module)
    {
        List<ConfigurationMetaData> result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                                                                 .map(flow -> flow.getFlowElements())
                                                                 .flatMap(Collection::stream).map(
                    flowElement -> flowElement.getFlowElementInvoker()).filter(
                    flowElementInvoker -> flowElementInvoker instanceof ConfiguredResource).map(
                    flowElementInvoker -> (ConfiguredResource) flowElementInvoker)
                                                                 // filter distinctive set of Configurations using
                                                                 // configurationId
                                                                 .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                                                                 .collect(Collectors.toList());

            result = this.describeConfiguredResources(configuredResources);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating invoker configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public ConfigurationMetaData getFlowConfiguration(Flow flow)
    {

        try
        {

            if ( flow instanceof ConfiguredResource resource )
            {
                return this.getConfiguration(resource);
            }
            else
            {
                throw new RuntimeException("Flow is not a instance of ConfiguredResource");
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow configuration meta data json!", e);
        }

    }

    @Override
    public List<ConfigurationMetaData> getFlowsConfiguration(Module<Flow> module)
    {

        try
        {
            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                                                                 .filter(flow -> flow instanceof ConfiguredResource)
                                                                 .map(flow -> (ConfiguredResource) flow)
                                                                 .collect(Collectors.toList());

            return this.describeConfiguredResources(configuredResources);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow configuration meta data json!", e);
        }

    }

    public List<ConfigurationMetaData> describeConfiguredResources(List<ConfiguredResource> configuredResource)
    {
        return configuredResource.stream()
                                 .filter(r -> r.getConfiguredResourceId()!=null && r.getConfiguration()!=null)
                                 .map(r -> getConfiguration(r))
                                 .collect(Collectors.toList());

    }


    private List<ConfigurationParameterMetaData> getParameters(List<ConfigurationParameter> parameters)
    {

        return parameters.stream().map(p -> convert(p)).collect(Collectors.toList());
    }

    private ConfigurationParameterMetaData convert(ConfigurationParameter p)
    {
        return new ConfigurationParameterMetaDataImpl(p.getId(), p.getName(), p.getValue(), p.getDescription(),
            p.getClass().getName()
        );
    }

}
