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

import com.unboundid.ldap.listener.Base64PasswordEncoderOutputFormatter;
import com.unboundid.ldap.listener.ClearInMemoryPasswordEncoder;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;
import jakarta.annotation.Resource;
import org.ikasan.security.SecurityConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.*;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@SpringJUnitConfig(classes = {SecurityConfiguration.class, TestImportConfig.class})
public class AuthenticationServiceTest
{
	private InMemoryDirectoryServer inMemoryDirectoryServer;
	
	@Resource 
	InMemoryDirectoryServerConfig inMemoryDirectoryServerConfig;
	
	@Resource
	private AuthenticationService authenticationService;
	
	@Resource
	private SecurityDao securityDao;
	
	@Resource 
	private UserService userService;
	
	@Resource
	private String ldapServerUrl;
	
	@Resource
	private SecurityService xaSecurityService;
	
	@Resource
	private AuthenticationProviderFactory xaAuthenticationProviderFactory;

    @BeforeEach
    void setup() throws IOException, LDAPException
    {
        setupLdapServer();

        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Policy> policies = new HashSet<Policy>();

        for(int i=0; i<10; i++)
        {
            Role role = new Role();
            role.setName("role" + i);

            for(int j=0; j<10; j++)
            {
                Policy policy = new Policy();
                policy.setName("role" + i + "policy" + j + i);
                policy.setDescription("description");
                this.securityDao.saveOrUpdatePolicy(policy);
                policies.add(policy);
            }

            role.setPolicies(policies);

            role.setDescription("description");
            this.securityDao.saveOrUpdateRole(role);
            roles.add(role);
            policies = new HashSet<>();

            RoleModule roleModule = new RoleModule();
            roleModule.setModuleName("role" + i + "moduleName");
            roleModule.setRole(role);
            this.securityDao.saveRoleModule(roleModule);
            role.setRoleModules(Set.of(roleModule));

            RoleJobPlan roleJobPlan = new RoleJobPlan();
            roleJobPlan.setJobPlanName("role" + i + "jobPlanName");
            roleJobPlan.setRole(role);
            this.securityDao.saveRoleJobPlan(roleJobPlan);
            role.setRoleJobPlans(Set.of(roleJobPlan));
        }

        IkasanPrincipal principalGroup = new IkasanPrincipal();
        principalGroup.setName("ISD_Middleware");
        principalGroup.setType("group");
        principalGroup.setRoles(roles);
        principalGroup.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principalGroup);

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("svc-acc");
        principal.setType("application");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        User user = new User("llogan", "password_local", "me@there.com", true);
        this.userService.createUser(user);
        
        user = this.userService.loadUserByUsername("llogan");
        
        Set<IkasanPrincipal> principals = new HashSet<IkasanPrincipal>();
        principals.add(principal);
        user.setPrincipals(principals);
        
