/*
 * $Id: CommonStringTransformer.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/CommonStringTransformer.java $
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
package org.ikasan.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Common String Transformer provides the base interface for all String Transformation.
 *
 * @author Jeff Mitchell
 */  
public interface CommonStringTransformer
{ 
    /** 
     * Provides functionality to parse a delimited string of single values or 
     * name-value pairs into a flat XML structure.
     * <p><u>Example 1:</u>
     * <p>Delimited string of single value: <code>|a|b|c|d|</code>
     * <p>Resulting xml:<pre>
     * &lt;root&gt;
     *   &lt;a/&gt;
     *   &lt;b/&gt;
     *   &lt;c/&gt;
     *   &lt;d/&gt;
     * &lt;/root&gt;</pre>
     * <p><u>Example 2:</u>
     * <p>Delimited string of name-value pair: <code>|a=1|b=2|c=3|d=4|</code>
     * <p>Resulting xml:<pre> 
     * &lt;root&gt;
     *    &lt;a&gt;1&lt;/a&gt;
     *    &lt;b&gt;2&lt;/b&gt;
     *    &lt;c&gt;3&lt;/c&gt;
     *    &lt;d&gt;4&lt;/d&gt;
     * &lt;/root&gt;</pre>
     * 
     * @param string The delimited string to convert to flat XML string
     * @param delimiter The delimiter pattern used to tokenise the input string
     * @param tokenSeperator A name-value pair separator (i.e. '=' in "a=b")
     * @param rootElementName The root element of the flat xml to be returned
     * @return String String representation of flat XML document.
     * @throws TransformerException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     */
    public String delimitedStringToXMLString(String string, String delimiter, 
            String tokenSeperator, String rootElementName)
        throws TransformerException, ParserConfigurationException, IOException;

} 