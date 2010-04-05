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
