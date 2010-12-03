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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.ldap.InitialLdapContext;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.AbstractAuthenticationToken;

/**
 * <p>
 * Authentication token to use when an application needs further access to the LDAP context used to authenticate the user.
 * </p>
 * 
 * <p>
 * When this is the Authentication object stored in the Spring Security context, an application can retrieve the current
 * LDAP context thusly:
 * </p>
 * 
 * <pre>
 * LdapAuthenticationToken ldapAuth = (LdapAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
 * InitialLdapContext ldapContext = ldapAuth.getContext();
 * </pre>
 * 
 * @author Ikasan Development Team
 */
public class LdapAuthenticationToken extends AbstractAuthenticationToken
{
    /** Generated serial UID */
    private static final long serialVersionUID = 3274000189997850275L;

    /** The authentication to use */
    private Authentication auth;

    /** The initial LDAP context */
    transient private InitialLdapContext context;

    /** The list of granted authorities for this token */
    private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    /**
     * Construct a new LdapAuthenticationToken, using an existing Authentication object and a default authority.
     * 
     * @param auth
     * @param defaultAuthority
     */
    public LdapAuthenticationToken(Authentication auth, GrantedAuthority defaultAuthority)
    {
        this.auth = auth;
        if (auth.getAuthorities() != null)
        {
            this.authorities.addAll(Arrays.asList(auth.getAuthorities()));
        }
        if (defaultAuthority != null)
        {
            this.authorities.add(defaultAuthority);
        }
        super.setAuthenticated(true);
    }

    /**
     * Construct a new LdapAuthenticationToken, using an existing Authentication object and a default authority.
     * 
     * @param auth
     * @param defaultAuthority
     */
    public LdapAuthenticationToken(Authentication auth, String defaultAuthority)
    {
        this(auth, new GrantedAuthorityImpl(defaultAuthority));
    }

    @Override
    public GrantedAuthority[] getAuthorities()
    {
        GrantedAuthority[] authoritiesArray = this.authorities.toArray(new GrantedAuthority[0]);
        return authoritiesArray;
    }

    /**
     * Add an authority to the list of granted authorities
     * @param authority - authority to be added
     */
    public void addAuthority(GrantedAuthority authority)
    {
        this.authorities.add(authority);
    }

    /**
     * Add authorities to the list of granted authorities
     * @param authoritiesToAdd - authorities to be added
     */
    public void addAuthorities(Collection<GrantedAuthority> authoritiesToAdd)
    {
        this.authorities.addAll(authoritiesToAdd);
    }
    
    public Object getCredentials()
    {
        return auth.getCredentials();
    }

    public Object getPrincipal()
    {
        return auth.getPrincipal();
    }

    /**
     * Retrieve the LDAP context attached to this user's authentication object.
     * 
     * @return the LDAP context
     */
    public InitialLdapContext getContext()
    {
        return context;
    }

    /**
     * Attach an LDAP context to this user's authentication object.
     * 
     * @param context the LDAP context
     */
    public void setContext(InitialLdapContext context)
    {
        this.context = context;
    }
}