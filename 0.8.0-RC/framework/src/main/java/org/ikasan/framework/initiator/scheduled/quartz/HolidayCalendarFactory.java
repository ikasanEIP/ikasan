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
