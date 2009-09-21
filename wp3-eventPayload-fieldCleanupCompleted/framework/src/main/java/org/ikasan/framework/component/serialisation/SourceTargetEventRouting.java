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
package org.ikasan.framework.component.serialisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ikasan Development Team
 */
public class SourceTargetEventRouting
{
    /**Route list*/
    private List<Route> routingList = new ArrayList<Route>();
    /**
     * 
     */
    private Map<String, List<TargetSystem>> router = new HashMap<String, List<TargetSystem>>();
    
    /**
     * Default constructor.
     */
    protected SourceTargetEventRouting()
    {
        // Does nothing.
    }

    /**
     * Constructor.
     * @param routingList
     */
    public SourceTargetEventRouting(List<Route> routingList)
    {
        this.routingList = routingList;
    }
    
    // Setters
    /**
     * @param routingList
     */
    public void setTargetSystemsList(List<Route> routingList)
    {
        this.routingList = routingList;
    }

    // Getters
    /**
     * @return targetSystems
     */
    public List<Route> getRoutingList()
    {
        return this.routingList;
    }
    
    /**
     * Add a target system to the existing list.
     * @param route
     */
    public void addRoute(Route route)
    {
        this.routingList.add(route);
    }
    
    /**
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Routes:\n");
        sb.append(this.routingList);
        sb.append('\n');
        return sb.toString();
    }
    
    /**
     * @param source
     * @param list
     */
    public void addRouteMap(String source, List<TargetSystem> list)
    {
        this.router.put(source, list);
    }
}
