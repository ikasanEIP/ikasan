/*
 * $Id: WiretapSearchCriteria.java 16798 2009-04-24 14:12:09Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/java/org/ikasan/framework/web/command/WiretapSearchCriteria.java $
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
package org.ikasan.framework.web.command;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Command class capturing the Wiretap search criteria fields
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteria implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3595514737829632181L;
    
    /** Logger for the class */
    private Logger logger  = Logger.getLogger(WiretapSearchCriteria.class);

    /** Constructor */
    public WiretapSearchCriteria()
    {
        ddMMyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        ddMMyyyyFormat.setLenient(false);
        HHmmss = new SimpleDateFormat("HH:mm:ss");
        HHmmss.setLenient(false);
    }

    /** Simple date format definition for days months and years */
    private SimpleDateFormat ddMMyyyyFormat;

    /** Simple date format definition for hours minutes and seconds */
    private SimpleDateFormat HHmmss;

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
     * Set of names of modules whose wiretapped events to include
     * 
     * Note that this must be non empty
     */
    private Set<String> modules;

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
     * Get the component name
     * 
     * @return component name
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Get the event id
     * 
     * @return event id
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * Get the payload content
     * 
     * @return the payload content
     */
    public String getPayloadContent()
    {
        return payloadContent;
    }

    /**
     * Get the payload id
     * 
     * @return The payload id
     */
    public String getPayloadId()
    {
        return payloadId;
    }

    /**
     * Set the component name
     * 
     * @param componentName - component name to set
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * Set the event id
     * 
     * @param eventId - event id to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * Set the payload content
     * 
     * @param payloadContent - payload content to set
     */
    public void setPayloadContent(String payloadContent)
    {
        this.payloadContent = payloadContent;
    }

    /**
     * Set the payload id
     * 
     * @param payloadId - payload id to set
     */
    public void setPayloadId(String payloadId)
    {
        this.payloadId = payloadId;
    }

    /**
     * Get a set of the modules
     * 
     * @return set of modules
     */
    public Set<String> getModules()
    {
        return modules;
    }

    /**
     * Set the modules
     * 
     * @param modules - Set of modules to set
     */
    public void setModules(Set<String> modules)
    {
        this.modules = modules;
    }

    /**
     * Get the from date
     * 
     * @return from date
     */
    public String getFromDate()
    {
        return fromDate;
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
        return untilDate;
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
        return fromTime;
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
        return untilTime;
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
        Date createDateTime = createDateTime(fromDate, fromTime);
        logger.info("From datetime:"+createDateTime);
        return createDateTime;
    }

    /**
     * Get the to date time object
     * 
     * @return to date_time object
     */
    public Date getUntilDateTime()
    { 
        Date createDateTime = createDateTime(untilDate, untilTime);
        logger.info("Until datetime:"+createDateTime);
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
        if (dateString ==null || "".equals(dateString)){
            return null;
        }
        
        
        Calendar calendar = new GregorianCalendar();
        try
        {
 
            Date time = HHmmss.parse(timeString);
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(time);
            
            
            calendar.setTime(ddMMyyyyFormat.parse(dateString));
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        }
        catch (ParseException e)
        {
           throw new RuntimeException(e);
        }
        return calendar.getTime();
    }
}
