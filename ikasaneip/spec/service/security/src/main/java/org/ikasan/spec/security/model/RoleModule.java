package org.ikasan.spec.security.model;

import java.util.Date;

/**
 * Represents an association between a {@link Role} and an integration module.
 *
 * <p>Defines the mapping that grants a role access to a specific integration module within the
 * Ikasan platform. This association allows fine-grained control over which roles can view,
 * manage, or operate particular integration modules and their flows.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface RoleModule
{
    Long getId();

    void setId(Long id);

    String getModuleName();

    void setModuleName(String moduleName);

    Role getRole();

    void setRole(Role role);

    Date getCreatedDateTime();

    void setCreatedDateTime(Date createdDateTime);

    Date getUpdatedDateTime();

    void setUpdatedDateTime(Date updatedDateTime);
}
