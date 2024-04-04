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

import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.systemevent.SystemEventAutoConfiguration;
import org.ikasan.systemevent.SystemEventTestAutoConfiguration;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SystemEventAutoConfiguration.class, SystemEventTestAutoConfiguration.class})
@Sql(scripts = "classpath:drop-system-event-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:create-system-event-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class HibernateSystemEventDaoTest
{

    /** Object being tested */
    @Autowired
    private SystemEventDao systemEventDao;

    @Autowired
    private SystemEventDao deleteOnceHarvestedSystemEventDao;

    @Test
    public void test_deleteExpiredWithBatchHousekeepDeleteTrueAndTransactionBatchSize2000() {
        systemEventDao.setBatchHousekeepDelete(true);
        systemEventDao.setHousekeepingBatchSize(100);
        systemEventDao.setTransactionBatchSize(2000);

        for(int i=0; i< 10000; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date()
                , "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        while(systemEventDao.housekeepablesExist())
        {
            this.systemEventDao.deleteExpired();
        }

        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(0, result.size());
    }

    @Test
    public void test_deleteExpiredWithBatchHousekeepDeleteFalse() {
        systemEventDao.setBatchHousekeepDelete(false);

        for(int i=0; i< 10000; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date()
                , "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        while(systemEventDao.housekeepablesExist())
        {
            this.systemEventDao.deleteExpired();
        }

        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(0, result.size());
    }

    @Test
    public void test_deleteOnceHarvestedWithBatchHousekeepDeleteTrueAndTransactionBatchSize2000() {
        deleteOnceHarvestedSystemEventDao.setBatchHousekeepDelete(true);
        deleteOnceHarvestedSystemEventDao.setHousekeepingBatchSize(100);
        deleteOnceHarvestedSystemEventDao.setTransactionBatchSize(2000);

        for(int i=0; i< 10000; i++)
        {
            SystemEventImpl systemEvent = new SystemEventImpl("subject", "action", new Date()
                , "actor", new Date(System.currentTimeMillis() - 1000000000));
            systemEvent.setHarvested(true);
            deleteOnceHarvestedSystemEventDao.save(systemEvent);
        }

        while(deleteOnceHarvestedSystemEventDao.housekeepablesExist())
        {
            this.deleteOnceHarvestedSystemEventDao.deleteExpired();
        }

        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(0, result.size());
    }

    @Test
    public void test_deleteOnceHarvestedWithBatchHousekeepDeleteFalse() {
        deleteOnceHarvestedSystemEventDao.setBatchHousekeepDelete(false);

        for(int i=0; i< 10000; i++)
        {
            SystemEventImpl systemEvent = new SystemEventImpl("subject", "action", new Date()
                , "actor", new Date(System.currentTimeMillis() - 1000000000));
            systemEvent.setHarvested(true);
            deleteOnceHarvestedSystemEventDao.save(systemEvent);
        }

        while(deleteOnceHarvestedSystemEventDao.housekeepablesExist())
        {
            this.deleteOnceHarvestedSystemEventDao.deleteExpired();
        }

        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(0, result.size());
    }

    @Test
    public void test_deleteExpiredWithBatchHousekeepDeleteTrueAndTransactionBatchSize20000() {
        systemEventDao.setBatchHousekeepDelete(true);
        systemEventDao.setHousekeepingBatchSize(1000);
        systemEventDao.setTransactionBatchSize(20000);

        for(int i=0; i< 23456; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        this.systemEventDao.deleteExpired();

        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(3456, result.size());
    }


    @Test
    public void test_harvesting() {
        this.systemEventDao.setOrderHarvestQuery(true);
        for(int i=0; i< 20; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        List<SystemEvent> events = this.systemEventDao.getHarvestableRecords(15);

        assertEquals(15, events.size());

        this.systemEventDao.updateAsHarvested(events);


        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(20, result.size());
        assertEquals(15, result.stream()
                               .filter(systemEvent -> ((SystemEventImpl)systemEvent).isHarvested()).count());
    }

    @Test
    public void test_harvesting_no_order_by() {
        this.systemEventDao.setOrderHarvestQuery(false);
        for(int i=0; i< 20; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        List<SystemEvent> events = this.systemEventDao.getHarvestableRecords(15);

        this.systemEventDao.updateAsHarvested(events);


        List<SystemEvent> result = systemEventDao.list(null,null,null,null);

        assertEquals(15, result.stream()
            .filter(systemEvent -> ((SystemEventImpl)systemEvent).isHarvested()).count());
    }

    @Test
    public void test_harvesting_no_order_by_with_gap() {
        this.systemEventDao.setOrderHarvestQuery(false);
        for(int i=0; i< 20; i++)
        {
            systemEventDao.save(new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000)));
        }

        List<SystemEvent> events = this.systemEventDao.getHarvestableRecords(3);

        this.systemEventDao.updateAsHarvested(List.of(events.get(1)));


        events = this.systemEventDao.getHarvestableRecords(3);

        assertEquals(Long.valueOf(1L), events.get(0).getId());
        assertEquals(Long.valueOf(3L), events.get(1).getId());
        assertEquals(Long.valueOf(4L), events.get(2).getId());
    }
}
