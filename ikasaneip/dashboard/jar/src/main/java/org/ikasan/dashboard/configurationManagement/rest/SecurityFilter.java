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
package org.ikasan.dashboard.configurationManagement.rest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;


/**
 * 
 * @author Ikasan Development Team
 *
 */

/**
 * A Jersey ContainerRequestFilter that provides a SecurityContext for all
 * requests processed by this application.
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecurityFilter implements ContainerRequestFilter {
 
	private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
	
    @Context
    UriInfo uriInfo;
 
    @Context
    HttpServletRequest request;
 
    @Inject
    private AuthenticationService authenticationService;
 
    /**
     * Perform the required authentication checks, and return the User instance
     * for the authenticated user.
     */
    private Authentication authenticate(ContainerRequestContext request) 
    {
    	
    	String auth = request.getHeaderString("Authorization");
    	
        if (auth == null || !auth.startsWith("Basic ")) 
        {
            return null;
        }

        auth = new String(Base64.decodeBase64(auth.substring("Basic ".length())));
        String[] vals = auth.split(":");
        String username = vals[0];
        String password = vals[1];
        
        Authentication authentication = null;
        
        try
        {
        	authentication = authenticationService.login(username, password);
        }
        catch(AuthenticationServiceException e)
        {
        	return null;
        }

        return authentication;
    }
 
    /**
     * SecurityContext used to perform authorisation checks.
     */
    public class Authorizer implements SecurityContext 
    {
        private Authentication authentication = null;
 
        public Authorizer(final Authentication authentication) 
        {
        	this.authentication = authentication;
        }
 
        public Principal getUserPrincipal() 
        {
            return (Principal)this.authentication.getPrincipal();
        }
 
        /**
         * @param role Role to be checked
         */
        public boolean isUserInRole(String role) 
        {       	
            for(GrantedAuthority authority: this.authentication.getAuthorities())
            {
            	if(role.equals(authority.getAuthority()))
            	{
            		return true;
            	}
            }
            
            return false;
        }
 
        public boolean isSecure() 
        {
            return "https".equals(uriInfo.getRequestUri().getScheme());
        }
 
        public String getAuthenticationScheme() 
        {
            if (authentication == null) 
            {
                return null;
            }
            return SecurityContext.BASIC_AUTH;
        }
 
    }

	/* (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext context) throws IOException
	{
		Authentication authentication = authenticate(context);
				
		if(authentication != null)
		{
			context.setSecurityContext(new Authorizer(authentication));
		}
	}
 
}
