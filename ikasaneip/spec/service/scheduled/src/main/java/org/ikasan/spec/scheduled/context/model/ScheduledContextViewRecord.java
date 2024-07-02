package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ScheduledContextViewRecord extends Serializable {

    /**
     * Retrieves the identifier of an object.
     *
     * @return The identifier of the object as a String.
     */
    String getId();

    /**
     * Returns the name of the parent context.
     *
     * @return the name of the parent context
     */
    String getParentContextName();

    /**
     * Sets the parent context name of this ScheduledContextViewRecord.
     *
     * @param parentContextName the parent context name to be set
     */
    void setParentContextName(String parentContextName);

    /**
     * Retrieves the name of the context.
     *
     * @return the name of the context
     */
    String getContextName();

    /**
     * Sets the name of the context.
     *
     * @param contextName the name of the context to be set
     */
    void setContextName(String contextName);

    /**
     * Retrieves the context view of the scheduled context view record.
     *
     * @return the context view of the scheduled context view record
     */
    String getContextView();

    /**
     * Sets the context view for the ScheduledContextViewRecord.
     *
     * @param context the new context view to set
     */
    void setContextView(String context);

    /**
     * Retrieves the timestamp representing the current system time.
     *
     * @return A long value representing the current system time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getTimestamp();

    /**
     * Sets the timestamp for the given object.
     *
     * @param timestamp the timestamp to be set
     */
    void setTimestamp(long timestamp);

    /**
     * Retrieves the timestamp when the record was last modified.
     *
     * @return The timestamp when the record was last modified.
     */
    long getModifiedTimestamp();

    /**
     * Sets the modified timestamp for the ScheduledContextViewRecord.
     *
     * @param timestamp The new modified timestamp.
     */
    void setModifiedTimestamp(long timestamp);

    /**
     * Retrieves the username of the person who last modified the record.
     *
     * @return The username of the person who last modified the record.
     */
    String getModifiedBy();

    /**
     * Sets the username of the user who last modified the record.
     *
     * @param modifiedBy the username of the user who last modified the record
     */
    void setModifiedBy(String modifiedBy);
}
