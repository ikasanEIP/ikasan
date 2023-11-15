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

import org.ikasan.security.SecurityConfiguration;
import org.ikasan.security.TestImportConfig;
import org.ikasan.security.model.JobPlanGrantedAuthority;
import org.ikasan.security.model.ModuleGrantedAuthority;
import org.ikasan.security.model.User;
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
@ContextConfiguration(classes = {SecurityConfiguration.class,TestImportConfig.class})
@Sql(scripts = {"/setupSecurityData.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/teardownSecurityData.sql"}, executionPhase = AFTER_TEST_METHOD)
public class HibernateUserDaoTest
{
	@Autowired
	private UserDao xaUserDao;

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

        Assert.assertTrue(users.size() == 1);
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
		
		Assert.assertTrue(users.size() == 0);
	}

}
