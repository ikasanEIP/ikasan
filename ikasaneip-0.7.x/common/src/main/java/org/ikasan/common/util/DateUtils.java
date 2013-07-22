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
package org.ikasan.common.util;

// Imported java classes
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

// Imported log4j classes
import org.apache.log4j.Logger;

/**
 * This class provides miscellaneous date and time specific utilities.
 * <p>
 * Here is an example of how to use this class:-
 * <pre>
 *    String format = "dd/MM/yyyy HH:mm:ss.SSS";
 *    long day0Long = System.currentTimeMillis();
 *
 *    DateUtils du1 = new DateUtils(day0Long);
 *    long day1Long = du1.getNextDayMidnightInMillis();
 *    long day2Long = du1.getNextDayMidnightInMillis();
 *    long day3Long = du1.getNextDayMidnightInMillis();
 *
 *    System.out.println("Day0 => long:[" + day0Long + "] str:[" + DateUtils.anyToAny(day0Long, format) + "].");
 *    System.out.println("Day1 => long:[" + day1Long + "] str:[" + DateUtils.anyToAny(day1Long, format) + "].");
 *    System.out.println("Day2 => long:[" + day2Long + "] str:[" + DateUtils.anyToAny(day2Long, format) + "].");
 *    System.out.println("Day3 => long:[" + day3Long + "] str:[" + DateUtils.anyToAny(day3Long, format) + "].");
 *    System.out.println("===============================================================================");
 *
 *    DateUtils du2 = new DateUtils(day0Long);
 *    long todayDateOnlyLong = du2.getOnlyDateInMillis();
 *    System.out.println("Today date only => long:[" + todayDateOnlyLong + "]"
 *                                        + " str:["  + DateUtils.anyToAny(todayDateOnlyLong, format) + "].");
 *
 * </pre>
 *
 * WARNING:  This is mainly used by stylesheets, take care in removing any code!
 * 
 * TODO:  Split out the main method into its own client
 *
 * @author Ikasan Development Team
 */
