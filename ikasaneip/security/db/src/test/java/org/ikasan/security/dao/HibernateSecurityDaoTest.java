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

package org.ikasan.security.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;
import org.ikasan.security.SecurityConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


/**
 * Unit test for {@link HibernateSecurityDao}
 * 
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@SpringJUnitConfig(classes = {SecurityConfiguration.class, TestImportConfig.class})
class HibernateSecurityDaoTest
{
    /** Object being tested */
    @Resource private SecurityDao xaSecurityDao;

    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @BeforeEach
    void setup()
    {
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setName("role" + i);

            for(int j=0; j<10; j++)
            {
                Policy policy = new Policy();
                policy.setName("policy" + j + i);
                policy.setDescription("description");
                this.xaSecurityDao.saveOrUpdatePolicy(policy);
                policies.add(policy);
            }

            role.setPolicies(policies);
            role.setDescription("description");
            this.xaSecurityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<Policy>();
        }

    	IkasanPrincipal principal = new IkasanPrincipal();
    	principal.setName("stewmi");
    	principal.setType("type");
    	principal.setRoles(roles);
    	principal.setDescription("description");

    	this.xaSecurityDao.saveOrUpdatePrincipal(principal);

    	principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal1");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal2");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal3");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal4");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal5");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal6");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");
        principal.setDescription("description");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        PolicyLinkType policyLinkType = new PolicyLinkType("name1", "table1");
        
        this.xaSecurityDao.saveOrUpdatePolicyLinkType(policyLinkType);
        
        policyLinkType = new PolicyLinkType("name2", "table2");
        
        this.xaSecurityDao.saveOrUpdatePolicyLinkType(policyLinkType);
        
        policyLinkType = new PolicyLinkType("name3", "table3");
        
        this.xaSecurityDao.saveOrUpdatePolicyLinkType(policyLinkType);
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());
    }

    @Test
        @DirtiesContext
    void test_success_get_policy_by_name()
    {
        Policy policy = this.xaSecurityDao.getPolicyByName("policy11");

        assertNotNull(policy);

        assertEquals("policy11", policy.getName());
    }

    @Test
        @DirtiesContext
    void test_success_get_role_by_name()
    {
        Role role = this.xaSecurityDao.getRoleByName("role1");

        assertNotNull(role);

        assertEquals("role1", role.getName());
    }

    @Test
        @DirtiesContext
    void test_success_add_role()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());

        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<Policy>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }


        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);
        policies = new HashSet<Policy>();

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");
        
        assertNotNull(principal);

        assertEquals(11, principal.getRoles().size());
    }

    @Test
        @DirtiesContext
    void test_success_add_role_wth_role_module()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());

        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<Policy>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }


        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);

        RoleModule roleModule = new RoleModule();
        roleModule.setModuleName("moduleName");
        roleModule.setRole(role);
        this.xaSecurityDao.saveRoleModule(roleModule);

        role.addRoleModule(roleModule);
        this.xaSecurityDao.saveOrUpdateRole(role);

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(11, principal.getRoles().size());

        Role foundRole = principal.getRoles().stream().filter(role1 -> role1.getName().equals("role_new")).findFirst().get();

        RoleModule foundRoleModule = foundRole.getRoleModules().stream().findFirst().get();

        assertEquals(roleModule.getModuleName(), foundRoleModule.getModuleName(), "found role module equals");
    }

    @Test
        @DirtiesContext
    void test_success_add_role_wth_role_job_plan()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());

        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<Policy>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }


        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);

        RoleJobPlan roleJobPlan = new RoleJobPlan();
        roleJobPlan.setJobPlanName("jobPlan");
        roleJobPlan.setRole(role);
        this.xaSecurityDao.saveRoleJobPlan(roleJobPlan);

        role.addRoleJobPlan(roleJobPlan);
        this.xaSecurityDao.saveOrUpdateRole(role);

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(11, principal.getRoles().size());

        Role foundRole = principal.getRoles().stream().filter(role1 -> role1.getName().equals("role_new")).findFirst().get();

        RoleJobPlan foundRoleJobPlan = foundRole.getRoleJobPlans().stream().findFirst().get();

        assertEquals(roleJobPlan.getJobPlanName(), foundRoleJobPlan.getJobPlanName(), "found role module equals");
    }

    @Test
        @DirtiesContext
    void test_success_remove_role()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());

        Role role = new Role();
        role.setName("role_new");

        HashSet<Policy> policies = new HashSet<Policy>();

        principal.getRoles().clear();

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(0, principal.getRoles().size());
    }

    @Test
        @DirtiesContext
    void test_exception_principal_no_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            HashSet<Role> roles = new HashSet<Role>();
            HashSet<Policy> policies = new HashSet<Policy>();

            for (int i = 0; i < 10; i++)
            {
                Role role = new Role();
                role.setName("role" + i);

                for (int j = 0; j < 10; j++)
                {
                    Policy policy = new Policy();
                    policy.setName("policy" + j + i);
                    this.xaSecurityDao.saveOrUpdatePolicy(policy);
                    policies.add(policy);
                }

                role.setPolicies(policies);
                this.xaSecurityDao.saveOrUpdateRole(role);
                roles.add(role);
                policies = new HashSet<Policy>();
            }

            IkasanPrincipal principal = new IkasanPrincipal();
            principal.setType("type");
            principal.setRoles(roles);

            this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_exception_principal_duplicate_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            IkasanPrincipal principal = new IkasanPrincipal();
            principal.setName("anotherPrincipal7");
            principal.setType("type");

            this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_principals()
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_delete_principal()
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        assertEquals(8, principals.size());

        this.xaSecurityDao.deletePrincipal(principals.get(0));

        principals = this.xaSecurityDao.getAllPrincipals();

        assertEquals(7, principals.size());
    }

    @Test
        @DirtiesContext
    void test_exception_role_no_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            HashSet<Role> roles = new HashSet<Role>();
            HashSet<Policy> policies = new HashSet<Policy>();

            for (int i = 0; i < 10; i++)
            {
                Role role = new Role();

                for (int j = 0; j < 10; j++)
                {
                    Policy policy = new Policy();
                    policy.setName("policy" + j + i);
                    this.xaSecurityDao.saveOrUpdatePolicy(policy);
                    policies.add(policy);
                }

                role.setPolicies(policies);
                this.xaSecurityDao.saveOrUpdateRole(role);
                roles.add(role);
                policies = new HashSet<Policy>();
            }

            IkasanPrincipal principal = new IkasanPrincipal();
            principal.setType("type");
            principal.setRoles(roles);

            this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_roles()
    {
        List<Role> roles = this.xaSecurityDao.getAllRoles();

        assertEquals(10, roles.size());
    }

    @Test
        @DirtiesContext
    void test_delete_role()
    {
        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<Policy>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }

        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);

        List<Role> roles = this.xaSecurityDao.getAllRoles();

        assertEquals(11, roles.size());

        this.xaSecurityDao.deleteRole(role);

        roles = this.xaSecurityDao.getAllRoles();

        assertEquals(10, roles.size());
    }

    @Test
        @DirtiesContext
    void test_exception_principal_policy_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            Policy policy = new Policy();
            policy.setName("policy11");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
        });
    }

    @Test
        @DirtiesContext
    void test_exception_policy_no_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            HashSet<Role> roles = new HashSet<Role>();
            HashSet<Policy> policies = new HashSet<Policy>();

            for (int i = 0; i < 10; i++)
            {
                Role role = new Role();
                role.setName("name");
                role.setDescription("description");

                for (int j = 0; j < 10; j++)
                {
                    Policy policy = new Policy();
                    policy.setName("blah");
                    policy.setDescription("description");
                    this.xaSecurityDao.saveOrUpdatePolicy(policy);
                    policies.add(policy);
                }

                role.setPolicies(policies);
                this.xaSecurityDao.saveOrUpdateRole(role);
                roles.add(role);
                policies = new HashSet<Policy>();
            }

            IkasanPrincipal principal = new IkasanPrincipal();
            principal.setType("type");
            principal.setRoles(roles);

            this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_exception_role_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            Role role = new Role();
            role.setName("role1");
            role.setDescription("description");

            this.xaSecurityDao.saveOrUpdateRole(role);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_policies()
    {
        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_delete_policy()
    {
        Policy policy = new Policy();
        policy.setName("blah");
        policy.setDescription("description");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);

        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        assertEquals(101, policies.size());

        this.xaSecurityDao.deletePolicy(policy);

        policies = this.xaSecurityDao.getAllPolicies();

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_with_role()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipalsWithRole("role0");

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_with_role_bad_role_name()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipalsWithRole("bad name");

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_names()
    {
    	ArrayList<String> names = new ArrayList<String>();
    	names.add("role0");
    	names.add("role1");
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_names_empty_names()
    {
    	ArrayList<String> names = new ArrayList<String>();

    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_names_bad_names()
    {
    	ArrayList<String> names = new ArrayList<String>();
    	names.add("bad name 1");
    	names.add("bad name 2");

    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name_like()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalByNameLike("anotherPrincipal");

        assertEquals(7, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name_like_bad_name()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalByNameLike("bad name");

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_all_policy_link_types()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityDao.getAllPolicyLinkTypes();

        assertEquals(3, plts.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_role_by_name_like()
    {    	
    	List<Role> roles = this.xaSecurityDao.getRoleByNameLike("role");

        assertEquals(10, roles.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_policy_by_name_like()
    {    	
    	List<Policy> policies = this.xaSecurityDao.getPolicyByNameLike("policy");

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_success_save_policy_link()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityDao.getAllPolicyLinkTypes();
    	PolicyLink policyLink = new PolicyLink(plts.get(0),Long.valueOf(1), "name");
    	
    	this.xaSecurityDao.saveOrUpdatePolicyLink(policyLink);
    	
    	assertNotNull(policyLink.getId());
    	
    	this.xaSecurityDao.deletePolicyLink(policyLink);
    }

    @Test
        @DirtiesContext
    void test_success_get_policies_by_role()
    {    	
    	List<Policy> policies = this.xaSecurityDao.getAllPoliciesWithRole("role1");

        assertEquals(10, policies.size());
    }
}
