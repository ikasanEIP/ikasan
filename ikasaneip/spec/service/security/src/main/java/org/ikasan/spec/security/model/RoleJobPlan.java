package org.ikasan.spec.security.model;

import java.util.Date;

/**
 * Represents an association between a {@link Role} and a job plan.
 *
 * <p>Defines the mapping that grants a role access to a specific job plan within the Ikasan
 * scheduler framework. This association allows fine-grained control over which roles can view,
 * execute, or manage particular scheduled job plans.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface RoleJobPlan
{
    /**
     * Retrieves the unique identifier of this role-job plan association.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Sets the unique identifier of this role-job plan association.
     *
     * @param id the unique identifier to set
     */
    void setId(Object id);

    /**
     * Retrieves the name of the job plan associated with this role.
     *
     * @return the job plan name, or {@code null} if not set
     */
    String getJobPlanName();

    /**
     * Sets the name of the job plan associated with this role.
     *
     * @param jobPlanName the job plan name to set
     */
    void setJobPlanName(String jobPlanName);

    /**
     * Retrieves the role associated with the job plan.
     *
     * @return the {@link Role} instance, or {@code null} if not set
     */
    Role getRole();

    /**
     * Sets the role associated with the job plan.
     *
     * @param role the {@link Role} to set
     */
    void setRole(Role role);

    /**
     * Retrieves the date and time when this role-job plan association was created.
     *
     * @return the creation timestamp, or {@code null} if not set
     */
    Date getCreatedDateTime();

    /**
     * Sets the date and time when this role-job plan association was created.
     *
     * @param createdDateTime the creation timestamp to set
     */
    void setCreatedDateTime(Date createdDateTime);

    /**
     * Retrieves the date and time when this role-job plan association was last updated.
     *
     * @return the last update timestamp, or {@code null} if not set
     */
    Date getUpdatedDateTime();

    /**
     * Sets the date and time when this role-job plan association was last updated.
     *
     * @param updatedDateTime the last update timestamp to set
     */
    void setUpdatedDateTime(Date updatedDateTime);
}
