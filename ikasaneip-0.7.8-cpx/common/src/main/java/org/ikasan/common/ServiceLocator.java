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
