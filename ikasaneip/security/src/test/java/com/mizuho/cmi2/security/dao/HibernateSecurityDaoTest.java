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
package com.mizuho.cmi2.security.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.SecurityDaoException;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
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
        "/securityModelService-context.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class HibernateSecurityDaoTest
{
    /** Object being tested */
    @Resource private HibernateSecurityDao xaSecurityDao;

    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     * @throws SecurityDaoException 
     */
    @Before public void setup() throws SecurityDaoException
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

            System.out.println("Adding policies: " + policies);
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

    	this.xaSecurityDao.saveOrUpdatePrincipal(principal);

    	principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal1");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal2");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal3");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal4");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal5");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal6");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");
        principal.setRoles(roles);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

    @Test 
    @DirtiesContext
    public void test_success_get_principal_by_name() throws SecurityDaoException
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        System.out.println(principal);
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);
    }

    @Test 
    @DirtiesContext
    public void test_success_get_policy_by_name() throws SecurityDaoException
    {
        Policy policy = this.xaSecurityDao.getPolicyByName("policy11");

        Assert.assertNotNull(policy);

        Assert.assertEquals(policy.getName(), "policy11");
    }

    @Test 
    @DirtiesContext
    public void test_success_get_role_by_name() throws SecurityDaoException
    {
        Role role = this.xaSecurityDao.getRoleByName("role1");

        Assert.assertNotNull(role);

        Assert.assertEquals(role.getName(), "role1");
    }

    @Test 
    @DirtiesContext
    public void test_success_add_role() throws SecurityDaoException
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        System.out.println(principal);
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

        System.out.println("Adding policies: " + policies);
        role.setPolicies(policies);
        this.xaSecurityDao.saveOrUpdateRole(role);
        policies = new HashSet<Policy>();

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        System.out.println(principal);
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);
    }

    @Test 
    @DirtiesContext
    public void test_success_remove_role() throws SecurityDaoException
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        System.out.println(principal);
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

        Role role = new Role();
        role.setName("role_new");

        HashSet<Policy> policies = new HashSet<Policy>();

        principal.getRoles().clear();

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        System.out.println(principal);
        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 0);
    }

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_principal_no_name() throws SecurityDaoException
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

            System.out.println("Adding policies: " + policies);
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

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_principal_duplicate_name() throws SecurityDaoException
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

    @Test
    @DirtiesContext
    public void test_get_all_principals() throws SecurityDaoException
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 8);
    }

    @Test
    @DirtiesContext
    public void test_delete_principal() throws SecurityDaoException
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 8);

        this.xaSecurityDao.deletePrincipal(principals.get(0));

        principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 7);
    }

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_role_no_name() throws SecurityDaoException
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

            System.out.println("Adding policies: " + policies);
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
    public void test_get_all_roles() throws SecurityDaoException
    {
        List<Role> roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 10);
    }

    @Test
    @DirtiesContext
    public void test_delete_role() throws SecurityDaoException
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

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_principal_policy_name() throws SecurityDaoException
    {
        Policy policy = new Policy();
        policy.setName("policy11");
        this.xaSecurityDao.saveOrUpdatePolicy(policy);
    }

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_policy_no_name() throws SecurityDaoException
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

            System.out.println("Adding policies: " + policies);
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

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_role_name() throws SecurityDaoException
    {
        Role role = new Role();
        role.setName("role1");
        role.setDescription("description");

        this.xaSecurityDao.saveOrUpdateRole(role);
    }

    @Test
    @DirtiesContext
    public void test_get_all_policies() throws SecurityDaoException
    {
        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 100);
    }

    @Test
    @DirtiesContext
    public void test_delete_policy() throws SecurityDaoException
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
}