        this.userService.updateUser(user);
        

        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal1");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal2");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal3");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal4");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal5");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal6");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);
        
        principal = new IkasanPrincipal();
        principal.setName("anotherPrincipal7");
        principal.setType("type");
        principal.setRoles(roles);
        principal.setDescription("description");

        this.securityDao.saveOrUpdatePrincipal(principal);

    }
    
    public void setupLdapServer() throws LDAPException, IOException
	{
        this.inMemoryDirectoryServerConfig.setPasswordEncoders(new ClearInMemoryPasswordEncoder("{CLEAR}", null),
            new ClearInMemoryPasswordEncoder("{BASE64}", Base64PasswordEncoderOutputFormatter.getInstance()));
		this.inMemoryDirectoryServer = new InMemoryDirectoryServer(this.inMemoryDirectoryServerConfig);
		inMemoryDirectoryServer.importFromLDIF(
				true,
				new LDIFReader(new File(new File(".").getCanonicalPath() + "/src/test/resources/data.ldif")));

		inMemoryDirectoryServer.startListening();
	}

    @Test
        @DirtiesContext
    void testNullAuthenticationProviderFactoryOnConstruction() throws AuthenticationServiceException
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new AuthenticationServiceImpl(null, this.xaSecurityService);
        });
    }

    @Test
        @DirtiesContext
    void testNullSecurityServiceOnConstruction() throws AuthenticationServiceException
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new AuthenticationServiceImpl(this.xaAuthenticationProviderFactory, null);
        });
    }

    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException 
     */
    @Test
        @DirtiesContext
    void testLocalLogin() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setOrder(1L);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);
		
		this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.authenticationService.login("llogan", "password_local");
		
		assertNotNull(authentication);
	}

    @Test
        @DirtiesContext
    void testLocalLoginFailBadPassword() throws AuthenticationServiceException
    {
        assertThrows(AuthenticationServiceException.class, () -> {
            AuthenticationMethod authMethod = new AuthenticationMethod();
            authMethod.setOrder(1L);
            authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);

            this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

            this.authenticationService.login("stewmi", "bad password");
        });
    }

    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException 
     */
    @Test
        @DirtiesContext
    void testLdapLoginFallingBackToLocal() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setOrder(1L);
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		
		this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.authenticationService.login("llogan", "password_local");
		
		assertNotNull(authentication);

        assertEquals(120, authentication.getAuthorities().size());
        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        authentication.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());
	}

    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException 
     */
    @Test
        @DirtiesContext
    void testLdapLoginFallingBackToLocalFailBadPassword() throws AuthenticationServiceException
    {
        assertThrows(AuthenticationServiceException.class, () -> {
            AuthenticationMethod authMethod = new AuthenticationMethod();
            authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
            authMethod.setOrder(1L);

            this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

            this.authenticationService.login("stewmi", "bad password");
        });
    }

    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException 
     */
    @Test
        @DirtiesContext
    void testLdapLogin() throws AuthenticationServiceException
	{
		AuthenticationMethod authMethod = new AuthenticationMethod();
		authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
		authMethod.setLdapServerUrl(ldapServerUrl);
        authMethod.setOrder(1L);
        authMethod.setEnabled(true);
		authMethod.setLdapBindUserDn("cn=Directory Manager");
		authMethod.setLdapBindUserPassword("password");
		authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
		authMethod.setLdapUserSearchFilter("(uid={0})");
		
		this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);
		
		Authentication authentication = this.authenticationService.login("llogan", "password");
		
		assertNotNull(authentication);

        assertEquals(120, authentication.getAuthorities().size());
        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        authentication.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());
	}

    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException
     */
    @Test
        @DirtiesContext
    void testLdapLoginMultipleAuthMethods() throws AuthenticationServiceException
    {
        AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setEnabled(true);
        authMethod.setOrder(1L);

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setEnabled(true);
        authMethod.setOrder(2L);

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setLdapServerUrl(ldapServerUrl);
        authMethod.setOrder(3L);
        authMethod.setEnabled(true);
        authMethod.setLdapBindUserDn("cn=Directory Manager");
        authMethod.setLdapBindUserPassword("password");
        authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
        authMethod.setLdapUserSearchFilter("(uid={0})");

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        Authentication authentication = this.authenticationService.login("llogan", "password");

        assertNotNull(authentication);

        assertEquals(120, authentication.getAuthorities().size());
        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        authentication.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());
    }


    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException
     */
    @Test
        @DirtiesContext
    void testLdapLoginMultipleAuthMethodsReverseOrderOfAuthMethods() throws AuthenticationServiceException
    {
        AuthenticationMethod authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setEnabled(true);
        authMethod.setOrder(3L);

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setEnabled(true);
        authMethod.setOrder(2L);

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        authMethod = new AuthenticationMethod();
        authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
        authMethod.setLdapServerUrl(ldapServerUrl);
        authMethod.setOrder(1L);
        authMethod.setEnabled(true);
        authMethod.setLdapBindUserDn("cn=Directory Manager");
        authMethod.setLdapBindUserPassword("password");
        authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
        authMethod.setLdapUserSearchFilter("(uid={0})");

        this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

        Authentication authentication = this.authenticationService.login("llogan", "password");

        assertNotNull(authentication);

        assertEquals(120, authentication.getAuthorities().size());
        AtomicBoolean containsModuleAuthorities = new AtomicBoolean(false);
        AtomicBoolean containsJobPlanAuthorities = new AtomicBoolean(false);
        authentication.getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("MODULE:"));
                containsModuleAuthorities.set(true);
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                assertTrue(grantedAuthority.getAuthority().startsWith("JOB_PLAN:"));
                containsJobPlanAuthorities.set(true);
            }
        });

        assertTrue(containsModuleAuthorities.get());
        assertTrue(containsJobPlanAuthorities.get());
    }


    /**
     * Test method for {@link org.ikasan.security.service.AuthenticationServiceImpl#login(java.lang.String, java.lang.String)}.
     * @throws AuthenticationServiceException 
     */
    @Test
        @DirtiesContext
    void testLdapLoginFailBadPassword() throws AuthenticationServiceException
    {
        assertThrows(AuthenticationServiceException.class, () -> {
            AuthenticationMethod authMethod = new AuthenticationMethod();
            authMethod.setOrder(1L);
            authMethod.setMethod(SecurityConstants.AUTH_METHOD_LDAP);
            authMethod.setLdapServerUrl(ldapServerUrl);
            authMethod.setLdapBindUserDn("cn=Directory Manager");
            authMethod.setLdapBindUserPassword("password");
            authMethod.setLdapUserSearchBaseDn("ou=people,ou=IL-Sunset,dc=slidev,dc=org");
            authMethod.setLdapUserSearchFilter("(uid={0})");

            this.securityDao.saveOrUpdateAuthenticationMethod(authMethod);

            this.authenticationService.login("llogan", "bad password");
        });
    }

    @AfterEach
    void teardownLdapServer()
	{
		// Disconnect from the server and cause the server to shut down.

		inMemoryDirectoryServer.clear();
		inMemoryDirectoryServer.shutDown(true);
	}
}
