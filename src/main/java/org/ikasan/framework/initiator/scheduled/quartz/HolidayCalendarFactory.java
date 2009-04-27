/*
 * $Id: HolidayCalendarFactory.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/scheduled/quartz/HolidayCalendarFactory.java $
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

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.impl.calendar.HolidayCalendar;

/**
 * Spring FactoryBean implementation for the Quartz Holiday Calendar.
 * 
 * This factory allows the setting of holiday dates to be excluded from a schedule via a list of Calendars.
 * 
 * Additionally, any Quartz Calendar may be set as a base calendar to allow overlay (stacking) of calendars).
 * 
 * @author Ikasan Development Team
 */
public class HolidayCalendarFactory implements org.springframework.beans.factory.FactoryBean
{
    /** Logger */
    private static Logger logger = Logger.getLogger(HolidayCalendarFactory.class);

    /** Any base calendar for stacking calendar instances */
    private org.quartz.Calendar baseCalendar;

    /** List of calendar dates to exclude */
    private List<Calendar> excludedDates;

    /**
     * Set a new list of excluded java.util.Calendar dates for the holiday calendar.
     * 
     * @param excludedDates - The excluded dates to set
     */
    public void setExcludedDates(List<Calendar> excludedDates)
    {
        this.excludedDates = excludedDates;
        if (logger.isDebugEnabled())
        {
            logger.debug("HolidayCalendar excluded date list [" + this.excludedDates.toString() + "]");
        }
    }

    /**
     * Set the base org.quartz.Calendar for this holiday calendar.
     * 
     * @param baseCalendar - The base quartz calendar
     */
    public void setBaseCalendar(org.quartz.Calendar baseCalendar)
    {
        this.baseCalendar = baseCalendar;
        if (logger.isDebugEnabled())
        {
            logger.debug("HolidayCalendar baseCalendar description [" + this.baseCalendar.getDescription() + "]");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
    {
        HolidayCalendar holidayCalendar = new HolidayCalendar();
        if (this.baseCalendar != null)
        {
            holidayCalendar.setBaseCalendar(baseCalendar);
        }
        if (this.excludedDates != null)
        {
            for (Calendar excludedDate : this.excludedDates)
            {
                holidayCalendar.addExcludedDate(excludedDate.getTime());
            }
        }
        return holidayCalendar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType()
    {
        return org.quartz.impl.calendar.HolidayCalendar.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }
}
