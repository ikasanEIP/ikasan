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
package org.ikasan.component.validator.xml;

import java.util.List;

/**
 * Configuration bean for the SchematronValidator
 */
public class XMLValidatorConfiguration
{
    /**
     * Option to skip the validation, defaults to false
     */
    public boolean skipValidation = false;

    /**
     * Option to throw an exception on any validation failure, defaults to false
     */
    private boolean throwExceptionOnValidationFailure = false;

    /**
     * Option to return ValidationResult object as result of validation, defaults to false
     */
    public boolean returnValidationResult = false;


    public boolean isSkipValidation()
    {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation)
    {
        this.skipValidation = skipValidation;
    }

    public boolean isThrowExceptionOnValidationFailure()
    {
        return throwExceptionOnValidationFailure;
    }

    public void setThrowExceptionOnValidationFailure(boolean throwExceptionOnValidationFailure)
    {
        this.throwExceptionOnValidationFailure = throwExceptionOnValidationFailure;
    }

    public boolean isReturnValidationResult() {
        return returnValidationResult;
    }

    public void setReturnValidationResult(boolean returnValidationResult) {
        this.returnValidationResult = returnValidationResult;
    }
}
