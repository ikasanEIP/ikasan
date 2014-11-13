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
package org.ikasan.component.converter.xml;

import javax.xml.bind.ValidationEvent;

/**
 * Use custom XML validation exception to allow specific handling of a failed XML
 * validation verses another type of XML failure.
 * 
 * @author Ikasan Development Team
 */
public class XmlValidationException extends RuntimeException
{
    /** serialVersionUID */
    private static final long serialVersionUID = 5643215546008399313L;

    /** take the event causing validation failure with the exception */
    private ValidationEvent validationEvent;
    
    /** String representation of event failing validation where possible */
    private String failedEvent;
    
    /**
     * Constructor
     * 
     * @param validationEvent
     */
    public XmlValidationException(ValidationEvent validationEvent)
    {
        this.validationEvent = validationEvent;
    }
    
    /**
     * Get the event encompassing the validation error and original object which failed.
     */
    public ValidationEvent getValidationEvent()
    {
        return this.validationEvent;
    }

    /**
     * Set the String representation of the event XML failing validation
     * @param failedEvent
     */
    public void setFailedEvent(String failedEvent)
    {
        this.failedEvent = failedEvent;
    }
    
    /**
     * Get the String representation of the event XML failing validation
     * @return failedEvent
     */
    public String getFailedEvent()
    {
        return this.failedEvent;
    }
}
