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
package org.ikasan.systemevent.dao;

import java.util.Date;

import javax.annotation.Resource;

import org.ikasan.systemevent.model.SystemEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/systemevent-service-conf.xml"
})
public class HibernateSystemEventDaoTest
{
    /** Object being tested */
    @Resource private SystemEventDao systemEventDao;

    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before public void setup()
    {    	
    	
    	for(int i=0; i< 10000; i++)
    	{
	    	systemEventDao.save(new SystemEvent("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
    	}
    	
    }

    /**
     * Putting an instance of StateModel into the StateModel store
     * 
     */
    @Test 
    @DirtiesContext
    public void test_success_no_results_sybase()
    {
		systemEventDao.setHousekeepQuery("delete top _bs_ from SystemEvent where Expiry <= getdate()");   //sybase
    }

	@Test
	@DirtiesContext
	public void test_success_no_results_mysql()
	{
		systemEventDao.setHousekeepQuery("delete top _bs_ from SystemEvent where Expiry <= now()"); //mysql
	}

	@Test
	@DirtiesContext
	public void test_success_no_results_mssql()
	{
		systemEventDao.setHousekeepQuery("delete top ( _bs_ ) from SystemEvent where Expiry <= getdate()"); //mssql
	}

	@After
	public void process()
	{
		systemEventDao.setBatchHousekeepDelete(true);
		systemEventDao.setHousekeepingBatchSize(100);
		systemEventDao.setTransactionBatchSize(2000);
		while(systemEventDao.housekeepablesExist())
    	{
    		this.systemEventDao.deleteExpired();
    	}
		for(int i=0; i< 13456; i++)
        {
            systemEventDao.save(new SystemEvent("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }
		systemEventDao.setHousekeepingBatchSize(1000);
		systemEventDao.setTransactionBatchSize(20000);
		this.systemEventDao.deleteExpired();
		for(int i=0; i< 79; i++)
        {
            systemEventDao.save(new SystemEvent("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }
		this.systemEventDao.deleteExpired();
	}
}
