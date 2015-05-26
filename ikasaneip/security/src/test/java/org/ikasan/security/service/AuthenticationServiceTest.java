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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;

/**
 * 
 * @author Ikasan Development Team
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
@Ignore // FIXME - Reactivate this test once the test data is fixed.
public class AuthenticationServiceTest
{
	private InMemoryDirectoryServer inMemoryDirectoryServer;
	
	@Resource 
	InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig;
	
	@Resource
	private AuthenticationService localTxAuthenticationService;
	
	@Resource
	private SecurityDao localTxSecurityDao;
	
	@Resource 
	private UserService localTxUserService; 
	
	@Resource
	private String ldapServerUrl;
	
	@Resource
	private SecurityService xaSecurityService;
	
	@Resource
	private AuthenticationProviderFactory xaAuthenticationProviderFactory;
	
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
                this.localTxSecurityDao.saveOrUpdatePolicy(policy);
                policies.add(policy);
            }

            role.setPolicies(policies);
            role.setDescription("description");
            this.localTxSecurityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<Policy>();
        }

        IkasanPrincipal principalGroup = new IkasanPrincipal();
        principalGroup.setName("ISD_Middleware");
        principalGroup.setType("group");
        principalGroup.setRoles(roles);
        principalGroup.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principalGroup);

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("stewmi");
        principal.setType("user");
        principal.setRoles(roles);
        principal.setDescription("description");
        
        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        User user = new User("stewmi", "password_local", "me@there.com", true);
        this.localTxUserService.createUser(user);
        
        user = this.localTxUserService.loadUserByUsername("stewmi");
        
        Set<IkasanPrincipal> principals = new HashSet<IkasanPrincipal>();
        principals.add(principal);
        user.setPrincipals(principals);
        
        this.localTxUserService.updateUser(user);
        

        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal1");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal2");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal3");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal4");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal5");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal6");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.localTxSecurityDao.saveOrUpdatePrincipal(principal);
    }
    
    @Before
	public void setupLdapServer() throws LDAPException, IOException
	{
		this.inMemoryDirectoryServer = new InMemoryDirectoryServer(this.inMemoryDirectoryServerConfig);
		inMemoryDirectoryServer.importFromLDIF(
				true,
				new LDIFReader(new File(new File(".").getCanonicalPath() + "/src/test/resources/data.ldif")));
		
		inMemoryDirectoryServer.startListening();
	}
    
    @Test (expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testNullAuthenticationProviderFactoryOnConstruction() throws AuthenticationServiceException
	{
		new AuthenticationServiceImpl(null, this.xaSecurityService);
	}
    
    @Test (expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testNullSecurityServiceOnConstruction() throws AuthenticationServiceException
	{
		new AuthenticationServiceImpl(this.xaAuthenticationProviderFactory, null);
	}

	/**
	 * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
	 * @throws AuthenticationServiceException 
	 */
	@Test
	@DirtiesContext
	public void testLocalLogin() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.localTxAuthenticationService.login("stewmi", "password_local");
		
		Assert.assertNotNull(authentication);
	}
	
	@Test (expected=AuthenticationServiceException.class)
	@DirtiesContext
	public void testLocalLoginFailBadPassword() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.localTxAuthenticationService.login("stewmi", "bad password");
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
	 * @throws AuthenticationServiceException 
	 */
	@Test
	@DirtiesContext
	public void testLdapLoginFallingBackToLocal() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.localTxAuthenticationService.login("stewmi", "password_local");
		
		Assert.assertNotNull(authentication);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
	 * @throws AuthenticationServiceException 
	 */
	@Test (expected=AuthenticationServiceException.class)
	@DirtiesContext
	public void testLdapLoginFallingBackToLocalFailBadPassword() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.localTxAuthenticationService.login("stewmi", "bad password");
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
	 * @throws AuthenticationServiceException 
	 */
	@Test
	@DirtiesContext
	public void testLdapLogin() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.localTxAuthenticationService.login("stewmi", "password");
		
		Assert.assertNotNull(authentication);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
	 * @throws AuthenticationServiceException 
	 */
	@Test(expected=AuthenticationServiceException.class)
	@DirtiesContext
	public void testLdapLoginFailBadPassword() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.localTxAuthenticationService.login("stewmi", "bad password");
	}
	
	@After
	public void teardownLdapServer()
	{
		// Disconnect from the server and cause the server to shut down.
		inMemoryDirectoryServer.clear();
		inMemoryDirectoryServer.shutDown(true);
	}
}
