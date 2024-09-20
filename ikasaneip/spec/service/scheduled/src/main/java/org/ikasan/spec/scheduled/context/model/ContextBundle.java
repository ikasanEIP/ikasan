package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationContext;
import org.ikasan.spec.scheduled.notification.model.EmailNotificationDetails;
import org.ikasan.spec.scheduled.profile.model.ContextProfileRecord;

import java.util.List;

public interface ContextBundle {

    /**
     * Retrieves the ContextTemplate of the initial symbol.
     *
     * @return the ContextTemplate of the initial symbol
     */
    ContextTemplate getContextTemplate();

    /**
     * Retrieves the roles associated with the current context bundle.
     *
     * @return a list of roles as strings
     */
    List<String> getRoles();

    /**
     * Retrieves a list of SchedulerJob instances.
     * <p>
     * This method returns a list of SchedulerJob instances, which represent individual scheduled jobs.
     * </p>
     *
     * @return a list of {@link SchedulerJob} instances
     */
    List<SchedulerJob> getSchedulerJobs();

    /**
     * Retrieves the list of context profiles.
     *
     * @return a list of ContextProfileRecord
     */
    List<ContextProfileRecord> getContextProfiles();

    /**
     * Returns a list of EmailNotificationDetails.
     *
     * @return a list of EmailNotificationDetails
     */
    List<EmailNotificationDetails> getEmailNotificationDetails();

    /**
     * Retrieves the EmailNotificationContext object.
     *
     * @return The EmailNotificationContext object.
     */
    EmailNotificationContext getEmailNotificationContext();
}
