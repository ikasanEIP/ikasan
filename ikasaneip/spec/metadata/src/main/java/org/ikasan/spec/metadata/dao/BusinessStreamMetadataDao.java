package org.ikasan.spec.metadata.dao;

import org.ikasan.spec.metadata.BusinessStreamMetadataSearchResults;
import org.ikasan.spec.metadata.model.BusinessStreamMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;

import java.util.List;

/**
 * Data Access Object interface for Business Stream Metadata operations.
 */
public interface BusinessStreamMetadataDao {

    /**
     * We need to give this dao it's context.
     */
    String BUSINESS_STREAM_METADATA = "businessStreamMetaData";

    /**
     * Find a business stream metadata by its ID.
     *
     * @param id the business stream ID
     * @return the business stream metadata, or null if not found
     */
    BusinessStreamMetaData findById(String id);

    /**
     * Find business stream metadata by business stream names with pagination.
     *
     * @param businessStreamNames list of business stream names to filter by
     * @param startOffset the starting offset for pagination
     * @param resultSize the maximum number of results to return
     * @return search results containing matching business stream metadata
     */
    BusinessStreamMetadataSearchResults find(List<String> businessStreamNames, Integer startOffset, Integer resultSize);

    /**
     * Find business streams that contain a specific flow.
     *
     * @param moduleName the module name
     * @param flowName the flow name
     * @param offset the starting offset for pagination
     * @param limit the maximum number of results to return
     * @return list of business stream metadata containing the specified flow
     */
    List<BusinessStreamMetaData> findBusinessStreamsContainingFlow(String moduleName, String flowName, int offset, int limit);

    /**
     * Find business streams for a list of modules with optional filtering.
     *
     * @param filter optional filter string to apply to module names
     * @param modules list of modules to search for
     * @param offset the starting offset for pagination
     * @param limit the maximum number of results to return
     * @return search results containing matching business stream metadata
     */
    BusinessStreamMetadataSearchResults findBusinessStreamsForModules(String filter, List<ModuleMetaData> modules, int offset, int limit);

    /**
     * Find all business stream metadata with pagination.
     *
     * @param startOffset the starting offset for pagination
     * @param resultSize the maximum number of results to return
     * @return list of all business stream metadata
     */
    List<BusinessStreamMetaData> findAll(Integer startOffset, Integer resultSize);

    /**
     * Delete a business stream metadata by its ID.
     *
     * @param id the business stream ID to delete
     */
    void delete(String id);

    /**
     * Save a business stream.
     *
     * @param businessStream the business stream to save
     */
    void save(BusinessStreamMetaData businessStream);
}
