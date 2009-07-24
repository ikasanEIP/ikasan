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
package org.ikasan.console.web.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * The search criteria validator for wire tap searches
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteriaValidator implements Validator
{
    /** Constructor */
    public WiretapSearchCriteriaValidator()
    {
        super();
        this.ddMMyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.ddMMyyyyFormat.setLenient(false);
        this.HHmmss = new SimpleDateFormat("HH:mm:ss");
        this.HHmmss.setLenient(false);
    }

    /** A representation day/month/year */
    private SimpleDateFormat ddMMyyyyFormat;

    /** A representation hour/minutes/seconds */    
    private SimpleDateFormat HHmmss;

    /**
     * Warning suppressed because .equals method does not support Generics
     * 
     * (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return WiretapSearchCriteria.class.equals(clazz);
    }

    /**
     * Validate the WiretapSearchCriteria
     * 
     * @param object - WiretapSearchCriteria to validate 
     * @param errors - Errors to 'return' (if any).
     */
    public void validate(Object object, Errors errors)
    {
        WiretapSearchCriteria wiretapSearchCriteria = (WiretapSearchCriteria) object;
        Set<String> modules = wiretapSearchCriteria.getModules();
        if (modules == null || modules.isEmpty())
        {
            errors.reject("modules", "You need to select at least one module");
        }
        validateDateAndTime(errors, wiretapSearchCriteria.getFromDate(), "fromDate", wiretapSearchCriteria.getFromTime(), "fromTime");
        validateDateAndTime(errors, wiretapSearchCriteria.getUntilDate(), "untilDate", wiretapSearchCriteria.getUntilTime(), "untilTime");
        if (!errors.hasErrors() && !isEmpty(wiretapSearchCriteria.getFromDate()) && !isEmpty(wiretapSearchCriteria.getUntilDate()))
        {
            // if both from and until date times are populated, check if until
            // is not before from
            Date fromDateTime = wiretapSearchCriteria.getFromDateTime();
            Date untilDateTime = wiretapSearchCriteria.getUntilDateTime();
            if (fromDateTime.compareTo(untilDateTime) > 0)
            {
                errors.reject("untilDate", "Until cannot be before From");
            }
        }
    }

    /**
     * Helper method to validate date and time
     * 
     * @param errors - Errors object to fill
     * @param dateFieldValue - date field to check
     * @param dateFieldName - date field name
     * @param timeFieldValue - time field to check
     * @param timeFieldName - time field name
     */
    private void validateDateAndTime(Errors errors, String dateFieldValue, String dateFieldName, String timeFieldValue, String timeFieldName)
    {
        // check that neither or both fields are supplied
        if (!isEmpty(dateFieldValue) ^ !isEmpty(timeFieldValue))
        {
            if (isEmpty(dateFieldValue))
            {
                errors.reject(dateFieldName, dateFieldName + " must be supplied if " + timeFieldName + " has been set");
            }
            else
            {
                errors.reject(timeFieldName, timeFieldName + " must be supplied if " + dateFieldName + " has been set");
            }
        }
        if (!isEmpty(dateFieldValue))
        {
            boolean validDate = true;
            try
            {
                this.ddMMyyyyFormat.parse(dateFieldValue);
            }
            catch (ParseException e)
            {
                validDate = false;
            }
            if (dateFieldValue.length()!=10){
                validDate=false;
            }
            if (!validDate){
                errors.reject(dateFieldName, dateFieldName + " must be supplied as dd/MM/yyyy");
            }
        }
        if (!isEmpty(timeFieldValue))
        {
            try
            {
                this.HHmmss.parse(timeFieldValue);
            }
            catch (ParseException e)
            {
                errors.reject(timeFieldName, timeFieldName + " must be supplied as HH:mm:ss, eg 00:30:00 for 12:30.00am");
            }
        }
    }

    /**
     * Returns true if a field value is null or empty
     * 
     * @param fieldValue - field value to check
     * @return true if a field value is null or empty
     */
    private boolean isEmpty(String fieldValue)
    {
        return fieldValue == null || "".equals(fieldValue);
    }
}
