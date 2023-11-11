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

package org.ikasan.security.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;
import org.ikasan.security.SecurityConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Unit test for {@link SecurityService}
 * 
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@SpringJUnitConfig(classes = {SecurityConfiguration.class, TestImportConfig.class})
class SecurityServiceTest
{

 
    /** Object being tested */
    @Resource private SecurityDao xaSecurityDao;
    @Resource private SecurityService xaSecurityService;


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
    void test_exception_null_dao_on_construction()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new SecurityServiceImpl(null);
        });
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name() 
    {
        IkasanPrincipal principal = this.xaSecurityService.findPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(10, principal.getRoles().size());
    }

    @Test
        @DirtiesContext
    void test_success_create_new_principal() 
    {
        IkasanPrincipal principal = this.xaSecurityService.createNewPrincipal("stewmi2", "type");

        principal = this.xaSecurityService.findPrincipalByName("stewmi2");

        assertNotNull(principal);
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_principal_null_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewPrincipal(null, "type");
        });
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_principal_null_type()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewPrincipal("name", null);
        });
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
        this.xaSecurityService.saveRole(role);

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        assertNotNull(principal);

        assertEquals(11, principal.getRoles().size());
    }

    @Test
        @DirtiesContext
    void test_success_create_new_role() 
    {
        Role role = this.xaSecurityService.createNewRole("testRole", "description");

        assertNotNull(role);
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_role_null_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewRole(null, "description");
        });
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_role_null_description()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewRole("role", null);
        });
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

        principal.getRoles().clear();

        this.xaSecurityService.savePrincipal(principal);

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

            this.xaSecurityService.savePrincipal(principal);
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

            this.xaSecurityService.savePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_principals() 
    {
        List<IkasanPrincipal> principals = this.xaSecurityService.getAllPrincipals();

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_delete_principal()
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        assertEquals(8, principals.size());

        this.xaSecurityService.deletePrincipal(principals.get(0));

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

            this.xaSecurityService.savePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_roles() 
    {
        List<Role> roles = this.xaSecurityService.getAllRoles();

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

        this.xaSecurityService.deleteRole(role);

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
            this.xaSecurityService.savePolicy(policy);
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

                for (int j = 0; j < 10; j++)
                {
                    Policy policy = new Policy();
                    this.xaSecurityService.savePolicy(policy);
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

            this.xaSecurityService.savePrincipal(principal);
        });
    }

    @Test
        @DirtiesContext
    void test_success_create_new_policy() 
    {
        Policy policy = this.xaSecurityService.createNewPolicy("testPolicy", "description");

        assertNotNull(policy);
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_policy_null_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewPolicy(null, "description");
        });
    }

    @Test
        @DirtiesContext
    void test_exception_create_new_policy_null_description()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            this.xaSecurityService.createNewPolicy("role", null);
        });
    }

    @Test
        @DirtiesContext
    void test_exception_role_name()
    {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            Role role = new Role();
            role.setName("role1");

            this.xaSecurityService.saveRole(role);
        });
    }

    @Test
        @DirtiesContext
    void test_get_all_policies() 
    {
        List<Policy> policies = this.xaSecurityService.getAllPolicies();

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_delete_policy() 
    {
        Policy policy = new Policy();
        policy.setName("blah");
        policy.setDescription("description");
        this.xaSecurityService.savePolicy(policy);

        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        assertEquals(101, policies.size());

        this.xaSecurityService.deletePolicy(policy);

        policies = this.xaSecurityDao.getAllPolicies();

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_with_role()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityService.getAllPrincipalsWithRole("role0");

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_with_role_bad_role_name()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityService.getAllPrincipalsWithRole("bad name");

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name_like()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityService.getPrincipalByNameLike("anotherPrincipal");

        assertEquals(7, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_name_like_bad_bname()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityService.getPrincipalByNameLike("bad name");

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_all_policy_link_types()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityService.getAllPolicyLinkTypes();

        assertEquals(3, plts.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_role_by_name_like()
    {    	
    	List<Role> roles = this.xaSecurityService.getRoleByNameLike("role");

        assertEquals(10, roles.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_policy_by_name_like()
    {    	
    	List<Policy> policies = this.xaSecurityService.getPolicyByNameLike("policy");

        assertEquals(100, policies.size());
    }

    @Test
        @DirtiesContext
    void test_success_save_policy_link()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityService.getAllPolicyLinkTypes();
    	PolicyLink policyLink = new PolicyLink(plts.get(0),Long.valueOf(1), "name");
    	
    	this.xaSecurityService.savePolicyLink(policyLink);
    	
    	assertNotNull(policyLink.getId());
    	
    	this.xaSecurityService.deletePolicyLink(policyLink);
    }

    @Test
        @DirtiesContext
    void test_success_get_policies_by_role()
    {    	
    	List<Policy> policies = this.xaSecurityService.getAllPoliciesWithRole("role1");

        assertEquals(10, policies.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_names()
    {
    	ArrayList<String> names = new ArrayList<String>();
    	names.add("role1");
    	names.add("role2");
    	List<IkasanPrincipal> principals = this.xaSecurityService.getPrincipalsByName(names);

        assertEquals(8, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_principal_by_names_empty_names()
    {
    	ArrayList<String> names = new ArrayList<String>();

    	List<IkasanPrincipal> principals = this.xaSecurityService.getPrincipalsByName(names);

        assertEquals(0, principals.size());
    }

    @Test
        @DirtiesContext
    void test_success_get_policy_by_name()
    {
        Policy policy = this.xaSecurityService.findPolicyByName("policy11");

        assertNotNull(policy);

        assertEquals("policy11", policy.getName());
    }

    @Test
        @DirtiesContext
    void test_success_get_role_by_name()
    {
        Role role = this.xaSecurityService.findRoleByName("role1");

        assertNotNull(role);

        assertEquals("role1", role.getName());
    }

    @Test
        @DirtiesContext
    void test_success_add_all_policies_to_role_with_different_policy_()
    {
        Role role = this.xaSecurityService.findRoleByName("role1");

        List<Policy> policies = this.xaSecurityService.getAllPolicies();
        policies.forEach(policy -> role.addPolicy(policy));
        this.xaSecurityService.saveRole(role);

        Role role2 = this.xaSecurityService.findRoleByName("role2");

        policies.forEach(policy -> role2.addPolicy(policy));
        this.xaSecurityService.saveRole(role2);

        // load the policies again so that there are 2 objects
        // in the session that reference the same row in the DB.
        List<Policy> policies2 = this.xaSecurityService.getAllPolicies();
        policies2.forEach(policy -> role.addPolicy(policy));

        this.xaSecurityService.saveRole(role);
    }

    @Test
        @DirtiesContext
    void test_success_confirm_deleting_role_does_not_delete_policies()
    {
        Role role = this.xaSecurityService.findRoleByName("role1");

        List<Policy> policies = this.xaSecurityService.getAllPolicies();
        policies.forEach(policy -> role.addPolicy(policy));
        this.xaSecurityService.saveRole(role);

        List<IkasanPrincipal> principals = this.xaSecurityService.getAllPrincipalsWithRole(role.getName());

        principals.forEach(principal -> {
            principal.getRoles().remove(role);
            this.xaSecurityService.savePrincipal(principal);
        });

        this.xaSecurityService.deleteRole(role);

        List<Policy> policies2 = this.xaSecurityService.getAllPolicies();

        // Make sure deleting roles does not delete policies too.
        assertEquals(policies.size(), policies2.size());
    }

}
