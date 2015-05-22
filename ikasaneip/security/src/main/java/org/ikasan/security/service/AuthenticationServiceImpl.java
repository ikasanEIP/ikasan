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

import org.apache.log4j.Logger;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.LocalAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService
{
	private static Logger logger = Logger.getLogger(AuthenticationServiceImpl.class);
	
	private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
	private SecurityService securityService;
	
	/**
	 * @param authenticationProviderFactory
	 */
	public AuthenticationServiceImpl(AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory,
			SecurityService securityService)
	{
		super();
		this.authenticationProviderFactory = authenticationProviderFactory;
		if(this.authenticationProviderFactory == null)
		{
			throw new IllegalArgumentException("authenticationProviderFactory cannot be null!");
		}
		this.securityService = securityService;
		if(this.securityService == null)
		{
			throw new IllegalArgumentException("securityService cannot be null!");
		}
	}


	/* (non-Javadoc)
	 * @see org.ikasan.security.service.AuthenticationService#login(java.lang.String, java.lang.String)
	 */
	@Override
	public Authentication login(String username, String password)
			throws AuthenticationServiceException
	{
		Authentication authentication = null;
		
		
		AuthenticationMethod authMethod = securityService.getAuthenticationMethod();
		AuthenticationProvider authProvider = authenticationProviderFactory.getAuthenticationProvider(authMethod);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password);
		
		try
		{
			logger.info("Attempting authentication using method " + authMethod.getMethod() + " for user " + username);
			authentication = authProvider.authenticate(auth);				
			logger.info("Authentication successful for user " + username + " authentication = " + authentication);
		}
		catch (Exception e)
		{
			// If the the authentication is not local and has failed, this try local authentication. 
			if(!authMethod.getMethod().equals(SecurityConstants.AUTH_METHOD_LOCAL))
			{
				logger.info("Attempting local authentication for user " + username);
				
				String currentAuthMethod = authMethod.getMethod();
				
				authMethod.setMethod(SecurityConstants.AUTH_METHOD_LOCAL);

				authProvider = authenticationProviderFactory.getAuthenticationProvider(authMethod);
				
				authentication = authProvider.authenticate(auth);
				
				authMethod.setMethod(currentAuthMethod);
			}
		}

		if(authentication == null)
		{
			logger.info("Authentication failed for user " + username);
			throw new AuthenticationServiceException("Error authenticating!");
		}

		return authentication;
	}
}
