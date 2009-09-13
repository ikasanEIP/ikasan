/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Common String Transformer provides the base interface for all String Transformation.
 *
 * @author Ikasan Development Team
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