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
package org.ikasan.security.service.model;


import org.ikasan.spec.security.model.Policy;
import org.ikasan.spec.security.model.Role;
import org.ikasan.spec.security.model.RoleJobPlan;
import org.ikasan.spec.security.model.RoleModule;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Test implementation of Role for use in test scenarios.
 *
 * @author Ikasan Development Team
 */
public class RoleImpl implements Role
{
    private Long id;
    private String name;
    private String description;
    private Date createdDateTime;
    private Date updatedDateTime;
    private Set<Policy> policies = new HashSet<>();
    private Set<RoleModule> roleModules = new HashSet<>();
    private Set<RoleJobPlan> roleJobPlans = new HashSet<>();

    @Override
    public void addPolicy(Policy policy)
    {
        if (this.policies == null)
        {
            this.policies = new HashSet<>();
        }
        this.policies.add(policy);
    }

    @Override
    public void addRoleModule(RoleModule roleModule)
    {
        if (this.roleModules == null)
        {
            this.roleModules = new HashSet<>();
        }
        this.roleModules.add(roleModule);
    }

    @Override
    public void addRoleJobPlan(RoleJobPlan roleJobPlan)
    {
        if (this.roleJobPlans == null)
        {
            this.roleJobPlans = new HashSet<>();
        }
        this.roleJobPlans.add(roleJobPlan);
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public Date getCreatedDateTime()
    {
        return createdDateTime;
    }

    @Override
    public void setCreatedDateTime(Date createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    @Override
    public Date getUpdatedDateTime()
    {
        return updatedDateTime;
    }

    @Override
    public void setUpdatedDateTime(Date updatedDateTime)
    {
        this.updatedDateTime = updatedDateTime;
    }

    @Override
    public Set<Policy> getPolicies()
    {
        return policies;
    }

    @Override
    public void setPolicies(Set<Policy> policies)
    {
        this.policies = policies;
    }

    @Override
    public Set<RoleModule> getRoleModules()
    {
        return roleModules;
    }

    @Override
    public void setRoleModules(Set<RoleModule> roleModules)
    {
        this.roleModules = roleModules;
    }

    @Override
    public Set<RoleJobPlan> getRoleJobPlans()
    {
        return roleJobPlans;
    }

    @Override
    public void setRoleJobPlans(Set<RoleJobPlan> roleJobPlans)
    {
        this.roleJobPlans = roleJobPlans;
    }

    @Override
    public int compareTo(Role role)
    {
        if (this.name == null && role.getName() == null)
        {
            return 0;
        }
        if (this.name == null)
        {
            return -1;
        }
        if (role.getName() == null)
        {
            return 1;
        }
        return this.name.compareTo(role.getName());
    }
}
