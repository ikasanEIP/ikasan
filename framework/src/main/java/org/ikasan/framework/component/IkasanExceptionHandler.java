/*
 * $Id: IkasanExceptionHandler.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/IkasanExceptionHandler.java $
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
package org.ikasan.framework.component;

import org.ikasan.framework.exception.IkasanExceptionAction;

/**
 * This interface defines the public interface for invocation of the Ikasan Exception Handler
 * 
 * @author Ikasan Development Team
 */
public interface IkasanExceptionHandler
{
    /**
     * Push an exception that occurred whilst handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param event The original event
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Event event, final Throwable throwable);

    /**
     * Push an exception that occurred outside the scope of handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Throwable throwable);
}
