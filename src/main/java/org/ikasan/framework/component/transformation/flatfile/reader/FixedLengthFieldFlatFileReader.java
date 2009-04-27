/* 
 * $Id: FixedLengthFieldFlatFileReader.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/flatfile/reader/FixedLengthFieldFlatFileReader.java $
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
