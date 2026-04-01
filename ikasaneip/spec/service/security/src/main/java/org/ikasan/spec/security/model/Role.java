/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.spec.security.model;

import java.util.*;

/**
 * Represents a security role in the Ikasan security framework.
 *
 * <p>A role is a collection of {@link Policy} permissions that can be assigned to {@link IkasanPrincipal}s
 * to grant access to specific functionality. Roles can also be associated with integration modules
 * ({@link RoleModule}) and job plans ({@link RoleJobPlan}) to provide fine-grained access control
 * over these resources.
 *
 * @author CMI2 Development Team
 * @since 1.0
 */
public interface Role extends Comparable<Role>
{
    /**
     * Adds a policy to this role's collection of policies.
     *
     * @param policy the {@link Policy} to add, must not be {@code null}
     */
    void addPolicy(Policy policy);

    /**
     * Adds a role-module association to this role.
     *
     * <p>This grants the role access to the specified integration module.
     *
     * @param roleModule the {@link RoleModule} association to add, must not be {@code null}
     */
    void addRoleModule(RoleModule roleModule);

    /**
     * Adds a role-job plan association to this role.
     *
     * <p>This grants the role access to the specified job plan.
     *
     * @param roleJobPlan the {@link RoleJobPlan} association to add, must not be {@code null}
     */
    void addRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Retrieves the unique identifier of this role.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Sets the unique identifier of this role.
     *
     * @param id the unique identifier to set
     */
    void setId(Object id);

    /**
     * Retrieves the name of this role.
     *
     * @return the role name, never {@code null}
     */
    String getName();

    /**
     * Sets the name of this role.
     *
     * @param name the name to set, must not be {@code null}
     */
    void setName(String name);

    /**
     * Retrieves the human-readable description of this role.
     *
     * @return the description, or {@code null} if not set
     */
    String getDescription();

    /**
     * Sets the human-readable description of this role.
     *
     * @param description the description to set, may be {@code null}
     */
    void setDescription(String description);

    /**
     * Retrieves the date and time when this role was created.
     *
     * @return the creation timestamp, or {@code null} if not set
     */
    Date getCreatedDateTime();

    /**
     * Sets the date and time when this role was created.
     *
     * @param createdDateTime the creation timestamp to set
     */
    void setCreatedDateTime(Date createdDateTime);

    /**
     * Retrieves the date and time when this role was last updated.
     *
     * @return the last update timestamp, or {@code null} if not set
     */
    Date getUpdatedDateTime();

    /**
     * Sets the date and time when this role was last updated.
     *
     * @param updatedDateTime the last update timestamp to set
     */
    void setUpdatedDateTime(Date updatedDateTime);

    /**
     * Retrieves the set of policies assigned to this role.
     *
     * @return a set of {@link Policy} instances, never {@code null}
     */
    Set<Policy> getPolicies();

    /**
     * Sets the policies assigned to this role.
     *
     * @param policies the set of {@link Policy} instances to assign, must not be {@code null}
     */
    void setPolicies(Set<Policy> policies);

    /**
     * Retrieves the set of module associations for this role.
     *
     * @return a set of {@link RoleModule} associations defining which modules this role can access, never {@code null}
     */
    Set<RoleModule> getRoleModules();

    /**
     * Sets the module associations for this role.
     *
     * @param roleModules the set of {@link RoleModule} associations to assign, must not be {@code null}
     */
    void setRoleModules(Set<RoleModule> roleModules);

    /**
     * Retrieves the set of job plan associations for this role.
     *
     * @return a set of {@link RoleJobPlan} associations defining which job plans this role can access, never {@code null}
     */
    Set<RoleJobPlan> getRoleJobPlans();

    /**
     * Sets the job plan associations for this role.
     *
     * @param roleJobPlans the set of {@link RoleJobPlan} associations to assign, must not be {@code null}
     */
    void setRoleJobPlans(Set<RoleJobPlan> roleJobPlans);
}
