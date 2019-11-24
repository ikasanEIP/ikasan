package org.ikasan.spec.metadata;

import java.util.List;

public interface  BusinessStreamMetaDataService<T>
{
    /**
     * Get the module metadata by id.
     *
     * @param id
     * @return
     */
    public T findById(String id);


    /**
     * Get all the persisted module metadata.
     *
     * @return
     */
    public List<T> findAll();

    /**
     * Save the meta data.
     *
     * @param metaData
     */
    public void save(T metaData);

    /**
     * Delete meta data.
     *
     * @param id
     */
    public void delete(String id);
}
