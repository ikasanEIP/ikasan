package org.ikasan.spec.metadata.dao;

import org.ikasan.spec.metadata.model.ConfigurationMetaData;

import java.util.List;

/**
 * Data Access Object interface for Component Configuration Metadata operations.
 */
public interface ComponentConfigurationMetadataDao {

    String COMPONENT_CONFIGURATION = "componentConfiguration";

    /**
     * Save a list of configuration metadata.
     *
     * @param configurationMetaDataList list of configuration metadata to save
     */
    void save(List<ConfigurationMetaData> configurationMetaDataList);

    /**
     * Find configuration metadata by its ID.
     *
     * @param id the configuration ID
     * @return the configuration metadata, or null if not found
     */
    ConfigurationMetaData findById(String id);

    /**
     * Find all configuration metadata.
     *
     * @return list of all configuration metadata
     */
    List<ConfigurationMetaData> findAll();

    /**
     * Find configuration metadata for a list of configuration IDs.
     *
     * @param configurationIds list of configuration IDs to search for
     * @return list of configuration metadata matching the provided IDs
     */
    List<ConfigurationMetaData> findInIdList(List<String> configurationIds);
}
