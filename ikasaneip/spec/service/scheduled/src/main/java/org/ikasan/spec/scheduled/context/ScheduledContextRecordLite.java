package org.ikasan.spec.scheduled.context;

import java.io.Serializable;

public interface ScheduledContextRecordLite extends Serializable {

    /**
     * Set the ID for the object.
     *
     * @param id the ID to set for the object
     */
    void setId(String id);

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
     * Retrieves the description of the context template.
     *
     * @return A string representing the description of the item.
     */
    String getDescription();

    /**
     * Sets the description of the context template.
     *
     * @param description the description to be set
     */
    void setDescription(String description);

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

    /**
     * Set the disabled status for the context template.
     *
     * @param disabled true if the context template is to be disabled, false otherwise
     */
    void setDisabled(boolean disabled);

    /**
     * Determine if all quartz schedule driven jobs are disabled for the context.
     *
     * @return
     */
    boolean isQuartzScheduleDrivenJobsDisabledForContext();

    /**
     * Set whether all Quartz schedule driven jobs are disabled for the context.
     *
     * @param disabled true if all Quartz schedule driven jobs for the context are to be disabled, false otherwise
     */
    void setQuartzScheduleDrivenJobsDisabledForContext(boolean disabled);
}
