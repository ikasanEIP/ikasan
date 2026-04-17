package org.ikasan.spec.scheduled.notification.service;

public interface NotificationSendAuditService<T> {

    /**
     * Finds and retrieves an instance of type {@code T} based on the specified parameters.
     * This method is typically used to locate a specific notification or audit record
     * matching the provided context and job details.
     *
     * @param contextInstanceId the unique identifier of the context instance; must not be null or empty
     * @param contextName the name of the context; must not be null or empty
     * @param jobName the name of the job associated with the record; must not be null or empty
     * @param monitorType the type of monitoring related to the record; must not be null or empty
     * @param notifierType the type of notifier used for notifications; must not be null or empty
     * @return an instance of {@code T} if a matching record is found, or {@code null} if no match is found
     */
    T find(String contextInstanceId, String contextName, String jobName, String monitorType, String notifierType);

    /**
     * Persists the provided instance of type {@code T}.
     *
     * @param var1 the instance of type {@code T} to be saved; must not be null
     */
    void save(T var1);
}