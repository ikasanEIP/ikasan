package org.ikasan.spec.scheduled.context.dao;

import org.ikasan.spec.scheduled.context.model.ScheduledContextRecord;
import org.ikasan.spec.scheduled.context.model.ScheduledContextSearchFilter;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface ScheduledContextDao {

    /**
     * Find all ScheduledContextRecords
     *
     * @return SearchResults containing a List of ScheduledContextRecord and associated meta data.
     */
    SearchResults<ScheduledContextRecord> findAll();

    /**
     * Find ScheduledContextRecord by id.
     * @param id
     *
     * @return matching ScheduledContextRecord or null if not found
     */
    ScheduledContextRecord findById(String id);

    /**
     * Find all ScheduledContextRecords with limit and offset. Used for result paging.
     *
     * @param limit
     * @param offset
     *
     * @return SearchResults containing a List of ScheduledContextRecord and associated meta data.
     */
    SearchResults<ScheduledContextRecord> findAll(int limit, int offset);

    /**
     * Find all ScheduledContextRecords containing keyword with limit and offset. Used for result paging.
     *
     * @param filter
     * @param limit
     * @param offset
     * @param sortColumn
     * @param sortOrder
     *
     * @return SearchResults containing a List of ScheduledContextRecord and associated meta data.
     */
    SearchResults<ScheduledContextRecord> findByFilter(ScheduledContextSearchFilter filter, int limit, int offset, String sortColumn, String sortOrder);


    /**
     * Find ScheduledContextRecord by name
     *
     * @param name
     *
     * @return matching ScheduledContextRecord or null if not found
     */
    ScheduledContextRecord findByName(String name);

    /**
     * Save a ScheduledContextRecordß
     *
     * @param scheduledContextRecord
     */
    void save(ScheduledContextRecord scheduledContextRecord);

    /**
     * Helper method to delete a context.
     *
     * @param contextName
     */
    void deleteContext(String contextName);
}
