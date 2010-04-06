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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ikasan.console.web.command.WiretapSearchCriteria;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.validation.Errors;

/**
 * JUnit based test class for testing WiretapSearchCriteriaValidator
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteriaValidatorTest
{
    /** The validator we're testing */
    private WiretapSearchCriteriaValidator wiretapSearchCriteriaValidator = new WiretapSearchCriteriaValidator();
    
    /**
     * The context that the tests run in, allows for mocking actual concrete classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /** Test the supports method */
    @Test
    public void testSupports()
    {
        assertFalse(wiretapSearchCriteriaValidator.supports(WiretapSearchCriteriaValidator.class));
        assertTrue(wiretapSearchCriteriaValidator.supports(WiretapSearchCriteria.class));
    }

    /** Test the validate method */
    @Test
    public void testValidate()
    {
        Errors errors = context.mock(Errors.class);
        try 
        {
            wiretapSearchCriteriaValidator.validate(new Object(), errors);
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            // Do Nothing, this is expected
        }
    }
    
    /** Test the case where NULL modules are supplied */
    @Test
    public void testValidate_createsErrorForNoModules()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        List<String> errors = new ArrayList<String>();
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("You need to select at least one module.", errors.get(0));
    }

    /** Test the case where empty list of modules are supplied */
    @Test
    public void testValidate_createsErrorForEmptyModules()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(new LinkedHashSet<Long>());
        List<String> errors = new ArrayList<String>();
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("You need to select at least one module.", errors.get(0));
    }

    /** Test the fromDate and fromTime field validation */
    @Test
    public void testValidateFromDateFields()
    {
        List<String> errors = new ArrayList<String>();
        Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(1));
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(moduleIds);
        
        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate(null);
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromDate must be supplied if fromTime has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromDate must be supplied if fromTime has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("10/12/2009");
        wiretapSearchCriteria.setFromTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromTime must be supplied if fromDate has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("10/12/2009");
        wiretapSearchCriteria.setFromTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromTime must be supplied if fromDate has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate(null);
        wiretapSearchCriteria.setFromTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("");
        wiretapSearchCriteria.setFromTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate(null);
        wiretapSearchCriteria.setFromTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("");
        wiretapSearchCriteria.setFromTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("21/10/19771");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromDate must be supplied as dd/MM/yyyy", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("21101977");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromDate must be supplied as dd/MM/yyyy", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("21/10/1977");
        wiretapSearchCriteria.setFromTime("000000");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("fromTime must be supplied as HH:mm:ss, eg 00:30:00 for 12:30.00AM", errors.get(0));
    }

    /** Test the untilDate and untilTime field validation */
    @Test
    public void testValidateUntilDateFields()
    {
        List<String> errors = new ArrayList<String>();
        Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(1));
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(moduleIds);
        
        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate(null);
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilDate must be supplied if untilTime has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilDate must be supplied if untilTime has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("10/12/2009");
        wiretapSearchCriteria.setUntilTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilTime must be supplied if untilDate has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("10/12/2009");
        wiretapSearchCriteria.setUntilTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilTime must be supplied if untilDate has been set.", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate(null);
        wiretapSearchCriteria.setUntilTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("");
        wiretapSearchCriteria.setUntilTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate(null);
        wiretapSearchCriteria.setUntilTime("");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("");
        wiretapSearchCriteria.setUntilTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("21/10/19771");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilDate must be supplied as dd/MM/yyyy", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("21101977");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilDate must be supplied as dd/MM/yyyy", errors.get(0));

        errors = new ArrayList<String>();
        wiretapSearchCriteria.setUntilDate("21/10/1977");
        wiretapSearchCriteria.setUntilTime("000000");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("untilTime must be supplied as HH:mm:ss, eg 00:30:00 for 12:30.00AM", errors.get(0));
    }
    
    /** Test that the untilDateTime is later than the fromDateTime */
    @Test
    public void testValidateDateRange()
    {
        List<String> errors = new ArrayList<String>();
        Set<Long> moduleIds = new LinkedHashSet<Long>();
        moduleIds.add(new Long(1));
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(moduleIds);

        wiretapSearchCriteria.setUntilDate("21/10/1978");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteria.setFromDate("21/10/1977");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());    

        wiretapSearchCriteria.setUntilDate("21/10/1977");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteria.setFromDate("21/10/1978");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertEquals("Until date/time cannot be before From date/time.", errors.get(0));    
        
        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate(null);
        wiretapSearchCriteria.setFromTime(null);
        wiretapSearchCriteria.setUntilDate("21/10/1977");
        wiretapSearchCriteria.setUntilTime("00:00:00");
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());
        
        errors = new ArrayList<String>();
        wiretapSearchCriteria.setFromDate("21/10/1977");
        wiretapSearchCriteria.setFromTime("00:00:00");
        wiretapSearchCriteria.setUntilDate(null);
        wiretapSearchCriteria.setUntilTime(null);
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        assertTrue(errors.isEmpty());    
        
    }    

}
