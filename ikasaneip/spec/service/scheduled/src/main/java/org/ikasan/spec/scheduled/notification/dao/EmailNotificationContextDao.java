package org.ikasan.spec.scheduled.notification.dao;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationContextRecord;
import org.ikasan.spec.search.SearchResults;

public interface EmailNotificationContextDao {

    /**
     * Retrieves all {@link EmailNotificationContextRecord} entries, with pagination support.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the starting position in the result set from which to retrieve records
     * @return a {@link SearchResults} object containing a list of {@link EmailNotificationContextRecord} entries
     *         and associated metadata
     */
    SearchResults<EmailNotificationContextRecord> findAll(int limit, int offset);

    /**
     * Retrieves a paginated list of EmailNotificationContextRecord objects filtered by a context name.
     *
     * @param contextName the name of the context to filter the records by
     * @param limit the maximum number of records to return
     * @param offset the starting position in the result set
     * @return the search results containing the filtered EmailNotificationContextRecord objects
     */
    SearchResults<EmailNotificationContextRecord> findByContextName(String contextName, int limit, int offset);

    /**
     * Persists the given EmailNotificationContextRecord instance to the underlying data store.
     *
     * @param var1 The EmailNotificationContextRecord to be saved.
     */
    void save(EmailNotificationContextRecord var1);

    /**
     * Deletes all records associated with the specified context name from the underlying data store.
     *
     * @param contextName the name of the context whose associated records are to be deleted
     */
    void deleteByContextName(String contextName);

}
