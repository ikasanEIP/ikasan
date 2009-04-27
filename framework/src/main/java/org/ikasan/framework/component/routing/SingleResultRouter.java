/* 
 * $Id: SingleResultRouter.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/routing/SingleResultRouter.java $
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
package org.ikasan.framework.component.routing;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;

/**
 * Simple abstraction for <code>Router</code> implementations that are only ever to return a single result
 * 
 * @author Ikasan Development Team
 */
public abstract class SingleResultRouter implements Router
{
    /**
     * Implementation of Router.onEvent(Event)
     * 
     * @param event The event to route
     * @return List of paths
     * @throws RouterException Exception if we could bnot route the event
     */
    public List<String> onEvent(Event event) throws RouterException
    {
        List<String> result = new ArrayList<String>();
        result.add(evaluate(event));
        return result;
    }

    /**
     * Returns a single value as a result of evaluating the <code>Event</code>
     * 
     * @param event to evaluate
     * @return result
     * @throws RouterException Exception if we could not route the event
     */
    protected abstract String evaluate(Event event) throws RouterException;
}
