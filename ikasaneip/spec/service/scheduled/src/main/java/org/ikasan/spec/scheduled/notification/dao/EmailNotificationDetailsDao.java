package org.ikasan.spec.scheduled.notification.dao;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetailsRecord;
import org.ikasan.spec.search.SearchResults;

import java.util.List;

public interface EmailNotificationDetailsDao {

    SearchResults<EmailNotificationDetailsRecord> findAll(int limit, int offset);

    SearchResults<EmailNotificationDetailsRecord> findByContextName(String contextName, int limit, int offset);

    EmailNotificationDetailsRecord findByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);

    void save(EmailNotificationDetailsRecord var1);

    void save(List<EmailNotificationDetailsRecord> var1);

    void deleteByContextName(String contextName);

    void deleteByJobNameAndMonitorType(String jobName, String childContextName, String monitorType);
}