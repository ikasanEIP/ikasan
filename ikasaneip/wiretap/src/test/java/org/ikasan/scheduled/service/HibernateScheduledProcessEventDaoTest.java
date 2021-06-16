package org.ikasan.scheduled.service;

import org.ikasan.scheduled.dao.HibernateScheduledProcessEventDao;
import org.ikasan.scheduled.model.ScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.ScheduledProcessService;
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
    private ScheduledProcessServiceImpl scheduledProcessService;

    @Test
    @DirtiesContext
    public void test() {
        ScheduledProcessEvent scheduledProcessEvent = this.getEvent();

        this.scheduledProcessService.save(scheduledProcessEvent);

        Assert.assertNotNull(((ScheduledProcessEventImpl)scheduledProcessEvent).getId());
    }

    @Test
    @DirtiesContext
    public void test_records_to_harvest_exist() {
        ScheduledProcessEvent scheduledProcessEvent = this.getEvent();

        this.scheduledProcessService.save(scheduledProcessEvent);

        Assert.assertTrue(this.scheduledProcessService.harvestableRecordsExist());
    }

    @Test
    @DirtiesContext
    public void test_get_harvestable_records() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessService.save(this.getEvent()));

        Assert.assertEquals(5, this.scheduledProcessService.harvest(5).size());
    }

    @Test
    @DirtiesContext
    public void test_get_mark_as_harvested() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessService.save(this.getEvent()));

        this.scheduledProcessService.updateAsHarvested(this.scheduledProcessService.harvest(5));
        this.scheduledProcessService.updateAsHarvested(this.scheduledProcessService.harvest(5));

        Assert.assertFalse(this.scheduledProcessService.harvestableRecordsExist());
    }

    @Test
    @DirtiesContext
    public void test_housekeep() {
        IntStream.range(0, 10).forEach(i -> this.scheduledProcessService.save(this.getEvent()));

        this.scheduledProcessService.updateAsHarvested(this.scheduledProcessService.harvest(5));
        this.scheduledProcessService.updateAsHarvested(this.scheduledProcessService.harvest(5));

        this.scheduledProcessService.housekeep();
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
        scheduledProcessEvent.setResult(1);
        scheduledProcessEvent.setUser("user");

        return scheduledProcessEvent;
    }
}
