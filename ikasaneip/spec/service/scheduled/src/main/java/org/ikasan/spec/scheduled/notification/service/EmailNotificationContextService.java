package org.ikasan.spec.scheduled.notification.service;

import org.ikasan.spec.scheduled.notification.model.EmailNotificationContext;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationContextRecord;
import org.ikasan.spec.search.SearchResults;

public interface EmailNotificationContextService {

    SearchResults<EmailNotificationContextRecord> findAll(int limit, int offset);

    SearchResults<EmailNotificationContextRecord> findByContextName(String contextName, int limit, int offset);

    void save(EmailNotificationContextRecord var1);

    void saveEmailNotificationContext(EmailNotificationContext emailNotificationContext);

    void deleteByContextName(String contextName);
}
