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
