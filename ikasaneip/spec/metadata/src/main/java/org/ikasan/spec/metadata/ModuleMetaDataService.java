package org.ikasan.spec.metadata;

import java.util.List;

public interface ModuleMetaDataService
{
    /**
     * Get the module metadata by id.
     *
     * @param id
     * @return
     */
    public ModuleMetaData findById(String id);


    /**
     * Get all the persisted module metadata.
     *
     * @return
     */
    public List<ModuleMetaData> findAll();

    /**
     * Find using module names and offset.
     *
     * @param modulesNames
     * @param startOffset
     * @param resultSize
     * @return
     */
    public ModuleMetadataSearchResults find(List<String> modulesNames, Integer startOffset, Integer resultSize);

}
