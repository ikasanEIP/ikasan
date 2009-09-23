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

import junit.framework.Assert;

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

    UserDao userDao = mockery.mock(UserDao.class);

    AuthorityDao authorityDao = mockery.mock(AuthorityDao.class);

    PasswordEncoder passwordEncoder = mockery.mock(PasswordEncoder.class);

    private UserServiceImpl userServiceImpl = new UserServiceImpl(userDao, authorityDao, passwordEncoder);

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

    @Test
    public void testChangePassword()
    {
        final String username = "username";
        final String newPassword = "newPassword";
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
        userServiceImpl.changeUsersPassword(username, newPassword);
        mockery.assertIsSatisfied();
    }

    @Test
    public void createUser_withValidCredentialsWillCreateUser()
    {
        final String username = "username";
        final String password = "password";
        final String encodedPassword = "encodedPassword";
        final boolean enabled = true;
        final User existingUser = null;
        final UserDetails userDetails = mockery.mock(UserDetails.class);
        expectCreateUserWillExtractCredentials(username, password, enabled, userDetails);
        expectsDaoGetUser(username, existingUser);
        expectCreateUserWillCreateUser(username, password, encodedPassword, enabled);
        userServiceImpl.createUser(userDetails);
        mockery.assertIsSatisfied();
    }
    
    
    @Test
    public void createUser_withNullOrEmptyUsernameWillThrowException()
    {

        String password = "password";
        boolean enabled = true;

        UserDetails userDetails = mockery.mock(UserDetails.class);
        
        
        //check null username throws exception
        expectCreateUserWillExtractCredentials(null, password, enabled, userDetails);
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.createUser(userDetails);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for null username",illegalArgumentException);
        mockery.assertIsSatisfied();
        
        //check empty username throws exception
        expectCreateUserWillExtractCredentials("", password, enabled, userDetails);
        illegalArgumentException = null;
        try{
            userServiceImpl.createUser(userDetails);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for empty username",illegalArgumentException);
        mockery.assertIsSatisfied();        
        
    }
    
    @Test
    public void createUser_withNullOrEmptyPasswordWillThrowException()
    {

        String username = "username";
        boolean enabled = true;

        UserDetails userDetails = mockery.mock(UserDetails.class);
        
        
        //check null password throws exception
        expectCreateUserWillExtractCredentials(username, null, enabled, userDetails);
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.createUser(userDetails);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for null password",illegalArgumentException);
        mockery.assertIsSatisfied();
        
        //check empty password throws exception
        expectCreateUserWillExtractCredentials(username, "", enabled, userDetails);
        illegalArgumentException = null;
        try{
            userServiceImpl.createUser(userDetails);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for empty password",illegalArgumentException);
        mockery.assertIsSatisfied();        
        
    }

    @Test
    public void createUser_withExistingUsernameWillThrowException()
    {

        String password = "password";
        String username = "username";
        boolean enabled = true;

        UserDetails userDetails = mockery.mock(UserDetails.class);
        
        
        //check existing username throws exception
        final User existingUser = mockery.mock(User.class);
        expectCreateUserWillExtractCredentials(username, password, enabled, userDetails);
        expectsDaoGetUser(username, existingUser);
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.createUser(userDetails);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for existing username",illegalArgumentException);
        mockery.assertIsSatisfied();
        
        
        
    }
    
    @Test
    public void deleteUser_withUnknownUsernameWillThrowException()
    {

        String username = "username";
        expectsDaoGetUser(username, null);
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.deleteUser(username);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for unknown username",illegalArgumentException);
        mockery.assertIsSatisfied();        
        
    }
    
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
    

    @Test
    public void disableUser_withUnknownUsernameWillThrowException()
    {

        String username = "username";
        expectsDaoGetUser(username, null);
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.disableUser(username);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for unknown username",illegalArgumentException);
        mockery.assertIsSatisfied();        
        
    }
    
    
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
    

    @Test
    public void enableUser_withUnknownUsernameWillThrowException()
    {

        String username = "username";
        expectsDaoGetUser(username, null);
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.enableUser(username);
            Assert.fail("Exception should have been thrown");
        } catch(IllegalArgumentException caughtException){
            illegalArgumentException = caughtException;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown for unknown username",illegalArgumentException);
        mockery.assertIsSatisfied();        
        
    }
    
    
    
    
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
    
    @Test
    public void createAuthority()
    {
        final Authority newAuthority = new Authority("authority");
        final List<Authority> existingAuthorities = new ArrayList<Authority>();
        
        mockery.checking(new Expectations()
        {
            {
                one(authorityDao).getAuthorities();will(returnValue(existingAuthorities));
                one(authorityDao).save(newAuthority);
            }
        });
        
        userServiceImpl.createAuthority(newAuthority);

        mockery.assertIsSatisfied();    
    }
    
    @Test
    public void createAuthority_withExistingAuthorityWillThrowException()
    {
        final Authority existingAuthority = new Authority("authority");
        final List<Authority> existingAuthorities = new ArrayList<Authority>();
        existingAuthorities.add(existingAuthority);
        
        mockery.checking(new Expectations()
        {
            {
                one(authorityDao).getAuthorities();will(returnValue(existingAuthorities));
            }
        });
        
        IllegalArgumentException illegalArgumentException = null;
        try{
            userServiceImpl.createAuthority(existingAuthority);
            Assert.fail("Exception should have been thrown");
        } catch (IllegalArgumentException iae){
            illegalArgumentException = iae;
        }
        Assert.assertNotNull("IllegalArgumentException should have been thrown if tried to create an existing authority", illegalArgumentException);

        mockery.assertIsSatisfied();    
    }

    private void expectCreateUserWillCreateUser(final String username, final String password, final String encodedPassword, final boolean enabled)
    {
        mockery.checking(new Expectations()
        {
            {
                one(passwordEncoder).encodePassword(password, null);
                will(returnValue(encodedPassword));
                one(userDao).save(with(equal(new User(username, encodedPassword, enabled))));
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

    private void expectCreateUserWillExtractCredentials(final String username, final String password, final boolean enabled, final UserDetails userDetails)
    {
        mockery.checking(new Expectations()
        {
            {
                one(userDetails).getUsername();
                will(returnValue(username));
                one(userDetails).getPassword();
                will(returnValue(password));
                one(userDetails).isEnabled();
                will(returnValue(enabled));
            }
        });
    }
}
