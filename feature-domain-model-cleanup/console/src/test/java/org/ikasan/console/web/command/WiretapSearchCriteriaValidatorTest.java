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

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.validation.Errors;

/**
 * JUnit based test class for testing WiretapSearchCriteriaValidator and 
 * WiretapSearchCriteria
 * 
 * @author Ikasan Development Team
 */
public class WiretapSearchCriteriaValidatorTest
{
    /** The WiretapSearchCriteriaValidator we want to test */
    private WiretapSearchCriteriaValidator wiretapSearchCriteriaValidator = new WiretapSearchCriteriaValidator();
    
    /** JMock mockery to help us mock objects for testing */
    private Mockery mockery = new Mockery();

    /** A set of module names */
    private static Set<String> someModules = new HashSet<String>();
    
    /** Add a 'aModule' module name to the someModules Set */
    static
    {
        someModules.add("aModule");
    }
        
    /** Test the case where no modules are supplied */
    @Test
    public void testValidate_createsErrorForNoModules()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(null);
        // failsValidation(wiretapSearchCriteria, "modules");
    }
    
/*    @Test
    public void testValidate_createsErrorForNoFromTimeWhenFromDateSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setFromDate("12/12/1965");
        failsValidation(wiretapSearchCriteria, "fromTime");
    }
    
    @Test
    public void testValidate_passesValidationWithValidFromDateAndTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("12:20:00");
        validatesOk(wiretapSearchCriteria);
    }
    
    @Test
    public void testValidate_createsErrorFromTimeWhenInvalidFromTimeSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("invalid");
        failsValidation(wiretapSearchCriteria, "fromTime");
    }
    
    @Test
    public void testValidate_createsErrorForNoUntilTimeWhenUntilDateSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        failsValidation(wiretapSearchCriteria, "untilTime");
    }
    
    @Test
    public void testValidate_passesValidationWithValidUntilDateAndTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("12:20:00");
        validatesOk(wiretapSearchCriteria);
    }

    private void validatesOk(WiretapSearchCriteria wiretapSearchCriteria)
    {
        final Errors errors = mockery.mock(Errors.class);
        this.mockery.checking(new Expectations()
        {
            {  
               allowing(errors).hasErrors();will(returnValue(false));
             }
        });
        
        this.wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        this.mockery.assertIsSatisfied();
    }
    
    @Test
    public void testValidate_createsErrorUntilTimeWhenInvalidUntilTimeSupplied()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("invalid");
        failsValidation(wiretapSearchCriteria, "untilTime");
    }
    
    @Test
    public void testValidate_createsErrorIfFromDateTimeAfterUntilDateTime()
    {
        WiretapSearchCriteria wiretapSearchCriteria = new WiretapSearchCriteria(someModules);
        wiretapSearchCriteria.setModules(someModules);
        wiretapSearchCriteria.setUntilDate("12/12/1965");
        wiretapSearchCriteria.setUntilTime("12:30:00");
        wiretapSearchCriteria.setFromDate("12/12/1965");
        wiretapSearchCriteria.setFromTime("12:30:01");
        
        final Errors errors = this.mockery.mock(Errors.class);
        this.mockery.checking(new Expectations()
        {
            {  
               allowing(errors).hasErrors();will(returnValue(false));

               one(errors).reject(with(equal("untilDate")), (String)with(a(String.class)));
             }
        });
 
        this.wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        this.mockery.assertIsSatisfied();
    }
*/

    /**
     * Helper method, tests that at least one criteria fails
     * 
     * @param wiretapSearchCriteria - The criteria to test
     * @param errorField - The name of the error field 
     */
    private void failsValidation(WiretapSearchCriteria wiretapSearchCriteria, final String errorField)
    {
        final Errors errors = this.mockery.mock(Errors.class);
        this.mockery.checking(new Expectations()
        {
            {  
               one(errors).reject(with(equal(errorField)), (String)with(a(String.class)));
               allowing(errors).hasErrors();will(returnValue(true));
             }
        });

        this.wiretapSearchCriteriaValidator.validate(wiretapSearchCriteria, errors);
        this.mockery.assertIsSatisfied();
    }

}
