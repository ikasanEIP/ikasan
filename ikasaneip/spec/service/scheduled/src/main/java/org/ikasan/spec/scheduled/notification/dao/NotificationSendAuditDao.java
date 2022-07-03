package org.ikasan.spec.scheduled.notification.dao;

public interface NotificationSendAuditDao<T> {

    T find(String contextInstanceId, String contextName, String jobName, String monitorType, String notifierType);

    void save(T var1);

}