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
    /**
     * Retrieves the unique identifier of this role-module association.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Sets the unique identifier of this role-module association.
     *
     * @param id the unique identifier to set
     */
    void setId(Object id);

    /**
     * Retrieves the name of the integration module associated with this role.
     *
     * @return the module name, or {@code null} if not set
     */
    String getModuleName();

    /**
     * Sets the name of the integration module associated with this role.
     *
     * @param moduleName the module name to set
     */
    void setModuleName(String moduleName);

    /**
     * Retrieves the role associated with the integration module.
     *
     * @return the {@link Role} instance, or {@code null} if not set
     */
    Role getRole();

    /**
     * Sets the role associated with the integration module.
     *
     * @param role the {@link Role} to set
     */
    void setRole(Role role);

    /**
     * Retrieves the date and time when this role-module association was created.
     *
     * @return the creation timestamp, or {@code null} if not set
     */
    Date getCreatedDateTime();

    /**
     * Sets the date and time when this role-module association was created.
     *
     * @param createdDateTime the creation timestamp to set
     */
    void setCreatedDateTime(Date createdDateTime);

    /**
     * Retrieves the date and time when this role-module association was last updated.
     *
     * @return the last update timestamp, or {@code null} if not set
     */
    Date getUpdatedDateTime();

    /**
     * Sets the date and time when this role-module association was last updated.
     *
     * @param updatedDateTime the last update timestamp to set
     */
    void setUpdatedDateTime(Date updatedDateTime);
}
