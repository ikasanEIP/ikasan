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
package org.ikasan.console.security.service.authentication;

import java.util.Arrays;

import javax.naming.ldap.InitialLdapContext;

import org.ikasan.framework.security.model.User;
import org.ikasan.framework.security.service.UserService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.ldap.LdapAuthenticator;

/**
 * Custom Spring Security authentication provider which tries to bind to an LDAP server with the passed-in credentials;
 * of note, when used with the custom {@link LdapAuthenticatorImpl}, does <strong>not</strong> require an LDAP username
 * and password for initial binding.
 * 
 * @author Ikasan Development Team
 */
public class LdapAuthenticationProvider implements AuthenticationProvider
{
    /** A fake role, used to by pass a strict API in Spring security */
    public static final String DUMMY_ROLE = "DUMMY";
    
    /** The authenticator we're going to authenticate with */
    private LdapAuthenticator authenticator;

    /** The User Service so we can get user's Ikasan groups in order to match against their LDAP/AD groups */ 
    private UserService userService;
    
    /**
     * Constructor - Takes a UserService
     * @param userService
     */
    public LdapAuthenticationProvider(UserService userService)
    {
        if (userService == null)
        {
            throw new IllegalArgumentException("UserService must be provided.");
        }
        this.userService = userService;
    }
    
    public Authentication authenticate(Authentication auth) throws AuthenticationException
    {
        // Authenticate, using the passed-in credentials.
        DirContextOperations authAdapter = authenticator.authenticate(auth);
        // Creating an LdapAuthenticationToken (rather than using the existing Authentication
        // object) allows us to add the already-created LDAP context for our application to use later.
        // Needs a dummy role in order to initially authenticate, empty "" isn't allowed
        LdapAuthenticationToken ldapAuth = new LdapAuthenticationToken(auth, DUMMY_ROLE);
        
        // Get the user from the Ikasan internal data store & add their authorities to the token
        User user = this.userService.loadUserByUsername(auth.getPrincipal().toString());
        ldapAuth.addAuthorities(Arrays.asList(user.getAuthorities()));
        InitialLdapContext ldapContext = (InitialLdapContext) authAdapter.getObjectAttribute("ldapContext");
        if (ldapContext != null)
        {
            ldapAuth.setContext(ldapContext);
        }
        return ldapAuth;
    }

    /**
     * Unchecked exception as we're implementing an interface that does not support Generics
     *  
     * @param clazz Authentication we're trying to support
     * @return True if we're supporting the type of authentication coming in
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz));
    }

    /**
     * Get the authenticator
     * @return The authenticator
     */
    public LdapAuthenticator getAuthenticator()
    {
        return authenticator;
    }

    /**
     * Set the authenticator to be used
     * @param authenticator
     */
    public void setAuthenticator(LdapAuthenticator authenticator)
    {
        this.authenticator = authenticator;
    }

}