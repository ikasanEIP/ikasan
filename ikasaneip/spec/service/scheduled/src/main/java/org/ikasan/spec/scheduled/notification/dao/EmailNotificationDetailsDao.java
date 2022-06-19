package org.ikasan.spec.scheduled.notification.dao;

import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface EmailNotificationDetailsDao<T> {

    SearchResults<T> findAll(int limit, int offset);

    T findByJobNameAndMonitorType(String jobName, String contextName, String monitorType);

    void save(T var1);

    void save(List<T> var1);
}