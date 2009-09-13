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

import java.io.File;

import org.ikasan.common.factory.EnvelopeFactory;
import org.ikasan.common.factory.JMSMessageFactory;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.common.xml.serializer.XMLSerializer;
import org.springframework.jndi.JndiTemplate;

/**
 * Interface for central service responsible for providing access to numerous other services
 * 
 * TODO Most of these calls should become deprecated as we configure via Spring
 * 
 * @author Ikasan Development Team
 */
public interface ServiceLocator
{
    /**
     * Returns a fully configured instance of <code>PayloadFactory</code>
     * 
     * @return PayloadFactory
     * @deprecated Should configure via Spring Beans
     */
    public PayloadFactory getPayloadFactory();

    /**
     * Returns a fully configured instance of <code>EnvelopeFactory</code>
     * 
     * @return EnvelopeFactory
     */
    public EnvelopeFactory getEnvelopeFactory();

    /**
     * Returns a fully configured instance of <code>CommonXMLParser</code>
     * 
     * @return CommonXMLParser
     */
    public CommonXMLParser getCommonXmlParser();

    /**
     * Returns a fully configured instance of <code>JMSMessageFactory</code>
     * 
     * @return JMSMessageFactory
     */
    public JMSMessageFactory getJMSMessageFactory();

    /**
     * Returns a fully configured XML Serialiser/Desrialiser for <code>Envelope</code>s
     * 
     * @return XMLSerializer<Envelope>
     */
    public XMLSerializer<Envelope> getEnvelopeXMLSerializer();

    /**
     * Returns a fully configured XML Serialiser/Desrialiser for <code>Payload</code>s
     * 
     * @return XMLSerializer<Payload>
     */
    public XMLSerializer<Payload> getPayloadXMLSerializer();

    /**
     * Returns a <code>JNDITemplate</code> for accessing JNDI resources from the JMS server
     * 
     * @return <code>JNDITemplate</code>
     */
    public JndiTemplate getJMSJndiTemplate();

    /**
     * Returns the base Ikasan configuration directory as a <code>File</code>
     * 
     * @return base Ikasan configuration directory
     */
    public File getIkasanConfigurationDirectory();
}
