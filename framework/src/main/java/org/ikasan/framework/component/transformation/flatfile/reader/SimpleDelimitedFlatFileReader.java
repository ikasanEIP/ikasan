/* 
 * $Id: SimpleDelimitedFlatFileReader.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/flatfile/reader/SimpleDelimitedFlatFileReader.java $
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
package org.ikasan.framework.component.transformation.flatfile.reader;

import java.util.List;
import java.util.StringTokenizer;

import org.ikasan.framework.component.transformation.flatfile.reader.field.BaseFieldDefinition;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Simple implementation of <code>XMLReader</code>, capable of parsing
 * content delimited by a known String
 * 
 * @author Ikasan Development Team
 * 
 */
public class SimpleDelimitedFlatFileReader extends BaseStringHandlingFlatFileReader
{
    /** The default delimter */
    private static final String DEFAULT_DELIMITER = ",";
    /**
     * Delimiter used for differentiating between fields
     */
    private String delimiter = DEFAULT_DELIMITER;
    /**
     * List of fields that we expect to find when parsing content
     */
    private List<BaseFieldDefinition> fieldDefinitions;

    /**
     * Constructor
     * 
     * @param rootElementName
     * @param trimTrailingWhitespace
     * @param fieldDefinitions
     */
    public SimpleDelimitedFlatFileReader(String rootElementName, List<BaseFieldDefinition> fieldDefinitions, boolean trimTrailingWhitespace)
    {
        super(rootElementName, trimTrailingWhitespace);
        this.fieldDefinitions = fieldDefinitions;
    }

    @Override
    protected void parseString(String string) throws SAXException
    {
        StringTokenizer st = new StringTokenizer(string, this.delimiter);
        for (BaseFieldDefinition fieldDefinition : this.fieldDefinitions)
        {
            if (!st.hasMoreTokens())
            {
                if (fieldDefinition.isOptionalField())
                {
                    // we are out of content and we have reached an optional
                    // field,
                    // therefore can exit early
                    break;
                }
                // Default else
                throw new SAXParseException("no more content exists to satisfy field [" + fieldDefinition.getFieldName() + "]", null);
            }
            reportField(fieldDefinition, st.nextToken());
        }
    }

    /**
     * Mutator for delimiter
     * 
     * @param delimiter
     */
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }
}
