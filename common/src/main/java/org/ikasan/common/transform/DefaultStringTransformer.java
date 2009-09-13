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
 * @author Ikasan Development Team
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
