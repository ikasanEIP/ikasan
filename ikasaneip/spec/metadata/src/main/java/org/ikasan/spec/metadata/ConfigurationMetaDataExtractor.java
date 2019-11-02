package org.ikasan.spec.metadata;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

import java.util.List;

public interface ConfigurationMetaDataExtractor<T>
{
    /**
     * Gets all component configured resources from runtime flow and provides them in meta data format.
     *
     * @param flow to extract the component configurations
     * @return the meta data describing given flow components configuration.
     */
    List<T> getComponentsConfiguration(Flow flow);

    /**
     * Gets all invoker configured resources from runtime flow and provides them in meta data format.
     *
     * @param flow to extract the invoker configurations
     * @return the meta data describing given flow invokers configuration.
     */
    List<T> getInvokersConfiguration(Flow flow);

    /**
     * Gets all component configured resources from runtime module aka all flows and provides them in meta data format.
     *
     * @param module to extract the component configurations
     * @return the meta data describing given flow components configuration.
     */
    List<T> getComponentsConfiguration(Module<Flow> module);

    /**
     * Gets all invoker configured resources from runtime module aka all flows and provides them in meta data format.
     *
     * @param module to extract the invoker configurations
     * @return the meta data describing given flow invokers configuration.
     */
    List<T> getInvokersConfiguration(Module<Flow> module);

    /**
     * Gets flow related configured resource and provides them in meta data format.
     *
     * @param flow to extract the flow configured resource
     * @return the meta data describing given flow configuration.
     */
    T getFlowConfiguration(Flow flow);

    /**
     * Gets flow related configured resource and provides them in meta data format.
     *
     * @param module to extract the all flows configured resources
     * @return the meta data describing given module all flows configuration.
     */
    List<T> getFlowsConfiguration(Module<Flow> module);
}
