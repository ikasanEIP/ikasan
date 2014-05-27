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
package org.ikasan.component.converter.xml;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Convert incoming event to an XML Document.
 * Ikasan Development Team.
 */
public class ToXmlDocumentConverter<T> implements Converter<T, Document>
{
    /** factory instance - not thread safe! */
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * Allow override of default document builder factory
     * @param documentBuilderFactory
     */
    public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory)
    {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    @Override
    public Document convert(T message) throws TransformationException
    {
        if(message instanceof Document)
        {
            return (Document)message;
        }

        try
        {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if(message instanceof byte[])
            {
                return documentBuilder.parse(new ByteArrayInputStream((byte[])message));
            }
            else if(message instanceof String)
            {
                return documentBuilder.parse(new ByteArrayInputStream( ((String)message).getBytes() ));
            }
            else if(message instanceof File)
            {
                return documentBuilder.parse((File)message);
            }

            throw new TransformationException("Unsupported incoming message type class[" + message.getClass().getName() + "]");
        }
        catch (ParserConfigurationException e)
        {
            throw new TransformationException(e);
        }
        catch (SAXException e)
        {
            throw new TransformationException(e);
        }
        catch (IOException e)
        {
            throw new TransformationException(e);
        }
    }
}
