package org.ikasan.spec.scheduled.notification.service;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetails;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetailsRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface EmailNotificationDetailsService {

    /**
     * Retrieves a paginated list of email notification details records.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the starting position in the result set to begin retrieving records
     * @return a {@code SearchResults} object containing the list of {@code EmailNotificationDetailsRecord}
     *         and additional metadata, such as total number of results and query response time
     */
    SearchResults<EmailNotificationDetailsRecord> findAll(int limit, int offset);

    /**
     * Retrieves a paginated list of email notification details records filtered by the specified context name.
     *
     * @param contextName the name of the context to filter records. It must be a non-null, non-empty string.
     * @param limit the maximum number of records to return. Must be a positive integer.
     * @param offset the starting position of the records to retrieve. Must be a non-negative integer.
     * @return a {@code SearchResults<EmailNotificationDetailsRecord>} object containing a list of matching
     *         email notification details records, the total number of results, and the query response time.
     */
    SearchResults<EmailNotificationDetailsRecord> findByContextName(String contextName, int limit, int offset);

    /**
     * Finds an {@code EmailNotificationDetailsRecord} based on the provided job name,
     * child context name, and monitor type.
     *
     * @param jobName the name of the job to search for; must be a non-null, non-empty string.
     * @param childContextName the name of the child context associated with the job;
     *                         can be null or empty if not applicable.
     * @param monitorType the type of monitor related to the email notification details;
     *                    must be a non-null, non-empty string.
     * @return an instance of {@code EmailNotificationDetailsRecord} if a matching record is found,
     *         or {@code null} if no record matches the given parameters.
     */
    EmailNotificationDetailsRecord findByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);

    /**
     * Persists the given email notification details record into the associated data store.
     *
     * @param var1 An instance of {@link EmailNotificationDetailsRecord} containing details
     *             such as job name, context name, monitor type, and other relevant attributes
     *             required for the persistence of the email notification.
     */
    void save(EmailNotificationDetailsRecord var1);

    /**
     * Persists a list of {@link EmailNotificationDetailsRecord} objects to the data store.
     *
     * @param var1 the list of {@code EmailNotificationDetailsRecord} instances to be saved.
     *             Each object in the list should contain all relevant details that need
     *             to be stored, including job name, context name, monitor type, and associated
     *             email notification details.
     */
    void save(List<EmailNotificationDetailsRecord> var1);

    /**
     * Persists a list of email notification details into the system.
     *
     * @param emailNotificationDetails a list of {@code EmailNotificationDetails} objects
     *                                 containing information about email notifications,
     *                                 including job name, context name, child context name,
     *                                 monitor type, recipients, subject, body, and other
     *                                 related attributes. Each object in the list must
     *                                 contain valid data to ensure successful persistence.
     */
    void saveEmailNotificationDetails(List<EmailNotificationDetails> emailNotificationDetails);

    /**
     * Deletes all email notification details associated with the specified context name.
     *
     * @param contextName the name of the context for which the associated email notification details
     *                    should be deleted; must not be null or empty
     */
    void deleteByContextName(String contextName);

    /**
     * Deletes email notification details based on the specified job name and monitor type.
     *
     * @param jobName the name of the job associated with the email notification details to delete;
     *                must not be null or empty
     * @param childContextName the name of the child context linked to the job; must not be null or empty
     * @param monitorType the type of monitor associated with the job; must not be null or empty
     */
    void deleteByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);
}