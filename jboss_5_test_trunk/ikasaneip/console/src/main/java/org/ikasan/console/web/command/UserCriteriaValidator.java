/*
 * $Id: WiretapSearchCriteriaValidator.java 2551 2009-10-21 11:22:10Z karianna $
 * $URL: https://open.jira.com/svn/IKASAN/branches/console-redesign/ikasaneip/console/src/main/java/org/ikasan/console/web/command/WiretapSearchCriteriaValidator.java $
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * The validator for users
 * 
 * @author Ikasan Development Team
 */
public class UserCriteriaValidator implements Validator
{

    /** The logger */
    private Logger logger = Logger.getLogger(UserCriteriaValidator.class);

    /** Constructor */
    public UserCriteriaValidator()
    {
        super();
    }

    /**
     * Warning suppressed because .equals method does not support Generics
     * 
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return UserCriteria.class.equals(clazz);
    }

    /**
     * Validate the UserCriteria
     * 
     * @param object - UserCriteria to validate
     * @param errors - Errors to 'return' (if any).
     */
    public void validate(Object object, List<String> errors)
    {
        UserCriteria userCriteria = (UserCriteria) object;
        
        if (userCriteria.getUsername() == null)
        {
            errors.add("Username cannot be empty.");
        }

        if (userCriteria.getPassword() == null)
        {
            errors.add("Password cannot be empty.");
        }
    }

    /**
     * Unused
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(@SuppressWarnings("unused") Object arg0, @SuppressWarnings("unused") Errors arg1)
    {
        // Unused on purpose, we're providing our own version.
        throw new UnsupportedOperationException("Unused on purpose, we're providing our own version.");
    }
}
