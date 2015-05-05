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
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.service.LdapServiceImpl.LdapUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class LdapServiceTest
{
	private InMemoryDirectoryServer inMemoryDirectoryServer;
	
	@Resource 
	InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig;
	

	
	@Resource
	private SecurityDao localTxSecurityDao;
	
	@Resource 
	private UserDao localTxUserDao; 
	
	@Resource
	private String ldapServerUrl;
	
	@Resource
	private SecurityService xaSecurityService;
	
	@Resource
	private LdapServiceImpl xaLdapService;

	@Before
	public void setupLdapServer() throws LDAPException, IOException
	{
		this.inMemoryDirectoryServer = new InMemoryDirectoryServer(this.inMemoryDirectoryServerConfig);
		inMemoryDirectoryServer.importFromLDIF(
				true,
				new LDIFReader(new File(new File(".").getCanonicalPath() + "/src/test/resources/data.ldif")));
		
		inMemoryDirectoryServer.startListening();
	}
	
	@Before public void setup()
    {
		HashSet<Policy> policies = new HashSet<Policy>();
		
        Role role = new Role();
        role.setName("User");

        for(int j=0; j<10; j++)
        {
            Policy policy = new Policy();
            policy.setName("policy" + j);
            policy.setDescription("description");
            this.localTxSecurityDao.saveOrUpdatePolicy(policy);
            policies.add(policy);
        }

        role.setPolicies(policies);
        role.setDescription("description");

        this.localTxSecurityDao.saveOrUpdateRole(role);
    }
	
	@Test (expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testNullSecurityDaoOnConstruction() throws AuthenticationServiceException
	{
		new LdapServiceImpl(null, this.localTxUserDao);
	}
    
    @Test (expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testNullUserDaoOnConstruction() throws AuthenticationServiceException
	{
		new LdapServiceImpl(this.localTxSecurityDao, null);
	}

	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getAllLdapUsers()}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetAllLdapUsers() throws LdapServiceException
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
		
		List<String> users = xaLdapService.getAllLdapUsers();
		
		Assert.assertTrue(users.size() == 2);

	}

	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getLdapUser(java.lang.String)}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetLdapUser() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		LdapUser user = xaLdapService.getLdapUser("Stewart Michael");
		
		Assert.assertNotNull(user);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getLdapUser(java.lang.String)}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetLdapUserBadUsername() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		LdapUser user = xaLdapService.getLdapUser("bad username");
		
		Assert.assertNull(user);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getLdapUser(java.lang.String)}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetLdapUserBadAccountType() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("bad account type");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		LdapUser user = xaLdapService.getLdapUser("Stewart Michael");
		
		Assert.assertNull(user);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getLdapUser(java.lang.String)}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetLdapUserBadEmail() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("bad email");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		LdapUser user = xaLdapService.getLdapUser("Stewart Michael");
		
		Assert.assertNotNull(user);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getLdapUser(java.lang.String)}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetLdapUserBadSurname() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("bad sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		LdapUser user = xaLdapService.getLdapUser("Stewart Michael");
		
		Assert.assertNotNull(user);
	}


	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#getAllApplicationSecurity()}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testGetAllApplicationSecurity() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		authMethod.setApplicationSecurityBaseDn("OU=Application,OU=Security,OU=Groups,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setApplicationSecurityGroupAttributeName("sAMAccountName");
		authMethod.setApplicationSecurityDescriptionAttributeName("description");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		xaLdapService.getAllApplicationSecurity();
	}
	
	@Test
	@DirtiesContext
	public void testGetApplicationSecurity() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		authMethod.setApplicationSecurityBaseDn("OU=Application,OU=Security,OU=Groups,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setApplicationSecurityGroupAttributeName("sAMAccountName");
		authMethod.setApplicationSecurityDescriptionAttributeName("description");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		IkasanPrincipal principal = xaLdapService.getApplicationSecurity("I_APP_DERIVATION_UAT_RO");
		
		Assert.assertNotNull(principal);
	}
	
	@Test
	@DirtiesContext
	public void testGetApplicationSecurityBadName() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		authMethod.setApplicationSecurityBaseDn("OU=Application,OU=Security,OU=Groups,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setApplicationSecurityGroupAttributeName("sAMAccountName");
		authMethod.setApplicationSecurityDescriptionAttributeName("description");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		IkasanPrincipal principal = xaLdapService.getApplicationSecurity("bad name");
		
		Assert.assertNull(principal);
	}
	
	@Test
	@DirtiesContext
	public void testGetApplicationSecurityNoDescription() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		authMethod.setApplicationSecurityBaseDn("OU=Application,OU=Security,OU=Groups,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setApplicationSecurityGroupAttributeName("sAMAccountName");
		authMethod.setApplicationSecurityDescriptionAttributeName("no description");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		IkasanPrincipal principal = xaLdapService.getApplicationSecurity("I_APP_DERIVATION_UAT_RO");
		
		Assert.assertNotNull(principal);
	}


	/**
	 * Test method for {@link org.ikasan.security.service.LdapServiceImpl#synchronize()}.
	 * @throws LdapServiceException 
	 */
	@Test
	@DirtiesContext
	public void testSynchronize() throws LdapServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		authMethod.setAccountTypeAttributeName("accountType");
		authMethod.setEmailAttributeName("mail");
		authMethod.setSurnameAttributeName("sn");
		authMethod.setFirstNameAttributeName("givenName");
		authMethod.setUserAccountNameAttributeName("sAMAccountName");
		authMethod.setDepartmentAttributeName("department");
		authMethod.setLdapUserDescriptionAttributeName("description");
		authMethod.setMemberofAttributeName("memberOf");
		authMethod.setApplicationSecurityBaseDn("OU=Application,OU=Security,OU=Groups,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setApplicationSecurityGroupAttributeName("sAMAccountName");
		authMethod.setApplicationSecurityDescriptionAttributeName("description");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		xaLdapService.synchronize();
		
		// Synchronise again as db will now be populated. Want to test against populated DB.
		xaLdapService.synchronize();
	}
	
	@After
	public void teardownLdapServer()
	{
		// Disconnect from the server and cause the server to shut down.
		inMemoryDirectoryServer.clear();
		inMemoryDirectoryServer.shutDown(true);
	}

}
