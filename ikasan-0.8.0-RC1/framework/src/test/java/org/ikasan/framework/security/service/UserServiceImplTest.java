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
package org.ikasan.framework.security.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.security.dao.AuthorityDao;
import org.ikasan.framework.security.dao.UserDao;
import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.model.User;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;

/**
 * Unit Test class for testing the org.ikasan.framework.security.service.UserServiceImpl
 * 
 * @author Ikasan Development Team
 * 
 */
public class UserServiceImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock User Dao to test with */
    UserDao userDao = mockery.mock(UserDao.class);

    /** Mock Authority Dao to test with */
    AuthorityDao authorityDao = mockery.mock(AuthorityDao.class);

    /** Mock Password Encoder to test with */
    PasswordEncoder passwordEncoder = mockery.mock(PasswordEncoder.class);

    /** User Service Impl that we are testing */
    private UserServiceImpl userServiceImpl = new UserServiceImpl(userDao, authorityDao, passwordEncoder);

    /**
     * Test that granting an authority to a User works
     */
    @Test
    public void testGrantAuthority()
    {
        final String username = "username";
        final String authority = "authority";
        final User user = mockery.mock(User.class);
        final Authority authorityGrant = mockery.mock(Authority.class);
        mockery.checking(new Expectations()
        {
            {
                one(userDao).getUser(username);
                will(returnValue(user));
                one(authorityDao).getAuthority(authority);
                will(returnValue(authorityGrant));
                one(user).grantAuthority(authorityGrant);
                one(userDao).save(user);
            }
        });
        userServiceImpl.grantAuthority(username, authority);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that revoking an authority from a User works
     */
    @Test
    public void testRevokeAuthority()
    {
        final String username = "username";
        final String authority = "authority";
        final User user = mockery.mock(User.class);
        final Authority authorityGrant = mockery.mock(Authority.class);
        mockery.checking(new Expectations()
        {
            {
                one(userDao).getUser(username);
                will(returnValue(user));
                one(authorityDao).getAuthority(authority);
                will(returnValue(authorityGrant));
                one(user).revokeAuthority(authorityGrant);
                one(userDao).save(user);
            }
        });
        userServiceImpl.revokeAuthority(username, authority);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that changing a User's password works
     */
    @Test
    public void testChangePassword()
    {
        final String username = "username";
        final String newPassword = "newPassword";
        final String confirmNewPassword = "newPassword";
        final String encodedPassword = "encodedPassword";
        final User user = mockery.mock(User.class);
        mockery.checking(new Expectations()
        {
            {
                one(passwordEncoder).encodePassword(newPassword, null);
                will(returnValue(encodedPassword));
                one(userDao).getUser(username);
                will(returnValue(user));
                one(user).setPassword(encodedPassword);
                one(userDao).save(user);
            }
        });
        userServiceImpl.changeUsersPassword(username, newPassword, confirmNewPassword);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when providing valid credentials a valid user is created
     */
    @Test
    public void createUser_withValidCredentialsWillCreateUser()
    {
        final String username = "username";
        final String password = "password";
        final String email = "email";
        final String encodedPassword = "encodedPassword";
        final boolean enabled = true;
        final User existingUser = null;
        final UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, password, email, enabled, userDetails);
        expectsDaoGetUser(username, existingUser);
        expectCreateUserWillCreateUser(username, password, encodedPassword, email, enabled);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when given a null user name, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withNullUsernameWillThrowException()
    {
        String password = "password";
        String email = "email";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(null, password, email, enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when given an empty user name, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withEmpyUsernameWillThrowException()
    {
        String password = "password";
        String email = "email";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials("", password, email, enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test that when given a null password, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withNullPasswordWillThrowException()
    {
        String username = "username";
        String email = "email";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, null, email, enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when given an empty password, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withEmptyPasswordWillThrowException()
    {
        String username = "username";
        String email = "email";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, "", email, enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when given a null email address, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withNullEmailWillThrowException()
    {
        String username = "username";
        String password = "password";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        // check null password throws exception
        expectCreateUserWillExtractCredentials(username, password, null, enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when given an empty email address, createUser throws an IllegalArgumentException
     * exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withEmptyEmailWillThrowException()
    {
        String username = "username";
        String password = "password";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, password, "", enabled, userDetails);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test that trying to create a user with a username that already exists
     * throws an IllegalArgumentException
     */
    @Test(expected=IllegalArgumentException.class)
    public void createUser_withExistingUsernameWillThrowException()
    {
        String password = "password";
        String username = "username";
        String email = "email";
        boolean enabled = true;
        UserDetails userDetails = mockery.mock(User.class);
        // check existing username throws exception
        final User existingUser = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, password, email, enabled, userDetails);
        expectsDaoGetUser(username, existingUser);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when deleting an unknown user an IllegalArgumentException 
     * is thrown 
     */
    @Test(expected=IllegalArgumentException.class)
    public void deleteUser_withUnknownUsernameWillThrowException()
    {
        String username = "username";
        expectsDaoGetUser(username, null);
        userServiceImpl.deleteUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that disabling a user works
     */
    @Test
    public void disableUser()
    {
        String username = "username";
        final User existingUser = mockery.mock(User.class);
        expectsDaoGetUser(username, existingUser);
        mockery.checking(new Expectations()
        {
            {
                one(existingUser).setEnabled(false);
                one(userDao).save(existingUser);
            }
        });
        userServiceImpl.disableUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that when trying to disable an unknown user an
     * IllegalArgumentException is thrown 
     */
    @Test(expected=IllegalArgumentException.class)
    public void disableUser_withUnknownUsernameWillThrowException()
    {
        String username = "username";
        expectsDaoGetUser(username, null);
        userServiceImpl.disableUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that enableUser works 
     */
    @Test
    public void enableUser()
    {
        String username = "username";
        final User existingUser = mockery.mock(User.class);
        expectsDaoGetUser(username, existingUser);
        mockery.checking(new Expectations()
        {
            {
                one(existingUser).setEnabled(true);
                one(userDao).save(existingUser);
            }
        });
        userServiceImpl.enableUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that calling enableUser with an unknown username throws an 
     * IllegalArgumentException
     */
    @Test(expected=IllegalArgumentException.class)
    public void enableUser_withUnknownUsernameWillThrowException()
    {
        String username = "username";
        expectsDaoGetUser(username, null);
        userServiceImpl.enableUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that deleteUser() works as expected
     */
    @Test
    public void deleteUser()
    {
        String username = "username";
        final User existingUser = mockery.mock(User.class);
        expectsDaoGetUser(username, existingUser);
        mockery.checking(new Expectations()
        {
            {
                one(userDao).delete(existingUser);
            }
        });
        userServiceImpl.deleteUser(username);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that createAuthority() works as expected
     */
    @Test
    public void createAuthority()
    {
        final Authority newAuthority = new Authority("authority");
        final List<Authority> existingAuthorities = new ArrayList<Authority>();
        mockery.checking(new Expectations()
        {
            {
                one(authorityDao).getAuthorities();
                will(returnValue(existingAuthorities));
                one(authorityDao).save(newAuthority);
            }
        });
        userServiceImpl.createAuthority(newAuthority);
        mockery.assertIsSatisfied();
    }

    /**
     * Test that trying to create an already existing authority throws
     * an IllegalArgumentException exception 
     */
    @Test(expected=IllegalArgumentException.class)
    public void createAuthority_withExistingAuthorityWillThrowException()
    {
        final Authority existingAuthority = new Authority("authority");
        final List<Authority> existingAuthorities = new ArrayList<Authority>();
        existingAuthorities.add(existingAuthority);
        mockery.checking(new Expectations()
        {
            {
                one(authorityDao).getAuthorities();
                will(returnValue(existingAuthorities));
            }
        });
        userServiceImpl.createAuthority(existingAuthority);
        mockery.assertIsSatisfied();
    }

    private void expectCreateUserWillCreateUser(final String username, final String password,
            final String encodedPassword, final String email, final boolean enabled)
    {
        mockery.checking(new Expectations()
        {
            {
                one(passwordEncoder).encodePassword(password, null);
                will(returnValue(encodedPassword));
                one(userDao).save(with(equal(new User(username, encodedPassword, email, enabled))));
            }
        });
    }

    private void expectsDaoGetUser(final String username, final User existingUser)
    {
        mockery.checking(new Expectations()
        {
            {
                one(userDao).getUser(username);
                will(returnValue(existingUser));
            }
        });
    }

    private void expectCreateUserWillExtractCredentials(final String username, final String password,
            final String email, final boolean enabled, final UserDetails userDetails)
    {
        mockery.checking(new Expectations()
        {
            {
                one(userDetails).getUsername();
                will(returnValue(username));
                one(userDetails).getPassword();
                will(returnValue(password));
                one((User) userDetails).getEmail();
                will(returnValue(email));
                one(userDetails).isEnabled();
                will(returnValue(enabled));
            }
        });
    }
}
