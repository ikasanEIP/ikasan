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

import org.ikasan.common.Payload;
import org.ikasan.framework.plugins.invoker.PluginInvocationException;

/**
 * Wrapper class for plugins that do not implement the <code>PayloadInvocable</code> interface, but nonetheless have a
 * functional method that matches the parameter list.
 * 
 * All wrapped plugins must still provide a public void method that takes as arguments a <code>Payload</code>, along
 * with a <code>TargetParams</code> object
 * 
 * @author Ikasan Development Team
 */
public class PayloadInvocablePojoWrapper extends PojoWrapperPlugin implements PayloadInvocable
{
    /**
     * Constructor
     * 
     * @param pojo POJO to wrap
     * @param pojoMethodName Method on the POJO to execute
     */
    public PayloadInvocablePojoWrapper(Object pojo, String pojoMethodName)
    {
        super(pojo, pojoMethodName);
    }

    public void invoke(Payload payload) throws PluginInvocationException
    {
        invoke(new Object[] { payload }, new Class[] { Payload.class });
    }
}
