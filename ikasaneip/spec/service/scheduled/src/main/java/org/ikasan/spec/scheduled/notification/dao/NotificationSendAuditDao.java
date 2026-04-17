package org.ikasan.spec.scheduled.notification.dao;

public interface NotificationSendAuditDao<T> {

    /**
     * Finds a record based on the specified contextual information.
     *
     * @param contextInstanceId the unique identifier of the instance within the context
     * @param contextName the name of the context associated with the record
     * @param jobName the name of the job associated with the record
     * @param monitorType the type of monitor associated with the record
     * @param notifierType the type of notifier associated with the record
     * @return the record of type T matching the specified criteria, or null if no match is found
     */
    T find(String contextInstanceId, String contextName, String jobName, String monitorType, String notifierType);

    /**
     * Persists the given entity instance to the underlying data store.
     *
     * @param var1 The entity instance to be saved.
     */
    void save(T var1);
}