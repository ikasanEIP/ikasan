package org.ikasan.spec.scheduled.profile.service;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.profile.model.ContextProfileRecord;
import org.ikasan.spec.scheduled.profile.model.ContextProfileSearchFilter;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface ContextProfileService {

    /**
     * Save a context profile record.
     *
     * @param contextProfileRecord
     */
    void save(ContextProfileRecord contextProfileRecord);

    /**
     * Save a list of context profiles
     *
     * @param records
     */
    void save(List<ContextProfileRecord> records);

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
