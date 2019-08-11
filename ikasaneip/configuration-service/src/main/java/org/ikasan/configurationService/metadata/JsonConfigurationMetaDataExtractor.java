package org.ikasan.configurationService.metadata;

import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.ConfigurationMetaDataExtractor;
import org.ikasan.spec.metadata.ConfigurationMetaDataProvider;
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
public class JsonConfigurationMetaDataExtractor implements ConfigurationMetaDataExtractor<String>
{

    private ConfigurationMetaDataProvider<String> configurationMetaDataProvider;
    /**
     * Constructor
     */
    public JsonConfigurationMetaDataExtractor(ConfigurationMetaDataProvider configurationMetaDataProvider)
    {
        this.configurationMetaDataProvider = configurationMetaDataProvider;
    }

    @Override
    public String getComponentsConfiguration(Flow flow)
    {
        String result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = flow.getFlowElements().stream()
                // extract all components from ever flow stream to single stream
                .map(flowElement -> flowElement.getFlowComponent())
                .filter( flowElementComponent -> flowElementComponent instanceof ConfiguredResource )
                // convert all ConfiguredResource components
                .map(flowElementComponent -> (ConfiguredResource) flowElementComponent)

                // filter distinctive set of Configurations using configurationId
                .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                .collect(Collectors.toList())
                ;

            result = this.configurationMetaDataProvider.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating component configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public String getInvokersConfiguration(Flow flow)
    {
        String result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = flow.getFlowElements().stream()
                .map(flowElement -> flowElement.getFlowElementInvoker())
                .filter( flowElementInvoker -> flowElementInvoker instanceof ConfiguredResource )
                .map(flowElementInvoker -> (ConfiguredResource) flowElementInvoker)
                // filter distinctive set of Configurations using configurationId
                .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                .collect(Collectors.toList());


            result = this.configurationMetaDataProvider.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred invoker configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public String getComponentsConfiguration(Module<Flow> module)
    {
        String result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                .map(flow -> flow.getFlowElements())
                .flatMap(Collection::stream)
                .map(flowElement -> flowElement.getFlowComponent())
                .filter( flowElementComponent -> flowElementComponent instanceof ConfiguredResource )
                .map(flowElementComponent -> (ConfiguredResource) flowElementComponent)
                // filter distinctive set of Configurations using configurationId
                .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                .collect(Collectors.toList());

            result = this.configurationMetaDataProvider.describeConfiguredResources(configuredResources);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating components configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public String getInvokersConfiguration(Module<Flow> module)
    {
        String result;

        try
        {

            // distinctive set of configurationId names used by stream filtering
            Set<String> nameSet = new HashSet<>();

            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                .map(flow -> flow.getFlowElements())
                .flatMap(Collection::stream)
                .map(flowElement -> flowElement.getFlowElementInvoker())
                .filter( flowElementInvoker -> flowElementInvoker instanceof ConfiguredResource )
                .map(flowElementInvoker -> (ConfiguredResource) flowElementInvoker)
                // filter distinctive set of Configurations using configurationId
                .filter(e -> nameSet.add(e.getConfiguredResourceId()))
                .collect(Collectors.toList());

            result = this.configurationMetaDataProvider.describeConfiguredResources(configuredResources);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating invoker configuration meta data json!", e);
        }

        return result;
    }

    @Override
    public String getFlowConfiguration(Flow flow)
    {

        try
        {

            if( flow instanceof ConfiguredResource )
            {
                return this.configurationMetaDataProvider.describeConfiguredResource((ConfiguredResource) flow);
            }else{
                throw new RuntimeException("Flow is not a instance of ConfiguredResource");
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow configuration meta data json!", e);
        }

    }

    @Override
    public String getFlowsConfiguration(Module<Flow> module)
    {

        try
        {
            List<ConfiguredResource> configuredResources = module.getFlows().stream()
                .filter( flow -> flow instanceof ConfiguredResource )
                .map(flow -> (ConfiguredResource) flow)
                .collect(Collectors.toList());

            return this.configurationMetaDataProvider.describeConfiguredResources(configuredResources);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating flow configuration meta data json!", e);
        }

    }

}
