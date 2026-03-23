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
    Long getId();

    void setId(Long id);

    String getJobPlanName();

    void setJobPlanName(String jobPlanName);

    Role getRole();

    void setRole(Role role);

    Date getCreatedDateTime();

    void setCreatedDateTime(Date createdDateTime);

    Date getUpdatedDateTime();

    void setUpdatedDateTime(Date updatedDateTime);
}
