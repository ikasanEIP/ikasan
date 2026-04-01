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

import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.spec.security.model.AuthenticationMethod;
import org.ikasan.spec.security.service.AuthenticationService;
import org.ikasan.spec.security.service.AuthenticationServiceException;
import org.ikasan.spec.security.service.SecurityService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Exhaustive unit tests for AuthenticationServiceImpl
 *
 * @author Ikasan Development Team
 */
public class AuthenticationServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private SecurityService securityService;
    private AuthenticationProvider mockAuthenticationProvider;
    private AuthenticationProvider localAuthenticationProvider;
    private AuthenticationMethod authenticationMethod1;
    private AuthenticationMethod authenticationMethod2;
    private AuthenticationService authenticationService;

    @Before
    public void setup()
    {
        authenticationProviderFactory = mockery.mock(AuthenticationProviderFactory.class, "authenticationProviderFactory");
        securityService = mockery.mock(SecurityService.class, "securityService");
        mockAuthenticationProvider = mockery.mock(AuthenticationProvider.class, "mockAuthenticationProvider");
        localAuthenticationProvider = mockery.mock(AuthenticationProvider.class, "localAuthenticationProvider");
        authenticationMethod1 = mockery.mock(AuthenticationMethod.class, "authenticationMethod1");
        authenticationMethod2 = mockery.mock(AuthenticationMethod.class, "authenticationMethod2");
    }

    /**
     * Test constructor with null authenticationProviderFactory throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_authenticationProviderFactory()
    {
        new AuthenticationServiceImpl(null, securityService);
    }

    /**
     * Test constructor with null securityService throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_securityService()
    {
        new AuthenticationServiceImpl(authenticationProviderFactory, null);
    }

    /**
     * Test constructor with valid parameters succeeds
     */
    @Test
    public void test_constructor_success()
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);
        assertNotNull(authenticationService);
    }

    /**
     * Test successful login with first enabled authentication method
     */
    @Test
    public void test_login_success_first_auth_method() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod1);
            will(returnValue(mockAuthenticationProvider));

            oneOf(mockAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(username, result.getName());
        assertEquals(1, result.getAuthorities().size());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login when first auth method fails but second succeeds
     */
    @Test
    public void test_login_success_second_auth_method() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);
        final AuthenticationProvider mockAuthenticationProvider2 = mockery.mock(AuthenticationProvider.class, "mockAuthenticationProvider2");

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1, authenticationMethod2)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod1);
            will(returnValue(mockAuthenticationProvider));

            oneOf(mockAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(throwException(new BadCredentialsException("Bad credentials")));

            oneOf(authenticationMethod2).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod2);
            will(returnValue(mockAuthenticationProvider2));

            oneOf(mockAuthenticationProvider2).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login falls back to local authentication when all methods fail
     */
    @Test
    public void test_login_success_local_fallback() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod1);
            will(returnValue(mockAuthenticationProvider));

            oneOf(mockAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(throwException(new BadCredentialsException("Bad credentials")));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login with disabled authentication method is skipped
     */
    @Test
    public void test_login_disabled_auth_method_skipped() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(false));

            // Should skip to local authentication
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login with empty authentication methods list falls back to local
     */
    @Test
    public void test_login_empty_auth_methods_uses_local() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login fails when local authentication returns null
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_login_fails_local_returns_null() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(null));
        }});

        authenticationService.login(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test login fails when local authentication throws exception
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_login_fails_local_throws_exception() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "wrongpass";

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(throwException(new BadCredentialsException("Bad credentials")));
        }});

        authenticationService.login(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test login fails when user has no authorities
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_login_fails_no_authorities() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final Authentication authWithNoAuthorities = new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(authWithNoAuthorities));
        }});

        authenticationService.login(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test login fails when authorities is null
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_login_fails_null_authorities() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final Authentication authWithNullAuthorities = new UsernamePasswordAuthenticationToken(username, password, null);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(authWithNullAuthorities));
        }});

        authenticationService.login(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test login stops at first successful authentication
     */
    @Test
    public void test_login_stops_at_first_success() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1, authenticationMethod2)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod1);
            will(returnValue(mockAuthenticationProvider));

            oneOf(mockAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));

            // Second authentication method should NOT be called
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticateLocal with successful authentication
     */
    @Test
    public void test_authenticateLocal_success() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.authenticateLocal(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(username, result.getName());
        assertEquals(1, result.getAuthorities().size());

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticateLocal fails with null authentication
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_authenticateLocal_fails_null_authentication() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(null));
        }});

        authenticationService.authenticateLocal(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticateLocal fails when provider throws exception
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_authenticateLocal_fails_exception() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "wrongpass";

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(throwException(new BadCredentialsException("Bad credentials")));
        }});

        authenticationService.authenticateLocal(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticateLocal fails with no authorities
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_authenticateLocal_fails_no_authorities() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final Authentication authWithNoAuthorities = new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(authWithNoAuthorities));
        }});

        authenticationService.authenticateLocal(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticateLocal fails with null authorities
     */
    @Test(expected = AuthenticationServiceException.class)
    public void test_authenticateLocal_fails_null_authorities() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final Authentication authWithNullAuthorities = new UsernamePasswordAuthenticationToken(username, password, null);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(authWithNullAuthorities));
        }});

        authenticationService.authenticateLocal(username, password);

        mockery.assertIsSatisfied();
    }

    /**
     * Test login with multiple authorities succeeds
     */
    @Test
    public void test_login_multiple_authorities() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "adminuser";
        final String password = "adminpass";
        final List<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_SUPERUSER")
        );
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Collections.emptyList()));

            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(3, result.getAuthorities().size());

        mockery.assertIsSatisfied();
    }

    /**
     * Test login with unauthenticated result from auth method continues to next
     */
    @Test
    public void test_login_unauthenticated_result_continues() throws AuthenticationServiceException
    {
        authenticationService = new AuthenticationServiceImpl(authenticationProviderFactory, securityService);

        final String username = "testuser";
        final String password = "testpass";
        final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        final Authentication successAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAuthenticationMethods();
            will(returnValue(Arrays.asList(authenticationMethod1)));

            oneOf(authenticationMethod1).isEnabled();
            will(returnValue(true));

            oneOf(authenticationProviderFactory).getAuthenticationProvider(authenticationMethod1);
            will(returnValue(mockAuthenticationProvider));

            oneOf(mockAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(null));

            // Should fall back to local
            oneOf(authenticationProviderFactory).getLocalAuthenticationProvider();
            will(returnValue(localAuthenticationProvider));

            oneOf(localAuthenticationProvider).authenticate(with(any(UsernamePasswordAuthenticationToken.class)));
            will(returnValue(successAuth));
        }});

        Authentication result = authenticationService.login(username, password);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());

        mockery.assertIsSatisfied();
    }
}
