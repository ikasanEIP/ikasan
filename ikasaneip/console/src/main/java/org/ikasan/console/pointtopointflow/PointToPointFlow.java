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

import org.ikasan.console.module.Module;

/**
 * Object that represents a PointToPointFlow in Ikasan.  A PointToPointFlow 
 * consists of 2 linked modules, e.g. src --> target
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlow
{
    /** Unique Id */
    private long id;
   
    /** The profile for this PointToPointFlow */
    private PointToPointFlowProfile pointToPointFlowProfile;

    /** The from Module */
    Module fromModule = null;
    
    /** The to Module */
    Module toModule = null;

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
     * Get the Profile for this PointToPointFlow
     *  
     * @return The Profile for this PointToPointFlow
     */
    public PointToPointFlowProfile getPointToPointFlowProfile()
    {
        return pointToPointFlowProfile;
    }

    /**
     * Set the Profile for this PointToPointFlow
     *  
     * @param pointToPointFlowProfile - The Profile for this PointToPointFlow
     */
    public void setPointToPointFlowProfile(PointToPointFlowProfile pointToPointFlowProfile)
    {
        this.pointToPointFlowProfile = pointToPointFlowProfile;
    }
    
    /**
     * Set the to Module
     * 
     * @param toModule - toModule for this PointToPointFlow
     */
    public void setToModule(Module toModule)
    {
        this.toModule = toModule;
    }

    /**
     * Get the toModule for this PointToPointFlow
     * 
     * @return toModule for this PointToPointFlow
     */
    public Module getToModule()
    {
        return toModule;
    }

    /**
     * Set the from Module
     * 
     * @param fromModule - fromModule for this PointToPointFlow
     */
    public void setFromModule(Module fromModule)
    {
        this.fromModule = fromModule;
    }

    /**
     * Get the fromModule for this PointToPointFlow
     * 
     * @return fromModule for this PointToPointFlow
     */
    public Module getFromModule()
    {
        return fromModule;
    }
    
}
