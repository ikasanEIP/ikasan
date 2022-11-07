package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetails;
import org.ikasan.spec.scheduled.profile.model.ContextProfileRecord;

import java.util.List;

public interface ContextBundle {

    ContextTemplate getContextTemplate();

    List<SchedulerJob> getSchedulerJobs();

    List<ContextProfileRecord> getContextProfiles();

    List<EmailNotificationDetails> getEmailNotificationDetails();
}
