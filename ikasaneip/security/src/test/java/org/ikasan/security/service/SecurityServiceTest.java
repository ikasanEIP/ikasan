/*
O * $Id: SecurityServiceTest.java 43977 2015-03-10 16:06:07Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/securityModelService/api/src/test/java/com/mizuho/cmi2/security/service/SecurityServiceTest.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2012 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.security.service;

import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.ikasan.security.dao.HibernateSecurityDao;
import org.ikasan.security.dao.SecurityDaoException;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.authentication.LdapAuthenticationProvider;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;



/**
 * Unit test for {@link SecurityService}
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
public class SecurityServiceTest
{
    /** Mockery for objects */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    private final LdapAuthenticationProvider authProvider = this.mockery.mock(LdapAuthenticationProvider.class, "authProvider");
   
 
    /** Object being tested */
    @Resource private HibernateSecurityDao xaSecurityDao;
    @Resource private SecurityService xaSecurityService;

    @Resource
	private InMemoryDirectoryServer inMemoryDirectoryServer;

//	@Before
//	public void setupLdapServer() throws LDAPException, IOException
//	{
//
//		inMemoryDirectoryServer.importFromLDIF(
//				true,
//				new LDIFReader(new File(new File(".").getCanonicalPath() + "/src/test/resources/data.ldif")));
//		
//		inMemoryDirectoryServer.startListening();
//	}

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

            role.setPolicies(policies);
            role.setDescription("description");
            this.xaSecurityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<Policy>();
        }

        IkasanPrincipal principalGroup = new IkasanPrincipal();
        principalGroup.setName("ISD_Middleware");
        principalGroup.setType("group");
        principalGroup.setRoles(roles);
        principalGroup.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principalGroup);

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("stewmi");
        principal.setType("user");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal1");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal2");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal3");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal4");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal5");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal6");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);
    }

