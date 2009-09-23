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
package org.ikasan.framework.component.transformation.flatfile.reader.field;

/**
 * Base class for flat file field definitions
 * 
 * @author Ikasan Development Team
 * 
 */
public class BaseFieldDefinition
{
    /**
     * Our name for the field
     */
    protected String fieldName;
    /**
     * This field can be skipped in output if its value is empty
     */
    protected boolean skipIfEmpty;
    /**
     * This field may not actually exist, ie if there is no more content,
     * neither this field nor any subsequent field will be evaluated
     */
    protected boolean optionalField;

    /**
     * Setter method for optionalField
     * 
     * @param optionalField the optional flag to set
     */
    public void setOptionalField(boolean optionalField)
    {
        this.optionalField = optionalField;
    }

    /**
     * Accessor for optionalField
     * 
     * @return true if it is allowable that the previous field was the last one
     */
    public boolean isOptionalField()
    {
        return this.optionalField;
    }

    /**
     * Accessor for skipIfEmpty
     * 
     * @return skipIfEmpty
     */
    public boolean isSkipIfEmpty()
    {
        return this.skipIfEmpty;
    }

    /**
     * Setter for skipIfEmpty
     * 
     * @param skipIfEmpty - The flag to set
     */
    public void setSkipIfEmpty(boolean skipIfEmpty)
    {
        this.skipIfEmpty = skipIfEmpty;
    }

    /**
     * Constructor
     * 
     * @param fieldName - The name of the field
     */
    public BaseFieldDefinition(String fieldName)
    {
        this.fieldName = fieldName;
    }

    /**
     * Accessor for fieldName
     * 
     * @return fieldName
     */
    public String getFieldName()
    {
        return this.fieldName;
    }
}
