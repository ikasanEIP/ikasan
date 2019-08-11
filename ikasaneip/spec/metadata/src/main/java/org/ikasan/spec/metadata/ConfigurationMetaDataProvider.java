package org.ikasan.spec.metadata;

import org.ikasan.spec.configuration.ConfiguredResource;

import java.util.List;

public interface ConfigurationMetaDataProvider<T>
{
    /**
     * Method to deserialise a meta data representation of a single configuration metadata.
     *
     * @param metaData the configuration meta data
     * @return the deserialised configuration meta data
     */
    ConfigurationMetaData deserialiseMetadataConfiguration(T metaData);

    /**
     * Method to deserialise a meta data representation of a list of configuration metadata.
     *
     * @param metaData the configuration list meta data
     * @return the deserialised list of configuration meta data
     */
    List<ConfigurationMetaData> deserialiseMetadataConfigurations(T metaData);

    /**
     * Method to convert a list of configured resources into a description of it.
     *
     * @param configuredResources list of configured resource we are describing.
     * @return the description of the list of configured resources.
     */
    T describeConfiguredResources(List<ConfiguredResource> configuredResources);

    /**
     * Method to convert a configured resource into a description of it.
     *
     * @param configuredResource configured Resource we are describing.
     * @return the description of the configured resource.
     */
    T describeConfiguredResource(ConfiguredResource configuredResource);
}
