package org.ikasan.ootb.scheduled.dao;

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

        return scheduledProcessEvent;
    }
}
