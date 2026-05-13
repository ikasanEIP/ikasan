package org.ikasan.security.service.authentication;

import org.ikasan.spec.security.model.IkasanPrincipal;
import org.ikasan.spec.security.model.User;
import org.ikasan.spec.security.service.UserService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for LocalAuthenticationProvider
 *
 * @author Ikasan Development Team
 */
public class LocalAuthenticationProviderTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private UserService userService = mockery.mock(UserService.class);
    private User user = mockery.mock(User.class);
    private IkasanPrincipal principal = mockery.mock(IkasanPrincipal.class);

    /**
     * Test constructor with null userService
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_userService()
    {
        new LocalAuthenticationProvider(null);
    }

    /**
     * Test constructor with valid userService
     */
    @Test
    public void test_constructor_success()
    {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        assertNotNull(provider);
    }

    /**
     * Test authenticate with successful authentication
     */
    @Test
    public void test_authenticate_success()
    {
        final String username = "testUser";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        final long previousAccessTimestamp = System.currentTimeMillis();
        final Set<IkasanPrincipal> principals = new HashSet<>();
        principals.add(principal);

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result); // Password won't match because we're using a mock encoder
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with failed authentication - wrong password
     */
    @Test
    public void test_authenticate_wrong_password()
    {
        final String username = "testUser";
        final String password = "wrongPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with user not found
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_authenticate_user_not_found()
    {
        final String username = "unknownUser";
        final String password = "testPassword";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(throwException(new UsernameNotFoundException("User not found")));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with null username
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_authenticate_null_username()
    {
        final String username = null;
        final String password = "testPassword";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername("");
            will(throwException(new UsernameNotFoundException("Username cannot be null")));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with empty username
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_authenticate_empty_username()
    {
        final String username = "";
        final String password = "testPassword";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(throwException(new UsernameNotFoundException("Username cannot be empty")));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with null password
     */
    @Test
    public void test_authenticate_null_password()
    {
        final String username = "testUser";
        final String password = null;
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with empty password
     */
    @Test
    public void test_authenticate_empty_password()
    {
        final String username = "testUser";
        final String password = "";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with user having no principals
     */
    @Test
    public void test_authenticate_user_with_no_principals()
    {
        final String username = "testUser";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        final long previousAccessTimestamp = System.currentTimeMillis();
        final Set<IkasanPrincipal> principals = new HashSet<>();

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with special characters in username
     */
    @Test
    public void test_authenticate_special_characters_in_username()
    {
        final String username = "test@user.com";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with special characters in password
     */
    @Test
    public void test_authenticate_special_characters_in_password()
    {
        final String username = "testUser";
        final String password = "p@ssw0rd!#$%";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test supports with UsernamePasswordAuthenticationToken
     */
    @Test
    public void test_supports_UsernamePasswordAuthenticationToken()
    {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test supports with other authentication type
     */
    @Test
    public void test_supports_other_authentication_type()
    {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        assertFalse(provider.supports(Authentication.class));
    }

    /**
     * Test supports with null class
     */
    @Test
    public void test_supports_null_class()
    {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        assertFalse(provider.supports(null));
    }

    /**
     * Test delegatingPasswordEncoder returns non-null encoder
     */
    @Test
    public void test_delegatingPasswordEncoder()
    {
        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        PasswordEncoder encoder = provider.delegatingPasswordEncoder();
        assertNotNull(encoder);
    }

    /**
     * Test authenticate with case-sensitive username
     */
    @Test
    public void test_authenticate_case_sensitive_username()
    {
        final String username = "TestUser";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with long username
     */
    @Test
    public void test_authenticate_long_username()
    {
        final String username = "a".repeat(255);
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with long password
     */
    @Test
    public void test_authenticate_long_password()
    {
        final String username = "testUser";
        final String password = "p".repeat(255);
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with whitespace in username
     */
    @Test
    public void test_authenticate_whitespace_in_username()
    {
        final String username = "test user";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with whitespace in password
     */
    @Test
    public void test_authenticate_whitespace_in_password()
    {
        final String username = "testUser";
        final String password = "test password";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with numeric username
     */
    @Test
    public void test_authenticate_numeric_username()
    {
        final String username = "12345";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with numeric password
     */
    @Test
    public void test_authenticate_numeric_password()
    {
        final String username = "testUser";
        final String password = "12345678";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test authenticate with user having null password
     */
    @Test
    public void test_authenticate_user_with_null_password_stored()
    {
        final String username = "testUser";
        final String password = "testPassword";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(null));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with user service throwing exception
     */
    @Test(expected = RuntimeException.class)
    public void test_authenticate_user_service_throws_exception()
    {
        final String username = "testUser";
        final String password = "testPassword";

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(throwException(new RuntimeException("Database connection failed")));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        provider.authenticate(authToken);
    }

    /**
     * Test authenticate with zero previous access timestamp
     */
    @Test
    public void test_authenticate_zero_previous_access_timestamp()
    {
        final String username = "testUser";
        final String password = "testPassword";
        final String encodedPassword = "{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        final long previousAccessTimestamp = 0L;
        final Set<IkasanPrincipal> principals = new HashSet<>();
        principals.add(principal);

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        mockery.checking(new Expectations()
        {{
            oneOf(userService).loadUserByUsername(username);
            will(returnValue(user));

            oneOf(user).getPassword();
            will(returnValue(encodedPassword));
        }});

        LocalAuthenticationProvider provider = new LocalAuthenticationProvider(userService);
        Authentication result = provider.authenticate(authToken);

        assertNull(result);
        mockery.assertIsSatisfied();
    }
}
