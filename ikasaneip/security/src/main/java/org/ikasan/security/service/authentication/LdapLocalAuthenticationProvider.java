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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.UserService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class LdapLocalAuthenticationProvider implements AuthenticationProvider
{
	private static Logger logger = Logger.getLogger(LdapLocalAuthenticationProvider.class);
	
    private LdapAuthenticator authenticator;
    private SecurityService securityService;
    private UserService userService;

	/**
	 * Constructor
	 * 
	 * @param securityService
	 * @param userService
	 */
	public LdapLocalAuthenticationProvider(BindAuthenticator authenticator,
    		SecurityService securityService, UserService userService)
	{
		this.authenticator = authenticator;
        if(this.authenticator == null)
        {
        	throw new IllegalArgumentException("authenticator cannot be null!");
        }
        this.securityService = securityService;
        if(this.securityService == null)
        {
        	throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.userService = userService;
        if(this.userService == null)
        {
        	throw new IllegalArgumentException("userService cannot be null!");
        }
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException
	{
		// Authenticate, using the passed-in credentials.
        DirContextOperations authAdapter = authenticator.authenticate(authentication);

		User user = this.userService.loadUserByUsername(authentication.getName());
		Set<IkasanPrincipal> principals = user.getPrincipals();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		logger.info("Logging in user: " + user.getName());

		for(IkasanPrincipal principal: principals)
		{
			Set<Role> roles = principal.getRoles();
			
			for(Role role: roles)
			{
				Set<Policy> policies = role.getPolicies();
				
				logger.info("User: " + user.getName() + " has role: " + role + " via association with principal: " + principal);
				
				for(Policy policy: policies)
				{
					logger.info("Attempting to add granted authority: " + policy);
					
					if(!authorities.contains(policy))
					{
						logger.info("Adding granted authority: " + policy);
						authorities.add(policy);
					}
				}
			}
		}

        return new IkasanAuthentication(true, user, authorities);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz)
	{
		 return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz));
	}

}
