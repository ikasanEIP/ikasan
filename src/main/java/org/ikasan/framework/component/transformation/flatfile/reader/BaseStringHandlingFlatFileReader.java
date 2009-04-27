/* 
 * $Id: BaseStringHandlingFlatFileReader.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/flatfile/reader/BaseStringHandlingFlatFileReader.java $
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ikasan.framework.component.transformation.flatfile.reader.field.BaseFieldDefinition;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Abstract base class for all flat file readers that must first convert the
 * incoming <code>InputSource</code> to a String for handling
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class BaseStringHandlingFlatFileReader extends
        BaseFlatFileReader
{
    /**
     * Flag specifying whether trailing white space should be trimmed from each
     * element value
     */
    protected boolean trimTrailingWhitespace;

    /**
     * Constructor
     * 
     * @param rootElementName
     * 
     * @param trimTrailingWhitespace
     */
    public BaseStringHandlingFlatFileReader(String rootElementName,
            boolean trimTrailingWhitespace)
    {
        super(rootElementName);
        this.trimTrailingWhitespace = trimTrailingWhitespace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see reader.BaseFlatFileReader#parseInputSource(org.xml.sax.InputSource)
     */
    @Override
    public void parseInputSource(InputSource source) throws IOException,
            SAXException
    {
        InputStream is = source.getByteStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = 0;
        while (read > -1)
        {
            read = is.read();
            if (read != -1)
            {
                byteArrayOutputStream.write(read);
            }
        }
        parseString(new String(byteArrayOutputStream.toByteArray()));
    }

    /**
     * Parse the string, is overridden by child classes
     * 
     * @param string
     * @throws IOException
     * @throws SAXException
     */
    protected abstract void parseString(String string) throws IOException,
            SAXException;

    /**
     * Format the field value, in this case trim whitespace and line carriages
     * 
     * @param fieldValue
     * @return formatted field value
     */
    protected String format(String fieldValue)
    {
        String formattedValue = fieldValue;
        if (this.trimTrailingWhitespace)
        {
            formattedValue = formattedValue.trim();
        }
        // TODO May need further expansion here to deal with other types of
        // carriage return
        formattedValue = formattedValue.replaceAll("\n", "");
        return formattedValue;
    }

    /**
     * Determines if a field's value is empty
     * 
     * @param fieldValue
     * @return true if null, empty string or blank String
     */
    protected boolean isEmpty(String fieldValue)
    {
        boolean result = false;
        if (fieldValue.trim().length() == 0)
        {
            result = true;
        }
        return result;
    }

    /**
     * @param fieldDefinition
     * @param rawFieldContent
     * @throws SAXException
     */
    protected void reportField(BaseFieldDefinition fieldDefinition,
            String rawFieldContent) throws SAXException
    {
        String fieldValue = format(rawFieldContent);
        // skip the reporting of this field if it is empty and allowed to be
        // skipped
        if (!(fieldDefinition.isSkipIfEmpty() && isEmpty(fieldValue)))
        {
            String fieldName = fieldDefinition.getFieldName();
            this.contentHandler.startElement(this.namespaceURI, fieldName, fieldName,
                this.attributes);
            this.contentHandler.characters(fieldValue.toCharArray(), 0, fieldValue
                .length());
            this.contentHandler.endElement(this.namespaceURI, fieldName, fieldName);
        }
    }
}
