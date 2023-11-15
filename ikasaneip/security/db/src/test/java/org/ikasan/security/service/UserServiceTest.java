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

import org.ikasan.security.SecurityConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityConfiguration.class, TestImportConfig.class })
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class UserServiceTest
{
    private User admin;
    private  User disabledUser;

    @Autowired
    private UserDao xaUserDao;
    @Autowired
    private UserService xaUserService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup()
    {
        admin = new User("admin", "admin", "admin@admin.com",true);
        disabledUser = new User("disabledUser", "disabledUser", "disabledUser@admin.com",false);

        xaUserDao.save(admin);
        xaUserDao.save(disabledUser);
    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#getUsers()}.
     */
    @Test
    @DirtiesContext
    public void testGetUsers()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#changePassword(java.lang.String, java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testChangePassword()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#createUser(org.springframework.security.core.userdetails.UserDetails)}.
     */
    @Test
    @DirtiesContext
    public void testCreateUser()
    {

    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#deleteUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testDeleteUser()
    {

    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#disableUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testDisableUser()
    {

    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#enableUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testEnableUser()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#updateUser(org.springframework.security.core.userdetails.UserDetails)}.
     */
    @Test
    @DirtiesContext
    public void testUpdateUser()
    {

    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#userExists(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testUserExists()
    {

    }

    @Test
    @DirtiesContext
    public void testLoadUserByUsername()
    {
        User result = xaUserService.loadUserByUsername("admin");

        Assert.assertEquals(admin.getEmail(),result.getEmail());
        Assert.assertEquals(admin.getUsername(),result.getUsername());
        Assert.assertEquals(admin.getId(),result.getId());
    }

    @Test
    @DirtiesContext
    public void testLoadUserByUsernameWhenUserIsDisabled()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Disabled username : disabledUser contact administrator.");

        User result = xaUserService.loadUserByUsername("disabledUser");

        Assert.assertEquals(admin,result);
    }

    @Test
    @DirtiesContext
    public void testLoadUserByUsernameWhenUserDoesntExist()
    {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("Unknown username : unknown");

        User result = xaUserService.loadUserByUsername("unknown");

        Assert.assertEquals(admin,result);
    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#getAuthorities()}.
     */
    @Test
    @DirtiesContext
    public void testGetAuthorities()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#grantAuthority(java.lang.String, java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testGrantAuthority()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#revokeAuthority(java.lang.String, java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testRevokeAuthority()
    {

    }

    /**
     * Test method for
     * {@link org.ikasan.security.service.UserServiceImpl#changeUsersPassword(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testChangeUsersPassword()
    {

    }

    /**
     * Test method for {@link org.ikasan.security.service.UserServiceImpl#changeUsersEmail(java.lang.String, java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testChangeUsersEmail()
    {

    }

}
