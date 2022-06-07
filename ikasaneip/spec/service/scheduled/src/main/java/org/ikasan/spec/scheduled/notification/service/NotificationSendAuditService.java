package org.ikasan.spec.scheduled.notification.service;

public interface NotificationSendAuditService<T> {

    T find(String contextInstanceId, String jobName, String monitorType, String notifierType);

    void save(T var1);
}