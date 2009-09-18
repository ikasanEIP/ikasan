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
package org.ikasan.connector.base.inbound;

import java.util.List;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;

/**
 * This interface provides the common default endpoints for all inbound resource
 * adapters.
 * 
 * @author Ikasan Development Team
 */
public interface EndpointListener
{
    /**
     * Only endpoint method we can guarantee will be required by all connectors
     * in the exception endpoint
     * 
     * @param t - reported exception
     * @throws ResourceException - Exception if pushing the throwable fails
     */
    public void push(Throwable t) throws ResourceException;

    /**
     * Push the Payload
     * 
     * @param payload - Payload to push
     * @throws ResourceException - Exception if pushing the Payload fails
     */
    public void push(Payload payload) throws ResourceException;

    /**
     * @param payloads - List of Payloads to push
     * @throws ResourceException - Exception if pushing the Payloads fails
     */
    public void push(List<Payload> payloads) throws ResourceException;
}
