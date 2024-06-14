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

import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.ikasan.security.SecurityAutoConfiguration;
import org.ikasan.security.SecurityTestAutoConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.*;
import org.ikasan.security.service.AuthenticationServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


/**
 * Unit test for {@link HibernateSecurityDao}
 * 
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityAutoConfiguration.class, SecurityTestAutoConfiguration.class})
public class HibernateSecurityDaoTest
{
    /** Object being tested */
    @Autowired
    private SecurityDao xaSecurityDao;


    /**
     * Setup method for the unit test of class HibernateSecurityDaoTest.
     * It initializes the necessary data for the test by creating roles, policies, principals,
     * and policy link types.
     */
    @Before public void setup()
    {
        HashSet<Role> roles = new HashSet<>();
        HashSet<Policy> policies = new HashSet<>();

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
            policies = new HashSet<>();
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
    public void test_success_get_principal_by_name()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);
    }

    @Test
    @DirtiesContext
    public void test_save_find_delete_authentication_method() throws AuthenticationServiceException
    {
        AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setOrder(1L);
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);

        this.xaSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);

        List<AuthenticationMethod> authenticationMethods = this.xaSecurityDao.getAuthenticationMethods();
        Assert.assertEquals(1, authenticationMethods.size());

        authenticationMethods.forEach(authenticationMethod -> {
            authenticationMethod.setOrder(2L);
            this.xaSecurityDao.saveOrUpdateAuthenticationMethod(authenticationMethod);
        });

        authenticationMethods = this.xaSecurityDao.getAuthenticationMethods();
        Assert.assertEquals(1, authenticationMethods.size());

        authenticationMethods.forEach(authenticationMethod -> {
            Assert.assertEquals(2L, authenticationMethod.getOrder().intValue());
            this.xaSecurityDao.deleteAuthenticationMethod(authenticationMethod);
        });

        authenticationMethods = this.xaSecurityDao.getAuthenticationMethods();
        Assert.assertEquals(0, authenticationMethods.size());
    }

    @Test 
    @DirtiesContext
    public void test_success_get_policy_by_name()
    {
        Policy policy = this.xaSecurityDao.getPolicyByName("policy11");

        Assert.assertNotNull(policy);

        Assert.assertEquals(policy.getName(), "policy11");
    }

    @Test 
    @DirtiesContext
    public void test_success_get_role_by_name()
    {
        Role role = this.xaSecurityDao.getRoleByName("role1");

        Assert.assertNotNull(role);

        Assert.assertEquals(role.getName(), "role1");
    }

    @Test 
    @DirtiesContext
    public void test_success_add_role()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

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

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");
        
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);
    }

    @Test
    @DirtiesContext
    public void test_success_add_role_wth_role_module()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

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

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);

        Role foundRole = principal.getRoles().stream().filter(role1 -> role1.getName().equals("role_new")).findFirst().get();

        Assert.assertEquals(1, foundRole.getRoleModules().size());

        RoleModule foundRoleModule = foundRole.getRoleModules().stream().findFirst().get();

        Assert.assertEquals("found role module equals", roleModule.getModuleName(), foundRoleModule.getModuleName());

        foundRole = this.xaSecurityDao.getRoleById(foundRole.getId());

        foundRole.getRoleModules().remove(roleModule);
        this.xaSecurityDao.saveOrUpdateRole(foundRole);

        foundRole = this.xaSecurityDao.getRoleById(role.getId());
        Assert.assertEquals(0, foundRole.getRoleModules().size());
    }

    @Test
    @DirtiesContext
    public void test_success_add_role_wth_role_job_plan()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

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

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);

        Role foundRole = principal.getRoles().stream().filter(role1 -> role1.getName().equals("role_new")).findFirst().get();

        RoleJobPlan foundRoleJobPlan = foundRole.getRoleJobPlans().stream().findFirst().get();

        Assert.assertEquals("found role module equals", roleJobPlan.getJobPlanName(), foundRoleJobPlan.getJobPlanName());
    }

    @Test 
    @DirtiesContext
    public void test_success_remove_role()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

        Role role = new Role();
        role.setName("role_new");

        principal.getRoles().clear();

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 0);
    }

    @Test(expected = PropertyValueException.class)
    @DirtiesContext
    public void test_exception_principal_no_name()
    {
        HashSet<Role> roles = new HashSet<>();
        HashSet<Policy> policies = new HashSet<>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setName("role-" + i);

            for(int j=0; j<10; j++)
            {
                Policy policy = new Policy();
                policy.setName("policy-" + j + i);
                this.xaSecurityDao.saveOrUpdatePolicy(policy);
                policies.add(policy);
            }

            role.setPolicies(policies);
            this.xaSecurityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<>();
        }

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

    @Test(expected = ConstraintViolationException.class)
    @DirtiesContext
    public void test_exception_principal_duplicate_name()
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setDescription("description");
        principal.setType("type");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

    @Test
    @DirtiesContext
    public void test_get_all_principals()
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 8);
    }

    @Test
    @DirtiesContext
    public void test_delete_principal()
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 8);

        this.xaSecurityDao.deletePrincipal(principals.get(0));

        principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 7);
    }

    @Test(expected = ConstraintViolationException.class)
    @DirtiesContext
    public void test_exception_role_no_name()
    {
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setDescription("description");

            for(int j=0; j<10; j++)
            {
                Policy policy = new Policy();
                policy.setName("policy" + j + i);
                policy.setDescription("description");
                this.xaSecurityDao.saveOrUpdatePolicy(policy);
                policies.add(policy);
            }

            role.setPolicies(policies);
            this.xaSecurityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<>();
        }

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

    @Test
    @DirtiesContext
    public void test_get_all_roles()
    {
        List<Role> roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 10);
    }

    @Test
    @DirtiesContext
    public void test_delete_role()
    {
        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }

        int numPolicies = this.xaSecurityDao.getAllPolicies().size();

        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);

        List<Role> roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 11);

        this.xaSecurityDao.deleteRole(role);

        roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 10);

        // make sure the delete of the policies associated with the rol did not cascade to the
        // policy table.
        Assert.assertEquals(numPolicies, this.xaSecurityDao.getAllPolicies().size());
    }

    @Test
    @DirtiesContext
    public void test_complex_add_policy_to_role()
    {
        Role role = new Role();
        role.setName("role_new");
        role.setDescription("description");

        HashSet<Policy> policies = new HashSet<>();

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.xaSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }

        // insert the role into the db
        this.xaSecurityDao.saveOrUpdateRole(role);

        // add a policy to the role and save the role
        List<Policy> dbPolicies = this.xaSecurityDao.getAllPolicies();
        role.addPolicy(dbPolicies.get(0));
        this.xaSecurityDao.saveOrUpdateRole(role);

        Assert.assertEquals(1, role.getPolicies().size());

        // add another policy to the role along with the existing and save the role
        role.addPolicy(dbPolicies.get(0));
        role.addPolicy(dbPolicies.get(1));

        // save the role
        this.xaSecurityDao.saveOrUpdateRole(role);
        // make sure there are only 2 policies associated with the role
        Assert.assertEquals(2, role.getPolicies().size());

        // refresh the role from the DB
        role = this.xaSecurityDao.getAllRoles().stream()
            .filter(role1 -> role1.getName().equals("role_new"))
            .collect(Collectors.toList()).get(0);

        // make sure there are still only 2 policies associated with the role
        Assert.assertEquals(2, role.getPolicies().size());

        // now add some more policies including ones that are already associated
        // with the role
        role.addPolicy(dbPolicies.get(0));
        role.addPolicy(dbPolicies.get(0));
        role.addPolicy(dbPolicies.get(1));
        role.addPolicy(dbPolicies.get(2));

        // save the role.
        this.xaSecurityDao.saveOrUpdateRole(role);
        Assert.assertEquals(3, role.getPolicies().size());

        // get the role from the db again.
        role = this.xaSecurityDao.getAllRoles().stream()
            .filter(role1 -> role1.getName().equals("role_new"))
            .collect(Collectors.toList()).get(0);

        // make sure we have the expected number of policies on the role
        Assert.assertEquals(3, role.getPolicies().size());

        // now we'll remove a policy from a role
        Policy policy = role.getPolicies().stream().findFirst().get();
        role.getPolicies().remove(policy);

        // save the role
        this.xaSecurityDao.saveOrUpdateRole(role);
        // confirm the number of policies
        Assert.assertEquals(2, role.getPolicies().size());

        // get the role from the db again.
        role = this.xaSecurityDao.getAllRoles().stream()
            .filter(role1 -> role1.getName().equals("role_new"))
            .collect(Collectors.toList()).get(0);

        // make sure we have the expected number of policies on the role
        Assert.assertEquals(2, role.getPolicies().size());
    }

    @Test(expected = ConstraintViolationException.class)
    @DirtiesContext
    public void test_exception_principal_policy_name()
    {
        Policy policy = new Policy();
        policy.setName("policy11");
        policy.setDescription("description");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);
    }

    @Test(expected = PropertyValueException.class)
    @DirtiesContext
    public void test_exception_policy_no_name() {
        Policy policy = new Policy();
        policy.setDescription("description");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);
    }

    @Test(expected = ConstraintViolationException.class)
    @DirtiesContext
    public void test_exception_role_name()
    {
        Role role = new Role();
        role.setName("role1");
        role.setDescription("description");

        this.xaSecurityDao.saveOrUpdateRole(role);
    }

    @Test
    @DirtiesContext
    public void test_get_all_policies()
    {
        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 100);
    }

    @Test
    @DirtiesContext
    public void test_delete_policy()
    {
        Policy policy = new Policy();
        policy.setName("blah");
        policy.setDescription("description");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);

        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 101);

        this.xaSecurityDao.deletePolicy(policy);

        policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 100);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_with_role()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipalsWithRole("role0");
    	
    	Assert.assertTrue(principals.size() == 8);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_with_role_bad_role_name()
    {
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipalsWithRole("bad name");
    	
    	Assert.assertTrue(principals.size() == 0);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_by_names()
    {
    	ArrayList<String> names = new ArrayList<String>();
    	names.add("role0");
    	names.add("role1");
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);

    	Assert.assertTrue(principals.size() == 8);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_by_names_empty_names()
    {
    	ArrayList<String> names = new ArrayList<String>();

    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);
    	
    	Assert.assertTrue(principals.size() == 0);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_by_names_bad_names()
    {
    	ArrayList<String> names = new ArrayList<String>();
    	names.add("bad name 1");
    	names.add("bad name 2");

    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalsByRoleNames(names);
    	
    	Assert.assertTrue(principals.size() == 0);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_by_name_like()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalByNameLike("anotherPrincipal");

    	Assert.assertTrue(principals.size() == 7);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_principal_by_name_like_bad_name()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalByNameLike("bad name");

    	Assert.assertTrue(principals.size() == 0);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_all_policy_link_types()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityDao.getAllPolicyLinkTypes();

    	Assert.assertTrue(plts.size() == 3);
    }
 
    @Test
    @DirtiesContext
    public void test_success_get_role_by_name_like()
    {    	
    	List<Role> roles = this.xaSecurityDao.getRoleByNameLike("role");

    	Assert.assertTrue(roles.size() == 10);
    }
 
    @Test
    @DirtiesContext
    public void test_success_get_policy_by_name_like()
    {    	
    	List<Policy> policies = this.xaSecurityDao.getPolicyByNameLike("policy");

    	Assert.assertTrue(policies.size() == 100);
    }
    
    @Test
    @DirtiesContext
    public void test_success_save_policy_link()
    {    	
    	List<PolicyLinkType> plts = this.xaSecurityDao.getAllPolicyLinkTypes();
    	PolicyLink policyLink = new PolicyLink(plts.get(0),Long.valueOf(1), "name");
    	
    	this.xaSecurityDao.saveOrUpdatePolicyLink(policyLink);
    	
    	Assert.assertNotNull(policyLink.getId());
    	
    	this.xaSecurityDao.deletePolicyLink(policyLink);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_policies_by_role()
    {    	
    	List<Policy> policies = this.xaSecurityDao.getAllPoliciesWithRole("role1");

    	Assert.assertTrue(policies.size() == 10);
    }
}
