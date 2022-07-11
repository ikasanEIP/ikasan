package org.ikasan.spec.scheduled.profile.dao;

import org.ikasan.spec.scheduled.profile.model.ContextProfileRecord;
import org.ikasan.spec.scheduled.profile.model.ContextProfileSearchFilter;
import org.ikasan.spec.search.SearchResults;

public interface ContextProfileDao {

    /**
     * Save a context profile record.
     *
     * @param contextProfileRecord
     */
    void save(ContextProfileRecord contextProfileRecord);

    /**
     * Delete context profiles by context names.
     *
     * @param contextName
     */
    void deleteByContextName(String contextName);

    /**
     * Find by id.
     *
     * @param id
     * @return
     */
    ContextProfileRecord findById(String id);


    /**
     * Find by filter
     *
     * @param contextProfileSearchFilter
     * @param limit
     * @param offset
     * @param sortColumn
     * @param sortOrder
     * @return
     */
    SearchResults<ContextProfileRecord> findByFilter(ContextProfileSearchFilter contextProfileSearchFilter, int limit, int offset, String sortColumn, String sortOrder);
}
