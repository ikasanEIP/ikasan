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
    void addPolicy(Policy policy);

    void addRoleModule(RoleModule roleModule);

    void addRoleJobPlan(RoleJobPlan roleJobPlan);

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Date getCreatedDateTime();

    void setCreatedDateTime(Date createdDateTime);

    Date getUpdatedDateTime();

    void setUpdatedDateTime(Date updatedDateTime);

    Set<Policy> getPolicies();

    void setPolicies(Set<Policy> policies);

    Set<RoleModule> getRoleModules();

    void setRoleModules(Set<RoleModule> roleModules);

    Set<RoleJobPlan> getRoleJobPlans();

    void setRoleJobPlans(Set<RoleJobPlan> roleJobPlans);

    int compareTo(Role role);
}
