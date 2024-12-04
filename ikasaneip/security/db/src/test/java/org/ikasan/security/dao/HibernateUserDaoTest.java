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
package org.ikasan.security.dao;

import org.ikasan.security.SecurityAutoConfiguration;
import org.ikasan.security.SecurityTestAutoConfiguration;
import org.ikasan.security.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SecurityAutoConfiguration.class, SecurityTestAutoConfiguration.class})
@Sql(scripts = {"/setupSecurityData.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/teardownSecurityData.sql"}, executionPhase = AFTER_TEST_METHOD)
public class HibernateUserDaoTest
{
	@Autowired
	private UserDao xaUserDao;

    @Autowired
    private SecurityDao xaSecurityDao;

	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
	 */
	@Test
	@DirtiesContext
	public void testGetUser()
	{
		User user = this.xaUserDao.getUser("username");
		
		Assert.assertNotNull(user);
		
		user = this.xaUserDao.getUser("bad name");
		
		Assert.assertNull(user);
	}

    @Test
    @DirtiesContext
    public void testGetUsersWithRole()
    {
        List<UserLite> users = this.xaUserDao.getUsersWithRole("admin", new UserFilter(), 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        users = this.xaUserDao.getUsersWithRole("admin", new UserFilter(), 0, 0);
        Assert.assertEquals(0, users.size());

        users = this.xaUserDao.getUsersWithRole("admin", new UserFilter(), 100, 1);
        Assert.assertEquals(0, users.size());

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals("username", users.get(0).getUsername());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithoutRole()
    {
        List<UserLite> users = this.xaUserDao.getUsersWithoutRole("admin", new UserFilter(), 100, 0);
        Assert.assertEquals(9, users.size());

        users = this.xaUserDao.getUsersWithoutRole("admin", new UserFilter(), 0, 0);
        Assert.assertEquals(0, users.size());

        users = this.xaUserDao.getUsersWithoutRole("admin", new UserFilter(), 100, 3);
        Assert.assertEquals(6, users.size());

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
        Assert.assertEquals(9, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
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

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
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

        users = this.xaUserDao.getUsersWithoutRole("admin", userFilter, 100, 0);
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
        int users = this.xaUserDao.getUsersWithoutRoleCount("admin", new UserFilter());
        Assert.assertEquals(9, users);

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
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

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);

        userFilter.setSortOrder("DESCENDING");

        users = this.xaUserDao.getUsersWithoutRoleCount("admin", userFilter);
        Assert.assertEquals(9, users);
    }

    @Test
    @DirtiesContext
    public void testGetUsersWithRoleCount()
    {
        int users = this.xaUserDao.getUsersWithRoleCount("admin", new UserFilter());
        Assert.assertEquals(1, users);

        UserFilter userFilter = new UserFilter();
        userFilter.setUsernameFilter("name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(1, users);

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsersWithRoleCount("admin", userFilter);
        Assert.assertEquals(0, users);
    }


    /**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
	 */
	@Test
	@DirtiesContext
	public void testGetUserByFirstNameLike()
	{
		List<User> users = this.xaUserDao.getUserByFirstnameLike("first");
		Assert.assertNotNull(users.size() == 1);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
	 */
	@Test
	@DirtiesContext
	public void testGetUserBySurnameLike()
	{
		List<User> users = this.xaUserDao.getUserBySurnameLike("sur");
		Assert.assertNotNull(users.size() == 1);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUser(java.lang.String)}.
	 */
	@Test
	@DirtiesContext
	public void testGetUserByUsernameLike()
	{
		List<User> users = this.xaUserDao.getUserByUsernameLike("user");
		
		Assert.assertNotNull(users.size() == 1);
	}

	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUsers()}.
	 */
	@Test
	@DirtiesContext
	public void testGetUsers()
	{
		List<User> users = this.xaUserDao.getUsers();
		
		Assert.assertTrue(users.size() == 10);
	}

    @Test
    @DirtiesContext
    public void testGetUsersLimitOffset()
    {
        UserFilter userFilter = new UserFilter();

        List<User> users = this.xaUserDao.getUsers(userFilter, 1, 0);

        Assert.assertTrue(users.size() == 1);

        users = this.xaUserDao.getUsers(userFilter, 1, 100);

        Assert.assertTrue(users.size() == 0);

        users = this.xaUserDao.getUsers(userFilter, 3, 5);

        Assert.assertTrue(users.size() == 3);

        users = this.xaUserDao.getUsers(userFilter,3, 9);

        Assert.assertTrue(users.size() == 1);

        userFilter.setUsernameFilter("name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setUsernameFilter("NAme");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setUsernameFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("dep");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter = new UserFilter();
        userFilter.setDepartmentFilter("DEP");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setDepartmentFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("fir");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter = new UserFilter();
        userFilter.setNameFilter("FIR");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setNameFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("sur");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter = new UserFilter();
        userFilter.setLastNameFilter("SUR");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setLastNameFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@LASTNAME");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(0, users.size());

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@lastname");
        userFilter.setUsernameFilter("name");
        userFilter.setNameFilter("fir");
        userFilter.setLastNameFilter("sur");
        userFilter.setDepartmentFilter("dep");
        userFilter.setEmailFilter("@lastname");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
        Assert.assertEquals(10, users.size());

        userFilter.setEmailFilter("bad-name");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
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

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
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

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
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

        userFilter = new UserFilter();
        userFilter.setEmailFilter("@LAST");
        userFilter.setUsernameFilter("NAME");
        userFilter.setNameFilter("FIR");
        userFilter.setLastNameFilter("Sur");
        userFilter.setDepartmentFilter("DEP");
        userFilter.setSortOrder("ASCENDING");
        userFilter.setSortColumn("username");

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
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

        users = this.xaUserDao.getUsers(userFilter, 100, 0);
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
        List<UserLite> users = this.xaUserDao.getUserLites(1, 0);

        Assert.assertTrue(users.size() == 1);

        users = this.xaUserDao.getUserLites(1, 100);

        Assert.assertTrue(users.size() == 0);

        users = this.xaUserDao.getUserLites(3, 5);

        Assert.assertTrue(users.size() == 3);

        users = this.xaUserDao.getUserLites(3, 9);

        Assert.assertTrue(users.size() == 1);
    }

    /**
     * Test method for {@link org.ikasan.security.dao.HibernateUserDao#getUsers()}.
     */
    @Test
    @DirtiesContext
    public void testAuthorities()
    {
        List<User> users = this.xaUserDao.getUsers();

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

	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateUserDao#delete(org.ikasan.security.model.User)}.
	 */
	@Test
	@DirtiesContext
	public void testDelete()
	{
		User user = this.xaUserDao.getUser("username");
		
		this.xaUserDao.delete(user);
		
		List<User> users = this.xaUserDao.getUsers();
		
		Assert.assertTrue(users.size() == 9);
	}

    @Test
    @DirtiesContext
    public void test_add_principals_to_user()
    {
        List<User> users = this.xaUserDao.getUsers();

        Assert.assertEquals(10, users.size());

        User user = users.get(0);

        List<IkasanPrincipal> principals = this.xaSecurityDao.getAllPrincipals();

        Assert.assertEquals(2, principals.size());
        Assert.assertEquals(2, user.getPrincipals().size());

        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName("name");
        principal.setDescription("description");
        principal.setType("type");
        principal.setApplicationSecurityBaseDn("baseDn");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        principals = this.xaSecurityDao.getAllPrincipals();
        Assert.assertEquals(3, principals.size());

        Assert.assertTrue(principals.size() > 0);

        principals.forEach(p -> user.addPrincipal(p));

        this.xaUserDao.save(user);

        Assert.assertEquals(3, user.getPrincipals().size());

        principal = new IkasanPrincipal();
        principal.setName("another name");
        principal.setDescription("another description");
        principal.setType("another type");
        principal.setApplicationSecurityBaseDn("another baseDn");

        this.xaSecurityDao.saveOrUpdatePrincipal(principal);

        User dbUser = this.xaUserDao.getUser(user.getUsername());

        Assert.assertEquals(3, dbUser.getPrincipals().size());

        dbUser.addPrincipal(principal);

        this.xaUserDao.save(dbUser);

        dbUser = this.xaUserDao.getUser(user.getUsername());

        Assert.assertEquals(4, dbUser.getPrincipals().size());
    }
}
