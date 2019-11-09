package org.ikasan.systemevent.service;

import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.systemevent.SystemEventDao;
import org.ikasan.systemevent.model.SystemEventImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SystemEventServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
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

        List<SystemEvent> events = Arrays.asList();

        mockery.checking(new Expectations()
        {
            {
                oneOf(systemEventDao).getHarvestableRecords(10);
                will(returnValue(events));
            }
        });

        uut.harvest(10);

        mockery.assertIsSatisfied();

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
}