public class DateUtils
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(DateUtils.class);

    /**
     * The calendar instance used throughout this class.
     */
    Calendar calendar = null;

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the specified date and time as <code>java.util.Date</code>
     * in the given time zone and the default trace level.
     * 
     * @param date 
     * @param timeZone 
     */
    public DateUtils(Date date, TimeZone timeZone)
    {
        this.calendar = new GregorianCalendar(timeZone);
        this.calendar.setTime(date);
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the specified date and time as <code>java.util.Date</code>
     * in the given time zone and the default trace level.
     * 
     * @param date 
     * @param timeZoneId 
     */
    public DateUtils(Date date, String timeZoneId)
    {
        this(date, TimeZone.getTimeZone(timeZoneId));
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the specified date and time as <code>java.util.Date</code>.
     * 
     * @param date 
     */
    public DateUtils(Date date)
    {
        this(date, TimeZone.getDefault());
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the specified date and time (milliseconds)
     * in the given time zone and the default trace level.
     * 
     * @param dateMillis 
     * @param timeZone 
     */
    public DateUtils(long dateMillis, TimeZone timeZone)
    {
        this.calendar = new GregorianCalendar(timeZone);
        this.calendar.setTimeInMillis(dateMillis);
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the specified date and time (milliseconds)
     * in the given time zone and the default trace level.
     * 
     * @param dateMillis 
     * @param timeZoneId 
     */
    public DateUtils(long dateMillis, String timeZoneId)
    {
        this(dateMillis, TimeZone.getTimeZone(timeZoneId));
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with date and time specified in milliseconds.
     * 
     * @param dateMillis 
     */
    public DateUtils(long dateMillis)
    {
        this(dateMillis, TimeZone.getDefault());
    }

    /**
     * Creates a new instance of <code>DateUtils</code>
     * with the current date and time
     * in the default time zone and the default trace level.
     */
    public DateUtils()
    {
        this(new Date());
    }

    /**
     * Returns the date and time as a <code>java.util.Calendar</code> object.
     * 
     * @return Time in calendar
     */
    public Calendar getTimeInCalendar()
    {
        return this.calendar;
    }

    /**
     * Sets this calendar's current time
     * to the given <code>java.util.Date</code> object.
     * 
     * @param date 
     */
    public void setTimeInDate(Date date)
    {
        this.calendar.setTime(date);
    }

    /**
     * Returns the date and time as a <code>java.util.Date</code> object.
     * 
     * @return time in date
     */
    public Date getTimeInDate()
    {
        return this.getTimeInCalendar().getTime();
    }

    /**
     * Sets this calendar's current time to the given long value.
     * @param dateMillis 
     */
    public void setTimeInMillis(long dateMillis)
    {
        this.calendar.setTimeInMillis(dateMillis);
    }

    /**
     * Returns the date and time in milliseconds.
     * 
     * @return the date and time in milliseconds.
     */
    public long getTimeInMillis()
    {
        return this.getTimeInDate().getTime();
    }

    /**
     * Formats the date and time in the given format
     * and returns the formatted string.
     *
     * @param dateFormat is the <code>DateFormat</code> instance
     *                      holding a new date/time format.
     * @param timeZone   is the <code>TimeZone</code> instance
     *                      holding a new time zone.
     * @return the date and time in the given format
     */
    public String getTimeInFormatString(DateFormat dateFormat,
                                        TimeZone timeZone)
    {
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(this.getTimeInDate());
    }

    /**
     * Formats the date and time in the given format
     * and returns the formatted string.
     *
     * @param format   is the date time format.
     * @param timeZone is the <code>TimeZone</code> instance
     *                    holding a new time zone.
     * @return the date and time in the given format                    
     */
    public String getTimeInFormatString(String format, TimeZone timeZone)
    {
        return this.getTimeInFormatString(new SimpleDateFormat(format),
                                          timeZone);
    }

    /**
     * Formats the date and time in the given format
     * and returns the formatted string.
     *
     * @param format     is the date time format.
     * @param timeZoneId is the new time zone ID.
     * @return the date and time in the given format 
     */
    public String getTimeInFormatString(String format, String timeZoneId)
    {
        return this.getTimeInFormatString(new SimpleDateFormat(format),
                                          TimeZone.getTimeZone(timeZoneId));
    }

    /**
     * Formats the date and time in the given format
     * and returns the formatted string.
     *
     * @param format is the date time format.
     * @return the date and time in the given format 
     */
    public String getTimeInFormatString(String format)
    {
        return this.getTimeInFormatString(new SimpleDateFormat(format),
                                          TimeZone.getDefault());
    }

    /**
     * Returns only date part as a <code>java.util.Date</code> object.
     *
     * @return only date part as a <code>java.util.Date</code> object.
     */
    public Date getOnlyDateInDate()
    {
        Calendar tempCalendar = (Calendar)this.calendar.clone();
        tempCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tempCalendar.set(Calendar.MINUTE, 0);
        tempCalendar.set(Calendar.SECOND, 0);

        return tempCalendar.getTime();
    }

    /**
     * Returns only date part in milliseconds.
     * 
     * @return only date part in milliseconds. 
     */
    public long getOnlyDateInMillis()
    {
        return this.getOnlyDateInDate().getTime();
    }

    /**
     * Adds/subtracts the specified amount of
     * days/months/years/hours/minutes/seconds
     * the previously specified date.
     *
     * @param field   is the field to be added/subtracted to/from
     *                   the specified date time.
     * @param howmany is the amount of units to add/subtract to/from
     *                   the specified date time.
     */
    public void add(int field, int howmany)
    {
        this.calendar.add(field, howmany);
    }

    /**
     * Adds/subtracts the specified amount of years to/from
     * the previously specified date.
     *
     * @param howmany is the amount of units to add/subtract to/from
     *                   the specified date time.
     */
    public void addYears(int howmany)
    {
        this.add(Calendar.YEAR, howmany);
    }

    /**
     * Adds/subtracts the specified amount of months
     * to/from the previously specified date.
     *
     * @param howmany is the amount of units to add/subtract
     *                   to/from the specified date time.
     */
    public void addMonths(int howmany)
    {
        this.add(Calendar.MONTH, howmany);
    }

    /**
     * Adds/subtracts the specified amount of days
     * to/from the previously specified date.
     *
     * @param howmany is the amount of units to add/subtract
     *                   to/from the specified date time.
     */
    public void addDays(int howmany)
    {
        this.add(Calendar.DAY_OF_MONTH, howmany);
    }

    /**
     * Resets the time to midnight.
     *
     */
    public void resetTime()
    {
        this.calendar.set(Calendar.HOUR_OF_DAY, 0);
        this.calendar.set(Calendar.MINUTE, 0);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Returns the date to the next date with its time set to midnight.
     * 
     * @return the date to the next date with its time set to midnight.
     */
    public Date getNextDayMidnightInDate()
    {
        int year  = this.calendar.get(Calendar.YEAR);
        int month = this.calendar.get(Calendar.MONTH);
        int date  = this.calendar.get(Calendar.DAY_OF_MONTH);
        Calendar cal = new GregorianCalendar(year, month, (date + 1));
        return cal.getTime();
    }

    /**
     * Returns the date to the next date with its time set to midnight.
     * 
     * @return the date to the next date with its time set to midnight.
     */
    public long getNextDayMidnightInMillis()
    {
        return this.getNextDayMidnightInDate().getTime();
    }

    /**
     * Compares today's date to the specified date for equality.
     * 
     * @param targetDate 
     * @return true if date is today
     */
    public static boolean isToday(Date targetDate)
    {
        DateUtils du1 = new DateUtils();
        long nowDateOnly = du1.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Current datetime  =[" + du1.getTimeInDate() + "].");
            logger.debug("Current date only =[" + nowDateOnly + "].");
        }

        DateUtils du2 = new DateUtils(targetDate);
        long tgtDateOnly = du2.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Target datetime   =[" + du2.getTimeInDate() + "].");
            logger.debug("Target date only  =[" + tgtDateOnly + "].");
        }

        return (nowDateOnly == tgtDateOnly);
    }

    /**
     * Compares today's date to the specified date.
     * 
     * @param targetDate 
     * @return true if data is today
     */
    public static boolean isToday(long targetDate)
    {
        return isToday(new Date(targetDate));
    }

    /**
     * Compares specified date to see if it is less than today's date.
     * 
     * @param targetDate 
     * @return true if date is less than today 
     */
    public static boolean isLessThanToday(Date targetDate)
    {
        DateUtils du1 = new DateUtils();
        long nowDateOnly = du1.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Current datetime  =[" + du1.getTimeInDate() + "].");
            logger.debug("Current date only =[" + nowDateOnly + "].");
        }

        DateUtils du2 = new DateUtils(targetDate);
        long tgtDateOnly = du2.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Target datetime   =[" + du2.getTimeInDate() + "].");
            logger.debug("Target date only  =[" + tgtDateOnly + "].");
        }

        return (tgtDateOnly < nowDateOnly);
    }

    /**
     * Compares specified date to see if it is less than today's date.
     * 
     * @param targetDate 
     * @return true if date is less than today 
     */
    public static boolean isLessThanToday(long targetDate)
    {
        return isLessThanToday(new Date(targetDate));
    }

    /**
     * Compares specified date to see if it is greater than today's date.
     * 
     * @param targetDate 
     * @return true if date is greater than today
     */
    public static boolean isGreaterThanToday(Date targetDate)
    {
        DateUtils du1 = new DateUtils();
        long nowDateOnly = du1.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Current datetime  =[" + du1.getTimeInDate() + "].");
            logger.debug("Current date only =[" + nowDateOnly + "].");
        }

        DateUtils du2 = new DateUtils(targetDate);
        long tgtDateOnly = du2.getOnlyDateInMillis();
        if (logger.isDebugEnabled())
        {
            logger.debug("Target datetime   =[" + du2.getTimeInDate() + "].");
            logger.debug("Target date only  =[" + tgtDateOnly + "].");
        }

        return (tgtDateOnly > nowDateOnly);
    }

    /**
     * Compares specified date to see if it is greater than today's date.
     * 
     * @param targetDate 
     * @return true if date is greater than today
     */
    public static boolean isGreaterThanToday(long targetDate)
    {
        return isGreaterThanToday(new Date(targetDate));
    }

    /**
     * Add/subtracts the specified amount of days/months/years
     * to/from the current date.
     *
     * @param dateTime is the current date and time.
     * @param format   is the current format.
     * @param field    is the field to be added/subtracted
     *                    to/from the current date time.
     * @param howmany  is the amount of units to add/subtract
     *                    to/from the current date time.
     * @return the added/subtracted date string.
     * @throws ParseException 
     */
    public static String add(String dateTime, String format,
                             int field, int howmany)
        throws ParseException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Datetime  =[" + dateTime + "].");
            logger.debug("  Format  =[" + format + "].");
            logger.debug("  Field   =[" + field + "].");
            logger.debug("  Howmany =[" + howmany + "].");
        }

        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = dateFormat.parse(dateTime);
        DateUtils du = new DateUtils(date);
        du.add(field, howmany);
        String newDataTime = dateFormat.format(du.getTimeInDate());
        if (logger.isDebugEnabled())
            logger.debug("New  date =[" + newDataTime + "].");

        return newDataTime;
    }

    /**
     * Add/subtracts the specified amount of days to/from the current date.
     *
     * @param dateTime is the current date and time.
     * @param format   is the current format.
     * @param howmany  is the days to add/subtract to/from
     *                    the current date time.
     * @return the added/subtracted date string.
     * @throws ParseException 
     */
    public static String addDaysToAny(String dateTime, String format,
                                      int howmany)
        throws ParseException
    {
        return add(dateTime, format, Calendar.DAY_OF_MONTH, howmany);
    }

    /**
     * Add/subtracts the specified amount of months to/from the current date.
     *
     * @param dateTime is the current date and time.
     * @param format   is the current format.
     * @param howmany  is the months to add/subtract to/from
     *                    the current date time.
     * @return the added/subtracted date string.
     * @throws ParseException 
     */
    public static String addMonthsToAny(String dateTime, String format,
                                        int howmany)
        throws ParseException
    {
        return add(dateTime, format, Calendar.MONTH, howmany);
    }

    /**
     * Add/subtracts the specified amount of years to/from the current date.
     *
     * @param dateTime is the current date and time.
     * @param format   is the current format.
     * @param howmany  is the years to add/subtract to/from
     *                    the current date time.
     * @return the added/subtracted date string.
     * @throws ParseException 
     */
    public static String addYearsToAny(String dateTime, String format,
                                       int howmany)
        throws ParseException
    {
        return add(dateTime, format, Calendar.YEAR, howmany);
    }

    /**
     * Formats the date and time in the given format from
     * any date-time format in <code>long</code>.
     *
     * @param dateTime     is the current date and time in milliseconds.
     * @param currTimeZone is the current time zone.
     * @param newFormat    is the new format.
     * @param newTimeZone  is the new time zone.
     * @return the newly formatted string.
     */
    public static String anyToAny(long dateTime, TimeZone currTimeZone,
                                  String newFormat, TimeZone newTimeZone)
    {
        DateUtils du = new DateUtils(dateTime, currTimeZone);

        if (logger.isDebugEnabled())
        {
            logger.debug("Date (long)   =[" + dateTime + "].");
            logger.debug("Date (string) =[" + du.getTimeInDate() + "].");
            logger.debug("Curr timezone =[" + currTimeZone.getID() + "].");
            logger.debug("New format    =[" + newFormat + "].");
            logger.debug("New timezone  =[" + newTimeZone.getID() + "].");
        }

        return du.getTimeInFormatString(newFormat, newTimeZone);
    }

    /**
     * Formats the specified date and time in the given time zone
     * into the specified format in the given time zone.
     *
     * @param dateTime       is the current date and time in milliseconds.
     * @param currTimeZoneId is the current time zone ID.
     * @param newFormat      is the new format.
     * @param newTimeZoneId  is the new time zone ID.
     * @return the newly formatted string.
     */
    public static String anyToAny(long dateTime, String currTimeZoneId,
                                  String newFormat, String newTimeZoneId)
    {
        return anyToAny(dateTime, TimeZone.getTimeZone(currTimeZoneId),
                        newFormat, TimeZone.getTimeZone(newTimeZoneId));
    }

    /**
     * Formats the specified date and time in the default time zone
     * into the specified format in the given time zone.
     *
     * @param dateTime    is the current date and time in milliseconds.
     * @param newFormat   is the new format.
     * @param newTimeZone is the new time zone.
     * @return the newly formatted string.
     */
    public static String anyToAny(long dateTime, String newFormat,
                                  TimeZone newTimeZone)
    {
        return anyToAny(dateTime, TimeZone.getDefault(),
                        newFormat, newTimeZone);
    }

    /**
     * Formats the specified date and time in the default time zone
     * into the specified format in the given time zone.
     *
     * @param dateTime      is the current date and time in milliseconds.
     * @param newFormat     is the new format.
     * @param newTimeZoneId is the new time zone ID.
     * @return the newly formatted string.
     */
    public static String anyToAny(long dateTime, String newFormat,
                                  String newTimeZoneId)
    {
        return anyToAny(dateTime, newFormat,
                        TimeZone.getTimeZone(newTimeZoneId));
    }

    /**
     * Formats the specified date and time in the default time zone
     * into the specified format in the default time zone.
     *
     * @param dateTime  is the current date and time in milliseconds.
     * @param newFormat is the new format.
     * @return the newly formatted string.
     */
    public static String anyToAny(long dateTime, String newFormat)
    {
        return anyToAny(dateTime, newFormat, TimeZone.getDefault());
    }

    /**
     * Formats the current date and time in the default time zone
     * into the specified format in the given time zone.
     *
     * @param newFormat is the new format.
     * @param newTimeZone 
     * @return the newly formatted string.
     */
    public static String nowToAny(String newFormat, TimeZone newTimeZone)
    {
        return anyToAny(System.currentTimeMillis(), newFormat, newTimeZone);
    }

    /**
     * Formats the current date and time in the default time zone
     * into the specified format in the given time zone.
     *
     * @param newFormat     is the new format.
     * @param newTimeZoneId is the new time zone ID.
     * @return the newly formatted string.
     */
    public static String nowToAny(String newFormat, String newTimeZoneId)
    {
        return anyToAny(System.currentTimeMillis(), newFormat,
                        TimeZone.getTimeZone(newTimeZoneId));
    }

    /**
     * Formats the current date and time in the default time zone
     * into the specified format in the default time zone.
     *
     * @param newFormat is the new format.
     * @return the newly formatted string.
     */
    public static String nowToAny(String newFormat)
    {
        return anyToAny(System.currentTimeMillis(), newFormat);
    }

    /**
     * Parses the specified date using the given custom format then
     * instantiates and returns the associated Date object.
     *
     * @param dateTime     is the current date and time as string.
     * @param currFormat   is the current format.
     * @return the newly created Date object.
     * @throws ParseException 
     */
    public static Date anyToDate(final String dateTime, final String currFormat)
        throws ParseException
    {
        // Parse the current date and time using the current format
        SimpleDateFormat dateFormat = new SimpleDateFormat(currFormat);

        return dateFormat.parse(dateTime);
    }

    /**
     * Parses the specified date using the given custom format then
     * instantiates and returns the associated Date object.
     *
     * @param dateTime     is the current date and time as long.
     * @param currFormat   is the current format.
     * @return the newly created Date object.
     * @throws ParseException 
     */
    public static Date anyToDate(final long dateTime, final String currFormat)
        throws ParseException
    {
        // Parse the current date and time using the current format
        SimpleDateFormat dateFormat = new SimpleDateFormat(currFormat);
        Date date = new Date(dateTime);
        String formattedDateString = dateFormat.format(date);
        return dateFormat.parse(formattedDateString);
    }
    
    /**
     * Parses the specified date using the given custom format then
     * formats the parsed date using the second given custom format.
     *
     * @param dateTime     is the current date and time as string.
     * @param currFormat   is the current format.
     * @param currTimeZone is the current time zone.
     * @param newFormat    is the new format.
     * @param newTimeZone  is the new time zone.
     * @return the newly formatted string.
     * @throws ParseException 
     */
    public static String anyToAny(String dateTime,
                                  String currFormat, TimeZone currTimeZone,
                                  String newFormat, TimeZone newTimeZone)
        throws ParseException
    {
        // Parse the current date and time using the current format
        SimpleDateFormat dateFormat = new SimpleDateFormat(currFormat);
        Date date = dateFormat.parse(dateTime);

        return anyToAny(date.getTime(), currTimeZone, newFormat, newTimeZone);
    }

    /**
     * Parses the specified date using the given custom format then
     * formats the parsed date using the second given custom format.
     *
     * @param dateTime       is the current date and time as string.
     * @param currFormat     is the current format.
     * @param currTimeZoneId is the current time zone ID.
     * @param newFormat      is the new format.
     * @param newTimeZoneId  is the new time zone ID.
     * @return the newly formatted string.
     * @throws ParseException 
     */
    public static String anyToAny(String dateTime,
                                  String currFormat, String currTimeZoneId,
                                  String newFormat, String newTimeZoneId)
        throws ParseException
    {
        return anyToAny(dateTime, currFormat, TimeZone.getTimeZone(currTimeZoneId),
                        newFormat, TimeZone.getTimeZone(newTimeZoneId));
    }

    /**
     * Parses the specified date using the given custom format then
     * formats the parsed date using the second given custom format.
     *
     * @param dateTime     is the current date and time as string.
     * @param currFormat   is the current format.
     * @param newFormat    is the new format.
     * @param newTimeZone  is the new time zone.
     * @return the newly formatted string.
     * @throws ParseException 
     */
    public static String anyToAny(String dateTime, String currFormat,
                                  String newFormat, TimeZone newTimeZone)
        throws ParseException
    {
        return anyToAny(dateTime, currFormat, TimeZone.getDefault(),
                        newFormat, newTimeZone);
    }

    /**
     * Parses the specified date using the given custom format then
     * formats the parsed date using the second given custom format.
     *
     * @param dateTime      is the current date and time as string.
     * @param currFormat    is the current format.
     * @param newFormat     is the new format.
     * @param newTimeZoneId is the new time zone ID.
     * @return the newly formatted string.
     * @throws ParseException 
     */
    public static String anyToAny(String dateTime, String currFormat,
                                  String newFormat, String newTimeZoneId)
        throws ParseException
    {
        return anyToAny(dateTime, currFormat, TimeZone.getDefault(),
                        newFormat, TimeZone.getTimeZone(newTimeZoneId));
    }

    /**
     * Parses the specified date using the given custom format then
     * formats the parsed date using the second given custom format.
     *
     * @param dateTime     is the current date and time as string.
     * @param currFormat   is the current format.
     * @param newFormat    is the new format.
     * @return the newly formatted string.
     * @throws ParseException 
     */
    public static String anyToAny(String dateTime, String currFormat,
                                  String newFormat)
        throws ParseException
    {
        return anyToAny(dateTime, currFormat,
                        newFormat, TimeZone.getDefault());
    }

