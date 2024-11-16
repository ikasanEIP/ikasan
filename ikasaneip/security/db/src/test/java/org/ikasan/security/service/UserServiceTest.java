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

import org.ikasan.security.SecurityAutoConfiguration;
import org.ikasan.security.SecurityTestAutoConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.dao.UserDao;
import org.ikasan.security.model.*;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityAutoConfiguration.class, SecurityTestAutoConfiguration.class})
@Sql(scripts = {"/setupSecurityData.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/teardownSecurityData.sql"}, executionPhase = AFTER_TEST_METHOD)
public class UserServiceTest
{
    @Autowired
    private SecurityService xaSecurityService;
    @Autowired
    private UserService xaUserService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup()
    {
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testGetUser()
    {
        User user = this.xaUserService.loadUserByUsername("username");

        Assert.assertNotNull(user);

        UsernameNotFoundException exception = null;
        try {
            user = this.xaUserService.loadUserByUsername("bad name");
        }
        catch (UsernameNotFoundException e) {
            exception = e;
        }

        Assert.assertNotNull(exception);
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithRole()
    {
        List<UserLite> users = this.xaUserService.getUsersWithRole("admin", new UserFilter(), 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        users = this.xaUserService.getUsersWithRole("admin", new UserFilter(), 0, 0);
        Assert.assertEquals(0, users.size());

        users = this.xaUserService.getUsersWithRole("admin", new UserFilter(), 100, 1);
        Assert.assertEquals(0, users.size());

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithoutRole()
    {
        List<UserLite> users = this.xaUserService.getUsersWithoutRole("admin", new UserFilter(), 100, 0);
        Assert.assertEquals(9, users.size());

        users = this.xaUserService.getUsersWithoutRole("admin", new UserFilter(), 0, 0);
        Assert.assertEquals(0, users.size());

        users = this.xaUserService.getUsersWithoutRole("admin", new UserFilter(), 100, 3);
        Assert.assertEquals(6, users.size());

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");
        userFilter.setSortOrder("ASCENDING");
        userFilter.setSortColumn("username");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());
        Assert.assertEquals("username1", users.get(0).getUsername());
        Assert.assertEquals("username2", users.get(1).getUsername());
        Assert.assertEquals("username3", users.get(2).getUsername());
        Assert.assertEquals("username4", users.get(3).getUsername());
        Assert.assertEquals("username5", users.get(4).getUsername());
        Assert.assertEquals("username6", users.get(5).getUsername());
        Assert.assertEquals("username7", users.get(6).getUsername());
        Assert.assertEquals("username8", users.get(7).getUsername());
        Assert.assertEquals("username9", users.get(8).getUsername());

        userFilter.setSortOrder("DESCENDING");

        users = this.xaUserService.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        Assert.assertEquals("username1", users.get(8).getUsername());
        Assert.assertEquals("username2", users.get(7).getUsername());
        Assert.assertEquals("username3", users.get(6).getUsername());
        Assert.assertEquals("username4", users.get(5).getUsername());
        Assert.assertEquals("username5", users.get(4).getUsername());
        Assert.assertEquals("username6", users.get(3).getUsername());
        Assert.assertEquals("username7", users.get(2).getUsername());
        Assert.assertEquals("username8", users.get(1).getUsername());
        Assert.assertEquals("username9", users.get(0).getUsername());
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithoutRoleCount()
    {
        int users = this.xaUserService.getUsersWithoutRoleCount("admin", new UserFilter());
        Assert.assertEquals(9, users);

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setNameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");
        userFilter.setSortOrder("ASCENDING");
        userFilter.setSortColumn("username");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setSortOrder("DESCENDING");

        users = this.xaUserService.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithRoleCount()
    {
        int users = this.xaUserService.getUsersWithRoleCount("admin", new UserFilter());
        Assert.assertEquals(1, users);

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setNameFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);
    }


    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testGetUserByFirstNameLike()
    {
        List<User> users = this.xaUserService.getUserByFirstnameLike("first");
        Assert.assertNotNull(users.size() == 1);
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testGetUserBySurnameLike()
    {
        List<User> users = this.xaUserService.getUserBySurnameLike("sur");
        Assert.assertNotNull(users.size() == 1);
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
     */
    @Test
    @DirtiesContext
    public void testGetUserByUsernameLike()
    {
        List<User> users = this.xaUserService.getUserByUsernameLike("user");

        Assert.assertNotNull(users.size() == 1);
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUsers()}.
     */
    @Test
    @DirtiesContext
    public void testGetUsers()
    {
        List<User> users = this.xaUserService.getUsers();

        Assert.assertTrue(users.size() == 10);
    }

    @Test
    @DirtiesContext
    public void testGetUsersLimitOffset()
    {
        UserFilter userFilter = new UserFilter();

        List<User> users = this.xaUserService.getUsers(userFilter, 1, 0);

        Assert.assertTrue(users.size() == 1);

        users = this.xaUserService.getUsers(userFilter, 1, 100);

        Assert.assertTrue(users.size() == 0);

        users = this.xaUserService.getUsers(userFilter, 3, 5);

        Assert.assertTrue(users.size() == 3);

        users = this.xaUserService.getUsers(userFilter,3, 9);

        Assert.assertTrue(users.size() == 1);

        userFilter.setUsernameFilter("name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");
        userFilter.setSortOrder("ASCENDING");
        userFilter.setSortColumn("username");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());
        Assert.assertEquals("username1", users.get(1).getUsername());
        Assert.assertEquals("username2", users.get(2).getUsername());
        Assert.assertEquals("username3", users.get(3).getUsername());
        Assert.assertEquals("username4", users.get(4).getUsername());
        Assert.assertEquals("username5", users.get(5).getUsername());
        Assert.assertEquals("username6", users.get(6).getUsername());
        Assert.assertEquals("username7", users.get(7).getUsername());
        Assert.assertEquals("username8", users.get(8).getUsername());
        Assert.assertEquals("username9", users.get(9).getUsername());

        userFilter.setSortOrder("DESCENDING");

        users = this.xaUserService.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        Assert.assertEquals("username", users.get(9).getUsername());
        Assert.assertEquals("username1", users.get(8).getUsername());
        Assert.assertEquals("username2", users.get(7).getUsername());
        Assert.assertEquals("username3", users.get(6).getUsername());
        Assert.assertEquals("username4", users.get(5).getUsername());
        Assert.assertEquals("username5", users.get(4).getUsername());
        Assert.assertEquals("username6", users.get(3).getUsername());
        Assert.assertEquals("username7", users.get(2).getUsername());
        Assert.assertEquals("username8", users.get(1).getUsername());
        Assert.assertEquals("username9", users.get(0).getUsername());
    }

    @Test
    @DirtiesContext
    public void testGetUserLitesLimitOffset()
    {
        List<UserLite> users = this.xaUserService.getUserLites(1, 0);

        Assert.assertTrue(users.size() == 1);

        users = this.xaUserService.getUserLites(1, 100);

        Assert.assertTrue(users.size() == 0);

        users = this.xaUserService.getUserLites(3, 5);

        Assert.assertTrue(users.size() == 3);

        users = this.xaUserService.getUserLites(3, 9);

        Assert.assertTrue(users.size() == 1);
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUsers()}.
     */
    @Test
    @DirtiesContext
    public void testAuthorities()
    {
        List<User> users = this.xaUserService.getUsers();

        Assert.assertTrue(users.size() == 10);
        Assert.assertTrue(users.get(0).getAuthorities().size() == 4);

        users.get(0).getAuthorities().forEach(grantedAuthority -> {
            if(grantedAuthority instanceof ModuleGrantedAuthority) {
                Assert.assertEquals("MODULE:sample module", grantedAuthority.getAuthority());
            }
            else if(grantedAuthority instanceof JobPlanGrantedAuthority) {
                Assert.assertEquals("JOB_PLAN:sample job plan", grantedAuthority.getAuthority());
            }
            else {
                Assert.assertTrue(grantedAuthority.getAuthority().equals("policy1") ||
                    grantedAuthority.getAuthority().equals("policy2"));
            }
        });
    }

    @Test
    @DirtiesContext
    public void testDelete()
    {
        User user = this.xaUserService.loadUserByUsername("username");

        this.xaUserService.deleteUser(user.getUsername());

        List<User> users = this.xaUserService.getUsers();

        Assert.assertTrue(users.size() == 9);
    }
}