//    @Test 
//    @DirtiesContext
//    @Ignore
//    public void test_success_login() throws SecurityServiceException
//    {
//        IkasanPrincipal principal = this.xaSecurityService.login("stewmi", "password");
//
//        Assert.assertNotNull(principal);
//    }

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_null_dao_on_construction() throws SecurityServiceException
    {
        new SecurityServiceImpl(null);
    }

    @Test 
    @DirtiesContext
    public void test_success_get_principal_by_name() throws SecurityServiceException
    {
        IkasanPrincipal principal = this.xaSecurityService.findPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);
    }

    @Test 
    @DirtiesContext
    public void test_success_create_new_principal() throws SecurityServiceException
    {
        IkasanPrincipal principal = this.xaSecurityService.createNewPrincipal("stewmi2", "type");

        principal = this.xaSecurityService.findPrincipalByName("stewmi2");

        Assert.assertNotNull(principal);
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_principal_null_name() throws SecurityServiceException
    {
        this.xaSecurityService.createNewPrincipal(null, "type");
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_principal_null_type() throws SecurityServiceException
    {
        this.xaSecurityService.createNewPrincipal("name", null);
    }

    @Test 
    @DirtiesContext
    public void test_success_add_role() throws SecurityDaoException, SecurityServiceException
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
        this.xaSecurityService.saveRole(role);
        policies = new HashSet<Policy>();

        principal.getRoles().add(role);

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 11);
    }

    @Test 
    @DirtiesContext
    public void test_success_create_new_role() throws SecurityServiceException
    {
        Role role = this.xaSecurityService.createNewRole("testRole", "description");

        Assert.assertNotNull(role);
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_role_null_name() throws SecurityServiceException
    {
        this.xaSecurityService.createNewRole(null, "description");
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_role_null_description() throws SecurityServiceException
    {
        this.xaSecurityService.createNewRole("role", null);
    }

    @Test 
    @DirtiesContext
    public void test_success_remove_role() throws SecurityDaoException, SecurityServiceException
    {
        IkasanPrincipal principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 10);

        Role role = new Role();
        role.setName("role_new");

        principal.getRoles().clear();

        this.xaSecurityService.savePrincipal(principal);

        principal = this.xaSecurityDao.getPrincipalByName("stewmi");

        Assert.assertNotNull(principal);

        Assert.assertEquals(principal.getRoles().size(), 0);
    }

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_principal_no_name() throws SecurityDaoException, SecurityServiceException
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

        this.xaSecurityService.savePrincipal(principal);
    }

    @Test(expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_principal_duplicate_name() throws SecurityServiceException
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");

        this.xaSecurityService.savePrincipal(principal);
    }

    @Test
    @DirtiesContext
    public void test_get_all_principals() throws SecurityServiceException
    {
        List<IkasanPrincipal> principals = this.xaSecurityService.getAllPrincipals();

        Assert.assertTrue(principals.size() == 9);
    }

    @Test
    @DirtiesContext
    public void test_delete_principal() throws SecurityDaoException, SecurityServiceException
    {
        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 9);

        this.xaSecurityService.deletePrincipal(principals.get(0));

        principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertTrue(principals.size() == 8);
    }

    @Test(expected = SecurityDaoException.class)
    @DirtiesContext
    public void test_exception_role_no_name() throws SecurityDaoException, SecurityServiceException
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

        this.xaSecurityService.savePrincipal(principal);
    }

    @Test
    @DirtiesContext
    public void test_get_all_roles() throws SecurityServiceException
    {
        List<Role> roles = this.xaSecurityService.getAllRoles();

        Assert.assertTrue(roles.size() == 10);
    }

    @Test
    @DirtiesContext
    public void test_delete_role() throws SecurityDaoException, SecurityServiceException
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

        this.xaSecurityService.deleteRole(role);

        roles = this.xaSecurityDao.getAllRoles();

        Assert.assertTrue(roles.size() == 10);
    }

    @Test(expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_principal_policy_name() throws SecurityServiceException
    {
        Policy policy = new Policy();
        policy.setName("policy11");
        this.xaSecurityService.savePolicy(policy);
    }

    @Test(expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_policy_no_name() throws SecurityDaoException, SecurityServiceException
    {
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setName("name");

            for(int j=0; j<10; j++)
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
    }

    @Test 
    @DirtiesContext
    public void test_success_create_new_policy() throws SecurityServiceException
    {
        Policy policy = this.xaSecurityService.createNewPolicy("testPolicy", "description");

        Assert.assertNotNull(policy);
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_policy_null_name() throws SecurityServiceException
    {
        this.xaSecurityService.createNewPolicy(null, "description");
    }

    @Test (expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_create_new_policy_null_description() throws SecurityServiceException
    {
        this.xaSecurityService.createNewPolicy("role", null);
    }

    @Test(expected = SecurityServiceException.class)
    @DirtiesContext
    public void test_exception_role_name() throws SecurityServiceException
    {
        Role role = new Role();
        role.setName("role1");

        this.xaSecurityService.saveRole(role);
    }

    @Test
    @DirtiesContext
    public void test_get_all_policies() throws SecurityServiceException
    {
        List<Policy> policies = this.xaSecurityService.getAllPolicies();

        Assert.assertTrue(policies.size() == 100);
    }

    @Test
    @DirtiesContext
    public void test_delete_policy() throws SecurityDaoException, SecurityServiceException
    {
        Policy policy = new Policy();
        policy.setName("blah");
        policy.setDescription("description");
        this.xaSecurityService.savePolicy(policy);

        List<Policy> policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 101);

        this.xaSecurityService.deletePolicy(policy);

        policies = this.xaSecurityDao.getAllPolicies();

        Assert.assertTrue(policies.size() == 100);
    }

    @After
	public void teardownLdapServer()
	{
		// Disconnect from the server and cause the server to shut down.
		inMemoryDirectoryServer.shutDown(true);
	}
}
