package org.ikasan.spec.scheduled.notification.service;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationContext;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationContextRecord;
import org.ikasan.spec.search.SearchResults;

public interface EmailNotificationContextService {

    /**
     * Retrieves a paginated list of all email notification context records.
     *
     * @param limit the maximum number of records to retrieve
     * @param offset the offset, indicating the starting point for record retrieval
     * @return a {@code SearchResults<EmailNotificationContextRecord>} instance containing the results,
     *         including the list of records and metadata about the query
     */
    SearchResults<EmailNotificationContextRecord> findAll(int limit, int offset);

    /**
     * Retrieves a paginated list of email notification context records that match the specified context name.
     *
     * @param contextName the name of the context to filter the records; must not be null or empty
     * @param limit the maximum number of records to retrieve; must be a positive integer
     * @param offset the starting position of the records to retrieve; must be a non-negative integer
     * @return a {@code SearchResults} object containing the list of matching {@code EmailNotificationContextRecord}
     *         instances, along with metadata such as the total number of results and query response time
     */
    SearchResults<EmailNotificationContextRecord> findByContextName(String contextName, int limit, int offset);

    /**
     * Persists the given EmailNotificationContextRecord instance.
     *
     * @param var1 the EmailNotificationContextRecord to be saved; must not be null
     */
    void save(EmailNotificationContextRecord var1);

    /**
     * Persists the given email notification context, allowing it to be used for
     * sending email notifications based on the specified configurations.
     *
     * @param emailNotificationContext the email notification context to be saved,
     *                                 containing details such as context name,
     *                                 email addresses for notifications,
     *                                 monitor types, templates for subject and body,
     *                                 and other email-related configurations.
     */
    void saveEmailNotificationContext(EmailNotificationContext emailNotificationContext);

    /**
     * Deletes the email notification context associated with the specified context name.
     *
     * @param contextName the name of the context to be deleted; must not be null or empty
     */
    void deleteByContextName(String contextName);
}
