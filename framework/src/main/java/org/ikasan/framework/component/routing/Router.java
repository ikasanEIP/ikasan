/* 
 * $Id: Router.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/routing/Router.java $
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

import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.FlowComponent;

/**
 * Interface for all FlowComponents that perform a Routing function. These are characterised by a dynamic evaluation of
 * business path. The resultant path or paths for the given <code>
 * Event</code> are identified by the values in the resultant
 * <code>List<String</code>
 * 
 * @author Ikasan Development Team
 */
public interface Router extends FlowComponent
{
    /** Default result for any unresolved routing implementation */
    public static final String DEFAULT_RESULT = "default";

    /**
     * Handles the <code>Event<code> in a read-only fashion, returning an ordered List of 
     * paths/routes for this <code>Event</code> to take next
     * 
     * @param event Event to handle
     * @return List<String> of paths/routes for this <code>Event</code> to take next
     * @throws RouterException - if the result cannot be calculated for any reason
     */
    public List<String> onEvent(Event event) throws RouterException;
}