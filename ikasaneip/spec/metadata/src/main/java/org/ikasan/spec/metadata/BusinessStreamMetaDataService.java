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
     * @param startOffset
     * @param resultSize
     * @return
     */
    public List<T> findAll(Integer startOffset, Integer resultSize);

    /**
     * Find with business stream name and paging.
     *
     * @param businessStreamNames
     * @param startOffset
     * @param resultSize
     * @return
     */
    public BusinessStreamMetadataSearchResults find(List<String> businessStreamNames, Integer startOffset, Integer resultSize);

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
