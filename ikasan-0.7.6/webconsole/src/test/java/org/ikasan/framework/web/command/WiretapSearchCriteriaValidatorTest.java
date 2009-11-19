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
package org.ikasan.framework.web.command;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.validation.Errors;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class WiretapSearchCriteriaValidatorTest
{
    private WiretapSearchCriteriaValidator wiretapSearchCriteriaValidator = new WiretapSearchCriteriaValidator();
    
    private Mockery mockery = new Mockery();
    
    private static Set<String> someModules = new HashSet<String>();
    
    static{
        
        someModules.add("aModule");
    }
    

    
    @Test
    public void testValidate_createsErrorForNoModules()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        failsValidation(wiretapSearchCriteria, "modules");
    }
    
    @Test
    public void testValidate_createsErrorForNoFromTimeWhenFromDateSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setFromDate("12/12/1965");
        failsValidation(wiretapSearchCriteria, "fromTime");
    }
    
    @Test
    public void testValidate_passesValidationWithValidFromDateAndTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("12:20:00");
        
        validatesOk(wiretapSearchCriteria);

    }
    
    @Test
    public void testValidate_createsErrorFromTimeWhenInvalidFromTimeSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("invalid");
        failsValidation(wiretapSearchCriteria, "fromTime");
    }
    
    @Test
    public void testValidate_createsErrorForNoUntilTimeWhenUntilDateSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        failsValidation(wiretapSearchCriteria, "untilTime");
    }
    
    @Test
    public void testValidate_passesValidationWithValidUntilDateAndTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("12:20:00");
        
        validatesOk(wiretapSearchCriteria);

    }

    private void validatesOk(WiretapSearchCriteria wiretapSearchCriteria)
    {
        final Errors errors = mockery.mock(Errors.class);
        mockery.checking(new Expectations()
        {
            {  
               allowing(errors).hasErrors();will(returnValue(false));
             }
        });
        
        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testValidate_createsErrorUntilTimeWhenInvalidUntilTimeSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("invalid");
        failsValidation(wiretapSearchCriteria, "untilTime");
    }
    
    @Test
    public void testValidate_createsErrorIfFromDateTimeAfterUntilDateTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria();
        wiretapSearchCriteria.setModules(someModules);
        
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("12:30:00");
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("12:30:01");
        
        final Errors errors = mockery.mock(Errors.class);
        mockery.checking(new Expectations()
        {
            {  
               allowing(errors).hasErrors();will(returnValue(false));

               one(errors).reject(with(equal("untilDate")), (String)with(a(String.class)));
             }
        });
 

        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
 
        mockery.assertIsSatisfied();
    }



    private void failsValidation(WiretapSearchCriteria wiretapSearchCriteria, final String errorField)
    {
        final Errors errors = mockery.mock(Errors.class);
        mockery.checking(new Expectations()
        {
            {  
               one(errors).reject(with(equal(errorField)), (String)with(a(String.class)));
               allowing(errors).hasErrors();will(returnValue(true));
             }
        });
 

        wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
 
        mockery.assertIsSatisfied();
    }
    

}
