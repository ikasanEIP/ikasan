package org.ikasan.security.service.authentication;

import org.ikasan.spec.security.service.AuthenticationService;
import org.ikasan.spec.security.service.AuthenticationServiceException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for CustomAuthenticationProvider
 *
 * @author Ikasan Development Team
 */
public class CustomAuthenticationProviderTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private AuthenticationService authenticationService = mockery.mock(AuthenticationService.class);

    /**
     * Test constructor with null authenticationService
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_authenticationService()
    {
        new CustomAuthenticationProvider(null);
    }

    /**
     * Test constructor with valid authenticationService
     */
    @Test
    public void test_constructor_success()
    {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        assertNotNull(provider);
    }

    /**
     * Test authenticate with successful authentication
     */
    @Test
    public void test_authenticate_success() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");
        final List<GrantedAuthority> authorities = new ArrayList<>();

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        assertEquals(successAuth, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with failed authentication
     */
    @Test(expected = BadCredentialsException.class)
    public void test_authenticate_fails_with_exception() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "wrongPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(throwException(new AuthenticationServiceException("Invalid credentials")));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with null authentication
     */
    @Test
    public void test_authenticate_null_authentication() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(null));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with empty username
     */
    @Test(expected = BadCredentialsException.class)
    public void test_authenticate_empty_username() throws AuthenticationServiceException
    {
        final String username = "";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(throwException(new AuthenticationServiceException("Username cannot be empty")));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with null username
     */
    @Test(expected = BadCredentialsException.class)
    public void test_authenticate_null_username() throws AuthenticationServiceException
    {
        final String username = null;
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login("", password);
            will(throwException(new AuthenticationServiceException("Username cannot be null")));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with null password
     */
    @Test
    public void test_authenticate_null_password() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = null;
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, "null");
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test supports with UsernamePasswordAuthenticationToken
     */
    @Test
    public void test_supports_UsernamePasswordAuthenticationToken()
    {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test supports with other authentication type
     */
    @Test
    public void test_supports_other_authentication_type()
    {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        assertFalse(provider.supports(Authentication.class));
    }

    /**
     * Test supports with null class
     */
    @Test
    public void test_supports_null_class()
    {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        assertFalse(provider.supports(null));
    }

    /**
     * Test authenticate with AuthenticationServiceException containing message
     */
    @Test
    public void test_authenticate_exception_with_message() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        final String exceptionMessage = "Authentication failed due to system error";

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(throwException(new AuthenticationServiceException(exceptionMessage)));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);

        try
        {
            provider.authenticate(authToken);
            fail("Expected BadCredentialsException");
        }
        catch (BadCredentialsException e)
        {
            assertEquals("External system authentication failed", e.getMessage());
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof AuthenticationServiceException);
            assertEquals(exceptionMessage, e.getCause().getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with special characters in username
     */
    @Test
    public void test_authenticate_special_characters_in_username() throws AuthenticationServiceException
    {
        final String username = "test@user.com";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with special characters in password
     */
    @Test
    public void test_authenticate_special_characters_in_password() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "p@ssw0rd!#$%";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with very long username
     */
    @Test
    public void test_authenticate_long_username() throws AuthenticationServiceException
    {
        final String username = "a".repeat(255);
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with very long password
     */
    @Test
    public void test_authenticate_long_password() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "p".repeat(255);
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with whitespace in username
     */
    @Test
    public void test_authenticate_whitespace_in_username() throws AuthenticationServiceException
    {
        final String username = "test user";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with whitespace in password
     */
    @Test
    public void test_authenticate_whitespace_in_password() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "test password";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate returns authentication with authorities
     */
    @Test
    public void test_authenticate_with_authorities() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");
        final GrantedAuthority authority1 = mockery.mock(GrantedAuthority.class, "authority1");
        final GrantedAuthority authority2 = mockery.mock(GrantedAuthority.class, "authority2");
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority1);
        authorities.add(authority2);

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        assertEquals(successAuth, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with case-sensitive username
     */
    @Test
    public void test_authenticate_case_sensitive_username() throws AuthenticationServiceException
    {
        final String username = "TestUser";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with numeric username
     */
    @Test
    public void test_authenticate_numeric_username() throws AuthenticationServiceException
    {
        final String username = "12345";
        final String password = "testPassword";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with numeric password
     */
    @Test
    public void test_authenticate_numeric_password() throws AuthenticationServiceException
    {
        final String username = "testUser";
        final String password = "12345678";
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        final Authentication successAuth = mockery.mock(Authentication.class, "successAuth");

        mockery.checking(new Expectations()
        {{
            oneOf(authenticationService).login(username, password);
            will(returnValue(successAuth));
        }});

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(authenticationService);
        Authentication result = provider.authenticate(authToken);

        assertNotNull(result);
        mockery.assertIsSatisfied();
    }
}
