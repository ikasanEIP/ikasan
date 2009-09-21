/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.initiator.scheduled.quartz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>HolidayCalendarFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class HolidayCalendarFactoryTest
{

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mock objects
     */
    final List<Calendar> excludedDates = mockery.mock(ArrayList.class);
    final org.quartz.Calendar baseCalendar = mockery.mock(org.quartz.Calendar.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test successful creation of a holidayCalendarFactory instance 
     * and associated retrieval of a Holiday Calendar instance
     * without any setter invocation.
     * @throws Exception 
     */
    @Test
    public void test_successfulHolidayCalendarFactory()
        throws Exception
    {
        //
        // create factory
        HolidayCalendarFactory holidayCalendarFactory = new HolidayCalendarFactory();
        
        //
        // simple interaction tests
        assertFalse(holidayCalendarFactory.isSingleton());
        assertEquals(holidayCalendarFactory.getObjectType().getName(), 
            "org.quartz.impl.calendar.HolidayCalendar");
        holidayCalendarFactory.getObject();
    }

    /**
     * Test successful creation of a holidayCalendar instance with excluded date
     * list setter.
     */
    @Test
    public void test_successfulHolidayCalendarFactory_setExcludeDates()
    {
        //
        // run simple tests
        HolidayCalendarFactory holidayCalendarFactory = new HolidayCalendarFactory();
        holidayCalendarFactory.setExcludedDates(excludedDates);
    }

    /**
     * Test successful creation of a holidayCalendar instance with a
     * base calendar setter.
     */
    @Test
    public void test_successfulHolidayCalendarFactory_setBaseCalendar()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get base calendar description
                exactly(1).of(baseCalendar).getDescription();
                will(returnValue("baseCalendarDescription"));
            }
        });
        
        //
        // run simple tests
        HolidayCalendarFactory holidayCalendarFactory = new HolidayCalendarFactory();
        holidayCalendarFactory.setBaseCalendar(baseCalendar);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        mockery.assertIsSatisfied();
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(HolidayCalendarFactoryTest.class);
    }

}
