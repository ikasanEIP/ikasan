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

import java.util.List;

import javax.annotation.Resource;

import org.ikasan.security.model.Authority;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/security-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
@Ignore
public class HibernateAuthorityDaoTest
{

	@Resource
	private AuthorityDao xaAuthorityDao;
	
	/**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     * @throws SecurityDaoException 
     */
    @Before public void setup()
    {
    	for(int i=0; i<10; i++)
    	{
    		Authority authority = new Authority("authority" + i);
    		this.xaAuthorityDao.save(authority);
    	}

    }
    
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testGetAuthorities()
	{
		List<Authority> authorities = this.xaAuthorityDao.getAuthorities();
		
		Assert.assertTrue(authorities.size() == 10);
	}

	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthority(java.lang.String)}.
	 */
	@Test
	@DirtiesContext
	public void testGetAuthority()
	{
		Authority authority = this.xaAuthorityDao.getAuthority("authority1");
		
		Assert.assertNotNull(authority);
		
		authority = this.xaAuthorityDao.getAuthority("does not exist");
		
		Assert.assertNull(authority);
	}

}
