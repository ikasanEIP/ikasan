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

import org.ikasan.framework.component.transformation.flatfile.reader.field.FixedLengthFieldDefinition;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implementation of <code>XMLReader</code> capable of reading flat files with
 * fixed length fields
 * 
 * @author Ikasan Development Team
 * 
 */
public class FixedLengthFieldFlatFileReader extends BaseStringHandlingFlatFileReader
{
	/**
	 * Constructor
	 * 
	 * @param rootElementName 
	 * @param trimTrailingWhitespace
	 */
	public FixedLengthFieldFlatFileReader(String rootElementName, boolean trimTrailingWhitespace) {
		super(rootElementName, trimTrailingWhitespace);
	}

    /**
     * Ordered List of field definitions.
     */
    private List<FixedLengthFieldDefinition> fieldDefinitions;

    /**
     * Constructor
     * 
     * @param rootElementName 
     * @param fieldDefinitions
     * @param trimTrailingWhitespace
     */
    public FixedLengthFieldFlatFileReader(String rootElementName, List<FixedLengthFieldDefinition> fieldDefinitions, boolean trimTrailingWhitespace)
    {
        super(rootElementName, trimTrailingWhitespace);
        this.fieldDefinitions = fieldDefinitions;
    }
    

    /**
     * parses the content of the flat file
     * 
     * @param fileContent - content of the flat file
     */
    @Override
    protected void parseString(String fileContent) throws SAXException
    {
        String tempFileContent = fileContent;
        int consumedIndex = 0;

        for (FixedLengthFieldDefinition fieldDefinition : fieldDefinitions)
        {
        	if (tempFileContent.length() < fieldDefinition.getFieldLength())
            {
            	if (tempFileContent.length()==0&&fieldDefinition.isOptionalField()){
            		//we are out of content and we have reached an optional field,
            		//therefore can exit early
            		break;
            	}
                throw new SAXParseException("content is too short for fixed length field [" + fieldDefinition + "]", new FlatFileLocator(consumedIndex));
            }
            String rawFieldContent = tempFileContent.substring(0, fieldDefinition.getFieldLength());
			reportField(fieldDefinition, rawFieldContent);
            
            tempFileContent = tempFileContent.substring(fieldDefinition.getFieldLength());
            consumedIndex += fieldDefinition.getFieldLength();
            
        }
    }



}
