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
package org.ikasan.wiretap.dao;

import javax.annotation.Resource;

import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/hsqldb-config.xml",
        "/substitute-components.xml",
})
public class HibernateWiretapDaoTest
{
	/** Object being tested */
	@Resource private WiretapDao wiretapDao;

//	/**
//	 * Before each test case, inject a mock {@link HibernateTemplate} to hibernate implementation
//	 * being tested
//	 */
//	@Before
//	public void setup()
//	{
//
//		for(int i=0; i< 10000; i++)
//		{
//			WiretapFlowEvent event = new WiretapFlowEvent("moduleName", "flowName", "componentName",
//					"eventId", "relatedEventId", System.currentTimeMillis() ,"event", System.currentTimeMillis() - 1000000000);
//
//			this.wiretapDao.save(event);
//		}
//
//	}

	@Test
	@DirtiesContext
	public void test_get_harvestableRecords()
	{
		System.out.println("Getting harvestable records");

        long startTime = System.currentTimeMillis();
	    List<WiretapEvent> events = wiretapDao.getHarvestableRecords(50);
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));

//		Assert.assertEquals("Wiretap events should equal!", events.size(), 50);

		wiretapDao.updateAsHarvested(events);

        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
		events = wiretapDao.getHarvestableRecords(1000);
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        events = wiretapDao.getHarvestableRecords(1000);
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();

        wiretapDao.updateAsHarvested(events);

        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime));

//		Assert.assertEquals("Wiretap events should equal!", events.size(), 9950);
        System.out.println("Finished getting harvestable records");
	}

//	@Test
//	@DirtiesContext
//	public void test_success_no_results_sybase()
//	{
//		wiretapDao.setHousekeepQuery("delete top _bs_ from IkasanWiretap where Expiry <= _ex_");   //sybase
//	}
//
//	@Test
//	@DirtiesContext
//	public void test_success_no_results_mssql()
//	{
//		wiretapDao.setHousekeepQuery("delete top ( _bs_ ) from IkasanWiretap where Expiry <= _ex_"); //mssql
//	}
//
//	@Test
//	@DirtiesContext
//	public void test_success_no_results_mysql()
//	{
//		wiretapDao.setHousekeepQuery("delete from IkasanWiretap where Expiry <= _ex_ limit _bs_"); //mysql
//	}
//
//	@After
//	public void process()
//	{
//		wiretapDao.setBatchHousekeepDelete(true);
//		wiretapDao.setHousekeepingBatchSize(100);
//		wiretapDao.setTransactionBatchSize(2000);
//		this.wiretapDao.deleteAllExpired();
//		this.wiretapDao.deleteAllExpired();
//		this.wiretapDao.deleteAllExpired();
//		this.wiretapDao.deleteAllExpired();
//		this.wiretapDao.deleteAllExpired();
//	}
}
