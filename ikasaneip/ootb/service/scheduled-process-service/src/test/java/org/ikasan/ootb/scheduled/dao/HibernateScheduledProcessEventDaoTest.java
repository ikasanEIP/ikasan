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
package org.ikasan.ootb.scheduled.dao;

import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.ootb.scheduled.model.ScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.ScheduledProcessEventDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/h2-config.xml",
    "/substitute-components.xml",
})
public class HibernateScheduledProcessEventDaoTest {

    @Resource
    private ScheduledProcessEventDao scheduledProcessEventDao;

    @Resource
    private LocalSessionFactoryBean sessionFactoryBean;

    @Test
    @DirtiesContext
    public void test() {
        ScheduledProcessEvent scheduledProcessEvent = this.getEvent();

        this.scheduledProcessEventDao.save(scheduledProcessEvent);

        Assert.assertNotNull(((ScheduledProcessEventImpl)scheduledProcessEvent).getId());
    }

    @Test
    @DirtiesContext
    public void test_records_to_harvest_exist() {
        ScheduledProcessEvent scheduledProcessEvent = this.getEvent();

        this.scheduledProcessEventDao.save(scheduledProcessEvent);

        Assert.assertTrue(this.scheduledProcessEventDao.harvestableRecordsExist());
    }

    @Test
    @DirtiesContext
    public void test_get_harvestable_records() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessEventDao.save(this.getEvent()));

        Assert.assertEquals(5, this.scheduledProcessEventDao.harvest(5).size());
    }

    @Test
    @DirtiesContext
    public void test_get_mark_as_harvested() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessEventDao.save(this.getEvent()));

        this.scheduledProcessEventDao.updateAsHarvested(this.scheduledProcessEventDao.harvest(5));
        this.scheduledProcessEventDao.updateAsHarvested(this.scheduledProcessEventDao.harvest(5));

        Assert.assertFalse(this.scheduledProcessEventDao.harvestableRecordsExist());
    }

    @Test
    @DirtiesContext
    public void test_housekeep() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessEventDao.save(this.getEvent()));

        this.scheduledProcessEventDao.updateAsHarvested(this.scheduledProcessEventDao.harvest(5));
        this.scheduledProcessEventDao.updateAsHarvested(this.scheduledProcessEventDao.harvest(5));

        this.scheduledProcessEventDao.housekeep();

        HibernateScheduledProcessEventDao dao = new HibernateScheduledProcessEventDao();
        dao.setSessionFactory(this.sessionFactoryBean.getObject());

        Assert.assertEquals(0, dao.findAll().size());
    }


    private ScheduledProcessEvent getEvent() {
        ScheduledProcessEventImpl scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setAgentName("agentName");
        scheduledProcessEvent.setAgentHostname("hostname");
        scheduledProcessEvent.setCommandLine("commandLine");
        scheduledProcessEvent.setFireTime(1000L);
        scheduledProcessEvent.setNextFireTime(2000L);
        scheduledProcessEvent.setJobDescription("jodDescription");
        scheduledProcessEvent.setJobGroup("jobGroup");
        scheduledProcessEvent.setJobName("jobName");
        scheduledProcessEvent.setPid(111111);
        scheduledProcessEvent.setResultOutput("output");
        scheduledProcessEvent.setReturnCode(1);
        scheduledProcessEvent.setSuccessful(false);
        scheduledProcessEvent.setCompletionTime(3000L);
        scheduledProcessEvent.setUser("user");
        scheduledProcessEvent.setOutcome(Outcome.EXECUTION_INVOKED);

        return scheduledProcessEvent;
    }
}
