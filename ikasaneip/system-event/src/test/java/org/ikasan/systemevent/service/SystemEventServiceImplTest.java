package org.ikasan.systemevent.service;

import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.spec.systemevent.SystemEventService;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/h2-config.xml", "/transaction-conf.xml",
    "/systemevent-service-conf.xml", "/test-conf.xml"
})
@Sql(scripts = "classpath:drop-system-event-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"classpath:create-system-event-table.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SystemEventServiceImplTest
{
    @Resource
    private SystemEventService<SystemEvent> systemEventService;

    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private SystemEventDao systemEventDao = mockery.mock(SystemEventDao.class, "mockSystemEventDao");

    private SystemEventServiceImpl uut;

    @Before
    public void setup()
    {
        uut = new SystemEventServiceImpl(systemEventDao, 100l);
    }

    @Test
    public void harvest()
    {
        uut = new SystemEventServiceImpl(systemEventDao, 100l, new TestModuleContainer());

        List<SystemEvent> events = new ArrayList<>();

        for(int i=0; i<10; i++) {
            events.add(new SystemEventImpl("subject", "action", new Date(), "actor", new Date()));
        }

        mockery.checking(new Expectations()
        {
            {
                oneOf(systemEventDao).getHarvestableRecords(10);
                will(returnValue(events));
            }
        });

        List<SystemEvent> results = uut.harvest(10);

        mockery.assertIsSatisfied();

        events.forEach(systemEvent -> Assert.assertEquals("Module name equals!", "name", systemEvent.getModuleName()));
    }

    @Test
    public void updateAsHarvested()
    {

        List<SystemEvent> events = Arrays.asList();

        mockery.checking(new Expectations()
        {
            {
                oneOf(systemEventDao).updateAsHarvested(events);
            }
        });

        uut.updateAsHarvested(events);
        mockery.assertIsSatisfied();

    }

    @Test
    public void saveHarvestedRecord()
    {


        SystemEvent event = new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000));
        mockery.checking(new Expectations()
        {
            {
                oneOf(systemEventDao).save(event);
            }
        });

        uut.saveHarvestedRecord(event);

        mockery.assertIsSatisfied();

    }

    @Test
    public void logEvent()
    {


        SystemEvent event = new SystemEventImpl("subject", "action", new Date(), "actor", new Date(System.currentTimeMillis() - 1000000000));
        mockery.checking(new Expectations()
        {
            {
                oneOf(systemEventDao).save(with(any(SystemEventImpl.class)));
            }
        });

        uut.logSystemEvent("subject", "action","actor");

        mockery.assertIsSatisfied();

    }

    @Test
    public void logEvent_long_subject()
    {
        this.systemEventService.logSystemEvent("""
            aasdssssssssssadsadasdasdasdasdasdasdasdasdasdasddassadasd\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
            """,
            "action", "actor"
            );

        Assert.assertEquals(1024, this.systemEventService.listSystemEvents
            (null, "actor", new Date(0L), new Date(System.currentTimeMillis()+10000000L)).get(0).getSubject().length());
    }

    @Test
    public void logEvent_long_subject_with_module_name()
    {
        this.systemEventService.logSystemEvent("moduleName", """
                aasdssssssssssadsadasdasdasdasdasdasdasdasdasdasddassadasd\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                asdasdasasdasdasdasdafsgghdrtgwadfgadfadfasdfadsfadfasdfadsfasfadsfadfadfasdfasdfasdfasdfasdfadfadfadfa\
                """,
            "action", "actor"
        );

        Assert.assertEquals(1024, this.systemEventService.listSystemEvents
            (null, "actor", new Date(0L), new Date(System.currentTimeMillis()+10000000L)).get(0).getSubject().length());
    }
}
