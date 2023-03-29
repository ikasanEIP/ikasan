package org.ikasan.spec.systemevent;

import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface SystemEventSearchDao {

    /**
     * Find a system event by its id.
     *
     * @param id
     * @return
     */
    SystemEvent findById(String id);

    /**
     * Find system events that conform to the search filter.
     *
     * @param searchFilter
     * @param limit
     * @param offset
     * @param sortColumn
     * @param sortOrder
     * @return
     */
    SearchResults<SystemEvent> findByFilter(SystemEventSearchFilter searchFilter, int limit, int offset
        , String sortColumn, String sortOrder);
}
