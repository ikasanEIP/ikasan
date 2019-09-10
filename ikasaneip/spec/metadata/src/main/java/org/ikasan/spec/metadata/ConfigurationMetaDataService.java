package org.ikasan.spec.metadata;

import java.util.List;

public interface ConfigurationMetaDataService
{
    /**
     * Get the configuration metadata by id.
     *
     * @param id
     * @return
     */
    public ConfigurationMetaData findById(String id);


    /**
     * Get all the persisted configuration metadata.
     *
     * @return
     */
    public List<ConfigurationMetaData> findAll();

    /**
     * Find a list of configuration meta data based on a list of ids.
     *
     * @param configurationIds
     * @return
     */
    public List<ConfigurationMetaData> findByIdList(List<String> configurationIds);
}
