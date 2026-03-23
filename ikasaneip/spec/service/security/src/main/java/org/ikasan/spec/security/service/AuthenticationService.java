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
package org.ikasan.spec.security.service;

import org.springframework.security.core.Authentication;

/**
 * Service interface for user authentication operations in the Ikasan security framework.
 *
 * <p>Provides methods for authenticating users against various authentication providers including
 * LDAP directories and local database storage. Supports multiple authentication methods with
 * fallback capabilities.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface AuthenticationService
{

    /**
     * Authenticates a user by validating the provided credentials against configured authentication providers.
     *
     * <p>Attempts authentication through all configured authentication methods in order, including
     * LDAP directories and local database authentication. Returns an authenticated session if any
     * provider successfully validates the credentials.
     *
     * @param username the username of the user attempting to log in, must not be {@code null}
     * @param password the password associated with the specified username, must not be {@code null}
     * @return an {@link Authentication} object representing the authenticated user's session with granted authorities
     * @throws AuthenticationServiceException if an error occurs during the authentication process or credentials are invalid
     */
    Authentication login(String username, String password) throws AuthenticationServiceException;


    /**
     * Authenticates a user against the local database data store only.
     *
     * <p>Bypasses external authentication providers (such as LDAP) and authenticates solely
     * against locally stored user credentials. Useful for administrative access or when
     * external authentication systems are unavailable.
     *
     * @param username the username of the user attempting to log in, must not be {@code null}
     * @param password the password associated with the specified username, must not be {@code null}
     * @return an {@link Authentication} object representing the authenticated user's session
     * @throws AuthenticationServiceException if an error occurs during authentication or credentials are invalid
     */
    Authentication authenticateLocal(String username, String password) throws AuthenticationServiceException;
}
