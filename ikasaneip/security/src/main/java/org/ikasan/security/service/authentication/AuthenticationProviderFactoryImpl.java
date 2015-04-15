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

import org.apache.log4j.Logger;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AuthenticationProviderFactoryImpl implements AuthenticationProviderFactory<AuthenticationMethod>
{
	/** Logger instance */
    private static Logger logger = Logger.getLogger(AuthenticationProviderFactoryImpl.class);

	private UserService userService;
	private SecurityService securityService;

	/**
	 * Constructor
	 * 
	 * @param userService
	 * @param securityService
	 */
	public AuthenticationProviderFactoryImpl(UserService userService,
			SecurityService securityService)
	{
		super();
		this.userService = userService;
		if(this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if(this.securityService == null)
		{
			throw new IllegalArgumentException("securityService cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.authentication.AuthenticationProviderFactory#getAuthenticationProvider(java.lang.Object)
	 */
	@Override
	public AuthenticationProvider getAuthenticationProvider(AuthenticationMethod authMethod)
	{
		if(authMethod == null)
		{
			return createLocalAuthenticationProvider(authMethod);
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LOCAL))
		{
			return createLocalAuthenticationProvider(authMethod);
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LDAP))
		{
			AuthenticationProvider authProvider = null;
			try
			{
				authProvider =  createLdapAuthenticationProvider(authMethod);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return authProvider;
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LDAP_LOCAL))
		{
			AuthenticationProvider authProvider = null;
			try
			{
				authProvider =  createLdapAuthenticationProvider(authMethod);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return authProvider;
		}
		else
		{
			throw new IllegalArgumentException("authMethod not supported: " + authMethod.getMethod());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.ikasan.security.service.authentication.AuthenticationProviderFactory#testAuthenticationConnection(org.ikasan.security.model.AuthenticationMethod)
	 */
	public void testAuthenticationConnection(AuthenticationMethod authMethod) throws Exception
	{
		if(authMethod == null)
		{
			return;
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LOCAL))
		{
			return;
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LDAP))
		{
			
			this.testLdapConnection(authMethod);
		}
		else if(authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LDAP_LOCAL))
		{
			this.testLdapConnection(authMethod);
		}
		else
		{
			throw new IllegalArgumentException("authMethod not supported: " + authMethod.getMethod());
		}
	}

	private String testLdapConnection(AuthenticationMethod authMethod) throws Exception 
	{
		logger.info("authMethod.getLdapServerUrl() = " + authMethod.getLdapServerUrl());
		logger.info("authMethod.getLdapBindUserDn() = " + authMethod.getLdapBindUserDn());
		logger.info("authMethod.getLdapBindUserPassword() = " + authMethod.getLdapBindUserPassword());
		logger.info("authMethod.getLdapUserSearchBaseDn() = " + authMethod.getLdapUserSearchBaseDn());
		logger.info("authMethod.getLdapUserSearchFilter() = " + authMethod.getLdapUserSearchFilter());
		
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(authMethod.getLdapServerUrl().trim());
		contextSource.setUserDn(authMethod.getLdapBindUserDn().trim());
		contextSource.setPassword(authMethod.getLdapBindUserPassword().trim());
		contextSource.afterPropertiesSet();
		
		contextSource.getReadOnlyContext().lookup(authMethod.getLdapBindUserDn());
		return contextSource.getBaseLdapPathAsString();
	}

	/**
	 * 
	 * @param authMethod
	 * @return
	 * @throws Exception 
	 */
	private LdapAuthenticationProvider createLdapAuthenticationProvider(AuthenticationMethod authMethod) throws Exception
	{
		logger.info("authMethod.getLdapServerUrl() = " + authMethod.getLdapServerUrl());
		logger.info("authMethod.getLdapBindUserDn() = " + authMethod.getLdapBindUserDn());
		logger.info("authMethod.getLdapBindUserPassword() = " + authMethod.getLdapBindUserPassword());
		logger.info("authMethod.getLdapUserSearchBaseDn() = " + authMethod.getLdapUserSearchBaseDn());
		logger.info("authMethod.getLdapUserSearchFilter() = " + authMethod.getLdapUserSearchFilter());
		
		// TODO consider making the LDAP stuff a singleton
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(authMethod.getLdapServerUrl().trim());
		contextSource.setUserDn(authMethod.getLdapBindUserDn().trim());
		contextSource.setPassword(authMethod.getLdapBindUserPassword().trim());
		contextSource.afterPropertiesSet();
		
		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(authMethod.getLdapUserSearchBaseDn().trim(),
				authMethod.getLdapUserSearchFilter().trim(), contextSource);
		
		BindAuthenticator bindAuthenicator = new BindAuthenticator(contextSource);
		bindAuthenicator.setUserSearch(userSearch);

		return new LdapAuthenticationProvider(bindAuthenicator, this.securityService, this.userService);
	}

	/**
	 * 
	 * @param authMethod
	 * @return
	 */
	private LocalAuthenticationProvider createLocalAuthenticationProvider(AuthenticationMethod authMethod)
	{
		return new LocalAuthenticationProvider(this.securityService, this.userService);
	}
}
