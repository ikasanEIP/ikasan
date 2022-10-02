package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ScheduledContextRecord extends Serializable {

    /**
     * Get the id of the record.
     *
     * @return
     */
    String getId();

    /**
     * Get the context template name.
     *
     * @return
     */
    String getContextName();

    /**
     * Set the context template name.
     *
     * @param contextName
     */
    void setContextName(String contextName);

    /**
     * Get the context template.
     *
     * @return
     */
    ContextTemplate getContext();

    /**
     * Set the context template.
     *
     * @param context
     */
    void setContext(ContextTemplate context);

    /**
     * Get the created timestamp of the context template.
     *
     * @return
     */
    long getTimestamp();

    /**
     * Set the created timestamp of the context template.
     * @param timestamp
     */
    void setTimestamp(long timestamp);

    /**
     * Get the last modified timestamp of the context template.
     *
     * @return
     */
    long getModifiedTimestamp();

    /**
     * Set the last modified timestamp of the context template.
     *
     * @param timestamp
     */
    void setModifiedTimestamp(long timestamp);

    /**
     * Get the username of the user who last modified the context template.
     *
     * @return
     */
    String getModifiedBy();

    /**
     * Set the username of the user who last modified the context template.
     *
     * @param modifiedBy
     */
    void setModifiedBy(String modifiedBy);

    /**
     * Determine if the context template is disabled.
     *
     * @return
     */
    boolean isDisabled();
}
