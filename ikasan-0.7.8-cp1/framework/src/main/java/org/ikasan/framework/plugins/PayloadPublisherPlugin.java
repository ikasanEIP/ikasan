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
package org.ikasan.framework.plugins;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.PayloadPublisher;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Simple wrapper for <code>PayloadPublisher</code> to allow it to work within the plugin framework
 * 
 * @author Ikasan Development Team
 */
public class PayloadPublisherPlugin implements PayloadInvocable
{
    /** A publisher for Payloads */
    private PayloadPublisher payloadPublisher;

    /**
     * Constructor
     * 
     * @param payloadPublisher The payload publisher to use
     */
    public PayloadPublisherPlugin(PayloadPublisher payloadPublisher)
    {
        super();
        this.payloadPublisher = payloadPublisher;
    }

    public void invoke(Payload payload) throws PluginInvocationException
    {
        try
        {
            payloadPublisher.publish(payload);
        }
        catch (ResourceException e)
        {
            throw new PluginInvocationException("ResourceException thrown by PayloadPublisher", e);
        }
    }
}
