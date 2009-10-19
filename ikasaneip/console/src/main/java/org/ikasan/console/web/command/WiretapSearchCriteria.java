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
package org.ikasan.console.web.command;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.console.web.controller.MasterDetailControllerUtil;

/**
 * Command class capturing the Wiretap search criteria fields
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteria implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3595514737829632181L;

    /** Simple date format definition for days months and years */
    private SimpleDateFormat ddMMyyyyFormat;

    /** Simple date format definition for hours minutes and seconds */
    private SimpleDateFormat HHmmss;

    /** The logger */
    private Logger logger = Logger.getLogger(WiretapSearchCriteria.class);

    /** Set of module ids whose wiretapped events we're going to search for */
    private Set<Long> moduleIds;

    /** Name of component to restrict by */
    private String componentName;

    /** Event Id to restrict by */
    private String eventId;

    /** Payload Id to restrict by */
    private String payloadId;

    /** Payload content to restrict by */
    private String payloadContent;

    /** From date to search on */
    private String fromDate;

    /** To date to search on */
    private String untilDate;

    /** From time to search on */
    private String fromTime;

    /** To time to search on */
    private String untilTime;
    
    /**
     * Constructor
     * 
     * @param moduleIds - Set of module ids (will ensure that check boxes
     *            are pre-checked)
     */
    public WiretapSearchCriteria(Set<Long> moduleIds)
    {
        this.ddMMyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.ddMMyyyyFormat.setLenient(false);
        this.HHmmss = new SimpleDateFormat("HH:mm:ss");
        this.HHmmss.setLenient(false);
        this.moduleIds = moduleIds;
    }

    /**
     * Get the serial uid
     * 
     * @return serial uid
     */
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    /**
     * Get the component name
     * 
     * @return component name
     */
    public String getComponentName()
    {
        return this.componentName;
    }

    /**
     * Get the event id
     * 
     * @return event id
     */
    public String getEventId()
    {
        return this.eventId;
    }

    /**
     * Get the payload content
     * 
     * @return the payload content
     */
    public String getPayloadContent()
    {
        return this.payloadContent;
    }

    /**
     * Get the payload id
     * 
     * @return The payload id
     */
    public String getPayloadId()
    {
        return this.payloadId;
    }

    /**
     * Set the component name
     * 
     * @param componentName - component name to set
     */
    public void setComponentName(String componentName)
    {
        this.componentName = MasterDetailControllerUtil.nullForEmpty(componentName);
    }

    /**
     * Set the event id
     * 
     * @param eventId - event id to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = MasterDetailControllerUtil.nullForEmpty(eventId);
    }

    /**
     * Set the payload content
     * 
     * @param payloadContent - payload content to set
     */
    public void setPayloadContent(String payloadContent)
    {
        this.payloadContent = MasterDetailControllerUtil.nullForEmpty(payloadContent);
    }

    /**
     * Set the payload id
     * 
     * @param payloadId - payload id to set
     */
    public void setPayloadId(String payloadId)
    {
        this.payloadId = MasterDetailControllerUtil.nullForEmpty(payloadId);
    }

    /**
     * Get a set of the modules
     * 
     * @return set of modules
     */
    public Set<Long> getModules()
    {
        return this.moduleIds;
    }

    /**
     * Set the modules
     * 
     * @param modules - Set of modules to set
     */
    public void setModules(Set<Long> modules)
    {
        this.moduleIds = modules;
    }

    /**
     * Get the from date
     * 
     * @return from date
     */
    public String getFromDate()
    {
        return this.fromDate;
    }

    /**
     * Set the from date
     * 
     * @param fromDate - from date to set
     */
    public void setFromDate(String fromDate)
    {
        this.fromDate = fromDate;
    }

    /**
     * Get the to date
     * 
     * @return to date
     */
    public String getUntilDate()
    {
        return this.untilDate;
    }

    /**
     * Set the to date
     * 
     * @param untilDate - to date to set
     */
    public void setUntilDate(String untilDate)
    {
        this.untilDate = untilDate;
    }

    /**
     * Get the from time
     * 
     * @return from time
     */
    public String getFromTime()
    {
        return this.fromTime;
    }

    /**
     * Set the from time
     * 
     * @param fromTime - from time to set
     */
    public void setFromTime(String fromTime)
    {
        this.fromTime = fromTime;
    }

    /**
     * Get the to time
     * 
     * @return to date
     */
    public String getUntilTime()
    {
        return this.untilTime;
    }

    /**
     * Set the to time
     * 
     * @param untilTime - to time to set
     */
    public void setUntilTime(String untilTime)
    {
        this.untilTime = untilTime;
    }

    /**
     * Get the from date time object
     * 
     * @return from date_time object
     */
    public Date getFromDateTime()
    {
        return getDateTime(this.fromDate, this.fromTime);
    }

    /**
     * Get the to date time object
     * 
     * @return to date_time object
     */
    public Date getUntilDateTime()
    {
        return getDateTime(this.untilDate, this.untilTime);
    }

    /**
     * Helper method to get the date_time, avoids code duplication
     * 
     * @param date - Date part
     * @param time - Time part
     * @return Date/time
     */
    private Date getDateTime(String date, String time)
    {
        Date createDateTime = createDateTime(date, time);
        return createDateTime;
    }

    /**
     * Helper method to create date and time from user supplied strings
     * 
     * @param dateString - The date component
     * @param timeString - the time component
     * @return A Java representation of the Date
     */
    private Date createDateTime(String dateString, String timeString)
    {
        if (dateString == null || "".equals(dateString))
        {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        try
        {
            Date time = null;
            // If we get an invalid time then set it to empty String where the ParseException code will deal with it
            // We're deliberately being strict, see the Validator class for this domain object
            if (timeString == null)
            {
                time = this.HHmmss.parse("");
            }
            else
            {
                time = this.HHmmss.parse(timeString);
            }
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(time);
            calendar.setTime(this.ddMMyyyyFormat.parse(dateString));
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        }
        catch (ParseException pe)
        {
            logger.debug("Could not parse date and/or time correctly.");
            return null;
        }
        return calendar.getTime();
    }
}
