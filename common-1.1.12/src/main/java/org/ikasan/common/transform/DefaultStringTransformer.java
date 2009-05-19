/*
 * $Id$
 * $URL$
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
package org.ikasan.common.transform;

import java.io.IOException;
import java.io.Serializable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;
import org.ikasan.common.CommonStringTransformer;

/**
 * Default implementation of the Payload interface.
 * 
 * @author Jeff Mitchell
 */
public class DefaultStringTransformer
    implements Serializable, CommonStringTransformer
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(DefaultStringTransformer.class);

    /**
     * Invokes the DefaultDelimitedToFlatXMLTransformer to tokenize the input
     * string based on the delimiter and token separator patterns and creates
     * an XML document with the defined root element name which holds all derived
     * tokens/values each as a child element of the root.
     * 
     * @param string 
     * @param delimiter 
     * @param tokenSeparator 
     * @param rootElementName
     *  
     * @return XML String representation
     * 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws TransformerException 
     */
    public String delimitedStringToXMLString(String string, String delimiter, 
            String tokenSeparator, String rootElementName) throws TransformerException, ParserConfigurationException, IOException
    {
        logger.debug("Creating a new DelimitedStringToFlatXMLTransformer");
        DelimitedStringToFlatXMLTransformer t = 
            new DelimitedStringToFlatXMLTransformer();
        logger.debug("Set transformer parameters");
        t.setSource(string);
        t.setMessageDelimiters(delimiter);
        t.setTokenSeparator(tokenSeparator);
        t.setRootElementName(rootElementName);
        logger.debug("Returned transformered string");
        String flatXMLString = t.getXMLString();
        return flatXMLString;
    }
}
