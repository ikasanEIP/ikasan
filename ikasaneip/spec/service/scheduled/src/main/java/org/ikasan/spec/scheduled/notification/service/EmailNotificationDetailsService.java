package org.ikasan.spec.scheduled.notification.service;

import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface EmailNotificationDetailsService<T> {

    SearchResults<T> findAll(int limit, int offset);

    T findByJobNameAndMonitorType(String jobName, String monitorType);

    void save(T var1);

    void save(List<T> var1);
}