/*
 * $Id: HibernateSecurityDaoTest.java 42408 2015-01-16 18:22:32Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/securityModelService/api/src/test/java/com/mizuho/cmi2/security/dao/HibernateSecurityDaoTest.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2012 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.security.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Unit test for {@link HibernateSecurityDao}
 * 
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/security-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class HibernateSecurityDaoTest
{
    /** Object being tested */
    @Resource private SecurityDao xaSecurityDao;

    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     * @throws SecurityDaoException 
     */
    @Before public void setup()
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
    public void test_success_get_principal_by_name()
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);
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
        policies = new HashSet<Policy>();

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");
        
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);
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

        HashSet<Policy> policies = new HashSet<Policy>();

        principal.getRoles().clear();

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 0);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @DirtiesContext
    public void test_exception_principal_no_name()
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
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @DirtiesContext
    public void test_exception_principal_duplicate_name()
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
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

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @DirtiesContext
    public void test_exception_role_no_name()
    {
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();

            for(int j=0; j<10; j++)
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

        Assert.assertTrue(roles.size() == 11);

        this.xaSecurityDao.deleteRole(role);

        roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 10);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @DirtiesContext
    public void test_exception_principal_policy_name()
    {
        Policy policy = new Policy();
        policy.setName("policy11");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @DirtiesContext
    public void test_exception_policy_no_name()
    {
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setName("name");
            role.setDescription("description");

            for(int j=0; j<10; j++)
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
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
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
    public void test_success_get_principal_by_name_like_bad_bname()
    {    	
    	List<IkasanPrincipal> principals = this.xaSecurityDao.getPrincipalByNameLike("bad name");

    	Assert.assertTrue(principals.size() == 0);
    }
    
    @Test
    @DirtiesContext
    public void test_success_get_all_policy_lonk_types()
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
    	PolicyLink policyLink = new PolicyLink(plts.get(0),new Long(1), "name");
    	
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
