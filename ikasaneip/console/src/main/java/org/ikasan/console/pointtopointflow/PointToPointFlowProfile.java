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
package org.ikasan.console.pointtopointflow;

import java.util.HashSet;
import java.util.Set;

/**
 * Object that represents a PointToPointFlowProfile in Ikasan.
 * 
 * A PointToPointFlowProfile can be see as owning a list of PointToPointFlows.
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowProfile
{
    
    /** Unique Id */
    private long id;
    
    /** The name for this PointToPointFlow */
    private String name;

    /** The set of PointToPointFlow objects that make up this profile */
    private Set<PointToPointFlow> pointToPointFlows = new HashSet<PointToPointFlow>();
    
    /**
     * Get the unique id
     * 
     * @return id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the unique id, private as it is only referenced by Hibernate and 
     * not actually used.
     * 
     * @param id - id to set
     */
    @SuppressWarnings("unused")
    private void setId(long id)
    {
        this.id = id;
    }
        
    /**
     * Get the set of PointToPointFlows
     * 
     * @return set of PointToPointFlows for this Profile
     */
    public Set<PointToPointFlow> getPointToPointFlows()
    {
        return pointToPointFlows;
    }

    /**
     * Set the PointToPointFlows for this profile
     * 
     * @param pointToPointFlows - PointToPointFlows for this profile
     */
    public void setPointToPointFlows(Set<PointToPointFlow> pointToPointFlows)
    {
        this.pointToPointFlows = pointToPointFlows;
    }

    /**
     * Get the name for this PointToPointFlow
     * 
     * @return The name for this PointToPointFlow
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name for this PointToPointFlow
     * 
     * @param name - The name for this PointToPointFlow
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
