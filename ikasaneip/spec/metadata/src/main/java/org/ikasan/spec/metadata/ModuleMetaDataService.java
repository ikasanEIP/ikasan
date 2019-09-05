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
}
