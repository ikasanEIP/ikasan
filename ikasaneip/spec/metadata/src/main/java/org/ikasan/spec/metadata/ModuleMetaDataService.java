package org.ikasan.spec.metadata;

import org.ikasan.spec.module.ModuleType;

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

    /**
     * Find using module names, module type and offset.
     *
     * @param modulesNames
     * @param moduleType
     * @param startOffset
     * @param resultSize
     * @return
     */
    public ModuleMetadataSearchResults find(List<String> modulesNames, ModuleType moduleType, Integer startOffset, Integer resultSize);

    /**
     * Delete the module meta data by its name.
     *
     * @param name
     */
    public void deleteById(String name);

}
