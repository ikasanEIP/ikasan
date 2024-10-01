package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.io.Serializable;

public interface InternalEventDrivenJobInstance extends SchedulerJobInstance, InternalEventDrivenJob, Serializable {

    /**
     * Sets the error acknowledged message for the SchedulerJobInstance.
     *
     * @param message The error acknowledged message to set.
     */
    void setErrorAcknowledgedMessage(String message);

    /**
     * Retrieves the error acknowledged message for the job instance.
     *
     * @return The error acknowledged message.
     */
    String getErrorAcknowledgedMessage();

    /**
     * Sets the ticket ID for error acknowledgement.
     *
     * @param ticketId The ID of the ticket for error acknowledgement.
     */
    void setErrorAcknowledgmentTicketId(String ticketId);

    /**
     * Retrieves the error acknowledgment ticket ID associated with the job instance.
     *
     * @return The error acknowledgment ticket ID.
     */
    String getErrorAcknowledgmentTicketId();

    /**
     * Sets the error acknowledge user for the SchedulerJobInstance.
     *
     * @param errorAcknowledgeUser The user who acknowledges the error.
     */
    void setErrorAcknowledgeUser(String errorAcknowledgeUser);

    /**
     * Retrieves the user who acknowledged the error for the SchedulerJobInstance.
     *
     * @return The user who acknowledged the error.
     */
    String getErrorAcknowledgeUser();

    /**
     * Sets the error acknowledge timestamp for the SchedulerJobInstance.
     *
     * @param errorAcknowledgeTimestamp The error acknowledge timestamp to set.
     */
    void setErrorAcknowledgeTimestamp(long errorAcknowledgeTimestamp);

    /**
     * Retrieves the timestamp when the error was acknowledged for the SchedulerJobInstance.
     *
     * @return The timestamp when the error was acknowledged.
     */
    long getErrorAcknowledgeTimestamp();


}
