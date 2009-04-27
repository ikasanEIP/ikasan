/* 
 * $Id: Flow.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/flow/Flow.java $
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
package org.ikasan.framework.flow;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.IkasanExceptionAction;

/**
 * Interface representing a business path for an <code>Event<code>
 * 
 * Invocation represents the traversal of that business path. Problems/errors
 * are represented by the invocation method returning a <code>IkasanExceptionAction</code>
 * 
 * @author Ikasan Development Team
 */
public interface Flow
{
    /**
     * Invocation of this method represents the handling of the <code>Event<code>
     * with respect to some business path
     * 
     * @param event The event we're dealing with
     * @return IkasanExceptionAction in the case of a problem/error
     */
    public IkasanExceptionAction invoke(Event event);

    /**
     * Returns the name of this flow
     * 
     * @return String name of this flow
     */
    public String getName();

    /**
     * Accessor for moduleName
     * 
     * @return name of the module this flow exist for
     */
    public String getModuleName();
}
