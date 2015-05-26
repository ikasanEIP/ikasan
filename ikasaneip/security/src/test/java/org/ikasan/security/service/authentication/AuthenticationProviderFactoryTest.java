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
package org.ikasan.security.service.authentication;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.ikasan.security.service.AuthenticationServiceImpl;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.AuthenticationProvider;
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
public class AuthenticationProviderFactoryTest
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
	public void testNullUserServiceOnConstruction() throws AuthenticationServiceException
	{
		new AuthenticationProviderFactoryImpl(null, this.xaSecurityService);
	}
    
    @Test (expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testNullSecurityServiceOnConstruction() throws AuthenticationServiceException
	{
		new AuthenticationProviderFactoryImpl(this.localTxUserService, null);
	}

	/**
	 * Test method for {@link org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl#getAuthenticationProvider(org.ikasan.security.model.AuthenticationMethod)}.
	 */
	@Test
	@DirtiesContext
	public void testGetAuthenticationProviderLocal()
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		AuthenticationProvider authProvider = xaAuthenticationProviderFactory.getAuthenticationProvider(authMethod);
		
		Assert.assertTrue(authProvider instanceof LocalAuthenticationProvider);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl#getAuthenticationProvider(org.ikasan.security.model.AuthenticationMethod)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testExceptionGetAuthenticationProviderLocalBadAuthMethod()
	{	
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod("bad method");
		
		xaAuthenticationProviderFactory.getAuthenticationProvider(authMethod);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl#getAuthenticationProvider(org.ikasan.security.model.AuthenticationMethod)}.
	 */
	@Test
	@DirtiesContext
	public void testGetAuthenticationProviderLocalNullAuthMethod()
	{	
		AuthenticationProvider authProvider = xaAuthenticationProviderFactory.getAuthenticationProvider(null);
		
		Assert.assertTrue(authProvider instanceof LocalAuthenticationProvider);
	}
	
	@Test
	@DirtiesContext
	public void testGetAuthenticationProviderLdap()
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
		
		AuthenticationProvider authProvider = xaAuthenticationProviderFactory.getAuthenticationProvider(authMethod);
		
		Assert.assertTrue(authProvider instanceof LdapAuthenticationProvider);
	}
	
	@Test
	@DirtiesContext
	public void testGetAuthenticationProviderLdapLocal()
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP_LOCAL);
		
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		AuthenticationProvider authProvider = xaAuthenticationProviderFactory.getAuthenticationProvider(authMethod);
		
		Assert.assertTrue(authProvider instanceof LdapLocalAuthenticationProvider);
	}

	/**
	 * Test method for {@link org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl#testAuthenticationConnection(org.ikasan.security.model.AuthenticationMethod)}.
	 * @throws Exception 
	 */
	@Test
	@DirtiesContext
	public void testTestAuthenticationConnectionLocal() throws Exception
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.xaAuthenticationProviderFactory.testAuthenticationConnection(authMethod);
		
		Assert.assertTrue(true);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.service.authentication.AuthenticationProviderFactoryImpl#testAuthenticationConnection(org.ikasan.security.model.AuthenticationMethod)}.
	 * @throws Exception 
	 */
	@Test
	@DirtiesContext
	public void testTestAuthenticationConnectionLdap() throws Exception
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
		
		this.xaAuthenticationProviderFactory.testAuthenticationConnection(authMethod);
		
		Assert.assertTrue(true);
	}
	
	@Test
	@DirtiesContext
	public void testTestAuthenticationConnectionLdapLocal() throws Exception
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP_LOCAL);
		
		authMethod.setLdapServerUrl(ldapServerUrl);
		authMethod.setLdapBindUserDn("CN=Stewart Michael,OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com");
		authMethod.setLdapUserSearchFilter("(sAMAccountName={0})");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.xaAuthenticationProviderFactory.testAuthenticationConnection(authMethod);
		
		Assert.assertTrue(true);
	}
	
	@Test
	@DirtiesContext
	public void testTestAuthenticationConnectionNullAuthMethod() throws Exception
	{
		this.xaAuthenticationProviderFactory.testAuthenticationConnection(null);
		
		Assert.assertTrue(true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	@DirtiesContext
	public void testTestAuthenticationConnectionUnsupportedAuthMethod() throws Exception
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setId(SecurityConstants.AUTH_METHOD_ID);
		authMethod.setMethod("unsupported method");
		
		this.localTxSecurityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		this.xaAuthenticationProviderFactory.testAuthenticationConnection(authMethod);
	}

	@After
	public void teardownLdapServer()
	{
		// Disconnect from the server and cause the server to shut down.
		inMemoryDirectoryServer.clear();
		inMemoryDirectoryServer.shutDown(true);
	}
}
