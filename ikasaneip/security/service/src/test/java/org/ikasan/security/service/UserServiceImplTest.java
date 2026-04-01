package org.ikasan.security.service;

import org.ikasan.spec.security.dao.UserDao;
import org.ikasan.spec.security.model.*;
import org.ikasan.spec.security.service.SecurityService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for UserServiceImpl
 *
 * @author Ikasan Development Team
 */
public class UserServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private UserDao userDao = mockery.mock(UserDao.class);
    private SecurityService securityService = mockery.mock(SecurityService.class);
    private PasswordEncoder passwordEncoder = mockery.mock(PasswordEncoder.class);
    private User user = mockery.mock(User.class);
    private UserLite userLite = mockery.mock(UserLite.class);
    private UserFilter userFilter = mockery.mock(UserFilter.class);
    private IkasanPrincipal principal = mockery.mock(IkasanPrincipal.class);
    private Role role = mockery.mock(Role.class);
    private Policy policy = mockery.mock(Policy.class);

    private UserServiceImpl userService;
    private boolean preventLocalAuthentication = false;

    @Before
    public void setup()
    {
        userService = new UserServiceImpl(userDao, securityService, passwordEncoder, preventLocalAuthentication);
    }

    /**
     * Test createUser delegates to DAO
     */
    @Test
    public void test_createUser()
    {
        final String username = "testUser";
        final String password = "password123";
        final String email = "test@example.com";
        final boolean enabled = true;

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).createUser(username, password, email, enabled);
            will(returnValue(user));
        }});

        User result = userService.createUser(username, password, email, enabled);

        assertEquals(user, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersWithRole delegates to DAO
     */
    @Test
    public void test_getUsersWithRole()
    {
        final String roleName = "Admin";
        final int limit = 10;
        final int offset = 0;
        final List<UserLite> users = new ArrayList<>();
        users.add(userLite);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsersWithRole(roleName, userFilter, limit, offset);
            will(returnValue(users));
        }});

        List<UserLite> result = userService.getUsersWithRole(roleName, userFilter, limit, offset);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersWithRoleCount delegates to DAO
     */
    @Test
    public void test_getUsersWithRoleCount()
    {
        final String roleName = "Admin";
        final int expectedCount = 5;

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsersWithRoleCount(roleName, userFilter);
            will(returnValue(expectedCount));
        }});

        int result = userService.getUsersWithRoleCount(roleName, userFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersWithoutRole delegates to DAO
     */
    @Test
    public void test_getUsersWithoutRole()
    {
        final String roleName = "Admin";
        final int limit = 10;
        final int offset = 0;
        final List<UserLite> users = new ArrayList<>();
        users.add(userLite);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsersWithoutRole(roleName, userFilter, limit, offset);
            will(returnValue(users));
        }});

        List<UserLite> result = userService.getUsersWithoutRole(roleName, userFilter, limit, offset);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsersWithoutRoleCount delegates to DAO
     */
    @Test
    public void test_getUsersWithoutRoleCount()
    {
        final String roleName = "Admin";
        final int expectedCount = 15;

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsersWithoutRoleCount(roleName, userFilter);
            will(returnValue(expectedCount));
        }});

        int result = userService.getUsersWithoutRoleCount(roleName, userFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUserCount delegates to DAO
     */
    @Test
    public void test_getUserCount()
    {
        final int expectedCount = 20;

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserCount(userFilter);
            will(returnValue(expectedCount));
        }});

        int result = userService.getUserCount(userFilter);

        assertEquals(expectedCount, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsers delegates to DAO
     */
    @Test
    public void test_getUsers()
    {
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsers();
            will(returnValue(users));
        }});

        List<User> result = userService.getUsers();

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUsers with filter, limit, and offset
     */
    @Test
    public void test_getUsers_with_filter()
    {
        final int limit = 10;
        final int offset = 0;
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUsers(userFilter, limit, offset);
            will(returnValue(users));
        }});

        List<User> result = userService.getUsers(userFilter, limit, offset);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUserLites delegates to DAO
     */
    @Test
    public void test_getUserLites()
    {
        final List<UserLite> userLites = new ArrayList<>();
        userLites.add(userLite);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserLites();
            will(returnValue(userLites));
        }});

        List<UserLite> result = userService.getUserLites();

        assertEquals(userLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUserLites with limit and offset
     */
    @Test
    public void test_getUserLites_with_pagination()
    {
        final int limit = 10;
        final int offset = 0;
        final List<UserLite> userLites = new ArrayList<>();
        userLites.add(userLite);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserLites(limit, offset);
            will(returnValue(userLites));
        }});

        List<UserLite> result = userService.getUserLites(limit, offset);

        assertEquals(userLites, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test changePassword throws UnsupportedOperationException
     */
    @Test(expected = UnsupportedOperationException.class)
    public void test_changePassword_unsupported()
    {
        userService.changePassword("oldPassword", "newPassword");
    }

    /**
     * Test createUser with UserDetails
     */
    @Test
    public void test_createUser_with_userDetails()
    {
        final String username = "testUser";
        final String password = "password123";
        final String email = "test@example.com";
        final String encodedPassword = "encodedPassword";
        final User newUser = mockery.mock(User.class, "newUser");

        mockery.checking(new Expectations()
        {{
            allowing(user).getUsername();
            will(returnValue(username));

            allowing(user).getPassword();
            will(returnValue(password));

            allowing(user).getEmail();
            will(returnValue(email));

            allowing(user).getFirstName();
            will(returnValue("John"));

            allowing(user).getSurname();
            will(returnValue("Doe"));

            allowing(user).getDepartment();
            will(returnValue("IT"));

            allowing(user).isRequiresPasswordChange();
            will(returnValue(false));

            oneOf(userDao).getUser(username);
            will(returnValue(null));

            oneOf(securityService).createPrincipal();
            will(returnValue(principal));

            oneOf(principal).setName(username);
            oneOf(principal).setType("user");
            oneOf(principal).setDescription(username + " user principal.");

            oneOf(securityService).savePrincipal(principal);

            oneOf(passwordEncoder).encode(password);
            will(returnValue(encodedPassword));

            oneOf(userDao).createUser(username, encodedPassword, email, true);
            will(returnValue(newUser));

            oneOf(newUser).setFirstName("John");
            oneOf(newUser).setSurname("Doe");
            oneOf(newUser).setDepartment("IT");
            oneOf(newUser).addPrincipal(principal);
            oneOf(newUser).setRequiresPasswordChange(false);

            oneOf(userDao).save(newUser);
        }});

        userService.createUser(user);

        mockery.assertIsSatisfied();
    }

    /**
     * Test createUser with empty username throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_createUser_empty_username()
    {
        mockery.checking(new Expectations()
        {{
            allowing(user).getUsername();
            will(returnValue(""));

            allowing(user).getPassword();
            will(returnValue("password"));

            allowing(user).getEmail();
            will(returnValue("me@there.com"));

            allowing(user).getFirstName();
            will(returnValue("Trevor"));

            allowing(user).getSurname();
            will(returnValue("Apps"));

            allowing(user).getDepartment();
            will(returnValue("Town Planning"));

            allowing(user).isRequiresPasswordChange();
            will(returnValue(true));
        }});

        userService.createUser(user);
    }

    /**
     * Test createUser with empty password throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_createUser_empty_password()
    {
        mockery.checking(new Expectations()
        {{
            allowing(user).getUsername();
            will(returnValue("testUser"));

            allowing(user).getPassword();
            will(returnValue(""));

            allowing(user).getEmail();
            will(returnValue("me@there.com"));

            allowing(user).getFirstName();
            will(returnValue("Trevor"));

            allowing(user).getSurname();
            will(returnValue("Apps"));

            allowing(user).getDepartment();
            will(returnValue("Town Planning"));

            allowing(user).isRequiresPasswordChange();
            will(returnValue(true));
        }});

        userService.createUser(user);
    }

    /**
     * Test createUser with empty email throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_createUser_empty_email()
    {
        mockery.checking(new Expectations()
        {{
            allowing(user).getUsername();
            will(returnValue("testUser"));

            allowing(user).getPassword();
            will(returnValue("password"));

            allowing(user).getEmail();
            will(returnValue(""));

            allowing(user).getFirstName();
            will(returnValue("Trevor"));

            allowing(user).getSurname();
            will(returnValue("Apps"));

            allowing(user).getDepartment();
            will(returnValue("Town Planning"));

            allowing(user).isRequiresPasswordChange();
            will(returnValue(true));
        }});

        userService.createUser(user);
    }

    /**
     * Test createUser with existing username throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_createUser_existing_username()
    {
        final String username = "existingUser";

        mockery.checking(new Expectations()
        {{
            allowing(user).getUsername();
            will(returnValue(username));

            allowing(user).getPassword();
            will(returnValue("password"));

            allowing(user).getEmail();
            will(returnValue("test@example.com"));

            allowing(user).getEmail();
            will(returnValue("me@there.com"));

            allowing(user).getFirstName();
            will(returnValue("Trevor"));

            allowing(user).getSurname();
            will(returnValue("Apps"));

            allowing(user).getDepartment();
            will(returnValue("Town Planning"));

            allowing(user).isRequiresPasswordChange();
            will(returnValue(true));

            oneOf(userDao).getUser(username);
            will(returnValue(user));
        }});

        userService.createUser(user);
    }

    /**
     * Test deleteUser delegates to DAO
     */
    @Test
    public void test_deleteUser()
    {
        final String username = "testUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(userDao).delete(user);
        }});

        userService.deleteUser(username);

        mockery.assertIsSatisfied();
    }

    /**
     * Test deleteUser with non-existent user throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_deleteUser_nonexistent()
    {
        final String username = "nonExistentUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(null));
        }});

        userService.deleteUser(username);
    }

    /**
     * Test disableUser delegates to DAO
     */
    @Test
    public void test_disableUser()
    {
        final String username = "testUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).setEnabled(false);

            oneOf(userDao).save(user);
        }});

        userService.disableUser(username);

        mockery.assertIsSatisfied();
    }

    /**
     * Test enableUser delegates to DAO
     */
    @Test
    public void test_enableUser()
    {
        final String username = "testUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).setEnabled(true);

            oneOf(userDao).save(user);
        }});

        userService.enableUser(username);

        mockery.assertIsSatisfied();
    }

    /**
     * Test updateUser delegates to DAO
     */
    @Test
    public void test_updateUser()
    {
        mockery.checking(new Expectations()
        {{
            oneOf(userDao).save(user);
        }});

        userService.updateUser(user);

        mockery.assertIsSatisfied();
    }

    /**
     * Test userExists returns true for existing user
     */
    @Test
    public void test_userExists_true()
    {
        final String username = "existingUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));
        }});

        boolean result = userService.userExists(username);

        assertTrue(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test userExists returns false for non-existent user
     */
    @Test
    public void test_userExists_false()
    {
        final String username = "nonExistentUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(null));
        }});

        boolean result = userService.userExists(username);

        assertFalse(result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test loadUserByUsername returns enabled user
     */
    @Test
    public void test_loadUserByUsername_enabled()
    {
        final String username = "testUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(true));
        }});

        User result = userService.loadUserByUsername(username);

        assertEquals(user, result);
        mockery.assertIsSatisfied();
    }

    /**
     * Test loadUserByUsername throws exception for disabled user
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_loadUserByUsername_disabled()
    {
        final String username = "disabledUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(false));
        }});

        userService.loadUserByUsername(username);
    }

    /**
     * Test loadUserByUsername throws exception for non-existent user
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_loadUserByUsername_nonexistent()
    {
        final String username = "nonExistentUser";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(null));
        }});

        userService.loadUserByUsername(username);
    }

    /**
     * Test loadUserByUsername with preventLocalAuthentication enabled
     */
    @Test(expected = UsernameNotFoundException.class)
    public void test_loadUserByUsername_preventLocalAuthentication()
    {
        UserServiceImpl service = new UserServiceImpl(userDao, securityService, passwordEncoder, true);

        service.loadUserByUsername("testUser");
    }

    /**
     * Test getAuthorities delegates to SecurityService
     */
    @Test
    public void test_getAuthorities()
    {
        final List<Policy> policies = new ArrayList<>();
        policies.add(policy);

        mockery.checking(new Expectations()
        {{
            oneOf(securityService).getAllPolicies();
            will(returnValue(policies));
        }});

        List<Policy> result = userService.getAuthorities();

        assertEquals(policies, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test grantAuthority
     */
    @Test
    public void test_grantAuthority()
    {
        final String username = "testUser";
        final String authority = "ReadPolicy";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(true));

            oneOf(securityService).findPolicyByName(authority);
            will(returnValue(policy));

            oneOf(user).getUsername();
            will(returnValue(username));

            oneOf(securityService).findPrincipalByName(username);
            will(returnValue(principal));

            oneOf(securityService).findRoleByName("User");
            will(returnValue(role));

            oneOf(user).addPrincipal(principal);
            oneOf(principal).addRole(role);
            oneOf(role).addPolicy(policy);

            oneOf(userDao).save(user);
        }});

        userService.grantAuthority(username, authority);

        mockery.assertIsSatisfied();
    }

    /**
     * Test revokeAuthority
     */
    @Test
    public void test_revokeAuthority()
    {
        final String username = "testUser";
        final String authority = "ReadPolicy";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(true));

            oneOf(securityService).findPolicyByName(authority);
            will(returnValue(policy));

            oneOf(user).revokePolicy(policy);

            oneOf(userDao).save(user);
        }});

        userService.revokeAuthority(username, authority);

        mockery.assertIsSatisfied();
    }

    /**
     * Test changeUsersPassword with matching passwords
     */
    @Test
    public void test_changeUsersPassword_success()
    {
        final String username = "testUser";
        final String newPassword = "newPassword123";
        final String confirmPassword = "newPassword123";
        final String encodedPassword = "encodedNewPassword";

        mockery.checking(new Expectations()
        {{
            oneOf(passwordEncoder).encode(newPassword);
            will(returnValue(encodedPassword));

            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(true));

            oneOf(user).setPassword(encodedPassword);
            oneOf(user).setEnabled(true);

            oneOf(userDao).save(user);
        }});

        userService.changeUsersPassword(username, newPassword, confirmPassword);

        mockery.assertIsSatisfied();
    }

    /**
     * Test changeUsersPassword with mismatched passwords throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_changeUsersPassword_mismatch()
    {
        final String username = "testUser";
        final String newPassword = "newPassword123";
        final String confirmPassword = "differentPassword";

        userService.changeUsersPassword(username, newPassword, confirmPassword);
    }

    /**
     * Test changeUsersEmail
     */
    @Test
    public void test_changeUsersEmail()
    {
        final String username = "testUser";
        final String newEmail = "newemail@example.com";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(user));

            oneOf(user).isEnabled();
            will(returnValue(true));

            oneOf(user).setEmail(newEmail);

            oneOf(userDao).save(user);
        }});

        userService.changeUsersEmail(username, newEmail);

        mockery.assertIsSatisfied();
    }

    /**
     * Test changeUsersEmail with non-existent user throws exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_changeUsersEmail_nonexistent()
    {
        final String username = "nonExistentUser";
        final String newEmail = "newemail@example.com";

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUser(username);
            will(returnValue(null));
        }});

        userService.changeUsersEmail(username, newEmail);
    }

    /**
     * Test getUserByUsernameLike delegates to DAO
     */
    @Test
    public void test_getUserByUsernameLike()
    {
        final String username = "test%";
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserByUsernameLike(username);
            will(returnValue(users));
        }});

        List<User> result = userService.getUserByUsernameLike(username);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUserByFirstnameLike delegates to DAO
     */
    @Test
    public void test_getUserByFirstnameLike()
    {
        final String firstname = "John%";
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserByFirstnameLike(firstname);
            will(returnValue(users));
        }});

        List<User> result = userService.getUserByFirstnameLike(firstname);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }

    /**
     * Test getUserBySurnameLike delegates to DAO
     */
    @Test
    public void test_getUserBySurnameLike()
    {
        final String surname = "Doe%";
        final List<User> users = new ArrayList<>();
        users.add(user);

        mockery.checking(new Expectations()
        {{
            oneOf(userDao).getUserBySurnameLike(surname);
            will(returnValue(users));
        }});

        List<User> result = userService.getUserBySurnameLike(surname);

        assertEquals(users, result);
        assertEquals(1, result.size());
        mockery.assertIsSatisfied();
    }
}
