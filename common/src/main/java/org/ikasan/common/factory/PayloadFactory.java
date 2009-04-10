/* 
 * $Id: PayloadFactory.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/factory/PayloadFactory.java $
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
package org.ikasan.common.factory;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;

/**
 * Interface that defines a Factory for producing Payloads
 * 
 * @author Ikasan Development Team
 */
public interface PayloadFactory
{
    /**
     * Create a new instance of the Payload based on the incoming name, spec (as a string) and source system.
     * 
     * @param name The payload name
     * @param spec The payload Spec (as a String)
     * @param srcSystem The payload source system
     * @return Payload
     */
    public Payload newPayload(final String name, final String spec, final String srcSystem);

    /**
     * Create a new instance of the Payload based on the incoming name, spec and source system.
     * 
     * @param name The payload name
     * @param spec The payload Spec
     * @param srcSystem The payload source system
     * @return Payload
     */
    public Payload newPayload(final String name, final Spec spec, final String srcSystem);

    /**
     * Create a new instance of the Payload based on the incoming Payload.
     * 
     * @param payload The incoming payload
     * @return Payload
     */
    public Payload newPayload(Payload payload);

    /**
     * Create a new instance of the Payload for the incoming content.
     * 
     * @param name The payload name
     * @param spec The payload Spec
     * @param srcSystem The payload source system
     * @param content The payload content
     * @return Payload
     */
    public Payload newPayload(final String name, final Spec spec, final String srcSystem, final byte[] content);

    /**
     * Create a new instance of the Payload for the incoming content.
     * 
     * @param name The payload name
     * @param spec The payload Spec (as a String)
     * @param srcSystem The payload source system
     * @param content The payload content
     * @return Payload
     */
    public Payload newPayload(final String name, final String spec, final String srcSystem, final byte[] content);

    /**
     * Create a new instance of the Payload for the incoming content, where the content is known to be XML
     * 
     * @param spec The payload Spec (as a String)
     * @param srcSystem The payload source system
     * @param xmlContent The content
     * @return Payload
     */
    public Payload newPayload(final Spec spec, final String srcSystem, final byte[] xmlContent);

    /**
     * Get the payload concrete implementation class.
     * 
     * @return the payloadImplClass
     */
    public Class<? extends Payload> getPayloadImplClass();

    /**
     * Set the payload concrete implementation class
     * 
     * @param payloadImplClass the payloadImplClass to set
     */
    public void setPayloadImplClass(final Class<? extends Payload> payloadImplClass);
}
