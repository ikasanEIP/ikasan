/*
 * $Id: HibernatePersistence.java 10042 2008-04-10 08:21:59Z verbma $
 * $URL: $
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

import org.ikasan.framework.component.Event;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Wrapper class for plugins that do not implement the <code>EventInvocable</code> interface, but nonetheless have a
 * functional method that matches the parameter list.
 * 
 * All wrapped plugins must still provide a public void method that takes as arguments an <code>Event</code>, along with
 * a <code>TargetParams</code> object
 * 
 * @author Ikasan Development Team
 */
public class EventInvocablePojoWrapper extends PojoWrapperPlugin implements EventInvocable
{
    /**
     * Constructor
     * 
     * @param pojo The POJO to wrap
     * @param pojoMethodName The method to execute on the POJO
     */
    public EventInvocablePojoWrapper(Object pojo, String pojoMethodName)
    {
        super(pojo, pojoMethodName);
    }

    public void invoke(Event event) throws PluginInvocationException
    {
        invoke(new Object[] { event }, new Class[] { Event.class });
    }
}
