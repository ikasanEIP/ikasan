package org.ikasan.spec.scheduled.notification.dao;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetailsRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface EmailNotificationDetailsDao {

    /**
     * Retrieves all {@link EmailNotificationDetailsRecord} entries, with pagination support.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the starting position in the result set from which to retrieve records
     * @return a {@link SearchResults} object containing a list of {@link EmailNotificationDetailsRecord} entries
     *         and associated metadata
     */
    SearchResults<EmailNotificationDetailsRecord> findAll(int limit, int offset);

    /**
     * Retrieves a paginated list of EmailNotificationDetailsRecord objects filtered by the specified context name.
     *
     * @param contextName the name of the context to filter the records by
     * @param limit the maximum number of records to return
     * @param offset the starting position in the result set
     * @return a SearchResults object containing the filtered EmailNotificationDetailsRecord objects and associated metadata
     */
    SearchResults<EmailNotificationDetailsRecord> findByContextName(String contextName, int limit, int offset);

    /**
     * Retrieves an {@link EmailNotificationDetailsRecord} based on the specified job name, child context name, and monitor type.
     *
     * @param jobName the name of the job associated with the desired email notification details
     * @param childContextName the name of the child context associated with the job
     * @param monitorType the type of monitor associated with the job
     * @return the {@link EmailNotificationDetailsRecord} corresponding to the specified job name, child context name, and monitor type,
     *         or null if no matching record is found
     */
    EmailNotificationDetailsRecord findByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);

    /**
     * Persists the given {@link EmailNotificationDetailsRecord} instance to the underlying data store.
     *
     * @param var1 The {@link EmailNotificationDetailsRecord} to be saved.
     */
    void save(EmailNotificationDetailsRecord var1);

    /**
     * Persists a list of {@link EmailNotificationDetailsRecord} objects into the underlying data store.
     *
     * @param var1 the list of {@link EmailNotificationDetailsRecord} objects to be saved
     */
    void save(List<EmailNotificationDetailsRecord> var1);

    /**
     * Deletes all records associated with the specified context name from the underlying data store.
     *
     * @param contextName the name of the context whose associated records are to be deleted
     */
    void deleteByContextName(String contextName);

    /**
     * Deletes an email notification details record based on the specified job name, child context name,
     * and monitor type from the underlying data store.
     *
     * @param jobName the name of the job associated with the email notification details to be deleted
     * @param childContextName the name of the child context associated with the email notification details to be deleted
     * @param monitorType the type of monitor associated with the email notification details to be deleted
     */
    void deleteByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);
}