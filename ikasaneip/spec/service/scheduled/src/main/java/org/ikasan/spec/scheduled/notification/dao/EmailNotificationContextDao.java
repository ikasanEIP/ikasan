package org.ikasan.spec.scheduled.notification.dao;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationContextRecord;
import org.ikasan.spec.search.SearchResults;

public interface EmailNotificationContextDao {

    SearchResults<EmailNotificationContextRecord> findAll(int limit, int offset);

    SearchResults<EmailNotificationContextRecord> findByContextName(String contextName, int limit, int offset);

    void save(EmailNotificationContextRecord var1);

    void deleteByContextName(String contextName);

}
