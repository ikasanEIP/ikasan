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
package org.ikasan.framework.web.command;

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
        ddMMyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        ddMMyyyyFormat.setLenient(false);
        HHmmss = new SimpleDateFormat("HH:mm:ss");
        HHmmss.setLenient(false);
    }

    /** A representation day/month/year */
    private SimpleDateFormat ddMMyyyyFormat;

    /** A representation hour/minutes/seconds */    
    private SimpleDateFormat HHmmss;

    /*
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
                ddMMyyyyFormat.parse(dateFieldValue);
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
                HHmmss.parse(timeFieldValue);
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
