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

import javax.naming.ldap.InitialLdapContext;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.providers.ldap.LdapAuthenticator;

/**
 * Custom Spring Security LDAP authenticator which tries to bind to an LDAP server using the passed-in credentials; does
 * <strong>not</strong> require "master" credentials for an initial bind prior to searching for the passed-in username.
 * 
 * @author Ikasan Development Team
 */
public class LdapAuthenticatorImpl implements LdapAuthenticator
{
    /** The context factory to get an LDAP context from */
    private DefaultSpringSecurityContextSource contextFactory;

    /** The principal prefix, often AD servers require a domain prefix */
    private String principalPrefix = "";

    public DirContextOperations authenticate(Authentication authentication)
    {
        // Grab the username and password out of the authentication object.
        String principal = principalPrefix + authentication.getName();
        String password = "";
        if (authentication.getCredentials() != null)
        {
            password = authentication.getCredentials().toString();
        }
        // If we have a valid username and password, try to authenticate.
        if (!("".equals(principal.trim())) && !("".equals(password.trim())))
        {
            InitialLdapContext ldapContext = (InitialLdapContext) contextFactory.getReadWriteContext(principal,
                password);
            // We need to pass the context back out, so that the auth provider can add it to the
            // Authentication object.
            DirContextOperations authAdapter = new DirContextAdapter();
            authAdapter.addAttributeValue("ldapContext", ldapContext);
            return authAdapter;
        }
        // Default else
        throw new BadCredentialsException("Blank username and/or password!");
    }

    /**
     * Since the InitialLdapContext that's stored as a property of an LdapAuthenticationToken is transient (because it
     * isn't Serializable), we need some way to recreate the InitialLdapContext if it's null (e.g., if the
     * LdapAuthenticationToken has been serialized and deserialized). This is that mechanism.
     * 
     * @param authenticator the LdapAuthenticator instance from your application's context
     * @param auth the LdapAuthenticationToken in which to recreate the InitialLdapContext
     * @return The reconstructed InitialLdapContext
     */
    static public InitialLdapContext recreateLdapContext(LdapAuthenticator authenticator, LdapAuthenticationToken auth)
    {
        DirContextOperations authAdapter = authenticator.authenticate(auth);
        InitialLdapContext context = (InitialLdapContext) authAdapter.getObjectAttribute("ldapContext");
        auth.setContext(context);
        return context;
    }

    /**
     * Get the context factory
     * @return context factory
     */
    public DefaultSpringSecurityContextSource getContextFactory()
    {
        return contextFactory;
    }

    /**
     * Set the context factory to use for generating a new LDAP context.
     * 
     * @param contextFactory
     */
    public void setContextFactory(DefaultSpringSecurityContextSource contextFactory)
    {
        this.contextFactory = contextFactory;
    }

    /**
     * Get the principal prefix
     * @return principal prefix
     */
    public String getPrincipalPrefix()
    {
        return principalPrefix;
    }

    /**
     * Set the string to be prepended to all principal names prior to attempting authentication against the LDAP server.
     * (For example, if the Active Directory wants the domain-name-plus backslash prepended, use this.)
     * 
     * @param principalPrefix
     */
    public void setPrincipalPrefix(String principalPrefix)
    {
        if (principalPrefix != null)
        {
            this.principalPrefix = principalPrefix;
        }
        else
        {
            this.principalPrefix = "";
        }
    }
}