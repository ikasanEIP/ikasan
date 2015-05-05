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
package org.ikasan.filetransfer;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

/**
 * Common XML Transformer provides the base interface for all XML Transformation.
 *
 * @author Ikasan Development Team
 */  
public interface CommonXMLTransformer
{ 
    /**
     * Transform document to String
     * 
     * @param document
     * @return String representing transformed Document
     * @throws IOException
     * @throws TransformerException
     */
    public String toString(Document document)
        throws IOException, TransformerException;
    
    /**
     * Transform Document to File
     * 
     * @param document
     * @throws IOException
     * @throws TransformerException
     */
    public void toFile(Document document)
        throws IOException, TransformerException;
    
    /**
     * Transform document to XML file
     * @param document
     * @param xmlFileName
     * @throws IOException
     * @throws TransformerException
     */
    public void toFile(Document document, String xmlFileName)
        throws IOException, TransformerException;

    /**
     * Configure the output properties of the transformer
     * @param name
     * @param value
     */
    public void setOutputProperty(String name, String value);
} 