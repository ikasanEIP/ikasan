package org.ikasan.spec.metadata.dao;

import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetadataSearchResults;
import org.ikasan.spec.module.ModuleType;

import java.util.List;

/**
 * Data Access Object interface for ModuleMetaData persistence operations.
 *
 * This interface defines the contract for storing, retrieving, and managing
 * module metadata across different persistence implementations (e.g., Solr, Hibernate).
 */
public interface ModuleMetadataDao {

    /**
     * Save a list of module metadata records.
     *
     * @param moduleMetaDataList the list of module metadata to save
     */
    void save(List<ModuleMetaData> moduleMetaDataList);

    /**
     * Find module metadata by its unique identifier.
     *
     * @param id the unique identifier of the module
     * @return the ModuleMetaData if found, null otherwise
     */
    ModuleMetaData findById(String id);

    /**
     * Delete module metadata by its unique identifier.
     *
     * @param id the unique identifier of the module to delete
     */
    void deleteById(String id);

    /**
     * Find all module metadata with pagination.
     *
     * @param startOffset the starting offset for pagination
     * @param resultSize the maximum number of results to return
     * @return a list of ModuleMetaData records
     */
    List<ModuleMetaData> findAll(Integer startOffset, Integer resultSize);

    /**
     * Find module metadata with filtering by module names and pagination.
     *
     * @param modulesNames the list of module names to filter by (can be null or empty for no filtering)
     * @param startOffset the starting offset for pagination
     * @param resultSize the maximum number of results to return
     * @return search results containing matching ModuleMetaData records and result metadata
     */
    ModuleMetadataSearchResults find(List<String> modulesNames, Integer startOffset, Integer resultSize);

    /**
     * Find module metadata with filtering by module names, module type, and pagination.
     *
     * @param modulesNames the list of module names to filter by (can be null or empty for no filtering)
     * @param moduleType the type of module to filter by
     * @param startOffset the starting offset for pagination (-1 to ignore offset)
     * @param resultSize the maximum number of results to return (-1 to return all results)
     * @return search results containing matching ModuleMetaData records and result metadata
     */
    ModuleMetadataSearchResults find(List<String> modulesNames, ModuleType moduleType, Integer startOffset, Integer resultSize);
}
