/* 
 * $Id: BaseFieldDefinition.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/flatfile/reader/field/BaseFieldDefinition.java $
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