// main() method
///////////////////////////////////////////////////////////////////////////////

    /**
     * Runs this class for testing in a live environment (sometimes Unit testing
     * does not cover the variations of date/time on a server).
     * 
     * @param args 
     */
    public static void main(String args[])
    {

        long dateMillis = System.currentTimeMillis();

        // Parse the command-line parameters
        for (int i = 0; i < args.length; i++)
        {
            // Display usage then get outta here
            if (args[i].equalsIgnoreCase("-help"))
            {
                usage();
                System.exit(1);
            }
            // Date in milliseconds (long)
            else if (args[i].equalsIgnoreCase("-datelong"))
            {
                try
                {
                    dateMillis = Long.parseLong(args[++i].trim());
                }
                catch (NumberFormatException e)
                {
                    dateMillis = System.currentTimeMillis();
                }
            }
            // Dunno what to do
            else
            {
                System.err.println("Invalid option - [" + args[i] + "].");
                usage();
                System.exit(1);
            }
        }

        System.out.println("");
        System.out.println("Date (long) =[" + dateMillis + "].");
        System.out.println("Date (date) =[" + new Date(dateMillis) + "].");
        System.out.println("");

        try
        {
            DateUtils du = new DateUtils(dateMillis);

            // Keep on asking until we've got zero "0" (to exit) as answer
            String question = "", answer = "";
            String currFormat = "dd/MM/yyyy HH:mm:ss.SSS";
            String newFormat  = "dd-MM-yyyy HH:mm:ss.SSS";
            String currTzId = TimeZone.getDefault().getID();
            String newTzId  = "GMT";
            int field = Calendar.DAY_OF_MONTH;
            int howmany = 0;
            SimpleDateFormat formatter = new SimpleDateFormat(currFormat);
            String dateText = formatter.format(du.getTimeInDate());
            while (!answer.equalsIgnoreCase("0"))
            {
                // Which method would you like to test?
                question = "Which method do you want to test?\n"
                    + "  0. exit(Get outta here!!)\n"
                    + "  1. getTimeInClandar()\n"
                    + "  2. getTimeInDate()\n"
                    + "  3. setTimeInMillis(long dateMillis)\n"
                    + "  4. getTimeInMillis()\n"
                    + "  5. getTimeInFormatString(String newFormat, String newTzId)\n"
                    + "  6. getOnlyDateInDate()\n"
                    + "  7. getOnlyDateInMillis()\n"
                    + "  8. addYears(int howmany)\n"
                    + "  9. addMonths(int howmany)\n"
                    + " 10. addDays(int howmany)\n"
                    + " 11. resetTime()\n"
                    + " 12. getNextDayMidnightInDate()\n"
                    + " 13. getNextDayMidnightInMillis()\n"
                    + " 14. isToday(long targetDate)\n"
                    + " 15. isLessThanToday(long targetDate)\n"
                    + " 16. isGreaterThanToday(long targetDate)\n"
                    + " 17. add(String dateText, String currFormat, int field, int howmany)\n"
                    + " 18. addDaysToAny(String dateText, String currFormat, int howmany)\n"
                    + " 19. addMonthsToAny(String dateText, String currFormat, int howmany)\n"
                    + " 20. addYearsToAny(String dateText, String currFormat, int howmany)\n"
                    + " 21. anyToAny(long dateMillis, String currTzId, String newFormat, String newTzId)\n"
                    + " 22. anyToAny(String dateText, String currFormat, String currTzId, String newFormat, String newTzId)\n"
                    + " 23. nowToAny(String newFormat, String newTzId)\n";
                answer = askQuestion(question);

                if (!answer.matches("[1-9][0-9]*")) continue;

                // We've got one of the above numbers
                // ask more questions
                // then callthe selected method and return the result
                if (answer.equals("1"))
                {
                    System.out.println("Result =["
                        + du.getTimeInCalendar() + "]");
                }
                else
                if (answer.equals("2"))
                {
                    System.out.println("Result =["
                        + du.getTimeInDate() + "]");
                }
                else
                if (answer.equals("3"))
                {
                    question = "Date in milliseconds (" + dateMillis + ")?";
                    dateMillis = getLong(dateMillis, question);
                    du.setTimeInMillis(dateMillis);
                    System.out.println("Result =["
                        + du.getTimeInMillis() + "]");
                }
                else
                if (answer.equals("4"))
                {
                    System.out.println("Result =["
                        + du.getTimeInMillis() + "]");
                }
                else
                if (answer.equals("5"))
                {
                    question = "New date time format (" + newFormat + ")?";
                    newFormat = getString(newFormat, question);
                    question = "New time zone (" + newTzId + ")?";
                    newTzId = getString(newTzId, question);
                    System.out.println("Result =["
                        + du.getTimeInFormatString(newFormat, newTzId)
                        + "]");
                }
                else
                if (answer.equals("6"))
                {
                    System.out.println("Result =["
                        + du.getOnlyDateInDate() + "]");
                }
                else
                if (answer.equals("7"))
                {
                    System.out.println("Result =["
                        + du.getOnlyDateInMillis() + "]");
                }
                else
                if (answer.equals("8"))
                {
                    question = "How many years (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    du.addYears(howmany);
                    dateMillis = du.getTimeInMillis();
                    System.out.println("Result =["
                        + du.getTimeInDate() + "]");
                }
                else
                if (answer.equals("9"))
                {
                    question = "How many months (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    du.addMonths(howmany);
                    dateMillis = du.getTimeInMillis();
                    System.out.println("Result =["
                        + du.getTimeInDate() + "]");
                }
                else
                if (answer.equals("10"))
                {
                    question = "How many days (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    du.addDays(howmany);
                    dateMillis = du.getTimeInMillis();
                    System.out.println("Result =["
                        + du.getTimeInDate() + "]");
                }
                else
                if (answer.equals("11"))
                {
                    du.resetTime();
                    dateMillis = du.getTimeInMillis();
                    System.out.println("Result =["
                        + du.getTimeInDate() + "]");
                }
                else
                if (answer.equals("12"))
                {
                    System.out.println("Result =["
                        + du.getNextDayMidnightInDate() + "]");
                }
                else
                if (answer.equals("13"))
                {
                    System.out.println("Result =["
                        + du.getNextDayMidnightInMillis() + "]");
                }
                else
                if (answer.equals("14"))
                {
                    question = "Date in milliseconds (" + dateMillis + ")?";
                    dateMillis = getLong(dateMillis, question);
                    System.out.println("Result =["
                        + DateUtils.isToday(dateMillis) + "]");
                }
                else
                if (answer.equals("15"))
                {
                    question = "Date in milliseconds (" + dateMillis + ")?";
                    dateMillis = getLong(dateMillis, question);
                    System.out.println("Result =["
                        + DateUtils.isLessThanToday(dateMillis) + "]");
                }
                else
                if (answer.equals("16"))
                {
                    question = "Date in milliseconds (" + dateMillis + ")?";
                    dateMillis = getLong(dateMillis, question);
                    System.out.println("Result =["
                        + DateUtils.isGreaterThanToday(dateMillis) + "]");
                }
                else
                if (answer.equals("17"))
                {
                    question = "Date as string (" + dateText + ")?";
                    dateText = getString(dateText, question);
                    question = "Current date time format (" + currFormat
                             + ")?";
                    currFormat = getString(currFormat, question);
                    question = "Field to add (" + field + ")?";
                    field = getInteger(field, question);
                    question = "How many (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    System.out.println("Result =["
                        + DateUtils.add(dateText, currFormat,
                                        field, howmany) + "]");
                }
                else
                if (answer.equals("18"))
                {
                    question = "Date as string (" + dateText + ")?";
                    dateText = getString(dateText, question);
                    question = "Current date time format (" + currFormat
                             + ")?";
                    currFormat = getString(currFormat, question);
                    question = "How many days (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    System.out.println("Result =["
                        + DateUtils.addDaysToAny(dateText, currFormat,
                                                 howmany) + "]");
                }
                else
                if (answer.equals("19"))
                {
                    question = "Date as string (" + dateText + ")?";
                    dateText = getString(dateText, question);
                    question = "Current date time format (" + currFormat
                             + ")?";
                    currFormat = getString(currFormat, question);
                    question = "How many months (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    System.out.println("Result =["
                        + DateUtils.addMonthsToAny(dateText, currFormat,
                                                   howmany) + "]");
                }
                else
                if (answer.equals("20"))
                {
                    question = "Date as string (" + dateText + ")?";
                    dateText = getString(dateText, question);
                    question = "Current date time format (" + currFormat
                             + ")?";
                    currFormat = getString(currFormat, question);
                    question = "How many years (" + howmany + ")?";
                    howmany = getInteger(howmany, question);
                    System.out.println("Result =["
                        + DateUtils.addYearsToAny(dateText, currFormat,
                                                  howmany) + "]");
                }
                else
                if (answer.equals("21"))
                {
                    question = "Date in milliseconds (" + dateMillis + ")?";
                    dateMillis = getLong(dateMillis, question);
                    question = "Current time zone (" + currTzId + ")?";
                    currTzId = getString(currTzId, question);
                    question = "New date time format (" + newFormat + ")?";
                    newFormat = getString(newFormat, question);
                    question = "New time zone (" + newTzId + ")?";
                    newTzId = getString(newTzId, question);
                    System.out.println("Result =["
                        + DateUtils.anyToAny(dateMillis, currTzId,
                                             newFormat, newTzId) + "]");
                }
                else
                if (answer.equals("22"))
                {
                    question = "Date as string (" + dateText + ")?";
                    dateText = getString(dateText, question);
                    question = "Current date time format (" + currFormat
                             + ")?";
                    currFormat = getString(currFormat, question);
                    question = "Current time zone (" + currTzId + ")?";
                    currTzId = getString(currTzId, question);
                    question = "New date time format (" + newFormat + ")?";
                    newFormat = getString(newFormat, question);
                    question = "New time zone (" + newTzId + ")?";
                    newTzId = getString(newTzId, question);
                    System.out.println("Result =["
                        + DateUtils.anyToAny(dateText, currFormat, currTzId,
                                             newFormat, newTzId) + "]");
                }
                else
                if (answer.equals("23"))
                {
                    question = "New date time format (" + newFormat + ")?";
                    newFormat = getString(newFormat, question);
                    question = "New time zone (" + newTzId + ")?";
                    newTzId = getString(newTzId, question);
                    System.out.println("Result =["
                        + DateUtils.nowToAny(newFormat, newTzId) + "]");
                }
                else
                {
                    System.out.println("Invalid number! "
                        + "Please select one of the following numbers.");
                }

                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }

        
        
        
        System.exit(0);
    }

    /**
     * Displays the usage for main() method.
     *
     */
    private static void usage()
    {
        String fqClassName = DateUtils.class.getName();
        System.err.println("");
        System.err.println("Usage:");
        System.err.println("java " + fqClassName + " [-options]");
        System.err.println("");
        System.err.println("where options include:");
        System.err.println("    -datelong <date> to specify date in long, current time by default");
        System.err.println("");
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as string. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param str 
     * @param question 
     * @return String from question 
     */
    protected static String getString(String str, String question)
    {
        return (str != null)
            ? askQuestion(question, str) : askQuestion(question);
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as integer. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param number 
     * @param question 
     * @return int from question 
     */
    protected static int getInteger(int number, String question)
    {
        String str = (number > -1)
                   ? askQuestion(question, String.valueOf(number))
                   : askQuestion(question);
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer as long. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param number 
     * @param question 
     * @return long from question 
     */
    protected static long getLong(long number, String question)
    {
        String str = askQuestion(question, String.valueOf(number));
        try
        {
            return Long.parseLong(str);
        }
        catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer. It returns the default value
     * if the answer is empty provided the default value is not null.
     * This is used in main() method.
     * 
     * @param question 
     * @param defValue 
     * @return answer 
     */
    private static String askQuestion(String question, String defValue)
    {
        String response = "";
        do
        {
            System.out.println(question);
            try
            {
                BufferedReader br = new BufferedReader(
                                      new InputStreamReader(System.in));
                response = br.readLine();
                response = (response != null) ? response : "";
            }
            catch (IOException e)
            {
                System.err.println("Error while reading answer: '" + e + "'");
                response = null;
            }
        }
        while (defValue == null && response != null && response.trim().length() == 0);

        return (response != null && response.trim().length() > 0) ? response : defValue;
    }

    /**
     * Asks a simple question, waits for response and
     * finally returns an answer. It keeps asking until it gets something.
     * This is used in main() method.
     * 
     * @param question 
     * @return answer 
     */
    protected static String askQuestion(String question)
    {
        return askQuestion(question, null);
    }

}
